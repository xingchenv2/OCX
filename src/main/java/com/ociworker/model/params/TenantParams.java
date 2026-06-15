/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.params.TenantParams
 *  jakarta.validation.constraints.NotBlank
 *  lombok.Generated
 */
package com.ociworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class TenantParams {
    private String id;
    @NotBlank(message="\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String username;
    @NotBlank(message="Tenant OCID \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="Tenant OCID \u4e0d\u80fd\u4e3a\u7a7a") String ociTenantId;
    @NotBlank(message="User OCID \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="User OCID \u4e0d\u80fd\u4e3a\u7a7a") String ociUserId;
    @NotBlank(message="Fingerprint \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="Fingerprint \u4e0d\u80fd\u4e3a\u7a7a") String ociFingerprint;
    @NotBlank(message="Region \u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="Region \u4e0d\u80fd\u4e3a\u7a7a") String ociRegion;
    private String ociKeyPath;
    private String groupLevel1;
    private String groupLevel2;

    @Generated
    public TenantParams() {
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
    public String getGroupLevel1() {
        return this.groupLevel1;
    }

    @Generated
    public String getGroupLevel2() {
        return this.groupLevel2;
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
    public void setGroupLevel1(String groupLevel1) {
        this.groupLevel1 = groupLevel1;
    }

    @Generated
    public void setGroupLevel2(String groupLevel2) {
        this.groupLevel2 = groupLevel2;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TenantParams)) {
            return false;
        }
        TenantParams other = (TenantParams)o;
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
        String this$groupLevel1 = this.getGroupLevel1();
        String other$groupLevel1 = other.getGroupLevel1();
        if (this$groupLevel1 == null ? other$groupLevel1 != null : !this$groupLevel1.equals(other$groupLevel1)) {
            return false;
        }
        String this$groupLevel2 = this.getGroupLevel2();
        String other$groupLevel2 = other.getGroupLevel2();
        return !(this$groupLevel2 == null ? other$groupLevel2 != null : !this$groupLevel2.equals(other$groupLevel2));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof TenantParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
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
        String $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        String $groupLevel2 = this.getGroupLevel2();
        result = result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "TenantParams(id=" + this.getId() + ", username=" + this.getUsername() + ", ociTenantId=" + this.getOciTenantId() + ", ociUserId=" + this.getOciUserId() + ", ociFingerprint=" + this.getOciFingerprint() + ", ociRegion=" + this.getOciRegion() + ", ociKeyPath=" + this.getOciKeyPath() + ", groupLevel1=" + this.getGroupLevel1() + ", groupLevel2=" + this.getGroupLevel2() + ")";
    }
}

