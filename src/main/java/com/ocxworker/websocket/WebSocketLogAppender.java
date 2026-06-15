/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.spi.ILoggingEvent
 *  ch.qos.logback.classic.spi.IThrowableProxy
 *  ch.qos.logback.core.AppenderBase
 *  com.ocxworker.websocket.LogWebSocketHandler
 *  com.ocxworker.websocket.WebSocketLogAppender
 */
package com.ocxworker.websocket;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.ocxworker.websocket.LogWebSocketHandler;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WebSocketLogAppender
extends AppenderBase<ILoggingEvent> {
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
            LogWebSocketHandler.broadcast((String)line.toString());
        }
        catch (Exception e) {
            System.err.println("[WebSocketLogAppender] broadcast failed: " + e.getMessage());
        }
    }
}

