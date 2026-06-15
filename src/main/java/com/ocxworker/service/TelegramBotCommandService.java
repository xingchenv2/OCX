package com.ocxworker.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.enums.TaskStatusEnum;
import com.ocxworker.mapper.OciCreateTaskMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciCreateTask;
import com.ocxworker.model.entity.OciUser;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotCommandService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TelegramBotCommandService.class);
    private static final int TG_TEXT_MAX = 3800;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private LoginSecurityService loginSecurityService;
    @Resource
    private SystemService systemService;
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private OciUserMapper userMapper;

    public void handleTelegramMessage(JsonNode message) {
        if (this.verifyCodeService.isTgConfigured()) {
            if (message != null && message.has("chat") && message.has("text")) {
                String configuredChat = normalizeChatIdStr(this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID));
                if (!StrUtil.isBlank(configuredChat)) {
                    String chatId = normalizeChatIdFromMessage(message);
                    if (!configuredChat.equals(chatId)) {
                        String raw = message.path("text").asText("").trim();
                        if (raw.startsWith("/")) {
                            log.warn("[TG] 斜杠命令已送达但 chat_id 不匹配：收到 [{}]，面板配置 TG_CHAT_ID=[{}]。请用 @userinfobot 查看本对话 id 并写入系统设置。", chatId, configuredChat);
                        } else {
                            log.debug("[TG] ignore message from chat {}", chatId);
                        }
                    } else {
                        String raw = message.get("text").asText("").trim();
                        if (!raw.isEmpty()) {
                            String firstToken = raw.split("\\s+")[0];
                            String lower = firstToken.toLowerCase();
                            String cmd = lower.contains("@") ? lower.substring(0, lower.indexOf(64)) : lower;
                            switch (cmd) {
                                case "/start":
                                    this.handleStart();
                                    break;
                                case "/stop":
                                    this.handleStop();
                                    break;
                                case "/logs":
                                    this.handleLogs();
                                    break;
                                case "/state":
                                    this.handleState();
                                    break;
                                case "/bans":
                                    this.handleBans();
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleStart() {
        this.loginSecurityService.setSitePaused(false);
        this.notificationService.sendMessage("OCIWorker已启动");
    }

    private void handleBans() {
        this.loginSecurityService.sendDenylistManagementKeyboard();
    }

    private void handleStop() {
        this.loginSecurityService.setSitePaused(true);
        this.notificationService.sendMessage("已暂停全站 API 访问。");
    }

    private void handleLogs() {
        List<OciCreateTask> list = this.taskMapper
            .selectList(
                (Wrapper)(new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()))
                    .orderByDesc(OciCreateTask::getCreateTime)
            );
        if (list.isEmpty()) {
            this.notificationService.sendMessage("当前无运行中的开机任务。");
        } else {
            Set<String> userIds = list.stream()
                .map(OciCreateTask::getUserId)
                .filter(CharSequenceUtil::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            Map<String, String> idToName = new HashMap<>();
            if (!userIds.isEmpty()) {
                for (OciUser u : this.userMapper.selectList((Wrapper)new LambdaQueryWrapper<OciUser>().in(OciUser::getId, userIds))) {
                    idToName.put(u.getId(), StrUtil.blankToDefault(u.getUsername(), u.getId()));
                }
            }

            int tenantN = userIds.size();
            StringBuilder sb = new StringBuilder();
            sb.append("当前有 ").append(tenantN).append(" 个租户正在开机（").append(list.size()).append(" 个运行中任务）。\n\n");

            for (String uid : userIds) {
                sb.append("· ").append(idToName.getOrDefault(uid, uid)).append('\n');
            }

            String out = sb.toString().trim();
            if (out.length() > 3800) {
                out = out.substring(0, 3800) + "\n…";
            }

            this.notificationService.sendMessage(out);
        }
    }

    private void handleState() {
        Map<String, Object> g = this.systemService.getGlance();
        boolean paused = this.loginSecurityService.isSitePaused();
        String norm = paused ? "全站已暂停" : "全站运行中";
        long tenants = 0L;
        if (g.get("tenantCount") instanceof Number n) {
            tenants = n.longValue();
        }

        long tasks = 0L;
        if (g.get("runningTaskCount") instanceof Number n) {
            tasks = n.longValue();
        }

        String cpu = g.get("cpuUsage") != null ? String.valueOf(g.get("cpuUsage")) : "—";
        String mem = g.get("memoryUsage") != null ? String.valueOf(g.get("memoryUsage")) : "—";
        String msg = String.format("状态：%s\n租户：%d\n运行中任务：%d\nCPU：%s%%\n内存：%s%%", norm, tenants, tasks, cpu, mem);
        this.notificationService.sendMessage(msg);
    }

    private static String normalizeChatIdFromMessage(JsonNode message) {
        JsonNode id = message.path("chat").path("id");
        return normalizeChatIdNode(id);
    }

    private static String normalizeChatIdNode(JsonNode id) {
        if (id == null || id.isMissingNode() || id.isNull()) {
            return "";
        } else if (!id.isNumber()) {
            return StrUtil.trim(id.asText(""));
        } else {
            return id.isIntegralNumber() ? Long.toString(id.longValue()) : id.asText("");
        }
    }

    private static String normalizeChatIdStr(String s) {
        if (StrUtil.isBlank(s)) {
            return "";
        } else {
            String t = StrUtil.trim(s);
            if (t.startsWith("\"") && t.endsWith("\"") || t.startsWith("'") && t.endsWith("'")) {
                t = t.substring(1, t.length() - 1).trim();
            }

            return t;
        }
    }
}
