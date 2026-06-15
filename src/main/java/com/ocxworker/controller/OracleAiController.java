/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.controller.OracleAiController
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciKvMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.entity.OciKv
 *  com.ocxworker.model.entity.OciOpenaiKey
 *  com.ocxworker.model.entity.OciOpenaiPortBinding
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.OciGenerativeOpenAiService
 *  com.ocxworker.service.OciOpenaiKeyService
 *  com.ocxworker.service.OciOpenaiKeyService$KeyCreateResult
 *  com.ocxworker.service.OracleAiGatewayConfigService
 *  com.ocxworker.service.OracleAiGatewayToggleService
 *  com.ocxworker.service.OracleAiPortBindingService
 *  com.ocxworker.util.CommonUtils
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.model.entity.OciOpenaiKey;
import com.ocxworker.model.entity.OciOpenaiPortBinding;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.OciGenerativeOpenAiService;
import com.ocxworker.service.OciOpenaiKeyService;
import com.ocxworker.service.OracleAiGatewayConfigService;
import com.ocxworker.service.OracleAiGatewayToggleService;
import com.ocxworker.service.OracleAiPortBindingService;
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/api/oci/oracle-ai"})
public class OracleAiController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OracleAiController.class);
    @Value(value="${ocxworker.openaiApi.port:8080}")
    private int openaiApiPort;
    @Resource
    private OciOpenaiKeyService openaiKeyService;
    @Resource
    private OciGenerativeOpenAiService generativeOpenAiService;
    @Resource
    private OciUserMapper ociUserMapper;
    @Resource
    private OracleAiGatewayToggleService gatewayToggleService;
    @Resource
    private OracleAiGatewayConfigService gatewayConfigService;
    @Resource
    private OracleAiPortBindingService portBindingService;
    @Resource
    private OciKvMapper kvMapper;
    private static final String UI_STATE_TYPE = "ui_state";
    private static final String UI_STATE_CODE = "oracle_ai.page_state.v1";
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");
    private static final List<String> PUBLIC_IPV4_ENDPOINTS = List.of("https://ipv4.icanhazip.com", "https://v4.ident.me", "https://api.ipify.org");
    private static final Duration PUBLIC_IP_CACHE_TTL = Duration.ofMinutes(10L);
    private volatile String cachedPublicIp;
    private volatile Instant cachedPublicIpAt = Instant.EPOCH;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value={"/gateway"})
    public ResponseData<?> gateway() {
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("openaiApiPort", this.openaiApiPort);
        m.put("pathPrefix", "/v1");
        m.put("baseUrlExample", OciGenerativeOpenAiService.gatewayHint((int)this.openaiApiPort));
        m.put("serverIp", this.detectServerIp());
        m.put("openaiProxyEnabled", this.gatewayToggleService.isEnabled());
        m.put("defaultMaxTokens", this.gatewayConfigService.getDefaultMaxTokens());
        return ResponseData.ok(m);
    }

    private String detectServerIp() {
        String cached = this.cachedPublicIp;
        if (cached != null && !cached.isBlank() && Duration.between(this.cachedPublicIpAt, Instant.now()).compareTo(PUBLIC_IP_CACHE_TTL) < 0) {
            return cached;
        }
        for (String endpoint : PUBLIC_IPV4_ENDPOINTS) {
            try {
                String ip;
                HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofSeconds(2L)).GET().build();
                HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() < 200 || resp.statusCode() >= 300 || !IPV4_PATTERN.matcher(ip = resp.body() == null ? "" : resp.body().trim()).matches()) continue;
                this.cachedPublicIp = ip;
                this.cachedPublicIpAt = Instant.now();
                return ip;
            }
            catch (Exception e) {
                log.debug("Failed to detect public IPv4 from {}: {}", (Object)endpoint, (Object)e.getMessage());
            }
        }
        return "";
    }

    @PostMapping(value={"/ui-state/get"})
    public ResponseData<?> getUiState() {
        try {
            OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)"oracle_ai.page_state.v1")).eq(OciKv::getType, (Object)"ui_state"));
            if (kv == null || kv.getValue() == null || kv.getValue().isBlank()) {
                return ResponseData.ok(Map.of());
            }
            Map obj = this.objectMapper.readValue(kv.getValue(), Object.class);
            return ResponseData.ok(obj != null ? obj : Map.of());
        }
        catch (Exception e) {
            return ResponseData.ok(Map.of());
        }
    }

    @PostMapping(value={"/ui-state/save"})
    public ResponseData<?> saveUiState(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return ResponseData.error((String)"\u53c2\u6570\u9519\u8bef");
        }
        String ociUserId = body.get("ociUserId") == null ? "" : String.valueOf(body.get("ociUserId")).trim();
        Object mp = body.get("modelPick");
        List<String> modelPick = new ArrayList();
        if (mp instanceof List) {
            List list = (List)mp;
            for (Object o : list) {
                String s;
                if (o == null || (s = String.valueOf(o).trim()).isBlank()) continue;
                modelPick.add(s);
            }
        }
        if (ociUserId.length() > 128) {
            ociUserId = ociUserId.substring(0, 128);
        }
        if (modelPick.size() > 200) {
            modelPick = modelPick.subList(0, 200);
        }
        HashMap<String, Object> state = new HashMap<String, Object>();
        state.put("ociUserId", ociUserId);
        state.put("modelPick", modelPick);
        state.put("updateAt", System.currentTimeMillis());
        try {
            String json = this.objectMapper.writeValueAsString(state);
            OciKv existing = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)"oracle_ai.page_state.v1")).eq(OciKv::getType, (Object)"ui_state"));
            if (existing != null) {
                existing.setValue(json);
                this.kvMapper.updateById((Object)existing);
            } else {
                OciKv kv = new OciKv();
                kv.setId(CommonUtils.generateId());
                kv.setCode("oracle_ai.page_state.v1");
                kv.setType("ui_state");
                kv.setValue(json);
                this.kvMapper.insert((Object)kv);
            }
            return ResponseData.ok();
        }
        catch (Exception e) {
            return ResponseData.error((String)("\u4fdd\u5b58\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef")));
        }
    }

    @PostMapping(value={"/gateway/setEnabled"})
    public ResponseData<?> setGatewayEnabled(@RequestBody Map<String, Object> body) {
        Object v;
        Object object = v = body == null ? null : body.get("enabled");
        boolean enabled = v instanceof Boolean ? (Boolean)v : v != null && "true".equalsIgnoreCase(String.valueOf(v));
        this.gatewayToggleService.setEnabled(enabled);
        return ResponseData.ok(Map.of("openaiProxyEnabled", enabled));
    }

    @PostMapping(value={"/gateway/default-max-tokens"})
    public ResponseData<?> setDefaultMaxTokens(@RequestBody Map<String, Object> body) {
        int value;
        Object raw;
        Object object = raw = body == null ? null : body.get("defaultMaxTokens");
        if (raw == null) {
            Object object2 = raw = body == null ? null : body.get("max_tokens");
        }
        if (raw == null) {
            return ResponseData.error((String)"defaultMaxTokens \u5fc5\u586b");
        }
        try {
            if (raw instanceof Number) {
                Number n = (Number)raw;
                value = n.intValue();
            } else {
                value = Integer.parseInt(String.valueOf(raw).trim());
            }
        }
        catch (Exception e) {
            return ResponseData.error((String)"defaultMaxTokens \u5fc5\u987b\u662f\u6570\u5b57");
        }
        int saved = this.gatewayConfigService.setDefaultMaxTokens(value);
        return ResponseData.ok(Map.of("defaultMaxTokens", saved));
    }

    @PostMapping(value={"/keys/create"})
    public ResponseData<?> createKey(@RequestBody Map<String, String> body) {
        String tid = body == null ? null : body.get("ociUserId");
        String name = body == null ? null : body.get("name");
        OciOpenaiKeyService.KeyCreateResult c = this.openaiKeyService.create(tid, name);
        HashMap<String, Object> d = new HashMap<String, Object>();
        d.put("id", c.id());
        d.put("apiKey", c.plainKey());
        d.put("keyPrefix", c.keyPrefix());
        d.put("keyMasked", c.keyMasked());
        d.put("warning", "\u5bc6\u94a5\u5df2\u5165\u5e93\uff0c\u53ef\u5728\u5217\u8868\u4e2d\u70b9\u51fb\u300c\u67e5\u770b\u300d\u518d\u6b21\u590d\u5236\u3002\u5bf9\u63a5 New API \u65f6 API \u5730\u5740\u4e3a http://<\u672c\u673a\u6216\u57df\u540d>:" + this.openaiApiPort + "/v1");
        return ResponseData.ok(d);
    }

    @PostMapping(value={"/keys/reveal"})
    public ResponseData<?> revealKey(@RequestBody Map<String, String> body) {
        if (body == null || body.get("id") == null) {
            return ResponseData.error((String)"id \u5fc5\u586b");
        }
        try {
            String plain = this.openaiKeyService.revealPlainKey(body.get("id"));
            return ResponseData.ok(Map.of("apiKey", plain));
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
    }

    @PostMapping(value={"/keys/list"})
    public ResponseData<?> listKeys(@RequestBody Map<String, String> body) {
        String tid = body == null ? null : body.get("ociUserId");
        List list = this.openaiKeyService.listByTenant(tid);
        return ResponseData.ok(list.stream().map(k -> {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("id", k.getId());
            row.put("name", k.getName());
            row.put("keyPrefix", k.getKeyPrefix());
            row.put("keyMasked", this.openaiKeyService.maskForList(k));
            row.put("disabled", k.getDisabled() != null && k.getDisabled() == 1);
            row.put("createTime", k.getCreateTime());
            row.put("lastUsed", k.getLastUsed());
            return row;
        }).collect(Collectors.toList()));
    }

    @PostMapping(value={"/keys/setDisabled"})
    public ResponseData<?> setDisabled(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return ResponseData.error((String)"\u53c2\u6570\u9519\u8bef");
        }
        String id = (String)body.get("id");
        Object d = body.get("disabled");
        boolean dis = d instanceof Boolean ? (Boolean)d : d != null && "true".equals(d.toString());
        this.openaiKeyService.setDisabled(id, dis);
        return ResponseData.ok();
    }

    @PostMapping(value={"/keys/remove"})
    public ResponseData<?> removeKey(@RequestBody Map<String, String> body) {
        if (body == null || body.get("id") == null) {
            return ResponseData.error((String)"id \u5fc5\u586b");
        }
        this.openaiKeyService.remove(body.get("id"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/ports/list"})
    public ResponseData<?> listPortBindings() {
        List list = this.portBindingService.list();
        return ResponseData.ok(list.stream().map(arg_0 -> this.portBindingRow(arg_0)).collect(Collectors.toList()));
    }

    @PostMapping(value={"/ports/save"})
    public ResponseData<?> savePortBinding(@RequestBody Map<String, Object> body) {
        try {
            String id = body == null ? null : OracleAiController.trimObj((Object)body.get("id"));
            String name = body == null ? null : OracleAiController.trimObj((Object)body.get("name"));
            String ociUserId = body == null ? null : OracleAiController.trimObj((Object)body.get("ociUserId"));
            String ociRegion = body == null ? null : OracleAiController.trimObj((Object)body.get("ociRegion"));
            String openaiKeyId = body == null ? null : OracleAiController.trimObj((Object)body.get("openaiKeyId"));
            int port = OracleAiController.intValue((Object)(body == null ? null : body.get("port")), (int)-1);
            Integer defaultMaxTokens = OracleAiController.nullableIntValue(body == null ? null : body.get("defaultMaxTokens"));
            List allowedModels = OracleAiController.stringListValue(body == null ? null : body.get("allowedModels"));
            boolean enabled = OracleAiController.boolValue((Object)(body == null ? null : body.get("enabled")), (boolean)true);
            OciOpenaiPortBinding row = id == null ? this.portBindingService.create(name, port, ociUserId, ociRegion, openaiKeyId, defaultMaxTokens, allowedModels, enabled) : this.portBindingService.update(id, name, port, ociUserId, ociRegion, openaiKeyId, defaultMaxTokens, allowedModels, enabled);
            return ResponseData.ok((Object)this.portBindingRow(row));
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
        catch (Exception e) {
            return ResponseData.error((String)(e.getMessage() != null ? e.getMessage() : "\u4fdd\u5b58\u5931\u8d25"));
        }
    }

    @PostMapping(value={"/ports/setEnabled"})
    public ResponseData<?> setPortBindingEnabled(@RequestBody Map<String, Object> body) {
        String id;
        String string = id = body == null ? null : OracleAiController.trimObj((Object)body.get("id"));
        if (id == null) {
            return ResponseData.error((String)"id \u5fc5\u586b");
        }
        boolean enabled = OracleAiController.boolValue((Object)body.get("enabled"), (boolean)true);
        try {
            this.portBindingService.setEnabled(id, enabled);
            return ResponseData.ok();
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
    }

    @PostMapping(value={"/ports/remove"})
    public ResponseData<?> removePortBinding(@RequestBody Map<String, Object> body) {
        String id;
        String string = id = body == null ? null : OracleAiController.trimObj((Object)body.get("id"));
        if (id == null) {
            return ResponseData.error((String)"id \u5fc5\u586b");
        }
        this.portBindingService.remove(id);
        return ResponseData.ok();
    }

    @PostMapping(value={"/models"})
    public ResponseData<?> models(@RequestBody Map<String, String> body) {
        if (body == null || body.get("ociUserId") == null) {
            return ResponseData.error((String)"ociUserId \u5fc5\u586b");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)body.get("ociUserId")));
        if (u == null) {
            return ResponseData.error((String)"\u79df\u6237\u4e0d\u5b58\u5728");
        }
        String after = body.get("after");
        String modelId = body.get("modelId");
        String ociRegion = OracleAiController.trimToNullOrBlank((String)body.get("ociRegion"));
        try {
            JsonNode j = this.generativeOpenAiService.getModelsAsJson(u, ociRegion, after, modelId);
            return ResponseData.ok((Object)j);
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
        catch (Exception e) {
            return ResponseData.error((String)("\u62c9\u53d6\u6a21\u578b\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef")));
        }
    }

    @PostMapping(value={"/generative-projects/list"})
    public ResponseData<?> listGenerativeProjects(@RequestBody Map<String, String> body) {
        if (body == null || body.get("ociUserId") == null) {
            return ResponseData.error((String)"ociUserId \u5fc5\u586b");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)body.get("ociUserId")));
        if (u == null) {
            return ResponseData.error((String)"\u79df\u6237\u4e0d\u5b58\u5728");
        }
        try {
            JsonNode j = this.generativeOpenAiService.listGenerativeAiProjectSummaries(u);
            return ResponseData.ok((Object)j);
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
        catch (Exception e) {
            return ResponseData.error((String)("\u5217\u4e3e\u9879\u76ee\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef")));
        }
    }

    @PostMapping(value={"/generative-projects/create"})
    public ResponseData<?> createGenerativeProject(@RequestBody Map<String, String> body) {
        if (body == null || body.get("ociUserId") == null) {
            return ResponseData.error((String)"ociUserId \u5fc5\u586b");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)body.get("ociUserId")));
        if (u == null) {
            return ResponseData.error((String)"\u79df\u6237\u4e0d\u5b58\u5728");
        }
        String displayName = body.get("displayName");
        try {
            JsonNode j = this.generativeOpenAiService.createGenerativeAiProject(u, displayName);
            if (j != null && j.isObject()) {
                String id;
                String string = id = j.get("id") != null && j.get("id").isTextual() ? j.get("id").asText() : null;
                if (id != null && !id.isBlank()) {
                    u.setGenerativeOpenaiProject(id);
                    this.ociUserMapper.updateById((Object)u);
                }
            }
            return ResponseData.ok((Object)j);
        }
        catch (OciException e) {
            return ResponseData.error((String)e.getMessage());
        }
        catch (Exception e) {
            return ResponseData.error((String)("\u521b\u5efa\u9879\u76ee\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef")));
        }
    }

    @PostMapping(value={"/generative-context/get"})
    public ResponseData<?> getGenerativeContext(@RequestBody Map<String, String> body) {
        if (body == null || body.get("ociUserId") == null) {
            return ResponseData.error((String)"ociUserId \u5fc5\u586b");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)body.get("ociUserId")));
        if (u == null) {
            return ResponseData.error((String)"\u79df\u6237\u4e0d\u5b58\u5728");
        }
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("generativeOpenaiProject", u.getGenerativeOpenaiProject());
        m.put("generativeConversationStoreId", u.getGenerativeConversationStoreId());
        return ResponseData.ok(m);
    }

    @PostMapping(value={"/generative-context/save"})
    public ResponseData<?> saveGenerativeContext(@RequestBody Map<String, String> body) {
        if (body == null || body.get("ociUserId") == null) {
            return ResponseData.error((String)"ociUserId \u5fc5\u586b");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)body.get("ociUserId")));
        if (u == null) {
            return ResponseData.error((String)"\u79df\u6237\u4e0d\u5b58\u5728");
        }
        u.setGenerativeOpenaiProject(OracleAiController.trimToNullOrBlank((String)body.get("generativeOpenaiProject")));
        u.setGenerativeConversationStoreId(OracleAiController.trimToNullOrBlank((String)body.get("generativeConversationStoreId")));
        this.ociUserMapper.updateById((Object)u);
        return ResponseData.ok();
    }

    @PostMapping(value={"/chat-test"})
    public ResponseData<?> chatTest(@RequestBody Map<String, Object> body) {
        String input;
        String apiKey = body == null ? null : String.valueOf(body.getOrDefault("apiKey", "")).trim();
        String model = body == null ? null : String.valueOf(body.getOrDefault("model", "")).trim();
        String string = input = body == null ? null : String.valueOf(body.getOrDefault("input", "")).trim();
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseData.error((String)"apiKey \u5fc5\u586b");
        }
        if (model == null || model.isBlank()) {
            return ResponseData.error((String)"model \u5fc5\u586b");
        }
        if (input == null || input.isBlank()) {
            return ResponseData.error((String)"input \u5fc5\u586b");
        }
        Object bearer = apiKey.toLowerCase().startsWith("bearer ") ? apiKey : "Bearer " + apiKey;
        boolean multiAgent = model.toLowerCase().contains("multi-agent") || model.toLowerCase().contains("multi agent") || model.toLowerCase().contains("multiagent");
        String url = "http://127.0.0.1:" + this.openaiApiPort + (multiAgent ? "/v1/responses" : "/v1/chat/completions");
        log.info("chat-test -> {} model={} (multiAgent={})", new Object[]{url, model, multiAgent});
        String payload = multiAgent ? "{\"model\":" + OracleAiController.jsonString((String)model) + ",\"input\":[{\"role\":\"user\",\"content\":[{\"type\":\"input_text\",\"text\":" + OracleAiController.jsonString((String)input) + "}]}],\"stream\":false}" : "{\"model\":" + OracleAiController.jsonString((String)model) + ",\"messages\":[{\"role\":\"user\",\"content\":" + OracleAiController.jsonString((String)input) + "}],\"stream\":false}";
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(120L)).header("content-type", "application/json").header("authorization", (String)bearer).POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            HashMap<String, Object> out = new HashMap<String, Object>();
            out.put("status", resp.statusCode());
            out.put("body", resp.body() != null ? resp.body() : "");
            return ResponseData.ok(out);
        }
        catch (Exception e) {
            return ResponseData.error((String)("chat-test \u8c03\u7528\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())));
        }
    }

    private static String jsonString(String s) {
        if (s == null) {
            return "null";
        }
        String t = s.replace("\\", "\\\\").replace("\"", "\\\"");
        t = t.replace("\r", "\\r").replace("\n", "\\n");
        return "\"" + t + "\"";
    }

    private static String trimToNullOrBlank(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String trimObj(Object v) {
        return v == null ? null : OracleAiController.trimToNullOrBlank((String)String.valueOf(v));
    }

    private static int intValue(Object v, int def) {
        if (v == null) {
            return def;
        }
        try {
            if (v instanceof Number) {
                Number n = (Number)v;
                return n.intValue();
            }
            return Integer.parseInt(String.valueOf(v).trim());
        }
        catch (Exception ignored) {
            return def;
        }
    }

    private static Integer nullableIntValue(Object v) {
        if (v == null) {
            return null;
        }
        String s = String.valueOf(v).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) {
            return null;
        }
        try {
            if (v instanceof Number) {
                Number n = (Number)v;
                return n.intValue();
            }
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("defaultMaxTokens \u5fc5\u987b\u662f\u6570\u5b57");
        }
    }

    private static boolean boolValue(Object v, boolean def) {
        if (v == null) {
            return def;
        }
        if (v instanceof Boolean) {
            Boolean b = (Boolean)v;
            return b;
        }
        String s = String.valueOf(v).trim();
        if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
            return true;
        }
        if ("false".equalsIgnoreCase(s) || "0".equals(s)) {
            return false;
        }
        return def;
    }

    private static List<String> stringListValue(Object v) {
        String s;
        ArrayList<String> out = new ArrayList<String>();
        if (v instanceof List) {
            List list = (List)v;
            for (Object o : list) {
                if (o == null) continue;
                out.add(String.valueOf(o));
            }
        } else if (v != null && !(s = String.valueOf(v).trim()).isBlank()) {
            out.add(s);
        }
        return OracleAiPortBindingService.normalizeAllowedModels(out);
    }

    private Map<String, Object> portBindingRow(OciOpenaiPortBinding b) {
        OciOpenaiKey key;
        OciUser u;
        HashMap<String, Object> row = new HashMap<String, Object>();
        if (b == null) {
            return row;
        }
        row.put("id", b.getId());
        row.put("name", b.getName());
        row.put("port", b.getPort());
        row.put("ociUserId", b.getOciUserId());
        row.put("ociRegion", b.getOciRegion());
        row.put("openaiKeyId", b.getOpenaiKeyId());
        row.put("defaultMaxTokens", b.getDefaultMaxTokens());
        row.put("allowedModels", OracleAiPortBindingService.decodeAllowedModels((String)b.getAllowedModelsJson()));
        row.put("enabled", b.getEnabled() != null && b.getEnabled() == 1);
        row.put("status", b.getStatus());
        row.put("statusMessage", b.getStatusMessage());
        row.put("createTime", b.getCreateTime());
        row.put("updateTime", b.getUpdateTime());
        row.put("lastUsed", b.getLastUsed());
        row.put("baseUrl", "http://<host>:" + b.getPort() + "/v1");
        OciUser ociUser = u = b.getOciUserId() == null ? null : (OciUser)this.ociUserMapper.selectById((Serializable)((Object)b.getOciUserId()));
        if (u != null) {
            row.put("tenantName", u.getUsername());
            if (row.get("ociRegion") == null || String.valueOf(row.get("ociRegion")).isBlank()) {
                row.put("ociRegion", u.getOciRegion());
            }
            row.put("tenantDefaultRegion", u.getOciRegion());
        }
        OciOpenaiKey ociOpenaiKey = key = b.getOpenaiKeyId() == null ? null : this.openaiKeyService.getById(b.getOpenaiKeyId());
        if (key != null) {
            row.put("keyMasked", this.openaiKeyService.maskForList(key));
            row.put("keyName", key.getName());
            row.put("keyDisabled", key.getDisabled() != null && key.getDisabled() == 1);
        }
        return row;
    }
}

