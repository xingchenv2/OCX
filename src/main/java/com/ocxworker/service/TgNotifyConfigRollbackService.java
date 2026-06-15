/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.RandomUtil
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONObject
 *  com.ocxworker.enums.SysCfgEnum
 *  com.ocxworker.service.LoginSecurityService
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.TelegramInboundUpdateDispatcher
 *  com.ocxworker.service.TgNotifyConfigRollbackService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.context.event.EventListener
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.TelegramInboundUpdateDispatcher;
import jakarta.annotation.Resource;
import java.lang.invoke.CallSite;
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

/*
 * Exception performing whole class analysis ignored.
 */
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

    @EventListener(value={ApplicationReadyEvent.class})
    public void resumeOldBotPollerIfNeeded() {
        if (!this.hasRollbackSession() || this.isRollbackExpired()) {
            if (this.hasRollbackSession() && this.isRollbackExpired()) {
                this.clearRollbackState(true);
            }
            return;
        }
        String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN);
        String sessionId = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID);
        if (StrUtil.isNotBlank((CharSequence)oldToken) && StrUtil.isNotBlank((CharSequence)sessionId)) {
            this.startOldBotPoller(oldToken.trim(), sessionId.trim());
            log.info("[TG rollback] resumed old-bot poller for session {}", (Object)sessionId);
        }
    }

    public void applyIdentityChange(String oldBotToken, String oldChatId, String newBotToken, String newChatId, String offenderIp, String offenderDeviceId) {
        this.clearRollbackState(false);
        String sessionId = RandomUtil.randomString((String)"abcdef0123456789", (int)16);
        long expireAt = System.currentTimeMillis() + 900000L;
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID, sessionId);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN, oldBotToken);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_OLD_CHAT_ID, oldChatId);
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_EXPIRE_AT, String.valueOf(expireAt));
        this.notificationService.removeKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET);
        this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, newBotToken);
        this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, newChatId);
        this.notificationService.resetTelegramUpdatesOffset();
        String alertText = TgNotifyConfigRollbackService.formatIdentityChangedAlert((String)offenderIp, (String)offenderDeviceId);
        List rows = this.buildAlertKeyboard(offenderIp, sessionId);
        this.notificationService.sendSecurityTextWithInlineKeyboard(oldBotToken, oldChatId, alertText, rows);
        this.startOldBotPoller(oldBotToken.trim(), sessionId);
        log.info("[TG rollback] identity change applied; session={} expireAt={}", (Object)sessionId, (Object)expireAt);
    }

    public boolean tryHandleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (rawData == null || !rawData.startsWith("n|")) {
            return false;
        }
        String token = rawData.substring("n|".length());
        if (token.length() > 32) {
            this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "\u65e0\u6548\u64cd\u4f5c", false, answeringBotToken);
            return true;
        }
        if (!this.isRollbackSessionValid(token)) {
            this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "\u64cd\u4f5c\u5df2\u8fc7\u671f\uff08\u8d85\u8fc7 15 \u5206\u949f\u6216\u5df2\u5904\u7406\uff09", false, answeringBotToken);
            return true;
        }
        this.rejectAndRestore();
        this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "\u5df2\u62d2\u7edd\u66f4\u6539\uff0cTelegram \u901a\u77e5\u5df2\u6062\u590d\u4e3a\u539f\u914d\u7f6e", false, answeringBotToken);
        return true;
    }

    @Scheduled(fixedRate=60000L)
    public void purgeExpiredRollback() {
        if (!this.hasRollbackSession()) {
            return;
        }
        if (this.isRollbackExpired()) {
            log.info("[TG rollback] session expired, clearing staged old config");
            this.clearRollbackState(true);
        }
    }

    private void rejectAndRestore() {
        String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_BOT_TOKEN);
        String oldChatId = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_OLD_CHAT_ID);
        if (StrUtil.isBlank((CharSequence)oldToken) || StrUtil.isBlank((CharSequence)oldChatId)) {
            this.clearRollbackState(true);
            return;
        }
        this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, oldToken.trim());
        this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, oldChatId.trim());
        this.notificationService.resetTelegramUpdatesOffset();
        this.clearRollbackState(true);
        log.warn("[TG rollback] notify config reverted to previous bot/chat via TG reject");
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
                    this.telegramInboundUpdateDispatcher.dispatchUpdateJson(u.toString(), oldBotToken);
                }
                if (maxSeen < 0L) continue;
                this.saveRollbackOffset(maxSeen + 1L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception e) {
                log.warn("[TG rollback] old bot getUpdates: {}", (Object)e.getMessage());
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

    private List<List<Map<String, String>>> buildAlertKeyboard(String offenderIp, String sessionId) {
        ArrayList<Map<String, CallSite>> row = new ArrayList<Map<String, CallSite>>();
        String blockTok = this.loginSecurityService.registerBlockIpCallback(offenderIp);
        if (blockTok != null) {
            row.add(Map.of("text", "\u62c9\u9ed1\u8be5IP", "callback_data", "i|" + blockTok));
        }
        row.add(Map.of("text", "\u62d2\u7edd\u66f4\u6539", "callback_data", "n|" + sessionId));
        return List.of(row);
    }

    private static String formatIdentityChangedAlert(String ip, String deviceId) {
        String ipLine = StrUtil.isNotBlank((CharSequence)ip) ? ip.trim() : "\u2014";
        String devLine = StrUtil.isNotBlank((CharSequence)deviceId) ? deviceId.trim() : "\u2014";
        return "\u3010OCI WORKER \u5b89\u5168\u63d0\u793a\u3011\nTelegram \u901a\u77e5\u914d\u7f6e\u5df2\u66f4\u6539\uff01\n\u5982\u975e\u672c\u4eba\u64cd\u4f5c\uff0c\u8bf7\u7acb\u5373\u5904\u7406\u3002\nIP: " + ipLine + "\n\u8bbe\u5907: " + devLine + "\n\n\uff0815 \u5206\u949f\u5185\u53ef\u70b9\u300c\u62d2\u7edd\u66f4\u6539\u300d\u6062\u590d\u539f\u914d\u7f6e\uff09";
    }

    private boolean hasRollbackSession() {
        return StrUtil.isNotBlank((CharSequence)this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID));
    }

    private boolean isRollbackExpired() {
        String exp = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_EXPIRE_AT);
        if (StrUtil.isBlank((CharSequence)exp)) {
            return true;
        }
        try {
            return System.currentTimeMillis() > Long.parseLong(exp.trim());
        }
        catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isRollbackSessionValid(String sessionId) {
        if (StrUtil.isBlank((CharSequence)sessionId) || this.isRollbackExpired()) {
            return false;
        }
        String stored = this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_SESSION_ID);
        return sessionId.equals(StrUtil.trimToNull((CharSequence)stored));
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
        String v = StrUtil.trimToNull((CharSequence)this.notificationService.getKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET));
        if (v == null) {
            return 0L;
        }
        try {
            return Long.parseLong(v);
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    private void saveRollbackOffset(long nextOffset) {
        if (nextOffset <= 0L) {
            return;
        }
        this.notificationService.saveKvValue(SysCfgEnum.TG_ROLLBACK_UPDATES_OFFSET, String.valueOf(nextOffset));
    }
}

