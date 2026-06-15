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
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
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
        if (this.isTypeEnabled(notifyType)) {
            this.sendTelegram(message);
        }
    }

    public void sendMessage(String message) {
        this.sendTelegram(message);
    }

    public boolean isNotifyTypeEnabled(String notifyType) {
        if (StrUtil.isBlank(notifyType)) {
            return false;
        } else {
            String types = this.getKvValue(SysCfgEnum.TG_NOTIFY_TYPES);
            if (StrUtil.isBlank(types)) {
                return true;
            } else {
                for (String t : types.split(",")) {
                    if (notifyType.equals(t.trim())) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    private boolean isTypeEnabled(String notifyType) {
        return this.isNotifyTypeEnabled(notifyType);
    }

    public void sendTelegramPlain(String botToken, String chatId, String message) {
        if (!StrUtil.isBlank(botToken) && !StrUtil.isBlank(chatId) && !StrUtil.isBlank(message)) {
            try {
                String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
                HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(Map.of("chat_id", chatId, "text", message))))
                    .timeout(Duration.ofSeconds(10L))
                    .build();
                c.send(req, BodyHandlers.discarding());
            } catch (Exception var7) {
                log.warn("Failed to send Telegram message to explicit chat: {}", var7.getMessage());
            }
        }
    }

    private void sendTelegram(String message) {
        this.sendTelegramPlain(this.getKvValue(SysCfgEnum.TG_BOT_TOKEN), this.getKvValue(SysCfgEnum.TG_CHAT_ID), message);
    }

    public void sendHtmlWithType(String notifyType, String html) {
        if (this.isTypeEnabled(notifyType)) {
            this.sendTelegramHtml(html, null);
        }
    }

    public void sendHtmlWithTypeAndInlineKeyboard(String notifyType, String html, List<List<Map<String, String>>> inlineKeyboard) {
        if (this.isTypeEnabled(notifyType)) {
            try {
                String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
                String chatId = this.getKvValue(SysCfgEnum.TG_CHAT_ID);
                if (StrUtil.isBlank(botToken) || StrUtil.isBlank(chatId) || StrUtil.isBlank(html)) {
                    return;
                }

                String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("chat_id", chatId);
                body.put("text", html);
                body.put("parse_mode", "HTML");
                if (inlineKeyboard != null && !inlineKeyboard.isEmpty()) {
                    body.put("reply_markup", Map.of("inline_keyboard", inlineKeyboard));
                }

                HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                    .timeout(Duration.ofSeconds(10L))
                    .build();
                c.send(req, BodyHandlers.discarding());
            } catch (Exception var10) {
                log.warn("Failed to send Telegram HTML keyboard message: {}", var10.getMessage());
            }
        }
    }

    public void sendTelegramHtml(String html, String copyText) {
        try {
            String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            String chatId = this.getKvValue(SysCfgEnum.TG_CHAT_ID);
            if (StrUtil.isBlank(botToken) || StrUtil.isBlank(chatId)) {
                return;
            }

            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("chat_id", chatId);
            body.put("text", html);
            body.put("parse_mode", "HTML");
            if (StrUtil.isNotBlank(copyText)) {
                body.put(
                    "reply_markup",
                    Map.of("inline_keyboard", List.of(List.of(Map.of("text", "\ud83d\udccb 复制验证码", "callback_data", "copy_noop", "copy_text", copyText))))
                );
            }

            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                .timeout(Duration.ofSeconds(10L))
                .build();
            c.send(req, BodyHandlers.discarding());
        } catch (Exception var9) {
            log.warn("Failed to send Telegram HTML message: {}", var9.getMessage());
        }
    }

    public String getKvValue(SysCfgEnum cfg) {
        OciKv kv = (OciKv)this.kvMapper
            .selectOne(
                (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, cfg.getCode()))
                        .eq(OciKv::getType, cfg.getType()))
                    .last("LIMIT 1")
            );
        return kv != null ? kv.getValue() : null;
    }

    public void saveKvValue(SysCfgEnum cfg, String value) {
        OciKv existing = (OciKv)this.kvMapper
            .selectOne(
                (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, cfg.getCode()))
                        .eq(OciKv::getType, cfg.getType()))
                    .last("LIMIT 1")
            );
        if (existing != null) {
            existing.setValue(value);
            this.kvMapper.updateById(existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode(cfg.getCode());
            kv.setType(cfg.getType());
            kv.setValue(value);
            this.kvMapper.insert(kv);
        }
    }

    public void removeKvValue(SysCfgEnum cfg) {
        this.kvMapper.delete((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, cfg.getCode())).eq(OciKv::getType, cfg.getType()));
    }

    public void sendSecurityTextWithInlineKeyboard(String text, List<List<Map<String, String>>> inlineKeyboard) {
        this.sendSecurityTextWithInlineKeyboard(this.getKvValue(SysCfgEnum.TG_BOT_TOKEN), this.getKvValue(SysCfgEnum.TG_CHAT_ID), text, inlineKeyboard);
    }

    public void sendSecurityTextWithInlineKeyboard(String botToken, String chatId, String text, List<List<Map<String, String>>> inlineKeyboard) {
        if (!StrUtil.isBlank(botToken) && !StrUtil.isBlank(chatId) && !StrUtil.isBlank(text)) {
            try {
                String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("chat_id", chatId);
                body.put("text", text);
                body.put("reply_markup", Map.of("inline_keyboard", inlineKeyboard));
                HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                    .timeout(Duration.ofSeconds(15L))
                    .build();
                c.send(req, BodyHandlers.discarding());
            } catch (Exception var9) {
                log.warn("Failed to send Telegram security keyboard message: {}", var9.getMessage());
            }
        }
    }

    public void answerTelegramCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        this.answerTelegramCallbackQuery(callbackQueryId, text, showAlert, null);
    }

    public void answerTelegramCallbackQuery(String callbackQueryId, String text, boolean showAlert, String botTokenOverride) {
        try {
            String botToken = StrUtil.isNotBlank(botTokenOverride) ? botTokenOverride : this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            if (StrUtil.isBlank(botToken) || StrUtil.isBlank(callbackQueryId)) {
                return;
            }

            String url = String.format("https://api.telegram.org/bot%s/answerCallbackQuery", botToken);
            String t = text == null ? "" : text;
            if (t.length() > 180) {
                t = t.substring(0, 177) + "...";
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("callback_query_id", callbackQueryId);
            body.put("text", t);
            body.put("show_alert", showAlert);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                .timeout(Duration.ofSeconds(10L))
                .build();
            c.send(req, BodyHandlers.discarding());
        } catch (Exception var11) {
            log.warn("Failed to answer Telegram callback: {}", var11.getMessage());
        }
    }

    public void registerTelegramBotCommands() {
        try {
            String botToken = this.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
            if (StrUtil.isBlank(botToken)) {
                return;
            }

            String url = String.format("https://api.telegram.org/bot%s/setMyCommands", botToken);
            List<Map<String, String>> commands = new ArrayList<>();
            commands.add(Map.of("command", "start", "description", "启动OCIWorker"));
            commands.add(Map.of("command", "stop", "description", "暂停全站访问"));
            commands.add(Map.of("command", "logs", "description", "抢机任务"));
            commands.add(Map.of("command", "state", "description", "系统状态"));
            commands.add(Map.of("command", "bans", "description", "禁止名单与解除"));
            Map<String, Object> body = Map.of("commands", commands);
            HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(body)))
                .timeout(Duration.ofSeconds(15L))
                .build();
            c.send(req, BodyHandlers.discarding());
            log.info("Telegram setMyCommands registered (start/stop/logs/state/bans)");
        } catch (Exception var7) {
            log.warn("Failed to register Telegram bot commands: {}", var7.getMessage());
        }
    }

    public void resetTelegramUpdatesOffset() {
        this.saveKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET, "");
    }

    public long getTelegramUpdatesNextOffset() {
        String v = StrUtil.trimToNull(this.getKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET));
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

    public void saveTelegramUpdatesNextOffset(long nextOffset) {
        if (nextOffset > 0L) {
            this.saveKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET, String.valueOf(nextOffset));
        }
    }

    public boolean telegramDeleteWebhook(String botToken) {
        if (StrUtil.isBlank(botToken)) {
            return false;
        } else {
            try {
                String url = String.format("https://api.telegram.org/bot%s/deleteWebhook?drop_pending_updates=false", botToken);
                HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().timeout(Duration.ofSeconds(20L)).build();
                HttpResponse<String> resp = c.send(req, BodyHandlers.ofString());
                if (resp.statusCode() == 200 && resp.body() != null) {
                    JSONObject root = JSONUtil.parseObj(resp.body());
                    return root.getBool("ok", false);
                } else {
                    return false;
                }
            } catch (Exception var7) {
                log.warn("[TG] deleteWebhook failed: {}", var7.getMessage());
                return false;
            }
        }
    }

    public JSONArray telegramGetUpdates(String botToken, long offset, int timeoutSec) {
        if (StrUtil.isBlank(botToken)) {
            return null;
        } else {
            try {
                String allowedEnc = URLEncoder.encode("[\"message\",\"callback_query\"]", StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                sb.append(String.format(Locale.US, "https://api.telegram.org/bot%s/getUpdates?timeout=%d&allowed_updates=%s", botToken, timeoutSec, allowedEnc));
                if (offset > 0L) {
                    sb.append("&offset=").append(offset);
                }

                HttpClient c = this.ociProxyConfigService.newOutboundHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(sb.toString())).GET().timeout(Duration.ofSeconds((long)timeoutSec + 45L)).build();
                HttpResponse<String> resp = c.send(req, BodyHandlers.ofString());
                if (resp.statusCode() == 200 && resp.body() != null) {
                    JSONObject root = JSONUtil.parseObj(resp.body());
                    if (!root.getBool("ok", false)) {
                        log.warn("[TG] getUpdates ok=false: {}", root.getStr("description"));
                        return null;
                    } else {
                        JSONArray arr = root.getJSONArray("result");
                        return arr != null ? arr : new JSONArray();
                    }
                } else {
                    log.warn("[TG] getUpdates HTTP {}", resp.statusCode());
                    return null;
                }
            } catch (Exception var12) {
                log.warn("[TG] getUpdates failed: {}", var12.getMessage());
                return null;
            }
        }
    }
}
