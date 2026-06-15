/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.exception.OciException
 *  com.ociworker.model.dto.OciProxySnapshot
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.util.socks.Socks5Tunnel
 *  com.oracle.bmc.http.ClientConfigurator
 *  com.oracle.bmc.http.client.ClientProperty
 *  com.oracle.bmc.http.client.ProxyConfiguration
 *  com.oracle.bmc.http.client.jersey3.Jersey3ClientProperty
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import com.ociworker.enums.SysCfgEnum;
import com.ociworker.exception.OciException;
import com.ociworker.model.dto.OciProxySnapshot;
import com.ociworker.service.NotificationService;
import com.ociworker.util.socks.Socks5Tunnel;
import com.oracle.bmc.http.ClientConfigurator;
import com.oracle.bmc.http.client.ClientProperty;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.jersey3.Jersey3ClientProperty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.net.Authenticator;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class OciProxyConfigService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OciProxyConfigService.class);
    public static final String TYPE_HTTP = "http";
    public static final String TYPE_SOCKS5 = "socks5";
    public static final String TYPE_SOCKS5H = "socks5h";
    private static OciProxyConfigService INSTANCE;
    @Resource
    private NotificationService notificationService;
    private volatile OciProxySnapshot cache = OciProxySnapshot.disabled();

    @PostConstruct
    public void postConstruct() {
        INSTANCE = this;
        this.reload();
    }

    public static OciProxyConfigService instance() {
        return INSTANCE;
    }

    public void reload() {
        try {
            OciProxySnapshot s;
            this.cache = s = OciProxySnapshot.fromKv(e -> this.notificationService.getKvValue(e));
            if (!s.enabled() || !s.canConnect()) {
                OciProxyConfigService.clearInProcessHttpSocksProxySystemProperties();
            }
        }
        catch (Exception e2) {
            log.warn("OciProxy reload: {}", (Object)e2.getMessage());
        }
    }

    public OciProxySnapshot snapshot() {
        return this.cache;
    }

    public Optional<ProxyConfiguration> getOciProxyConfiguration() {
        return this.cache.toOciProxyConfiguration();
    }

    public boolean ociUsesExplicitClientProxy() {
        return this.cache.usesSocksForOci() || this.getOciProxyConfiguration().isPresent();
    }

    public static ClientConfigurator ociSdkJerseyDirectConfigurator() {
        return b -> {
            b.property((ClientProperty)Jersey3ClientProperty.create((String)"jersey.config.apache.client.useSystemProperties"), (Object)Boolean.FALSE);
            b.property((ClientProperty)Jersey3ClientProperty.create((String)"jersey.config.apache.client.credentialsProvider"), null);
            b.property((ClientProperty)Jersey3ClientProperty.create((String)"jersey.config.client.proxy.uri"), null);
            b.property((ClientProperty)Jersey3ClientProperty.create((String)"jersey.config.client.proxy.username"), null);
            b.property((ClientProperty)Jersey3ClientProperty.create((String)"jersey.config.client.proxy.password"), null);
        };
    }

    public HttpClient newOutboundHttpClient() {
        return this.newOutboundHttpClientBuilder().build();
    }

    public HttpClient.Builder newOutboundHttpClientBuilder() {
        HttpClient.Builder b = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L));
        if (!this.cache.enabled() || !this.cache.canConnect()) {
            return b.proxy(OciProxyConfigService.noProxySelector());
        }
        b.proxy(OciProxyConfigService.singleProxy((Proxy)this.cache.toJavaNetProxy()));
        b.authenticator(OciProxyConfigService.authenticatorFor((OciProxySnapshot)this.cache));
        return b;
    }

    public String testWithParams(OciProxySnapshot test) {
        if (!test.canConnect()) {
            throw new OciException("\u8bf7\u586b\u5199\u6709\u6548\u7684\u4e3b\u673a\u3001\u7aef\u53e3\uff0c\u6216\u300c\u5b8c\u6574 URL\u300d");
        }
        try {
            HttpClient client = OciProxyConfigService.newHttpClientForSnapshot((OciProxySnapshot)test);
            HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/zen")).header("User-Agent", "oci-worker/1.0").timeout(Duration.ofSeconds(20L)).GET().build();
            long t0 = System.currentTimeMillis();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 400) {
                throw new OciException("HTTP \u72b6\u6001: " + resp.statusCode());
            }
            return "ok\uff0c" + (System.currentTimeMillis() - t0) + " ms";
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u6d4b\u8bd5\u5931\u8d25: " + e.getMessage());
        }
    }

    public static HttpClient newHttpClientForSnapshot(OciProxySnapshot t) {
        return OciProxyConfigService.newHttpClientBuilderForSnapshot((OciProxySnapshot)t).build();
    }

    public static HttpClient.Builder newHttpClientBuilderForSnapshot(OciProxySnapshot t) {
        HttpClient.Builder b = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L));
        if (!t.enabled() || !t.canConnect()) {
            return b.proxy(OciProxyConfigService.noProxySelector());
        }
        b.proxy(OciProxyConfigService.singleProxy((Proxy)t.toJavaNetProxy()));
        b.authenticator(OciProxyConfigService.authenticatorFor((OciProxySnapshot)t));
        return b;
    }

    private static Authenticator authenticatorFor(OciProxySnapshot s) {
        String user = Socks5Tunnel.normalizeSocksCredential((String)s.proxyUser());
        char[] pass = Socks5Tunnel.normalizeSocksCredential((String)s.proxyPass()).toCharArray();
        if (user.isEmpty() && pass.length == 0) {
            return new /* Unavailable Anonymous Inner Class!! */;
        }
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public void persistAndReload(OciProxySnapshot s) {
        Map m = s.toRawKvForPersistence();
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_ENABLED, (String)m.get("enabled"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_TYPE, (String)m.get("type"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_HOST, (String)m.get("host"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_PORT, (String)m.get("port"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_USER, (String)m.get("user"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_PASS, (String)m.get("pass"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_FULL_URL, (String)m.get("fullUrl"));
        this.reload();
    }

    private static ProxySelector noProxySelector() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private static ProxySelector singleProxy(Proxy proxy) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public static void clearInProcessHttpSocksProxySystemProperties() {
        for (String k : List.of("http.proxyHost", "http.proxyPort", "https.proxyHost", "https.proxyPort", "ftp.proxyHost", "ftp.proxyPort", "socksProxyHost", "socksProxyPort", "http.nonProxyHosts", "socksNonProxyHosts")) {
            try {
                System.clearProperty(k);
            }
            catch (Exception exception) {}
        }
        try {
            System.setProperty("java.net.useSystemProxies", "false");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

