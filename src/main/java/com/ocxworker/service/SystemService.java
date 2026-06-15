package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.enums.TaskStatusEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciCreateTaskMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciCreateTask;
import com.ocxworker.util.OciRegionCatalog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Service
public class SystemService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SystemService.class);
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Lazy
    @Resource
    private OciProxyConfigService ociProxyConfigService;
    @Resource
    private StorageService storageService;
    private static final String REPO = "OCIworker/OCIworker";
    private static final String PUBLIC_RELEASE_REPO = "OCIworker/OCIworker";
    private static final String JAR_PATH = "/opt/ocx-worker/ocx-worker.jar";
    private static final String ASSET_NAME = "ocx-worker-1.0.0.jar";
    private static final ObjectMapper JSON = new ObjectMapper();
    private String currentCommit;

    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("build-commit.txt")) {
            if (is != null) {
                this.currentCommit = new String(is.readAllBytes()).trim();
                if (this.currentCommit.length() > 7) {
                    this.currentCommit = this.currentCommit.substring(0, 7);
                }

                log.info("Current build commit: {}", this.currentCommit);
            }
        } catch (Exception var6) {
            log.warn("Could not read build-commit.txt: {}", var6.getMessage());
        }
    }

    public Map<String, Object> checkUpdate() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("currentCommit", this.currentCommit != null ? this.currentCommit : "dev");
        File jarFile = new File("/opt/ocx-worker/ocx-worker.jar");
        if (jarFile.exists()) {
            result.put("currentSize", jarFile.length());
            result.put("currentSizeHuman", this.humanReadableSize(jarFile.length()));
            long lastModified = jarFile.lastModified();
            ZonedDateTime ldt = Instant.ofEpochMilli(lastModified).atZone(ZoneId.of("Asia/Shanghai"));
            result.put("currentBuildTime", ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            result.put("currentSize", -1);
            result.put("currentSizeHuman", "未找到");
        }

        try {
            String tagLatestApi = "https://api.github.com/repos/OCIworker/OCIworker/releases/tags/latest";
            HttpClient client = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tagLatestApi))
                .header("Accept", "application/vnd.github.v3+json")
                .timeout(Duration.ofSeconds(15L))
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                result.put("latestSize", -1);
                result.put("latestSizeHuman", "查询失败");
                result.put("hasUpdate", false);
                result.put("error", "GitHub 返回 " + response.statusCode() + "（可能无 tag latest）");
                return result;
            }

            JsonNode root = JSON.readTree(response.body());
            result.put("latestTag", root.path("tag_name").asText(""));
            String publishedAt = root.path("published_at").asText("");
            if (!publishedAt.isEmpty()) {
                result.put("publishedAt", publishedAt);
            }

            long jarSize = 0L;

            for (JsonNode a : root.withArray("assets")) {
                if ("ocx-worker-1.0.0.jar".equals(a.path("name").asText())) {
                    jarSize = a.path("size").asLong(0L);
                    break;
                }
            }

            if (jarSize > 0L) {
                result.put("latestSize", jarSize);
                result.put("latestSizeHuman", this.humanReadableSize(jarSize));
            } else {
                result.put("latestSize", -1);
                result.put("latestSizeHuman", "未知");
            }

            String latestCommit = null;
            String bodyText = root.path("body").asText("");
            Matcher cm = Pattern.compile("(?i)commit[\\s]+([0-9a-f]{7,40})").matcher(bodyText);
            if (cm.find()) {
                String full = cm.group(1);
                latestCommit = full.length() > 7 ? full.substring(0, 7) : full;
                result.put("latestCommit", latestCommit);
            }

            if (this.currentCommit == null) {
                result.put("hasUpdate", false);
                result.put("notice", "当前为开发版本，无法对比 commit");
            } else if (latestCommit == null) {
                result.put("hasUpdate", false);
                result.put("notice", "无法从 GitHub Release 说明中解析构建 commit，请去仓库 Releases 核对");
            } else if (this.currentCommit.equalsIgnoreCase(latestCommit)) {
                result.put("hasUpdate", false);
            } else if ("OCIworker/OCIworker".equalsIgnoreCase("OCIworker/OCIworker")) {
                result.put("hasUpdate", true);
            } else {
                String compareApi = "https://api.github.com/repos/OCIworker/OCIworker/compare/" + latestCommit + "..." + this.currentCommit;
                HttpRequest cr = HttpRequest.newBuilder()
                    .uri(URI.create(compareApi))
                    .header("Accept", "application/vnd.github.v3+json")
                    .timeout(Duration.ofSeconds(15L))
                    .GET()
                    .build();
                HttpResponse<String> cResp = client.send(cr, BodyHandlers.ofString());
                if (cResp.statusCode() == 200) {
                    String status = JSON.readTree(cResp.body()).path("status").asText("");
                    switch (status) {
                        case "identical":
                            result.put("hasUpdate", false);
                            break;
                        case "ahead":
                            result.put("hasUpdate", false);
                            result.put("versionNotice", "当前运行版本已新于或等于 GitHub 上 tag latest 发布包，无需在线更新。");
                            break;
                        case "behind":
                            result.put("hasUpdate", true);
                            break;
                        case "diverged":
                            result.put("hasUpdate", true);
                            result.put("versionNotice", "本地与线上下载包提交已分叉，更新前请确认数据与回滚方式。");
                            break;
                        default:
                            result.put("hasUpdate", !this.currentCommit.equalsIgnoreCase(latestCommit));
                    }
                } else {
                    log.warn("GitHub compare 失败 HTTP {}: {}", cResp.statusCode(), cResp.body());
                    result.put("hasUpdate", !this.currentCommit.equalsIgnoreCase(latestCommit));
                }
            }
        } catch (Exception var20) {
            log.warn("Failed to check update: {}", var20.getMessage());
            result.put("latestSize", -1);
            result.put("latestSizeHuman", "查询失败");
            result.put("hasUpdate", false);
            result.put("error", var20.getMessage());
        }

        return result;
    }

    public void performUpdate() {
        File jarFile = new File("/opt/ocx-worker/ocx-worker.jar");
        if (!jarFile.exists()) {
            throw new OciException("未找到 /opt/ocx-worker/ocx-worker.jar，请先通过 deploy.sh 部署");
        } else {
            try {
                String script = "#!/bin/bash\nset -e\nREPO=\"%s\"\nASSET=\"%s\"\nJAR=\"%s\"\n# 直连 latest 资源，避免先调 api.github.com + grep（省一次 RTT，也降低限流概率）\nJAR_URL=\"https://github.com/${REPO}/releases/download/latest/${ASSET}\"\ncurl -fSL --retry 2 --retry-delay 2 --connect-timeout 15 --max-time 600 -o \"${JAR}.tmp\" \"$JAR_URL\"\nNEW_SIZE=$(stat -c%%s \"${JAR}.tmp\" 2>/dev/null || echo 0)\nif [ \"$NEW_SIZE\" -lt 1000 ]; then\n  rm -f \"${JAR}.tmp\"\n  echo \"Download failed: file too small (${NEW_SIZE} bytes)\"\n  exit 1\nfi\nmv \"${JAR}.tmp\" \"$JAR\"\nsystemctl stop oci-webssh 2>/dev/null || true\nsystemctl disable oci-webssh 2>/dev/null || true\nrm -f /opt/ocx-worker/oci-webssh\nrm -f /etc/systemd/system/oci-webssh.service\nsystemctl daemon-reload 2>/dev/null || true\nsystemctl restart ocx-worker\n"
                    .formatted("OCIworker/OCIworker", "ocx-worker-1.0.0.jar", "/opt/ocx-worker/ocx-worker.jar");
                Path scriptFile = Path.of("/tmp/ocx-worker-update.sh");
                Files.writeString(scriptFile, script);

                try {
                    Files.setPosixFilePermissions(scriptFile, PosixFilePermissions.fromString("rwxr-xr-x"));
                } catch (UnsupportedOperationException var5) {
                }

                new ProcessBuilder("bash", "-c", "nohup bash /tmp/ocx-worker-update.sh > /tmp/ocx-worker-update.log 2>&1 &").redirectErrorStream(true).start();
                log.info("Update process started in background");
            } catch (IOException var6) {
                throw new OciException("启动更新失败: " + var6.getMessage());
            }
        }
    }

    private String humanReadableSize(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        } else {
            String[] units = new String[]{"KB", "MB", "GB"};
            double size = (double)bytes;

            for (String unit : units) {
                size /= 1024.0;
                if (size < 1024.0) {
                    return String.format("%.1f %s", size, unit);
                }
            }

            return String.format("%.1f TB", size / 1024.0);
        }
    }

    public Map<String, Object> getGlance() {
        Map<String, Object> result = new LinkedHashMap<>();
        long tenantCount = this.userMapper.selectCount(null);
        long runningTaskCount = this.taskMapper.selectCount((Wrapper)new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()));
        result.put("tenantCount", tenantCount);
        result.put("runningTaskCount", runningTaskCount);

        try {
            SystemInfo si = new SystemInfo();
            CentralProcessor processor = si.getHardware().getProcessor();
            GlobalMemory memory = si.getHardware().getMemory();
            double cpuLoad = processor.getSystemCpuLoad(500L) * 100.0;
            long totalMem = memory.getTotal();
            long availMem = memory.getAvailable();
            double memUsage = (double)(totalMem - availMem) / (double)totalMem * 100.0;
            result.put("cpuUsage", String.format("%.1f", cpuLoad));
            result.put("memoryUsage", String.format("%.1f", memUsage));
            result.put("totalMemoryGB", String.format("%.1f", (double)totalMem / 1024.0 / 1024.0 / 1024.0));
        } catch (Exception var17) {
            log.warn("Failed to get system info: {}", var17.getMessage());
        }

        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration uptime = Duration.ofMillis(uptimeMs);
        result.put("uptime", String.format("%dd %dh %dm", uptime.toDays(), uptime.toHoursPart(), uptime.toMinutesPart()));
        return result;
    }

    public List<Map<String, String>> listOciRegionCatalog(String userId) {
        return StrUtil.isNotBlank(userId)
            ? OciRegionCatalog.listUiRowsForIds(this.storageService.listSubscribedRegionIds(userId))
            : OciRegionCatalog.listUiRows();
    }
}
