package com.ocxworker.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class HttpRequestUtil {
    private HttpRequestUtil() {
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        } else {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }

            return ip != null ? ip : "";
        }
    }

    public static String getCookie(HttpServletRequest request, String name) {
        if (request != null && name != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            } else {
                for (Cookie c : cookies) {
                    if (name.equals(c.getName())) {
                        String v = c.getValue();
                        return v != null && !v.isBlank() ? v.trim() : null;
                    }
                }

                return null;
            }
        } else {
            return null;
        }
    }

    public static String getCookieValueFromCookieHeader(String cookieHeader, String name) {
        if (cookieHeader != null && name != null) {
            for (String part : cookieHeader.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && name.equals(kv[0].trim())) {
                    String v = kv[1].trim();
                    return v.isBlank() ? null : v;
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
