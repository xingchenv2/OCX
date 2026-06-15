/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ocxworker.model.entity.CfCfg
 *  lombok.Generated
 */
package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="cf_cfg")
public class CfCfg {
    @TableId
    private String id;
    private String domain;
    private String zoneId;
    private String apiToken;
    private LocalDateTime createTime;

    @Generated
    public CfCfg() {
    }

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
    public void setId(String id) {
        this.id = id;
    }

    @Generated
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Generated
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    @Generated
    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
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
        if (!(o instanceof CfCfg)) {
            return false;
        }
        CfCfg other = (CfCfg)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$domain = this.getDomain();
        String other$domain = other.getDomain();
        if (this$domain == null ? other$domain != null : !this$domain.equals(other$domain)) {
            return false;
        }
        String this$zoneId = this.getZoneId();
        String other$zoneId = other.getZoneId();
        if (this$zoneId == null ? other$zoneId != null : !this$zoneId.equals(other$zoneId)) {
            return false;
        }
        String this$apiToken = this.getApiToken();
        String other$apiToken = other.getApiToken();
        if (this$apiToken == null ? other$apiToken != null : !this$apiToken.equals(other$apiToken)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CfCfg;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $domain = this.getDomain();
        result = result * 59 + ($domain == null ? 43 : $domain.hashCode());
        String $zoneId = this.getZoneId();
        result = result * 59 + ($zoneId == null ? 43 : $zoneId.hashCode());
        String $apiToken = this.getApiToken();
        result = result * 59 + ($apiToken == null ? 43 : $apiToken.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CfCfg(id=" + this.getId() + ", domain=" + this.getDomain() + ", zoneId=" + this.getZoneId() + ", apiToken=" + this.getApiToken() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

