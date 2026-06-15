/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.OspSubscriptionEnricher
 *  com.ocxworker.service.UsageCostService
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.model.RegionSubscription
 *  com.oracle.bmc.identity.model.Tenancy
 *  com.oracle.bmc.identity.requests.GetTenancyRequest
 *  com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.usageapi.UsageapiClient
 *  com.oracle.bmc.usageapi.model.Dimension
 *  com.oracle.bmc.usageapi.model.Filter
 *  com.oracle.bmc.usageapi.model.Filter$Operator
 *  com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails
 *  com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails$Builder
 *  com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails$Granularity
 *  com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails$QueryType
 *  com.oracle.bmc.usageapi.model.UsageSummary
 *  com.oracle.bmc.usageapi.requests.RequestSummarizedUsagesRequest
 *  com.oracle.bmc.usageapi.requests.RequestSummarizedUsagesRequest$Builder
 *  com.oracle.bmc.usageapi.responses.RequestSummarizedUsagesResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciClientService;
import com.ocxworker.service.OspSubscriptionEnricher;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.RegionSubscription;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.usageapi.UsageapiClient;
import com.oracle.bmc.usageapi.model.Dimension;
import com.oracle.bmc.usageapi.model.Filter;
import com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails;
import com.oracle.bmc.usageapi.model.UsageSummary;
import com.oracle.bmc.usageapi.requests.RequestSummarizedUsagesRequest;
import com.oracle.bmc.usageapi.responses.RequestSummarizedUsagesResponse;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
public class UsageCostService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UsageCostService.class);
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private static final int MAX_USAGE_PERIOD_DAYS = 90;
    @Resource
    private OciUserMapper userMapper;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> fetchSubscriptionUsageCost(OciClientService oci, String tenancyId, List<String> subscriptionIds, String usageStartIso, String fallbackRegion) {
        ArrayList<String> failureNotes;
        ArrayList<String> attempted;
        LinkedHashMap<String, Object> out;
        out = new LinkedHashMap<String, Object>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("subscriptionIdUsed", null);
        out.put("attemptedSubscriptionIds", List.of());
        out.put("timeUsageStarted", null);
        out.put("timeUsageEnded", null);
        out.put("summary", null);
        out.put("byService", new ArrayList());
        if (StrUtil.isBlank((CharSequence)tenancyId)) {
            out.put("reason", "\u7f3a\u5c11 tenancy OCID");
            return out;
        }
        List candidates = UsageCostService.dedupeOcidCandidates(subscriptionIds);
        if (candidates.isEmpty()) {
            out.put("reason", "\u7f3a\u5c11\u8ba2\u9605 OCID\uff0c\u65e0\u6cd5\u6309\u8ba2\u9605\u67e5\u8be2 Usage \u6d88\u8d39");
            return out;
        }
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        LocalDate startDay = UsageCostService.parseUsageStartDay((String)usageStartIso, (LocalDate)todayUtc);
        LocalDate endDay = todayUtc.plusDays(1L);
        Date timeStart = Date.from(startDay.atStartOfDay(ZoneOffset.UTC).toInstant());
        Date timeEnd = Date.from(endDay.atStartOfDay(ZoneOffset.UTC).toInstant());
        out.put("timeUsageStarted", timeStart.toInstant().toString());
        out.put("timeUsageEnded", timeEnd.toInstant().toString());
        UsageapiClient client = UsageapiClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());
        String usageRegion = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)oci.getIdentityClient(), (String)tenancyId, (String)fallbackRegion);
        try {
            client.setRegion(Region.fromRegionId((String)usageRegion));
        }
        catch (Exception e) {
            client.setRegion(Region.US_ASHBURN_1);
        }
        attempted = new ArrayList<String>();
        failureNotes = new ArrayList<String>();
        for (String subId : candidates) {
            attempted.add(subId);
            try {
                List totalRows = UsageCostService.queryCost((UsageapiClient)client, (String)tenancyId, (Date)timeStart, (Date)timeEnd, (RequestSummarizedUsagesDetails.Granularity)RequestSummarizedUsagesDetails.Granularity.Monthly, List.of(), (boolean)true, (Filter)UsageCostService.subscriptionFilter((String)subId));
                List serviceRows = UsageCostService.queryCost((UsageapiClient)client, (String)tenancyId, (Date)timeStart, (Date)timeEnd, (RequestSummarizedUsagesDetails.Granularity)RequestSummarizedUsagesDetails.Granularity.Monthly, List.of("service"), (boolean)true, (Filter)UsageCostService.subscriptionFilter((String)subId));
                BigDecimal total = UsageCostService.sumComputedAmount((List)totalRows);
                String currency = UsageCostService.pickCurrency((List[])new List[]{totalRows, serviceRows});
                LinkedHashMap<String, String> summary = new LinkedHashMap<String, String>();
                summary.put("totalConsumed", UsageCostService.toPlain((BigDecimal)total));
                summary.put("currency", currency);
                summary.put("totalConsumedLabel", UsageCostService.formatCostLabel((BigDecimal)total, (String)currency));
                out.put("summary", summary);
                out.put("byService", UsageCostService.aggregateByService((List)serviceRows, (String)currency));
                out.put("available", Boolean.TRUE);
                out.put("subscriptionIdUsed", subId);
                out.put("attemptedSubscriptionIds", attempted);
                out.put("reason", null);
                LinkedHashMap<String, Object> linkedHashMap = out;
                return linkedHashMap;
            }
            catch (BmcException e) {
                log.warn("Usage API subscription {} failed: {}", (Object)subId, (Object)e.getMessage());
                failureNotes.add(UsageCostService.shortOcid((String)subId) + ": " + UsageCostService.formatUsageApiError((Exception)((Object)e)));
            }
            catch (Exception e) {
                log.warn("Usage API subscription {} failed: {}", (Object)subId, (Object)e.getMessage());
                failureNotes.add(UsageCostService.shortOcid((String)subId) + ": " + UsageCostService.formatUsageApiError((Exception)e));
            }
        }
        out.put("attemptedSubscriptionIds", attempted);
        try {
            client.close();
        } catch (Exception ignored) {}
        out.put("reason", failureNotes.isEmpty() ? "Usage API \u672a\u8fd4\u56de\u8be5\u8ba2\u9605\u6d88\u8d39\u6570\u636e" : "\u5df2\u5c1d\u8bd5 " + attempted.size() + " \u4e2a\u8ba2\u9605 OCID\uff1b" + String.join((CharSequence)"\uff1b", failureNotes));
        return out;
    }

    private static Filter subscriptionFilter(String subscriptionId) {
        return Filter.builder().operator(Filter.Operator.And).dimensions(List.of(Dimension.builder().key("subscriptionId").value(subscriptionId.trim()).build())).build();
    }

    private static List<String> dedupeOcidCandidates(List<String> subscriptionIds) {
        if (subscriptionIds == null || subscriptionIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> ordered = new LinkedHashSet<String>();
        for (String id : subscriptionIds) {
            if (!StrUtil.isNotBlank((CharSequence)id) || !OspSubscriptionEnricher.isOciOcid((String)id)) continue;
            ordered.add(id.trim());
        }
        return new ArrayList<String>(ordered);
    }

    private static LocalDate parseUsageStartDay(String usageStartIso, LocalDate todayUtc) {
        LocalDate earliest = todayUtc.minusDays(90L);
        if (StrUtil.isBlank((CharSequence)usageStartIso)) {
            return earliest;
        }
        try {
            LocalDate start = Instant.parse(usageStartIso.trim()).atZone(ZoneOffset.UTC).toLocalDate();
            if (start.isAfter(todayUtc)) {
                return todayUtc;
            }
            if (start.isBefore(earliest)) {
                return earliest;
            }
            return start;
        }
        catch (Exception e) {
            return earliest;
        }
    }

    private static String formatCostLabel(BigDecimal amount, String currency) {
        if (amount == null) {
            return null;
        }
        String cur = StrUtil.isNotBlank((CharSequence)currency) ? " " + currency.trim() : "";
        return UsageCostService.toPlain((BigDecimal)amount) + cur;
    }

    private static String shortOcid(String ocid) {
        if (StrUtil.isBlank((CharSequence)ocid) || ocid.length() <= 48) {
            return ocid;
        }
        return ocid.substring(0, 22) + "\u2026" + ocid.substring(ocid.length() - 10);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> fetchCostAnalysis(String tenantId, int days) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        int periodDays = Math.max(1, Math.min(90, days));
        String tenancyId = user.getOciTenantId();
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        Date timeStart = Date.from(todayUtc.minusDays(periodDays).atStartOfDay(ZoneOffset.UTC).toInstant());
        Date timeEnd = Date.from(todayUtc.plusDays(1L).atStartOfDay(ZoneOffset.UTC).toInstant());
        LinkedHashMap<String, Object> usage = new LinkedHashMap<String, Object>();
        usage.put("available", Boolean.FALSE);
        usage.put("periodDays", periodDays);
        usage.put("timeUsageStarted", timeStart.toInstant().toString());
        usage.put("timeUsageEnded", timeEnd.toInstant().toString());
        usage.put("summary", null);
        usage.put("byService", new ArrayList());
        usage.put("byDay", new ArrayList());
        SysUserDTO dto = SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build();
        try (OciClientService oci = new OciClientService(dto);){
            String usageRegion = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)oci.getIdentityClient(), (String)tenancyId, (String)user.getOciRegion());
            UsageapiClient client = UsageapiClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());
            try {
                client.setRegion(Region.fromRegionId((String)usageRegion));
            }
            catch (Exception e) {
                client.setRegion(Region.US_ASHBURN_1);
            }
            try {
                List totalRows = UsageCostService.queryCost((UsageapiClient)client, (String)tenancyId, (Date)timeStart, (Date)timeEnd, (RequestSummarizedUsagesDetails.Granularity)RequestSummarizedUsagesDetails.Granularity.Monthly, List.of(), (boolean)true, null);
                List serviceRows = UsageCostService.queryCost((UsageapiClient)client, (String)tenancyId, (Date)timeStart, (Date)timeEnd, (RequestSummarizedUsagesDetails.Granularity)RequestSummarizedUsagesDetails.Granularity.Monthly, List.of("service"), (boolean)true, null);
                List dailyRows = UsageCostService.queryCost((UsageapiClient)client, (String)tenancyId, (Date)timeStart, (Date)timeEnd, (RequestSummarizedUsagesDetails.Granularity)RequestSummarizedUsagesDetails.Granularity.Daily, List.of(), (boolean)false, null);
                LinkedHashMap<String, String> summary = new LinkedHashMap<String, String>();
                BigDecimal total = UsageCostService.sumComputedAmount((List)totalRows);
                String currency = UsageCostService.pickCurrency((List[])new List[]{totalRows, serviceRows, dailyRows});
                summary.put("totalCost", UsageCostService.toPlain((BigDecimal)total));
                summary.put("currency", currency);
                usage.put("summary", summary);
                List byService = UsageCostService.aggregateByService((List)serviceRows, (String)currency);
                usage.put("byService", byService);
                List byDay = UsageCostService.aggregateByDay((List)dailyRows, (String)currency);
                usage.put("byDay", byDay);
                usage.put("available", Boolean.TRUE);
                usage.put("reason", null);
            }
            catch (Exception e) {
                log.warn("Usage API cost query failed for {}: {}", (Object)tenantId, (Object)e.getMessage());
                usage.put("reason", UsageCostService.formatUsageApiError((Exception)e));
            }
            finally {
                client.close();
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            usage.put("reason", "\u521d\u59cb\u5316 Usage API \u5ba2\u6237\u7aef\u5931\u8d25\uff1a" + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        return usage;
    }

    private static List<UsageSummary> queryCost(UsageapiClient client, String tenancyId, Date timeStart, Date timeEnd, RequestSummarizedUsagesDetails.Granularity granularity, List<String> groupBy, boolean aggregateByTime, Filter filter) throws Exception {
        RequestSummarizedUsagesResponse resp;
        RequestSummarizedUsagesDetails.Builder detailsB = RequestSummarizedUsagesDetails.builder().tenantId(tenancyId).timeUsageStarted(timeStart).timeUsageEnded(timeEnd).granularity(granularity).isAggregateByTime(Boolean.valueOf(aggregateByTime)).queryType(RequestSummarizedUsagesDetails.QueryType.Cost).groupBy(groupBy == null || groupBy.isEmpty() ? null : groupBy);
        if (filter != null) {
            detailsB.filter(filter);
        }
        RequestSummarizedUsagesDetails details = detailsB.build();
        ArrayList<UsageSummary> items = new ArrayList<UsageSummary>();
        String page = null;
        do {
            RequestSummarizedUsagesRequest.Builder req = RequestSummarizedUsagesRequest.builder().requestSummarizedUsagesDetails(details).limit(Integer.valueOf(1000));
            if (page != null) {
                req.page(page);
            }
            if ((resp = client.requestSummarizedUsages(req.build())).getUsageAggregation() == null || resp.getUsageAggregation().getItems() == null) continue;
            items.addAll(resp.getUsageAggregation().getItems());
        } while ((page = resp.getOpcNextPage()) != null && !page.isBlank());
        return items;
    }

    private static List<Map<String, Object>> aggregateByService(List<UsageSummary> rows, String defaultCurrency) {
        LinkedHashMap<String, BigDecimal> costByService = new LinkedHashMap<String, BigDecimal>();
        for (UsageSummary u : rows) {
            if (u == null || Boolean.TRUE.equals(u.getIsForecast())) continue;
            String svc = StrUtil.blankToDefault((CharSequence)u.getService(), (String)"\uff08\u672a\u5206\u7c7b\uff09");
            costByService.merge(svc, UsageCostService.nz((BigDecimal)u.getComputedAmount()), BigDecimal::add);
        }
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map.Entry e : costByService.entrySet()) {
            LinkedHashMap row = new LinkedHashMap();
            row.put("service", e.getKey());
            row.put("cost", UsageCostService.toPlain((BigDecimal)((BigDecimal)e.getValue())));
            row.put("currency", defaultCurrency);
            list.add(row);
        }
        list.sort(Comparator.comparing(m -> new BigDecimal(String.valueOf(m.get("cost")))).reversed());
        return list;
    }

    private static List<Map<String, Object>> aggregateByDay(List<UsageSummary> rows, String defaultCurrency) {
        LinkedHashMap<String, BigDecimal> costByDay = new LinkedHashMap<String, BigDecimal>();
        for (UsageSummary u : rows) {
            if (u == null || Boolean.TRUE.equals(u.getIsForecast()) || u.getTimeUsageStarted() == null) continue;
            String day = DAY_FMT.format(u.getTimeUsageStarted().toInstant());
            costByDay.merge(day, UsageCostService.nz((BigDecimal)u.getComputedAmount()), BigDecimal::add);
        }
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map.Entry e : costByDay.entrySet()) {
            LinkedHashMap row = new LinkedHashMap();
            row.put("date", e.getKey());
            row.put("cost", UsageCostService.toPlain((BigDecimal)((BigDecimal)e.getValue())));
            row.put("currency", defaultCurrency);
            list.add(row);
        }
        list.sort(Comparator.comparing(m -> String.valueOf(m.get("date"))));
        return list;
    }

    private static BigDecimal sumComputedAmount(List<UsageSummary> rows) {
        BigDecimal sum = BigDecimal.ZERO;
        for (UsageSummary u : rows) {
            if (u == null || Boolean.TRUE.equals(u.getIsForecast())) continue;
            sum = sum.add(UsageCostService.nz((BigDecimal)u.getComputedAmount()));
        }
        return sum;
    }

    @SafeVarargs
    private static String pickCurrency(List<UsageSummary> ... lists) {
        for (List<UsageSummary> rows : lists) {
            if (rows == null) continue;
            for (UsageSummary u : rows) {
                if (u == null || !StrUtil.isNotBlank((CharSequence)u.getCurrency())) continue;
                return u.getCurrency();
            }
        }
        return null;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String toPlain(BigDecimal v) {
        if (v == null) {
            return "0";
        }
        return v.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    static String resolveTenancyHomeRegionName(IdentityClient identityClient, String tenancyId, String fallback) {
        if (identityClient == null || StrUtil.isBlank((CharSequence)tenancyId)) {
            return StrUtil.blankToDefault((CharSequence)fallback, (String)Region.US_ASHBURN_1.getRegionId());
        }
        try {
            String homeKey;
            Tenancy tenancy = identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
            String string = homeKey = tenancy == null ? null : tenancy.getHomeRegionKey();
            if (StrUtil.isBlank((CharSequence)homeKey)) {
                return StrUtil.blankToDefault((CharSequence)fallback, (String)Region.US_ASHBURN_1.getRegionId());
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
        return StrUtil.blankToDefault((CharSequence)fallback, (String)Region.US_ASHBURN_1.getRegionId());
    }

    static String formatUsageApiError(Exception e) {
        String msg;
        String string = msg = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
        if (e instanceof BmcException) {
            BmcException bmc = (BmcException)((Object)e);
            int code = bmc.getStatusCode();
            if (code == 404) {
                return "Usage API \u65e0\u6570\u636e\uff08404\uff09";
            }
            if (code == 401 || code == 403 || msg.contains("NotAuthorized")) {
                return "Usage \u6743\u9650\u4e0d\u8db3\uff08\u9700 usage-report / \u6210\u672c\u5206\u6790\u8bfb\u6743\u9650\uff09\uff1a" + msg;
            }
            return "Usage API \u5931\u8d25\uff08HTTP " + code + "\uff09\uff1a" + msg;
        }
        if (msg.contains("InvalidParameter") && msg.contains("precision")) {
            return "\u6210\u672c\u5206\u6790\u8bf7\u6c42\u65f6\u95f4\u683c\u5f0f\u4e0d\u7b26\u5408 OCI \u8981\u6c42\uff08\u5df2\u6309 UTC \u6574\u65e5\u5bf9\u9f50\uff0c\u82e5\u4ecd\u5931\u8d25\u8bf7\u53cd\u9988\u65e5\u5fd7\uff09";
        }
        if (msg.contains("NotAuthorized") || msg.contains("403")) {
            return "\u6210\u672c\u5206\u6790\u6743\u9650\u4e0d\u8db3\uff08\u9700 usage-report \u76f8\u5173\u8bfb\u6743\u9650\uff09\uff1a" + msg;
        }
        return "\u6210\u672c\u5206\u6790\u67e5\u8be2\u5931\u8d25\uff1a" + msg;
    }
}

