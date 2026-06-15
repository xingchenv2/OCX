package com.ocxworker.model.dto;

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
    private SysUserDTO.OciCfg ociCfg;

    @Generated
    public static SysUserDTO.SysUserDTOBuilder builder() {
        return new SysUserDTO.SysUserDTOBuilder();
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
    public SysUserDTO.OciCfg getOciCfg() {
        return this.ociCfg;
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
    public void setInstanceDisplayOrdinal(final Integer instanceDisplayOrdinal) {
        this.instanceDisplayOrdinal = instanceDisplayOrdinal;
    }

    @Generated
    public void setExcludedAvailabilityDomains(final Set<String> excludedAvailabilityDomains) {
        this.excludedAvailabilityDomains = excludedAvailabilityDomains;
    }

    @Generated
    public void setOciCfg(final SysUserDTO.OciCfg ociCfg) {
        this.ociCfg = ociCfg;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SysUserDTO other)) {
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
                                Object this$assignPublicIp = this.getAssignPublicIp();
                                Object other$assignPublicIp = other.getAssignPublicIp();
                                if (this$assignPublicIp == null ? other$assignPublicIp == null : this$assignPublicIp.equals(other$assignPublicIp)) {
                                    Object this$assignIpv6 = this.getAssignIpv6();
                                    Object other$assignIpv6 = other.getAssignIpv6();
                                    if (this$assignIpv6 == null ? other$assignIpv6 == null : this$assignIpv6.equals(other$assignIpv6)) {
                                        Object this$instanceDisplayOrdinal = this.getInstanceDisplayOrdinal();
                                        Object other$instanceDisplayOrdinal = other.getInstanceDisplayOrdinal();
                                        if (this$instanceDisplayOrdinal == null
                                            ? other$instanceDisplayOrdinal == null
                                            : this$instanceDisplayOrdinal.equals(other$instanceDisplayOrdinal)) {
                                            Object this$taskId = this.getTaskId();
                                            Object other$taskId = other.getTaskId();
                                            if (this$taskId == null ? other$taskId == null : this$taskId.equals(other$taskId)) {
                                                Object this$username = this.getUsername();
                                                Object other$username = other.getUsername();
                                                if (this$username == null ? other$username == null : this$username.equals(other$username)) {
                                                    Object this$architecture = this.getArchitecture();
                                                    Object other$architecture = other.getArchitecture();
                                                    if (this$architecture == null ? other$architecture == null : this$architecture.equals(other$architecture)) {
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
                                                                    Object this$excludedAvailabilityDomains = this.getExcludedAvailabilityDomains();
                                                                    Object other$excludedAvailabilityDomains = other.getExcludedAvailabilityDomains();
                                                                    if (this$excludedAvailabilityDomains == null
                                                                        ? other$excludedAvailabilityDomains == null
                                                                        : this$excludedAvailabilityDomains.equals(other$excludedAvailabilityDomains)) {
                                                                        Object this$ociCfg = this.getOciCfg();
                                                                        Object other$ociCfg = other.getOciCfg();
                                                                        return this$ociCfg == null ? other$ociCfg == null : this$ociCfg.equals(other$ociCfg);
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
        return other instanceof SysUserDTO;
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
        Object $assignPublicIp = this.getAssignPublicIp();
        result = result * 59 + ($assignPublicIp == null ? 43 : $assignPublicIp.hashCode());
        Object $assignIpv6 = this.getAssignIpv6();
        result = result * 59 + ($assignIpv6 == null ? 43 : $assignIpv6.hashCode());
        Object $instanceDisplayOrdinal = this.getInstanceDisplayOrdinal();
        result = result * 59 + ($instanceDisplayOrdinal == null ? 43 : $instanceDisplayOrdinal.hashCode());
        Object $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $architecture = this.getArchitecture();
        result = result * 59 + ($architecture == null ? 43 : $architecture.hashCode());
        Object $rootPassword = this.getRootPassword();
        result = result * 59 + ($rootPassword == null ? 43 : $rootPassword.hashCode());
        Object $operationSystem = this.getOperationSystem();
        result = result * 59 + ($operationSystem == null ? 43 : $operationSystem.hashCode());
        Object $customScript = this.getCustomScript();
        result = result * 59 + ($customScript == null ? 43 : $customScript.hashCode());
        Object $excludedAvailabilityDomains = this.getExcludedAvailabilityDomains();
        result = result * 59 + ($excludedAvailabilityDomains == null ? 43 : $excludedAvailabilityDomains.hashCode());
        Object $ociCfg = this.getOciCfg();
        return result * 59 + ($ociCfg == null ? 43 : $ociCfg.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "SysUserDTO(taskId="
            + this.getTaskId()
            + ", username="
            + this.getUsername()
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
            + ", instanceDisplayOrdinal="
            + this.getInstanceDisplayOrdinal()
            + ", excludedAvailabilityDomains="
            + this.getExcludedAvailabilityDomains()
            + ", ociCfg="
            + this.getOciCfg()
            + ")";
    }

    @Generated
    public SysUserDTO() {
    }

    @Generated
    public SysUserDTO(
        final String taskId,
        final String username,
        final String architecture,
        final Double ocpus,
        final Double memory,
        final Integer disk,
        final Integer vpusPerGB,
        final Integer createNumbers,
        final String rootPassword,
        final String operationSystem,
        final String customScript,
        final Boolean assignPublicIp,
        final Boolean assignIpv6,
        final Integer instanceDisplayOrdinal,
        final Set<String> excludedAvailabilityDomains,
        final SysUserDTO.OciCfg ociCfg
    ) {
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

    public static class CloudInstance {
        private String instanceId;
        private String name;
        private String region;
        private String shape;
        private float ocpus;
        private float memoryInGBs;
        private String state;
        private String publicIp;
        private String privateIp;
        private String imageId;
        private String availabilityDomain;
        private String timeCreated;

        @Generated
        public static SysUserDTO.CloudInstance.CloudInstanceBuilder builder() {
            return new SysUserDTO.CloudInstance.CloudInstanceBuilder();
        }

        @Generated
        public String getInstanceId() {
            return this.instanceId;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public String getRegion() {
            return this.region;
        }

        @Generated
        public String getShape() {
            return this.shape;
        }

        @Generated
        public float getOcpus() {
            return this.ocpus;
        }

        @Generated
        public float getMemoryInGBs() {
            return this.memoryInGBs;
        }

        @Generated
        public String getState() {
            return this.state;
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
        public String getImageId() {
            return this.imageId;
        }

        @Generated
        public String getAvailabilityDomain() {
            return this.availabilityDomain;
        }

        @Generated
        public String getTimeCreated() {
            return this.timeCreated;
        }

        @Generated
        public void setInstanceId(final String instanceId) {
            this.instanceId = instanceId;
        }

        @Generated
        public void setName(final String name) {
            this.name = name;
        }

        @Generated
        public void setRegion(final String region) {
            this.region = region;
        }

        @Generated
        public void setShape(final String shape) {
            this.shape = shape;
        }

        @Generated
        public void setOcpus(final float ocpus) {
            this.ocpus = ocpus;
        }

        @Generated
        public void setMemoryInGBs(final float memoryInGBs) {
            this.memoryInGBs = memoryInGBs;
        }

        @Generated
        public void setState(final String state) {
            this.state = state;
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
        public void setImageId(final String imageId) {
            this.imageId = imageId;
        }

        @Generated
        public void setAvailabilityDomain(final String availabilityDomain) {
            this.availabilityDomain = availabilityDomain;
        }

        @Generated
        public void setTimeCreated(final String timeCreated) {
            this.timeCreated = timeCreated;
        }

        @Generated
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SysUserDTO.CloudInstance other)) {
                return false;
            } else if (!other.canEqual(this)) {
                return false;
            } else if (Float.compare(this.getOcpus(), other.getOcpus()) != 0) {
                return false;
            } else if (Float.compare(this.getMemoryInGBs(), other.getMemoryInGBs()) != 0) {
                return false;
            } else {
                Object this$instanceId = this.getInstanceId();
                Object other$instanceId = other.getInstanceId();
                if (this$instanceId == null ? other$instanceId == null : this$instanceId.equals(other$instanceId)) {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                        Object this$region = this.getRegion();
                        Object other$region = other.getRegion();
                        if (this$region == null ? other$region == null : this$region.equals(other$region)) {
                            Object this$shape = this.getShape();
                            Object other$shape = other.getShape();
                            if (this$shape == null ? other$shape == null : this$shape.equals(other$shape)) {
                                Object this$state = this.getState();
                                Object other$state = other.getState();
                                if (this$state == null ? other$state == null : this$state.equals(other$state)) {
                                    Object this$publicIp = this.getPublicIp();
                                    Object other$publicIp = other.getPublicIp();
                                    if (this$publicIp == null ? other$publicIp == null : this$publicIp.equals(other$publicIp)) {
                                        Object this$privateIp = this.getPrivateIp();
                                        Object other$privateIp = other.getPrivateIp();
                                        if (this$privateIp == null ? other$privateIp == null : this$privateIp.equals(other$privateIp)) {
                                            Object this$imageId = this.getImageId();
                                            Object other$imageId = other.getImageId();
                                            if (this$imageId == null ? other$imageId == null : this$imageId.equals(other$imageId)) {
                                                Object this$availabilityDomain = this.getAvailabilityDomain();
                                                Object other$availabilityDomain = other.getAvailabilityDomain();
                                                if (this$availabilityDomain == null
                                                    ? other$availabilityDomain == null
                                                    : this$availabilityDomain.equals(other$availabilityDomain)) {
                                                    Object this$timeCreated = this.getTimeCreated();
                                                    Object other$timeCreated = other.getTimeCreated();
                                                    return this$timeCreated == null ? other$timeCreated == null : this$timeCreated.equals(other$timeCreated);
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
            return other instanceof SysUserDTO.CloudInstance;
        }

        @Generated
        @Override
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + Float.floatToIntBits(this.getOcpus());
            result = result * 59 + Float.floatToIntBits(this.getMemoryInGBs());
            Object $instanceId = this.getInstanceId();
            result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
            Object $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Object $region = this.getRegion();
            result = result * 59 + ($region == null ? 43 : $region.hashCode());
            Object $shape = this.getShape();
            result = result * 59 + ($shape == null ? 43 : $shape.hashCode());
            Object $state = this.getState();
            result = result * 59 + ($state == null ? 43 : $state.hashCode());
            Object $publicIp = this.getPublicIp();
            result = result * 59 + ($publicIp == null ? 43 : $publicIp.hashCode());
            Object $privateIp = this.getPrivateIp();
            result = result * 59 + ($privateIp == null ? 43 : $privateIp.hashCode());
            Object $imageId = this.getImageId();
            result = result * 59 + ($imageId == null ? 43 : $imageId.hashCode());
            Object $availabilityDomain = this.getAvailabilityDomain();
            result = result * 59 + ($availabilityDomain == null ? 43 : $availabilityDomain.hashCode());
            Object $timeCreated = this.getTimeCreated();
            return result * 59 + ($timeCreated == null ? 43 : $timeCreated.hashCode());
        }

        @Generated
        @Override
        public String toString() {
            return "SysUserDTO.CloudInstance(instanceId="
                + this.getInstanceId()
                + ", name="
                + this.getName()
                + ", region="
                + this.getRegion()
                + ", shape="
                + this.getShape()
                + ", ocpus="
                + this.getOcpus()
                + ", memoryInGBs="
                + this.getMemoryInGBs()
                + ", state="
                + this.getState()
                + ", publicIp="
                + this.getPublicIp()
                + ", privateIp="
                + this.getPrivateIp()
                + ", imageId="
                + this.getImageId()
                + ", availabilityDomain="
                + this.getAvailabilityDomain()
                + ", timeCreated="
                + this.getTimeCreated()
                + ")";
        }

        @Generated
        public CloudInstance() {
        }

        @Generated
        public CloudInstance(
            final String instanceId,
            final String name,
            final String region,
            final String shape,
            final float ocpus,
            final float memoryInGBs,
            final String state,
            final String publicIp,
            final String privateIp,
            final String imageId,
            final String availabilityDomain,
            final String timeCreated
        ) {
            this.instanceId = instanceId;
            this.name = name;
            this.region = region;
            this.shape = shape;
            this.ocpus = ocpus;
            this.memoryInGBs = memoryInGBs;
            this.state = state;
            this.publicIp = publicIp;
            this.privateIp = privateIp;
            this.imageId = imageId;
            this.availabilityDomain = availabilityDomain;
            this.timeCreated = timeCreated;
        }

        @Generated
        public static class CloudInstanceBuilder {
            @Generated
            private String instanceId;
            @Generated
            private String name;
            @Generated
            private String region;
            @Generated
            private String shape;
            @Generated
            private float ocpus;
            @Generated
            private float memoryInGBs;
            @Generated
            private String state;
            @Generated
            private String publicIp;
            @Generated
            private String privateIp;
            @Generated
            private String imageId;
            @Generated
            private String availabilityDomain;
            @Generated
            private String timeCreated;

            @Generated
            CloudInstanceBuilder() {
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder instanceId(final String instanceId) {
                this.instanceId = instanceId;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder name(final String name) {
                this.name = name;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder region(final String region) {
                this.region = region;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder shape(final String shape) {
                this.shape = shape;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder ocpus(final float ocpus) {
                this.ocpus = ocpus;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder memoryInGBs(final float memoryInGBs) {
                this.memoryInGBs = memoryInGBs;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder state(final String state) {
                this.state = state;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder publicIp(final String publicIp) {
                this.publicIp = publicIp;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder privateIp(final String privateIp) {
                this.privateIp = privateIp;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder imageId(final String imageId) {
                this.imageId = imageId;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder availabilityDomain(final String availabilityDomain) {
                this.availabilityDomain = availabilityDomain;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance.CloudInstanceBuilder timeCreated(final String timeCreated) {
                this.timeCreated = timeCreated;
                return this;
            }

            @Generated
            public SysUserDTO.CloudInstance build() {
                return new SysUserDTO.CloudInstance(
                    this.instanceId,
                    this.name,
                    this.region,
                    this.shape,
                    this.ocpus,
                    this.memoryInGBs,
                    this.state,
                    this.publicIp,
                    this.privateIp,
                    this.imageId,
                    this.availabilityDomain,
                    this.timeCreated
                );
            }

            @Generated
            @Override
            public String toString() {
                return "SysUserDTO.CloudInstance.CloudInstanceBuilder(instanceId="
                    + this.instanceId
                    + ", name="
                    + this.name
                    + ", region="
                    + this.region
                    + ", shape="
                    + this.shape
                    + ", ocpus="
                    + this.ocpus
                    + ", memoryInGBs="
                    + this.memoryInGBs
                    + ", state="
                    + this.state
                    + ", publicIp="
                    + this.publicIp
                    + ", privateIp="
                    + this.privateIp
                    + ", imageId="
                    + this.imageId
                    + ", availabilityDomain="
                    + this.availabilityDomain
                    + ", timeCreated="
                    + this.timeCreated
                    + ")";
            }
        }
    }

    public static class OciCfg {
        private String tenantId;
        private String userId;
        private String fingerprint;
        private String region;
        private String privateKeyPath;
        private String compartmentId;

        @Generated
        public static SysUserDTO.OciCfg.OciCfgBuilder builder() {
            return new SysUserDTO.OciCfg.OciCfgBuilder();
        }

        @Generated
        public String getTenantId() {
            return this.tenantId;
        }

        @Generated
        public String getUserId() {
            return this.userId;
        }

        @Generated
        public String getFingerprint() {
            return this.fingerprint;
        }

        @Generated
        public String getRegion() {
            return this.region;
        }

        @Generated
        public String getPrivateKeyPath() {
            return this.privateKeyPath;
        }

        @Generated
        public String getCompartmentId() {
            return this.compartmentId;
        }

        @Generated
        public void setTenantId(final String tenantId) {
            this.tenantId = tenantId;
        }

        @Generated
        public void setUserId(final String userId) {
            this.userId = userId;
        }

        @Generated
        public void setFingerprint(final String fingerprint) {
            this.fingerprint = fingerprint;
        }

        @Generated
        public void setRegion(final String region) {
            this.region = region;
        }

        @Generated
        public void setPrivateKeyPath(final String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        @Generated
        public void setCompartmentId(final String compartmentId) {
            this.compartmentId = compartmentId;
        }

        @Generated
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SysUserDTO.OciCfg other)) {
                return false;
            } else if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$tenantId = this.getTenantId();
                Object other$tenantId = other.getTenantId();
                if (this$tenantId == null ? other$tenantId == null : this$tenantId.equals(other$tenantId)) {
                    Object this$userId = this.getUserId();
                    Object other$userId = other.getUserId();
                    if (this$userId == null ? other$userId == null : this$userId.equals(other$userId)) {
                        Object this$fingerprint = this.getFingerprint();
                        Object other$fingerprint = other.getFingerprint();
                        if (this$fingerprint == null ? other$fingerprint == null : this$fingerprint.equals(other$fingerprint)) {
                            Object this$region = this.getRegion();
                            Object other$region = other.getRegion();
                            if (this$region == null ? other$region == null : this$region.equals(other$region)) {
                                Object this$privateKeyPath = this.getPrivateKeyPath();
                                Object other$privateKeyPath = other.getPrivateKeyPath();
                                if (this$privateKeyPath == null ? other$privateKeyPath == null : this$privateKeyPath.equals(other$privateKeyPath)) {
                                    Object this$compartmentId = this.getCompartmentId();
                                    Object other$compartmentId = other.getCompartmentId();
                                    return this$compartmentId == null ? other$compartmentId == null : this$compartmentId.equals(other$compartmentId);
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
            return other instanceof SysUserDTO.OciCfg;
        }

        @Generated
        @Override
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Object $tenantId = this.getTenantId();
            result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
            Object $userId = this.getUserId();
            result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
            Object $fingerprint = this.getFingerprint();
            result = result * 59 + ($fingerprint == null ? 43 : $fingerprint.hashCode());
            Object $region = this.getRegion();
            result = result * 59 + ($region == null ? 43 : $region.hashCode());
            Object $privateKeyPath = this.getPrivateKeyPath();
            result = result * 59 + ($privateKeyPath == null ? 43 : $privateKeyPath.hashCode());
            Object $compartmentId = this.getCompartmentId();
            return result * 59 + ($compartmentId == null ? 43 : $compartmentId.hashCode());
        }

        @Generated
        @Override
        public String toString() {
            return "SysUserDTO.OciCfg(tenantId="
                + this.getTenantId()
                + ", userId="
                + this.getUserId()
                + ", fingerprint="
                + this.getFingerprint()
                + ", region="
                + this.getRegion()
                + ", privateKeyPath="
                + this.getPrivateKeyPath()
                + ", compartmentId="
                + this.getCompartmentId()
                + ")";
        }

        @Generated
        public OciCfg() {
        }

        @Generated
        public OciCfg(
            final String tenantId, final String userId, final String fingerprint, final String region, final String privateKeyPath, final String compartmentId
        ) {
            this.tenantId = tenantId;
            this.userId = userId;
            this.fingerprint = fingerprint;
            this.region = region;
            this.privateKeyPath = privateKeyPath;
            this.compartmentId = compartmentId;
        }

        @Generated
        public static class OciCfgBuilder {
            @Generated
            private String tenantId;
            @Generated
            private String userId;
            @Generated
            private String fingerprint;
            @Generated
            private String region;
            @Generated
            private String privateKeyPath;
            @Generated
            private String compartmentId;

            @Generated
            OciCfgBuilder() {
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder tenantId(final String tenantId) {
                this.tenantId = tenantId;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder userId(final String userId) {
                this.userId = userId;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder fingerprint(final String fingerprint) {
                this.fingerprint = fingerprint;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder region(final String region) {
                this.region = region;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder privateKeyPath(final String privateKeyPath) {
                this.privateKeyPath = privateKeyPath;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg.OciCfgBuilder compartmentId(final String compartmentId) {
                this.compartmentId = compartmentId;
                return this;
            }

            @Generated
            public SysUserDTO.OciCfg build() {
                return new SysUserDTO.OciCfg(this.tenantId, this.userId, this.fingerprint, this.region, this.privateKeyPath, this.compartmentId);
            }

            @Generated
            @Override
            public String toString() {
                return "SysUserDTO.OciCfg.OciCfgBuilder(tenantId="
                    + this.tenantId
                    + ", userId="
                    + this.userId
                    + ", fingerprint="
                    + this.fingerprint
                    + ", region="
                    + this.region
                    + ", privateKeyPath="
                    + this.privateKeyPath
                    + ", compartmentId="
                    + this.compartmentId
                    + ")";
            }
        }
    }

    @Generated
    public static class SysUserDTOBuilder {
        @Generated
        private String taskId;
        @Generated
        private String username;
        @Generated
        private String architecture;
        @Generated
        private Double ocpus;
        @Generated
        private Double memory;
        @Generated
        private Integer disk;
        @Generated
        private Integer vpusPerGB;
        @Generated
        private Integer createNumbers;
        @Generated
        private String rootPassword;
        @Generated
        private String operationSystem;
        @Generated
        private String customScript;
        @Generated
        private Boolean assignPublicIp;
        @Generated
        private Boolean assignIpv6;
        @Generated
        private Integer instanceDisplayOrdinal;
        @Generated
        private Set<String> excludedAvailabilityDomains;
        @Generated
        private SysUserDTO.OciCfg ociCfg;

        @Generated
        SysUserDTOBuilder() {
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder taskId(final String taskId) {
            this.taskId = taskId;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder username(final String username) {
            this.username = username;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder architecture(final String architecture) {
            this.architecture = architecture;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder ocpus(final Double ocpus) {
            this.ocpus = ocpus;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder memory(final Double memory) {
            this.memory = memory;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder disk(final Integer disk) {
            this.disk = disk;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder vpusPerGB(final Integer vpusPerGB) {
            this.vpusPerGB = vpusPerGB;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder createNumbers(final Integer createNumbers) {
            this.createNumbers = createNumbers;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder rootPassword(final String rootPassword) {
            this.rootPassword = rootPassword;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder operationSystem(final String operationSystem) {
            this.operationSystem = operationSystem;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder customScript(final String customScript) {
            this.customScript = customScript;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder assignPublicIp(final Boolean assignPublicIp) {
            this.assignPublicIp = assignPublicIp;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder assignIpv6(final Boolean assignIpv6) {
            this.assignIpv6 = assignIpv6;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder instanceDisplayOrdinal(final Integer instanceDisplayOrdinal) {
            this.instanceDisplayOrdinal = instanceDisplayOrdinal;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder excludedAvailabilityDomains(final Set<String> excludedAvailabilityDomains) {
            this.excludedAvailabilityDomains = excludedAvailabilityDomains;
            return this;
        }

        @Generated
        public SysUserDTO.SysUserDTOBuilder ociCfg(final SysUserDTO.OciCfg ociCfg) {
            this.ociCfg = ociCfg;
            return this;
        }

        @Generated
        public SysUserDTO build() {
            return new SysUserDTO(
                this.taskId,
                this.username,
                this.architecture,
                this.ocpus,
                this.memory,
                this.disk,
                this.vpusPerGB,
                this.createNumbers,
                this.rootPassword,
                this.operationSystem,
                this.customScript,
                this.assignPublicIp,
                this.assignIpv6,
                this.instanceDisplayOrdinal,
                this.excludedAvailabilityDomains,
                this.ociCfg
            );
        }

        @Generated
        @Override
        public String toString() {
            return "SysUserDTO.SysUserDTOBuilder(taskId="
                + this.taskId
                + ", username="
                + this.username
                + ", architecture="
                + this.architecture
                + ", ocpus="
                + this.ocpus
                + ", memory="
                + this.memory
                + ", disk="
                + this.disk
                + ", vpusPerGB="
                + this.vpusPerGB
                + ", createNumbers="
                + this.createNumbers
                + ", rootPassword="
                + this.rootPassword
                + ", operationSystem="
                + this.operationSystem
                + ", customScript="
                + this.customScript
                + ", assignPublicIp="
                + this.assignPublicIp
                + ", assignIpv6="
                + this.assignIpv6
                + ", instanceDisplayOrdinal="
                + this.instanceDisplayOrdinal
                + ", excludedAvailabilityDomains="
                + this.excludedAvailabilityDomains
                + ", ociCfg="
                + this.ociCfg
                + ")";
        }
    }
}
