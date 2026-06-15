/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ociworker.model.entity.OciOpenaiPortBinding
 *  lombok.Generated
 */
package com.ociworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_openai_port_binding")
public class OciOpenaiPortBinding {
    @TableId
    private String id;
    private String name;
    private Integer port;
    private String ociUserId;
    private String ociRegion;
    private String openaiKeyId;
    private Integer defaultMaxTokens;
    private String allowedModelsJson;
    private Integer enabled;
    private String status;
    private String statusMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastUsed;

    @Generated
    public OciOpenaiPortBinding() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Integer getPort() {
        return this.port;
    }

    @Generated
    public String getOciUserId() {
        return this.ociUserId;
    }

    @Generated
    public String getOciRegion() {
        return this.ociRegion;
    }

    @Generated
    public String getOpenaiKeyId() {
        return this.openaiKeyId;
    }

    @Generated
    public Integer getDefaultMaxTokens() {
        return this.defaultMaxTokens;
    }

    @Generated
    public String getAllowedModelsJson() {
        return this.allowedModelsJson;
    }

    @Generated
    public Integer getEnabled() {
        return this.enabled;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public String getStatusMessage() {
        return this.statusMessage;
    }

    @Generated
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Generated
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
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
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setPort(Integer port) {
        this.port = port;
    }

    @Generated
    public void setOciUserId(String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setOciRegion(String ociRegion) {
        this.ociRegion = ociRegion;
    }

    @Generated
    public void setOpenaiKeyId(String openaiKeyId) {
        this.openaiKeyId = openaiKeyId;
    }

    @Generated
    public void setDefaultMaxTokens(Integer defaultMaxTokens) {
        this.defaultMaxTokens = defaultMaxTokens;
    }

    @Generated
    public void setAllowedModelsJson(String allowedModelsJson) {
        this.allowedModelsJson = allowedModelsJson;
    }

    @Generated
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Generated
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
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
        if (!(o instanceof OciOpenaiPortBinding)) {
            return false;
        }
        OciOpenaiPortBinding other = (OciOpenaiPortBinding)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$port = this.getPort();
        Integer other$port = other.getPort();
        if (this$port == null ? other$port != null : !((Object)this$port).equals(other$port)) {
            return false;
        }
        Integer this$defaultMaxTokens = this.getDefaultMaxTokens();
        Integer other$defaultMaxTokens = other.getDefaultMaxTokens();
        if (this$defaultMaxTokens == null ? other$defaultMaxTokens != null : !((Object)this$defaultMaxTokens).equals(other$defaultMaxTokens)) {
            return false;
        }
        Integer this$enabled = this.getEnabled();
        Integer other$enabled = other.getEnabled();
        if (this$enabled == null ? other$enabled != null : !((Object)this$enabled).equals(other$enabled)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$ociUserId = this.getOciUserId();
        String other$ociUserId = other.getOciUserId();
        if (this$ociUserId == null ? other$ociUserId != null : !this$ociUserId.equals(other$ociUserId)) {
            return false;
        }
        String this$ociRegion = this.getOciRegion();
        String other$ociRegion = other.getOciRegion();
        if (this$ociRegion == null ? other$ociRegion != null : !this$ociRegion.equals(other$ociRegion)) {
            return false;
        }
        String this$openaiKeyId = this.getOpenaiKeyId();
        String other$openaiKeyId = other.getOpenaiKeyId();
        if (this$openaiKeyId == null ? other$openaiKeyId != null : !this$openaiKeyId.equals(other$openaiKeyId)) {
            return false;
        }
        String this$allowedModelsJson = this.getAllowedModelsJson();
        String other$allowedModelsJson = other.getAllowedModelsJson();
        if (this$allowedModelsJson == null ? other$allowedModelsJson != null : !this$allowedModelsJson.equals(other$allowedModelsJson)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$statusMessage = this.getStatusMessage();
        String other$statusMessage = other.getStatusMessage();
        if (this$statusMessage == null ? other$statusMessage != null : !this$statusMessage.equals(other$statusMessage)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        if (this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime)) {
            return false;
        }
        LocalDateTime this$updateTime = this.getUpdateTime();
        LocalDateTime other$updateTime = other.getUpdateTime();
        if (this$updateTime == null ? other$updateTime != null : !((Object)this$updateTime).equals(other$updateTime)) {
            return false;
        }
        LocalDateTime this$lastUsed = this.getLastUsed();
        LocalDateTime other$lastUsed = other.getLastUsed();
        return !(this$lastUsed == null ? other$lastUsed != null : !((Object)this$lastUsed).equals(other$lastUsed));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciOpenaiPortBinding;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $port = this.getPort();
        result = result * 59 + ($port == null ? 43 : ((Object)$port).hashCode());
        Integer $defaultMaxTokens = this.getDefaultMaxTokens();
        result = result * 59 + ($defaultMaxTokens == null ? 43 : ((Object)$defaultMaxTokens).hashCode());
        Integer $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : ((Object)$enabled).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        String $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        String $openaiKeyId = this.getOpenaiKeyId();
        result = result * 59 + ($openaiKeyId == null ? 43 : $openaiKeyId.hashCode());
        String $allowedModelsJson = this.getAllowedModelsJson();
        result = result * 59 + ($allowedModelsJson == null ? 43 : $allowedModelsJson.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $statusMessage = this.getStatusMessage();
        result = result * 59 + ($statusMessage == null ? 43 : $statusMessage.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        LocalDateTime $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : ((Object)$updateTime).hashCode());
        LocalDateTime $lastUsed = this.getLastUsed();
        result = result * 59 + ($lastUsed == null ? 43 : ((Object)$lastUsed).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciOpenaiPortBinding(id=" + this.getId() + ", name=" + this.getName() + ", port=" + this.getPort() + ", ociUserId=" + this.getOciUserId() + ", ociRegion=" + this.getOciRegion() + ", openaiKeyId=" + this.getOpenaiKeyId() + ", defaultMaxTokens=" + this.getDefaultMaxTokens() + ", allowedModelsJson=" + this.getAllowedModelsJson() + ", enabled=" + this.getEnabled() + ", status=" + this.getStatus() + ", statusMessage=" + this.getStatusMessage() + ", createTime=" + String.valueOf(this.getCreateTime()) + ", updateTime=" + String.valueOf(this.getUpdateTime()) + ", lastUsed=" + String.valueOf(this.getLastUsed()) + ")";
    }
}

