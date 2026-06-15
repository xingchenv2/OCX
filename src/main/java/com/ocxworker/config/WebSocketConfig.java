package com.ocxworker.config;

import com.ocxworker.websocket.LogWebSocketHandler;
import com.ocxworker.webssh.WebSshConsoleTerminalWebSocketHandler;
import com.ocxworker.webssh.WebSshTerminalWebSocketHandler;
import com.ocxworker.webssh.WebSshUploadProgressWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ocxworker.controller.AuthController;
import com.ocxworker.util.CommonUtils;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * WebSocketConfig — Security-hardened version.
 *
 * Key fixes:
 *   1. Added auth HandshakeInterceptor — WebSocket connections now require a valid token.
 *   2. Removed AllowedOrigins("*") → now uses same-origin by default.
 *   3. WebSSH endpoints require authentication before establishing SSH sessions.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private LogWebSocketHandler logWebSocketHandler;
    @Resource
    private WebSshTerminalWebSocketHandler webSshTerminalWebSocketHandler;
    @Resource
    private WebSshConsoleTerminalWebSocketHandler webSshConsoleTerminalWebSocketHandler;
    @Resource
    private WebSshUploadProgressWebSocketHandler webSshUploadProgressWebSocketHandler;

    @Autowired
    private AuthController authController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // FIX: Add auth interceptor to ALL WebSocket endpoints
        HandshakeInterceptor authInterceptor = new WebSocketAuthHandshakeInterceptor(authController);

        registry.addHandler(logWebSocketHandler, "/ws/log")
            .addInterceptors(authInterceptor)
            .setAllowedOriginPatterns(new String[]{"localhost", "127.0.0.1", "oci.7700.eu.org"})
            .setAllowedOrigins(new String[]{});

        registry.addHandler(webSshTerminalWebSocketHandler, "/webssh-api/term")
            .addInterceptors(authInterceptor)
            .setAllowedOriginPatterns(new String[]{"localhost", "127.0.0.1", "oci.7700.eu.org"})
            .setAllowedOrigins(new String[]{});

        registry.addHandler(webSshConsoleTerminalWebSocketHandler, "/webssh-api/console-term")
            .addInterceptors(authInterceptor)
            .setAllowedOriginPatterns(new String[]{"localhost", "127.0.0.1", "oci.7700.eu.org"})
            .setAllowedOrigins(new String[]{});

        registry.addHandler(webSshUploadProgressWebSocketHandler, "/webssh-api/file/progress")
            .addInterceptors(authInterceptor)
            .setAllowedOriginPatterns(new String[]{"localhost", "127.0.0.1", "oci.7700.eu.org"})
            .setAllowedOrigins(new String[]{});
    }

    /**
     * HandshakeInterceptor that validates the auth token from query parameter
     * before allowing a WebSocket connection.
     * The browser JS should pass the token as a query parameter:
     *   new WebSocket("wss://host/ws/log?token=xxx")
     */
    static class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {
        private final AuthController authController;

        WebSocketAuthHandshakeInterceptor(AuthController authController) {
            this.authController = authController;
        }

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            String token = null;
            if (request instanceof ServletServerHttpRequest servletRequest) {
                token = servletRequest.getServletRequest().getParameter("token");
                // Also check Authorization header
                if (StrUtil.isBlank((CharSequence) token)) {
                    String auth = servletRequest.getServletRequest().getHeader("Authorization");
                    if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
                        token = auth.substring(7).trim();
                    }
                }
                // Also check cookie
                if (StrUtil.isBlank((CharSequence) token)) {
                    token = com.ocxworker.util.HttpRequestUtil.getCookie(
                        servletRequest.getServletRequest(), "token");
                }
            }

            if (StrUtil.isBlank((CharSequence) token)) {
                return false; // Reject — no auth
            }

            String account = authController.getEffectiveAccount();
            String pwdHash = authController.getEffectivePasswordHash();

            if (CommonUtils.validateToken(token, account, pwdHash)) {
                attributes.put("auth.token", token);
                attributes.put("auth.account", account);
                return true;
            }
            return false; // Reject — invalid token
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Exception exception) {
            // No-op
        }
    }
}
