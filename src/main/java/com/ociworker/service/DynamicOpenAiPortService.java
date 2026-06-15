/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.service.DynamicOpenAiPortService
 *  lombok.Generated
 *  org.apache.catalina.Service
 *  org.apache.catalina.connector.Connector
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.web.context.WebServerInitializedEvent
 *  org.springframework.boot.web.embedded.tomcat.TomcatWebServer
 *  org.springframework.boot.web.server.WebServer
 *  org.springframework.context.event.EventListener
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class DynamicOpenAiPortService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DynamicOpenAiPortService.class);
    public static final int MIN_PORT = 30000;
    public static final int MAX_PORT = 39999;
    private final Map<Integer, Connector> connectors = new ConcurrentHashMap();
    private volatile org.apache.catalina.Service tomcatService;

    @EventListener
    @Order(value=-2147483648)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        if (webServer instanceof TomcatWebServer) {
            TomcatWebServer tomcatWebServer = (TomcatWebServer)webServer;
            this.tomcatService = tomcatWebServer.getTomcat().getService();
        }
    }

    public static boolean isManagedPort(int port) {
        return port >= 30000 && port <= 39999;
    }

    public synchronized void startPort(int port) {
        DynamicOpenAiPortService.validateManagedPort((int)port);
        if (this.connectors.containsKey(port)) {
            return;
        }
        org.apache.catalina.Service svc = this.tomcatService;
        if (svc == null) {
            throw new IllegalStateException("Tomcat service not ready");
        }
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        try {
            svc.addConnector(connector);
            this.connectors.put(port, connector);
            log.info("OpenAI multi-account connector started on port {}", (Object)port);
        }
        catch (Exception e) {
            try {
                svc.removeConnector(connector);
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw new IllegalStateException("Failed to start port " + port + ": " + e.getMessage(), e);
        }
    }

    public synchronized void stopPort(int port) {
        Connector connector = (Connector)this.connectors.remove(port);
        if (connector == null) {
            return;
        }
        org.apache.catalina.Service svc = this.tomcatService;
        try {
            connector.stop();
        }
        catch (Exception e) {
            log.warn("Failed to stop OpenAI connector {}: {}", (Object)port, (Object)e.getMessage());
        }
        try {
            connector.destroy();
        }
        catch (Exception e) {
            log.warn("Failed to destroy OpenAI connector {}: {}", (Object)port, (Object)e.getMessage());
        }
        if (svc != null) {
            try {
                svc.removeConnector(connector);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        log.info("OpenAI multi-account connector stopped on port {}", (Object)port);
    }

    public boolean isRunning(int port) {
        return this.connectors.containsKey(port);
    }

    public static void validateManagedPort(int port) {
        if (!DynamicOpenAiPortService.isManagedPort((int)port)) {
            throw new IllegalArgumentException("\u7aef\u53e3\u5fc5\u987b\u5728 30000-39999 \u4e4b\u95f4");
        }
    }
}

