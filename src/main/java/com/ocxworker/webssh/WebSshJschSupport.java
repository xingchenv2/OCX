/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jsch.Channel
 *  com.jcraft.jsch.ChannelExec
 *  com.jcraft.jsch.ChannelSftp
 *  com.jcraft.jsch.ChannelShell
 *  com.jcraft.jsch.JSch
 *  com.jcraft.jsch.JSchException
 *  com.jcraft.jsch.Proxy
 *  com.jcraft.jsch.ProxySOCKS5
 *  com.jcraft.jsch.Session
 *  com.ocxworker.webssh.WebSshConnectInfo
 *  com.ocxworker.webssh.WebSshJschSupport
 */
package com.ocxworker.webssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;
import com.ocxworker.webssh.WebSshConnectInfo;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

final class WebSshJschSupport {
    private WebSshJschSupport() {
    }

    static Session openSession(WebSshConnectInfo info) throws JSchException {
        JSch jsch = new JSch();
        if (info.getLoginType() != 0) {
            String key = info.getPrivateKey();
            if (key == null || key.isBlank()) {
                throw new JSchException("private key is empty");
            }
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            String pass = info.getPassphrase();
            if (pass != null && !pass.isBlank()) {
                jsch.addIdentity("key", keyBytes, null, pass.getBytes(StandardCharsets.UTF_8));
            } else {
                jsch.addIdentity("key", keyBytes, null, null);
            }
        }
        String host = info.getHostname();
        int port = info.getPort() > 0 ? info.getPort() : 22;
        Session session = jsch.getSession(info.getUsername(), host, port);
        if (info.getLoginType() == 0) {
            session.setPassword(info.getPassword() != null ? info.getPassword() : "");
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        String proxyHost = info.getProxyHost();
        if (proxyHost != null && !proxyHost.isBlank()) {
            int proxyPort = info.getProxyPort() > 0 ? info.getProxyPort() : 1080;
            ProxySOCKS5 proxy = new ProxySOCKS5(proxyHost, proxyPort);
            String pu = info.getProxyUser();
            if (pu != null && !pu.isBlank()) {
                proxy.setUserPasswd(pu, info.getProxyPass() != null ? info.getProxyPass() : "");
            }
            session.setProxy((Proxy)proxy);
        }
        session.connect(10000);
        return session;
    }

    static ChannelShell openShell(Session session, int cols, int rows) throws JSchException {
        ChannelShell shell = (ChannelShell)session.openChannel("shell");
        shell.setPtyType("xterm", cols, rows, 0, 0);
        shell.setPty(true);
        shell.connect();
        return shell;
    }

    static void resizeShell(Channel channel, int cols, int rows) throws JSchException {
        if (channel instanceof ChannelShell) {
            ChannelShell shell = (ChannelShell)channel;
            shell.setPtySize(cols, rows, 0, 0);
        }
    }

    static ChannelSftp openSftp(Session session) throws JSchException {
        ChannelSftp sftp = (ChannelSftp)session.openChannel("sftp");
        sftp.connect(15000);
        return sftp;
    }

    static String execCombined(Session session, String command) throws Exception {
        ChannelExec exec = (ChannelExec)session.openChannel("exec");
        exec.setCommand(command);
        exec.setInputStream(null);
        InputStream in = exec.getInputStream();
        InputStream err = exec.getErrStream();
        exec.connect(30000);
        byte[] out = in.readAllBytes();
        byte[] er = err.readAllBytes();
        exec.disconnect();
        if (out.length == 0 && er.length > 0) {
            return new String(er, StandardCharsets.UTF_8);
        }
        return new String(out, StandardCharsets.UTF_8);
    }

    static void closeQuietly(Session session, Channel ... channels) {
        if (channels != null) {
            for (Channel ch : channels) {
                if (ch == null || !ch.isConnected()) continue;
                try {
                    ch.disconnect();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        if (session != null && session.isConnected()) {
            try {
                session.disconnect();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    static OutputStream shellInput(ChannelShell shell) throws Exception {
        return shell.getOutputStream();
    }

    static InputStream shellOutput(ChannelShell shell) throws Exception {
        return shell.getInputStream();
    }
}

