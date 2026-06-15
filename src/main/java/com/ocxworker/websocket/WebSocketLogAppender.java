package com.ocxworker.websocket;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WebSocketLogAppender extends AppenderBase<ILoggingEvent> {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected void append(ILoggingEvent event) {
        try {
            String ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeStamp()), ZoneId.systemDefault()).format(FMT);
            StringBuilder line = new StringBuilder(ts).append(" ").append(event.getLevel()).append("  ").append(event.getFormattedMessage());
            IThrowableProxy tp = event.getThrowableProxy();
            if (tp != null) {
                line.append(" | ");
                line.append(tp.getClassName());
                if (tp.getMessage() != null && !tp.getMessage().isEmpty()) {
                    line.append(": ").append(tp.getMessage());
                }
            }

            LogWebSocketHandler.broadcast(line.toString());
        } catch (Exception var5) {
            System.err.println("[WebSocketLogAppender] broadcast failed: " + var5.getMessage());
        }
    }
}
