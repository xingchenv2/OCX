package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Generated;

public class TenantBatchMoveGroupParams {
    @NotEmpty(
        message = "请选择要移动的租户"
    )
    private List<String> idList;
    @NotBlank(
        message = "请选择一级分组"
    )
    private String groupLevel1;
    private String groupLevel2;

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
    public void setIdList(final List<String> idList) {
        this.idList = idList;
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
        } else if (!(o instanceof TenantBatchMoveGroupParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$idList = this.getIdList();
            Object other$idList = other.getIdList();
            if (this$idList == null ? other$idList == null : this$idList.equals(other$idList)) {
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
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof TenantBatchMoveGroupParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $idList = this.getIdList();
        result = result * 59 + ($idList == null ? 43 : $idList.hashCode());
        Object $groupLevel1 = this.getGroupLevel1();
        result = result * 59 + ($groupLevel1 == null ? 43 : $groupLevel1.hashCode());
        Object $groupLevel2 = this.getGroupLevel2();
        return result * 59 + ($groupLevel2 == null ? 43 : $groupLevel2.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "TenantBatchMoveGroupParams(idList="
            + this.getIdList()
            + ", groupLevel1="
            + this.getGroupLevel1()
            + ", groupLevel2="
            + this.getGroupLevel2()
            + ")";
    }
}
