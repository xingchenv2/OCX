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
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Service
public class LoginAuditService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LoginAuditService.class);
    private static final int PASSWORD_FIELD_MAX = 500;
    private static final int LOGIN_DETAIL_JSON_MAX = 15500000;
    private static final int SINGLE_HEADER_VALUE_MAX = 524288;
    @Resource
    private OciLoginAuditMapper loginAuditMapper;

    public static LoginAuditService.ParsedUa parseUserAgent(String ua) {
        if (ua != null && !ua.isBlank()) {
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

            return new LoginAuditService.ParsedUa(os, browser);
        } else {
            return new LoginAuditService.ParsedUa("未知", "未知");
        }
    }

    public void recordPasswordLogin(String account, String passwordPlain, String ip, String deviceId, boolean success, HttpServletRequest request) {
        String ua = request != null ? request.getHeader("User-Agent") : null;
        this.insertRow(account, passwordPlain, ip, deviceId, success, ua, "password", request);
    }

    public void recordTelegramLogin(String account, String ip, String deviceId, boolean success, HttpServletRequest request, String passwordPlaceholder) {
        String ua = request != null ? request.getHeader("User-Agent") : null;
        this.insertRow(account, passwordPlaceholder, ip, deviceId, success, ua, "telegram", request);
    }

    private void insertRow(
        String account, String passwordPlain, String ip, String deviceId, boolean success, String userAgent, String channel, HttpServletRequest request
    ) {
        try {
            LoginAuditService.ParsedUa p = parseUserAgent(userAgent);
            OciLoginAudit row = new OciLoginAudit();
            row.setId(CommonUtils.generateId());
            row.setAccount(StrUtil.trimToNull(account));
            row.setPasswordAttempt(truncatePwd(passwordPlain));
            row.setIp(ip != null ? ip.trim() : null);
            row.setSuccess(success);
            row.setDeviceId(StrUtil.trimToNull(deviceId));
            row.setOsName(p.os());
            row.setBrowserName(p.browser());
            row.setLoginChannel(channel);
            row.setUserAgent(userAgent != null && userAgent.length() > 2000 ? userAgent.substring(0, 2000) : userAgent);
            row.setLoginDetail(buildLoginDetailJson(request));
            row.setCreateTime(LocalDateTime.now());
            this.loginAuditMapper.insert(row);
        } catch (Exception var11) {
            log.warn("[LoginAudit] insert skipped: {}", var11.getMessage());
        }
    }

    private static String buildLoginDetailJson(HttpServletRequest req) {
        if (req == null) {
            return null;
        } else {
            try {
                Map<String, Object> root = new LinkedHashMap<>();
                Map<String, Object> entry = new LinkedHashMap<>();
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
                Map<String, Object> net = new LinkedHashMap<>();
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
                Map<String, Object> fetch = new LinkedHashMap<>();
                fetch.put("Sec-Fetch-Site", nz(req.getHeader("Sec-Fetch-Site")));
                fetch.put("Sec-Fetch-Mode", nz(req.getHeader("Sec-Fetch-Mode")));
                fetch.put("Sec-Fetch-Dest", nz(req.getHeader("Sec-Fetch-Dest")));
                fetch.put("Sec-Fetch-User", nz(req.getHeader("Sec-Fetch-User")));
                fetch.put("Sec-Fetch-Priority", nz(req.getHeader("Sec-Fetch-Priority")));
                root.put("Fetch 元数据", fetch);
                Map<String, Object> hints = new LinkedHashMap<>();
                hints.put("Sec-CH-UA", nz(req.getHeader("Sec-CH-UA")));
                hints.put("Sec-CH-UA-Full-Version-List", nz(req.getHeader("Sec-CH-UA-Full-Version-List")));
                hints.put("Sec-CH-UA-Platform", nz(req.getHeader("Sec-CH-UA-Platform")));
                hints.put("Sec-CH-UA-Platform-Version", nz(req.getHeader("Sec-CH-UA-Platform-Version")));
                hints.put("Sec-CH-UA-Mobile", nz(req.getHeader("Sec-CH-UA-Mobile")));
                hints.put("Sec-CH-UA-Model", nz(req.getHeader("Sec-CH-UA-Model")));
                hints.put("Sec-CH-UA-Arch", nz(req.getHeader("Sec-CH-UA-Arch")));
                hints.put("Sec-CH-UA-Bitness", nz(req.getHeader("Sec-CH-UA-Bitness")));
                hints.put("Sec-CH-Viewport-Width", nz(req.getHeader("Sec-CH-Viewport-Width")));
                hints.put("Viewport-Width", nz(req.getHeader("Viewport-Width")));
                hints.put("Device-Memory", nz(req.getHeader("Device-Memory")));
                hints.put("DPR", nz(req.getHeader("DPR")));
                hints.put("Downlink", nz(req.getHeader("Downlink")));
                hints.put("RTT", nz(req.getHeader("RTT")));
                hints.put("ECT", nz(req.getHeader("ECT")));
                hints.put("Save-Data", nz(req.getHeader("Save-Data")));
                root.put("Client Hints", hints);
                Map<String, Object> client = new LinkedHashMap<>();
                client.put("Accept-Language", nz(req.getHeader("Accept-Language")));
                client.put("Accept-Encoding", nz(req.getHeader("Accept-Encoding")));
                client.put("Accept", nz(req.getHeader("Accept")));
                client.put("User-Agent", nz(req.getHeader("User-Agent")));
                String did = HttpRequestUtil.getCookie(req, "ow_did");
                client.put("设备Cookie(ow_did)已携带", StrUtil.isNotBlank(did) ? "是" : "否");
                client.put("ow_did(明文)", nz(did));
                root.put("客户端与能力", client);
                Map<String, Object> allHeaders = new TreeMap<>();
                Enumeration<String> names = req.getHeaderNames();
                if (names != null) {
                    for (String hn : Collections.list(names)) {
                        if (hn != null) {
                            String v = req.getHeader(hn);
                            allHeaders.put(hn, truncPlain(v, 524288));
                        }
                    }
                }

                root.put("全部请求头（明文）", allHeaders);
                Map<String, Object> raw = new LinkedHashMap<>();
                raw.put("Cookie", nz(req.getHeader("Cookie")));
                raw.put("Authorization", nz(req.getHeader("Authorization")));
                raw.put("RequestBody", readCachedRequestBody(req));
                root.put("请求原文（高敏感）", raw);
                String json = JSONUtil.toJsonStr(root);
                return json.length() > 15500000 ? json.substring(0, 15500000) + "…(login_detail JSON 超长已截断)" : json;
            } catch (Exception var13) {
                log.warn("[LoginAudit] buildLoginDetailJson: {}", var13.getMessage());
                return null;
            }
        }
    }

    private static String readCachedRequestBody(HttpServletRequest req) {
        if (req instanceof ContentCachingRequestWrapper w) {
            byte[] buf = w.getContentAsByteArray();
            if (buf != null && buf.length != 0) {
                Charset cs = StandardCharsets.UTF_8;
                String enc = req.getCharacterEncoding();
                if (enc != null && !enc.isBlank()) {
                    try {
                        cs = Charset.forName(enc.trim());
                    } catch (Exception var6) {
                    }
                }

                return new String(buf, cs);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private static String truncPlain(String s, int max) {
        if (s == null) {
            return "";
        } else {
            return s.length() <= max ? s : s.substring(0, max) + "…(超长已截断)";
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    private static String truncatePwd(String p) {
        if (p == null) {
            return null;
        } else {
            return p.length() <= 500 ? p : p.substring(0, 500);
        }
    }

    public IPage<OciLoginAudit> pageAudits(long current, long size) {
        return this.loginAuditMapper.selectPage(new Page(current, size), (Wrapper)new LambdaQueryWrapper<OciLoginAudit>().orderByDesc(OciLoginAudit::getCreateTime));
    }

    @Scheduled(
        cron = "0 0 3 * * ?"
    )
    public void purgeOlderThanSevenDays() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7L);
            int n = this.loginAuditMapper.delete((Wrapper)new LambdaQueryWrapper<OciLoginAudit>().lt(OciLoginAudit::getCreateTime, cutoff));
            if (n > 0) {
                log.info("[LoginAudit] purged {} rows older than 7 days", n);
            }
        } catch (Exception var3) {
            log.warn("[LoginAudit] purge failed (表可能尚未创建): {}", var3.getMessage());
        }
    }

    public static record ParsedUa(String os, String browser) {
    }
}
