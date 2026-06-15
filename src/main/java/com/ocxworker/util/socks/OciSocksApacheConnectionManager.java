package com.ocxworker.util.socks;

import com.ocxworker.model.dto.OciProxySnapshot;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Collections;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

public final class OciSocksApacheConnectionManager {
    private OciSocksApacheConnectionManager() {
    }

    public static PoolingHttpClientConnectionManager create(OciProxySnapshot snap) {
        if (!snap.usesSocksForOci()) {
            throw new IllegalArgumentException("not a SOCKS snapshot");
        } else {
            String proxyHost = snap.host();
            int proxyPort = snap.port();
            String user = Socks5Tunnel.normalizeSocksCredential(snap.proxyUser());
            String pass = Socks5Tunnel.normalizeSocksCredential(snap.proxyPass());
            boolean remoteDns = "socks5h".equals(snap.type());
            OciSocksApacheConnectionManager.SocksParams p = new OciSocksApacheConnectionManager.SocksParams(proxyHost, proxyPort, user, pass, remoteDns);
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.create()
                .register("http", new OciSocksApacheConnectionManager.HttpOverSocksFactory(p))
                .register("https", new OciSocksApacheConnectionManager.HttpsOverSocksFactory(p))
                .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
            cm.setMaxTotal(100);
            cm.setDefaultMaxPerRoute(50);
            cm.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(120000).setTcpNoDelay(true).build());
            return cm;
        }
    }

    private static final class HttpOverSocksFactory implements ConnectionSocketFactory {
        private final OciSocksApacheConnectionManager.SocksParams p;

        HttpOverSocksFactory(OciSocksApacheConnectionManager.SocksParams p) {
            this.p = p;
        }

        public Socket createSocket(HttpContext context) {
            return new Socket();
        }

        public Socket connectSocket(
            int connectTimeout, Socket sock, HttpHost remoteHost, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context
        ) throws IOException {
            return Socks5Tunnel.connect(
                this.p.proxyHost, this.p.proxyPort, this.p.user, this.p.pass, remoteHost.getHostName(), remoteHost.getPort(), this.p.remoteDns, connectTimeout
            );
        }
    }

    private static final class HttpsOverSocksFactory implements ConnectionSocketFactory {
        private final OciSocksApacheConnectionManager.SocksParams p;

        HttpsOverSocksFactory(OciSocksApacheConnectionManager.SocksParams p) {
            this.p = p;
        }

        public Socket createSocket(HttpContext context) {
            return new Socket();
        }

        public Socket connectSocket(
            int connectTimeout, Socket sock, HttpHost remoteHost, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context
        ) throws IOException {
            Socket tunnel = Socks5Tunnel.connect(
                this.p.proxyHost, this.p.proxyPort, this.p.user, this.p.pass, remoteHost.getHostName(), remoteHost.getPort(), this.p.remoteDns, connectTimeout
            );
            tunnel.setSoTimeout(120000);

            SSLContext ctx;
            try {
                ctx = SSLContext.getDefault();
            } catch (GeneralSecurityException var14) {
                throw new IOException("TLS default SSLContext unavailable", var14);
            }

            SSLSocketFactory sf = ctx.getSocketFactory();
            SSLSocket ssl = (SSLSocket)sf.createSocket(tunnel, remoteHost.getHostName(), remoteHost.getPort(), true);
            SSLParameters params = ssl.getSSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");

            try {
                params.setServerNames(Collections.singletonList(new SNIHostName(remoteHost.getHostName())));
            } catch (IllegalArgumentException var13) {
            }

            ssl.setSSLParameters(params);
            ssl.setSoTimeout(120000);
            ssl.startHandshake();
            return ssl;
        }
    }

    private static record SocksParams(String proxyHost, int proxyPort, String user, String pass, boolean remoteDns) {
    }
}
