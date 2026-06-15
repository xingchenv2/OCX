/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.DomainManagementService
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.VerifyCodeService
 *  com.oracle.bmc.audit.AuditClient
 *  com.oracle.bmc.audit.model.AuditEvent
 *  com.oracle.bmc.audit.model.Data
 *  com.oracle.bmc.audit.model.Identity
 *  com.oracle.bmc.audit.requests.ListEventsRequest
 *  com.oracle.bmc.audit.requests.ListEventsRequest$Builder
 *  com.oracle.bmc.audit.responses.ListEventsResponse
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.model.DomainSummary
 *  com.oracle.bmc.identity.requests.ListDomainsRequest
 *  com.oracle.bmc.identity.requests.ListDomainsRequest$Builder
 *  com.oracle.bmc.identity.responses.ListDomainsResponse
 *  com.oracle.bmc.identitydomains.IdentityDomainsClient
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSetting
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSetting$Builder
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsEndpointRestrictions
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsEndpointRestrictions$Builder
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsThirdPartyFactor
 *  com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsThirdPartyFactor$Builder
 *  com.oracle.bmc.identitydomains.model.Operations
 *  com.oracle.bmc.identitydomains.model.Operations$Op
 *  com.oracle.bmc.identitydomains.model.PasswordPolicy
 *  com.oracle.bmc.identitydomains.model.PatchOp
 *  com.oracle.bmc.identitydomains.model.Policy
 *  com.oracle.bmc.identitydomains.model.SortOrder
 *  com.oracle.bmc.identitydomains.requests.GetPolicyRequest
 *  com.oracle.bmc.identitydomains.requests.ListAuthenticationFactorSettingsRequest
 *  com.oracle.bmc.identitydomains.requests.ListPasswordPoliciesRequest
 *  com.oracle.bmc.identitydomains.requests.ListPoliciesRequest
 *  com.oracle.bmc.identitydomains.requests.PatchPasswordPolicyRequest
 *  com.oracle.bmc.identitydomains.requests.PatchPolicyRequest
 *  com.oracle.bmc.identitydomains.requests.PutAuthenticationFactorSettingRequest
 *  com.oracle.bmc.identitydomains.responses.GetPolicyResponse
 *  com.oracle.bmc.identitydomains.responses.ListAuthenticationFactorSettingsResponse
 *  com.oracle.bmc.identitydomains.responses.ListPasswordPoliciesResponse
 *  com.oracle.bmc.identitydomains.responses.ListPoliciesResponse
 *  com.oracle.bmc.limits.LimitsClient
 *  com.oracle.bmc.limits.model.LimitValueSummary
 *  com.oracle.bmc.limits.model.ServiceSummary
 *  com.oracle.bmc.limits.requests.GetResourceAvailabilityRequest
 *  com.oracle.bmc.limits.requests.ListLimitValuesRequest
 *  com.oracle.bmc.limits.requests.ListLimitValuesRequest$Builder
 *  com.oracle.bmc.limits.requests.ListServicesRequest
 *  com.oracle.bmc.limits.responses.GetResourceAvailabilityResponse
 *  com.oracle.bmc.limits.responses.ListLimitValuesResponse
 *  com.oracle.bmc.limits.responses.ListServicesResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import com.ociworker.exception.OciException;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.dto.SysUserDTO;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.OciClientService;
