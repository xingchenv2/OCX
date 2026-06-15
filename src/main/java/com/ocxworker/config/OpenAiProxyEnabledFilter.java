package com.ocxworker.config;

import com.ocxworker.service.OracleAiGatewayToggleService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(-2147483647)
public class OpenAiProxyEnabledFilter extends OncePerRequestFilter {
    @Resource
    private OracleAiGatewayToggleService toggleService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path == null) {
            path = "";
        }

        String ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (ctx.length() > 0 && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        if (path == null || !path.startsWith("/v1") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
        } else if (!this.toggleService.isEnabled()) {
            response.setStatus(503);
            response.setContentType("application/json; charset=utf-8");
            String msg = "{\"error\":{\"message\":\"OpenAI 兼容转发已临时关闭，请在 Oracle AI 页面开启后重试\",\"type\":\"ociworker_error\",\"code\":\"proxy_disabled\"}}";
            response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
