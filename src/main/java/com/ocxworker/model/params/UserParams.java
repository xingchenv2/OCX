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
    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    @Generated
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @Generated
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Generated
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Generated
    public void setPassword(final String password) {
        this.password = password;
    }

    @Generated
    public void setAddToAdminGroup(final Boolean addToAdminGroup) {
        this.addToAdminGroup = addToAdminGroup;
    }

    @Generated
    public void setDomainId(final String domainId) {
        this.domainId = domainId;
    }

    @Generated
    public void setBypassCodeCount(final Integer bypassCodeCount) {
        this.bypassCodeCount = bypassCodeCount;
    }

    @Generated
    public void setBypassCodeExpiryDays(final Integer bypassCodeExpiryDays) {
        this.bypassCodeExpiryDays = bypassCodeExpiryDays;
    }

    @Generated
    public void setCapabilities(final Map<String, Boolean> capabilities) {
        this.capabilities = capabilities;
    }

    @Generated
    public void setGroupIds(final List<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UserParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$addToAdminGroup = this.getAddToAdminGroup();
            Object other$addToAdminGroup = other.getAddToAdminGroup();
            if (this$addToAdminGroup == null ? other$addToAdminGroup == null : this$addToAdminGroup.equals(other$addToAdminGroup)) {
                Object this$bypassCodeCount = this.getBypassCodeCount();
                Object other$bypassCodeCount = other.getBypassCodeCount();
                if (this$bypassCodeCount == null ? other$bypassCodeCount == null : this$bypassCodeCount.equals(other$bypassCodeCount)) {
                    Object this$bypassCodeExpiryDays = this.getBypassCodeExpiryDays();
                    Object other$bypassCodeExpiryDays = other.getBypassCodeExpiryDays();
                    if (this$bypassCodeExpiryDays == null ? other$bypassCodeExpiryDays == null : this$bypassCodeExpiryDays.equals(other$bypassCodeExpiryDays)) {
                        Object this$tenantId = this.getTenantId();
                        Object other$tenantId = other.getTenantId();
                        if (this$tenantId == null ? other$tenantId == null : this$tenantId.equals(other$tenantId)) {
                            Object this$userId = this.getUserId();
                            Object other$userId = other.getUserId();
                            if (this$userId == null ? other$userId == null : this$userId.equals(other$userId)) {
                                Object this$userName = this.getUserName();
                                Object other$userName = other.getUserName();
                                if (this$userName == null ? other$userName == null : this$userName.equals(other$userName)) {
                                    Object this$email = this.getEmail();
                                    Object other$email = other.getEmail();
                                    if (this$email == null ? other$email == null : this$email.equals(other$email)) {
                                        Object this$firstName = this.getFirstName();
                                        Object other$firstName = other.getFirstName();
                                        if (this$firstName == null ? other$firstName == null : this$firstName.equals(other$firstName)) {
                                            Object this$lastName = this.getLastName();
                                            Object other$lastName = other.getLastName();
                                            if (this$lastName == null ? other$lastName == null : this$lastName.equals(other$lastName)) {
                                                Object this$password = this.getPassword();
                                                Object other$password = other.getPassword();
                                                if (this$password == null ? other$password == null : this$password.equals(other$password)) {
                                                    Object this$domainId = this.getDomainId();
                                                    Object other$domainId = other.getDomainId();
                                                    if (this$domainId == null ? other$domainId == null : this$domainId.equals(other$domainId)) {
                                                        Object this$capabilities = this.getCapabilities();
                                                        Object other$capabilities = other.getCapabilities();
                                                        if (this$capabilities == null
                                                            ? other$capabilities == null
                                                            : this$capabilities.equals(other$capabilities)) {
                                                            Object this$groupIds = this.getGroupIds();
                                                            Object other$groupIds = other.getGroupIds();
                                                            return this$groupIds == null ? other$groupIds == null : this$groupIds.equals(other$groupIds);
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
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof UserParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $addToAdminGroup = this.getAddToAdminGroup();
        result = result * 59 + ($addToAdminGroup == null ? 43 : $addToAdminGroup.hashCode());
        Object $bypassCodeCount = this.getBypassCodeCount();
        result = result * 59 + ($bypassCodeCount == null ? 43 : $bypassCodeCount.hashCode());
        Object $bypassCodeExpiryDays = this.getBypassCodeExpiryDays();
        result = result * 59 + ($bypassCodeExpiryDays == null ? 43 : $bypassCodeExpiryDays.hashCode());
        Object $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        Object $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        Object $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $firstName = this.getFirstName();
        result = result * 59 + ($firstName == null ? 43 : $firstName.hashCode());
        Object $lastName = this.getLastName();
        result = result * 59 + ($lastName == null ? 43 : $lastName.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $domainId = this.getDomainId();
        result = result * 59 + ($domainId == null ? 43 : $domainId.hashCode());
        Object $capabilities = this.getCapabilities();
        result = result * 59 + ($capabilities == null ? 43 : $capabilities.hashCode());
        Object $groupIds = this.getGroupIds();
        return result * 59 + ($groupIds == null ? 43 : $groupIds.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "UserParams(tenantId="
            + this.getTenantId()
            + ", userId="
            + this.getUserId()
            + ", userName="
            + this.getUserName()
            + ", email="
            + this.getEmail()
            + ", firstName="
            + this.getFirstName()
            + ", lastName="
            + this.getLastName()
            + ", password="
            + this.getPassword()
            + ", addToAdminGroup="
            + this.getAddToAdminGroup()
            + ", domainId="
            + this.getDomainId()
            + ", bypassCodeCount="
            + this.getBypassCodeCount()
            + ", bypassCodeExpiryDays="
            + this.getBypassCodeExpiryDays()
            + ", capabilities="
            + this.getCapabilities()
            + ", groupIds="
            + this.getGroupIds()
            + ")";
    }
}
