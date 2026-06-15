/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.config.OpenAiPortGateFilter
 *  com.ociworker.service.DynamicOpenAiPortService
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.ociworker.config;

import com.ociworker.service.DynamicOpenAiPortService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(value=-2147483648)
public class OpenAiPortGateFilter
extends OncePerRequestFilter {
    @Value(value="${server.port:8818}")
    private int serverPort;
    @Value(value="${ociworker.openaiApi.port:8080}")
    private int openaiApiPort;

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
        if (path == null || !path.startsWith("/v1")) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (this.openaiApiPort <= 0 || this.openaiApiPort == this.serverPort) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        int localPort = request.getLocalPort();
        if (localPort != this.openaiApiPort && !DynamicOpenAiPortService.isManagedPort((int)localPort)) {
            response.setStatus(404);
            response.setContentType("application/json; charset=utf-8");
            String msg = "{\"error\":{\"message\":\"OpenAI \u517c\u5bb9 API \u8bf7\u4f7f\u7528 :" + this.openaiApiPort + " \u7aef\u53e3\uff08/v1\uff09\uff0c\u9762\u677f\u7aef\u53e3\u4e0d\u66b4\u9732\u6b64\u80fd\u529b\",\"type\":\"ociworker_error\"}}";
            response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

