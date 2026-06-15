package com.ocxworker.webssh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Generated;

@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class WebSshConnectInfo {
    private String username;
    private String password;
    private String hostname;
    private int port = 22;
    @JsonProperty("logintype")
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

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public String getHostname() {
        return this.hostname;
    }

    @Generated
    public int getPort() {
        return this.port;
    }

    @Generated
    public int getLoginType() {
        return this.loginType;
    }

    @Generated
    public String getPrivateKey() {
        return this.privateKey;
    }

    @Generated
    public String getPassphrase() {
        return this.passphrase;
    }

    @Generated
    public String getProxyHost() {
        return this.proxyHost;
    }

    @Generated
    public int getProxyPort() {
        return this.proxyPort;
    }

    @Generated
    public String getProxyUser() {
        return this.proxyUser;
    }

    @Generated
    public String getProxyPass() {
        return this.proxyPass;
    }

    @Generated
    public void setUsername(final String username) {
        this.username = username;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    @Generated
    public void setPort(final int port) {
        this.port = port;
    }

    @JsonProperty("logintype")
    @Generated
    public void setLoginType(final int loginType) {
        this.loginType = loginType;
    }

    @Generated
    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }

    @Generated
    public void setPassphrase(final String passphrase) {
        this.passphrase = passphrase;
    }

    @Generated
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    @Generated
    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Generated
    public void setProxyUser(final String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Generated
    public void setProxyPass(final String proxyPass) {
        this.proxyPass = proxyPass;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WebSshConnectInfo other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.getPort() != other.getPort()) {
            return false;
        } else if (this.getLoginType() != other.getLoginType()) {
            return false;
        } else if (this.getProxyPort() != other.getProxyPort()) {
            return false;
        } else {
            Object this$username = this.getUsername();
            Object other$username = other.getUsername();
            if (this$username == null ? other$username == null : this$username.equals(other$username)) {
                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null ? other$password == null : this$password.equals(other$password)) {
                    Object this$hostname = this.getHostname();
                    Object other$hostname = other.getHostname();
                    if (this$hostname == null ? other$hostname == null : this$hostname.equals(other$hostname)) {
                        Object this$privateKey = this.getPrivateKey();
                        Object other$privateKey = other.getPrivateKey();
                        if (this$privateKey == null ? other$privateKey == null : this$privateKey.equals(other$privateKey)) {
                            Object this$passphrase = this.getPassphrase();
                            Object other$passphrase = other.getPassphrase();
                            if (this$passphrase == null ? other$passphrase == null : this$passphrase.equals(other$passphrase)) {
                                Object this$proxyHost = this.getProxyHost();
                                Object other$proxyHost = other.getProxyHost();
                                if (this$proxyHost == null ? other$proxyHost == null : this$proxyHost.equals(other$proxyHost)) {
                                    Object this$proxyUser = this.getProxyUser();
                                    Object other$proxyUser = other.getProxyUser();
                                    if (this$proxyUser == null ? other$proxyUser == null : this$proxyUser.equals(other$proxyUser)) {
                                        Object this$proxyPass = this.getProxyPass();
                                        Object other$proxyPass = other.getProxyPass();
                                        return this$proxyPass == null ? other$proxyPass == null : this$proxyPass.equals(other$proxyPass);
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof WebSshConnectInfo;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPort();
        result = result * 59 + this.getLoginType();
        result = result * 59 + this.getProxyPort();
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $hostname = this.getHostname();
        result = result * 59 + ($hostname == null ? 43 : $hostname.hashCode());
        Object $privateKey = this.getPrivateKey();
        result = result * 59 + ($privateKey == null ? 43 : $privateKey.hashCode());
        Object $passphrase = this.getPassphrase();
        result = result * 59 + ($passphrase == null ? 43 : $passphrase.hashCode());
        Object $proxyHost = this.getProxyHost();
        result = result * 59 + ($proxyHost == null ? 43 : $proxyHost.hashCode());
        Object $proxyUser = this.getProxyUser();
        result = result * 59 + ($proxyUser == null ? 43 : $proxyUser.hashCode());
        Object $proxyPass = this.getProxyPass();
        return result * 59 + ($proxyPass == null ? 43 : $proxyPass.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "WebSshConnectInfo(username="
            + this.getUsername()
            + ", password="
            + this.getPassword()
            + ", hostname="
            + this.getHostname()
            + ", port="
            + this.getPort()
            + ", loginType="
            + this.getLoginType()
            + ", privateKey="
            + this.getPrivateKey()
            + ", passphrase="
            + this.getPassphrase()
            + ", proxyHost="
            + this.getProxyHost()
            + ", proxyPort="
            + this.getProxyPort()
            + ", proxyUser="
            + this.getProxyUser()
            + ", proxyPass="
            + this.getProxyPass()
            + ")";
    }
}
