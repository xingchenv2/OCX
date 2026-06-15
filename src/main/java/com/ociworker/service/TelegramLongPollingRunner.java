/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONObject
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.TelegramInboundUpdateDispatcher
 *  com.ociworker.service.TelegramLongPollingRunner
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.context.event.EventListener
 *  org.springframework.stereotype.Component
 */
package com.ociworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.service.NotificationService;
import com.ociworker.service.TelegramInboundUpdateDispatcher;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TelegramLongPollingRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TelegramLongPollingRunner.class);
    private static final int LONG_POLL_TIMEOUT_SEC = 25;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private TelegramInboundUpdateDispatcher telegramInboundUpdateDispatcher;
    private volatile String lastWebhookCleanupToken;

    @EventListener(value={ApplicationReadyEvent.class})
    public void start() {
        Thread t = Thread.ofVirtual().name("oci-tg-getUpdates").unstarted(() -> this.runForever());
        t.setDaemon(true);
        t.start();
        log.info("[TG] getUpdates long-poll thread started");
    }

    private void runForever() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                long nextOffset;
                JSONArray updates;
                if (!this.verifyCodeService.isTgConfigured()) {
                    Thread.sleep(5000L);
                    continue;
                }
                String token = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
                if (StrUtil.isBlank((CharSequence)token)) {
                    Thread.sleep(5000L);
                    continue;
                }
                if (!token.equals(this.lastWebhookCleanupToken)) {
                    boolean cleared = this.notificationService.telegramDeleteWebhook(token);
                    this.lastWebhookCleanupToken = token;
                    log.info("[TG] deleteWebhook for getUpdates mode: ok={}", (Object)cleared);
                }
                if ((updates = this.notificationService.telegramGetUpdates(token, nextOffset = this.notificationService.getTelegramUpdatesNextOffset(), 25)) == null) {
                    Thread.sleep(2000L);
                    continue;
                }
                long maxSeen = -1L;
                for (int i = 0; i < updates.size(); ++i) {
                    long uid;
                    JSONObject u = updates.getJSONObject((Object)i);
                    if (u == null) continue;
                    Long uidObj = u.getLong((Object)"update_id");
                    long l = uid = uidObj == null ? 0L : uidObj;
                    if (uid > 0L) {
                        maxSeen = Math.max(maxSeen, uid);
                    }
                    this.telegramInboundUpdateDispatcher.dispatchUpdateJson(u.toString());
                }
                if (maxSeen < 0L) continue;
                this.notificationService.saveTelegramUpdatesNextOffset(maxSeen + 1L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception e) {
                log.warn("[TG] getUpdates loop: {}", (Object)e.getMessage());
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}

