/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.metadata.IPage
 *  com.baomidou.mybatisplus.extension.plugins.pagination.Page
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.ocxworker.enums.TaskStatusEnum
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciCreateTaskMapper
 *  com.ocxworker.mapper.OciKvMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciCreateTask
 *  com.ocxworker.model.entity.OciKv
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.model.params.IdListParams
 *  com.ocxworker.model.params.PageParams
 *  com.ocxworker.model.params.TenantBatchMoveGroupParams
 *  com.ocxworker.model.params.TenantParams
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.OciProxyConfigService
 *  com.ocxworker.service.OrganizationSubscriptionService
 *  com.ocxworker.service.OspSubscriptionEnricher
 *  com.ocxworker.service.TenantService
 *  com.ocxworker.service.UsageCostService
 *  com.ocxworker.util.CommonUtils
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.IdentityClient$Builder
 *  com.oracle.bmc.identity.model.RegionSubscription
 *  com.oracle.bmc.identity.model.Tenancy
 *  com.oracle.bmc.identity.requests.GetTenancyRequest
 *  com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest
 *  com.oracle.bmc.ospgateway.InvoiceServiceClient
 *  com.oracle.bmc.ospgateway.SubscriptionServiceClient
 *  com.oracle.bmc.ospgateway.SubscriptionServiceClient$Builder
 *  com.oracle.bmc.ospgateway.model.SubscriptionSummary
 *  com.oracle.bmc.ospgateway.requests.ListInvoicesRequest
 *  com.oracle.bmc.ospgateway.requests.ListSubscriptionsRequest
 *  com.oracle.bmc.ospgateway.responses.ListInvoicesResponse
 *  com.oracle.bmc.ospgateway.responses.ListSubscriptionsResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.ocxworker.enums.TaskStatusEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciCreateTaskMapper;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciCreateTask;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.model.params.IdListParams;
