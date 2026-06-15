package com.ocxworker.webssh;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSshUploadProgressWebSocketHandler implements WebSocketHandler {
    private final WebSshUploadRegistry uploadRegistry;

    public WebSshUploadProgressWebSocketHandler(WebSshUploadRegistry uploadRegistry) {
        this.uploadRegistry = uploadRegistry;
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = parseQuery(session, "id");
        if (id != null && !id.isBlank()) {
            boolean ready = false;

            while (session.isOpen()) {
                Integer total = this.uploadRegistry.peek(id);
                if (total != null) {
                    session.sendMessage(new TextMessage(String.valueOf(total)));
                    ready = true;
                }

                if (ready && this.uploadRegistry.peek(id) == null) {
                    break;
                }

                Thread.sleep(300L);
            }

            if (session.isOpen()) {
                session.close();
            }
        } else {
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
        if (ws.getUri() != null && ws.getUri().getQuery() != null) {
            for (String part : ws.getUri().getQuery().split("&")) {
                int i = part.indexOf(61);
                if (i > 0 && key.equals(part.substring(0, i))) {
                    return part.substring(i + 1);
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
