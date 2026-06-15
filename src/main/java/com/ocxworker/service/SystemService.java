/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.enums.TaskStatusEnum
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciCreateTaskMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.entity.OciCreateTask
 *  com.ocxworker.service.OciProxyConfigService
 *  com.ocxworker.service.StorageService
 *  com.ocxworker.service.SystemService
 *  com.ocxworker.util.OciRegionCatalog
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.stereotype.Service
 *  oshi.SystemInfo
 *  oshi.hardware.CentralProcessor
 *  oshi.hardware.GlobalMemory
 */
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
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.service.StorageService;
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
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
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
    private static final String REPO = "xingchenv2/OCX";
    private static final String PUBLIC_RELEASE_REPO = "xingchenv2/OCX";
    private static final String JAR_PATH = "/opt/oci-worker/oci-worker.jar";
    private static final String ASSET_NAME = "oci-worker-1.0.0.jar";
    private static final ObjectMapper JSON = new ObjectMapper();
    private String currentCommit;

    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("build-commit.txt");){
            if (is != null) {
                this.currentCommit = new String(is.readAllBytes()).trim();
                if (this.currentCommit.length() > 7) {
                    this.currentCommit = this.currentCommit.substring(0, 7);
                }
                log.info("Current build commit: {}", (Object)this.currentCommit);
            }
        }
        catch (Exception e) {
            log.warn("Could not read build-commit.txt: {}", (Object)e.getMessage());
        }
    }

    public Map<String, Object> checkUpdate() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("currentCommit", this.currentCommit != null ? this.currentCommit : "dev");
        File jarFile = new File(JAR_PATH);
        if (jarFile.exists()) {
            result.put("currentSize", jarFile.length());
            result.put("currentSizeHuman", this.humanReadableSize(jarFile.length()));
            long lastModified = jarFile.lastModified();
            ZonedDateTime ldt = Instant.ofEpochMilli(lastModified).atZone(ZoneId.of("Asia/Shanghai"));
            result.put("currentBuildTime", ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            result.put("currentSize", -1);
            result.put("currentSizeHuman", "\u672a\u627e\u5230");
        }
        try {
            String tagLatestApi = "https://api.github.com/repos/xingchenv2/OCX/releases/tags/latest";
            HttpClient client = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tagLatestApi)).header("Accept", "application/vnd.github.v3+json").timeout(Duration.ofSeconds(15L)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                result.put("latestSize", -1);
                result.put("latestSizeHuman", "\u67e5\u8be2\u5931\u8d25");
                result.put("hasUpdate", false);
                result.put("error", "GitHub \u8fd4\u56de " + response.statusCode() + "\uff08\u53ef\u80fd\u65e0 tag latest\uff09");
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
                if (!ASSET_NAME.equals(a.path("name").asText())) continue;
                jarSize = a.path("size").asLong(0L);
                break;
            }
            if (jarSize > 0L) {
                result.put("latestSize", jarSize);
                result.put("latestSizeHuman", this.humanReadableSize(jarSize));
            } else {
                result.put("latestSize", -1);
                result.put("latestSizeHuman", "\u672a\u77e5");
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
                result.put("notice", "\u5f53\u524d\u4e3a\u5f00\u53d1\u7248\u672c\uff0c\u65e0\u6cd5\u5bf9\u6bd4 commit");
            } else if (latestCommit == null) {
                result.put("hasUpdate", false);
                result.put("notice", "\u65e0\u6cd5\u4ece GitHub Release \u8bf4\u660e\u4e2d\u89e3\u6790\u6784\u5efa commit\uff0c\u8bf7\u53bb\u4ed3\u5e93 Releases \u6838\u5bf9");
            } else if (this.currentCommit.equalsIgnoreCase(latestCommit)) {
                result.put("hasUpdate", false);
            } else if ("xingchenv2/OCX".equalsIgnoreCase("xingchenv2/OCX")) {
                result.put("hasUpdate", true);
            } else {
                String compareApi = "https://api.github.com/repos/xingchenv2/OCX/compare/" + latestCommit + "..." + this.currentCommit;
                HttpRequest cr = HttpRequest.newBuilder().uri(URI.create(compareApi)).header("Accept", "application/vnd.github.v3+json").timeout(Duration.ofSeconds(15L)).GET().build();
                HttpResponse<String> cResp = client.send(cr, HttpResponse.BodyHandlers.ofString());
                if (cResp.statusCode() == 200) {
                    String status;
                    switch (status = JSON.readTree(cResp.body()).path("status").asText("")) {
                        case "identical": {
                            result.put("hasUpdate", false);
                            break;
                        }
                        case "ahead": {
                            result.put("hasUpdate", false);
                            result.put("versionNotice", "\u5f53\u524d\u8fd0\u884c\u7248\u672c\u5df2\u65b0\u4e8e\u6216\u7b49\u4e8e GitHub \u4e0a tag latest \u53d1\u5e03\u5305\uff0c\u65e0\u9700\u5728\u7ebf\u66f4\u65b0\u3002");
                            break;
                        }
                        case "behind": {
                            result.put("hasUpdate", true);
                            break;
                        }
                        case "diverged": {
                            result.put("hasUpdate", true);
                            result.put("versionNotice", "\u672c\u5730\u4e0e\u7ebf\u4e0a\u4e0b\u8f7d\u5305\u63d0\u4ea4\u5df2\u5206\u53c9\uff0c\u66f4\u65b0\u524d\u8bf7\u786e\u8ba4\u6570\u636e\u4e0e\u56de\u6eda\u65b9\u5f0f\u3002");
                            break;
                        }
                        default: {
                            result.put("hasUpdate", !this.currentCommit.equalsIgnoreCase(latestCommit));
                            break;
                        }
                    }
                } else {
                    log.warn("GitHub compare \u5931\u8d25 HTTP {}: {}", (Object)cResp.statusCode(), (Object)cResp.body());
                    result.put("hasUpdate", !this.currentCommit.equalsIgnoreCase(latestCommit));
                }
            }
        }
        catch (Exception e) {
            log.warn("Failed to check update: {}", (Object)e.getMessage());
            result.put("latestSize", -1);
            result.put("latestSizeHuman", "\u67e5\u8be2\u5931\u8d25");
            result.put("hasUpdate", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    public void performUpdate() {
        File jarFile = new File(JAR_PATH);
        if (!jarFile.exists()) {
            throw new OciException("\u672a\u627e\u5230 /opt/oci-worker/oci-worker.jar\uff0c\u8bf7\u5148\u901a\u8fc7 deploy.sh \u90e8\u7f72");
        }
        try {
            String script = "#!/bin/bash\nset -e\nREPO=\"%s\"\nASSET=\"%s\"\nJAR=\"%s\"\n# \u76f4\u8fde latest \u8d44\u6e90\uff0c\u907f\u514d\u5148\u8c03 api.github.com + grep\uff08\u7701\u4e00\u6b21 RTT\uff0c\u4e5f\u964d\u4f4e\u9650\u6d41\u6982\u7387\uff09\nJAR_URL=\"https://github.com/${REPO}/releases/download/latest/${ASSET}\"\ncurl -fSL --retry 2 --retry-delay 2 --connect-timeout 15 --max-time 600 -o \"${JAR}.tmp\" \"$JAR_URL\"\nNEW_SIZE=$(stat -c%%s \"${JAR}.tmp\" 2>/dev/null || echo 0)\nif [ \"$NEW_SIZE\" -lt 1000 ]; then\n  rm -f \"${JAR}.tmp\"\n  echo \"Download failed: file too small (${NEW_SIZE} bytes)\"\n  exit 1\nfi\nmv \"${JAR}.tmp\" \"$JAR\"\nsystemctl stop oci-webssh 2>/dev/null || true\nsystemctl disable oci-webssh 2>/dev/null || true\nrm -f /opt/oci-worker/oci-webssh\nrm -f /etc/systemd/system/oci-webssh.service\nsystemctl daemon-reload 2>/dev/null || true\nsystemctl restart oci-worker\n".formatted("xingchenv2/OCX", ASSET_NAME, JAR_PATH);
            Path scriptFile = Path.of("/tmp/oci-worker-update.sh", new String[0]);
            Files.writeString(scriptFile, (CharSequence)script, new OpenOption[0]);
            try {
                Files.setPosixFilePermissions(scriptFile, PosixFilePermissions.fromString("rwxr-xr-x"));
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            new ProcessBuilder("bash", "-c", "nohup bash /tmp/oci-worker-update.sh > /tmp/oci-worker-update.log 2>&1 &").redirectErrorStream(true).start();
            log.info("Update process started in background");
        }
        catch (IOException e) {
            throw new OciException("\u542f\u52a8\u66f4\u65b0\u5931\u8d25: " + e.getMessage());
        }
    }

    private String humanReadableSize(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }
        String[] units = new String[]{"KB", "MB", "GB"};
        double size = bytes;
        for (String unit : units) {
            if (!((size /= 1024.0) < 1024.0)) continue;
            return String.format("%.1f %s", size, unit);
        }
        return String.format("%.1f TB", size / 1024.0);
    }

    public Map<String, Object> getGlance() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        long tenantCount = this.userMapper.selectCount(null);
        long runningTaskCount = this.taskMapper.selectCount((Wrapper)new LambdaQueryWrapper().eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus()));
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
        }
        catch (Exception e) {
            log.warn("Failed to get system info: {}", (Object)e.getMessage());
        }
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration uptime = Duration.ofMillis(uptimeMs);
        result.put("uptime", String.format("%dd %dh %dm", uptime.toDays(), uptime.toHoursPart(), uptime.toMinutesPart()));
        return result;
    }

    public List<Map<String, String>> listOciRegionCatalog(String userId) {
        if (StrUtil.isNotBlank((CharSequence)userId)) {
            return OciRegionCatalog.listUiRowsForIds((Collection)this.storageService.listSubscribedRegionIds(userId));
        }
        return OciRegionCatalog.listUiRows();
    }
}

