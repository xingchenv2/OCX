/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.OpenAiProxyEnabledFilter
 *  com.ocxworker.service.OracleAiGatewayToggleService
 *  jakarta.annotation.Resource
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.ocxworker.config;

import com.ocxworker.service.OracleAiGatewayToggleService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(value=-2147483647)
public class OpenAiProxyEnabledFilter
extends OncePerRequestFilter {
    @Resource
    private OracleAiGatewayToggleService toggleService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ctx;
        String path = request.getRequestURI();
        if (path == null) {
            path = "";
        }
        String string = ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (ctx.length() > 0 && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        if (path == null || !path.startsWith("/v1") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (!this.toggleService.isEnabled()) {
            response.setStatus(503);
            response.setContentType("application/json; charset=utf-8");
            String msg = "{\"error\":{\"message\":\"OpenAI \u517c\u5bb9\u8f6c\u53d1\u5df2\u4e34\u65f6\u5173\u95ed\uff0c\u8bf7\u5728 Oracle AI \u9875\u9762\u5f00\u542f\u540e\u91cd\u8bd5\",\"type\":\"ocxworker_error\",\"code\":\"proxy_disabled\"}}";
            response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

