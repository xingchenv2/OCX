/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.service.ConsoleService
 *  com.ocxworker.webssh.WebSshConsoleTerminalWebSocketHandler
 *  com.pty4j.PtyProcess
 *  com.pty4j.PtyProcessBuilder
 *  com.pty4j.WinSize
 *  jakarta.annotation.Resource
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
package com.ocxworker.webssh;

import com.ocxworker.exception.OciException;
import com.ocxworker.service.ConsoleService;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
public class WebSshConsoleTerminalWebSocketHandler
implements WebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshConsoleTerminalWebSocketHandler.class);
    private static final int DEFAULT_CONSOLE_COLS = 80;
    private static final int DEFAULT_CONSOLE_ROWS = 24;
    private final ExecutorService ioPool = Executors.newVirtualThreadPerTaskExecutor();
    @Resource
    private ConsoleService consoleService;
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
            this.handleConsoleInput(ws, payload);
            return;
        }
        this.startConsole(ws, payload.trim());
    }

    private void startConsole(WebSocketSession ws, String connectionId) {
        int cols = WebSshConsoleTerminalWebSocketHandler.parseQueryInt((WebSocketSession)ws, (String)"cols", (int)80);
        int rows = WebSshConsoleTerminalWebSocketHandler.parseQueryInt((WebSocketSession)ws, (String)"rows", (int)24);
        String closeTip = WebSshConsoleTerminalWebSocketHandler.parseQuery((WebSocketSession)ws, (String)"closeTip", (String)"Connection timed out!");
        ws.getAttributes().put("started", Boolean.TRUE);
        Future<?> readerFuture = this.ioPool.submit(() -> {
            PtyProcess process = null;
            try {
                Path script = this.consoleService.getOrCreateExecScript(connectionId);
                HashMap<String, String> env = new HashMap<String, String>(System.getenv());
                env.put("TERM", "vt100");
                process = new PtyProcessBuilder().setCommand(new String[]{"/bin/bash", script.toAbsolutePath().toString()}).setEnvironment(env).setInitialColumns(Integer.valueOf(cols)).setInitialRows(Integer.valueOf(rows)).start();
                ws.getAttributes().put("process", process);
                ws.getAttributes().put("stdin", process.getOutputStream());
                InputStream stdout = process.getInputStream();
                byte[] buf = new byte[4096];
                long deadline = System.nanoTime() + Duration.ofMinutes(this.timeoutMinutes).toNanos();
                while (ws.isOpen() && process.isAlive()) {
                    if (System.nanoTime() > deadline) {
                        WebSshConsoleTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)("\u001b[33m" + closeTip + "\u001b[0m"));
                    } else {
                        int n = stdout.read(buf);
                        if (n > 0) {
                            WebSshConsoleTerminalWebSocketHandler.sendConsoleOutput((WebSocketSession)ws, (byte[])buf, (int)n);
                            continue;
                        }
                        if (n >= 0) continue;
                    }
                    break;
                }
            }
            catch (OciException e) {
                log.debug("OCI console error: {}", (Object)e.getMessage());
                try {
                    WebSshConsoleTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)("\u001b[31m" + e.getMessage() + "\u001b[0m"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            catch (Exception e) {
                log.debug("Console terminal error: {}", (Object)e.getMessage());
                try {
                    WebSshConsoleTerminalWebSocketHandler.sendText((WebSocketSession)ws, (String)("\u001b[31m" + e.getMessage() + "\u001b[0m"));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            finally {
                this.closeProcess(ws);
            }
        });
        ws.getAttributes().put("reader", readerFuture);
    }

    private void handleConsoleInput(WebSocketSession ws, String payload) throws Exception {
        if ("ping".equals(payload)) {
            return;
        }
        if (payload.startsWith("resize:")) {
            String[] parts = payload.split(":");
            if (parts.length >= 3) {
                PtyProcess process;
                int rows = Integer.parseInt(parts[1]);
                int cols = Integer.parseInt(parts[2]);
                Object processObj = ws.getAttributes().get("process");
                if (processObj instanceof PtyProcess && (process = (PtyProcess)processObj).isAlive()) {
                    process.setWinSize(new WinSize(cols, rows));
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
        String v = WebSshConsoleTerminalWebSocketHandler.parseQuery((WebSocketSession)ws, (String)key, null);
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

    private void closeProcess(WebSocketSession ws) {
        Object processObj = ws.getAttributes().remove("process");
        ws.getAttributes().remove("stdin");
        if (processObj instanceof PtyProcess) {
            PtyProcess process = (PtyProcess)processObj;
            try {
                if (process.isAlive()) {
                    process.destroy();
                    process.waitFor(3L, TimeUnit.SECONDS);
                }
            }
            catch (Exception e) {
                log.debug("Console process cleanup: {}", (Object)e.getMessage());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void sendConsoleOutput(WebSocketSession ws, byte[] buf, int len) throws Exception {
        if (ws.isOpen()) {
            WebSocketSession webSocketSession = ws;
            synchronized (webSocketSession) {
                ws.sendMessage((WebSocketMessage)new TextMessage((CharSequence)new String(buf, 0, len, StandardCharsets.ISO_8859_1)));
            }
        }
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
        log.debug("Console ws transport error: {}", (Object)exception.getMessage());
    }

    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
        Object f = ws.getAttributes().remove("reader");
        if (f instanceof Future) {
            Future future = (Future)f;
            future.cancel(true);
        }
        this.closeProcess(ws);
    }

    public boolean supportsPartialMessages() {
        return false;
    }
}

