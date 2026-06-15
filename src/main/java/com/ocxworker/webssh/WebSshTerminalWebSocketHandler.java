package com.ocxworker.webssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSshTerminalWebSocketHandler implements WebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshTerminalWebSocketHandler.class);
    private final ExecutorService ioPool = Executors.newVirtualThreadPerTaskExecutor();
    @Value("${webssh.timeout-minutes:120}")
    private int timeoutMinutes;

    public void afterConnectionEstablished(WebSocketSession session) {
    }

    public void handleMessage(WebSocketSession ws, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage textMessage) {
            String payload = (String)textMessage.getPayload();
            if (ws.getAttributes().containsKey("started")) {
                this.handleTerminalInput(ws, payload);
            } else {
                this.startTerminal(ws, payload);
            }
        }
    }

    private void startTerminal(WebSocketSession ws, String sshInfoB64) {
        int cols = parseQueryInt(ws, "cols", 150);
        int rows = parseQueryInt(ws, "rows", 35);
        String closeTip = parseQuery(ws, "closeTip", "Connection timed out!");
        ws.getAttributes().put("started", Boolean.TRUE);
        Future<?> readerFuture = this.ioPool.submit(() -> {
            Session session = null;
            ChannelShell shell = null;

            try {
                WebSshConnectInfo info = WebSshConnectInfoParser.parse(sshInfoB64);
                session = WebSshJschSupport.openSession(info);
                shell = WebSshJschSupport.openShell(session, cols, rows);
                ws.getAttributes().put("shell", shell);
                ws.getAttributes().put("sshSession", session);
                ws.getAttributes().put("stdin", WebSshJschSupport.shellInput(shell));
                InputStream stdout = WebSshJschSupport.shellOutput(shell);
                byte[] buf = new byte[4096];
                long deadline = System.nanoTime() + Duration.ofMinutes((long)this.timeoutMinutes).toNanos();

                while (ws.isOpen() && shell.isConnected()) {
                    if (System.nanoTime() > deadline) {
                        sendText(ws, "\u001b[33m" + closeTip + "\u001b[0m");
                        break;
                    }

                    while (stdout.available() > 0) {
                        int n = stdout.read(buf);
                        if (n > 0) {
                            sendText(ws, new String(buf, 0, n, StandardCharsets.UTF_8));
                        }
                    }

                    Thread.sleep(50L);
                }
            } catch (Exception var19) {
                Exception e = var19;
                log.debug("SSH terminal error: {}", var19.getMessage());

                try {
                    sendText(ws, "\u001b[31m" + e.getMessage() + "\u001b[0m");
                } catch (Exception var18) {
                }
            } finally {
                this.closeSsh(ws);
            }
        });
        ws.getAttributes().put("reader", readerFuture);
    }

    private void handleTerminalInput(WebSocketSession ws, String payload) throws Exception {
        if (!"ping".equals(payload)) {
            if (payload.startsWith("resize:")) {
                String[] parts = payload.split(":");
                if (parts.length >= 3) {
                    int rows = Integer.parseInt(parts[1]);
                    int cols = Integer.parseInt(parts[2]);
                    if (ws.getAttributes().get("shell") instanceof ChannelShell shell) {
                        WebSshJschSupport.resizeShell(shell, cols, rows);
                    }
                }
            } else {
                if (ws.getAttributes().get("stdin") instanceof OutputStream stdin) {
                    stdin.write(payload.getBytes(StandardCharsets.UTF_8));
                    stdin.flush();
                }
            }
        }
    }

    private static int parseQueryInt(WebSocketSession ws, String key, int def) {
        String v = parseQuery(ws, key, null);
        if (v == null) {
            return def;
        } else {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException var5) {
                return def;
            }
        }
    }

    private static String parseQuery(WebSocketSession ws, String key, String def) {
        if (ws.getUri() != null && ws.getUri().getQuery() != null) {
            for (String part : ws.getUri().getQuery().split("&")) {
                int i = part.indexOf(61);
                if (i > 0 && key.equals(part.substring(0, i))) {
                    return part.substring(i + 1);
                }
            }

            return def;
        } else {
            return def;
        }
    }

    private void closeSsh(WebSocketSession ws) {
        Object shellObj = ws.getAttributes().remove("shell");
        Object sessionObj = ws.getAttributes().remove("sshSession");
        ws.getAttributes().remove("stdin");
        ChannelShell shell = shellObj instanceof ChannelShell s ? s : null;
        Session session = sessionObj instanceof Session sx ? sx : null;
        WebSshJschSupport.closeQuietly(session, shell);
    }

    private static void sendText(WebSocketSession ws, String text) throws Exception {
        if (ws.isOpen()) {
            synchronized (ws) {
                ws.sendMessage(new TextMessage(text));
            }
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("SSH ws transport error: {}", exception.getMessage());
    }

    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
        if (ws.getAttributes().remove("reader") instanceof Future<?> future) {
            future.cancel(true);
        }

        this.closeSsh(ws);
    }

    public boolean supportsPartialMessages() {
        return false;
    }
}
