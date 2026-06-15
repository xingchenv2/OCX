/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.service.OspSubscriptionEnricher
 *  com.ociworker.service.UsageCostService
 *  com.ociworker.service.UsageRewardsService
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.usage.RewardsClient
 *  com.oracle.bmc.usage.RewardsClient$Builder
 *  com.oracle.bmc.usage.model.MonthlyRewardSummary
 *  com.oracle.bmc.usage.model.RewardCollection
 *  com.oracle.bmc.usage.model.RewardDetails
 *  com.oracle.bmc.usage.requests.ListRewardsRequest
 *  com.oracle.bmc.usage.responses.ListRewardsResponse
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
import com.oracle.bmc.usage.RewardsClient;
import com.oracle.bmc.usage.model.MonthlyRewardSummary;
import com.oracle.bmc.usage.model.RewardCollection;
import com.oracle.bmc.usage.model.RewardDetails;
import com.oracle.bmc.usage.requests.ListRewardsRequest;
import com.oracle.bmc.usage.responses.ListRewardsResponse;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
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
public class UsageRewardsService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UsageRewardsService.class);

    public Map<String, Object> fetchSubscriptionRewards(OciClientService oci, String tenancyId, List<String> subscriptionIds, String fallbackRegion) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("summary", null);
        out.put("periods", new ArrayList());
        out.put("attemptedSubscriptionIds", List.of());
        out.put("subscriptionIdUsed", null);
        if (StrUtil.isBlank((CharSequence)tenancyId)) {
            out.put("reason", "\u7f3a\u5c11 tenancy OCID");
            return out;
        }
        List candidates = UsageRewardsService.dedupeOcidCandidates(subscriptionIds);
        if (candidates.isEmpty()) {
            out.put("reason", "\u7f3a\u5c11\u53ef\u7528\u4e8e Rewards \u7684\u8ba2\u9605 OCID\uff08OSP \u7f16\u53f7\u9700\u5148\u89e3\u6790\u4e3a ocid1.*\uff09");
            return out;
        }
        ArrayList<String> attempted = new ArrayList<String>();
        ArrayList<CallSite> failureNotes = new ArrayList<CallSite>();
        for (String subId : candidates) {
            attempted.add(subId);
            Map one = this.fetchSubscriptionRewardsSingle(oci, tenancyId, subId, fallbackRegion);
            if (Boolean.TRUE.equals(one.get("available"))) {
                one.put("attemptedSubscriptionIds", attempted);
                one.put("subscriptionIdUsed", subId);
                return one;
            }
            Object reason = one.get("reason");
            if (reason == null || !StrUtil.isNotBlank((CharSequence)String.valueOf(reason))) continue;
            failureNotes.add((CallSite)((Object)(UsageRewardsService.shortId((String)subId) + ": " + String.valueOf(reason))));
        }
        out.put("attemptedSubscriptionIds", attempted);
        out.put("reason", UsageRewardsService.buildMultiAttemptReason(failureNotes));
        return out;
    }

    public Map<String, Object> fetchSubscriptionRewards(OciClientService oci, String tenancyId, String subscriptionId, String fallbackRegion) {
        List ids = StrUtil.isBlank((CharSequence)subscriptionId) ? List.of() : List.of(subscriptionId.trim());
        return this.fetchSubscriptionRewards(oci, tenancyId, ids, fallbackRegion);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Map<String, Object> fetchSubscriptionRewardsSingle(OciClientService oci, String tenancyId, String subscriptionId, String fallbackRegion) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("available", Boolean.FALSE);
        out.put("reason", null);
        out.put("summary", null);
        out.put("periods", new ArrayList());
        if (StrUtil.isBlank((CharSequence)subscriptionId)) {
            out.put("reason", "\u7f3a\u5c11\u8ba2\u9605 ID\uff0c\u65e0\u6cd5\u67e5\u8be2\u4fc3\u9500\u4f59\u989d");
            return out;
        }
        if (!OspSubscriptionEnricher.isOciOcid((String)subscriptionId)) {
            out.put("reason", "\u8ba2\u9605\u5f15\u7528\u300c" + subscriptionId.trim() + "\u300d\u4e0d\u662f OCID\uff08\u9700 ocid1.*\uff09\uff1bUsage Rewards \u4e0d\u63a5\u53d7 OSP \u8ba2\u9605\u7f16\u53f7");
            return out;
        }
        RewardsClient.Builder rewardsB = RewardsClient.builder();
        OciProxyConfigService pxy = OciProxyConfigService.instance();
        if (pxy == null || !pxy.ociUsesExplicitClientProxy()) {
            rewardsB = (RewardsClient.Builder)rewardsB.additionalClientConfigurator(OciProxyConfigService.ociSdkJerseyDirectConfigurator());
        }
        try (RewardsClient client = rewardsB.build((AbstractAuthenticationDetailsProvider)oci.getProvider());){
            RewardCollection col;
            String region = UsageCostService.resolveTenancyHomeRegionName((IdentityClient)oci.getIdentityClient(), (String)tenancyId, (String)fallbackRegion);
            try {
                client.setRegion(Region.fromRegionId((String)region));
            }
            catch (Exception e) {
                client.setRegion(Region.US_ASHBURN_1);
            }
            ListRewardsResponse resp = client.listRewards(ListRewardsRequest.builder().tenancyId(tenancyId.trim()).subscriptionId(subscriptionId.trim()).build());
            RewardCollection rewardCollection = col = resp == null ? null : resp.getRewardCollection();
            if (col == null) {
                out.put("reason", "\u4fc3\u9500\u4f59\u989d\u63a5\u53e3\u8fd4\u56de\u4e3a\u7a7a");
                LinkedHashMap<String, Object> linkedHashMap = out;
                return linkedHashMap;
            }
            out.put("available", Boolean.TRUE);
            if (col.getSummary() != null) {
                out.put("summary", UsageRewardsService.mapSummary((RewardDetails)col.getSummary()));
            }
            ArrayList<Map> periods = new ArrayList<Map>();
            if (col.getItems() != null) {
                for (MonthlyRewardSummary item : col.getItems()) {
                    if (item == null) continue;
                    periods.add(UsageRewardsService.mapPeriod((MonthlyRewardSummary)item));
                }
            }
            out.put("periods", periods);
            if (!periods.isEmpty()) return out;
            if (col.getSummary() != null) return out;
            out.put("available", Boolean.FALSE);
            out.put("reason", "\u65e0\u4fc3\u9500\u4f59\u989d\u8bb0\u5f55");
            return out;
        }
        catch (BmcException e) {
            log.warn("listRewards failed: {}", (Object)e.getMessage());
            out.put("reason", UsageRewardsService.formatRewardsError((BmcException)e));
            return out;
        }
        catch (Exception e) {
            log.warn("listRewards failed: {}", (Object)e.getMessage());
            out.put("reason", "\u4fc3\u9500\u4f59\u989d\u67e5\u8be2\u5931\u8d25\uff1a" + (e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage()));
        }
        return out;
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

    private static String shortId(String ocid) {
        if (StrUtil.isBlank((CharSequence)ocid) || ocid.length() <= 48) {
            return ocid;
        }
        return ocid.substring(0, 24) + "\u2026" + ocid.substring(ocid.length() - 12);
    }

    private static String buildMultiAttemptReason(List<String> failureNotes) {
        if (failureNotes == null || failureNotes.isEmpty()) {
            return "\u4fc3\u9500\u4f59\u989d\u63a5\u53e3\u5747\u65e0\u6570\u636e";
        }
        if (failureNotes.size() == 1) {
            return failureNotes.get(0);
        }
        return "\u5df2\u4f9d\u6b21\u5c1d\u8bd5 " + failureNotes.size() + " \u4e2a\u8ba2\u9605 OCID\uff0c\u5747\u672a\u8fd4\u56de\u4fc3\u9500\u4f59\u989d\uff1b" + String.join((CharSequence)"\uff1b", failureNotes);
    }

    private static Map<String, Object> mapSummary(RewardDetails s) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tenancyId", s.getTenancyId());
        m.put("subscriptionId", s.getSubscriptionId());
        m.put("currency", s.getCurrency());
        m.put("rewardsRate", s.getRewardsRate());
        m.put("totalRewardsAvailable", s.getTotalRewardsAvailable());
        m.put("redemptionCode", s.getRedemptionCode());
        m.put("totalRewardsAvailableLabel", UsageRewardsService.formatRewardAmount((Number)s.getTotalRewardsAvailable(), (String)s.getCurrency()));
        return m;
    }

    private static Map<String, Object> mapPeriod(MonthlyRewardSummary item) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("availableRewards", item.getAvailableRewards());
        m.put("earnedRewards", item.getEarnedRewards());
        m.put("redeemedRewards", item.getRedeemedRewards());
        m.put("isManual", item.getIsManual());
        m.put("timeRewardsExpired", UsageRewardsService.formatInstant((Date)item.getTimeRewardsExpired()));
        m.put("timeRewardsEarned", UsageRewardsService.formatInstant((Date)item.getTimeRewardsEarned()));
        m.put("timeUsageStarted", UsageRewardsService.formatInstant((Date)item.getTimeUsageStarted()));
        m.put("timeUsageEnded", UsageRewardsService.formatInstant((Date)item.getTimeUsageEnded()));
        m.put("usageAmount", item.getUsageAmount());
        m.put("eligibleUsageAmount", item.getEligibleUsageAmount());
        m.put("ineligibleUsageAmount", item.getIneligibleUsageAmount());
        m.put("usagePeriodKey", item.getUsagePeriodKey());
        m.put("availableRewardsLabel", UsageRewardsService.formatRewardAmount((Number)item.getAvailableRewards(), null));
        m.put("earnedRewardsLabel", UsageRewardsService.formatRewardAmount((Number)item.getEarnedRewards(), null));
        m.put("redeemedRewardsLabel", UsageRewardsService.formatRewardAmount((Number)item.getRedeemedRewards(), null));
        return m;
    }

    static String formatRewardAmount(Number amount, String currency) {
        if (amount == null) {
            return null;
        }
        String cur = StrUtil.isNotBlank((CharSequence)currency) ? " " + currency.trim() : "";
        return String.valueOf(amount) + cur;
    }

    private static String formatInstant(Date d) {
        return d == null ? null : d.toInstant().toString();
    }

    private static String formatRewardsError(BmcException e) {
        String msg = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
        int code = e.getStatusCode();
        if (code == 404) {
            return "\u4fc3\u9500\u4f59\u989d\u63a5\u53e3\u65e0\u6570\u636e\uff08404\uff09";
        }
        if (code == 401 || code == 403 || msg.contains("NotAuthorized")) {
            return "\u4fc3\u9500\u4f59\u989d\u6743\u9650\u4e0d\u8db3\uff08\u9700 usage / rewards \u76f8\u5173\u8bfb\u6743\u9650\uff09\uff1a" + msg;
        }
        return "\u4fc3\u9500\u4f59\u989d\u67e5\u8be2\u5931\u8d25\uff08HTTP " + code + "\uff09\uff1a" + msg;
    }
}

