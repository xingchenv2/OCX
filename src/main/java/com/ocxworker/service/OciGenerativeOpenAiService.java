package com.ocxworker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ocxworker.exception.OciException;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.util.OciBasicForSigning;
import com.ocxworker.util.OciDuplicatableByteArrayInputStream;
import com.ocxworker.util.OciRegionUtil;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.IntSupplier;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OciGenerativeOpenAiService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OciGenerativeOpenAiService.class);
    public static final int DEFAULT_MAX_TOKENS = 2048;
    private static final String V1 = "/v1";
    private static final String GA_API_VERSION = "20231130";
    private static final int LIST_PAGE_LIMIT = 200;
    private static final int LIST_MAX_PAGES = 50;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static volatile IntSupplier defaultMaxTokensSupplier = () -> 2048;
    @Resource
    private OciProxyConfigService ociProxyConfigService;
    @Resource
    private OracleAiGatewayConfigService gatewayConfigService;

    @PostConstruct
    public void initDefaultMaxTokensSupplier() {
        defaultMaxTokensSupplier = this.gatewayConfigService::getDefaultMaxTokens;
    }

    public void proxy(OciUser tenant, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathAfterV1 = extractPathAfterV1(request);
        if (pathAfterV1 == null || pathAfterV1.isEmpty() || pathAfterV1.equals("/")) {
            pathAfterV1 = "/";
        }

        if (!pathAfterV1.startsWith("/")) {
            pathAfterV1 = "/" + pathAfterV1;
        }

        String origPathAfterV1 = pathAfterV1;
        String regionId = effectivePublicRegionId(tenant, request.getAttribute("ociworker.openai.ociRegion"));
        String baseOpenAi = "https://inference.generativeai." + regionId + ".oci.oraclecloud.com/openai/v1";
        String baseRawV1 = "https://inference.generativeai." + regionId + ".oci.oraclecloud.com/v1";
        String query = request.getQueryString();
        RequestSigner signer = newRequestSigner(tenant, regionId);
        String method = request.getMethod().toUpperCase();
        String accept = request.getHeader("Accept");
        if (accept == null || accept.isBlank()) {
            accept = "*/*";
        }

        String contentType = request.getContentType();
        if (contentType != null && contentType.contains(";")) {
            contentType = contentType.split(";")[0].trim();
        }

        byte[] body = null;
        if (!"GET".equals(method) && !"HEAD".equals(method) && !"DELETE".equals(method)) {
            body = request.getInputStream().readAllBytes();
        }

        byte[] origBody = body;
        int requestDefaultMaxTokens = requestDefaultMaxTokens(request);
        List<String> requestAllowedModels = requestAllowedModels(request);
        if ("GET".equalsIgnoreCase(method) && isModelsPath(pathAfterV1) && !requestAllowedModels.isEmpty()) {
            writeJson(response, allowedModelsToOpenAiList(requestAllowedModels));
        } else {
            String requestedModel = extractModelFromBody(body, contentType);
            if ((isChatCompletionsPath(pathAfterV1) || isResponsesPath(pathAfterV1)) && !isAllowedModel(requestedModel, requestAllowedModels)) {
                writeOpenAiError(response, 400, "invalid_request_error", "Model is not allowed for this port binding: " + requestedModel, "model_not_allowed");
            } else {
                request.setAttribute("ociworker.debug.origPathAfterV1", pathAfterV1);
                boolean looksLikeJson = contentType == null || contentType.isBlank() || contentType.toLowerCase().contains("json");
                if ("POST".equalsIgnoreCase(method) && isChatCompletionsPath(pathAfterV1) && body != null && body.length > 0) {
                    boolean bodyMentionsMultiAgent = false;

                    try {
                        String raw = new String(origBody, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                        bodyMentionsMultiAgent = raw.contains("multi-agent") || raw.contains("multiagent") || raw.contains("multi agent");
                    } catch (Exception var31) {
                    }

                    if (bodyMentionsMultiAgent) {
                        if (isStreamRequest(body, contentType)) {
                            request.setAttribute("ociworker.rewrite.simulateSse", Boolean.TRUE);
                        }

                        request.setAttribute("ociworker.rewrite.chatToResponses", Boolean.TRUE);
                        request.setAttribute("ociworker.rewrite.useRawV1Base", Boolean.TRUE);
                        request.setAttribute("ociworker.rewrite.model", "multi-agent");
                        pathAfterV1 = "/responses";
                        body = transformChatCompletionsToResponsesJson(body, requestDefaultMaxTokens);

                        try {
                            if (request != null && body != null) {
                                request.setAttribute("ociworker.debug.responsesInputShape.before", describeResponsesInputShape(body));
                            }

                            body = normalizeResponsesInputForOci(body);
                            body = truncateResponsesInputForMultiAgent(body, 20);
                            if (request != null && body != null) {
                                request.setAttribute("ociworker.debug.responsesInputShape.after", describeResponsesInputShape(body));
                            }
                        } catch (Exception var29) {
                        }
                    } else if (looksLikeJson) {
                        try {
                            JsonNode root = MAPPER.readTree(origBody);
                            if (root != null && root.isObject()) {
                                String model = textOrNull((ObjectNode)root, "model");
                                if (isLikelyMultiAgentModelName(model)) {
                                    if (isStreamRequest(origBody, contentType)) {
                                        log.debug("Multi Agent 模型在 chat/completions 上收到 stream=true，将改写为 /v1/responses 且按非流式处理");
                                        request.setAttribute("ociworker.rewrite.simulateSse", Boolean.TRUE);
                                    }

                                    request.setAttribute("ociworker.rewrite.chatToResponses", Boolean.TRUE);
                                    if (model != null) {
                                        request.setAttribute("ociworker.rewrite.model", model);
                                    }

                                    request.setAttribute("ociworker.rewrite.useRawV1Base", Boolean.TRUE);
                                    pathAfterV1 = "/responses";
                                    body = transformChatCompletionsToResponsesJson(origBody, requestDefaultMaxTokens);

                                    try {
                                        if (request != null && body != null) {
                                            request.setAttribute("ociworker.debug.responsesInputShape.before", describeResponsesInputShape(body));
                                        }

                                        body = normalizeResponsesInputForOci(body);
                                        body = truncateResponsesInputForMultiAgent(body, 20);
                                        if (request != null && body != null) {
                                            request.setAttribute("ociworker.debug.responsesInputShape.after", describeResponsesInputShape(body));
                                        }
                                    } catch (Exception var28) {
                                    }
                                } else if (isChatCompletionsPath(origPathAfterV1)) {
                                    body = transformChatCompletionsJson(origBody, requestDefaultMaxTokens);
                                }
                            } else if (isChatCompletionsPath(origPathAfterV1)) {
                                body = transformChatCompletionsJson(origBody, requestDefaultMaxTokens);
                            }
                        } catch (Exception var30) {
                            body = transformChatCompletionsJson(origBody, requestDefaultMaxTokens);
                        }
                    } else if (body != null && body.length > 0 && looksLikeJson) {
                        body = transformChatCompletionsJson(body, requestDefaultMaxTokens);
                    }
                } else if (isChatCompletionsPath(pathAfterV1) && body != null && body.length > 0 && looksLikeJson) {
                    body = transformChatCompletionsJson(body, requestDefaultMaxTokens);
                }

                if ("POST".equalsIgnoreCase(method) && isResponsesPath(origPathAfterV1) && body != null && body.length > 0 && looksLikeJson) {
                    try {
                        if (request != null) {
                            request.setAttribute("ociworker.debug.responsesInputShape.before", describeResponsesInputShape(body));
                        }

                        body = normalizeResponsesInputForOci(body);
                        body = truncateResponsesInputForMultiAgent(body, 20);
                        if (request != null) {
                            request.setAttribute("ociworker.debug.responsesInputShape.after", describeResponsesInputShape(body));
                            if (isStreamRequest(origBody, contentType)) {
                                request.setAttribute("ociworker.rewrite.forceBuffer", Boolean.TRUE);
                            }
                        }
                    } catch (Exception var27) {
                    }
                }

                boolean useRawV1Base = Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.useRawV1Base"));
                if (!useRawV1Base && isResponsesPath(origPathAfterV1) && origBody != null && origBody.length > 0 && looksLikeJson) {
                    try {
                        JsonNode root = MAPPER.readTree(origBody);
                        if (root != null && root.isObject()) {
                            String model = textOrNull((ObjectNode)root, "model");
                            if (isLikelyMultiAgentModelName(model)) {
                                useRawV1Base = true;
                            }
                        }
                    } catch (Exception var26) {
                    }
                }

                request.setAttribute("ociworker.debug.finalPathAfterV1", pathAfterV1);
                StringBuilder u = new StringBuilder(useRawV1Base ? baseRawV1 : baseOpenAi);
                u.append(pathAfterV1);
                if (query != null && !query.isEmpty()) {
                    u.append("?").append(query);
                }

                URI target = URI.create(u.toString());
                HttpRequest httpRequest = this.buildSignedRequest(
                    signer,
                    method,
                    target,
                    body,
                    contentType,
                    accept,
                    tenant != null ? tenant.getOciTenantId() : null,
                    extractOciGenerativeForwardHeaders(request, tenant)
                );
                HttpClient client = this.pickHttpClient();
                boolean useStreamCopy = (isChatCompletionsPath(origPathAfterV1) || isResponsesPath(origPathAfterV1))
                    && isStreamRequest(origBody, contentType)
                    && !Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.chatToResponses"))
                    && !Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.forceBuffer"));
                if (useStreamCopy) {
                    this.longCopyStream(client, httpRequest, response, request);
                } else {
                    this.bufferAndCopy(client, httpRequest, response, request);
                }
            }
        }
    }

    public JsonNode getModelsAsJson(OciUser tenant) throws Exception {
        return this.getModelsAsJson(tenant, null, null);
    }

    public JsonNode getModelsAsJson(OciUser tenant, String after, String modelId) throws Exception {
        return this.getModelsAsJson(tenant, null, after, modelId);
    }

    public JsonNode getModelsAsJson(OciUser tenant, String ociRegion, String after, String modelId) throws Exception {
        String regionId = effectivePublicRegionId(tenant, ociRegion);
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String tenantId = tenant.getOciTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            if (modelId != null && !modelId.isBlank()) {
                String path = "/20231130/models/" + encodePathSegmentOciModel(modelId);
                return this.managementGetToOpenAiList(tenant, regionId, "https://" + managementHost + path, true);
            } else {
                List<JsonNode> all = new ArrayList<>();
                String page = after != null && !after.isBlank() ? after : null;

                for (int p = 0; p < 50; p++) {
                    String q = "compartmentId=" + URLEncoder.encode(tenantId, StandardCharsets.UTF_8) + "&limit=200";
                    if (page != null) {
                        q = q + "&page=" + URLEncoder.encode(page, StandardCharsets.UTF_8);
                    }

                    URI listUri = URI.create("https://" + managementHost + "/20231130/models?" + q);
                    HttpRequest req = this.buildSignedRequest(
                        newRequestSigner(tenant, regionId), "GET", listUri, null, "application/json", "application/json", tenantId, null
                    );

                    HttpResponse<String> resp;
                    try {
                        resp = this.pickHttpClient().send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
                    } catch (Exception var19) {
                        throw new OciException(
                            "拉取 models 异常(" + var19.getClass().getSimpleName() + "): " + (var19.getMessage() != null ? var19.getMessage() : "未知错误")
                        );
                    }

                    if (resp.statusCode() / 100 != 2) {
                        throw new OciException(
                            "拉取 models 失败: HTTP "
                                + resp.statusCode()
                                + " headers="
                                + truncate(String.valueOf(resp.headers().map()), 500)
                                + " body="
                                + truncate(resp.body(), 500)
                        );
                    }

                    JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
                    JsonNode items = root.get("items");
                    if (items != null && items.isArray()) {
                        for (JsonNode it : items) {
                            all.add(it);
                        }
                    }

                    String next = resp.headers().firstValue("opc-next-page").orElse(null);
                    if (next == null || next.isBlank()) {
                        break;
                    }

                    page = next;
                }

                return this.ociModelsToOpenAiList(MAPPER.createObjectNode().set("items", toArrayNode(all)));
            }
        } else {
            throw new OciException("租户无 ociTenantId，无法 list models");
        }
    }

    public JsonNode listGenerativeAiProjectSummaries(OciUser tenant) throws Exception {
        String regionId = OciRegionUtil.publicRegionId(tenant.getOciRegion());
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String compartmentId = tenant.getOciTenantId();
        if (compartmentId != null && !compartmentId.isBlank()) {
            List<JsonNode> all = new ArrayList<>();
            String page = null;

            for (int p = 0; p < 50; p++) {
                String q = "compartmentId=" + URLEncoder.encode(compartmentId, StandardCharsets.UTF_8) + "&limit=200";
                if (page != null) {
                    q = q + "&page=" + URLEncoder.encode(page, StandardCharsets.UTF_8);
                }

                URI listUri = URI.create("https://" + managementHost + "/20231130/generativeAiProjects?" + q);
                HttpRequest req = this.buildSignedRequest(
                    newRequestSigner(tenant), "GET", listUri, null, "application/json", "application/json", compartmentId, null
                );

                HttpResponse<String> resp;
                try {
                    resp = this.pickHttpClient().send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
                } catch (Exception var16) {
                    throw new OciException(
                        "列举 generativeAiProjects 异常(" + var16.getClass().getSimpleName() + "): " + (var16.getMessage() != null ? var16.getMessage() : "未知错误")
                    );
                }

                if (resp.statusCode() / 100 != 2) {
                    throw new OciException("列举 generativeAiProjects 失败: HTTP " + resp.statusCode() + " body=" + truncate(resp.body(), 800));
                }

                JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
                JsonNode items = root.get("items");
                if (items != null && items.isArray()) {
                    for (JsonNode it : items) {
                        all.add(it);
                    }
                }

                String next = resp.headers().firstValue("opc-next-page").orElse(null);
                if (next == null || next.isBlank()) {
                    break;
                }

                page = next;
            }

            ArrayNode arr = MAPPER.createArrayNode();

            for (JsonNode it : all) {
                if (it != null && it.isObject()) {
                    String id = firstText(it, "id");
                    if (id != null && !id.isBlank()) {
                        ObjectNode row = MAPPER.createObjectNode();
                        row.put("id", id);
                        String dn = firstText(it, "displayName");
                        if (dn != null && !dn.isBlank()) {
                            row.put("displayName", dn);
                        }

                        arr.add(row);
                    }
                }
            }

            ObjectNode out = MAPPER.createObjectNode();
            out.set("items", arr);
            return out;
        } else {
            throw new OciException("租户无 ociTenantId，无法列举 Generative AI 项目");
        }
    }

    public JsonNode createGenerativeAiProject(OciUser tenant, String displayName) throws Exception {
        String regionId = OciRegionUtil.publicRegionId(tenant.getOciRegion());
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String compartmentId = tenant.getOciTenantId();
        if (compartmentId != null && !compartmentId.isBlank()) {
            String name = displayName != null && !displayName.isBlank() ? displayName.trim() : "ociworker-default";
            ObjectNode body = MAPPER.createObjectNode();
            body.put("compartmentId", compartmentId);
            body.put("displayName", name);
            ObjectNode conversationConfig = MAPPER.createObjectNode();
            conversationConfig.put("responsesRetentionInHours", 720);
            conversationConfig.put("conversationsRetentionInHours", 720);
            body.set("conversationConfig", conversationConfig);
            byte[] bytes = MAPPER.writeValueAsBytes(body);
            URI uri = URI.create("https://" + managementHost + "/20231130/generativeAiProjects");
            HttpRequest req = this.buildSignedRequest(newRequestSigner(tenant), "POST", uri, bytes, "application/json", "application/json", compartmentId, null);

            HttpResponse<String> resp;
            try {
                resp = this.pickHttpClient().send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (Exception var17) {
                throw new OciException(
                    "创建 generativeAiProject 异常(" + var17.getClass().getSimpleName() + "): " + (var17.getMessage() != null ? var17.getMessage() : "未知错误")
                );
            }

            if (resp.statusCode() / 100 != 2) {
                String rid = resp.headers().firstValue("opc-request-id").orElse("");
                throw new OciException(
                    "创建 generativeAiProject 失败: HTTP "
                        + resp.statusCode()
                        + (rid.isBlank() ? "" : " opc-request-id=" + rid)
                        + " body="
                        + truncate(resp.body(), 1200)
                );
            } else {
                JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
                if (root != null && root.isObject()) {
                    ObjectNode out = MAPPER.createObjectNode();
                    String id = firstText(root, "id");
                    if (id != null) {
                        out.put("id", id);
                    }

                    String dn = firstText(root, "displayName");
                    if (dn != null) {
                        out.put("displayName", dn);
                    }

                    return out;
                } else {
                    return root;
                }
            }
        } else {
            throw new OciException("租户无 ociTenantId，无法创建 Generative AI 项目");
        }
    }

    private static ArrayNode toArrayNode(List<JsonNode> nodes) {
        ArrayNode a = MAPPER.createArrayNode();

        for (JsonNode n : nodes) {
            a.add(n);
        }

        return a;
    }

    private JsonNode managementGetToOpenAiList(OciUser tenant, String regionId, String url, boolean oneItemAsList) throws Exception {
        RequestSigner signer = newRequestSigner(tenant, regionId);
        URI uri = URI.create(url);
        HttpRequest req = this.buildSignedRequest(
            signer, "GET", uri, null, "application/json", "application/json", tenant != null ? tenant.getOciTenantId() : null, null
        );

        HttpResponse<String> resp;
        try {
            resp = this.pickHttpClient().send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception var10) {
            throw new OciException("拉取 models 异常(" + var10.getClass().getSimpleName() + "): " + (var10.getMessage() != null ? var10.getMessage() : "未知错误"));
        }

        if (resp.statusCode() / 100 != 2) {
            throw new OciException(
                "拉取 models 失败: HTTP "
                    + resp.statusCode()
                    + " headers="
                    + truncate(String.valueOf(resp.headers().map()), 500)
                    + " body="
                    + truncate(resp.body(), 500)
            );
        } else {
            return this.ociModelsToOpenAiList(MAPPER.readTree(resp.body() != null ? resp.body() : "{}"), oneItemAsList);
        }
    }

    private JsonNode ociModelsToOpenAiList(JsonNode ociBody) {
        return this.ociModelsToOpenAiList(ociBody, false);
    }

    private JsonNode ociModelsToOpenAiList(JsonNode ociBody, boolean single) {
        ArrayNode outItems = MAPPER.createArrayNode();
        if (single && ociBody != null && !ociBody.isObject()) {
            return buildOpenAiModelList(outItems);
        } else {
            if (single && ociBody != null && ociBody.isObject() && !ociBody.has("items") && ociBody.has("id")) {
                outItems.add(ociItemToOpenAi(ociBody));
            } else if (ociBody != null && ociBody.isObject() && ociBody.has("items")) {
                for (JsonNode n : ociBody.withArray("items")) {
                    outItems.add(ociItemToOpenAi(n));
                }
            }

            return buildOpenAiModelList(outItems);
        }
    }

    private static ObjectNode buildOpenAiModelList(ArrayNode data) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("object", "list");
        root.set("data", data);
        return root;
    }

    private static ObjectNode ociItemToOpenAi(JsonNode oci) {
        ObjectNode row = MAPPER.createObjectNode();
        String id = firstText(oci, "name", "modelName", "model", "modelId");
        JsonNode display = oci != null ? (oci.get("displayName") != null ? oci.get("displayName") : oci.get("modelName")) : null;
        String dn = display != null && display.isTextual() ? display.asText().trim() : null;
        if (dn != null && !dn.isBlank()) {
            String dnl = dn.toLowerCase(Locale.ROOT);
            boolean looksLikeModelName = dnl.startsWith("xai.")
                || dnl.startsWith("cohere.")
                || dnl.startsWith("meta.")
                || dnl.startsWith("mistral.")
                || dnl.startsWith("openai.")
                || dn.matches("^[a-z0-9]+\\.[a-z0-9._\\-]+$");
            if (looksLikeModelName) {
                id = dn;
            }
        }

        if ((id == null || id.isBlank()) && dn != null && !dn.isBlank()) {
            id = dn;
        }

        if ((id == null || id.isBlank()) && oci != null) {
            JsonNode idn = oci.get("id");
            if (idn != null && !idn.isNull()) {
                id = idn.asText();
            }
        }

        if (id == null || id.isBlank()) {
            id = "unknown";
        }

        row.put("id", id);
        row.put("object", "model");
        if (dn != null && !dn.isBlank()) {
            row.put("displayName", dn);
        }

        if (oci != null) {
            JsonNode ociId = oci.get("id");
            if (ociId != null && ociId.isTextual() && !ociId.asText().isBlank()) {
                row.put("ociId", ociId.asText());
            }
        }

        if (isLikelyMultiAgentModelName(id) || display != null && display.isTextual() && isLikelyMultiAgentModelName(display.asText())) {
            row.put(
                "ociworkerNote",
                "该模型为 Multi Agent：本网关会把 /v1/chat/completions 改写为 /v1/responses 并尽量把响应装成 chat.completion。 OCI 通常要求 OpenAI-Project 或 opc-conversation-store-id；可在「Oracle 生成式 AI」页为租户保存默认值，或由上游转发明文头。"
            );
        }

        return row;
    }

    private static boolean isLikelyMultiAgentModelName(String s) {
        if (s == null) {
            return false;
        } else {
            String t = s.toLowerCase();
            return t.contains("multi-agent") || t.contains("multi agent") || t.contains("multiagent");
        }
    }

    private static String firstText(JsonNode o, String... fieldNames) {
        if (o == null) {
            return null;
        } else {
            for (String f : fieldNames) {
                JsonNode n = o.get(f);
                if (n != null && n.isTextual() && !n.asText().isBlank()) {
                    return n.asText();
                }
            }

            return null;
        }
    }

    public static String extractPathAfterV1(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String path = request.getContextPath() != null ? request.getContextPath() : "";
        if (!path.isEmpty() && uri.startsWith(path)) {
            uri = uri.substring(path.length());
        }

        int p = uri.indexOf("/v1");
        if (p < 0) {
            return "/";
        } else {
            String sub = uri.substring(p + "/v1".length());
            if (sub.isEmpty()) {
                return "/";
            } else {
                return !sub.startsWith("/") ? "/" + sub : sub;
            }
        }
    }

    public static String gatewayHint(int openaiPort) {
        return "http://<本机或域名>:" + openaiPort + "/v1";
    }

    public static SimpleAuthenticationDetailsProvider buildProvider(OciUser tenant) {
        return buildProvider(tenant, null);
    }

    public static SimpleAuthenticationDetailsProvider buildProvider(OciUser tenant, String regionId) {
        if (tenant == null) {
            throw new OciException("租户无效");
        } else {
            String effectiveRegion = effectivePublicRegionId(tenant, regionId);
            return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(tenant.getOciTenantId())
                .userId(tenant.getOciUserId())
                .fingerprint(tenant.getOciFingerprint())
                .region(OciRegionUtil.toRegion(effectiveRegion))
                .privateKeySupplier(() -> {
                    try {
                        ByteArrayInputStream var5;
                        try (
                            FileInputStream fis = new FileInputStream(tenant.getOciKeyPath());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ) {
                            byte[] buffer = new byte[1024];

                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                baos.write(buffer, 0, bytesRead);
                            }

                            var5 = new ByteArrayInputStream(baos.toByteArray());
                        }

                        return var5;
                    } catch (Exception var10) {
                        throw new OciException("无法读取 OCI 私钥: " + var10.getMessage());
                    }
                })
                .build();
        }
    }

    private static RequestSigner newRequestSigner(OciUser tenant) {
        return newRequestSigner(tenant, null);
    }

    private static RequestSigner newRequestSigner(OciUser tenant, String regionId) {
        return DefaultRequestSigner.createRequestSigner(OciBasicForSigning.from(buildProvider(tenant, regionId)));
    }

    private static String effectivePublicRegionId(OciUser tenant, Object region) {
        String r = region == null ? null : String.valueOf(region).trim();
        if (r == null || r.isEmpty() || "null".equalsIgnoreCase(r)) {
            r = tenant == null ? null : tenant.getOciRegion();
        }

        return OciRegionUtil.publicRegionId(r);
    }

    private static String encodePathSegmentOciModel(String s) {
        return s == null ? "" : URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static boolean isChatCompletionsPath(String p) {
        return p != null && (p.equals("/chat/completions") || p.endsWith("/chat/completions"));
    }

    private static boolean isResponsesPath(String p) {
        return p != null && (p.equals("/responses") || p.endsWith("/responses"));
    }

    private static boolean isModelsPath(String p) {
        return p != null && (p.equals("/models") || p.endsWith("/models"));
    }

    private static String extractModelFromBody(byte[] body, String contentType) {
        if (body != null && body.length != 0) {
            if (contentType != null && !contentType.isBlank() && !contentType.toLowerCase().contains("json")) {
                return null;
            } else {
                try {
                    JsonNode root = MAPPER.readTree(body);
                    if (root != null && root.isObject()) {
                        return textOrNull((ObjectNode)root, "model");
                    }
                } catch (Exception var3) {
                }

                return null;
            }
        } else {
            return null;
        }
    }

    private static boolean isAllowedModel(String model, List<String> allowedModels) {
        if (allowedModels != null && !allowedModels.isEmpty()) {
            if (model != null && !model.isBlank()) {
                Set<String> allowed = new HashSet<>();

                for (String item : allowedModels) {
                    if (item != null && !item.isBlank()) {
                        allowed.add(item.trim());
                    }
                }

                return allowed.contains(model.trim());
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private static List<String> requestAllowedModels(HttpServletRequest request) {
        if (request == null) {
            return List.of();
        } else {
            Object v = request.getAttribute("ociworker.openai.allowedModelsJson");
            return v == null ? List.of() : OracleAiPortBindingService.decodeAllowedModels(String.valueOf(v));
        }
    }

    private static ObjectNode allowedModelsToOpenAiList(List<String> models) {
        ArrayNode data = MAPPER.createArrayNode();
        if (models != null) {
            for (String model : models) {
                if (model != null && !model.isBlank()) {
                    ObjectNode row = MAPPER.createObjectNode();
                    row.put("id", model.trim());
                    row.put("object", "model");
                    data.add(row);
                }
            }
        }

        return buildOpenAiModelList(data);
    }

    private static void writeJson(HttpServletResponse response, JsonNode body) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        response.getOutputStream().write(MAPPER.writeValueAsBytes(body));
    }

    private static void writeOpenAiError(HttpServletResponse response, int status, String type, String message, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=utf-8");
        ObjectNode root = MAPPER.createObjectNode();
        ObjectNode error = MAPPER.createObjectNode();
        error.put("type", type);
        error.put("message", message);
        error.put("code", code);
        root.set("error", error);
        response.getOutputStream().write(MAPPER.writeValueAsBytes(root));
    }

    private static byte[] normalizeResponsesInputForOci(byte[] input) {
        try {
            JsonNode root = MAPPER.readTree(input);
            if (root != null && root.isObject()) {
                ObjectNode in = (ObjectNode)root;
                JsonNode inputNode = in.get("input");
                if (inputNode != null && !inputNode.isNull() && !inputNode.isMissingNode()) {
                    if (inputNode.isTextual()) {
                        ArrayNode arr = MAPPER.createArrayNode();
                        ObjectNode item = MAPPER.createObjectNode();
                        item.put("role", "user");
                        ArrayNode parts = MAPPER.createArrayNode();
                        ObjectNode p = MAPPER.createObjectNode();
                        p.put("type", "input_text");
                        p.put("text", inputNode.asText());
                        parts.add(p);
                        item.set("content", parts);
                        arr.add(item);
                        in.set("input", arr);
                        return MAPPER.writeValueAsBytes(in);
                    } else {
                        if (inputNode.isObject()) {
                            ObjectNode io = (ObjectNode)inputNode;
                            JsonNode msgs = io.get("messages");
                            if (msgs != null && msgs.isArray()) {
                                ObjectNode fauxChat = MAPPER.createObjectNode();
                                fauxChat.set("messages", msgs);
                                byte[] mapped = transformChatCompletionsToResponsesJson(MAPPER.writeValueAsBytes(fauxChat), defaultMaxTokens());
                                JsonNode mappedRoot = MAPPER.readTree(mapped);
                                if (mappedRoot != null && mappedRoot.isObject() && mappedRoot.get("input") != null) {
                                    in.set("input", mappedRoot.get("input"));
                                    return MAPPER.writeValueAsBytes(in);
                                }
                            }

                            ArrayNode arr = MAPPER.createArrayNode();
                            ObjectNode item = MAPPER.createObjectNode();
                            String role = textOrNull(io, "role");
                            item.put("role", role != null && !role.isBlank() ? role : "user");
                            JsonNode content = io.get("content");
                            if (content != null && content.isTextual()) {
                                item.set("content", toInputTextParts(content.asText()));
                            } else if (content != null && content.isArray()) {
                                item.set("content", content);
                            } else if (content != null && content.isObject()) {
                                item.set("content", toInputTextParts(content.toString()));
                            } else {
                                item.set("content", toInputTextParts(String.valueOf(content)));
                            }

                            arr.add(item);
                            in.set("input", arr);
                            inputNode = in.get("input");
                        }

                        if (!inputNode.isArray()) {
                            return input;
                        } else {
                            ArrayNode outArr = MAPPER.createArrayNode();

                            for (JsonNode it : inputNode) {
                                if (it != null) {
                                    if (it.isTextual()) {
                                        ObjectNode item = MAPPER.createObjectNode();
                                        item.put("role", "user");
                                        item.set("content", toInputTextParts(it.asText()));
                                        outArr.add(item);
                                    } else if (!it.isObject()) {
                                        ObjectNode item = MAPPER.createObjectNode();
                                        item.put("role", "user");
                                        item.set("content", toInputTextParts(String.valueOf(it)));
                                        outArr.add(item);
                                    } else {
                                        ObjectNode iox = (ObjectNode)it;
                                        String role = textOrNull(iox, "role");
                                        if (role != null && !role.isBlank()) {
                                            String rl = role.toLowerCase(Locale.ROOT);
                                            if (!"user".equals(rl) && !"assistant".equals(rl) && !"system".equals(rl)) {
                                                role = "user";
                                            }
                                        } else {
                                            role = "user";
                                        }

                                        iox.put("role", role);
                                        JsonNode content = iox.get("content");
                                        if (content != null && content.isTextual()) {
                                            iox.set("content", toInputTextParts(content.asText()));
                                        } else if (content != null && content.isArray()) {
                                            ArrayNode normalized = MAPPER.createArrayNode();

                                            for (JsonNode part : content) {
                                                if (part != null && !part.isNull()) {
                                                    if (part.isTextual()) {
                                                        normalized.add(toInputTextPartNode(part.asText()));
                                                    } else {
                                                        if (part.isObject()) {
                                                            ObjectNode po = (ObjectNode)part;
                                                            String t = textOrNull(po, "type");
                                                            if (t != null && ("text".equalsIgnoreCase(t) || "input_text".equalsIgnoreCase(t))) {
                                                                String tx = textOrNull(po, "text");
                                                                if (tx != null) {
                                                                    normalized.add(toInputTextPartNode(tx));
                                                                    continue;
                                                                }
                                                            }

                                                            if (t != null && "input_image".equalsIgnoreCase(t)) {
                                                                normalized.add(po);
                                                                continue;
                                                            }
                                                        }

                                                        normalized.add(toInputTextPartNode(part.isTextual() ? part.asText() : part.toString()));
                                                    }
                                                }
                                            }

                                            if (normalized.size() > 0) {
                                                iox.set("content", normalized);
                                            } else {
                                                iox.set("content", toInputTextParts(""));
                                            }
                                        } else if (content != null && content.isObject()) {
                                            iox.set("content", toInputTextParts(content.toString()));
                                        } else if (content == null || content.isNull()) {
                                            iox.set("content", toInputTextParts(""));
                                        }

                                        outArr.add(iox);
                                    }
                                }
                            }

                            in.set("input", outArr);
                            return MAPPER.writeValueAsBytes(in);
                        }
                    }
                } else {
                    return input;
                }
            } else {
                return input;
            }
        } catch (Exception var16) {
            return input;
        }
    }

    private static byte[] truncateResponsesInputForMultiAgent(byte[] body, int maxItems) {
        if (body != null && body.length != 0 && maxItems > 0) {
            try {
                JsonNode root = MAPPER.readTree(body);
                if (root != null && root.isObject()) {
                    ObjectNode o = (ObjectNode)root;
                    String model = textOrNull(o, "model");
                    if (!isLikelyMultiAgentModelName(model)) {
                        return body;
                    } else {
                        JsonNode input = o.get("input");
                        if (input != null && input.isArray()) {
                            ArrayNode arr = (ArrayNode)input;
                            int n = arr.size();
                            if (n <= maxItems) {
                                return body;
                            } else {
                                ArrayNode out = MAPPER.createArrayNode();

                                for (int i = n - maxItems; i < n; i++) {
                                    JsonNode it = arr.get(i);
                                    if (it != null) {
                                        out.add(it);
                                    }
                                }

                                o.set("input", out);
                                return MAPPER.writeValueAsBytes(o);
                            }
                        } else {
                            return body;
                        }
                    }
                } else {
                    return body;
                }
            } catch (Exception var11) {
                return body;
            }
        } else {
            return body;
        }
    }

    private static String describeResponsesInputShape(byte[] body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            if (root != null && root.isObject()) {
                JsonNode input = root.get("input");
                if (input == null) {
                    return "input=<missing>";
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("input=").append(input.getNodeType());
                    if (input.isTextual()) {
                        sb.append("(len=").append(input.asText().length()).append(")");
                        return sb.toString();
                    } else if (input.isObject()) {
                        sb.append("(keys=");
                        Iterator<String> it = input.fieldNames();

                        for (int c = 0; it.hasNext() && c < 8; c++) {
                            if (c > 0) {
                                sb.append(",");
                            }

                            sb.append(it.next());
                        }

                        if (it.hasNext()) {
                            sb.append(",…");
                        }

                        sb.append(")");
                        return sb.toString();
                    } else if (!input.isArray()) {
                        return sb.toString();
                    } else {
                        sb.append("(n=").append(input.size()).append(")");
                        if (input.size() > 0) {
                            JsonNode first = input.get(0);
                            sb.append(" first=").append(first == null ? "null" : first.getNodeType());
                            if (first != null && first.isObject()) {
                                JsonNode ctn = first.get("content");
                                sb.append(" content=").append(ctn == null ? "<missing>" : ctn.getNodeType().toString());
                                if (ctn != null && ctn.isArray() && ctn.size() > 0) {
                                    JsonNode p0 = ctn.get(0);
                                    String t = p0 != null && p0.isObject() ? textOrNull((ObjectNode)p0, "type") : null;
                                    if (t != null) {
                                        sb.append(" part0.type=").append(t);
                                    }
                                }
                            }
                        }

                        return sb.toString();
                    }
                }
            } else {
                return "root=" + (root == null ? "null" : root.getNodeType());
            }
        } catch (Exception var8) {
            return "parse_error(" + var8.getClass().getSimpleName() + ")";
        }
    }

    private static ArrayNode toInputTextParts(String text) {
        ArrayNode parts = MAPPER.createArrayNode();
        parts.add(toInputTextPartNode(text));
        return parts;
    }

    private static ObjectNode toInputTextPartNode(String text) {
        ObjectNode p = MAPPER.createObjectNode();
        p.put("type", "input_text");
        p.put("text", text == null ? "" : text);
        return p;
    }

    private static boolean isStreamRequest(byte[] body, String contentType) {
        if (body != null && contentType != null && contentType.toLowerCase().contains("json")) {
            try {
                JsonNode n = MAPPER.readTree(body);
                if (n != null && n.isObject()) {
                    JsonNode s = n.get("stream");
                    if (s == null) {
                        return false;
                    }

                    if (s.isBoolean()) {
                        return s.asBoolean();
                    }

                    if (s.isTextual() && "true".equalsIgnoreCase(s.asText())) {
                        return true;
                    }
                }

                return false;
            } catch (Exception var4) {
                return false;
            }
        } else {
            return false;
        }
    }

    private static int defaultMaxTokens() {
        try {
            return OracleAiGatewayConfigService.normalizeDefaultMaxTokens(defaultMaxTokensSupplier.getAsInt());
        } catch (Exception var1) {
            return 2048;
        }
    }

    private static int requestDefaultMaxTokens(HttpServletRequest request) {
        if (request != null) {
            Object v = request.getAttribute("ociworker.openai.defaultMaxTokens");
            if (v instanceof Number n) {
                return OracleAiGatewayConfigService.normalizeDefaultMaxTokens(n.intValue());
            }

            if (v != null) {
                try {
                    return OracleAiGatewayConfigService.normalizeDefaultMaxTokens(Integer.parseInt(String.valueOf(v)));
                } catch (Exception var3) {
                }
            }
        }

        return defaultMaxTokens();
    }

    private static byte[] transformChatCompletionsJson(byte[] input, int defaultMaxTokens) {
        try {
            JsonNode root = MAPPER.readTree(input);
            if (root != null && root.isObject()) {
                ObjectNode o = (ObjectNode)root;
                if (o.get("max_tokens") == null || o.get("max_tokens").isNull() || o.get("max_tokens").isMissingNode()) {
                    o.put("max_tokens", OracleAiGatewayConfigService.normalizeDefaultMaxTokens(defaultMaxTokens));
                }

                JsonNode force = o.get("force_non_stream");
                if (force != null && (force.isBoolean() && force.asBoolean() || force.isTextual() && "true".equalsIgnoreCase(force.asText()))) {
                    o.put("stream", false);
                }

                o.remove("force_non_stream");
                return MAPPER.writeValueAsBytes(o);
            } else {
                return input;
            }
        } catch (Exception var5) {
            return input;
        }
    }

    private static byte[] transformChatCompletionsToResponsesJson(byte[] input, int defaultMaxTokens) {
        try {
            JsonNode root = MAPPER.readTree(input);
            if (root != null && root.isObject()) {
                ObjectNode in = (ObjectNode)root;
                String model = textOrNull(in, "model");
                ObjectNode out = MAPPER.createObjectNode();
                if (model != null && !model.isBlank()) {
                    out.put("model", model);
                }

                JsonNode messages = in.get("messages");
                if (messages != null && messages.isArray()) {
                    ArrayNode inputArr = MAPPER.createArrayNode();

                    for (JsonNode m : messages) {
                        if (m != null && m.isObject()) {
                            ObjectNode mo = (ObjectNode)m;
                            String role = textOrNull(mo, "role");
                            if (role == null || role.isBlank()) {
                                role = "user";
                            }

                            ObjectNode item = MAPPER.createObjectNode();
                            item.put("role", role);
                            JsonNode content = mo.get("content");
                            if (content != null && !content.isNull()) {
                                if (content.isTextual()) {
                                    ArrayNode parts = MAPPER.createArrayNode();
                                    ObjectNode p = MAPPER.createObjectNode();
                                    p.put("type", "input_text");
                                    p.put("text", content.asText());
                                    parts.add(p);
                                    item.set("content", parts);
                                } else if (content.isArray()) {
                                    item.set("content", content);
                                } else if (content.isObject()) {
                                    ArrayNode parts = MAPPER.createArrayNode();
                                    ObjectNode p = MAPPER.createObjectNode();
                                    p.put("type", "input_text");
                                    p.put("text", content.toString());
                                    parts.add(p);
                                    item.set("content", parts);
                                } else {
                                    ArrayNode parts = MAPPER.createArrayNode();
                                    ObjectNode p = MAPPER.createObjectNode();
                                    p.put("type", "input_text");
                                    p.put("text", String.valueOf(content.asText()));
                                    parts.add(p);
                                    item.set("content", parts);
                                }

                                inputArr.add(item);
                            }
                        }
                    }

                    out.set("input", inputArr);
                } else {
                    JsonNode p = in.get("prompt");
                    if (p != null && p.isTextual()) {
                        out.put("input", p.asText());
                    }
                }

                JsonNode mt = in.get("max_tokens");
                if (mt == null || mt.isNull() || mt.isMissingNode()) {
                    out.put("max_output_tokens", OracleAiGatewayConfigService.normalizeDefaultMaxTokens(defaultMaxTokens));
                } else if (mt.isNumber()) {
                    out.put("max_output_tokens", mt.intValue());
                } else {
                    out.put("max_output_tokens", mt.asInt(OracleAiGatewayConfigService.normalizeDefaultMaxTokens(defaultMaxTokens)));
                }

                JsonNode temp = in.get("temperature");
                if (temp != null && !temp.isNull() && !temp.isMissingNode()) {
                    out.set("temperature", temp);
                }

                JsonNode topP = in.get("top_p");
                if (topP != null && !topP.isNull() && !topP.isMissingNode()) {
                    out.set("top_p", topP);
                }

                out.put("stream", false);
                return MAPPER.writeValueAsBytes(out);
            } else {
                return input;
            }
        } catch (Exception var16) {
            return input;
        }
    }

    private void longCopyStream(HttpClient client, HttpRequest httpRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            HttpResponse<InputStream> resp = client.send(httpRequest, BodyHandlers.ofInputStream());
            int code = resp.statusCode();

            for (Entry<String, List<String>> e : resp.headers().map().entrySet()) {
                String k = e.getKey();
                if (k != null
                    && !"transfer-encoding".equalsIgnoreCase(k)
                    && !"connection".equalsIgnoreCase(k)
                    && e.getValue() != null
                    && !e.getValue().isEmpty()
                    && (!"content-length".equalsIgnoreCase(k) || code < 200 || code >= 300)) {
                    if (!"content-type".equalsIgnoreCase(k) && !"cache-control".equalsIgnoreCase(k)) {
                        for (String v : e.getValue()) {
                            response.addHeader(k, v);
                        }
                    } else {
                        response.setHeader(k, e.getValue().get(0));
                    }
                }
            }

            response.setStatus(code);
            if (code >= 400) {
                byte[] bytes = new byte[0];

                try (InputStream in = resp.body()) {
                    if (in != null) {
                        bytes = in.readAllBytes();
                        response.getOutputStream().write(bytes);
                    }
                }

                try {
                    String b = bytes.length > 0 ? new String(bytes, StandardCharsets.UTF_8) : "";
                    String bl = b.toLowerCase(Locale.ROOT);
                    boolean looksLikeInputDeserializeError = bl.contains("failed to deserialize")
                        || bl.contains("untagged enum")
                        || bl.contains("modelinput")
                        || bl.contains("modellnput");
                    if (request != null) {
                        String rid = firstRequestHeader(request, "x-request-id", "x-cursor-request-id", "x-openai-request-id", "x-amzn-trace-id", "traceparent");
                        String origPath = String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1"));
                        String finalPath = String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1"));
                        String before = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.before"));
                        String after = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.after"));
                        log.warn(
                            "OCI proxy error(stream); rid={} code={} origPath={} finalPath={} before={} after={} body={}",
                            new Object[]{rid, code, origPath, finalPath, before, after, truncate(b, 1200)}
                        );
                        if (looksLikeInputDeserializeError && isResponsesPath(extractPathAfterV1(request))) {
                            log.warn(
                                "OCI /responses ModelInput error(stream); rid={} before={} after={} body={}",
                                new Object[]{rid, before, after, truncate(b, 1200)}
                            );
                        }
                    }
                } catch (Exception var20) {
                }
            } else {
                if (response.getContentType() == null) {
                    String ct = resp.headers().firstValue("content-type").orElse("text/event-stream; charset=utf-8");
                    response.setContentType(ct);
                }

                try (
                    InputStream inx = resp.body();
                    OutputStream out = response.getOutputStream();
                ) {
                    if (inx != null) {
                        response.setBufferSize(8192);
                        byte[] buf = new byte[16384];

                        int n;
                        while ((n = inx.read(buf)) != -1) {
                            out.write(buf, 0, n);
                            out.flush();
                        }
                    }
                }
            }
        } catch (InterruptedException var23) {
            Thread.currentThread().interrupt();
            throw new OciException("流式请求中断");
        } catch (IOException var24) {
            if (var24.getMessage() == null || !var24.getMessage().contains("Broken pipe")) {
                throw var24;
            }
        }
    }

    private void bufferAndCopy(HttpClient client, HttpRequest httpRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            HttpResponse<String> resp = client.send(httpRequest, BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = resp.statusCode();
            resp.headers().map().forEach((k, vals) -> {
                if (k != null && vals != null) {
                    if (!"transfer-encoding".equalsIgnoreCase(k) && !"connection".equalsIgnoreCase(k) && !"content-length".equalsIgnoreCase(k)) {
                        if (vals != null) {
                            for (String v : vals) {
                                if (v != null) {
                                    response.addHeader(k, v);
                                }
                            }
                        }
                    }
                }
            });
            if (response.getContentType() == null) {
                String ct = resp.headers().firstValue("content-type").orElse("application/json; charset=utf-8");
                response.setContentType(ct);
            }

            response.setStatus(code);
            String b = resp.body() != null ? resp.body() : "";
            if (code >= 400 && request != null && b != null) {
                String bl = b.toLowerCase(Locale.ROOT);
                boolean looksLikeInputDeserializeError = bl.contains("failed to deserialize")
                    || bl.contains("untagged enum")
                    || bl.contains("modelinput")
                    || bl.contains("modellnput");
                boolean maybeResponses = Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.useRawV1Base"))
                    || isResponsesPath(String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1")))
                    || isResponsesPath(String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1")))
                    || isResponsesPath(extractPathAfterV1(request));
                String before = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.before"));
                String after = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.after"));
                String rid = firstRequestHeader(request, "x-request-id", "x-cursor-request-id", "x-openai-request-id", "x-amzn-trace-id", "traceparent");
                String origPath = String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1"));
                String finalPath = String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1"));
                log.warn(
                    "OCI proxy error; rid={} code={} origPath={} finalPath={} maybeResponses={} before={} after={} body={}",
                    new Object[]{rid, code, origPath, finalPath, maybeResponses, before, after, truncate(b, 1200)}
                );
                if (looksLikeInputDeserializeError && maybeResponses) {
                    log.warn(
                        "OCI /responses ModelInput error; rid={} code={} origPath={} finalPath={} before={} after={} body={}",
                        new Object[]{rid, code, origPath, finalPath, before, after, truncate(b, 1200)}
                    );
                }
            }

            if (code >= 200
                && code < 300
                && request != null
                && Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.chatToResponses"))
                && b != null
                && !b.isBlank()) {
                String ct = resp.headers().firstValue("content-type").orElse("application/json; charset=utf-8");
                if (ct.toLowerCase().contains("json")) {
                    try {
                        String modelHint = (String)request.getAttribute("ociworker.rewrite.model");
                        if (Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.simulateSse"))) {
                            String text = extractResponsesAssistantText((ObjectNode)MAPPER.readTree(b));
                            if (text != null) {
                                response.setStatus(200);
                                response.setHeader("cache-control", "no-cache");
                                response.setContentType("text/event-stream; charset=utf-8");
                                writeChatCompletionSseFromText(response, text, modelHint);
                                return;
                            }
                        }

                        b = convertResponsesJsonToChatCompletionJson(b, modelHint);
                        response.setContentType("application/json; charset=utf-8");
                    } catch (Exception var16) {
                    }
                }
            }

            response.getOutputStream().write(b.getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException var17) {
            Thread.currentThread().interrupt();
            throw new OciException("请求中断");
        } catch (IOException var18) {
            if (var18.getMessage() == null || !var18.getMessage().contains("Broken pipe")) {
                throw var18;
            }
        }
    }

    private static String textOrNull(ObjectNode o, String field) {
        if (o == null) {
            return null;
        } else {
            JsonNode n = o.get(field);
            if (n == null || n.isNull() || n.isMissingNode()) {
                return null;
            } else if (n.isTextual()) {
                return n.asText();
            } else {
                return !n.isNumber() && !n.isBoolean() ? null : n.toString();
            }
        }
    }

    private static String convertResponsesJsonToChatCompletionJson(String responsesJson, String modelHint) throws Exception {
        JsonNode r = MAPPER.readTree(responsesJson);
        if (r != null && r.isObject()) {
            ObjectNode ro = (ObjectNode)r;
            String text = extractResponsesAssistantText(ro);
            if (text == null) {
                return responsesJson;
            } else {
                String model = modelHint;
                if (modelHint == null || modelHint.isBlank()) {
                    JsonNode m = ro.get("model");
                    if (m != null && m.isTextual()) {
                        model = m.asText();
                    }
                }

                if (model == null) {
                    model = "";
                }

                long created = System.currentTimeMillis() / 1000L;
                String id = "chatcmpl-ociworker";
                JsonNode idn = ro.get("id");
                if (idn != null && idn.isTextual() && !idn.asText().isBlank()) {
                    id = idn.asText();
                }

                ObjectNode out = MAPPER.createObjectNode();
                out.put("id", id);
                out.put("object", "chat.completion");
                out.put("created", created);
                out.put("model", model);
                ArrayNode choices = MAPPER.createArrayNode();
                ObjectNode ch = MAPPER.createObjectNode();
                ch.put("index", 0);
                ObjectNode msg = MAPPER.createObjectNode();
                msg.put("role", "assistant");
                msg.put("content", text);
                ch.set("message", msg);
                ch.put("finish_reason", "stop");
                choices.add(ch);
                out.set("choices", choices);
                return MAPPER.writeValueAsString(out);
            }
        } else {
            return responsesJson;
        }
    }

    private static String extractResponsesAssistantText(ObjectNode r) {
        if (r == null) {
            return null;
        } else {
            JsonNode ot = r.get("output_text");
            if (ot != null && ot.isTextual() && !ot.asText().isBlank()) {
                return ot.asText();
            } else {
                JsonNode out = r.get("output");
                if (out != null && out.isArray()) {
                    StringBuilder sb = new StringBuilder();

                    for (JsonNode item : out) {
                        if (item != null && item.isObject()) {
                            ObjectNode io = (ObjectNode)item;
                            String type = textOrNull(io, "type");
                            if (type != null && !"message".equalsIgnoreCase(type) && !"output_message".equalsIgnoreCase(type)) {
                            }

                            JsonNode role = io.get("role");
                            if (role == null || !role.isTextual() || "assistant".equalsIgnoreCase(role.asText())) {
                                JsonNode content = io.get("content");
                                if (content != null) {
                                    if (content.isTextual()) {
                                        appendText(sb, content.asText());
                                    } else if (content.isArray()) {
                                        for (JsonNode part : content) {
                                            if (part != null && part.isObject()) {
                                                ObjectNode po = (ObjectNode)part;
                                                String pt = textOrNull(po, "type");
                                                if (pt != null && ("output_text".equalsIgnoreCase(pt) || "text".equalsIgnoreCase(pt))) {
                                                    JsonNode tx = po.get("text");
                                                    if (tx != null && tx.isTextual()) {
                                                        appendText(sb, tx.asText());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return sb.length() == 0 ? null : sb.toString();
                } else {
                    return null;
                }
            }
        }
    }

    private static void appendText(StringBuilder sb, String s) {
        if (s != null && !s.isBlank()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }

            sb.append(s);
        }
    }

    private static void writeChatCompletionSseFromText(HttpServletResponse response, String text, String modelHint) throws IOException {
        OutputStream out = response.getOutputStream();
        String id = "chatcmpl-ociworker";
        long created = System.currentTimeMillis() / 1000L;
        String model = modelHint == null ? "" : modelHint;
        String first = "{\"id\":\""
            + id
            + "\",\"object\":\"chat.completion.chunk\",\"created\":"
            + created
            + ",\"model\":\""
            + escapeJson(model)
            + "\",\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\"},\"finish_reason\":null}]}";
        out.write(("data: " + first + "\n\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
        int step = 200;

        for (int i = 0; i < text.length(); i += step) {
            String part = text.substring(i, Math.min(text.length(), i + step));
            String j = "{\"id\":\""
                + id
                + "\",\"object\":\"chat.completion.chunk\",\"created\":"
                + created
                + ",\"model\":\""
                + escapeJson(model)
                + "\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\""
                + escapeJson(part)
                + "\"},\"finish_reason\":null}]}";
            out.write(("data: " + j + "\n\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        }

        String last = "{\"id\":\""
            + id
            + "\",\"object\":\"chat.completion.chunk\",\"created\":"
            + created
            + ",\"model\":\""
            + escapeJson(model)
            + "\",\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"stop\"}]}";
        out.write(("data: " + last + "\n\n").getBytes(StandardCharsets.UTF_8));
        out.write("data: [DONE]\n\n".getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }

    private static Map<String, String> extractOciGenerativeForwardHeaders(HttpServletRequest request, OciUser tenant) {
        Map<String, String> out = new LinkedHashMap<>();
        if (request != null) {
            String project = firstRequestHeader(request, "OpenAI-Project", "openai-project", "X-OpenAI-Project");
            if (project != null && !project.isBlank()) {
                out.put("OpenAI-Project", project.trim());
            }

            String convStore = firstRequestHeader(request, "opc-conversation-store-id", "OPC-Conversation-Store-Id");
            if (convStore != null && !convStore.isBlank()) {
                out.put("opc-conversation-store-id", convStore.trim());
            }
        }

        if (tenant != null) {
            if (!out.containsKey("OpenAI-Project") && tenant.getGenerativeOpenaiProject() != null && !tenant.getGenerativeOpenaiProject().isBlank()) {
                out.put("OpenAI-Project", tenant.getGenerativeOpenaiProject().trim());
            }

            if (!out.containsKey("opc-conversation-store-id")
                && tenant.getGenerativeConversationStoreId() != null
                && !tenant.getGenerativeConversationStoreId().isBlank()) {
                out.put("opc-conversation-store-id", tenant.getGenerativeConversationStoreId().trim());
            }
        }

        return out.isEmpty() ? null : out;
    }

    private static String firstRequestHeader(HttpServletRequest request, String... headerNames) {
        if (request != null && headerNames != null) {
            for (String name : headerNames) {
                if (name != null) {
                    String v = request.getHeader(name);
                    if (v != null && !v.isBlank()) {
                        return v;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    private HttpRequest buildSignedRequest(
        RequestSigner signer,
        String method,
        URI uri,
        byte[] body,
        String contentType,
        String clientAccept,
        String opcCompartmentId,
        Map<String, String> extraSignedHeaders
    ) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        headers.put("host", list(h(uri.getHost())));
        headers.put("accept", list(h(clientAccept)));
        if (opcCompartmentId != null && !opcCompartmentId.isBlank()) {
            headers.put("opc-compartment-id", list(opcCompartmentId));
        }

        if (extraSignedHeaders != null) {
            for (Entry<String, String> e : extraSignedHeaders.entrySet()) {
                if (e.getKey() != null) {
                    String val = e.getValue();
                    if (val != null && !val.isBlank()) {
                        headers.put(e.getKey(), list(h(val.trim())));
                    }
                }
            }
        }

        if (contentType != null && !contentType.isBlank()) {
            headers.put("content-type", list(contentType));
        } else if (body != null && body.length > 0) {
            headers.put("content-type", list("application/json"));
        }

        Object toSign = null;
        if (body != null && body.length > 0) {
            toSign = new OciDuplicatableByteArrayInputStream(body);
        }

        Object signedObject = signer.signRequest(uri, method, headers, toSign);
        Map<String, List<String>> signed = castSignedHeaders(signedObject);
        Builder b = HttpRequest.newBuilder().uri(uri).version(Version.HTTP_1_1).timeout(Duration.ofHours(1L));
        this.applyToBuilder(b, headers);
        this.applyToBuilder(b, signed);
        if (body != null && body.length != 0) {
            return b.method(method, BodyPublishers.ofByteArray(body)).build();
        } else if ("GET".equalsIgnoreCase(method)) {
            return b.GET().build();
        } else {
            return "HEAD".equalsIgnoreCase(method) ? b.method("HEAD", BodyPublishers.noBody()).build() : b.method(method, BodyPublishers.noBody()).build();
        }
    }

    private static String h(String s) {
        return s == null ? "" : s;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (signed == null) {
            return new LinkedHashMap<>();
        } else if (!(signed instanceof Map<?, ?> raw)) {
            return signed instanceof String ? new LinkedHashMap<>() : new LinkedHashMap<>();
        } else {
            Map<String, List<String>> out = new LinkedHashMap<>();

            for (Entry<?, ?> e : raw.entrySet()) {
                String key = String.valueOf(e.getKey());
                if (e.getValue() != null) {
                    List<String> ls = (List<String>)e.getValue();
                    if (ls instanceof List) {
                        List<?> list = ls;
                        ls = new ArrayList<>();

                        for (Object o : list) {
                            if (o != null) {
                                ls.add(String.valueOf(o));
                            }
                        }

                        if (!ls.isEmpty()) {
                            out.put(key, ls);
                        }
                    } else if (e.getValue() instanceof String s) {
                        out.put(key, list(s));
                    } else {
                        out.put(key, list(String.valueOf(e.getValue())));
                    }
                }
            }

            return out;
        }
    }

    private static List<String> list(String v) {
        List<String> l = new ArrayList<>(1);
        l.add(v);
        return l;
    }

    private void applyToBuilder(Builder b, Map<String, List<String>> headers) {
        for (Entry<String, List<String>> e : headers.entrySet()) {
            if (e.getValue() != null) {
                String name = e.getKey();
                if (name != null && !isDisallowedOnHttpRequestBuilder(name)) {
                    for (String v : e.getValue()) {
                        if (v != null) {
                            b.header(name, v);
                        }
                    }
                }
            }
        }
    }

    private static boolean isDisallowedOnHttpRequestBuilder(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        return n.equals("host") || n.equals("connection") || n.equals("content-length") || n.equals("expect") || n.equals("upgrade");
    }

    private HttpClient pickHttpClient() {
        return this.ociProxyConfigService == null
            ? HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(30L)).build()
            : this.ociProxyConfigService.newOutboundHttpClient();
    }

    private static String truncate(String s, int n) {
        if (s == null) {
            return "";
        } else {
            return s.length() > n ? s.substring(0, n) + "…" : s;
        }
    }
}
