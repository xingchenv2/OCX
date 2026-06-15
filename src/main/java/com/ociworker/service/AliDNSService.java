/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONObject
 *  cn.hutool.json.JSONUtil
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.exception.OciException
 *  com.ociworker.service.AliDNSService
 *  com.ociworker.service.NotificationService
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.exception.OciException;
import com.ociworker.service.NotificationService;
import jakarta.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AliDNSService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AliDNSService.class);
    private static final String DNS_API = "https://alidns.aliyuncs.com";
    private static final String API_VERSION = "2015-01-09";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
    @Resource
    private NotificationService notificationService;

    private String getAccessKeyId() {
        return this.notificationService.getKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_ID);
    }

    private String getAccessKeySecret() {
        return this.notificationService.getKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_SECRET);
    }

    public boolean isConfigured() {
        return StrUtil.isNotBlank((CharSequence)this.getAccessKeyId()) && StrUtil.isNotBlank((CharSequence)this.getAccessKeySecret());
    }

    public void saveAccountConfig(String accessKeyId, String accessKeySecret) {
        if (StrUtil.isNotBlank((CharSequence)accessKeyId)) {
            this.notificationService.saveKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_ID, accessKeyId.trim());
        }
        if (StrUtil.isNotBlank((CharSequence)accessKeySecret)) {
            this.notificationService.saveKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_SECRET, accessKeySecret.trim());
        }
    }

    public Map<String, Object> getAccountConfigForDisplay() {
        String accessKeyId = this.getAccessKeyId();
        String accessKeySecret = this.getAccessKeySecret();
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("configured", StrUtil.isNotBlank((CharSequence)accessKeyId) && StrUtil.isNotBlank((CharSequence)accessKeySecret));
        result.put("accessKeyId", StrUtil.nullToEmpty((CharSequence)accessKeyId));
        result.put("secretConfigured", StrUtil.isNotBlank((CharSequence)accessKeySecret));
        return result;
    }

    public String testAccountConfig(String accessKeyId, String accessKeySecret) {
        JSONObject json = this.request("DescribeDomains", Map.of("PageNumber", "1", "PageSize", "1"), accessKeyId, accessKeySecret);
        if (json.containsKey((Object)"Domains") || json.containsKey((Object)"Domain")) {
            return "\u8fde\u63a5\u6210\u529f";
        }
        return "\u8fde\u63a5\u5931\u8d25";
    }

    public Map<String, Object> listDomains(int page, int perPage) {
        JSONObject json = this.request("DescribeDomains", Map.of("PageNumber", String.valueOf(Math.max(page, 1)), "PageSize", String.valueOf(Math.max(perPage, 1))));
        JSONArray domains = json.getJSONObject((Object)"Domains") != null ? json.getJSONObject((Object)"Domains").getJSONArray((Object)"Domain") : new JSONArray();
        ArrayList records = new ArrayList();
        if (domains != null) {
            for (int i = 0; i < domains.size(); ++i) {
                JSONObject row = domains.getJSONObject((Object)i);
                LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
                item.put("domainId", row.getStr((Object)"DomainId"));
                item.put("domainName", row.getStr((Object)"DomainName"));
                item.put("punyCode", row.getStr((Object)"PunyCode"));
                item.put("groupId", row.getStr((Object)"GroupId"));
                item.put("groupName", row.getStr((Object)"GroupName"));
                item.put("recordCount", row.getInt((Object)"RecordCount", Integer.valueOf(0)));
                item.put("versionName", row.getStr((Object)"VersionName"));
                item.put("dnsStatus", this.parseDnsServersFromDomain(row.getJSONObject((Object)"DnsServers")));
                records.add(item);
            }
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("records", records);
        result.put("total", json.getInt((Object)"TotalCount", Integer.valueOf(records.size())));
        result.put("page", json.getInt((Object)"PageNumber", Integer.valueOf(page)));
        result.put("perPage", json.getInt((Object)"PageSize", Integer.valueOf(perPage)));
        return result;
    }

    public Map<String, Object> listRecords(String domainName, String rrKeyWord, String typeKeyWord, String valueKeyWord, String line, int page, int perPage) {
        this.requireDomain(domainName);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("DomainName", domainName.trim());
        params.put("PageNumber", String.valueOf(Math.max(page, 1)));
        params.put("PageSize", String.valueOf(Math.max(perPage, 1)));
        this.putIfNotBlank(params, "RRKeyWord", rrKeyWord);
        this.putIfNotBlank(params, "TypeKeyWord", typeKeyWord);
        this.putIfNotBlank(params, "ValueKeyWord", valueKeyWord);
        this.putIfNotBlank(params, "Line", line);
        JSONObject json = this.request("DescribeDomainRecords", params);
        JSONArray array = json.getJSONObject((Object)"DomainRecords") != null ? json.getJSONObject((Object)"DomainRecords").getJSONArray((Object)"Record") : new JSONArray();
        ArrayList<Map> records = new ArrayList<Map>();
        if (array != null) {
            for (int i = 0; i < array.size(); ++i) {
                records.add(this.mapRecord(array.getJSONObject((Object)i)));
            }
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("records", records);
        result.put("total", json.getInt((Object)"TotalCount", Integer.valueOf(records.size())));
        result.put("page", json.getInt((Object)"PageNumber", Integer.valueOf(page)));
        result.put("perPage", json.getInt((Object)"PageSize", Integer.valueOf(perPage)));
        return result;
    }

    public Map<String, Object> addRecord(Map<String, Object> input) {
        Map params = this.buildRecordParams(input, false);
        JSONObject json = this.request("AddDomainRecord", params);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("recordId", json.getStr((Object)"RecordId"));
        return result;
    }

    public Map<String, Object> updateRecord(Map<String, Object> input) {
        String recordId = this.parseString(input.get("recordId"));
        if (StrUtil.isBlank((CharSequence)recordId)) {
            throw new OciException("\u8bb0\u5f55ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Map params = this.buildRecordParams(input, true);
        params.put("DomainName", this.parseString(input.get("domainName")).trim());
        JSONObject json = this.request("UpdateDomainRecord", params);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("recordId", json.getStr((Object)"RecordId"));
        return result;
    }

    public void deleteRecord(String recordId) {
        if (StrUtil.isBlank((CharSequence)recordId)) {
            throw new OciException("\u8bb0\u5f55ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        this.request("DeleteDomainRecord", Map.of("RecordId", recordId.trim()));
    }

    public Map<String, Object> setRecordStatus(String recordId, String status) {
        if (StrUtil.isBlank((CharSequence)recordId)) {
            throw new OciException("\u8bb0\u5f55ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String normalized = "DISABLE".equalsIgnoreCase(status) ? "DISABLE" : "ENABLE";
        JSONObject json = this.request("SetDomainRecordStatus", Map.of("RecordId", recordId.trim(), "Status", normalized));
        return this.mapRecord(json);
    }

    public List<Map<String, Object>> listDomainDnsServers(String domainName) {
        this.requireDomain(domainName);
        JSONObject json = this.request("DescribeDomainInfo", Map.of("DomainName", domainName.trim()));
        JSONArray servers = json.getJSONArray((Object)"DnsServers");
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (servers != null) {
            for (int i = 0; i < servers.size(); ++i) {
                String server = servers.getStr((Object)i);
                LinkedHashMap<String, String> item = new LinkedHashMap<String, String>();
                item.put("server", server);
                item.put("status", "active");
                result.add(item);
            }
        }
        return result;
    }

    public List<Map<String, Object>> listSupportLines(String domainName, String domainType) {
        LinkedHashMap params = new LinkedHashMap();
        this.putIfNotBlank(params, "DomainType", domainType);
        JSONObject json = this.request("DescribeSupportLines", params);
        Object recordLinesObj = json.get((Object)"RecordLines");
        JSONArray lines = null;
        if (recordLinesObj instanceof JSONArray) {
            lines = (JSONArray)recordLinesObj;
        } else if (recordLinesObj instanceof JSONObject) {
            lines = ((JSONObject)recordLinesObj).getJSONArray((Object)"RecordLine");
        }
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (lines != null) {
            for (int i = 0; i < lines.size(); ++i) {
                JSONObject line = lines.getJSONObject((Object)i);
                LinkedHashMap<String, String> item = new LinkedHashMap<String, String>();
                item.put("lineCode", this.firstNonBlank(new String[]{line.getStr((Object)"LineCode"), line.getStr((Object)"LineCodeEn"), line.getStr((Object)"Code")}));
                item.put("lineName", this.firstNonBlank(new String[]{line.getStr((Object)"LineName"), line.getStr((Object)"LineDisplayName"), line.getStr((Object)"Name")}));
                item.put("fatherCode", line.getStr((Object)"FatherCode"));
                item.put("lineDisplayName", this.firstNonBlank(new String[]{line.getStr((Object)"LineDisplayName"), line.getStr((Object)"LineName")}));
                result.add(item);
            }
        }
        if (result.isEmpty()) {
            result.add(this.defaultLine("default", "\u9ed8\u8ba4"));
            result.add(this.defaultLine("telecom", "\u4e2d\u56fd\u7535\u4fe1"));
            result.add(this.defaultLine("unicom", "\u4e2d\u56fd\u8054\u901a"));
            result.add(this.defaultLine("mobile", "\u4e2d\u56fd\u79fb\u52a8"));
            result.add(this.defaultLine("edu", "\u6559\u80b2\u7f51"));
            result.add(this.defaultLine("oversea", "\u6d77\u5916"));
        }
        return result;
    }

    private JSONObject request(String action, Map<String, String> actionParams) {
        return this.request(action, actionParams, null, null);
    }

    private JSONObject request(String action, Map<String, String> actionParams, String accessKeyIdOverride, String accessKeySecretOverride) {
        String accessKeyId = StrUtil.blankToDefault((CharSequence)StrUtil.trimToNull((CharSequence)accessKeyIdOverride), (String)this.getAccessKeyId());
        String accessKeySecret = StrUtil.blankToDefault((CharSequence)StrUtil.trimToNull((CharSequence)accessKeySecretOverride), (String)this.getAccessKeySecret());
        if (StrUtil.isBlank((CharSequence)accessKeyId) || StrUtil.isBlank((CharSequence)accessKeySecret)) {
            throw new OciException("\u963f\u91cc\u4e91DNS\u672a\u914d\u7f6e");
        }
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Action", action);
            params.put("Version", API_VERSION);
            params.put("AccessKeyId", accessKeyId);
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureVersion", "1.0");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("Timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC).format(Instant.now()));
            params.put("Format", "JSON");
            if (actionParams != null) {
                params.putAll(actionParams);
            }
            params.put("Signature", this.sign(params, accessKeySecret, "GET"));
            String url = "https://alidns.aliyuncs.com?" + this.buildQuery(params);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = JSONUtil.parseObj((String)response.body());
            String code = json.getStr((Object)"Code");
            if (code != null && !"200".equals(code)) {
                throw new OciException(json.getStr((Object)"Message", "\u963f\u91cc\u4e91DNS\u8c03\u7528\u5931\u8d25"));
            }
            return json;
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u963f\u91cc\u4e91DNS\u8c03\u7528\u5f02\u5e38: " + e.getMessage());
        }
    }

    private Map<String, String> buildRecordParams(Map<String, Object> input, boolean update) {
        Integer priority;
        String domainName = this.parseString(input.get("domainName"));
        String rr = this.parseString(input.get("rr"));
        String type = this.parseString(input.get("type"));
        String value = this.parseString(input.get("value"));
        if (!update) {
            this.requireDomain(domainName);
        }
        if (StrUtil.isBlank((CharSequence)rr)) {
            throw new OciException("\u8bf7\u586b\u5199\u4e3b\u673a\u8bb0\u5f55");
        }
        if (StrUtil.isBlank((CharSequence)type)) {
            throw new OciException("\u8bf7\u586b\u5199\u8bb0\u5f55\u7c7b\u578b");
        }
        if (StrUtil.isBlank((CharSequence)value)) {
            throw new OciException("\u8bf7\u586b\u5199\u8bb0\u5f55\u503c");
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        if (!update) {
            params.put("DomainName", domainName.trim());
        }
        params.put("RR", rr.trim());
        if (!update) {
            params.put("Type", type.trim().toUpperCase());
        }
        params.put("Value", value.trim());
        params.put("Line", this.normalizeLine(this.parseString(input.get("line"))));
        this.putIfNotBlank(params, "Lang", this.parseString(input.get("lang")));
        Integer ttl = this.parseInteger(input.get("ttl"));
        if (ttl != null && ttl > 0) {
            params.put("TTL", String.valueOf(ttl));
        }
        if ((priority = this.parseInteger(input.get("priority"))) != null && priority >= 0 && type != null && this.supportsPriority(type)) {
            params.put("Priority", String.valueOf(priority));
        }
        return params;
    }

    private Map<String, Object> mapRecord(JSONObject row) {
        LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("recordId", row.getStr((Object)"RecordId"));
        item.put("domainName", row.getStr((Object)"DomainName"));
        item.put("rr", row.getStr((Object)"RR"));
        item.put("type", row.getStr((Object)"Type"));
        item.put("value", row.getStr((Object)"Value"));
        item.put("line", row.getStr((Object)"Line"));
        item.put("lineName", this.firstNonBlank(new String[]{row.getStr((Object)"LineName"), row.getStr((Object)"Line")}));
        item.put("ttl", row.getInt((Object)"TTL"));
        item.put("priority", row.getInt((Object)"Priority"));
        item.put("status", row.getStr((Object)"Status"));
        Boolean locked = row.getBool((Object)"Locked");
        item.put("locked", locked != null && locked != false);
        item.put("weight", row.getInt((Object)"Weight"));
        item.put("remark", row.getStr((Object)"Remark"));
        return item;
    }

    private String sign(Map<String, String> params, String secret, String method) throws Exception {
        ArrayList<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder canonical = new StringBuilder();
        boolean first = true;
        for (String key : keys) {
            if (!first) {
                canonical.append("&");
            }
            first = false;
            canonical.append(this.percentEncode(key)).append("=").append(this.percentEncode(params.get(key)));
        }
        String stringToSign = method + "&%2F&" + this.percentEncode(canonical.toString());
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec((secret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.getEncoder().encodeToString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
    }

    private String percentEncode(String value) {
        return URLEncoder.encode(StrUtil.nullToEmpty((CharSequence)value), StandardCharsets.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                query.append("&");
            }
            first = false;
            query.append(this.percentEncode(entry.getKey())).append("=").append(this.percentEncode(entry.getValue()));
        }
        return query.toString();
    }

    private void requireDomain(String domainName) {
        if (StrUtil.isBlank((CharSequence)domainName)) {
            throw new OciException("\u8bf7\u8f93\u5165\u57df\u540d");
        }
    }

    private void putIfNotBlank(Map<String, String> params, String key, String value) {
        if (StrUtil.isNotBlank((CharSequence)value)) {
            params.put(key, value.trim());
        }
    }

    private String normalizeLine(String line) {
        return StrUtil.blankToDefault((CharSequence)StrUtil.trimToNull((CharSequence)line), (String)"default");
    }

    private boolean supportsPriority(String type) {
        return "MX".equalsIgnoreCase(type) || "SRV".equalsIgnoreCase(type);
    }

    private String parseString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer parseInteger(Object value) {
        if (value == null || StrUtil.isBlank((CharSequence)String.valueOf(value))) {
            return null;
        }
        if (value instanceof Number) {
            Number n = (Number)value;
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        }
        catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> defaultLine(String code, String name) {
        LinkedHashMap<String, Object> line = new LinkedHashMap<String, Object>();
        line.put("lineCode", code);
        line.put("lineName", name);
        line.put("lineDisplayName", name);
        return line;
    }

    private String firstNonBlank(String ... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!StrUtil.isNotBlank((CharSequence)value)) continue;
            return value;
        }
        return null;
    }

    private JSONObject requestPost(String action, Map<String, String> actionParams) {
        String ak = StrUtil.blankToDefault((CharSequence)StrUtil.trimToNull((CharSequence)this.getAccessKeyId()), (String)"");
        String sk = StrUtil.blankToDefault((CharSequence)StrUtil.trimToNull((CharSequence)this.getAccessKeySecret()), (String)"");
        if (StrUtil.isBlank((CharSequence)ak) || StrUtil.isBlank((CharSequence)sk)) {
            throw new OciException("\u963f\u91cc\u4e91DNS\u672a\u914d\u7f6e");
        }
        try {
            HttpResponse<String> response;
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("Action", action);
            params.put("Version", API_VERSION);
            params.put("AccessKeyId", ak);
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureVersion", "1.0");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("Timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC).format(Instant.now()));
            params.put("Format", "JSON");
            if (actionParams != null) {
                params.putAll(actionParams);
            }
            try {
                params.put("Signature", this.sign(params, sk, "POST"));
            }
            catch (Exception ex) {
                throw new OciException("\u7b7e\u540d\u5931\u8d25: " + ex.getMessage());
            }
            StringBuilder body = new StringBuilder();
            boolean first = true;
            for (Map.Entry e : params.entrySet()) {
                if (!first) {
                    body.append("&");
                }
                first = false;
                body.append(this.percentEncode((String)e.getKey())).append("=").append(this.percentEncode((String)e.getValue()));
            }
            HttpRequest request = HttpRequest.newBuilder(URI.create(DNS_API)).header("Content-Type", "application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(body.toString())).build();
            try {
                response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new OciException("\u7f51\u7edc\u8bf7\u6c42\u4e2d\u65ad");
            }
            JSONObject json = JSONUtil.parseObj((String)response.body());
            String code = json.getStr((Object)"Code");
            if (code != null && !"200".equals(code)) {
                throw new OciException(json.getStr((Object)"Message", "\u963f\u91cc\u4e91DNS\u8c03\u7528\u5931\u8d25"));
            }
            return json;
        }
        catch (Exception e) {
            throw new OciException("\u963f\u91cc\u4e91DNS\u8c03\u7528\u5f02\u5e38: " + e.getMessage());
        }
    }

    private String parseDnsServersFromDomain(JSONObject dnsServers) {
        String s;
        if (dnsServers == null) {
            return "not_system";
        }
        Object dnsServerObj = dnsServers.get((Object)"DnsServer");
        if (dnsServerObj == null) {
            return "not_system";
        }
        if (dnsServerObj instanceof JSONArray) {
            JSONArray arr = (JSONArray)dnsServerObj;
            for (int i = 0; i < arr.size(); ++i) {
                Object item = arr.get(i);
                String serverStr = String.valueOf(item);
                if (serverStr.startsWith("{")) {
                    try {
                        JSONObject inner = JSONUtil.parseObj((String)serverStr);
                        JSONArray innerArr = inner.getJSONArray((Object)"DnsServer");
                        if (innerArr == null) continue;
                        for (int j = 0; j < innerArr.size(); ++j) {
                            if (!String.valueOf(innerArr.get(j)).contains("alidns")) continue;
                            return "normal";
                        }
                        continue;
                    }
                    catch (Exception ignored) {
                        if (!serverStr.contains("alidns")) continue;
                        return "normal";
                    }
                }
                if (!serverStr.contains("alidns")) continue;
                return "normal";
            }
        } else if (dnsServerObj instanceof String && (s = (String)dnsServerObj).contains("alidns")) {
            return "normal";
        }
        return "not_system";
    }
}

