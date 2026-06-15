/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.dto.ShapeEditTaskStatus
 *  com.ociworker.model.entity.ShapeEditTask
 *  com.ociworker.model.entity.ShapeEditTask$Status
 *  lombok.Generated
 */
package com.ociworker.model.entity;

import com.ociworker.model.dto.ShapeEditTaskStatus;
import com.ociworker.model.entity.ShapeEditTask;
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
    private final Instant createdAt;
    private volatile Instant updatedAt = this.createdAt = Instant.now();
    private volatile Instant finishedAt;
    private volatile Status status = Status.PENDING;
    private volatile String message = "\u68c0\u6d4b\u5230\u7f3a\u8d27\uff0c\u5c06\u5728\u540e\u53f0\u81ea\u52a8\u91cd\u8bd5";
    private volatile int retryCount;
    private volatile boolean pauseRequested;
    private volatile boolean stopRequested;
    private volatile Thread thread;
    private volatile Map<String, Object> result;

    public ShapeEditTask(String taskId, String tenantId, String instanceId, String region, String targetShape, Float targetOcpus, Float targetMemoryInGBs, int maxRetries, long retryIntervalMillis, Callable<Map<String, Object>> operation) {
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
        this.status = Status.RUNNING;
        this.message = message;
        this.touch();
    }

    public void markWaiting(String message) {
        this.status = this.pauseRequested ? Status.PAUSED : Status.PENDING;
        this.message = message;
        this.touch();
    }

    public void markSuccess(Map<String, Object> result) {
        this.result = result;
        this.status = Status.SUCCESS;
        this.message = "\u5f62\u72b6\u53d8\u66f4\u6210\u529f";
        this.finish();
    }

    public void markFailed(String message) {
        this.status = Status.FAILED;
        this.message = message;
        this.finish();
    }

    public void markStopped(String message) {
        this.status = Status.STOPPED;
        this.message = message;
        this.finish();
    }

    public void incrementRetryCount() {
        ++this.retryCount;
        this.touch();
    }

    public void pause() {
        if (this.isTerminal()) {
            return;
        }
        this.pauseRequested = true;
        this.status = Status.PAUSED;
        this.message = "\u5df2\u6682\u505c";
        this.touch();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resume() {
        if (this.isTerminal()) {
            return;
        }
        Object object = this.pauseMonitor;
        synchronized (object) {
            this.pauseRequested = false;
            this.status = Status.PENDING;
            this.message = "\u5df2\u6062\u590d\uff0c\u7b49\u5f85\u4e0b\u4e00\u6b21\u91cd\u8bd5";
            this.touch();
            this.pauseMonitor.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        if (this.isTerminal()) {
            return;
        }
        this.stopRequested = true;
        Thread t = this.thread;
        if (t != null) {
            t.interrupt();
        }
        Object object = this.pauseMonitor;
        synchronized (object) {
            this.pauseMonitor.notifyAll();
        }
        this.markStopped("\u5df2\u505c\u6b62");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean awaitIfPaused() throws InterruptedException {
        Object object = this.pauseMonitor;
        synchronized (object) {
            while (this.pauseRequested && !this.stopRequested) {
                this.status = Status.PAUSED;
                this.message = "\u5df2\u6682\u505c";
                this.touch();
                this.pauseMonitor.wait();
            }
        }
        return this.stopRequested;
    }

    public boolean isTerminal() {
        return this.status == Status.SUCCESS || this.status == Status.FAILED || this.status == Status.STOPPED;
    }

    public ShapeEditTaskStatus toStatus() {
        return ShapeEditTaskStatus.builder().taskId(this.taskId).tenantId(this.tenantId).instanceId(this.instanceId).region(this.region).status(this.status).message(this.message).retryCount(this.retryCount).maxRetries(this.maxRetries).pending(this.status == Status.PENDING || this.status == Status.RUNNING || this.status == Status.PAUSED).paused(this.status == Status.PAUSED).stopped(this.status == Status.STOPPED).terminal(this.isTerminal()).createdAt(this.createdAt).updatedAt(this.updatedAt).finishedAt(this.finishedAt).result(this.result).build();
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
    public Status getStatus() {
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
}

