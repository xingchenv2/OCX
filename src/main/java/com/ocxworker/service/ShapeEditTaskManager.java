package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.dto.ShapeEditTaskStatus;
import com.ocxworker.model.entity.ShapeEditTask;
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
    private final ConcurrentHashMap<String, ShapeEditTask> tasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> instanceTaskIndex = new ConcurrentHashMap<>();

    public synchronized ShapeEditTaskStatus startTask(
        String tenantId,
        String instanceId,
        String region,
        String targetShape,
        Float targetOcpus,
        Float targetMemoryInGBs,
        Callable<Map<String, Object>> operation
    ) {
        this.cleanupTerminalTasks();
        String instanceKey = instanceKey(tenantId, instanceId, region);
        String existingTaskId = this.instanceTaskIndex.get(instanceKey);
        if (existingTaskId != null) {
            ShapeEditTask existing = this.tasks.get(existingTaskId);
            if (existing != null && !existing.isTerminal()) {
                return existing.toStatus();
            }

            this.instanceTaskIndex.remove(instanceKey, existingTaskId);
        }

        String taskId = UUID.randomUUID().toString();
        ShapeEditTask task = new ShapeEditTask(taskId, tenantId, instanceId, region, targetShape, targetOcpus, targetMemoryInGBs, 480, 30000L, operation);
        String activeTaskId = this.instanceTaskIndex.putIfAbsent(instanceKey, taskId);
        if (activeTaskId != null) {
            ShapeEditTask active = this.tasks.get(activeTaskId);
            if (active != null && !active.isTerminal()) {
                return active.toStatus();
            }

            this.instanceTaskIndex.remove(instanceKey, activeTaskId);
            activeTaskId = this.instanceTaskIndex.putIfAbsent(instanceKey, taskId);
            if (activeTaskId != null) {
                ShapeEditTask current = this.tasks.get(activeTaskId);
                if (current != null) {
                    return current.toStatus();
                }
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
        ShapeEditTask old = this.tasks.get(taskId);
        if (old == null) {
            throw new OciException("形状编辑任务不存在或已过期");
        } else if (!old.isTerminal()) {
            return old.toStatus();
        } else if (old.getStatus() == ShapeEditTask.Status.SUCCESS) {
            throw new OciException("形状编辑任务已成功，无需继续重试");
        } else {
            String instanceKey = instanceKey(old.getTenantId(), old.getInstanceId(), old.getRegion());
            String existingTaskId = this.instanceTaskIndex.get(instanceKey);
            if (existingTaskId != null) {
                ShapeEditTask existing = this.tasks.get(existingTaskId);
                if (existing != null && !existing.isTerminal()) {
                    return existing.toStatus();
                }

                this.instanceTaskIndex.remove(instanceKey, existingTaskId);
            }

            String newTaskId = UUID.randomUUID().toString();
            ShapeEditTask task = new ShapeEditTask(
                newTaskId,
                old.getTenantId(),
                old.getInstanceId(),
                old.getRegion(),
                old.getTargetShape(),
                old.getTargetOcpus(),
                old.getTargetMemoryInGBs(),
                480,
                30000L,
                old.getOperation()
            );
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
    }

    public boolean tryHandleTelegramCallback(String rawData, String callbackQueryId, String answeringBotToken) {
        if (rawData != null && rawData.startsWith("se|")) {
            String taskId = rawData.substring("se|".length());
            if (taskId.length() > 64) {
                this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "无效任务", false, answeringBotToken);
                return true;
            } else {
                try {
                    ShapeEditTaskStatus status = this.restartTask(taskId);
                    this.notificationService.answerTelegramCallbackQuery(callbackQueryId, "已继续后台重试: " + status.getTaskId(), false, answeringBotToken);
                } catch (Exception var6) {
                    this.notificationService
                        .answerTelegramCallbackQuery(callbackQueryId, var6.getMessage() == null ? "无法继续重试" : var6.getMessage(), true, answeringBotToken);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public ShapeEditTaskStatus getStatus(String taskId) {
        this.cleanupTerminalTasks();
        ShapeEditTask task = this.tasks.get(taskId);
        if (task == null) {
            throw new OciException("形状编辑任务不存在或已结束");
        } else {
            return task.toStatus();
        }
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
        if (e == null) {
            return false;
        } else {
            String msg = e.getMessage();
            String serviceCode = "";
            if (e instanceof BmcException bmcException && bmcException.getServiceCode() != null) {
                serviceCode = bmcException.getServiceCode();
            }

            String text = ((serviceCode == null ? "" : serviceCode) + " " + (msg == null ? "" : msg)).toLowerCase(Locale.ROOT);
            return text.contains("outofhostcapacity")
                || text.contains("out of host capacity")
                || text.contains("out of capacity")
                || text.contains("insufficient capacity")
                || text.contains("capacity is not available")
                || text.contains("no available host")
                || text.contains("缺货")
                || text.contains("容量不足");
        }
    }

    private void runTask(ShapeEditTask task, String instanceKey) {
        try {
            try {
                while (!task.isStopRequested() && task.getRetryCount() < task.getMaxRetries()) {
                    if (this.sleepBeforeRetry(task)) {
                        task.markStopped("已停止");
                        return;
                    }

                    if (task.awaitIfPaused()) {
                        task.markStopped("已停止");
                        return;
                    }

                    task.incrementRetryCount();
                    task.markRunning("重试中 (第 " + task.getRetryCount() + " 次)");
                    this.logTaskEvent("retrying", task, null);

                    try {
                        Map<String, Object> result = task.getOperation().call();
                        if (!task.isStopRequested() && !task.isTerminal()) {
                            task.markSuccess(result);
                            this.logTaskEvent("success", task, null);
                            this.notifyTaskSuccess(task);
                            return;
                        }

                        return;
                    } catch (Throwable var9) {
                        if (task.isStopRequested() || task.isTerminal()) {
                            return;
                        }

                        if (isOutOfStock(var9)) {
                            task.markWaiting("仍然缺货，等待下一次重试 (第 " + task.getRetryCount() + " 次)");
                            this.logTaskEvent("out_of_stock", task, var9);
                        } else {
                            task.markFailed("失败: " + briefMessage(var9));
                            this.logTaskEvent("failed", task, var9);
                            this.notifyTaskFailed(task);
                            return;
                        }
                    }
                }

                if (!task.isTerminal()) {
                    task.markStopped("重试超时，已自动停止");
                    this.logTaskEvent("timeout_stopped", task, null);
                    this.notifyTaskStopped(task);
                }

                return;
            } catch (InterruptedException var10) {
                if (!task.isTerminal()) {
                    task.markStopped("已停止");
                    this.logTaskEvent("interrupted_stopped", task, var10);
                    this.notifyTaskStopped(task);
                }

                Thread.currentThread().interrupt();
            } catch (Throwable var11) {
                if (!task.isTerminal()) {
                    task.markFailed("失败: " + briefMessage(var11));
                    this.logTaskEvent("failed", task, var11);
                    this.notifyTaskFailed(task);
                    return;
                }
            }
        } finally {
            this.instanceTaskIndex.remove(instanceKey, task.getTaskId());
        }
    }

    private boolean sleepBeforeRetry(ShapeEditTask task) throws InterruptedException {
        long slept = 0L;

        while (slept < task.getRetryIntervalMillis()) {
            if (task.isStopRequested()) {
                return true;
            }

            if (task.awaitIfPaused()) {
                return true;
            }

            long step = Math.min(1000L, task.getRetryIntervalMillis() - slept);
            task.markWaiting("等待中，" + (task.getRetryIntervalMillis() - slept + 999L) / 1000L + " 秒后重试");
            Thread.sleep(step);
            slept += step;
        }

        return task.isStopRequested();
    }

    private void logTaskEvent(String event, ShapeEditTask task, Throwable error) {
        String errorText = error == null ? "" : briefMessage(error);
        String msg = "Shape edit task event={} taskId={} tenantId={} region={} instanceId={} targetShape={} ocpus={} memoryInGBs={} retry={}/{} status={} message={} error={}";
        if (!"failed".equals(event) && !event.contains("stopped")) {
            log.info(
                msg,
                new Object[]{
                    event,
                    task.getTaskId(),
                    task.getTenantId(),
                    task.getRegion(),
                    task.getInstanceId(),
                    task.getTargetShape(),
                    task.getTargetOcpus(),
                    task.getTargetMemoryInGBs(),
                    task.getRetryCount(),
                    task.getMaxRetries(),
                    task.getStatus(),
                    task.getMessage(),
                    errorText
                }
            );
        } else {
            log.warn(
                msg,
                new Object[]{
                    event,
                    task.getTaskId(),
                    task.getTenantId(),
                    task.getRegion(),
                    task.getInstanceId(),
                    task.getTargetShape(),
                    task.getTargetOcpus(),
                    task.getTargetMemoryInGBs(),
                    task.getRetryCount(),
                    task.getMaxRetries(),
                    task.getStatus(),
                    task.getMessage(),
                    errorText
                }
            );
        }
    }

    private void notifyTaskCreated(ShapeEditTask task) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("实例形状后台重试已创建", task, null));
    }

    private void notifyTaskContinued(ShapeEditTask task, String oldTaskId) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("实例形状后台重试已继续", task, "来源任务: " + oldTaskId));
    }

    private void notifyTaskSuccess(ShapeEditTask task) {
        this.notificationService.sendHtmlWithType("instance", this.taskHtml("实例形状修改成功", task, this.resultLine(task)));
    }

    private void notifyTaskFailed(ShapeEditTask task) {
        this.notificationService.sendHtmlWithTypeAndInlineKeyboard("instance", this.taskHtml("实例形状后台重试失败", task, null), this.retryKeyboard(task));
    }

    private void notifyTaskStopped(ShapeEditTask task) {
        this.notificationService.sendHtmlWithTypeAndInlineKeyboard("instance", this.taskHtml("实例形状后台重试已停止", task, null), this.retryKeyboard(task));
    }

    private List<List<Map<String, String>>> retryKeyboard(ShapeEditTask task) {
        return List.of(List.of(Map.of("text", "继续重试形状编辑", "callback_data", "se|" + task.getTaskId())));
    }

    private String taskHtml(String title, ShapeEditTask task, String extraLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(html(title)).append("</b>\n\n");
        sb.append("任务ID: <code>").append(html(task.getTaskId())).append("</code>\n");
        sb.append("租户ID: <code>").append(html(task.getTenantId())).append("</code>\n");
        sb.append("区域: <code>").append(html(display(task.getRegion()))).append("</code>\n");
        sb.append("实例ID: <code>").append(html(task.getInstanceId())).append("</code>\n");
        sb.append("目标 Shape: <code>").append(html(display(task.getTargetShape()))).append("</code>\n");
        sb.append("OCPU/内存: <code>")
            .append(html(display(task.getTargetOcpus())))
            .append(" / ")
            .append(html(display(task.getTargetMemoryInGBs())))
            .append(" GB</code>\n");
        sb.append("重试进度: <code>").append(task.getRetryCount()).append("/").append(task.getMaxRetries()).append("</code>\n");
        sb.append("状态: <code>").append(html(String.valueOf(task.getStatus()))).append("</code>\n");
        sb.append("消息: ").append(html(display(task.getMessage())));
        if (extraLine != null && !extraLine.isBlank()) {
            sb.append("\n").append(html(extraLine));
        }

        return sb.toString();
    }

    private String resultLine(ShapeEditTask task) {
        Map<String, Object> result = task.getResult();
        if (result != null && !result.isEmpty()) {
            Object shape = result.get("shape");
            Object ocpus = result.get("ocpus");
            Object memory = result.get("memoryInGBs");
            return shape == null && ocpus == null && memory == null
                ? null
                : "实际结果: " + display(shape) + " / " + display(ocpus) + " OCPU / " + display(memory) + " GB";
        } else {
            return null;
        }
    }

    private static String display(Object value) {
        return value != null && !String.valueOf(value).isBlank() ? String.valueOf(value) : "-";
    }

    private static String html(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private ShapeEditTask taskOrThrow(String taskId) {
        this.cleanupTerminalTasks();
        ShapeEditTask task = this.tasks.get(taskId);
        if (task == null) {
            throw new OciException("形状编辑任务不存在或已结束");
        } else {
            return task;
        }
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
        return safe(tenantId) + "|" + safe(region) + "|" + safe(instanceId);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String briefMessage(Throwable e) {
        String msg = e.getMessage();
        if (msg != null && !msg.isBlank()) {
            return msg.length() > 180 ? msg.substring(0, 180) + "..." : msg;
        } else {
            return e.getClass().getSimpleName();
        }
    }
}
