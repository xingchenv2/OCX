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
import java.util.Map;
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

@Component
public class WebSshConsoleTerminalWebSocketHandler implements WebSocketHandler {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshConsoleTerminalWebSocketHandler.class);
    private static final int DEFAULT_CONSOLE_COLS = 80;
    private static final int DEFAULT_CONSOLE_ROWS = 24;
    private final ExecutorService ioPool = Executors.newVirtualThreadPerTaskExecutor();
    @Resource
    private ConsoleService consoleService;
    @Value("${webssh.timeout-minutes:120}")
    private int timeoutMinutes;

    public void afterConnectionEstablished(WebSocketSession session) {
    }

    public void handleMessage(WebSocketSession ws, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage textMessage) {
            String payload = (String)textMessage.getPayload();
            if (ws.getAttributes().containsKey("started")) {
                this.handleConsoleInput(ws, payload);
            } else {
                this.startConsole(ws, payload.trim());
            }
        }
    }

    private void startConsole(WebSocketSession ws, String connectionId) {
        int cols = parseQueryInt(ws, "cols", 80);
        int rows = parseQueryInt(ws, "rows", 24);
        String closeTip = parseQuery(ws, "closeTip", "Connection timed out!");
        ws.getAttributes().put("started", Boolean.TRUE);
        Future<?> readerFuture = this.ioPool
            .submit(
                () -> {
                    PtyProcess process = null;

                    try {
                        Path script = this.consoleService.getOrCreateExecScript(connectionId);
                        Map<String, String> env = new HashMap<>(System.getenv());
                        env.put("TERM", "vt100");
                        process = new PtyProcessBuilder()
                            .setCommand(new String[]{"/bin/bash", script.toAbsolutePath().toString()})
                            .setEnvironment(env)
                            .setInitialColumns(cols)
                            .setInitialRows(rows)
                            .start();
                        ws.getAttributes().put("process", process);
                        ws.getAttributes().put("stdin", process.getOutputStream());
                        InputStream stdout = process.getInputStream();
                        byte[] buf = new byte[4096];
                        long deadline = System.nanoTime() + Duration.ofMinutes((long)this.timeoutMinutes).toNanos();

                        while (ws.isOpen() && process.isAlive()) {
                            if (System.nanoTime() > deadline) {
                                sendText(ws, "\u001b[33m" + closeTip + "\u001b[0m");
                                break;
                            }

                            int n = stdout.read(buf);
                            if (n > 0) {
                                sendConsoleOutput(ws, buf, n);
                            } else if (n < 0) {
                                break;
                            }
                        }
                    } catch (OciException var22) {
                        OciException e = var22;
                        log.debug("OCI console error: {}", var22.getMessage());

                        try {
                            sendText(ws, "\u001b[31m" + e.getMessage() + "\u001b[0m");
                        } catch (Exception var21) {
                        }
                    } catch (Exception var23) {
                        Exception e = var23;
                        log.debug("Console terminal error: {}", var23.getMessage());

                        try {
                            sendText(ws, "\u001b[31m" + e.getMessage() + "\u001b[0m");
                        } catch (Exception var20) {
                        }
                    } finally {
                        this.closeProcess(ws);
                    }
                }
            );
        ws.getAttributes().put("reader", readerFuture);
    }

    private void handleConsoleInput(WebSocketSession ws, String payload) throws Exception {
        if (!"ping".equals(payload)) {
            if (payload.startsWith("resize:")) {
                String[] parts = payload.split(":");
                if (parts.length >= 3) {
                    int rows = Integer.parseInt(parts[1]);
                    int cols = Integer.parseInt(parts[2]);
                    if (ws.getAttributes().get("process") instanceof PtyProcess process && process.isAlive()) {
                        process.setWinSize(new WinSize(cols, rows));
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

    private void closeProcess(WebSocketSession ws) {
        Object processObj = ws.getAttributes().remove("process");
        ws.getAttributes().remove("stdin");
        if (processObj instanceof PtyProcess process) {
            try {
                if (process.isAlive()) {
                    process.destroy();
                    process.waitFor(3L, TimeUnit.SECONDS);
                }
            } catch (Exception var5) {
                log.debug("Console process cleanup: {}", var5.getMessage());
            }
        }
    }

    private static void sendConsoleOutput(WebSocketSession ws, byte[] buf, int len) throws Exception {
        if (ws.isOpen()) {
            synchronized (ws) {
                ws.sendMessage(new TextMessage(new String(buf, 0, len, StandardCharsets.ISO_8859_1)));
            }
        }
    }

    private static void sendText(WebSocketSession ws, String text) throws Exception {
        if (ws.isOpen()) {
            synchronized (ws) {
                ws.sendMessage(new TextMessage(text));
            }
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("Console ws transport error: {}", exception.getMessage());
    }

    public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
        if (ws.getAttributes().remove("reader") instanceof Future<?> future) {
            future.cancel(true);
        }

        this.closeProcess(ws);
    }

    public boolean supportsPartialMessages() {
        return false;
    }
}
