package com.ocxworker.util.socks;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.Proxy.Type;

public final class Socks5Tunnel {
    private static final Object JDK_SOCKS_CONNECT_LOCK = new Object();

    private Socks5Tunnel() {
    }

    public static Socket connect(
        String proxyHost, int proxyPort, String proxyUser, String proxyPass, String targetHost, int targetPort, boolean remoteDns, int connectTimeoutMs
    ) throws IOException {
        final String u = normalizeSocksCredential(proxyUser);
        final String p = normalizeSocksCredential(proxyPass);
        boolean hasCreds = !u.isEmpty() || !p.isEmpty();
        Proxy proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        SocketAddress remote = remoteDns
            ? InetSocketAddress.createUnresolved(targetHost, targetPort)
            : new InetSocketAddress(InetAddress.getByName(targetHost), targetPort);
        synchronized (JDK_SOCKS_CONNECT_LOCK) {
            Authenticator old = Authenticator.getDefault();

            Socket e;
            try {
                if (hasCreds) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(u, p.toCharArray());
                        }
                    });
                } else {
                    Authenticator.setDefault(new Authenticator() {
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
                    e = s;
                } catch (RuntimeException | IOException var25) {
                    try {
                        s.close();
                    } catch (IOException var24) {
                    }

                    throw var25;
                }
            } finally {
                Authenticator.setDefault(old);
            }

            return e;
        }
    }

    public static String normalizeSocksCredential(String s) {
        if (s == null) {
            return "";
        } else {
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
}
