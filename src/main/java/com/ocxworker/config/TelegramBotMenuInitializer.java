/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.TelegramBotMenuInitializer
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.context.event.EventListener
 *  org.springframework.stereotype.Component
 */
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

    @EventListener(value={ApplicationReadyEvent.class})
    public void onApplicationReady() {
        if (!this.verifyCodeService.isTgConfigured()) {
            return;
        }
        try {
            this.notificationService.registerTelegramBotCommands();
        }
        catch (Exception e) {
            log.warn("Telegram setMyCommands skipped: {}", (Object)e.getMessage());
        }
    }
}

