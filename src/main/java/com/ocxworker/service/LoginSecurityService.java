package com.ocxworker.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LoginSecurityService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LoginSecurityService.class);
    private static final long PENDING_TTL_MS = 900000L;
    private static final long DENYLIST_UI_TTL_MS = 1800000L;
    private static final long FAIL_WINDOW_MS = 900000L;
    private static final int PAUSE_OFFER_THRESHOLD = 5;
    private final ConcurrentHashMap<String, LoginSecurityService.Pending> pendingByToken = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LoginSecurityService.IpFailWindow> ipFailWindows = new ConcurrentHashMap<>();
    @Resource
    private NotificationService notificationService;
    @Resource
    private VerifyCodeService verifyCodeService;

    public boolean isSitePaused() {
        String v = this.notificationService.getKvValue(SysCfgEnum.SITE_ACCESS_PAUSED);
        return "true".equalsIgnoreCase(StrUtil.trim(v));
    }

    public void setSitePaused(boolean paused) {
        this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, paused ? "true" : "false");
        log.warn("[LoginSecurity] site_access_paused = {}", paused);
    }

    public boolean isDeniedForLogin(String ip, String deviceId) {
        return containsIp(this.readIpDenylist(), normalizeIp(ip))
            ? true
            : StrUtil.isNotBlank(deviceId) && containsToken(this.readDeviceDenylist(), deviceId.trim());
    }

    public boolean isLoginHardenedPath(String uri) {
        return uri == null ? false : "/api/auth/login".equals(uri) || "/api/auth/tgLogin".equals(uri) || "/api/auth/tgLoginSendCode".equals(uri);
    }

    public boolean isExemptFromSitePause(String uri) {
        if (uri == null) {
            return false;
        } else if (uri.startsWith("/api/auth/device")) {
            return true;
        } else if (uri.startsWith("/api/auth/needSetup")) {
            return true;
        } else if (uri.startsWith("/api/auth/setup")) {
            return true;
        } else if (uri.startsWith("/ws/")) {
            return true;
        } else if (uri.equals("/") || uri.startsWith("/assets/")) {
            return true;
        } else if (uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".ico")) {
            return true;
        } else {
            return uri.startsWith("/webssh/") ? true : uri.startsWith("/ip-info");
        }
    }

    public void onPasswordLoginFailed(String account, String ip, String deviceId) {
        String ipN = normalizeIp(ip);
        String dev = StrUtil.isBlank(deviceId) ? null : deviceId.trim();
        if (!this.verifyCodeService.isTgConfigured()) {
            this.notificationService.sendMessage("login", String.format("【登录通知】⚠️ 登录失败\n账号: %s\nIP: %s\n时间: %s", account, ipN, this.nowStr()));
        } else {
            String tokIp = this.registerPending(
                new LoginSecurityService.Pending(LoginSecurityService.PendingKind.BLOCK_IP, ipN, null, System.currentTimeMillis() + 900000L)
            );
            String tokDev = null;
            if (StrUtil.isNotBlank(dev)) {
                tokDev = this.registerPending(
                    new LoginSecurityService.Pending(LoginSecurityService.PendingKind.BLOCK_DEVICE, ipN, dev, System.currentTimeMillis() + 900000L)
                );
            }

            List<List<Map<String, String>>> rows = new ArrayList<>();
            List<Map<String, String>> row1 = new ArrayList<>();
            row1.add(Map.of("text", "拉黑该IP", "callback_data", "i|" + tokIp));
            rows.add(row1);
            if (tokDev != null) {
                List<Map<String, String>> row2 = new ArrayList<>();
                row2.add(Map.of("text", "禁止该设备", "callback_data", "d|" + tokDev));
                rows.add(row2);
            }

            String text = String.format(
                "【登录通知】⚠️ 登录失败\n账号: %s\nIP: %s\n设备: %s\n时间: %s\n\n（15 分钟内有效）点击下方按钮执行操作。", account, ipN, dev != null ? dev : "未知", this.nowStr()
            );
            this.notificationService.sendSecurityTextWithInlineKeyboard(text, rows);
            int n = this.bumpFailureCount(ipN);
            if (n == 5) {
                this.maybeSendPauseOffer(ipN, n);
            }
        }
    }

    private void maybeSendPauseOffer(String ipN, int n) {
        LoginSecurityService.IpFailWindow w = this.ipFailWindows.get(ipN);
        if (w != null) {
            synchronized (w) {
                if (n != 5 || w.pauseOfferSent) {
                    return;
                }

                w.pauseOfferSent = true;
            }

            String tokPause = this.registerPending(
                new LoginSecurityService.Pending(LoginSecurityService.PendingKind.PAUSE_SITE, ipN, null, System.currentTimeMillis() + 900000L)
            );
            String tokIgnore = this.registerPending(
                new LoginSecurityService.Pending(LoginSecurityService.PendingKind.IGNORE_FAILS, ipN, null, System.currentTimeMillis() + 900000L)
            );
            List<List<Map<String, String>>> rows = List.of(
                List.of(Map.of("text", "暂停全站访问", "callback_data", "p|" + tokPause), Map.of("text", "忽略(清零计数)", "callback_data", "g|" + tokIgnore))
            );
            String text = String.format("【登录安全】同一 IP 在 %d 分钟内已连续密码登录失败 %d 次\nIP: %s\n\n可选择暂停整站 API 访问（仍可通过下方「恢复」与 Telegram 内联按钮解除），或仅清零计数。", 15L, 5, ipN);
            this.notificationService.sendSecurityTextWithInlineKeyboard(text, rows);
        }
    }

    public void handleTelegramCallback(String rawData, String callbackQueryId) {
        this.handleTelegramCallback(rawData, callbackQueryId, null);
    }

    public void handleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (!StrUtil.isBlank(callbackQueryId)) {
            if (rawData != null && rawData.contains("|")) {
                int p = rawData.indexOf(124);
                String prefix = rawData.substring(0, p);
                String token = rawData.substring(p + 1);
                if (token.length() > 32) {
                    this.answerCallback(callbackQueryId, "无效操作", false, answeringBotToken);
                } else {
                    LoginSecurityService.Pending pend = this.pendingByToken.get(token);
                    if (pend == null || System.currentTimeMillis() > pend.expireAt) {
                        this.answerCallback(callbackQueryId, "操作已过期，请重新发送 /bans 或重新登录后再试", false, answeringBotToken);
                    } else if (!prefixMatchesKind(prefix, pend.kind)) {
                        this.answerCallback(callbackQueryId, "无效操作", false, answeringBotToken);
                    } else if (!isPendingPayloadValid(pend)) {
                        this.answerCallback(callbackQueryId, "数据无效", false, answeringBotToken);
                    } else {
                        this.pendingByToken.remove(token);

                        try {
                            switch (prefix) {
                                case "i":
                                    this.appendIpDenylist(pend.ip);
                                    this.answerCallback(callbackQueryId, "已拉黑 IP: " + pend.ip, false, answeringBotToken);
                                    log.warn("[LoginSecurity] IP denylisted via TG: {}", pend.ip);
                                    break;
                                case "d":
                                    this.appendDeviceDenylist(pend.deviceId);
                                    this.answerCallback(callbackQueryId, "已禁止设备: " + pend.deviceId, false, answeringBotToken);
                                    log.warn("[LoginSecurity] Device denylisted via TG: {}", pend.deviceId);
                                    break;
                                case "p":
                                    this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, "true");
                                    this.answerCallback(callbackQueryId, "全站 API 已暂停（静态页与 TG 回调仍可用）", false, answeringBotToken);
                                    log.warn("[LoginSecurity] Site access paused via TG, trigger IP: {}", pend.ip);
                                    this.sendResumeOfferAfterPause();
                                    break;
                                case "u":
                                    this.notificationService.saveKvValue(SysCfgEnum.SITE_ACCESS_PAUSED, "false");
                                    this.answerCallback(callbackQueryId, "已恢复全站访问", false, answeringBotToken);
                                    log.info("[LoginSecurity] Site access resumed via TG");
                                    break;
                                case "g":
                                    this.ipFailWindows.remove(pend.ip);
                                    this.answerCallback(callbackQueryId, "已清零该 IP 的失败计数", false, answeringBotToken);
                                    break;
                                case "R":
                                    boolean okx = this.removeIpFromDenylist(pend.ip);
                                    this.answerCallback(callbackQueryId, okx ? "已解除 IP 禁止: " + pend.ip : "该 IP 已不在名单中", false, answeringBotToken);
                                    if (okx) {
                                        log.info("[LoginSecurity] IP removed from denylist via TG: {}", pend.ip);
                                    }
                                    break;
                                case "r":
                                    boolean ok = this.removeDeviceFromDenylist(pend.deviceId);
                                    this.answerCallback(callbackQueryId, ok ? "已解除设备禁止" : "该设备已不在名单中", false, answeringBotToken);
                                    if (ok) {
                                        log.info("[LoginSecurity] Device removed from denylist via TG: {}", pend.deviceId);
                                    }
                                    break;
                                default:
                                    this.answerCallback(callbackQueryId, "未知操作", false, answeringBotToken);
                            }
                        } catch (Exception var11) {
                            log.warn("[LoginSecurity] Callback handling failed: {}", var11.getMessage());
                            this.answerCallback(callbackQueryId, "执行失败", true, answeringBotToken);
                        }
                    }
                }
            } else {
                this.answerCallback(callbackQueryId, "无效操作", false, answeringBotToken);
            }
        }
    }

    public String registerBlockIpCallback(String ip) {
        String ipN = normalizeIp(ip);
        return StrUtil.isBlank(ipN)
            ? null
            : this.registerPending(new LoginSecurityService.Pending(LoginSecurityService.PendingKind.BLOCK_IP, ipN, null, System.currentTimeMillis() + 900000L));
    }

    private void answerCallback(String callbackQueryId, String text, boolean showAlert, String answeringBotToken) {
        this.notificationService.answerTelegramCallbackQuery(callbackQueryId, text, showAlert, answeringBotToken);
    }

    private static boolean isPendingPayloadValid(LoginSecurityService.Pending pend) {
        return switch (pend.kind) {
            case BLOCK_IP -> StrUtil.isNotBlank(pend.ip);
            case BLOCK_DEVICE -> StrUtil.isNotBlank(pend.deviceId);
            case PAUSE_SITE, RESUME_SITE -> true;
            case IGNORE_FAILS -> StrUtil.isNotBlank(pend.ip);
            case UNBLOCK_IP -> StrUtil.isNotBlank(pend.ip);
            case UNBLOCK_DEVICE -> StrUtil.isNotBlank(pend.deviceId);
        };
    }

    private static boolean prefixMatchesKind(String prefix, LoginSecurityService.PendingKind kind) {
        return switch (prefix) {
            case "i" -> kind == LoginSecurityService.PendingKind.BLOCK_IP;
            case "d" -> kind == LoginSecurityService.PendingKind.BLOCK_DEVICE;
            case "p" -> kind == LoginSecurityService.PendingKind.PAUSE_SITE;
            case "u" -> kind == LoginSecurityService.PendingKind.RESUME_SITE;
            case "g" -> kind == LoginSecurityService.PendingKind.IGNORE_FAILS;
            case "R" -> kind == LoginSecurityService.PendingKind.UNBLOCK_IP;
            case "r" -> kind == LoginSecurityService.PendingKind.UNBLOCK_DEVICE;
            default -> false;
        };
    }

    private void sendResumeOfferAfterPause() {
        String tok = this.registerPending(
            new LoginSecurityService.Pending(LoginSecurityService.PendingKind.RESUME_SITE, null, null, System.currentTimeMillis() + 900000L)
        );
        List<List<Map<String, String>>> rows = List.of(List.of(Map.of("text", "恢复全站访问", "callback_data", "u|" + tok)));
        this.notificationService.sendSecurityTextWithInlineKeyboard("【登录安全】全站 API 已暂停。\n若误操作或风险解除，请点击恢复。", rows);
    }

    private String registerPending(LoginSecurityService.Pending pending) {
        for (int i = 0; i < 12; i++) {
            String token = RandomUtil.randomString("abcdef0123456789", 16);
            if (this.pendingByToken.putIfAbsent(token, pending) == null) {
                return token;
            }
        }

        String token = IdUtil.fastSimpleUUID();
        this.pendingByToken.put(token, pending);
        return token;
    }

    private int bumpFailureCount(String ipN) {
        LoginSecurityService.IpFailWindow w = this.ipFailWindows.computeIfAbsent(ipN, k -> new LoginSecurityService.IpFailWindow());
        synchronized (w) {
            long now = System.currentTimeMillis();
            if (now - w.windowStart > 900000L) {
                w.count.set(0);
                w.pauseOfferSent = false;
                w.windowStart = now;
            }

            return w.count.incrementAndGet();
        }
    }

    @Scheduled(
        fixedRate = 120000L
    )
    public void purgeExpiredPending() {
        long now = System.currentTimeMillis();
        this.pendingByToken.entrySet().removeIf(e -> e.getValue().expireAt < now);
    }

    private String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static String normalizeIp(String ip) {
        return ip == null ? "" : ip.trim();
    }

    private Set<String> readIpDenylist() {
        return parseCommaSet(this.notificationService.getKvValue(SysCfgEnum.LOGIN_IP_DENYLIST));
    }

    private Set<String> readDeviceDenylist() {
        return parseCommaSet(this.notificationService.getKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST));
    }

    private static Set<String> parseCommaSet(String raw) {
        Set<String> s = new LinkedHashSet<>();
        if (StrUtil.isBlank(raw)) {
            return s;
        } else {
            for (String p : raw.split(",")) {
                if (StrUtil.isNotBlank(p)) {
                    s.add(p.trim());
                }
            }

            return s;
        }
    }

    private static boolean containsIp(Set<String> set, String ip) {
        return ip != null && set.contains(ip);
    }

    private static boolean containsToken(Set<String> set, String id) {
        return id != null && set.contains(id);
    }

    private void appendIpDenylist(String ip) {
        if (!StrUtil.isBlank(ip)) {
            Set<String> s = this.readIpDenylist();
            s.add(ip.trim());
            this.notificationService.saveKvValue(SysCfgEnum.LOGIN_IP_DENYLIST, String.join(",", s));
        }
    }

    private void appendDeviceDenylist(String deviceId) {
        if (!StrUtil.isBlank(deviceId)) {
            Set<String> s = this.readDeviceDenylist();
            s.add(deviceId.trim());
            this.notificationService.saveKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST, String.join(",", s));
        }
    }

    public void sendDenylistManagementKeyboard() {
        long exp = System.currentTimeMillis() + 1800000L;
        List<String> ips = new ArrayList<>(this.readIpDenylist());
        List<String> devs = new ArrayList<>(this.readDeviceDenylist());
        StringBuilder text = new StringBuilder();
        text.append("【禁止名单】点下方按钮解除对应项（30 分钟内有效）。\n");
        text.append("IP：").append(ips.size()).append(" 条；设备：").append(devs.size()).append(" 条。\n");
        if (ips.isEmpty() && devs.isEmpty()) {
            text.append("\n当前无禁止的 IP 与设备。");
            this.notificationService.sendMessage(text.toString());
        } else {
            int capIp = 40;
            int capDev = 40;
            List<List<Map<String, String>>> rows = new ArrayList<>();
            int ipShown = 0;

            for (String ip : ips) {
                if (ipShown >= 40) {
                    break;
                }

                String tok = this.registerPending(new LoginSecurityService.Pending(LoginSecurityService.PendingKind.UNBLOCK_IP, ip, null, exp));
                rows.add(List.of(Map.of("text", "解除IP " + shortenForTelegramButton(ip, 48), "callback_data", "R|" + tok)));
                ipShown++;
            }

            int devShown = 0;

            for (String did : devs) {
                if (devShown >= 40) {
                    break;
                }

                String tok = this.registerPending(new LoginSecurityService.Pending(LoginSecurityService.PendingKind.UNBLOCK_DEVICE, null, did, exp));
                rows.add(List.of(Map.of("text", "解除设备 " + shortenForTelegramButton(did, 44), "callback_data", "r|" + tok)));
                devShown++;
            }

            if (ips.size() > 40) {
                text.append("\n⚠ IP 较多，仅生成前 ").append(40).append(" 条的解除按钮；解除后可再发 /bans。");
            }

            if (devs.size() > 40) {
                text.append("\n⚠ 设备较多，仅生成前 ").append(40).append(" 条的解除按钮；解除后可再发 /bans。");
            }

            this.notificationService.sendSecurityTextWithInlineKeyboard(text.toString(), rows);
        }
    }

    private static String shortenForTelegramButton(String s, int maxLen) {
        if (s == null) {
            return "";
        } else {
            return s.length() <= maxLen ? s : s.substring(0, Math.max(0, maxLen - 1)) + "…";
        }
    }

    public boolean removeIpFromDenylist(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        } else {
            Set<String> s = this.readIpDenylist();
            if (!s.remove(normalizeIp(ip))) {
                return false;
            } else {
                this.notificationService.saveKvValue(SysCfgEnum.LOGIN_IP_DENYLIST, s.isEmpty() ? "" : String.join(",", s));
                return true;
            }
        }
    }

    public boolean removeDeviceFromDenylist(String deviceId) {
        if (StrUtil.isBlank(deviceId)) {
            return false;
        } else {
            Set<String> s = this.readDeviceDenylist();
            if (!s.remove(deviceId.trim())) {
                return false;
            } else {
                this.notificationService.saveKvValue(SysCfgEnum.LOGIN_DEVICE_DENYLIST, s.isEmpty() ? "" : String.join(",", s));
                return true;
            }
        }
    }

    public List<String> listBannedIps() {
        return new ArrayList<>(this.readIpDenylist());
    }

    public List<String> listBannedDevices() {
        return new ArrayList<>(this.readDeviceDenylist());
    }

    public void addIpToDenylist(String ip) {
        this.appendIpDenylist(ip);
    }

    public void addDeviceToDenylist(String deviceId) {
        this.appendDeviceDenylist(deviceId);
    }

    public String readDeviceIdFromRequest(HttpServletRequest request) {
        return HttpRequestUtil.getCookie(request, "ow_did");
    }

    private static final class IpFailWindow {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
        volatile boolean pauseOfferSent;
    }

    private static record Pending(LoginSecurityService.PendingKind kind, String ip, String deviceId, long expireAt) {
    }

    private static enum PendingKind {
        BLOCK_IP,
        BLOCK_DEVICE,
        PAUSE_SITE,
        RESUME_SITE,
        IGNORE_FAILS,
        UNBLOCK_IP,
        UNBLOCK_DEVICE;
    }
}
