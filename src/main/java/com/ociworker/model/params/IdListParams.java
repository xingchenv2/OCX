/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.params.IdListParams
 *  jakarta.validation.constraints.NotEmpty
 *  lombok.Generated
 */
package com.ociworker.model.params;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Generated;

public class IdListParams {
    @NotEmpty(message="ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotEmpty(message="ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a") List<String> idList;

    @Generated
    public IdListParams() {
    }

    @Generated
    public List<String> getIdList() {
        return this.idList;
    }

    @Generated
    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IdListParams)) {
            return false;
        }
        IdListParams other = (IdListParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$idList = this.getIdList();
        List other$idList = other.getIdList();
        return !(this$idList == null ? other$idList != null : !((Object)this$idList).equals(other$idList));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof IdListParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $idList = this.getIdList();
        result = result * 59 + ($idList == null ? 43 : ((Object)$idList).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "IdListParams(idList=" + String.valueOf(this.getIdList()) + ")";
    }
}

