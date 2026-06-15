/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.IamPolicyService
 *  com.ocxworker.service.OciClientService
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.identity.model.Policy
 *  com.oracle.bmc.identity.requests.GetPolicyRequest
 *  com.oracle.bmc.identity.requests.ListPoliciesRequest
 *  com.oracle.bmc.identity.requests.ListPoliciesRequest$Builder
 *  com.oracle.bmc.identity.responses.GetPolicyResponse
 *  com.oracle.bmc.identity.responses.ListPoliciesResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciClientService;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.Policy;
import com.oracle.bmc.identity.requests.GetPolicyRequest;
import com.oracle.bmc.identity.requests.ListPoliciesRequest;
import com.oracle.bmc.identity.responses.GetPolicyResponse;
import com.oracle.bmc.identity.responses.ListPoliciesResponse;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class IamPolicyService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(IamPolicyService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return new OciClientService(SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build());
    }

    public Map<String, Object> listPolicies(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        String tenancyId = user.getOciTenantId();
        ArrayList<Map> items = new ArrayList<Map>();
        HashSet<String> seenPolicyIds = new HashSet<String>();
        try (OciClientService client = this.buildClient(tenantId);){
            IdentityClient identityClient = client.getIdentityClient();
            List compartments = client.listAllCompartments();
            for (Compartment compartment : compartments) {
                ListPoliciesResponse resp;
                String cid = compartment.getId();
                if (cid == null || cid.isBlank()) continue;
                String page = null;
                do {
                    ListPoliciesRequest.Builder req = ListPoliciesRequest.builder().compartmentId(cid);
                    if (page != null) {
                        req.page(page);
                    }
                    if ((resp = identityClient.listPolicies(req.build())).getItems() == null) continue;
                    for (Policy p : resp.getItems()) {
                        if (p.getId() != null && !seenPolicyIds.add(p.getId())) continue;
                        items.add(IamPolicyService.policySummary((Policy)p));
                    }
                } while ((page = resp.getOpcNextPage()) != null && !page.isBlank());
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn("listPolicies failed for tenant config {}: {}", (Object)tenantId, (Object)e.getMessage());
            throw new OciException("\u83b7\u53d6 IAM \u7b56\u7565\u5931\u8d25: " + e.getMessage());
        }
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("compartmentId", tenancyId);
        out.put("items", items);
        out.put("count", items.size());
        return out;
    }

    public Map<String, Object> getPolicy(String tenantId, String policyId) {
        Map map;
        block11: {
            if (policyId == null || policyId.isBlank()) {
                throw new OciException("policyId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciClientService client = this.buildClient(tenantId);
            try {
                GetPolicyResponse resp = client.getIdentityClient().getPolicy(GetPolicyRequest.builder().policyId(policyId).build());
                Policy p = resp.getPolicy();
                if (p == null) {
                    throw new OciException("\u7b56\u7565\u4e0d\u5b58\u5728");
                }
                Map detail = IamPolicyService.policySummary((Policy)p);
                detail.put("statements", p.getStatements() != null ? p.getStatements() : List.of());
                map = detail;
                if (client == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (client != null) {
                        try {
                            client.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    log.warn("getPolicy {} failed: {}", (Object)policyId, (Object)e.getMessage());
                    throw new OciException("\u83b7\u53d6\u7b56\u7565\u8be6\u60c5\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    private static Map<String, Object> policySummary(Policy p) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("description", p.getDescription());
        m.put("compartmentId", p.getCompartmentId());
        m.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        List stmts = p.getStatements();
        m.put("statementCount", stmts != null ? stmts.size() : 0);
        m.put("timeCreated", p.getTimeCreated());
        return m;
    }
}

