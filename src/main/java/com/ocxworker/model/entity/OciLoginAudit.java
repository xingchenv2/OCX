package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_login_audit")
public class OciLoginAudit {
    @TableId
    private String id;
    private String account;
    private String passwordAttempt;
    private String ip;
    private Boolean success;
    private String deviceId;
    private String osName;
    private String browserName;
    private String loginChannel;
    private String userAgent;
    private String loginDetail;
    private LocalDateTime createTime;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getAccount() {
        return this.account;
    }

    @Generated
    public String getPasswordAttempt() {
        return this.passwordAttempt;
    }

    @Generated
    public String getIp() {
        return this.ip;
    }

    @Generated
    public Boolean getSuccess() {
        return this.success;
    }

    @Generated
    public String getDeviceId() {
        return this.deviceId;
    }

    @Generated
    public String getOsName() {
        return this.osName;
    }

    @Generated
    public String getBrowserName() {
        return this.browserName;
    }

    @Generated
    public String getLoginChannel() {
        return this.loginChannel;
    }

    @Generated
    public String getUserAgent() {
        return this.userAgent;
    }

    @Generated
    public String getLoginDetail() {
        return this.loginDetail;
    }

    @Generated
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Generated
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setAccount(final String account) {
        this.account = account;
    }

    @Generated
    public void setPasswordAttempt(final String passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
    }

    @Generated
    public void setIp(final String ip) {
        this.ip = ip;
    }

    @Generated
    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    @Generated
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    @Generated
    public void setOsName(final String osName) {
        this.osName = osName;
    }

    @Generated
    public void setBrowserName(final String browserName) {
        this.browserName = browserName;
    }

    @Generated
    public void setLoginChannel(final String loginChannel) {
        this.loginChannel = loginChannel;
    }

    @Generated
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    @Generated
    public void setLoginDetail(final String loginDetail) {
        this.loginDetail = loginDetail;
    }

    @Generated
    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OciLoginAudit other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$success = this.getSuccess();
            Object other$success = other.getSuccess();
            if (this$success == null ? other$success == null : this$success.equals(other$success)) {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                    Object this$account = this.getAccount();
                    Object other$account = other.getAccount();
                    if (this$account == null ? other$account == null : this$account.equals(other$account)) {
                        Object this$passwordAttempt = this.getPasswordAttempt();
                        Object other$passwordAttempt = other.getPasswordAttempt();
                        if (this$passwordAttempt == null ? other$passwordAttempt == null : this$passwordAttempt.equals(other$passwordAttempt)) {
                            Object this$ip = this.getIp();
                            Object other$ip = other.getIp();
                            if (this$ip == null ? other$ip == null : this$ip.equals(other$ip)) {
                                Object this$deviceId = this.getDeviceId();
                                Object other$deviceId = other.getDeviceId();
                                if (this$deviceId == null ? other$deviceId == null : this$deviceId.equals(other$deviceId)) {
                                    Object this$osName = this.getOsName();
                                    Object other$osName = other.getOsName();
                                    if (this$osName == null ? other$osName == null : this$osName.equals(other$osName)) {
                                        Object this$browserName = this.getBrowserName();
                                        Object other$browserName = other.getBrowserName();
                                        if (this$browserName == null ? other$browserName == null : this$browserName.equals(other$browserName)) {
                                            Object this$loginChannel = this.getLoginChannel();
                                            Object other$loginChannel = other.getLoginChannel();
                                            if (this$loginChannel == null ? other$loginChannel == null : this$loginChannel.equals(other$loginChannel)) {
                                                Object this$userAgent = this.getUserAgent();
                                                Object other$userAgent = other.getUserAgent();
                                                if (this$userAgent == null ? other$userAgent == null : this$userAgent.equals(other$userAgent)) {
                                                    Object this$loginDetail = this.getLoginDetail();
                                                    Object other$loginDetail = other.getLoginDetail();
                                                    if (this$loginDetail == null ? other$loginDetail == null : this$loginDetail.equals(other$loginDetail)) {
                                                        Object this$createTime = this.getCreateTime();
                                                        Object other$createTime = other.getCreateTime();
                                                        return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
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
        return other instanceof OciLoginAudit;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $success = this.getSuccess();
        result = result * 59 + ($success == null ? 43 : $success.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $account = this.getAccount();
        result = result * 59 + ($account == null ? 43 : $account.hashCode());
        Object $passwordAttempt = this.getPasswordAttempt();
        result = result * 59 + ($passwordAttempt == null ? 43 : $passwordAttempt.hashCode());
        Object $ip = this.getIp();
        result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
        Object $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        Object $osName = this.getOsName();
        result = result * 59 + ($osName == null ? 43 : $osName.hashCode());
        Object $browserName = this.getBrowserName();
        result = result * 59 + ($browserName == null ? 43 : $browserName.hashCode());
        Object $loginChannel = this.getLoginChannel();
        result = result * 59 + ($loginChannel == null ? 43 : $loginChannel.hashCode());
        Object $userAgent = this.getUserAgent();
        result = result * 59 + ($userAgent == null ? 43 : $userAgent.hashCode());
        Object $loginDetail = this.getLoginDetail();
        result = result * 59 + ($loginDetail == null ? 43 : $loginDetail.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciLoginAudit(id="
            + this.getId()
            + ", account="
            + this.getAccount()
            + ", passwordAttempt="
            + this.getPasswordAttempt()
            + ", ip="
            + this.getIp()
            + ", success="
            + this.getSuccess()
            + ", deviceId="
            + this.getDeviceId()
            + ", osName="
            + this.getOsName()
            + ", browserName="
            + this.getBrowserName()
            + ", loginChannel="
            + this.getLoginChannel()
            + ", userAgent="
            + this.getUserAgent()
            + ", loginDetail="
            + this.getLoginDetail()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
