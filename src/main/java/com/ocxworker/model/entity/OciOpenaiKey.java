/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ocxworker.model.entity.OciOpenaiKey
 *  lombok.Generated
 */
package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_openai_key")
public class OciOpenaiKey {
    @TableId
    private String id;
    private String ociUserId;
    private String keyHash;
    private String keyPrefix;
    private String keyEncrypted;
    private String name;
    private Integer disabled;
    private LocalDateTime createTime;
    private LocalDateTime lastUsed;

    @Generated
    public OciOpenaiKey() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getOciUserId() {
        return this.ociUserId;
    }

    @Generated
    public String getKeyHash() {
        return this.keyHash;
    }

    @Generated
    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    @Generated
    public String getKeyEncrypted() {
        return this.keyEncrypted;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Integer getDisabled() {
        return this.disabled;
    }

    @Generated
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Generated
    public LocalDateTime getLastUsed() {
        return this.lastUsed;
    }

    @Generated
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setOciUserId(String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    @Generated
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Generated
    public void setKeyEncrypted(String keyEncrypted) {
        this.keyEncrypted = keyEncrypted;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Generated
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OciOpenaiKey)) {
            return false;
        }
        OciOpenaiKey other = (OciOpenaiKey)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$disabled = this.getDisabled();
        Integer other$disabled = other.getDisabled();
        if (this$disabled == null ? other$disabled != null : !((Object)this$disabled).equals(other$disabled)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$ociUserId = this.getOciUserId();
        String other$ociUserId = other.getOciUserId();
        if (this$ociUserId == null ? other$ociUserId != null : !this$ociUserId.equals(other$ociUserId)) {
            return false;
        }
        String this$keyHash = this.getKeyHash();
        String other$keyHash = other.getKeyHash();
        if (this$keyHash == null ? other$keyHash != null : !this$keyHash.equals(other$keyHash)) {
            return false;
        }
        String this$keyPrefix = this.getKeyPrefix();
        String other$keyPrefix = other.getKeyPrefix();
        if (this$keyPrefix == null ? other$keyPrefix != null : !this$keyPrefix.equals(other$keyPrefix)) {
            return false;
        }
        String this$keyEncrypted = this.getKeyEncrypted();
        String other$keyEncrypted = other.getKeyEncrypted();
        if (this$keyEncrypted == null ? other$keyEncrypted != null : !this$keyEncrypted.equals(other$keyEncrypted)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        if (this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime)) {
            return false;
        }
        LocalDateTime this$lastUsed = this.getLastUsed();
        LocalDateTime other$lastUsed = other.getLastUsed();
        return !(this$lastUsed == null ? other$lastUsed != null : !((Object)this$lastUsed).equals(other$lastUsed));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciOpenaiKey;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $disabled = this.getDisabled();
        result = result * 59 + ($disabled == null ? 43 : ((Object)$disabled).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        String $keyHash = this.getKeyHash();
        result = result * 59 + ($keyHash == null ? 43 : $keyHash.hashCode());
        String $keyPrefix = this.getKeyPrefix();
        result = result * 59 + ($keyPrefix == null ? 43 : $keyPrefix.hashCode());
        String $keyEncrypted = this.getKeyEncrypted();
        result = result * 59 + ($keyEncrypted == null ? 43 : $keyEncrypted.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        LocalDateTime $lastUsed = this.getLastUsed();
        result = result * 59 + ($lastUsed == null ? 43 : ((Object)$lastUsed).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciOpenaiKey(id=" + this.getId() + ", ociUserId=" + this.getOciUserId() + ", keyHash=" + this.getKeyHash() + ", keyPrefix=" + this.getKeyPrefix() + ", keyEncrypted=" + this.getKeyEncrypted() + ", name=" + this.getName() + ", disabled=" + this.getDisabled() + ", createTime=" + String.valueOf(this.getCreateTime()) + ", lastUsed=" + String.valueOf(this.getLastUsed()) + ")";
    }
}

