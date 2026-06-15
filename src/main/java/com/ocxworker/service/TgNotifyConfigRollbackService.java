package com.ocxworker.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ocxworker.enums.SysCfgEnum;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TgNotifyConfigRollbackService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TgNotifyConfigRollbackService.class);
    public static final long ROLLBACK_TTL_MS = 900000L;
    private static final String CALLBACK_PREFIX_REJECT = "n|";
    @Resource
    private NotificationService notificationService;
    @Resource
    private LoginSecurityService loginSecurityService;
    @Lazy
    @Resource
    private TelegramInboundUpdateDispatcher telegramInboundUpdateDispatcher;
    private volatile String pollerSessionId;
    private volatile Thread oldBotPollerThread;

    @EventListener({ApplicationReadyEvent.class})
    public void resumeOldBotPollerIfNeeded() {
        if (this.hasRollbackSession() && !this.isRollbackExpired()) {
            String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN);
            String sessionId = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID);
            if (StrUtil.isNotBlank(oldToken) && StrUtil.isNotBlank(sessionId)) {
                this.startOldBotPoller(oldToken.trim(), sessionId.trim());
                log.info("[TG rollback] resumed old-bot poller for session {}", sessionId);
            }
        } else {
            if (this.hasRollbackSession() && this.isRollbackExpired()) {
                this.clearRollbackState(true);
            }
        }
    }

    public void applyIdentityChange(String oldBotToken, String oldChatId, String newBotToken, String newChatId, String offenderIp, String offenderDeviceId) {
        this.clearRollbackState(false);
        String sessionId = RandomUtil.randomString("abcdef0123456789", 16);
        long expireAt = System.currentTimeMillis() + 900000L;
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID, sessionId);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN, oldBotToken);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_OLD_CHAT_ID, oldChatId);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_EXPIRE_AT, String.valueOf(expireAt));
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET);
        this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, newBotToken);
        this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, newChatId);
        this.notificationService.resetTelegramUpdatesOffset();
        String alertText = formatIdentityChangedAlert(offenderIp, offenderDeviceId);
        List<List<Map<String, String>>> rows = this.buildAlertKeyboard(offenderIp, sessionId);
        this.notificationService.sendSecurityTextWithInlineKeyboard(oldBotToken, oldChatId, alertText, rows);
        this.startOldBotPoller(oldBotToken.trim(), sessionId);
        log.info("[TG rollback] identity change applied; session={} expireAt={}", sessionId, expireAt);
    }

    public boolean tryHandleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (rawData != null && rawData.startsWith("n|")) {
            String token = rawData.substring("n|".length());
            if (token.length() > 32) {
                this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "无效操作", false, answeringBotToken);
                return true;
            } else if (!this.isRollbackSessionValid(token)) {
                this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "操作已过期（超过 15 分钟或已处理）", false, answeringBotToken);
                return true;
            } else {
                this.rejectAndRestore();
                this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "已拒绝更改，Telegram 通知已恢复为原配置", false, answeringBotToken);
                return true;
            }
        } else {
            return false;
        }
    }

    @Scheduled(
        fixedRate = 60000L
    )
    public void purgeExpiredRollback() {
        if (this.hasRollbackSession()) {
            if (this.isRollbackExpired()) {
                log.info("[TG rollback] session expired, clearing staged old config");
                this.clearRollbackState(true);
            }
        }
    }

    private void rejectAndRestore() {
        String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN);
        String oldChatId = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_CHAT_ID);
        if (!StrUtil.isBlank(oldToken) && !StrUtil.isBlank(oldChatId)) {
            this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, oldToken.trim());
            this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, oldChatId.trim());
            this.notificationService.resetTelegramUpdatesOffset();
            this.clearRollbackState(true);
            log.warn("[TG rollback] notify config reverted to previous bot/chat via TG reject");
        } else {
            this.clearRollbackState(true);
        }
    }

    private synchronized void startOldBotPoller(String oldBotToken, String sessionId) {
        this.stopOldBotPoller();
        this.pollerSessionId = sessionId;
        Thread t = Thread.ofVirtual().name("oci-tg-rollback-getUpdates").unstarted(() -> this.pollOldBot(oldBotToken, sessionId));
        t.setDaemon(true);
        this.oldBotPollerThread = t;
        t.start();
    }

    private synchronized void stopOldBotPoller() {
        this.pollerSessionId = null;
        Thread t = this.oldBotPollerThread;
        this.oldBotPollerThread = null;
        if (t != null) {
            t.interrupt();
        }
    }

    private void pollOldBot(String oldBotToken, String sessionId) {
        while (Objects.equals(sessionId, this.pollerSessionId) && this.isRollbackSessionValid(sessionId)) {
            try {
                long offset = this.readRollbackOffset();
                JSONArray updates = this.notificationService.telegramGetUpdates(oldBotToken, offset, 25);
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

                            this.telegramInboundUpdateDispatcher.dispatchUpdateJson(u.toString(), oldBotToken);
                        }
                    }

                    if (maxSeen >= 0L) {
                        this.saveRollbackOffset(maxSeen + 1L);
                    }
                }
            } catch (InterruptedException var14) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception var15) {
                log.warn("[TG rollback] old bot getUpdates: {}", var15.getMessage());

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException var13) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private List<List<Map<String, String>>> buildAlertKeyboard(String offenderIp, String sessionId) {
        List<Map<String, String>> row = new ArrayList<>();
        String blockTok = this.loginSecurityService.registerBlockIpCallback(offenderIp);
        if (blockTok != null) {
            row.add(Map.of("text", "拉黑该IP", "callback_data", "i|" + blockTok));
        }

        row.add(Map.of("text", "拒绝更改", "callback_data", "n|" + sessionId));
        return List.of(row);
    }

    private static String formatIdentityChangedAlert(String ip, String deviceId) {
        String ipLine = StrUtil.isNotBlank(ip) ? ip.trim() : "—";
        String devLine = StrUtil.isNotBlank(deviceId) ? deviceId.trim() : "—";
        return "【OCI WORKER 安全提示】\nTelegram 通知配置已更改！\n如非本人操作，请立即处理。\nIP: " + ipLine + "\n设备: " + devLine + "\n\n（15 分钟内可点「拒绝更改」恢复原配置）";
    }

    private boolean hasRollbackSession() {
        return StrUtil.isNotBlank(this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID));
    }

    private boolean isRollbackExpired() {
        String exp = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_EXPIRE_AT);
        if (StrUtil.isBlank(exp)) {
            return true;
        } else {
            try {
                return System.currentTimeMillis() > Long.parseLong(exp.trim());
            } catch (NumberFormatException var3) {
                return true;
            }
        }
    }

    private boolean isRollbackSessionValid(String sessionId) {
        if (!StrUtil.isBlank(sessionId) && !this.isRollbackExpired()) {
            String stored = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID);
            return sessionId.equals(StrUtil.trimToNull(stored));
        } else {
            return false;
        }
    }

    private void clearRollbackState(boolean stopPoller) {
        if (stopPoller) {
            this.stopOldBotPoller();
        }

        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID);
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN);
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_OLD_CHAT_ID);
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_EXPIRE_AT);
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET);
    }

    private long readRollbackOffset() {
        String v = StrUtil.trimToNull(this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET));
        if (v == null) {
            return 0L;
        } else {
            try {
                return Long.parseLong(v);
            } catch (NumberFormatException var3) {
                return 0L;
            }
        }
    }

    private void saveRollbackOffset(long nextOffset) {
        if (nextOffset > 0L) {
            this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET, String.valueOf(nextOffset));
        }
    }
}
