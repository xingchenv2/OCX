/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.params.UserParams
 *  lombok.Generated
 */
package com.ocxworker.model.params;

import java.util.List;
import java.util.Map;
import lombok.Generated;

public class UserParams {
    private String tenantId;
    private String userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean addToAdminGroup;
    private String domainId;
    private Integer bypassCodeCount;
    private Integer bypassCodeExpiryDays;
    private Map<String, Boolean> capabilities;
    private List<String> groupIds;

    @Generated
    public UserParams() {
    }

    @Generated
    public String getTenantId() {
        return this.tenantId;
    }

    @Generated
    public String getUserId() {
        return this.userId;
    }

    @Generated
    public String getUserName() {
        return this.userName;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getFirstName() {
        return this.firstName;
    }

    @Generated
    public String getLastName() {
        return this.lastName;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public Boolean getAddToAdminGroup() {
        return this.addToAdminGroup;
    }

    @Generated
    public String getDomainId() {
        return this.domainId;
    }

    @Generated
    public Integer getBypassCodeCount() {
        return this.bypassCodeCount;
    }

    @Generated
    public Integer getBypassCodeExpiryDays() {
        return this.bypassCodeExpiryDays;
    }

    @Generated
    public Map<String, Boolean> getCapabilities() {
        return this.capabilities;
    }

    @Generated
    public List<String> getGroupIds() {
        return this.groupIds;
    }

    @Generated
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Generated
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Generated
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Generated
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated
    public void setAddToAdminGroup(Boolean addToAdminGroup) {
        this.addToAdminGroup = addToAdminGroup;
    }

    @Generated
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    @Generated
    public void setBypassCodeCount(Integer bypassCodeCount) {
        this.bypassCodeCount = bypassCodeCount;
    }

    @Generated
    public void setBypassCodeExpiryDays(Integer bypassCodeExpiryDays) {
        this.bypassCodeExpiryDays = bypassCodeExpiryDays;
    }

    @Generated
    public void setCapabilities(Map<String, Boolean> capabilities) {
        this.capabilities = capabilities;
    }

    @Generated
    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserParams)) {
            return false;
        }
        UserParams other = (UserParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Boolean this$addToAdminGroup = this.getAddToAdminGroup();
        Boolean other$addToAdminGroup = other.getAddToAdminGroup();
        if (this$addToAdminGroup == null ? other$addToAdminGroup != null : !((Object)this$addToAdminGroup).equals(other$addToAdminGroup)) {
            return false;
        }
        Integer this$bypassCodeCount = this.getBypassCodeCount();
        Integer other$bypassCodeCount = other.getBypassCodeCount();
        if (this$bypassCodeCount == null ? other$bypassCodeCount != null : !((Object)this$bypassCodeCount).equals(other$bypassCodeCount)) {
            return false;
        }
        Integer this$bypassCodeExpiryDays = this.getBypassCodeExpiryDays();
        Integer other$bypassCodeExpiryDays = other.getBypassCodeExpiryDays();
        if (this$bypassCodeExpiryDays == null ? other$bypassCodeExpiryDays != null : !((Object)this$bypassCodeExpiryDays).equals(other$bypassCodeExpiryDays)) {
            return false;
        }
        String this$tenantId = this.getTenantId();
        String other$tenantId = other.getTenantId();
        if (this$tenantId == null ? other$tenantId != null : !this$tenantId.equals(other$tenantId)) {
            return false;
        }
        String this$userId = this.getUserId();
        String other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) {
            return false;
        }
        String this$userName = this.getUserName();
        String other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$firstName = this.getFirstName();
        String other$firstName = other.getFirstName();
        if (this$firstName == null ? other$firstName != null : !this$firstName.equals(other$firstName)) {
            return false;
        }
        String this$lastName = this.getLastName();
        String other$lastName = other.getLastName();
        if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$domainId = this.getDomainId();
        String other$domainId = other.getDomainId();
        if (this$domainId == null ? other$domainId != null : !this$domainId.equals(other$domainId)) {
            return false;
        }
        Map this$capabilities = this.getCapabilities();
        Map other$capabilities = other.getCapabilities();
        if (this$capabilities == null ? other$capabilities != null : !((Object)this$capabilities).equals(other$capabilities)) {
            return false;
        }
        List this$groupIds = this.getGroupIds();
        List other$groupIds = other.getGroupIds();
        return !(this$groupIds == null ? other$groupIds != null : !((Object)this$groupIds).equals(other$groupIds));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $addToAdminGroup = this.getAddToAdminGroup();
        result = result * 59 + ($addToAdminGroup == null ? 43 : ((Object)$addToAdminGroup).hashCode());
        Integer $bypassCodeCount = this.getBypassCodeCount();
        result = result * 59 + ($bypassCodeCount == null ? 43 : ((Object)$bypassCodeCount).hashCode());
        Integer $bypassCodeExpiryDays = this.getBypassCodeExpiryDays();
        result = result * 59 + ($bypassCodeExpiryDays == null ? 43 : ((Object)$bypassCodeExpiryDays).hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        String $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $firstName = this.getFirstName();
        result = result * 59 + ($firstName == null ? 43 : $firstName.hashCode());
        String $lastName = this.getLastName();
        result = result * 59 + ($lastName == null ? 43 : $lastName.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $domainId = this.getDomainId();
        result = result * 59 + ($domainId == null ? 43 : $domainId.hashCode());
        Map $capabilities = this.getCapabilities();
        result = result * 59 + ($capabilities == null ? 43 : ((Object)$capabilities).hashCode());
        List $groupIds = this.getGroupIds();
        result = result * 59 + ($groupIds == null ? 43 : ((Object)$groupIds).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UserParams(tenantId=" + this.getTenantId() + ", userId=" + this.getUserId() + ", userName=" + this.getUserName() + ", email=" + this.getEmail() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", password=" + this.getPassword() + ", addToAdminGroup=" + this.getAddToAdminGroup() + ", domainId=" + this.getDomainId() + ", bypassCodeCount=" + this.getBypassCodeCount() + ", bypassCodeExpiryDays=" + this.getBypassCodeExpiryDays() + ", capabilities=" + String.valueOf(this.getCapabilities()) + ", groupIds=" + String.valueOf(this.getGroupIds()) + ")";
    }
}

