/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.model.dto.ShapeEditTaskStatus
 *  com.ocxworker.model.entity.ShapeEditTask
 *  com.ocxworker.model.entity.ShapeEditTask$Status
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.ShapeEditTaskManager
 *  com.oracle.bmc.model.BmcException
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.dto.ShapeEditTaskStatus;
import com.ocxworker.model.entity.ShapeEditTask;
import com.ocxworker.service.NotificationService;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class ShapeEditTaskManager {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ShapeEditTaskManager.class);
    private static final int MAX_RETRIES = 480;
    private static final long RETRY_INTERVAL_MILLIS = 30000L;
    private static final Duration TERMINAL_TTL = Duration.ofHours(2L);
    private static final String CALLBACK_RETRY_PREFIX = "se|";
    @Resource
    private NotificationService notificationService;
    private final ConcurrentHashMap<String, ShapeEditTask> tasks = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, String> instanceTaskIndex = new ConcurrentHashMap();

    public synchronized ShapeEditTaskStatus startTask(String tenantId, String instanceId, String region, String targetShape, Float targetOcpus, Float targetMemoryInGBs, Callable<Map<String, Object>> operation) {
        this.cleanupTerminalTasks();
        String instanceKey = ShapeEditTaskManager.instanceKey((String)tenantId, (String)instanceId, (String)region);
        String existingTaskId = (String)this.instanceTaskIndex.get(instanceKey);
        if (existingTaskId != null) {
            ShapeEditTask existing = (ShapeEditTask)this.tasks.get(existingTaskId);
            if (existing != null && !existing.isTerminal()) {
                return existing.toStatus();
            }
            this.instanceTaskIndex.remove(instanceKey, existingTaskId);
        }
        String taskId = UUID.randomUUID().toString();
        ShapeEditTask task = new ShapeEditTask(taskId, tenantId, instanceId, region, targetShape, targetOcpus, targetMemoryInGBs, 480, 30000L, operation);
        String activeTaskId = this.instanceTaskIndex.putIfAbsent(instanceKey, taskId);
        if (activeTaskId != null) {
            ShapeEditTask current;
            ShapeEditTask active = (ShapeEditTask)this.tasks.get(activeTaskId);
            if (active != null && !active.isTerminal()) {
                return active.toStatus();
            }
            this.instanceTaskIndex.remove(instanceKey, activeTaskId);
            activeTaskId = this.instanceTaskIndex.putIfAbsent(instanceKey, taskId);
            if (activeTaskId != null && (current = (ShapeEditTask)this.tasks.get(activeTaskId)) != null) {
                return current.toStatus();
            }
        }
        this.tasks.put(taskId, task);
        Thread worker = new Thread(() -> this.runTask(task, instanceKey), "shape-edit-task-" + taskId);
        worker.setDaemon(true);
        task.bindThread(worker);
        worker.start();
        this.logTaskEvent("created", task, null);
        this.notifyTaskCreated(task);
        return task.toStatus();
    }

    public synchronized ShapeEditTaskStatus restartTask(String taskId) {
        this.cleanupTerminalTasks();
        ShapeEditTask old = (ShapeEditTask)this.tasks.get(taskId);
        if (old == null) {
            throw new OciException("\u5f62\u72b6\u7f16\u8f91\u4efb\u52a1\u4e0d\u5b58\u5728\u6216\u5df2\u8fc7\u671f");
        }
        if (!old.isTerminal()) {
            return old.toStatus();
        }
        if (old.getStatus() == ShapeEditTask.Status.SUCCESS) {
            throw new OciException("\u5f62\u72b6\u7f16\u8f91\u4efb\u52a1\u5df2\u6210\u529f\uff0c\u65e0\u9700\u7ee7\u7eed\u91cd\u8bd5");
        }
        String instanceKey = ShapeEditTaskManager.instanceKey((String)old.getTenantId(), (String)old.getInstanceId(), (String)old.getRegion());
        String existingTaskId = (String)this.instanceTaskIndex.get(instanceKey);
        if (existingTaskId != null) {
            ShapeEditTask existing = (ShapeEditTask)this.tasks.get(existingTaskId);
            if (existing != null && !existing.isTerminal()) {
                return existing.toStatus();
            }
            this.instanceTaskIndex.remove(instanceKey, existingTaskId);
        }
        String newTaskId = UUID.randomUUID().toString();
        ShapeEditTask task = new ShapeEditTask(newTaskId, old.getTenantId(), old.getInstanceId(), old.getRegion(), old.getTargetShape(), old.getTargetOcpus(), old.getTargetMemoryInGBs(), 480, 30000L, old.getOperation());
        this.instanceTaskIndex.put(instanceKey, newTaskId);
        this.tasks.put(newTaskId, task);
        Thread worker = new Thread(() -> this.runTask(task, instanceKey), "shape-edit-task-" + newTaskId);
        worker.setDaemon(true);
        task.bindThread(worker);
        worker.start();
        this.logTaskEvent("continued", task, null);
        this.notifyTaskContinued(task, old.getTaskId());
        return task.toStatus();
    }

    public boolean tryHandleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (rawData == null || !rawData.startsWith("se|")) {
            return false;
        }
        String taskId = rawData.substring("se|".length());
        if (taskId.length() > 64) {
            this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "\u65e0\u6548\u4efb\u52a1", false, answeringBotToken);
            return true;
        }
        try {
            ShapeEditTaskStatus status = this.restartTask(taskId);
            this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "\u5df2\u7ee7\u7eed\u540e\u53f0\u91cd\u8bd5: " + status.getTaskId(), false, answeringBotToken);
        }
        catch (Exception e) {
            this.notificationService.answerTelegramCallbackQuery(callbackQueryId, e.getMessage() == null ? "\u65e0\u6cd5\u7ee7\u7eed\u91cd\u8bd5" : e.getMessage(), true, answeringBotToken);
        }
        return true;
    }

    public ShapeEditTaskStatus getStatus(String taskId) {
        this.cleanupTerminalTasks();
        ShapeEditTask task = (ShapeEditTask)this.tasks.get(taskId);
        if (task == null) {
            throw new OciException("\u5f62\u72b6\u7f16\u8f91\u4efb\u52a1\u4e0d\u5b58\u5728\u6216\u5df2\u7ed3\u675f");
        }
        return task.toStatus();
    }

    public ShapeEditTaskStatus pause(String taskId) {
        ShapeEditTask task = this.taskOrThrow(taskId);
        task.pause();
        return task.toStatus();
    }

    public ShapeEditTaskStatus resume(String taskId) {
        ShapeEditTask task = this.taskOrThrow(taskId);
        task.resume();
        return task.toStatus();
    }

    public ShapeEditTaskStatus stop(String taskId) {
        ShapeEditTask task = this.taskOrThrow(taskId);
        boolean wasTerminal = task.isTerminal();
        task.stop();
        if (!wasTerminal) {
            this.logTaskEvent("stopped", task, null);
            this.notifyTaskStopped(task);
        }
        return task.toStatus();
    }

    public static boolean isOutOfStock(Throwable e) {
        BmcException bmcException;
        if (e == null) {
            return false;
        }
        String msg = e.getMessage();
        String serviceCode = "";
        if (e instanceof BmcException && (bmcException = (BmcException)e).getServiceCode() != null) {
            serviceCode = bmcException.getServiceCode();
        }
        String text = ((serviceCode == null ? "" : serviceCode) + " " + (msg == null ? "" : msg)).toLowerCase(Locale.ROOT);
        return text.contains("outofhostcapacity") || text.contains("out of host capacity") || text.contains("out of capacity") || text.contains("insufficient capacity") || text.contains("capacity is not available") || text.contains("no available host") || text.contains("\u7f3a\u8d27") || text.contains("\u5bb9\u91cf\u4e0d\u8db3");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void runTask(ShapeEditTask task, String instanceKey) {
        try {
            while (!task.isStopRequested() && task.getRetryCount() < task.getMaxRetries()) {
                Map result;
                block21: {
                    if (this.sleepBeforeRetry(task)) {
                        task.markStopped("\u5df2\u505c\u6b62");
                        return;
                    }
                    if (task.awaitIfPaused()) {
                        task.markStopped("\u5df2\u505c\u6b62");
                        return;
                    }
                    task.incrementRetryCount();
                    task.markRunning("\u91cd\u8bd5\u4e2d (\u7b2c " + task.getRetryCount() + " \u6b21)");
                    this.logTaskEvent("retrying", task, null);
                    try {
                        result = (Map)task.getOperation().call();
                        if (!task.isStopRequested() && !task.isTerminal()) break block21;
                        return;
                    }
                    catch (Throwable e) {
                        if (task.isStopRequested() || task.isTerminal()) {
                            return;
                        }
                        if (!ShapeEditTaskManager.isOutOfStock((Throwable)e)) {
                            task.markFailed("\u5931\u8d25: " + ShapeEditTaskManager.briefMessage((Throwable)e));
                            this.logTaskEvent("failed", task, e);
                            this.notifyTaskFailed(task);
                            return;
                        }
                        task.markWaiting("\u4ecd\u7136\u7f3a\u8d27\uff0c\u7b49\u5f85\u4e0b\u4e00\u6b21\u91cd\u8bd5 (\u7b2c " + task.getRetryCount() + " \u6b21)");
                        this.logTaskEvent("out_of_stock", task, e);
                        continue;
                    }
                }
                task.markSuccess(result);
                this.logTaskEvent("success", task, null);
                this.notifyTaskSuccess(task);
                return;
            }
            if (task.isTerminal()) return;
        }
        catch (InterruptedException e) {
            if (!task.isTerminal()) {
                task.markStopped("\u5df2\u505c\u6b62");
                this.logTaskEvent("interrupted_stopped", task, (Throwable)e);
                this.notifyTaskStopped(task);
            }
            Thread.currentThread().interrupt();
            return;
        }
        catch (Throwable e) {
            if (task.isTerminal()) return;
            task.markFailed("\u5931\u8d25: " + ShapeEditTaskManager.briefMessage((Throwable)e));
            this.logTaskEvent("failed", task, e);
            this.notifyTaskFailed(task);
            return;
        }
        finally {
            this.instanceTaskIndex.remove(instanceKey, task.getTaskId());
        }
        task.markStopped("\u91cd\u8bd5\u8d85\u65f6\uff0c\u5df2\u81ea\u52a8\u505c\u6b62");
        this.logTaskEvent("timeout_stopped", task, null);
        this.notifyTaskStopped(task);
        return;
    }

    private boolean sleepBeforeRetry(ShapeEditTask task) throws InterruptedException {
        long step;
        for (long slept = 0L; slept < task.getRetryIntervalMillis(); slept += step) {
            if (task.isStopRequested()) {
                return true;
            }
            if (task.awaitIfPaused()) {
                return true;
            }
            step = Math.min(1000L, task.getRetryIntervalMillis() - slept);
            task.markWaiting("\u7b49\u5f85\u4e2d\uff0c" + (task.getRetryIntervalMillis() - slept + 999L) / 1000L + " \u79d2\u540e\u91cd\u8bd5");
            Thread.sleep(step);
        }
        return task.isStopRequested();
    }

    private void logTaskEvent(String event, ShapeEditTask task, Throwable error) {
        String errorText = error == null ? "" : ShapeEditTaskManager.briefMessage((Throwable)error);
        String msg = "Shape edit task event={} taskId={} tenantId={} region={} instanceId={} targetShape={} ocpus={} memoryInGBs={} retry={}/{} status={} message={} error={}";
        if ("failed".equals(event) || event.contains("stopped")) {
            log.warn(msg, new Object[]{event, task.getTaskId(), task.getTenantId(), task.getRegion(), task.getInstanceId(), task.getTargetShape(), task.getTargetOcpus(), task.getTargetMemoryInGBs(), task.getRetryCount(), task.getMaxRetries(), task.getStatus(), task.getMessage(), errorText});
            return;
        }
        log.info(msg, new Object[]{event, task.getTaskId(), task.getTenantId(), task.getRegion(), task.getInstanceId(), task.getTargetShape(), task.getTargetOcpus(), task.getTargetMemoryInGBs(), task.getRetryCount(), task.getMaxRetries(), task.getStatus(), task.getMessage(), errorText});
    }

    private void notifyTaskCreated(ShapeEditTask task) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("\u5b9e\u4f8b\u5f62\u72b6\u540e\u53f0\u91cd\u8bd5\u5df2\u521b\u5efa", task, null));
    }

    private void notifyTaskContinued(ShapeEditTask task, String oldTaskId) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("\u5b9e\u4f8b\u5f62\u72b6\u540e\u53f0\u91cd\u8bd5\u5df2\u7ee7\u7eed", task, "\u6765\u6e90\u4efb\u52a1: " + oldTaskId));
    }

    private void notifyTaskSuccess(ShapeEditTask task) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("\u5b9e\u4f8b\u5f62\u72b6\u4fee\u6539\u6210\u529f", task, this.resultLine(task)));
    }

    private void notifyTaskFailed(ShapeEditTask task) {
        this.notificationService.sendHtmlWithTypeAndInlineKeyboard("instance", this.taskHtml("\u5b9e\u4f8b\u5f62\u72b6\u540e\u53f0\u91cd\u8bd5\u5931\u8d25", task, null), this.retryKeyboard(task));
    }

    private void notifyTaskStopped(ShapeEditTask task) {
        this.notificationService.sendHtmlWithTypeAndInlineKeyboard("instance", this.taskHtml("\u5b9e\u4f8b\u5f62\u72b6\u540e\u53f0\u91cd\u8bd5\u5df2\u505c\u6b62", task, null), this.retryKeyboard(task));
    }

    private List<List<Map<String, String>>> retryKeyboard(ShapeEditTask task) {
        return List.of(List.of(Map.of("text", "\u7ee7\u7eed\u91cd\u8bd5\u5f62\u72b6\u7f16\u8f91", "callback_data", "se|" + task.getTaskId())));
    }

    private String taskHtml(String title, ShapeEditTask task, String extraLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(ShapeEditTaskManager.html((String)title)).append("</b>\n\n");
        sb.append("\u4efb\u52a1ID: <code>").append(ShapeEditTaskManager.html((String)task.getTaskId())).append("</code>\n");
        sb.append("\u79df\u6237ID: <code>").append(ShapeEditTaskManager.html((String)task.getTenantId())).append("</code>\n");
        sb.append("\u533a\u57df: <code>").append(ShapeEditTaskManager.html((String)ShapeEditTaskManager.display((Object)task.getRegion()))).append("</code>\n");
        sb.append("\u5b9e\u4f8bID: <code>").append(ShapeEditTaskManager.html((String)task.getInstanceId())).append("</code>\n");
        sb.append("\u76ee\u6807 Shape: <code>").append(ShapeEditTaskManager.html((String)ShapeEditTaskManager.display((Object)task.getTargetShape()))).append("</code>\n");
        sb.append("OCPU/\u5185\u5b58: <code>").append(ShapeEditTaskManager.html((String)ShapeEditTaskManager.display((Object)task.getTargetOcpus()))).append(" / ").append(ShapeEditTaskManager.html((String)ShapeEditTaskManager.display((Object)task.getTargetMemoryInGBs()))).append(" GB</code>\n");
        sb.append("\u91cd\u8bd5\u8fdb\u5ea6: <code>").append(task.getRetryCount()).append("/").append(task.getMaxRetries()).append("</code>\n");
        sb.append("\u72b6\u6001: <code>").append(ShapeEditTaskManager.html((String)String.valueOf(task.getStatus()))).append("</code>\n");
        sb.append("\u6d88\u606f: ").append(ShapeEditTaskManager.html((String)ShapeEditTaskManager.display((Object)task.getMessage())));
        if (extraLine != null && !extraLine.isBlank()) {
            sb.append("\n").append(ShapeEditTaskManager.html((String)extraLine));
        }
        return sb.toString();
    }

    private String resultLine(ShapeEditTask task) {
        Map result = task.getResult();
        if (result == null || result.isEmpty()) {
            return null;
        }
        Object shape = result.get("shape");
        Object ocpus = result.get("ocpus");
        Object memory = result.get("memoryInGBs");
        if (shape == null && ocpus == null && memory == null) {
            return null;
        }
        return "\u5b9e\u9645\u7ed3\u679c: " + ShapeEditTaskManager.display(shape) + " / " + ShapeEditTaskManager.display(ocpus) + " OCPU / " + ShapeEditTaskManager.display(memory) + " GB";
    }

    private static String display(Object value) {
        return value == null || String.valueOf(value).isBlank() ? "-" : String.valueOf(value);
    }

    private static String html(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private ShapeEditTask taskOrThrow(String taskId) {
        this.cleanupTerminalTasks();
        ShapeEditTask task = (ShapeEditTask)this.tasks.get(taskId);
        if (task == null) {
            throw new OciException("\u5f62\u72b6\u7f16\u8f91\u4efb\u52a1\u4e0d\u5b58\u5728\u6216\u5df2\u7ed3\u675f");
        }
        return task;
    }

    private void cleanupTerminalTasks() {
        Instant now = Instant.now();
        this.tasks.forEach((taskId, task) -> {
            Instant finishedAt = task.getFinishedAt();
            if (task.isTerminal() && finishedAt != null && Duration.between(finishedAt, now).compareTo(TERMINAL_TTL) > 0) {
                this.tasks.remove(taskId, task);
            }
        });
    }

    private static String instanceKey(String tenantId, String instanceId, String region) {
        return ShapeEditTaskManager.safe((String)tenantId) + "|" + ShapeEditTaskManager.safe((String)region) + "|" + ShapeEditTaskManager.safe((String)instanceId);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String briefMessage(Throwable e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return msg.length() > 180 ? msg.substring(0, 180) + "..." : msg;
    }
}

