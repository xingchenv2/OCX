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

@RestController
@RequestMapping({"/api/oci/instance"})
public class InstanceController {
    @Resource
    private InstanceService instanceService;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private ConsoleService consoleService;
    @Resource
    private ShapeEditTaskManager shapeEditTaskManager;

    @PostMapping({"/list"})
    public ResponseData<?> list(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listInstances(params.get("id"), regStr(params)));
    }

    @PostMapping({"/updateState"})
    public ResponseData<?> updateState(@RequestBody Map<String, String> params) {
        this.instanceService.updateInstanceState(params.get("id"), params.get("instanceId"), params.get("action"), regStr(params));
        return ResponseData.ok();
    }

    @PostMapping({"/terminate"})
    public ResponseData<?> terminate(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("terminate", params.get("verifyCode") == null ? null : String.valueOf(params.get("verifyCode")));
        boolean preserveBootVolume = Boolean.TRUE.equals(params.get("preserveBootVolume"));
        this.instanceService
            .terminateInstance(
                params.get("id") == null ? null : String.valueOf(params.get("id")),
                params.get("instanceId") == null ? null : String.valueOf(params.get("instanceId")),
                preserveBootVolume,
                regObj(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/updateInstance"})
    public ResponseData<?> updateInstance(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.instanceService
                .updateInstance(
                    asString(params.get("id")),
                    asString(params.get("instanceId")),
                    asString(params.get("displayName")),
                    asString(params.get("shape")),
                    asFloat(params.get("ocpus")),
                    asFloat(params.get("memoryInGBs")),
                    regObj(params)
                )
        );
    }

    @GetMapping({"/shapeEditTask/{taskId}"})
    public ResponseData<?> shapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok(this.shapeEditTaskManager.getStatus(taskId));
    }

    @PostMapping({"/shapeEditTask/{taskId}/pause"})
    public ResponseData<?> pauseShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok(this.shapeEditTaskManager.pause(taskId));
    }

    @PostMapping({"/shapeEditTask/{taskId}/resume"})
    public ResponseData<?> resumeShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok(this.shapeEditTaskManager.resume(taskId));
    }

    @PostMapping({"/shapeEditTask/{taskId}/stop"})
    public ResponseData<?> stopShapeEditTask(@PathVariable String taskId) {
        return ResponseData.ok(this.shapeEditTaskManager.stop(taskId));
    }

    @PostMapping({"/shapes"})
    public ResponseData<?> listShapes(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listAvailableShapes(params.get("id"), regStr(params)));
    }

    @PostMapping({"/shapesForInstance"})
    public ResponseData<?> shapesForInstance(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listShapesForInstance(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/forceA2ToA1"})
    public ResponseData<?> forceA2ToA1(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.forceA2FlexToA1Flex(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/bootVolumes"})
    public ResponseData<?> bootVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listBootVolumesByInstance(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/updateBootVolume"})
    public ResponseData<?> updateBootVolume(@RequestBody Map<String, Object> params) {
        this.instanceService
            .updateBootVolume(
                asString(params.get("id")),
                asString(params.get("bootVolumeId")),
                asLong(params.get("sizeInGBs")),
                asString(params.get("displayName")),
                asLong(params.get("vpusPerGB")),
                regObj(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/blockVolumes"})
    public ResponseData<?> blockVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listBlockVolumesByInstance(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/unattachedBlockVolumes"})
    public ResponseData<?> unattachedBlockVolumes(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listUnattachedBlockVolumesForInstance(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/createBlockVolumeAndAttach"})
    public ResponseData<?> createBlockVolumeAndAttach(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.instanceService
                .createBlockVolumeAndAttach(
                    asString(params.get("id")),
                    asString(params.get("instanceId")),
                    asString(params.get("displayName")),
                    asLong(params.get("sizeInGBs")),
                    asLong(params.get("vpusPerGB")),
                    asString(params.get("device")),
                    regObj(params)
                )
        );
    }

    @PostMapping({"/attachBlockVolume"})
    public ResponseData<?> attachBlockVolume(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(
            this.instanceService
                .attachBlockVolume(
                    asString(params.get("id")),
                    asString(params.get("instanceId")),
                    asString(params.get("volumeId")),
                    asString(params.get("device")),
                    regObj(params)
                )
        );
    }

    @PostMapping({"/detachBlockVolume"})
    public ResponseData<?> detachBlockVolume(@RequestBody Map<String, Object> params) {
        this.instanceService.detachBlockVolume(asString(params.get("id")), asString(params.get("volumeAttachmentId")), regObj(params));
        return ResponseData.ok();
    }

    @PostMapping({"/updateBlockVolume"})
    public ResponseData<?> updateBlockVolume(@RequestBody Map<String, Object> params) {
        this.instanceService
            .updateBlockVolume(
                asString(params.get("id")),
                asString(params.get("volumeId")),
                asLong(params.get("sizeInGBs")),
                asString(params.get("displayName")),
                asLong(params.get("vpusPerGB")),
                regObj(params)
            );
        return ResponseData.ok();
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Float asFloat(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Number n) {
            return n.floatValue();
        } else {
            try {
                return Float.parseFloat(String.valueOf(v));
            } catch (NumberFormatException var2) {
                return null;
            }
        }
    }

    private static Long asLong(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Number n) {
            return n.longValue();
        } else {
            try {
                return Long.parseLong(String.valueOf(v));
            } catch (NumberFormatException var2) {
                return null;
            }
        }
    }

    @PostMapping({"/instanceDetail"})
    public ResponseData<?> instanceDetail(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.getInstanceNetworkDetail(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/addIpv6"})
    public ResponseData<?> addIpv6(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.addIpv6(params.get("id"), params.get("instanceId"), params.get("vnicId"), regStr(params)));
    }

    @PostMapping({"/removeIpv6"})
    public ResponseData<?> removeIpv6(@RequestBody Map<String, String> params) {
        this.instanceService.removeIpv6(params.get("id"), params.get("ipv6Id"), regStr(params));
        return ResponseData.ok();
    }

    @PostMapping({"/createReservedIp"})
    public ResponseData<?> createReservedIp(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.createReservedIp(params.get("id"), params.get("displayName"), regStr(params)));
    }

    @PostMapping({"/listReservedIps"})
    public ResponseData<?> listReservedIps(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.instanceService.listReservedIps(params.get("id"), regStr(params)));
    }

    @PostMapping({"/deleteReservedIp"})
    public ResponseData<?> deleteReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.deleteReservedIp(params.get("id"), params.get("publicIpId"), regStr(params));
        return ResponseData.ok();
    }

    @PostMapping({"/assignReservedIp"})
    public ResponseData<?> assignReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.assignReservedIp(params.get("id"), params.get("publicIpId"), params.get("instanceId"), regStr(params));
        return ResponseData.ok();
    }

    @PostMapping({"/unassignReservedIp"})
    public ResponseData<?> unassignReservedIp(@RequestBody Map<String, String> params) {
        this.instanceService.unassignReservedIp(params.get("id"), params.get("publicIpId"), regStr(params));
        return ResponseData.ok();
    }

    @PostMapping({"/createConsole"})
    public ResponseData<?> createConsole(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.consoleService.createConsoleConnection(params.get("id"), params.get("instanceId"), regStr(params)));
    }

    @PostMapping({"/deleteConsole"})
    public ResponseData<?> deleteConsole(@RequestBody Map<String, String> params) {
        this.consoleService.deleteConsoleConnection(params.get("id"), params.get("connectionId"), regStr(params));
        return ResponseData.ok();
    }

    private static String regStr(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            String s = params.get("region");
            if (s == null) {
                return null;
            } else {
                s = s.trim();
                return s.isEmpty() ? null : s;
            }
        }
    }

    private static String regObj(Map<String, Object> params) {
        if (params == null) {
            return null;
        } else {
            Object v = params.get("region");
            if (v == null) {
                return null;
            } else {
                String s = String.valueOf(v).trim();
                return s.isEmpty() ? null : s;
            }
        }
    }
}
