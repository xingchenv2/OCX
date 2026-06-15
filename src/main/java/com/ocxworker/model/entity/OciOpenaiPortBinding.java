package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_openai_port_binding")
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
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setPort(final Integer port) {
        this.port = port;
    }

    @Generated
    public void setOciUserId(final String ociUserId) {
        this.ociUserId = ociUserId;
    }

    @Generated
    public void setOciRegion(final String ociRegion) {
        this.ociRegion = ociRegion;
    }

    @Generated
    public void setOpenaiKeyId(final String openaiKeyId) {
        this.openaiKeyId = openaiKeyId;
    }

    @Generated
    public void setDefaultMaxTokens(final Integer defaultMaxTokens) {
        this.defaultMaxTokens = defaultMaxTokens;
    }

    @Generated
    public void setAllowedModelsJson(final String allowedModelsJson) {
        this.allowedModelsJson = allowedModelsJson;
    }

    @Generated
    public void setEnabled(final Integer enabled) {
        this.enabled = enabled;
    }

    @Generated
    public void setStatus(final String status) {
        this.status = status;
    }

    @Generated
    public void setStatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Generated
    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Generated
    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
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
        } else if (!(o instanceof OciOpenaiPortBinding other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$port = this.getPort();
            Object other$port = other.getPort();
            if (this$port == null ? other$port == null : this$port.equals(other$port)) {
                Object this$defaultMaxTokens = this.getDefaultMaxTokens();
                Object other$defaultMaxTokens = other.getDefaultMaxTokens();
                if (this$defaultMaxTokens == null ? other$defaultMaxTokens == null : this$defaultMaxTokens.equals(other$defaultMaxTokens)) {
                    Object this$enabled = this.getEnabled();
                    Object other$enabled = other.getEnabled();
                    if (this$enabled == null ? other$enabled == null : this$enabled.equals(other$enabled)) {
                        Object this$id = this.getId();
                        Object other$id = other.getId();
                        if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                            Object this$name = this.getName();
                            Object other$name = other.getName();
                            if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                                Object this$ociUserId = this.getOciUserId();
                                Object other$ociUserId = other.getOciUserId();
                                if (this$ociUserId == null ? other$ociUserId == null : this$ociUserId.equals(other$ociUserId)) {
                                    Object this$ociRegion = this.getOciRegion();
                                    Object other$ociRegion = other.getOciRegion();
                                    if (this$ociRegion == null ? other$ociRegion == null : this$ociRegion.equals(other$ociRegion)) {
                                        Object this$openaiKeyId = this.getOpenaiKeyId();
                                        Object other$openaiKeyId = other.getOpenaiKeyId();
                                        if (this$openaiKeyId == null ? other$openaiKeyId == null : this$openaiKeyId.equals(other$openaiKeyId)) {
                                            Object this$allowedModelsJson = this.getAllowedModelsJson();
                                            Object other$allowedModelsJson = other.getAllowedModelsJson();
                                            if (this$allowedModelsJson == null
                                                ? other$allowedModelsJson == null
                                                : this$allowedModelsJson.equals(other$allowedModelsJson)) {
                                                Object this$status = this.getStatus();
                                                Object other$status = other.getStatus();
                                                if (this$status == null ? other$status == null : this$status.equals(other$status)) {
                                                    Object this$statusMessage = this.getStatusMessage();
                                                    Object other$statusMessage = other.getStatusMessage();
                                                    if (this$statusMessage == null
                                                        ? other$statusMessage == null
                                                        : this$statusMessage.equals(other$statusMessage)) {
                                                        Object this$createTime = this.getCreateTime();
                                                        Object other$createTime = other.getCreateTime();
                                                        if (this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime)) {
                                                            Object this$updateTime = this.getUpdateTime();
                                                            Object other$updateTime = other.getUpdateTime();
                                                            if (this$updateTime == null ? other$updateTime == null : this$updateTime.equals(other$updateTime)) {
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
        return other instanceof OciOpenaiPortBinding;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $port = this.getPort();
        result = result * 59 + ($port == null ? 43 : $port.hashCode());
        Object $defaultMaxTokens = this.getDefaultMaxTokens();
        result = result * 59 + ($defaultMaxTokens == null ? 43 : $defaultMaxTokens.hashCode());
        Object $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : $enabled.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $ociUserId = this.getOciUserId();
        result = result * 59 + ($ociUserId == null ? 43 : $ociUserId.hashCode());
        Object $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        Object $openaiKeyId = this.getOpenaiKeyId();
        result = result * 59 + ($openaiKeyId == null ? 43 : $openaiKeyId.hashCode());
        Object $allowedModelsJson = this.getAllowedModelsJson();
        result = result * 59 + ($allowedModelsJson == null ? 43 : $allowedModelsJson.hashCode());
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $statusMessage = this.getStatusMessage();
        result = result * 59 + ($statusMessage == null ? 43 : $statusMessage.hashCode());
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
        Object $lastUsed = this.getLastUsed();
        return result * 59 + ($lastUsed == null ? 43 : $lastUsed.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciOpenaiPortBinding(id="
            + this.getId()
            + ", name="
            + this.getName()
            + ", port="
            + this.getPort()
            + ", ociUserId="
            + this.getOciUserId()
            + ", ociRegion="
            + this.getOciRegion()
            + ", openaiKeyId="
            + this.getOpenaiKeyId()
            + ", defaultMaxTokens="
            + this.getDefaultMaxTokens()
            + ", allowedModelsJson="
            + this.getAllowedModelsJson()
            + ", enabled="
            + this.getEnabled()
            + ", status="
            + this.getStatus()
            + ", statusMessage="
            + this.getStatusMessage()
            + ", createTime="
            + this.getCreateTime()
            + ", updateTime="
            + this.getUpdateTime()
            + ", lastUsed="
            + this.getLastUsed()
            + ")";
    }
}
