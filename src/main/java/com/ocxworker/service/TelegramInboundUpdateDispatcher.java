package com.ocxworker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        if (updateJson != null && !updateJson.isBlank()) {
            try {
                this.dispatchUpdate(MAPPER.readTree(updateJson), receivingBotToken);
            } catch (Exception var4) {
                log.warn("[TG] parse update failed: {}", var4.getMessage());
            }
        }
    }

    public void dispatchUpdate(JsonNode root) {
        this.dispatchUpdate(root, null);
    }

    public void dispatchUpdate(JsonNode root, String receivingBotToken) {
        try {
            JsonNode cq = root.get("callback_query");
            if (cq != null && cq.has("id") && cq.has("data")) {
                String id = cq.get("id").asText();
                String data = cq.get("data").asText();
                if ("copy_noop".equals(data)) {
                    this.notificationService.answerTelegramCallbackQuery(id, "", false, receivingBotToken);
                } else if (!this.tgNotifyConfigRollbackService.tryHandleTelegramCallback(data, id, receivingBotToken)
                    && !this.shapeEditTaskManager.tryHandleTelegramCallback(data, id, receivingBotToken)) {
                    this.loginSecurityService.handleTelegramCallback(data, id, receivingBotToken);
                }
            }

            JsonNode msg = root.get("message");
            if (msg == null) {
                msg = root.get("edited_message");
            }

            if (msg != null) {
                this.telegramBotCommandService.handleTelegramMessage(msg);
            }
        } catch (Exception var6) {
            log.warn("[TG] dispatch update error: {}", var6.getMessage());
        }
    }
}
