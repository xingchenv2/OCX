package com.ocxworker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@Order(-2147483643)
public class LoginAuditBodyCaptureFilter extends OncePerRequestFilter {
    private static final int MAX_BODY_CACHE = 524288;

    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isPasswordOrTgLoginPost(request);
    }

    private static boolean isPasswordOrTgLoginPost(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        } else {
            String path = pathWithoutContext(request);
            return "/api/auth/login".equals(path) || "/api/auth/tgLogin".equals(path);
        }
    }

    private static String pathWithoutContext(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        return ctx != null && !ctx.isEmpty() && uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request, 524288);
        filterChain.doFilter(wrapped, response);
    }
}
