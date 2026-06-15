package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.oracle.bmc.Region;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.onesubscription.SubscribedServiceClient;
import com.oracle.bmc.onesubscription.model.SubscribedServiceSummary;
import com.oracle.bmc.onesubscription.requests.ListSubscribedServicesRequest;
import com.oracle.bmc.onesubscription.responses.ListSubscribedServicesResponse;
import com.oracle.bmc.tenantmanagercontrolplane.SubscriptionClient;
import com.oracle.bmc.tenantmanagercontrolplane.SubscriptionClient.Builder;
import com.oracle.bmc.tenantmanagercontrolplane.model.AssignedSubscriptionCollection;
import com.oracle.bmc.tenantmanagercontrolplane.model.AssignedSubscriptionSummary;
import com.oracle.bmc.tenantmanagercontrolplane.requests.ListAssignedSubscriptionsRequest;
import com.oracle.bmc.tenantmanagercontrolplane.responses.ListAssignedSubscriptionsResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrganizationSubscriptionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OrganizationSubscriptionService.class);
    private static final int PAGE_LIMIT = 100;

    public List<Map<String, Object>> listAssignedSubscriptionsOnly(OciClientService oci, String tenancyId, String fallbackRegion) {
        String region = UsageCostService.resolveTenancyHomeRegionName(oci.getIdentityClient(), tenancyId, fallbackRegion);
        return this.listAssignedSubscriptionsOnlyInRegion(oci, tenancyId, region);
    }

    private List<Map<String, Object>> listAssignedSubscriptionsOnlyInRegion(OciClientService oci, String tenancyId, String homeRegionName) {
        if (StrUtil.isBlank(tenancyId)) {
            return List.of();
        } else {
            String region = StrUtil.isNotBlank(homeRegionName) ? homeRegionName.trim() : Region.US_ASHBURN_1.getRegionId();

            try {
                SubscriptionClient subClient = buildSubscriptionClient(oci);

                List var6;
                try {
                    setRegion(subClient, region);
                    var6 = listAssignedSubscriptions(subClient, tenancyId);
                } catch (Throwable var9) {
                    if (subClient != null) {
                        try {
                            subClient.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (subClient != null) {
                    subClient.close();
                }

                return var6;
            } catch (Exception var10) {
                log.warn("listAssignedSubscriptionsOnly failed: {}", var10.getMessage());
                return List.of();
            }
        }
    }

    public Map<String, Object> fetchOrganizationSubscription(
        OciClientService oci, String tenancyId, String fallbackRegion, String ospSubscriptionRef, List<String> extraSubscriptionOcids
    ) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("assignedSubscriptions", new ArrayList());
        out.put("subscribedServices", new ArrayList());
        if (StrUtil.isBlank(tenancyId)) {
            out.put("reason", "缺少 tenancy OCID");
            return out;
        } else {
            String region = UsageCostService.resolveTenancyHomeRegionName(oci.getIdentityClient(), tenancyId, fallbackRegion);
            List<Map<String, Object>> assignedRows = new ArrayList<>();
            List<Map<String, Object>> serviceRows = new ArrayList<>();
            List<String> notes = new ArrayList<>();

            try {
                SubscriptionClient subClient = buildSubscriptionClient(oci);

                try {
                    setRegion(subClient, region);
                    assignedRows.addAll(listAssignedSubscriptions(subClient, tenancyId));
                } catch (Throwable var22) {
                    if (subClient != null) {
                        try {
                            subClient.close();
                        } catch (Throwable var19) {
                            var22.addSuppressed(var19);
                        }
                    }

                    throw var22;
                }

                if (subClient != null) {
                    subClient.close();
                }
            } catch (BmcException var23) {
                log.warn("listAssignedSubscriptions failed: {}", var23.getMessage());
                notes.add(formatOrgError("订购分配", var23));
            } catch (Exception var24) {
                log.warn("listAssignedSubscriptions failed: {}", var24.getMessage());
                notes.add("订购分配查询失败：" + var24.getMessage());
            }

            Set<String> subscriptionIdsToQuery = resolveSubscriptionOcidCandidates(assignedRows, ospSubscriptionRef, extraSubscriptionOcids);
            if (subscriptionIdsToQuery.isEmpty()) {
                out.put("assignedSubscriptions", assignedRows);
                out.put("subscribedServices", serviceRows);
                notes.add(buildNoOcidReason(ospSubscriptionRef, assignedRows));
                out.put("reason", String.join("；", notes));
                return out;
            } else {
                try {
                    SubscribedServiceClient svcClient = buildSubscribedServiceClient(oci);

                    try {
                        setRegion(svcClient, region);

                        for (String subId : subscriptionIdsToQuery) {
                            try {
                                serviceRows.addAll(listSubscribedServices(svcClient, tenancyId, subId));
                            } catch (BmcException var18) {
                                log.warn("listSubscribedServices {} failed: {}", subId, var18.getMessage());
                                Map<String, Object> err = new LinkedHashMap<>();
                                err.put("subscriptionId", subId);
                                err.put("error", formatOrgError("子服务额度", var18));
                                serviceRows.add(err);
                            }
                        }
                    } catch (Throwable var20) {
                        if (svcClient != null) {
                            try {
                                svcClient.close();
                            } catch (Throwable var17) {
                                var20.addSuppressed(var17);
                            }
                        }

                        throw var20;
                    }

                    if (svcClient != null) {
                        svcClient.close();
                    }
                } catch (Exception var21) {
                    log.warn("SubscribedService client failed: {}", var21.getMessage());
                    out.put("assignedSubscriptions", assignedRows);
                    out.put("reason", "子服务额度查询失败：" + var21.getMessage());
                    return out;
                }

                out.put("available", Boolean.TRUE);
                out.put("assignedSubscriptions", assignedRows);
                out.put("subscribedServices", serviceRows);
                if (assignedRows.isEmpty() && serviceRows.isEmpty()) {
                    out.put("available", Boolean.FALSE);
                    notes.add("订购与子服务接口均无数据");
                }

                if (!notes.isEmpty()) {
                    out.put("reason", String.join("；", notes));
                }

                return out;
            }
        }
    }

    static Set<String> resolveSubscriptionOcidCandidates(List<Map<String, Object>> assignedRows, String ospSubscriptionRef, List<String> extraSubscriptionOcids) {
        Set<String> ids = new LinkedHashSet<>();
        if (extraSubscriptionOcids != null) {
            for (String id : extraSubscriptionOcids) {
                if (OspSubscriptionEnricher.isOciOcid(id) && !OspSubscriptionEnricher.isOrganizationsSubscriptionOcid(id)) {
                    ids.add(id.trim());
                }
            }
        }

        if (assignedRows != null) {
            for (Map<String, Object> row : assignedRows) {
                String idx = row.get("id") == null ? null : String.valueOf(row.get("id")).trim();
                if (OspSubscriptionEnricher.isOciOcid(idx)) {
                    ids.add(idx);
                }
            }
        }

        if (OspSubscriptionEnricher.isOciOcid(ospSubscriptionRef)) {
            ids.add(ospSubscriptionRef.trim());
        } else if (StrUtil.isNotBlank(ospSubscriptionRef) && assignedRows != null) {
            String ref = ospSubscriptionRef.trim();

            for (Map<String, Object> rowx : assignedRows) {
                String num = rowx.get("subscriptionNumber") == null ? null : String.valueOf(rowx.get("subscriptionNumber")).trim();
                if (ref.equals(num)) {
                    String idx = rowx.get("id") == null ? null : String.valueOf(rowx.get("id")).trim();
                    if (OspSubscriptionEnricher.isOciOcid(idx)) {
                        ids.add(idx);
                    }
                }
            }
        }

        return ids;
    }

    private static String buildNoOcidReason(String ospRef, List<Map<String, Object>> assignedRows) {
        if (assignedRows != null && !assignedRows.isEmpty()) {
            return StrUtil.isNotBlank(ospRef) && !OspSubscriptionEnricher.isOciOcid(ospRef)
                ? "OSP 订阅引用「" + ospRef + "」为编号非 OCID；Subscribed Service / Rewards 需 ocid1.*，请对照下方 Assigned 表中的订阅 ID"
                : "未解析到可用于 Subscribed Service 的订阅 OCID";
        } else {
            return "无 Assigned Subscription 记录" + (StrUtil.isNotBlank(ospRef) ? "（OSP 引用：" + ospRef + "）" : "");
        }
    }

    private static SubscriptionClient buildSubscriptionClient(OciClientService oci) {
        Builder b = SubscriptionClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }

        return b.build(oci.getProvider());
    }

    private static SubscribedServiceClient buildSubscribedServiceClient(OciClientService oci) {
        com.oracle.bmc.onesubscription.SubscribedServiceClient.Builder b = SubscribedServiceClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (com.oracle.bmc.onesubscription.SubscribedServiceClient.Builder)b.additionalClientConfigurator(
                OciProxyConfigService.ociSdkJerseyDirectConfigurator()
            );
        }

        return b.build(oci.getProvider());
    }

    private static void setRegion(Object client, String regionId) {
        try {
            if (client instanceof SubscriptionClient c) {
                c.setRegion(Region.fromRegionId(regionId));
            } else if (client instanceof SubscribedServiceClient c) {
                c.setRegion(Region.fromRegionId(regionId));
            }
        } catch (Exception var5) {
            if (client instanceof SubscriptionClient c) {
                c.setRegion(Region.US_ASHBURN_1);
            } else if (client instanceof SubscribedServiceClient c) {
                c.setRegion(Region.US_ASHBURN_1);
            }
        }
    }

    private static List<Map<String, Object>> listAssignedSubscriptions(SubscriptionClient client, String tenancyId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String page = null;

        do {
            com.oracle.bmc.tenantmanagercontrolplane.requests.ListAssignedSubscriptionsRequest.Builder req = ListAssignedSubscriptionsRequest.builder()
                .compartmentId(tenancyId)
                .limit(100);
            if (StrUtil.isNotBlank(page)) {
                req.page(page);
            }

            ListAssignedSubscriptionsResponse resp = client.listAssignedSubscriptions(req.build());
            AssignedSubscriptionCollection col = resp == null ? null : resp.getAssignedSubscriptionCollection();
            if (col == null || col.getItems() == null) {
                break;
            }

            for (AssignedSubscriptionSummary item : col.getItems()) {
                if (item != null) {
                    rows.add(mapAssignedSubscription(item));
                }
            }

            page = resp.getOpcNextPage();
        } while (StrUtil.isNotBlank(page));

        return rows;
    }

    private static Map<String, Object> mapAssignedSubscription(AssignedSubscriptionSummary item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("compartmentId", item.getCompartmentId());
        m.put("serviceName", item.getServiceName());
        m.put("timeCreated", formatInstant(item.getTimeCreated()));
        m.put("timeUpdated", formatInstant(item.getTimeUpdated()));
        m.put("entityVersion", enumValue(tryInvoke(item, "getEntityVersion")));
        String lifecycle = firstString(item, "getLifecycleState", "getLifecycleStateDetails");
        m.put("lifecycleState", lifecycle);
        m.put("subscriptionNumber", asString(tryInvoke(item, "getSubscriptionNumber")));
        m.put("currencyCode", asString(tryInvoke(item, "getCurrencyCode")));
        return m;
    }

    private static List<Map<String, Object>> listSubscribedServices(SubscribedServiceClient client, String tenancyId, String subscriptionId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String page = null;

        do {
            com.oracle.bmc.onesubscription.requests.ListSubscribedServicesRequest.Builder req = ListSubscribedServicesRequest.builder()
                .compartmentId(tenancyId)
                .subscriptionId(subscriptionId)
                .limit(100);
            if (StrUtil.isNotBlank(page)) {
                req.page(page);
            }

            ListSubscribedServicesResponse resp = client.listSubscribedServices(req.build());
            List<SubscribedServiceSummary> items = resp == null ? null : resp.getItems();
            if (items == null || items.isEmpty()) {
                break;
            }

            for (SubscribedServiceSummary item : items) {
                if (item != null) {
                    rows.add(mapSubscribedService(item, subscriptionId));
                }
            }

            page = resp.getOpcNextPage();
        } while (StrUtil.isNotBlank(page));

        return rows;
    }

    private static Map<String, Object> mapSubscribedService(SubscribedServiceSummary item, String subscriptionId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("subscriptionId", subscriptionId);
        m.put("orderNumber", item.getOrderNumber());
        m.put("status", item.getStatus());
        m.put("fundedAllocationValue", item.getFundedAllocationValue());
        m.put("availableAmount", item.getAvailableAmount());
        m.put("creditPercentage", item.getCreditPercentage());
        m.put("timeStart", formatInstant(item.getTimeStart()));
        m.put("timeEnd", formatInstant(item.getTimeEnd()));
        m.put("timeCreated", formatInstant(item.getTimeCreated()));
        m.put("timeUpdated", formatInstant(item.getTimeUpdated()));
        Object product = item.getProduct();
        if (product != null) {
            Object name = tryInvoke(product, "getName");
            if (name == null) {
                name = tryInvoke(product, "getProductName");
            }

            if (name == null) {
                name = tryInvoke(product, "getDisplayName");
            }

            m.put("productName", name == null ? null : String.valueOf(name));
        }

        return m;
    }

    private static String formatOrgError(String apiLabel, BmcException e) {
        String msg = e.getMessage() == null ? "未知错误" : e.getMessage();
        int code = e.getStatusCode();
        if (code == 404) {
            return apiLabel + " 无数据（404）";
        } else {
            return code != 401 && code != 403 && !msg.contains("NotAuthorized")
                ? apiLabel + " 查询失败（HTTP " + code + "）：" + msg
                : apiLabel + " 权限不足（需 inspect/read 订购与子服务相关权限）：" + msg;
        }
    }

    private static String formatInstant(Date d) {
        return d == null ? null : d.toInstant().toString();
    }

    private static String enumValue(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Enum<?> e) {
            Object val = tryInvoke(e, "getValue");
            return val != null ? String.valueOf(val) : e.name();
        } else {
            return String.valueOf(v);
        }
    }

    private static String firstString(Object target, String... getters) {
        for (String g : getters) {
            Object v = tryInvoke(target, g);
            String s = asString(v);
            if (StrUtil.isNotBlank(s)) {
                return s;
            }
        }

        return null;
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v).trim();
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
}
