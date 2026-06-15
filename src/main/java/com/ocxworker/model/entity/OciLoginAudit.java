/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ocxworker.model.entity.OciLoginAudit
 *  lombok.Generated
 */
package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_login_audit")
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
    public OciLoginAudit() {
    }

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
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setAccount(String account) {
        this.account = account;
    }

    @Generated
    public void setPasswordAttempt(String passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
    }

    @Generated
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Generated
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Generated
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Generated
    public void setOsName(String osName) {
        this.osName = osName;
    }

    @Generated
    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    @Generated
    public void setLoginChannel(String loginChannel) {
        this.loginChannel = loginChannel;
    }

    @Generated
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Generated
    public void setLoginDetail(String loginDetail) {
        this.loginDetail = loginDetail;
    }

    @Generated
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OciLoginAudit)) {
            return false;
        }
        OciLoginAudit other = (OciLoginAudit)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Boolean this$success = this.getSuccess();
        Boolean other$success = other.getSuccess();
        if (this$success == null ? other$success != null : !((Object)this$success).equals(other$success)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$account = this.getAccount();
        String other$account = other.getAccount();
        if (this$account == null ? other$account != null : !this$account.equals(other$account)) {
            return false;
        }
        String this$passwordAttempt = this.getPasswordAttempt();
        String other$passwordAttempt = other.getPasswordAttempt();
        if (this$passwordAttempt == null ? other$passwordAttempt != null : !this$passwordAttempt.equals(other$passwordAttempt)) {
            return false;
        }
        String this$ip = this.getIp();
        String other$ip = other.getIp();
        if (this$ip == null ? other$ip != null : !this$ip.equals(other$ip)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) {
            return false;
        }
        String this$osName = this.getOsName();
        String other$osName = other.getOsName();
        if (this$osName == null ? other$osName != null : !this$osName.equals(other$osName)) {
            return false;
        }
        String this$browserName = this.getBrowserName();
        String other$browserName = other.getBrowserName();
        if (this$browserName == null ? other$browserName != null : !this$browserName.equals(other$browserName)) {
            return false;
        }
        String this$loginChannel = this.getLoginChannel();
        String other$loginChannel = other.getLoginChannel();
        if (this$loginChannel == null ? other$loginChannel != null : !this$loginChannel.equals(other$loginChannel)) {
            return false;
        }
        String this$userAgent = this.getUserAgent();
        String other$userAgent = other.getUserAgent();
        if (this$userAgent == null ? other$userAgent != null : !this$userAgent.equals(other$userAgent)) {
            return false;
        }
        String this$loginDetail = this.getLoginDetail();
        String other$loginDetail = other.getLoginDetail();
        if (this$loginDetail == null ? other$loginDetail != null : !this$loginDetail.equals(other$loginDetail)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciLoginAudit;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $success = this.getSuccess();
        result = result * 59 + ($success == null ? 43 : ((Object)$success).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $account = this.getAccount();
        result = result * 59 + ($account == null ? 43 : $account.hashCode());
        String $passwordAttempt = this.getPasswordAttempt();
        result = result * 59 + ($passwordAttempt == null ? 43 : $passwordAttempt.hashCode());
        String $ip = this.getIp();
        result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        String $osName = this.getOsName();
        result = result * 59 + ($osName == null ? 43 : $osName.hashCode());
        String $browserName = this.getBrowserName();
        result = result * 59 + ($browserName == null ? 43 : $browserName.hashCode());
        String $loginChannel = this.getLoginChannel();
        result = result * 59 + ($loginChannel == null ? 43 : $loginChannel.hashCode());
        String $userAgent = this.getUserAgent();
        result = result * 59 + ($userAgent == null ? 43 : $userAgent.hashCode());
        String $loginDetail = this.getLoginDetail();
        result = result * 59 + ($loginDetail == null ? 43 : $loginDetail.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciLoginAudit(id=" + this.getId() + ", account=" + this.getAccount() + ", passwordAttempt=" + this.getPasswordAttempt() + ", ip=" + this.getIp() + ", success=" + this.getSuccess() + ", deviceId=" + this.getDeviceId() + ", osName=" + this.getOsName() + ", browserName=" + this.getBrowserName() + ", loginChannel=" + this.getLoginChannel() + ", userAgent=" + this.getUserAgent() + ", loginDetail=" + this.getLoginDetail() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

