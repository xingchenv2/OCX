package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class LoginParams {
    @NotBlank(
        message = "账号不能为空"
    )
    private String account;
    @NotBlank(
        message = "密码不能为空"
    )
    private String password;
    private String mfaCode;

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
    public void setAccount(final String account) {
        this.account = account;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setMfaCode(final String mfaCode) {
        this.mfaCode = mfaCode;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LoginParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$account = this.getAccount();
            Object other$account = other.getAccount();
            if (this$account == null ? other$account == null : this$account.equals(other$account)) {
                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null ? other$password == null : this$password.equals(other$password)) {
                    Object this$mfaCode = this.getMfaCode();
                    Object other$mfaCode = other.getMfaCode();
                    return this$mfaCode == null ? other$mfaCode == null : this$mfaCode.equals(other$mfaCode);
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
        return other instanceof LoginParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $account = this.getAccount();
        result = result * 59 + ($account == null ? 43 : $account.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $mfaCode = this.getMfaCode();
        return result * 59 + ($mfaCode == null ? 43 : $mfaCode.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "LoginParams(account=" + this.getAccount() + ", password=" + this.getPassword() + ", mfaCode=" + this.getMfaCode() + ")";
    }
}
