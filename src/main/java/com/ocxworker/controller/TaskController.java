package com.ocxworker.controller;

import com.ocxworker.model.params.CreateTaskParams;
import com.ocxworker.model.params.PageParams;
import com.ocxworker.model.params.UpdateTaskParams;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.TaskSchedulerService;
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
@RequestMapping({"/api/oci/task"})
public class TaskController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    @Resource
    private TaskSchedulerService taskSchedulerService;

    @PostMapping({"/list"})
    public ResponseData<?> list(@RequestBody PageParams params) {
        return ResponseData.ok(this.taskSchedulerService.listTasks(params));
    }

    @PostMapping({"/hasRunning"})
    public ResponseData<?> hasRunning(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.taskSchedulerService.hasRunningTask(params.get("userId")));
    }

    @PostMapping({"/create"})
    public ResponseData<?> create(@RequestBody @Valid CreateTaskParams params) {
        this.taskSchedulerService
            .createTask(
                params.getUserId(),
                params.getArchitecture(),
                params.getOcpus(),
                params.getMemory(),
                params.getDisk(),
                params.getVpusPerGB(),
                params.getCreateNumbers(),
                params.getInterval(),
                params.getRootPassword(),
                params.getOperationSystem(),
                params.getCustomScript(),
                params.getAssignPublicIp(),
                params.getAssignIpv6(),
                params.getOciRegion()
            );
        return ResponseData.ok();
    }

    @PostMapping({"/update"})
    public ResponseData<?> update(@RequestBody @Valid UpdateTaskParams params) {
        this.taskSchedulerService
            .updateTask(
                params.getTaskId(),
                params.getArchitecture(),
                params.getOcpus(),
                params.getMemory(),
                params.getDisk(),
                params.getVpusPerGB(),
                params.getCreateNumbers(),
                params.getInterval(),
                params.getRootPassword(),
                params.getOperationSystem(),
                params.getCustomScript(),
                params.getAssignPublicIp(),
                params.getAssignIpv6()
            );
        return ResponseData.ok();
    }

    @PostMapping({"/stop"})
    public ResponseData<?> stop(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.stopTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping({"/resume"})
    public ResponseData<?> resume(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.resumeTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping({"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, String> params) {
        this.taskSchedulerService.deleteTask(params.get("taskId"));
        return ResponseData.ok();
    }

    @PostMapping({"/detail"})
    public ResponseData<?> detail(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.taskSchedulerService.getTaskDetail(params.get("taskId")));
    }

    @PostMapping({"/batchStop"})
    public ResponseData<?> batchStop(@RequestBody Map<String, Object> params) {
        List<String> ids = this.extractStringList(params, "taskIds");
        int count = 0;

        for (String id : ids) {
            try {
                this.taskSchedulerService.stopTask(id);
                count++;
            } catch (Exception var7) {
                log.warn("batchStop failed for taskId={}: {}", id, var7.getMessage());
            }
        }

        return ResponseData.ok(count);
    }

    @PostMapping({"/batchResume"})
    public ResponseData<?> batchResume(@RequestBody Map<String, Object> params) {
        List<String> ids = this.extractStringList(params, "taskIds");
        int count = 0;

        for (String id : ids) {
            try {
                this.taskSchedulerService.resumeTask(id);
                count++;
            } catch (Exception var7) {
                log.warn("batchResume failed for taskId={}: {}", id, var7.getMessage());
            }
        }

        return ResponseData.ok(count);
    }

    private List<String> extractStringList(Map<String, Object> params, String key) {
        if ((params == null ? null : params.get(key)) instanceof List<?> list && !list.isEmpty()) {
            List<String> ids = new ArrayList<>(list.size());

            for (Object o : list) {
                if (o != null) {
                    ids.add(String.valueOf(o));
                }
            }

            return ids;
        }

        return Collections.emptyList();
    }
}
