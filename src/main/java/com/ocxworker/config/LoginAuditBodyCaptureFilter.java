/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.LoginAuditBodyCaptureFilter
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 *  org.springframework.web.util.ContentCachingRequestWrapper
 */
package com.ocxworker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
@Order(value=-2147483643)
public class LoginAuditBodyCaptureFilter
extends OncePerRequestFilter {
    private static final int MAX_BODY_CACHE = 524288;

    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !LoginAuditBodyCaptureFilter.isPasswordOrTgLoginPost((HttpServletRequest)request);
    }

    private static boolean isPasswordOrTgLoginPost(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = LoginAuditBodyCaptureFilter.pathWithoutContext((HttpServletRequest)request);
        return "/api/auth/login".equals(path) || "/api/auth/tgLogin".equals(path);
    }

    private static String pathWithoutContext(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            return uri.substring(ctx.length());
        }
        return uri;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request, 524288);
        filterChain.doFilter((ServletRequest)wrapped, (ServletResponse)response);
    }
}

