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
    private List<String> adsExcludedNoShape = new ArrayList<>();
    private boolean allAdsExcludedNoShape;

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
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setUsername(final String username) {
        this.username = username;
    }

    @Generated
    public void setRegion(final String region) {
        this.region = region;
    }

    @Generated
    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    @Generated
    public void setCreateNumbers(final Integer createNumbers) {
        this.createNumbers = createNumbers;
    }

    @Generated
    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    @Generated
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }

    @Generated
    public void setShape(final String shape) {
        this.shape = shape;
    }

    @Generated
    public void setOcpus(final Double ocpus) {
        this.ocpus = ocpus;
    }

    @Generated
    public void setMemory(final Double memory) {
        this.memory = memory;
    }

    @Generated
    public void setDisk(final Integer disk) {
        this.disk = disk;
    }

    @Generated
    public void setPublicIp(final String publicIp) {
        this.publicIp = publicIp;
    }

    @Generated
    public void setPrivateIp(final String privateIp) {
        this.privateIp = privateIp;
    }

    @Generated
    public void setIpv6Address(final String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    @Generated
    public void setImage(final String image) {
        this.image = image;
    }

    @Generated
    public void setRootPassword(final String rootPassword) {
        this.rootPassword = rootPassword;
    }

    @Generated
    public void setSuccess(final boolean success) {
        this.success = success;
    }

    @Generated
    public void setDie(final boolean die) {
        this.die = die;
    }

    @Generated
    public void setNoShape(final boolean noShape) {
        this.noShape = noShape;
    }

    @Generated
    public void setNoPubVcn(final boolean noPubVcn) {
        this.noPubVcn = noPubVcn;
    }

    @Generated
    public void setOutOfCapacity(final boolean outOfCapacity) {
        this.outOfCapacity = outOfCapacity;
    }

    @Generated
    public void setBootVolumeQuotaExceeded(final boolean bootVolumeQuotaExceeded) {
        this.bootVolumeQuotaExceeded = bootVolumeQuotaExceeded;
    }

    @Generated
    public void setFailureHint(final String failureHint) {
        this.failureHint = failureHint;
    }

    @Generated
    public void setResolvedTargetShape(final String resolvedTargetShape) {
        this.resolvedTargetShape = resolvedTargetShape;
    }

    @Generated
    public void setAdsExcludedNoShape(final List<String> adsExcludedNoShape) {
        this.adsExcludedNoShape = adsExcludedNoShape;
    }

    @Generated
    public void setAllAdsExcludedNoShape(final boolean allAdsExcludedNoShape) {
        this.allAdsExcludedNoShape = allAdsExcludedNoShape;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof InstanceDetailDTO other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.isSuccess() != other.isSuccess()) {
            return false;
        } else if (this.isDie() != other.isDie()) {
            return false;
        } else if (this.isNoShape() != other.isNoShape()) {
            return false;
        } else if (this.isNoPubVcn() != other.isNoPubVcn()) {
            return false;
        } else if (this.isOutOfCapacity() != other.isOutOfCapacity()) {
            return false;
        } else if (this.isBootVolumeQuotaExceeded() != other.isBootVolumeQuotaExceeded()) {
            return false;
        } else if (this.isAllAdsExcludedNoShape() != other.isAllAdsExcludedNoShape()) {
            return false;
        } else {
            Object this$createNumbers = this.getCreateNumbers();
            Object other$createNumbers = other.getCreateNumbers();
            if (this$createNumbers == null ? other$createNumbers == null : this$createNumbers.equals(other$createNumbers)) {
                Object this$ocpus = this.getOcpus();
                Object other$ocpus = other.getOcpus();
                if (this$ocpus == null ? other$ocpus == null : this$ocpus.equals(other$ocpus)) {
                    Object this$memory = this.getMemory();
                    Object other$memory = other.getMemory();
                    if (this$memory == null ? other$memory == null : this$memory.equals(other$memory)) {
                        Object this$disk = this.getDisk();
                        Object other$disk = other.getDisk();
                        if (this$disk == null ? other$disk == null : this$disk.equals(other$disk)) {
                            Object this$taskId = this.getTaskId();
                            Object other$taskId = other.getTaskId();
                            if (this$taskId == null ? other$taskId == null : this$taskId.equals(other$taskId)) {
                                Object this$username = this.getUsername();
                                Object other$username = other.getUsername();
                                if (this$username == null ? other$username == null : this$username.equals(other$username)) {
                                    Object this$region = this.getRegion();
                                    Object other$region = other.getRegion();
                                    if (this$region == null ? other$region == null : this$region.equals(other$region)) {
                                        Object this$architecture = this.getArchitecture();
                                        Object other$architecture = other.getArchitecture();
                                        if (this$architecture == null ? other$architecture == null : this$architecture.equals(other$architecture)) {
                                            Object this$instanceId = this.getInstanceId();
                                            Object other$instanceId = other.getInstanceId();
                                            if (this$instanceId == null ? other$instanceId == null : this$instanceId.equals(other$instanceId)) {
                                                Object this$instanceName = this.getInstanceName();
                                                Object other$instanceName = other.getInstanceName();
                                                if (this$instanceName == null ? other$instanceName == null : this$instanceName.equals(other$instanceName)) {
                                                    Object this$shape = this.getShape();
                                                    Object other$shape = other.getShape();
                                                    if (this$shape == null ? other$shape == null : this$shape.equals(other$shape)) {
                                                        Object this$publicIp = this.getPublicIp();
                                                        Object other$publicIp = other.getPublicIp();
                                                        if (this$publicIp == null ? other$publicIp == null : this$publicIp.equals(other$publicIp)) {
                                                            Object this$privateIp = this.getPrivateIp();
                                                            Object other$privateIp = other.getPrivateIp();
                                                            if (this$privateIp == null ? other$privateIp == null : this$privateIp.equals(other$privateIp)) {
                                                                Object this$ipv6Address = this.getIpv6Address();
                                                                Object other$ipv6Address = other.getIpv6Address();
                                                                if (this$ipv6Address == null
                                                                    ? other$ipv6Address == null
                                                                    : this$ipv6Address.equals(other$ipv6Address)) {
                                                                    Object this$image = this.getImage();
                                                                    Object other$image = other.getImage();
                                                                    if (this$image == null ? other$image == null : this$image.equals(other$image)) {
                                                                        Object this$rootPassword = this.getRootPassword();
                                                                        Object other$rootPassword = other.getRootPassword();
                                                                        if (this$rootPassword == null
                                                                            ? other$rootPassword == null
                                                                            : this$rootPassword.equals(other$rootPassword)) {
                                                                            Object this$failureHint = this.getFailureHint();
                                                                            Object other$failureHint = other.getFailureHint();
                                                                            if (this$failureHint == null
                                                                                ? other$failureHint == null
                                                                                : this$failureHint.equals(other$failureHint)) {
                                                                                Object this$resolvedTargetShape = this.getResolvedTargetShape();
                                                                                Object other$resolvedTargetShape = other.getResolvedTargetShape();
                                                                                if (this$resolvedTargetShape == null
                                                                                    ? other$resolvedTargetShape == null
                                                                                    : this$resolvedTargetShape.equals(other$resolvedTargetShape)) {
                                                                                    Object this$adsExcludedNoShape = this.getAdsExcludedNoShape();
                                                                                    Object other$adsExcludedNoShape = other.getAdsExcludedNoShape();
                                                                                    return this$adsExcludedNoShape == null
                                                                                        ? other$adsExcludedNoShape == null
                                                                                        : this$adsExcludedNoShape.equals(other$adsExcludedNoShape);
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
        return other instanceof InstanceDetailDTO;
    }

    @Generated
    @Override
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
        Object $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : $createNumbers.hashCode());
        Object $ocpus = this.getOcpus();
        result = result * 59 + ($ocpus == null ? 43 : $ocpus.hashCode());
        Object $memory = this.getMemory();
        result = result * 59 + ($memory == null ? 43 : $memory.hashCode());
        Object $disk = this.getDisk();
        result = result * 59 + ($disk == null ? 43 : $disk.hashCode());
        Object $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $region = this.getRegion();
        result = result * 59 + ($region == null ? 43 : $region.hashCode());
        Object $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        Object $instanceId = this.getInstanceId();
        result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
        Object $instanceName = this.getInstanceName();
        result = result * 59 + ($instanceName == null ? 43 : $instanceName.hashCode());
        Object $shape = this.getShape();
        result = result * 59 + ($shape == null ? 43 : $shape.hashCode());
        Object $publicIp = this.getPublicIp();
        result = result * 59 + ($publicIp == null ? 43 : $publicIp.hashCode());
        Object $privateIp = this.getPrivateIp();
        result = result * 59 + ($privateIp == null ? 43 : $privateIp.hashCode());
        Object $ipv6Address = this.getIpv6Address();
        result = result * 59 + ($ipv6Address == null ? 43 : $ipv6Address.hashCode());
        Object $image = this.getImage();
        result = result * 59 + ($image == null ? 43 : $image.hashCode());
        Object $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        Object $failureHint = this.getFailureHint();
        result = result * 59 + ($failureHint == null ? 43 : $failureHint.hashCode());
        Object $resolvedTargetShape = this.getResolvedTargetShape();
        result = result * 59 + ($resolvedTargetShape == null ? 43 : $resolvedTargetShape.hashCode());
        Object $adsExcludedNoShape = this.getAdsExcludedNoShape();
        return result * 59 + ($adsExcludedNoShape == null ? 43 : $adsExcludedNoShape.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "InstanceDetailDTO(taskId="
            + this.getTaskId()
            + ", username="
            + this.getUsername()
            + ", region="
            + this.getRegion()
            + ", architecture="
            + this.getArchitecture()
            + ", createNumbers="
            + this.getCreateNumbers()
            + ", instanceId="
            + this.getInstanceId()
            + ", instanceName="
            + this.getInstanceName()
            + ", shape="
            + this.getShape()
            + ", ocpus="
            + this.getOcpus()
            + ", memory="
            + this.getMemory()
            + ", disk="
            + this.getDisk()
            + ", publicIp="
            + this.getPublicIp()
            + ", privateIp="
            + this.getPrivateIp()
            + ", ipv6Address="
            + this.getIpv6Address()
            + ", image="
            + this.getImage()
            + ", rootPassword="
            + this.getRootPassword()
            + ", success="
            + this.isSuccess()
            + ", die="
            + this.isDie()
            + ", noShape="
            + this.isNoShape()
            + ", noPubVcn="
            + this.isNoPubVcn()
            + ", outOfCapacity="
            + this.isOutOfCapacity()
            + ", bootVolumeQuotaExceeded="
            + this.isBootVolumeQuotaExceeded()
            + ", failureHint="
            + this.getFailureHint()
            + ", resolvedTargetShape="
            + this.getResolvedTargetShape()
            + ", adsExcludedNoShape="
            + this.getAdsExcludedNoShape()
            + ", allAdsExcludedNoShape="
            + this.isAllAdsExcludedNoShape()
            + ")";
    }
}
