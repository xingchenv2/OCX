/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.StrUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.enums.TaskStatusEnum
 *  com.ociworker.mapper.OciCreateTaskMapper
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.entity.OciCreateTask
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.LoginSecurityService
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.SystemService
 *  com.ociworker.service.TelegramBotCommandService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.enums.TaskStatusEnum;
import com.ociworker.mapper.OciCreateTaskMapper;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.entity.OciCreateTask;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.LoginSecurityService;
import com.ociworker.service.NotificationService;
import com.ociworker.service.SystemService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.Collection;
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

/*
 * Exception performing whole class analysis ignored.
 */
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
        String cmd;
        if (!this.verifyCodeService.isTgConfigured()) {
            return;
        }
        if (message == null || !message.has("chat") || !message.has("text")) {
            return;
        }
        String configuredChat = TelegramBotCommandService.normalizeChatIdStr((String)this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID));
        if (StrUtil.isBlank((CharSequence)configuredChat)) {
            return;
        }
        String chatId = TelegramBotCommandService.normalizeChatIdFromMessage((JsonNode)message);
        if (!configuredChat.equals(chatId)) {
            String raw = message.path("text").asText("").trim();
            if (raw.startsWith("/")) {
                log.warn("[TG] \u659c\u6760\u547d\u4ee4\u5df2\u9001\u8fbe\u4f46 chat_id \u4e0d\u5339\u914d\uff1a\u6536\u5230 [{}]\uff0c\u9762\u677f\u914d\u7f6e TG_CHAT_ID=[{}]\u3002\u8bf7\u7528 @userinfobot \u67e5\u770b\u672c\u5bf9\u8bdd id \u5e76\u5199\u5165\u7cfb\u7edf\u8bbe\u7f6e\u3002", (Object)chatId, (Object)configuredChat);
            } else {
                log.debug("[TG] ignore message from chat {}", (Object)chatId);
            }
            return;
        }
        String raw = message.get("text").asText("").trim();
        if (raw.isEmpty()) {
            return;
        }
        String firstToken = raw.split("\\s+")[0];
        String lower = firstToken.toLowerCase();
        switch (cmd = lower.contains("@") ? lower.substring(0, lower.indexOf(64)) : lower) {
            case "/start": {
                this.handleStart();
                break;
            }
            case "/stop": {
                this.handleStop();
                break;
            }
            case "/logs": {
                this.handleLogs();
                break;
            }
            case "/state": {
                this.handleState();
                break;
            }
            case "/bans": {
                this.handleBans();
                break;
            }
        }
    }

    private void handleStart() {
        this.loginSecurityService.setSitePaused(false);
        this.notificationService.sendMessage("OCIWorker\u5df2\u542f\u52a8");
    }

    private void handleBans() {
        this.loginSecurityService.sendDenylistManagementKeyboard();
    }

    private void handleStop() {
        this.loginSecurityService.setSitePaused(true);
        this.notificationService.sendMessage("\u5df2\u6682\u505c\u5168\u7ad9 API \u8bbf\u95ee\u3002");
    }

    private void handleLogs() {
        List list = this.taskMapper.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus())).orderByDesc(OciCreateTask::getCreateTime));
        if (list.isEmpty()) {
            this.notificationService.sendMessage("\u5f53\u524d\u65e0\u8fd0\u884c\u4e2d\u7684\u5f00\u673a\u4efb\u52a1\u3002");
            return;
        }
        Set userIds = list.stream().map(OciCreateTask::getUserId).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toCollection(LinkedHashSet::new));
        HashMap<String, String> idToName = new HashMap<String, String>();
        if (!userIds.isEmpty()) {
            List users = this.userMapper.selectList((Wrapper)new LambdaQueryWrapper().in(OciUser::getId, (Collection)userIds));
            for (Object u : users) {
                idToName.put(u.getId(), StrUtil.blankToDefault((CharSequence)u.getUsername(), (String)u.getId()));
            }
        }
        int tenantN = userIds.size();
        StringBuilder sb = new StringBuilder();
        sb.append("\u5f53\u524d\u6709 ").append(tenantN).append(" \u4e2a\u79df\u6237\u6b63\u5728\u5f00\u673a\uff08").append(list.size()).append(" \u4e2a\u8fd0\u884c\u4e2d\u4efb\u52a1\uff09\u3002\n\n");
        for (String uid : userIds) {
            sb.append("\u00b7 ").append(idToName.getOrDefault(uid, uid)).append('\n');
        }
        Object out = sb.toString().trim();
        if (((String)out).length() > 3800) {
            out = ((String)out).substring(0, 3800) + "\n\u2026";
        }
        this.notificationService.sendMessage((String)out);
    }

    private void handleState() {
        Map g = this.systemService.getGlance();
        boolean paused = this.loginSecurityService.isSitePaused();
        String norm = paused ? "\u5168\u7ad9\u5df2\u6682\u505c" : "\u5168\u7ad9\u8fd0\u884c\u4e2d";
        long tenants = 0L;
        Object tc = g.get("tenantCount");
        if (tc instanceof Number) {
            Number n = (Number)tc;
            tenants = n.longValue();
        }
        long tasks = 0L;
        Object rc = g.get("runningTaskCount");
        if (rc instanceof Number) {
            Number n = (Number)rc;
            tasks = n.longValue();
        }
        String cpu = g.get("cpuUsage") != null ? String.valueOf(g.get("cpuUsage")) : "\u2014";
        String mem = g.get("memoryUsage") != null ? String.valueOf(g.get("memoryUsage")) : "\u2014";
        String msg = String.format("\u72b6\u6001\uff1a%s\n\u79df\u6237\uff1a%d\n\u8fd0\u884c\u4e2d\u4efb\u52a1\uff1a%d\nCPU\uff1a%s%%\n\u5185\u5b58\uff1a%s%%", norm, tenants, tasks, cpu, mem);
        this.notificationService.sendMessage(msg);
    }

    private static String normalizeChatIdFromMessage(JsonNode message) {
        JsonNode id = message.path("chat").path("id");
        return TelegramBotCommandService.normalizeChatIdNode((JsonNode)id);
    }

    private static String normalizeChatIdNode(JsonNode id) {
        if (id == null || id.isMissingNode() || id.isNull()) {
            return "";
        }
        if (id.isNumber()) {
            if (id.isIntegralNumber()) {
                return Long.toString(id.longValue());
            }
            return id.asText("");
        }
        return StrUtil.trim((CharSequence)id.asText(""));
    }

    private static String normalizeChatIdStr(String s) {
        if (StrUtil.isBlank((CharSequence)s)) {
            return "";
        }
        String t = StrUtil.trim((CharSequence)s);
        if (t.startsWith("\"") && t.endsWith("\"") || t.startsWith("'") && t.endsWith("'")) {
            t = t.substring(1, t.length() - 1).trim();
        }
        return t;
    }
}

