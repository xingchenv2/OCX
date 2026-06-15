package com.ocxworker.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Generated;

@TableName("oci_create_task")
public class OciCreateTask {
    @TableId
    private String id;
    private String userId;
    private String ociRegion;
    private Double ocpus;
    private Double memory;
    private Integer disk;
    @TableField("vpus_per_gb")
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
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @Generated
    public void setOciRegion(final String ociRegion) {
        this.ociRegion = ociRegion;
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
    public void setVpusPerGB(final Integer vpusPerGB) {
        this.vpusPerGB = vpusPerGB;
    }

    @Generated
    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    @Generated
    public void setIntervalSeconds(final Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    @Generated
    public void setCreateNumbers(final Integer createNumbers) {
        this.createNumbers = createNumbers;
    }

    @Generated
    public void setRootPassword(final String rootPassword) {
        this.rootPassword = rootPassword;
    }

    @Generated
    public void setOperationSystem(final String operationSystem) {
        this.operationSystem = operationSystem;
    }

    @Generated
    public void setCustomScript(final String customScript) {
        this.customScript = customScript;
    }

    @Generated
    public void setAssignPublicIp(final Boolean assignPublicIp) {
        this.assignPublicIp = assignPublicIp;
    }

    @Generated
    public void setAssignIpv6(final Boolean assignIpv6) {
        this.assignIpv6 = assignIpv6;
    }

    @Generated
    public void setStatus(final String status) {
        this.status = status;
    }

    @Generated
    public void setAttemptCount(final Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    @Generated
    public void setSuccessCount(final Integer successCount) {
        this.successCount = successCount;
    }

    @Generated
    public void setCreatedInstances(final String createdInstances) {
        this.createdInstances = createdInstances;
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
        } else if (!(o instanceof OciCreateTask other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$ocpus = this.getOcpus();
            Object other$ocpus = other.getOcpus();
            if (this$ocpus == null ? other$ocpus == null : this$ocpus.equals(other$ocpus)) {
                Object this$memory = this.getMemory();
                Object other$memory = other.getMemory();
                if (this$memory == null ? other$memory == null : this$memory.equals(other$memory)) {
                    Object this$disk = this.getDisk();
                    Object other$disk = other.getDisk();
                    if (this$disk == null ? other$disk == null : this$disk.equals(other$disk)) {
                        Object this$vpusPerGB = this.getVpusPerGB();
                        Object other$vpusPerGB = other.getVpusPerGB();
                        if (this$vpusPerGB == null ? other$vpusPerGB == null : this$vpusPerGB.equals(other$vpusPerGB)) {
                            Object this$intervalSeconds = this.getIntervalSeconds();
                            Object other$intervalSeconds = other.getIntervalSeconds();
                            if (this$intervalSeconds == null ? other$intervalSeconds == null : this$intervalSeconds.equals(other$intervalSeconds)) {
                                Object this$createNumbers = this.getCreateNumbers();
                                Object other$createNumbers = other.getCreateNumbers();
                                if (this$createNumbers == null ? other$createNumbers == null : this$createNumbers.equals(other$createNumbers)) {
                                    Object this$assignPublicIp = this.getAssignPublicIp();
                                    Object other$assignPublicIp = other.getAssignPublicIp();
                                    if (this$assignPublicIp == null ? other$assignPublicIp == null : this$assignPublicIp.equals(other$assignPublicIp)) {
                                        Object this$assignIpv6 = this.getAssignIpv6();
                                        Object other$assignIpv6 = other.getAssignIpv6();
                                        if (this$assignIpv6 == null ? other$assignIpv6 == null : this$assignIpv6.equals(other$assignIpv6)) {
                                            Object this$attemptCount = this.getAttemptCount();
                                            Object other$attemptCount = other.getAttemptCount();
                                            if (this$attemptCount == null ? other$attemptCount == null : this$attemptCount.equals(other$attemptCount)) {
                                                Object this$successCount = this.getSuccessCount();
                                                Object other$successCount = other.getSuccessCount();
                                                if (this$successCount == null ? other$successCount == null : this$successCount.equals(other$successCount)) {
                                                    Object this$id = this.getId();
                                                    Object other$id = other.getId();
                                                    if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                                                        Object this$userId = this.getUserId();
                                                        Object other$userId = other.getUserId();
                                                        if (this$userId == null ? other$userId == null : this$userId.equals(other$userId)) {
                                                            Object this$ociRegion = this.getOciRegion();
                                                            Object other$ociRegion = other.getOciRegion();
                                                            if (this$ociRegion == null ? other$ociRegion == null : this$ociRegion.equals(other$ociRegion)) {
                                                                Object this$architecture = this.getArchitecture();
                                                                Object other$architecture = other.getArchitecture();
                                                                if (this$architecture == null
                                                                    ? other$architecture == null
                                                                    : this$architecture.equals(other$architecture)) {
                                                                    Object this$rootPassword = this.getRootPassword();
                                                                    Object other$rootPassword = other.getRootPassword();
                                                                    if (this$rootPassword == null
                                                                        ? other$rootPassword == null
                                                                        : this$rootPassword.equals(other$rootPassword)) {
                                                                        Object this$operationSystem = this.getOperationSystem();
                                                                        Object other$operationSystem = other.getOperationSystem();
                                                                        if (this$operationSystem == null
                                                                            ? other$operationSystem == null
                                                                            : this$operationSystem.equals(other$operationSystem)) {
                                                                            Object this$customScript = this.getCustomScript();
                                                                            Object other$customScript = other.getCustomScript();
                                                                            if (this$customScript == null
                                                                                ? other$customScript == null
                                                                                : this$customScript.equals(other$customScript)) {
                                                                                Object this$status = this.getStatus();
                                                                                Object other$status = other.getStatus();
                                                                                if (this$status == null
                                                                                    ? other$status == null
                                                                                    : this$status.equals(other$status)) {
                                                                                    Object this$createdInstances = this.getCreatedInstances();
                                                                                    Object other$createdInstances = other.getCreatedInstances();
                                                                                    if (this$createdInstances == null
                                                                                        ? other$createdInstances == null
                                                                                        : this$createdInstances.equals(other$createdInstances)) {
                                                                                        Object this$createTime = this.getCreateTime();
                                                                                        Object other$createTime = other.getCreateTime();
                                                                                        return this$createTime == null
                                                                                            ? other$createTime == null
                                                                                            : this$createTime.equals(other$createTime);
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
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof OciCreateTask;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $ocpus = this.getOcpus();
        result = result * 59 + ($ocpus == null ? 43 : $ocpus.hashCode());
        Object $memory = this.getMemory();
        result = result * 59 + ($memory == null ? 43 : $memory.hashCode());
        Object $disk = this.getDisk();
        result = result * 59 + ($disk == null ? 43 : $disk.hashCode());
        Object $vpusPerGB = this.getVpusPerGB();
        result = result * 59 + ($vpusPerGB == null ? 43 : $vpusPerGB.hashCode());
        Object $intervalSeconds = this.getIntervalSeconds();
        result = result * 59 + ($intervalSeconds == null ? 43 : $intervalSeconds.hashCode());
        Object $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : $createNumbers.hashCode());
        Object $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : $assignPublicIp.hashCode());
        Object $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : $assignIpv6.hashCode());
        Object $attemptCount = this.getAttemptCount();
        result = result * 59 + ($attemptCount == null ? 43 : $attemptCount.hashCode());
        Object $successCount = this.getSuccessCount();
        result = result * 59 + ($successCount == null ? 43 : $successCount.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        Object $ociRegion = this.getOciRegion();
        result = result * 59 + ($ociRegion == null ? 43 : $ociRegion.hashCode());
        Object $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        Object $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        Object $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        Object $customScript = this.getCustomScript();
        result = result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $createdInstances = this.getCreatedInstances();
        result = result * 59 + ($createdInstances == null ? 43 : $createdInstances.hashCode());
        Object $createTime = this.getCreateTime();
        return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciCreateTask(id="
            + this.getId()
            + ", userId="
            + this.getUserId()
            + ", ociRegion="
            + this.getOciRegion()
            + ", ocpus="
            + this.getOcpus()
            + ", memory="
            + this.getMemory()
            + ", disk="
            + this.getDisk()
            + ", vpusPerGB="
            + this.getVpusPerGB()
            + ", architecture="
            + this.getArchitecture()
            + ", intervalSeconds="
            + this.getIntervalSeconds()
            + ", createNumbers="
            + this.getCreateNumbers()
            + ", rootPassword="
            + this.getRootPassword()
            + ", operationSystem="
            + this.getOperationSystem()
            + ", customScript="
            + this.getCustomScript()
            + ", assignPublicIp="
            + this.getAssignPublicIp()
            + ", assignIpv6="
            + this.getAssignIpv6()
            + ", status="
            + this.getStatus()
            + ", attemptCount="
            + this.getAttemptCount()
            + ", successCount="
            + this.getSuccessCount()
            + ", createdInstances="
            + this.getCreatedInstances()
            + ", createTime="
            + this.getCreateTime()
            + ")";
    }
}
