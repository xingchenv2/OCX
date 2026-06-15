/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.service.OspSubscriptionEnricher
 *  com.ocxworker.service.OspSubscriptionEnricher$ResolvedStatus
 *  com.oracle.bmc.ospgateway.SubscriptionServiceClient
 *  com.oracle.bmc.ospgateway.requests.GetSubscriptionRequest
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.service.OspSubscriptionEnricher;
import com.oracle.bmc.ospgateway.SubscriptionServiceClient;
import com.oracle.bmc.ospgateway.requests.GetSubscriptionRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Exception performing whole class analysis ignored.
 */
final class OspSubscriptionEnricher {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OspSubscriptionEnricher.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    private OspSubscriptionEnricher() {
    }

    static Object fetchSubscriptionDetail(SubscriptionServiceClient client, String ospHomeRegion, String compartmentId, String subscriptionId) {
        if (client == null || StrUtil.isBlank((CharSequence)subscriptionId)) {
            return null;
        }
        try {
            return client.getSubscription(GetSubscriptionRequest.builder().subscriptionId(subscriptionId.trim()).ospHomeRegion(ospHomeRegion).compartmentId(compartmentId).build()).getSubscription();
        }
        catch (Exception e) {
            log.warn("getSubscription failed for {}: {}", (Object)subscriptionId, (Object)e.getMessage());
            return null;
        }
    }

    static void enrichFromRawJson(JsonNode root, Map<String, Object> result) {
        if (root == null || root.isNull() || result == null) {
            return;
        }
        JsonNode sub = root;
        if (!(sub.has("id") || sub.has("planType") || sub.has("timeStart"))) {
            JsonNode inner = root.get("subscription");
            if (inner != null && !inner.isNull()) {
                sub = inner;
            } else if (root.has("items") && root.get("items").isArray() && !root.get("items").isEmpty()) {
                sub = root.get("items").get(0);
            }
        }
        OspSubscriptionEnricher.scanJsonNode((JsonNode)sub, result);
        OspSubscriptionEnricher.applySubscriptionIdentifiers(result, (JsonNode)sub, null);
        OspSubscriptionEnricher.collectOspRewardSubscriptionOcids((JsonNode)sub, null, result);
        OspSubscriptionEnricher.reconcileAfterMerge(null, result);
    }

    static boolean isOrganizationsSubscriptionOcid(String value) {
        if (StrUtil.isBlank((CharSequence)value)) {
            return false;
        }
        return value.trim().toLowerCase(Locale.ROOT).contains("organizationssubscription");
    }

    static boolean isOciOcid(String value) {
        if (StrUtil.isBlank((CharSequence)value)) {
            return false;
        }
        return value.trim().toLowerCase(Locale.ROOT).startsWith("ocid1.");
    }

