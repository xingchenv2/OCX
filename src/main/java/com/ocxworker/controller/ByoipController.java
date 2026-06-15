/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.controller.ByoipController
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.ByoipService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.ByoipService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/oci/byoip"})
public class ByoipController {
    @Resource
    private ByoipService byoipService;

    @PostMapping(value={"/help"})
    public ResponseData<?> help() {
        return ResponseData.ok((Object)this.byoipService.getByoipHelp());
    }

    @PostMapping(value={"/listRanges"})
    public ResponseData<?> listRanges(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.listByoipRanges(params.get("id"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/getRange"})
    public ResponseData<?> getRange(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.getByoipRange(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/createRange"})
    public ResponseData<?> createRange(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.createByoipRange(params.get("id"), params.get("displayName"), params.get("cidrBlock"), params.get("ipv6CidrBlock"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/updateRange"})
    public ResponseData<?> updateRange(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.updateByoipRange(params.get("id"), params.get("byoipRangeId"), params.get("displayName"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/deleteRange"})
    public ResponseData<?> deleteRange(@RequestBody Map<String, String> params) {
        this.byoipService.deleteByoipRange(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/validateRange"})
    public ResponseData<?> validateRange(@RequestBody Map<String, String> params) {
        this.byoipService.validateByoipRange(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/advertiseRange"})
    public ResponseData<?> advertiseRange(@RequestBody Map<String, String> params) {
        this.byoipService.advertiseByoipRange(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/withdrawRange"})
    public ResponseData<?> withdrawRange(@RequestBody Map<String, String> params) {
        this.byoipService.withdrawByoipRange(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/changeRangeCompartment"})
    public ResponseData<?> changeRangeCompartment(@RequestBody Map<String, String> params) {
        this.byoipService.changeByoipRangeCompartment(params.get("id"), params.get("byoipRangeId"), params.get("compartmentId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/listAllocatedRanges"})
    public ResponseData<?> listAllocatedRanges(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.listByoipAllocatedRanges(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/listPools"})
    public ResponseData<?> listPools(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.listPublicIpPools(params.get("id"), params.get("byoipRangeId"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/createPool"})
    public ResponseData<?> createPool(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.createPublicIpPool(params.get("id"), params.get("displayName"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/updatePool"})
    public ResponseData<?> updatePool(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.updatePublicIpPool(params.get("id"), params.get("publicIpPoolId"), params.get("displayName"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/deletePool"})
    public ResponseData<?> deletePool(@RequestBody Map<String, String> params) {
        this.byoipService.deletePublicIpPool(params.get("id"), params.get("publicIpPoolId"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/addPoolCapacity"})
    public ResponseData<?> addPoolCapacity(@RequestBody Map<String, String> params) {
        this.byoipService.addPublicIpPoolCapacity(params.get("id"), params.get("publicIpPoolId"), params.get("byoipRangeId"), params.get("cidrBlock"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/removePoolCapacity"})
    public ResponseData<?> removePoolCapacity(@RequestBody Map<String, String> params) {
        this.byoipService.removePublicIpPoolCapacity(params.get("id"), params.get("publicIpPoolId"), params.get("cidrBlock"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/createPublicIp"})
    public ResponseData<?> createPublicIp(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.createByoipReservedIp(params.get("id"), params.get("displayName"), params.get("publicIpPoolId"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/listPublicIps"})
    public ResponseData<?> listPublicIps(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.byoipService.listByoipPublicIps(params.get("id"), ByoipController.reg(params)));
    }

    @PostMapping(value={"/assignIpv6ToVcn"})
    public ResponseData<?> assignIpv6ToVcn(@RequestBody Map<String, String> params) {
        this.byoipService.assignByoipv6ToVcn(params.get("id"), params.get("vcnId"), params.get("byoipRangeId"), params.get("ipv6CidrBlock"), ByoipController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/listByoipRanges"})
    public ResponseData<?> listByoipRangesLegacy(@RequestBody Map<String, String> params) {
        return this.listRanges(params);
    }

    @PostMapping(value={"/listPublicIpPools"})
    public ResponseData<?> listPublicIpPoolsLegacy(@RequestBody Map<String, String> params) {
        return this.listPools(params);
    }

    @PostMapping(value={"/createByoipReservedIp"})
    public ResponseData<?> createByoipReservedIpLegacy(@RequestBody Map<String, String> params) {
        return this.createPublicIp(params);
    }

    private static String reg(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        String s = params.get("region");
        if (s == null) {
            return null;
        }
        return (s = s.trim()).isEmpty() ? null : s;
    }
}

