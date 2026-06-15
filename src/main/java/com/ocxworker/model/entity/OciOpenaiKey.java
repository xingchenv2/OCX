package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_openai_key")
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
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setOciUserId(final String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setKeyHash(final String keyHash) {
        this.keyHash = keyHash;
    }

    @Generated
    public void setKeyPrefix(final String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Generated
    public void setKeyEncrypted(final String keyEncrypted) {
        this.keyEncrypted = keyEncrypted;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setDisabled(final Integer disabled) {
        this.disabled = disabled;
    }

    @Generated
    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    public void setLastUsed(final LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OciOpenaiKey other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$disabled = this.getDisabled();
            Object other$disabled = other.getDisabled();
            if (this$disabled == null ? other$disabled == null : this$disabled.equals(other$disabled)) {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                    Object this$ociUserId = this.getOciUserId();
                    Object other$ociUserId = other.getOciUserId();
                    if (this$ociUserId == null ? other$ociUserId == null : this$ociUserId.equals(other$ociUserId)) {
                        Object this$keyHash = this.getKeyHash();
                        Object other$keyHash = other.getKeyHash();
                        if (this$keyHash == null ? other$keyHash == null : this$keyHash.equals(other$keyHash)) {
                            Object this$keyPrefix = this.getKeyPrefix();
                            Object other$keyPrefix = other.getKeyPrefix();
                            if (this$keyPrefix == null ? other$keyPrefix == null : this$keyPrefix.equals(other$keyPrefix)) {
                                Object this$keyEncrypted = this.getKeyEncrypted();
                                Object other$keyEncrypted = other.getKeyEncrypted();
                                if (this$keyEncrypted == null ? other$keyEncrypted == null : this$keyEncrypted.equals(other$keyEncrypted)) {
                                    Object this$name = this.getName();
                                    Object other$name = other.getName();
                                    if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                                        Object this$createTime = this.getCreateTime();
                                        Object other$createTime = other.getCreateTime();
                                        if (this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime)) {
                                            Object this$lastUsed = this.getLastUsed();
                                            Object other$lastUsed = other.getLastUsed();
                                            return this$lastUsed == null ? other$lastUsed == null : this$lastUsed.equals(other$lastUsed);
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
        return other instanceof OciOpenaiKey;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $disabled = this.getDisabled();
        result = result * 59 + ($disabled == null ? 43 : $disabled.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        Object $keyHash = this.getKeyHash();
        result = result * 59 + ($keyHash == null ? 43 : $keyHash.hashCode());
        Object $keyPrefix = this.getKeyPrefix();
        result = result * 59 + ($keyPrefix == null ? 43 : $keyPrefix.hashCode());
        Object $keyEncrypted = this.getKeyEncrypted();
        result = result * 59 + ($keyEncrypted == null ? 43 : $keyEncrypted.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $lastUsed = this.getLastUsed();
        return result * 59 + ($lastUsed == null ? 43 : $lastUsed.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciOpenaiKey(id="
            + this.getId()
            + ", ociUserId="
            + this.getOciUserId()
            + ", keyHash="
            + this.getKeyHash()
            + ", keyPrefix="
            + this.getKeyPrefix()
            + ", keyEncrypted="
            + this.getKeyEncrypted()
            + ", name="
            + this.getName()
            + ", disabled="
            + this.getDisabled()
            + ", createTime="
            + this.getCreateTime()
            + ", lastUsed="
            + this.getLastUsed()
            + ")";
    }
}
