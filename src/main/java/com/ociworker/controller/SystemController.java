/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.crypto.digest.DigestUtil
 *  com.google.common.net.InetAddresses
 *  com.ociworker.controller.AuthController
 *  com.ociworker.controller.SystemController
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.model.dto.OciProxySnapshot
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.BanlistViewSessionService
 *  com.ociworker.service.LoginAuditService
 *  com.ociworker.service.LoginAuditViewSessionService
 *  com.ociworker.service.LoginSecurityService
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.service.SystemService
 *  com.ociworker.service.TgNotifyConfigRollbackService
 *  com.ociworker.service.VerifyCodeService
 *  com.ociworker.util.HttpRequestUtil
 *  jakarta.annotation.Resource
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.net.InetAddresses;
import com.ociworker.controller.AuthController;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.model.dto.OciProxySnapshot;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.BanlistViewSessionService;
import com.ociworker.service.LoginAuditService;
import com.ociworker.service.LoginAuditViewSessionService;
import com.ociworker.service.LoginSecurityService;
import com.ociworker.service.NotificationService;
import com.ociworker.service.OciProxyConfigService;
import com.ociworker.service.SystemService;
import com.ociworker.service.TgNotifyConfigRollbackService;
import com.ociworker.service.VerifyCodeService;
import com.ociworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/sys"})
public class SystemController {
    private static final Pattern DAILY_REPORT_TIME = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final String BANLIST_SESSION_HEADER = "X-Oci-Banlist-Session";
    private static final String LOGIN_AUDIT_SESSION_HEADER = "X-Oci-Login-Audit-Session";
    @Resource
    private SystemService systemService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private AuthController authController;
    @Resource
    private OciProxyConfigService ociProxyConfigService;
    @Resource
    private LoginAuditService loginAuditService;
    @Resource
    private LoginSecurityService loginSecurityService;
    @Resource
    private BanlistViewSessionService banlistViewSessionService;
    @Resource
    private LoginAuditViewSessionService loginAuditViewSessionService;
    @Resource
    private TgNotifyConfigRollbackService tgNotifyConfigRollbackService;

    @GetMapping(value={"/glance"})
    public ResponseData<?> glance() {
        return ResponseData.ok((Object)this.systemService.getGlance());
    }

    @GetMapping(value={"/ociRegionOptions"})
    public ResponseData<?> ociRegionOptions(@RequestParam(required=false) String userId) {
        return ResponseData.ok((Object)this.systemService.listOciRegionCatalog(userId));
    }

