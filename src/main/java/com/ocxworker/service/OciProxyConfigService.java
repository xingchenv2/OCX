package com.ocxworker.service;

import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.model.dto.OciProxySnapshot;
import com.ocxworker.util.socks.Socks5Tunnel;
import com.oracle.bmc.http.ClientConfigurator;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.jersey3.Jersey3ClientProperty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.Authenticator.RequestorType;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
            OciProxySnapshot s = OciProxySnapshot.fromKv(ex -> this.notificationService.getKvValue(ex));
            this.cache = s;
            if (!s.enabled() || !s.canConnect()) {
                clearInProcessHttpSocksProxySystemProperties();
            }
        } catch (Exception var2) {
            log.warn("OciProxy reload: {}", var2.getMessage());
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
            b.property(Jersey3ClientProperty.create("jersey.config.apache.client.useSystemProperties"), Boolean.FALSE);
            b.property(Jersey3ClientProperty.create("jersey.config.apache.client.credentialsProvider"), null);
            b.property(Jersey3ClientProperty.create("jersey.config.client.proxy.uri"), null);
            b.property(Jersey3ClientProperty.create("jersey.config.client.proxy.username"), null);
            b.property(Jersey3ClientProperty.create("jersey.config.client.proxy.password"), null);
        };
    }

    public HttpClient newOutboundHttpClient() {
        return this.newOutboundHttpClientBuilder().build();
    }

    public Builder newOutboundHttpClientBuilder() {
        Builder b = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L));
        if (this.cache.enabled() && this.cache.canConnect()) {
            b.proxy(singleProxy(this.cache.toJavaNetProxy()));
            b.authenticator(authenticatorFor(this.cache));
            return b;
        } else {
            return b.proxy(noProxySelector());
        }
    }

    public String testWithParams(OciProxySnapshot test) {
        if (!test.canConnect()) {
            throw new OciException("请填写有效的主机、端口，或「完整 URL」");
        } else {
            try {
                HttpClient client = newHttpClientForSnapshot(test);
                HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/zen"))
                    .header("User-Agent", "ocx-worker/1.0")
                    .timeout(Duration.ofSeconds(20L))
                    .GET()
                    .build();
                long t0 = System.currentTimeMillis();
                HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
                if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                    return "ok，" + (System.currentTimeMillis() - t0) + " ms";
                } else {
                    throw new OciException("HTTP 状态: " + resp.statusCode());
                }
            } catch (OciException var7) {
                throw var7;
            } catch (Exception var8) {
                throw new OciException("测试失败: " + var8.getMessage());
            }
        }
    }

    public static HttpClient newHttpClientForSnapshot(OciProxySnapshot t) {
        return newHttpClientBuilderForSnapshot(t).build();
    }

    public static Builder newHttpClientBuilderForSnapshot(OciProxySnapshot t) {
        Builder b = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L));
        if (t.enabled() && t.canConnect()) {
            b.proxy(singleProxy(t.toJavaNetProxy()));
            b.authenticator(authenticatorFor(t));
            return b;
        } else {
            return b.proxy(noProxySelector());
        }
    }

    private static Authenticator authenticatorFor(OciProxySnapshot s) {
        final String user = Socks5Tunnel.normalizeSocksCredential(s.proxyUser());
        final char[] pass = Socks5Tunnel.normalizeSocksCredential(s.proxyPass()).toCharArray();
        return user.isEmpty() && pass.length == 0 ? new Authenticator() {
        } : new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return this.getRequestorType() == RequestorType.PROXY ? new PasswordAuthentication(user, pass) : null;
            }
        };
    }

    public void persistAndReload(OciProxySnapshot s) {
        Map<String, String> m = s.toRawKvForPersistence();
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_ENABLED, m.get("enabled"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_TYPE, m.get("type"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_HOST, m.get("host"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_PORT, m.get("port"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_USER, m.get("user"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_PASS, m.get("pass"));
        this.notificationService.saveKvValue(SysCfgEnum.OCI_PROXY_FULL_URL, m.get("fullUrl"));
        this.reload();
    }

    private static ProxySelector noProxySelector() {
        return new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return List.of(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        };
    }

    private static ProxySelector singleProxy(Proxy proxy) {
        return new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return List.of(proxy);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        };
    }

    public static void clearInProcessHttpSocksProxySystemProperties() {
        for (String k : List.of(
            "http.proxyHost",
            "http.proxyPort",
            "https.proxyHost",
            "https.proxyPort",
            "ftp.proxyHost",
            "ftp.proxyPort",
            "socksProxyHost",
            "socksProxyPort",
            "http.nonProxyHosts",
            "socksNonProxyHosts"
        )) {
            try {
                System.clearProperty(k);
            } catch (Exception var4) {
            }
        }

        try {
            System.setProperty("java.net.useSystemProxies", "false");
        } catch (Exception var3) {
        }
    }
}
