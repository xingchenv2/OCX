package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("ip_data")
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
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setIp(final String ip) {
        this.ip = ip;
    }

    @Generated
    public void setCountry(final String country) {
        this.country = country;
    }

    @Generated
    public void setArea(final String area) {
        this.area = area;
    }

    @Generated
    public void setCity(final String city) {
        this.city = city;
    }

    @Generated
    public void setOrg(final String org) {
        this.org = org;
    }

    @Generated
    public void setAsn(final String asn) {
        this.asn = asn;
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public void setLat(final Double lat) {
        this.lat = lat;
    }

    @Generated
    public void setLng(final Double lng) {
        this.lng = lng;
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
        } else if (!(o instanceof IpData other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$lat = this.getLat();
            Object other$lat = other.getLat();
            if (this$lat == null ? other$lat == null : this$lat.equals(other$lat)) {
                Object this$lng = this.getLng();
                Object other$lng = other.getLng();
                if (this$lng == null ? other$lng == null : this$lng.equals(other$lng)) {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                        Object this$ip = this.getIp();
                        Object other$ip = other.getIp();
                        if (this$ip == null ? other$ip == null : this$ip.equals(other$ip)) {
                            Object this$country = this.getCountry();
                            Object other$country = other.getCountry();
                            if (this$country == null ? other$country == null : this$country.equals(other$country)) {
                                Object this$area = this.getArea();
                                Object other$area = other.getArea();
                                if (this$area == null ? other$area == null : this$area.equals(other$area)) {
                                    Object this$city = this.getCity();
                                    Object other$city = other.getCity();
                                    if (this$city == null ? other$city == null : this$city.equals(other$city)) {
                                        Object this$org = this.getOrg();
                                        Object other$org = other.getOrg();
                                        if (this$org == null ? other$org == null : this$org.equals(other$org)) {
                                            Object this$asn = this.getAsn();
                                            Object other$asn = other.getAsn();
                                            if (this$asn == null ? other$asn == null : this$asn.equals(other$asn)) {
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
        return other instanceof IpData;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $lat = this.getLat();
        result = result * 59 + ($lat == null ? 43 : $lat.hashCode());
        Object $lng = this.getLng();
        result = result * 59 + ($lng == null ? 43 : $lng.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $ip = this.getIp();
        result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
        Object $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        Object $area = this.getArea();
        result = result * 59 + ($area == null ? 43 : $area.hashCode());
        Object $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        Object $org = this.getOrg();
        result = result * 59 + ($org == null ? 43 : $org.hashCode());
        Object $asn = this.getAsn();
        result = result * 59 + ($asn == null ? 43 : $asn.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "IpData(id="
            + this.getId()
            + ", ip="
            + this.getIp()
            + ", country="
            + this.getCountry()
            + ", area="
            + this.getArea()
            + ", city="
            + this.getCity()
            + ", org="
            + this.getOrg()
            + ", asn="
            + this.getAsn()
            + ", type="
            + this.getType()
            + ", lat="
            + this.getLat()
            + ", lng="
            + this.getLng()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
