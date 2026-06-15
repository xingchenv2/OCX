/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.service.OrganizationSubscriptionService
 *  com.ociworker.service.OspSubscriptionEnricher
 *  com.ociworker.service.UsageCostService
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.onesubscription.SubscribedServiceClient
 *  com.oracle.bmc.onesubscription.SubscribedServiceClient$Builder
 *  com.oracle.bmc.onesubscription.model.RateCardProduct
 *  com.oracle.bmc.onesubscription.model.SubscribedServiceSummary
 *  com.oracle.bmc.onesubscription.requests.ListSubscribedServicesRequest
 *  com.oracle.bmc.onesubscription.requests.ListSubscribedServicesRequest$Builder
 *  com.oracle.bmc.onesubscription.responses.ListSubscribedServicesResponse
 *  com.oracle.bmc.tenantmanagercontrolplane.SubscriptionClient
 *  com.oracle.bmc.tenantmanagercontrolplane.SubscriptionClient$Builder
 *  com.oracle.bmc.tenantmanagercontrolplane.model.AssignedSubscriptionCollection
 *  com.oracle.bmc.tenantmanagercontrolplane.model.AssignedSubscriptionSummary
 *  com.oracle.bmc.tenantmanagercontrolplane.requests.ListAssignedSubscriptionsRequest
 *  com.oracle.bmc.tenantmanagercontrolplane.requests.ListAssignedSubscriptionsRequest$Builder
 *  com.oracle.bmc.tenantmanagercontrolplane.responses.ListAssignedSubscriptionsResponse
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.util.StrUtil;
import com.ociworker.service.OciClientService;
import com.ociworker.service.OciProxyConfigService;
import com.ociworker.service.OspSubscriptionEnricher;
import com.ociworker.service.UsageCostService;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.onesubscription.SubscribedServiceClient;
import com.oracle.bmc.onesubscription.model.RateCardProduct;
import com.oracle.bmc.onesubscription.model.SubscribedServiceSummary;
import com.oracle.bmc.onesubscription.requests.ListSubscribedServicesRequest;
import com.oracle.bmc.onesubscription.responses.ListSubscribedServicesResponse;
import com.oracle.bmc.tenantmanagercontrolplane.SubscriptionClient;
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

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class OrganizationSubscriptionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OrganizationSubscriptionService.class);
    private static final int PAGE_LIMIT = 100;

    public List<Map<String, Object>> listAssignedSubscriptionsOnly(OciClientService oci, String tenancyId, String fallbackRegion) {
        String region = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)oci.getIdentityClient(), (String)tenancyId, (String)fallbackRegion);
        return this.listAssignedSubscriptionsOnlyInRegion(oci, tenancyId, region);
    }

    private List<Map<String, Object>> listAssignedSubscriptionsOnlyInRegion(OciClientService oci, String tenancyId, String homeRegionName) {
        List list;
        block9: {
            if (StrUtil.isBlank((CharSequence)tenancyId)) {
                return List.of();
            }
            String region = StrUtil.isNotBlank((CharSequence)homeRegionName) ? homeRegionName.trim() : Region.US_ASHBURN_1.getRegionId();
            SubscriptionClient subClient = OrganizationSubscriptionService.buildSubscriptionClient((OciClientService)oci);
            try {
                OrganizationSubscriptionService.setRegion((Object)subClient, (String)region);
                list = OrganizationSubscriptionService.listAssignedSubscriptions((SubscriptionClient)subClient, (String)tenancyId);
                if (subClient == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (subClient != null) {
                        try {
                            subClient.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    log.warn("listAssignedSubscriptionsOnly failed: {}", (Object)e.getMessage());
                    return List.of();
                }
            }
            subClient.close();
        }
        return list;
    }

    public Map<String, Object> fetchOrganizationSubscription(OciClientService oci, String tenancyId, String fallbackRegion, String ospSubscriptionRef, List<String> extraSubscriptionOcids) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("assignedSubscriptions", new ArrayList());
        out.put("subscribedServices", new ArrayList());
        if (StrUtil.isBlank((CharSequence)tenancyId)) {
            out.put("reason", "\u7f3a\u5c11 tenancy OCID");
            return out;
        }
        String region = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)oci.getIdentityClient(), (String)tenancyId, (String)fallbackRegion);
        ArrayList assignedRows = new ArrayList();
        ArrayList serviceRows = new ArrayList();
        ArrayList<Object> notes = new ArrayList<Object>();
        try (SubscriptionClient subClient = OrganizationSubscriptionService.buildSubscriptionClient((OciClientService)oci);){
            OrganizationSubscriptionService.setRegion((Object)subClient, (String)region);
            assignedRows.addAll(OrganizationSubscriptionService.listAssignedSubscriptions((SubscriptionClient)subClient, (String)tenancyId));
        }
        catch (BmcException e) {
            log.warn("listAssignedSubscriptions failed: {}", (Object)e.getMessage());
            notes.add(OrganizationSubscriptionService.formatOrgError((String)"\u8ba2\u8d2d\u5206\u914d", (BmcException)e));
        }
        catch (Exception e) {
            log.warn("listAssignedSubscriptions failed: {}", (Object)e.getMessage());
            notes.add("\u8ba2\u8d2d\u5206\u914d\u67e5\u8be2\u5931\u8d25\uff1a" + e.getMessage());
        }
        Set subscriptionIdsToQuery = OrganizationSubscriptionService.resolveSubscriptionOcidCandidates(assignedRows, (String)ospSubscriptionRef, extraSubscriptionOcids);
        if (subscriptionIdsToQuery.isEmpty()) {
            out.put("assignedSubscriptions", assignedRows);
            out.put("subscribedServices", serviceRows);
            notes.add(OrganizationSubscriptionService.buildNoOcidReason((String)ospSubscriptionRef, assignedRows));
            out.put("reason", String.join((CharSequence)"\uff1b", notes));
            return out;
        }
        try (SubscribedServiceClient svcClient = OrganizationSubscriptionService.buildSubscribedServiceClient((OciClientService)oci);){
            OrganizationSubscriptionService.setRegion((Object)svcClient, (String)region);
            for (String subId : subscriptionIdsToQuery) {
                try {
                    serviceRows.addAll(OrganizationSubscriptionService.listSubscribedServices((SubscribedServiceClient)svcClient, (String)tenancyId, (String)subId));
                }
                catch (BmcException e) {
                    log.warn("listSubscribedServices {} failed: {}", (Object)subId, (Object)e.getMessage());
                    LinkedHashMap<String, String> err = new LinkedHashMap<String, String>();
                    err.put("subscriptionId", subId);
                    err.put("error", OrganizationSubscriptionService.formatOrgError((String)"\u5b50\u670d\u52a1\u989d\u5ea6", (BmcException)e));
                    serviceRows.add(err);
                }
            }
        }
        catch (Exception e) {
            log.warn("SubscribedService client failed: {}", (Object)e.getMessage());
            out.put("assignedSubscriptions", assignedRows);
            out.put("reason", "\u5b50\u670d\u52a1\u989d\u5ea6\u67e5\u8be2\u5931\u8d25\uff1a" + e.getMessage());
            return out;
        }
        out.put("available", Boolean.TRUE);
        out.put("assignedSubscriptions", assignedRows);
        out.put("subscribedServices", serviceRows);
        if (assignedRows.isEmpty() && serviceRows.isEmpty()) {
            out.put("available", Boolean.FALSE);
            notes.add("\u8ba2\u8d2d\u4e0e\u5b50\u670d\u52a1\u63a5\u53e3\u5747\u65e0\u6570\u636e");
        }
        if (!notes.isEmpty()) {
            out.put("reason", String.join((CharSequence)"\uff1b", notes));
        }
        return out;
    }

    static Set<String> resolveSubscriptionOcidCandidates(List<Map<String, Object>> assignedRows, String ospSubscriptionRef, List<String> extraSubscriptionOcids) {
        LinkedHashSet<String> ids = new LinkedHashSet<String>();
        if (extraSubscriptionOcids != null) {
            for (String string : extraSubscriptionOcids) {
                if (!OspSubscriptionEnricher.isOciOcid((String)string) || OspSubscriptionEnricher.isOrganizationsSubscriptionOcid((String)string)) continue;
                ids.add(string.trim());
            }
        }
        if (assignedRows != null) {
            for (Map map : assignedRows) {
                String id = map.get("id") == null ? null : String.valueOf(map.get("id")).trim();
                if (!OspSubscriptionEnricher.isOciOcid(id)) continue;
                ids.add(id);
            }
        }
        if (OspSubscriptionEnricher.isOciOcid((String)ospSubscriptionRef)) {
            ids.add(ospSubscriptionRef.trim());
        } else if (StrUtil.isNotBlank((CharSequence)ospSubscriptionRef) && assignedRows != null) {
            String ref = ospSubscriptionRef.trim();
            for (Map<String, Object> row : assignedRows) {
                String id;
                String num = row.get("subscriptionNumber") == null ? null : String.valueOf(row.get("subscriptionNumber")).trim();
                if (!ref.equals(num) || !OspSubscriptionEnricher.isOciOcid(id = row.get("id") == null ? null : String.valueOf(row.get("id")).trim())) continue;
                ids.add(id);
            }
        }
        return ids;
    }

    private static String buildNoOcidReason(String ospRef, List<Map<String, Object>> assignedRows) {
        if (assignedRows == null || assignedRows.isEmpty()) {
            return "\u65e0 Assigned Subscription \u8bb0\u5f55" + (String)(StrUtil.isNotBlank((CharSequence)ospRef) ? "\uff08OSP \u5f15\u7528\uff1a" + ospRef + "\uff09" : "");
        }
        if (StrUtil.isNotBlank((CharSequence)ospRef) && !OspSubscriptionEnricher.isOciOcid((String)ospRef)) {
            return "OSP \u8ba2\u9605\u5f15\u7528\u300c" + ospRef + "\u300d\u4e3a\u7f16\u53f7\u975e OCID\uff1bSubscribed Service / Rewards \u9700 ocid1.*\uff0c\u8bf7\u5bf9\u7167\u4e0b\u65b9 Assigned \u8868\u4e2d\u7684\u8ba2\u9605 ID";
        }
        return "\u672a\u89e3\u6790\u5230\u53ef\u7528\u4e8e Subscribed Service \u7684\u8ba2\u9605 OCID";
    }

    private static SubscriptionClient buildSubscriptionClient(OciClientService oci) {
        SubscriptionClient.Builder b = SubscriptionClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (SubscriptionClient.Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }
        return b.build((AbstractAuthenticationDetailsProvider)oci.getProvider());
    }

    private static SubscribedServiceClient buildSubscribedServiceClient(OciClientService oci) {
        SubscribedServiceClient.Builder b = SubscribedServiceClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            b = (SubscribedServiceClient.Builder)b.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }
        return b.build((AbstractAuthenticationDetailsProvider)oci.getProvider());
    }

    private static void setRegion(Object client, String regionId) {
        block6: {
            try {
                if (client instanceof SubscriptionClient) {
                    SubscriptionClient c = (SubscriptionClient)client;
                    c.setRegion(Region.fromRegionId((String)regionId));
                } else if (client instanceof SubscribedServiceClient) {
                    SubscribedServiceClient c = (SubscribedServiceClient)client;
                    c.setRegion(Region.fromRegionId((String)regionId));
                }
            }
            catch (Exception e) {
                if (client instanceof SubscriptionClient) {
                    SubscriptionClient c = (SubscriptionClient)client;
                    c.setRegion(Region.US_ASHBURN_1);
                }
                if (!(client instanceof SubscribedServiceClient)) break block6;
                SubscribedServiceClient c = (SubscribedServiceClient)client;
                c.setRegion(Region.US_ASHBURN_1);
            }
        }
    }

    private static List<Map<String, Object>> listAssignedSubscriptions(SubscriptionClient client, String tenancyId) {
        ListAssignedSubscriptionsResponse resp;
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String page = null;
        do {
            AssignedSubscriptionCollection col;
            ListAssignedSubscriptionsRequest.Builder req = ListAssignedSubscriptionsRequest.builder().compartmentId(tenancyId).limit(Integer.valueOf(100));
            if (StrUtil.isNotBlank(page)) {
                req.page(page);
            }
            AssignedSubscriptionCollection assignedSubscriptionCollection = col = (resp = client.listAssignedSubscriptions(req.build())) == null ? null : resp.getAssignedSubscriptionCollection();
            if (col == null || col.getItems() == null) break;
            for (AssignedSubscriptionSummary item : col.getItems()) {
                if (item == null) continue;
                rows.add(OrganizationSubscriptionService.mapAssignedSubscription((AssignedSubscriptionSummary)item));
            }
        } while (StrUtil.isNotBlank((CharSequence)(page = resp.getOpcNextPage())));
        return rows;
    }

    private static Map<String, Object> mapAssignedSubscription(AssignedSubscriptionSummary item) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", item.getId());
        m.put("compartmentId", item.getCompartmentId());
        m.put("serviceName", item.getServiceName());
        m.put("timeCreated", OrganizationSubscriptionService.formatInstant((Date)item.getTimeCreated()));
        m.put("timeUpdated", OrganizationSubscriptionService.formatInstant((Date)item.getTimeUpdated()));
        m.put("entityVersion", OrganizationSubscriptionService.enumValue((Object)OrganizationSubscriptionService.tryInvoke((Object)item, (String)"getEntityVersion")));
        String lifecycle = OrganizationSubscriptionService.firstString((Object)item, (String[])new String[]{"getLifecycleState", "getLifecycleStateDetails"});
        m.put("lifecycleState", lifecycle);
        m.put("subscriptionNumber", OrganizationSubscriptionService.asString((Object)OrganizationSubscriptionService.tryInvoke((Object)item, (String)"getSubscriptionNumber")));
        m.put("currencyCode", OrganizationSubscriptionService.asString((Object)OrganizationSubscriptionService.tryInvoke((Object)item, (String)"getCurrencyCode")));
        return m;
    }

    private static List<Map<String, Object>> listSubscribedServices(SubscribedServiceClient client, String tenancyId, String subscriptionId) {
        ListSubscribedServicesResponse resp;
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        String page = null;
        do {
            List items;
            ListSubscribedServicesRequest.Builder req = ListSubscribedServicesRequest.builder().compartmentId(tenancyId).subscriptionId(subscriptionId).limit(Integer.valueOf(100));
            if (StrUtil.isNotBlank(page)) {
                req.page(page);
            }
            List list = items = (resp = client.listSubscribedServices(req.build())) == null ? null : resp.getItems();
            if (items == null || items.isEmpty()) break;
            for (SubscribedServiceSummary item : items) {
                if (item == null) continue;
                rows.add(OrganizationSubscriptionService.mapSubscribedService((SubscribedServiceSummary)item, (String)subscriptionId));
            }
        } while (StrUtil.isNotBlank((CharSequence)(page = resp.getOpcNextPage())));
        return rows;
    }

    private static Map<String, Object> mapSubscribedService(SubscribedServiceSummary item, String subscriptionId) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("subscriptionId", subscriptionId);
        m.put("orderNumber", item.getOrderNumber());
        m.put("status", item.getStatus());
        m.put("fundedAllocationValue", item.getFundedAllocationValue());
        m.put("availableAmount", item.getAvailableAmount());
        m.put("creditPercentage", item.getCreditPercentage());
        m.put("timeStart", OrganizationSubscriptionService.formatInstant((Date)item.getTimeStart()));
        m.put("timeEnd", OrganizationSubscriptionService.formatInstant((Date)item.getTimeEnd()));
        m.put("timeCreated", OrganizationSubscriptionService.formatInstant((Date)item.getTimeCreated()));
        m.put("timeUpdated", OrganizationSubscriptionService.formatInstant((Date)item.getTimeUpdated()));
        RateCardProduct product = item.getProduct();
        if (product != null) {
            Object name = OrganizationSubscriptionService.tryInvoke((Object)product, (String)"getName");
            if (name == null) {
                name = OrganizationSubscriptionService.tryInvoke((Object)product, (String)"getProductName");
            }
            if (name == null) {
                name = OrganizationSubscriptionService.tryInvoke((Object)product, (String)"getDisplayName");
            }
            m.put("productName", name == null ? null : String.valueOf(name));
        }
        return m;
    }

    private static String formatOrgError(String apiLabel, BmcException e) {
        String msg = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
        int code = e.getStatusCode();
        if (code == 404) {
            return apiLabel + " \u65e0\u6570\u636e\uff08404\uff09";
        }
        if (code == 401 || code == 403 || msg.contains("NotAuthorized")) {
            return apiLabel + " \u6743\u9650\u4e0d\u8db3\uff08\u9700 inspect/read \u8ba2\u8d2d\u4e0e\u5b50\u670d\u52a1\u76f8\u5173\u6743\u9650\uff09\uff1a" + msg;
        }
        return apiLabel + " \u67e5\u8be2\u5931\u8d25\uff08HTTP " + code + "\uff09\uff1a" + msg;
    }

    private static String formatInstant(Date d) {
        return d == null ? null : d.toInstant().toString();
    }

    private static String enumValue(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Enum) {
            Enum e = (Enum)v;
            Object val = OrganizationSubscriptionService.tryInvoke((Object)e, (String)"getValue");
            return val != null ? String.valueOf(val) : e.name();
        }
        return String.valueOf(v);
    }

    private static String firstString(Object target, String ... getters) {
        for (String g : getters) {
            Object v = OrganizationSubscriptionService.tryInvoke((Object)target, (String)g);
            String s = OrganizationSubscriptionService.asString((Object)v);
            if (!StrUtil.isNotBlank((CharSequence)s)) continue;
            return s;
        }
        return null;
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v).trim();
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
}

