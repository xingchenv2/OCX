package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.Region;
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
import com.oracle.bmc.usageapi.model.Filter.Operator;
import com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails.Builder;
import com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails.Granularity;
import com.oracle.bmc.usageapi.model.RequestSummarizedUsagesDetails.QueryType;
import com.oracle.bmc.usageapi.requests.RequestSummarizedUsagesRequest;
import com.oracle.bmc.usageapi.responses.RequestSummarizedUsagesResponse;
import jakarta.annotation.Resource;
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
import java.util.Set;
import java.util.Map.Entry;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UsageCostService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UsageCostService.class);
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private static final int MAX_USAGE_PERIOD_DAYS = 90;
    @Resource
    private OciUserMapper userMapper;

    public Map<String, Object> fetchSubscriptionUsageCost(
        OciClientService oci, String tenancyId, List<String> subscriptionIds, String usageStartIso, String fallbackRegion
    ) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("subscriptionIdUsed", null);
        out.put("attemptedSubscriptionIds", List.of());
        out.put("timeUsageStarted", null);
        out.put("timeUsageEnded", null);
        out.put("summary", null);
        out.put("byService", new ArrayList());
        if (StrUtil.isBlank(tenancyId)) {
            out.put("reason", "缺少 tenancy OCID");
            return out;
        } else {
            List<String> candidates = dedupeOcidCandidates(subscriptionIds);
            if (candidates.isEmpty()) {
                out.put("reason", "缺少订阅 OCID，无法按订阅查询 Usage 消费");
                return out;
            } else {
                LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
                LocalDate startDay = parseUsageStartDay(usageStartIso, todayUtc);
                LocalDate endDay = todayUtc.plusDays(1L);
                Date timeStart = Date.from(startDay.atStartOfDay(ZoneOffset.UTC).toInstant());
                Date timeEnd = Date.from(endDay.atStartOfDay(ZoneOffset.UTC).toInstant());
                out.put("timeUsageStarted", timeStart.toInstant().toString());
                out.put("timeUsageEnded", timeEnd.toInstant().toString());
                UsageapiClient client = UsageapiClient.builder().build(oci.getProvider());

                try {
                    String usageRegion = resolveTenancyHomeRegionName(oci.getIdentityClient(), tenancyId, fallbackRegion);

                    try {
                        client.setRegion(Region.fromRegionId(usageRegion));
                    } catch (Exception var30) {
                        client.setRegion(Region.US_ASHBURN_1);
                    }

                    List<String> attempted = new ArrayList<>();
                    List<String> failureNotes = new ArrayList<>();

                    for (String subId : candidates) {
                        attempted.add(subId);

                        try {
                            List<UsageSummary> totalRows = queryCost(
                                client, tenancyId, timeStart, timeEnd, Granularity.Monthly, List.of(), true, subscriptionFilter(subId)
                            );
                            List<UsageSummary> serviceRows = queryCost(
                                client, tenancyId, timeStart, timeEnd, Granularity.Monthly, List.of("service"), true, subscriptionFilter(subId)
                            );
                            BigDecimal total = sumComputedAmount(totalRows);
                            String currency = pickCurrency(totalRows, serviceRows);
                            Map<String, Object> summary = new LinkedHashMap<>();
                            summary.put("totalConsumed", toPlain(total));
                            summary.put("currency", currency);
                            summary.put("totalConsumedLabel", formatCostLabel(total, currency));
                            out.put("summary", summary);
                            out.put("byService", aggregateByService(serviceRows, currency));
                            out.put("available", Boolean.TRUE);
                            out.put("subscriptionIdUsed", subId);
                            out.put("attemptedSubscriptionIds", attempted);
                            out.put("reason", null);
                            return out;
                        } catch (BmcException var31) {
                            log.warn("Usage API subscription {} failed: {}", subId, var31.getMessage());
                            failureNotes.add(shortOcid(subId) + ": " + formatUsageApiError(var31));
                        } catch (Exception var32) {
                            log.warn("Usage API subscription {} failed: {}", subId, var32.getMessage());
                            failureNotes.add(shortOcid(subId) + ": " + formatUsageApiError(var32));
                        }
                    }

                    out.put("attemptedSubscriptionIds", attempted);
                    out.put(
                        "reason", failureNotes.isEmpty() ? "Usage API 未返回该订阅消费数据" : "已尝试 " + attempted.size() + " 个订阅 OCID；" + String.join("；", failureNotes)
                    );
                    return out;
                } finally {
                    client.close();
                }
            }
        }
    }

    private static Filter subscriptionFilter(String subscriptionId) {
        return Filter.builder()
            .operator(Operator.And)
            .dimensions(List.of(Dimension.builder().key("subscriptionId").value(subscriptionId.trim()).build()))
            .build();
    }

    private static List<String> dedupeOcidCandidates(List<String> subscriptionIds) {
        if (subscriptionIds != null && !subscriptionIds.isEmpty()) {
            Set<String> ordered = new LinkedHashSet<>();

            for (String id : subscriptionIds) {
                if (StrUtil.isNotBlank(id) && OspSubscriptionEnricher.isOciOcid(id)) {
                    ordered.add(id.trim());
                }
            }

            return new ArrayList<>(ordered);
        } else {
            return List.of();
        }
    }

    private static LocalDate parseUsageStartDay(String usageStartIso, LocalDate todayUtc) {
        LocalDate earliest = todayUtc.minusDays(90L);
        if (StrUtil.isBlank(usageStartIso)) {
            return earliest;
        } else {
            try {
                LocalDate start = Instant.parse(usageStartIso.trim()).atZone(ZoneOffset.UTC).toLocalDate();
                if (start.isAfter(todayUtc)) {
                    return todayUtc;
                } else {
                    return start.isBefore(earliest) ? earliest : start;
                }
            } catch (Exception var4) {
                return earliest;
            }
        }
    }

    private static String formatCostLabel(BigDecimal amount, String currency) {
        if (amount == null) {
            return null;
        } else {
            String cur = StrUtil.isNotBlank(currency) ? " " + currency.trim() : "";
            return toPlain(amount) + cur;
        }
    }

    private static String shortOcid(String ocid) {
        return !StrUtil.isBlank(ocid) && ocid.length() > 48 ? ocid.substring(0, 22) + "…" + ocid.substring(ocid.length() - 10) : ocid;
    }

    public Map<String, Object> fetchCostAnalysis(String tenantId, int days) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            int periodDays = Math.max(1, Math.min(90, days));
            String tenancyId = user.getOciTenantId();
            LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
            Date timeStart = Date.from(todayUtc.minusDays((long)periodDays).atStartOfDay(ZoneOffset.UTC).toInstant());
            Date timeEnd = Date.from(todayUtc.plusDays(1L).atStartOfDay(ZoneOffset.UTC).toInstant());
            Map<String, Object> usage = new LinkedHashMap<>();
            usage.put("available", Boolean.FALSE);
            usage.put("periodDays", periodDays);
            usage.put("timeUsageStarted", timeStart.toInstant().toString());
            usage.put("timeUsageEnded", timeEnd.toInstant().toString());
            usage.put("summary", null);
            usage.put("byService", new ArrayList());
            usage.put("byDay", new ArrayList());
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

            try (OciClientService oci = new OciClientService(dto)) {
                String usageRegion = resolveTenancyHomeRegionName(oci.getIdentityClient(), tenancyId, user.getOciRegion());
                UsageapiClient client = UsageapiClient.builder().build(oci.getProvider());

                try {
                    client.setRegion(Region.fromRegionId(usageRegion));
                } catch (Exception var33) {
                    client.setRegion(Region.US_ASHBURN_1);
                }

                try {
                    List<UsageSummary> totalRows = queryCost(client, tenancyId, timeStart, timeEnd, Granularity.Monthly, List.of(), true, null);
                    List<UsageSummary> serviceRows = queryCost(client, tenancyId, timeStart, timeEnd, Granularity.Monthly, List.of("service"), true, null);
                    List<UsageSummary> dailyRows = queryCost(client, tenancyId, timeStart, timeEnd, Granularity.Daily, List.of(), false, null);
                    Map<String, Object> summary = new LinkedHashMap<>();
                    BigDecimal total = sumComputedAmount(totalRows);
                    String currency = pickCurrency(totalRows, serviceRows, dailyRows);
                    summary.put("totalCost", toPlain(total));
                    summary.put("currency", currency);
                    usage.put("summary", summary);
                    List<Map<String, Object>> byService = aggregateByService(serviceRows, currency);
                    usage.put("byService", byService);
                    List<Map<String, Object>> byDay = aggregateByDay(dailyRows, currency);
                    usage.put("byDay", byDay);
                    usage.put("available", Boolean.TRUE);
                    usage.put("reason", null);
                } catch (Exception var31) {
                    log.warn("Usage API cost query failed for {}: {}", tenantId, var31.getMessage());
                    usage.put("reason", formatUsageApiError(var31));
                } finally {
                    client.close();
                }
            } catch (OciException var35) {
                throw var35;
            } catch (Exception var36) {
                usage.put("reason", "初始化 Usage API 客户端失败：" + (var36.getMessage() == null ? "未知错误" : var36.getMessage()));
            }

            return usage;
        }
    }

    private static List<UsageSummary> queryCost(
        UsageapiClient client,
        String tenancyId,
        Date timeStart,
        Date timeEnd,
        Granularity granularity,
        List<String> groupBy,
        boolean aggregateByTime,
        Filter filter
    ) throws Exception {
        Builder detailsB = RequestSummarizedUsagesDetails.builder()
            .tenantId(tenancyId)
            .timeUsageStarted(timeStart)
            .timeUsageEnded(timeEnd)
            .granularity(granularity)
            .isAggregateByTime(aggregateByTime)
            .queryType(QueryType.Cost)
            .groupBy(groupBy != null && !groupBy.isEmpty() ? groupBy : null);
        if (filter != null) {
            detailsB.filter(filter);
        }

        RequestSummarizedUsagesDetails details = detailsB.build();
        List<UsageSummary> items = new ArrayList<>();
        String page = null;

        do {
            com.oracle.bmc.usageapi.requests.RequestSummarizedUsagesRequest.Builder req = RequestSummarizedUsagesRequest.builder()
                .requestSummarizedUsagesDetails(details)
                .limit(1000);
            if (page != null) {
                req.page(page);
            }

            RequestSummarizedUsagesResponse resp = client.requestSummarizedUsages(req.build());
            if (resp.getUsageAggregation() != null && resp.getUsageAggregation().getItems() != null) {
                items.addAll(resp.getUsageAggregation().getItems());
            }

            page = resp.getOpcNextPage();
        } while (page != null && !page.isBlank());

        return items;
    }

    private static List<Map<String, Object>> aggregateByService(List<UsageSummary> rows, String defaultCurrency) {
        Map<String, BigDecimal> costByService = new LinkedHashMap<>();

        for (UsageSummary u : rows) {
            if (u != null && !Boolean.TRUE.equals(u.getIsForecast())) {
                String svc = StrUtil.blankToDefault(u.getService(), "（未分类）");
                costByService.merge(svc, nz(u.getComputedAmount()), BigDecimal::add);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();

        for (Entry<String, BigDecimal> e : costByService.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("service", e.getKey());
            row.put("cost", toPlain(e.getValue()));
            row.put("currency", defaultCurrency);
            list.add(row);
        }

        list.sort(Comparator.<Map<String, Object>, BigDecimal>comparing(m -> new BigDecimal(String.valueOf(m.get("cost")))).reversed());
        return list;
    }

    private static List<Map<String, Object>> aggregateByDay(List<UsageSummary> rows, String defaultCurrency) {
        Map<String, BigDecimal> costByDay = new LinkedHashMap<>();

        for (UsageSummary u : rows) {
            if (u != null && !Boolean.TRUE.equals(u.getIsForecast()) && u.getTimeUsageStarted() != null) {
                String day = DAY_FMT.format(u.getTimeUsageStarted().toInstant());
                costByDay.merge(day, nz(u.getComputedAmount()), BigDecimal::add);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();

        for (Entry<String, BigDecimal> e : costByDay.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", e.getKey());
            row.put("cost", toPlain(e.getValue()));
            row.put("currency", defaultCurrency);
            list.add(row);
        }

        list.sort(Comparator.comparing(m -> String.valueOf(m.get("date"))));
        return list;
    }

    private static BigDecimal sumComputedAmount(List<UsageSummary> rows) {
        BigDecimal sum = BigDecimal.ZERO;

        for (UsageSummary u : rows) {
            if (u != null && !Boolean.TRUE.equals(u.getIsForecast())) {
                sum = sum.add(nz(u.getComputedAmount()));
            }
        }

        return sum;
    }

    @SafeVarargs
    private static String pickCurrency(List<UsageSummary>... lists) {
        for (List<UsageSummary> rows : lists) {
            if (rows != null) {
                for (UsageSummary u : rows) {
                    if (u != null && StrUtil.isNotBlank(u.getCurrency())) {
                        return u.getCurrency();
                    }
                }
            }
        }

        return null;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String toPlain(BigDecimal v) {
        return v == null ? "0" : v.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    static String resolveTenancyHomeRegionName(IdentityClient identityClient, String tenancyId, String fallback) {
        if (identityClient != null && !StrUtil.isBlank(tenancyId)) {
            try {
                Tenancy tenancy = identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenancyId).build()).getTenancy();
                String homeKey = tenancy == null ? null : tenancy.getHomeRegionKey();
                if (StrUtil.isBlank(homeKey)) {
                    return StrUtil.blankToDefault(fallback, Region.US_ASHBURN_1.getRegionId());
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

            return StrUtil.blankToDefault(fallback, Region.US_ASHBURN_1.getRegionId());
        } else {
            return StrUtil.blankToDefault(fallback, Region.US_ASHBURN_1.getRegionId());
        }
    }

    static String formatUsageApiError(Exception e) {
        String msg = e.getMessage() == null ? "未知错误" : e.getMessage();
        if (e instanceof BmcException bmc) {
            int code = bmc.getStatusCode();
            if (code == 404) {
                return "Usage API 无数据（404）";
            } else {
                return code != 401 && code != 403 && !msg.contains("NotAuthorized")
                    ? "Usage API 失败（HTTP " + code + "）：" + msg
                    : "Usage 权限不足（需 usage-report / 成本分析读权限）：" + msg;
            }
        } else if (msg.contains("InvalidParameter") && msg.contains("precision")) {
            return "成本分析请求时间格式不符合 OCI 要求（已按 UTC 整日对齐，若仍失败请反馈日志）";
        } else {
            return !msg.contains("NotAuthorized") && !msg.contains("403") ? "成本分析查询失败：" + msg : "成本分析权限不足（需 usage-report 相关读权限）：" + msg;
        }
    }
}
