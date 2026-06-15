package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class IdParams {
    @NotBlank(
        message = "ID不能为空"
    )
    private String id;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof IdParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            return this$id == null ? other$id == null : this$id.equals(other$id);
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof IdParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        return result * 59 + ($id == null ? 43 : $id.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "IdParams(id=" + this.getId() + ")";
    }
}
