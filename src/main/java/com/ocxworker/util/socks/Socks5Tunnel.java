/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.util.socks.Socks5Tunnel
 */
package com.ocxworker.util.socks;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Socks5Tunnel {
    private static final Object JDK_SOCKS_CONNECT_LOCK = new Object();

    private Socks5Tunnel() {
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static Socket connect(String proxyHost, int proxyPort, String proxyUser, String proxyPass, String targetHost, int targetPort, boolean remoteDns, int connectTimeoutMs) throws IOException {
        String u = Socks5Tunnel.normalizeSocksCredential((String)proxyUser);
        String p = Socks5Tunnel.normalizeSocksCredential((String)proxyPass);
        boolean hasCreds = !u.isEmpty() || !p.isEmpty();
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        InetSocketAddress remote = remoteDns ? InetSocketAddress.createUnresolved(targetHost, targetPort) : new InetSocketAddress(InetAddress.getByName(targetHost), targetPort);
        Object object = JDK_SOCKS_CONNECT_LOCK;
        synchronized (object) {
            Authenticator old = Authenticator.getDefault();
            try {
                Socket socket;
                if (hasCreds) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected java.net.PasswordAuthentication requestPasswordAuthenticationInstance(String host, int port, String protocol, String prompt, String scheme) {
                            return new java.net.PasswordAuthentication(u, p.toCharArray());
                        }
                    });
                } else {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected java.net.PasswordAuthentication requestPasswordAuthenticationInstance(String host, int port, String protocol, String prompt, String scheme) {
                            return null;
                        }
                    });
                }
                Socket s = new Socket(proxy);
                try {
                    s.setTcpNoDelay(true);
                    if (connectTimeoutMs > 0) {
                        s.connect(remote, connectTimeoutMs);
                    } else {
                        s.connect(remote);
                    }
                    s.setSoTimeout(connectTimeoutMs > 0 ? Math.max(connectTimeoutMs, 30000) : 30000);
                    socket = s;
                }
                catch (IOException | RuntimeException e) {
                    try {
                        s.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    throw e;
                }
                return socket;
            }
            finally {
                Authenticator.setDefault(old);
            }
        }
    }

    public static String normalizeSocksCredential(String s) {
        if (s == null) {
            return "";
        }
        String t = s.strip();
        if (t.startsWith("\ufeff")) {
            t = t.substring(1).strip();
        }
        while (!t.isEmpty() && (t.endsWith("\r") || t.endsWith("\n"))) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }
}

