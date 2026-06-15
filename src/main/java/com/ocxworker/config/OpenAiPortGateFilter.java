package com.ocxworker.config;

import com.ocxworker.service.DynamicOpenAiPortService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Integer.MIN_VALUE)
public class OpenAiPortGateFilter extends OncePerRequestFilter {
    @Value("${server.port:8818}")
    private int serverPort;
    @Value("${ociworker.openaiApi.port:8080}")
    private int openaiApiPort;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path == null) {
            path = "";
        }

        String ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (ctx.length() > 0 && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        if (path == null || !path.startsWith("/v1")) {
            filterChain.doFilter(request, response);
        } else if (this.openaiApiPort > 0 && this.openaiApiPort != this.serverPort) {
            int localPort = request.getLocalPort();
            if (localPort != this.openaiApiPort && !DynamicOpenAiPortService.isManagedPort(localPort)) {
                response.setStatus(404);
                response.setContentType("application/json; charset=utf-8");
                String msg = "{\"error\":{\"message\":\"OpenAI 兼容 API 请使用 :" + this.openaiApiPort + " 端口（/v1），面板端口不暴露此能力\",\"type\":\"ociworker_error\"}}";
                response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
