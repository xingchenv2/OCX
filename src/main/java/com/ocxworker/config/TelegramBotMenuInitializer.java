package com.ocxworker.config;

import com.ocxworker.service.NotificationService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotMenuInitializer {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TelegramBotMenuInitializer.class);
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private NotificationService notificationService;

    @EventListener({ApplicationReadyEvent.class})
    public void onApplicationReady() {
        if (this.verifyCodeService.isTgConfigured()) {
            try {
                this.notificationService.registerTelegramBotCommands();
            } catch (Exception var2) {
                log.warn("Telegram setMyCommands skipped: {}", var2.getMessage());
            }
        }
    }
}
