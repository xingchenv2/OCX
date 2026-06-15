/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONObject
 *  cn.hutool.json.JSONUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.metadata.IPage
 *  com.baomidou.mybatisplus.extension.plugins.pagination.Page
 *  com.ocxworker.enums.SysCfgEnum
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.CfCfgMapper
 *  com.ocxworker.model.entity.CfCfg
 *  com.ocxworker.service.CloudflareService
 *  com.ocxworker.service.CloudflareService$Credentials
 *  com.ocxworker.service.CloudflareService$HttpBinaryResponse
 *  com.ocxworker.service.CloudflareService$MetricQuery
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.OciProxyConfigService
 *  com.ocxworker.util.CommonUtils
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.CfCfgMapper;
import com.ocxworker.model.entity.CfCfg;
import com.ocxworker.service.CloudflareService;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class CloudflareService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CloudflareService.class);
    private static final String CF_API_BASE = "https://api.cloudflare.com/client/v4";
    @Resource
    private CfCfgMapper cfCfgMapper;
    @Resource
    private NotificationService notificationService;
    @Lazy
    @Resource
    private OciProxyConfigService ociProxyConfigService;
    private static final Set<String> IP_ACCESS_TARGETS = Set.of("ip", "ip6", "ip_range", "country", "asn");
    private static final Set<String> IP_ACCESS_MODES = Set.of("block", "challenge", "js_challenge", "managed_challenge", "whitelist");
    private static final Set<String> TUNNEL_SERVICE_PREFIXES = Set.of("http://", "https://", "tcp://", "unix://", "ssh://", "rdp://", "smb://", "http_status:");
    private static final List<String> SSL_SETTING_IDS = List.of("ssl", "always_use_https", "min_tls_version", "tls_1_3");
    private static final List<String> CACHE_SETTING_IDS = List.of("cache_level", "browser_cache_ttl", "development_mode", "always_online");
    private static final List<String> SECURITY_SETTING_IDS = List.of("security_level", "bot_fight_mode", "browser_check");
    private static final Set<String> FIREWALL_ACTIONS = Set.of("block", "challenge", "js_challenge", "managed_challenge", "allow", "log", "bypass");
    private static final String CUSTOM_FIREWALL_PHASE = "http_request_firewall_custom";
    private static final Set<String> CUSTOM_FIREWALL_SKIP_ACTIONS = Set.of("execute", "skip");
    private static final Set<String> CUSTOM_FIREWALL_RULESET_KINDS = Set.of("zone", "custom", "root");
    private static final Set<String> DNS_TYPES = Set.of("A", "AAAA", "CNAME", "TXT", "MX", "NS", "SRV", "CAA", "HTTPS", "PTR");
    private static final String WORKER_HELLO_WORLD = "export default {\n  async fetch() {\n    return new Response('Hello World!', {\n      headers: { 'content-type': 'text/plain;charset=UTF-8' },\n    });\n  },\n};\n";
    private static final Map<String, String> WORKER_TEMPLATES = Map.of("json-api", "export default {\n  async fetch() {\n    return Response.json({ ok: true, message: 'Hello from Workers' });\n  },\n};\n", "html", "export default {\n  async fetch() {\n    const html = '<!DOCTYPE html><html><body><h1>Hello World</h1></body></html>';\n    return new Response(html, { headers: { 'content-type': 'text/html;charset=UTF-8' } });\n  },\n};\n");
    private static final int MAX_PAGES_UPLOAD_FILES = 100;
    private static final long MAX_PAGES_UPLOAD_BYTES = 0x1900000L;
    private static final Map<String, List<Map<String, String>>> PAGES_TEMPLATE_FILES = Map.of("static-starter", List.of(Map.of("path", "index.html", "content", "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head><meta charset=\"utf-8\"><title>Cloudflare Pages</title></head>\n<body><h1>Hello from Cloudflare Pages</h1></body>\n</html>\n")), "blog-starter", List.of(Map.of("path", "index.html", "content", "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head><meta charset=\"utf-8\"><title>My Blog</title></head>\n<body><h1>My Blog</h1><p>Powered by Cloudflare Pages.</p></body>\n</html>\n"), Map.of("path", "styles.css", "content", "body { font-family: system-ui, sans-serif; margin: 2rem; }")));

    public Map<String, Object> getAccountConfigForDisplay() {
        String accountId = this.notificationService.getKvValue(SysCfgEnum.CF_ACCOUNT_ID);
        String token = this.notificationService.getKvValue(SysCfgEnum.CF_API_TOKEN);
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("accountId", StrUtil.nullToEmpty((CharSequence)accountId));
        m.put("tokenConfigured", StrUtil.isNotBlank((CharSequence)token));
        m.put("apiToken", StrUtil.isBlank((CharSequence)token) ? "" : CloudflareService.maskSecret((String)token));
        m.put("configured", StrUtil.isNotBlank((CharSequence)accountId) && StrUtil.isNotBlank((CharSequence)token));
        return m;
    }

    public void saveAccountConfig(String accountId, String apiTokenFromClient) {
        String token;
        String curToken = this.notificationService.getKvValue(SysCfgEnum.CF_API_TOKEN);
        if (StrUtil.isNotBlank((CharSequence)accountId)) {
            this.notificationService.saveKvValue(SysCfgEnum.CF_ACCOUNT_ID, accountId.trim());
        }
        if (StrUtil.isNotBlank((CharSequence)(token = CloudflareService.resolveMasked((String)apiTokenFromClient, (String)curToken)))) {
            this.notificationService.saveKvValue(SysCfgEnum.CF_API_TOKEN, token.trim());
        }
    }

    public String testAccountConfig(String accountId, String apiTokenFromClient) {
        String token;
        String acc = StrUtil.trimToNull((CharSequence)accountId);
        if (acc == null) {
            acc = StrUtil.trimToNull((CharSequence)this.notificationService.getKvValue(SysCfgEnum.CF_ACCOUNT_ID));
        }
        if (StrUtil.isBlank((CharSequence)(token = CloudflareService.resolveMasked((String)apiTokenFromClient, (String)this.notificationService.getKvValue(SysCfgEnum.CF_API_TOKEN))))) {
            throw new OciException("\u8bf7\u5148\u586b\u5199 API Token");
        }
        this.verifyApiToken(token, acc);
        if (acc != null) {
            JSONObject accJson = CloudflareService.parseJson((String)this.apiGet(token, "https://api.cloudflare.com/client/v4/accounts/" + acc));
            CloudflareService.requireSuccess((JSONObject)accJson, (String)"Account ID \u65e0\u6548\u6216\u65e0\u6743\u9650");
            JSONObject result = accJson.getJSONObject((Object)"result");
            String name = result != null ? result.getStr((Object)"name") : acc;
            return "\u8fde\u63a5\u6210\u529f\uff1aToken \u6709\u6548\uff0c\u8d26\u6237 " + name;
        }
        return "\u8fde\u63a5\u6210\u529f\uff1aToken \u6709\u6548";
    }

    private void verifyApiToken(String token, String accountId) {
        if (CloudflareService.isAccountApiToken((String)token)) {
            if (StrUtil.isBlank((CharSequence)accountId)) {
                throw new OciException("\u8d26\u6237 API \u4ee4\u724c\uff08cfat_\uff09\u6d4b\u8bd5\u9700\u586b\u5199 Account ID");
            }
            JSONObject verify = CloudflareService.parseJson((String)this.apiGet(token, "https://api.cloudflare.com/client/v4/accounts/" + accountId.trim() + "/tokens/verify"));
            CloudflareService.requireSuccess((JSONObject)verify, (String)"Token \u9a8c\u8bc1\u5931\u8d25");
            return;
        }
        JSONObject verify = CloudflareService.parseJson((String)this.apiGet(token, "https://api.cloudflare.com/client/v4/user/tokens/verify"));
        CloudflareService.requireSuccess((JSONObject)verify, (String)"Token \u9a8c\u8bc1\u5931\u8d25");
    }

    private static boolean isAccountApiToken(String token) {
        return token != null && token.startsWith("cfat_");
    }

    public Map<String, Object> listZonesPage(int page, int perPage) {
        JSONObject info;
        Credentials c = this.requireCredentials();
        String url = String.format("%s/zones?page=%d&per_page=%d&account.id=%s", "https://api.cloudflare.com/client/v4", page, perPage, c.accountId());
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Zone \u5217\u8868\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map> zones = new ArrayList<Map>();
        if (result != null) {
            for (int i = 0; i < result.size(); ++i) {
                zones.add(CloudflareService.mapZoneSummary((JSONObject)result.getJSONObject((Object)i)));
            }
        }
        int total = (info = json.getJSONObject((Object)"result_info")) != null ? info.getInt((Object)"total_count", Integer.valueOf(zones.size())).intValue() : zones.size();
        int totalPages = info != null ? info.getInt((Object)"total_pages", Integer.valueOf(1)) : 1;
        int curPage = info != null ? info.getInt((Object)"page", Integer.valueOf(page)) : page;
        int curPerPage = info != null ? info.getInt((Object)"per_page", Integer.valueOf(perPage)) : perPage;
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("records", zones);
        out.put("total", total);
        out.put("page", curPage);
        out.put("perPage", curPerPage);
        out.put("totalPages", totalPages);
        return out;
    }

    public List<Map<String, Object>> listZones(int page, int perPage) {
        return (List)this.listZonesPage(page, perPage).get("records");
    }

    public Map<String, Object> getZoneDetail(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Zone \u8be6\u60c5\u5931\u8d25");
        JSONObject z = json.getJSONObject((Object)"result");
        if (z == null) {
            throw new OciException("Zone \u4e0d\u5b58\u5728");
        }
        Map map = CloudflareService.mapZoneSummary((JSONObject)z);
        JSONArray ns = z.getJSONArray((Object)"name_servers");
        ArrayList<String> nameServers = new ArrayList<String>();
        if (ns != null) {
            for (int i = 0; i < ns.size(); ++i) {
                nameServers.add(ns.getStr((Object)i));
            }
        }
        map.put("nameServers", nameServers);
        JSONObject plan = z.getJSONObject((Object)"plan");
        map.put("planName", plan != null ? plan.getStr((Object)"name") : null);
        return map;
    }

    public Map<String, Object> createZone(String name) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)name)) {
            throw new OciException("\u57df\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("name", name.trim());
        body.put("account", Map.of("id", c.accountId()));
        String url = "https://api.cloudflare.com/client/v4/zones";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa Zone \u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        if (result == null) {
            throw new OciException("\u521b\u5efa Zone \u5931\u8d25\uff1a\u65e0\u8fd4\u56de\u6570\u636e");
        }
        return CloudflareService.mapZoneSummary((JSONObject)result);
    }

    public void deleteZone(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public Map<String, Object> setZonePaused(String zoneId, boolean paused) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, Map.of("paused", paused)));
        CloudflareService.requireSuccess((JSONObject)json, (String)(paused ? "\u6682\u505c Zone \u5931\u8d25" : "\u6062\u590d Zone \u5931\u8d25"));
        JSONObject result = json.getJSONObject((Object)"result");
        return result != null ? CloudflareService.mapZoneSummary((JSONObject)result) : Map.of("paused", paused);
    }

    private static Map<String, Object> mapZoneSummary(JSONObject z) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", z.getStr((Object)"id"));
        map.put("name", z.getStr((Object)"name"));
        map.put("status", z.getStr((Object)"status"));
        map.put("paused", z.getBool((Object)"paused"));
        return map;
    }

    public List<Map<String, Object>> listTunnels() {
        Credentials c = this.requireCredentials();
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel?per_page=50";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Tunnel \u5217\u8868\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> tunnels = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return tunnels;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject t = result.getJSONObject((Object)i);
            tunnels.add(CloudflareService.mapTunnel((JSONObject)t));
        }
        return tunnels;
    }

    public Map<String, Object> createTunnel(String name) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)name)) {
            throw new OciException("Tunnel \u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Map<String, String> body = Map.of("name", name.trim(), "config_src", "cloudflare");
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa Tunnel \u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        if (result == null) {
            throw new OciException("\u521b\u5efa Tunnel \u5931\u8d25\uff1a\u65e0\u8fd4\u56de\u6570\u636e");
        }
        Map out = CloudflareService.mapTunnel((JSONObject)result);
        out.put("token", result.getStr((Object)"token"));
        return out;
    }

    public void deleteTunnel(String tunnelId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)tunnelId)) {
            throw new OciException("Tunnel ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel/" + tunnelId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public List<Map<String, Object>> listIpAccessRules() {
        Credentials c = this.requireCredentials();
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/firewall/access_rules/rules?per_page=100";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 IP \u8bbf\u95ee\u89c4\u5219\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            list.add(CloudflareService.mapIpAccessRule((JSONObject)result.getJSONObject((Object)i)));
        }
        return list;
    }

    public Map<String, Object> createIpAccessRule(String target, String value, String mode, String notes) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)target)) {
            throw new OciException("\u5339\u914d\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String tgt = target.trim().toLowerCase();
        if (!IP_ACCESS_TARGETS.contains(tgt)) {
            throw new OciException("\u4e0d\u652f\u6301\u7684\u5339\u914d\u7c7b\u578b: " + tgt);
        }
        if (StrUtil.isBlank((CharSequence)value)) {
            throw new OciException("\u5339\u914d\u503c\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (StrUtil.isBlank((CharSequence)mode)) {
            throw new OciException("\u52a8\u4f5c\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String act = mode.trim().toLowerCase();
        if (!IP_ACCESS_MODES.contains(act)) {
            throw new OciException("\u4e0d\u652f\u6301\u7684\u52a8\u4f5c: " + act);
        }
        LinkedHashMap<String, String> configuration = new LinkedHashMap<String, String>();
        configuration.put("target", tgt);
        configuration.put("value", value.trim());
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("mode", act);
        body.put("configuration", configuration);
        if (StrUtil.isNotBlank((CharSequence)notes)) {
            body.put("notes", notes.trim());
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/firewall/access_rules/rules";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa IP \u8bbf\u95ee\u89c4\u5219\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        if (result == null) {
            throw new OciException("\u521b\u5efa IP \u8bbf\u95ee\u89c4\u5219\u5931\u8d25\uff1a\u65e0\u8fd4\u56de\u6570\u636e");
        }
        return CloudflareService.mapIpAccessRule((JSONObject)result);
    }

    public void deleteIpAccessRule(String ruleId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/firewall/access_rules/rules/" + ruleId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    private static Map<String, Object> mapIpAccessRule(JSONObject r) {
        JSONObject scope;
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", r.getStr((Object)"id"));
        m.put("mode", r.getStr((Object)"mode"));
        m.put("notes", r.getStr((Object)"notes"));
        m.put("createdOn", r.getStr((Object)"created_on"));
        m.put("modifiedOn", r.getStr((Object)"modified_on"));
        JSONObject cfg = r.getJSONObject((Object)"configuration");
        if (cfg != null) {
            m.put("target", cfg.getStr((Object)"target"));
            m.put("value", cfg.getStr((Object)"value"));
        }
        if ((scope = r.getJSONObject((Object)"scope")) != null) {
            m.put("scopeType", scope.getStr((Object)"type"));
            m.put("scopeEmail", scope.getStr((Object)"email"));
        }
        return m;
    }

    public String getTunnelRunToken(String tunnelId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)tunnelId)) {
            throw new OciException("Tunnel ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel/" + tunnelId.trim() + "/token";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u83b7\u53d6 Tunnel Token \u5931\u8d25");
        String token = json.getStr((Object)"result");
        if (StrUtil.isBlank((CharSequence)token)) {
            throw new OciException("Tunnel Token \u4e3a\u7a7a");
        }
        return token;
    }

    public List<Map<String, Object>> listTunnelConnections(String tunnelId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)tunnelId)) {
            throw new OciException("Tunnel ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel/" + tunnelId.trim() + "/connections";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6\u8fde\u63a5\u72b6\u6001\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject client = result.getJSONObject((Object)i);
            JSONArray conns = client.getJSONArray((Object)"conns");
            if (conns != null && !conns.isEmpty()) {
                for (int j = 0; j < conns.size(); ++j) {
                    list.add(CloudflareService.mapTunnelConnection((JSONObject)conns.getJSONObject((Object)j), (JSONObject)client));
                }
                continue;
            }
            list.add(CloudflareService.mapTunnelConnection((JSONObject)client, null));
        }
        return list;
    }

    public List<Map<String, Object>> listTunnelRoutes(String tunnelId) {
        Credentials c = this.requireCredentials();
        this.requireTunnelId(tunnelId);
        return this.loadHostnameIngressRules(c, tunnelId.trim());
    }

    public Map<String, Object> addTunnelRoute(String tunnelId, String zoneId, String subdomain, String service) {
        Credentials c = this.requireCredentials();
        this.requireTunnelId(tunnelId);
        CloudflareService.requireZoneId((String)zoneId);
        String tid = tunnelId.trim();
        String svc = CloudflareService.validateTunnelService((String)service);
        Map zone = this.getZoneDetail(zoneId);
        String zoneName = String.valueOf(zone.get("name"));
        String hostname = CloudflareService.buildTunnelHostname((String)zoneName, (String)subdomain);
        List rules = this.loadHostnameIngressRules(c, tid);
        for (Map r : rules) {
            if (!hostname.equalsIgnoreCase(String.valueOf(r.get("hostname")))) continue;
            throw new OciException("\u8be5 Public Hostname \u5df2\u5b58\u5728: " + hostname);
        }
        LinkedHashMap<String, Object> newRule = new LinkedHashMap<String, Object>();
        newRule.put("hostname", hostname);
        newRule.put("service", svc);
        newRule.put("originRequest", Map.of());
        rules.add(newRule);
        Map dnsResult = this.ensureTunnelCname(c, zoneId.trim(), tid, hostname);
        this.putTunnelHostnameIngress(c, tid, rules);
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("hostname", hostname);
        out.put("service", svc);
        out.put("zoneId", zoneId.trim());
        out.put("zoneName", zoneName);
        out.putAll(dnsResult);
        return out;
    }

    public void deleteTunnelRoute(String tunnelId, String hostname) {
        Credentials c = this.requireCredentials();
        this.requireTunnelId(tunnelId);
        if (StrUtil.isBlank((CharSequence)hostname)) {
            throw new OciException("Public Hostname \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String host = hostname.trim().toLowerCase();
        List rules = this.loadHostnameIngressRules(c, tunnelId.trim());
        ArrayList<Map> kept = new ArrayList<Map>();
        boolean removed = false;
        for (Map r : rules) {
            String h = String.valueOf(r.get("hostname"));
            if (host.equalsIgnoreCase(h)) {
                removed = true;
                continue;
            }
            kept.add(r);
        }
        if (!removed) {
            throw new OciException("\u672a\u627e\u5230 Public Hostname: " + hostname);
        }
        this.putTunnelHostnameIngress(c, tunnelId.trim(), kept);
    }

    private void requireTunnelId(String tunnelId) {
        if (StrUtil.isBlank((CharSequence)tunnelId)) {
            throw new OciException("Tunnel ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
    }

    private static String validateTunnelService(String service) {
        if (StrUtil.isBlank((CharSequence)service)) {
            throw new OciException("Service URL \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String svc = service.trim();
        boolean ok = TUNNEL_SERVICE_PREFIXES.stream().anyMatch(svc::startsWith);
        if (!ok) {
            throw new OciException("Service URL \u987b\u4ee5 http://\u3001https://\u3001tcp:// \u7b49\u534f\u8bae\u5f00\u5934");
        }
        return svc;
    }

    private static String buildTunnelHostname(String zoneName, String subdomain) {
        if (StrUtil.isBlank((CharSequence)zoneName)) {
            throw new OciException("Zone \u57df\u540d\u65e0\u6548");
        }
        String zone = zoneName.trim().toLowerCase();
        String sub = StrUtil.blankToDefault((CharSequence)subdomain, (String)"").trim();
        if (sub.isEmpty() || "@".equals(sub)) {
            return zone;
        }
        if (sub.contains(".")) {
            String lower = sub.toLowerCase();
            if (!lower.endsWith("." + zone) && !lower.equals(zone)) {
                throw new OciException("\u5b50\u57df\u540d\u987b\u5c5e\u4e8e Zone: " + zone);
            }
            return lower;
        }
        return sub.toLowerCase() + "." + zone;
    }

    private JSONArray fetchTunnelIngressArray(Credentials c, String tunnelId) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel/" + tunnelId + "/configurations";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Tunnel \u914d\u7f6e\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        if (result == null) {
            return new JSONArray();
        }
        JSONObject config = result.getJSONObject((Object)"config");
        if (config == null) {
            return new JSONArray();
        }
        JSONArray ingress = config.getJSONArray((Object)"ingress");
        return ingress != null ? ingress : new JSONArray();
    }

    private List<Map<String, Object>> loadHostnameIngressRules(Credentials c, String tunnelId) {
        JSONArray ingress = this.fetchTunnelIngressArray(c, tunnelId);
        ArrayList<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < ingress.size(); ++i) {
            JSONObject originRequest;
            JSONObject row = ingress.getJSONObject((Object)i);
            if (row == null || CloudflareService.isCatchAllIngress((JSONObject)row)) continue;
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
            m.put("hostname", row.getStr((Object)"hostname"));
            m.put("service", row.getStr((Object)"service"));
            if (StrUtil.isNotBlank((CharSequence)row.getStr((Object)"path"))) {
                m.put("path", row.getStr((Object)"path"));
            }
            if ((originRequest = row.getJSONObject((Object)"originRequest")) != null && !originRequest.isEmpty()) {
                m.put("originRequest", (String)originRequest);
            }
            rules.add(m);
        }
        return rules;
    }

    private static boolean isCatchAllIngress(JSONObject row) {
        return StrUtil.isBlank((CharSequence)row.getStr((Object)"hostname"));
    }

    private void putTunnelHostnameIngress(Credentials c, String tunnelId, List<Map<String, Object>> hostnameRules) {
        ArrayList ingress = new ArrayList();
        for (Map<String, Object> rule : hostnameRules) {
            Map originRequest;
            LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("hostname", rule.get("hostname"));
            item.put("service", rule.get("service"));
            if (rule.get("path") != null) {
                item.put("path", rule.get("path"));
            }
            item.put("originRequest", (originRequest = rule.get("originRequest")) != null ? originRequest : Map.of());
            ingress.add(item);
        }
        LinkedHashMap<String, String> catchAll = new LinkedHashMap<String, String>();
        catchAll.put("service", "http_status:404");
        ingress.add(catchAll);
        Map body = Map.of("config", Map.of("ingress", ingress));
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/cfd_tunnel/" + tunnelId + "/configurations";
        JSONObject json = CloudflareService.parseJson((String)this.apiPut(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0 Tunnel \u8def\u7531\u5931\u8d25");
    }

    private Map<String, Object> ensureTunnelCname(Credentials c, String zoneId, String tunnelId, String hostname) {
        String target = tunnelId + ".cfargotunnel.com";
        String url = String.format("%s/zones/%s/dns_records?type=CNAME&name=%s", "https://api.cloudflare.com/client/v4", zoneId, URLEncoder.encode(hostname, StandardCharsets.UTF_8));
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u67e5\u8be2 DNS \u8bb0\u5f55\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("dnsCreated", false);
        out.put("dnsUpdated", false);
        if (result != null && !result.isEmpty()) {
            JSONObject rec = result.getJSONObject((Object)0);
            String recordType = rec.getStr((Object)"type");
            if (!"CNAME".equalsIgnoreCase(recordType)) {
                throw new OciException("DNS \u8bb0\u5f55 " + hostname + " \u5df2\u5b58\u5728\u4e14\u7c7b\u578b\u4e3a " + recordType + "\uff0c\u8bf7\u5148\u5220\u9664\u6216\u624b\u52a8\u6539\u4e3a CNAME");
            }
            String content = CloudflareService.normalizeDnsContent((String)rec.getStr((Object)"content"));
            if (target.equalsIgnoreCase(content)) {
                return out;
            }
            String recordId = rec.getStr((Object)"id");
            this.updateDnsRecord(zoneId, recordId, "CNAME", rec.getStr((Object)"name"), target, Boolean.valueOf(true), Integer.valueOf(1), null, "OCX Tunnel");
            out.put("dnsUpdated", true);
            return out;
        }
        this.addDnsRecord(zoneId, "CNAME", hostname, target, Boolean.valueOf(true), Integer.valueOf(1), null, "OCX Tunnel");
        out.put("dnsCreated", true);
        return out;
    }

    private static String normalizeDnsContent(String content) {
        if (content == null) {
            return "";
        }
        return content.endsWith(".") ? content.substring(0, content.length() - 1) : content;
    }

    private static Map<String, Object> mapTunnelConnection(JSONObject conn, JSONObject client) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("coloName", conn.getStr((Object)"colo_name"));
        m.put("uuid", StrUtil.blankToDefault((CharSequence)conn.getStr((Object)"uuid"), (String)conn.getStr((Object)"id")));
        m.put("isPendingReconnect", conn.getBool((Object)"is_pending_reconnect"));
        m.put("openedAt", conn.getStr((Object)"opened_at"));
        m.put("originIp", conn.getStr((Object)"origin_ip"));
        m.put("clientId", conn.getStr((Object)"client_id"));
        m.put("clientVersion", StrUtil.blankToDefault((CharSequence)conn.getStr((Object)"client_version"), client != null ? client.getStr((Object)"version") : null));
        if (client != null) {
            m.put("arch", client.getStr((Object)"arch"));
            m.put("runAt", client.getStr((Object)"run_at"));
        }
        return m;
    }

    public Map<String, Object> getSecuritySettings(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        for (String id : SECURITY_SETTING_IDS) {
            out.put(id, this.readZoneSettingValueOptional(c.apiToken(), zoneId, id));
        }
        return out;
    }

    public Map<String, Object> updateSecuritySetting(String zoneId, String settingId, Object value) {
        if (!SECURITY_SETTING_IDS.contains(settingId)) {
            throw new OciException("\u4e0d\u652f\u6301\u7684\u9632\u62a4\u8bbe\u7f6e: " + settingId);
        }
        if ("security_level".equals(settingId)) {
            String level;
            String string = level = value != null ? value.toString().trim().toLowerCase() : "";
            if (!Set.of("off", "essentially_off", "low", "medium", "high", "under_attack").contains(level)) {
                throw new OciException("\u65e0\u6548\u7684\u5b89\u5168\u7ea7\u522b: " + level);
            }
        }
        this.patchZoneSetting(zoneId, settingId, value);
        return this.getSecuritySettings(zoneId);
    }

    public Map<String, Object> getSslSettings(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        for (String id : SSL_SETTING_IDS) {
            out.put(id, this.readZoneSettingValue(c.apiToken(), zoneId, id));
        }
        return out;
    }

    public Map<String, Object> updateSslSetting(String zoneId, String settingId, Object value) {
        this.patchZoneSetting(zoneId, settingId, value);
        return this.getSslSettings(zoneId);
    }

    public Map<String, Object> getCacheSettings(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        for (String id : CACHE_SETTING_IDS) {
            out.put(id, this.readZoneSettingValue(c.apiToken(), zoneId, id));
        }
        return out;
    }

    public Map<String, Object> updateCacheSetting(String zoneId, String settingId, Object value) {
        this.patchZoneSetting(zoneId, settingId, value);
        return this.getCacheSettings(zoneId);
    }

    public void purgeZoneCache(String zoneId, boolean purgeEverything, List<String> files) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        if (purgeEverything) {
            body.put("purge_everything", true);
        } else if (files != null && !files.isEmpty()) {
            body.put("files", files);
        } else {
            throw new OciException("\u8bf7\u6307\u5b9a purge_everything \u6216 files");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/purge_cache";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u6e05\u7406\u7f13\u5b58\u5931\u8d25");
    }

    public List<Map<String, Object>> listFirewallRules(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        List list = this.listCustomFirewallRulesFromRulesets(c, zoneId.trim());
        Map eventCounts = this.fetchFirewallEventCounts24h(c, zoneId.trim());
        for (Map rule : list) {
            String id = (String)rule.get("id");
            if (!eventCounts.containsKey(id)) continue;
            rule.put("events24h", eventCounts.get(id));
        }
        return list;
    }

    public Map<String, Object> createFirewallRule(String zoneId, String action, String expression, String description, boolean paused) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String act = CloudflareService.normalizeFirewallAction((String)action);
        if (StrUtil.isBlank((CharSequence)expression)) {
            throw new OciException("\u8fc7\u6ee4\u8868\u8fbe\u5f0f\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String rulesetId = this.ensureCustomFirewallEntrypointRulesetId(c, zoneId.trim());
        Map body = CloudflareService.buildCustomFirewallRuleBody((String)act, (String)expression, (String)description, (!paused ? 1 : 0) != 0);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets/" + rulesetId + "/rules";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, (Object)body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa\u81ea\u5b9a\u4e49\u89c4\u5219\u5931\u8d25");
        JSONObject ruleset = json.getJSONObject((Object)"result");
        Map created = CloudflareService.findCustomFirewallRuleInRuleset((JSONObject)ruleset, null, (String)expression.trim(), (String)act);
        if (created == null) {
            throw new OciException("\u521b\u5efa\u81ea\u5b9a\u4e49\u89c4\u5219\u5931\u8d25\uff1a\u65e0\u8fd4\u56de\u6570\u636e");
        }
        return created;
    }

    public Map<String, Object> setFirewallRulePaused(String zoneId, String rulesetId, String ruleId, boolean paused) {
        return this.patchCustomFirewallRule(zoneId, rulesetId, ruleId, null, null, null, Boolean.valueOf(!paused));
    }

    public Map<String, Object> updateFirewallRule(String zoneId, String rulesetId, String ruleId, String action, String description, String expression, Boolean paused) {
        Boolean enabled = paused != null ? Boolean.valueOf(paused == false) : null;
        return this.patchCustomFirewallRule(zoneId, rulesetId, ruleId, action, description, expression, enabled);
    }

    public void deleteFirewallRule(String zoneId, String rulesetId, String ruleId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        CloudflareService.requireRulesetId((String)rulesetId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets/" + rulesetId.trim() + "/rules/" + ruleId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    private List<Map<String, Object>> listCustomFirewallRulesFromRulesets(Credentials c, String zoneId) {
        String listUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/rulesets";
        JSONObject listJson = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), listUrl));
        CloudflareService.requireSuccess((JSONObject)listJson, (String)"\u62c9\u53d6\u81ea\u5b9a\u4e49\u89c4\u5219\u5931\u8d25");
        JSONArray rulesets = listJson.getJSONArray((Object)"result");
        ArrayList<Map> zoneRules = new ArrayList<Map>();
        ArrayList<Map> customRules = new ArrayList<Map>();
        if (rulesets == null) {
            return new ArrayList<Map<String, Object>>();
        }
        int zoneOrder = 1;
        int customOrder = 1;
        for (int i = 0; i < rulesets.size(); ++i) {
            JSONArray ruleArr;
            JSONObject detail;
            String rulesetId;
            String summaryPhase;
            JSONObject summary = rulesets.getJSONObject((Object)i);
            String kind = summary.getStr((Object)"kind");
            if (kind == null || !CUSTOM_FIREWALL_RULESET_KINDS.contains(kind) || (summaryPhase = summary.getStr((Object)"phase")) != null && !"http_request_firewall_custom".equals(summaryPhase) || StrUtil.isBlank((CharSequence)(rulesetId = summary.getStr((Object)"id"))) || (detail = this.fetchZoneRulesetDetail(c, zoneId, rulesetId)) == null || !"http_request_firewall_custom".equals(detail.getStr((Object)"phase")) || (ruleArr = detail.getJSONArray((Object)"rules")) == null) continue;
            boolean entrypoint = "zone".equals(detail.getStr((Object)"kind"));
            for (int j = 0; j < ruleArr.size(); ++j) {
                JSONObject rule = ruleArr.getJSONObject((Object)j);
                String action = rule.getStr((Object)"action");
                if (action != null && CUSTOM_FIREWALL_SKIP_ACTIONS.contains(action)) continue;
                Map mapped = CloudflareService.mapCustomFirewallRule((String)rulesetId, (JSONObject)rule, (int)(entrypoint ? zoneOrder++ : customOrder++));
                if (entrypoint) {
                    zoneRules.add(mapped);
                    continue;
                }
                customRules.add(mapped);
            }
        }
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>(zoneRules.size() + customRules.size());
        list.addAll(zoneRules);
        list.addAll(customRules);
        for (int i = 0; i < list.size(); ++i) {
            ((Map)list.get(i)).put("position", i + 1);
        }
        return list;
    }

    public Map<String, Object> reorderFirewallRule(String zoneId, String rulesetId, String ruleId, String beforeRuleId, String afterRuleId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        CloudflareService.requireRulesetId((String)rulesetId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (beforeRuleId == null && StrUtil.isBlank((CharSequence)afterRuleId)) {
            throw new OciException("\u8bf7\u6307\u5b9a\u6392\u5e8f\u4f4d\u7f6e");
        }
        LinkedHashMap<String, String> position = new LinkedHashMap<String, String>();
        if (beforeRuleId != null) {
            position.put("before", beforeRuleId);
        } else {
            position.put("after", afterRuleId.trim());
        }
        Map body = Map.of("position", position);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets/" + rulesetId.trim() + "/rules/" + ruleId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u8c03\u6574\u89c4\u5219\u987a\u5e8f\u5931\u8d25");
        JSONObject ruleset = json.getJSONObject((Object)"result");
        Map updated = CloudflareService.findCustomFirewallRuleInRuleset((JSONObject)ruleset, (String)ruleId.trim(), null, null);
        if (updated != null) {
            return updated;
        }
        return Map.of("id", ruleId, "rulesetId", rulesetId);
    }

    private JSONObject fetchZoneRulesetDetail(Credentials c, String zoneId, String rulesetId) {
        String detailUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/rulesets/" + rulesetId;
        JSONObject detailJson = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), detailUrl));
        CloudflareService.requireSuccess((JSONObject)detailJson, (String)"\u62c9\u53d6 Ruleset \u8be6\u60c5\u5931\u8d25");
        return detailJson.getJSONObject((Object)"result");
    }

    private JSONObject tryFetchCustomFirewallEntrypoint(Credentials c, String zoneId) {
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/rulesets/phases/http_request_firewall_custom/entrypoint";
        try {
            JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
            CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 entrypoint \u5931\u8d25");
            return json.getJSONObject((Object)"result");
        }
        catch (OciException e) {
            if (e.getMessage() != null && e.getMessage().contains("HTTP 404")) {
                return null;
            }
            throw e;
        }
    }

    private String ensureCustomFirewallEntrypointRulesetId(Credentials c, String zoneId) {
        JSONObject entry = this.tryFetchCustomFirewallEntrypoint(c, zoneId);
        if (entry != null && StrUtil.isNotBlank((CharSequence)entry.getStr((Object)"id"))) {
            return entry.getStr((Object)"id");
        }
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("kind", "zone");
        body.put("phase", "http_request_firewall_custom");
        body.put("name", "default");
        body.put("description", "Zone custom firewall rules");
        body.put("rules", List.of());
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/rulesets";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa\u81ea\u5b9a\u4e49\u89c4\u5219 entrypoint \u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        if (result == null || StrUtil.isBlank((CharSequence)result.getStr((Object)"id"))) {
            throw new OciException("\u521b\u5efa\u81ea\u5b9a\u4e49\u89c4\u5219 entrypoint \u5931\u8d25\uff1a\u65e0 ruleset ID");
        }
        return result.getStr((Object)"id");
    }

    private Map<String, Object> patchCustomFirewallRule(String zoneId, String rulesetId, String ruleId, String action, String description, String expression, Boolean enabled) {
        String desc;
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        CloudflareService.requireRulesetId((String)rulesetId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        JSONObject existing = this.fetchCustomFirewallRule(c, zoneId.trim(), rulesetId.trim(), ruleId.trim());
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("action", StrUtil.isNotBlank((CharSequence)action) ? CloudflareService.normalizeFirewallAction((String)action) : existing.getStr((Object)"action"));
        body.put("expression", StrUtil.isNotBlank((CharSequence)expression) ? expression.trim() : existing.getStr((Object)"expression"));
        String string = desc = description != null ? description : existing.getStr((Object)"description");
        if (StrUtil.isNotBlank((CharSequence)desc)) {
            body.put("description", desc);
        }
        boolean ruleEnabled = enabled != null ? enabled : existing.getBool((Object)"enabled", Boolean.valueOf(true));
        body.put("enabled", ruleEnabled);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets/" + rulesetId.trim() + "/rules/" + ruleId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0\u81ea\u5b9a\u4e49\u89c4\u5219\u5931\u8d25");
        JSONObject ruleset = json.getJSONObject((Object)"result");
        Map updated = CloudflareService.findCustomFirewallRuleInRuleset((JSONObject)ruleset, (String)ruleId.trim(), null, null);
        if (updated != null) {
            return updated;
        }
        Map fallback = CloudflareService.mapCustomFirewallRule((String)rulesetId.trim(), (JSONObject)existing, (int)-1);
        fallback.put("paused", !ruleEnabled);
        return fallback;
    }

    private JSONObject fetchCustomFirewallRule(Credentials c, String zoneId, String rulesetId, String ruleId) {
        JSONObject detail = this.fetchZoneRulesetDetail(c, zoneId, rulesetId);
        if (detail == null) {
            throw new OciException("Ruleset \u4e0d\u5b58\u5728");
        }
        JSONArray rules = detail.getJSONArray((Object)"rules");
        if (rules != null) {
            for (int i = 0; i < rules.size(); ++i) {
                JSONObject rule = rules.getJSONObject((Object)i);
                if (!ruleId.equals(rule.getStr((Object)"id"))) continue;
                return rule;
            }
        }
        throw new OciException("\u81ea\u5b9a\u4e49\u89c4\u5219\u4e0d\u5b58\u5728");
    }

    private static Map<String, Object> buildCustomFirewallRuleBody(String action, String expression, String description, boolean enabled) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("action", action);
        body.put("expression", expression.trim());
        body.put("enabled", enabled);
        String desc = StrUtil.trimToNull((CharSequence)description);
        if (desc != null) {
            body.put("description", desc);
        }
        return body;
    }

    private static String normalizeFirewallAction(String action) {
        if (StrUtil.isBlank((CharSequence)action)) {
            throw new OciException("\u9632\u706b\u5899\u52a8\u4f5c\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String act = action.trim().toLowerCase();
        if (!FIREWALL_ACTIONS.contains(act)) {
            throw new OciException("\u4e0d\u652f\u6301\u7684\u9632\u706b\u5899\u52a8\u4f5c: " + act);
        }
        return act;
    }

    private static void requireRulesetId(String rulesetId) {
        if (StrUtil.isBlank((CharSequence)rulesetId)) {
            throw new OciException("Ruleset ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
    }

    private static Map<String, Object> mapCustomFirewallRule(String rulesetId, JSONObject rule, int order) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", rule.getStr((Object)"id"));
        m.put("rulesetId", rulesetId);
        m.put("description", rule.getStr((Object)"description"));
        m.put("action", rule.getStr((Object)"action"));
        m.put("expression", rule.getStr((Object)"expression"));
        boolean enabled = rule.getBool((Object)"enabled", Boolean.valueOf(true));
        m.put("enabled", enabled);
        m.put("paused", !enabled);
        m.put("ref", rule.getStr((Object)"ref"));
        if (order > 0) {
            m.put("position", order);
        }
        return m;
    }

    private static Map<String, Object> findCustomFirewallRuleInRuleset(JSONObject ruleset, String ruleId, String expression, String action) {
        if (ruleset == null) {
            return null;
        }
        String rulesetId = ruleset.getStr((Object)"id");
        JSONArray rules = ruleset.getJSONArray((Object)"rules");
        if (rules == null || StrUtil.isBlank((CharSequence)rulesetId)) {
            return null;
        }
        Map fallback = null;
        for (int i = 0; i < rules.size(); ++i) {
            JSONObject rule = rules.getJSONObject((Object)i);
            if (StrUtil.isNotBlank((CharSequence)ruleId) && ruleId.equals(rule.getStr((Object)"id"))) {
                return CloudflareService.mapCustomFirewallRule((String)rulesetId, (JSONObject)rule, (int)-1);
            }
            if (!StrUtil.isNotBlank((CharSequence)expression) || !expression.equals(rule.getStr((Object)"expression"))) continue;
            String ruleAction = rule.getStr((Object)"action");
            if (action != null && !action.equalsIgnoreCase(ruleAction)) continue;
            fallback = CloudflareService.mapCustomFirewallRule((String)rulesetId, (JSONObject)rule, (int)-1);
        }
        return fallback;
    }

    private Map<String, Integer> fetchFirewallEventCounts24h(Credentials c, String zoneId) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<String, Integer>();
        try {
            Instant until = Instant.now();
            Instant since = until.minus(24L, ChronoUnit.HOURS);
            LinkedHashMap<String, String> filter = new LinkedHashMap<String, String>();
            filter.put("datetime_geq", since.toString());
            filter.put("datetime_leq", until.toString());
            LinkedHashMap<String, Object> variables = new LinkedHashMap<String, Object>();
            variables.put("zoneTag", zoneId);
            variables.put("filter", filter);
            String gql = "query FWEvents($zoneTag: string!, $filter: FirewallEventsAdaptiveFilter_InputObject!) {\n  viewer {\n    zones(filter: { zoneTag: $zoneTag }) {\n      firewallEventsAdaptive(filter: $filter, limit: 10000) {\n        ruleId\n      }\n    }\n  }\n}";
            Map payload = Map.of("query", gql, "variables", variables);
            JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), "https://api.cloudflare.com/client/v4/graphql", payload));
            JSONObject data = json.getJSONObject((Object)"data");
            if (data == null) {
                return counts;
            }
            JSONObject viewer = data.getJSONObject((Object)"viewer");
            if (viewer == null) {
                return counts;
            }
            JSONArray zones = viewer.getJSONArray((Object)"zones");
            if (zones == null || zones.isEmpty()) {
                return counts;
            }
            JSONArray events = zones.getJSONObject((Object)0).getJSONArray((Object)"firewallEventsAdaptive");
            if (events == null) {
                return counts;
            }
            for (int i = 0; i < events.size(); ++i) {
                JSONObject ev = events.getJSONObject((Object)i);
                String ruleId = ev.getStr((Object)"ruleId");
                if (!StrUtil.isNotBlank((CharSequence)ruleId)) continue;
                counts.merge(ruleId, 1, Integer::sum);
            }
        }
        catch (Exception e) {
            log.debug("Firewall 24h events GraphQL skipped: {}", (Object)e.getMessage());
        }
        return counts;
    }

    public List<Map<String, Object>> listWorkersRoutes(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/workers/routes";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Workers \u8def\u7531\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject r = result.getJSONObject((Object)i);
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
            m.put("id", r.getStr((Object)"id"));
            m.put("pattern", r.getStr((Object)"pattern"));
            m.put("script", r.getStr((Object)"script"));
            list.add(m);
        }
        return list;
    }

    public Map<String, Object> createWorkersRoute(String zoneId, String pattern, String script) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)pattern)) {
            throw new OciException("\u8def\u7531 pattern \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (StrUtil.isBlank((CharSequence)script)) {
            throw new OciException("Worker \u811a\u672c\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Map<String, String> body = Map.of("pattern", pattern.trim(), "script", script.trim());
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/workers/routes";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa Workers \u8def\u7531\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        if (result != null) {
            m.put("id", result.getStr((Object)"id"));
            m.put("pattern", result.getStr((Object)"pattern"));
            m.put("script", result.getStr((Object)"script"));
        }
        return m;
    }

    public void deleteWorkersRoute(String zoneId, String routeId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)routeId)) {
            throw new OciException("\u8def\u7531 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/workers/routes/" + routeId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public List<Map<String, Object>> listZoneRules(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String listUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets";
        JSONObject listJson = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), listUrl));
        CloudflareService.requireSuccess((JSONObject)listJson, (String)"\u62c9\u53d6 Rulesets \u5931\u8d25");
        JSONArray rulesets = listJson.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
        if (rulesets == null) {
            return rules;
        }
        Set<String> userKinds = Set.of("zone", "custom", "root");
        for (int i = 0; i < rulesets.size(); ++i) {
            String rulesetId;
            JSONObject summary = rulesets.getJSONObject((Object)i);
            String kind = summary.getStr((Object)"kind");
            if (kind == null || !userKinds.contains(kind) || StrUtil.isBlank((CharSequence)(rulesetId = summary.getStr((Object)"id")))) continue;
            String detailUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/rulesets/" + rulesetId.trim();
            JSONObject detailJson = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), detailUrl));
            CloudflareService.requireSuccess((JSONObject)detailJson, (String)"\u62c9\u53d6 Ruleset \u8be6\u60c5\u5931\u8d25");
            JSONObject detail = detailJson.getJSONObject((Object)"result");
            if (detail == null) continue;
            String phase = detail.getStr((Object)"phase");
            String rulesetName = detail.getStr((Object)"name");
            JSONArray ruleArr = detail.getJSONArray((Object)"rules");
            if (ruleArr == null) continue;
            for (int j = 0; j < ruleArr.size(); ++j) {
                JSONObject rule = ruleArr.getJSONObject((Object)j);
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", rule.getStr((Object)"id"));
                m.put("ref", rule.getStr((Object)"ref"));
                m.put("rulesetId", rulesetId);
                m.put("phase", phase);
                m.put("rulesetName", rulesetName);
                m.put("description", rule.getStr((Object)"description"));
                m.put("expression", rule.getStr((Object)"expression"));
                m.put("action", rule.getStr((Object)"action"));
                m.put("enabled", rule.getBool((Object)"enabled", Boolean.valueOf(true)));
                rules.add(m);
            }
        }
        return rules;
    }

    @Deprecated
    public List<Map<String, Object>> listPageRules(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/pagerules?status=active,disabled";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Page Rules \u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject r = result.getJSONObject((Object)i);
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", r.getStr((Object)"id"));
            m.put("status", r.getStr((Object)"status"));
            m.put("priority", r.getInt((Object)"priority"));
            m.put("targets", r.get((Object)"targets"));
            m.put("actions", r.get((Object)"actions"));
            list.add(m);
        }
        return list;
    }

    public void deletePageRule(String zoneId, String ruleId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/pagerules/" + ruleId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public List<Map<String, Object>> listWorkerScripts() {
        return this.listWorkers();
    }

    private Object readZoneSettingValue(String token, String zoneId, String settingId) {
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/settings/" + settingId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(token, url));
        CloudflareService.requireSuccess((JSONObject)json, (String)("\u8bfb\u53d6 Zone \u8bbe\u7f6e " + settingId + " \u5931\u8d25"));
        JSONObject result = json.getJSONObject((Object)"result");
        return result != null ? result.get((Object)"value") : null;
    }

    private Object readZoneSettingValueOptional(String token, String zoneId, String settingId) {
        try {
            return this.readZoneSettingValue(token, zoneId, settingId);
        }
        catch (Exception e) {
            log.debug("Zone setting {} skipped: {}", (Object)settingId, (Object)e.getMessage());
            return null;
        }
    }

    private void patchZoneSetting(String zoneId, String settingId, Object value) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)settingId)) {
            throw new OciException("\u8bbe\u7f6e\u9879\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/settings/" + settingId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, Map.of("value", value)));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0 Zone \u8bbe\u7f6e\u5931\u8d25");
    }

    public Page<CfCfg> listCfgPage(int current, int size) {
        return (Page)this.cfCfgMapper.selectPage((IPage)new Page((long)current, (long)size), (Wrapper)new LambdaQueryWrapper().orderByDesc(CfCfg::getCreateTime));
    }

    public void addCfg(CfCfg cfg) {
        cfg.setId(CommonUtils.generateId());
        cfg.setCreateTime(LocalDateTime.now());
        this.cfCfgMapper.insert((Object)cfg);
    }

    public void removeCfg(String id) {
        this.cfCfgMapper.deleteById((Serializable)((Object)id));
    }

    public Map<String, Object> listDnsRecordsPage(String zoneId, int page, int perPage, String search, String type) {
        JSONObject info;
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        Map workerByHost = this.fetchWorkerDomainsByHostname(c, zoneId);
        Map tunnelByCname = this.fetchTunnelsByCnameTarget(c);
        if (StrUtil.isNotBlank((CharSequence)type) && "WORKER".equalsIgnoreCase(type.trim())) {
            return this.listWorkerBoundDnsRecordsPage(c, zoneId, page, perPage, search, workerByHost);
        }
        if (StrUtil.isNotBlank((CharSequence)type) && "TUNNEL".equalsIgnoreCase(type.trim())) {
            return this.listTunnelBoundDnsRecordsPage(c, zoneId, page, perPage, search, tunnelByCname, workerByHost);
        }
        StringBuilder url = new StringBuilder(String.format("%s/zones/%s/dns_records?page=%d&per_page=%d", "https://api.cloudflare.com/client/v4", zoneId.trim(), page, perPage));
        if (StrUtil.isNotBlank((CharSequence)search)) {
            url.append("&name.contains=").append(URLEncoder.encode(search.trim(), StandardCharsets.UTF_8));
        }
        if (StrUtil.isNotBlank((CharSequence)type)) {
            url.append("&type=").append(URLEncoder.encode(type.trim().toUpperCase(), StandardCharsets.UTF_8));
        }
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url.toString()));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 DNS \u8bb0\u5f55\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map> records = new ArrayList<Map>();
        if (result != null) {
            for (int i = 0; i < result.size(); ++i) {
                Map map = CloudflareService.mapDnsRecord((JSONObject)result.getJSONObject((Object)i));
                CloudflareService.applyWorkerEnrichment((Map)map, (Map)workerByHost);
                CloudflareService.applyTunnelEnrichment((Map)map, (Map)tunnelByCname);
                records.add(map);
            }
        }
        int total = (info = json.getJSONObject((Object)"result_info")) != null ? info.getInt((Object)"total_count", Integer.valueOf(records.size())).intValue() : records.size();
        int totalPages = info != null ? info.getInt((Object)"total_pages", Integer.valueOf(1)) : 1;
        int curPage = info != null ? info.getInt((Object)"page", Integer.valueOf(page)) : page;
        int curPerPage = info != null ? info.getInt((Object)"per_page", Integer.valueOf(perPage)) : perPage;
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("records", records);
        out.put("total", total);
        out.put("page", curPage);
        out.put("perPage", curPerPage);
        out.put("totalPages", totalPages);
        return out;
    }

    private Map<String, Map<String, Object>> fetchWorkerDomainsByHostname(Credentials c, String zoneId) {
        LinkedHashMap<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
        int wp = 1;
        int wPerPage = 50;
        while (true) {
            int totalPages;
            JSONObject json;
            String url = String.format("%s/accounts/%s/workers/domains?zone_id=%s&page=%d&per_page=%d", "https://api.cloudflare.com/client/v4", c.accountId(), zoneId.trim(), wp, wPerPage);
            try {
                json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
                CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Worker \u81ea\u5b9a\u4e49\u57df\u5931\u8d25");
            }
            catch (Exception e) {
                log.debug("workers/domains skipped for zone {}: {}", (Object)zoneId, (Object)e.getMessage());
                break;
            }
            JSONArray result = json.getJSONArray((Object)"result");
            if (result == null || result.isEmpty()) break;
            for (int i = 0; i < result.size(); ++i) {
                JSONObject d = result.getJSONObject((Object)i);
                String hostname = d.getStr((Object)"hostname");
                if (StrUtil.isBlank((CharSequence)hostname)) continue;
                String key = CloudflareService.normalizeDnsHostname((String)hostname);
                LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
                info.put("workerDomainId", d.getStr((Object)"id"));
                info.put("service", d.getStr((Object)"service"));
                info.put("hostname", hostname);
                map.put(key, info);
            }
            JSONObject info = json.getJSONObject((Object)"result_info");
            int n = totalPages = info != null ? info.getInt((Object)"total_pages", Integer.valueOf(1)) : 1;
            if (wp >= totalPages) break;
            ++wp;
        }
        return map;
    }

    private Map<String, Object> listWorkerBoundDnsRecordsPage(Credentials c, String zoneId, int page, int perPage, String search, Map<String, Map<String, Object>> workerByHost) {
        int to;
        ArrayList matched = new ArrayList();
        String searchLower = StrUtil.isNotBlank((CharSequence)search) ? search.trim().toLowerCase() : null;
        ArrayList<String> hosts = new ArrayList<String>(workerByHost.keySet());
        hosts.sort(String::compareTo);
        for (String hostKey : hosts) {
            String lookupName;
            LinkedHashMap<String, Object> rec;
            Map<String, Object> wd = workerByHost.get(hostKey);
            String hostname = (String)wd.get("hostname");
            if (searchLower != null) {
                String service;
                Object hay = hostKey;
                if (hostname != null) {
                    hay = (String)hay + " " + hostname.toLowerCase();
                }
                if ((service = (String)wd.get("service")) != null) {
                    hay = (String)hay + " " + service.toLowerCase();
                }
                if (!((String)hay).contains(searchLower)) continue;
            }
            if ((rec = this.lookupDnsRecordByHostname(c, zoneId, lookupName = StrUtil.isNotBlank((CharSequence)hostname) ? hostname : hostKey)) == null) {
                rec = new LinkedHashMap<String, Object>();
                rec.put("id", "");
                rec.put("name", lookupName);
                rec.put("type", "AAAA");
                rec.put("content", "100::");
                rec.put("proxied", true);
                rec.put("ttl", 1);
            }
            CloudflareService.applyWorkerEnrichment((Map)rec, workerByHost);
            matched.add(rec);
        }
        int total = matched.size();
        int from = Math.max(0, (page - 1) * perPage);
        List pageRecords = from < (to = Math.min(total, from + perPage)) ? matched.subList(from, to) : List.of();
        int totalPages = total == 0 ? 1 : (total + perPage - 1) / perPage;
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("records", new ArrayList(pageRecords));
        out.put("total", total);
        out.put("page", page);
        out.put("perPage", perPage);
        out.put("totalPages", totalPages);
        return out;
    }

    private Map<String, Object> lookupDnsRecordByHostname(Credentials c, String zoneId, String hostname) {
        if (StrUtil.isBlank((CharSequence)hostname)) {
            return null;
        }
        String url = String.format("%s/zones/%s/dns_records?name=%s&per_page=20", "https://api.cloudflare.com/client/v4", zoneId.trim(), URLEncoder.encode(hostname.trim(), StandardCharsets.UTF_8));
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u67e5\u8be2 DNS \u8bb0\u5f55\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        if (result == null) {
            return null;
        }
        String want = CloudflareService.normalizeDnsHostname((String)hostname);
        for (int i = 0; i < result.size(); ++i) {
            JSONObject r = result.getJSONObject((Object)i);
            if (!want.equals(CloudflareService.normalizeDnsHostname((String)r.getStr((Object)"name")))) continue;
            return CloudflareService.mapDnsRecord((JSONObject)r);
        }
        return null;
    }

    private static void applyWorkerEnrichment(Map<String, Object> rec, Map<String, Map<String, Object>> workerByHost) {
        if (workerByHost == null || workerByHost.isEmpty() || rec == null) {
            return;
        }
        String name = CloudflareService.normalizeDnsHostname((String)((String)rec.get("name")));
        Map<String, Object> wd = workerByHost.get(name);
        if (wd == null) {
            return;
        }
        rec.put("rawType", rec.get("type"));
        rec.put("rawContent", rec.get("content"));
        rec.put("workerBound", true);
        rec.put("workerDomainId", wd.get("workerDomainId"));
        rec.put("workerService", wd.get("service"));
        rec.put("type", "Worker");
        Object service = wd.get("service");
        rec.put("content", service != null ? service.toString() : "");
    }

    private Map<String, Map<String, Object>> fetchTunnelsByCnameTarget(Credentials c) {
        LinkedHashMap<String, Map<String, Object>> map = new LinkedHashMap<String, Map<String, Object>>();
        int tp = 1;
        int tPerPage = 50;
        while (true) {
            int totalPages;
            JSONObject json;
            String url = String.format("%s/accounts/%s/cfd_tunnel?per_page=%d&page=%d&is_deleted=false", "https://api.cloudflare.com/client/v4", c.accountId(), tPerPage, tp);
            try {
                json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
                CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Tunnel \u5217\u8868\u5931\u8d25");
            }
            catch (Exception e) {
                log.debug("cfd_tunnel list skipped: {}", (Object)e.getMessage());
                break;
            }
            JSONArray result = json.getJSONArray((Object)"result");
            if (result == null || result.isEmpty()) break;
            for (int i = 0; i < result.size(); ++i) {
                JSONObject t = result.getJSONObject((Object)i);
                String tunnelId = t.getStr((Object)"id");
                if (StrUtil.isBlank((CharSequence)tunnelId)) continue;
                String name = t.getStr((Object)"name");
                String cnameTarget = (tunnelId.trim() + ".cfargotunnel.com").toLowerCase();
                LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
                info.put("tunnelId", tunnelId.trim());
                info.put("tunnelName", StrUtil.isNotBlank((CharSequence)name) ? name.trim() : tunnelId.trim());
                map.put(cnameTarget, info);
            }
            JSONObject info = json.getJSONObject((Object)"result_info");
            int n = totalPages = info != null ? info.getInt((Object)"total_pages", Integer.valueOf(1)) : 1;
            if (tp >= totalPages) break;
            ++tp;
        }
        return map;
    }

    private Map<String, Object> listTunnelBoundDnsRecordsPage(Credentials c, String zoneId, int page, int perPage, String search, Map<String, Map<String, Object>> tunnelByCname, Map<String, Map<String, Object>> workerByHost) {
        int totalPages;
        ArrayList<Map> matched = new ArrayList<Map>();
        String searchLower = StrUtil.isNotBlank((CharSequence)search) ? search.trim().toLowerCase() : null;
        int dp = 1;
        int dPerPage = 100;
        while (true) {
            JSONObject info;
            String url = String.format("%s/zones/%s/dns_records?page=%d&per_page=%d&type=CNAME", "https://api.cloudflare.com/client/v4", zoneId.trim(), dp, dPerPage);
            JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
            CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 DNS \u8bb0\u5f55\u5931\u8d25");
            JSONArray result = json.getJSONArray((Object)"result");
            if (result != null) {
                for (int i = 0; i < result.size(); ++i) {
                    Map map = CloudflareService.mapDnsRecord((JSONObject)result.getJSONObject((Object)i));
                    CloudflareService.applyWorkerEnrichment((Map)map, workerByHost);
                    CloudflareService.applyTunnelEnrichment((Map)map, tunnelByCname);
                    if (!Boolean.TRUE.equals(map.get("tunnelBound")) || searchLower != null && !CloudflareService.dnsRecordMatchesSearch((Map)map, (String)searchLower)) continue;
                    matched.add(map);
                }
            }
            int n = totalPages = (info = json.getJSONObject((Object)"result_info")) != null ? info.getInt((Object)"total_pages", Integer.valueOf(1)) : 1;
            if (dp >= totalPages) break;
            ++dp;
        }
        matched.sort(Comparator.comparing(m -> CloudflareService.normalizeDnsHostname((String)((String)m.get("name")))));
        int total = matched.size();
        int from = Math.max(0, (page - 1) * perPage);
        int to = Math.min(total, from + perPage);
        List pageRecords = from < to ? matched.subList(from, to) : List.of();
        totalPages = total == 0 ? 1 : (total + perPage - 1) / perPage;
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("records", new ArrayList(pageRecords));
        out.put("total", total);
        out.put("page", page);
        out.put("perPage", perPage);
        out.put("totalPages", totalPages);
        return out;
    }

    private static boolean dnsRecordMatchesSearch(Map<String, Object> rec, String searchLower) {
        Object workerService;
        Object tunnelName;
        Object content;
        Object type;
        StringBuilder hay = new StringBuilder();
        Object name = rec.get("name");
        if (name != null) {
            hay.append(name.toString().toLowerCase()).append(' ');
        }
        if ((type = rec.get("type")) != null) {
            hay.append(type.toString().toLowerCase()).append(' ');
        }
        if ((content = rec.get("content")) != null) {
            hay.append(content.toString().toLowerCase()).append(' ');
        }
        if ((tunnelName = rec.get("tunnelName")) != null) {
            hay.append(tunnelName.toString().toLowerCase()).append(' ');
        }
        if ((workerService = rec.get("workerService")) != null) {
            hay.append(workerService.toString().toLowerCase()).append(' ');
        }
        return hay.toString().contains(searchLower);
    }

    private static void applyTunnelEnrichment(Map<String, Object> rec, Map<String, Map<String, Object>> tunnelByCname) {
        if (rec == null || Boolean.TRUE.equals(rec.get("workerBound")) || tunnelByCname == null || tunnelByCname.isEmpty()) {
            return;
        }
        Object typeObj = rec.get("type");
        if (typeObj == null || !"CNAME".equalsIgnoreCase(typeObj.toString())) {
            return;
        }
        String content = CloudflareService.normalizeDnsContent((String)(rec.get("content") != null ? rec.get("content").toString() : ""));
        if (!content.toLowerCase().endsWith(".cfargotunnel.com")) {
            return;
        }
        String key = content.toLowerCase();
        Map<String, Object> ti = tunnelByCname.get(key);
        if (ti == null) {
            String tunnelId = content.substring(0, content.length() - ".cfargotunnel.com".length()).trim();
            for (Map.Entry<String, Map<String, Object>> e : tunnelByCname.entrySet()) {
                if (!e.getKey().startsWith(tunnelId.toLowerCase())) continue;
                ti = e.getValue();
                break;
            }
            if (ti == null && StrUtil.isNotBlank((CharSequence)tunnelId)) {
                ti = new LinkedHashMap<String, Object>();
                ti.put("tunnelId", tunnelId);
                ti.put("tunnelName", tunnelId);
            }
        }
        if (ti == null) {
            return;
        }
        rec.put("rawType", rec.get("type"));
        rec.put("rawContent", rec.get("content"));
        rec.put("tunnelBound", true);
        rec.put("tunnelId", ti.get("tunnelId"));
        rec.put("tunnelName", ti.get("tunnelName"));
        rec.put("type", "\u96a7\u9053");
        Object tunnelName = ti.get("tunnelName");
        rec.put("content", tunnelName != null ? tunnelName.toString() : "");
    }

    private static String normalizeDnsHostname(String name) {
        if (name == null) {
            return "";
        }
        String n = name.trim().toLowerCase();
        if (n.endsWith(".")) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }

    public void deleteWorkerDomain(String workerDomainId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)workerDomainId)) {
            throw new OciException("Worker \u81ea\u5b9a\u4e49\u57df ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/domains/" + workerDomainId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public List<Map<String, Object>> listDnsRecords(String zoneId, int page, int perPage) {
        return (List)this.listDnsRecordsPage(zoneId, page, perPage, null, null).get("records");
    }

    private static Map<String, Object> mapDnsRecord(JSONObject r) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", r.getStr((Object)"id"));
        map.put("type", r.getStr((Object)"type"));
        map.put("name", r.getStr((Object)"name"));
        map.put("content", r.getStr((Object)"content"));
        map.put("proxied", r.getBool((Object)"proxied"));
        map.put("ttl", r.getInt((Object)"ttl"));
        if (r.containsKey((Object)"priority")) {
            map.put("priority", r.getInt((Object)"priority"));
        }
        if (r.containsKey((Object)"comment")) {
            map.put("comment", r.getStr((Object)"comment"));
        }
        return map;
    }

    private Map<String, Object> buildDnsBody(String type, String name, String content, Boolean proxied, Integer ttl, Integer priority, String comment) {
        if (StrUtil.isBlank((CharSequence)type)) {
            throw new OciException("DNS \u8bb0\u5f55\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String upperType = type.trim().toUpperCase();
        if (!DNS_TYPES.contains(upperType)) {
            throw new OciException("\u4e0d\u652f\u6301\u7684 DNS \u8bb0\u5f55\u7c7b\u578b: " + upperType);
        }
        if (StrUtil.isBlank((CharSequence)name)) {
            throw new OciException("DNS \u8bb0\u5f55\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (StrUtil.isBlank((CharSequence)content)) {
            throw new OciException("DNS \u8bb0\u5f55\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a");
        }
        LinkedHashMap<String, Object> b = new LinkedHashMap<String, Object>();
        b.put("type", upperType);
        b.put("name", name.trim());
        b.put("content", content.trim());
        b.put("ttl", ttl != null ? ttl : 1);
        if (proxied != null && Set.of("A", "AAAA", "CNAME").contains(upperType)) {
            b.put("proxied", proxied);
        }
        if (priority != null && Set.of("MX", "SRV").contains(upperType)) {
            b.put("priority", priority);
        }
        if (StrUtil.isNotBlank((CharSequence)comment)) {
            b.put("comment", comment.trim());
        }
        return b;
    }

    public void addDnsRecord(String zoneId, String type, String name, String content, Boolean proxied, Integer ttl, Integer priority, String comment) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = String.format("%s/zones/%s/dns_records", "https://api.cloudflare.com/client/v4", zoneId.trim());
        Map b = this.buildDnsBody(type, name, content, proxied, ttl, priority, comment);
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, (Object)b));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u6dfb\u52a0 DNS \u8bb0\u5f55\u5931\u8d25");
    }

    public void deleteDnsRecord(String zoneId, String recordId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)recordId)) {
            throw new OciException("DNS \u8bb0\u5f55 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = String.format("%s/zones/%s/dns_records/%s", "https://api.cloudflare.com/client/v4", zoneId.trim(), recordId.trim());
        this.apiDelete(c.apiToken(), url);
    }

    public void updateDnsRecord(String zoneId, String recordId, String type, String name, String content, Boolean proxied, Integer ttl, Integer priority, String comment) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)recordId)) {
            throw new OciException("DNS \u8bb0\u5f55 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = String.format("%s/zones/%s/dns_records/%s", "https://api.cloudflare.com/client/v4", zoneId.trim(), recordId.trim());
        Map b = this.buildDnsBody(type, name, content, proxied, ttl, priority, comment);
        JSONObject json = CloudflareService.parseJson((String)this.apiPut(c.apiToken(), url, (Object)b));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0 DNS \u8bb0\u5f55\u5931\u8d25");
    }

    public String exportDnsRecords(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/dns_records/export";
        return this.apiGet(c.apiToken(), url);
    }

    public void importDnsRecords(String zoneId, String bindContent, Boolean proxied) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)bindContent)) {
            throw new OciException("BIND \u6587\u4ef6\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/dns_records/import";
        LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
        if (proxied != null) {
            fields.put("proxied", proxied.toString());
        }
        JSONObject json = CloudflareService.parseJson((String)this.apiPostMultipart(c.apiToken(), url, "file", "zone.txt", bindContent, fields));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u5bfc\u5165 DNS \u8bb0\u5f55\u5931\u8d25");
    }

    public Map<String, Object> getDnssec(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/dnssec";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 DNSSEC \u72b6\u6001\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        if (result != null) {
            m.put("status", result.getStr((Object)"status"));
            m.put("algorithm", result.getStr((Object)"algorithm"));
            m.put("digest", result.getStr((Object)"digest"));
            m.put("digestAlgorithm", result.getStr((Object)"digest_algorithm"));
            m.put("digestType", result.getStr((Object)"digest_type"));
            m.put("ds", result.getStr((Object)"ds"));
            m.put("flags", result.getInt((Object)"flags"));
            m.put("keyTag", result.getInt((Object)"key_tag"));
            m.put("keyType", result.getStr((Object)"key_type"));
            m.put("modifiedOn", result.getStr((Object)"modified_on"));
            m.put("publicKey", result.getStr((Object)"public_key"));
        }
        return m;
    }

    public Map<String, Object> setDnssec(String zoneId, String status) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)status)) {
            throw new OciException("DNSSEC \u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String normalized = status.trim().toLowerCase();
        if (!"active".equals(normalized) && !"disabled".equals(normalized)) {
            throw new OciException("DNSSEC \u72b6\u6001\u4ec5\u652f\u6301 active \u6216 disabled");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/dnssec";
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, Map.of("status", normalized)));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0 DNSSEC \u72b6\u6001\u5931\u8d25");
        return this.getDnssec(zoneId);
    }

    public Map<String, Object> getEmailRoutingSettings(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Email Routing \u8bbe\u7f6e\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        return CloudflareService.mapEmailSettings((JSONObject)result);
    }

    public Map<String, Object> enableEmailRouting(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/enable";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, Map.of()));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u542f\u7528 Email Routing \u5931\u8d25");
        return CloudflareService.mapEmailSettings((JSONObject)json.getJSONObject((Object)"result"));
    }

    public void disableEmailRouting(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/disable";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, Map.of()));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u7981\u7528 Email Routing \u5931\u8d25");
    }

    public List<Map<String, Object>> getEmailRoutingDns(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/dns";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Email Routing DNS \u8bb0\u5f55\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject r = result.getJSONObject((Object)i);
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", r.getStr((Object)"id"));
            m.put("type", r.getStr((Object)"type"));
            m.put("name", r.getStr((Object)"name"));
            m.put("content", r.getStr((Object)"content"));
            m.put("priority", r.getInt((Object)"priority"));
            m.put("ttl", r.getInt((Object)"ttl"));
            m.put("proxied", r.getBool((Object)"proxied"));
            m.put("locked", r.getBool((Object)"locked"));
            list.add(m);
        }
        return list;
    }

    public void lockEmailDns(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/dns";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, Map.of()));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u9501\u5b9a Email Routing DNS \u5931\u8d25");
    }

    public void unlockEmailDns(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/dns";
        JSONObject json = CloudflareService.parseJson((String)this.apiPatch(c.apiToken(), url, Map.of()));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u89e3\u9501 Email Routing DNS \u5931\u8d25");
    }

    public List<Map<String, Object>> listEmailRoutingRules(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6\u90ae\u4ef6\u8def\u7531\u89c4\u5219\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            list.add(CloudflareService.mapEmailRule((JSONObject)result.getJSONObject((Object)i)));
        }
        return list;
    }

    public Map<String, Object> createEmailRoutingRule(String zoneId, String name, String customAddress, String actionType, List<String> destinations, String workerName, Integer priority, Boolean enabled) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)customAddress)) {
            throw new OciException("\u81ea\u5b9a\u4e49\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String act = StrUtil.blankToDefault((CharSequence)actionType, (String)"forward").trim().toLowerCase();
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("name", StrUtil.blankToDefault((CharSequence)name, (String)customAddress.trim()));
        body.put("enabled", enabled == null || enabled != false);
        if (priority != null) {
            body.put("priority", priority);
        }
        body.put("matchers", List.of(Map.of("type", "literal", "field", "to", "value", customAddress.trim())));
        body.put("actions", List.of(CloudflareService.buildEmailAction((String)act, destinations, (String)workerName)));
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa\u90ae\u4ef6\u8def\u7531\u89c4\u5219\u5931\u8d25");
        return CloudflareService.mapEmailRule((JSONObject)json.getJSONObject((Object)"result"));
    }

    public Map<String, Object> createEmailRoutingRule(String zoneId, String name, String customAddress, String destination, boolean enabled) {
        List dests = StrUtil.isBlank((CharSequence)destination) ? List.of() : List.of(destination.trim());
        return this.createEmailRoutingRule(zoneId, name, customAddress, "forward", dests, null, null, Boolean.valueOf(enabled));
    }

    public void deleteEmailRoutingRule(String zoneId, String ruleId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules/" + ruleId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public Map<String, Object> updateEmailRoutingRule(String zoneId, String ruleId, String name, String customAddress, String actionType, List<String> destinations, String workerName, Boolean enabled, Integer priority) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)ruleId)) {
            throw new OciException("\u89c4\u5219 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        if (StrUtil.isNotBlank((CharSequence)name)) {
            body.put("name", name.trim());
        }
        if (enabled != null) {
            body.put("enabled", enabled);
        }
        if (priority != null) {
            body.put("priority", priority);
        }
        if (StrUtil.isNotBlank((CharSequence)customAddress)) {
            body.put("matchers", List.of(Map.of("type", "literal", "field", "to", "value", customAddress.trim())));
        }
        if (StrUtil.isNotBlank((CharSequence)actionType)) {
            body.put("actions", List.of(CloudflareService.buildEmailAction((String)actionType.trim().toLowerCase(), destinations, (String)workerName)));
        }
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules/" + ruleId.trim();
        JSONObject json = CloudflareService.parseJson((String)this.apiPut(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0\u90ae\u4ef6\u8def\u7531\u89c4\u5219\u5931\u8d25");
        return CloudflareService.mapEmailRule((JSONObject)json.getJSONObject((Object)"result"));
    }

    public Map<String, Object> getCatchAllRule(String zoneId) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules/catch_all";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Catch-All \u89c4\u5219\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        return result != null ? CloudflareService.mapEmailRule((JSONObject)result) : Map.of();
    }

    public Map<String, Object> updateCatchAllRule(String zoneId, String actionType, List<String> destinations, String workerName, Boolean enabled) {
        Credentials c = this.requireCredentials();
        CloudflareService.requireZoneId((String)zoneId);
        if (StrUtil.isBlank((CharSequence)actionType)) {
            throw new OciException("Catch-All \u52a8\u4f5c\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
        }
        LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("enabled", enabled == null || enabled != false);
        body.put("actions", List.of(CloudflareService.buildEmailAction((String)actionType.trim().toLowerCase(), destinations, (String)workerName)));
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId.trim() + "/email/routing/rules/catch_all";
        JSONObject json = CloudflareService.parseJson((String)this.apiPut(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u66f4\u65b0 Catch-All \u89c4\u5219\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        return result != null ? CloudflareService.mapEmailRule((JSONObject)result) : Map.of();
    }

    public List<Map<String, Object>> listEmailDestinations() {
        Credentials c = this.requireCredentials();
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/email/routing/addresses";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6\u76ee\u6807\u90ae\u7bb1\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            list.add(CloudflareService.mapEmailDestination((JSONObject)result.getJSONObject((Object)i)));
        }
        return list;
    }

    public Map<String, Object> createEmailDestination(String email) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)email)) {
            throw new OciException("\u90ae\u7bb1\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/email/routing/addresses";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, Map.of("email", email.trim())));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u6dfb\u52a0\u76ee\u6807\u90ae\u7bb1\u5931\u8d25");
        return CloudflareService.mapEmailDestination((JSONObject)json.getJSONObject((Object)"result"));
    }

    public void deleteEmailDestination(String destinationId) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)destinationId)) {
            throw new OciException("\u76ee\u6807\u90ae\u7bb1 ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/email/routing/addresses/" + destinationId.trim();
        this.apiDelete(c.apiToken(), url);
    }

    public Map<String, Object> resendEmailDestination(String email) {
        Credentials c = this.requireCredentials();
        if (StrUtil.isBlank((CharSequence)email)) {
            throw new OciException("\u90ae\u7bb1\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/email/routing/addresses";
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, Map.of("email", email.trim())));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u91cd\u53d1\u9a8c\u8bc1\u90ae\u4ef6\u5931\u8d25");
        return CloudflareService.mapEmailDestination((JSONObject)json.getJSONObject((Object)"result"));
    }

    public List<Map<String, Object>> listWorkers() {
        Credentials c = this.requireCredentials();
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Workers \u5217\u8868\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject w = result.getJSONObject((Object)i);
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
            m.put("id", w.getStr((Object)"id"));
            m.put("createdOn", w.getStr((Object)"created_on"));
            m.put("modifiedOn", w.getStr((Object)"modified_on"));
            list.add(m);
        }
        return list;
    }

    public Map<String, Object> getWorkersUsageSummary() {
        Credentials c = this.requireCredentials();
        Instant now = Instant.now();
        Instant todayStart = now.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant monthStart = now.atZone(ZoneOffset.UTC).toLocalDate().withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        MetricQuery todayRequests = this.queryWorkersRequestsSum(c, todayStart, now);
        MetricQuery periodRequests = this.queryWorkersRequestsSum(c, monthStart, now);
        MetricQuery cpuTimeMs = this.queryWorkersCpuTimeMs(c, monthStart, now);
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("dateRangeLabel", CloudflareService.formatUsageDateRange((Instant)monthStart, (Instant)now));
        m.put("limitsNote", "\u4ee5\u4e0b\u4e3a Workers \u514d\u8d39\u7248\u53c2\u8003\u9650\u989d\uff1b\u4ed8\u8d39\u8d26\u6237\u9650\u989d\u4e0d\u540c");
        m.put("todayRequestsLimit", 100000L);
        m.put("todayObservabilityLimit", 200000L);
        CloudflareService.putMetric(m, (String)"todayRequests", (MetricQuery)todayRequests);
        CloudflareService.putMetric(m, (String)"periodRequests", (MetricQuery)periodRequests);
        CloudflareService.putMetric(m, (String)"cpuTimeMs", (MetricQuery)cpuTimeMs);
        m.put("todayObservabilityEvents", null);
        m.put("observabilityEvents", null);
        m.put("buildMinutes", null);
        m.put("observabilityAvailable", false);
        m.put("buildMinutesAvailable", false);
        return m;
    }

    private static void putMetric(Map<String, Object> m, String key, MetricQuery q) {
        m.put(key, q.available() ? Long.valueOf(q.value()) : null);
        m.put(key + "Available", q.available());
    }

    public List<Map<String, Object>> listWorkersAndPagesApplications() {
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        Credentials c = this.requireCredentials();
        String workersDevSubdomain = this.fetchAccountWorkersSubdomain(c);
        try {
            for (Map w : this.listWorkers()) {
                LinkedHashMap<String, String> row = new LinkedHashMap<String, String>(w);
                row.put("kind", "worker");
                String scriptName = String.valueOf(w.get("id"));
                row.put("name", scriptName);
                row.put("url", this.buildWorkerDevUrl(c, scriptName, workersDevSubdomain));
                items.add(row);
            }
        }
        catch (Exception e) {
            log.warn("Workers \u5217\u8868\u62c9\u53d6\u5931\u8d25: {}", (Object)e.getMessage());
        }
        try {
            items.addAll(this.listPagesProjects());
        }
        catch (Exception e) {
            log.warn("Pages \u5217\u8868\u62c9\u53d6\u5931\u8d25: {}", (Object)e.getMessage());
        }
        return items;
    }

    public List<Map<String, Object>> listWorkerTemplates() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(CloudflareService.templateMeta((String)"json-api", (String)"JSON API", (String)"\u8fd4\u56de JSON \u54cd\u5e94", (String)"worker"));
        list.add(CloudflareService.templateMeta((String)"html", (String)"HTML", (String)"\u8fd4\u56de\u7b80\u5355 HTML \u9875\u9762", (String)"worker"));
        list.add(CloudflareService.templateMeta((String)"static-starter", (String)"\u9759\u6001\u7ad9\u70b9", (String)"\u5355\u9875 HTML \u9759\u6001\u7ad9\u70b9", (String)"pages"));
        list.add(CloudflareService.templateMeta((String)"blog-starter", (String)"\u535a\u5ba2\u5165\u95e8", (String)"\u5e26\u6837\u5f0f\u7684\u7b80\u5355\u535a\u5ba2\u9996\u9875", (String)"pages"));
        return list;
    }

    private static Map<String, Object> templateMeta(String id, String name, String description, String kind) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", id);
        m.put("name", name);
        m.put("description", description);
        m.put("kind", kind);
        return m;
    }

    public Map<String, Object> getWorkersSubdomainInfo() {
        Credentials c = this.requireCredentials();
        String subdomain = this.fetchAccountWorkersSubdomain(c);
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("subdomain", subdomain);
        m.put("suffix", ".workers.dev");
        return m;
    }

    public Map<String, Object> getWorkersPagesTemplatePreview(String templateId) {
        if (StrUtil.isBlank((CharSequence)templateId)) {
            throw new OciException("\u6a21\u677f ID \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if ("hello-world".equals(templateId)) {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", "hello-world");
            m.put("kind", "worker");
            m.put("name", "Hello World");
            m.put("script", "export default {\n  async fetch() {\n    return new Response('Hello World!', {\n      headers: { 'content-type': 'text/plain;charset=UTF-8' },\n    });\n  },\n};\n");
            m.put("module", "worker.mjs");
            return m;
        }
        String workerCode = (String)WORKER_TEMPLATES.get(templateId);
        if (workerCode != null) {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", templateId);
            m.put("kind", "worker");
            m.put("script", workerCode);
            m.put("module", "worker.mjs");
            return m;
        }
        List pagesFiles = (List)PAGES_TEMPLATE_FILES.get(templateId);
        if (pagesFiles != null) {
            LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("id", templateId);
            m.put("kind", "pages");
            m.put("files", pagesFiles);
            return m;
        }
        throw new OciException("\u4e0d\u652f\u6301\u7684\u6a21\u677f: " + templateId);
    }

    public Map<String, Object> deployWorker(String scriptName, String scriptContent) {
        if (StrUtil.isBlank((CharSequence)scriptContent)) {
            throw new OciException("Worker \u4ee3\u7801\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Credentials c = this.requireCredentials();
        String name = CloudflareService.normalizeWorkerScriptName((String)scriptName);
        Map result = this.uploadWorkerScript(name, scriptContent.trim());
        this.enableWorkerSubdomain(c, name);
        String accountSubdomain = this.fetchAccountWorkersSubdomain(c);
        result.put("url", CloudflareService.resolveWorkerPublicUrl((String)name, (String)accountSubdomain));
        result.put("subdomainEnabled", true);
        return result;
    }

    public Map<String, Object> getWorkerScriptContent(String scriptName) {
        Credentials c = this.requireCredentials();
        String name = CloudflareService.normalizeWorkerScriptName((String)scriptName);
        HttpBinaryResponse resp = this.apiGetBinary(c.apiToken(), "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts/" + CloudflareService.urlEncodePath((String)name));
        Map parsed = CloudflareService.parseDownloadedWorkerScript((byte[])resp.body(), (String)resp.contentType());
        parsed.put("name", name);
        return parsed;
    }

    public Map<String, Object> updateWorkerScript(String scriptName, String scriptContent) {
        return this.deployWorker(scriptName, scriptContent);
    }

    public Map<String, Object> renameWorkerScript(String oldName, String newName) {
        String newNorm;
        String oldNorm = CloudflareService.normalizeWorkerScriptName((String)oldName);
        if (oldNorm.equals(newNorm = CloudflareService.normalizeWorkerScriptName((String)newName))) {
            throw new OciException("\u65b0\u540d\u79f0\u4e0d\u80fd\u4e0e\u5f53\u524d\u540d\u79f0\u76f8\u540c");
        }
        Map content = this.getWorkerScriptContent(oldNorm);
        String script = String.valueOf(content.get("script"));
        if (StrUtil.isBlank((CharSequence)script)) {
            throw new OciException("\u65e0\u6cd5\u8bfb\u53d6 Worker \u811a\u672c\u5185\u5bb9");
        }
        Map result = this.deployWorker(newNorm, script);
        this.deleteWorkerScriptInternal(oldNorm);
        result.put("oldName", oldNorm);
        result.put("name", newNorm);
        return result;
    }

    public void deleteWorkerScript(String scriptName) {
        this.deleteWorkerScriptInternal(CloudflareService.normalizeWorkerScriptName((String)scriptName));
    }

    private void deleteWorkerScriptInternal(String scriptName) {
        Credentials c = this.requireCredentials();
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts/" + CloudflareService.urlEncodePath((String)scriptName);
        this.apiDelete(c.apiToken(), url);
    }

    public Map<String, Object> createWorkerHelloWorld(String scriptName, String scriptContent) {
        String code = StrUtil.isNotBlank((CharSequence)scriptContent) ? scriptContent : "export default {\n  async fetch() {\n    return new Response('Hello World!', {\n      headers: { 'content-type': 'text/plain;charset=UTF-8' },\n    });\n  },\n};\n";
        return this.deployWorker(scriptName, code);
    }

    public Map<String, Object> createWorkerFromTemplate(String scriptName, String templateId, String scriptContent) {
        String code;
        String string = code = StrUtil.isNotBlank((CharSequence)scriptContent) ? scriptContent : (String)WORKER_TEMPLATES.get(templateId);
        if (code == null) {
            throw new OciException("\u4e0d\u652f\u6301\u7684 Worker \u6a21\u677f: " + templateId);
        }
        return this.deployWorker(scriptName, code);
    }

    public Map<String, Object> createPagesFromTemplate(String projectName, String templateId) {
        List files = (List)PAGES_TEMPLATE_FILES.get(templateId);
        if (files == null) {
            throw new OciException("\u4e0d\u652f\u6301\u7684 Pages \u6a21\u677f: " + templateId);
        }
        return this.deployPagesStaticFiles(projectName, files);
    }

    public Map<String, Object> deployPagesStaticFromUpload(String projectName, List<Map<String, String>> encodedFiles) {
        List decoded = this.decodePagesUploadFiles(encodedFiles);
        return this.deployPagesStaticFileEntries(projectName, decoded);
    }

    public Map<String, Object> deployPagesStaticFiles(String projectName, List<Map<String, String>> files) {
        return this.deployPagesStaticFileEntries(projectName, CloudflareService.toPagesFileBytes(files));
    }

    private Map<String, Object> deployPagesStaticFileEntries(String projectName, List<Map<String, Object>> fileEntries) {
        Credentials c = this.requireCredentials();
        String name = CloudflareService.normalizePagesProjectName((String)projectName);
        if (fileEntries == null || fileEntries.isEmpty()) {
            throw new OciException("\u8bf7\u81f3\u5c11\u4e0a\u4f20\u4e00\u4e2a\u6587\u4ef6");
        }
        this.ensurePagesProject(c, name);
        return this.createPagesDeployment(c, name, fileEntries);
    }

    private static List<Map<String, Object>> toPagesFileBytes(List<Map<String, String>> files) {
        ArrayList<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        if (files == null) {
            return out;
        }
        for (Map<String, String> f : files) {
            String path = f.get("path");
            String content = f.get("content");
            if (StrUtil.isBlank((CharSequence)path) || content == null) continue;
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("path", path);
            row.put("bytes", content.getBytes(StandardCharsets.UTF_8));
            out.add(row);
        }
        return out;
    }

    public List<Map<String, Object>> listPagesProjects() {
        Credentials c = this.requireCredentials();
        String apiUrl = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/pages/projects?per_page=50";
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), apiUrl));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u62c9\u53d6 Pages \u9879\u76ee\u5931\u8d25");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return list;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject latest;
            JSONObject p = result.getJSONObject((Object)i);
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
            m.put("kind", "pages");
            m.put("id", p.getStr((Object)"id"));
            m.put("name", p.getStr((Object)"name"));
            m.put("createdOn", p.getStr((Object)"created_on"));
            m.put("modifiedOn", p.getStr((Object)"modified_on"));
            String url = CloudflareService.resolvePagesProjectUrl((JSONObject)p);
            if (StrUtil.isNotBlank((CharSequence)url)) {
                m.put("url", url);
            }
            if ((latest = p.getJSONObject((Object)"latest_deployment")) != null) {
                if (StrUtil.isBlank((CharSequence)url) && StrUtil.isNotBlank((CharSequence)latest.getStr((Object)"url"))) {
                    m.put("url", latest.getStr((Object)"url"));
                }
                m.put("deploymentUrl", latest.getStr((Object)"url"));
                m.put("deploymentStatus", latest.getStr((Object)"latest_stage"));
            }
            list.add(m);
        }
        return list;
    }

    private static String resolvePagesProjectUrl(JSONObject p) {
        JSONObject latest = p.getJSONObject((Object)"latest_deployment");
        if (latest != null && StrUtil.isNotBlank((CharSequence)latest.getStr((Object)"url"))) {
            return latest.getStr((Object)"url");
        }
        String subdomain = p.getStr((Object)"subdomain");
        if (StrUtil.isNotBlank((CharSequence)subdomain)) {
            if (subdomain.startsWith("http://") || subdomain.startsWith("https://")) {
                return subdomain;
            }
            if (subdomain.contains(".")) {
                return "https://" + subdomain;
            }
            return "https://" + subdomain + ".pages.dev";
        }
        String name = p.getStr((Object)"name");
        if (StrUtil.isNotBlank((CharSequence)name)) {
            return "https://" + name + ".pages.dev";
        }
        return null;
    }

    private String fetchAccountWorkersSubdomain(Credentials c) {
        try {
            String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/subdomain";
            JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
            if (!json.getBool((Object)"success", Boolean.valueOf(false)).booleanValue()) {
                return null;
            }
            JSONObject result = json.getJSONObject((Object)"result");
            return result != null ? StrUtil.trimToNull((CharSequence)result.getStr((Object)"subdomain")) : null;
        }
        catch (Exception e) {
            log.debug("Workers \u5b50\u57df\u62c9\u53d6\u8df3\u8fc7: {}", (Object)e.getMessage());
            return null;
        }
    }

    private String buildWorkerDevUrl(Credentials c, String scriptName, String accountSubdomain) {
        if (StrUtil.isBlank((CharSequence)scriptName)) {
            return null;
        }
        try {
            String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts/" + CloudflareService.urlEncodePath((String)scriptName) + "/subdomain";
            JSONObject json = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), url));
            JSONObject result = json.getJSONObject((Object)"result");
            if (result != null && Boolean.FALSE.equals(result.getBool((Object)"enabled"))) {
                return null;
            }
        }
        catch (Exception e) {
            log.debug("Worker subdomain \u72b6\u6001\u8df3\u8fc7 {}: {}", (Object)scriptName, (Object)e.getMessage());
        }
        if (StrUtil.isNotBlank((CharSequence)accountSubdomain)) {
            return "https://" + scriptName + "." + accountSubdomain + ".workers.dev";
        }
        return "https://" + scriptName + ".workers.dev";
    }

    private void ensurePagesProject(Credentials c, String projectName) {
        String getUrl = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/pages/projects/" + CloudflareService.urlEncodePath((String)projectName);
        try {
            JSONObject getJson = CloudflareService.parseJson((String)this.apiGet(c.apiToken(), getUrl));
            if (getJson.getBool((Object)"success", Boolean.valueOf(false)).booleanValue()) {
                return;
            }
        }
        catch (OciException e) {
            log.debug("Pages \u9879\u76ee\u4e0d\u5b58\u5728\uff0c\u51c6\u5907\u521b\u5efa: {}", (Object)projectName);
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/pages/projects";
        Map<String, String> body = Map.of("name", projectName, "production_branch", "main");
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), url, body));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u521b\u5efa Pages \u9879\u76ee\u5931\u8d25");
    }

    private void enableWorkerSubdomain(Credentials c, String scriptName) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts/" + CloudflareService.urlEncodePath((String)scriptName) + "/subdomain";
        JSONObject json = CloudflareService.parseJson((String)this.apiPut(c.apiToken(), url, Map.of("enabled", true)));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u542f\u7528 workers.dev \u5b50\u57df\u5931\u8d25");
    }

    private static String resolveWorkerPublicUrl(String scriptName, String accountSubdomain) {
        if (StrUtil.isNotBlank((CharSequence)accountSubdomain)) {
            return "https://" + scriptName + "." + accountSubdomain + ".workers.dev";
        }
        return "https://" + scriptName + ".workers.dev";
    }

    private HttpBinaryResponse apiGetBinary(String token, String url) {
        try {
            byte[] body;
            HttpClient client = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).timeout(Duration.ofSeconds(60L)).GET().build();
            HttpResponse<byte[]> r = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            byte[] byArray = body = r.body() == null ? new byte[]{} : r.body();
            if (r.statusCode() < 200 || r.statusCode() >= 400) {
                String errBody = body.length == 0 ? "" : new String(body, StandardCharsets.UTF_8);
                String msg = CloudflareService.parseCfError((String)errBody);
                throw new OciException("HTTP " + r.statusCode() + (String)(msg != null ? ": " + msg : ""));
            }
            String contentType = r.headers().firstValue("Content-Type").orElse("");
            return new HttpBinaryResponse(contentType, body);
        }
        catch (OciException e) {
            throw e;
        }
        catch (IOException e) {
            throw new OciException("\u8bf7\u6c42\u5931\u8d25: " + e.getMessage());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("\u8bf7\u6c42\u4e2d\u65ad");
        }
    }

    private static Map<String, Object> parseDownloadedWorkerScript(byte[] raw, String contentType) {
        if (raw == null || raw.length == 0) {
            throw new OciException("Worker \u811a\u672c\u4e3a\u7a7a");
        }
        String boundary = CloudflareService.extractMultipartBoundary((String)contentType);
        if (boundary == null) {
            String text = new String(raw, StandardCharsets.UTF_8).trim();
            if (text.startsWith("export") || text.startsWith("{")) {
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("module", "worker.mjs");
                m.put("script", text);
                return m;
            }
            throw new OciException("\u65e0\u6cd5\u89e3\u6790 Worker \u811a\u672c\u54cd\u5e94");
        }
        String bodyText = new String(raw, StandardCharsets.UTF_8);
        String module = "worker.mjs";
        String script = CloudflareService.extractMultipartPart((String)bodyText, (String)boundary, (String)"worker.mjs");
        if (StrUtil.isBlank((CharSequence)script) && StrUtil.isBlank((CharSequence)(script = CloudflareService.extractFirstScriptPart((String)bodyText, (String)boundary)))) {
            throw new OciException("Worker \u811a\u672c\u4e2d\u672a\u627e\u5230\u53ef\u7f16\u8f91\u6a21\u5757");
        }
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("module", module);
        m.put("script", script);
        return m;
    }

    private static String extractMultipartBoundary(String contentType) {
        if (StrUtil.isBlank((CharSequence)contentType)) {
            return null;
        }
        for (String part : contentType.split(";")) {
            String p = part.trim();
            if (!p.startsWith("boundary=")) continue;
            String b = p.substring("boundary=".length()).trim();
            if (b.startsWith("\"") && b.endsWith("\"") && b.length() >= 2) {
                b = b.substring(1, b.length() - 1);
            }
            return b;
        }
        return null;
    }

    private static String extractMultipartPart(String body, String boundary, String fieldName) {
        String marker = "name=\"" + fieldName + "\"";
        int idx = body.indexOf(marker);
        if (idx < 0) {
            return null;
        }
        return CloudflareService.extractPartBody((String)body, (int)idx);
    }

    private static String extractFirstScriptPart(String body, String boundary) {
        int searchFrom = 0;
        int idx;
        while ((idx = body.indexOf("Content-Disposition:", searchFrom)) >= 0) {
            String part;
            String headerLine;
            int lineEnd = body.indexOf(10, idx);
            String string = headerLine = lineEnd > idx ? body.substring(idx, lineEnd) : body.substring(idx);
            if (headerLine.contains("name=\"metadata\"")) {
                searchFrom = idx + 1;
                continue;
            }
            if ((headerLine.contains("filename=") || headerLine.contains("name=\"worker")) && StrUtil.isNotBlank((CharSequence)(part = CloudflareService.extractPartBody((String)body, (int)idx)))) {
                return part;
            }
            searchFrom = idx + 1;
        }
        return null;
    }

    private static String extractPartBody(String body, int dispositionIdx) {
        int headerEnd = body.indexOf("\r\n\r\n", dispositionIdx);
        if (headerEnd < 0) {
            headerEnd = body.indexOf("\n\n", dispositionIdx);
            if (headerEnd < 0) {
                return null;
            }
            headerEnd += 2;
        } else {
            headerEnd += 4;
        }
        int nextBoundary = body.indexOf("\r\n--", headerEnd);
        if (nextBoundary < 0) {
            nextBoundary = body.indexOf("\n--", headerEnd);
        }
        String content = nextBoundary > headerEnd ? body.substring(headerEnd, nextBoundary) : body.substring(headerEnd);
        return content.stripTrailing();
    }

    private Map<String, Object> uploadWorkerScript(String scriptName, String scriptContent) {
        Credentials c = this.requireCredentials();
        String name = CloudflareService.normalizeWorkerScriptName((String)scriptName);
        String module = "worker.mjs";
        String metadata = JSONUtil.toJsonStr(Map.of("main_module", module, "compatibility_date", "2024-09-23"));
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/workers/scripts/" + CloudflareService.urlEncodePath((String)name);
        JSONObject json = CloudflareService.parseJson((String)this.apiPutMultipart(c.apiToken(), url, Map.of("metadata", metadata), Map.of(module, scriptContent)));
        CloudflareService.requireSuccess((JSONObject)json, (String)"\u4e0a\u4f20 Worker \u811a\u672c\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", name);
        m.put("kind", "worker");
        if (result != null) {
            m.put("createdOn", result.getStr((Object)"created_on"));
            m.put("modifiedOn", result.getStr((Object)"modified_on"));
        }
        return m;
    }

    private Map<String, Object> createPagesDeployment(Credentials c, String projectName, List<Map<String, Object>> fileEntries) {
        LinkedHashMap<String, String> manifest = new LinkedHashMap<String, String>();
        LinkedHashMap<String, byte[]> fileBytes = new LinkedHashMap<String, byte[]>();
        for (Map<String, Object> f : fileEntries) {
            byte[] bytes;
            String path = CloudflareService.normalizePagesFilePath((String)String.valueOf(f.get("path")));
            Object raw = f.get("bytes");
            if (StrUtil.isBlank((CharSequence)path) || !(raw instanceof byte[]) || (bytes = (byte[])raw).length == 0) continue;
            manifest.put(path, CloudflareService.sha256Hex((byte[])bytes));
            fileBytes.put(path, bytes);
        }
        if (manifest.isEmpty()) {
            throw new OciException("\u6ca1\u6709\u6709\u6548\u7684\u9759\u6001\u6587\u4ef6");
        }
        String url = "https://api.cloudflare.com/client/v4/accounts/" + c.accountId() + "/pages/projects/" + CloudflareService.urlEncodePath((String)projectName) + "/deployments";
        LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
        fields.put("manifest", JSONUtil.toJsonStr(manifest));
        fields.put("branch", "main");
        JSONObject json = CloudflareService.parseJson((String)this.apiPostMultipartBinary(c.apiToken(), url, fields, fileBytes));
        CloudflareService.requireSuccess((JSONObject)json, (String)"Pages \u90e8\u7f72\u5931\u8d25");
        JSONObject result = json.getJSONObject((Object)"result");
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("kind", "pages");
        m.put("name", projectName);
        if (result != null) {
            m.put("id", result.getStr((Object)"id"));
            m.put("url", result.getStr((Object)"url"));
        }
        return m;
    }

    public List<Map<String, Object>> decodePagesUploadFiles(List<Map<String, String>> encodedFiles) {
        ArrayList<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        if (encodedFiles == null) {
            return files;
        }
        long totalBytes = 0L;
        for (Map<String, String> f : encodedFiles) {
            String path = f.get("path");
            String contentBase64 = f.get("contentBase64");
            if (StrUtil.isBlank((CharSequence)path) || StrUtil.isBlank((CharSequence)contentBase64)) continue;
            byte[] raw = Base64.getDecoder().decode(contentBase64);
            totalBytes += (long)raw.length;
            if (files.size() >= 100) {
                throw new OciException("\u5355\u6b21\u6700\u591a\u4e0a\u4f20 100 \u4e2a\u6587\u4ef6");
            }
            if (totalBytes > 0x1900000L) {
                throw new OciException("\u4e0a\u4f20\u603b\u5927\u5c0f\u4e0d\u80fd\u8d85\u8fc7 25 MiB");
            }
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("path", path);
            row.put("bytes", raw);
            files.add(row);
        }
        if (files.isEmpty()) {
            throw new OciException("\u6ca1\u6709\u6709\u6548\u7684\u9759\u6001\u6587\u4ef6");
        }
        return CloudflareService.normalizeDecodedUploadPaths(files);
    }

    private static List<Map<String, Object>> normalizeDecodedUploadPaths(List<Map<String, Object>> files) {
        if (files.size() <= 1) {
            return files;
        }
        String firstPath = String.valueOf(files.get(0).get("path"));
        int slash = firstPath.indexOf(47);
        if (slash <= 0) {
            return files;
        }
        String root = firstPath.substring(0, slash);
        boolean allShareRoot = true;
        for (Map<String, Object> f : files) {
            String p = String.valueOf(f.get("path"));
            if (p.startsWith(root + "/")) continue;
            allShareRoot = false;
            break;
        }
        if (!allShareRoot) {
            return files;
        }
        ArrayList<Map<String, Object>> normalized = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> f : files) {
            String p = String.valueOf(f.get("path"));
            String stripped = p.substring(root.length() + 1);
            if (StrUtil.isBlank((CharSequence)stripped)) continue;
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>(f);
            row.put("path", stripped);
            normalized.add(row);
        }
        return normalized.isEmpty() ? files : normalized;
    }

    private MetricQuery queryWorkersRequestsSum(Credentials c, Instant start, Instant end) {
        try {
            LinkedHashMap<String, String> variables = new LinkedHashMap<String, String>();
            variables.put("accountTag", c.accountId());
            variables.put("datetimeStart", start.toString());
            variables.put("datetimeEnd", end.toString());
            String gql = "query WorkersUsage($accountTag: string!, $datetimeStart: string!, $datetimeEnd: string!) {\n  viewer {\n    accounts(filter: { accountTag: $accountTag }) {\n      workersInvocationsAdaptive(\n        limit: 10000,\n        filter: { datetime_geq: $datetimeStart, datetime_leq: $datetimeEnd }\n      ) {\n        sum { requests }\n      }\n    }\n  }\n}";
            JSONObject data = this.graphqlData(c, gql, variables);
            if (data == null) {
                return new MetricQuery(0L, false);
            }
            JSONObject viewer = data.getJSONObject((Object)"viewer");
            if (viewer == null) {
                return new MetricQuery(0L, false);
            }
            JSONArray accounts = viewer.getJSONArray((Object)"accounts");
            if (accounts == null || accounts.isEmpty()) {
                return new MetricQuery(0L, false);
            }
            JSONArray rows = accounts.getJSONObject((Object)0).getJSONArray((Object)"workersInvocationsAdaptive");
            if (rows == null) {
                return new MetricQuery(0L, false);
            }
            long total = 0L;
            for (int i = 0; i < rows.size(); ++i) {
                JSONObject row = rows.getJSONObject((Object)i);
                JSONObject sum = row.getJSONObject((Object)"sum");
                if (sum == null || sum.get((Object)"requests") == null) continue;
                total += sum.getLong((Object)"requests", Long.valueOf(0L)).longValue();
            }
            return new MetricQuery(total, true);
        }
        catch (Exception e) {
            log.debug("Workers usage requests GraphQL skipped: {}", (Object)e.getMessage());
            return new MetricQuery(0L, false);
        }
    }

    private MetricQuery queryWorkersCpuTimeMs(Credentials c, Instant start, Instant end) {
        try {
            LinkedHashMap<String, String> variables = new LinkedHashMap<String, String>();
            variables.put("accountTag", c.accountId());
            variables.put("datetimeStart", start.toString());
            variables.put("datetimeEnd", end.toString());
            String gql = "query WorkersCpu($accountTag: string!, $datetimeStart: string!, $datetimeEnd: string!) {\n  viewer {\n    accounts(filter: { accountTag: $accountTag }) {\n      workersInvocationsAdaptive(\n        limit: 10000,\n        filter: { datetime_geq: $datetimeStart, datetime_leq: $datetimeEnd }\n      ) {\n        sum { cpuTime }\n      }\n    }\n  }\n}";
            JSONObject data = this.graphqlData(c, gql, variables);
            if (data == null) {
                return new MetricQuery(0L, false);
            }
            JSONObject viewer = data.getJSONObject((Object)"viewer");
            if (viewer == null) {
                return new MetricQuery(0L, false);
            }
            JSONArray accounts = viewer.getJSONArray((Object)"accounts");
            if (accounts == null || accounts.isEmpty()) {
                return new MetricQuery(0L, false);
            }
            JSONArray rows = accounts.getJSONObject((Object)0).getJSONArray((Object)"workersInvocationsAdaptive");
            if (rows == null) {
                return new MetricQuery(0L, false);
            }
            long total = 0L;
            for (int i = 0; i < rows.size(); ++i) {
                JSONObject row = rows.getJSONObject((Object)i);
                JSONObject sum = row.getJSONObject((Object)"sum");
                if (sum == null || sum.get((Object)"cpuTime") == null) continue;
                total += sum.getLong((Object)"cpuTime", Long.valueOf(0L)).longValue();
            }
            return new MetricQuery(total, true);
        }
        catch (Exception e) {
            log.debug("Workers CPU GraphQL skipped: {}", (Object)e.getMessage());
            return new MetricQuery(0L, false);
        }
    }

    private JSONObject graphqlData(Credentials c, String query, Map<String, Object> variables) {
        Map<String, Map<String, Object>> payload = Map.of("query", query, "variables", variables);
        JSONObject json = CloudflareService.parseJson((String)this.apiPost(c.apiToken(), "https://api.cloudflare.com/client/v4/graphql", payload));
        if (json.getJSONArray((Object)"errors") != null && !json.getJSONArray((Object)"errors").isEmpty()) {
            log.debug("GraphQL errors: {}", (Object)json.getJSONArray((Object)"errors"));
            return null;
        }
        return json.getJSONObject((Object)"data");
    }

    private static String formatUsageDateRange(Instant start, Instant end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M\u6708d\u65e5").withZone(ZoneOffset.UTC);
        LocalDate startDate = start.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = end.atZone(ZoneOffset.UTC).toLocalDate();
        if (startDate.getYear() != endDate.getYear()) {
            DateTimeFormatter withYear = DateTimeFormatter.ofPattern("yyyy\u5e74M\u6708d\u65e5").withZone(ZoneOffset.UTC);
            return withYear.format(start) + " - " + withYear.format(end);
        }
        return fmt.format(start) + " - " + fmt.format(end);
    }

    private static String sha256Hex(String content) {
        return CloudflareService.sha256Hex((byte[])content.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(byte[] content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new OciException("\u8ba1\u7b97\u6587\u4ef6\u54c8\u5e0c\u5931\u8d25");
        }
    }

    private static String normalizeWorkerScriptName(String name) {
        String n = StrUtil.trimToNull((CharSequence)name);
        if (n == null) {
            throw new OciException("Worker \u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (!n.matches("^[a-zA-Z0-9_-]{1,64}$")) {
            throw new OciException("Worker \u540d\u79f0\u4ec5\u5141\u8bb8\u5b57\u6bcd\u3001\u6570\u5b57\u3001\u4e0b\u5212\u7ebf\u4e0e\u8fde\u5b57\u7b26");
        }
        return n;
    }

    private static String normalizePagesProjectName(String name) {
        String n = StrUtil.trimToNull((CharSequence)name);
        if (n == null) {
            throw new OciException("\u9879\u76ee\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if ((n = n.toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-")).length() < 1 || n.length() > 58) {
            throw new OciException("\u9879\u76ee\u540d\u79f0\u957f\u5ea6\u987b\u4e3a 1\u201358 \u4e2a\u5b57\u7b26");
        }
        return n;
    }

    private static String normalizePagesFilePath(String path) {
        String p = StrUtil.trimToNull((CharSequence)path);
        if (p == null) {
            throw new OciException("\u6587\u4ef6\u8def\u5f84\u4e0d\u80fd\u4e3a\u7a7a");
        }
        p = p.replace('\\', '/');
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        if (p.contains("..")) {
            throw new OciException("\u975e\u6cd5\u6587\u4ef6\u8def\u5f84");
        }
        return p;
    }

    private static String urlEncodePath(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static Map<String, Object> buildEmailAction(String actionType, List<String> destinations, String workerName) {
        return switch (actionType) {
            case "drop" -> Map.of("type", "drop");
            case "worker" -> {
                if (StrUtil.isBlank((CharSequence)workerName)) {
                    throw new OciException("Worker \u52a8\u4f5c\u9700\u6307\u5b9a workerName");
                }
                yield Map.of("type", "worker", "value", List.of(workerName.trim()));
            }
            case "forward" -> {
                if (destinations == null || destinations.isEmpty()) {
                    throw new OciException("\u8f6c\u53d1\u52a8\u4f5c\u9700\u6307\u5b9a\u81f3\u5c11\u4e00\u4e2a\u76ee\u6807\u90ae\u7bb1");
                }
                ArrayList<String> cleaned = new ArrayList<String>();
                for (String d : destinations) {
                    if (!StrUtil.isNotBlank((CharSequence)d)) continue;
                    cleaned.add(d.trim());
                }
                if (cleaned.isEmpty()) {
                    throw new OciException("\u8f6c\u53d1\u52a8\u4f5c\u9700\u6307\u5b9a\u81f3\u5c11\u4e00\u4e2a\u76ee\u6807\u90ae\u7bb1");
                }
                yield Map.of("type", "forward", "value", cleaned);
            }
            default -> throw new OciException("\u4e0d\u652f\u6301\u7684\u90ae\u4ef6\u8def\u7531\u52a8\u4f5c: " + actionType);
        };
    }

    private static Map<String, Object> mapEmailSettings(JSONObject s) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        if (s == null) {
            return m;
        }
        m.put("id", s.getStr((Object)"id"));
        m.put("enabled", s.getBool((Object)"enabled"));
        m.put("name", s.getStr((Object)"name"));
        m.put("status", s.getStr((Object)"status"));
        m.put("created", s.getStr((Object)"created"));
        m.put("modified", s.getStr((Object)"modified"));
        return m;
    }

    private static Map<String, Object> mapEmailRule(JSONObject r) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", r.getStr((Object)"id"));
        m.put("name", r.getStr((Object)"name"));
        m.put("enabled", r.getBool((Object)"enabled"));
        m.put("priority", r.getInt((Object)"priority"));
        String customAddress = null;
        JSONArray matchers = r.getJSONArray((Object)"matchers");
        if (matchers != null) {
            for (int i = 0; i < matchers.size(); ++i) {
                JSONObject matcher = matchers.getJSONObject((Object)i);
                if (!"literal".equals(matcher.getStr((Object)"type")) || !"to".equals(matcher.getStr((Object)"field"))) continue;
                customAddress = matcher.getStr((Object)"value");
                break;
            }
        }
        m.put("customAddress", customAddress);
        ArrayList<String> destinations = new ArrayList<String>();
        String actionType = "forward";
        String workerName = null;
        JSONArray actions = r.getJSONArray((Object)"actions");
        if (actions != null && !actions.isEmpty()) {
            JSONObject action = actions.getJSONObject((Object)0);
            String type = action.getStr((Object)"type");
            if ("drop".equals(type)) {
                actionType = "drop";
            } else if ("worker".equals(type)) {
                actionType = "worker";
                JSONArray values = action.getJSONArray((Object)"value");
                if (values != null && !values.isEmpty()) {
                    workerName = values.getStr((Object)0);
                }
            } else if ("forward".equals(type)) {
                actionType = "forward";
                JSONArray values = action.getJSONArray((Object)"value");
                if (values != null) {
                    for (int j = 0; j < values.size(); ++j) {
                        destinations.add(values.getStr((Object)j));
                    }
                }
            } else if (StrUtil.isNotBlank((CharSequence)type)) {
                actionType = type;
            }
        }
        m.put("destinations", destinations);
        m.put("actionType", actionType);
        m.put("workerName", workerName);
        return m;
    }

    private static Map<String, Object> mapEmailDestination(JSONObject d) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", d.getStr((Object)"id"));
        m.put("email", d.getStr((Object)"email"));
        String verifiedAt = d.getStr((Object)"verified");
        m.put("verifiedAt", verifiedAt);
        m.put("verified", StrUtil.isNotBlank((CharSequence)verifiedAt));
        m.put("created", d.getStr((Object)"created"));
        m.put("modified", d.getStr((Object)"modified"));
        return m;
    }

    @Deprecated
    public List<Map<String, Object>> listDnsRecordsByCfgId(String cfgId, int page, int perPage) {
        CfCfg cfg = (CfCfg)this.cfCfgMapper.selectById((Serializable)((Object)cfgId));
        if (cfg == null) {
            throw new OciException("CF \u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return this.listDnsRecordsLegacy(cfg, page, perPage);
    }

    private List<Map<String, Object>> listDnsRecordsLegacy(CfCfg cfg, int page, int perPage) {
        String url = String.format("%s/zones/%s/dns_records?page=%d&per_page=%d", "https://api.cloudflare.com/client/v4", cfg.getZoneId(), page, perPage);
        JSONObject json = CloudflareService.parseJson((String)this.apiGet(cfg.getApiToken(), url));
        CloudflareService.requireSuccess((JSONObject)json, (String)"Cloudflare API \u9519\u8bef");
        JSONArray result = json.getJSONArray((Object)"result");
        ArrayList<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        if (result == null) {
            return records;
        }
        for (int i = 0; i < result.size(); ++i) {
            JSONObject r = result.getJSONObject((Object)i);
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("id", r.getStr((Object)"id"));
            map.put("type", r.getStr((Object)"type"));
            map.put("name", r.getStr((Object)"name"));
            map.put("content", r.getStr((Object)"content"));
            map.put("proxied", r.getBool((Object)"proxied"));
            map.put("ttl", r.getInt((Object)"ttl"));
            records.add(map);
        }
        return records;
    }

    private Credentials requireCredentials() {
        String accountId = StrUtil.trimToNull((CharSequence)this.notificationService.getKvValue(SysCfgEnum.CF_ACCOUNT_ID));
        String token = StrUtil.trimToNull((CharSequence)this.notificationService.getKvValue(SysCfgEnum.CF_API_TOKEN));
        if (accountId == null || token == null) {
            throw new OciException("\u8bf7\u5148\u5728\u7cfb\u7edf\u8bbe\u7f6e \u2192 Cloudflare \u4e2d\u914d\u7f6e Account ID \u4e0e API Token");
        }
        return new Credentials(accountId, token);
    }

    private static void requireZoneId(String zoneId) {
        if (StrUtil.isBlank((CharSequence)zoneId)) {
            throw new OciException("\u8bf7\u9009\u62e9 Zone");
        }
    }

    private String apiGet(String token, String url) {
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").timeout(Duration.ofSeconds(30L)).GET());
    }

    private String apiPost(String token, String url, Object body) {
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr((Object)body))).timeout(Duration.ofSeconds(30L)));
    }

    private String apiPut(String token, String url, Object body) {
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").method("PUT", HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr((Object)body))).timeout(Duration.ofSeconds(30L)));
    }

    private String apiPatch(String token, String url, Object body) {
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "application/json").method("PATCH", HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr((Object)body))).timeout(Duration.ofSeconds(30L)));
    }

    private String apiPostMultipart(String token, String url, String fileFieldName, String fileName, String fileContent, Map<String, String> formFields) {
        String boundary = "----CloudflareBoundary" + System.currentTimeMillis();
        byte[] body = CloudflareService.buildMultipartBody((String)boundary, (String)fileFieldName, (String)fileName, (String)fileContent, formFields);
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(body)).timeout(Duration.ofSeconds(120L)));
    }

    private String apiPostMultipart(String token, String url, Map<String, String> formFields, Map<String, String> fileContents) {
        String boundary = "----CloudflareBoundary" + System.currentTimeMillis();
        byte[] body = CloudflareService.buildMultipartBodyMulti((String)boundary, formFields, fileContents, (boolean)false);
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(body)).timeout(Duration.ofSeconds(180L)));
    }

    private String apiPostMultipartBinary(String token, String url, Map<String, String> formFields, Map<String, byte[]> fileContents) {
        String boundary = "----CloudflareBoundary" + System.currentTimeMillis();
        byte[] body = CloudflareService.buildMultipartBodyBinary((String)boundary, formFields, fileContents);
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(body)).timeout(Duration.ofSeconds(180L)));
    }

    private String apiPutMultipart(String token, String url, Map<String, String> formFields, Map<String, String> fileContents) {
        String boundary = "----CloudflareBoundary" + System.currentTimeMillis();
        byte[] body = CloudflareService.buildMultipartBodyMulti((String)boundary, formFields, fileContents, (boolean)true);
        return this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary).method("PUT", HttpRequest.BodyPublishers.ofByteArray(body)).timeout(Duration.ofSeconds(120L)));
    }

    private static byte[] buildMultipartBodyMulti(String boundary, Map<String, String> formFields, Map<String, String> fileContents, boolean workerModule) {
        String lineEnd = "\r\n";
        StringBuilder sb = new StringBuilder();
        if (formFields != null) {
            for (Map.Entry<String, String> e : formFields.entrySet()) {
                sb.append("--").append(boundary).append(lineEnd);
                sb.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"").append(lineEnd);
                if ("metadata".equals(e.getKey()) && workerModule) {
                    sb.append("Content-Type: application/json").append(lineEnd);
                }
                sb.append(lineEnd);
                sb.append(e.getValue()).append(lineEnd);
            }
        }
        if (fileContents != null) {
            for (Map.Entry<String, String> e : fileContents.entrySet()) {
                sb.append("--").append(boundary).append(lineEnd);
                sb.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"; filename=\"").append(e.getKey()).append("\"").append(lineEnd);
                String contentType = workerModule ? "application/javascript+module" : CloudflareService.guessPagesContentType((String)e.getKey());
                sb.append("Content-Type: ").append(contentType).append(lineEnd);
                sb.append(lineEnd);
                sb.append(e.getValue()).append(lineEnd);
            }
        }
        sb.append("--").append(boundary).append("--").append(lineEnd);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String guessPagesContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return "text/html";
        }
        if (lower.endsWith(".css")) {
            return "text/css";
        }
        if (lower.endsWith(".js") || lower.endsWith(".mjs")) {
            return "application/javascript";
        }
        if (lower.endsWith(".json")) {
            return "application/json";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private static byte[] buildMultipartBodyBinary(String boundary, Map<String, String> formFields, Map<String, byte[]> fileContents) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String lineEnd = "\r\n";
            if (formFields != null) {
                for (Map.Entry<String, String> entry : formFields.entrySet()) {
                    out.write(("--" + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
                    out.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                    out.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                    out.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                }
            }
            if (fileContents != null) {
                for (Map.Entry<String, String> entry : fileContents.entrySet()) {
                    out.write(("--" + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + entry.getKey() + "\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
                    out.write(("Content-Type: " + CloudflareService.guessPagesContentType((String)entry.getKey()) + lineEnd).getBytes(StandardCharsets.UTF_8));
                    out.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                    out.write((byte[])entry.getValue());
                    out.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                }
            }
            out.write(("--" + boundary + "--" + lineEnd).getBytes(StandardCharsets.UTF_8));
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new OciException("\u6784\u5efa\u4e0a\u4f20\u8bf7\u6c42\u5931\u8d25");
        }
    }

    private static byte[] buildMultipartBody(String boundary, String fileFieldName, String fileName, String fileContent, Map<String, String> formFields) {
        String lineEnd = "\r\n";
        StringBuilder sb = new StringBuilder();
        if (formFields != null) {
            for (Map.Entry<String, String> e : formFields.entrySet()) {
                sb.append("--").append(boundary).append(lineEnd);
                sb.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"").append(lineEnd);
                sb.append(lineEnd);
                sb.append(e.getValue()).append(lineEnd);
            }
        }
        sb.append("--").append(boundary).append(lineEnd);
        sb.append("Content-Disposition: form-data; name=\"").append(fileFieldName).append("\"; filename=\"").append(fileName).append("\"").append(lineEnd);
        sb.append("Content-Type: text/plain").append(lineEnd);
        sb.append(lineEnd);
        sb.append(fileContent).append(lineEnd);
        sb.append("--").append(boundary).append("--").append(lineEnd);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void apiDelete(String token, String url) {
        this.httpSend(HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + token).timeout(Duration.ofSeconds(30L)).DELETE());
    }

    private String httpSend(HttpRequest.Builder b) {
        try {
            String body;
            HttpClient client = this.ociProxyConfigService.newOutboundHttpClient();
            HttpRequest req = b.build();
            HttpResponse<String> r = client.send(req, HttpResponse.BodyHandlers.ofString());
            String string = body = r.body() == null ? "" : r.body();
            if (r.statusCode() < 200 || r.statusCode() >= 400) {
                String msg = CloudflareService.parseCfError((String)body);
                throw new OciException("HTTP " + r.statusCode() + (String)(msg != null ? ": " + msg : ""));
            }
            return body;
        }
        catch (OciException e) {
            throw e;
        }
        catch (IOException e) {
            throw new OciException("\u8bf7\u6c42\u5931\u8d25: " + e.getMessage());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("\u8bf7\u6c42\u4e2d\u65ad");
        }
    }

    private static JSONObject parseJson(String body) {
        if (StrUtil.isBlank((CharSequence)body)) {
            return new JSONObject();
        }
        return JSONUtil.parseObj((String)body);
    }

    private static void requireSuccess(JSONObject json, String prefix) {
        if (json.getBool((Object)"success", Boolean.valueOf(false)).booleanValue()) {
            return;
        }
        String err = json.getStr((Object)"errors");
        if (StrUtil.isBlank((CharSequence)err) && json.getJSONArray((Object)"errors") != null) {
            err = json.getJSONArray((Object)"errors").toString();
        }
        throw new OciException(prefix + (String)(StrUtil.isNotBlank((CharSequence)err) ? ": " + err : ""));
    }

    private static String parseCfError(String body) {
        try {
            JSONObject j = JSONUtil.parseObj((String)body);
            if (j.getJSONArray((Object)"errors") != null && !j.getJSONArray((Object)"errors").isEmpty()) {
                return j.getJSONArray((Object)"errors").toString();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return body.length() > 200 ? body.substring(0, 200) : body;
    }

    private static Map<String, Object> mapTunnel(JSONObject t) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", t.getStr((Object)"id"));
        map.put("name", t.getStr((Object)"name"));
        map.put("status", t.getStr((Object)"status"));
        map.put("createdAt", t.getStr((Object)"created_at"));
        map.put("deletedAt", t.getStr((Object)"deleted_at"));
        map.put("remoteConfig", t.getBool((Object)"remote_config"));
        map.put("connections", t.getJSONArray((Object)"connections"));
        return map;
    }

    private static String maskSecret(String s) {
        if (s == null || s.isBlank()) {
            return "";
        }
        if (s.length() <= 8) {
            return "****";
        }
        return s.substring(0, 4) + "****" + s.substring(s.length() - 4);
    }

    private static String resolveMasked(String fromClient, String existing) {
        if (fromClient != null && fromClient.contains("****") && existing != null && !existing.isBlank()) {
            return existing;
        }
        return fromClient == null ? "" : fromClient.trim();
    }
}

