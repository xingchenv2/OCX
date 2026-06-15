package com.ocxworker.config;

import com.ocxworker.websocket.LogWebSocketHandler;
import com.ocxworker.webssh.WebSshConsoleTerminalWebSocketHandler;
import com.ocxworker.webssh.WebSshTerminalWebSocketHandler;
import com.ocxworker.webssh.WebSshUploadProgressWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

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

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this.logWebSocketHandler, new String[]{"/ws/log"}).setAllowedOrigins(new String[]{"*"});
        registry.addHandler(this.webSshTerminalWebSocketHandler, new String[]{"/webssh-api/term"}).setAllowedOrigins(new String[]{"*"});
        registry.addHandler(this.webSshConsoleTerminalWebSocketHandler, new String[]{"/webssh-api/console-term"}).setAllowedOrigins(new String[]{"*"});
        registry.addHandler(this.webSshUploadProgressWebSocketHandler, new String[]{"/webssh-api/file/progress"}).setAllowedOrigins(new String[]{"*"});
    }
}
