/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.params.LoginParams
 *  jakarta.validation.constraints.NotBlank
 *  lombok.Generated
 */
package com.ociworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class LoginParams {
    @NotBlank(message="\u8d26\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8d26\u53f7\u4e0d\u80fd\u4e3a\u7a7a") String account;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String password;
    private String mfaCode;

    @Generated
    public LoginParams() {
    }

    @Generated
    public String getAccount() {
        return this.account;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public String getMfaCode() {
        return this.mfaCode;
    }

    @Generated
    public void setAccount(String account) {
        this.account = account;
    }

    @Generated
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated
    public void setMfaCode(String mfaCode) {
        this.mfaCode = mfaCode;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginParams)) {
            return false;
        }
        LoginParams other = (LoginParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$account = this.getAccount();
        String other$account = other.getAccount();
        if (this$account == null ? other$account != null : !this$account.equals(other$account)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$mfaCode = this.getMfaCode();
        String other$mfaCode = other.getMfaCode();
        return !(this$mfaCode == null ? other$mfaCode != null : !this$mfaCode.equals(other$mfaCode));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof LoginParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $account = this.getAccount();
        result = result * 59 + ($account == null ? 43 : $account.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $mfaCode = this.getMfaCode();
        result = result * 59 + ($mfaCode == null ? 43 : $mfaCode.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "LoginParams(account=" + this.getAccount() + ", password=" + this.getPassword() + ", mfaCode=" + this.getMfaCode() + ")";
    }
}

