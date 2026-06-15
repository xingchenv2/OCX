/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.IdUtil
 *  cn.hutool.core.util.RandomUtil
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.service.LoginSecurityService
 *  com.ociworker.service.LoginSecurityService$IpFailWindow
 *  com.ociworker.service.LoginSecurityService$Pending
 *  com.ociworker.service.LoginSecurityService$PendingKind
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.VerifyCodeService
 *  com.ociworker.util.HttpRequestUtil
 *  jakarta.annotation.Resource
 *  jakarta.servlet.http.HttpServletRequest
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.service.LoginSecurityService;
import com.ociworker.service.NotificationService;
import com.ociworker.service.VerifyCodeService;
import com.ociworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.invoke.CallSite;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class LoginSecurityService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LoginSecurityService.class);
    private static final long PENDING_TTL_MS = 900000L;
    private static final long DENYLIST_UI_TTL_MS = 1800000L;
    private static final long FAIL_WINDOW_MS = 900000L;
    private static final int PAUSE_OFFER_THRESHOLD = 5;
    private final ConcurrentHashMap<String, Pending> pendingByToken = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, IpFailWindow> ipFailWindows = new ConcurrentHashMap();
    @Resource
    private NotificationService notificationService;
    @Resource
    private VerifyCodeService verifyCodeService;

    public boolean isSitePaused() {
        String v = this.notificationService.getKvValue(SysCfgEnum.SITE_ACCESS_PAUSED);
        return "true".equalsIgnoreCase(StrUtil.trim((CharSequence)v));
    }

    public void setSitePaused(boolean paused) {
        this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, paused ? "true" : "false");
        log.warn("[LoginSecurity] site_access_paused = {}", (Object)paused);
    }

    public boolean isDeniedForLogin(String ip, String deviceId) {
        if (LoginSecurityService.containsIp((Set)this.readIpDenylist(), (String)LoginSecurityService.normalizeIp((String)ip))) {
            return true;
        }
        return StrUtil.isNotBlank((CharSequence)deviceId) && LoginSecurityService.containsToken((Set)this.readDeviceDenylist(), (String)deviceId.trim());
    }

    public boolean isLoginHardenedPath(String uri) {
        if (uri == null) {
            return false;
        }
        return "/api/auth/login".equals(uri) || "/api/auth/tgLogin".equals(uri) || "/api/auth/tgLoginSendCode".equals(uri);
    }

    public boolean isExemptFromSitePause(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.startsWith("/api/auth/device")) {
            return true;
        }
        if (uri.startsWith("/api/auth/needSetup")) {
            return true;
        }
        if (uri.startsWith("/api/auth/setup")) {
            return true;
        }
        if (uri.startsWith("/ws/")) {
            return true;
        }
        if (uri.equals("/") || uri.startsWith("/assets/")) {
            return true;
        }
        if (uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".ico")) {
            return true;
        }
        if (uri.startsWith("/webssh/")) {
            return true;
        }
        return uri.startsWith("/ip-info");
    }

    public void onPasswordLoginFailed(String account, String ip, String deviceId) {
        String dev;
        String ipN = LoginSecurityService.normalizeIp((String)ip);
        String string = dev = StrUtil.isBlank((CharSequence)deviceId) ? null : deviceId.trim();
        if (!this.verifyCodeService.isTgConfigured()) {
            this.notificationService.sendMessage("login", String.format("\u3010\u767b\u5f55\u901a\u77e5\u3011\u26a0\ufe0f \u767b\u5f55\u5931\u8d25\n\u8d26\u53f7: %s\nIP: %s\n\u65f6\u95f4: %s", account, ipN, this.nowStr()));
            return;
        }
        String tokIp = this.registerPending(new Pending(PendingKind.BLOCK_IP, ipN, null, System.currentTimeMillis() + 900000L));
        String tokDev = null;
        if (StrUtil.isNotBlank((CharSequence)dev)) {
            tokDev = this.registerPending(new Pending(PendingKind.BLOCK_DEVICE, ipN, dev, System.currentTimeMillis() + 900000L));
        }
        ArrayList rows = new ArrayList();
        ArrayList<Map<String, CallSite>> row1 = new ArrayList<Map<String, CallSite>>();
        row1.add(Map.of("text", "\u62c9\u9ed1\u8be5IP", "callback_data", "i|" + tokIp));
        rows.add(row1);
        if (tokDev != null) {
            ArrayList<Map<String, CallSite>> row2 = new ArrayList<Map<String, CallSite>>();
            row2.add(Map.of("text", "\u7981\u6b62\u8be5\u8bbe\u5907", "callback_data", "d|" + tokDev));
            rows.add(row2);
        }
        String text = String.format("\u3010\u767b\u5f55\u901a\u77e5\u3011\u26a0\ufe0f \u767b\u5f55\u5931\u8d25\n\u8d26\u53f7: %s\nIP: %s\n\u8bbe\u5907: %s\n\u65f6\u95f4: %s\n\n\uff0815 \u5206\u949f\u5185\u6709\u6548\uff09\u70b9\u51fb\u4e0b\u65b9\u6309\u94ae\u6267\u884c\u64cd\u4f5c\u3002", account, ipN, dev != null ? dev : "\u672a\u77e5", this.nowStr());
        this.notificationService.sendSecurityTextWithInlineKeyboard(text, rows);
        int n = this.bumpFailureCount(ipN);
        if (n == 5) {
            this.maybeSendPauseOffer(ipN, n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void maybeSendPauseOffer(String ipN, int n) {
        IpFailWindow w = (IpFailWindow)this.ipFailWindows.get(ipN);
        if (w == null) {
            return;
        }
        IpFailWindow ipFailWindow = w;
        synchronized (ipFailWindow) {
            if (n != 5 || w.pauseOfferSent) {
                return;
            }
            w.pauseOfferSent = true;
        }
        String tokPause = this.registerPending(new Pending(PendingKind.PAUSE_SITE, ipN, null, System.currentTimeMillis() + 900000L));
        String tokIgnore = this.registerPending(new Pending(PendingKind.IGNORE_FAILS, ipN, null, System.currentTimeMillis() + 900000L));
        List<List<Map<String, CallSite>>> rows = List.of(List.of(Map.of("text", "\u6682\u505c\u5168\u7ad9\u8bbf\u95ee", "callback_data", "p|" + tokPause), Map.of("text", "\u5ffd\u7565(\u6e05\u96f6\u8ba1\u6570)", "callback_data", "g|" + tokIgnore)));
        String text = String.format("\u3010\u767b\u5f55\u5b89\u5168\u3011\u540c\u4e00 IP \u5728 %d \u5206\u949f\u5185\u5df2\u8fde\u7eed\u5bc6\u7801\u767b\u5f55\u5931\u8d25 %d \u6b21\nIP: %s\n\n\u53ef\u9009\u62e9\u6682\u505c\u6574\u7ad9 API \u8bbf\u95ee\uff08\u4ecd\u53ef\u901a\u8fc7\u4e0b\u65b9\u300c\u6062\u590d\u300d\u4e0e Telegram \u5185\u8054\u6309\u94ae\u89e3\u9664\uff09\uff0c\u6216\u4ec5\u6e05\u96f6\u8ba1\u6570\u3002", 15L, 5, ipN);
        this.notificationService.sendSecurityTextWithInlineKeyboard(text, rows);
    }

    public void handleTelegramCallback(String rawData, String callbackQueryId) {
        this.handleTelegramCallback(rawData, callbackQueryId, null);
    }

    public void handleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (StrUtil.isBlank((CharSequence)callbackQueryId)) {
            return;
        }
        if (rawData == null || !rawData.contains("|")) {
            this.answerCallback(callbackQueryId, "\u65e0\u6548\u64cd\u4f5c", false, answeringBotToken);
            return;
        }
        int p = rawData.indexOf(124);
        String prefix = rawData.substring(0, p);
        String token = rawData.substring(p + 1);
        if (token.length() > 32) {
            this.answerCallback(callbackQueryId, "\u65e0\u6548\u64cd\u4f5c", false, answeringBotToken);
            return;
        }
        Pending pend = (Pending)this.pendingByToken.get(token);
        if (pend == null || System.currentTimeMillis() > pend.expireAt) {
            this.answerCallback(callbackQueryId, "\u64cd\u4f5c\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u53d1\u9001 /bans \u6216\u91cd\u65b0\u767b\u5f55\u540e\u518d\u8bd5", false, answeringBotToken);
            return;
        }
        if (!LoginSecurityService.prefixMatchesKind((String)prefix, (PendingKind)pend.kind)) {
            this.answerCallback(callbackQueryId, "\u65e0\u6548\u64cd\u4f5c", false, answeringBotToken);
            return;
        }
        if (!LoginSecurityService.isPendingPayloadValid((Pending)pend)) {
            this.answerCallback(callbackQueryId, "\u6570\u636e\u65e0\u6548", false, answeringBotToken);
            return;
        }
        this.pendingByToken.remove(token);
        try {
            switch (prefix) {
                case "i": {
                    this.appendIpDenylist(pend.ip);
                    this.answerCallback(callbackQueryId, "\u5df2\u62c9\u9ed1 IP: " + pend.ip, false, answeringBotToken);
                    log.warn("[LoginSecurity] IP denylisted via TG: {}", (Object)pend.ip);
                    break;
                }
                case "d": {
                    this.appendDeviceDenylist(pend.deviceId);
                    this.answerCallback(callbackQueryId, "\u5df2\u7981\u6b62\u8bbe\u5907: " + pend.deviceId, false, answeringBotToken);
                    log.warn("[LoginSecurity] Device denylisted via TG: {}", (Object)pend.deviceId);
                    break;
                }
                case "p": {
                    this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, "true");
                    this.answerCallback(callbackQueryId, "\u5168\u7ad9 API \u5df2\u6682\u505c\uff08\u9759\u6001\u9875\u4e0e TG \u56de\u8c03\u4ecd\u53ef\u7528\uff09", false, answeringBotToken);
                    log.warn("[LoginSecurity] Site access paused via TG, trigger IP: {}", (Object)pend.ip);
                    this.sendResumeOfferAfterPause();
                    break;
                }
                case "u": {
                    this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, "false");
                    this.answerCallback(callbackQueryId, "\u5df2\u6062\u590d\u5168\u7ad9\u8bbf\u95ee", false, answeringBotToken);
                    log.info("[LoginSecurity] Site access resumed via TG");
                    break;
                }
                case "g": {
                    this.ipFailWindows.remove(pend.ip);
                    this.answerCallback(callbackQueryId, "\u5df2\u6e05\u96f6\u8be5 IP \u7684\u5931\u8d25\u8ba1\u6570", false, answeringBotToken);
                    break;
                }
                case "R": {
                    boolean ok = this.removeIpFromDenylist(pend.ip);
                    this.answerCallback(callbackQueryId, (String)(ok ? "\u5df2\u89e3\u9664 IP \u7981\u6b62: " + pend.ip : "\u8be5 IP \u5df2\u4e0d\u5728\u540d\u5355\u4e2d"), false, answeringBotToken);
                    if (ok) {
                        log.info("[LoginSecurity] IP removed from denylist via TG: {}", (Object)pend.ip);
                    }
                    break;
                }
                case "r": {
                    boolean ok = this.removeDeviceFromDenylist(pend.deviceId);
                    this.answerCallback(callbackQueryId, ok ? "\u5df2\u89e3\u9664\u8bbe\u5907\u7981\u6b62" : "\u8be5\u8bbe\u5907\u5df2\u4e0d\u5728\u540d\u5355\u4e2d", false, answeringBotToken);
                    if (ok) {
                        log.info("[LoginSecurity] Device removed from denylist via TG: {}", (Object)pend.deviceId);
                    }
                    break;
                }
                default: {
                    this.answerCallback(callbackQueryId, "\u672a\u77e5\u64cd\u4f5c", false, answeringBotToken);
                }
            }
        }
        catch (Exception e) {
            log.warn("[LoginSecurity] Callback handling failed: {}", (Object)e.getMessage());
            this.answerCallback(callbackQueryId, "\u6267\u884c\u5931\u8d25", true, answeringBotToken);
        }
    }

    public String registerBlockIpCallback(String ip) {
        String ipN = LoginSecurityService.normalizeIp((String)ip);
        if (StrUtil.isBlank((CharSequence)ipN)) {
            return null;
        }
        return this.registerPending(new Pending(PendingKind.BLOCK_IP, ipN, null, System.currentTimeMillis() + 900000L));
    }

    private void answerCallback(String callbackQueryId, String text, boolean showAlert, String answeringBotToken) {
        this.notificationService.answerTelegramCallbackQuery(callbackQueryId, text, showAlert, answeringBotToken);
    }

    private static boolean isPendingPayloadValid(Pending pend) {
        return switch (pend.kind.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> StrUtil.isNotBlank((CharSequence)pend.ip);
            case 1 -> StrUtil.isNotBlank((CharSequence)pend.deviceId);
            case 4 -> StrUtil.isNotBlank((CharSequence)pend.ip);
            case 2, 3 -> true;
            case 5 -> StrUtil.isNotBlank((CharSequence)pend.ip);
            case 6 -> StrUtil.isNotBlank((CharSequence)pend.deviceId);
        };
    }

    private static boolean prefixMatchesKind(String prefix, PendingKind kind) {
        return switch (prefix) {
            case "i" -> {
                if (kind == PendingKind.BLOCK_IP) {
                    yield true;
                }
                yield false;
            }
            case "d" -> {
                if (kind == PendingKind.BLOCK_DEVICE) {
                    yield true;
                }
                yield false;
            }
            case "p" -> {
                if (kind == PendingKind.PAUSE_SITE) {
                    yield true;
                }
                yield false;
            }
            case "u" -> {
                if (kind == PendingKind.RESUME_SITE) {
                    yield true;
                }
                yield false;
            }
            case "g" -> {
                if (kind == PendingKind.IGNORE_FAILS) {
                    yield true;
                }
                yield false;
            }
            case "R" -> {
                if (kind == PendingKind.UNBLOCK_IP) {
                    yield true;
                }
                yield false;
            }
            case "r" -> {
                if (kind == PendingKind.UNBLOCK_DEVICE) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    private void sendResumeOfferAfterPause() {
        String tok = this.registerPending(new Pending(PendingKind.RESUME_SITE, null, null, System.currentTimeMillis() + 900000L));
        List<List<Map<String, CallSite>>> rows = List.of(List.of(Map.of("text", "\u6062\u590d\u5168\u7ad9\u8bbf\u95ee", "callback_data", "u|" + tok)));
        this.notificationService.sendSecurityTextWithInlineKeyboard("\u3010\u767b\u5f55\u5b89\u5168\u3011\u5168\u7ad9 API \u5df2\u6682\u505c\u3002\n\u82e5\u8bef\u64cd\u4f5c\u6216\u98ce\u9669\u89e3\u9664\uff0c\u8bf7\u70b9\u51fb\u6062\u590d\u3002", rows);
    }

    private String registerPending(Pending pending) {
        for (int i = 0; i < 12; ++i) {
            String token = RandomUtil.randomString((String)"abcdef0123456789", (int)16);
            if (this.pendingByToken.putIfAbsent(token, pending) != null) continue;
            return token;
        }
        String token = IdUtil.fastSimpleUUID();
        this.pendingByToken.put(token, pending);
        return token;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int bumpFailureCount(String ipN) {
        IpFailWindow w;
        IpFailWindow ipFailWindow = w = this.ipFailWindows.computeIfAbsent(ipN, k -> new IpFailWindow());
        synchronized (ipFailWindow) {
            long now = System.currentTimeMillis();
            if (now - w.windowStart > 900000L) {
                w.count.set(0);
                w.pauseOfferSent = false;
                w.windowStart = now;
            }
            return w.count.incrementAndGet();
        }
    }

    @Scheduled(fixedRate=120000L)
    public void purgeExpiredPending() {
        long now = System.currentTimeMillis();
        this.pendingByToken.entrySet().removeIf(e -> ((Pending)e.getValue()).expireAt < now);
    }

    private String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static String normalizeIp(String ip) {
        if (ip == null) {
            return "";
        }
        return ip.trim();
    }

    private Set<String> readIpDenylist() {
        return LoginSecurityService.parseCommaSet((String)this.notificationService.getKvValue(SysCfgEnum.LOGIN_IP_DENYLIST));
    }

    private Set<String> readDeviceDenylist() {
        return LoginSecurityService.parseCommaSet((String)this.notificationService.getKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST));
    }

    private static Set<String> parseCommaSet(String raw) {
        LinkedHashSet<String> s = new LinkedHashSet<String>();
        if (StrUtil.isBlank((CharSequence)raw)) {
            return s;
        }
        for (String p : raw.split(",")) {
            if (!StrUtil.isNotBlank((CharSequence)p)) continue;
            s.add(p.trim());
        }
        return s;
    }

    private static boolean containsIp(Set<String> set, String ip) {
        return ip != null && set.contains(ip);
    }

    private static boolean containsToken(Set<String> set, String id) {
        return id != null && set.contains(id);
    }

    private void appendIpDenylist(String ip) {
        if (StrUtil.isBlank((CharSequence)ip)) {
            return;
        }
        Set s = this.readIpDenylist();
        s.add(ip.trim());
        this.notificationService.saveKvValue(SysCfgEnum.LOGIN_IP_DENYLIST, String.join((CharSequence)",", s));
    }

    private void appendDeviceDenylist(String deviceId) {
        if (StrUtil.isBlank((CharSequence)deviceId)) {
            return;
        }
        Set s = this.readDeviceDenylist();
        s.add(deviceId.trim());
        this.notificationService.saveKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST, String.join((CharSequence)",", s));
    }

    public void sendDenylistManagementKeyboard() {
        long exp = System.currentTimeMillis() + 1800000L;
        ArrayList ips = new ArrayList(this.readIpDenylist());
        ArrayList devs = new ArrayList(this.readDeviceDenylist());
        StringBuilder text = new StringBuilder();
        text.append("\u3010\u7981\u6b62\u540d\u5355\u3011\u70b9\u4e0b\u65b9\u6309\u94ae\u89e3\u9664\u5bf9\u5e94\u9879\uff0830 \u5206\u949f\u5185\u6709\u6548\uff09\u3002\n");
        text.append("IP\uff1a").append(ips.size()).append(" \u6761\uff1b\u8bbe\u5907\uff1a").append(devs.size()).append(" \u6761\u3002\n");
        if (ips.isEmpty() && devs.isEmpty()) {
            text.append("\n\u5f53\u524d\u65e0\u7981\u6b62\u7684 IP \u4e0e\u8bbe\u5907\u3002");
            this.notificationService.sendMessage(text.toString());
            return;
        }
        int capIp = 40;
        int capDev = 40;
        ArrayList<List<Map<String, CallSite>>> rows = new ArrayList<List<Map<String, CallSite>>>();
        int ipShown = 0;
        for (String ip : ips) {
            if (ipShown >= 40) break;
            String tok = this.registerPending(new Pending(PendingKind.UNBLOCK_IP, ip, null, exp));
            rows.add(List.of(Map.of("text", "\u89e3\u9664IP " + LoginSecurityService.shortenForTelegramButton((String)ip, (int)48), "callback_data", "R|" + tok)));
            ++ipShown;
        }
        int devShown = 0;
        for (String did : devs) {
            if (devShown >= 40) break;
            String tok = this.registerPending(new Pending(PendingKind.UNBLOCK_DEVICE, null, did, exp));
            rows.add(List.of(Map.of("text", "\u89e3\u9664\u8bbe\u5907 " + LoginSecurityService.shortenForTelegramButton((String)did, (int)44), "callback_data", "r|" + tok)));
            ++devShown;
        }
        if (ips.size() > 40) {
            text.append("\n\u26a0 IP \u8f83\u591a\uff0c\u4ec5\u751f\u6210\u524d ").append(40).append(" \u6761\u7684\u89e3\u9664\u6309\u94ae\uff1b\u89e3\u9664\u540e\u53ef\u518d\u53d1 /bans\u3002");
        }
        if (devs.size() > 40) {
            text.append("\n\u26a0 \u8bbe\u5907\u8f83\u591a\uff0c\u4ec5\u751f\u6210\u524d ").append(40).append(" \u6761\u7684\u89e3\u9664\u6309\u94ae\uff1b\u89e3\u9664\u540e\u53ef\u518d\u53d1 /bans\u3002");
        }
        this.notificationService.sendSecurityTextWithInlineKeyboard(text.toString(), rows);
    }

    private static String shortenForTelegramButton(String s, int maxLen) {
        if (s == null) {
            return "";
        }
        if (s.length() <= maxLen) {
            return s;
        }
        return s.substring(0, Math.max(0, maxLen - 1)) + "\u2026";
    }

    public boolean removeIpFromDenylist(String ip) {
        if (StrUtil.isBlank((CharSequence)ip)) {
            return false;
        }
        Set s = this.readIpDenylist();
        if (!s.remove(LoginSecurityService.normalizeIp((String)ip))) {
            return false;
        }
        this.notificationService.saveKvValue(SysCfgEnum.LOGIN_IP_DENYLIST, s.isEmpty() ? "" : String.join((CharSequence)",", s));
        return true;
    }

    public boolean removeDeviceFromDenylist(String deviceId) {
        if (StrUtil.isBlank((CharSequence)deviceId)) {
            return false;
        }
        Set s = this.readDeviceDenylist();
        if (!s.remove(deviceId.trim())) {
            return false;
        }
        this.notificationService.saveKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST, s.isEmpty() ? "" : String.join((CharSequence)",", s));
        return true;
    }

    public List<String> listBannedIps() {
        return new ArrayList<String>(this.readIpDenylist());
    }

    public List<String> listBannedDevices() {
        return new ArrayList<String>(this.readDeviceDenylist());
    }

    public void addIpToDenylist(String ip) {
        this.appendIpDenylist(ip);
    }

    public void addDeviceToDenylist(String deviceId) {
        this.appendDeviceDenylist(deviceId);
    }

    public String readDeviceIdFromRequest(HttpServletRequest request) {
        return HttpRequestUtil.getCookie((HttpServletRequest)request, (String)"ow_did");
    }
}

