/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.webssh.WebSshUploadProgressWebSocketHandler
 *  com.ociworker.webssh.WebSshUploadRegistry
 *  org.springframework.stereotype.Component
 *  org.springframework.web.socket.CloseStatus
 *  org.springframework.web.socket.TextMessage
 *  org.springframework.web.socket.WebSocketHandler
 *  org.springframework.web.socket.WebSocketMessage
 *  org.springframework.web.socket.WebSocketSession
 */
package com.ociworker.webssh;

import com.ociworker.webssh.WebSshUploadRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
public class WebSshUploadProgressWebSocketHandler
implements WebSocketHandler {
    private final WebSshUploadRegistry uploadRegistry;

    public WebSshUploadProgressWebSocketHandler(WebSshUploadRegistry uploadRegistry) {
        this.uploadRegistry = uploadRegistry;
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = WebSshUploadProgressWebSocketHandler.parseQuery((WebSocketSession)session, (String)"id");
        if (id == null || id.isBlank()) {
            session.close();
            return;
        }
        boolean ready = false;
        while (session.isOpen()) {
            Integer total = this.uploadRegistry.peek(id);
            if (total != null) {
                session.sendMessage((WebSocketMessage)new TextMessage((CharSequence)String.valueOf(total)));
                ready = true;
            }
            if (ready && this.uploadRegistry.peek(id) == null) break;
            Thread.sleep(300L);
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) {
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    private static String parseQuery(WebSocketSession ws, String key) {
        if (ws.getUri() == null || ws.getUri().getQuery() == null) {
            return null;
        }
        for (String part : ws.getUri().getQuery().split("&")) {
            int i = part.indexOf(61);
            if (i <= 0 || !key.equals(part.substring(0, i))) continue;
            return part.substring(i + 1);
        }
        return null;
    }
}

