/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.VcnController
 *  com.ociworker.exception.OciException
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.VcnService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.exception.OciException;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.VcnService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
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
@RequestMapping(value={"/api/oci/vcn"})
public class VcnController {
    @Resource
    private VcnService vcnService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/list"})
    public ResponseData<?> list(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listVcns(VcnController.str(params, (String)"id"), VcnController.reg(params)));
    }

    @PostMapping(value={"/create"})
    public ResponseData<?> create(@RequestBody Map<String, Object> params) {
        this.vcnService.createVcn(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"compartmentId"), VcnController.str(params, (String)"displayName"), VcnController.str(params, (String)"cidrBlock"), VcnController.str(params, (String)"dnsLabel"), VcnController.bool(params, (String)"createIgw", (boolean)true), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/preview-delete"})
    public ResponseData<?> previewDelete(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.previewVcnDelete(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcn", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteVcn(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.bool(params, (String)"cascade", (boolean)true), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/update"})
    public ResponseData<?> updateVcn(@RequestBody Map<String, Object> params) {
        this.vcnService.updateVcn(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), null, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/gateways"})
    public ResponseData<?> listVcnGateways(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listVcnGateways(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/subnet/list"})
    public ResponseData<?> listSubnets(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listSubnets(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/subnet/create"})
    public ResponseData<?> createSubnet(@RequestBody Map<String, Object> params) {
        this.vcnService.createSubnet(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), VcnController.str(params, (String)"cidrBlock"), VcnController.str(params, (String)"availabilityDomain"), VcnController.str(params, (String)"routeTableId"), params.get("prohibitPublicIp") == null ? null : Boolean.valueOf(VcnController.bool(params, (String)"prohibitPublicIp", (boolean)false)), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/subnet/delete"})
    public ResponseData<?> deleteSubnet(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSubnet", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteSubnet(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"subnetId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/subnet/update"})
    public ResponseData<?> updateSubnet(@RequestBody Map<String, Object> params) {
        Object secIds = params.get("securityListIds");
        ArrayList<String> sl = null;
        if (secIds instanceof List) {
            List list = (List)secIds;
            sl = new ArrayList<String>();
            for (Object o : list) {
                if (o == null) continue;
                sl.add(String.valueOf(o));
            }
        }
        this.vcnService.updateSubnet(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"subnetId"), VcnController.str(params, (String)"displayName"), VcnController.str(params, (String)"routeTableId"), sl, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/igw/list"})
    public ResponseData<?> listIgw(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listInternetGateways(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/igw/create"})
    public ResponseData<?> createIgw(@RequestBody Map<String, Object> params) {
        this.vcnService.createInternetGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), VcnController.bool(params, (String)"isEnabled", (boolean)true), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/igw/delete"})
    public ResponseData<?> deleteIgw(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnIgw", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteInternetGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"igwId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/igw/update"})
    public ResponseData<?> updateIgw(@RequestBody Map<String, Object> params) {
        Boolean enabled = params.get("isEnabled") == null ? null : Boolean.valueOf(VcnController.bool(params, (String)"isEnabled", (boolean)true));
        this.vcnService.updateInternetGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"igwId"), VcnController.str(params, (String)"displayName"), enabled, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/igw/setupDefaultRoutes"})
    public ResponseData<?> setupIgwDefaultRoutes(@RequestBody Map<String, Object> params) {
        this.vcnService.setupIgwDefaultRoutes(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"igwId"), VcnController.bool(params, (String)"addIpv6", (boolean)true), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/nat/list"})
    public ResponseData<?> listNat(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listNatGateways(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/nat/create"})
    public ResponseData<?> createNat(@RequestBody Map<String, Object> params) {
        this.vcnService.createNatGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/nat/delete"})
    public ResponseData<?> deleteNat(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnNat", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteNatGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"natId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/nat/update"})
    public ResponseData<?> updateNat(@RequestBody Map<String, Object> params) {
        Boolean block = params.get("blockTraffic") == null ? null : Boolean.valueOf(VcnController.bool(params, (String)"blockTraffic", (boolean)false));
        this.vcnService.updateNatGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"natId"), VcnController.str(params, (String)"displayName"), block, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sg/list"})
    public ResponseData<?> listSg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listServiceGateways(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/sg/create"})
    public ResponseData<?> createSg(@RequestBody Map<String, Object> params) {
        this.vcnService.createServiceGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sg/delete"})
    public ResponseData<?> deleteSg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSg", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteServiceGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"sgId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sg/update"})
    public ResponseData<?> updateSg(@RequestBody Map<String, Object> params) {
        Boolean block = params.get("blockTraffic") == null ? null : Boolean.valueOf(VcnController.bool(params, (String)"blockTraffic", (boolean)false));
        this.vcnService.updateServiceGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"sgId"), VcnController.str(params, (String)"displayName"), block, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/rt/list"})
    public ResponseData<?> listRt(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listRouteTables(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/rt/delete"})
    public ResponseData<?> deleteRt(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnRt", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteRouteTable(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"rtId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/rt/detail"})
    public ResponseData<?> rtDetail(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.getRouteTable(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"rtId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/rt/update"})
    public ResponseData<?> updateRt(@RequestBody Map<String, Object> params) {
        ArrayList<Map> rules = null;
        Object rr = params.get("routeRules");
        if (rr instanceof List) {
            List list = (List)rr;
            rules = new ArrayList<Map>();
            for (Object o : list) {
                if (!(o instanceof Map)) continue;
                rules.add((Map)o);
            }
        }
        this.vcnService.updateRouteTable(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"rtId"), VcnController.str(params, (String)"displayName"), rules, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sl/list"})
    public ResponseData<?> listSl(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listSecurityLists(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/sl/delete"})
    public ResponseData<?> deleteSl(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnSl", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteSecurityList(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"slId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sl/detail"})
    public ResponseData<?> slDetail(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.getSecurityList(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"slId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/sl/addRule"})
    public ResponseData<?> slAddRule(@RequestBody Map<String, Object> params) {
        this.vcnService.addSecurityListRule(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"slId"), VcnController.str(params, (String)"direction"), VcnController.str(params, (String)"protocol"), VcnController.str(params, (String)"source"), VcnController.str(params, (String)"portMin"), VcnController.str(params, (String)"portMax"), VcnController.str(params, (String)"description"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/sl/deleteRule"})
    public ResponseData<?> slDeleteRule(@RequestBody Map<String, Object> params) {
        int i;
        Object idx = params.get("ruleIndex");
        try {
            i = Integer.parseInt(String.valueOf(idx));
        }
        catch (Exception e) {
            throw new OciException("ruleIndex \u975e\u6cd5");
        }
        this.vcnService.deleteSecurityListRule(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"slId"), VcnController.str(params, (String)"direction"), i, VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/drg/list"})
    public ResponseData<?> listDrg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listDrgs(VcnController.str(params, (String)"id"), VcnController.reg(params)));
    }

    @PostMapping(value={"/drg/create"})
    public ResponseData<?> createDrg(@RequestBody Map<String, Object> params) {
        this.vcnService.createDrg(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"compartmentId"), VcnController.str(params, (String)"displayName"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/drg/delete"})
    public ResponseData<?> deleteDrg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnDrg", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteDrg(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"drgId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/lpg/list"})
    public ResponseData<?> listLpg(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.vcnService.listLocalPeeringGateways(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.reg(params)));
    }

    @PostMapping(value={"/lpg/create"})
    public ResponseData<?> createLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.createLocalPeeringGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"vcnId"), VcnController.str(params, (String)"displayName"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/lpg/connect"})
    public ResponseData<?> connectLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.connectLocalPeeringGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"lpgId"), VcnController.str(params, (String)"peerId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/lpg/delete"})
    public ResponseData<?> deleteLpg(@RequestBody Map<String, Object> params) {
        this.verifyCodeService.verifyCode("deleteVcnLpg", VcnController.str(params, (String)"verifyCode"));
        this.vcnService.deleteLocalPeeringGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"lpgId"), VcnController.reg(params));
        return ResponseData.ok();
    }

    @PostMapping(value={"/lpg/update"})
    public ResponseData<?> updateLpg(@RequestBody Map<String, Object> params) {
        this.vcnService.updateLocalPeeringGateway(VcnController.str(params, (String)"id"), VcnController.str(params, (String)"lpgId"), VcnController.str(params, (String)"displayName"), VcnController.reg(params));
        return ResponseData.ok();
    }

    private static String reg(Map<String, Object> params) {
        Object v;
        Object object = v = params == null ? null : params.get("region");
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    private static String str(Map<String, Object> params, String key) {
        Object v = params == null ? null : params.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static boolean bool(Map<String, Object> params, String key, boolean def) {
        Object v;
        Object object = v = params == null ? null : params.get(key);
        if (v == null) {
            return def;
        }
        if (v instanceof Boolean) {
            Boolean b = (Boolean)v;
            return b;
        }
        String s = String.valueOf(v).trim().toLowerCase();
        return "true".equals(s) || "1".equals(s) || "yes".equals(s);
    }
}

