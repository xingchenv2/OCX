/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.dto.SysUserDTO$SysUserDTOBuilder
 *  lombok.Generated
 */
package com.ocxworker.model.dto;

import com.ocxworker.model.dto.SysUserDTO;
import java.util.Set;
import lombok.Generated;

public class SysUserDTO {
    private String taskId;
    private String username;
    private String architecture;
    private Double ocpus;
    private Double memory;
    private Integer disk;
    private Integer vpusPerGB;
    private Integer createNumbers;
    private String rootPassword;
    private String operationSystem;
    private String customScript;
    private Boolean assignPublicIp;
    private Boolean assignIpv6;
    private Integer instanceDisplayOrdinal;
    private Set<String> excludedAvailabilityDomains;
    private OciCfg ociCfg;

    @Generated
    public static SysUserDTOBuilder builder() {
        return new SysUserDTOBuilder();
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
    public Integer getInstanceDisplayOrdinal() {
        return this.instanceDisplayOrdinal;
    }

    @Generated
    public Set<String> getExcludedAvailabilityDomains() {
        return this.excludedAvailabilityDomains;
    }

    @Generated
    public OciCfg getOciCfg() {
        return this.ociCfg;
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
    public void setInstanceDisplayOrdinal(Integer instanceDisplayOrdinal) {
        this.instanceDisplayOrdinal = instanceDisplayOrdinal;
    }

    @Generated
    public void setExcludedAvailabilityDomains(Set<String> excludedAvailabilityDomains) {
        this.excludedAvailabilityDomains = excludedAvailabilityDomains;
    }

    @Generated
    public void setOciCfg(OciCfg ociCfg) {
        this.ociCfg = ociCfg;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SysUserDTO)) {
            return false;
        }
        SysUserDTO other = (SysUserDTO)o;
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
        Integer this$instanceDisplayOrdinal = this.getInstanceDisplayOrdinal();
        Integer other$instanceDisplayOrdinal = other.getInstanceDisplayOrdinal();
        if (this$instanceDisplayOrdinal == null ? other$instanceDisplayOrdinal != null : !((Object)this$instanceDisplayOrdinal).equals(other$instanceDisplayOrdinal)) {
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
        Set this$excludedAvailabilityDomains = this.getExcludedAvailabilityDomains();
        Set other$excludedAvailabilityDomains = other.getExcludedAvailabilityDomains();
        if (this$excludedAvailabilityDomains == null ? other$excludedAvailabilityDomains != null : !((Object)this$excludedAvailabilityDomains).equals(other$excludedAvailabilityDomains)) {
            return false;
        }
        OciCfg this$ociCfg = this.getOciCfg();
        OciCfg other$ociCfg = other.getOciCfg();
        return !(this$ociCfg == null ? other$ociCfg != null : !this$ociCfg.equals(other$ociCfg));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SysUserDTO;
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
        Boolean $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : ((Object)$assignPublicIp).hashCode());
        Boolean $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : ((Object)$assignIpv6).hashCode());
        Integer $instanceDisplayOrdinal = this.getInstanceDisplayOrdinal();
        result = result * 59 + ($instanceDisplayOrdinal == null ? 43 : ((Object)$instanceDisplayOrdinal).hashCode());
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        String $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        String $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        String $customScript = this.getCustomScript();
        result = result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
        Set $excludedAvailabilityDomains = this.getExcludedAvailabilityDomains();
        result = result * 59 + ($excludedAvailabilityDomains == null ? 43 : ((Object)$excludedAvailabilityDomains).hashCode());
        OciCfg $ociCfg = this.getOciCfg();
        result = result * 59 + ($ociCfg == null ? 43 : $ociCfg.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SysUserDTO(taskId=" + this.getTaskId() + ", username=" + this.getUsername() + ", architecture=" + this.getArchitecture() + ", ocpus=" + this.getOcpus() + ", memory=" + this.getMemory() + ", disk=" + this.getDisk() + ", vpusPerGB=" + this.getVpusPerGB() + ", createNumbers=" + this.getCreateNumbers() + ", rootPassword=" + this.getRootPassword() + ", operationSystem=" + this.getOperationSystem() + ", customScript=" + this.getCustomScript() + ", assignPublicIp=" + this.getAssignPublicIp() + ", assignIpv6=" + this.getAssignIpv6() + ", instanceDisplayOrdinal=" + this.getInstanceDisplayOrdinal() + ", excludedAvailabilityDomains=" + String.valueOf(this.getExcludedAvailabilityDomains()) + ", ociCfg=" + String.valueOf(this.getOciCfg()) + ")";
    }

    @Generated
    public SysUserDTO() {
    }

    @Generated
    public SysUserDTO(String taskId, String username, String architecture, Double ocpus, Double memory, Integer disk, Integer vpusPerGB, Integer createNumbers, String rootPassword, String operationSystem, String customScript, Boolean assignPublicIp, Boolean assignIpv6, Integer instanceDisplayOrdinal, Set<String> excludedAvailabilityDomains, OciCfg ociCfg) {
        this.taskId = taskId;
        this.username = username;
        this.architecture = architecture;
        this.ocpus = ocpus;
        this.memory = memory;
        this.disk = disk;
        this.vpusPerGB = vpusPerGB;
        this.createNumbers = createNumbers;
        this.rootPassword = rootPassword;
        this.operationSystem = operationSystem;
        this.customScript = customScript;
        this.assignPublicIp = assignPublicIp;
        this.assignIpv6 = assignIpv6;
        this.instanceDisplayOrdinal = instanceDisplayOrdinal;
        this.excludedAvailabilityDomains = excludedAvailabilityDomains;
        this.ociCfg = ociCfg;
    }
}