import com.ocxworker.model.params.PageParams;
import com.ocxworker.model.params.TenantBatchMoveGroupParams;
import com.ocxworker.model.params.TenantParams;
import com.ocxworker.service.OciClientService;
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.service.OrganizationSubscriptionService;
import com.ocxworker.service.OspSubscriptionEnricher;
import com.ocxworker.service.UsageCostService;
import com.ocxworker.util.CommonUtils;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.RegionSubscription;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import com.oracle.bmc.ospgateway.InvoiceServiceClient;
import com.oracle.bmc.ospgateway.SubscriptionServiceClient;
import com.oracle.bmc.ospgateway.model.SubscriptionSummary;
import com.oracle.bmc.ospgateway.requests.ListInvoicesRequest;
import com.oracle.bmc.ospgateway.requests.ListSubscriptionsRequest;
import com.oracle.bmc.ospgateway.responses.ListInvoicesResponse;
import com.oracle.bmc.ospgateway.responses.ListSubscriptionsResponse;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class TenantService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TenantService.class);
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private OciKvMapper kvMapper;
    @Resource
    private UsageCostService usageCostService;
    @Resource
    private OrganizationSubscriptionService organizationSubscriptionService;
    private static final Set<String> TENANT_ACCOUNT_INFO_KEYS = Set.of("tenantName", "homeRegionKey", "tenantId", "description", "subscribedRegions", "planType", "planTypeLabel", "paymentMethod", "paymentMethodLabel", "subscriptionUsage", "accountType", "upgradeState", "upgradeStateLabel", "subscriptionStatus", "subscriptionStatusLabel", "currencyCode", "isIntentToPay", "subscriptionStartTime", "registrationLocation", "subscriptionPlanNumber", "subscriptionOrgOcid");
    private static final ExecutorService TENANT_ACCOUNT_EXECUTOR = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "tenant-account");
        t.setDaemon(true);
        return t;
    });
    private static final String GROUP_TYPE = "group";
    private static final String GROUP_L1_PREFIX = "group_l1:";
    private static final String GROUP_L2_PREFIX = "group_l2:";
    private static final String GROUP_ORDER_CODE = "group_order_l1";
    @Value(value="${oci-cfg.key-dir-path}")
    private String keyDirPath;

    public Page<Map<String, Object>> list(PageParams params) {
        int pageSize = params.getSize();
        if (pageSize < 1) {
            pageSize = 10;
        } else if (pageSize > 500) {
            pageSize = 500;
        }
        Page page = new Page((long)params.getCurrent(), (long)pageSize);
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper();
        if (StrUtil.isNotBlank((CharSequence)params.getKeyword())) {
            wrapper.and(w -> ((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)w.like(OciUser::getUsername, (Object)params.getKeyword())).or()).like(OciUser::getTenantName, (Object)params.getKeyword())).or()).like(OciUser::getOciRegion, (Object)params.getKeyword()));
        }
        wrapper.orderByDesc(OciUser::getCreateTime);
        Page result = (Page)this.userMapper.selectPage((IPage)page, (Wrapper)wrapper);
        Page enriched = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        enriched.setRecords(result.getRecords().stream().map(u -> {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("tenantName", u.getTenantName());
            map.put("ociTenantId", u.getOciTenantId());
            map.put("ociUserId", u.getOciUserId());
            map.put("ociFingerprint", u.getOciFingerprint());
            map.put("ociRegion", u.getOciRegion());
            map.put("ociKeyPath", u.getOciKeyPath());
            map.put("planType", u.getPlanType());
            map.put("groupLevel1", u.getGroupLevel1());
            map.put("groupLevel2", u.getGroupLevel2());
            map.put("createTime", u.getCreateTime());
            long running = this.taskMapper.selectCount((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciCreateTask::getUserId, (Object)u.getId())).eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus()));
            map.put("taskStatus", running > 0L ? "\u6267\u884c\u5f00\u673a\u4efb\u52a1\u4e2d" : "\u65e0\u5f00\u673a\u4efb\u52a1");
            map.put("hasRunningTask", running > 0L);
            return map;
        }).toList());
        return enriched;
    }

    public void add(TenantParams params) {
        long duplicateCount = this.userMapper.selectCount((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciUser::getOciTenantId, (Object)params.getOciTenantId())).eq(OciUser::getOciUserId, (Object)params.getOciUserId())).eq(OciUser::getOciRegion, (Object)params.getOciRegion()));
        if (duplicateCount > 0L) {
            throw new OciException("\u8be5\u79df\u6237\u914d\u7f6e\u5df2\u5b58\u5728\uff08\u76f8\u540c Tenant ID + User ID + Region\uff09\uff0c\u8bf7\u52ff\u91cd\u590d\u6dfb\u52a0");
        }
        long nameCount = this.userMapper.selectCount((Wrapper)new LambdaQueryWrapper().eq(OciUser::getUsername, (Object)params.getUsername()));
        if (nameCount > 0L) {
            throw new OciException("\u540d\u79f0\u300c" + params.getUsername() + "\u300d\u5df2\u88ab\u4f7f\u7528\uff0c\u8bf7\u66f4\u6362\u540d\u79f0");
        }
        this.validateOciCredentials(params);
        OciUser user = new OciUser();
        user.setId(CommonUtils.generateId());
        user.setUsername(params.getUsername());
        user.setOciTenantId(params.getOciTenantId());
        user.setOciUserId(params.getOciUserId());
        user.setOciFingerprint(params.getOciFingerprint());
        user.setOciRegion(params.getOciRegion());
        user.setOciKeyPath(params.getOciKeyPath());
        user.setGroupLevel1(StrUtil.isBlank((CharSequence)params.getGroupLevel1()) ? "\u672a\u5206\u7ec4" : params.getGroupLevel1());
        user.setGroupLevel2(StrUtil.isBlank((CharSequence)params.getGroupLevel2()) ? null : params.getGroupLevel2());
        user.setCreateTime(LocalDateTime.now());
        this.userMapper.insert((Object)user);
        log.info("Added tenant config: {}", (Object)params.getUsername());
        Thread.ofVirtual().start(() -> this.fetchTenantInfo(user));
    }

    private void validateOciCredentials(TenantParams params) {
        SysUserDTO dto = SysUserDTO.builder().username(params.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(params.getOciTenantId()).userId(params.getOciUserId()).fingerprint(params.getOciFingerprint()).region(params.getOciRegion()).privateKeyPath(params.getOciKeyPath()).build()).build();
        try (OciClientService client = new OciClientService(dto);){
            client.getIdentityClient().getTenancy(GetTenancyRequest.builder().tenancyId(params.getOciTenantId()).build());
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("NotAuthenticated")) {
                throw new OciException("API \u914d\u7f6e\u9a8c\u8bc1\u5931\u8d25\uff1a\u8ba4\u8bc1\u4e0d\u901a\u8fc7\uff0c\u8bf7\u68c0\u67e5 Tenant ID\u3001User ID\u3001Fingerprint \u548c\u5bc6\u94a5\u6587\u4ef6");
            }
            if (msg != null && msg.contains("not found")) {
                throw new OciException("API \u914d\u7f6e\u9a8c\u8bc1\u5931\u8d25\uff1aTenant ID \u4e0d\u5b58\u5728");
            }
            if (e instanceof IOException || msg != null && msg.contains("key")) {
                throw new OciException("API \u914d\u7f6e\u9a8c\u8bc1\u5931\u8d25\uff1a\u5bc6\u94a5\u6587\u4ef6\u65e0\u6548\u6216\u4e0d\u5b58\u5728");
            }
            throw new OciException("API \u914d\u7f6e\u9a8c\u8bc1\u5931\u8d25\uff1a" + (msg != null ? msg.substring(0, Math.min(msg.length(), 120)) : "\u672a\u77e5\u9519\u8bef"));
        }
    }

    public void update(TenantParams params) {
        if (StrUtil.isBlank((CharSequence)params.getId())) {
            throw new OciException("ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)params.getId()));
        if (user == null) {
            throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        user.setUsername(params.getUsername());
        user.setOciTenantId(params.getOciTenantId());
        user.setOciUserId(params.getOciUserId());
        user.setOciFingerprint(params.getOciFingerprint());
        user.setOciRegion(params.getOciRegion());
        if (StrUtil.isNotBlank((CharSequence)params.getOciKeyPath())) {
            user.setOciKeyPath(params.getOciKeyPath());
        }
        user.setGroupLevel1(StrUtil.isBlank((CharSequence)params.getGroupLevel1()) ? null : params.getGroupLevel1());
        user.setGroupLevel2(StrUtil.isBlank((CharSequence)params.getGroupLevel2()) ? null : params.getGroupLevel2());
        this.userMapper.updateById((Object)user);
        log.info("Updated tenant config: {}", (Object)params.getUsername());
    }

    public void remove(IdListParams params) {
        this.userMapper.deleteByIds((Collection)params.getIdList());
        log.info("Removed tenant configs: {}", (Object)params.getIdList());
    }

    @Transactional(rollbackFor={Exception.class})
    public void batchMoveGroup(TenantBatchMoveGroupParams params) {
        String l1 = params.getGroupLevel1().trim();
        String l2 = null;
        if (!"\u672a\u5206\u7ec4".equals(l1) && StrUtil.isNotBlank((CharSequence)params.getGroupLevel2())) {
            l2 = params.getGroupLevel2().trim();
        }
        for (String id : params.getIdList()) {
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)id));
            if (user == null) {
                throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728: " + id);
            }
            user.setGroupLevel1(l1);
            user.setGroupLevel2(l2);
            this.userMapper.updateById((Object)user);
        }
        log.info("Batch moved {} tenants to group {}/{}", new Object[]{params.getIdList().size(), l1, l2});
    }

    public OciUser getById(String id) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)id));
        if (user == null) {
            throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return user;
    }

    public void refreshPlanType(String id) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)id));
        if (user == null) {
            throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        this.fetchTenantInfo(user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fetchTenantInfo(OciUser user) {
        try {
            SysUserDTO dto = SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build();
            try (OciClientService client = new OciClientService(dto);){
                try {
                    Tenancy tenancy = client.getIdentityClient().getTenancy(GetTenancyRequest.builder().tenancyId(user.getOciTenantId()).build()).getTenancy();
                    if (tenancy != null && StrUtil.isNotBlank((CharSequence)tenancy.getName())) {
                        user.setTenantName(tenancy.getName());
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to fetch tenantName for {}: {}", (Object)user.getUsername(), (Object)e.getMessage());
                }
                SubscriptionServiceClient.Builder ospB = SubscriptionServiceClient.builder();
                OciProxyConfigService pxy = OciProxyConfigService.instance();
                if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
                    ospB = (SubscriptionServiceClient.Builder)ospB.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
                }
                try (SubscriptionServiceClient ospClient = ospB.build((AbstractAuthenticationDetailsProvider)client.getProvider());){
                    ListSubscriptionsResponse resp = ospClient.listSubscriptions(ListSubscriptionsRequest.builder().ospHomeRegion(user.getOciRegion()).compartmentId(client.getCompartmentId()).build());
                    List items = resp.getSubscriptionCollection().getItems();
                    if (items != null && !items.isEmpty()) {
                        String planType = ((SubscriptionSummary)items.get(0)).getPlanType() != null ? ((SubscriptionSummary)items.get(0)).getPlanType().getValue() : "UNKNOWN";
                        user.setPlanType(planType);
                    }
                }
                this.userMapper.updateById((Object)user);
            }
        }
        catch (Exception e) {
            log.warn("Failed to fetch tenant info for {}: {}", (Object)user.getUsername(), (Object)e.getMessage());
        }
    }

    public Map<String, Object> getTenantFullInfo(String id) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)id));
        if (user == null) {
            throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("configName", user.getUsername());
        result.put("id", user.getId());
        SysUserDTO dto = SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build();
        try (OciClientService client = new OciClientService(dto);){
            String planVal;
            String savedTenantName = user.getTenantName();
            String savedPlanType = user.getPlanType();
            SimpleAuthenticationDetailsProvider provider = client.getProvider();
            IdentityClient identityClient = client.getIdentityClient();
            String tenancyId = user.getOciTenantId();
            String fallbackRegion = user.getOciRegion();
            String compartmentId = client.getCompartmentId();
            String ospHomeRegion = TenantService.resolveOspHomeRegion((IdentityClient)identityClient, (String)tenancyId, (String)fallbackRegion);
            String usageRegion = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)identityClient, (String)tenancyId, (String)fallbackRegion);
            CompletableFuture<Void> identityFut = CompletableFuture.runAsync(() -> this.applyIdentityAccountFields(provider, tenancyId, user, result), TENANT_ACCOUNT_EXECUTOR);
            CompletableFuture<List> assignedFut = CompletableFuture.supplyAsync(() -> this.organizationSubscriptionService.listAssignedSubscriptionsOnly(client, tenancyId, usageRegion), TENANT_ACCOUNT_EXECUTOR);
            CompletableFuture<Void> ospFut = CompletableFuture.runAsync(() -> TenantService.applyOspAccountFields((SimpleAuthenticationDetailsProvider)provider, (String)ospHomeRegion, (String)compartmentId, (Map)result), TENANT_ACCOUNT_EXECUTOR);
            try {
                CompletableFuture.allOf(identityFut, assignedFut, ospFut).get(90L, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                log.warn("Tenant account parallel fetch timeout or error: {}", (Object)e.getMessage());
            }
            List assignedRows = assignedFut.getNow(List.of());
            LinkedHashMap orgSub = new LinkedHashMap();
            orgSub.put("assignedSubscriptions", assignedRows);
            TenantService.enrichSubscriptionStatusFromAssigned(result, orgSub);
            String ospRef = result.get("subscriptionOspRef") == null ? null : String.valueOf(result.get("subscriptionOspRef")).trim();
            String orgOcid = TenantService.resolveOrganizationSubscriptionOcid((String)ospRef, orgSub);
            if (StrUtil.isNotBlank((CharSequence)orgOcid)) {
                result.put("subscriptionOrgOcid", orgOcid);
                String usageStart = result.get("subscriptionStartTime") == null ? null : String.valueOf(result.get("subscriptionStartTime"));
                try {
                    Map subUsage = this.usageCostService.fetchSubscriptionUsageCost(client, tenancyId, List.of(orgOcid), usageStart, fallbackRegion);
                    result.put("subscriptionUsage", TenantService.slimSubscriptionUsageForAccount((Map)subUsage));
                }
                catch (Exception ex) {
                    log.warn("Failed to get subscription usage cost: {}", (Object)ex.getMessage());
                }
            }
            String string = planVal = result.get("planType") == null ? null : String.valueOf(result.get("planType"));
            if (StrUtil.isNotBlank((CharSequence)planVal) && !Objects.equals(planVal, savedPlanType)) {
                user.setPlanType(planVal);
            }
            if (!Objects.equals(savedTenantName, user.getTenantName()) || !Objects.equals(savedPlanType, user.getPlanType())) {
                this.userMapper.updateById((Object)user);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u83b7\u53d6\u79df\u6237\u8be6\u60c5\u5931\u8d25: " + e.getMessage());
        }
        TenantService.pruneTenantAccountInfo(result);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> getTenantBillingSummary(String id, Object limitsRaw) {
        Object e;
        Map<String, Object> usage;
        if (StrUtil.isBlank((CharSequence)id)) {
            throw new OciException("ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)id));
        if (user == null) {
            throw new OciException("\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        HashMap<String, Integer> limits = new HashMap<String, Integer>();
        limits.put("invoices", 5);
        limits.put("payments", 5);
        limits.put("usageStatements", 3);
        limits.put("costDays", 30);
        if (limitsRaw instanceof Map) {
            Number n;
            Map m = (Map)limitsRaw;
            Object inv = m.get("invoices");
            Object pay = m.get("payments");
            Object us = m.get("usageStatements");
            Object costDays = m.get("costDays");
            if (inv instanceof Number) {
                n = (Number)inv;
                limits.put("invoices", Math.max(1, Math.min(50, n.intValue())));
            }
            if (pay instanceof Number) {
                n = (Number)pay;
                limits.put("payments", Math.max(1, Math.min(50, n.intValue())));
            }
            if (us instanceof Number) {
                n = (Number)us;
                limits.put("usageStatements", Math.max(1, Math.min(50, n.intValue())));
            }
            if (costDays instanceof Number) {
                n = (Number)costDays;
                limits.put("costDays", Math.max(1, Math.min(90, n.intValue())));
            }
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", user.getId());
        result.put("configName", user.getUsername());
        result.put("ociRegion", user.getOciRegion());
        LinkedHashMap<String, CallSite> links = new LinkedHashMap<String, CallSite>();
        links.put("billingOverview", (CallSite)((Object)("https://cloud.oracle.com/billing/overview?region=" + user.getOciRegion())));
        links.put("costAnalysis", (CallSite)((Object)("https://cloud.oracle.com/billing/cost-analysis?region=" + user.getOciRegion())));
        links.put("invoices", (CallSite)((Object)("https://cloud.oracle.com/billing/invoices?region=" + user.getOciRegion())));
        links.put("paymentHistory", (CallSite)((Object)("https://cloud.oracle.com/billing/payments?region=" + user.getOciRegion())));
        links.put("upgradeAndPayment", (CallSite)((Object)("https://cloud.oracle.com/billing/account?region=" + user.getOciRegion())));
        result.put("links", links);
        LinkedHashMap<String, Object> invoices = new LinkedHashMap<String, Object>();
        invoices.put("available", Boolean.TRUE);
        invoices.put("items", new ArrayList());
        result.put("invoices", invoices);
        LinkedHashMap<String, Object> payments = new LinkedHashMap<String, Object>();
        payments.put("available", Boolean.FALSE);
        payments.put("reason", "\u6682\u672a\u63a5\u5165\u4ed8\u6b3e\u5386\u53f2 API\uff08\u4e0d\u540c\u8d26\u53f7\u5f62\u6001\u53ef\u7528\u6027\u4e0d\u4e00\u81f4\uff09\uff0c\u8bf7\u4f7f\u7528\u63a7\u5236\u53f0\u67e5\u770b");
        payments.put("items", new ArrayList());
        result.put("payments", payments);
        try {
            usage = this.usageCostService.fetchCostAnalysis(id, ((Integer)limits.get("costDays")).intValue());
        }
        catch (Exception e2) {
            usage = new LinkedHashMap<String, Boolean>();
            usage.put("available", Boolean.FALSE);
            usage.put("reason", e2.getMessage() == null ? "\u6210\u672c\u5206\u6790\u67e5\u8be2\u5931\u8d25" : e2.getMessage());
            usage.put("summary", null);
            usage.put("byService", new ArrayList());
            usage.put("byDay", new ArrayList());
        }
        result.put("usage", usage);
        SysUserDTO dto = SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build();
        try (OciClientService client = new OciClientService(dto);){
            String ospHomeRegion = TenantService.resolveOspHomeRegion((IdentityClient)client.getIdentityClient(), (String)user.getOciTenantId(), (String)user.getOciRegion());
            try (InvoiceServiceClient invoiceClient = InvoiceServiceClient.builder().build((AbstractAuthenticationDetailsProvider)client.getProvider());){
                ListInvoicesResponse resp = invoiceClient.listInvoices(ListInvoicesRequest.builder().ospHomeRegion(ospHomeRegion).compartmentId(client.getCompartmentId()).limit((Integer)limits.get("invoices")).build());
                ArrayList items = new ArrayList();
                Object col = null;
                try {
                    col = resp.getClass().getMethod("getInvoiceSummaryCollection", new Class[0]).invoke((Object)resp, new Object[0]);
                }
                catch (Exception ignored) {
                    try {
                        col = resp.getClass().getMethod("getInvoiceCollection", new Class[0]).invoke((Object)resp, new Object[0]);
                    }
                    catch (Exception ignored2) {
                        col = null;
                    }
                }
                List summaries = null;
                if (col != null) {
                    try {
                        Object rawItems = col.getClass().getMethod("getItems", new Class[0]).invoke(col, new Object[0]);
                        if (rawItems instanceof List) {
                            List list;
                            summaries = list = (List)rawItems;
                        }
                    }
                    catch (Exception ignored) {
                        summaries = null;
                    }
                }
                if (summaries != null) {
                    for (Object inv : summaries) {
                        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
                        row.put("invoiceId", TenantService.tryInvoke(inv, (String)"getInternalInvoiceId"));
                        row.put("invoiceNo", TenantService.tryInvoke(inv, (String)"getInvoiceNo"));
                        row.put("refNo", TenantService.tryInvoke(inv, (String)"getRefNo"));
                        row.put("status", TenantService.tryEnumValue((Object)TenantService.tryInvoke(inv, (String)"getStatus")));
                        row.put("type", TenantService.tryEnumValue((Object)TenantService.tryInvoke(inv, (String)"getType")));
                        row.put("invoiceDate", TenantService.tryToString((Object)TenantService.tryInvoke(inv, (String)"getInvoiceDate")));
                        row.put("dueDate", TenantService.tryToString((Object)TenantService.tryInvoke(inv, (String)"getDueDate")));
                        row.put("totalAmount", TenantService.tryInvoke(inv, (String)"getTotalAmount"));
                        row.put("currencyCode", TenantService.tryInvoke(inv, (String)"getCurrencyCode"));
                        items.add(row);
                    }
                }
                items.sort((a, b) -> {
                    String da = String.valueOf(a.getOrDefault("invoiceDate", ""));
                    String db = String.valueOf(b.getOrDefault("invoiceDate", ""));
                    return db.compareTo(da);
                });
                invoices.put("items", items);
            }
        }
        catch (Exception e3) {
            invoices.put("available", Boolean.FALSE);
            invoices.put("reason", "\u521d\u59cb\u5316\u8d26\u52a1\u5ba2\u6237\u7aef\u5931\u8d25\uff1a" + (e3.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e3.getMessage()));
        }
        LinkedHashMap<String, Serializable> summary = new LinkedHashMap<String, Serializable>();
        summary.put("invoiceCount", Integer.valueOf(invoices.getOrDefault("items", List.of()).size()));
        LinkedHashMap latestInvoice = null;
        List invItems = invoices.getOrDefault("items", List.of());
        if (!invItems.isEmpty() && (e = invItems.get(0)) instanceof Map) {
            Map m = (Map)e;
            latestInvoice = new LinkedHashMap();
            latestInvoice.put("invoiceNo", m.get("invoiceNo"));
            latestInvoice.put("status", m.get("status"));
            latestInvoice.put("totalAmount", m.get("totalAmount"));
            latestInvoice.put("currencyCode", m.get("currencyCode"));
            latestInvoice.put("dueDate", m.get("dueDate"));
        }
        summary.put("latestInvoice", latestInvoice);
        result.put("summary", summary);
        return result;
    }

    private static void pruneTenantAccountInfo(Map<String, Object> result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        result.keySet().removeIf(k -> !TENANT_ACCOUNT_INFO_KEYS.contains(k));
    }

    private static Map<String, Object> slimSubscriptionUsageForAccount(Map<String, Object> raw) {
        if (raw == null || !Boolean.TRUE.equals(raw.get("available"))) {
            return null;
        }
        LinkedHashMap<String, Object> slim = new LinkedHashMap<String, Object>();
        slim.put("timeUsageStarted", raw.get("timeUsageStarted"));
        Object summary = raw.get("summary");
        if (summary instanceof Map) {
            Map s = (Map)summary;
            LinkedHashMap ss = new LinkedHashMap();
            ss.put("totalConsumed", s.get("totalConsumed"));
            ss.put("totalConsumedLabel", s.get("totalConsumedLabel"));
            slim.put("summary", ss);
        }
        return slim;
    }

    private static String resolveOrganizationSubscriptionOcid(String ospRef, Map<String, Object> orgSub) {
        List ids = TenantService.resolveOrganizationSubscriptionOcids((String)ospRef, orgSub);
        return ids.isEmpty() ? null : (String)ids.get(0);
    }

    private static List<String> resolveOrganizationSubscriptionOcids(String ospRef, Map<String, Object> orgSub) {
        LinkedHashSet<String> ids = new LinkedHashSet<String>();
        if (orgSub == null) {
            return List.of();
        }
        Object assigned = orgSub.get("assignedSubscriptions");
        if (assigned instanceof List) {
            Map m;
            String id;
            List list = (List)assigned;
            for (Object row : list) {
                String num;
                if (!(row instanceof Map) || !OspSubscriptionEnricher.isOciOcid(id = (m = (Map)row).get("id") == null ? null : String.valueOf(m.get("id")).trim())) continue;
                if (StrUtil.isBlank((CharSequence)ospRef)) {
                    ids.add(id);
                    continue;
                }
                String string = num = m.get("subscriptionNumber") == null ? null : String.valueOf(m.get("subscriptionNumber")).trim();
                if (!ospRef.equals(num) && !ospRef.equals(id)) continue;
                ids.add(id);
            }
            for (Object row : list) {
                if (!(row instanceof Map) || !OspSubscriptionEnricher.isOciOcid((String)(id = (m = (Map)row).get("id") == null ? null : String.valueOf(m.get("id")).trim()))) continue;
                ids.add(id);
            }
        }
        return new ArrayList<String>(ids);
    }

    private static IdentityClient buildIdentityClient(SimpleAuthenticationDetailsProvider provider) {
        IdentityClient.Builder b = IdentityClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (IdentityClient.Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }
        return b.build((AbstractAuthenticationDetailsProvider)provider);
    }

    private static SubscriptionServiceClient buildOspClient(SimpleAuthenticationDetailsProvider provider) {
        SubscriptionServiceClient.Builder b = SubscriptionServiceClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (SubscriptionServiceClient.Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }
        return b.build((AbstractAuthenticationDetailsProvider)provider);
    }

    private void applyIdentityAccountFields(SimpleAuthenticationDetailsProvider provider, String tenancyId, OciUser user, Map<String, Object> result) {
        try (IdentityClient ic = TenantService.buildIdentityClient((SimpleAuthenticationDetailsProvider)provider);){
            Tenancy tenancy = ic.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
            if (tenancy != null) {
                result.put("tenantName", tenancy.getName());
                if (StrUtil.isNotBlank((CharSequence)tenancy.getName()) && !tenancy.getName().equals(user.getTenantName())) {
                    user.setTenantName(tenancy.getName());
                }
                result.put("homeRegionKey", tenancy.getHomeRegionKey());
                result.put("tenantId", tenancy.getId());
                result.put("description", tenancy.getDescription());
            }
            List regions = ic.listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(tenancyId).build()).getItems();
            ArrayList<String> regionNames = new ArrayList<String>();
            if (regions != null) {
                for (RegionSubscription r : regions) {
                    regionNames.add(r.getRegionName());
                }
            }
            result.put("subscribedRegions", regionNames);
        }
        catch (Exception e) {
            log.warn("Failed to get identity account fields: {}", (Object)e.getMessage());
        }
    }

    private static void applyOspAccountFields(SimpleAuthenticationDetailsProvider provider, String ospHomeRegion, String compartmentId, Map<String, Object> result) {
        try (SubscriptionServiceClient ospClient = TenantService.buildOspClient((SimpleAuthenticationDetailsProvider)provider);){
            Object detail;
            List items;
            ListSubscriptionsResponse resp = ospClient.listSubscriptions(ListSubscriptionsRequest.builder().ospHomeRegion(ospHomeRegion).compartmentId(compartmentId).build());
            List list = items = resp.getSubscriptionCollection() == null ? null : resp.getSubscriptionCollection().getItems();
            if (items == null || items.isEmpty()) {
                return;
            }
            SubscriptionSummary sub = (SubscriptionSummary)items.get(0);
            String subId = sub.getId();
            OspSubscriptionEnricher.enrich((Object)sub, result);
            Object merged = sub;
            if (StrUtil.isNotBlank((CharSequence)subId) && (detail = OspSubscriptionEnricher.fetchSubscriptionDetail((SubscriptionServiceClient)ospClient, (String)ospHomeRegion, (String)compartmentId, (String)subId)) != null) {
                merged = detail;
                OspSubscriptionEnricher.enrich((Object)detail, result);
            }
            TenantService.applyRegistrationFromSdk((Object)merged, result);
            if (StrUtil.isNotBlank((CharSequence)subId)) {
                result.put("subscriptionOspRef", subId.trim());
                if (!OspSubscriptionEnricher.isOciOcid((String)subId) && result.get("subscriptionPlanNumber") == null) {
                    result.put("subscriptionPlanNumber", subId.trim());
                }
            }
        }
        catch (Exception e) {
            log.warn("Failed to get OSP subscription: {}", (Object)e.getMessage());
        }
    }

    private static void applyRegistrationFromSdk(Object merged, Map<String, Object> result) {
        Object n;
        Object country;
        if (merged == null || result == null) {
            return;
        }
        String countryName = null;
        Object addr = TenantService.tryInvoke((Object)merged, (String)"getBillToAddress");
        if (addr == null) {
            addr = TenantService.tryInvoke((Object)merged, (String)"getBillingAddress");
        }
        if (addr == null) {
            addr = TenantService.tryInvoke((Object)merged, (String)"getAddress");
        }
        Object object = country = addr == null ? null : TenantService.tryInvoke((Object)addr, (String)"getCountry");
        if (country != null) {
            n = TenantService.tryInvoke((Object)country, (String)"getName");
            if (n == null) {
                n = TenantService.tryInvoke((Object)country, (String)"getCountryName");
            }
            if (n == null) {
                n = TenantService.tryInvoke((Object)country, (String)"getDisplayName");
            }
            if (n != null) {
                countryName = String.valueOf(n);
            }
        }
        if (StrUtil.isBlank(countryName) && addr != null) {
            n = TenantService.tryInvoke((Object)addr, (String)"getCountryName");
            if (n == null) {
                n = TenantService.tryInvoke((Object)addr, (String)"getCountry");
            }
            if (n != null) {
                countryName = String.valueOf(n);
            }
        }
        result.put("registrationLocation", StrUtil.isBlank(countryName) ? null : countryName);
    }

    private static void enrichSubscriptionStatusFromAssigned(Map<String, Object> result, Map<String, Object> orgSub) {
        List list;
        if (result == null || orgSub == null) {
            return;
        }
        if (result.get("subscriptionStatus") != null) {
            return;
        }
        Object assigned = orgSub.get("assignedSubscriptions");
        if (!(assigned instanceof List) || (list = (List)assigned).isEmpty()) {
            return;
        }
        for (Object row : list) {
            Map m;
            String lifecycle;
            if (!(row instanceof Map) || !StrUtil.isNotBlank(lifecycle = (m = (Map)row).get("lifecycleState") == null ? null : String.valueOf(m.get("lifecycleState")).trim())) continue;
            String code = lifecycle.toUpperCase(Locale.ROOT);
            result.put("subscriptionStatus", code);
            result.put("subscriptionStatusLabel", OspSubscriptionEnricher.labelSubscriptionStatus((String)code));
            return;
        }
    }

    private static String countryNameFromRaw(JsonNode sub) {
        if (sub == null || sub.isNull()) {
            return null;
        }
        for (String addrKey : List.of("billingAddress", "billToAddress", "address")) {
            JsonNode addr = sub.get(addrKey);
            if (addr == null || addr.isNull()) continue;
            JsonNode country = addr.get("country");
            if (country != null && !country.isNull()) {
                if (country.hasNonNull("name")) {
                    return country.get("name").asText();
                }
                if (country.hasNonNull("countryName")) {
                    return country.get("countryName").asText();
                }
            }
            if (!addr.hasNonNull("countryName")) continue;
            return addr.get("countryName").asText();
        }
        return null;
    }

    private static Object tryInvoke(Object target, String method) {
        if (target == null) {
            return null;
        }
        try {
            return target.getClass().getMethod(method, new Class[0]).invoke(target, new Object[0]);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static String tryToString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static String tryEnumValue(Object v) {
        if (v == null) {
            return null;
        }
        try {
            Object raw = v.getClass().getMethod("getValue", new Class[0]).invoke(v, new Object[0]);
            return raw == null ? null : String.valueOf(raw);
        }
        catch (Exception ignored) {
            return String.valueOf(v);
        }
    }

    private static String resolveOspHomeRegion(IdentityClient identityClient, String tenancyId, String fallbackRegionName) {
        if (identityClient == null || StrUtil.isBlank((CharSequence)tenancyId)) {
            return fallbackRegionName;
        }
        try {
            String homeKey;
            Tenancy tenancy = identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
            String string = homeKey = tenancy == null ? null : tenancy.getHomeRegionKey();
            if (StrUtil.isBlank((CharSequence)homeKey)) {
                return fallbackRegionName;
            }
            List regions = identityClient.listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(tenancyId).build()).getItems();
            if (regions != null) {
                for (RegionSubscription r : regions) {
                    String name;
                    if (!homeKey.equalsIgnoreCase(r.getRegionKey()) || !StrUtil.isNotBlank((CharSequence)(name = r.getRegionName()))) continue;
                    return name;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return fallbackRegionName;
    }

    /*
     * Exception decompiling
     */
    public byte[] downloadInvoicePdf(String id, String invoiceId) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public Map<String, Object> getDistinctGroups() {
        List all = this.userMapper.selectList(null);
        TreeSet<String> level1 = new TreeSet<String>();
        TreeMap<String, Set> level2Map = new TreeMap<String, Set>();
        for (Object u : all) {
            String g1 = u.getGroupLevel1();
            if (!StrUtil.isNotBlank((CharSequence)g1)) continue;
            level1.add(g1);
            String g2 = u.getGroupLevel2();
            if (!StrUtil.isNotBlank((CharSequence)g2)) continue;
            level2Map.computeIfAbsent(g1, k -> new TreeSet()).add(g2);
        }
        List kvGroups = this.kvMapper.selectList((Wrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group"));
        for (OciKv kv : kvGroups) {
            String val;
            String[] code = kv.getCode();
            if (code.startsWith("group_l1:")) {
                level1.add(code.substring("group_l1:".length()));
                continue;
            }
            if (!code.startsWith("group_l2:") || !StrUtil.isNotBlank((CharSequence)(val = kv.getValue()))) continue;
            String parent = code.substring("group_l2:".length());
            level2Map.computeIfAbsent(parent, k -> new TreeSet()).add(val);
        }
        ArrayList<String> ordered = new ArrayList<String>();
        OciKv orderKv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)"group_order_l1"));
        if (orderKv != null && StrUtil.isNotBlank((CharSequence)orderKv.getValue())) {
            for (String name : orderKv.getValue().split(",")) {
                String n = name.trim();
                if (!level1.remove(n)) continue;
                ordered.add(n);
            }
        }
        ordered.addAll(level1);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("level1", ordered);
        LinkedHashMap l2 = new LinkedHashMap();
        level2Map.forEach((k, v) -> l2.put(k, new ArrayList(v)));
        result.put("level2", l2);
        return result;
    }

    public void saveGroupOrder(List<String> order) {
        if (order == null || order.isEmpty()) {
            return;
        }
        String value = String.join((CharSequence)",", order);
        OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)"group_order_l1"));
        if (kv != null) {
            kv.setValue(value);
            this.kvMapper.updateById((Object)kv);
        } else {
            kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode("group_order_l1");
            kv.setValue(value);
            kv.setType("group");
            kv.setCreateTime(LocalDateTime.now());
            this.kvMapper.insert((Object)kv);
        }
        log.info("Saved group order: {}", (Object)value);
    }

    public void createGroup(String name, String level, String parent) {
        if (StrUtil.isBlank((CharSequence)name)) {
            throw new OciException("\u5206\u7ec4\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if ("1".equals(level)) {
            String code = "group_l1:" + name;
            OciKv exist = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)code));
            if (exist == null) {
                OciKv kv = new OciKv();
                kv.setId(CommonUtils.generateId());
                kv.setCode(code);
                kv.setValue(name);
                kv.setType("group");
                kv.setCreateTime(LocalDateTime.now());
                this.kvMapper.insert((Object)kv);
            }
        } else if ("2".equals(level)) {
            if (StrUtil.isBlank((CharSequence)parent)) {
                throw new OciException("\u5b50\u5206\u7ec4\u5fc5\u987b\u6307\u5b9a\u7236\u5206\u7ec4");
            }
            String code = "group_l2:" + parent;
            OciKv exist = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)code)).eq(OciKv::getValue, (Object)name));
            if (exist == null) {
                OciKv kv = new OciKv();
                kv.setId(CommonUtils.generateId());
                kv.setCode(code);
                kv.setValue(name);
                kv.setType("group");
                kv.setCreateTime(LocalDateTime.now());
                this.kvMapper.insert((Object)kv);
            }
        }
        log.info("Created group [{}] {} parent={}", new Object[]{level, name, parent});
    }

    public void renameGroup(String oldName, String newName, String level) {
        if (StrUtil.isBlank((CharSequence)oldName) || StrUtil.isBlank((CharSequence)newName)) {
            throw new OciException("\u5206\u7ec4\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (oldName.equals(newName)) {
            return;
        }
        List users = this.userMapper.selectList(null);
        for (OciUser u : users) {
            boolean changed = false;
            if ("1".equals(level) && oldName.equals(u.getGroupLevel1())) {
                u.setGroupLevel1(newName);
                changed = true;
            }
            if ("2".equals(level) && oldName.equals(u.getGroupLevel2())) {
                u.setGroupLevel2(newName);
                changed = true;
            }
            if (!changed) continue;
            this.userMapper.updateById((Object)u);
        }
        if ("1".equals(level)) {
            OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)("group_l1:" + oldName)));
            if (kv != null) {
                kv.setCode("group_l1:" + newName);
                kv.setValue(newName);
                this.kvMapper.updateById((Object)kv);
            }
            List l2Kvs = this.kvMapper.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)("group_l2:" + oldName)));
            for (OciKv l2 : l2Kvs) {
                l2.setCode("group_l2:" + newName);
                this.kvMapper.updateById((Object)l2);
            }
        } else if ("2".equals(level)) {
            List kvs = this.kvMapper.selectList((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).likeRight(OciKv::getCode, (Object)"group_l2:")).eq(OciKv::getValue, (Object)oldName));
            for (OciKv kv : kvs) {
                kv.setValue(newName);
                this.kvMapper.updateById((Object)kv);
            }
        }
        log.info("Renamed group [{}] {} -> {}", new Object[]{level, oldName, newName});
    }

    public void deleteGroup(String name, String level) {
        if (StrUtil.isBlank((CharSequence)name)) {
            return;
        }
        List users = this.userMapper.selectList(null);
        for (OciUser u : users) {
            boolean changed = false;
            if ("1".equals(level) && name.equals(u.getGroupLevel1())) {
                u.setGroupLevel1("\u672a\u5206\u7ec4");
                u.setGroupLevel2(null);
                changed = true;
            }
            if ("2".equals(level) && name.equals(u.getGroupLevel2())) {
                u.setGroupLevel2(null);
                changed = true;
            }
            if (!changed) continue;
            this.userMapper.updateById((Object)u);
        }
        if ("1".equals(level)) {
            this.kvMapper.delete((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)("group_l1:" + name)));
            this.kvMapper.delete((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).eq(OciKv::getCode, (Object)("group_l2:" + name)));
        } else if ("2".equals(level)) {
            this.kvMapper.delete((Wrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getType, (Object)"group")).likeRight(OciKv::getCode, (Object)"group_l2:")).eq(OciKv::getValue, (Object)name));
        }
        log.info("Deleted group [{}] {}", (Object)level, (Object)name);
    }

    public String uploadKey(MultipartFile file) throws IOException {
        Path dirPath = Path.of(System.getProperty("user.dir"), this.keyDirPath).normalize();
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = CommonUtils.generateId() + ".pem";
        File target = new File(dir, fileName);
        file.transferTo(target);
        log.info("Uploaded key file: {}", (Object)target.getAbsolutePath());
        return target.getAbsolutePath();
    }
}

