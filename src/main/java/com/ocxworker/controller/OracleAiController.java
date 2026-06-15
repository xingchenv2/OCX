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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
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

@RestController
@RequestMapping({"/api/oci/oracle-ai"})
public class OracleAiController {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OracleAiController.class);
    @Value("${ociworker.openaiApi.port:8080}")
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

    @PostMapping({"/gateway"})
    public ResponseData<?> gateway() {
        Map<String, Object> m = new HashMap<>();
        m.put("openaiApiPort", this.openaiApiPort);
        m.put("pathPrefix", "/v1");
        m.put("baseUrlExample", OciGenerativeOpenAiService.gatewayHint(this.openaiApiPort));
        m.put("serverIp", this.detectServerIp());
        m.put("openaiProxyEnabled", this.gatewayToggleService.isEnabled());
        m.put("defaultMaxTokens", this.gatewayConfigService.getDefaultMaxTokens());
        return ResponseData.ok(m);
    }

    private String detectServerIp() {
        String cached = this.cachedPublicIp;
        if (cached != null && !cached.isBlank() && Duration.between(this.cachedPublicIpAt, Instant.now()).compareTo(PUBLIC_IP_CACHE_TTL) < 0) {
            return cached;
        } else {
            for (String endpoint : PUBLIC_IPV4_ENDPOINTS) {
                try {
                    HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofSeconds(2L)).GET().build();
                    HttpResponse<String> resp = HttpClient.newHttpClient().send(req, BodyHandlers.ofString());
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        String ip = resp.body() == null ? "" : resp.body().trim();
                        if (IPV4_PATTERN.matcher(ip).matches()) {
                            this.cachedPublicIp = ip;
                            this.cachedPublicIpAt = Instant.now();
                            return ip;
                        }
                    }
                } catch (Exception var7) {
                    log.debug("Failed to detect public IPv4 from {}: {}", endpoint, var7.getMessage());
                }
            }

            return "";
        }
    }

    @PostMapping({"/ui-state/get"})
    public ResponseData<?> getUiState() {
        try {
            OciKv kv = (OciKv)this.kvMapper
                .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai.page_state.v1")).eq(OciKv::getType, "ui_state"));
            if (kv != null && kv.getValue() != null && !kv.getValue().isBlank()) {
                Object obj = this.objectMapper.readValue(kv.getValue(), Object.class);
                return ResponseData.ok(obj != null ? obj : Map.of());
            } else {
                return ResponseData.ok(Map.of());
            }
        } catch (Exception var3) {
            return ResponseData.ok(Map.of());
        }
    }

    @PostMapping({"/ui-state/save"})
    public ResponseData<?> saveUiState(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return ResponseData.error("参数错误");
        } else {
            String ociUserId = body.get("ociUserId") == null ? "" : String.valueOf(body.get("ociUserId")).trim();
            Object mp = body.get("modelPick");
            List<String> modelPick = new ArrayList<>();
            if (mp instanceof List) {
                for (Object o : (List)mp) {
                    if (o != null) {
                        String s = String.valueOf(o).trim();
                        if (!s.isBlank()) {
                            modelPick.add(s);
                        }
                    }
                }
            }

            if (ociUserId.length() > 128) {
                ociUserId = ociUserId.substring(0, 128);
            }

            if (modelPick.size() > 200) {
                modelPick = modelPick.subList(0, 200);
            }

            Map<String, Object> state = new HashMap<>();
            state.put("ociUserId", ociUserId);
            state.put("modelPick", modelPick);
            state.put("updateAt", System.currentTimeMillis());

            try {
                String json = this.objectMapper.writeValueAsString(state);
                OciKv existing = (OciKv)this.kvMapper
                    .selectOne(
                        (Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai.page_state.v1")).eq(OciKv::getType, "ui_state")
                    );
                if (existing != null) {
                    existing.setValue(json);
                    this.kvMapper.updateById(existing);
                } else {
                    OciKv kv = new OciKv();
                    kv.setId(CommonUtils.generateId());
                    kv.setCode("oracle_ai.page_state.v1");
                    kv.setType("ui_state");
                    kv.setValue(json);
                    this.kvMapper.insert(kv);
                }

                return ResponseData.ok();
            } catch (Exception var9) {
                return ResponseData.error("保存失败: " + (var9.getMessage() != null ? var9.getMessage() : "未知错误"));
            }
        }
    }

    @PostMapping({"/gateway/setEnabled"})
    public ResponseData<?> setGatewayEnabled(@RequestBody Map<String, Object> body) {
        Object v = body == null ? null : body.get("enabled");
        boolean enabled = v instanceof Boolean ? (Boolean)v : v != null && "true".equalsIgnoreCase(String.valueOf(v));
        this.gatewayToggleService.setEnabled(enabled);
        return ResponseData.ok(Map.of("openaiProxyEnabled", enabled));
    }

    @PostMapping({"/gateway/default-max-tokens"})
    public ResponseData<?> setDefaultMaxTokens(@RequestBody Map<String, Object> body) {
        Object raw = body == null ? null : body.get("defaultMaxTokens");
        if (raw == null) {
            raw = body == null ? null : body.get("max_tokens");
        }

        if (raw == null) {
            return ResponseData.error("defaultMaxTokens 必填");
        } else {
            int value;
            try {
                if (raw instanceof Number n) {
                    value = n.intValue();
                } else {
                    value = Integer.parseInt(String.valueOf(raw).trim());
                }
            } catch (Exception var5) {
                return ResponseData.error("defaultMaxTokens 必须是数字");
            }

            int saved = this.gatewayConfigService.setDefaultMaxTokens(value);
            return ResponseData.ok(Map.of("defaultMaxTokens", saved));
        }
    }

    @PostMapping({"/keys/create"})
    public ResponseData<?> createKey(@RequestBody Map<String, String> body) {
        String tid = body == null ? null : body.get("ociUserId");
        String name = body == null ? null : body.get("name");
        OciOpenaiKeyService.KeyCreateResult c = this.openaiKeyService.create(tid, name);
        Map<String, String> d = new HashMap<>();
        d.put("id", c.id());
        d.put("apiKey", c.plainKey());
        d.put("keyPrefix", c.keyPrefix());
        d.put("keyMasked", c.keyMasked());
        d.put("warning", "密钥已入库，可在列表中点击「查看」再次复制。对接 New API 时 API 地址为 http://<本机或域名>:" + this.openaiApiPort + "/v1");
        return ResponseData.ok(d);
    }

    @PostMapping({"/keys/reveal"})
    public ResponseData<?> revealKey(@RequestBody Map<String, String> body) {
        if (body != null && body.get("id") != null) {
            try {
                String plain = this.openaiKeyService.revealPlainKey(body.get("id"));
                return ResponseData.ok(Map.of("apiKey", plain));
            } catch (OciException var3) {
                return ResponseData.error(var3.getMessage());
            }
        } else {
            return ResponseData.error("id 必填");
        }
    }

    @PostMapping({"/keys/list"})
    public ResponseData<?> listKeys(@RequestBody Map<String, String> body) {
        String tid = body == null ? null : body.get("ociUserId");
        List<OciOpenaiKey> list = this.openaiKeyService.listByTenant(tid);
        return ResponseData.ok(list.stream().map(k -> {
            Map<String, Object> row = new HashMap<>();
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

    @PostMapping({"/keys/setDisabled"})
    public ResponseData<?> setDisabled(@RequestBody Map<String, Object> body) {
        if (body == null) {
            return ResponseData.error("参数错误");
        } else {
            String id = (String)body.get("id");
            Object d = body.get("disabled");
            boolean dis = d instanceof Boolean ? (Boolean)d : d != null && "true".equals(d.toString());
            this.openaiKeyService.setDisabled(id, dis);
            return ResponseData.ok();
        }
    }

    @PostMapping({"/keys/remove"})
    public ResponseData<?> removeKey(@RequestBody Map<String, String> body) {
        if (body != null && body.get("id") != null) {
            this.openaiKeyService.remove(body.get("id"));
            return ResponseData.ok();
        } else {
            return ResponseData.error("id 必填");
        }
    }

    @PostMapping({"/ports/list"})
    public ResponseData<?> listPortBindings() {
        List<OciOpenaiPortBinding> list = this.portBindingService.list();
        return ResponseData.ok(list.stream().map(this::portBindingRow).collect(Collectors.toList()));
    }

    @PostMapping({"/ports/save"})
    public ResponseData<?> savePortBinding(@RequestBody Map<String, Object> body) {
        try {
            String id = body == null ? null : trimObj(body.get("id"));
            String name = body == null ? null : trimObj(body.get("name"));
            String ociUserId = body == null ? null : trimObj(body.get("ociUserId"));
            String ociRegion = body == null ? null : trimObj(body.get("ociRegion"));
            String openaiKeyId = body == null ? null : trimObj(body.get("openaiKeyId"));
            int port = intValue(body == null ? null : body.get("port"), -1);
            Integer defaultMaxTokens = nullableIntValue(body == null ? null : body.get("defaultMaxTokens"));
            List<String> allowedModels = stringListValue(body == null ? null : body.get("allowedModels"));
            boolean enabled = boolValue(body == null ? null : body.get("enabled"), true);
            OciOpenaiPortBinding row = id == null
                ? this.portBindingService.create(name, port, ociUserId, ociRegion, openaiKeyId, defaultMaxTokens, allowedModels, enabled)
                : this.portBindingService.update(id, name, port, ociUserId, ociRegion, openaiKeyId, defaultMaxTokens, allowedModels, enabled);
            return ResponseData.ok(this.portBindingRow(row));
        } catch (OciException var12) {
            return ResponseData.error(var12.getMessage());
        } catch (Exception var13) {
            return ResponseData.error(var13.getMessage() != null ? var13.getMessage() : "保存失败");
        }
    }

    @PostMapping({"/ports/setEnabled"})
    public ResponseData<?> setPortBindingEnabled(@RequestBody Map<String, Object> body) {
        String id = body == null ? null : trimObj(body.get("id"));
        if (id == null) {
            return ResponseData.error("id 必填");
        } else {
            boolean enabled = boolValue(body.get("enabled"), true);

            try {
                this.portBindingService.setEnabled(id, enabled);
                return ResponseData.ok();
            } catch (OciException var5) {
                return ResponseData.error(var5.getMessage());
            }
        }
    }

    @PostMapping({"/ports/remove"})
    public ResponseData<?> removePortBinding(@RequestBody Map<String, Object> body) {
        String id = body == null ? null : trimObj(body.get("id"));
        if (id == null) {
            return ResponseData.error("id 必填");
        } else {
            this.portBindingService.remove(id);
            return ResponseData.ok();
        }
    }

    @PostMapping({"/models"})
    public ResponseData<?> models(@RequestBody Map<String, String> body) {
        if (body != null && body.get("ociUserId") != null) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(body.get("ociUserId"));
            if (u == null) {
                return ResponseData.error("租户不存在");
            } else {
                String after = body.get("after");
                String modelId = body.get("modelId");
                String ociRegion = trimToNullOrBlank(body.get("ociRegion"));

                try {
                    JsonNode j = this.generativeOpenAiService.getModelsAsJson(u, ociRegion, after, modelId);
                    return ResponseData.ok(j);
                } catch (OciException var7) {
                    return ResponseData.error(var7.getMessage());
                } catch (Exception var8) {
                    return ResponseData.error("拉取模型失败: " + (var8.getMessage() != null ? var8.getMessage() : "未知错误"));
                }
            }
        } else {
            return ResponseData.error("ociUserId 必填");
        }
    }

    @PostMapping({"/generative-projects/list"})
    public ResponseData<?> listGenerativeProjects(@RequestBody Map<String, String> body) {
        if (body != null && body.get("ociUserId") != null) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(body.get("ociUserId"));
            if (u == null) {
                return ResponseData.error("租户不存在");
            } else {
                try {
                    JsonNode j = this.generativeOpenAiService.listGenerativeAiProjectSummaries(u);
                    return ResponseData.ok(j);
                } catch (OciException var4) {
                    return ResponseData.error(var4.getMessage());
                } catch (Exception var5) {
                    return ResponseData.error("列举项目失败: " + (var5.getMessage() != null ? var5.getMessage() : "未知错误"));
                }
            }
        } else {
            return ResponseData.error("ociUserId 必填");
        }
    }

    @PostMapping({"/generative-projects/create"})
    public ResponseData<?> createGenerativeProject(@RequestBody Map<String, String> body) {
        if (body != null && body.get("ociUserId") != null) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(body.get("ociUserId"));
            if (u == null) {
                return ResponseData.error("租户不存在");
            } else {
                String displayName = body.get("displayName");

                try {
                    JsonNode j = this.generativeOpenAiService.createGenerativeAiProject(u, displayName);
                    if (j != null && j.isObject()) {
                        String id = j.get("id") != null && j.get("id").isTextual() ? j.get("id").asText() : null;
                        if (id != null && !id.isBlank()) {
                            u.setGenerativeOpenaiProject(id);
                            this.ociUserMapper.updateById(u);
                        }
                    }

                    return ResponseData.ok(j);
                } catch (OciException var6) {
                    return ResponseData.error(var6.getMessage());
                } catch (Exception var7) {
                    return ResponseData.error("创建项目失败: " + (var7.getMessage() != null ? var7.getMessage() : "未知错误"));
                }
            }
        } else {
            return ResponseData.error("ociUserId 必填");
        }
    }

    @PostMapping({"/generative-context/get"})
    public ResponseData<?> getGenerativeContext(@RequestBody Map<String, String> body) {
        if (body != null && body.get("ociUserId") != null) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(body.get("ociUserId"));
            if (u == null) {
                return ResponseData.error("租户不存在");
            } else {
                Map<String, String> m = new HashMap<>();
                m.put("generativeOpenaiProject", u.getGenerativeOpenaiProject());
                m.put("generativeConversationStoreId", u.getGenerativeConversationStoreId());
                return ResponseData.ok(m);
            }
        } else {
            return ResponseData.error("ociUserId 必填");
        }
    }

    @PostMapping({"/generative-context/save"})
    public ResponseData<?> saveGenerativeContext(@RequestBody Map<String, String> body) {
        if (body != null && body.get("ociUserId") != null) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(body.get("ociUserId"));
            if (u == null) {
                return ResponseData.error("租户不存在");
            } else {
                u.setGenerativeOpenaiProject(trimToNullOrBlank(body.get("generativeOpenaiProject")));
                u.setGenerativeConversationStoreId(trimToNullOrBlank(body.get("generativeConversationStoreId")));
                this.ociUserMapper.updateById(u);
                return ResponseData.ok();
            }
        } else {
            return ResponseData.error("ociUserId 必填");
        }
    }

    @PostMapping({"/chat-test"})
    public ResponseData<?> chatTest(@RequestBody Map<String, Object> body) {
        String apiKey = body == null ? null : String.valueOf(body.getOrDefault("apiKey", "")).trim();
        String model = body == null ? null : String.valueOf(body.getOrDefault("model", "")).trim();
        String input = body == null ? null : String.valueOf(body.getOrDefault("input", "")).trim();
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseData.error("apiKey 必填");
        } else if (model == null || model.isBlank()) {
            return ResponseData.error("model 必填");
        } else if (input != null && !input.isBlank()) {
            String bearer = apiKey.toLowerCase().startsWith("bearer ") ? apiKey : "Bearer " + apiKey;
            boolean multiAgent = model.toLowerCase().contains("multi-agent")
                || model.toLowerCase().contains("multi agent")
                || model.toLowerCase().contains("multiagent");
            String url = "http://127.0.0.1:" + this.openaiApiPort + (multiAgent ? "/v1/responses" : "/v1/chat/completions");
            log.info("chat-test -> {} model={} (multiAgent={})", new Object[]{url, model, multiAgent});
            String payload;
            if (multiAgent) {
                payload = "{\"model\":"
                    + jsonString(model)
                    + ",\"input\":[{\"role\":\"user\",\"content\":[{\"type\":\"input_text\",\"text\":"
                    + jsonString(input)
                    + "}]}],\"stream\":false}";
            } else {
                payload = "{\"model\":" + jsonString(model) + ",\"messages\":[{\"role\":\"user\",\"content\":" + jsonString(input) + "}],\"stream\":false}";
            }

            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(120L))
                    .header("content-type", "application/json")
                    .header("authorization", bearer)
                    .POST(BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
                HttpResponse<String> resp = HttpClient.newHttpClient().send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
                Map<String, Object> out = new HashMap<>();
                out.put("status", resp.statusCode());
                out.put("body", resp.body() != null ? resp.body() : "");
                return ResponseData.ok(out);
            } catch (Exception var12) {
                return ResponseData.error("chat-test 调用失败: " + (var12.getMessage() != null ? var12.getMessage() : var12.getClass().getSimpleName()));
            }
        } else {
            return ResponseData.error("input 必填");
        }
    }

    private static String jsonString(String s) {
        if (s == null) {
            return "null";
        } else {
            String t = s.replace("\\", "\\\\").replace("\"", "\\\"");
            t = t.replace("\r", "\\r").replace("\n", "\\n");
            return "\"" + t + "\"";
        }
    }

    private static String trimToNullOrBlank(String s) {
        if (s == null) {
            return null;
        } else {
            String t = s.trim();
            return t.isEmpty() ? null : t;
        }
    }

    private static String trimObj(Object v) {
        return v == null ? null : trimToNullOrBlank(String.valueOf(v));
    }

    private static int intValue(Object v, int def) {
        if (v == null) {
            return def;
        } else {
            try {
                return v instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(v).trim());
            } catch (Exception var3) {
                return def;
            }
        }
    }

    private static Integer nullableIntValue(Object v) {
        if (v == null) {
            return null;
        } else {
            String s = String.valueOf(v).trim();
            if (!s.isEmpty() && !"null".equalsIgnoreCase(s)) {
                try {
                    return v instanceof Number n ? n.intValue() : Integer.parseInt(s);
                } catch (Exception var3) {
                    throw new IllegalArgumentException("defaultMaxTokens 必须是数字");
                }
            } else {
                return null;
            }
        }
    }

    private static boolean boolValue(Object v, boolean def) {
        if (v == null) {
            return def;
        } else if (v instanceof Boolean b) {
            return b;
        } else {
            String s = String.valueOf(v).trim();
            if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
                return true;
            } else {
                return !"false".equalsIgnoreCase(s) && !"0".equals(s) ? def : false;
            }
        }
    }

    private static List<String> stringListValue(Object v) {
        List<String> out = new ArrayList<>();
        if (v instanceof List) {
            for (Object o : (List)v) {
                if (o != null) {
                    out.add(String.valueOf(o));
                }
            }
        } else if (v != null) {
            String s = String.valueOf(v).trim();
            if (!s.isBlank()) {
                out.add(s);
            }
        }

        return OracleAiPortBindingService.normalizeAllowedModels(out);
    }

    private Map<String, Object> portBindingRow(OciOpenaiPortBinding b) {
        Map<String, Object> row = new HashMap<>();
        if (b == null) {
            return row;
        } else {
            row.put("id", b.getId());
            row.put("name", b.getName());
            row.put("port", b.getPort());
            row.put("ociUserId", b.getOciUserId());
            row.put("ociRegion", b.getOciRegion());
            row.put("openaiKeyId", b.getOpenaiKeyId());
            row.put("defaultMaxTokens", b.getDefaultMaxTokens());
            row.put("allowedModels", OracleAiPortBindingService.decodeAllowedModels(b.getAllowedModelsJson()));
            row.put("enabled", b.getEnabled() != null && b.getEnabled() == 1);
            row.put("status", b.getStatus());
            row.put("statusMessage", b.getStatusMessage());
            row.put("createTime", b.getCreateTime());
            row.put("updateTime", b.getUpdateTime());
            row.put("lastUsed", b.getLastUsed());
            row.put("baseUrl", "http://<host>:" + b.getPort() + "/v1");
            OciUser u = b.getOciUserId() == null ? null : (OciUser)this.ociUserMapper.selectById(b.getOciUserId());
            if (u != null) {
                row.put("tenantName", u.getUsername());
                if (row.get("ociRegion") == null || String.valueOf(row.get("ociRegion")).isBlank()) {
                    row.put("ociRegion", u.getOciRegion());
                }

                row.put("tenantDefaultRegion", u.getOciRegion());
            }

            OciOpenaiKey key = b.getOpenaiKeyId() == null ? null : this.openaiKeyService.getById(b.getOpenaiKeyId());
            if (key != null) {
                row.put("keyMasked", this.openaiKeyService.maskForList(key));
                row.put("keyName", key.getName());
                row.put("keyDisabled", key.getDisabled() != null && key.getDisabled() == 1);
            }

            return row;
        }
    }
}