    @GetMapping(value={"/notifyConfig"})
    public ResponseData<?> getNotifyConfig() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<String, Object>();
        String botToken = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String chatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        config.put("botToken", this.maskSecret(botToken));
        config.put("chatId", this.maskSecret(chatId));
        config.put("botTokenConfigured", botToken != null && !botToken.isBlank());
        config.put("chatIdConfigured", chatId != null && !chatId.isBlank());
        config.put("notifyTypes", this.notificationService.getKvValue(SysCfgEnum.TG_NOTIFY_TYPES));
        String dailyTime = this.notificationService.getKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME);
        config.put("dailyReportTime", dailyTime == null || dailyTime.isBlank() ? "09:00" : dailyTime.trim());
        config.put("tgInboundMode", "getUpdates");
        config.put("tgUpdatesOffsetConfigured", StrUtil.isNotBlank((CharSequence)this.notificationService.getKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET)));
        return ResponseData.ok(config);
    }

    private String maskSecret(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        int len = value.length();
        if (len <= 4) {
            return "****";
        }
        if (len <= 10) {
            return value.substring(0, 2) + "****" + value.substring(len - 2);
        }
        return value.substring(0, 4) + "********" + value.substring(len - 4);
    }

    @PostMapping(value={"/notifyConfig"})
    public ResponseData<?> saveNotifyConfig(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String t;
        boolean identityRollback;
        String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String oldChatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        boolean tokenWillChange = SystemController.willTgSecretChange((String)params.get("botToken"), (String)oldToken);
        boolean chatWillChange = SystemController.willTgSecretChange((String)params.get("chatId"), (String)oldChatId);
        if (this.verifyCodeService.isTgConfigured()) {
            String code = params.get("verifyCode");
            if (StrUtil.isBlank((CharSequence)code)) {
                return ResponseData.error((String)"\u8bf7\u5148\u83b7\u53d6 Telegram \u9a8c\u8bc1\u7801");
            }
            this.verifyCodeService.verifyCode("notifyConfig", code);
        } else {
            String pwd = params.get("password");
            if (StrUtil.isBlank((CharSequence)pwd)) {
                return ResponseData.error((String)"\u8bf7\u8f93\u5165\u767b\u5f55\u5bc6\u7801\u8fdb\u884c\u9a8c\u8bc1");
            }
            String inputHash = DigestUtil.sha256Hex((String)pwd);
            if (!inputHash.equals(this.authController.getEffectivePasswordHash())) {
                return ResponseData.error((String)"\u5bc6\u7801\u9519\u8bef");
            }
        }
        boolean bl = identityRollback = (tokenWillChange || chatWillChange) && StrUtil.isNotBlank((CharSequence)oldToken) && StrUtil.isNotBlank((CharSequence)oldChatId);
        if (identityRollback) {
            String ip = HttpRequestUtil.getClientIp((HttpServletRequest)request);
            String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
            String newToken = SystemController.resolveIncomingSecret((String)params.get("botToken"), (String)oldToken);
            String newChatId = SystemController.resolveIncomingSecret((String)params.get("chatId"), (String)oldChatId);
            this.tgNotifyConfigRollbackService.applyIdentityChange(oldToken.trim(), oldChatId.trim(), newToken, newChatId, ip, deviceId);
        } else {
            String v;
            if (params.containsKey("botToken") && (v = params.get("botToken")) != null && !v.contains("****")) {
                this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, v);
                this.notificationService.resetTelegramUpdatesOffset();
            }
            if (params.containsKey("chatId") && (v = params.get("chatId")) != null && !v.contains("****")) {
                this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, v);
            }
        }
        if (params.containsKey("notifyTypes")) {
            this.notificationService.saveKvValue(SysCfgEnum.TG_NOTIFY_TYPES, params.get("notifyTypes"));
        }
        if (params.containsKey("dailyReportTime") && (t = params.get("dailyReportTime")) != null && !t.isBlank()) {
            if (!DAILY_REPORT_TIME.matcher(t = t.trim()).matches()) {
                return ResponseData.error((String)"\u6bcf\u65e5\u64ad\u62a5\u65f6\u95f4\u987b\u4e3a 24 \u5c0f\u65f6\u5236 HH:mm\uff08\u5982 09:00\u300114:30\uff09");
            }
            this.notificationService.saveKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME, t);
        }
        return ResponseData.ok();
    }

    private static String resolveIncomingSecret(String incoming, String current) {
        if (incoming == null || incoming.contains("****")) {
            return StrUtil.trimToEmpty((CharSequence)current);
        }
        return incoming.trim();
    }

    private static boolean willTgSecretChange(String incoming, String current) {
        if (incoming == null || incoming.contains("****")) {
            return false;
        }
        return !Objects.equals(StrUtil.trim((CharSequence)incoming), StrUtil.trimToEmpty((CharSequence)current));
    }

    @PostMapping(value={"/testNotify"})
    public ResponseData<?> testNotify() {
        this.notificationService.sendMessage("\u3010\u6d4b\u8bd5\u901a\u77e5\u3011\ud83d\udd14 Telegram \u901a\u77e5\u914d\u7f6e\u6b63\u5e38\uff01");
        return ResponseData.ok();
    }

    @PostMapping(value={"/sendVerifyCode"})
    public ResponseData<?> sendVerifyCode(@RequestBody Map<String, String> params) {
        this.verifyCodeService.sendCode(params.get("action"));
        return ResponseData.ok();
    }

    @GetMapping(value={"/tgStatus"})
    public ResponseData<?> tgStatus() {
        return ResponseData.ok(Map.of("configured", this.verifyCodeService.isTgConfigured()));
    }

    @PostMapping(value={"/loginAudit/unlock"})
    public ResponseData<?> loginAuditUnlock(@RequestBody Map<String, String> body) {
        this.verifyCodeService.verifyCode("loginAudit", body.get("verifyCode"));
        String sid = this.loginAuditViewSessionService.issue();
        return ResponseData.ok(Map.of("loginAuditSession", sid));
    }

    @GetMapping(value={"/loginAudit"})
    public ResponseData<?> loginAudit(@RequestHeader(value="X-Oci-Login-Audit-Session", required=false) String loginAuditSession, @RequestParam(defaultValue="1") long page, @RequestParam(defaultValue="20") long size) {
        ResponseData gate = this.requireLoginAuditViewSession(loginAuditSession);
        if (gate != null) {
            return gate;
        }
        return ResponseData.ok((Object)this.loginAuditService.pageAudits(page, Math.min(size, 100L)));
    }

    @GetMapping(value={"/banlist"})
    public ResponseData<?> banlist(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        LinkedHashMap<String, List> m = new LinkedHashMap<String, List>();
        m.put("ips", this.loginSecurityService.listBannedIps());
        m.put("devices", this.loginSecurityService.listBannedDevices());
        return ResponseData.ok(m);
    }

    @PostMapping(value={"/banlist/unlock"})
    public ResponseData<?> banlistUnlock(@RequestBody Map<String, String> body) {
        this.verifyCodeService.verifyCode("banlist", body.get("verifyCode"));
        String sid = this.banlistViewSessionService.issue();
        return ResponseData.ok(Map.of("banlistSession", sid));
    }

    @PostMapping(value={"/banlist/add"})
    public ResponseData<?> banlistAdd(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession, @RequestBody Map<String, String> body) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        String value = StrUtil.trimToNull((CharSequence)body.get("value"));
        if (value == null) {
            return ResponseData.error((String)"\u8bf7\u8f93\u5165 IP \u6216\u8bbe\u5907\u7801");
        }
        if (InetAddresses.isInetAddress((String)value)) {
            this.loginSecurityService.addIpToDenylist(value);
        } else {
            this.loginSecurityService.addDeviceToDenylist(value);
        }
        return ResponseData.ok();
    }

    @PostMapping(value={"/banlist/addIp"})
    public ResponseData<?> banlistAddIp(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession, @RequestBody Map<String, String> body) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        String ip = StrUtil.trimToNull((CharSequence)body.get("ip"));
        if (ip == null) {
            return ResponseData.error((String)"\u8bf7\u8f93\u5165 IP");
        }
        this.loginSecurityService.addIpToDenylist(ip);
        return ResponseData.ok();
    }

    @PostMapping(value={"/banlist/addDevice"})
    public ResponseData<?> banlistAddDevice(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession, @RequestBody Map<String, String> body) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        String did = StrUtil.trimToNull((CharSequence)body.get("deviceId"));
        if (did == null) {
            return ResponseData.error((String)"\u8bf7\u8f93\u5165\u8bbe\u5907\u7801");
        }
        this.loginSecurityService.addDeviceToDenylist(did);
        return ResponseData.ok();
    }

    @PostMapping(value={"/banlist/removeIp"})
    public ResponseData<?> banlistRemoveIp(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession, @RequestBody Map<String, String> body) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        String ip = StrUtil.trimToNull((CharSequence)body.get("ip"));
        if (ip == null) {
            return ResponseData.error((String)"\u7f3a\u5c11 ip");
        }
        this.loginSecurityService.removeIpFromDenylist(ip);
        return ResponseData.ok();
    }

    @PostMapping(value={"/banlist/removeDevice"})
    public ResponseData<?> banlistRemoveDevice(@RequestHeader(value="X-Oci-Banlist-Session", required=false) String banlistSession, @RequestBody Map<String, String> body) {
        ResponseData gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        }
        String did = StrUtil.trimToNull((CharSequence)body.get("deviceId"));
        if (did == null) {
            return ResponseData.error((String)"\u7f3a\u5c11 deviceId");
        }
        this.loginSecurityService.removeDeviceFromDenylist(did);
        return ResponseData.ok();
    }

    private ResponseData<?> requireBanlistViewSession(String sessionId) {
        if (!this.banlistViewSessionService.isValid(sessionId)) {
            return ResponseData.error((int)403, (String)"\u8bf7\u5148\u901a\u8fc7 Telegram \u9a8c\u8bc1\u8fdb\u5165\u5c01\u7981\u5217\u8868");
        }
        return null;
    }

    private ResponseData<?> requireLoginAuditViewSession(String sessionId) {
        if (!this.loginAuditViewSessionService.isValid(sessionId)) {
            return ResponseData.error((int)403, (String)"\u8bf7\u5148\u901a\u8fc7 Telegram \u9a8c\u8bc1\u67e5\u770b\u767b\u5f55\u7edf\u8ba1");
        }
        return null;
    }

    @GetMapping(value={"/checkUpdate"})
    public ResponseData<?> checkUpdate() {
        return ResponseData.ok((Object)this.systemService.checkUpdate());
    }

    @PostMapping(value={"/performUpdate"})
    public ResponseData<?> performUpdate() {
        this.systemService.performUpdate();
        return ResponseData.ok((Object)"\u66f4\u65b0\u5df2\u542f\u52a8\uff0c\u670d\u52a1\u5c06\u5728\u51e0\u79d2\u540e\u91cd\u542f");
    }

    @GetMapping(value={"/ociProxy"})
    public ResponseData<?> getOciProxy() {
        OciProxySnapshot s = this.ociProxyConfigService.snapshot();
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("enabled", s.enabled());
        m.put("proxyType", s.type());
        m.put("host", s.host() == null ? "" : s.host());
        m.put("port", s.port() > 0 ? Integer.valueOf(s.port()) : null);
        String u = s.proxyUser();
        m.put("username", u == null || u.isBlank() ? "" : this.maskSecret(u));
        m.put("passwordConfigured", s.proxyPass() != null && !s.proxyPass().isBlank());
        m.put("password", s.proxyPass() == null || s.proxyPass().isBlank() ? "" : this.maskSecret(s.proxyPass()));
        m.put("fullUrl", s.fullUrl() == null || s.fullUrl().isBlank() ? "" : this.maskUrlForDisplay(s.fullUrl()));
        m.put("fullUrlConfigured", s.fullUrl() != null && !s.fullUrl().isBlank());
        return ResponseData.ok(m);
    }

    private String maskUrlForDisplay(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        if (url.contains("@")) {
            return url.replaceAll("://([^/]+)@", "://****@");
        }
        return url.length() > 48 ? url.substring(0, 24) + "\u2026" : url;
    }

    @PostMapping(value={"/ociProxy"})
    public ResponseData<?> saveOciProxy(@RequestBody Map<String, String> params) {
        OciProxySnapshot cur = this.ociProxyConfigService.snapshot();
        boolean en = "true".equalsIgnoreCase(this.nvl(params.get("enabled"))) || "1".equals(this.nvl(params.get("enabled")));
        String type = this.nvl(params.get("proxyType"));
        String host = this.nvl(params.get("host"));
        int port = 0;
        String ps = params.get("port");
        if (ps != null && !ps.isBlank()) {
            try {
                port = Integer.parseInt(ps.trim());
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        String user = this.resolveMasked(params.get("username"), cur.proxyUser());
        String pass = this.resolveMasked(params.get("password"), cur.proxyPass());
        String full = this.resolveMasked(params.get("fullUrl"), cur.fullUrl());
        OciProxySnapshot snap = OciProxySnapshot.fromForm((boolean)en, (String)type, (String)host, (int)port, (String)user, (String)pass, (String)full);
        this.ociProxyConfigService.persistAndReload(snap);
        return ResponseData.ok();
    }

    @PostMapping(value={"/ociProxy/test"})
    public ResponseData<?> testOciProxy(@RequestBody Map<String, String> params) {
        OciProxySnapshot cur = this.ociProxyConfigService.snapshot();
        boolean en = "true".equalsIgnoreCase(this.nvl(params.get("enabled"))) || "1".equals(this.nvl(params.get("enabled")));
        String type = this.nvl(params.get("proxyType"));
        String host = this.nvl(params.get("host"));
        int port = 0;
        String ps = params.get("port");
        if (ps != null && !ps.isBlank()) {
            try {
                port = Integer.parseInt(ps.trim());
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        String user = this.resolveMasked(params.get("username"), cur.proxyUser());
        String pass = this.resolveMasked(params.get("password"), cur.proxyPass());
        String full = this.resolveMasked(params.get("fullUrl"), cur.fullUrl());
        OciProxySnapshot test = OciProxySnapshot.fromForm((boolean)en, (String)type, (String)host, (int)port, (String)user, (String)pass, (String)full);
        String msg = this.ociProxyConfigService.testWithParams(test);
        return ResponseData.ok((Object)msg);
    }

    private String nvl(String s) {
        return s == null ? "" : s.trim();
    }

    private String resolveMasked(String fromClient, String existing) {
        if (fromClient != null && fromClient.contains("****") && existing != null && !existing.isBlank()) {
            return existing;
        }
        return this.nvl(fromClient);
    }
}

