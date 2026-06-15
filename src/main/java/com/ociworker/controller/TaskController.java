/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.TaskController
 *  com.ociworker.model.params.CreateTaskParams
 *  com.ociworker.model.params.PageParams
 *  com.ociworker.model.params.UpdateTaskParams
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.TaskSchedulerService
 *  jakarta.annotation.Resource
 *  jakarta.validation.Valid
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.model.params.CreateTaskParams;
import com.ociworker.model.params.PageParams;
import com.ociworker.model.params.UpdateTaskParams;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.TaskSchedulerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/oci/task"})
public class TaskController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private TaskSchedulerService taskSchedulerService;

    @PostMapping(value={"/list"})
    public ResponseData<?> list(@RequestBody PageParams params) {
        return ResponseData.ok((Object)this.taskSchedulerService.listTasks(params));
    }

    @PostMapping(value={"/hasRunning"})
    public ResponseData<?> hasRunning(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.taskSchedulerService.hasRunningTask(params.get("userId")));
    }

    @PostMapping(value={"/create"})
    public ResponseData<?> create(@RequestBody @Valid CreateTaskParams params) {
        this.taskSchedulerService.createTask(params.getUserId(), params.getArchitecture(), params.getOcpus(), params.getMemory(), params.getDisk(), params.getVpusPerGB(), params.getCreateNumbers(), params.getInterval(), params.getRootPassword(), params.getOperationSystem(), params.getCustomScript(), params.getAssignPublicIp(), params.getAssignIpv6(), params.getOciRegion());
        return ResponseData.ok();
    }

    @PostMapping(value={"/update"})
    public ResponseData<?> update(@RequestBody @Valid UpdateTaskParams params) {
        this.taskSchedulerService.updateTask(params.getTaskId(), params.getArchitecture(), params.getOcpus(), params.getMemory(), params.getDisk(), params.getVpusPerGB(), params.getCreateNumbers(), params.getInterval(), params.getRootPassword(), params.getOperationSystem(), params.getCustomScript(), params.getAssignPublicIp(), params.getAssignIpv6());
        return ResponseData.ok();
    }

    @PostMapping(value={"/stop"})
    public ResponseData<?> stop(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.stopTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/resume"})
    public ResponseData<?> resume(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.resumeTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.deleteTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/detail"})
    public ResponseData<?> detail(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.taskSchedulerService.getTaskDetail(params.get("taskId")));
    }

    @PostMapping(value={"/batchStop"})
    public ResponseData<?> batchStop(@RequestBody Map<String, Object> params) {
        List ids = this.extractStringList(params, "taskIds");
        int count = 0;
        for (String id : ids) {
            try {
                this.taskSchedulerService.stopTask(id);
                ++count;
            }
            catch (Exception e) {
                log.warn("batchStop failed for taskId={}: {}", (Object)id, (Object)e.getMessage());
            }
        }
        return ResponseData.ok((Object)count);
    }

    @PostMapping(value={"/batchResume"})
    public ResponseData<?> batchResume(@RequestBody Map<String, Object> params) {
        List ids = this.extractStringList(params, "taskIds");
        int count = 0;
        for (String id : ids) {
            try {
                this.taskSchedulerService.resumeTask(id);
                ++count;
            }
            catch (Exception e) {
                log.warn("batchResume failed for taskId={}: {}", (Object)id, (Object)e.getMessage());
            }
        }
        return ResponseData.ok((Object)count);
    }

    private List<String> extractStringList(Map<String, Object> params, String key) {
        List list;
        Object raw;
        Object object = raw = params == null ? null : params.get(key);
        if (!(raw instanceof List) || (list = (List)raw).isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> ids = new ArrayList<String>(list.size());
        for (Object o : list) {
            if (o == null) continue;
            ids.add(String.valueOf(o));
        }
        return ids;
    }
}

