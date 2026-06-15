/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ocxworker.model.entity.OciKv
 *  lombok.Generated
 */
package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_kv")
public class OciKv {
    @TableId
    private String id;
    private String code;
    private String value;
    private String type;
    private LocalDateTime createTime;

    @Generated
    public OciKv() {
    }

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
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setCode(String code) {
        this.code = code;
    }

    @Generated
    public void setValue(String value) {
        this.value = value;
    }

    @Generated
    public void setType(String type) {
        this.type = type;
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
        if (!(o instanceof OciKv)) {
            return false;
        }
        OciKv other = (OciKv)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) {
            return false;
        }
        String this$type = this.getType();
        String other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciKv;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        String $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciKv(id=" + this.getId() + ", code=" + this.getCode() + ", value=" + this.getValue() + ", type=" + this.getType() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

