package com.ocxworker.model.params;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Generated;

public class IdListParams {
    @NotEmpty(
        message = "ID列表不能为空"
    )
    private List<String> idList;

    @Generated
    public List<String> getIdList() {
        return this.idList;
    }

    @Generated
    public void setIdList(final List<String> idList) {
        this.idList = idList;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof IdListParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$idList = this.getIdList();
            Object other$idList = other.getIdList();
            return this$idList == null ? other$idList == null : this$idList.equals(other$idList);
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof IdListParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $idList = this.getIdList();
        return result * 59 + ($idList == null ? 43 : $idList.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "IdListParams(idList=" + this.getIdList() + ")";
    }
}
