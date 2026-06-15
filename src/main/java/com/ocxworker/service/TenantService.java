package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.ocxworker.util.CommonUtils;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.RegionSubscription;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import com.oracle.bmc.ospgateway.InvoiceServiceClient;
import com.oracle.bmc.ospgateway.SubscriptionServiceClient;
import com.oracle.bmc.ospgateway.SubscriptionServiceClient.Builder;
import com.oracle.bmc.ospgateway.model.SubscriptionSummary;
import com.oracle.bmc.ospgateway.requests.DownloadPdfContentRequest;
import com.oracle.bmc.ospgateway.requests.ListInvoicesRequest;
import com.oracle.bmc.ospgateway.requests.ListSubscriptionsRequest;
import com.oracle.bmc.ospgateway.responses.DownloadPdfContentResponse;
import com.oracle.bmc.ospgateway.responses.ListInvoicesResponse;
import com.oracle.bmc.ospgateway.responses.ListSubscriptionsResponse;
import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private static final Set<String> TENANT_ACCOUNT_INFO_KEYS = Set.of(
        "tenantName",
        "homeRegionKey",
        "tenantId",
        "description",
        "subscribedRegions",
        "planType",
        "planTypeLabel",
        "paymentMethod",
        "paymentMethodLabel",
        "subscriptionUsage",
        "accountType",
        "upgradeState",
        "upgradeStateLabel",
        "subscriptionStatus",
        "subscriptionStatusLabel",
        "currencyCode",
        "isIntentToPay",
        "subscriptionStartTime",
        "registrationLocation",
        "subscriptionPlanNumber",
        "subscriptionOrgOcid"
    );
    private static final ExecutorService TENANT_ACCOUNT_EXECUTOR = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "tenant-account");
        t.setDaemon(true);
        return t;
    });
    private static final String GROUP_TYPE = "group";
    private static final String GROUP_L1_PREFIX = "group_l1:";
    private static final String GROUP_L2_PREFIX = "group_l2:";
    private static final String GROUP_ORDER_CODE = "group_order_l1";
    @Value("${oci-cfg.key-dir-path}")
    private String keyDirPath;

    public Page<Map<String, Object>> list(PageParams params) {
        int pageSize = params.getSize();
        if (pageSize < 1) {
            pageSize = 10;
        } else if (pageSize > 500) {
            pageSize = 500;
        }

        Page<OciUser> page = new Page((long)params.getCurrent(), (long)pageSize);
        LambdaQueryWrapper<OciUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(params.getKeyword())) {
            wrapper.and(
                w -> ((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)((LambdaQueryWrapper)w.like(OciUser::getUsername, params.getKeyword()))
                                    .or())
                                .like(OciUser::getTenantName, params.getKeyword()))
                            .or())
                        .like(OciUser::getOciRegion, params.getKeyword())
            );
        }

        wrapper.orderByDesc(OciUser::getCreateTime);
        Page<OciUser> result = (Page<OciUser>)this.userMapper.selectPage(page, wrapper);
        Page<Map<String, Object>> enriched = new Page(result.getCurrent(), result.getSize(), result.getTotal());
        enriched.setRecords(
            result.getRecords()
                .stream()
                .map(
                    u -> {
                        Map<String, Object> map = new LinkedHashMap<>();
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
                        long running = this.taskMapper
                            .selectCount(
                                (Wrapper)(new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getUserId, u.getId()))
                                    .eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus())
                            );
                        map.put("taskStatus", running > 0L ? "执行开机任务中" : "无开机任务");
                        map.put("hasRunningTask", running > 0L);
                        return map;
                    }
                )
                .toList()
        );
        return enriched;
    }

    public void add(TenantParams params) {
        long duplicateCount = this.userMapper
            .selectCount(
                (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciUser>().eq(OciUser::getOciTenantId, params.getOciTenantId()))
                        .eq(OciUser::getOciUserId, params.getOciUserId()))
                    .eq(OciUser::getOciRegion, params.getOciRegion())
            );
        if (duplicateCount > 0L) {
            throw new OciException("该租户配置已存在（相同 Tenant ID + User ID + Region），请勿重复添加");
        } else {
            long nameCount = this.userMapper.selectCount((Wrapper)new LambdaQueryWrapper<OciUser>().eq(OciUser::getUsername, params.getUsername()));
            if (nameCount > 0L) {
                throw new OciException("名称「" + params.getUsername() + "」已被使用，请更换名称");
            } else {
                this.validateOciCredentials(params);
                OciUser user = new OciUser();
                user.setId(CommonUtils.generateId());
                user.setUsername(params.getUsername());
                user.setOciTenantId(params.getOciTenantId());
                user.setOciUserId(params.getOciUserId());
                user.setOciFingerprint(params.getOciFingerprint());
                user.setOciRegion(params.getOciRegion());
                user.setOciKeyPath(params.getOciKeyPath());
                user.setGroupLevel1(StrUtil.isBlank(params.getGroupLevel1()) ? "未分组" : params.getGroupLevel1());
                user.setGroupLevel2(StrUtil.isBlank(params.getGroupLevel2()) ? null : params.getGroupLevel2());
                user.setCreateTime(LocalDateTime.now());
                this.userMapper.insert(user);
                log.info("Added tenant config: {}", params.getUsername());
                Thread.ofVirtual().start(() -> this.fetchTenantInfo(user));
            }
        }
    }

    private void validateOciCredentials(TenantParams params) {
        SysUserDTO dto = SysUserDTO.builder()
            .username(params.getUsername())
            .ociCfg(
                SysUserDTO.OciCfg.builder()
                    .tenantId(params.getOciTenantId())
                    .userId(params.getOciUserId())
                    .fingerprint(params.getOciFingerprint())
                    .region(params.getOciRegion())
                    .privateKeyPath(params.getOciKeyPath())
                    .build()
            )
            .build();

        try {
            try (OciClientService client = new OciClientService(dto)) {
                client.getIdentityClient().getTenancy(GetTenancyRequest.builder().tenancyId(params.getOciTenantId()).build());
            }
        } catch (Exception var8) {
            String msg = var8.getMessage();
            if (msg != null && msg.contains("NotAuthenticated")) {
                throw new OciException("API 配置验证失败：认证不通过，请检查 Tenant ID、User ID、Fingerprint 和密钥文件");
            } else if (msg != null && msg.contains("not found")) {
                throw new OciException("API 配置验证失败：Tenant ID 不存在");
            } else if (!(var8 instanceof IOException) && (msg == null || !msg.contains("key"))) {
                throw new OciException("API 配置验证失败：" + (msg != null ? msg.substring(0, Math.min(msg.length(), 120)) : "未知错误"));
            } else {
                throw new OciException("API 配置验证失败：密钥文件无效或不存在");
            }
        }
    }

    public void update(TenantParams params) {
        if (StrUtil.isBlank(params.getId())) {
            throw new OciException("ID不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(params.getId());
            if (user == null) {
                throw new OciException("配置不存在");
            } else {
                user.setUsername(params.getUsername());
                user.setOciTenantId(params.getOciTenantId());
                user.setOciUserId(params.getOciUserId());
                user.setOciFingerprint(params.getOciFingerprint());
                user.setOciRegion(params.getOciRegion());
                if (StrUtil.isNotBlank(params.getOciKeyPath())) {
                    user.setOciKeyPath(params.getOciKeyPath());
                }

                user.setGroupLevel1(StrUtil.isBlank(params.getGroupLevel1()) ? null : params.getGroupLevel1());
                user.setGroupLevel2(StrUtil.isBlank(params.getGroupLevel2()) ? null : params.getGroupLevel2());
                this.userMapper.updateById(user);
                log.info("Updated tenant config: {}", params.getUsername());
            }
        }
    }

    public void remove(IdListParams params) {
        this.userMapper.deleteByIds(params.getIdList());
        log.info("Removed tenant configs: {}", params.getIdList());
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public void batchMoveGroup(TenantBatchMoveGroupParams params) {
        String l1 = params.getGroupLevel1().trim();
        String l2 = null;
        if (!"未分组".equals(l1) && StrUtil.isNotBlank(params.getGroupLevel2())) {
            l2 = params.getGroupLevel2().trim();
        }

        for (String id : params.getIdList()) {
            OciUser user = (OciUser)this.userMapper.selectById(id);
            if (user == null) {
                throw new OciException("配置不存在: " + id);
            }

            user.setGroupLevel1(l1);
            user.setGroupLevel2(l2);
            this.userMapper.updateById(user);
        }

        log.info("Batch moved {} tenants to group {}/{}", new Object[]{params.getIdList().size(), l1, l2});
    }

    public OciUser getById(String id) {
        OciUser user = (OciUser)this.userMapper.selectById(id);
        if (user == null) {
            throw new OciException("配置不存在");
        } else {
            return user;
        }
    }

    public void refreshPlanType(String id) {
        OciUser user = (OciUser)this.userMapper.selectById(id);
        if (user == null) {
            throw new OciException("配置不存在");
        } else {
            this.fetchTenantInfo(user);
        }
    }

    private void fetchTenantInfo(OciUser user) {
        try {
            SysUserDTO dto = SysUserDTO.builder()
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
                .build();

            try (OciClientService client = new OciClientService(dto)) {
                try {
                    Tenancy tenancy = client.getIdentityClient().getTenancy(GetTenancyRequest.builder().tenancyId(user.getOciTenantId()).build()).getTenancy();
                    if (tenancy != null && StrUtil.isNotBlank(tenancy.getName())) {
                        user.setTenantName(tenancy.getName());
                    }
                } catch (Exception var17) {
                    log.warn("Failed to fetch tenantName for {}: {}", user.getUsername(), var17.getMessage());
                }

                Builder ospB = SubscriptionServiceClient.builder();
                OciProxyConfigService pxy = OciProxyConfigService.instance();
                if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
                    ospB = (Builder)ospB.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
                }

                SubscriptionServiceClient ospClient = ospB.build(client.getProvider());

                try {
                    ListSubscriptionsResponse resp = ospClient.listSubscriptions(
                        ListSubscriptionsRequest.builder().ospHomeRegion(user.getOciRegion()).compartmentId(client.getCompartmentId()).build()
                    );
                    List<SubscriptionSummary> items = resp.getSubscriptionCollection().getItems();
                    if (items != null && !items.isEmpty()) {
                        String planType = items.get(0).getPlanType() != null ? items.get(0).getPlanType().getValue() : "UNKNOWN";
                        user.setPlanType(planType);
                    }
                } finally {
                    ospClient.close();
                }

                this.userMapper.updateById(user);
            }
        } catch (Exception var20) {
            log.warn("Failed to fetch tenant info for {}: {}", user.getUsername(), var20.getMessage());
        }
    }

    public Map<String, Object> getTenantFullInfo(String id) {
        OciUser user = (OciUser)this.userMapper.selectById(id);
        if (user == null) {
            throw new OciException("配置不存在");
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("configName", user.getUsername());
            result.put("id", user.getId());
            SysUserDTO dto = SysUserDTO.builder()
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
                .build();

            try (OciClientService client = new OciClientService(dto)) {
                String savedTenantName = user.getTenantName();
                String savedPlanType = user.getPlanType();
                SimpleAuthenticationDetailsProvider provider = client.getProvider();
                IdentityClient identityClient = client.getIdentityClient();
                String tenancyId = user.getOciTenantId();
                String fallbackRegion = user.getOciRegion();
                String compartmentId = client.getCompartmentId();
                String ospHomeRegion = resolveOspHomeRegion(identityClient, tenancyId, fallbackRegion);
                String usageRegion = UsageCostService.resolveTenancyHomeRegionName(identityClient, tenancyId, fallbackRegion);
                CompletableFuture<Void> identityFut = CompletableFuture.runAsync(
                    () -> this.applyIdentityAccountFields(provider, tenancyId, user, result), TENANT_ACCOUNT_EXECUTOR
                );
                CompletableFuture<List<Map<String, Object>>> assignedFut = CompletableFuture.supplyAsync(
                    () -> this.organizationSubscriptionService.listAssignedSubscriptionsOnly(client, tenancyId, usageRegion), TENANT_ACCOUNT_EXECUTOR
                );
                CompletableFuture<Void> ospFut = CompletableFuture.runAsync(
                    () -> applyOspAccountFields(provider, ospHomeRegion, compartmentId, result), TENANT_ACCOUNT_EXECUTOR
                );

                try {
                    CompletableFuture.allOf(identityFut, assignedFut, ospFut).get(90L, TimeUnit.SECONDS);
                } catch (Exception var26) {
                    log.warn("Tenant account parallel fetch timeout or error: {}", var26.getMessage());
                }

                List<Map<String, Object>> assignedRows = assignedFut.getNow(List.of());
                Map<String, Object> orgSub = new LinkedHashMap<>();
                orgSub.put("assignedSubscriptions", assignedRows);
                enrichSubscriptionStatusFromAssigned(result, orgSub);
                String ospRef = result.get("subscriptionOspRef") == null ? null : String.valueOf(result.get("subscriptionOspRef")).trim();
                String orgOcid = resolveOrganizationSubscriptionOcid(ospRef, orgSub);
                if (StrUtil.isNotBlank(orgOcid)) {
                    result.put("subscriptionOrgOcid", orgOcid);
                    String usageStart = result.get("subscriptionStartTime") == null ? null : String.valueOf(result.get("subscriptionStartTime"));

                    try {
                        Map<String, Object> subUsage = this.usageCostService
                            .fetchSubscriptionUsageCost(client, tenancyId, List.of(orgOcid), usageStart, fallbackRegion);
                        result.put("subscriptionUsage", slimSubscriptionUsageForAccount(subUsage));
                    } catch (Exception var25) {
                        log.warn("Failed to get subscription usage cost: {}", var25.getMessage());
                    }
                }

                String planVal = result.get("planType") == null ? null : String.valueOf(result.get("planType"));
                if (StrUtil.isNotBlank(planVal) && !Objects.equals(planVal, savedPlanType)) {
                    user.setPlanType(planVal);
                }

                if (!Objects.equals(savedTenantName, user.getTenantName()) || !Objects.equals(savedPlanType, user.getPlanType())) {
                    this.userMapper.updateById(user);
                }
            } catch (OciException var28) {
                throw var28;
            } catch (Exception var29) {
                throw new OciException("获取租户详情失败: " + var29.getMessage());
            }

            pruneTenantAccountInfo(result);
            return result;
        }
    }

    public Map<String, Object> getTenantBillingSummary(String id, Object limitsRaw) {
        if (StrUtil.isBlank(id)) {
            throw new OciException("ID不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(id);
            if (user == null) {
                throw new OciException("配置不存在");
            } else {
                Map<String, Integer> limits = new HashMap<>();
                limits.put("invoices", 5);
                limits.put("payments", 5);
                limits.put("usageStatements", 3);
                limits.put("costDays", 30);
                if (limitsRaw instanceof Map<?, ?> m) {
                    Object inv = m.get("invoices");
                    Object pay = m.get("payments");
                    Object us = m.get("usageStatements");
                    Object costDays = m.get("costDays");
                    if (inv instanceof Number n) {
                        limits.put("invoices", Math.max(1, Math.min(50, n.intValue())));
                    }

                    if (pay instanceof Number n) {
                        limits.put("payments", Math.max(1, Math.min(50, n.intValue())));
                    }

                    if (us instanceof Number n) {
                        limits.put("usageStatements", Math.max(1, Math.min(50, n.intValue())));
                    }

                    if (costDays instanceof Number n) {
                        limits.put("costDays", Math.max(1, Math.min(90, n.intValue())));
                    }
                }

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("id", user.getId());
                result.put("configName", user.getUsername());
                result.put("ociRegion", user.getOciRegion());
                Map<String, Object> links = new LinkedHashMap<>();
                links.put("billingOverview", "https://cloud.oracle.com/billing/overview?region=" + user.getOciRegion());
                links.put("costAnalysis", "https://cloud.oracle.com/billing/cost-analysis?region=" + user.getOciRegion());
                links.put("invoices", "https://cloud.oracle.com/billing/invoices?region=" + user.getOciRegion());
                links.put("paymentHistory", "https://cloud.oracle.com/billing/payments?region=" + user.getOciRegion());
                links.put("upgradeAndPayment", "https://cloud.oracle.com/billing/account?region=" + user.getOciRegion());
                result.put("links", links);
                Map<String, Object> invoices = new LinkedHashMap<>();
                invoices.put("available", Boolean.TRUE);
                invoices.put("items", new ArrayList());
                result.put("invoices", invoices);
                Map<String, Object> payments = new LinkedHashMap<>();
                payments.put("available", Boolean.FALSE);
                payments.put("reason", "暂未接入付款历史 API（不同账号形态可用性不一致），请使用控制台查看");
                payments.put("items", new ArrayList());
                result.put("payments", payments);

                Map<String, Object> usage;
                try {
                    usage = this.usageCostService.fetchCostAnalysis(id, limits.get("costDays"));
                } catch (Exception var39) {
                    usage = new LinkedHashMap<>();
                    usage.put("available", Boolean.FALSE);
                    usage.put("reason", var39.getMessage() == null ? "成本分析查询失败" : var39.getMessage());
                    usage.put("summary", null);
                    usage.put("byService", new ArrayList());
                    usage.put("byDay", new ArrayList());
                }

                result.put("usage", usage);
                SysUserDTO dto = SysUserDTO.builder()
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
                    .build();

                try (OciClientService client = new OciClientService(dto)) {
                    String ospHomeRegion = resolveOspHomeRegion(client.getIdentityClient(), user.getOciTenantId(), user.getOciRegion());
                    InvoiceServiceClient invoiceClient = InvoiceServiceClient.builder().build(client.getProvider());

                    try {
                        ListInvoicesResponse resp = invoiceClient.listInvoices(
                            ListInvoicesRequest.builder()
                                .ospHomeRegion(ospHomeRegion)
                                .compartmentId(client.getCompartmentId())
                                .limit(limits.get("invoices"))
                                .build()
                        );
                        List<Map<String, Object>> items = new ArrayList<>();
                        Object col = null;

                        try {
                            col = resp.getClass().getMethod("getInvoiceSummaryCollection").invoke(resp);
                        } catch (Exception var34) {
                            try {
                                col = resp.getClass().getMethod("getInvoiceCollection").invoke(resp);
                            } catch (Exception var33) {
                                col = null;
                            }
                        }

                        List<?> summaries = null;
                        if (col != null) {
                            try {
                                if (col.getClass().getMethod("getItems").invoke(col) instanceof List<?> list) {
                                    summaries = list;
                                }
                            } catch (Exception var32) {
                                summaries = null;
                            }
                        }

                        if (summaries != null) {
                            for (Object invx : summaries) {
                                Map<String, Object> row = new LinkedHashMap<>();
                                row.put("invoiceId", tryInvoke(invx, "getInternalInvoiceId"));
                                row.put("invoiceNo", tryInvoke(invx, "getInvoiceNo"));
                                row.put("refNo", tryInvoke(invx, "getRefNo"));
                                row.put("status", tryEnumValue(tryInvoke(invx, "getStatus")));
                                row.put("type", tryEnumValue(tryInvoke(invx, "getType")));
                                row.put("invoiceDate", tryToString(tryInvoke(invx, "getInvoiceDate")));
                                row.put("dueDate", tryToString(tryInvoke(invx, "getDueDate")));
                                row.put("totalAmount", tryInvoke(invx, "getTotalAmount"));
                                row.put("currencyCode", tryInvoke(invx, "getCurrencyCode"));
                                items.add(row);
                            }
                        }

                        items.sort((a, b) -> {
                            String da = String.valueOf(a.getOrDefault("invoiceDate", ""));
                            String db = String.valueOf(b.getOrDefault("invoiceDate", ""));
                            return db.compareTo(da);
                        });
                        invoices.put("items", items);
                    } catch (Exception var35) {
                        invoices.put("available", Boolean.FALSE);
                        invoices.put("reason", "发票接口不可用/权限不足：" + (var35.getMessage() == null ? "未知错误" : var35.getMessage()));
                    } finally {
                        invoiceClient.close();
                    }
                } catch (Exception var38) {
                    invoices.put("available", Boolean.FALSE);
                    invoices.put("reason", "初始化账务客户端失败：" + (var38.getMessage() == null ? "未知错误" : var38.getMessage()));
                }

                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("invoiceCount", ((List)invoices.getOrDefault("items", List.of())).size());
                Map<String, Object> latestInvoice = null;
                List<?> invItems = (List<?>)invoices.getOrDefault("items", List.of());
                if (!invItems.isEmpty() && invItems.get(0) instanceof Map<?, ?> m) {
                    latestInvoice = new LinkedHashMap<>();
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
        }
    }

    private static void pruneTenantAccountInfo(Map<String, Object> result) {
        if (result != null && !result.isEmpty()) {
            result.keySet().removeIf(k -> !TENANT_ACCOUNT_INFO_KEYS.contains(k));
        }
    }

    private static Map<String, Object> slimSubscriptionUsageForAccount(Map<String, Object> raw) {
        if (raw != null && Boolean.TRUE.equals(raw.get("available"))) {
            Map<String, Object> slim = new LinkedHashMap<>();
            slim.put("timeUsageStarted", raw.get("timeUsageStarted"));
            if (raw.get("summary") instanceof Map<?, ?> s) {
                Map<String, Object> ss = new LinkedHashMap<>();
                ss.put("totalConsumed", s.get("totalConsumed"));
                ss.put("totalConsumedLabel", s.get("totalConsumedLabel"));
                slim.put("summary", ss);
            }

            return slim;
        } else {
            return null;
        }
    }

    private static String resolveOrganizationSubscriptionOcid(String ospRef, Map<String, Object> orgSub) {
        List<String> ids = resolveOrganizationSubscriptionOcids(ospRef, orgSub);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private static List<String> resolveOrganizationSubscriptionOcids(String ospRef, Map<String, Object> orgSub) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        if (orgSub == null) {
            return List.of();
        } else {
            if (orgSub.get("assignedSubscriptions") instanceof List<?> list) {
                for (Object row : list) {
                    if (row instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>)row;
                        String id = m.get("id") == null ? null : String.valueOf(m.get("id")).trim();
                        if (OspSubscriptionEnricher.isOciOcid(id)) {
                            if (StrUtil.isBlank(ospRef)) {
                                ids.add(id);
                            } else {
                                String num = m.get("subscriptionNumber") == null ? null : String.valueOf(m.get("subscriptionNumber")).trim();
                                if (ospRef.equals(num) || ospRef.equals(id)) {
                                    ids.add(id);
                                }
                            }
                        }
                    }
                }

                for (Object rowx : list) {
                    if (rowx instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>)rowx;
                        String id = m.get("id") == null ? null : String.valueOf(m.get("id")).trim();
                        if (OspSubscriptionEnricher.isOciOcid(id)) {
                            ids.add(id);
                        }
                    }
                }
            }

            return new ArrayList<>(ids);
        }
    }

    private static IdentityClient buildIdentityClient(SimpleAuthenticationDetailsProvider provider) {
        com.oracle.bmc.identity.IdentityClient.Builder b = IdentityClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (com.oracle.bmc.identity.IdentityClient.Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }

        return b.build(provider);
    }

    private static SubscriptionServiceClient buildOspClient(SimpleAuthenticationDetailsProvider provider) {
        Builder b = SubscriptionServiceClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }

        return b.build(provider);
    }

    private void applyIdentityAccountFields(SimpleAuthenticationDetailsProvider provider, String tenancyId, OciUser user, Map<String, Object> result) {
        try {
            IdentityClient ic = buildIdentityClient(provider);

            try {
                Tenancy tenancy = ic.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
                if (tenancy != null) {
                    result.put("tenantName", tenancy.getName());
                    if (StrUtil.isNotBlank(tenancy.getName()) && !tenancy.getName().equals(user.getTenantName())) {
                        user.setTenantName(tenancy.getName());
                    }

                    result.put("homeRegionKey", tenancy.getHomeRegionKey());
                    result.put("tenantId", tenancy.getId());
                    result.put("description", tenancy.getDescription());
                }

                List<RegionSubscription> regions = ic.listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(tenancyId).build()).getItems();
                List<String> regionNames = new ArrayList<>();
                if (regions != null) {
                    for (RegionSubscription r : regions) {
                        regionNames.add(r.getRegionName());
                    }
                }

                result.put("subscribedRegions", regionNames);
            } catch (Throwable var12) {
                if (ic != null) {
                    try {
                        ic.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }

                throw var12;
            }

            if (ic != null) {
                ic.close();
            }
        } catch (Exception var13) {
            log.warn("Failed to get identity account fields: {}", var13.getMessage());
        }
    }

    private static void applyOspAccountFields(
        SimpleAuthenticationDetailsProvider provider, String ospHomeRegion, String compartmentId, Map<String, Object> result
    ) {
        try {
            SubscriptionServiceClient ospClient = buildOspClient(provider);

            label67: {
                try {
                    ListSubscriptionsResponse resp = ospClient.listSubscriptions(
                        ListSubscriptionsRequest.builder().ospHomeRegion(ospHomeRegion).compartmentId(compartmentId).build()
                    );
                    List<SubscriptionSummary> items = resp.getSubscriptionCollection() == null ? null : resp.getSubscriptionCollection().getItems();
                    if (items != null && !items.isEmpty()) {
                        SubscriptionSummary sub = items.get(0);
                        String subId = sub.getId();
                        OspSubscriptionEnricher.enrich(sub, result);
                        Object merged = sub;
                        if (StrUtil.isNotBlank(subId)) {
                            Object detail = OspSubscriptionEnricher.fetchSubscriptionDetail(ospClient, ospHomeRegion, compartmentId, subId);
                            if (detail != null) {
                                merged = detail;
                                OspSubscriptionEnricher.enrich(detail, result);
                            }
                        }

                        applyRegistrationFromSdk(merged, result);
                        if (!StrUtil.isNotBlank(subId)) {
                            break label67;
                        }

                        result.put("subscriptionOspRef", subId.trim());
                        if (!OspSubscriptionEnricher.isOciOcid(subId) && result.get("subscriptionPlanNumber") == null) {
                            result.put("subscriptionPlanNumber", subId.trim());
                        }
                        break label67;
                    }
                } catch (Throwable var12) {
                    if (ospClient != null) {
                        try {
                            ospClient.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                if (ospClient != null) {
                    ospClient.close();
                }

                return;
            }

            if (ospClient != null) {
                ospClient.close();
            }
        } catch (Exception var13) {
            log.warn("Failed to get OSP subscription: {}", var13.getMessage());
        }
    }

    private static void applyRegistrationFromSdk(Object merged, Map<String, Object> result) {
        if (merged != null && result != null) {
            String countryName = null;
            Object addr = tryInvoke(merged, "getBillToAddress");
            if (addr == null) {
                addr = tryInvoke(merged, "getBillingAddress");
            }

            if (addr == null) {
                addr = tryInvoke(merged, "getAddress");
            }

            Object country = addr == null ? null : tryInvoke(addr, "getCountry");
            if (country != null) {
                Object n = tryInvoke(country, "getName");
                if (n == null) {
                    n = tryInvoke(country, "getCountryName");
                }

                if (n == null) {
                    n = tryInvoke(country, "getDisplayName");
                }

                if (n != null) {
                    countryName = String.valueOf(n);
                }
            }

            if (StrUtil.isBlank(countryName) && addr != null) {
                Object nx = tryInvoke(addr, "getCountryName");
                if (nx == null) {
                    nx = tryInvoke(addr, "getCountry");
                }

                if (nx != null) {
                    countryName = String.valueOf(nx);
                }
            }

            result.put("registrationLocation", StrUtil.isBlank(countryName) ? null : countryName);
        }
    }

    private static void enrichSubscriptionStatusFromAssigned(Map<String, Object> result, Map<String, Object> orgSub) {
        if (result != null && orgSub != null) {
            if (result.get("subscriptionStatus") == null) {
                if (orgSub.get("assignedSubscriptions") instanceof List<?> list && !list.isEmpty()) {
                    for (Object row : list) {
                        if (row instanceof Map<?, ?> m) {
                            String lifecycle = m.get("lifecycleState") == null ? null : String.valueOf(m.get("lifecycleState")).trim();
                            if (StrUtil.isNotBlank(lifecycle)) {
                                String code = lifecycle.toUpperCase(Locale.ROOT);
                                result.put("subscriptionStatus", code);
                                result.put("subscriptionStatusLabel", OspSubscriptionEnricher.labelSubscriptionStatus(code));
                                return;
                            }
                        }
                    }

                    return;
                }
            }
        }
    }

    private static String countryNameFromRaw(JsonNode sub) {
        if (sub != null && !sub.isNull()) {
            for (String addrKey : List.of("billingAddress", "billToAddress", "address")) {
                JsonNode addr = sub.get(addrKey);
                if (addr != null && !addr.isNull()) {
                    JsonNode country = addr.get("country");
                    if (country != null && !country.isNull()) {
                        if (country.hasNonNull("name")) {
                            return country.get("name").asText();
                        }

                        if (country.hasNonNull("countryName")) {
                            return country.get("countryName").asText();
                        }
                    }

                    if (addr.hasNonNull("countryName")) {
                        return addr.get("countryName").asText();
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    private static Object tryInvoke(Object target, String method) {
        if (target == null) {
            return null;
        } else {
            try {
                return target.getClass().getMethod(method).invoke(target);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    private static String tryToString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static String tryEnumValue(Object v) {
        if (v == null) {
            return null;
        } else {
            try {
                Object raw = v.getClass().getMethod("getValue").invoke(v);
                return raw == null ? null : String.valueOf(raw);
            } catch (Exception var2) {
                return String.valueOf(v);
            }
        }
    }

    private static String resolveOspHomeRegion(IdentityClient identityClient, String tenancyId, String fallbackRegionName) {
        if (identityClient != null && !StrUtil.isBlank(tenancyId)) {
            try {
                Tenancy tenancy = identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
                String homeKey = tenancy == null ? null : tenancy.getHomeRegionKey();
                if (StrUtil.isBlank(homeKey)) {
                    return fallbackRegionName;
                }

                List<RegionSubscription> regions = identityClient.listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(tenancyId).build())
                    .getItems();
                if (regions != null) {
                    for (RegionSubscription r : regions) {
                        if (homeKey.equalsIgnoreCase(r.getRegionKey())) {
                            String name = r.getRegionName();
                            if (StrUtil.isNotBlank(name)) {
                                return name;
                            }
                        }
                    }
                }
            } catch (Exception var9) {
            }

            return fallbackRegionName;
        } else {
            return fallbackRegionName;
        }
    }

    public byte[] downloadInvoicePdf(String id, String invoiceId) {
        if (StrUtil.isBlank(id)) {
            throw new OciException("ID不能为空");
        } else if (StrUtil.isBlank(invoiceId)) {
            throw new OciException("invoiceId不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(id);
            if (user == null) {
                throw new OciException("配置不存在");
            } else {
                SysUserDTO dto = SysUserDTO.builder()
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
                    .build();

                try {
                    byte[] var11;
                    try (OciClientService client = new OciClientService(dto)) {
                        String ospHomeRegion = resolveOspHomeRegion(client.getIdentityClient(), user.getOciTenantId(), user.getOciRegion());
                        InvoiceServiceClient invoiceClient = InvoiceServiceClient.builder().build(client.getProvider());

                        try {
                            DownloadPdfContentResponse resp = invoiceClient.downloadPdfContent(
                                DownloadPdfContentRequest.builder()
                                    .ospHomeRegion(ospHomeRegion)
                                    .compartmentId(client.getCompartmentId())
                                    .internalInvoiceId(invoiceId)
                                    .build()
                            );

                            try (
                                InputStream is = resp.getInputStream();
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                            ) {
                                is.transferTo(out);
                                var11 = out.toByteArray();
                            }
                        } finally {
                            invoiceClient.close();
                        }
                    }

                    return var11;
                } catch (Exception var29) {
                    throw new OciException("下载发票 PDF 失败：" + (var29.getMessage() == null ? "未知错误" : var29.getMessage()));
                }
            }
        }
    }

    public Map<String, Object> getDistinctGroups() {
        List<OciUser> all = this.userMapper.selectList(null);
        Set<String> level1 = new TreeSet<>();
        Map<String, Set<String>> level2Map = new TreeMap<>();

        for (OciUser u : all) {
            String g1 = u.getGroupLevel1();
            if (StrUtil.isNotBlank(g1)) {
                level1.add(g1);
                String g2 = u.getGroupLevel2();
                if (StrUtil.isNotBlank(g2)) {
                    level2Map.computeIfAbsent(g1, k -> new TreeSet<>()).add(g2);
                }
            }
        }

        for (OciKv kv : this.kvMapper.selectList((Wrapper)new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group"))) {
            String code = kv.getCode();
            if (code.startsWith("group_l1:")) {
                level1.add(code.substring("group_l1:".length()));
            } else if (code.startsWith("group_l2:")) {
                String val = kv.getValue();
                if (StrUtil.isNotBlank(val)) {
                    String parent = code.substring("group_l2:".length());
                    level2Map.computeIfAbsent(parent, k -> new TreeSet<>()).add(val);
                }
            }
        }

        List<String> ordered = new ArrayList<>();
        OciKv orderKv = (OciKv)this.kvMapper
            .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_order_l1"));
        if (orderKv != null && StrUtil.isNotBlank(orderKv.getValue())) {
            for (String name : orderKv.getValue().split(",")) {
                String n = name.trim();
                if (level1.remove(n)) {
                    ordered.add(n);
                }
            }
        }

        ordered.addAll(level1);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("level1", ordered);
        Map<String, List<String>> l2 = new LinkedHashMap<>();
        level2Map.forEach((k, v) -> l2.put(k, new ArrayList<>(v)));
        result.put("level2", l2);
        return result;
    }

    public void saveGroupOrder(List<String> order) {
        if (order != null && !order.isEmpty()) {
            String value = String.join(",", order);
            OciKv kv = (OciKv)this.kvMapper
                .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_order_l1"));
            if (kv != null) {
                kv.setValue(value);
                this.kvMapper.updateById(kv);
            } else {
                kv = new OciKv();
                kv.setId(CommonUtils.generateId());
                kv.setCode("group_order_l1");
                kv.setValue(value);
                kv.setType("group");
                kv.setCreateTime(LocalDateTime.now());
                this.kvMapper.insert(kv);
            }

            log.info("Saved group order: {}", value);
        }
    }

    public void createGroup(String name, String level, String parent) {
        if (StrUtil.isBlank(name)) {
            throw new OciException("分组名不能为空");
        } else {
            if ("1".equals(level)) {
                String code = "group_l1:" + name;
                OciKv exist = (OciKv)this.kvMapper
                    .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, code));
                if (exist == null) {
                    OciKv kv = new OciKv();
                    kv.setId(CommonUtils.generateId());
                    kv.setCode(code);
                    kv.setValue(name);
                    kv.setType("group");
                    kv.setCreateTime(LocalDateTime.now());
                    this.kvMapper.insert(kv);
                }
            } else if ("2".equals(level)) {
                if (StrUtil.isBlank(parent)) {
                    throw new OciException("子分组必须指定父分组");
                }

                String code = "group_l2:" + parent;
                OciKv exist = (OciKv)this.kvMapper
                    .selectOne(
                        (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, code))
                            .eq(OciKv::getValue, name)
                    );
                if (exist == null) {
                    OciKv kv = new OciKv();
                    kv.setId(CommonUtils.generateId());
                    kv.setCode(code);
                    kv.setValue(name);
                    kv.setType("group");
                    kv.setCreateTime(LocalDateTime.now());
                    this.kvMapper.insert(kv);
                }
            }

            log.info("Created group [{}] {} parent={}", new Object[]{level, name, parent});
        }
    }

    public void renameGroup(String oldName, String newName, String level) {
        if (StrUtil.isBlank(oldName) || StrUtil.isBlank(newName)) {
            throw new OciException("分组名不能为空");
        } else if (!oldName.equals(newName)) {
            for (OciUser u : this.userMapper.selectList(null)) {
                boolean changed = false;
                if ("1".equals(level) && oldName.equals(u.getGroupLevel1())) {
                    u.setGroupLevel1(newName);
                    changed = true;
                }

                if ("2".equals(level) && oldName.equals(u.getGroupLevel2())) {
                    u.setGroupLevel2(newName);
                    changed = true;
                }

                if (changed) {
                    this.userMapper.updateById(u);
                }
            }

            if ("1".equals(level)) {
                OciKv kv = (OciKv)this.kvMapper
                    .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_l1:" + oldName));
                if (kv != null) {
                    kv.setCode("group_l1:" + newName);
                    kv.setValue(newName);
                    this.kvMapper.updateById(kv);
                }

                for (OciKv l2 : this.kvMapper
                    .selectList((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_l2:" + oldName))) {
                    l2.setCode("group_l2:" + newName);
                    this.kvMapper.updateById(l2);
                }
            } else if ("2".equals(level)) {
                for (OciKv kv : this.kvMapper
                    .selectList(
                        (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group"))
                                .likeRight(OciKv::getCode, "group_l2:"))
                            .eq(OciKv::getValue, oldName)
                    )) {
                    kv.setValue(newName);
                    this.kvMapper.updateById(kv);
                }
            }

            log.info("Renamed group [{}] {} -> {}", new Object[]{level, oldName, newName});
        }
    }

    public void deleteGroup(String name, String level) {
        if (!StrUtil.isBlank(name)) {
            for (OciUser u : this.userMapper.selectList(null)) {
                boolean changed = false;
                if ("1".equals(level) && name.equals(u.getGroupLevel1())) {
                    u.setGroupLevel1("未分组");
                    u.setGroupLevel2(null);
                    changed = true;
                }

                if ("2".equals(level) && name.equals(u.getGroupLevel2())) {
                    u.setGroupLevel2(null);
                    changed = true;
                }

                if (changed) {
                    this.userMapper.updateById(u);
                }
            }

            if ("1".equals(level)) {
                this.kvMapper
                    .delete((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_l1:" + name));
                this.kvMapper
                    .delete((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group")).eq(OciKv::getCode, "group_l2:" + name));
            } else if ("2".equals(level)) {
                this.kvMapper
                    .delete(
                        (Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getType, "group"))
                                .likeRight(OciKv::getCode, "group_l2:"))
                            .eq(OciKv::getValue, name)
                    );
            }

            log.info("Deleted group [{}] {}", level, name);
        }
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
        log.info("Uploaded key file: {}", target.getAbsolutePath());
        return target.getAbsolutePath();
    }
}
