package com.ocxworker.websocket;

import com.ocxworker.service.LogPersistService;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.util.HttpRequestUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class LogWebSocketHandler extends TextWebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LogWebSocketHandler.class);
    private static final Map<String, ConcurrentWebSocketSessionDecorator> SESSIONS = new ConcurrentHashMap<>();
    private static volatile LogPersistService logPersistService;
    private static volatile LoginSecurityService loginSecurityService;

    public LogWebSocketHandler(LogPersistService persistService, LoginSecurityService loginSecurityService) {
        logPersistService = persistService;
        LogWebSocketHandler.loginSecurityService = loginSecurityService;
    }

    public void afterConnectionEstablished(WebSocketSession session) {
        LoginSecurityService sec = loginSecurityService;
        if (sec != null) {
            String ip = "";
            InetSocketAddress history = session.getRemoteAddress();
            if (history instanceof InetSocketAddress && history.getAddress() != null) {
                ip = history.getAddress().getHostAddress();
            }

            String cookieHeader = session.getHandshakeHeaders().getFirst("Cookie");
            String did = HttpRequestUtil.getCookieValueFromCookieHeader(cookieHeader, "ow_did");
            if (sec.isDeniedForLogin(ip, did)) {
                try {
                    session.close(CloseStatus.NOT_ACCEPTABLE);
                } catch (IOException var8) {
                }

                log.warn("Log WebSocket rejected (denylist): ip={} session={}", ip, session.getId());
                return;
            }
        }

        ConcurrentWebSocketSessionDecorator decorated = new ConcurrentWebSocketSessionDecorator(session, 2000, 65536);
        SESSIONS.put(session.getId(), decorated);
        log.info("Log WebSocket connected: {}", session.getId());

        try {
            LogPersistService persist = logPersistService;
            if (persist != null) {
                for (String line : persist.readLastLines(500)) {
                    decorated.sendMessage(new TextMessage(line));
                }
            }
        } catch (IOException var9) {
            log.warn("Failed to send history logs: {}", var9.getMessage());
        }
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SESSIONS.remove(session.getId());
    }

    public static void broadcast(String message) {
        LogPersistService persist = logPersistService;
        if (persist != null) {
            persist.appendLog(message);
        }

        TextMessage textMessage = new TextMessage(message);

        for (Entry<String, ConcurrentWebSocketSessionDecorator> entry : SESSIONS.entrySet()) {
            ConcurrentWebSocketSessionDecorator decorated = entry.getValue();
            if (decorated.isOpen()) {
                try {
                    decorated.sendMessage(textMessage);
                } catch (IOException var7) {
                    SESSIONS.remove(entry.getKey());
                }
            } else {
                SESSIONS.remove(entry.getKey());
            }
        }
    }
}