    static void applySubscriptionIdentifiers(Map<String, Object> result, JsonNode sub, Object sdkObj) {
        String planNum;
        if (result == null) {
            return;
        }
        String jsonId = sub != null && sub.hasNonNull("id") ? sub.get("id").asText() : null;
        String string = planNum = sub != null && sub.hasNonNull("subscriptionPlanNumber") ? sub.get("subscriptionPlanNumber").asText() : null;
        if (sdkObj != null) {
            if (StrUtil.isBlank(planNum)) {
                planNum = OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)sdkObj, (String)"getSubscriptionPlanNumber"));
            }
            if (StrUtil.isBlank((CharSequence)jsonId)) {
                jsonId = OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)sdkObj, (String)"getId"));
            }
        }
        if (StrUtil.isNotBlank(planNum)) {
            result.put("subscriptionPlanNumber", planNum.trim());
        }
        if (StrUtil.isNotBlank((CharSequence)jsonId)) {
            result.put("subscriptionOspRef", jsonId.trim());
            if (OspSubscriptionEnricher.isOciOcid((String)jsonId) && !OspSubscriptionEnricher.isOrganizationsSubscriptionOcid((String)jsonId)) {
                result.put("subscriptionOspOcid", jsonId.trim());
            }
        }
    }

    static void collectOspRewardSubscriptionOcids(JsonNode raw, Object sdkObj, Map<String, Object> result) {
        if (result == null) {
            return;
        }
        LinkedHashSet<String> ids = new LinkedHashSet<String>();
        if (raw != null && !raw.isNull()) {
            OspSubscriptionEnricher.collectOcidsFromJsonNode((JsonNode)raw, ids);
        }
        OspSubscriptionEnricher.collectOcidsFromSdkObject((Object)sdkObj, ids);
        Object ospOcid = result.get("subscriptionOspOcid");
        if (ospOcid != null && OspSubscriptionEnricher.isOciOcid((String)String.valueOf(ospOcid))) {
            ids.add(String.valueOf(ospOcid).trim());
        }
        if (!ids.isEmpty()) {
            result.put("ospRewardSubscriptionOcids", new ArrayList(ids));
        }
    }

    private static void collectOcidsFromJsonNode(JsonNode node, Set<String> ids) {
        block6: {
            block5: {
                if (node == null || node.isNull()) {
                    return;
                }
                if (node.isTextual()) {
                    String t = node.asText();
                    if (OspSubscriptionEnricher.isOciOcid((String)t) && !OspSubscriptionEnricher.isOrganizationsSubscriptionOcid((String)t)) {
                        ids.add(t.trim());
                    }
                    return;
                }
                if (!node.isObject()) break block5;
                for (Map.Entry entry : node.properties()) {
                    OspSubscriptionEnricher.collectOcidsFromJsonNode((JsonNode)((JsonNode)entry.getValue()), ids);
                }
                break block6;
            }
            if (!node.isArray()) break block6;
            for (JsonNode child : node) {
                OspSubscriptionEnricher.collectOcidsFromJsonNode((JsonNode)child, ids);
            }
        }
    }

    private static void collectOcidsFromSdkObject(Object sdkObj, Set<String> ids) {
        if (sdkObj == null) {
            return;
        }
        for (String getter : List.of("getId", "getSubscriptionId", "getClassicSubscriptionId", "getBillingSubscriptionId")) {
            String v = OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)sdkObj, (String)getter));
            if (!OspSubscriptionEnricher.isOciOcid((String)v) || OspSubscriptionEnricher.isOrganizationsSubscriptionOcid((String)v)) continue;
            ids.add(v.trim());
        }
    }

    static void enrich(Object sub, Map<String, Object> result) {
        if (sub == null || result == null) {
            return;
        }
        String planVal = OspSubscriptionEnricher.enumValue((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getPlanType"));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"planType", (Object)planVal);
        OspSubscriptionEnricher.putIfAbsent(result, (String)"planTypeLabel", (Object)OspSubscriptionEnricher.labelPlanType((String)planVal));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"accountType", (Object)OspSubscriptionEnricher.enumValue((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getAccountType")));
        String upgrade = OspSubscriptionEnricher.enumValue((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getUpgradeState"));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"upgradeState", (Object)upgrade);
        OspSubscriptionEnricher.putIfAbsent(result, (String)"upgradeStateLabel", (Object)OspSubscriptionEnricher.labelUpgradeState((String)upgrade));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"currencyCode", (Object)OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getCurrencyCode")));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"isIntentToPay", (Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getIsIntentToPay"));
        OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionStartTime", (Object)OspSubscriptionEnricher.formatInstant((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getTimeStart")));
        Date timeEnd = OspSubscriptionEnricher.firstDate((Object)sub, (String[])new String[]{"getTimeEnd", "getTimeEnded", "getEndTime", "getSubscriptionEndTime", "getPromoEndTime"});
        OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionEndTime", (Object)OspSubscriptionEnricher.formatInstant((Object)timeEnd));
        Integer durationDays = OspSubscriptionEnricher.durationDays((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getTimeStart"), (Date)timeEnd);
        if (durationDays == null) {
            Object dur = OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getDurationDays");
            if (dur == null) {
                dur = OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getDuration");
            }
            durationDays = OspSubscriptionEnricher.parseInt((Object)dur);
        }
        OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionDurationDays", (Object)durationDays);
        String paymentMethod = OspSubscriptionEnricher.resolvePaymentMethod((Object)sub);
        OspSubscriptionEnricher.putIfAbsent(result, (String)"paymentMethod", (Object)paymentMethod);
        OspSubscriptionEnricher.putIfAbsent(result, (String)"paymentMethodLabel", (Object)OspSubscriptionEnricher.labelPaymentMethod((String)paymentMethod));
        Number amount = OspSubscriptionEnricher.resolveSubscriptionAmount((Object)sub);
        OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionAmount", (Object)amount);
        String currency = OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getCurrencyCode"));
        if (result.get("subscriptionAmountLabel") == null) {
            OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionAmountLabel", (Object)OspSubscriptionEnricher.formatAmount((Number)amount, (String)currency));
        }
        String rawStatus = OspSubscriptionEnricher.firstString((Object)sub, (String[])new String[]{"getStatus", "getSubscriptionStatus", "getLifecycleState", "getState"});
        if (result.get("subscriptionStatus") == null) {
            ResolvedStatus resolved = OspSubscriptionEnricher.resolveSubscriptionStatus((String)rawStatus, (Date)timeEnd);
            OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionStatus", (Object)resolved.code());
            OspSubscriptionEnricher.putIfAbsent(result, (String)"subscriptionStatusLabel", (Object)resolved.label());
        }
        OspSubscriptionEnricher.mergeFromJsonTree((Object)sub, result);
        try {
            OspSubscriptionEnricher.applySubscriptionIdentifiers(result, (JsonNode)JSON.valueToTree(sub), (Object)sub);
            OspSubscriptionEnricher.collectOspRewardSubscriptionOcids((JsonNode)JSON.valueToTree(sub), (Object)sub, result);
        }
        catch (Exception ignored) {
            OspSubscriptionEnricher.applySubscriptionIdentifiers(result, null, (Object)sub);
            OspSubscriptionEnricher.collectOspRewardSubscriptionOcids(null, (Object)sub, result);
        }
        OspSubscriptionEnricher.reconcileAfterMerge((Object)sub, result);
    }

    private static void putIfAbsent(Map<String, Object> result, String key, Object value) {
        String s;
        if (value == null || result == null) {
            return;
        }
        if (value instanceof String && StrUtil.isBlank((CharSequence)(s = (String)value))) {
            return;
        }
        result.putIfAbsent(key, value);
    }

    private static void reconcileAfterMerge(Object sub, Map<String, Object> result) {
        ResolvedStatus resolved;
        String pm;
        String status;
        Date end = OspSubscriptionEnricher.parseIsoDate((String)OspSubscriptionEnricher.asString((Object)result.get("subscriptionEndTime")));
        if (end != null && result.get("subscriptionDurationDays") == null) {
            Integer d;
            Date start = OspSubscriptionEnricher.parseIsoDate((String)OspSubscriptionEnricher.asString((Object)result.get("subscriptionStartTime")));
            if (start == null && sub != null) {
                start = OspSubscriptionEnricher.asDate((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getTimeStart"));
            }
            if ((d = OspSubscriptionEnricher.durationDays((Date)start, (Date)end)) != null) {
                result.putIfAbsent("subscriptionDurationDays", d);
            }
        }
        if (StrUtil.isNotBlank((CharSequence)(status = OspSubscriptionEnricher.asString((Object)result.get("subscriptionStatus")))) && result.get("subscriptionStatusLabel") == null) {
            result.put("subscriptionStatusLabel", OspSubscriptionEnricher.labelSubscriptionStatus((String)status));
        }
        if (StrUtil.isNotBlank((CharSequence)(pm = OspSubscriptionEnricher.asString((Object)result.get("paymentMethod")))) && result.get("paymentMethodLabel") == null) {
            result.put("paymentMethodLabel", OspSubscriptionEnricher.labelPaymentMethod((String)pm));
        }
        Number amt = null;
        Object amountObj = result.get("subscriptionAmount");
        if (amountObj instanceof Number) {
            amt = (Number)amountObj;
        }
        if (amt != null && result.get("subscriptionAmountLabel") == null) {
            result.put("subscriptionAmountLabel", OspSubscriptionEnricher.formatAmount((Number)amt, (String)OspSubscriptionEnricher.asString((Object)result.get("currencyCode"))));
        }
        if (StrUtil.isBlank((CharSequence)status) && (resolved = OspSubscriptionEnricher.resolveSubscriptionStatus(null, (Date)end)).code() != null) {
            result.putIfAbsent("subscriptionStatus", resolved.code());
            result.putIfAbsent("subscriptionStatusLabel", resolved.label());
        }
    }

    private static String resolvePaymentMethod(Object sub) {
        String pm;
        Object gateway;
        List opts = OspSubscriptionEnricher.asList((Object)OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getPaymentOptions"));
        if (opts != null) {
            for (Object opt : opts) {
                String pm2 = OspSubscriptionEnricher.enumValue((Object)OspSubscriptionEnricher.tryInvoke(opt, (String)"getPaymentMethod"));
                if (StrUtil.isBlank((CharSequence)pm2) && opt != null) {
                    String simple = opt.getClass().getSimpleName();
                    if (simple.contains("FreeTrial")) {
                        pm2 = "FREE_TRIAL";
                    } else if (simple.contains("CreditCard")) {
                        pm2 = "CREDIT_CARD";
                    } else if (simple.contains("Paypal")) {
                        pm2 = "PAYPAL";
                    }
                }
                if (!StrUtil.isNotBlank((CharSequence)pm2)) continue;
                return pm2;
            }
        }
        if ((gateway = OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getPaymentGateway")) != null && StrUtil.isNotBlank((CharSequence)(pm = OspSubscriptionEnricher.firstString((Object)gateway, (String[])new String[]{"getPaymentMethod", "getType", "getGatewayType"})))) {
            return pm;
        }
        return null;
    }

    private static Number resolveSubscriptionAmount(Object sub) {
        Number n = OspSubscriptionEnricher.firstNumber((Object)sub, (String[])new String[]{"getSubscriptionAmount", "getPromoAmount", "getPromotionalCreditAmount", "getTotalAmount", "getContractValue", "getListPrice", "getAmount", "getCreditAmount"});
        if (n != null) {
            return n;
        }
        Object gateway = OspSubscriptionEnricher.tryInvoke((Object)sub, (String)"getPaymentGateway");
        if (gateway != null && (n = OspSubscriptionEnricher.firstNumber((Object)gateway, (String[])new String[]{"getAmount", "getTotalAmount", "getSubscriptionAmount"})) != null) {
            return n;
        }
        return null;
    }

    private static ResolvedStatus resolveSubscriptionStatus(String rawStatus, Date timeEnd) {
        if (StrUtil.isNotBlank((CharSequence)rawStatus)) {
            return new ResolvedStatus(rawStatus.toUpperCase(Locale.ROOT), OspSubscriptionEnricher.labelSubscriptionStatus((String)rawStatus));
        }
        if (timeEnd != null) {
            if (timeEnd.toInstant().isBefore(Instant.now())) {
                return new ResolvedStatus("EXPIRED", "\u5df2\u8fc7\u671f");
            }
            return new ResolvedStatus("ACTIVE", "\u6709\u6548");
        }
        return new ResolvedStatus(null, null);
    }

    static String labelSubscriptionStatus(String status) {
        if (StrUtil.isBlank((CharSequence)status)) {
            return null;
        }
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "ACTIVE" -> "\u6709\u6548";
            case "EXPIRED" -> "\u5df2\u8fc7\u671f";
            case "INACTIVE" -> "\u672a\u6fc0\u6d3b";
            case "PENDING" -> "\u5904\u7406\u4e2d";
            case "ERROR" -> "\u5f02\u5e38";
            default -> status;
        };
    }

    static String labelPaymentMethod(String method) {
        if (StrUtil.isBlank((CharSequence)method)) {
            return null;
        }
        return switch (method.toUpperCase(Locale.ROOT)) {
            case "FREE_TRIAL" -> "\u514d\u8d39\u8bd5\u7528 (FREE_TRIAL)";
            case "CREDIT_CARD" -> "\u4fe1\u7528\u5361";
            case "PAYPAL" -> "PayPal";
            default -> method;
        };
    }

    static String labelUpgradeState(String upgrade) {
        if (StrUtil.isBlank((CharSequence)upgrade)) {
            return null;
        }
        return switch (upgrade.toUpperCase(Locale.ROOT)) {
            case "PROMO" -> "\u4fc3\u9500/\u8bd5\u7528";
            case "SUBMITTED" -> "\u5df2\u63d0\u4ea4";
            case "ERROR" -> "\u9519\u8bef";
            case "UPGRADED" -> "\u5df2\u5347\u7ea7";
            case "UPGRADE_PENDING" -> "\u5347\u7ea7\u5f85\u5904\u7406";
            case "UPGRADE_COMPLETE" -> "\u5347\u7ea7\u5b8c\u6210";
            case "UPGRADE_FAILED" -> "\u5347\u7ea7\u5931\u8d25";
            default -> upgrade;
        };
    }

    static String labelPlanType(String plan) {
        if (StrUtil.isBlank((CharSequence)plan)) {
            return null;
        }
        if (OspSubscriptionEnricher.isFreeTierPlan((String)plan)) {
            return "\u514d\u8d39\u5957\u9910 (Free Tier)";
        }
        return switch (plan.toUpperCase(Locale.ROOT)) {
            case "PAYG" -> "\u6309\u91cf\u4ed8\u8d39 (PAYG)";
            default -> plan;
        };
    }

    private static boolean isFreeTierPlan(String plan) {
        if (StrUtil.isBlank((CharSequence)plan)) {
            return false;
        }
        String p = plan.toUpperCase(Locale.ROOT).replace("_", "").replace("-", "");
        return "FREE".equals(p) || "FREETIER".equals(p);
    }

    private static void mergeFromJsonTree(Object sub, Map<String, Object> result) {
        try {
            JsonNode root = JSON.valueToTree(sub);
            OspSubscriptionEnricher.scanJsonNode((JsonNode)root, result);
        }
        catch (Exception e) {
            log.debug("subscription json scan skipped: {}", (Object)e.getMessage());
        }
    }

    private static void scanJsonNode(JsonNode node, Map<String, Object> result) {
        block28: {
            block27: {
                if (node == null || node.isNull()) {
                    return;
                }
                if (!node.isObject()) break block27;
                for (Map.Entry e : node.properties()) {
                    String key = (String)e.getKey();
                    JsonNode val = (JsonNode)e.getValue();
                    String lower = key.toLowerCase(Locale.ROOT);
                    if (OspSubscriptionEnricher.matchesEndKey((String)lower)) {
                        OspSubscriptionEnricher.putEndIfAbsent(result, (JsonNode)val);
                    } else if (OspSubscriptionEnricher.matchesStartKey((String)lower)) {
                        OspSubscriptionEnricher.putStartIfAbsent(result, (JsonNode)val);
                    } else if (lower.equals("subscriptionplannumber")) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"subscriptionPlanNumber", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                    } else if (lower.equals("plantype")) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"planType", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"planTypeLabel", (String)OspSubscriptionEnricher.labelPlanType((String)OspSubscriptionEnricher.textNode((JsonNode)val)));
                    } else if (lower.equals("upgradestate")) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"upgradeState", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"upgradeStateLabel", (String)OspSubscriptionEnricher.labelUpgradeState((String)OspSubscriptionEnricher.textNode((JsonNode)val)));
                    } else if (lower.contains("paymentmethod") || "paymenttype".equals(lower)) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"paymentMethod", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                    } else if (OspSubscriptionEnricher.isSubscriptionStatusKey((String)lower)) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"subscriptionStatus", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                    } else if (OspSubscriptionEnricher.isAmountKey((String)lower)) {
                        OspSubscriptionEnricher.putAmountIfAbsent(result, (JsonNode)val);
                    } else if (lower.contains("duration")) {
                        OspSubscriptionEnricher.putDurationIfAbsent(result, (JsonNode)val);
                    } else if (lower.equals("currencycode")) {
                        OspSubscriptionEnricher.putStringIfAbsent(result, (String)"currencyCode", (String)OspSubscriptionEnricher.textNode((JsonNode)val));
                    } else if (lower.equals("isintenttopay")) {
                        if (val.isBoolean()) {
                            result.putIfAbsent("isIntentToPay", val.asBoolean());
                        }
                    } else if (lower.contains("renew") && lower.contains("time")) {
                        OspSubscriptionEnricher.putEndIfAbsent(result, (JsonNode)val, (String)"subscriptionRenewTime");
                    }
                    OspSubscriptionEnricher.scanJsonNode((JsonNode)val, result);
                }
                break block28;
            }
            if (!node.isArray()) break block28;
            for (JsonNode child : node) {
                OspSubscriptionEnricher.scanJsonNode((JsonNode)child, result);
            }
        }
    }

    private static boolean matchesEndKey(String lower) {
        return lower.equals("timeend") || lower.equals("endtime") || lower.equals("timeended") || lower.contains("subscriptionend") || lower.contains("promoend") || lower.contains("end") && lower.contains("time") && !lower.contains("renew");
    }

    private static boolean matchesStartKey(String lower) {
        return lower.equals("timestart") || lower.equals("starttime") || lower.contains("start") && lower.contains("time") && !lower.contains("restart");
    }

    private static boolean isSubscriptionStatusKey(String lower) {
        if (lower.contains("upgrade")) {
            return false;
        }
        return lower.equals("status") || lower.equals("subscriptionstatus") || lower.equals("lifecyclestate") || lower.equals("state");
    }

    private static void putStartIfAbsent(Map<String, Object> result, JsonNode val) {
        if (result.get("subscriptionStartTime") != null) {
            return;
        }
        String iso = OspSubscriptionEnricher.parseDateIso((JsonNode)val);
        if (iso != null) {
            result.put("subscriptionStartTime", iso);
        }
    }

    private static void putDurationIfAbsent(Map<String, Object> result, JsonNode val) {
        Integer days;
        if (result.get("subscriptionDurationDays") != null) {
            return;
        }
        if (val.isNumber()) {
            result.put("subscriptionDurationDays", val.asInt());
            return;
        }
        String text = OspSubscriptionEnricher.textNode((JsonNode)val);
        if (StrUtil.isNotBlank((CharSequence)text) && (days = OspSubscriptionEnricher.parseDurationDaysFromApiText((String)text)) != null) {
            result.put("subscriptionDurationDays", days);
        }
    }

    private static Integer parseDurationDaysFromApiText(String text) {
        if (StrUtil.isBlank((CharSequence)text)) {
            return null;
        }
        String t = text.trim().toUpperCase(Locale.ROOT);
        Matcher m = Pattern.compile("(\\d+)\\s*DAY").matcher(t);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return OspSubscriptionEnricher.parseInt((Object)text);
    }

    private static void putEndIfAbsent(Map<String, Object> result, JsonNode val, String targetKey) {
        if (result.get(targetKey) != null) {
            return;
        }
        String iso = OspSubscriptionEnricher.parseDateIso((JsonNode)val);
        if (iso != null) {
            result.put(targetKey, iso);
        }
    }

    private static boolean isAmountKey(String lower) {
        return lower.contains("subscriptionamount") || lower.contains("promoamount") || lower.contains("promotionalcredit") || lower.equals("totalamount") || lower.contains("amount") && !lower.contains("discount");
    }

    private static void putEndIfAbsent(Map<String, Object> result, JsonNode val) {
        if (result.get("subscriptionEndTime") != null) {
            return;
        }
        String iso = OspSubscriptionEnricher.parseDateIso((JsonNode)val);
        if (iso != null) {
            result.put("subscriptionEndTime", iso);
        }
    }

    private static void putAmountIfAbsent(Map<String, Object> result, JsonNode val) {
        if (result.get("subscriptionAmount") != null) {
            return;
        }
        if (val.isNumber()) {
            result.put("subscriptionAmount", val.numberValue());
            String cur = OspSubscriptionEnricher.asString((Object)result.get("currencyCode"));
            result.put("subscriptionAmountLabel", OspSubscriptionEnricher.formatAmount((Number)val.numberValue(), (String)cur));
        }
    }

    private static void putStringIfAbsent(Map<String, Object> result, String key, String val) {
        if (StrUtil.isBlank((CharSequence)val) || result.get(key) != null) {
            return;
        }
        result.put(key, val);
        if ("paymentMethod".equals(key)) {
            result.put("paymentMethodLabel", OspSubscriptionEnricher.labelPaymentMethod((String)val));
        }
        if ("subscriptionStatus".equals(key)) {
            result.put("subscriptionStatusLabel", OspSubscriptionEnricher.labelSubscriptionStatus((String)val));
        }
    }

    private static String textNode(JsonNode val) {
        if (val == null || val.isNull()) {
            return null;
        }
        if (val.isTextual()) {
            return val.asText();
        }
        if (val.isNumber()) {
            return val.asText();
        }
        return null;
    }

    private static String parseDateIso(JsonNode val) {
        if (val == null || val.isNull()) {
            return null;
        }
        if (val.isNumber()) {
            return OspSubscriptionEnricher.formatInstant((Object)new Date(val.asLong()));
        }
        if (val.isTextual()) {
            try {
                return Instant.parse(val.asText()).toString();
            }
            catch (Exception ignored) {
                return val.asText();
            }
        }
        return null;
    }

    private static Date parseIsoDate(String iso) {
        if (StrUtil.isBlank((CharSequence)iso)) {
            return null;
        }
        try {
            return Date.from(Instant.parse(iso));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static String formatAmount(Number amount, String currency) {
        if (amount == null) {
            return null;
        }
        String cur = StrUtil.isNotBlank((CharSequence)currency) ? " " + currency.trim() : "";
        return String.valueOf(amount) + cur;
    }

    private static Integer durationDays(Object startObj, Date end) {
        Date start = OspSubscriptionEnricher.asDate((Object)startObj);
        return OspSubscriptionEnricher.durationDays((Date)start, (Date)end);
    }

    private static Integer durationDays(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }
        long days = ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
        return days >= 0L ? Integer.valueOf((int)days) : null;
    }

    private static Date firstDate(Object target, String ... getters) {
        for (String g : getters) {
            Object v = OspSubscriptionEnricher.tryInvoke((Object)target, (String)g);
            Date d = OspSubscriptionEnricher.asDate((Object)v);
            if (d == null) continue;
            return d;
        }
        return null;
    }

    private static String firstString(Object target, String ... getters) {
        for (String g : getters) {
            String s = OspSubscriptionEnricher.asString((Object)OspSubscriptionEnricher.tryInvoke((Object)target, (String)g));
            if (!StrUtil.isNotBlank((CharSequence)s)) continue;
            return s;
        }
        return null;
    }

    private static Number firstNumber(Object target, String ... getters) {
        for (String g : getters) {
            Object v = OspSubscriptionEnricher.tryInvoke((Object)target, (String)g);
            if (!(v instanceof Number)) continue;
            Number n = (Number)v;
            return n;
        }
        return null;
    }

    private static Integer parseInt(Object v) {
        if (v instanceof Number) {
            Number n = (Number)v;
            return n.intValue();
        }
        if (v == null) {
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(v).replaceAll("[^0-9]", ""));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static String formatInstant(Object v) {
        Date d = OspSubscriptionEnricher.asDate((Object)v);
        return d == null ? null : d.toInstant().toString();
    }

    private static Date asDate(Object v) {
        if (v instanceof Date) {
            Date d = (Date)v;
            return d;
        }
        return null;
    }

    private static String enumValue(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Enum) {
            Enum e = (Enum)v;
            Object val = OspSubscriptionEnricher.tryInvoke((Object)e, (String)"getValue");
            if (val != null) {
                return String.valueOf(val);
            }
            return e.name();
        }
        return String.valueOf(v);
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v).trim();
    }

    private static List<?> asList(Object v) {
        List l;
        return v instanceof List ? (l = (List)v) : null;
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

