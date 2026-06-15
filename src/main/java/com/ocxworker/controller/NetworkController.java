package com.ocxworker.controller;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.NetworkService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/oci/network"})
public class NetworkController {
    @Resource
    private NetworkService networkService;

    @PostMapping({"/vcns"})
    public ResponseData<?> listVcns(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.networkService.listVcns(params.get("id"), reg(params)));
    }

    @PostMapping({"/securityRules"})
    public ResponseData<?> listSecurityRules(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.networkService.listSecurityRulesByInstance(params.get("id"), params.get("instanceId"), reg(params)));
    }

    @PostMapping({"/releaseAllPorts"})
    public ResponseData<?> releaseAllPorts(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.networkService.releaseAllPortsByInstance(params.get("id"), params.get("instanceId"), reg(params)));
    }

    @PostMapping({"/releaseOciPreset"})
    public ResponseData<?> releaseOciPreset(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.networkService.releaseOciPresetByInstance(params.get("id"), params.get("instanceId"), reg(params)));
    }

    @PostMapping({"/addSecurityRule"})
    public ResponseData<?> addSecurityRule(@RequestBody Map<String, String> params) {
        this.networkService
            .addSecurityRule(
                params.get("id"),
                params.get("instanceId"),
                params.get("direction"),
                params.get("protocol"),
                params.get("source"),
                params.get("portMin"),
                params.get("portMax"),
                params.get("description"),
                reg(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/deleteSecurityRule"})
    public ResponseData<?> deleteSecurityRule(@RequestBody Map<String, String> params) {
        String idxStr = params.get("ruleIndex");
        if (idxStr != null && !idxStr.isBlank()) {
            int idx;
            try {
                idx = Integer.parseInt(idxStr);
            } catch (NumberFormatException var5) {
                throw new OciException("ruleIndex 格式非法");
            }

            this.networkService.deleteSecurityRule(params.get("id"), params.get("instanceId"), params.get("direction"), idx, reg(params));
            return ResponseData.ok();
        } else {
            throw new OciException("ruleIndex 不能为空");
        }
    }

    @PostMapping({"/changeIp"})
    public ResponseData<?> changeIp(@RequestBody Map<String, Object> params) {
        this.networkService
            .changePublicIp(
                params.get("id") == null ? null : String.valueOf(params.get("id")),
                params.get("instanceId") == null ? null : String.valueOf(params.get("instanceId")),
                extractStringList(params.get("cidrFilters")),
                regObj(params)
            );
        return ResponseData.ok();
    }

    private static List<String> extractStringList(Object raw) {
        if (raw instanceof List<?> list && !list.isEmpty()) {
            List<String> out = new ArrayList<>(list.size());

            for (Object o : list) {
                if (o != null) {
                    out.add(String.valueOf(o));
                }
            }

            return out;
        }

        return Collections.emptyList();
    }

    @PostMapping({"/assignEphemeralIp"})
    public ResponseData<?> assignEphemeralIp(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.networkService.assignEphemeralPublicIp(params.get("id"), params.get("instanceId"), params.get("privateIpId"), reg(params)));
    }

    @PostMapping({"/deletePublicIp"})
    public ResponseData<?> deletePublicIp(@RequestBody Map<String, String> params) {
        this.networkService.deletePublicIpByPrivateIpId(params.get("id"), params.get("privateIpId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/deleteSecondaryIp"})
    public ResponseData<?> deleteSecondaryIp(@RequestBody Map<String, String> params) {
        this.networkService.deleteSecondaryIp(params.get("id"), params.get("privateIpId"), reg(params));
        return ResponseData.ok();
    }

    private static String reg(Map<String, String> params) {
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
