/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jsch.Channel
 *  com.jcraft.jsch.ChannelShell
 *  com.jcraft.jsch.Session
 *  com.ociworker.webssh.WebSshConnectInfo
 *  com.ociworker.webssh.WebSshConnectInfoParser
 *  com.ociworker.webssh.WebSshJschSupport
 *  com.ociworker.webssh.WebSshTerminalWebSocketHandler
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Component
 *  org.springframework.web.socket.CloseStatus
 *  org.springframework.web.socket.TextMessage
 *  org.springframework.web.socket.WebSocketHandler
 *  org.springframework.web.socket.WebSocketMessage
 *  org.springframework.web.socket.WebSocketSession
 */
package com.ociworker.webssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import com.ociworker.webssh.WebSshConnectInfo;
import com.ociworker.webssh.WebSshConnectInfoParser;
import com.ociworker.webssh.WebSshJschSupport;
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

/*
 * Exception performing whole class analysis ignored.
 */
@Component
public class WebSshTerminalWebSocketHandler
implements WebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshTerminalWebSocketHandler.class);
    private final ExecutorService ioPool = Executors.newVirtualThreadPerTaskExecutor();
    @Value(value="${webssh.timeout-minutes:120}")
    private int timeoutMinutes;

    public void afterConnectionEstablished(WebSocketSession session) {
    }

    public void handleMessage(WebSocketSession ws, WebSocketMessage<?> message) throws Exception {
        if (!(message instanceof TextMessage)) {
            return;
        }
        TextMessage textMessage = (TextMessage)message;
        String payload = (String)textMessage.getPayload();
        if (ws.getAttributes().containsKey("started")) {
            this.handleTerminalInput(ws, payload);
            return;
        }
        this.startTerminal(ws, payload);
    }

    private void startTerminal(WebSocketSession ws, String sshInfoB64) {
        int cols = WebSshTerminalWebSocketHandler.parseQueryInt((WebSocketSession)ws, (String)"cols", (int)150);
        int rows = WebSshTerminalWebSocketHandler.parseQueryInt((WebSocketSession)ws, (String)"rows", (int)35);
        String closeTip = WebSshTerminalWebSocketHandler.parseQuery((WebSocketSession)ws, (String)"closeTip", (String)"Connection timed out!");
        ws.getAttributes().put("started", Boolean.TRUE);
        Future<?> readerFuture = this.ioPool.submit(() -> {
            Session session = null;
            ChannelShell shell = null;
            try {
                WebSshConnectInfo info = WebSshConnectInfoParser.parse((String)sshInfoB64);
                session = WebSshJschSupport.openSession((WebSshConnectInfo)info);
                shell = WebSshJschSupport.openShell((Session)session, (int)cols, (int)rows);
                ws.getAttributes().put("shell", shell);
                ws.getAttributes().put("sshSession", session);
                ws.getAttributes().put("stdin", WebSshJschSupport.shellInput((ChannelShell)shell));
                InputStream stdout = WebSshJschSupport.shellOutput((ChannelShell)shell);
                byte[] buf = new byte[4096];
                long deadline = System.nanoTime() + Duration.ofMinutes(this.timeoutMinutes).toNanos();
                while (ws.isOpen() && shell.isConnected()) {
                    if (System.nanoTime() > deadline) {
                        WebSshTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)("\u001b[33m" + closeTip + "\u001b[0m"));
                        break;
                    }
                    while (stdout.available() > 0) {
                        int n = stdout.read(buf);
                        if (n <= 0) continue;
                        WebSshTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)new String(buf, 0, n, StandardCharsets.UTF_8));
                    }
                    Thread.sleep(50L);
                }
            }
            catch (Exception e) {
                log.debug("SSH terminal error: {}", (Object)e.getMessage());
                try {
                    WebSshTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)("\u001b[31m" + e.getMessage() + "\u001b[0m"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            finally {
                this.closeSsh(ws);
            }
        });
        ws.getAttributes().put("reader", readerFuture);
    }

    private void handleTerminalInput(WebSocketSession ws, String payload) throws Exception {
        if ("ping".equals(payload)) {
            return;
        }
        if (payload.startsWith("resize:")) {
            String[] parts = payload.split(":");
            if (parts.length >= 3) {
                int rows = Integer.parseInt(parts[1]);
                int cols = Integer.parseInt(parts[2]);
                Object shellObj = ws.getAttributes().get("shell");
                if (shellObj instanceof ChannelShell) {
                    ChannelShell shell = (ChannelShell)shellObj;
                    WebSshJschSupport.resizeShell((Channel)shell, (int)cols, (int)rows);
                }
            }
            return;
        }
        Object stdinObj = ws.getAttributes().get("stdin");
        if (stdinObj instanceof OutputStream) {
            OutputStream stdin = (OutputStream)stdinObj;
            stdin.write(payload.getBytes(StandardCharsets.UTF_8));
            stdin.flush();
        }
    }

    private static int parseQueryInt(WebSocketSession ws, String key, int def) {
        String v = WebSshTerminalWebSocketHandler.parseQuery((WebSocketSession)ws, (String)key, null);
        if (v == null) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        }
        catch (NumberFormatException e) {
            return def;
        }
    }

    private static String parseQuery(WebSocketSession ws, String key, String def) {
        if (ws.getUri() == null || ws.getUri().getQuery() == null) {
            return def;
        }
        for (String part : ws.getUri().getQuery().split("&")) {
            int i = part.indexOf(61);
            if (i <= 0 || !key.equals(part.substring(0, i))) continue;
            return part.substring(i + 1);
        }
        return def;
    }

    private void closeSsh(WebSocketSession ws) {
        Session s;
        ChannelShell s2;
        Object shellObj = ws.getAttributes().remove("shell");
        Object sessionObj = ws.getAttributes().remove("sshSession");
        ws.getAttributes().remove("stdin");
        ChannelShell shell = shellObj instanceof ChannelShell ? (s2 = (ChannelShell)shellObj) : null;
        Session session = sessionObj instanceof Session ? (s = (Session)sessionObj) : null;
        WebSshJschSupport.closeQuietly((Session)session, (Channel[])new Channel[]{shell});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void sendText(WebSocketSession ws, String text) throws Exception {
        if (ws.isOpen()) {
            WebSocketSession webSocketSession = ws;
            synchronized (webSocketSession) {
                ws.sendMessage((WebSocketMessage)new TextMessage((CharSequence)text));
            }
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("SSH ws transport error: {}", (Object)exception.getMessage());
    }

    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
        Object f = ws.getAttributes().remove("reader");
        if (f instanceof Future) {
            Future future = (Future)f;
            future.cancel(true);
        }
        this.closeSsh(ws);
    }

    public boolean supportsPartialMessages() {
        return false;
    }
}

