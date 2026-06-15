package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_kv")
public class OciKv {
    @TableId
    private String id;
    private String code;
    private String value;
    private String type;
    private LocalDateTime createTime;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getValue() {
        return this.value;
    }

    @Generated
    public String getType() {
        return this.type;
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
    public void setCode(final String code) {
        this.code = code;
    }

    @Generated
    public void setValue(final String value) {
        this.value = value;
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
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
        } else if (!(o instanceof OciKv other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null ? other$code == null : this$code.equals(other$code)) {
                    Object this$value = this.getValue();
                    Object other$value = other.getValue();
                    if (this$value == null ? other$value == null : this$value.equals(other$value)) {
                        Object this$type = this.getType();
                        Object other$type = other.getType();
                        if (this$type == null ? other$type == null : this$type.equals(other$type)) {
                            Object this$createTime = this.getCreateTime();
                            Object other$createTime = other.getCreateTime();
                            return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
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
        return other instanceof OciKv;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciKv(id="
            + this.getId()
            + ", code="
            + this.getCode()
            + ", value="
            + this.getValue()
            + ", type="
            + this.getType()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
