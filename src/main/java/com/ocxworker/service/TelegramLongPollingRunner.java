package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ocxworker.enums.SysCfgEnum;
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

    @EventListener({ApplicationReadyEvent.class})
    public void start() {
        Thread t = Thread.ofVirtual().name("oci-tg-getUpdates").unstarted(this::runForever);
        t.setDaemon(true);
        t.start();
        log.info("[TG] getUpdates long-poll thread started");
    }

    private void runForever() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!this.verifyCodeService.isTgConfigured()) {
                    Thread.sleep(5000L);
                } else {
                    String token = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
                    if (StrUtil.isBlank(token)) {
                        Thread.sleep(5000L);
                    } else {
                        if (!token.equals(this.lastWebhookCleanupToken)) {
                            boolean cleared = this.notificationService.telegramDeleteWebhook(token);
                            this.lastWebhookCleanupToken = token;
                            log.info("[TG] deleteWebhook for getUpdates mode: ok={}", cleared);
                        }

                        long nextOffset = this.notificationService.getTelegramUpdatesNextOffset();
                        JSONArray updates = this.notificationService.telegramGetUpdates(token, nextOffset, 25);
                        if (updates == null) {
                            Thread.sleep(2000L);
                        } else {
                            long maxSeen = -1L;

                            for (int i = 0; i < updates.size(); i++) {
                                JSONObject u = updates.getJSONObject(i);
                                if (u != null) {
                                    Long uidObj = u.getLong("update_id");
                                    long uid = uidObj == null ? 0L : uidObj;
                                    if (uid > 0L) {
                                        maxSeen = Math.max(maxSeen, uid);
                                    }

                                    this.telegramInboundUpdateDispatcher.dispatchUpdateJson(u.toString());
                                }
                            }

                            if (maxSeen >= 0L) {
                                this.notificationService.saveTelegramUpdatesNextOffset(maxSeen + 1L);
                            }
                        }
                    }
                }
            } catch (InterruptedException var13) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception var14) {
                log.warn("[TG] getUpdates loop: {}", var14.getMessage());

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException var12) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
