/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
 *  com.baomidou.mybatisplus.core.metadata.IPage
 *  com.baomidou.mybatisplus.extension.plugins.pagination.Page
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.config.VirtualThreadConfig
 *  com.ocxworker.enums.TaskStatusEnum
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciCreateTaskMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.InstanceDetailDTO
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciCreateTask
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.model.params.PageParams
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.TaskSchedulerService
 *  com.ocxworker.util.BootVolumeVpusUtil
 *  com.ocxworker.util.CommonUtils
 *  com.ocxworker.util.OciRegionUtil
 *  com.ocxworker.util.ShapeFlexLimitsUtil
 *  com.ocxworker.util.ShapeSeriesUtil
 *  com.ocxworker.websocket.LogWebSocketHandler
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.context.annotation.DependsOn
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.ocxworker.service.NotificationService;
import com.ocxworker.util.BootVolumeVpusUtil;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.OciRegionUtil;
import com.ocxworker.util.ShapeFlexLimitsUtil;
import com.ocxworker.util.ShapeSeriesUtil;
import com.ocxworker.websocket.LogWebSocketHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
@DependsOn(value={"databaseGuardService"})
public class TaskSchedulerService
implements SmartLifecycle {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private NotificationService notificationService;
    private final Map<String, Future<?>> taskMap = new ConcurrentHashMap();
    private final Set<String> runningTasks = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, Set<String>> taskExcludedAds = new ConcurrentHashMap();
    private static final ObjectMapper JSON = new ObjectMapper();
    private volatile boolean lifecycleRunning = false;

    @PostConstruct
    public void init() {
        this.repairInconsistentRunningTasks();
        List runningTaskList = this.taskMapper.selectList((Wrapper)new LambdaQueryWrapper().eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus()));
        if (!runningTaskList.isEmpty()) {
            log.info("Restoring {} running tasks from database...", (Object)runningTaskList.size());
            for (OciCreateTask task : runningTaskList) {
                try {
                    OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()));
                    if (ociUser == null) {
                        task.setStatus(TaskStatusEnum.FAILED.getStatus());
                        this.taskMapper.updateById((Object)task);
                        continue;
                    }
                    SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
                    this.scheduleTask(task.getId(), dto, task.getIntervalSeconds().intValue());
                    this.broadcastLog(String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s],\u7cfb\u7edf\u67b6\u6784:[%s] - \u670d\u52a1\u91cd\u542f\uff0c\u6062\u590d\u4efb\u52a1\u6267\u884c", ociUser.getUsername(), ociUser.getOciRegion(), task.getArchitecture()));
                }
                catch (Exception e) {
                    log.error("Failed to restore task {}: {}", (Object)task.getId(), (Object)e.getMessage());
                    task.setStatus(TaskStatusEnum.FAILED.getStatus());
                    this.taskMapper.updateById((Object)task);
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
        if (this.taskMap.isEmpty()) {
            return;
        }
        int n = this.taskMap.size();
        for (Future future : new ArrayList(this.taskMap.values())) {
            future.cancel(true);
        }
        this.taskMap.clear();
        log.info("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u5e94\u7528\u5173\u95ed\uff0c\u5df2\u53d6\u6d88 {} \u4e2a\u8c03\u5ea6\u4e2d\u7684\u865a\u62df\u7ebf\u7a0b\uff08\u5e93\u4e2d RUNNING \u672a\u6539\uff0c\u91cd\u542f\u540e\u5c06\u6062\u590d\uff09", (Object)n);
    }

    public boolean hasRunningTask(String userId) {
        return this.taskMapper.selectCount((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciCreateTask::getUserId, (Object)userId)).eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus())) > 0L;
    }

    public Page<Map<String, Object>> listTasks(PageParams params) {
        this.repairInconsistentRunningTasks();
        this.cleanExpiredTasks();
        Page page = new Page((long)params.getCurrent(), (long)params.getSize());
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        if (params.getStatus() != null && !params.getStatus().isEmpty()) {
            wrapper.eq(OciCreateTask::getStatus, (Object)params.getStatus());
        }
        if (params.getKeyword() != null && !params.getKeyword().isBlank()) {
            String kw = params.getKeyword();
            List matchedUsers = this.userMapper.selectList((Wrapper)new LambdaQueryWrapper().like(OciUser::getUsername, (Object)kw));
            List<String> matchedUserIds = matchedUsers.stream().map(OciUser::getId).toList();
            wrapper.and(w -> {
                ((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)w.like(OciCreateTask::getOciRegion, (Object)kw)).or()).like(OciCreateTask::getArchitecture, (Object)kw)).or()).like(OciCreateTask::getOperationSystem, (Object)kw);
                if (!matchedUserIds.isEmpty()) {
                    ((LambdaQueryWrapper)w.or()).in(OciCreateTask::getUserId, (Collection)matchedUserIds);
                }
            });
        }
        wrapper.orderByDesc(OciCreateTask::getCreateTime);
        Page result = (Page)this.taskMapper.selectPage((IPage)page, (Wrapper)wrapper);
        Page enriched = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        enriched.setRecords(result.getRecords().stream().map(task -> {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()));
            map.put("id", task.getId());
            map.put("userId", task.getUserId());
            map.put("username", user != null ? user.getUsername() : "unknown");
            map.put("ociRegion", task.getOciRegion());
            map.put("ocpus", task.getOcpus());
            map.put("memory", task.getMemory());
            map.put("disk", task.getDisk());
            map.put("vpusPerGB", BootVolumeVpusUtil.normalize((Integer)task.getVpusPerGB()));
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

    public void createTask(String userId, String architecture, Double ocpus, Double memory, Integer disk, Integer vpusPerGB, Integer createNumbers, Integer interval, String rootPassword, String operationSystem, String customScript, Boolean assignPublicIp, Boolean assignIpv6, String ociRegionOverride) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        String effectiveRegion = StrUtil.trimToNull((CharSequence)ociRegionOverride);
        effectiveRegion = effectiveRegion == null ? OciRegionUtil.publicRegionId((String)ociUser.getOciRegion()) : OciRegionUtil.publicRegionId((String)effectiveRegion);
        OciCreateTask task = new OciCreateTask();
        task.setId(CommonUtils.generateId());
        task.setUserId(userId);
        task.setOciRegion(effectiveRegion);
        task.setArchitecture(architecture);
        double[] normalized = ShapeFlexLimitsUtil.normalizeAndLogIfAdjusted((String)architecture, (Double)ocpus, (Double)memory, (String)"\u521b\u5efa\u5f00\u673a\u4efb\u52a1");
        task.setOcpus(Double.valueOf(normalized[0]));
        task.setMemory(Double.valueOf(normalized[1]));
        task.setDisk(disk);
        task.setVpusPerGB(Integer.valueOf(BootVolumeVpusUtil.normalize((Integer)vpusPerGB)));
        task.setCreateNumbers(createNumbers);
        task.setIntervalSeconds(interval);
        task.setRootPassword(rootPassword);
        task.setOperationSystem(operationSystem);
        task.setCustomScript(customScript);
        task.setAssignPublicIp(Boolean.valueOf(assignPublicIp != null ? assignPublicIp : true));
        task.setAssignIpv6(Boolean.valueOf(assignIpv6 != null ? assignIpv6 : false));
        task.setStatus(TaskStatusEnum.RUNNING.getStatus());
        task.setAttemptCount(Integer.valueOf(0));
        task.setSuccessCount(Integer.valueOf(0));
        task.setCreateTime(LocalDateTime.now());
        this.taskMapper.insert((Object)task);
        this.clearTaskExcludedAds(task.getId());
        SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
        this.scheduleTask(task.getId(), dto, interval.intValue());
        String series = ShapeSeriesUtil.resolveSeries((String)architecture);
        String logMsg = String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s],\u67b6\u6784:[%s],\u6570\u91cf:[%d] - \u4efb\u52a1\u5df2\u521b\u5efa", ociUser.getUsername(), effectiveRegion, series, createNumbers);
        this.broadcastLog(logMsg);
        String pwd = rootPassword != null ? rootPassword : "\u968f\u673a";
        String html = "\ud83d\udccb <b>\u5f00\u673a\u4efb\u52a1\u5df2\u521b\u5efa</b>\n\n\ud83d\udc64 <b>\u79df\u6237\uff1a</b>" + ociUser.getUsername() + "\n\ud83c\udf0d <b>\u533a\u57df\uff1a</b>" + effectiveRegion + "\n\u2699\ufe0f <b>\u67b6\u6784\uff1a</b>" + series + "\n" + TaskSchedulerService.targetShapeLineForNotify((String)architecture) + "\ud83d\udcca <b>\u914d\u7f6e\uff1a</b>" + normalized[0] + "C / " + normalized[1] + "GB / " + BootVolumeVpusUtil.formatDiskWithVpus((int)(disk != null ? disk : 50), (int)task.getVpusPerGB()) + "\n\ud83d\udd22 <b>\u6570\u91cf\uff1a</b>" + createNumbers + "\n\ud83d\udd11 <b>\u5bc6\u7801\uff1a</b><code>" + pwd + "</code>";
        this.notificationService.sendHtmlWithType("task_create", html);
    }

    public void resumeTask(String taskId) {
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
        if (task == null) {
            throw new OciException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        if (TaskStatusEnum.RUNNING.getStatus().equals(task.getStatus())) {
            throw new OciException("\u4efb\u52a1\u5df2\u5728\u8fd0\u884c\u4e2d");
        }
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        task.setStatus(TaskStatusEnum.RUNNING.getStatus());
        this.taskMapper.updateById((Object)task);
        this.clearTaskExcludedAds(taskId);
        SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
        this.scheduleTask(task.getId(), dto, task.getIntervalSeconds().intValue());
        this.broadcastLog(String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s],\u7cfb\u7edf\u67b6\u6784:[%s] - \u4efb\u52a1\u5df2\u6062\u590d\u8fd0\u884c", ociUser.getUsername(), task.getOciRegion(), task.getArchitecture()));
    }

    public void updateTask(String taskId, String architecture, Double ocpus, Double memory, Integer disk, Integer vpusPerGB, Integer createNumbers, Integer interval, String rootPassword, String operationSystem, String customScript, Boolean assignPublicIp, Boolean assignIpv6) {
        OciUser user;
        OciUser ociUser;
        Future future;
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
        if (task == null) {
            throw new OciException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        boolean wasRunning = TaskStatusEnum.RUNNING.getStatus().equals(task.getStatus());
        if (wasRunning && (future = (Future)this.taskMap.get(taskId)) != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
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
            task.setVpusPerGB(Integer.valueOf(BootVolumeVpusUtil.normalize((Integer)vpusPerGB)));
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
        double[] normalized = ShapeFlexLimitsUtil.normalizeAndLogIfAdjusted((String)task.getArchitecture(), (Double)task.getOcpus(), (Double)task.getMemory(), (String)"\u66f4\u65b0\u5f00\u673a\u4efb\u52a1");
        task.setOcpus(Double.valueOf(normalized[0]));
        task.setMemory(Double.valueOf(normalized[1]));
        this.taskMapper.updateById((Object)task);
        this.clearTaskExcludedAds(taskId);
        if (wasRunning && (ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()))) != null) {
            SysUserDTO dto = this.buildSysUserDTO(ociUser, task);
            this.scheduleTask(task.getId(), dto, task.getIntervalSeconds().intValue());
        }
        String name = (user = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()))) != null ? user.getUsername() : "unknown";
        this.broadcastLog(String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s] - \u4efb\u52a1\u5df2\u7f16\u8f91%s", name, task.getOciRegion(), wasRunning ? "\uff08\u81ea\u52a8\u91cd\u542f\u8c03\u5ea6\uff09" : ""));
    }

    public void deleteTask(String taskId) {
        Future future = (Future)this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }
        this.taskMapper.deleteById((Serializable)((Object)taskId));
        this.clearTaskExcludedAds(taskId);
    }

    public void stopTask(String taskId) {
        OciCreateTask task;
        Future future = (Future)this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }
        if ((task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId))) != null) {
            task.setStatus(TaskStatusEnum.STOPPED.getStatus());
            this.taskMapper.updateById((Object)task);
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()));
            String name = user != null ? user.getUsername() : "unknown";
            this.broadcastLog(String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s] - \u4efb\u52a1\u5df2\u624b\u52a8\u505c\u6b62", name, task.getOciRegion()));
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runTaskLoop(String taskId, SysUserDTO dto, int delaySec) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                OciCreateTask t = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
                if (t == null) {
                    break;
                }
                if (!TaskStatusEnum.RUNNING.getStatus().equals(t.getStatus())) {
                    break;
                }
                this.executeCreate(taskId, dto, delaySec);
                t = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
                if (t == null) {
                    break;
                }
                if (!TaskStatusEnum.RUNNING.getStatus().equals(t.getStatus())) {
                    break;
                }
                try {
                    Thread.sleep((long)delaySec * 1000L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        finally {
            this.taskMap.remove(taskId);
        }
    }

    /*
     * Exception decompiling
     */
    private void executeCreate(String taskId, SysUserDTO dto, int intervalSeconds) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 7 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private int incrementAttempt(String taskId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        ((UpdateWrapper)wrapper.eq((Object)"id", (Object)taskId)).setSql("attempt_count = COALESCE(attempt_count, 0) + 1", new Object[0]);
        this.taskMapper.update(null, (Wrapper)wrapper);
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
        return task != null && task.getAttemptCount() != null ? task.getAttemptCount() : 0;
    }

    private int tryIncrementSuccessCount(String taskId) {
        UpdateWrapper w = new UpdateWrapper();
        w.eq((Object)"id", (Object)taskId);
        w.apply("COALESCE(success_count, 0) < COALESCE(create_numbers, 1)", new Object[0]);
        w.setSql("success_count = COALESCE(success_count, 0) + 1", new Object[0]);
        return this.taskMapper.update(null, (Wrapper)w);
    }

    private synchronized void appendCreatedInstance(String taskId, InstanceDetailDTO result) {
        try {
            OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
            if (task == null) {
                return;
            }
            List list = this.parseCreatedInstances(task.getCreatedInstances());
            LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("instanceId", result.getInstanceId());
            item.put("instanceName", result.getInstanceName());
            item.put("shape", result.getShape());
            item.put("ocpus", result.getOcpus());
            item.put("memory", result.getMemory());
            item.put("disk", result.getDisk());
            item.put("publicIp", result.getPublicIp());
            item.put("privateIp", result.getPrivateIp());
            if (StrUtil.isNotBlank((CharSequence)result.getIpv6Address())) {
                item.put("ipv6Address", result.getIpv6Address());
            }
            item.put("image", result.getImage());
            item.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            list.add(item);
            UpdateWrapper wrapper = new UpdateWrapper();
            ((UpdateWrapper)wrapper.eq((Object)"id", (Object)taskId)).set((Object)"created_instances", (Object)JSON.writeValueAsString((Object)list));
            this.taskMapper.update(null, (Wrapper)wrapper);
        }
        catch (Exception e) {
            log.warn("Failed to append created instance record for task {}: {}", (Object)taskId, (Object)e.getMessage());
        }
    }

    private List<Map<String, Object>> parseCreatedInstances(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<Map<String, Object>>();
        }
        try {
            return (List)JSON.readValue(json, (TypeReference)new /* Unavailable Anonymous Inner Class!! */);
        }
        catch (Exception e) {
            log.warn("Failed to parse created_instances: {}", (Object)e.getMessage());
            return new ArrayList<Map<String, Object>>();
        }
    }

    public Map<String, Object> getTaskDetail(String taskId) {
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
        if (task == null) {
            throw new OciException("\u4efb\u52a1\u4e0d\u5b58\u5728");
        }
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)task.getUserId()));
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", task.getId());
        data.put("userId", task.getUserId());
        data.put("username", user != null ? user.getUsername() : "unknown");
        data.put("ociRegion", task.getOciRegion());
        data.put("architecture", task.getArchitecture());
        data.put("ocpus", task.getOcpus());
        data.put("memory", task.getMemory());
        data.put("disk", task.getDisk());
        data.put("vpusPerGB", BootVolumeVpusUtil.normalize((Integer)task.getVpusPerGB()));
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
        List inst = this.parseCreatedInstances(task.getCreatedInstances());
        int recD = inst.size();
        data.put("recordedInstanceCount", recD);
        data.put("progressOverTarget", scD > tgtD || recD > tgtD);
        data.put("createTime", task.getCreateTime());
        data.put("rootPassword", task.getRootPassword());
        data.put("instances", inst);
        return data;
    }

    private void completeTask(String taskId, TaskStatusEnum status) {
        Future future = (Future)this.taskMap.get(taskId);
        if (future != null) {
            future.cancel(true);
            this.taskMap.remove(taskId);
        }
        this.clearTaskExcludedAds(taskId);
        OciCreateTask task = (OciCreateTask)this.taskMapper.selectById((Serializable)((Object)taskId));
        if (task != null) {
            task.setStatus(status.getStatus());
            this.taskMapper.updateById((Object)task);
        }
    }

    private void applyAdExcludedNoShapeBroadcast(String taskId, String user, String region, String arch, InstanceDetailDTO result, Set<String> excludedAds) {
        if (result.getAdsExcludedNoShape() == null || result.getAdsExcludedNoShape().isEmpty()) {
            return;
        }
        String shapeLine = StrUtil.isNotBlank((CharSequence)result.getResolvedTargetShape()) ? result.getResolvedTargetShape() : arch;
        for (String adName : result.getAdsExcludedNoShape()) {
            if (!excludedAds.add(adName)) continue;
            this.broadcastLog(String.format("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[%s],\u533a\u57df:[%s],\u7cfb\u7edf\u67b6\u6784:[%s],\u53ef\u7528\u57df:[%s] - \u5f53\u524d\u53ef\u7528\u57df\u65e0\u6b64 Shape", user, region, shapeLine, TaskSchedulerService.formatAdForLog((String)adName)));
        }
    }

    private static String formatAdForLog(String adName) {
        if (StrUtil.isBlank((CharSequence)adName)) {
            return "?";
        }
        int idx = adName.lastIndexOf("AD-");
        return idx >= 0 ? adName.substring(idx) : adName;
    }

    private SysUserDTO buildSysUserDTO(OciUser ociUser, OciCreateTask task) {
        double[] normalized = ShapeFlexLimitsUtil.normalizeOcpusAndMemory((String)task.getArchitecture(), (Double)task.getOcpus(), (Double)task.getMemory());
        return SysUserDTO.builder().taskId(task.getId()).username(ociUser.getUsername()).architecture(task.getArchitecture()).ocpus(Double.valueOf(normalized[0])).memory(Double.valueOf(normalized[1])).disk(task.getDisk()).vpusPerGB(Integer.valueOf(BootVolumeVpusUtil.normalize((Integer)task.getVpusPerGB()))).createNumbers(task.getCreateNumbers()).rootPassword(task.getRootPassword()).operationSystem(task.getOperationSystem()).customScript(task.getCustomScript()).assignPublicIp(Boolean.valueOf(task.getAssignPublicIp() != null ? task.getAssignPublicIp() : true)).assignIpv6(Boolean.valueOf(task.getAssignIpv6() != null ? task.getAssignIpv6() : false)).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(task.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }

    private void cleanExpiredTasks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7L);
        this.taskMapper.delete((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().ne(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus())).lt(OciCreateTask::getCreateTime, (Object)cutoff));
    }

    private void repairInconsistentRunningTasks() {
        List running = this.taskMapper.selectList((Wrapper)new LambdaQueryWrapper().eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus()));
        for (OciCreateTask t : running) {
            int sc;
            int target = t.getCreateNumbers() != null && t.getCreateNumbers() > 0 ? t.getCreateNumbers() : 1;
            int n = sc = t.getSuccessCount() != null ? t.getSuccessCount() : 0;
            if (sc < target) continue;
            try {
                log.info("\u4fee\u590d\u5f00\u673a\u4efb\u52a1: id={} \u8fdb\u5ea6{}/{} -> \u5df2\u5b8c\u6210\uff08\u8ba1\u6b21\u4e0d\u88c1\u526a\uff09", new Object[]{t.getId(), sc, target});
                this.completeTask(t.getId(), TaskStatusEnum.COMPLETED);
            }
            catch (Exception e) {
                log.warn("repairInconsistentRunningTasks id={}: {}", (Object)t.getId(), (Object)e.getMessage());
            }
        }
    }

    private static String targetShapeLineForNotify(String shapeOrArchitecture) {
        if (ShapeSeriesUtil.isFullShapeName((String)shapeOrArchitecture)) {
            return "\ud83d\udcbb <b>Shape\uff1a</b><code>" + shapeOrArchitecture.trim() + "</code>\n";
        }
        return "";
    }

    private void broadcastLog(String message) {
        log.info(message);
        LogWebSocketHandler.broadcast((String)message);
    }

    private static /* synthetic */ Set lambda$executeCreate$3(String k) {
        return ConcurrentHashMap.newKeySet();
    }
}

