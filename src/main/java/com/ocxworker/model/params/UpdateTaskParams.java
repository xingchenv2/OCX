package com.ocxworker.model.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Generated;

public class UpdateTaskParams {
    @NotBlank(
        message = "任务ID不能为空"
    )
    private String taskId;
    private String architecture;
    private Double ocpus;
    private Double memory;
    private Integer disk;
    private Integer vpusPerGB;
    private Integer createNumbers;
    private Integer interval;
    private String rootPassword;
    private String operationSystem;
    private String customScript;
    private Boolean assignPublicIp;
    private Boolean assignIpv6;

    @Generated
    public String getTaskId() {
        return this.taskId;
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
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
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
    public void setCreateNumbers(final Integer createNumbers) {
        this.createNumbers = createNumbers;
    }

    @Generated
    public void setInterval(final Integer interval) {
        this.interval = interval;
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
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UpdateTaskParams other)) {
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
                            Object this$createNumbers = this.getCreateNumbers();
                            Object other$createNumbers = other.getCreateNumbers();
                            if (this$createNumbers == null ? other$createNumbers == null : this$createNumbers.equals(other$createNumbers)) {
                                Object this$interval = this.getInterval();
                                Object other$interval = other.getInterval();
                                if (this$interval == null ? other$interval == null : this$interval.equals(other$interval)) {
                                    Object this$assignPublicIp = this.getAssignPublicIp();
                                    Object other$assignPublicIp = other.getAssignPublicIp();
                                    if (this$assignPublicIp == null ? other$assignPublicIp == null : this$assignPublicIp.equals(other$assignPublicIp)) {
                                        Object this$assignIpv6 = this.getAssignIpv6();
                                        Object other$assignIpv6 = other.getAssignIpv6();
                                        if (this$assignIpv6 == null ? other$assignIpv6 == null : this$assignIpv6.equals(other$assignIpv6)) {
                                            Object this$taskId = this.getTaskId();
                                            Object other$taskId = other.getTaskId();
                                            if (this$taskId == null ? other$taskId == null : this$taskId.equals(other$taskId)) {
                                                Object this$architecture = this.getArchitecture();
                                                Object other$architecture = other.getArchitecture();
                                                if (this$architecture == null ? other$architecture == null : this$architecture.equals(other$architecture)) {
                                                    Object this$rootPassword = this.getRootPassword();
                                                    Object other$rootPassword = other.getRootPassword();
                                                    if (this$rootPassword == null ? other$rootPassword == null : this$rootPassword.equals(other$rootPassword)) {
                                                        Object this$operationSystem = this.getOperationSystem();
                                                        Object other$operationSystem = other.getOperationSystem();
                                                        if (this$operationSystem == null
                                                            ? other$operationSystem == null
                                                            : this$operationSystem.equals(other$operationSystem)) {
                                                            Object this$customScript = this.getCustomScript();
                                                            Object other$customScript = other.getCustomScript();
                                                            return this$customScript == null
                                                                ? other$customScript == null
                                                                : this$customScript.equals(other$customScript);
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
        return other instanceof UpdateTaskParams;
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
        Object $createNumbers = this.getCreateNumbers();
        result = result * 59 + ($createNumbers == null ? 43 : $createNumbers.hashCode());
        Object $interval = this.getInterval();
        result = result * 59 + ($interval == null ? 43 : $interval.hashCode());
        Object $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : $assignPublicIp.hashCode());
        Object $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : $assignIpv6.hashCode());
        Object $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        Object $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        Object $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        Object $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        Object $customScript = this.getCustomScript();
        return result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "UpdateTaskParams(taskId="
            + this.getTaskId()
            + ", architecture="
            + this.getArchitecture()
            + ", ocpus="
            + this.getOcpus()
            + ", memory="
            + this.getMemory()
            + ", disk="
            + this.getDisk()
            + ", vpusPerGB="
            + this.getVpusPerGB()
            + ", createNumbers="
            + this.getCreateNumbers()
            + ", interval="
            + this.getInterval()
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
            + ")";
    }
}
