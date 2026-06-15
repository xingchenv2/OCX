package com.ocxworker.model.dto;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.util.socks.Socks5Tunnel;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.ProxyConfiguration.Builder;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URLDecoder;
import java.net.Proxy.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record OciProxySnapshot(boolean enabled, String type, String host, int port, String proxyUser, String proxyPass, String fullUrl) {
    public static OciProxySnapshot disabled() {
        return new OciProxySnapshot(false, "http", "", 0, "", "", "");
    }

    public static OciProxySnapshot fromKv(Function<SysCfgEnum, String> getKv) {
        String en = s(getKv, SysCfgEnum.OCI_PROXY_ENABLED);
        boolean on = "true".equalsIgnoreCase(en) || "1".equals(en) || "yes".equalsIgnoreCase(en);
        String type = s(getKv, SysCfgEnum.OCI_PROXY_TYPE);
        if (StrUtil.isBlank(type)) {
            type = "http";
        }

        type = type.trim().toLowerCase();
        if (!"socks5".equals(type) && !"socks5h".equals(type) && !"http".equals(type)) {
            type = "http";
        }

        String host = s(getKv, SysCfgEnum.OCI_PROXY_HOST);
        int port = 0;
        String ps = s(getKv, SysCfgEnum.OCI_PROXY_PORT);
        if (StrUtil.isNotBlank(ps)) {
            try {
                port = Integer.parseInt(ps.trim());
            } catch (NumberFormatException var11) {
            }
        }

        String user = s(getKv, SysCfgEnum.OCI_PROXY_USER);
        String pass = s(getKv, SysCfgEnum.OCI_PROXY_PASS);
        String full = s(getKv, SysCfgEnum.OCI_PROXY_FULL_URL);
        OciProxySnapshot base = new OciProxySnapshot(on, type, host, port, nvl(user), nvl(pass), nvl(full));
        return base.mergedWithFullUrl();
    }

    private static String s(Function<SysCfgEnum, String> getKv, SysCfgEnum e) {
        return getKv == null ? "" : nvl(getKv.apply(e));
    }

    private static String nvl(String v) {
        return v == null ? "" : v;
    }

    private static String decodeUriUserInfoPart(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException var2) {
                return s;
            }
        } else {
            return "";
        }
    }

    public static OciProxySnapshot fromForm(boolean enabled, String type, String host, int port, String user, String pass, String fullUrl) {
        String t = StrUtil.isBlank(type) ? "http" : type.trim().toLowerCase();
        if (!"socks5".equals(t) && !"socks5h".equals(t) && !"http".equals(t)) {
            t = "http";
        }

        OciProxySnapshot snap = new OciProxySnapshot(enabled, t, nvl(host), port, nvl(user), nvl(pass), nvl(fullUrl));
        return snap.mergedWithFullUrl();
    }

    private OciProxySnapshot mergedWithFullUrl() {
        if (StrUtil.isBlank(this.fullUrl)) {
            return this;
        } else {
            String u = this.fullUrl.trim();

            try {
                URI uri = URI.create(u);
                String scheme = uri.getScheme();
                if (scheme == null) {
                    return this;
                } else {
                    String t = mapSchemeToType(scheme);
                    String h = uri.getHost();
                    if (h == null) {
                        return this;
                    } else {
                        int p = uri.getPort();
                        if (p < 0) {
                            p = defaultPortForType(t, scheme);
                        }

                        String userInfo = uri.getUserInfo();
                        String nu = nvl(this.proxyUser);
                        String np = nvl(this.proxyPass);
                        if (StrUtil.isNotBlank(userInfo)) {
                            if (userInfo.contains(":")) {
                                int idx = userInfo.indexOf(58);
                                nu = decodeUriUserInfoPart(userInfo.substring(0, idx));
                                np = decodeUriUserInfoPart(userInfo.substring(idx + 1));
                            } else {
                                nu = decodeUriUserInfoPart(userInfo);
                            }
                        }

                        return new OciProxySnapshot(this.enabled, t, h, p, nu, np, u);
                    }
                }
            } catch (Exception var11) {
                return this;
            }
        }
    }

    private static int defaultPortForType(String t, String scheme) {
        if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) {
            return 8080;
        } else {
            return "http".equals(t) ? 8080 : 1080;
        }
    }

    private static String mapSchemeToType(String scheme) {
        String s = scheme.toLowerCase();
        if ("socks5h".equals(s)) {
            return "socks5h";
        } else {
            return !"socks5".equals(s) && !"socks".equals(s) ? "http" : "socks5";
        }
    }

    public boolean canConnect() {
        if (!StrUtil.isNotBlank(this.fullUrl) || this.host != null && !this.host.isBlank() && this.port > 0) {
            return StrUtil.isNotBlank(this.host) && this.port > 0 && this.port <= 65535;
        } else {
            try {
                URI u = URI.create(this.fullUrl.trim());
                if (u.getHost() == null) {
                    return false;
                } else {
                    int p = u.getPort();
                    if (p < 0) {
                        p = defaultPortForType(mapSchemeToType(nvl(u.getScheme())), nvl(u.getScheme()));
                    }

                    return p > 0 && p <= 65535;
                }
            } catch (Exception var3) {
                return false;
            }
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    public Proxy toJavaNetProxy() {
        InetSocketAddress addr = this.toInetSocketAddress();
        Type pt = "http".equals(this.type) ? Type.HTTP : Type.SOCKS;
        return new Proxy(pt, addr);
    }

    public boolean usesSocksForOci() {
        return this.enabled && this.canConnect() && ("socks5".equals(this.type) || "socks5h".equals(this.type));
    }

    public Optional<ProxyConfiguration> toOciProxyConfiguration() {
        if (!this.enabled || !this.canConnect()) {
            return Optional.empty();
        } else if (this.usesSocksForOci()) {
            return Optional.empty();
        } else {
            Builder b = ProxyConfiguration.builder().proxy(this.toJavaNetProxy());
            String u = Socks5Tunnel.normalizeSocksCredential(this.proxyUser);
            String p = Socks5Tunnel.normalizeSocksCredential(this.proxyPass);
            if (!u.isEmpty() || !p.isEmpty()) {
                b.username(u).password(p.toCharArray());
            }

            return Optional.of(b.build());
        }
    }

    public Map<String, String> toRawKvForPersistence() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("enabled", this.enabled ? "true" : "false");
        m.put("type", this.type);
        m.put("host", nvl(this.host));
        m.put("port", this.port > 0 ? String.valueOf(this.port) : "");
        m.put("user", nvl(this.proxyUser));
        m.put("pass", nvl(this.proxyPass));
        m.put("fullUrl", nvl(this.fullUrl));
        return m;
    }
}
