/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.model.dto.OciProxySnapshot
 *  com.ociworker.util.socks.Socks5Tunnel
 *  com.oracle.bmc.http.client.ProxyConfiguration
 *  com.oracle.bmc.http.client.ProxyConfiguration$Builder
 */
package com.ociworker.model.dto;

import cn.hutool.core.util.StrUtil;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.util.socks.Socks5Tunnel;
import com.oracle.bmc.http.client.ProxyConfiguration;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/*
 * Exception performing whole class analysis ignored.
 */
public record OciProxySnapshot(boolean enabled, String type, String host, int port, String proxyUser, String proxyPass, String fullUrl) {
    private final boolean enabled;
    private final String type;
    private final String host;
    private final int port;
    private final String proxyUser;
    private final String proxyPass;
    private final String fullUrl;

    public OciProxySnapshot(boolean enabled, String type, String host, int port, String proxyUser, String proxyPass, String fullUrl) {
        this.enabled = enabled;
        this.type = type;
        this.host = host;
        this.port = port;
        this.proxyUser = proxyUser;
        this.proxyPass = proxyPass;
        this.fullUrl = fullUrl;
    }

    public static OciProxySnapshot disabled() {
        return new OciProxySnapshot(false, "http", "", 0, "", "", "");
    }

    public static OciProxySnapshot fromKv(Function<SysCfgEnum, String> getKv) {
        String en = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_ENABLED);
        boolean on = "true".equalsIgnoreCase(en) || "1".equals(en) || "yes".equalsIgnoreCase(en);
        String type = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_TYPE);
        if (StrUtil.isBlank((CharSequence)type)) {
            type = "http";
        }
        if (!("socks5".equals(type = type.trim().toLowerCase()) || "socks5h".equals(type) || "http".equals(type))) {
            type = "http";
        }
        String host = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_HOST);
        int port = 0;
        String ps = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_PORT);
        if (StrUtil.isNotBlank((CharSequence)ps)) {
            try {
                port = Integer.parseInt(ps.trim());
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        String user = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_USER);
        String pass = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_PASS);
        String full = OciProxySnapshot.s(getKv, (SysCfgEnum)SysCfgEnum.OCI_PROXY_FULL_URL);
        OciProxySnapshot base = new OciProxySnapshot(on, type, host, port, OciProxySnapshot.nvl((String)user), OciProxySnapshot.nvl((String)pass), OciProxySnapshot.nvl((String)full));
        return base.mergedWithFullUrl();
    }

    private static String s(Function<SysCfgEnum, String> getKv, SysCfgEnum e) {
        if (getKv == null) {
            return "";
        }
        return OciProxySnapshot.nvl((String)getKv.apply(e));
    }

    private static String nvl(String v) {
        return v == null ? "" : v;
    }

    private static String decodeUriUserInfoPart(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        try {
            return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e) {
            return s;
        }
    }

    public static OciProxySnapshot fromForm(boolean enabled, String type, String host, int port, String user, String pass, String fullUrl) {
        String t;
        String string = t = StrUtil.isBlank((CharSequence)type) ? "http" : type.trim().toLowerCase();
        if (!("socks5".equals(t) || "socks5h".equals(t) || "http".equals(t))) {
            t = "http";
        }
        OciProxySnapshot snap = new OciProxySnapshot(enabled, t, OciProxySnapshot.nvl((String)host), port, OciProxySnapshot.nvl((String)user), OciProxySnapshot.nvl((String)pass), OciProxySnapshot.nvl((String)fullUrl));
        return snap.mergedWithFullUrl();
    }

    private OciProxySnapshot mergedWithFullUrl() {
        if (StrUtil.isBlank((CharSequence)this.fullUrl)) {
            return this;
        }
        String u = this.fullUrl.trim();
        try {
            URI uri = URI.create(u);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return this;
            }
            String t = OciProxySnapshot.mapSchemeToType((String)scheme);
            String h = uri.getHost();
            if (h == null) {
                return this;
            }
            int p = uri.getPort();
            if (p < 0) {
                p = OciProxySnapshot.defaultPortForType((String)t, (String)scheme);
            }
            String userInfo = uri.getUserInfo();
            String nu = OciProxySnapshot.nvl((String)this.proxyUser);
            String np = OciProxySnapshot.nvl((String)this.proxyPass);
            if (StrUtil.isNotBlank((CharSequence)userInfo)) {
                if (userInfo.contains(":")) {
                    int idx = userInfo.indexOf(58);
                    nu = OciProxySnapshot.decodeUriUserInfoPart((String)userInfo.substring(0, idx));
                    np = OciProxySnapshot.decodeUriUserInfoPart((String)userInfo.substring(idx + 1));
                } else {
                    nu = OciProxySnapshot.decodeUriUserInfoPart((String)userInfo);
                }
            }
            return new OciProxySnapshot(this.enabled, t, h, p, nu, np, u);
        }
        catch (Exception e) {
            return this;
        }
    }

    private static int defaultPortForType(String t, String scheme) {
        if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) {
            return 8080;
        }
        if ("http".equals(t)) {
            return 8080;
        }
        return 1080;
    }

    private static String mapSchemeToType(String scheme) {
        String s = scheme.toLowerCase();
        if ("socks5h".equals(s)) {
            return "socks5h";
        }
        if ("socks5".equals(s) || "socks".equals(s)) {
            return "socks5";
        }
        return "http";
    }

    public boolean canConnect() {
        if (StrUtil.isNotBlank((CharSequence)this.fullUrl) && (this.host == null || this.host.isBlank() || this.port <= 0)) {
            try {
                URI u = URI.create(this.fullUrl.trim());
                if (u.getHost() == null) {
                    return false;
                }
                int p = u.getPort();
                if (p < 0) {
                    p = OciProxySnapshot.defaultPortForType((String)OciProxySnapshot.mapSchemeToType((String)OciProxySnapshot.nvl((String)u.getScheme())), (String)OciProxySnapshot.nvl((String)u.getScheme()));
                }
                return p > 0 && p <= 65535;
            }
            catch (Exception e) {
                return false;
            }
        }
        return StrUtil.isNotBlank((CharSequence)this.host) && this.port > 0 && this.port <= 65535;
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    public Proxy toJavaNetProxy() {
        InetSocketAddress addr = this.toInetSocketAddress();
        Proxy.Type pt = "http".equals(this.type) ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
        return new Proxy(pt, addr);
    }

    public boolean usesSocksForOci() {
        return this.enabled && this.canConnect() && ("socks5".equals(this.type) || "socks5h".equals(this.type));
    }

    public Optional<ProxyConfiguration> toOciProxyConfiguration() {
        if (!this.enabled || !this.canConnect()) {
            return Optional.empty();
        }
        if (this.usesSocksForOci()) {
            return Optional.empty();
        }
        ProxyConfiguration.Builder b = ProxyConfiguration.builder().proxy(this.toJavaNetProxy());
        String u = Socks5Tunnel.normalizeSocksCredential((String)this.proxyUser);
        String p = Socks5Tunnel.normalizeSocksCredential((String)this.proxyPass);
        if (!u.isEmpty() || !p.isEmpty()) {
            b.username(u).password(p.toCharArray());
        }
        return Optional.of(b.build());
    }

    public Map<String, String> toRawKvForPersistence() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("enabled", this.enabled ? "true" : "false");
        m.put("type", this.type);
        m.put("host", OciProxySnapshot.nvl((String)this.host));
        m.put("port", this.port > 0 ? String.valueOf(this.port) : "");
        m.put("user", OciProxySnapshot.nvl((String)this.proxyUser));
        m.put("pass", OciProxySnapshot.nvl((String)this.proxyPass));
        m.put("fullUrl", OciProxySnapshot.nvl((String)this.fullUrl));
        return m;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public String type() {
        return this.type;
    }

    public String host() {
        return this.host;
    }

    public int port() {
        return this.port;
    }

    public String proxyUser() {
        return this.proxyUser;
    }

    public String proxyPass() {
        return this.proxyPass;
    }

    public String fullUrl() {
        return this.fullUrl;
    }
}

