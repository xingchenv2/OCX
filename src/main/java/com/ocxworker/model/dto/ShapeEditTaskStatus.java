package com.ocxworker.model.dto;

import com.ocxworker.model.entity.ShapeEditTask;
import java.time.Instant;
import java.util.Map;
import lombok.Generated;

public class ShapeEditTaskStatus {
    private String taskId;
    private String instanceId;
    private String tenantId;
    private String region;
    private ShapeEditTask.Status status;
    private String message;
    private int retryCount;
    private int maxRetries;
    private boolean pending;
    private boolean paused;
    private boolean stopped;
    private boolean terminal;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant finishedAt;
    private Map<String, Object> result;

    @Generated
    public static ShapeEditTaskStatus.ShapeEditTaskStatusBuilder builder() {
        return new ShapeEditTaskStatus.ShapeEditTaskStatusBuilder();
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getInstanceId() {
        return this.instanceId;
    }

    @Generated
    public String getTenantId() {
        return this.tenantId;
    }

    @Generated
    public String getRegion() {
        return this.region;
    }

    @Generated
    public ShapeEditTask.Status getStatus() {
        return this.status;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public int getRetryCount() {
        return this.retryCount;
    }

    @Generated
    public int getMaxRetries() {
        return this.maxRetries;
    }

    @Generated
    public boolean isPending() {
        return this.pending;
    }

    @Generated
    public boolean isPaused() {
        return this.paused;
    }

    @Generated
    public boolean isStopped() {
        return this.stopped;
    }

    @Generated
    public boolean isTerminal() {
        return this.terminal;
    }

    @Generated
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    @Generated
    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    @Generated
    public Map<String, Object> getResult() {
        return this.result;
    }

    @Generated
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    @Generated
    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    @Generated
    public void setRegion(final String region) {
        this.region = region;
    }

    @Generated
    public void setStatus(final ShapeEditTask.Status status) {
        this.status = status;
    }

    @Generated
    public void setMessage(final String message) {
        this.message = message;
    }

    @Generated
    public void setRetryCount(final int retryCount) {
        this.retryCount = retryCount;
    }

    @Generated
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Generated
    public void setPending(final boolean pending) {
        this.pending = pending;
    }

    @Generated
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

    @Generated
    public void setStopped(final boolean stopped) {
        this.stopped = stopped;
    }

    @Generated
    public void setTerminal(final boolean terminal) {
        this.terminal = terminal;
    }

    @Generated
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated
    public void setFinishedAt(final Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Generated
    public void setResult(final Map<String, Object> result) {
        this.result = result;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ShapeEditTaskStatus other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.getRetryCount() != other.getRetryCount()) {
            return false;
        } else if (this.getMaxRetries() != other.getMaxRetries()) {
            return false;
        } else if (this.isPending() != other.isPending()) {
            return false;
        } else if (this.isPaused() != other.isPaused()) {
            return false;
        } else if (this.isStopped() != other.isStopped()) {
            return false;
        } else if (this.isTerminal() != other.isTerminal()) {
            return false;
        } else {
            Object this$taskId = this.getTaskId();
            Object other$taskId = other.getTaskId();
            if (this$taskId == null ? other$taskId == null : this$taskId.equals(other$taskId)) {
                Object this$instanceId = this.getInstanceId();
                Object other$instanceId = other.getInstanceId();
                if (this$instanceId == null ? other$instanceId == null : this$instanceId.equals(other$instanceId)) {
                    Object this$tenantId = this.getTenantId();
                    Object other$tenantId = other.getTenantId();
                    if (this$tenantId == null ? other$tenantId == null : this$tenantId.equals(other$tenantId)) {
                        Object this$region = this.getRegion();
                        Object other$region = other.getRegion();
                        if (this$region == null ? other$region == null : this$region.equals(other$region)) {
                            Object this$status = this.getStatus();
                            Object other$status = other.getStatus();
                            if (this$status == null ? other$status == null : this$status.equals(other$status)) {
                                Object this$message = this.getMessage();
                                Object other$message = other.getMessage();
                                if (this$message == null ? other$message == null : this$message.equals(other$message)) {
                                    Object this$createdAt = this.getCreatedAt();
                                    Object other$createdAt = other.getCreatedAt();
                                    if (this$createdAt == null ? other$createdAt == null : this$createdAt.equals(other$createdAt)) {
                                        Object this$updatedAt = this.getUpdatedAt();
                                        Object other$updatedAt = other.getUpdatedAt();
                                        if (this$updatedAt == null ? other$updatedAt == null : this$updatedAt.equals(other$updatedAt)) {
                                            Object this$finishedAt = this.getFinishedAt();
                                            Object other$finishedAt = other.getFinishedAt();
                                            if (this$finishedAt == null ? other$finishedAt == null : this$finishedAt.equals(other$finishedAt)) {
                                                Object this$result = this.getResult();
                                                Object other$result = other.getResult();
                                                return this$result == null ? other$result == null : this$result.equals(other$result);
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
        return other instanceof ShapeEditTaskStatus;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRetryCount();
        result = result * 59 + this.getMaxRetries();
        result = result * 59 + (this.isPending() ? 79 : 97);
        result = result * 59 + (this.isPaused() ? 79 : 97);
        result = result * 59 + (this.isStopped() ? 79 : 97);
        result = result * 59 + (this.isTerminal() ? 79 : 97);
        Object $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        Object $instanceId = this.getInstanceId();
        result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
        Object $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        Object $region = this.getRegion();
        result = result * 59 + ($region == null ? 43 : $region.hashCode());
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        Object $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        Object $finishedAt = this.getFinishedAt();
        result = result * 59 + ($finishedAt == null ? 43 : $finishedAt.hashCode());
        Object $result = this.getResult();
        return result * 59 + ($result == null ? 43 : $result.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "ShapeEditTaskStatus(taskId="
            + this.getTaskId()
            + ", instanceId="
            + this.getInstanceId()
            + ", tenantId="
            + this.getTenantId()
            + ", region="
            + this.getRegion()
            + ", status="
            + this.getStatus()
            + ", message="
            + this.getMessage()
            + ", retryCount="
            + this.getRetryCount()
            + ", maxRetries="
            + this.getMaxRetries()
            + ", pending="
            + this.isPending()
            + ", paused="
            + this.isPaused()
            + ", stopped="
            + this.isStopped()
            + ", terminal="
            + this.isTerminal()
            + ", createdAt="
            + this.getCreatedAt()
            + ", updatedAt="
            + this.getUpdatedAt()
            + ", finishedAt="
            + this.getFinishedAt()
            + ", result="
            + this.getResult()
            + ")";
    }

    @Generated
    public ShapeEditTaskStatus() {
    }

    @Generated
    public ShapeEditTaskStatus(
        final String taskId,
        final String instanceId,
        final String tenantId,
        final String region,
        final ShapeEditTask.Status status,
        final String message,
        final int retryCount,
        final int maxRetries,
        final boolean pending,
        final boolean paused,
        final boolean stopped,
        final boolean terminal,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant finishedAt,
        final Map<String, Object> result
    ) {
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.tenantId = tenantId;
        this.region = region;
        this.status = status;
        this.message = message;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries;
        this.pending = pending;
        this.paused = paused;
        this.stopped = stopped;
        this.terminal = terminal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.finishedAt = finishedAt;
        this.result = result;
    }

    @Generated
    public static class ShapeEditTaskStatusBuilder {
        @Generated
        private String taskId;
        @Generated
        private String instanceId;
        @Generated
        private String tenantId;
        @Generated
        private String region;
        @Generated
        private ShapeEditTask.Status status;
        @Generated
        private String message;
        @Generated
        private int retryCount;
        @Generated
        private int maxRetries;
        @Generated
        private boolean pending;
        @Generated
        private boolean paused;
        @Generated
        private boolean stopped;
        @Generated
        private boolean terminal;
        @Generated
        private Instant createdAt;
        @Generated
        private Instant updatedAt;
        @Generated
        private Instant finishedAt;
        @Generated
        private Map<String, Object> result;

        @Generated
        ShapeEditTaskStatusBuilder() {
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder taskId(final String taskId) {
            this.taskId = taskId;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder instanceId(final String instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder tenantId(final String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder region(final String region) {
            this.region = region;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder status(final ShapeEditTask.Status status) {
            this.status = status;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder message(final String message) {
            this.message = message;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder retryCount(final int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder maxRetries(final int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder pending(final boolean pending) {
            this.pending = pending;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder paused(final boolean paused) {
            this.paused = paused;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder stopped(final boolean stopped) {
            this.stopped = stopped;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder terminal(final boolean terminal) {
            this.terminal = terminal;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder createdAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder updatedAt(final Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder finishedAt(final Instant finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus.ShapeEditTaskStatusBuilder result(final Map<String, Object> result) {
            this.result = result;
            return this;
        }

        @Generated
        public ShapeEditTaskStatus build() {
            return new ShapeEditTaskStatus(
                this.taskId,
                this.instanceId,
                this.tenantId,
                this.region,
                this.status,
                this.message,
                this.retryCount,
                this.maxRetries,
                this.pending,
                this.paused,
                this.stopped,
                this.terminal,
                this.createdAt,
                this.updatedAt,
                this.finishedAt,
                this.result
            );
        }

        @Generated
        @Override
        public String toString() {
            return "ShapeEditTaskStatus.ShapeEditTaskStatusBuilder(taskId="
                + this.taskId
                + ", instanceId="
                + this.instanceId
                + ", tenantId="
                + this.tenantId
                + ", region="
                + this.region
                + ", status="
                + this.status
                + ", message="
                + this.message
                + ", retryCount="
                + this.retryCount
                + ", maxRetries="
                + this.maxRetries
                + ", pending="
                + this.pending
                + ", paused="
                + this.paused
                + ", stopped="
                + this.stopped
                + ", terminal="
                + this.terminal
                + ", createdAt="
                + this.createdAt
                + ", updatedAt="
                + this.updatedAt
                + ", finishedAt="
                + this.finishedAt
                + ", result="
                + this.result
                + ")";
        }
    }
}
