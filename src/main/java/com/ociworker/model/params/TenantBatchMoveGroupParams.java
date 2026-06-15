/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.params.TenantBatchMoveGroupParams
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotEmpty
 *  lombok.Generated
 */
package com.ociworker.model.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Generated;

public class TenantBatchMoveGroupParams {
    @NotEmpty(message="\u8bf7\u9009\u62e9\u8981\u79fb\u52a8\u7684\u79df\u6237")
    private @NotEmpty(message="\u8bf7\u9009\u62e9\u8981\u79fb\u52a8\u7684\u79df\u6237") List<String> idList;
    @NotBlank(message="\u8bf7\u9009\u62e9\u4e00\u7ea7\u5206\u7ec4")
    private @NotBlank(message="\u8bf7\u9009\u62e9\u4e00\u7ea7\u5206\u7ec4") String groupLevel1;
    private String groupLevel2;

    @Generated
    public TenantBatchMoveGroupParams() {
    }

    @Generated
    public List<String> getIdList() {
        return this.idList;
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
    public void setIdList(List<String> idList) {
        this.idList = idList;
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
        if (!(o instanceof TenantBatchMoveGroupParams)) {
            return false;
        }
        TenantBatchMoveGroupParams other = (TenantBatchMoveGroupParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$idList = this.getIdList();
        List other$idList = other.getIdList();
        if (this$idList == null ? other$idList != null : !((Object)this$idList).equals(other$idList)) {
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
        return other instanceof TenantBatchMoveGroupParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $idList = this.getIdList();
        result = result * 59 + ($idList == null ? 43 : ((Object)$idList).hashCode());
        String $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        String $groupLevel2 = this.getGroupLevel2();
        result = result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "TenantBatchMoveGroupParams(idList=" + String.valueOf(this.getIdList()) + ", groupLevel1=" + this.getGroupLevel1() + ", groupLevel2=" + this.getGroupLevel2() + ")";
    }
}

