/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONObject
 *  cn.hutool.json.JSONUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.ocxworker.enums.SysCfgEnum
 *  com.ocxworker.mapper.OciKvMapper
 *  com.ocxworker.model.entity.OciKv
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.OciProxyConfigService
 *  com.ocxworker.util.CommonUtils
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_TASK_CREATE = "task_create";
    public static final String TYPE_TASK_RESULT = "task_result";
    public static final String TYPE_DAILY_REPORT = "daily_report";
    public static final String TYPE_INSTANCE = "instance";
    @Resource
    private OciKvMapper kvMapper;
    @Lazy
    @Resource
    private OciProxyConfigService ociProxyConfigService;

    public void sendMessage(String notifyType, String message) {
        if (!this.isTypeEnabled(notifyType)) {
            return;
        }
        this.sendTelegram(message);
    }

    public void sendMessage(String message) {
        this.sendTelegram(message);
    }

    public boolean isNotifyTypeEnabled(String notifyType) {
        if (StrUtil.isBlank((CharSequence)notifyType)) {
            return false;
        }
        String types = this.getKvValue(SysCfgEnum.TG_NOTIFY_TYPES);
        if (StrUtil.isBlank((CharSequence)types)) {
            return true;
        }
        for (String t : types.split(",")) {
            if (!notifyType.equals(t.trim())) continue;
            return true;
        }
        return false;
    }

    private boolean isTypeEnabled(String notifyType) {
        return this.isNotifyTypeEnabled(notifyType);
    }

    public void sendTelegramPlain(String botToken, String chatId, String message) {
        if (StrUtil.isBlank((CharSequence)botToken) || StrUtil.isBlank((CharSequence)chatId) || StrUtil.isBlank((CharSequence)message)) {
            return;
        }
        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(Map.of("chat_id", chatId, "text", message)))).timeout(Duration.ofSeconds(10L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
        }
        catch (Exception e) {
            log.warn("Failed to send Telegram message to explicit chat: {}", (Object)e.getMessage());
        }
    }

    private void sendTelegram(String message) {
        this.sendTelegramPlain(this.getKvValue(SysCfgEnum.TG_BOT_TOKEN), this.getKvValue(SysCfgEnum.TG_CHAT_ID), message);
    }

    public void sendHtmlWithType(String notifyType, String html) {
        if (!this.isTypeEnabled(notifyType)) {
            return;
        }
        this.sendTelegramHtml(html, null);
    }

    public void sendHtmlWithTypeAndInlineKeyboard(String notifyType, String html, List<List<Map<String, String>>> inlineKeyboard) {
        if (!this.isTypeEnabled(notifyType)) {
            return;
        }
        try {
            String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            String chatId = this.getKvValue(SysCfgEnum.TG_CHAT_ID);
            if (StrUtil.isBlank((CharSequence)botToken) || StrUtil.isBlank((CharSequence)chatId) || StrUtil.isBlank((CharSequence)html)) {
                return;
            }
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("chat_id", chatId);
            body.put("text", html);
            body.put("parse_mode", "HTML");
            if (inlineKeyboard != null && !inlineKeyboard.isEmpty()) {
                body.put("reply_markup", Map.of("inline_keyboard", inlineKeyboard));
            }
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body))).timeout(Duration.ofSeconds(10L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
        }
        catch (Exception e) {
            log.warn("Failed to send Telegram HTML keyboard message: {}", (Object)e.getMessage());
        }
    }

    public void sendTelegramHtml(String html, String copyText) {
        try {
            String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            String chatId = this.getKvValue(SysCfgEnum.TG_CHAT_ID);
            if (StrUtil.isBlank((CharSequence)botToken) || StrUtil.isBlank((CharSequence)chatId)) {
                return;
            }
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("chat_id", chatId);
            body.put("text", html);
            body.put("parse_mode", "HTML");
            if (StrUtil.isNotBlank((CharSequence)copyText)) {
                body.put("reply_markup", Map.of("inline_keyboard", List.of(List.of(Map.of("text", "\ud83d\udccb \u590d\u5236\u9a8c\u8bc1\u7801", "callback_data", "copy_noop", "copy_text", copyText)))));
            }
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body))).timeout(Duration.ofSeconds(10L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
        }
        catch (Exception e) {
            log.warn("Failed to send Telegram HTML message: {}", (Object)e.getMessage());
        }
    }

    public String getKvValue(SysCfgEnum cfg) {
        OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)cfg.getCode())).eq(OciKv::getType, (Object)cfg.getType())).last("LIMIT 1"));
        return kv != null ? kv.getValue() : null;
    }

    public void saveKvValue(SysCfgEnum cfg, String value) {
        OciKv existing = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)cfg.getCode())).eq(OciKv::getType, (Object)cfg.getType())).last("LIMIT 1"));
        if (existing != null) {
            existing.setValue(value);
            this.kvMapper.updateById((Object)existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode(cfg.getCode());
            kv.setType(cfg.getType());
            kv.setValue(value);
            this.kvMapper.insert((Object)kv);
        }
    }

    public void removeKvValue(SysCfgEnum cfg) {
        this.kvMapper.delete((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)cfg.getCode())).eq(OciKv::getType, (Object)cfg.getType()));
    }

    public void sendSecurityTextWithInlineKeyboard(String text, List<List<Map<String, String>>> inlineKeyboard) {
        this.sendSecurityTextWithInlineKeyboard(this.getKvValue(SysCfgEnum.TG_BOT_TOKEN), this.getKvValue(SysCfgEnum.TG_CHAT_ID), text, inlineKeyboard);
    }

    public void sendSecurityTextWithInlineKeyboard(String botToken, String chatId, String text, List<List<Map<String, String>>> inlineKeyboard) {
        if (StrUtil.isBlank((CharSequence)botToken) || StrUtil.isBlank((CharSequence)chatId) || StrUtil.isBlank((CharSequence)text)) {
            return;
        }
        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("chat_id", chatId);
            body.put("text", text);
            body.put("reply_markup", Map.of("inline_keyboard", inlineKeyboard));
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body))).timeout(Duration.ofSeconds(15L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
        }
        catch (Exception e) {
            log.warn("Failed to send Telegram security keyboard message: {}", (Object)e.getMessage());
        }
    }

    public void answerTelegramCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        this.answerTelegramCallbackQuery(callbackQueryId, text, showAlert, null);
    }

    public void answerTelegramCallbackQuery(String callbackQueryId, String text, boolean showAlert, String botTokenOverride) {
        try {
            Object t;
            String botToken;
            String string = botToken = StrUtil.isNotBlank((CharSequence)botTokenOverride) ? botTokenOverride : this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            if (StrUtil.isBlank((CharSequence)botToken) || StrUtil.isBlank((CharSequence)callbackQueryId)) {
                return;
            }
            String url = String.format("https://api.telegram.org/bot%s/answerCallbackQuery", botToken);
            Object object = t = text == null ? "" : text;
            if (((String)t).length() > 180) {
                t = ((String)t).substring(0, 177) + "...";
            }
            LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
            body.put("callback_query_id", callbackQueryId);
            body.put("text", t);
            body.put("show_alert", showAlert);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body))).timeout(Duration.ofSeconds(10L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
        }
        catch (Exception e) {
            log.warn("Failed to answer Telegram callback: {}", (Object)e.getMessage());
        }
    }

    public void registerTelegramBotCommands() {
        try {
            String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            if (StrUtil.isBlank((CharSequence)botToken)) {
                return;
            }
            String url = String.format("https://api.telegram.org/bot%s/setMyCommands", botToken);
            ArrayList<Map<String, String>> commands = new ArrayList<Map<String, String>>();
            commands.add(Map.of("command", "start", "description", "\u542f\u52a8OCX"));
            commands.add(Map.of("command", "stop", "description", "\u6682\u505c\u5168\u7ad9\u8bbf\u95ee"));
            commands.add(Map.of("command", "logs", "description", "\u62a2\u673a\u4efb\u52a1"));
            commands.add(Map.of("command", "state", "description", "\u7cfb\u7edf\u72b6\u6001"));
            commands.add(Map.of("command", "bans", "description", "\u7981\u6b62\u540d\u5355\u4e0e\u89e3\u9664"));
            Map body = Map.of("commands", commands);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body))).timeout(Duration.ofSeconds(15L)).build();
            c.send(req, HttpResponse.BodyHandlers.discarding());
            log.info("Telegram setMyCommands registered (start/stop/logs/state/bans)");
        }
        catch (Exception e) {
            log.warn("Failed to register Telegram bot commands: {}", (Object)e.getMessage());
        }
    }

    public void resetTelegramUpdatesOffset() {
        this.saveKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET, "");
    }

    public long getTelegramUpdatesNextOffset() {
        String v = StrUtil.trimToNull((CharSequence)this.getKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET));
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

    public void saveTelegramUpdatesNextOffset(long nextOffset) {
        if (nextOffset <= 0L) {
            return;
        }
        this.saveKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET, String.valueOf(nextOffset));
    }

    public boolean telegramDeleteWebhook(String botToken) {
        if (StrUtil.isBlank((CharSequence)botToken)) {
            return false;
        }
        try {
            String url = String.format("https://api.telegram.org/bot%s/deleteWebhook?drop_pending_updates=false", botToken);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().timeout(Duration.ofSeconds(20L)).build();
            HttpResponse<String> resp = c.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200 || resp.body() == null) {
                return false;
            }
            JSONObject root = JSONUtil.parseObj((String)resp.body());
            return root.getBool((Object)"ok", Boolean.valueOf(false));
        }
        catch (Exception e) {
            log.warn("[TG] deleteWebhook failed: {}", (Object)e.getMessage());
            return false;
        }
    }

    public JSONArray telegramGetUpdates(String botToken, long offset, int timeoutSec) {
        if (StrUtil.isBlank((CharSequence)botToken)) {
            return null;
        }
        try {
            HttpRequest req;
            HttpClient c;
            HttpResponse<String> resp;
            String allowedEnc = URLEncoder.encode("[\"message\",\"callback_query\"]", StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.US, "https://api.telegram.org/bot%s/getUpdates?timeout=%d&allowed_updates=%s", botToken, timeoutSec, allowedEnc));
            if (offset > 0L) {
                sb.append("&offset=").append(offset);
            }
            if ((resp = (c = this.ociProxyConfigService.newOutboundHttpClient()).send(req = HttpRequest.newBuilder(URI.create(sb.toString())).GET().timeout(Duration.ofSeconds((long)timeoutSec + 45L)).build(), HttpResponse.BodyHandlers.ofString())).statusCode() != 200 || resp.body() == null) {
                log.warn("[TG] getUpdates HTTP {}", (Object)resp.statusCode());
                return null;
            }
            JSONObject root = JSONUtil.parseObj((String)resp.body());
            if (!root.getBool((Object)"ok", Boolean.valueOf(false)).booleanValue()) {
                log.warn("[TG] getUpdates ok=false: {}", (Object)root.getStr((Object)"description"));
                return null;
            }
            JSONArray arr = root.getJSONArray((Object)"result");
            return arr != null ? arr : new JSONArray();
        }
        catch (Exception e) {
            log.warn("[TG] getUpdates failed: {}", (Object)e.getMessage());
            return null;
        }
    }
}

