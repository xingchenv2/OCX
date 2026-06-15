package com.ocxworker.controller;

import com.ocxworker.exception.OciException;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.VcnService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/oci/vcn"})
public class VcnController {
    @Resource
    private VcnService vcnService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping({"/list"})
    public ResponseData<?> list(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listVcns(str(params, "id"), reg(params)));
    }

    @PostMapping({"/create"})
    public ResponseData<?> create(@RequestBody Map<String, Object> params) {
        this.vcnService
            .createVcn(
                str(params, "id"),
                str(params, "compartmentId"),
                str(params, "displayName"),
                str(params, "cidrBlock"),
                str(params, "dnsLabel"),
                bool(params, "createIgw", true),
                reg(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/preview-delete"})
    public ResponseData<?> previewDelete(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.previewVcnDelete(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcn", str(params, "verifyCode"));
        this.vcnService.deleteVcn(str(params, "id"), str(params, "vcnId"), bool(params, "cascade", true), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/update"})
    public ResponseData<?> updateVcn(@RequestBody Map<String, Object> params) {
        this.vcnService.updateVcn(str(params, "id"), str(params, "vcnId"), str(params, "displayName"), null, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/gateways"})
    public ResponseData<?> listVcnGateways(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listVcnGateways(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/subnet/list"})
    public ResponseData<?> listSubnets(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listSubnets(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/subnet/create"})
    public ResponseData<?> createSubnet(@RequestBody Map<String, Object> params) {
        this.vcnService
            .createSubnet(
                str(params, "id"),
                str(params, "vcnId"),
                str(params, "displayName"),
                str(params, "cidrBlock"),
                str(params, "availabilityDomain"),
                str(params, "routeTableId"),
                params.get("prohibitPublicIp") == null ? null : bool(params, "prohibitPublicIp", false),
                reg(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/subnet/delete"})
    public ResponseData<?> deleteSubnet(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSubnet", str(params, "verifyCode"));
        this.vcnService.deleteSubnet(str(params, "id"), str(params, "subnetId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/subnet/update"})
    public ResponseData<?> updateSubnet(@RequestBody Map<String, Object> params) {
        Object secIds = params.get("securityListIds");
        List<String> sl = null;
        if (secIds instanceof List<?> list) {
            sl = new ArrayList<>();

            for (Object o : list) {
                if (o != null) {
                    sl.add(String.valueOf(o));
                }
            }
        }

        this.vcnService.updateSubnet(str(params, "id"), str(params, "subnetId"), str(params, "displayName"), str(params, "routeTableId"), sl, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/igw/list"})
    public ResponseData<?> listIgw(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listInternetGateways(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/igw/create"})
    public ResponseData<?> createIgw(@RequestBody Map<String, Object> params) {
        this.vcnService
            .createInternetGateway(str(params, "id"), str(params, "vcnId"), str(params, "displayName"), bool(params, "isEnabled", true), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/igw/delete"})
    public ResponseData<?> deleteIgw(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnIgw", str(params, "verifyCode"));
        this.vcnService.deleteInternetGateway(str(params, "id"), str(params, "igwId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/igw/update"})
    public ResponseData<?> updateIgw(@RequestBody Map<String, Object> params) {
        Boolean enabled = params.get("isEnabled") == null ? null : bool(params, "isEnabled", true);
        this.vcnService.updateInternetGateway(str(params, "id"), str(params, "igwId"), str(params, "displayName"), enabled, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/igw/setupDefaultRoutes"})
    public ResponseData<?> setupIgwDefaultRoutes(@RequestBody Map<String, Object> params) {
        this.vcnService.setupIgwDefaultRoutes(str(params, "id"), str(params, "vcnId"), str(params, "igwId"), bool(params, "addIpv6", true), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/nat/list"})
    public ResponseData<?> listNat(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listNatGateways(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/nat/create"})
    public ResponseData<?> createNat(@RequestBody Map<String, Object> params) {
        this.vcnService.createNatGateway(str(params, "id"), str(params, "vcnId"), str(params, "displayName"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/nat/delete"})
    public ResponseData<?> deleteNat(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnNat", str(params, "verifyCode"));
        this.vcnService.deleteNatGateway(str(params, "id"), str(params, "natId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/nat/update"})
    public ResponseData<?> updateNat(@RequestBody Map<String, Object> params) {
        Boolean block = params.get("blockTraffic") == null ? null : bool(params, "blockTraffic", false);
        this.vcnService.updateNatGateway(str(params, "id"), str(params, "natId"), str(params, "displayName"), block, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/sg/list"})
    public ResponseData<?> listSg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listServiceGateways(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/sg/create"})
    public ResponseData<?> createSg(@RequestBody Map<String, Object> params) {
        this.vcnService.createServiceGateway(str(params, "id"), str(params, "vcnId"), str(params, "displayName"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/sg/delete"})
    public ResponseData<?> deleteSg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSg", str(params, "verifyCode"));
        this.vcnService.deleteServiceGateway(str(params, "id"), str(params, "sgId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/sg/update"})
    public ResponseData<?> updateSg(@RequestBody Map<String, Object> params) {
        Boolean block = params.get("blockTraffic") == null ? null : bool(params, "blockTraffic", false);
        this.vcnService.updateServiceGateway(str(params, "id"), str(params, "sgId"), str(params, "displayName"), block, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/rt/list"})
    public ResponseData<?> listRt(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listRouteTables(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/rt/delete"})
    public ResponseData<?> deleteRt(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnRt", str(params, "verifyCode"));
        this.vcnService.deleteRouteTable(str(params, "id"), str(params, "rtId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/rt/detail"})
    public ResponseData<?> rtDetail(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.getRouteTable(str(params, "id"), str(params, "rtId"), reg(params)));
    }

    @PostMapping({"/rt/update"})
    public ResponseData<?> updateRt(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> rules = null;
        if (params.get("routeRules") instanceof List<?> list) {
            rules = new ArrayList<>();

            for (Object o : list) {
                if (o instanceof Map) {
                    rules.add((Map<String, Object>)o);
                }
            }
        }

        this.vcnService.updateRouteTable(str(params, "id"), str(params, "rtId"), str(params, "displayName"), rules, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/sl/list"})
    public ResponseData<?> listSl(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listSecurityLists(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/sl/delete"})
    public ResponseData<?> deleteSl(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSl", str(params, "verifyCode"));
        this.vcnService.deleteSecurityList(str(params, "id"), str(params, "slId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/sl/detail"})
    public ResponseData<?> slDetail(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.getSecurityList(str(params, "id"), str(params, "slId"), reg(params)));
    }

    @PostMapping({"/sl/addRule"})
    public ResponseData<?> slAddRule(@RequestBody Map<String, Object> params) {
        this.vcnService
            .addSecurityListRule(
                str(params, "id"),
                str(params, "slId"),
                str(params, "direction"),
                str(params, "protocol"),
                str(params, "source"),
                str(params, "portMin"),
                str(params, "portMax"),
                str(params, "description"),
                reg(params)
            );
        return ResponseData.ok();
    }

    @PostMapping({"/sl/deleteRule"})
    public ResponseData<?> slDeleteRule(@RequestBody Map<String, Object> params) {
        Object idx = params.get("ruleIndex");

        int i;
        try {
            i = Integer.parseInt(String.valueOf(idx));
        } catch (Exception var5) {
            throw new OciException("ruleIndex 非法");
        }

        this.vcnService.deleteSecurityListRule(str(params, "id"), str(params, "slId"), str(params, "direction"), i, reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/drg/list"})
    public ResponseData<?> listDrg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listDrgs(str(params, "id"), reg(params)));
    }

    @PostMapping({"/drg/create"})
    public ResponseData<?> createDrg(@RequestBody Map<String, Object> params) {
        this.vcnService.createDrg(str(params, "id"), str(params, "compartmentId"), str(params, "displayName"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/drg/delete"})
    public ResponseData<?> deleteDrg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnDrg", str(params, "verifyCode"));
        this.vcnService.deleteDrg(str(params, "id"), str(params, "drgId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/lpg/list"})
    public ResponseData<?> listLpg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.vcnService.listLocalPeeringGateways(str(params, "id"), str(params, "vcnId"), reg(params)));
    }

    @PostMapping({"/lpg/create"})
    public ResponseData<?> createLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.createLocalPeeringGateway(str(params, "id"), str(params, "vcnId"), str(params, "displayName"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/lpg/connect"})
    public ResponseData<?> connectLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.connectLocalPeeringGateway(str(params, "id"), str(params, "lpgId"), str(params, "peerId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/lpg/delete"})
    public ResponseData<?> deleteLpg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnLpg", str(params, "verifyCode"));
        this.vcnService.deleteLocalPeeringGateway(str(params, "id"), str(params, "lpgId"), reg(params));
        return ResponseData.ok();
    }

    @PostMapping({"/lpg/update"})
    public ResponseData<?> updateLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.updateLocalPeeringGateway(str(params, "id"), str(params, "lpgId"), str(params, "displayName"), reg(params));
        return ResponseData.ok();
    }

    private static String reg(Map<String, Object> params) {
        Object v = params == null ? null : params.get("region");
        if (v == null) {
            return null;
        } else {
            String s = String.valueOf(v).trim();
            return s.isEmpty() ? null : s;
        }
    }

    private static String str(Map<String, Object> params, String key) {
        Object v = params == null ? null : params.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static boolean bool(Map<String, Object> params, String key, boolean def) {
        Object v = params == null ? null : params.get(key);
        if (v == null) {
            return def;
        } else if (v instanceof Boolean b) {
            return b;
        } else {
            String s = String.valueOf(v).trim().toLowerCase();
            return "true".equals(s) || "1".equals(s) || "yes".equals(s);
        }
    }
}
