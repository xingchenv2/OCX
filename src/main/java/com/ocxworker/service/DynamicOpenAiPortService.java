package com.ocxworker.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class DynamicOpenAiPortService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DynamicOpenAiPortService.class);
    public static final int MIN_PORT = 30000;
    public static final int MAX_PORT = 39999;
    private final Map<Integer, Connector> connectors = new ConcurrentHashMap<>();
    private volatile org.apache.catalina.Service tomcatService;

    @EventListener
    @Order(Integer.MIN_VALUE)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        if (event.getWebServer() instanceof TomcatWebServer tomcatWebServer) {
            this.tomcatService = tomcatWebServer.getTomcat().getService();
        }
    }

    public static boolean isManagedPort(int port) {
        return port >= 30000 && port <= 39999;
    }

    public synchronized void startPort(int port) {
        validateManagedPort(port);
        if (!this.connectors.containsKey(port)) {
            org.apache.catalina.Service svc = this.tomcatService;
            if (svc == null) {
                throw new IllegalStateException("Tomcat service not ready");
            } else {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setPort(port);

                try {
                    svc.addConnector(connector);
                    this.connectors.put(port, connector);
                    log.info("OpenAI multi-account connector started on port {}", port);
                } catch (Exception var7) {
                    try {
                        svc.removeConnector(connector);
                    } catch (Exception var6) {
                    }

                    throw new IllegalStateException("Failed to start port " + port + ": " + var7.getMessage(), var7);
                }
            }
        }
    }

    public synchronized void stopPort(int port) {
        Connector connector = this.connectors.remove(port);
        if (connector != null) {
            org.apache.catalina.Service svc = this.tomcatService;

            try {
                connector.stop();
            } catch (Exception var7) {
                log.warn("Failed to stop OpenAI connector {}: {}", port, var7.getMessage());
            }

            try {
                connector.destroy();
            } catch (Exception var6) {
                log.warn("Failed to destroy OpenAI connector {}: {}", port, var6.getMessage());
            }

            if (svc != null) {
                try {
                    svc.removeConnector(connector);
                } catch (Exception var5) {
                }
            }

            log.info("OpenAI multi-account connector stopped on port {}", port);
        }
    }

    public boolean isRunning(int port) {
        return this.connectors.containsKey(port);
    }

    public static void validateManagedPort(int port) {
        if (!isManagedPort(port)) {
            throw new IllegalArgumentException("端口必须在 30000-39999 之间");
        }
    }
}
