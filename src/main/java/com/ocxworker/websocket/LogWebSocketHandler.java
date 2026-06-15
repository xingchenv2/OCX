/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.service.LogPersistService
 *  com.ocxworker.service.LoginSecurityService
 *  com.ocxworker.util.HttpRequestUtil
 *  com.ocxworker.websocket.LogWebSocketHandler
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 *  org.springframework.web.socket.CloseStatus
 *  org.springframework.web.socket.TextMessage
 *  org.springframework.web.socket.WebSocketMessage
 *  org.springframework.web.socket.WebSocketSession
 *  org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
 *  org.springframework.web.socket.handler.TextWebSocketHandler
 */
package com.ocxworker.websocket;

import com.ocxworker.service.LogPersistService;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.util.HttpRequestUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class LogWebSocketHandler
extends TextWebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LogWebSocketHandler.class);
    private static final Map<String, ConcurrentWebSocketSessionDecorator> SESSIONS = new ConcurrentHashMap();
    private static volatile LogPersistService logPersistService;
    private static volatile LoginSecurityService loginSecurityService;

    public LogWebSocketHandler(LogPersistService persistService, LoginSecurityService loginSecurityService) {
        logPersistService = persistService;
        LogWebSocketHandler.loginSecurityService = loginSecurityService;
    }

    public void afterConnectionEstablished(WebSocketSession session) {
        LoginSecurityService sec = loginSecurityService;
        if (sec != null) {
            String cookieHeader;
            String did;
            InetSocketAddress isa;
            String ip = "";
            InetSocketAddress inetSocketAddress = session.getRemoteAddress();
            if (inetSocketAddress instanceof InetSocketAddress && (isa = inetSocketAddress).getAddress() != null) {
                ip = isa.getAddress().getHostAddress();
            }
            if (sec.isDeniedForLogin(ip, did = HttpRequestUtil.getCookieValueFromCookieHeader((String)(cookieHeader = session.getHandshakeHeaders().getFirst("Cookie")), (String)"ow_did"))) {
                try {
                    session.close(CloseStatus.NOT_ACCEPTABLE);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                log.warn("Log WebSocket rejected (denylist): ip={} session={}", (Object)ip, (Object)session.getId());
                return;
            }
        }
        ConcurrentWebSocketSessionDecorator decorated = new ConcurrentWebSocketSessionDecorator(session, 2000, 65536);
        SESSIONS.put(session.getId(), decorated);
        log.info("Log WebSocket connected: {}", (Object)session.getId());
        try {
            LogPersistService persist = logPersistService;
            if (persist != null) {
                List history = persist.readLastLines(500);
                for (String line : history) {
                    decorated.sendMessage((WebSocketMessage)new TextMessage((CharSequence)line));
                }
            }
        }
        catch (IOException e) {
            log.warn("Failed to send history logs: {}", (Object)e.getMessage());
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
        TextMessage textMessage = new TextMessage((CharSequence)message);
        for (Map.Entry entry : SESSIONS.entrySet()) {
            ConcurrentWebSocketSessionDecorator decorated = (ConcurrentWebSocketSessionDecorator)entry.getValue();
            if (decorated.isOpen()) {
                try {
                    decorated.sendMessage((WebSocketMessage)textMessage);
                }
                catch (IOException e) {
                    SESSIONS.remove(entry.getKey());
                }
                continue;
            }
            SESSIONS.remove(entry.getKey());
        }
    }
}

