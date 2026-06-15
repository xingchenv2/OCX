/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.ociworker.model.entity.OciCreateTask
 *  lombok.Generated
 */
package com.ociworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName(value="oci_create_task")
public class OciCreateTask {
    @TableId
    private String id;
    private String userId;
    private String ociRegion;
    private Double ocpus;
    private Double memory;
    private Integer disk;
    @TableField(value="vpus_per_gb")
    private Integer vpusPerGB;
    private String architecture;
    private Integer intervalSeconds;
    private Integer createNumbers;
    private String rootPassword;
    private String operationSystem;
    private String customScript;
    private Boolean assignPublicIp;
    private Boolean assignIpv6;
    private String status;
    private Integer attemptCount;
    private Integer successCount;
    private String createdInstances;
    private LocalDateTime createTime;

    @Generated
    public OciCreateTask() {
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public String getUserId() {
        return this.userId;
    }

    @Generated
    public String getOciRegion() {
        return this.ociRegion;
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
    public String getArchitecture() {
        return this.architecture;
    }

    @Generated
    public Integer getIntervalSeconds() {
        return this.intervalSeconds;
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
    public String getStatus() {
        return this.status;
    }

    @Generated
    public Integer getAttemptCount() {
        return this.attemptCount;
    }

    @Generated
    public Integer getSuccessCount() {
        return this.successCount;
    }

    @Generated
    public String getCreatedInstances() {
        return this.createdInstances;
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
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated
    public void setOciRegion(String ociRegion) {
        this.ociRegion = ociRegion;
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
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    @Generated
    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
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
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    @Generated
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    @Generated
    public void setCreatedInstances(String createdInstances) {
        this.createdInstances = createdInstances;
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
        if (!(o instanceof OciCreateTask)) {
            return false;
        }
        OciCreateTask other = (OciCreateTask)o;
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
        Integer this$intervalSeconds = this.getIntervalSeconds();
        Integer other$intervalSeconds = other.getIntervalSeconds();
        if (this$intervalSeconds == null ? other$intervalSeconds != null : !((Object)this$intervalSeconds).equals(other$intervalSeconds)) {
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
        Integer this$attemptCount = this.getAttemptCount();
        Integer other$attemptCount = other.getAttemptCount();
        if (this$attemptCount == null ? other$attemptCount != null : !((Object)this$attemptCount).equals(other$attemptCount)) {
            return false;
        }
        Integer this$successCount = this.getSuccessCount();
        Integer other$successCount = other.getSuccessCount();
        if (this$successCount == null ? other$successCount != null : !((Object)this$successCount).equals(other$successCount)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$userId = this.getUserId();
        String other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) {
            return false;
        }
        String this$ociRegion = this.getOciRegion();
        String other$ociRegion = other.getOciRegion();
        if (this$ociRegion == null ? other$ociRegion != null : !this$ociRegion.equals(other$ociRegion)) {
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
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$createdInstances = this.getCreatedInstances();
        String other$createdInstances = other.getCreatedInstances();
        if (this$createdInstances == null ? other$createdInstances != null : !this$createdInstances.equals(other$createdInstances)) {
            return false;
        }
        LocalDateTime this$createTime = this.getCreateTime();
        LocalDateTime other$createTime = other.getCreateTime();
        return !(this$createTime == null ? other$createTime != null : !((Object)this$createTime).equals(other$createTime));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciCreateTask;
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
        Integer $intervalSeconds = this.getIntervalSeconds();
        result = result * 59 + ($intervalSeconds == null ? 43 : ((Object)$intervalSeconds).hashCode());
        Integer $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : ((Object)$createNumbers).hashCode());
        Boolean $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : ((Object)$assignPublicIp).hashCode());
        Boolean $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : ((Object)$assignIpv6).hashCode());
        Integer $attemptCount = this.getAttemptCount();
        result = result * 59 + ($attemptCount == null ? 43 : ((Object)$attemptCount).hashCode());
        Integer $successCount = this.getSuccessCount();
        result = result * 59 + ($successCount == null ? 43 : ((Object)$successCount).hashCode());
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        String $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        String $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        String $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        String $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        String $customScript = this.getCustomScript();
        result = result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $createdInstances = this.getCreatedInstances();
        result = result * 59 + ($createdInstances == null ? 43 : $createdInstances.hashCode());
        LocalDateTime $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : ((Object)$createTime).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciCreateTask(id=" + this.getId() + ", userId=" + this.getUserId() + ", ociRegion=" + this.getOciRegion() + ", ocpus=" + this.getOcpus() + ", memory=" + this.getMemory() + ", disk=" + this.getDisk() + ", vpusPerGB=" + this.getVpusPerGB() + ", architecture=" + this.getArchitecture() + ", intervalSeconds=" + this.getIntervalSeconds() + ", createNumbers=" + this.getCreateNumbers() + ", rootPassword=" + this.getRootPassword() + ", operationSystem=" + this.getOperationSystem() + ", customScript=" + this.getCustomScript() + ", assignPublicIp=" + this.getAssignPublicIp() + ", assignIpv6=" + this.getAssignIpv6() + ", status=" + this.getStatus() + ", attemptCount=" + this.getAttemptCount() + ", successCount=" + this.getSuccessCount() + ", createdInstances=" + this.getCreatedInstances() + ", createTime=" + String.valueOf(this.getCreateTime()) + ")";
    }
}

