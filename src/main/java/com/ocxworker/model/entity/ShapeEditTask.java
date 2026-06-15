package com.ocxworker.model.entity;

import com.ocxworker.model.dto.ShapeEditTaskStatus;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import lombok.Generated;

public class ShapeEditTask {
    private final String taskId;
    private final String tenantId;
    private final String instanceId;
    private final String region;
    private final String targetShape;
    private final Float targetOcpus;
    private final Float targetMemoryInGBs;
    private final int maxRetries;
    private final long retryIntervalMillis;
    private final Callable<Map<String, Object>> operation;
    private final Object pauseMonitor = new Object();
    private final Instant createdAt = Instant.now();
    private volatile Instant updatedAt = this.createdAt;
    private volatile Instant finishedAt;
    private volatile ShapeEditTask.Status status = ShapeEditTask.Status.PENDING;
    private volatile String message = "检测到缺货，将在后台自动重试";
    private volatile int retryCount;
    private volatile boolean pauseRequested;
    private volatile boolean stopRequested;
    private volatile Thread thread;
    private volatile Map<String, Object> result;

    public ShapeEditTask(
        String taskId,
        String tenantId,
        String instanceId,
        String region,
        String targetShape,
        Float targetOcpus,
        Float targetMemoryInGBs,
        int maxRetries,
        long retryIntervalMillis,
        Callable<Map<String, Object>> operation
    ) {
        this.taskId = taskId;
        this.tenantId = tenantId;
        this.instanceId = instanceId;
        this.region = region;
        this.targetShape = targetShape;
        this.targetOcpus = targetOcpus;
        this.targetMemoryInGBs = targetMemoryInGBs;
        this.maxRetries = maxRetries;
        this.retryIntervalMillis = retryIntervalMillis;
        this.operation = operation;
    }

    public void bindThread(Thread thread) {
        this.thread = thread;
    }

    public void markRunning(String message) {
        this.status = ShapeEditTask.Status.RUNNING;
        this.message = message;
        this.touch();
    }

    public void markWaiting(String message) {
        this.status = this.pauseRequested ? ShapeEditTask.Status.PAUSED : ShapeEditTask.Status.PENDING;
        this.message = message;
        this.touch();
    }

    public void markSuccess(Map<String, Object> result) {
        this.result = result;
        this.status = ShapeEditTask.Status.SUCCESS;
        this.message = "形状变更成功";
        this.finish();
    }

    public void markFailed(String message) {
        this.status = ShapeEditTask.Status.FAILED;
        this.message = message;
        this.finish();
    }

    public void markStopped(String message) {
        this.status = ShapeEditTask.Status.STOPPED;
        this.message = message;
        this.finish();
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.touch();
    }

    public void pause() {
        if (!this.isTerminal()) {
            this.pauseRequested = true;
            this.status = ShapeEditTask.Status.PAUSED;
            this.message = "已暂停";
            this.touch();
        }
    }

    public void resume() {
        if (!this.isTerminal()) {
            synchronized (this.pauseMonitor) {
                this.pauseRequested = false;
                this.status = ShapeEditTask.Status.PENDING;
                this.message = "已恢复，等待下一次重试";
                this.touch();
                this.pauseMonitor.notifyAll();
            }
        }
    }

    public void stop() {
        if (!this.isTerminal()) {
            this.stopRequested = true;
            Thread t = this.thread;
            if (t != null) {
                t.interrupt();
            }

            synchronized (this.pauseMonitor) {
                this.pauseMonitor.notifyAll();
            }

            this.markStopped("已停止");
        }
    }

    public boolean awaitIfPaused() throws InterruptedException {
        synchronized (this.pauseMonitor) {
            while (this.pauseRequested && !this.stopRequested) {
                this.status = ShapeEditTask.Status.PAUSED;
                this.message = "已暂停";
                this.touch();
                this.pauseMonitor.wait();
            }
        }

        return this.stopRequested;
    }

    public boolean isTerminal() {
        return this.status == ShapeEditTask.Status.SUCCESS || this.status == ShapeEditTask.Status.FAILED || this.status == ShapeEditTask.Status.STOPPED;
    }

    public ShapeEditTaskStatus toStatus() {
        return ShapeEditTaskStatus.builder()
            .taskId(this.taskId)
            .tenantId(this.tenantId)
            .instanceId(this.instanceId)
            .region(this.region)
            .status(this.status)
            .message(this.message)
            .retryCount(this.retryCount)
            .maxRetries(this.maxRetries)
            .pending(this.status == ShapeEditTask.Status.PENDING || this.status == ShapeEditTask.Status.RUNNING || this.status == ShapeEditTask.Status.PAUSED)
            .paused(this.status == ShapeEditTask.Status.PAUSED)
            .stopped(this.status == ShapeEditTask.Status.STOPPED)
            .terminal(this.isTerminal())
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .finishedAt(this.finishedAt)
            .result(this.result)
            .build();
    }

    private void finish() {
        this.finishedAt = Instant.now();
        this.touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getTenantId() {
        return this.tenantId;
    }

    @Generated
    public String getInstanceId() {
        return this.instanceId;
    }

    @Generated
    public String getRegion() {
        return this.region;
    }

    @Generated
    public String getTargetShape() {
        return this.targetShape;
    }

    @Generated
    public Float getTargetOcpus() {
        return this.targetOcpus;
    }

    @Generated
    public Float getTargetMemoryInGBs() {
        return this.targetMemoryInGBs;
    }

    @Generated
    public int getMaxRetries() {
        return this.maxRetries;
    }

    @Generated
    public long getRetryIntervalMillis() {
        return this.retryIntervalMillis;
    }

    @Generated
    public Callable<Map<String, Object>> getOperation() {
        return this.operation;
    }

    @Generated
    public Object getPauseMonitor() {
        return this.pauseMonitor;
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
    public boolean isPauseRequested() {
        return this.pauseRequested;
    }

    @Generated
    public boolean isStopRequested() {
        return this.stopRequested;
    }

    @Generated
    public Thread getThread() {
        return this.thread;
    }

    @Generated
    public Map<String, Object> getResult() {
        return this.result;
    }

    public static enum Status {
        PENDING,
        RUNNING,
        PAUSED,
        SUCCESS,
        FAILED,
        STOPPED;
    }
}
