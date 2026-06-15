/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.dto.OciProxySnapshot
 *  com.ocxworker.util.socks.OciSocksApacheConnectionManager
 *  com.ocxworker.util.socks.OciSocksApacheConnectionManager$HttpOverSocksFactory
 *  com.ocxworker.util.socks.OciSocksApacheConnectionManager$HttpsOverSocksFactory
 *  com.ocxworker.util.socks.OciSocksApacheConnectionManager$SocksParams
 *  com.ocxworker.util.socks.Socks5Tunnel
 *  org.apache.http.config.Registry
 *  org.apache.http.config.RegistryBuilder
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 */
package com.ocxworker.util.socks;

import com.ocxworker.model.dto.OciProxySnapshot;
import com.ocxworker.util.socks.OciSocksApacheConnectionManager;
import com.ocxworker.util.socks.Socks5Tunnel;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public final class OciSocksApacheConnectionManager {
    private OciSocksApacheConnectionManager() {
    }

    public static PoolingHttpClientConnectionManager create(OciProxySnapshot snap) {
        if (!snap.usesSocksForOci()) {
            throw new IllegalArgumentException("not a SOCKS snapshot");
        }
        String proxyHost = snap.host();
        int proxyPort = snap.port();
        String user = Socks5Tunnel.normalizeSocksCredential((String)snap.proxyUser());
        String pass = Socks5Tunnel.normalizeSocksCredential((String)snap.proxyPass());
        boolean remoteDns = "socks5h".equals(snap.type());
        SocksParams p = new SocksParams(proxyHost, proxyPort, user, pass, remoteDns);
        Registry reg = RegistryBuilder.create().register("http", (Object)new HttpOverSocksFactory(p)).register("https", (Object)new HttpsOverSocksFactory(p)).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(50);
        cm.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(120000).setTcpNoDelay(true).build());
        return cm;
    }
}

