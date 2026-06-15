package com.ocxworker.config;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiApiPortConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private static final Logger log = LoggerFactory.getLogger(OpenAiApiPortConfig.class);
    @Value("${server.port:8818}")
    private int serverPort;
    @Value("${ociworker.openaiApi.port:8080}")
    private int openaiApiPort;

    public void customize(TomcatServletWebServerFactory factory) {
        if (this.openaiApiPort > 0) {
            if (this.openaiApiPort == this.serverPort) {
                log.info("ociworker.openaiApi.port equals server.port, skip additional Tomcat connector");
            } else {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setPort(this.openaiApiPort);
                factory.addAdditionalTomcatConnectors(new Connector[]{connector});
                log.info("OpenAI-compatible API listening on additional port {}", this.openaiApiPort);
            }
        }
    }
}
