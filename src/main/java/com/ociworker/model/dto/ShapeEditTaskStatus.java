/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.dto.ShapeEditTaskStatus
 *  com.ociworker.model.dto.ShapeEditTaskStatus$ShapeEditTaskStatusBuilder
 *  com.ociworker.model.entity.ShapeEditTask$Status
 *  lombok.Generated
 */
package com.ociworker.model.dto;

import com.ociworker.model.dto.ShapeEditTaskStatus;
import com.ociworker.model.entity.ShapeEditTask;
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
    public static ShapeEditTaskStatusBuilder builder() {
        return new ShapeEditTaskStatusBuilder();
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
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Generated
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Generated
    public void setRegion(String region) {
        this.region = region;
    }

    @Generated
    public void setStatus(ShapeEditTask.Status status) {
        this.status = status;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Generated
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Generated
    public void setPending(boolean pending) {
        this.pending = pending;
    }

    @Generated
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Generated
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Generated
    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    @Generated
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated
    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Generated
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShapeEditTaskStatus)) {
            return false;
        }
        ShapeEditTaskStatus other = (ShapeEditTaskStatus)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getRetryCount() != other.getRetryCount()) {
            return false;
        }
        if (this.getMaxRetries() != other.getMaxRetries()) {
            return false;
        }
        if (this.isPending() != other.isPending()) {
            return false;
        }
        if (this.isPaused() != other.isPaused()) {
            return false;
        }
        if (this.isStopped() != other.isStopped()) {
            return false;
        }
        if (this.isTerminal() != other.isTerminal()) {
            return false;
        }
        String this$taskId = this.getTaskId();
        String other$taskId = other.getTaskId();
        if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
            return false;
        }
        String this$instanceId = this.getInstanceId();
        String other$instanceId = other.getInstanceId();
        if (this$instanceId == null ? other$instanceId != null : !this$instanceId.equals(other$instanceId)) {
            return false;
        }
        String this$tenantId = this.getTenantId();
        String other$tenantId = other.getTenantId();
        if (this$tenantId == null ? other$tenantId != null : !this$tenantId.equals(other$tenantId)) {
            return false;
        }
        String this$region = this.getRegion();
        String other$region = other.getRegion();
        if (this$region == null ? other$region != null : !this$region.equals(other$region)) {
            return false;
        }
        ShapeEditTask.Status this$status = this.getStatus();
        ShapeEditTask.Status other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        Instant this$createdAt = this.getCreatedAt();
        Instant other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        Instant this$updatedAt = this.getUpdatedAt();
        Instant other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        Instant this$finishedAt = this.getFinishedAt();
        Instant other$finishedAt = other.getFinishedAt();
        if (this$finishedAt == null ? other$finishedAt != null : !((Object)this$finishedAt).equals(other$finishedAt)) {
            return false;
        }
        Map this$result = this.getResult();
        Map other$result = other.getResult();
        return !(this$result == null ? other$result != null : !((Object)this$result).equals(other$result));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ShapeEditTaskStatus;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRetryCount();
        result = result * 59 + this.getMaxRetries();
        result = result * 59 + (this.isPending() ? 79 : 97);
        result = result * 59 + (this.isPaused() ? 79 : 97);
        result = result * 59 + (this.isStopped() ? 79 : 97);
        result = result * 59 + (this.isTerminal() ? 79 : 97);
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $instanceId = this.getInstanceId();
        result = result * 59 + ($instanceId == null ? 43 : $instanceId.hashCode());
        String $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        String $region = this.getRegion();
        result = result * 59 + ($region == null ? 43 : $region.hashCode());
        ShapeEditTask.Status $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Instant $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        Instant $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        Instant $finishedAt = this.getFinishedAt();
        result = result * 59 + ($finishedAt == null ? 43 : ((Object)$finishedAt).hashCode());
        Map $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : ((Object)$result).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ShapeEditTaskStatus(taskId=" + this.getTaskId() + ", instanceId=" + this.getInstanceId() + ", tenantId=" + this.getTenantId() + ", region=" + this.getRegion() + ", status=" + String.valueOf(this.getStatus()) + ", message=" + this.getMessage() + ", retryCount=" + this.getRetryCount() + ", maxRetries=" + this.getMaxRetries() + ", pending=" + this.isPending() + ", paused=" + this.isPaused() + ", stopped=" + this.isStopped() + ", terminal=" + this.isTerminal() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ", updatedAt=" + String.valueOf(this.getUpdatedAt()) + ", finishedAt=" + String.valueOf(this.getFinishedAt()) + ", result=" + String.valueOf(this.getResult()) + ")";
    }

    @Generated
    public ShapeEditTaskStatus() {
    }

    @Generated
    public ShapeEditTaskStatus(String taskId, String instanceId, String tenantId, String region, ShapeEditTask.Status status, String message, int retryCount, int maxRetries, boolean pending, boolean paused, boolean stopped, boolean terminal, Instant createdAt, Instant updatedAt, Instant finishedAt, Map<String, Object> result) {
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
}

