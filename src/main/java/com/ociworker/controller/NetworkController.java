/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.NetworkController
 *  com.ociworker.exception.OciException
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.NetworkService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.exception.OciException;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.NetworkService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/oci/network"})
public class NetworkController {
    @Resource
    private NetworkService networkService;

    @PostMapping(value={"/vcns"})
    public ResponseData<?> listVcns(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.networkService.listVcns(params.get("id"), NetworkController.reg(params)));
    }

    @PostMapping(value={"/securityRules"})
    public ResponseData<?> listSecurityRules(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.networkService.listSecurityRulesByInstance(params.get("id"), params.get("instanceId"), NetworkController.reg(params)));
    }

    @PostMapping(value={"/releaseAllPorts"})
    public ResponseData<?> releaseAllPorts(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.networkService.releaseAllPortsByInstance(params.get("id"), params.get("instanceId"), NetworkController.reg(params)));
    }

    @PostMapping(value={"/releaseOciPreset"})
    public ResponseData<?> releaseOciPreset(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.networkService.releaseOciPresetByInstance(params.get("id"), params.get("instanceId"), NetworkController.reg(params)));
    }

    @PostMapping(value={"/addSecurityRule"})
    public ResponseData<?> addSecurityRule(@RequestBody Map<String, String> params) {
        this.networkService.addSecurityRule(params.get("id"), params.get("instanceId"), params.get("direction"), params.get("protocol"), params.get("source"), params.get("portMin"), params.get("portMax"), params.get("description"), NetworkController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/deleteSecurityRule"})
    public ResponseData<?> deleteSecurityRule(@RequestBody Map<String, String> params) {
        int idx;
        String idxStr = params.get("ruleIndex");
        if (idxStr == null || idxStr.isBlank()) {
            throw new OciException("ruleIndex \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try {
            idx = Integer.parseInt(idxStr);
        }
        catch (NumberFormatException e) {
            throw new OciException("ruleIndex \u683c\u5f0f\u975e\u6cd5");
        }
        this.networkService.deleteSecurityRule(params.get("id"), params.get("instanceId"), params.get("direction"), idx, NetworkController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/changeIp"})
    public ResponseData<?> changeIp(@RequestBody Map<String, Object> params) {
        this.networkService.changePublicIp(params.get("id") == null ? null : String.valueOf(params.get("id")), params.get("instanceId") == null ? null : String.valueOf(params.get("instanceId")), NetworkController.extractStringList((Object)params.get("cidrFilters")), NetworkController.regObj(params));
        return ResponseData.ok();
    }

    private static List<String> extractStringList(Object raw) {
        List list;
        if (!(raw instanceof List) || (list = (List)raw).isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> out = new ArrayList<String>(list.size());
        for (Object o : list) {
            if (o == null) continue;
            out.add(String.valueOf(o));
        }
        return out;
    }

    @PostMapping(value={"/assignEphemeralIp"})
    public ResponseData<?> assignEphemeralIp(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.networkService.assignEphemeralPublicIp(params.get("id"), params.get("instanceId"), params.get("privateIpId"), NetworkController.reg(params)));
    }

    @PostMapping(value={"/deletePublicIp"})
    public ResponseData<?> deletePublicIp(@RequestBody Map<String, String> params) {
        this.networkService.deletePublicIpByPrivateIpId(params.get("id"), params.get("privateIpId"), NetworkController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/deleteSecondaryIp"})
    public ResponseData<?> deleteSecondaryIp(@RequestBody Map<String, String> params) {
        this.networkService.deleteSecondaryIp(params.get("id"), params.get("privateIpId"), NetworkController.reg(params));
        return ResponseData.ok();
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

