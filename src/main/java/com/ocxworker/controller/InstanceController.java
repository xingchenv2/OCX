/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.controller.InstanceController
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.ConsoleService
 *  com.ocxworker.service.InstanceService
 *  com.ocxworker.service.ShapeEditTaskManager
 *  com.ocxworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.ConsoleService;
import com.ocxworker.service.InstanceService;
import com.ocxworker.service.ShapeEditTaskManager;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/oci/instance"})
public class InstanceController {
    @Resource
    private InstanceService instanceService;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private ConsoleService consoleService;
    @Resource
    private ShapeEditTaskManager shapeEditTaskManager;

    @PostMapping(value={"/list"})
    public ResponseData<?> list(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listInstances(params.get("id"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/updateState"})
    public ResponseData<?> updateState(@RequestBody Map<String, String> params) {
        this.instanceService.updateInstanceState(params.get("id"), params.get("instanceId"), params.get("action"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/terminate"})
    public ResponseData<?> terminate(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("terminate", params.get("verifyCode") == null ? null : String.valueOf(params.get("verifyCode")));
        boolean preserveBootVolume = Boolean.TRUE.equals(params.get("preserveBootVolume"));
        this.instanceService.terminateInstance(params.get("id") == null ? null : String.valueOf(params.get("id")), params.get("instanceId") == null ? null : String.valueOf(params.get("instanceId")), preserveBootVolume, InstanceController.regObj(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/updateInstance"})
    public ResponseData<?> updateInstance(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.instanceService.updateInstance(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("instanceId")), InstanceController.asString((Object)params.get("displayName")), InstanceController.asString((Object)params.get("shape")), InstanceController.asFloat((Object)params.get("ocpus")), InstanceController.asFloat((Object)params.get("memoryInGBs")), InstanceController.regObj(params)));
    }

    @GetMapping(value={"/shapeEditTask/{taskId}"})
    public ResponseData<?> shapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok((Object)this.shapeEditTaskManager.getStatus(taskId));
    }

    @PostMapping(value={"/shapeEditTask/{taskId}/pause"})
    public ResponseData<?> pauseShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok((Object)this.shapeEditTaskManager.pause(taskId));
    }

    @PostMapping(value={"/shapeEditTask/{taskId}/resume"})
    public ResponseData<?> resumeShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok((Object)this.shapeEditTaskManager.resume(taskId));
    }

    @PostMapping(value={"/shapeEditTask/{taskId}/stop"})
    public ResponseData<?> stopShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok((Object)this.shapeEditTaskManager.stop(taskId));
    }

    @PostMapping(value={"/shapes"})
    public ResponseData<?> listShapes(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listAvailableShapes(params.get("id"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/shapesForInstance"})
    public ResponseData<?> shapesForInstance(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listShapesForInstance(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/forceA2ToA1"})
    public ResponseData<?> forceA2ToA1(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.forceA2FlexToA1Flex(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/bootVolumes"})
    public ResponseData<?> bootVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listBootVolumesByInstance(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/updateBootVolume"})
    public ResponseData<?> updateBootVolume(@RequestBody Map<String, Object> params) {
        this.instanceService.updateBootVolume(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("bootVolumeId")), InstanceController.asLong((Object)params.get("sizeInGBs")), InstanceController.asString((Object)params.get("displayName")), InstanceController.asLong((Object)params.get("vpusPerGB")), InstanceController.regObj(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/blockVolumes"})
    public ResponseData<?> blockVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listBlockVolumesByInstance(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/unattachedBlockVolumes"})
    public ResponseData<?> unattachedBlockVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listUnattachedBlockVolumesForInstance(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/createBlockVolumeAndAttach"})
    public ResponseData<?> createBlockVolumeAndAttach(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.instanceService.createBlockVolumeAndAttach(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("instanceId")), InstanceController.asString((Object)params.get("displayName")), InstanceController.asLong((Object)params.get("sizeInGBs")), InstanceController.asLong((Object)params.get("vpusPerGB")), InstanceController.asString((Object)params.get("device")), InstanceController.regObj(params)));
    }

    @PostMapping(value={"/attachBlockVolume"})
    public ResponseData<?> attachBlockVolume(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.instanceService.attachBlockVolume(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("instanceId")), InstanceController.asString((Object)params.get("volumeId")), InstanceController.asString((Object)params.get("device")), InstanceController.regObj(params)));
    }

    @PostMapping(value={"/detachBlockVolume"})
    public ResponseData<?> detachBlockVolume(@RequestBody Map<String, Object> params) {
        this.instanceService.detachBlockVolume(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("volumeAttachmentId")), InstanceController.regObj(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/updateBlockVolume"})
    public ResponseData<?> updateBlockVolume(@RequestBody Map<String, Object> params) {
        this.instanceService.updateBlockVolume(InstanceController.asString((Object)params.get("id")), InstanceController.asString((Object)params.get("volumeId")), InstanceController.asLong((Object)params.get("sizeInGBs")), InstanceController.asString((Object)params.get("displayName")), InstanceController.asLong((Object)params.get("vpusPerGB")), InstanceController.regObj(params));
        return ResponseData.ok();
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Float asFloat(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            Number n = (Number)v;
            return Float.valueOf(n.floatValue());
        }
        try {
            return Float.valueOf(Float.parseFloat(String.valueOf(v)));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long asLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            Number n = (Number)v;
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @PostMapping(value={"/instanceDetail"})
    public ResponseData<?> instanceDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.getInstanceNetworkDetail(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/addIpv6"})
    public ResponseData<?> addIpv6(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.addIpv6(params.get("id"), params.get("instanceId"), params.get("vnicId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/removeIpv6"})
    public ResponseData<?> removeIpv6(@RequestBody Map<String, String> params) {
        this.instanceService.removeIpv6(params.get("id"), params.get("ipv6Id"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/createReservedIp"})
    public ResponseData<?> createReservedIp(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.createReservedIp(params.get("id"), params.get("displayName"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/listReservedIps"})
    public ResponseData<?> listReservedIps(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.instanceService.listReservedIps(params.get("id"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/deleteReservedIp"})
    public ResponseData<?> deleteReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.deleteReservedIp(params.get("id"), params.get("publicIpId"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/assignReservedIp"})
    public ResponseData<?> assignReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.assignReservedIp(params.get("id"), params.get("publicIpId"), params.get("instanceId"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/unassignReservedIp"})
    public ResponseData<?> unassignReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.unassignReservedIp(params.get("id"), params.get("publicIpId"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/createConsole"})
    public ResponseData<?> createConsole(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.consoleService.createConsoleConnection(params.get("id"), params.get("instanceId"), InstanceController.regStr(params)));
    }

    @PostMapping(value={"/deleteConsole"})
    public ResponseData<?> deleteConsole(@RequestBody Map<String, String> params) {
        this.consoleService.deleteConsoleConnection(params.get("id"), params.get("connectionId"), InstanceController.regStr(params));
        return ResponseData.ok();
    }

    private static String regStr(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        String s = params.get("region");
        if (s == null) {
            return null;
        }
        return (s = s.trim()).isEmpty() ? null : s;
    }

    private static String regObj(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        Object v = params.get("region");
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }
}

