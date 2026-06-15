package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class TenantParams {
    private String id;
    @NotBlank(
        message = "名称不能为空"
    )
    private String username;
    @NotBlank(
        message = "Tenant OCID 不能为空"
    )
    private String ociTenantId;
    @NotBlank(
        message = "User OCID 不能为空"
    )
    private String ociUserId;
    @NotBlank(
        message = "Fingerprint 不能为空"
    )
    private String ociFingerprint;
    @NotBlank(
        message = "Region 不能为空"
    )
    private String ociRegion;
    private String ociKeyPath;
    private String groupLevel1;
    private String groupLevel2;

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
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setUsername(final String username) {
        this.username = username;
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
    public void setGroupLevel1(final String groupLevel1) {
        this.groupLevel1 = groupLevel1;
    }

    @Generated
    public void setGroupLevel2(final String groupLevel2) {
        this.groupLevel2 = groupLevel2;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TenantParams other)) {
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
                                        Object this$groupLevel1 = this.getGroupLevel1();
                                        Object other$groupLevel1 = other.getGroupLevel1();
                                        if (this$groupLevel1 == null ? other$groupLevel1 == null : this$groupLevel1.equals(other$groupLevel1)) {
                                            Object this$groupLevel2 = this.getGroupLevel2();
                                            Object other$groupLevel2 = other.getGroupLevel2();
                                            return this$groupLevel2 == null ? other$groupLevel2 == null : this$groupLevel2.equals(other$groupLevel2);
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
        return other instanceof TenantParams;
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
        Object $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        Object $groupLevel2 = this.getGroupLevel2();
        return result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "TenantParams(id="
            + this.getId()
            + ", username="
            + this.getUsername()
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
            + ", groupLevel1="
            + this.getGroupLevel1()
            + ", groupLevel2="
            + this.getGroupLevel2()
            + ")";
    }
}
