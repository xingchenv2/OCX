package com.ociworker.webssh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Generated;

/**
 * WebSshConnectInfo — Security-hardened version.
 *
 * FIX: toString() no longer leaks passwords, private keys, or passphrases.
 * Original code printed ALL sensitive fields to logs — any logging framework
 * that called toString() would expose SSH credentials in plaintext.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSshConnectInfo {
    private String username;
    private String password;
    private String hostname;
    private int port = 22;
    @JsonProperty(value = "logintype")
    private int loginType;
    private String privateKey;
    private String passphrase;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPass;

    void normalizeHostname() {
        if (this.hostname != null && this.hostname.contains(":") && !this.hostname.startsWith("[")) {
            this.hostname = "[" + this.hostname + "]";
        }
        if (this.port <= 0) {
            this.port = 22;
        }
    }

    // ---- FIX: Safe toString() that redacts all sensitive fields ----
    @Override
    public String toString() {
        return "WebSshConnectInfo(" +
            "username=" + username +
            ", password=****" +
            ", hostname=" + hostname +
            ", port=" + port +
            ", loginType=" + loginType +
            ", privateKey=****" +
            ", passphrase=****" +
            ", proxyHost=" + proxyHost +
            ", proxyPort=" + proxyPort +
            ", proxyUser=" + proxyUser +
            ", proxyPass=****" +
            ")";
    }

    // ---- Getters and Setters (unchanged) ----

    @Generated
    public WebSshConnectInfo() {}

    @Generated
    public String getUsername() { return this.username; }

    @Generated
    public void setUsername(String username) { this.username = username; }

    @Generated
    public String getPassword() { return this.password; }

    @Generated
    public void setPassword(String password) { this.password = password; }

    @Generated
    public String getHostname() { return this.hostname; }

    @Generated
    public void setHostname(String hostname) { this.hostname = hostname; }

    @Generated
    public int getPort() { return this.port; }

    @Generated
    public void setPort(int port) { this.port = port; }

    @Generated
    public int getLoginType() { return this.loginType; }

    @JsonProperty(value = "logintype")
    @Generated
    public void setLoginType(int loginType) { this.loginType = loginType; }

    @Generated
    public String getPrivateKey() { return this.privateKey; }

    @Generated
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    @Generated
    public String getPassphrase() { return this.passphrase; }

    @Generated
    public void setPassphrase(String passphrase) { this.passphrase = passphrase; }

    @Generated
    public String getProxyHost() { return this.proxyHost; }

    @Generated
    public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }

    @Generated
    public int getProxyPort() { return this.proxyPort; }

    @Generated
    public void setProxyPort(int proxyPort) { this.proxyPort = proxyPort; }

    @Generated
    public String getProxyUser() { return this.proxyUser; }

    @Generated
    public void setProxyUser(String proxyUser) { this.proxyUser = proxyUser; }

    @Generated
    public String getProxyPass() { return this.proxyPass; }

    @Generated
    public void setProxyPass(String proxyPass) { this.proxyPass = proxyPass; }
}
