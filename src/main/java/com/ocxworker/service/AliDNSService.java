package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.exception.OciException;
import jakarta.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
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
import java.util.Map.Entry;
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
        return StrUtil.isNotBlank(this.getAccessKeyId()) && StrUtil.isNotBlank(this.getAccessKeySecret());
    }

    public void saveAccountConfig(String accessKeyId, String accessKeySecret) {
        if (StrUtil.isNotBlank(accessKeyId)) {
            this.notificationService.saveKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_ID, accessKeyId.trim());
        }

        if (StrUtil.isNotBlank(accessKeySecret)) {
            this.notificationService.saveKvValue(SysCfgEnum.ALIDNS_ACCESS_KEY_SECRET, accessKeySecret.trim());
        }
    }

    public Map<String, Object> getAccountConfigForDisplay() {
        String accessKeyId = this.getAccessKeyId();
        String accessKeySecret = this.getAccessKeySecret();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("configured", StrUtil.isNotBlank(accessKeyId) && StrUtil.isNotBlank(accessKeySecret));
        result.put("accessKeyId", StrUtil.nullToEmpty(accessKeyId));
        result.put("secretConfigured", StrUtil.isNotBlank(accessKeySecret));
        return result;
    }

    public String testAccountConfig(String accessKeyId, String accessKeySecret) {
        JSONObject json = this.request("DescribeDomains", Map.of("PageNumber", "1", "PageSize", "1"), accessKeyId, accessKeySecret);
        return !json.containsKey("Domains") && !json.containsKey("Domain") ? "连接失败" : "连接成功";
    }

    public Map<String, Object> listDomains(int page, int perPage) {
        JSONObject json = this.request(
            "DescribeDomains", Map.of("PageNumber", String.valueOf(Math.max(page, 1)), "PageSize", String.valueOf(Math.max(perPage, 1)))
        );
        JSONArray domains = json.getJSONObject("Domains") != null ? json.getJSONObject("Domains").getJSONArray("Domain") : new JSONArray();
        List<Map<String, Object>> records = new ArrayList<>();
        if (domains != null) {
            for (int i = 0; i < domains.size(); i++) {
                JSONObject row = domains.getJSONObject(i);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("domainId", row.getStr("DomainId"));
                item.put("domainName", row.getStr("DomainName"));
                item.put("punyCode", row.getStr("PunyCode"));
                item.put("groupId", row.getStr("GroupId"));
                item.put("groupName", row.getStr("GroupName"));
                item.put("recordCount", row.getInt("RecordCount", 0));
                item.put("versionName", row.getStr("VersionName"));
                item.put("dnsStatus", this.parseDnsServersFromDomain(row.getJSONObject("DnsServers")));
                records.add(item);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", json.getInt("TotalCount", records.size()));
        result.put("page", json.getInt("PageNumber", page));
        result.put("perPage", json.getInt("PageSize", perPage));
        return result;
    }

    public Map<String, Object> listRecords(String domainName, String rrKeyWord, String typeKeyWord, String valueKeyWord, String line, int page, int perPage) {
        this.requireDomain(domainName);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("DomainName", domainName.trim());
        params.put("PageNumber", String.valueOf(Math.max(page, 1)));
        params.put("PageSize", String.valueOf(Math.max(perPage, 1)));
        this.putIfNotBlank(params, "RRKeyWord", rrKeyWord);
        this.putIfNotBlank(params, "TypeKeyWord", typeKeyWord);
        this.putIfNotBlank(params, "ValueKeyWord", valueKeyWord);
        this.putIfNotBlank(params, "Line", line);
        JSONObject json = this.request("DescribeDomainRecords", params);
        JSONArray array = json.getJSONObject("DomainRecords") != null ? json.getJSONObject("DomainRecords").getJSONArray("Record") : new JSONArray();
        List<Map<String, Object>> records = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                records.add(this.mapRecord(array.getJSONObject(i)));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", json.getInt("TotalCount", records.size()));
        result.put("page", json.getInt("PageNumber", page));
        result.put("perPage", json.getInt("PageSize", perPage));
        return result;
    }

    public Map<String, Object> addRecord(Map<String, Object> input) {
        Map<String, String> params = this.buildRecordParams(input, false);
        JSONObject json = this.request("AddDomainRecord", params);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", json.getStr("RecordId"));
        return result;
    }

    public Map<String, Object> updateRecord(Map<String, Object> input) {
        String recordId = this.parseString(input.get("recordId"));
        if (StrUtil.isBlank(recordId)) {
            throw new OciException("记录ID不能为空");
        } else {
            Map<String, String> params = this.buildRecordParams(input, true);
            params.put("DomainName", this.parseString(input.get("domainName")).trim());
            JSONObject json = this.request("UpdateDomainRecord", params);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("recordId", json.getStr("RecordId"));
            return result;
        }
    }

    public void deleteRecord(String recordId) {
        if (StrUtil.isBlank(recordId)) {
            throw new OciException("记录ID不能为空");
        } else {
            this.request("DeleteDomainRecord", Map.of("RecordId", recordId.trim()));
        }
    }

    public Map<String, Object> setRecordStatus(String recordId, String status) {
        if (StrUtil.isBlank(recordId)) {
            throw new OciException("记录ID不能为空");
        } else {
            String normalized = "DISABLE".equalsIgnoreCase(status) ? "DISABLE" : "ENABLE";
            JSONObject json = this.request("SetDomainRecordStatus", Map.of("RecordId", recordId.trim(), "Status", normalized));
            return this.mapRecord(json);
        }
    }

    public List<Map<String, Object>> listDomainDnsServers(String domainName) {
        this.requireDomain(domainName);
        JSONObject json = this.request("DescribeDomainInfo", Map.of("DomainName", domainName.trim()));
        JSONArray servers = json.getJSONArray("DnsServers");
        List<Map<String, Object>> result = new ArrayList<>();
        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {
                String server = servers.getStr(i);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("server", server);
                item.put("status", "active");
                result.add(item);
            }
        }

        return result;
    }

    public List<Map<String, Object>> listSupportLines(String domainName, String domainType) {
        Map<String, String> params = new LinkedHashMap<>();
        this.putIfNotBlank(params, "DomainType", domainType);
        JSONObject json = this.request("DescribeSupportLines", params);
        Object recordLinesObj = json.get("RecordLines");
        JSONArray lines = null;
        if (recordLinesObj instanceof JSONArray) {
            lines = (JSONArray)recordLinesObj;
        } else if (recordLinesObj instanceof JSONObject) {
            lines = ((JSONObject)recordLinesObj).getJSONArray("RecordLine");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                JSONObject line = lines.getJSONObject(i);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("lineCode", this.firstNonBlank(line.getStr("LineCode"), line.getStr("LineCodeEn"), line.getStr("Code")));
                item.put("lineName", this.firstNonBlank(line.getStr("LineName"), line.getStr("LineDisplayName"), line.getStr("Name")));
                item.put("fatherCode", line.getStr("FatherCode"));
                item.put("lineDisplayName", this.firstNonBlank(line.getStr("LineDisplayName"), line.getStr("LineName")));
                result.add(item);
            }
        }

        if (result.isEmpty()) {
            result.add(this.defaultLine("default", "默认"));
            result.add(this.defaultLine("telecom", "中国电信"));
            result.add(this.defaultLine("unicom", "中国联通"));
            result.add(this.defaultLine("mobile", "中国移动"));
            result.add(this.defaultLine("edu", "教育网"));
            result.add(this.defaultLine("oversea", "海外"));
        }

        return result;
    }

    private JSONObject request(String action, Map<String, String> actionParams) {
        return this.request(action, actionParams, null, null);
    }

    private JSONObject request(String action, Map<String, String> actionParams, String accessKeyIdOverride, String accessKeySecretOverride) {
        String accessKeyId = StrUtil.blankToDefault(StrUtil.trimToNull(accessKeyIdOverride), this.getAccessKeyId());
        String accessKeySecret = StrUtil.blankToDefault(StrUtil.trimToNull(accessKeySecretOverride), this.getAccessKeySecret());
        if (!StrUtil.isBlank(accessKeyId) && !StrUtil.isBlank(accessKeySecret)) {
            try {
                Map<String, String> params = new LinkedHashMap<>();
                params.put("Action", action);
                params.put("Version", "2015-01-09");
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
                HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
                JSONObject json = JSONUtil.parseObj(response.body());
                String code = json.getStr("Code");
                if (code != null && !"200".equals(code)) {
                    throw new OciException(json.getStr("Message", "阿里云DNS调用失败"));
                } else {
                    return json;
                }
            } catch (OciException var13) {
                throw var13;
            } catch (Exception var14) {
                throw new OciException("阿里云DNS调用异常: " + var14.getMessage());
            }
        } else {
            throw new OciException("阿里云DNS未配置");
        }
    }

    private Map<String, String> buildRecordParams(Map<String, Object> input, boolean update) {
        String domainName = this.parseString(input.get("domainName"));
        String rr = this.parseString(input.get("rr"));
        String type = this.parseString(input.get("type"));
        String value = this.parseString(input.get("value"));
        if (!update) {
            this.requireDomain(domainName);
        }

        if (StrUtil.isBlank(rr)) {
            throw new OciException("请填写主机记录");
        } else if (StrUtil.isBlank(type)) {
            throw new OciException("请填写记录类型");
        } else if (StrUtil.isBlank(value)) {
            throw new OciException("请填写记录值");
        } else {
            Map<String, String> params = new LinkedHashMap<>();
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

            Integer priority = this.parseInteger(input.get("priority"));
            if (priority != null && priority >= 0 && type != null && this.supportsPriority(type)) {
                params.put("Priority", String.valueOf(priority));
            }

            return params;
        }
    }

    private Map<String, Object> mapRecord(JSONObject row) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("recordId", row.getStr("RecordId"));
        item.put("domainName", row.getStr("DomainName"));
        item.put("rr", row.getStr("RR"));
        item.put("type", row.getStr("Type"));
        item.put("value", row.getStr("Value"));
        item.put("line", row.getStr("Line"));
        item.put("lineName", this.firstNonBlank(row.getStr("LineName"), row.getStr("Line")));
        item.put("ttl", row.getInt("TTL"));
        item.put("priority", row.getInt("Priority"));
        item.put("status", row.getStr("Status"));
        Boolean locked = row.getBool("Locked");
        item.put("locked", locked != null && locked);
        item.put("weight", row.getInt("Weight"));
        item.put("remark", row.getStr("Remark"));
        return item;
    }

    private String sign(Map<String, String> params, String secret, String method) throws Exception {
        List<String> keys = new ArrayList<>(params.keySet());
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
        return URLEncoder.encode(StrUtil.nullToEmpty(value), StandardCharsets.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        boolean first = true;

        for (Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                query.append("&");
            }

            first = false;
            query.append(this.percentEncode(entry.getKey())).append("=").append(this.percentEncode(entry.getValue()));
        }

        return query.toString();
    }

    private void requireDomain(String domainName) {
        if (StrUtil.isBlank(domainName)) {
            throw new OciException("请输入域名");
        }
    }

    private void putIfNotBlank(Map<String, String> params, String key, String value) {
        if (StrUtil.isNotBlank(value)) {
            params.put(key, value.trim());
        }
    }

    private String normalizeLine(String line) {
        return StrUtil.blankToDefault(StrUtil.trimToNull(line), "default");
    }

    private boolean supportsPriority(String type) {
        return "MX".equalsIgnoreCase(type) || "SRV".equalsIgnoreCase(type);
    }

    private String parseString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer parseInteger(Object value) {
        if (value == null || StrUtil.isBlank(String.valueOf(value))) {
            return null;
        } else if (value instanceof Number n) {
            return n.intValue();
        } else {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (Exception var3) {
                return null;
            }
        }
    }

    private Map<String, Object> defaultLine(String code, String name) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("lineCode", code);
        line.put("lineName", name);
        line.put("lineDisplayName", name);
        return line;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        } else {
            for (String value : values) {
                if (StrUtil.isNotBlank(value)) {
                    return value;
                }
            }

            return null;
        }
    }

    private JSONObject requestPost(String action, Map<String, String> actionParams) {
        String ak = StrUtil.blankToDefault(StrUtil.trimToNull(this.getAccessKeyId()), "");
        String sk = StrUtil.blankToDefault(StrUtil.trimToNull(this.getAccessKeySecret()), "");
        if (!StrUtil.isBlank(ak) && !StrUtil.isBlank(sk)) {
            try {
                Map<String, String> params = new LinkedHashMap<>();
                params.put("Action", action);
                params.put("Version", "2015-01-09");
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
                } catch (Exception var13) {
                    throw new OciException("签名失败: " + var13.getMessage());
                }

                StringBuilder body = new StringBuilder();
                boolean first = true;

                for (Entry<String, String> e : params.entrySet()) {
                    if (!first) {
                        body.append("&");
                    }

                    first = false;
                    body.append(this.percentEncode(e.getKey())).append("=").append(this.percentEncode(e.getValue()));
                }

                HttpRequest request = HttpRequest.newBuilder(URI.create("https://alidns.aliyuncs.com"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString(body.toString()))
                    .build();

                HttpResponse<String> response;
                try {
                    response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
                } catch (InterruptedException var12) {
                    Thread.currentThread().interrupt();
                    throw new OciException("网络请求中断");
                }

                JSONObject json = JSONUtil.parseObj(response.body());
                String code = json.getStr("Code");
                if (code != null && !"200".equals(code)) {
                    throw new OciException(json.getStr("Message", "阿里云DNS调用失败"));
                } else {
                    return json;
                }
            } catch (Exception var14) {
                throw new OciException("阿里云DNS调用异常: " + var14.getMessage());
            }
        } else {
            throw new OciException("阿里云DNS未配置");
        }
    }

    private String parseDnsServersFromDomain(JSONObject dnsServers) {
        if (dnsServers == null) {
            return "not_system";
        } else {
            Object dnsServerObj = dnsServers.get("DnsServer");
            if (dnsServerObj == null) {
                return "not_system";
            } else {
                if (!(dnsServerObj instanceof JSONArray arr)) {
                    if (dnsServerObj instanceof String s && s.contains("alidns")) {
                        return "normal";
                    }
                } else {
                    for (int i = 0; i < arr.size(); i++) {
                        Object item = arr.get(i);
                        String serverStr = String.valueOf(item);
                        if (serverStr.startsWith("{")) {
                            try {
                                JSONObject inner = JSONUtil.parseObj(serverStr);
                                JSONArray innerArr = inner.getJSONArray("DnsServer");
                                if (innerArr != null) {
                                    for (int j = 0; j < innerArr.size(); j++) {
                                        if (String.valueOf(innerArr.get(j)).contains("alidns")) {
                                            return "normal";
                                        }
                                    }
                                }
                            } catch (Exception var11) {
                                if (serverStr.contains("alidns")) {
                                    return "normal";
                                }
                            }
                        } else if (serverStr.contains("alidns")) {
                            return "normal";
                        }
                    }
                }

                return "not_system";
            }
        }
    }
}