import com.ociworker.service.VerifyCodeService;
import com.oracle.bmc.audit.AuditClient;
import com.oracle.bmc.audit.model.AuditEvent;
import com.oracle.bmc.audit.model.Data;
import com.oracle.bmc.audit.model.Identity;
import com.oracle.bmc.audit.requests.ListEventsRequest;
import com.oracle.bmc.audit.responses.ListEventsResponse;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.DomainSummary;
import com.oracle.bmc.identity.requests.ListDomainsRequest;
import com.oracle.bmc.identity.responses.ListDomainsResponse;
import com.oracle.bmc.identitydomains.IdentityDomainsClient;
import com.oracle.bmc.identitydomains.model.AuthenticationFactorSetting;
import com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsEndpointRestrictions;
import com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsThirdPartyFactor;
import com.oracle.bmc.identitydomains.model.Operations;
import com.oracle.bmc.identitydomains.model.PasswordPolicy;
import com.oracle.bmc.identitydomains.model.PatchOp;
import com.oracle.bmc.identitydomains.model.Policy;
import com.oracle.bmc.identitydomains.model.SortOrder;
import com.oracle.bmc.identitydomains.requests.GetPolicyRequest;
import com.oracle.bmc.identitydomains.requests.ListAuthenticationFactorSettingsRequest;
import com.oracle.bmc.identitydomains.requests.ListPasswordPoliciesRequest;
import com.oracle.bmc.identitydomains.requests.ListPoliciesRequest;
import com.oracle.bmc.identitydomains.requests.PatchPasswordPolicyRequest;
import com.oracle.bmc.identitydomains.requests.PatchPolicyRequest;
import com.oracle.bmc.identitydomains.requests.PutAuthenticationFactorSettingRequest;
import com.oracle.bmc.identitydomains.responses.GetPolicyResponse;
import com.oracle.bmc.identitydomains.responses.ListAuthenticationFactorSettingsResponse;
import com.oracle.bmc.identitydomains.responses.ListPasswordPoliciesResponse;
import com.oracle.bmc.identitydomains.responses.ListPoliciesResponse;
import com.oracle.bmc.limits.LimitsClient;
import com.oracle.bmc.limits.model.LimitValueSummary;
import com.oracle.bmc.limits.model.ServiceSummary;
import com.oracle.bmc.limits.requests.GetResourceAvailabilityRequest;
import com.oracle.bmc.limits.requests.ListLimitValuesRequest;
import com.oracle.bmc.limits.requests.ListServicesRequest;
import com.oracle.bmc.limits.responses.GetResourceAvailabilityResponse;
import com.oracle.bmc.limits.responses.ListLimitValuesResponse;
import com.oracle.bmc.limits.responses.ListServicesResponse;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class DomainManagementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DomainManagementService.class);
    private static final String OCI_CONSOLE_POLICY_ID = "OciConsolePolicy";
    private static final String DEFAULT_PASSWORD_POLICY_NAME = "DefaultPasswordPolicy";
    private static final String CONSENT_SCHEMA = "urn:ietf:params:scim:schemas:oracle:idcs:extension:ociconsolesignonpolicyconsent:Policy";
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private VerifyCodeService verifyCodeService;
    private static final Map<String, Long> AUTH_FACTOR_TOKENS = new ConcurrentHashMap();
    private static final long AUTH_FACTOR_TOKEN_TTL_MS = 600000L;
    private static final Map<String, String> FACTOR_PATH = new LinkedHashMap();

    public String unlockAuthFactors(String inputCode) {
        if (inputCode == null || inputCode.isBlank()) {
            throw new OciException("\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801");
        }
        this.verifyCodeService.verifyCode("authFactors", inputCode);
        long now = System.currentTimeMillis();
        AUTH_FACTOR_TOKENS.entrySet().removeIf(e -> (Long)e.getValue() < now);
        String token = UUID.randomUUID().toString();
        AUTH_FACTOR_TOKENS.put(token, now + 600000L);
        return token;
    }

    private void requireAuthFactorToken(String token) {
        if (token == null || token.isBlank()) {
            throw new OciException("\u4f1a\u8bdd\u672a\u89e3\u9501\uff0c\u8bf7\u5148\u901a\u8fc7 TG \u9a8c\u8bc1\u7801\u89e3\u9501");
        }
        Long exp = (Long)AUTH_FACTOR_TOKENS.get(token);
        if (exp == null) {
            throw new OciException("\u4f1a\u8bdd\u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u89e3\u9501");
        }
        if (System.currentTimeMillis() > exp) {
            AUTH_FACTOR_TOKENS.remove(token);
            throw new OciException("\u4f1a\u8bdd\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u89e3\u9501");
        }
    }

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return new OciClientService(SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build());
    }

    public List<Map<String, Object>> listDomains(OciClientService client, boolean suppressErrors) {
        ArrayList<Map<String, Object>> domains;
        block7: {
            domains = new ArrayList<Map<String, Object>>();
            try {
                ListDomainsResponse resp;
                IdentityClient identityClient = client.getIdentityClient();
                String tenancyId = client.getProvider().getTenantId();
                String page = null;
                do {
                    ListDomainsRequest.Builder req = ListDomainsRequest.builder().compartmentId(tenancyId).limit(Integer.valueOf(1000));
                    if (page != null) {
                        req.page(page);
                    }
                    resp = identityClient.listDomains(req.build());
                    for (DomainSummary d : resp.getItems()) {
                        String state;
                        if (d.getUrl() == null || "DELETING".equalsIgnoreCase(state = d.getLifecycleState() == null ? null : d.getLifecycleState().getValue()) || "DELETED".equalsIgnoreCase(state)) continue;
                        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                        m.put("id", d.getId());
                        m.put("displayName", d.getDisplayName());
                        m.put("type", d.getType());
                        m.put("url", d.getUrl());
                        m.put("lifecycleState", state);
                        m.put("isHiddenOnLogin", d.getIsHiddenOnLogin());
                        domains.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null && !page.isEmpty());
                domains.sort((a, b) -> this.domainRank(a) - this.domainRank(b) != 0 ? this.domainRank(a) - this.domainRank(b) : String.valueOf(a.get("displayName")).compareToIgnoreCase(String.valueOf(b.get("displayName"))));
                if (log.isInfoEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    for (Map map : domains) {
                        sb.append("[").append(map.get("displayName")).append("/").append(map.get("type")).append("] ");
                    }
                    log.info("Identity Domains found: {}", (Object)sb);
                }
            }
            catch (Exception e) {
                log.warn("Failed to list domains: {}", (Object)e.getMessage());
                if (suppressErrors) break block7;
                throw new OciException("\u5217\u51fa Identity Domain \u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
            }
        }
        return domains;
    }

    public List<Map<String, Object>> listIdentityDomains(String tenantId) {
        try (OciClientService c = this.buildClient(tenantId);){
            List list = this.listDomains(c, false);
            return list;
        }
    }

    public OciClientService openOciClient(String tenantId) {
        return this.buildClient(tenantId);
    }

    private int domainRank(Map<String, Object> d) {
        String name = String.valueOf(d.get("displayName"));
        if ("Default".equals(name)) {
            return 0;
        }
        if ("OracleIdentityCloudService".equalsIgnoreCase(name)) {
            return 1;
        }
        return 2;
    }

    private IdentityDomainsClient newDomainClient(OciClientService client, String domainUrl) {
        IdentityDomainsClient c = IdentityDomainsClient.builder().build((AbstractAuthenticationDetailsProvider)client.getProvider());
        c.setEndpoint(domainUrl);
        return c;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> getDomainSettings(String tenantId) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        ArrayList domainResults = new ArrayList();
        try (OciClientService client = this.buildClient(tenantId);){
            List domains = this.listDomains(client, true);
            if (domains.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230 Identity Domain");
            }
            for (Map d : domains) {
                LinkedHashMap r = new LinkedHashMap();
                r.put("domainId", d.get("id"));
                r.put("displayName", d.get("displayName"));
                r.put("type", d.get("type"));
                IdentityDomainsClient dc = null;
                try {
                    dc = this.newDomainClient(client, (String)d.get("url"));
                    Map mfa = this.fetchOciConsolePolicy(dc);
                    r.putAll(mfa);
                    Map pwd = this.fetchDefaultPasswordPolicy(dc);
                    r.putAll(pwd);
                }
                catch (Exception e) {
                    log.warn("Domain {} settings fetch failed: {}", d.get("displayName"), (Object)e.getMessage());
                    r.put("error", e.getMessage());
                }
                finally {
                    if (dc != null) {
                        try {
                            dc.close();
                        }
                        catch (Exception exception) {}
                    }
                }
                domainResults.add(r);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u83b7\u53d6\u57df\u8bbe\u7f6e\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        result.put("domains", domainResults);
        return result;
    }

    private Map<String, Object> fetchOciConsolePolicy(IdentityDomainsClient dc) {
        LinkedHashMap<String, Object> r = new LinkedHashMap<String, Object>();
        try {
            GetPolicyResponse resp = dc.getPolicy(GetPolicyRequest.builder().policyId("OciConsolePolicy").build());
            Policy p = resp.getPolicy();
            r.put("consolePolicyId", p.getId());
            r.put("consolePolicyName", p.getName());
            r.put("mfaEnabled", Boolean.TRUE.equals(p.getActive()));
            r.put("consolePolicyDescription", p.getDescription());
        }
        catch (Exception e) {
            try {
                ListPoliciesResponse lr = dc.listPolicies(ListPoliciesRequest.builder().filter("name eq \"OciConsolePolicy\"").count(Integer.valueOf(1)).build());
                List items = lr.getPolicies().getResources();
                if (items != null && !items.isEmpty()) {
                    Policy p = (Policy)items.get(0);
                    r.put("consolePolicyId", p.getId());
                    r.put("consolePolicyName", p.getName());
                    r.put("mfaEnabled", Boolean.TRUE.equals(p.getActive()));
                    r.put("consolePolicyDescription", p.getDescription());
                } else {
                    r.put("mfaEnabled", null);
                    r.put("mfaError", "\u672a\u627e\u5230 Security Policy for OCI Console\uff08\u8be5\u79df\u6237\u53ef\u80fd\u672a\u542f\u7528 Identity Domain \u65b0\u7248\u7b7e\u540d\u7b56\u7565\uff09");
                }
            }
            catch (Exception ee) {
                log.warn("Fallback listPolicies failed: {}", (Object)ee.getMessage());
                r.put("mfaEnabled", null);
                r.put("mfaError", ee.getMessage());
            }
        }
        return r;
    }

    private Map<String, Object> fetchDefaultPasswordPolicy(IdentityDomainsClient dc) {
        LinkedHashMap<String, Object> r = new LinkedHashMap<String, Object>();
        try {
            ListPasswordPoliciesResponse resp = dc.listPasswordPolicies(ListPasswordPoliciesRequest.builder().filter("name eq \"DefaultPasswordPolicy\"").count(Integer.valueOf(1)).build());
            List list = resp.getPasswordPolicies().getResources();
            PasswordPolicy pp = null;
            if (list != null && !list.isEmpty()) {
                pp = (PasswordPolicy)list.get(0);
            } else {
                ListPasswordPoliciesResponse any = dc.listPasswordPolicies(ListPasswordPoliciesRequest.builder().sortBy("priority").sortOrder(SortOrder.Ascending).count(Integer.valueOf(1)).build());
                List anyItems = any.getPasswordPolicies().getResources();
                if (anyItems != null && !anyItems.isEmpty()) {
                    pp = (PasswordPolicy)anyItems.get(0);
                }
            }
            if (pp != null) {
                r.put("passwordPolicyId", pp.getId());
                r.put("passwordPolicyName", pp.getName());
                r.put("passwordExpiresAfterDays", pp.getPasswordExpiresAfter());
                r.put("passwordPolicyPriority", pp.getPriority());
            }
        }
        catch (Exception e) {
            log.warn("fetchDefaultPasswordPolicy failed: {}", (Object)e.getMessage());
            r.put("passwordPolicyError", e.getMessage());
        }
        return r;
    }

    public void updateMfaSetting(String tenantId, String domainId, boolean enabled) {
        try (OciClientService client = this.buildClient(tenantId);){
            List domains = this.listDomains(client, true);
            Map target = this.findDomain(domains, domainId);
            try (IdentityDomainsClient dc = this.newDomainClient(client, (String)target.get("url"));){
                ArrayList<Operations> ops = new ArrayList<Operations>();
                ops.add(Operations.builder().op(Operations.Op.Replace).path("active").value((Object)enabled).build());
                ops.add(Operations.builder().op(Operations.Op.Replace).path("urn:ietf:params:scim:schemas:oracle:idcs:extension:ociconsolesignonpolicyconsent:Policy:consent").value((Object)true).build());
                ops.add(Operations.builder().op(Operations.Op.Replace).path("urn:ietf:params:scim:schemas:oracle:idcs:extension:ociconsolesignonpolicyconsent:Policy:justification").value((Object)(enabled ? "MFA enabled via oci-worker" : "MFA disabled via oci-worker")).build());
                PatchOp patch = PatchOp.builder().schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp")).operations(ops).build();
                dc.patchPolicy(PatchPolicyRequest.builder().policyId("OciConsolePolicy").patchOp(patch).build());
                log.info("OciConsolePolicy active={} for tenant={} domain={}", new Object[]{enabled, tenantId, target.get("displayName")});
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 MFA \u7b56\u7565\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
    }

    public void updatePasswordExpiry(String tenantId, String domainId, int days) {
        try (OciClientService client = this.buildClient(tenantId);){
            List domains = this.listDomains(client, true);
            Map target = this.findDomain(domains, domainId);
            try (IdentityDomainsClient dc = this.newDomainClient(client, (String)target.get("url"));){
                List any;
                List list = dc.listPasswordPolicies(ListPasswordPoliciesRequest.builder().filter("name eq \"DefaultPasswordPolicy\"").count(Integer.valueOf(1)).build()).getPasswordPolicies().getResources();
                PasswordPolicy existing = null;
                if (list != null && !list.isEmpty()) {
                    existing = (PasswordPolicy)list.get(0);
                }
                if (existing == null && (any = dc.listPasswordPolicies(ListPasswordPoliciesRequest.builder().sortBy("priority").sortOrder(SortOrder.Ascending).count(Integer.valueOf(1)).build()).getPasswordPolicies().getResources()) != null && !any.isEmpty()) {
                    existing = (PasswordPolicy)any.get(0);
                }
                if (existing == null) {
                    throw new OciException("\u672a\u627e\u5230\u5bc6\u7801\u7b56\u7565\uff08DefaultPasswordPolicy\uff09");
                }
                ArrayList<Operations> ops = new ArrayList<Operations>();
                ops.add(Operations.builder().op(Operations.Op.Replace).path("passwordExpiresAfter").value((Object)days).build());
                PatchOp patch = PatchOp.builder().schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp")).operations(ops).build();
                dc.patchPasswordPolicy(PatchPasswordPolicyRequest.builder().passwordPolicyId(existing.getId()).patchOp(patch).build());
                log.info("passwordExpiresAfter={} days for tenant={} domain={} policy={}", new Object[]{days, tenantId, target.get("displayName"), existing.getName()});
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0\u5bc6\u7801\u7b56\u7565\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
    }

    private Map<String, Object> findDomain(List<Map<String, Object>> domains, String domainId) {
        if (domains == null || domains.isEmpty()) {
            throw new OciException("\u672a\u627e\u5230 Identity Domain");
        }
        if (domainId == null || domainId.isBlank()) {
            for (Map<String, Object> d : domains) {
                if (!"DEFAULT".equalsIgnoreCase(String.valueOf(d.get("type")))) continue;
                return d;
            }
            return domains.get(0);
        }
        for (Map<String, Object> d : domains) {
            if (!domainId.equals(d.get("id"))) continue;
            return d;
        }
        throw new OciException("\u672a\u627e\u5230\u6307\u5b9a domain: " + domainId);
    }

    public List<Map<String, Object>> getAuditLogs(String tenantId) {
        return this.getAuditLogs(tenantId, 7);
    }

    public List<Map<String, Object>> getAuditLogs(String tenantId, int days) {
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try (OciClientService client = this.buildClient(tenantId);){
            List domains = this.listDomains(client, true);
            if (domains.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230 Identity Domain");
            }
            int window = Math.max(1, Math.min(days, 30));
            Date endTime = new Date();
            Date startTime = Date.from(Instant.now().minus(Duration.ofDays(window)));
            ArrayList events = new ArrayList();
            try (AuditClient auditClient = AuditClient.builder().build((AbstractAuthenticationDetailsProvider)client.getProvider());){
                ListEventsResponse resp;
                String tenancyId = client.getProvider().getTenantId();
                String page = null;
                int maxEvents = 12000;
                do {
                    ListEventsRequest.Builder reqB = ListEventsRequest.builder().compartmentId(tenancyId).startTime(startTime).endTime(endTime);
                    if (page != null) {
                        reqB.page(page);
                    }
                    if ((resp = auditClient.listEvents(reqB.build())).getItems() == null) continue;
                    events.addAll(resp.getItems());
                } while ((page = resp.getOpcNextPage()) != null && !page.isEmpty() && events.size() < maxEvents);
                log.info("OCI Audit listEvents rawTotal={} windowDays={}", (Object)events.size(), (Object)window);
            }
            LinkedHashMap grouped = new LinkedHashMap();
            for (Map d : domains) {
                grouped.put((String)d.get("id"), new ArrayList());
            }
            ArrayList unknown = new ArrayList();
            HashMap<String, String> nameToId = new HashMap<String, String>();
            for (Object d : domains) {
                Object n = d.get("displayName");
                if (n == null) continue;
                nameToId.put(String.valueOf(n).trim().toLowerCase(Locale.ROOT), (String)d.get("id"));
            }
            String fallbackDomainId = DomainManagementService.resolveLoginLogFallbackDomainId((List)domains);
            for (AuditEvent ev : events) {
                Object eid;
                Map addl;
                String etFull = ev.getEventType();
                String scmEventId = null;
                Data data = ev.getData();
                Map map = addl = data != null && data.getAdditionalDetails() != null ? DomainManagementService.castStringObjectMap((Object)data.getAdditionalDetails()) : null;
                if (addl != null && (eid = addl.get("eventId")) != null) {
                    scmEventId = String.valueOf(eid).trim();
                }
                if (!DomainManagementService.matchesLoginAuditEvent(scmEventId, (String)etFull)) continue;
                String domainIdFromEvent = DomainManagementService.resolveLoginLogDomainId((Data)data, nameToId);
                String actorName = null;
                String principalId = null;
                String clientIp = null;
                String userAgent = null;
                String ssoApp = null;
                String ssoProtectedResource = null;
                String ssoIdp = null;
                String ssoFactor = null;
                String msg = null;
                if (data != null) {
                    Identity identity = data.getIdentity();
                    if (identity != null) {
                        actorName = identity.getPrincipalName();
                        principalId = identity.getPrincipalId();
                        clientIp = identity.getIpAddress();
                        userAgent = identity.getUserAgent();
                    }
                    if (addl != null) {
                        Object xc;
                        Object f;
                        Object ip;
                        Object ap;
                        Object a2;
                        Object an = addl.get("actorName");
                        if (an != null && !String.valueOf(an).isBlank()) {
                            actorName = String.valueOf(an).trim();
                        }
                        if ((a2 = DomainManagementService.firstNonBlank((Map)addl, (String)"ssoProtectedResource", (String)"protectedResource")) != null) {
                            ssoProtectedResource = String.valueOf(a2);
                        }
                        if ((ap = DomainManagementService.firstNonBlank((Map)addl, (String)"ssoApplicationType", (String)"applicationDisplayName")) != null) {
                            ssoApp = String.valueOf(ap);
                        }
                        if ((ip = addl.get("ssoIdentityProvider")) != null) {
                            ssoIdp = String.valueOf(ip);
                        }
                        if ((f = addl.get("ssoAuthFactor")) != null) {
                            ssoFactor = String.valueOf(f);
                        }
                        if ((clientIp == null || clientIp.isBlank()) && (xc = DomainManagementService.firstNonBlank((Map)addl, (String)"clientIp", (String)"ipAddress")) != null) {
                            clientIp = String.valueOf(xc).trim();
                        }
                    }
                    if (data.getResponse() != null && data.getResponse().getMessage() != null) {
                        msg = data.getResponse().getMessage();
                    }
                    if (msg == null && data.getEventName() != null) {
                        msg = data.getEventName();
                    }
                }
                LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                row.put("eventTime", ev.getEventTime() == null ? null : ev.getEventTime().toInstant().toString());
                row.put("eventId", scmEventId != null && !scmEventId.isBlank() ? scmEventId : (etFull != null ? etFull : ""));
                row.put("auditEventType", etFull);
                row.put("actorName", actorName);
                row.put("principalId", principalId);
                row.put("actorDisplayName", actorName);
                row.put("ssoIdentityProvider", ssoIdp);
                row.put("ssoApplicationType", ssoApp);
                row.put("ssoProtectedResource", ssoProtectedResource);
                row.put("ssoUserAgent", userAgent);
                row.put("clientIp", clientIp);
                row.put("ssoAuthFactor", ssoFactor);
                row.put("message", msg);
                if (domainIdFromEvent != null && grouped.containsKey(domainIdFromEvent)) {
                    ((List)grouped.get(domainIdFromEvent)).add(row);
                    continue;
                }
                unknown.add(row);
            }
            if (!unknown.isEmpty()) {
                String target = fallbackDomainId != null && grouped.containsKey(fallbackDomainId) ? fallbackDomainId : (String)((Map)domains.getFirst()).get("id");
                ((List)grouped.get(target)).addAll(unknown);
            }
            for (Map d : domains) {
                LinkedHashMap entry = new LinkedHashMap();
                entry.put("domainId", d.get("id"));
                entry.put("displayName", d.get("displayName"));
                entry.put("type", d.get("type"));
                List logs = grouped.getOrDefault((String)d.get("id"), new ArrayList());
                logs.sort((a, b) -> {
                    String ta = String.valueOf(a.getOrDefault("eventTime", ""));
                    String tb = String.valueOf(b.getOrDefault("eventTime", ""));
                    return tb.compareTo(ta);
                });
                entry.put("logs", logs);
                result.add(entry);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u83b7\u53d6\u767b\u5f55\u65e5\u5fd7\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        return result;
    }

    private static Map<String, Object> castStringObjectMap(Object raw) {
        if (!(raw instanceof Map)) {
            return null;
        }
        Map map = (Map)raw;
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        for (Map.Entry e : map.entrySet()) {
            if (e.getKey() == null) continue;
            out.put(String.valueOf(e.getKey()), e.getValue());
        }
        return out;
    }

    private static Object firstNonBlank(Map<String, Object> map, String k1, String k2) {
        Object v2;
        Object v1;
        Object object = v1 = map == null ? null : map.get(k1);
        if (v1 != null && String.valueOf(v1).trim().length() > 0) {
            return v1;
        }
        Object object2 = v2 = map == null ? null : map.get(k2);
        if (v2 != null && String.valueOf(v2).trim().length() > 0) {
            return v2;
        }
        return null;
    }

    private static boolean matchesLoginAuditScmEventId(String scmEventId) {
        String s = scmEventId.trim().toLowerCase(Locale.ROOT);
        return s.startsWith("sso.session.") || "sso.authentication.failure".equals(s) || s.startsWith("sso.app.access.") || s.startsWith("admin.authentication.") || "sso.auth.factor.initiated".equals(s);
    }

    private static boolean matchesLoginAuditByLegacyEventType(String eventTypeFull) {
        if (eventTypeFull == null) {
            return false;
        }
        String etl = eventTypeFull.toLowerCase(Locale.ROOT);
        if (!(etl.contains("identitydomain") || etl.contains("identitydomains") || etl.contains("idcs"))) {
            return false;
        }
        return etl.contains("session") || etl.contains("authentication") || etl.contains("appaccess") || etl.contains("signin") || etl.contains("sso");
    }

    private static boolean matchesLoginAuditEvent(String scmEventIdNullable, String eventTypeFull) {
        if (scmEventIdNullable != null && !scmEventIdNullable.isBlank()) {
            return DomainManagementService.matchesLoginAuditScmEventId((String)scmEventIdNullable);
        }
        return DomainManagementService.matchesLoginAuditByLegacyEventType((String)eventTypeFull);
    }

    private static String resolveLoginLogDomainId(Data data, Map<String, String> nameToId) {
        String ds;
        if (data == null) {
            return null;
        }
        String rid = data.getResourceId();
        if (rid != null && rid.contains("ocid1.domain.")) {
            return rid;
        }
        Map addl = DomainManagementService.castStringObjectMap((Object)data.getAdditionalDetails());
        if (addl == null) {
            return null;
        }
        Object did = DomainManagementService.firstNonBlank((Map)addl, (String)"domainOcid", (String)"domainId");
        String string = ds = did != null ? String.valueOf(did).trim() : "";
        if (ds.startsWith("ocid1.domain.")) {
            return ds;
        }
        Object dn = addl.get("domainDisplayName");
        if (dn == null || String.valueOf(dn).isBlank()) {
            return null;
        }
        return nameToId.get(String.valueOf(dn).trim().toLowerCase(Locale.ROOT));
    }

    private static String resolveLoginLogFallbackDomainId(List<Map<String, Object>> domains) {
        if (domains == null || domains.isEmpty()) {
            return null;
        }
        for (Map<String, Object> d : domains) {
            if (!"DEFAULT".equalsIgnoreCase(String.valueOf(d.get("type")))) continue;
            return (String)d.get("id");
        }
        for (Map<String, Object> d : domains) {
            if (!"Default".equals(d.get("displayName"))) continue;
            return (String)d.get("id");
        }
        return (String)domains.getFirst().get("id");
    }

    public Map<String, Object> listAuthFactorSettings(String tenantId, String token) {
        this.requireAuthFactorToken(token);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        ArrayList domainResults = new ArrayList();
        try (OciClientService client = this.buildClient(tenantId);){
            List domains = this.listDomains(client, true);
            if (domains.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230 Identity Domain");
            }
            for (Map d : domains) {
                LinkedHashMap r = new LinkedHashMap();
                r.put("domainId", d.get("id"));
                r.put("displayName", d.get("displayName"));
                r.put("type", d.get("type"));
                try (IdentityDomainsClient dc = this.newDomainClient(client, (String)d.get("url"));){
                    AuthenticationFactorSetting s = this.firstAuthFactorSetting(dc);
                    r.put("settingId", s.getId());
                    LinkedHashMap<String, Boolean> factors = new LinkedHashMap<String, Boolean>();
                    factors.put("totp", this.bool(s.getTotpEnabled()));
                    factors.put("push", this.bool(s.getPushEnabled()));
                    factors.put("sms", this.bool(s.getSmsEnabled()));
                    factors.put("phoneCall", this.bool(s.getPhoneCallEnabled()));
                    factors.put("email", this.bool(s.getEmailEnabled()));
                    factors.put("securityQuestions", this.bool(s.getSecurityQuestionsEnabled()));
                    factors.put("fido", this.bool(s.getFidoAuthenticatorEnabled()));
                    factors.put("yubico", this.bool(s.getYubicoOtpEnabled()));
                    factors.put("bypassCode", this.bool(s.getBypassCodeEnabled()));
                    factors.put("duoSecurity", s.getThirdPartyFactor() != null && Boolean.TRUE.equals(s.getThirdPartyFactor().getDuoSecurity()));
                    r.put("factors", factors);
                    LinkedHashMap<String, Integer> limits = new LinkedHashMap<String, Integer>();
                    LinkedHashMap<String, Comparable<Boolean>> trusted = new LinkedHashMap<String, Comparable<Boolean>>();
                    int maxIncorrect = 0;
                    AuthenticationFactorSettingsEndpointRestrictions er = s.getEndpointRestrictions();
                    if (er != null) {
                        limits.put("maxEnrolledDevices", er.getMaxEnrolledDevices());
                        trusted.put("enabled", Boolean.valueOf(this.bool(er.getTrustedEndpointsEnabled())));
                        trusted.put("maxTrustedEndpoints", er.getMaxTrustedEndpoints());
                        trusted.put("maxEndpointTrustDurationInDays", er.getMaxEndpointTrustDurationInDays());
                        if (er.getMaxIncorrectAttempts() != null) {
                            maxIncorrect = er.getMaxIncorrectAttempts();
                        }
                    }
                    limits.put("maxIncorrectAttempts", maxIncorrect);
                    r.put("limits", limits);
                    r.put("trustedDevice", trusted);
                }
                catch (Exception e) {
                    log.warn("list auth factor for domain {} failed: {}", d.get("displayName"), (Object)e.getMessage());
                    r.put("error", e.getMessage() == null ? "\u67e5\u8be2\u5931\u8d25" : e.getMessage());
                }
                domainResults.add(r);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u8bfb\u53d6\u9a8c\u8bc1\u56e0\u7d20\u8bbe\u7f6e\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        result.put("domains", domainResults);
        return result;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public Map<String, Object> updateAuthFactorSettings(String tenantId, String domainId, String token, Map<String, Object> desiredFactors, Map<String, Object> desiredLimits, Map<String, Object> desiredTrustedDevice) {
        this.requireAuthFactorToken(token);
        try (OciClientService client = this.buildClient(tenantId);){
            LinkedHashMap<String, Object> linkedHashMap;
            block54: {
                LinkedHashMap<String, Object> resp;
                int changed;
                AuthenticationFactorSetting.Builder b;
                AuthenticationFactorSetting current;
                IdentityDomainsClient dc;
                Map target;
                block52: {
                    block53: {
                        List domains = this.listDomains(client, true);
                        target = this.findDomain(domains, domainId);
                        dc = this.newDomainClient(client, (String)target.get("url"));
                        try {
                            AuthenticationFactorSettingsEndpointRestrictions er;
                            current = this.firstAuthFactorSetting(dc);
                            b = current.toBuilder();
                            changed = 0;
                            if (desiredFactors != null) {
                                for (String key : FACTOR_PATH.keySet()) {
                                    boolean now;
                                    boolean want;
                                    if (!desiredFactors.containsKey(key) || (want = Boolean.TRUE.equals(desiredFactors.get(key))) == (now = this.currentFactorValue(current, key))) continue;
                                    ++changed;
                                    switch (key) {
                                        case "totp": {
                                            b.totpEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "push": {
                                            b.pushEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "sms": {
                                            b.smsEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "phoneCall": {
                                            b.phoneCallEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "email": {
                                            b.emailEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "securityQuestions": {
                                            b.securityQuestionsEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "fido": {
                                            b.fidoAuthenticatorEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "yubico": {
                                            b.yubicoOtpEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "bypassCode": {
                                            b.bypassCodeEnabled(Boolean.valueOf(want));
                                            break;
                                        }
                                        case "duoSecurity": {
                                            AuthenticationFactorSettingsThirdPartyFactor.Builder tpfBase = current.getThirdPartyFactor() != null ? current.getThirdPartyFactor().toBuilder() : AuthenticationFactorSettingsThirdPartyFactor.builder();
                                            b.thirdPartyFactor(tpfBase.duoSecurity(Boolean.valueOf(want)).build());
                                            break;
                                        }
                                    }
                                }
                            }
                            AuthenticationFactorSettingsEndpointRestrictions.Builder erBuilder = (er = current.getEndpointRestrictions()) != null ? er.toBuilder() : AuthenticationFactorSettingsEndpointRestrictions.builder();
                            boolean erChanged = false;
                            if (desiredLimits != null) {
                                Integer nowInc;
                                Integer now;
                                Integer want = this.asInt(desiredLimits.get("maxEnrolledDevices"));
                                Integer n = now = er == null ? null : er.getMaxEnrolledDevices();
                                if (want != null && !Objects.equals(want, now)) {
                                    erBuilder.maxEnrolledDevices(want);
                                    erChanged = true;
                                }
                                Integer wantInc = this.asInt(desiredLimits.get("maxIncorrectAttempts"));
                                Integer n2 = nowInc = er == null ? null : er.getMaxIncorrectAttempts();
                                if (wantInc != null && !Objects.equals(wantInc, nowInc)) {
                                    erBuilder.maxIncorrectAttempts(wantInc);
                                    erChanged = true;
                                }
                            }
                            if (desiredTrustedDevice != null) {
                                Integer nowDays;
                                Integer nowMax;
                                if (desiredTrustedDevice.containsKey("enabled")) {
                                    boolean now;
                                    boolean want = Boolean.TRUE.equals(desiredTrustedDevice.get("enabled"));
                                    boolean bl = now = er != null && Boolean.TRUE.equals(er.getTrustedEndpointsEnabled());
                                    if (want != now) {
                                        erBuilder.trustedEndpointsEnabled(Boolean.valueOf(want));
                                        erChanged = true;
                                    }
                                }
                                Integer wantMax = this.asInt(desiredTrustedDevice.get("maxTrustedEndpoints"));
                                Integer n = nowMax = er == null ? null : er.getMaxTrustedEndpoints();
                                if (wantMax != null && !Objects.equals(wantMax, nowMax)) {
                                    erBuilder.maxTrustedEndpoints(wantMax);
                                    erChanged = true;
                                }
                                Integer wantDays = this.asInt(desiredTrustedDevice.get("maxEndpointTrustDurationInDays"));
                                Integer n3 = nowDays = er == null ? null : er.getMaxEndpointTrustDurationInDays();
                                if (wantDays != null && !Objects.equals(wantDays, nowDays)) {
                                    erBuilder.maxEndpointTrustDurationInDays(wantDays);
                                    erChanged = true;
                                }
                            }
                            if (erChanged) {
                                b.endpointRestrictions(erBuilder.build());
                                ++changed;
                            }
                            resp = new LinkedHashMap<String, Object>();
                            resp.put("domainId", target.get("id"));
                            resp.put("displayName", target.get("displayName"));
                            resp.put("changedOps", changed);
                            if (changed != 0) break block52;
                            resp.put("skipped", true);
                            linkedHashMap = resp;
                            if (dc == null) break block53;
                        }
                        catch (Throwable throwable) {
                            if (dc != null) {
                                try {
                                    dc.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        dc.close();
                    }
                    return linkedHashMap;
                }
                dc.putAuthenticationFactorSetting(PutAuthenticationFactorSettingRequest.builder().authenticationFactorSettingId(current.getId()).authenticationFactorSetting(b.build()).build());
                log.info("AuthFactorSetting put: tenant={} domain={} changedGroups={}", new Object[]{tenantId, target.get("displayName"), changed});
                linkedHashMap = resp;
                if (dc == null) break block54;
                dc.close();
            }
            return linkedHashMap;
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0\u9a8c\u8bc1\u56e0\u7d20\u8bbe\u7f6e\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
    }

    private boolean currentFactorValue(AuthenticationFactorSetting s, String key) {
        switch (key) {
            case "totp": {
                return Boolean.TRUE.equals(s.getTotpEnabled());
            }
            case "push": {
                return Boolean.TRUE.equals(s.getPushEnabled());
            }
            case "sms": {
                return Boolean.TRUE.equals(s.getSmsEnabled());
            }
            case "phoneCall": {
                return Boolean.TRUE.equals(s.getPhoneCallEnabled());
            }
            case "email": {
                return Boolean.TRUE.equals(s.getEmailEnabled());
            }
            case "securityQuestions": {
                return Boolean.TRUE.equals(s.getSecurityQuestionsEnabled());
            }
            case "fido": {
                return Boolean.TRUE.equals(s.getFidoAuthenticatorEnabled());
            }
            case "yubico": {
                return Boolean.TRUE.equals(s.getYubicoOtpEnabled());
            }
            case "bypassCode": {
                return Boolean.TRUE.equals(s.getBypassCodeEnabled());
            }
            case "duoSecurity": {
                return s.getThirdPartyFactor() != null && Boolean.TRUE.equals(s.getThirdPartyFactor().getDuoSecurity());
            }
        }
        return false;
    }

    private AuthenticationFactorSetting firstAuthFactorSetting(IdentityDomainsClient dc) {
        List items;
        ListAuthenticationFactorSettingsResponse resp = dc.listAuthenticationFactorSettings(ListAuthenticationFactorSettingsRequest.builder().build());
        List list = items = resp.getAuthenticationFactorSettings() == null ? null : resp.getAuthenticationFactorSettings().getResources();
        if (items == null || items.isEmpty()) {
            throw new OciException("\u672a\u627e\u5230 AuthenticationFactorSetting");
        }
        return (AuthenticationFactorSetting)items.get(0);
    }

    private boolean bool(Boolean b) {
        return Boolean.TRUE.equals(b);
    }

    private Integer asInt(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            Number n = (Number)v;
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Map<String, Object>> getServiceQuotas(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        ArrayList<Map<String, Object>> quotaList = new ArrayList<Map<String, Object>>();
        try (OciClientService client = this.buildClient(tenantId);
             LimitsClient limitsClient = LimitsClient.builder().build((AbstractAuthenticationDetailsProvider)client.getProvider());){
            ListServicesResponse servicesResp = limitsClient.listServices(ListServicesRequest.builder().compartmentId(user.getOciTenantId()).build());
            List<String> targetServices = Arrays.asList("compute", "vcn", "block-storage", "load-balancer", "network-load-balancer", "identity", "regions", "database", "objectstorage", "file-storage", "container-engine", "generative-ai", "data-science", "dns");
            for (ServiceSummary svc : servicesResp.getItems()) {
                ListLimitValuesResponse limitsResp;
                String svcName = svc.getName();
                if (!targetServices.contains(svcName)) continue;
                String nextPage = null;
                do {
                    ListLimitValuesRequest.Builder reqBuilder = ListLimitValuesRequest.builder().compartmentId(user.getOciTenantId()).serviceName(svcName);
                    if (nextPage != null) {
                        reqBuilder.page(nextPage);
                    }
                    limitsResp = limitsClient.listLimitValues(reqBuilder.build());
                    for (LimitValueSummary lv : limitsResp.getItems()) {
                        if (lv.getValue() == null || lv.getValue() == 0L) continue;
                        LinkedHashMap<String, Object> entry = new LinkedHashMap<String, Object>();
                        entry.put("serviceName", svcName);
                        entry.put("limitName", lv.getName());
                        entry.put("availabilityDomain", lv.getAvailabilityDomain());
                        entry.put("limit", lv.getValue());
                        try {
                            GetResourceAvailabilityResponse usageResp = limitsClient.getResourceAvailability(GetResourceAvailabilityRequest.builder().compartmentId(user.getOciTenantId()).serviceName(svcName).limitName(lv.getName()).availabilityDomain(lv.getAvailabilityDomain()).build());
                            entry.put("used", usageResp.getResourceAvailability().getUsed());
                            entry.put("available", usageResp.getResourceAvailability().getAvailable());
                        }
                        catch (Exception ignored) {
                            entry.put("used", null);
                            entry.put("available", null);
                        }
                        quotaList.add(entry);
                    }
                } while ((nextPage = limitsResp.getOpcNextPage()) != null);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u83b7\u53d6\u914d\u989d\u4fe1\u606f\u5931\u8d25: " + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        return quotaList;
    }

    static {
        FACTOR_PATH.put("totp", "totpEnabled");
        FACTOR_PATH.put("push", "pushEnabled");
        FACTOR_PATH.put("sms", "smsEnabled");
        FACTOR_PATH.put("phoneCall", "phoneCallEnabled");
        FACTOR_PATH.put("email", "emailEnabled");
        FACTOR_PATH.put("securityQuestions", "securityQuestionsEnabled");
        FACTOR_PATH.put("fido", "fidoAuthenticatorEnabled");
        FACTOR_PATH.put("yubico", "yubicoOtpEnabled");
        FACTOR_PATH.put("bypassCode", "bypassCodeEnabled");
        FACTOR_PATH.put("duoSecurity", "thirdPartyFactor.duoSecurity");
    }
}

