package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_user")
public class OciUser {
    @TableId
    private String id;
    private String username;
    private String tenantName;
    private LocalDateTime tenantCreateTime;
    private String ociTenantId;
    private String ociUserId;
    private String ociFingerprint;
    private String ociRegion;
    private String ociKeyPath;
    private String planType;
    @TableField(
        updateStrategy = FieldStrategy.ALWAYS
    )
    private String groupLevel1;
    @TableField(
        updateStrategy = FieldStrategy.ALWAYS
    )
    private String groupLevel2;
    @TableField(
        value = "generative_openai_project",
        updateStrategy = FieldStrategy.ALWAYS
    )
    private String generativeOpenaiProject;
    @TableField(
        value = "generative_conversation_store_id",
        updateStrategy = FieldStrategy.ALWAYS
    )
    private String generativeConversationStoreId;
    private LocalDateTime createTime;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getTenantName() {
        return this.tenantName;
    }

    @Generated
    public LocalDateTime getTenantCreateTime() {
        return this.tenantCreateTime;
    }

    @Generated
    public String getOciTenantId() {
        return this.ociTenantId;
    }

    @Generated
    public String getOciUserId() {
        return this.ociUserId;
    }

    @Generated
    public String getOciFingerprint() {
        return this.ociFingerprint;
    }

    @Generated
    public String getOciRegion() {
        return this.ociRegion;
    }

    @Generated
    public String getOciKeyPath() {
        return this.ociKeyPath;
    }

    @Generated
    public String getPlanType() {
        return this.planType;
    }

    @Generated
    public String getGroupLevel1() {
        return this.groupLevel1;
    }

    @Generated
    public String getGroupLevel2() {
        return this.groupLevel2;
    }

    @Generated
    public String getGenerativeOpenaiProject() {
        return this.generativeOpenaiProject;
    }

    @Generated
    public String getGenerativeConversationStoreId() {
        return this.generativeConversationStoreId;
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
    public void setUsername(final String username) {
        this.username = username;
    }

    @Generated
    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }

    @Generated
    public void setTenantCreateTime(final LocalDateTime tenantCreateTime) {
        this.tenantCreateTime = tenantCreateTime;
    }

    @Generated
    public void setOciTenantId(final String ociTenantId) {
        this.ociTenantId = ociTenantId;
    }

