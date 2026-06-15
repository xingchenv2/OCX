/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.dto.InstanceDetailDTO
 *  lombok.Generated
 */
package com.ocxworker.model.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class InstanceDetailDTO {
    private String taskId;
    private String username;
    private String region;
    private String architecture;
    private Integer createNumbers;
    private String instanceId;
    private String instanceName;
    private String shape;
    private Double ocpus;
    private Double memory;
    private Integer disk;
    private String publicIp;
    private String privateIp;
    private String ipv6Address;
    private String image;
    private String rootPassword;
    private boolean success;
    private boolean die;
    private boolean noShape;
    private boolean noPubVcn;
    private boolean outOfCapacity;
    private boolean bootVolumeQuotaExceeded;
    private String failureHint;
    private String resolvedTargetShape;
    private List<String> adsExcludedNoShape = new ArrayList();
    private boolean allAdsExcludedNoShape;

    @Generated
    public InstanceDetailDTO() {
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getRegion() {
        return this.region;
    }

    @Generated
    public String getArchitecture() {
        return this.architecture;
    }

    @Generated
    public Integer getCreateNumbers() {
        return this.createNumbers;
    }

    @Generated
    public String getInstanceId() {
        return this.instanceId;
    }

    @Generated
    public String getInstanceName() {
        return this.instanceName;
    }

    @Generated
    public String getShape() {
        return this.shape;
    }

    @Generated
    public Double getOcpus() {
        return this.ocpus;
    }

    @Generated
    public Double getMemory() {
        return this.memory;
    }

    @Generated
    public Integer getDisk() {
        return this.disk;
    }

    @Generated
    public String getPublicIp() {
        return this.publicIp;
    }

    @Generated
    public String getPrivateIp() {
        return this.privateIp;
    }

    @Generated
    public String getIpv6Address() {
        return this.ipv6Address;
    }

    @Generated
    public String getImage() {
        return this.image;
    }

    @Generated
    public String getRootPassword() {
        return this.rootPassword;
    }

    @Generated
    public boolean isSuccess() {
        return this.success;
    }

    @Generated
    public boolean isDie() {
        return this.die;
    }

    @Generated
    public boolean isNoShape() {
        return this.noShape;
    }

    @Generated
    public boolean isNoPubVcn() {
        return this.noPubVcn;
    }

    @Generated
    public boolean isOutOfCapacity() {
        return this.outOfCapacity;
    }

    @Generated
    public boolean isBootVolumeQuotaExceeded() {
        return this.bootVolumeQuotaExceeded;
    }

    @Generated
    public String getFailureHint() {
        return this.failureHint;
    }

    @Generated
    public String getResolvedTargetShape() {
        return this.resolvedTargetShape;
    }

    @Generated
    public List<String> getAdsExcludedNoShape() {
        return this.adsExcludedNoShape;
    }

    @Generated
    public boolean isAllAdsExcludedNoShape() {
        return this.allAdsExcludedNoShape;
    }

    @Generated
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated
    public void setRegion(String region) {
        this.region = region;
    }

    @Generated
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    @Generated
    public void setCreateNumbers(Integer createNumbers) {
        this.createNumbers = createNumbers;
    }

    @Generated
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Generated
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Generated
    public void setShape(String shape) {
        this.shape = shape;
    }

    @Generated
    public void setOcpus(Double ocpus) {
        this.ocpus = ocpus;
    }

    @Generated
    public void setMemory(Double memory) {
        this.memory = memory;
    }

    @Generated
    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    @Generated
    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    @Generated
    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    @Generated
    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    @Generated
    public void setImage(String image) {
        this.image = image;
    }

    @Generated
    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    @Generated
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Generated
    public void setDie(boolean die) {
        this.die = die;
    }

    @Generated
    public void setNoShape(boolean noShape) {
        this.noShape = noShape;
    }

    @Generated
    public void setNoPubVcn(boolean noPubVcn) {
        this.noPubVcn = noPubVcn;
    }

    @Generated
    public void setOutOfCapacity(boolean outOfCapacity) {
        this.outOfCapacity = outOfCapacity;
    }

    @Generated
    public void setBootVolumeQuotaExceeded(boolean bootVolumeQuotaExceeded) {
        this.bootVolumeQuotaExceeded = bootVolumeQuotaExceeded;
    }

    @Generated
    public void setFailureHint(String failureHint) {
        this.failureHint = failureHint;
    }

    @Generated
    public void setResolvedTargetShape(String resolvedTargetShape) {
        this.resolvedTargetShape = resolvedTargetShape;
    }

    @Generated
    public void setAdsExcludedNoShape(List<String> adsExcludedNoShape) {
        this.adsExcludedNoShape = adsExcludedNoShape;
    }

    @Generated
    public void setAllAdsExcludedNoShape(boolean allAdsExcludedNoShape) {
        this.allAdsExcludedNoShape = allAdsExcludedNoShape;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InstanceDetailDTO)) {
            return false;
        }
        InstanceDetailDTO other = (InstanceDetailDTO)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        if (this.isDie() != other.isDie()) {
            return false;
        }
        if (this.isNoShape() != other.isNoShape()) {
            return false;
        }
        if (this.isNoPubVcn() != other.isNoPubVcn()) {
            return false;
        }
        if (this.isOutOfCapacity() != other.isOutOfCapacity()) {
            return false;
        }
        if (this.isBootVolumeQuotaExceeded() != other.isBootVolumeQuotaExceeded()) {
            return false;
        }
        if (this.isAllAdsExcludedNoShape() != other.isAllAdsExcludedNoShape()) {
            return false;
        }
        Integer this$createNumbers = this.getCreateNumbers();
        Integer other$createNumbers = other.getCreateNumbers();
        if (this$createNumbers == null ? other$createNumbers != null : !((Object)this$createNumbers).equals(other$createNumbers)) {
            return false;
        }
        Double this$ocpus = this.getOcpus();
        Double other$ocpus = other.getOcpus();
        if (this$ocpus == null ? other$ocpus != null : !((Object)this$ocpus).equals(other$ocpus)) {
            return false;
        }
        Double this$memory = this.getMemory();
        Double other$memory = other.getMemory();
        if (this$memory == null ? other$memory != null : !((Object)this$memory).equals(other$memory)) {
            return false;
        }
        Integer this$disk = this.getDisk();
        Integer other$disk = other.getDisk();
        if (this$disk == null ? other$disk != null : !((Object)this$disk).equals(other$disk)) {
            return false;
        }
        String this$taskId = this.getTaskId();
        String other$taskId = other.getTaskId();
        if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$region = this.getRegion();
        String other$region = other.getRegion();
        if (this$region == null ? other$region != null : !this$region.equals(other$region)) {
            return false;
        }
        String this$architecture = this.getArchitecture();
        String other$architecture = other.getArchitecture();
        if (this$architecture == null ? other$architecture != null : !this$architecture.equals(other$architecture)) {
            return false;
        }
        String this$instanceId = this.getInstanceId();
        String other$instanceId = other.getInstanceId();
        if (this$instanceId == null ? other$instanceId != null : !this$instanceId.equals(other$instanceId)) {
            return false;
        }
        String this$instanceName = this.getInstanceName();
        String other$instanceName = other.getInstanceName();
        if (this$instanceName == null ? other$instanceName != null : !this$instanceName.equals(other$instanceName)) {
            return false;
        }
        String this$shape = this.getShape();
        String other$shape = other.getShape();
        if (this$shape == null ? other$shape != null : !this$shape.equals(other$shape)) {
            return false;
        }
        String this$publicIp = this.getPublicIp();
        String other$publicIp = other.getPublicIp();
        if (this$publicIp == null ? other$publicIp != null : !this$publicIp.equals(other$publicIp)) {
            return false;
        }
        String this$privateIp = this.getPrivateIp();
        String other$privateIp = other.getPrivateIp();
        if (this$privateIp == null ? other$privateIp != null : !this$privateIp.equals(other$privateIp)) {
            return false;
        }
        String this$ipv6Address = this.getIpv6Address();
        String other$ipv6Address = other.getIpv6Address();
        if (this$ipv6Address == null ? other$ipv6Address != null : !this$ipv6Address.equals(other$ipv6Address)) {
            return false;
        }
        String this$image = this.getImage();
        String other$image = other.getImage();
        if (this$image == null ? other$image != null : !this$image.equals(other$image)) {
            return false;
        }
        String this$rootPassword = this.getRootPassword();
        String other$rootPassword = other.getRootPassword();
        if (this$rootPassword == null ? other$rootPassword != null : !this$rootPassword.equals(other$rootPassword)) {
            return false;
        }
        String this$failureHint = this.getFailureHint();
        String other$failureHint = other.getFailureHint();
        if (this$failureHint == null ? other$failureHint != null : !this$failureHint.equals(other$failureHint)) {
            return false;
        }
        String this$resolvedTargetShape = this.getResolvedTargetShape();
        String other$resolvedTargetShape = other.getResolvedTargetShape();
        if (this$resolvedTargetShape == null ? other$resolvedTargetShape != null : !this$resolvedTargetShape.equals(other$resolvedTargetShape)) {
            return false;
        }
        List this$adsExcludedNoShape = this.getAdsExcludedNoShape();
        List other$adsExcludedNoShape = other.getAdsExcludedNoShape();
        return !(this$adsExcludedNoShape == null ? other$adsExcludedNoShape != null : !((Object)this$adsExcludedNoShape).equals(other$adsExcludedNoShape));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof InstanceDetailDTO;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        result = result * 59 + (this.isDie() ? 79 : 97);
        result = result * 59 + (this.isNoShape() ? 79 : 97);
        result = result * 59 + (this.isNoPubVcn() ? 79 : 97);
        result = result * 59 + (this.isOutOfCapacity() ? 79 : 97);
        result = result * 59 + (this.isBootVolumeQuotaExceeded() ? 79 : 97);
        result = result * 59 + (this.isAllAdsExcludedNoShape() ? 79 : 97);
        Integer $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : ((Object)$createNumbers).hashCode());
        Double $ocpus = this.getOcpus();
        result = result * 59 + ($ocpus == null ? 43 : ((Object)$ocpus).hashCode());
        Double $memory = this.getMemory();
        result = result * 59 + ($memory == null ? 43 : ((Object)$memory).hashCode());
        Integer $disk = this.getDisk();
        result = result * 59 + ($disk == null ? 43 : ((Object)$disk).hashCode());
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $region = this.getRegion();
        result = result * 59 + ($region == null ? 43 : $region.hashCode());
        String $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        String $instanceId = this.getInstanceId();
        result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
        String $instanceName = this.getInstanceName();
        result = result * 59 + ($instanceName == null ? 43 : $instanceName.hashCode());
        String $shape = this.getShape();
        result = result * 59 + ($shape == null ? 43 : $shape.hashCode());
        String $publicIp = this.getPublicIp();
        result = result * 59 + ($publicIp == null ? 43 : $publicIp.hashCode());
        String $privateIp = this.getPrivateIp();
        result = result * 59 + ($privateIp == null ? 43 : $privateIp.hashCode());
        String $ipv6Address = this.getIpv6Address();
        result = result * 59 + ($ipv6Address == null ? 43 : $ipv6Address.hashCode());
        String $image = this.getImage();
        result = result * 59 + ($image == null ? 43 : $image.hashCode());
        String $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        String $failureHint = this.getFailureHint();
        result = result * 59 + ($failureHint == null ? 43 : $failureHint.hashCode());
        String $resolvedTargetShape = this.getResolvedTargetShape();
        result = result * 59 + ($resolvedTargetShape == null ? 43 : $resolvedTargetShape.hashCode());
        List $adsExcludedNoShape = this.getAdsExcludedNoShape();
        result = result * 59 + ($adsExcludedNoShape == null ? 43 : ((Object)$adsExcludedNoShape).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "InstanceDetailDTO(taskId=" + this.getTaskId() + ", username=" + this.getUsername() + ", region=" + this.getRegion() + ", architecture=" + this.getArchitecture() + ", createNumbers=" + this.getCreateNumbers() + ", instanceId=" + this.getInstanceId() + ", instanceName=" + this.getInstanceName() + ", shape=" + this.getShape() + ", ocpus=" + this.getOcpus() + ", memory=" + this.getMemory() + ", disk=" + this.getDisk() + ", publicIp=" + this.getPublicIp() + ", privateIp=" + this.getPrivateIp() + ", ipv6Address=" + this.getIpv6Address() + ", image=" + this.getImage() + ", rootPassword=" + this.getRootPassword() + ", success=" + this.isSuccess() + ", die=" + this.isDie() + ", noShape=" + this.isNoShape() + ", noPubVcn=" + this.isNoPubVcn() + ", outOfCapacity=" + this.isOutOfCapacity() + ", bootVolumeQuotaExceeded=" + this.isBootVolumeQuotaExceeded() + ", failureHint=" + this.getFailureHint() + ", resolvedTargetShape=" + this.getResolvedTargetShape() + ", adsExcludedNoShape=" + String.valueOf(this.getAdsExcludedNoShape()) + ", allAdsExcludedNoShape=" + this.isAllAdsExcludedNoShape() + ")";
    }
}

