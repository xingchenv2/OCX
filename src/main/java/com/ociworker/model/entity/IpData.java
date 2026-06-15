/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ociworker.model.entity.IpData
 *  lombok.Generated
 */
package com.ociworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="ip_data")
public class IpData {
    @TableId
    private String id;
    private String ip;
    private String country;
    private String area;
    private String city;
    private String org;
    private String asn;
    private String type;
    private Double lat;
    private Double lng;
    private LocalDateTime createTime;

    @Generated
    public IpData() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getIp() {
        return this.ip;
    }

    @Generated
    public String getCountry() {
        return this.country;
    }

    @Generated
    public String getArea() {
        return this.area;
    }

    @Generated
    public String getCity() {
        return this.city;
    }

    @Generated
    public String getOrg() {
        return this.org;
    }

    @Generated
    public String getAsn() {
        return this.asn;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public Double getLat() {
        return this.lat;
    }

    @Generated
    public Double getLng() {
        return this.lng;
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
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Generated
    public void setCountry(String country) {
        this.country = country;
    }

    @Generated
    public void setArea(String area) {
        this.area = area;
    }

    @Generated
    public void setCity(String city) {
        this.city = city;
    }

    @Generated
    public void setOrg(String org) {
        this.org = org;
    }

    @Generated
    public void setAsn(String asn) {
        this.asn = asn;
    }

    @Generated
    public void setType(String type) {
        this.type = type;
    }

    @Generated
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Generated
    public void setLng(Double lng) {
        this.lng = lng;
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
        if (!(o instanceof IpData)) {
            return false;
        }
        IpData other = (IpData)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Double this$lat = this.getLat();
        Double other$lat = other.getLat();
        if (this$lat == null ? other$lat != null : !((Object)this$lat).equals(other$lat)) {
            return false;
        }
        Double this$lng = this.getLng();
        Double other$lng = other.getLng();
        if (this$lng == null ? other$lng != null : !((Object)this$lng).equals(other$lng)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$ip = this.getIp();
        String other$ip = other.getIp();
        if (this$ip == null ? other$ip != null : !this$ip.equals(other$ip)) {
            return false;
        }
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        String this$area = this.getArea();
        String other$area = other.getArea();
        if (this$area == null ? other$area != null : !this$area.equals(other$area)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$org = this.getOrg();
        String other$org = other.getOrg();
        if (this$org == null ? other$org != null : !this$org.equals(other$org)) {
            return false;
        }
        String this$asn = this.getAsn();
        String other$asn = other.getAsn();
        if (this$asn == null ? other$asn != null : !this$asn.equals(other$asn)) {
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
        return other instanceof IpData;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $lat = this.getLat();
        result = result * 59 + ($lat == null ? 43 : ((Object)$lat).hashCode());
        Double $lng = this.getLng();
        result = result * 59 + ($lng == null ? 43 : ((Object)$lng).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $ip = this.getIp();
        result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        String $area = this.getArea();
        result = result * 59 + ($area == null ? 43 : $area.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $org = this.getOrg();
        result = result * 59 + ($org == null ? 43 : $org.hashCode());
        String $asn = this.getAsn();
        result = result * 59 + ($asn == null ? 43 : $asn.hashCode());
        String $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "IpData(id=" + this.getId() + ", ip=" + this.getIp() + ", country=" + this.getCountry() + ", area=" + this.getArea() + ", city=" + this.getCity() + ", org=" + this.getOrg() + ", asn=" + this.getAsn() + ", type=" + this.getType() + ", lat=" + this.getLat() + ", lng=" + this.getLng() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