    @Generated
    public void setOciUserId(final String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setOciFingerprint(final String ociFingerprint) {
        this.ociFingerprint = ociFingerprint;
    }

    @Generated
    public void setOciRegion(final String ociRegion) {
        this.ociRegion = ociRegion;
    }

    @Generated
    public void setOciKeyPath(final String ociKeyPath) {
        this.ociKeyPath = ociKeyPath;
    }

    @Generated
    public void setPlanType(final String planType) {
        this.planType = planType;
    }

    @Generated
    public void setGroupLevel1(final String groupLevel1) {
        this.groupLevel1 = groupLevel1;
    }

    @Generated
    public void setGroupLevel2(final String groupLevel2) {
        this.groupLevel2 = groupLevel2;
    }

    @Generated
    public void setGenerativeOpenaiProject(final String generativeOpenaiProject) {
        this.generativeOpenaiProject = generativeOpenaiProject;
    }

    @Generated
    public void setGenerativeConversationStoreId(final String generativeConversationStoreId) {
        this.generativeConversationStoreId = generativeConversationStoreId;
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
        } else if (!(o instanceof OciUser other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                Object this$username = this.getUsername();
                Object other$username = other.getUsername();
                if (this$username == null ? other$username == null : this$username.equals(other$username)) {
                    Object this$tenantName = this.getTenantName();
                    Object other$tenantName = other.getTenantName();
                    if (this$tenantName == null ? other$tenantName == null : this$tenantName.equals(other$tenantName)) {
                        Object this$tenantCreateTime = this.getTenantCreateTime();
                        Object other$tenantCreateTime = other.getTenantCreateTime();
                        if (this$tenantCreateTime == null ? other$tenantCreateTime == null : this$tenantCreateTime.equals(other$tenantCreateTime)) {
                            Object this$ociTenantId = this.getOciTenantId();
                            Object other$ociTenantId = other.getOciTenantId();
                            if (this$ociTenantId == null ? other$ociTenantId == null : this$ociTenantId.equals(other$ociTenantId)) {
                                Object this$ociUserId = this.getOciUserId();
                                Object other$ociUserId = other.getOciUserId();
                                if (this$ociUserId == null ? other$ociUserId == null : this$ociUserId.equals(other$ociUserId)) {
                                    Object this$ociFingerprint = this.getOciFingerprint();
                                    Object other$ociFingerprint = other.getOciFingerprint();
                                    if (this$ociFingerprint == null ? other$ociFingerprint == null : this$ociFingerprint.equals(other$ociFingerprint)) {
                                        Object this$ociRegion = this.getOciRegion();
                                        Object other$ociRegion = other.getOciRegion();
                                        if (this$ociRegion == null ? other$ociRegion == null : this$ociRegion.equals(other$ociRegion)) {
                                            Object this$ociKeyPath = this.getOciKeyPath();
                                            Object other$ociKeyPath = other.getOciKeyPath();
                                            if (this$ociKeyPath == null ? other$ociKeyPath == null : this$ociKeyPath.equals(other$ociKeyPath)) {
                                                Object this$planType = this.getPlanType();
                                                Object other$planType = other.getPlanType();
                                                if (this$planType == null ? other$planType == null : this$planType.equals(other$planType)) {
                                                    Object this$groupLevel1 = this.getGroupLevel1();
                                                    Object other$groupLevel1 = other.getGroupLevel1();
                                                    if (this$groupLevel1 == null ? other$groupLevel1 == null : this$groupLevel1.equals(other$groupLevel1)) {
                                                        Object this$groupLevel2 = this.getGroupLevel2();
                                                        Object other$groupLevel2 = other.getGroupLevel2();
                                                        if (this$groupLevel2 == null ? other$groupLevel2 == null : this$groupLevel2.equals(other$groupLevel2)) {
                                                            Object this$generativeOpenaiProject = this.getGenerativeOpenaiProject();
                                                            Object other$generativeOpenaiProject = other.getGenerativeOpenaiProject();
                                                            if (this$generativeOpenaiProject == null
                                                                ? other$generativeOpenaiProject == null
                                                                : this$generativeOpenaiProject.equals(other$generativeOpenaiProject)) {
                                                                Object this$generativeConversationStoreId = this.getGenerativeConversationStoreId();
                                                                Object other$generativeConversationStoreId = other.getGenerativeConversationStoreId();
                                                                if (this$generativeConversationStoreId == null
                                                                    ? other$generativeConversationStoreId == null
                                                                    : this$generativeConversationStoreId.equals(other$generativeConversationStoreId)) {
                                                                    Object this$createTime = this.getCreateTime();
                                                                    Object other$createTime = other.getCreateTime();
                                                                    return this$createTime == null
                                                                        ? other$createTime == null
                                                                        : this$createTime.equals(other$createTime);
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
        return other instanceof OciUser;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $tenantName = this.getTenantName();
        result = result * 59 + ($tenantName == null ? 43 : $tenantName.hashCode());
        Object $tenantCreateTime = this.getTenantCreateTime();
        result = result * 59 + ($tenantCreateTime == null ? 43 : $tenantCreateTime.hashCode());
        Object $ociTenantId = this.getOciTenantId();
        result = result * 59 + ($ociTenantId == null ? 43 : $ociTenantId.hashCode());
        Object $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        Object $ociFingerprint = this.getOciFingerprint();
        result = result * 59 + ($ociFingerprint == null ? 43 : $ociFingerprint.hashCode());
        Object $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        Object $ociKeyPath = this.getOciKeyPath();
        result = result * 59 + ($ociKeyPath == null ? 43 : $ociKeyPath.hashCode());
        Object $planType = this.getPlanType();
        result = result * 59 + ($planType == null ? 43 : $planType.hashCode());
        Object $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        Object $groupLevel2 = this.getGroupLevel2();
        result = result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
        Object $generativeOpenaiProject = this.getGenerativeOpenaiProject();
        result = result * 59 + ($generativeOpenaiProject == null ? 43 : $generativeOpenaiProject.hashCode());
        Object $generativeConversationStoreId = this.getGenerativeConversationStoreId();
        result = result * 59 + ($generativeConversationStoreId == null ? 43 : $generativeConversationStoreId.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciUser(id="
            + this.getId()
            + ", username="
            + this.getUsername()
            + ", tenantName="
            + this.getTenantName()
            + ", tenantCreateTime="
            + this.getTenantCreateTime()
            + ", ociTenantId="
            + this.getOciTenantId()
            + ", ociUserId="
            + this.getOciUserId()
            + ", ociFingerprint="
            + this.getOciFingerprint()
            + ", ociRegion="
            + this.getOciRegion()
            + ", ociKeyPath="
            + this.getOciKeyPath()
            + ", planType="
            + this.getPlanType()
            + ", groupLevel1="
            + this.getGroupLevel1()
            + ", groupLevel2="
            + this.getGroupLevel2()
            + ", generativeOpenaiProject="
            + this.getGenerativeOpenaiProject()
            + ", generativeConversationStoreId="
            + this.getGenerativeConversationStoreId()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
