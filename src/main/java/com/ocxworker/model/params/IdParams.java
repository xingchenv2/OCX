/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.params.IdParams
 *  jakarta.validation.constraints.NotBlank
 *  lombok.Generated
 */
package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class IdParams {
    @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="ID\u4e0d\u80fd\u4e3a\u7a7a") String id;

    @Generated
    public IdParams() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IdParams)) {
            return false;
        }
        IdParams other = (IdParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        return !(this$id == null ? other$id != null : !this$id.equals(other$id));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof IdParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "IdParams(id=" + this.getId() + ")";
    }
}

