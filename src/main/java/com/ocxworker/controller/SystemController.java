package com.ocxworker.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.net.InetAddresses;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.model.dto.OciProxySnapshot;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.BanlistViewSessionService;
import com.ocxworker.service.LoginAuditService;
import com.ocxworker.service.LoginAuditViewSessionService;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.service.SystemService;
import com.ocxworker.service.TgNotifyConfigRollbackService;
import com.ocxworker.service.VerifyCodeService;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
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

@RestController
@RequestMapping({"/api/sys"})
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

    @GetMapping({"/glance"})
    public ResponseData<?> glance() {
        return ResponseData.ok(this.systemService.getGlance());
    }

    @GetMapping({"/ociRegionOptions"})
    public ResponseData<?> ociRegionOptions(@RequestParam(required = false) String userId) {
        return ResponseData.ok(this.systemService.listOciRegionCatalog(userId));
    }

    @GetMapping({"/notifyConfig"})
    public ResponseData<?> getNotifyConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        String botToken = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String chatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        config.put("botToken", this.maskSecret(botToken));
        config.put("chatId", this.maskSecret(chatId));
        config.put("botTokenConfigured", botToken != null && !botToken.isBlank());
        config.put("chatIdConfigured", chatId != null && !chatId.isBlank());
        config.put("notifyTypes", this.notificationService.getKvValue(SysCfgEnum.TG_NOTIFY_TYPES));
        String dailyTime = this.notificationService.getKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME);
        config.put("dailyReportTime", dailyTime != null && !dailyTime.isBlank() ? dailyTime.trim() : "09:00");
        config.put("tgInboundMode", "getUpdates");
        config.put("tgUpdatesOffsetConfigured", StrUtil.isNotBlank(this.notificationService.getKvValue(SysCfgEnum.TG_UPDATES_NEXT_OFFSET)));
        return ResponseData.ok(config);
    }

    private String maskSecret(String value) {
        if (value != null && !value.isBlank()) {
            int len = value.length();
            if (len <= 4) {
                return "****";
            } else {
                return len <= 10 ? value.substring(0, 2) + "****" + value.substring(len - 2) : value.substring(0, 4) + "********" + value.substring(len - 4);
            }
        } else {
            return "";
        }
    }

    @PostMapping({"/notifyConfig"})
    public ResponseData<?> saveNotifyConfig(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String oldToken = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String oldChatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        boolean tokenWillChange = willTgSecretChange(params.get("botToken"), oldToken);
        boolean chatWillChange = willTgSecretChange(params.get("chatId"), oldChatId);
        if (this.verifyCodeService.isTgConfigured()) {
            String code = params.get("verifyCode");
            if (StrUtil.isBlank(code)) {
                return ResponseData.error("请先获取 Telegram 验证码");
            }

            this.verifyCodeService.verifyCode("notifyConfig", code);
        } else {
            String pwd = params.get("password");
            if (StrUtil.isBlank(pwd)) {
                return ResponseData.error("请输入登录密码进行验证");
            }

            String inputHash = DigestUtil.sha256Hex(pwd);
            if (!inputHash.equals(this.authController.getEffectivePasswordHash())) {
                return ResponseData.error("密码错误");
            }
        }

        boolean identityRollback = (tokenWillChange || chatWillChange) && StrUtil.isNotBlank(oldToken) && StrUtil.isNotBlank(oldChatId);
        if (identityRollback) {
            String ip = HttpRequestUtil.getClientIp(request);
            String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
            String newToken = resolveIncomingSecret(params.get("botToken"), oldToken);
            String newChatId = resolveIncomingSecret(params.get("chatId"), oldChatId);
            this.tgNotifyConfigRollbackService.applyIdentityChange(oldToken.trim(), oldChatId.trim(), newToken, newChatId, ip, deviceId);
        } else {
            if (params.containsKey("botToken")) {
                String v = params.get("botToken");
                if (v != null && !v.contains("****")) {
                    this.notificationService.saveKvValue(SysCfgEnum.TG_BOT_TOKEN, v);
                    this.notificationService.resetTelegramUpdatesOffset();
                }
            }

            if (params.containsKey("chatId")) {
                String v = params.get("chatId");
                if (v != null && !v.contains("****")) {
                    this.notificationService.saveKvValue(SysCfgEnum.TG_CHAT_ID, v);
                }
            }
        }

        if (params.containsKey("notifyTypes")) {
            this.notificationService.saveKvValue(SysCfgEnum.TG_NOTIFY_TYPES, params.get("notifyTypes"));
        }

        if (params.containsKey("dailyReportTime")) {
            String t = params.get("dailyReportTime");
            if (t != null && !t.isBlank()) {
                t = t.trim();
                if (!DAILY_REPORT_TIME.matcher(t).matches()) {
                    return ResponseData.error("每日播报时间须为 24 小时制 HH:mm（如 09:00、14:30）");
                }

                this.notificationService.saveKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME, t);
            }
        }

        return ResponseData.ok();
    }

    private static String resolveIncomingSecret(String incoming, String current) {
        return incoming != null && !incoming.contains("****") ? incoming.trim() : StrUtil.trimToEmpty(current);
    }

    private static boolean willTgSecretChange(String incoming, String current) {
        return incoming != null && !incoming.contains("****") ? !Objects.equals(StrUtil.trim(incoming), StrUtil.trimToEmpty(current)) : false;
    }

    @PostMapping({"/testNotify"})
    public ResponseData<?> testNotify() {
        this.notificationService.sendMessage("【测试通知】\ud83d\udd14 Telegram 通知配置正常！");
        return ResponseData.ok();
    }

    @PostMapping({"/sendVerifyCode"})
    public ResponseData<?> sendVerifyCode(@RequestBody Map<String, String> params) {
        this.verifyCodeService.sendCode(params.get("action"));
        return ResponseData.ok();
    }

    @GetMapping({"/tgStatus"})
    public ResponseData<?> tgStatus() {
        return ResponseData.ok(Map.of("configured", this.verifyCodeService.isTgConfigured()));
    }

    @PostMapping({"/loginAudit/unlock"})
    public ResponseData<?> loginAuditUnlock(@RequestBody Map<String, String> body) {
        this.verifyCodeService.verifyCode("loginAudit", body.get("verifyCode"));
        String sid = this.loginAuditViewSessionService.issue();
        return ResponseData.ok(Map.of("loginAuditSession", sid));
    }

    @GetMapping({"/loginAudit"})
    public ResponseData<?> loginAudit(
        @RequestHeader(value = "X-Oci-Login-Audit-Session",required = false) String loginAuditSession,
        @RequestParam(defaultValue = "1") long page,
        @RequestParam(defaultValue = "20") long size
    ) {
        ResponseData<?> gate = this.requireLoginAuditViewSession(loginAuditSession);
        return gate != null ? gate : ResponseData.ok(this.loginAuditService.pageAudits(page, Math.min(size, 100L)));
    }

    @GetMapping({"/banlist"})
    public ResponseData<?> banlist(@RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("ips", this.loginSecurityService.listBannedIps());
            m.put("devices", this.loginSecurityService.listBannedDevices());
            return ResponseData.ok(m);
        }
    }

    @PostMapping({"/banlist/unlock"})
    public ResponseData<?> banlistUnlock(@RequestBody Map<String, String> body) {
        this.verifyCodeService.verifyCode("banlist", body.get("verifyCode"));
        String sid = this.banlistViewSessionService.issue();
        return ResponseData.ok(Map.of("banlistSession", sid));
    }

    @PostMapping({"/banlist/add"})
    public ResponseData<?> banlistAdd(
        @RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession, @RequestBody Map<String, String> body
    ) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            String value = StrUtil.trimToNull(body.get("value"));
            if (value == null) {
                return ResponseData.error("请输入 IP 或设备码");
            } else {
                if (InetAddresses.isInetAddress(value)) {
                    this.loginSecurityService.addIpToDenylist(value);
                } else {
                    this.loginSecurityService.addDeviceToDenylist(value);
                }

                return ResponseData.ok();
            }
        }
    }

    @PostMapping({"/banlist/addIp"})
    public ResponseData<?> banlistAddIp(
        @RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession, @RequestBody Map<String, String> body
    ) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            String ip = StrUtil.trimToNull(body.get("ip"));
            if (ip == null) {
                return ResponseData.error("请输入 IP");
            } else {
                this.loginSecurityService.addIpToDenylist(ip);
                return ResponseData.ok();
            }
        }
    }

    @PostMapping({"/banlist/addDevice"})
    public ResponseData<?> banlistAddDevice(
        @RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession, @RequestBody Map<String, String> body
    ) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            String did = StrUtil.trimToNull(body.get("deviceId"));
            if (did == null) {
                return ResponseData.error("请输入设备码");
            } else {
                this.loginSecurityService.addDeviceToDenylist(did);
                return ResponseData.ok();
            }
        }
    }

    @PostMapping({"/banlist/removeIp"})
    public ResponseData<?> banlistRemoveIp(
        @RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession, @RequestBody Map<String, String> body
    ) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            String ip = StrUtil.trimToNull(body.get("ip"));
            if (ip == null) {
                return ResponseData.error("缺少 ip");
            } else {
                this.loginSecurityService.removeIpFromDenylist(ip);
                return ResponseData.ok();
            }
        }
    }

    @PostMapping({"/banlist/removeDevice"})
    public ResponseData<?> banlistRemoveDevice(
        @RequestHeader(value = "X-Oci-Banlist-Session",required = false) String banlistSession, @RequestBody Map<String, String> body
    ) {
        ResponseData<?> gate = this.requireBanlistViewSession(banlistSession);
        if (gate != null) {
            return gate;
        } else {
            String did = StrUtil.trimToNull(body.get("deviceId"));
            if (did == null) {
                return ResponseData.error("缺少 deviceId");
            } else {
                this.loginSecurityService.removeDeviceFromDenylist(did);
                return ResponseData.ok();
            }
        }
    }

    private ResponseData<?> requireBanlistViewSession(String sessionId) {
        return !this.banlistViewSessionService.isValid(sessionId) ? ResponseData.error(403, "请先通过 Telegram 验证进入封禁列表") : null;
    }

    private ResponseData<?> requireLoginAuditViewSession(String sessionId) {
        return !this.loginAuditViewSessionService.isValid(sessionId) ? ResponseData.error(403, "请先通过 Telegram 验证查看登录统计") : null;
    }

    @GetMapping({"/checkUpdate"})
    public ResponseData<?> checkUpdate() {
        return ResponseData.ok(this.systemService.checkUpdate());
    }

    @PostMapping({"/performUpdate"})
    public ResponseData<?> performUpdate() {
        this.systemService.performUpdate();
        return ResponseData.ok("更新已启动，服务将在几秒后重启");
    }

    @GetMapping({"/ociProxy"})
    public ResponseData<?> getOciProxy() {
        OciProxySnapshot s = this.ociProxyConfigService.snapshot();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("enabled", s.enabled());
        m.put("proxyType", s.type());
        m.put("host", s.host() == null ? "" : s.host());
        m.put("port", s.port() > 0 ? s.port() : null);
        String u = s.proxyUser();
        m.put("username", u != null && !u.isBlank() ? this.maskSecret(u) : "");
        m.put("passwordConfigured", s.proxyPass() != null && !s.proxyPass().isBlank());
        m.put("password", s.proxyPass() != null && !s.proxyPass().isBlank() ? this.maskSecret(s.proxyPass()) : "");
        m.put("fullUrl", s.fullUrl() != null && !s.fullUrl().isBlank() ? this.maskUrlForDisplay(s.fullUrl()) : "");
        m.put("fullUrlConfigured", s.fullUrl() != null && !s.fullUrl().isBlank());
        return ResponseData.ok(m);
    }

    private String maskUrlForDisplay(String url) {
        if (url == null || url.isBlank()) {
            return "";
        } else if (url.contains("@")) {
            return url.replaceAll("://([^/]+)@", "://****@");
        } else {
            return url.length() > 48 ? url.substring(0, 24) + "…" : url;
        }
    }

    @PostMapping({"/ociProxy"})
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
            } catch (NumberFormatException var12) {
            }
        }

        String user = this.resolveMasked(params.get("username"), cur.proxyUser());
        String pass = this.resolveMasked(params.get("password"), cur.proxyPass());
        String full = this.resolveMasked(params.get("fullUrl"), cur.fullUrl());
        OciProxySnapshot snap = OciProxySnapshot.fromForm(en, type, host, port, user, pass, full);
        this.ociProxyConfigService.persistAndReload(snap);
        return ResponseData.ok();
    }

    @PostMapping({"/ociProxy/test"})
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
            } catch (NumberFormatException var13) {
            }
        }

        String user = this.resolveMasked(params.get("username"), cur.proxyUser());
        String pass = this.resolveMasked(params.get("password"), cur.proxyPass());
        String full = this.resolveMasked(params.get("fullUrl"), cur.fullUrl());
        OciProxySnapshot test = OciProxySnapshot.fromForm(en, type, host, port, user, pass, full);
        String msg = this.ociProxyConfigService.testWithParams(test);
        return ResponseData.ok(msg);
    }

    private String nvl(String s) {
        return s == null ? "" : s.trim();
    }

    private String resolveMasked(String fromClient, String existing) {
        return fromClient != null && fromClient.contains("****") && existing != null && !existing.isBlank() ? existing : this.nvl(fromClient);
    }
}
