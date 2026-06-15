/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.service.LoginSecurityService
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.ShapeEditTaskManager
 *  com.ocxworker.service.TelegramBotCommandService
 *  com.ocxworker.service.TelegramInboundUpdateDispatcher
 *  com.ocxworker.service.TgNotifyConfigRollbackService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.ShapeEditTaskManager;
import com.ocxworker.service.TelegramBotCommandService;
import com.ocxworker.service.TgNotifyConfigRollbackService;
import jakarta.annotation.Resource;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramInboundUpdateDispatcher {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TelegramInboundUpdateDispatcher.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource
    private LoginSecurityService loginSecurityService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private TelegramBotCommandService telegramBotCommandService;
    @Resource
    private TgNotifyConfigRollbackService tgNotifyConfigRollbackService;
    @Resource
    private ShapeEditTaskManager shapeEditTaskManager;

    public void dispatchUpdateJson(String updateJson) {
        this.dispatchUpdateJson(updateJson, null);
    }

    public void dispatchUpdateJson(String updateJson, String receivingBotToken) {
        if (updateJson == null || updateJson.isBlank()) {
            return;
        }
        try {
            this.dispatchUpdate(MAPPER.readTree(updateJson), receivingBotToken);
        }
        catch (Exception e) {
            log.warn("[TG] parse update failed: {}", (Object)e.getMessage());
        }
    }

    public void dispatchUpdate(JsonNode root) {
        this.dispatchUpdate(root, null);
    }

    public void dispatchUpdate(JsonNode root, String receivingBotToken) {
        try {
            JsonNode msg;
            JsonNode cq = root.get("callback_query");
            if (cq != null && cq.has("id") && cq.has("data")) {
                String id = cq.get("id").asText();
                String data = cq.get("data").asText();
                if ("copy_noop".equals(data)) {
                    this.notificationService.answerTelegramCallbackQuery(id, "", false, receivingBotToken);
                } else if (!this.tgNotifyConfigRollbackService.tryHandleTelegramCallback(data, id, receivingBotToken) && !this.shapeEditTaskManager.tryHandleTelegramCallback(data, id, receivingBotToken)) {
                    this.loginSecurityService.handleTelegramCallback(data, id, receivingBotToken);
                }
            }
            if ((msg = root.get("message")) == null) {
                msg = root.get("edited_message");
            }
            if (msg != null) {
                this.telegramBotCommandService.handleTelegramMessage(msg);
            }
        }
        catch (Exception e) {
            log.warn("[TG] dispatch update error: {}", (Object)e.getMessage());
        }
    }
}

