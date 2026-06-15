package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.config.VirtualThreadConfig;
import com.ocxworker.enums.TaskStatusEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciCreateTaskMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.InstanceDetailDTO;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciCreateTask;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.model.params.PageParams;
import com.ocxworker.util.BootVolumeVpusUtil;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.OciRegionUtil;
import com.ocxworker.util.ShapeFlexLimitsUtil;
import com.ocxworker.util.ShapeSeriesUtil;
import com.ocxworker.websocket.LogWebSocketHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn({"databaseGuardService"})
public class TaskSchedulerService implements SmartLifecycle {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private NotificationService notificationService;
    private final Map<String, Future<?>> taskMap = new ConcurrentHashMap<>();
    private final Set<String> runningTasks = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, Set<String>> taskExcludedAds = new ConcurrentHashMap<>();
    private static final ObjectMapper JSON = new ObjectMapper();
    private volatile boolean lifecycleRunning = false;

    @PostConstruct
    public void init() {
        this.repairInconsistentRunningTasks();
        List<OciCreateTask> runningTaskList = this.taskMapper
            .selectList((Wrapper)new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()));
        if (!runningTaskList.isEmpty()) {
            log.info("Restoring {} running tasks from database...", runningTaskList.size());

            for (OciCreateTask task : runningTaskList) {
                try {
                    OciUser ociUser = (OciUser)this.userMapper.selectById(task.getUserId());
                    if (ociUser == null) {
                        task.setStatus(TaskStatusEnum.FAILED.getStatus());
                        this.taskMapper.updateById(task);
                    } else {
                        SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
                        this.scheduleTask(task.getId(), dto, task.getIntervalSeconds());
                        this.broadcastLog(
                            String.format(
                                "【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - 服务重启，恢复任务执行", ociUser.getUsername(), ociUser.getOciRegion(), task.getArchitecture()
                            )
                        );
                    }
                } catch (Exception var6) {
                    log.error("Failed to restore task {}: {}", task.getId(), var6.getMessage());
                    task.setStatus(TaskStatusEnum.FAILED.getStatus());
                    this.taskMapper.updateById(task);
                }
            }
        }
    }

    public void start() {
        this.lifecycleRunning = true;
    }

    public void stop() {
        this.cancelAllBootTasksForShutdown();
        this.lifecycleRunning = false;
    }

    public boolean isRunning() {
        return this.lifecycleRunning;
    }

    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void cancelAllBootTasksForShutdown() {
        if (!this.taskMap.isEmpty()) {
            int n = this.taskMap.size();

            for (Future<?> future : new ArrayList<>(this.taskMap.values())) {
                future.cancel(true);
            }

            this.taskMap.clear();
            log.info("【开机任务】应用关闭，已取消 {} 个调度中的虚拟线程（库中 RUNNING 未改，重启后将恢复）", n);
        }
    }

    public boolean hasRunningTask(String userId) {
        return this.taskMapper
                .selectCount(
                    (Wrapper)(new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getUserId, userId))
                        .eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus())
                )
            > 0L;
    }

    public Page<Map<String, Object>> listTasks(PageParams params) {
        this.repairInconsistentRunningTasks();
        this.cleanExpiredTasks();
        Page<OciCreateTask> page = new Page((long)params.getCurrent(), (long)params.getSize());
        LambdaQueryWrapper<OciCreateTask> wrapper = new LambdaQueryWrapper<>();
        if (params.getStatus() != null && !params.getStatus().isEmpty()) {
            wrapper.eq(OciCreateTask::getStatus, params.getStatus());
        }

        if (params.getKeyword() != null && !params.getKeyword().isBlank()) {
            String kw = params.getKeyword();
            List<OciUser> matchedUsers = this.userMapper.selectList((Wrapper)new LambdaQueryWrapper<OciUser>().like(OciUser::getUsername, kw));
            List<String> matchedUserIds = matchedUsers.stream().map(OciUser::getId).toList();
            wrapper.and(
                w -> {
                    ((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)w.like(OciCreateTask::getOciRegion, kw)).or())
                                .like(OciCreateTask::getArchitecture, kw))
                            .or())
                        .like(OciCreateTask::getOperationSystem, kw);
                    if (!matchedUserIds.isEmpty()) {
                        ((LambdaQueryWrapper)w.or()).in(OciCreateTask::getUserId, matchedUserIds);
                    }
                }
            );
        }

        wrapper.orderByDesc(OciCreateTask::getCreateTime);
        Page<OciCreateTask> result = (Page<OciCreateTask>)this.taskMapper.selectPage(page, wrapper);
        Page<Map<String, Object>> enriched = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        enriched.setRecords(result.getRecords().stream().map(task -> {
            Map<String, Object> map = new LinkedHashMap<>();
            OciUser user = (OciUser)this.userMapper.selectById(task.getUserId());
            map.put("id", task.getId());
            map.put("userId", task.getUserId());
            map.put("username", user != null ? user.getUsername() : "unknown");
            map.put("ociRegion", task.getOciRegion());
            map.put("ocpus", task.getOcpus());
            map.put("memory", task.getMemory());
            map.put("disk", task.getDisk());
            map.put("vpusPerGB", BootVolumeVpusUtil.normalize(task.getVpusPerGB()));
            map.put("architecture", task.getArchitecture());
            map.put("intervalSeconds", task.getIntervalSeconds());
            map.put("createNumbers", task.getCreateNumbers());
            map.put("operationSystem", task.getOperationSystem());
            map.put("customScript", task.getCustomScript());
            map.put("assignPublicIp", task.getAssignPublicIp() != null ? task.getAssignPublicIp() : true);
            map.put("assignIpv6", task.getAssignIpv6() != null ? task.getAssignIpv6() : false);
            map.put("status", task.getStatus());
            map.put("attemptCount", task.getAttemptCount());
            int scL = task.getSuccessCount() != null ? task.getSuccessCount() : 0;
            int tgtL = task.getCreateNumbers() != null && task.getCreateNumbers() > 0 ? task.getCreateNumbers() : 1;
            map.put("successCount", scL);
            int recL = this.parseCreatedInstances(task.getCreatedInstances()).size();
            map.put("recordedInstanceCount", recL);
            map.put("progressOverTarget", scL > tgtL || recL > tgtL);
            map.put("createTime", task.getCreateTime());
            return map;
        }).toList());
        return enriched;
    }

    public void createTask(
        String userId,
        String architecture,
        Double ocpus,
        Double memory,
        Integer disk,
        Integer vpusPerGB,
        Integer createNumbers,
        Integer interval,
        String rootPassword,
        String operationSystem,
        String customScript,
        Boolean assignPublicIp,
        Boolean assignIpv6,
        String ociRegionOverride
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            String effectiveRegion = StrUtil.trimToNull(ociRegionOverride);
            if (effectiveRegion == null) {
                effectiveRegion = OciRegionUtil.publicRegionId(ociUser.getOciRegion());
            } else {
                effectiveRegion = OciRegionUtil.publicRegionId(effectiveRegion);
            }

            OciCreateTask task = new OciCreateTask();
            task.setId(CommonUtils.generateId());
            task.setUserId(userId);
            task.setOciRegion(effectiveRegion);
            task.setArchitecture(architecture);
            double[] normalized = ShapeFlexLimitsUtil.normalizeAndLogIfAdjusted(architecture, ocpus, memory, "创建开机任务");
            task.setOcpus(normalized[0]);
            task.setMemory(normalized[1]);
            task.setDisk(disk);
            task.setVpusPerGB(BootVolumeVpusUtil.normalize(vpusPerGB));
            task.setCreateNumbers(createNumbers);
            task.setIntervalSeconds(interval);
            task.setRootPassword(rootPassword);
            task.setOperationSystem(operationSystem);
            task.setCustomScript(customScript);
            task.setAssignPublicIp(assignPublicIp != null ? assignPublicIp : true);
            task.setAssignIpv6(assignIpv6 != null ? assignIpv6 : false);
            task.setStatus(TaskStatusEnum.RUNNING.getStatus());
            task.setAttemptCount(0);
            task.setSuccessCount(0);
            task.setCreateTime(LocalDateTime.now());
            this.taskMapper.insert(task);
            this.clearTaskExcludedAds(task.getId());
            SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
            this.scheduleTask(task.getId(), dto, interval);
            String series = ShapeSeriesUtil.resolveSeries(architecture);
            String logMsg = String.format("【开机任务】用户:[%s],区域:[%s],架构:[%s],数量:[%d] - 任务已创建", ociUser.getUsername(), effectiveRegion, series, createNumbers);
            this.broadcastLog(logMsg);
            String pwd = rootPassword != null ? rootPassword : "随机";
            String html = "\ud83d\udccb <b>开机任务已创建</b>\n\n\ud83d\udc64 <b>租户：</b>"
                + ociUser.getUsername()
                + "\n\ud83c\udf0d <b>区域：</b>"
                + effectiveRegion
                + "\n⚙️ <b>架构：</b>"
                + series
                + "\n"
                + targetShapeLineForNotify(architecture)
                + "\ud83d\udcca <b>配置：</b>"
                + normalized[0]
                + "C / "
                + normalized[1]
                + "GB / "
                + BootVolumeVpusUtil.formatDiskWithVpus(disk != null ? disk : 50, task.getVpusPerGB())
                + "\n\ud83d\udd22 <b>数量：</b>"
                + createNumbers
                + "\n\ud83d\udd11 <b>密码：</b><code>"
                + pwd
                + "</code>";
            this.notificationService.sendHtmlWithType("task_create", html);
        }
    }

    public void resumeTask(String taskId) {
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        if (task == null) {
            throw new OciException("任务不存在");
        } else if (TaskStatusEnum.RUNNING.getStatus().equals(task.getStatus())) {
            throw new OciException("任务已在运行中");
        } else {
            OciUser ociUser = (OciUser)this.userMapper.selectById(task.getUserId());
            if (ociUser == null) {
                throw new OciException("租户配置不存在");
            } else {
                task.setStatus(TaskStatusEnum.RUNNING.getStatus());
                this.taskMapper.updateById(task);
                this.clearTaskExcludedAds(taskId);
                SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
                this.scheduleTask(task.getId(), dto, task.getIntervalSeconds());
                this.broadcastLog(
                    String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - 任务已恢复运行", ociUser.getUsername(), task.getOciRegion(), task.getArchitecture())
                );
            }
        }
    }

    public void updateTask(
        String taskId,
        String architecture,
        Double ocpus,
        Double memory,
        Integer disk,
        Integer vpusPerGB,
        Integer createNumbers,
        Integer interval,
        String rootPassword,
        String operationSystem,
        String customScript,
        Boolean assignPublicIp,
        Boolean assignIpv6
    ) {
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        if (task == null) {
            throw new OciException("任务不存在");
        } else {
            boolean wasRunning = TaskStatusEnum.RUNNING.getStatus().equals(task.getStatus());
            if (wasRunning) {
                Future<?> future = this.taskMap.get(taskId);
                if (future != null) {
                    future.cancel(true);
                    this.taskMap.remove(taskId);
                }
            }

            if (architecture != null) {
                task.setArchitecture(architecture);
            }

            if (ocpus != null) {
                task.setOcpus(ocpus);
            }

            if (memory != null) {
                task.setMemory(memory);
            }

            if (disk != null) {
                task.setDisk(disk);
            }

            if (vpusPerGB != null) {
                task.setVpusPerGB(BootVolumeVpusUtil.normalize(vpusPerGB));
            }

            if (createNumbers != null) {
                task.setCreateNumbers(createNumbers);
            }

            if (interval != null) {
                task.setIntervalSeconds(interval);
            }

            if (rootPassword != null && !rootPassword.isBlank()) {
                task.setRootPassword(rootPassword);
            }

            if (operationSystem != null) {
                task.setOperationSystem(operationSystem);
            }

            if (customScript != null) {
                task.setCustomScript(customScript);
            }

            if (assignPublicIp != null) {
                task.setAssignPublicIp(assignPublicIp);
            }

            if (assignIpv6 != null) {
                task.setAssignIpv6(assignIpv6);
            }

            double[] normalized = ShapeFlexLimitsUtil.normalizeAndLogIfAdjusted(task.getArchitecture(), task.getOcpus(), task.getMemory(), "更新开机任务");
            task.setOcpus(normalized[0]);
            task.setMemory(normalized[1]);
            this.taskMapper.updateById(task);
            this.clearTaskExcludedAds(taskId);
            if (wasRunning) {
                OciUser ociUser = (OciUser)this.userMapper.selectById(task.getUserId());
                if (ociUser != null) {
                    SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
                    this.scheduleTask(task.getId(), dto, task.getIntervalSeconds());
                }
            }

            OciUser user = (OciUser)this.userMapper.selectById(task.getUserId());
            String name = user != null ? user.getUsername() : "unknown";
            this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s] - 任务已编辑%s", name, task.getOciRegion(), wasRunning ? "（自动重启调度）" : ""));
        }
    }

    public void deleteTask(String taskId) {
        Future<?> future = this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }

        this.taskMapper.deleteById(taskId);
        this.clearTaskExcludedAds(taskId);
    }

    public void stopTask(String taskId) {
        Future<?> future = this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }

        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(TaskStatusEnum.STOPPED.getStatus());
            this.taskMapper.updateById(task);
            OciUser user = (OciUser)this.userMapper.selectById(task.getUserId());
            String name = user != null ? user.getUsername() : "unknown";
            this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s] - 任务已手动停止", name, task.getOciRegion()));
        }

        this.clearTaskExcludedAds(taskId);
    }

    private void clearTaskExcludedAds(String taskId) {
        if (taskId != null) {
            this.taskExcludedAds.remove(taskId);
        }
    }

    private void scheduleTask(String taskId, SysUserDTO dto, int intervalSeconds) {
        int delaySec = Math.max(1, intervalSeconds);
        Future<?> future = VirtualThreadConfig.VIRTUAL_EXECUTOR.submit(() -> this.runTaskLoop(taskId, dto, delaySec));
        this.taskMap.put(taskId, future);
    }

    private void runTaskLoop(String taskId, SysUserDTO dto, int delaySec) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                OciCreateTask t = (OciCreateTask)this.taskMapper.selectById(taskId);
                if (t != null && TaskStatusEnum.RUNNING.getStatus().equals(t.getStatus())) {
                    this.executeCreate(taskId, dto, delaySec);
                    t = (OciCreateTask)this.taskMapper.selectById(taskId);
                    if (t != null && TaskStatusEnum.RUNNING.getStatus().equals(t.getStatus())) {
                        try {
                            Thread.sleep((long)delaySec * 1000L);
                            continue;
                        } catch (InterruptedException var9) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                break;
            }
        } finally {
            this.taskMap.remove(taskId);
        }
    }

    private void executeCreate(String taskId, SysUserDTO dto, int intervalSeconds) {
        if (this.runningTasks.add(taskId)) {
            String user = "";
            String region = "";
            String arch = "";

            try {
                OciCreateTask head = (OciCreateTask)this.taskMapper.selectById(taskId);
                if (head == null) {
                    return;
                }

                if (!TaskStatusEnum.RUNNING.getStatus().equals(head.getStatus())) {
                    return;
                }

                int headTarget = head.getCreateNumbers() != null && head.getCreateNumbers() > 0 ? head.getCreateNumbers() : 1;
                int headSc = head.getSuccessCount() != null ? head.getSuccessCount() : 0;
                if (headSc >= headTarget) {
                    if (TaskStatusEnum.RUNNING.getStatus().equals(head.getStatus())) {
                        this.completeTask(taskId, TaskStatusEnum.COMPLETED);
                    }

                    return;
                }

                double[] launchNorm = ShapeFlexLimitsUtil.normalizeAndLogIfAdjusted(head.getArchitecture(), head.getOcpus(), head.getMemory(), "执行开机任务");
                if (!Objects.equals(head.getOcpus(), launchNorm[0]) || !Objects.equals(head.getMemory(), launchNorm[1])) {
                    head.setOcpus(launchNorm[0]);
                    head.setMemory(launchNorm[1]);
                    this.taskMapper.updateById(head);
                }

                dto.setOcpus(launchNorm[0]);
                dto.setMemory(launchNorm[1]);
                user = dto.getUsername();
                region = dto.getOciCfg().getRegion();
                arch = dto.getArchitecture();
                String series = ShapeSeriesUtil.resolveSeries(arch);
                int attempt = this.incrementAttempt(taskId);
                this.broadcastLog(
                    String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s],开机数量:[%d],开始执行第 [%d] 次创建实例操作...", user, region, series, dto.getCreateNumbers(), attempt)
                );
                dto.setInstanceDisplayOrdinal(headSc + 1);
                Set<String> excludedAds = this.taskExcludedAds.computeIfAbsent(taskId, k -> ConcurrentHashMap.newKeySet());
                dto.setExcludedAvailabilityDomains(new HashSet<>(excludedAds));

                try (OciClientService client = new OciClientService(dto)) {
                    InstanceDetailDTO result = client.createInstanceData();
                    this.applyAdExcludedNoShapeBroadcast(taskId, user, region, arch, result, excludedAds);
                    if (result.isDie()) {
                        this.completeTask(taskId, TaskStatusEnum.FAILED);
                        this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],架构:[%s] - 认证失败(401)，任务已停止", user, region, series));
                        String html = "❌ <b>开机任务失败</b>\n\n\ud83d\udc64 <b>租户：</b>"
                            + user
                            + "\n\ud83c\udf0d <b>区域：</b>"
                            + region
                            + "\n⚙️ <b>架构：</b>"
                            + series
                            + "\n"
                            + targetShapeLineForNotify(arch)
                            + "\ud83d\udcdb <b>原因：</b>认证失败 (401)，任务已停止";
                        this.notificationService.sendHtmlWithType("task_result", html);
                        return;
                    }

                    if (result.isNoShape()) {
                        this.completeTask(taskId, TaskStatusEnum.FAILED);
                        this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - ❌ Shape 不可用，任务已停止", user, region, arch));
                        return;
                    }

                    if (result.isBootVolumeQuotaExceeded()) {
                        String hint = StrUtil.isNotBlank(result.getFailureHint()) ? result.getFailureHint() : "引导卷（启动盘）存储配额已达上限，硬盘配额用尽，创建失败";
                        this.completeTask(taskId, TaskStatusEnum.FAILED);
                        this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - ❌ %s", user, region, arch, hint));
                        return;
                    }

                    if (result.isOutOfCapacity()) {
                        this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - 各可用域容量不足，[%d]秒后将重试...", user, region, arch, intervalSeconds));
                        return;
                    }

                    if (result.isAllAdsExcludedNoShape()) {
                        this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - 各可用域均无此 Shape，[%d]秒后将重试...", user, region, arch, intervalSeconds));
                        return;
                    }

                    if (!result.isNoPubVcn()) {
                        if (result.isSuccess()) {
                            int n = this.tryIncrementSuccessCount(taskId);
                            OciCreateTask t = (OciCreateTask)this.taskMapper.selectById(taskId);
                            int targetCount = t != null && t.getCreateNumbers() != null && t.getCreateNumbers() > 0 ? t.getCreateNumbers() : 1;
                            int successCount = t != null && t.getSuccessCount() != null ? t.getSuccessCount() : 0;
                            if (n > 0) {
                                this.appendCreatedInstance(taskId, result);
                                String shapeName = StrUtil.isNotBlank(result.getShape()) ? result.getShape() : arch;
                                String successSeries = ShapeSeriesUtil.resolveSeries(shapeName);
                                this.broadcastLog(
                                    String.format(
                                        "【开机任务】用户:[%s],区域:[%s],架构:[%s] - 实例创建成功(%d/%d)！IP:%s%s",
                                        user,
                                        region,
                                        successSeries,
                                        successCount,
                                        targetCount,
                                        result.getPublicIp(),
                                        StrUtil.isNotBlank(result.getIpv6Address()) ? " IPv6:" + result.getIpv6Address() : ""
                                    )
                                );
                                String html = "\ud83c\udf89 <b>实例创建成功！</b>（"
                                    + successCount
                                    + "/"
                                    + targetCount
                                    + "）\n\n\ud83d\udc64 <b>租户：</b>"
                                    + user
                                    + "\n\ud83c\udf0d <b>区域：</b>"
                                    + region
                                    + "\n⚙️ <b>架构：</b>"
                                    + successSeries
                                    + "\n\ud83d\udcbb <b>Shape：</b><code>"
                                    + shapeName
                                    + "</code>\n\ud83d\udcca <b>配置：</b>"
                                    + result.getOcpus()
                                    + "C / "
                                    + result.getMemory()
                                    + "GB / "
                                    + result.getDisk()
                                    + "GB\n\ud83c\udf10 <b>公网IP：</b><code>"
                                    + result.getPublicIp()
                                    + "</code>\n"
                                    + (
                                        StrUtil.isNotBlank(result.getIpv6Address())
                                            ? "\ud83c\udf10 <b>IPv6：</b><code>" + result.getIpv6Address() + "</code>\n"
                                            : ""
                                    )
                                    + "\ud83d\udd11 <b>密码：</b><code>"
                                    + result.getRootPassword()
                                    + "</code>";
                                this.notificationService.sendHtmlWithType("task_result", html);
                            } else {
                                this.broadcastLog(
                                    String.format(
                                        "【开机任务】用户:[%s],区域:[%s],架构:[%s] - 实例已创建(计次未增加) IP:%s（已达目标或并发争用，请在控制台核对实例）", user, region, arch, result.getPublicIp()
                                    )
                                );
                            }

                            if (successCount >= targetCount) {
                                this.completeTask(taskId, TaskStatusEnum.COMPLETED);
                                if (successCount > targetCount) {
                                    this.broadcastLog(
                                        String.format(
                                            "【开机任务】用户:[%s],区域:[%s],架构:[%s] - 任务已结束。⚠ 成功数(%d) 已超过目标(%d) 台，多开的实例可能产生费用，请至 OCI 与实例页核对。",
                                            user,
                                            region,
                                            arch,
                                            successCount,
                                            targetCount
                                        )
                                    );
                                } else {
                                    this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],架构:[%s] - 已达到目标数量(%d台)，任务完成！", user, region, arch, targetCount));
                                }

                                return;
                            } else {
                                int need = Math.max(0, targetCount - successCount);
                                this.broadcastLog(
                                    String.format("【开机任务】用户:[%s],区域:[%s],架构:[%s] - 还需创建 %d 台，[%d]秒后继续...", user, region, arch, need, intervalSeconds)
                                );
                                return;
                            }
                        } else {
                            String hint = StrUtil.isNotBlank(result.getFailureHint()) ? result.getFailureHint() : "创建未成功";
                            this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - %s，[%d]秒后将重试...", user, region, arch, hint, intervalSeconds));
                            return;
                        }
                    }

                    this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - 未找到可用公有子网，正在尝试创建...", user, region, arch));
                } catch (Exception var30) {
                    String hint = OciClientService.describeThrowableFailure(var30);
                    this.broadcastLog(String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s] - %s，[%d]秒后将重试...", user, region, arch, hint, intervalSeconds));
                    return;
                }
            } finally {
                this.runningTasks.remove(taskId);
            }
        }
    }

    private int incrementAttempt(String taskId) {
        UpdateWrapper<OciCreateTask> wrapper = new UpdateWrapper();
        ((UpdateWrapper)wrapper.eq("id", taskId)).setSql("attempt_count = COALESCE(attempt_count, 0) + 1", new Object[0]);
        this.taskMapper.update(null, wrapper);
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        return task != null && task.getAttemptCount() != null ? task.getAttemptCount() : 0;
    }

    private int tryIncrementSuccessCount(String taskId) {
        UpdateWrapper<OciCreateTask> w = new UpdateWrapper();
        w.eq("id", taskId);
        w.apply("COALESCE(success_count, 0) < COALESCE(create_numbers, 1)", new Object[0]);
        w.setSql("success_count = COALESCE(success_count, 0) + 1", new Object[0]);
        return this.taskMapper.update(null, w);
    }

    private synchronized void appendCreatedInstance(String taskId, InstanceDetailDTO result) {
        try {
            OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
            if (task == null) {
                return;
            }

            List<Map<String, Object>> list = this.parseCreatedInstances(task.getCreatedInstances());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("instanceId", result.getInstanceId());
            item.put("instanceName", result.getInstanceName());
            item.put("shape", result.getShape());
            item.put("ocpus", result.getOcpus());
            item.put("memory", result.getMemory());
            item.put("disk", result.getDisk());
            item.put("publicIp", result.getPublicIp());
            item.put("privateIp", result.getPrivateIp());
            if (StrUtil.isNotBlank(result.getIpv6Address())) {
                item.put("ipv6Address", result.getIpv6Address());
            }

            item.put("image", result.getImage());
            item.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            list.add(item);
            UpdateWrapper<OciCreateTask> wrapper = new UpdateWrapper();
            ((UpdateWrapper)wrapper.eq("id", taskId)).set("created_instances", JSON.writeValueAsString(list));
            this.taskMapper.update(null, wrapper);
        } catch (Exception var7) {
            log.warn("Failed to append created instance record for task {}: {}", taskId, var7.getMessage());
        }
    }

    private List<Map<String, Object>> parseCreatedInstances(String json) {
        if (json != null && !json.isBlank()) {
            try {
                return (List<Map<String, Object>>)JSON.readValue(json, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception var3) {
                log.warn("Failed to parse created_instances: {}", var3.getMessage());
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> getTaskDetail(String taskId) {
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        if (task == null) {
            throw new OciException("任务不存在");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(task.getUserId());
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", task.getId());
            data.put("userId", task.getUserId());
            data.put("username", user != null ? user.getUsername() : "unknown");
            data.put("ociRegion", task.getOciRegion());
            data.put("architecture", task.getArchitecture());
            data.put("ocpus", task.getOcpus());
            data.put("memory", task.getMemory());
            data.put("disk", task.getDisk());
            data.put("vpusPerGB", BootVolumeVpusUtil.normalize(task.getVpusPerGB()));
            data.put("createNumbers", task.getCreateNumbers());
            data.put("operationSystem", task.getOperationSystem());
            data.put("customScript", task.getCustomScript());
            data.put("assignPublicIp", task.getAssignPublicIp() != null ? task.getAssignPublicIp() : true);
            data.put("assignIpv6", task.getAssignIpv6() != null ? task.getAssignIpv6() : false);
            data.put("status", task.getStatus());
            data.put("attemptCount", task.getAttemptCount());
            int scD = task.getSuccessCount() != null ? task.getSuccessCount() : 0;
            int tgtD = task.getCreateNumbers() != null && task.getCreateNumbers() > 0 ? task.getCreateNumbers() : 1;
            data.put("successCount", scD);
            List<Map<String, Object>> inst = this.parseCreatedInstances(task.getCreatedInstances());
            int recD = inst.size();
            data.put("recordedInstanceCount", recD);
            data.put("progressOverTarget", scD > tgtD || recD > tgtD);
            data.put("createTime", task.getCreateTime());
            data.put("rootPassword", task.getRootPassword());
            data.put("instances", inst);
            return data;
        }
    }

    private void completeTask(String taskId, TaskStatusEnum status) {
        Future<?> future = this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }

        this.clearTaskExcludedAds(taskId);
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(status.getStatus());
            this.taskMapper.updateById(task);
        }
    }

    private void applyAdExcludedNoShapeBroadcast(String taskId, String user, String region, String arch, InstanceDetailDTO result, Set<String> excludedAds) {
        if (result.getAdsExcludedNoShape() != null && !result.getAdsExcludedNoShape().isEmpty()) {
            String shapeLine = StrUtil.isNotBlank(result.getResolvedTargetShape()) ? result.getResolvedTargetShape() : arch;

            for (String adName : result.getAdsExcludedNoShape()) {
                if (excludedAds.add(adName)) {
                    this.broadcastLog(
                        String.format("【开机任务】用户:[%s],区域:[%s],系统架构:[%s],可用域:[%s] - 当前可用域无此 Shape", user, region, shapeLine, formatAdForLog(adName))
                    );
                }
            }
        }
    }

    private static String formatAdForLog(String adName) {
        if (StrUtil.isBlank(adName)) {
            return "?";
        } else {
            int idx = adName.lastIndexOf("AD-");
            return idx >= 0 ? adName.substring(idx) : adName;
        }
    }

    private SysUserDTO buildSysUserDTO(OciUser ociUser, OciCreateTask task) {
        double[] normalized = ShapeFlexLimitsUtil.normalizeOcpusAndMemory(task.getArchitecture(), task.getOcpus(), task.getMemory());
        return SysUserDTO.builder()
            .taskId(task.getId())
            .username(ociUser.getUsername())
            .architecture(task.getArchitecture())
            .ocpus(normalized[0])
            .memory(normalized[1])
            .disk(task.getDisk())
            .vpusPerGB(BootVolumeVpusUtil.normalize(task.getVpusPerGB()))
            .createNumbers(task.getCreateNumbers())
            .rootPassword(task.getRootPassword())
            .operationSystem(task.getOperationSystem())
            .customScript(task.getCustomScript())
            .assignPublicIp(task.getAssignPublicIp() != null ? task.getAssignPublicIp() : true)
            .assignIpv6(task.getAssignIpv6() != null ? task.getAssignIpv6() : false)
            .ociCfg(
                SysUserDTO.OciCfg.builder()
                    .tenantId(ociUser.getOciTenantId())
                    .userId(ociUser.getOciUserId())
                    .fingerprint(ociUser.getOciFingerprint())
                    .region(task.getOciRegion())
                    .privateKeyPath(ociUser.getOciKeyPath())
                    .build()
            )
            .build();
    }

    private void cleanExpiredTasks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7L);
        this.taskMapper
            .delete(
                (Wrapper)(new LambdaQueryWrapper<OciCreateTask>().ne(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()))
                    .lt(OciCreateTask::getCreateTime, cutoff)
            );
    }

    private void repairInconsistentRunningTasks() {
        for (OciCreateTask t : this.taskMapper.selectList((Wrapper)new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()))) {
            int target = t.getCreateNumbers() != null && t.getCreateNumbers() > 0 ? t.getCreateNumbers() : 1;
            int sc = t.getSuccessCount() != null ? t.getSuccessCount() : 0;
            if (sc >= target) {
                try {
                    log.info("修复开机任务: id={} 进度{}/{} -> 已完成（计次不裁剪）", new Object[]{t.getId(), sc, target});
                    this.completeTask(t.getId(), TaskStatusEnum.COMPLETED);
                } catch (Exception var7) {
                    log.warn("repairInconsistentRunningTasks id={}: {}", t.getId(), var7.getMessage());
                }
            }
        }
    }

    private static String targetShapeLineForNotify(String shapeOrArchitecture) {
        return ShapeSeriesUtil.isFullShapeName(shapeOrArchitecture) ? "\ud83d\udcbb <b>Shape：</b><code>" + shapeOrArchitecture.trim() + "</code>\n" : "";
    }

    private void broadcastLog(String message) {
        log.info(message);
        LogWebSocketHandler.broadcast(message);
    }
}
