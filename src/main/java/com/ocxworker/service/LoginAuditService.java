package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ocxworker.mapper.OciLoginAuditMapper;
import com.ocxworker.model.entity.OciLoginAudit;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TreeMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * LoginAuditService — Security-hardened version.
 *
 * Key fixes:
 *   1. passwordAttempt is NEVER stored in plaintext. Always replaced with "****".
 *      The original code stored the actual password in the database — any DB breach
 *      or admin viewing the audit table would see all login passwords in clear text.
 *   2. loginDetail JSON no longer includes raw Cookie, Authorization header, or RequestBody.
 *      These were being stored verbatim, meaning OAuth tokens, session cookies, and
 *      password POST bodies were all persisted to disk in a queryable table.
 *   3. Sensitive header values (Cookie, Authorization) are replaced with "[REDACTED]".
 */
@Service
public class LoginAuditService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LoginAuditService.class);
    private static final int LOGIN_DETAIL_JSON_MAX = 15500000;
    private static final int SINGLE_HEADER_VALUE_MAX = 524288;
    @Resource
    private OciLoginAuditMapper loginAuditMapper;

    public static ParsedUa parseUserAgent(String ua) {
        if (ua == null || ua.isBlank()) {
            return new ParsedUa("未知", "未知");
        }
        String u = ua.toLowerCase(Locale.ROOT);
        String os = "未知";
        if (u.contains("windows")) {
            os = "Windows";
        } else if (u.contains("android")) {
            os = "Android";
        } else if (u.contains("iphone") || u.contains("ipad") || u.contains("ios")) {
            os = "iOS";
        } else if (u.contains("mac os") || u.contains("macintosh")) {
            os = "macOS";
        } else if (u.contains("linux")) {
            os = "Linux";
        }
        String browser = "未知";
        if (u.contains("edg/")) {
            browser = "Edge";
        } else if (u.contains("opr/") || u.contains("opera")) {
            browser = "Opera";
        } else if (u.contains("firefox/")) {
            browser = "Firefox";
        } else if (u.contains("chrome/") || u.contains("crios/")) {
            browser = "Chrome";
        } else if (u.contains("safari/") && !u.contains("chrome")) {
            browser = "Safari";
        }
        return new ParsedUa(os, browser);
    }

    /**
     * Record password login attempt.
     * FIX: passwordPlain is NEVER stored. We only record success/failure status.
     */
    public void recordPasswordLogin(String account, String passwordPlain, String ip, String deviceId, boolean success, HttpServletRequest request) {
        String ua = request != null ? request.getHeader("User-Agent") : null;
        this.insertRow(account, ip, deviceId, success, ua, "password", request);
    }

    public void recordTelegramLogin(String account, String ip, String deviceId, boolean success, HttpServletRequest request, String note) {
        String ua = request != null ? request.getHeader("User-Agent") : null;
        this.insertRow(account, ip, deviceId, success, ua, "telegram", request);
    }

    /**
     * FIX: No longer accepts or stores passwordPlain.
     * The passwordAttempt field is always set to indicate only attempt/success status.
     */
    private void insertRow(String account, String ip, String deviceId, boolean success, String userAgent, String channel, HttpServletRequest request) {
        try {
            ParsedUa p = LoginAuditService.parseUserAgent(userAgent);
            OciLoginAudit row = new OciLoginAudit();
            row.setId(CommonUtils.generateId());
            row.setAccount(StrUtil.trimToNull((CharSequence) account));
            // FIX: Never store the password, even truncated. Only store status indicator.
            row.setPasswordAttempt(success ? "[success]" : "[failed]");
            row.setIp(ip != null ? ip.trim() : null);
            row.setSuccess(Boolean.valueOf(success));
            row.setDeviceId(StrUtil.trimToNull((CharSequence) deviceId));
            row.setOsName(p.os());
            row.setBrowserName(p.browser());
            row.setLoginChannel(channel);
            row.setUserAgent(userAgent != null && userAgent.length() > 2000 ? userAgent.substring(0, 2000) : userAgent);
            row.setLoginDetail(LoginAuditService.buildLoginDetailJson(request));
            row.setCreateTime(LocalDateTime.now());
            this.loginAuditMapper.insert((Object) row);
        } catch (Exception e) {
            log.warn("[LoginAudit] insert skipped: {}", (Object) e.getMessage());
        }
    }

    /**
     * FIX: Build login detail JSON WITHOUT storing raw Cookie, Authorization,
     * or request body. These are replaced with [REDACTED] indicators.
     */
    private static String buildLoginDetailJson(HttpServletRequest req) {
        if (req == null) {
            return null;
        }
        try {
            LinkedHashMap<String, AbstractMap> root = new LinkedHashMap<>();
            LinkedHashMap<String, String> entry = new LinkedHashMap<>();
            entry.put("Method", nz(req.getMethod()));
            entry.put("RequestURI", nz(req.getRequestURI()));
            entry.put("QueryString", nz(req.getQueryString()));
            entry.put("Content-Type", nz(req.getContentType()));
            entry.put("CharacterEncoding", nz(req.getCharacterEncoding()));
            entry.put("Host", nz(req.getHeader("Host")));
            entry.put("X-Forwarded-Host", nz(req.getHeader("X-Forwarded-Host")));
            entry.put("X-Forwarded-Proto", nz(req.getHeader("X-Forwarded-Proto")));
            entry.put("Origin", nz(req.getHeader("Origin")));
            entry.put("Referer", nz(req.getHeader("Referer")));
            root.put("访问入口", entry);

            LinkedHashMap<String, String> net = new LinkedHashMap<>();
            net.put("X-Forwarded-For", nz(req.getHeader("X-Forwarded-For")));
            net.put("X-Real-IP", nz(req.getHeader("X-Real-IP")));
            net.put("Forwarded", nz(req.getHeader("Forwarded")));
            net.put("Via", nz(req.getHeader("Via")));
            net.put("Proxy-Connection", nz(req.getHeader("Proxy-Connection")));
            net.put("CF-Ray", nz(req.getHeader("CF-Ray")));
            net.put("CF-Connecting-IP", nz(req.getHeader("CF-Connecting-IP")));
            net.put("CF-Visitor", nz(req.getHeader("CF-Visitor")));
            net.put("True-Client-IP", nz(req.getHeader("True-Client-IP")));
            net.put("X-Request-Id", nz(req.getHeader("X-Request-Id")));
            net.put("X-Correlation-Id", nz(req.getHeader("X-Correlation-Id")));
            net.put("X-Amzn-Trace-Id", nz(req.getHeader("X-Amzn-Trace-Id")));
            net.put("Fastly-Client-IP", nz(req.getHeader("Fastly-Client-IP")));
            net.put("Fly-Client-IP", nz(req.getHeader("Fly-Client-IP")));
            net.put("RemoteAddr", nz(req.getRemoteAddr()));
            net.put("RemotePort", String.valueOf(req.getRemotePort()));
            net.put("LocalAddr", nz(req.getLocalAddr()));
            net.put("LocalPort", String.valueOf(req.getLocalPort()));
            net.put("Protocol", nz(req.getProtocol()));
            root.put("网络与链路", net);

            LinkedHashMap<String, String> fetch = new LinkedHashMap<>();
            fetch.put("Sec-Fetch-Site", nz(req.getHeader("Sec-Fetch-Site")));
            fetch.put("Sec-Fetch-Mode", nz(req.getHeader("Sec-Fetch-Mode")));
            fetch.put("Sec-Fetch-Dest", nz(req.getHeader("Sec-Fetch-Dest")));
            fetch.put("Sec-Fetch-User", nz(req.getHeader("Sec-Fetch-User")));
            fetch.put("Sec-Fetch-Priority", nz(req.getHeader("Sec-Fetch-Priority")));
            root.put("Fetch 元数据", fetch);

            LinkedHashMap<String, String> hints = new LinkedHashMap<>();
            hints.put("Sec-CH-UA", nz(req.getHeader("Sec-CH-UA")));
            hints.put("Sec-CH-UA-Platform", nz(req.getHeader("Sec-CH-UA-Platform")));
            root.put("Client Hints", hints);

            LinkedHashMap<String, String> client = new LinkedHashMap<>();
            client.put("Accept-Language", nz(req.getHeader("Accept-Language")));
            client.put("Accept-Encoding", nz(req.getHeader("Accept-Encoding")));
            client.put("Accept", nz(req.getHeader("Accept")));
            client.put("User-Agent", nz(req.getHeader("User-Agent")));
            String did = HttpRequestUtil.getCookie(req, "ow_did");
            client.put("设备Cookie(ow_did)已携带", StrUtil.isNotBlank((CharSequence) did) ? "是" : "否");
            root.put("客户端与能力", client);

            // FIX: Redact sensitive headers from the all-headers dump
            TreeMap<String, String> allHeaders = new TreeMap<>();
            Enumeration<String> names = req.getHeaderNames();
            if (names != null) {
                for (String hn : Collections.list(names)) {
                    if (hn == null) continue;
                    String v = req.getHeader(hn);
                    // FIX: Redact sensitive headers
                    String lower = hn.toLowerCase(Locale.ROOT);
                    if (lower.equals("cookie") || lower.equals("authorization") ||
                        lower.equals("set-cookie") || lower.startsWith("proxy-authorization")) {
                        allHeaders.put(hn, "[REDACTED]");
                    } else {
                        allHeaders.put(hn, truncPlain(v, SINGLE_HEADER_VALUE_MAX));
                    }
                }
            }
            root.put("全部请求头", allHeaders);

            // FIX: Removed the "请求原文（高敏感）" section entirely.
            // Original code stored raw Cookie, Authorization header, and request body
            // (which contains the login password in the POST body) in the database.
            String json = JSONUtil.toJsonStr(root);
            if (json.length() > LOGIN_DETAIL_JSON_MAX) {
                return json.substring(0, LOGIN_DETAIL_JSON_MAX) + "…(login_detail JSON 超长已截断)";
            }
            return json;
        } catch (Exception e) {
            log.warn("[LoginAudit] buildLoginDetailJson: {}", (Object) e.getMessage());
            return null;
        }
    }

    private static String truncPlain(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…(超长已截断)";
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    public IPage<OciLoginAudit> pageAudits(long current, long size) {
        return this.loginAuditMapper.selectPage((IPage) new Page(current, size),
            (Wrapper) new LambdaQueryWrapper().orderByDesc(OciLoginAudit::getCreateTime));
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void purgeOlderThanSevenDays() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7L);
            int n = this.loginAuditMapper.delete(
                (Wrapper) new LambdaQueryWrapper().lt(OciLoginAudit::getCreateTime, (Object) cutoff));
            if (n > 0) {
                log.info("[LoginAudit] purged {} rows older than 7 days", (Object) n);
            }
        } catch (Exception e) {
            log.warn("[LoginAudit] purge failed: {}", (Object) e.getMessage());
        }
    }

    public record ParsedUa(String os, String browser) {}

}
