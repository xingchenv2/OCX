/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.params.CreateTaskParams
 *  jakarta.validation.constraints.NotBlank
 *  lombok.Generated
 */
package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class CreateTaskParams {
    @NotBlank(message="\u8bf7\u9009\u62e9\u79df\u6237")
    private @NotBlank(message="\u8bf7\u9009\u62e9\u79df\u6237") String userId;
    private String architecture = "ARM";
    private Double ocpus = 1.0;
    private Double memory = 6.0;
    private Integer disk = 50;
    private Integer vpusPerGB = 10;
    private Integer createNumbers = 1;
    private Integer interval = 60;
    private String rootPassword;
    private String operationSystem = "Ubuntu";
    private String customScript;
    private Boolean assignPublicIp = true;
    private Boolean assignIpv6 = false;
    private String ociRegion;

    @Generated
    public CreateTaskParams() {
    }

    @Generated
    public String getUserId() {
        return this.userId;
    }

    @Generated
    public String getArchitecture() {
        return this.architecture;
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
    public Integer getVpusPerGB() {
        return this.vpusPerGB;
    }

    @Generated
    public Integer getCreateNumbers() {
        return this.createNumbers;
    }

    @Generated
    public Integer getInterval() {
        return this.interval;
    }

    @Generated
    public String getRootPassword() {
        return this.rootPassword;
    }

    @Generated
    public String getOperationSystem() {
        return this.operationSystem;
    }

    @Generated
    public String getCustomScript() {
        return this.customScript;
    }

    @Generated
    public Boolean getAssignPublicIp() {
        return this.assignPublicIp;
    }

    @Generated
    public Boolean getAssignIpv6() {
        return this.assignIpv6;
    }

    @Generated
    public String getOciRegion() {
        return this.ociRegion;
    }

    @Generated
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
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
    public void setVpusPerGB(Integer vpusPerGB) {
        this.vpusPerGB = vpusPerGB;
    }

    @Generated
    public void setCreateNumbers(Integer createNumbers) {
        this.createNumbers = createNumbers;
    }

    @Generated
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Generated
    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    @Generated
    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem;
    }

    @Generated
    public void setCustomScript(String customScript) {
        this.customScript = customScript;
    }

    @Generated
    public void setAssignPublicIp(Boolean assignPublicIp) {
        this.assignPublicIp = assignPublicIp;
    }

    @Generated
    public void setAssignIpv6(Boolean assignIpv6) {
        this.assignIpv6 = assignIpv6;
    }

    @Generated
    public void setOciRegion(String ociRegion) {
        this.ociRegion = ociRegion;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateTaskParams)) {
            return false;
        }
        CreateTaskParams other = (CreateTaskParams)o;
        if (!other.canEqual((Object)this)) {
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
        Integer this$vpusPerGB = this.getVpusPerGB();
        Integer other$vpusPerGB = other.getVpusPerGB();
        if (this$vpusPerGB == null ? other$vpusPerGB != null : !((Object)this$vpusPerGB).equals(other$vpusPerGB)) {
            return false;
        }
        Integer this$createNumbers = this.getCreateNumbers();
        Integer other$createNumbers = other.getCreateNumbers();
        if (this$createNumbers == null ? other$createNumbers != null : !((Object)this$createNumbers).equals(other$createNumbers)) {
            return false;
        }
        Integer this$interval = this.getInterval();
        Integer other$interval = other.getInterval();
        if (this$interval == null ? other$interval != null : !((Object)this$interval).equals(other$interval)) {
            return false;
        }
        Boolean this$assignPublicIp = this.getAssignPublicIp();
        Boolean other$assignPublicIp = other.getAssignPublicIp();
        if (this$assignPublicIp == null ? other$assignPublicIp != null : !((Object)this$assignPublicIp).equals(other$assignPublicIp)) {
            return false;
        }
        Boolean this$assignIpv6 = this.getAssignIpv6();
        Boolean other$assignIpv6 = other.getAssignIpv6();
        if (this$assignIpv6 == null ? other$assignIpv6 != null : !((Object)this$assignIpv6).equals(other$assignIpv6)) {
            return false;
        }
        String this$userId = this.getUserId();
        String other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) {
            return false;
        }
        String this$architecture = this.getArchitecture();
        String other$architecture = other.getArchitecture();
        if (this$architecture == null ? other$architecture != null : !this$architecture.equals(other$architecture)) {
            return false;
        }
        String this$rootPassword = this.getRootPassword();
        String other$rootPassword = other.getRootPassword();
        if (this$rootPassword == null ? other$rootPassword != null : !this$rootPassword.equals(other$rootPassword)) {
            return false;
        }
        String this$operationSystem = this.getOperationSystem();
        String other$operationSystem = other.getOperationSystem();
        if (this$operationSystem == null ? other$operationSystem != null : !this$operationSystem.equals(other$operationSystem)) {
            return false;
        }
        String this$customScript = this.getCustomScript();
        String other$customScript = other.getCustomScript();
        if (this$customScript == null ? other$customScript != null : !this$customScript.equals(other$customScript)) {
            return false;
        }
        String this$ociRegion = this.getOciRegion();
        String other$ociRegion = other.getOciRegion();
        return !(this$ociRegion == null ? other$ociRegion != null : !this$ociRegion.equals(other$ociRegion));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CreateTaskParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $ocpus = this.getOcpus();
        result = result * 59 + ($ocpus == null ? 43 : ((Object)$ocpus).hashCode());
        Double $memory = this.getMemory();
        result = result * 59 + ($memory == null ? 43 : ((Object)$memory).hashCode());
        Integer $disk = this.getDisk();
        result = result * 59 + ($disk == null ? 43 : ((Object)$disk).hashCode());
        Integer $vpusPerGB = this.getVpusPerGB();
        result = result * 59 + ($vpusPerGB == null ? 43 : ((Object)$vpusPerGB).hashCode());
        Integer $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : ((Object)$createNumbers).hashCode());
        Integer $interval = this.getInterval();
        result = result * 59 + ($interval == null ? 43 : ((Object)$interval).hashCode());
        Boolean $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : ((Object)$assignPublicIp).hashCode());
        Boolean $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : ((Object)$assignIpv6).hashCode());
        String $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        String $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        String $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        String $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        String $customScript = this.getCustomScript();
        result = result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
        String $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CreateTaskParams(userId=" + this.getUserId() + ", architecture=" + this.getArchitecture() + ", ocpus=" + this.getOcpus() + ", memory=" + this.getMemory() + ", disk=" + this.getDisk() + ", vpusPerGB=" + this.getVpusPerGB() + ", createNumbers=" + this.getCreateNumbers() + ", interval=" + this.getInterval() + ", rootPassword=" + this.getRootPassword() + ", operationSystem=" + this.getOperationSystem() + ", customScript=" + this.getCustomScript() + ", assignPublicIp=" + this.getAssignPublicIp() + ", assignIpv6=" + this.getAssignIpv6() + ", ociRegion=" + this.getOciRegion() + ")";
    }
}

