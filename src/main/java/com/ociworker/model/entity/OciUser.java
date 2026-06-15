/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.FieldStrategy
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ociworker.model.entity.OciUser
 *  lombok.Generated
 */
package com.ociworker.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_user")
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
    @TableField(updateStrategy=FieldStrategy.ALWAYS)
    private String groupLevel1;
    @TableField(updateStrategy=FieldStrategy.ALWAYS)
    private String groupLevel2;
    @TableField(value="generative_openai_project", updateStrategy=FieldStrategy.ALWAYS)
    private String generativeOpenaiProject;
    @TableField(value="generative_conversation_store_id", updateStrategy=FieldStrategy.ALWAYS)
    private String generativeConversationStoreId;
    private LocalDateTime createTime;

    @Generated
    public OciUser() {
    }

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
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Generated
    public void setTenantCreateTime(LocalDateTime tenantCreateTime) {
        this.tenantCreateTime = tenantCreateTime;
    }

    @Generated
    public void setOciTenantId(String ociTenantId) {
        this.ociTenantId = ociTenantId;
    }

    @Generated
    public void setOciUserId(String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setOciFingerprint(String ociFingerprint) {
        this.ociFingerprint = ociFingerprint;
    }

    @Generated
    public void setOciRegion(String ociRegion) {
        this.ociRegion = ociRegion;
    }

    @Generated
    public void setOciKeyPath(String ociKeyPath) {
        this.ociKeyPath = ociKeyPath;
    }

    @Generated
    public void setPlanType(String planType) {
        this.planType = planType;
    }

    @Generated
    public void setGroupLevel1(String groupLevel1) {
        this.groupLevel1 = groupLevel1;
    }

    @Generated
    public void setGroupLevel2(String groupLevel2) {
        this.groupLevel2 = groupLevel2;
    }

    @Generated
    public void setGenerativeOpenaiProject(String generativeOpenaiProject) {
        this.generativeOpenaiProject = generativeOpenaiProject;
    }

    @Generated
    public void setGenerativeConversationStoreId(String generativeConversationStoreId) {
        this.generativeConversationStoreId = generativeConversationStoreId;
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
        if (!(o instanceof OciUser)) {
            return false;
        }
        OciUser other = (OciUser)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$tenantName = this.getTenantName();
        String other$tenantName = other.getTenantName();
        if (this$tenantName == null ? other$tenantName != null : !this$tenantName.equals(other$tenantName)) {
            return false;
        }
        LocalDateTime this$tenantCreateTime = this.getTenantCreateTime();
        LocalDateTime other$tenantCreateTime = other.getTenantCreateTime();
        if (this$tenantCreateTime == null ? other$tenantCreateTime != null : !((Object)this$tenantCreateTime).equals(other$tenantCreateTime)) {
            return false;
        }
        String this$ociTenantId = this.getOciTenantId();
        String other$ociTenantId = other.getOciTenantId();
        if (this$ociTenantId == null ? other$ociTenantId != null : !this$ociTenantId.equals(other$ociTenantId)) {
            return false;
        }
        String this$ociUserId = this.getOciUserId();
        String other$ociUserId = other.getOciUserId();
        if (this$ociUserId == null ? other$ociUserId != null : !this$ociUserId.equals(other$ociUserId)) {
            return false;
        }
        String this$ociFingerprint = this.getOciFingerprint();
        String other$ociFingerprint = other.getOciFingerprint();
        if (this$ociFingerprint == null ? other$ociFingerprint != null : !this$ociFingerprint.equals(other$ociFingerprint)) {
            return false;
        }
        String this$ociRegion = this.getOciRegion();
        String other$ociRegion = other.getOciRegion();
        if (this$ociRegion == null ? other$ociRegion != null : !this$ociRegion.equals(other$ociRegion)) {
            return false;
        }
        String this$ociKeyPath = this.getOciKeyPath();
        String other$ociKeyPath = other.getOciKeyPath();
        if (this$ociKeyPath == null ? other$ociKeyPath != null : !this$ociKeyPath.equals(other$ociKeyPath)) {
            return false;
        }
        String this$planType = this.getPlanType();
        String other$planType = other.getPlanType();
        if (this$planType == null ? other$planType != null : !this$planType.equals(other$planType)) {
            return false;
        }
        String this$groupLevel1 = this.getGroupLevel1();
        String other$groupLevel1 = other.getGroupLevel1();
        if (this$groupLevel1 == null ? other$groupLevel1 != null : !this$groupLevel1.equals(other$groupLevel1)) {
            return false;
        }
        String this$groupLevel2 = this.getGroupLevel2();
        String other$groupLevel2 = other.getGroupLevel2();
        if (this$groupLevel2 == null ? other$groupLevel2 != null : !this$groupLevel2.equals(other$groupLevel2)) {
            return false;
        }
        String this$generativeOpenaiProject = this.getGenerativeOpenaiProject();
        String other$generativeOpenaiProject = other.getGenerativeOpenaiProject();
        if (this$generativeOpenaiProject == null ? other$generativeOpenaiProject != null : !this$generativeOpenaiProject.equals(other$generativeOpenaiProject)) {
            return false;
        }
        String this$generativeConversationStoreId = this.getGenerativeConversationStoreId();
        String other$generativeConversationStoreId = other.getGenerativeConversationStoreId();
        if (this$generativeConversationStoreId == null ? other$generativeConversationStoreId != null : !this$generativeConversationStoreId.equals(other$generativeConversationStoreId)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciUser;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $tenantName = this.getTenantName();
        result = result * 59 + ($tenantName == null ? 43 : $tenantName.hashCode());
        LocalDateTime $tenantCreateTime = this.getTenantCreateTime();
        result = result * 59 + ($tenantCreateTime == null ? 43 : ((Object)$tenantCreateTime).hashCode());
        String $ociTenantId = this.getOciTenantId();
        result = result * 59 + ($ociTenantId == null ? 43 : $ociTenantId.hashCode());
        String $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        String $ociFingerprint = this.getOciFingerprint();
        result = result * 59 + ($ociFingerprint == null ? 43 : $ociFingerprint.hashCode());
        String $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        String $ociKeyPath = this.getOciKeyPath();
        result = result * 59 + ($ociKeyPath == null ? 43 : $ociKeyPath.hashCode());
        String $planType = this.getPlanType();
        result = result * 59 + ($planType == null ? 43 : $planType.hashCode());
        String $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        String $groupLevel2 = this.getGroupLevel2();
        result = result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
        String $generativeOpenaiProject = this.getGenerativeOpenaiProject();
        result = result * 59 + ($generativeOpenaiProject == null ? 43 : $generativeOpenaiProject.hashCode());
        String $generativeConversationStoreId = this.getGenerativeConversationStoreId();
        result = result * 59 + ($generativeConversationStoreId == null ? 43 : $generativeConversationStoreId.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciUser(id=" + this.getId() + ", username=" + this.getUsername() + ", tenantName=" + this.getTenantName() + ", tenantCreateTime=" + String.valueOf(this.getTenantCreateTime()) + ", ociTenantId=" + this.getOciTenantId() + ", ociUserId=" + this.getOciUserId() + ", ociFingerprint=" + this.getOciFingerprint() + ", ociRegion=" + this.getOciRegion() + ", ociKeyPath=" + this.getOciKeyPath() + ", planType=" + this.getPlanType() + ", groupLevel1=" + this.getGroupLevel1() + ", groupLevel2=" + this.getGroupLevel2() + ", generativeOpenaiProject=" + this.getGenerativeOpenaiProject() + ", generativeConversationStoreId=" + this.getGenerativeConversationStoreId() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

