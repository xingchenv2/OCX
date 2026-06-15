package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.audit.AuditClient;
import com.oracle.bmc.audit.model.AuditEvent;
import com.oracle.bmc.audit.model.Data;
import com.oracle.bmc.audit.model.Identity;
import com.oracle.bmc.audit.requests.ListEventsRequest;
import com.oracle.bmc.audit.responses.ListEventsResponse;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.DomainSummary;
import com.oracle.bmc.identity.requests.ListDomainsRequest;
import com.oracle.bmc.identity.requests.ListDomainsRequest.Builder;
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
import com.oracle.bmc.identitydomains.model.Operations.Op;
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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private static final Map<String, Long> AUTH_FACTOR_TOKENS = new ConcurrentHashMap<>();
    private static final long AUTH_FACTOR_TOKEN_TTL_MS = 600000L;
    private static final Map<String, String> FACTOR_PATH = new LinkedHashMap<>();

    public String unlockAuthFactors(String inputCode) {
        if (inputCode != null && !inputCode.isBlank()) {
            this.verifyCodeService.verifyCode("authFactors", inputCode);
            long now = System.currentTimeMillis();
            AUTH_FACTOR_TOKENS.entrySet().removeIf(e -> e.getValue() < now);
            String token = UUID.randomUUID().toString();
            AUTH_FACTOR_TOKENS.put(token, now + 600000L);
            return token;
        } else {
            throw new OciException("请输入验证码");
        }
    }

    private void requireAuthFactorToken(String token) {
        if (token != null && !token.isBlank()) {
            Long exp = AUTH_FACTOR_TOKENS.get(token);
            if (exp == null) {
                throw new OciException("会话已失效，请重新解锁");
            } else if (System.currentTimeMillis() > exp) {
                AUTH_FACTOR_TOKENS.remove(token);
                throw new OciException("会话已过期，请重新解锁");
            }
        } else {
            throw new OciException("会话未解锁，请先通过 TG 验证码解锁");
        }
    }

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

    public List<Map<String, Object>> listDomains(OciClientService client, boolean suppressErrors) {
        List<Map<String, Object>> domains = new ArrayList<>();

        try {
            IdentityClient identityClient = client.getIdentityClient();
            String tenancyId = client.getProvider().getTenantId();
            String page = null;

            do {
                Builder req = ListDomainsRequest.builder().compartmentId(tenancyId).limit(1000);
                if (page != null) {
                    req.page(page);
                }

                ListDomainsResponse resp = identityClient.listDomains(req.build());

                for (DomainSummary d : resp.getItems()) {
                    if (d.getUrl() != null) {
                        String state = d.getLifecycleState() == null ? null : d.getLifecycleState().getValue();
                        if (!"DELETING".equalsIgnoreCase(state) && !"DELETED".equalsIgnoreCase(state)) {
                            Map<String, Object> m = new LinkedHashMap<>();
                            m.put("id", d.getId());
                            m.put("displayName", d.getDisplayName());
                            m.put("type", d.getType());
                            m.put("url", d.getUrl());
                            m.put("lifecycleState", state);
                            m.put("isHiddenOnLogin", d.getIsHiddenOnLogin());
                            domains.add(m);
                        }
                    }
                }

                page = resp.getOpcNextPage();
            } while (page != null && !page.isEmpty());

            domains.sort(
                (a, b) -> this.domainRank((Map<String, Object>)a) - this.domainRank((Map<String, Object>)b) != 0
                        ? this.domainRank((Map<String, Object>)a) - this.domainRank((Map<String, Object>)b)
                        : String.valueOf(a.get("displayName")).compareToIgnoreCase(String.valueOf(b.get("displayName")))
            );
            if (log.isInfoEnabled()) {
                StringBuilder sb = new StringBuilder();

                for (Map<String, Object> dx : domains) {
                    sb.append("[").append(dx.get("displayName")).append("/").append(dx.get("type")).append("] ");
                }

                log.info("Identity Domains found: {}", sb);
            }
        } catch (Exception var13) {
            log.warn("Failed to list domains: {}", var13.getMessage());
            if (!suppressErrors) {
                throw new OciException("列出 Identity Domain 失败: " + (var13.getMessage() != null ? var13.getMessage() : "未知错误"));
            }
        }

        return domains;
    }

    public List<Map<String, Object>> listIdentityDomains(String tenantId) {
        List var3;
        try (OciClientService c = this.buildClient(tenantId)) {
            var3 = this.listDomains(c, false);
        }

        return var3;
    }

    public OciClientService openOciClient(String tenantId) {
        return this.buildClient(tenantId);
    }

    private int domainRank(Map<String, Object> d) {
        String name = String.valueOf(d.get("displayName"));
        if ("Default".equals(name)) {
            return 0;
        } else {
            return "OracleIdentityCloudService".equalsIgnoreCase(name) ? 1 : 2;
        }
    }

    private IdentityDomainsClient newDomainClient(OciClientService client, String domainUrl) {
        IdentityDomainsClient c = IdentityDomainsClient.builder().build(client.getProvider());
        c.setEndpoint(domainUrl);
        return c;
    }

    public Map<String, Object> getDomainSettings(String tenantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> domainResults = new ArrayList<>();

        try (OciClientService client = this.buildClient(tenantId)) {
            List<Map<String, Object>> domains = this.listDomains(client, true);
            if (domains.isEmpty()) {
                throw new OciException("未找到 Identity Domain");
            }

            for (Map<String, Object> d : domains) {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("domainId", d.get("id"));
                r.put("displayName", d.get("displayName"));
                r.put("type", d.get("type"));
                IdentityDomainsClient dc = null;

                try {
                    dc = this.newDomainClient(client, (String)d.get("url"));
                    Map<String, Object> mfa = this.fetchOciConsolePolicy(dc);
                    r.putAll(mfa);
                    Map<String, Object> pwd = this.fetchDefaultPasswordPolicy(dc);
                    r.putAll(pwd);
                } catch (Exception var25) {
                    log.warn("Domain {} settings fetch failed: {}", d.get("displayName"), var25.getMessage());
                    r.put("error", var25.getMessage());
                } finally {
                    if (dc != null) {
                        try {
                            dc.close();
                        } catch (Exception var24) {
                        }
                    }
                }

                domainResults.add(r);
            }
        } catch (OciException var28) {
            throw var28;
        } catch (Exception var29) {
            throw new OciException("获取域设置失败: " + (var29.getMessage() == null ? "未知错误" : var29.getMessage()));
        }

        result.put("domains", domainResults);
        return result;
    }

    private Map<String, Object> fetchOciConsolePolicy(IdentityDomainsClient dc) {
        Map<String, Object> r = new LinkedHashMap<>();

        try {
            GetPolicyResponse resp = dc.getPolicy(GetPolicyRequest.builder().policyId("OciConsolePolicy").build());
            Policy p = resp.getPolicy();
            r.put("consolePolicyId", p.getId());
            r.put("consolePolicyName", p.getName());
            r.put("mfaEnabled", Boolean.TRUE.equals(p.getActive()));
            r.put("consolePolicyDescription", p.getDescription());
        } catch (Exception var8) {
            try {
                ListPoliciesResponse lr = dc.listPolicies(ListPoliciesRequest.builder().filter("name eq \"OciConsolePolicy\"").count(1).build());
                List<Policy> items = lr.getPolicies().getResources();
                if (items != null && !items.isEmpty()) {
                    Policy px = items.get(0);
                    r.put("consolePolicyId", px.getId());
                    r.put("consolePolicyName", px.getName());
                    r.put("mfaEnabled", Boolean.TRUE.equals(px.getActive()));
                    r.put("consolePolicyDescription", px.getDescription());
                } else {
                    r.put("mfaEnabled", null);
                    r.put("mfaError", "未找到 Security Policy for OCI Console（该租户可能未启用 Identity Domain 新版签名策略）");
                }
            } catch (Exception var7) {
                log.warn("Fallback listPolicies failed: {}", var7.getMessage());
                r.put("mfaEnabled", null);
                r.put("mfaError", var7.getMessage());
            }
        }

        return r;
    }

    private Map<String, Object> fetchDefaultPasswordPolicy(IdentityDomainsClient dc) {
        Map<String, Object> r = new LinkedHashMap<>();

        try {
            ListPasswordPoliciesResponse resp = dc.listPasswordPolicies(
                ListPasswordPoliciesRequest.builder().filter("name eq \"DefaultPasswordPolicy\"").count(1).build()
            );
            List<PasswordPolicy> list = resp.getPasswordPolicies().getResources();
            PasswordPolicy pp = null;
            if (list != null && !list.isEmpty()) {
                pp = list.get(0);
            } else {
                ListPasswordPoliciesResponse any = dc.listPasswordPolicies(
                    ListPasswordPoliciesRequest.builder().sortBy("priority").sortOrder(SortOrder.Ascending).count(1).build()
                );
                List<PasswordPolicy> anyItems = any.getPasswordPolicies().getResources();
                if (anyItems != null && !anyItems.isEmpty()) {
                    pp = anyItems.get(0);
                }
            }

            if (pp != null) {
                r.put("passwordPolicyId", pp.getId());
                r.put("passwordPolicyName", pp.getName());
                r.put("passwordExpiresAfterDays", pp.getPasswordExpiresAfter());
                r.put("passwordPolicyPriority", pp.getPriority());
            }
        } catch (Exception var8) {
            log.warn("fetchDefaultPasswordPolicy failed: {}", var8.getMessage());
            r.put("passwordPolicyError", var8.getMessage());
        }

        return r;
    }

    public void updateMfaSetting(String tenantId, String domainId, boolean enabled) {
        try {
            try (OciClientService client = this.buildClient(tenantId)) {
                List<Map<String, Object>> domains = this.listDomains(client, true);
                Map<String, Object> target = this.findDomain(domains, domainId);
                IdentityDomainsClient dc = this.newDomainClient(client, (String)target.get("url"));

                try {
                    List<Operations> ops = new ArrayList<>();
                    ops.add(Operations.builder().op(Op.Replace).path("active").value(enabled).build());
                    ops.add(
                        Operations.builder()
                            .op(Op.Replace)
                            .path("urn:ietf:params:scim:schemas:oracle:idcs:extension:ociconsolesignonpolicyconsent:Policy:consent")
                            .value(true)
                            .build()
                    );
                    ops.add(
                        Operations.builder()
                            .op(Op.Replace)
                            .path("urn:ietf:params:scim:schemas:oracle:idcs:extension:ociconsolesignonpolicyconsent:Policy:justification")
                            .value(enabled ? "MFA enabled via ocx-worker" : "MFA disabled via ocx-worker")
                            .build()
                    );
                    PatchOp patch = PatchOp.builder().schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp")).operations(ops).build();
                    dc.patchPolicy(PatchPolicyRequest.builder().policyId("OciConsolePolicy").patchOp(patch).build());
                    log.info("OciConsolePolicy active={} for tenant={} domain={}", new Object[]{enabled, tenantId, target.get("displayName")});
                } catch (Throwable var12) {
                    if (dc != null) {
                        try {
                            dc.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                if (dc != null) {
                    dc.close();
                }
            }
        } catch (OciException var14) {
            throw var14;
        } catch (Exception var15) {
            throw new OciException("更新 MFA 策略失败: " + (var15.getMessage() == null ? "未知错误" : var15.getMessage()));
        }
    }

    public void updatePasswordExpiry(String tenantId, String domainId, int days) {
        try {
            try (OciClientService client = this.buildClient(tenantId)) {
                List<Map<String, Object>> domains = this.listDomains(client, true);
                Map<String, Object> target = this.findDomain(domains, domainId);
                IdentityDomainsClient dc = this.newDomainClient(client, (String)target.get("url"));

                try {
                    List<PasswordPolicy> list = dc.listPasswordPolicies(
                            ListPasswordPoliciesRequest.builder().filter("name eq \"DefaultPasswordPolicy\"").count(1).build()
                        )
                        .getPasswordPolicies()
                        .getResources();
                    PasswordPolicy existing = null;
                    if (list != null && !list.isEmpty()) {
                        existing = list.get(0);
                    }

                    if (existing == null) {
                        List<PasswordPolicy> any = dc.listPasswordPolicies(
                                ListPasswordPoliciesRequest.builder().sortBy("priority").sortOrder(SortOrder.Ascending).count(1).build()
                            )
                            .getPasswordPolicies()
                            .getResources();
                        if (any != null && !any.isEmpty()) {
                            existing = any.get(0);
                        }
                    }

                    if (existing == null) {
                        throw new OciException("未找到密码策略（DefaultPasswordPolicy）");
                    }

                    List<Operations> ops = new ArrayList<>();
                    ops.add(Operations.builder().op(Op.Replace).path("passwordExpiresAfter").value(days).build());
                    PatchOp patch = PatchOp.builder().schemas(List.of("urn:ietf:params:scim:api:messages:2.0:PatchOp")).operations(ops).build();
                    dc.patchPasswordPolicy(PatchPasswordPolicyRequest.builder().passwordPolicyId(existing.getId()).patchOp(patch).build());
                    log.info(
                        "passwordExpiresAfter={} days for tenant={} domain={} policy={}",
                        new Object[]{days, tenantId, target.get("displayName"), existing.getName()}
                    );
                } catch (Throwable var14) {
                    if (dc != null) {
                        try {
                            dc.close();
                        } catch (Throwable var13) {
                            var14.addSuppressed(var13);
                        }
                    }

                    throw var14;
                }

                if (dc != null) {
                    dc.close();
                }
            }
        } catch (OciException var16) {
            throw var16;
        } catch (Exception var17) {
            throw new OciException("更新密码策略失败: " + (var17.getMessage() == null ? "未知错误" : var17.getMessage()));
        }
    }

    private Map<String, Object> findDomain(List<Map<String, Object>> domains, String domainId) {
        if (domains != null && !domains.isEmpty()) {
            if (domainId != null && !domainId.isBlank()) {
                for (Map<String, Object> d : domains) {
                    if (domainId.equals(d.get("id"))) {
                        return d;
                    }
                }

                throw new OciException("未找到指定 domain: " + domainId);
            } else {
                for (Map<String, Object> dx : domains) {
                    if ("DEFAULT".equalsIgnoreCase(String.valueOf(dx.get("type")))) {
                        return dx;
                    }
                }

                return domains.get(0);
            }
        } else {
            throw new OciException("未找到 Identity Domain");
        }
    }

    public List<Map<String, Object>> getAuditLogs(String tenantId) {
        return this.getAuditLogs(tenantId, 7);
    }

    public List<Map<String, Object>> getAuditLogs(String tenantId, int days) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            try (OciClientService client = this.buildClient(tenantId)) {
                List<Map<String, Object>> domains = this.listDomains(client, true);
                if (domains.isEmpty()) {
                    throw new OciException("未找到 Identity Domain");
                }

                int window = Math.max(1, Math.min(days, 30));
                Date endTime = new Date();
                Date startTime = Date.from(Instant.now().minus(Duration.ofDays((long)window)));
                List<AuditEvent> events = new ArrayList<>();
                AuditClient auditClient = AuditClient.builder().build(client.getProvider());

                try {
                    String tenancyId = client.getProvider().getTenantId();
                    String page = null;
                    int maxEvents = 12000;

                    do {
                        com.oracle.bmc.audit.requests.ListEventsRequest.Builder reqB = ListEventsRequest.builder()
                            .compartmentId(tenancyId)
                            .startTime(startTime)
                            .endTime(endTime);
                        if (page != null) {
                            reqB.page(page);
                        }

                        ListEventsResponse resp = auditClient.listEvents(reqB.build());
                        if (resp.getItems() != null) {
                            events.addAll(resp.getItems());
                        }

                        page = resp.getOpcNextPage();
                    } while (page != null && !page.isEmpty() && events.size() < maxEvents);

                    log.info("OCI Audit listEvents rawTotal={} windowDays={}", events.size(), window);
                } catch (Throwable var39) {
                    if (auditClient != null) {
                        try {
                            auditClient.close();
                        } catch (Throwable var38) {
                            var39.addSuppressed(var38);
                        }
                    }

                    throw var39;
                }

                if (auditClient != null) {
                    auditClient.close();
                }

                Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();

                for (Map<String, Object> d : domains) {
                    grouped.put((String)d.get("id"), new ArrayList<>());
                }

                List<Map<String, Object>> unknown = new ArrayList<>();
                Map<String, String> nameToId = new HashMap<>();

                for (Map<String, Object> d : domains) {
                    Object n = d.get("displayName");
                    if (n != null) {
                        nameToId.put(String.valueOf(n).trim().toLowerCase(Locale.ROOT), (String)d.get("id"));
                    }
                }

                String fallbackDomainId = resolveLoginLogFallbackDomainId(domains);

                for (AuditEvent ev : events) {
                    String etFull = ev.getEventType();
                    String scmEventId = null;
                    Data data = ev.getData();
                    Map<String, Object> addl = data != null && data.getAdditionalDetails() != null ? castStringObjectMap(data.getAdditionalDetails()) : null;
                    if (addl != null) {
                        Object eid = addl.get("eventId");
                        if (eid != null) {
                            scmEventId = String.valueOf(eid).trim();
                        }
                    }

                    if (matchesLoginAuditEvent(scmEventId, etFull)) {
                        String domainIdFromEvent = resolveLoginLogDomainId(data, nameToId);
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
                                Object an = addl.get("actorName");
                                if (an != null && !String.valueOf(an).isBlank()) {
                                    actorName = String.valueOf(an).trim();
                                }

                                Object a = firstNonBlank(addl, "ssoProtectedResource", "protectedResource");
                                if (a != null) {
                                    ssoProtectedResource = String.valueOf(a);
                                }

                                Object ap = firstNonBlank(addl, "ssoApplicationType", "applicationDisplayName");
                                if (ap != null) {
                                    ssoApp = String.valueOf(ap);
                                }

                                Object ip = addl.get("ssoIdentityProvider");
                                if (ip != null) {
                                    ssoIdp = String.valueOf(ip);
                                }

                                Object f = addl.get("ssoAuthFactor");
                                if (f != null) {
                                    ssoFactor = String.valueOf(f);
                                }

                                if (clientIp == null || clientIp.isBlank()) {
                                    Object xc = firstNonBlank(addl, "clientIp", "ipAddress");
                                    if (xc != null) {
                                        clientIp = String.valueOf(xc).trim();
                                    }
                                }
                            }

                            if (data.getResponse() != null && data.getResponse().getMessage() != null) {
                                msg = data.getResponse().getMessage();
                            }

                            if (msg == null && data.getEventName() != null) {
                                msg = data.getEventName();
                            }
                        }

                        Map<String, Object> row = new LinkedHashMap<>();
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
                            grouped.get(domainIdFromEvent).add(row);
                        } else {
                            unknown.add(row);
                        }
                    }
                }

                if (!unknown.isEmpty()) {
                    String target = fallbackDomainId != null && grouped.containsKey(fallbackDomainId) ? fallbackDomainId : (String)domains.getFirst().get("id");
                    grouped.get(target).addAll(unknown);
                }

                for (Map<String, Object> dx : domains) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("domainId", dx.get("id"));
                    entry.put("displayName", dx.get("displayName"));
                    entry.put("type", dx.get("type"));
                    List<Map<String, Object>> logs = grouped.getOrDefault((String)dx.get("id"), new ArrayList<>());
                    logs.sort((ax, b) -> {
                        String ta = String.valueOf(ax.getOrDefault("eventTime", ""));
                        String tb = String.valueOf(b.getOrDefault("eventTime", ""));
                        return tb.compareTo(ta);
                    });
                    entry.put("logs", logs);
                    result.add(entry);
                }
            }

            return result;
        } catch (OciException var41) {
            throw var41;
        } catch (Exception var42) {
            throw new OciException("获取登录日志失败: " + (var42.getMessage() == null ? "未知错误" : var42.getMessage()));
        }
    }

    private static Map<String, Object> castStringObjectMap(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            LinkedHashMap out = new LinkedHashMap();

            for (Entry<?, ?> e : map.entrySet()) {
                if (e.getKey() != null) {
                    out.put(String.valueOf(e.getKey()), e.getValue());
                }
            }

            return out;
        } else {
            return null;
        }
    }

    private static Object firstNonBlank(Map<String, Object> map, String k1, String k2) {
        Object v1 = map == null ? null : map.get(k1);
        if (v1 != null && String.valueOf(v1).trim().length() > 0) {
            return v1;
        } else {
            Object v2 = map == null ? null : map.get(k2);
            return v2 != null && String.valueOf(v2).trim().length() > 0 ? v2 : null;
        }
    }

    private static boolean matchesLoginAuditScmEventId(String scmEventId) {
        String s = scmEventId.trim().toLowerCase(Locale.ROOT);
        return s.startsWith("sso.session.")
            || "sso.authentication.failure".equals(s)
            || s.startsWith("sso.app.access.")
            || s.startsWith("admin.authentication.")
            || "sso.auth.factor.initiated".equals(s);
    }

    private static boolean matchesLoginAuditByLegacyEventType(String eventTypeFull) {
        if (eventTypeFull == null) {
            return false;
        } else {
            String etl = eventTypeFull.toLowerCase(Locale.ROOT);
            return !etl.contains("identitydomain") && !etl.contains("identitydomains") && !etl.contains("idcs")
                ? false
                : etl.contains("session") || etl.contains("authentication") || etl.contains("appaccess") || etl.contains("signin") || etl.contains("sso");
        }
    }

    private static boolean matchesLoginAuditEvent(String scmEventIdNullable, String eventTypeFull) {
        return scmEventIdNullable != null && !scmEventIdNullable.isBlank()
            ? matchesLoginAuditScmEventId(scmEventIdNullable)
            : matchesLoginAuditByLegacyEventType(eventTypeFull);
    }

    private static String resolveLoginLogDomainId(Data data, Map<String, String> nameToId) {
        if (data == null) {
            return null;
        } else {
            String rid = data.getResourceId();
            if (rid != null && rid.contains("ocid1.domain.")) {
                return rid;
            } else {
                Map<String, Object> addl = castStringObjectMap(data.getAdditionalDetails());
                if (addl == null) {
                    return null;
                } else {
                    Object did = firstNonBlank(addl, "domainOcid", "domainId");
                    String ds = did != null ? String.valueOf(did).trim() : "";
                    if (ds.startsWith("ocid1.domain.")) {
                        return ds;
                    } else {
                        Object dn = addl.get("domainDisplayName");
                        return dn != null && !String.valueOf(dn).isBlank() ? nameToId.get(String.valueOf(dn).trim().toLowerCase(Locale.ROOT)) : null;
                    }
                }
            }
        }
    }

    private static String resolveLoginLogFallbackDomainId(List<Map<String, Object>> domains) {
        if (domains != null && !domains.isEmpty()) {
            for (Map<String, Object> d : domains) {
                if ("DEFAULT".equalsIgnoreCase(String.valueOf(d.get("type")))) {
                    return (String)d.get("id");
                }
            }

            for (Map<String, Object> dx : domains) {
                if ("Default".equals(dx.get("displayName"))) {
                    return (String)dx.get("id");
                }
            }

            return (String)domains.getFirst().get("id");
        } else {
            return null;
        }
    }

    public Map<String, Object> listAuthFactorSettings(String tenantId, String token) {
        this.requireAuthFactorToken(token);
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> domainResults = new ArrayList<>();

        try (OciClientService client = this.buildClient(tenantId)) {
            List<Map<String, Object>> domains = this.listDomains(client, true);
            if (domains.isEmpty()) {
                throw new OciException("未找到 Identity Domain");
            }

            for (Map<String, Object> d : domains) {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("domainId", d.get("id"));
                r.put("displayName", d.get("displayName"));
                r.put("type", d.get("type"));

                try {
                    IdentityDomainsClient dc = this.newDomainClient(client, (String)d.get("url"));

                    try {
                        AuthenticationFactorSetting s = this.firstAuthFactorSetting(dc);
                        r.put("settingId", s.getId());
                        Map<String, Object> factors = new LinkedHashMap<>();
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
                        Map<String, Object> limits = new LinkedHashMap<>();
                        Map<String, Object> trusted = new LinkedHashMap<>();
                        int maxIncorrect = 0;
                        AuthenticationFactorSettingsEndpointRestrictions er = s.getEndpointRestrictions();
                        if (er != null) {
                            limits.put("maxEnrolledDevices", er.getMaxEnrolledDevices());
                            trusted.put("enabled", this.bool(er.getTrustedEndpointsEnabled()));
                            trusted.put("maxTrustedEndpoints", er.getMaxTrustedEndpoints());
                            trusted.put("maxEndpointTrustDurationInDays", er.getMaxEndpointTrustDurationInDays());
                            if (er.getMaxIncorrectAttempts() != null) {
                                maxIncorrect = er.getMaxIncorrectAttempts();
                            }
                        }

                        limits.put("maxIncorrectAttempts", maxIncorrect);
                        r.put("limits", limits);
                        r.put("trustedDevice", trusted);
                    } catch (Throwable var19) {
                        if (dc != null) {
                            try {
                                dc.close();
                            } catch (Throwable var18) {
                                var19.addSuppressed(var18);
                            }
                        }

                        throw var19;
                    }

                    if (dc != null) {
                        dc.close();
                    }
                } catch (Exception var20) {
                    log.warn("list auth factor for domain {} failed: {}", d.get("displayName"), var20.getMessage());
                    r.put("error", var20.getMessage() == null ? "查询失败" : var20.getMessage());
                }

                domainResults.add(r);
            }
        } catch (OciException var22) {
            throw var22;
        } catch (Exception var23) {
            throw new OciException("读取验证因素设置失败: " + (var23.getMessage() == null ? "未知错误" : var23.getMessage()));
        }

        result.put("domains", domainResults);
        return result;
    }

    public Map<String, Object> updateAuthFactorSettings(
        String tenantId,
        String domainId,
        String token,
        Map<String, Object> desiredFactors,
        Map<String, Object> desiredLimits,
        Map<String, Object> desiredTrustedDevice
    ) {
        this.requireAuthFactorToken(token);

        try {
            Object var37;
            try (OciClientService client = this.buildClient(tenantId)) {
                IdentityDomainsClient dc;
                label255: {
                    List<Map<String, Object>> domains = this.listDomains(client, true);
                    Map<String, Object> target = this.findDomain(domains, domainId);
                    dc = this.newDomainClient(client, (String)target.get("url"));

                    try {
                        AuthenticationFactorSetting current = this.firstAuthFactorSetting(dc);
                        com.oracle.bmc.identitydomains.model.AuthenticationFactorSetting.Builder b = current.toBuilder();
                        int changed = 0;
                        if (desiredFactors != null) {
                            for (String key : FACTOR_PATH.keySet()) {
                                if (desiredFactors.containsKey(key)) {
                                    boolean want = Boolean.TRUE.equals(desiredFactors.get(key));
                                    boolean now = this.currentFactorValue(current, key);
                                    if (want != now) {
                                        changed++;
                                        switch (key) {
                                            case "totp":
                                                b.totpEnabled(want);
                                                break;
                                            case "push":
                                                b.pushEnabled(want);
                                                break;
                                            case "sms":
                                                b.smsEnabled(want);
                                                break;
                                            case "phoneCall":
                                                b.phoneCallEnabled(want);
                                                break;
                                            case "email":
                                                b.emailEnabled(want);
                                                break;
                                            case "securityQuestions":
                                                b.securityQuestionsEnabled(want);
                                                break;
                                            case "fido":
                                                b.fidoAuthenticatorEnabled(want);
                                                break;
                                            case "yubico":
                                                b.yubicoOtpEnabled(want);
                                                break;
                                            case "bypassCode":
                                                b.bypassCodeEnabled(want);
                                                break;
                                            case "duoSecurity":
                                                com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsThirdPartyFactor.Builder tpfBase = current.getThirdPartyFactor()
                                                        != null
                                                    ? current.getThirdPartyFactor().toBuilder()
                                                    : AuthenticationFactorSettingsThirdPartyFactor.builder();
                                                b.thirdPartyFactor(tpfBase.duoSecurity(want).build());
                                        }
                                    }
                                }
                            }
                        }

                        AuthenticationFactorSettingsEndpointRestrictions er = current.getEndpointRestrictions();
                        com.oracle.bmc.identitydomains.model.AuthenticationFactorSettingsEndpointRestrictions.Builder erBuilder = er != null
                            ? er.toBuilder()
                            : AuthenticationFactorSettingsEndpointRestrictions.builder();
                        boolean erChanged = false;
                        if (desiredLimits != null) {
                            Integer want = this.asInt(desiredLimits.get("maxEnrolledDevices"));
                            Integer now = er == null ? null : er.getMaxEnrolledDevices();
                            if (want != null && !Objects.equals(want, now)) {
                                erBuilder.maxEnrolledDevices(want);
                                erChanged = true;
                            }

                            Integer wantInc = this.asInt(desiredLimits.get("maxIncorrectAttempts"));
                            Integer nowInc = er == null ? null : er.getMaxIncorrectAttempts();
                            if (wantInc != null && !Objects.equals(wantInc, nowInc)) {
                                erBuilder.maxIncorrectAttempts(wantInc);
                                erChanged = true;
                            }
                        }

                        if (desiredTrustedDevice != null) {
                            if (desiredTrustedDevice.containsKey("enabled")) {
                                boolean wantx = Boolean.TRUE.equals(desiredTrustedDevice.get("enabled"));
                                boolean nowx = er != null && Boolean.TRUE.equals(er.getTrustedEndpointsEnabled());
                                if (wantx != nowx) {
                                    erBuilder.trustedEndpointsEnabled(wantx);
                                    erChanged = true;
                                }
                            }

                            Integer wantMax = this.asInt(desiredTrustedDevice.get("maxTrustedEndpoints"));
                            Integer nowMax = er == null ? null : er.getMaxTrustedEndpoints();
                            if (wantMax != null && !Objects.equals(wantMax, nowMax)) {
                                erBuilder.maxTrustedEndpoints(wantMax);
                                erChanged = true;
                            }

                            Integer wantDays = this.asInt(desiredTrustedDevice.get("maxEndpointTrustDurationInDays"));
                            Integer nowDays = er == null ? null : er.getMaxEndpointTrustDurationInDays();
                            if (wantDays != null && !Objects.equals(wantDays, nowDays)) {
                                erBuilder.maxEndpointTrustDurationInDays(wantDays);
                                erChanged = true;
                            }
                        }

                        if (erChanged) {
                            b.endpointRestrictions(erBuilder.build());
                            changed++;
                        }

                        Map<String, Object> resp = new LinkedHashMap<>();
                        resp.put("domainId", target.get("id"));
                        resp.put("displayName", target.get("displayName"));
                        resp.put("changedOps", changed);
                        if (changed != 0) {
                            dc.putAuthenticationFactorSetting(
                                PutAuthenticationFactorSettingRequest.builder()
                                    .authenticationFactorSettingId(current.getId())
                                    .authenticationFactorSetting(b.build())
                                    .build()
                            );
                            log.info("AuthFactorSetting put: tenant={} domain={} changedGroups={}", new Object[]{tenantId, target.get("displayName"), changed});
                            var37 = resp;
                            break label255;
                        }

                        resp.put("skipped", true);
                        var37 = resp;
                    } catch (Throwable var23) {
                        if (dc != null) {
                            try {
                                dc.close();
                            } catch (Throwable var22) {
                                var23.addSuppressed(var22);
                            }
                        }

                        throw var23;
                    }

                    if (dc != null) {
                        dc.close();
                    }

                    return (Map<String, Object>)var37;
                }

                if (dc != null) {
                    dc.close();
                }
            }

            return (Map<String, Object>)var37;
        } catch (OciException var25) {
            throw var25;
        } catch (Exception var26) {
            throw new OciException("更新验证因素设置失败: " + (var26.getMessage() == null ? "未知错误" : var26.getMessage()));
        }
    }

    private boolean currentFactorValue(AuthenticationFactorSetting s, String key) {
        switch (key) {
            case "totp":
                return Boolean.TRUE.equals(s.getTotpEnabled());
            case "push":
                return Boolean.TRUE.equals(s.getPushEnabled());
            case "sms":
                return Boolean.TRUE.equals(s.getSmsEnabled());
            case "phoneCall":
                return Boolean.TRUE.equals(s.getPhoneCallEnabled());
            case "email":
                return Boolean.TRUE.equals(s.getEmailEnabled());
            case "securityQuestions":
                return Boolean.TRUE.equals(s.getSecurityQuestionsEnabled());
            case "fido":
                return Boolean.TRUE.equals(s.getFidoAuthenticatorEnabled());
            case "yubico":
                return Boolean.TRUE.equals(s.getYubicoOtpEnabled());
            case "bypassCode":
                return Boolean.TRUE.equals(s.getBypassCodeEnabled());
            case "duoSecurity":
                return s.getThirdPartyFactor() != null && Boolean.TRUE.equals(s.getThirdPartyFactor().getDuoSecurity());
            default:
                return false;
        }
    }

    private AuthenticationFactorSetting firstAuthFactorSetting(IdentityDomainsClient dc) {
        ListAuthenticationFactorSettingsResponse resp = dc.listAuthenticationFactorSettings(ListAuthenticationFactorSettingsRequest.builder().build());
        List<AuthenticationFactorSetting> items = resp.getAuthenticationFactorSettings() == null ? null : resp.getAuthenticationFactorSettings().getResources();
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        } else {
            throw new OciException("未找到 AuthenticationFactorSetting");
        }
    }

    private boolean bool(Boolean b) {
        return Boolean.TRUE.equals(b);
    }

    private Integer asInt(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Number n) {
            return n.intValue();
        } else {
            try {
                return Integer.parseInt(String.valueOf(v));
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public List<Map<String, Object>> getServiceQuotas(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            List<Map<String, Object>> quotaList = new ArrayList<>();

            try {
                try (OciClientService client = this.buildClient(tenantId)) {
                    LimitsClient limitsClient = LimitsClient.builder().build(client.getProvider());

                    try {
                        ListServicesResponse servicesResp = limitsClient.listServices(
                            ListServicesRequest.builder().compartmentId(user.getOciTenantId()).build()
                        );
                        List<String> targetServices = Arrays.asList(
                            "compute",
                            "vcn",
                            "block-storage",
                            "load-balancer",
                            "network-load-balancer",
                            "identity",
                            "regions",
                            "database",
                            "objectstorage",
                            "file-storage",
                            "container-engine",
                            "generative-ai",
                            "data-science",
                            "dns"
                        );

                        for (ServiceSummary svc : servicesResp.getItems()) {
                            String svcName = svc.getName();
                            if (targetServices.contains(svcName)) {
                                String nextPage = null;

                                while (true) {
                                    com.oracle.bmc.limits.requests.ListLimitValuesRequest.Builder reqBuilder = ListLimitValuesRequest.builder()
                                        .compartmentId(user.getOciTenantId())
                                        .serviceName(svcName);
                                    if (nextPage != null) {
                                        reqBuilder.page(nextPage);
                                    }

                                    ListLimitValuesResponse limitsResp = limitsClient.listLimitValues(reqBuilder.build());

                                    for (LimitValueSummary lv : limitsResp.getItems()) {
                                        if (lv.getValue() != null && lv.getValue() != 0L) {
                                            Map<String, Object> entry = new LinkedHashMap<>();
                                            entry.put("serviceName", svcName);
                                            entry.put("limitName", lv.getName());
                                            entry.put("availabilityDomain", lv.getAvailabilityDomain());
                                            entry.put("limit", lv.getValue());

                                            try {
                                                GetResourceAvailabilityResponse usageResp = limitsClient.getResourceAvailability(
                                                    GetResourceAvailabilityRequest.builder()
                                                        .compartmentId(user.getOciTenantId())
                                                        .serviceName(svcName)
                                                        .limitName(lv.getName())
                                                        .availabilityDomain(lv.getAvailabilityDomain())
                                                        .build()
                                                );
                                                entry.put("used", usageResp.getResourceAvailability().getUsed());
                                                entry.put("available", usageResp.getResourceAvailability().getAvailable());
                                            } catch (Exception var26) {
                                                entry.put("used", null);
                                                entry.put("available", null);
                                            }

                                            quotaList.add(entry);
                                        }
                                    }

                                    nextPage = limitsResp.getOpcNextPage();
                                    if (nextPage == null) {
                                        break;
                                    }
                                }
                            }
                        }
                    } finally {
                        limitsClient.close();
                    }
                }

                return quotaList;
            } catch (OciException var29) {
                throw var29;
            } catch (Exception var30) {
                throw new OciException("获取配额信息失败: " + (var30.getMessage() == null ? "未知错误" : var30.getMessage()));
            }
        }
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
