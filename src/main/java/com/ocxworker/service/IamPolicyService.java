package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.Policy;
import com.oracle.bmc.identity.requests.GetPolicyRequest;
import com.oracle.bmc.identity.requests.ListPoliciesRequest;
import com.oracle.bmc.identity.requests.ListPoliciesRequest.Builder;
import com.oracle.bmc.identity.responses.GetPolicyResponse;
import com.oracle.bmc.identity.responses.ListPoliciesResponse;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IamPolicyService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(IamPolicyService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            return new OciClientService(
                SysUserDTO.builder()
                    .username(user.getUsername())
                    .ociCfg(
                        SysUserDTO.OciCfg.builder()
                            .tenantId(user.getOciTenantId())
                            .userId(user.getOciUserId())
                            .fingerprint(user.getOciFingerprint())
                            .region(user.getOciRegion())
                            .privateKeyPath(user.getOciKeyPath())
                            .build()
                    )
                    .build()
            );
        }
    }

    public Map<String, Object> listPolicies(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            String tenancyId = user.getOciTenantId();
            List<Map<String, Object>> items = new ArrayList<>();
            Set<String> seenPolicyIds = new HashSet<>();

            try (OciClientService client = this.buildClient(tenantId)) {
                IdentityClient identityClient = client.getIdentityClient();

                for (Compartment compartment : client.listAllCompartments()) {
                    String cid = compartment.getId();
                    if (cid != null && !cid.isBlank()) {
                        String page = null;

                        while (true) {
                            Builder req = ListPoliciesRequest.builder().compartmentId(cid);
                            if (page != null) {
                                req.page(page);
                            }

                            ListPoliciesResponse resp = identityClient.listPolicies(req.build());
                            if (resp.getItems() != null) {
                                for (Policy p : resp.getItems()) {
                                    if (p.getId() == null || seenPolicyIds.add(p.getId())) {
                                        items.add(policySummary(p));
                                    }
                                }
                            }

                            page = resp.getOpcNextPage();
                            if (page == null || page.isBlank()) {
                                break;
                            }
                        }
                    }
                }
            } catch (OciException var19) {
                throw var19;
            } catch (Exception var20) {
                log.warn("listPolicies failed for tenant config {}: {}", tenantId, var20.getMessage());
                throw new OciException("获取 IAM 策略失败: " + var20.getMessage());
            }

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("compartmentId", tenancyId);
            out.put("items", items);
            out.put("count", items.size());
            return out;
        }
    }

    public Map<String, Object> getPolicy(String tenantId, String policyId) {
        if (policyId != null && !policyId.isBlank()) {
            try {
                Map var7;
                try (OciClientService client = this.buildClient(tenantId)) {
                    GetPolicyResponse resp = client.getIdentityClient().getPolicy(GetPolicyRequest.builder().policyId(policyId).build());
                    Policy p = resp.getPolicy();
                    if (p == null) {
                        throw new OciException("策略不存在");
                    }

                    Map<String, Object> detail = policySummary(p);
                    detail.put("statements", p.getStatements() != null ? p.getStatements() : List.of());
                    var7 = detail;
                }

                return var7;
            } catch (OciException var10) {
                throw var10;
            } catch (Exception var11) {
                log.warn("getPolicy {} failed: {}", policyId, var11.getMessage());
                throw new OciException("获取策略详情失败: " + var11.getMessage());
            }
        } else {
            throw new OciException("policyId 不能为空");
        }
    }

    private static Map<String, Object> policySummary(Policy p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("description", p.getDescription());
        m.put("compartmentId", p.getCompartmentId());
        m.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        List<String> stmts = p.getStatements();
        m.put("statementCount", stmts != null ? stmts.size() : 0);
        m.put("timeCreated", p.getTimeCreated());
        return m;
    }
}
