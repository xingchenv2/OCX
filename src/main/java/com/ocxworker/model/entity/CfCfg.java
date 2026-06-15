package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("cf_cfg")
public class CfCfg {
    @TableId
    private String id;
    private String domain;
    private String zoneId;
    private String apiToken;
    private LocalDateTime createTime;

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getDomain() {
        return this.domain;
    }

    @Generated
    public String getZoneId() {
        return this.zoneId;
    }

    @Generated
    public String getApiToken() {
        return this.apiToken;
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
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    @Generated
    public void setZoneId(final String zoneId) {
        this.zoneId = zoneId;
    }

    @Generated
    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
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
        } else if (!(o instanceof CfCfg other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                Object this$domain = this.getDomain();
                Object other$domain = other.getDomain();
                if (this$domain == null ? other$domain == null : this$domain.equals(other$domain)) {
                    Object this$zoneId = this.getZoneId();
                    Object other$zoneId = other.getZoneId();
                    if (this$zoneId == null ? other$zoneId == null : this$zoneId.equals(other$zoneId)) {
                        Object this$apiToken = this.getApiToken();
                        Object other$apiToken = other.getApiToken();
                        if (this$apiToken == null ? other$apiToken == null : this$apiToken.equals(other$apiToken)) {
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
        return other instanceof CfCfg;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $domain = this.getDomain();
        result = result * 59 + ($domain == null ? 43 : $domain.hashCode());
        Object $zoneId = this.getZoneId();
        result = result * 59 + ($zoneId == null ? 43 : $zoneId.hashCode());
        Object $apiToken = this.getApiToken();
        result = result * 59 + ($apiToken == null ? 43 : $apiToken.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "CfCfg(id="
            + this.getId()
            + ", domain="
            + this.getDomain()
            + ", zoneId="
            + this.getZoneId()
            + ", apiToken="
            + this.getApiToken()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
