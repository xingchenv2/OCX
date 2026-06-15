/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.ArrayNode
 *  com.fasterxml.jackson.databind.node.ObjectNode
 *  com.ociworker.exception.OciException
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.OciGenerativeOpenAiService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.service.OracleAiGatewayConfigService
 *  com.ociworker.service.OracleAiPortBindingService
 *  com.ociworker.util.OciBasicForSigning
 *  com.ociworker.util.OciDuplicatableByteArrayInputStream
 *  com.ociworker.util.OciRegionUtil
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 *  com.oracle.bmc.http.signing.DefaultRequestSigner
 *  com.oracle.bmc.http.signing.RequestSigner
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ociworker.exception.OciException;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.OciProxyConfigService;
import com.ociworker.service.OracleAiGatewayConfigService;
import com.ociworker.service.OracleAiPortBindingService;
import com.ociworker.util.OciBasicForSigning;
import com.ociworker.util.OciDuplicatableByteArrayInputStream;
import com.ociworker.util.OciRegionUtil;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntSupplier;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
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
        defaultMaxTokensSupplier = () -> ((OracleAiGatewayConfigService)this.gatewayConfigService).getDefaultMaxTokens();
    }

    public void proxy(OciUser tenant, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean useStreamCopy;
        boolean useRawV1Base;
        String model;
        JsonNode root;
        boolean looksLikeJson;
        byte[] origBody;
        byte[] body;
        String contentType;
        String accept;
        String method;
        RequestSigner signer;
        String query;
        String baseRawV1;
        String baseOpenAi;
        String origPathAfterV1;
        Object pathAfterV1;
        block47: {
            pathAfterV1 = OciGenerativeOpenAiService.extractPathAfterV1((HttpServletRequest)request);
            if (pathAfterV1 == null || ((String)pathAfterV1).isEmpty() || ((String)pathAfterV1).equals("/")) {
                pathAfterV1 = "/";
            }
            if (!((String)pathAfterV1).startsWith("/")) {
                pathAfterV1 = "/" + (String)pathAfterV1;
            }
            origPathAfterV1 = pathAfterV1;
            String regionId = OciGenerativeOpenAiService.effectivePublicRegionId((OciUser)tenant, (Object)request.getAttribute("ociworker.openai.ociRegion"));
            baseOpenAi = "https://inference.generativeai." + regionId + ".oci.oraclecloud.com/openai/v1";
            baseRawV1 = "https://inference.generativeai." + regionId + ".oci.oraclecloud.com/v1";
            query = request.getQueryString();
            signer = OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant, (String)regionId);
            method = request.getMethod().toUpperCase();
            accept = request.getHeader("Accept");
            if (accept == null || accept.isBlank()) {
                accept = "*/*";
            }
            if ((contentType = request.getContentType()) != null && contentType.contains(";")) {
                contentType = contentType.split(";")[0].trim();
            }
            body = null;
            if (!("GET".equals(method) || "HEAD".equals(method) || "DELETE".equals(method))) {
                body = request.getInputStream().readAllBytes();
            }
            origBody = body;
            int requestDefaultMaxTokens = OciGenerativeOpenAiService.requestDefaultMaxTokens((HttpServletRequest)request);
            List requestAllowedModels = OciGenerativeOpenAiService.requestAllowedModels((HttpServletRequest)request);
            if ("GET".equalsIgnoreCase(method) && OciGenerativeOpenAiService.isModelsPath((String)origPathAfterV1) && !requestAllowedModels.isEmpty()) {
                OciGenerativeOpenAiService.writeJson((HttpServletResponse)response, (JsonNode)OciGenerativeOpenAiService.allowedModelsToOpenAiList((List)requestAllowedModels));
                return;
            }
            String requestedModel = OciGenerativeOpenAiService.extractModelFromBody((byte[])origBody, (String)contentType);
            if ((OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1) || OciGenerativeOpenAiService.isResponsesPath((String)origPathAfterV1)) && !OciGenerativeOpenAiService.isAllowedModel((String)requestedModel, (List)requestAllowedModels)) {
                OciGenerativeOpenAiService.writeOpenAiError((HttpServletResponse)response, (int)400, (String)"invalid_request_error", (String)("Model is not allowed for this port binding: " + requestedModel), (String)"model_not_allowed");
                return;
            }
            request.setAttribute("ociworker.debug.origPathAfterV1", (Object)origPathAfterV1);
            boolean bl = looksLikeJson = contentType == null || contentType.isBlank() || contentType.toLowerCase().contains("json");
            if ("POST".equalsIgnoreCase(method) && OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1) && origBody != null && origBody.length > 0) {
                boolean bodyMentionsMultiAgent = false;
                try {
                    String raw = new String(origBody, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                    bodyMentionsMultiAgent = raw.contains("multi-agent") || raw.contains("multiagent") || raw.contains("multi agent");
                }
                catch (Exception raw) {
                    // empty catch block
                }
                if (bodyMentionsMultiAgent) {
                    if (OciGenerativeOpenAiService.isStreamRequest((byte[])origBody, (String)contentType)) {
                        request.setAttribute("ociworker.rewrite.simulateSse", (Object)Boolean.TRUE);
                    }
                    request.setAttribute("ociworker.rewrite.chatToResponses", (Object)Boolean.TRUE);
                    request.setAttribute("ociworker.rewrite.useRawV1Base", (Object)Boolean.TRUE);
                    request.setAttribute("ociworker.rewrite.model", (Object)"multi-agent");
                    pathAfterV1 = "/responses";
                    body = OciGenerativeOpenAiService.transformChatCompletionsToResponsesJson((byte[])origBody, (int)requestDefaultMaxTokens);
                    try {
                        if (request != null && body != null) {
                            request.setAttribute("ociworker.debug.responsesInputShape.before", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                        }
                        body = OciGenerativeOpenAiService.normalizeResponsesInputForOci((byte[])body);
                        body = OciGenerativeOpenAiService.truncateResponsesInputForMultiAgent((byte[])body, (int)20);
                        if (request != null && body != null) {
                            request.setAttribute("ociworker.debug.responsesInputShape.after", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                        }
                    }
                    catch (Exception raw) {}
                } else if (looksLikeJson) {
                    try {
                        root = MAPPER.readTree(origBody);
                        if (root != null && root.isObject()) {
                            model = OciGenerativeOpenAiService.textOrNull((ObjectNode)((ObjectNode)root), (String)"model");
                            if (OciGenerativeOpenAiService.isLikelyMultiAgentModelName((String)model)) {
                                if (OciGenerativeOpenAiService.isStreamRequest((byte[])origBody, (String)contentType)) {
                                    log.debug("Multi Agent \u6a21\u578b\u5728 chat/completions \u4e0a\u6536\u5230 stream=true\uff0c\u5c06\u6539\u5199\u4e3a /v1/responses \u4e14\u6309\u975e\u6d41\u5f0f\u5904\u7406");
                                    request.setAttribute("ociworker.rewrite.simulateSse", (Object)Boolean.TRUE);
                                }
                                request.setAttribute("ociworker.rewrite.chatToResponses", (Object)Boolean.TRUE);
                                if (model != null) {
                                    request.setAttribute("ociworker.rewrite.model", (Object)model);
                                }
                                request.setAttribute("ociworker.rewrite.useRawV1Base", (Object)Boolean.TRUE);
                                pathAfterV1 = "/responses";
                                body = OciGenerativeOpenAiService.transformChatCompletionsToResponsesJson((byte[])origBody, (int)requestDefaultMaxTokens);
                                try {
                                    if (request != null && body != null) {
                                        request.setAttribute("ociworker.debug.responsesInputShape.before", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                                    }
                                    body = OciGenerativeOpenAiService.normalizeResponsesInputForOci((byte[])body);
                                    body = OciGenerativeOpenAiService.truncateResponsesInputForMultiAgent((byte[])body, (int)20);
                                    if (request != null && body != null) {
                                        request.setAttribute("ociworker.debug.responsesInputShape.after", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                                    }
                                    break block47;
                                }
                                catch (Exception exception) {}
                                break block47;
                            }
                            if (OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1)) {
                                body = OciGenerativeOpenAiService.transformChatCompletionsJson((byte[])origBody, (int)requestDefaultMaxTokens);
                            }
                            break block47;
                        }
                        if (OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1)) {
                            body = OciGenerativeOpenAiService.transformChatCompletionsJson((byte[])origBody, (int)requestDefaultMaxTokens);
                        }
                    }
                    catch (Exception e) {
                        body = OciGenerativeOpenAiService.transformChatCompletionsJson((byte[])origBody, (int)requestDefaultMaxTokens);
                    }
                } else if (body != null && body.length > 0 && looksLikeJson) {
                    body = OciGenerativeOpenAiService.transformChatCompletionsJson((byte[])body, (int)requestDefaultMaxTokens);
                }
            } else if (OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1) && body != null && body.length > 0 && looksLikeJson) {
                body = OciGenerativeOpenAiService.transformChatCompletionsJson((byte[])body, (int)requestDefaultMaxTokens);
            }
        }
        if ("POST".equalsIgnoreCase(method) && OciGenerativeOpenAiService.isResponsesPath((String)origPathAfterV1) && body != null && body.length > 0 && looksLikeJson) {
            try {
                if (request != null) {
                    request.setAttribute("ociworker.debug.responsesInputShape.before", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                }
                body = OciGenerativeOpenAiService.normalizeResponsesInputForOci((byte[])body);
                body = OciGenerativeOpenAiService.truncateResponsesInputForMultiAgent((byte[])body, (int)20);
                if (request != null) {
                    request.setAttribute("ociworker.debug.responsesInputShape.after", (Object)OciGenerativeOpenAiService.describeResponsesInputShape((byte[])body));
                    if (OciGenerativeOpenAiService.isStreamRequest((byte[])origBody, (String)contentType)) {
                        request.setAttribute("ociworker.rewrite.forceBuffer", (Object)Boolean.TRUE);
                    }
                }
            }
            catch (Exception bodyMentionsMultiAgent) {
                // empty catch block
            }
        }
        if (!(useRawV1Base = Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.useRawV1Base"))) && OciGenerativeOpenAiService.isResponsesPath((String)origPathAfterV1) && origBody != null && origBody.length > 0 && looksLikeJson) {
            try {
                root = MAPPER.readTree(origBody);
                if (root != null && root.isObject() && OciGenerativeOpenAiService.isLikelyMultiAgentModelName((String)(model = OciGenerativeOpenAiService.textOrNull((ObjectNode)((ObjectNode)root), (String)"model")))) {
                    useRawV1Base = true;
                }
            }
            catch (Exception root2) {
                // empty catch block
            }
        }
        request.setAttribute("ociworker.debug.finalPathAfterV1", pathAfterV1);
        StringBuilder u = new StringBuilder(useRawV1Base ? baseRawV1 : baseOpenAi);
        u.append((String)pathAfterV1);
        if (query != null && !query.isEmpty()) {
            u.append("?").append(query);
        }
        URI target = URI.create(u.toString());
        HttpRequest httpRequest = this.buildSignedRequest(signer, method, target, body, contentType, accept, tenant != null ? tenant.getOciTenantId() : null, OciGenerativeOpenAiService.extractOciGenerativeForwardHeaders((HttpServletRequest)request, (OciUser)tenant));
        HttpClient client = this.pickHttpClient();
        boolean bl = useStreamCopy = (OciGenerativeOpenAiService.isChatCompletionsPath((String)origPathAfterV1) || OciGenerativeOpenAiService.isResponsesPath((String)origPathAfterV1)) && OciGenerativeOpenAiService.isStreamRequest((byte[])origBody, (String)contentType) && !Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.chatToResponses")) && !Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.forceBuffer"));
        if (useStreamCopy) {
            this.longCopyStream(client, httpRequest, response, request);
        } else {
            this.bufferAndCopy(client, httpRequest, response, request);
        }
    }

    public JsonNode getModelsAsJson(OciUser tenant) throws Exception {
        return this.getModelsAsJson(tenant, null, null);
    }

    public JsonNode getModelsAsJson(OciUser tenant, String after, String modelId) throws Exception {
        return this.getModelsAsJson(tenant, null, after, modelId);
    }

    public JsonNode getModelsAsJson(OciUser tenant, String ociRegion, String after, String modelId) throws Exception {
        String regionId = OciGenerativeOpenAiService.effectivePublicRegionId((OciUser)tenant, (Object)ociRegion);
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String tenantId = tenant.getOciTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new OciException("\u79df\u6237\u65e0 ociTenantId\uff0c\u65e0\u6cd5 list models");
        }
        if (modelId != null && !modelId.isBlank()) {
            String path = "/20231130/models/" + OciGenerativeOpenAiService.encodePathSegmentOciModel((String)modelId);
            return this.managementGetToOpenAiList(tenant, regionId, "https://" + managementHost + path, true);
        }
        ArrayList<JsonNode> all = new ArrayList<JsonNode>();
        String page = after != null && !after.isBlank() ? after : null;
        for (int p = 0; p < 50; ++p) {
            String next;
            HttpResponse<String> resp;
            String q = "compartmentId=" + URLEncoder.encode(tenantId, StandardCharsets.UTF_8) + "&limit=200";
            if (page != null) {
                q = q + "&page=" + URLEncoder.encode(page, StandardCharsets.UTF_8);
            }
            URI listUri = URI.create("https://" + managementHost + "/20231130/models?" + q);
            HttpRequest req = this.buildSignedRequest(OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant, (String)regionId), "GET", listUri, null, "application/json", "application/json", tenantId, null);
            try {
                resp = this.pickHttpClient().send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }
            catch (Exception e) {
                throw new OciException("\u62c9\u53d6 models \u5f02\u5e38(" + e.getClass().getSimpleName() + "): " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
            }
            if (resp.statusCode() / 100 != 2) {
                throw new OciException("\u62c9\u53d6 models \u5931\u8d25: HTTP " + resp.statusCode() + " headers=" + OciGenerativeOpenAiService.truncate((String)String.valueOf(resp.headers().map()), (int)500) + " body=" + OciGenerativeOpenAiService.truncate((String)resp.body(), (int)500));
            }
            JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
            JsonNode items = root.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode it : items) {
                    all.add(it);
                }
            }
            if ((next = (String)resp.headers().firstValue("opc-next-page").orElse(null)) == null || next.isBlank()) break;
            page = next;
        }
        return this.ociModelsToOpenAiList(MAPPER.createObjectNode().set("items", (JsonNode)OciGenerativeOpenAiService.toArrayNode(all)));
    }

    public JsonNode listGenerativeAiProjectSummaries(OciUser tenant) throws Exception {
        String regionId = OciRegionUtil.publicRegionId((String)tenant.getOciRegion());
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String compartmentId = tenant.getOciTenantId();
        if (compartmentId == null || compartmentId.isBlank()) {
            throw new OciException("\u79df\u6237\u65e0 ociTenantId\uff0c\u65e0\u6cd5\u5217\u4e3e Generative AI \u9879\u76ee");
        }
        ArrayList<JsonNode> all = new ArrayList<JsonNode>();
        String page = null;
        for (int p = 0; p < 50; ++p) {
            String next;
            HttpResponse<String> resp;
            Object q = "compartmentId=" + URLEncoder.encode(compartmentId, StandardCharsets.UTF_8) + "&limit=200";
            if (page != null) {
                q = (String)q + "&page=" + URLEncoder.encode(page, StandardCharsets.UTF_8);
            }
            URI listUri = URI.create("https://" + managementHost + "/20231130/generativeAiProjects?" + (String)q);
            HttpRequest req = this.buildSignedRequest(OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant), "GET", listUri, null, "application/json", "application/json", compartmentId, null);
            try {
                resp = this.pickHttpClient().send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            }
            catch (Exception e) {
                throw new OciException("\u5217\u4e3e generativeAiProjects \u5f02\u5e38(" + e.getClass().getSimpleName() + "): " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
            }
            if (resp.statusCode() / 100 != 2) {
                throw new OciException("\u5217\u4e3e generativeAiProjects \u5931\u8d25: HTTP " + resp.statusCode() + " body=" + OciGenerativeOpenAiService.truncate((String)resp.body(), (int)800));
            }
            JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
            JsonNode items = root.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode it : items) {
                    all.add(it);
                }
            }
            if ((next = (String)resp.headers().firstValue("opc-next-page").orElse(null)) == null || next.isBlank()) break;
            page = next;
        }
        ArrayNode arr = MAPPER.createArrayNode();
        for (JsonNode it : all) {
            String id;
            if (it == null || !it.isObject() || (id = OciGenerativeOpenAiService.firstText((JsonNode)it, (String[])new String[]{"id"})) == null || id.isBlank()) continue;
            ObjectNode row = MAPPER.createObjectNode();
            row.put("id", id);
            String dn = OciGenerativeOpenAiService.firstText((JsonNode)it, (String[])new String[]{"displayName"});
            if (dn != null && !dn.isBlank()) {
                row.put("displayName", dn);
            }
            arr.add((JsonNode)row);
        }
        ObjectNode out = MAPPER.createObjectNode();
        out.set("items", (JsonNode)arr);
        return out;
    }

    public JsonNode createGenerativeAiProject(OciUser tenant, String displayName) throws Exception {
        HttpResponse<String> resp;
        String regionId = OciRegionUtil.publicRegionId((String)tenant.getOciRegion());
        String managementHost = "generativeai." + regionId + ".oci.oraclecloud.com";
        String compartmentId = tenant.getOciTenantId();
        if (compartmentId == null || compartmentId.isBlank()) {
            throw new OciException("\u79df\u6237\u65e0 ociTenantId\uff0c\u65e0\u6cd5\u521b\u5efa Generative AI \u9879\u76ee");
        }
        String name = displayName == null || displayName.isBlank() ? "ociworker-default" : displayName.trim();
        ObjectNode body = MAPPER.createObjectNode();
        body.put("compartmentId", compartmentId);
        body.put("displayName", name);
        ObjectNode conversationConfig = MAPPER.createObjectNode();
        conversationConfig.put("responsesRetentionInHours", 720);
        conversationConfig.put("conversationsRetentionInHours", 720);
        body.set("conversationConfig", (JsonNode)conversationConfig);
        byte[] bytes = MAPPER.writeValueAsBytes((Object)body);
        URI uri = URI.create("https://" + managementHost + "/20231130/generativeAiProjects");
        HttpRequest req = this.buildSignedRequest(OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant), "POST", uri, bytes, "application/json", "application/json", compartmentId, null);
        try {
            resp = this.pickHttpClient().send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa generativeAiProject \u5f02\u5e38(" + e.getClass().getSimpleName() + "): " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
        }
        if (resp.statusCode() / 100 != 2) {
            String rid = resp.headers().firstValue("opc-request-id").orElse("");
            throw new OciException("\u521b\u5efa generativeAiProject \u5931\u8d25: HTTP " + resp.statusCode() + (String)(rid.isBlank() ? "" : " opc-request-id=" + rid) + " body=" + OciGenerativeOpenAiService.truncate((String)resp.body(), (int)1200));
        }
        JsonNode root = MAPPER.readTree(resp.body() != null ? resp.body() : "{}");
        if (root != null && root.isObject()) {
            String dn;
            ObjectNode out = MAPPER.createObjectNode();
            String id = OciGenerativeOpenAiService.firstText((JsonNode)root, (String[])new String[]{"id"});
            if (id != null) {
                out.put("id", id);
            }
            if ((dn = OciGenerativeOpenAiService.firstText((JsonNode)root, (String[])new String[]{"displayName"})) != null) {
                out.put("displayName", dn);
            }
            return out;
        }
        return root;
    }

    private static ArrayNode toArrayNode(List<JsonNode> nodes) {
        ArrayNode a = MAPPER.createArrayNode();
        for (JsonNode n : nodes) {
            a.add(n);
        }
        return a;
    }

    private JsonNode managementGetToOpenAiList(OciUser tenant, String regionId, String url, boolean oneItemAsList) throws Exception {
        HttpResponse<String> resp;
        RequestSigner signer = OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant, (String)regionId);
        URI uri = URI.create(url);
        HttpRequest req = this.buildSignedRequest(signer, "GET", uri, null, "application/json", "application/json", tenant != null ? tenant.getOciTenantId() : null, null);
        try {
            resp = this.pickHttpClient().send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            throw new OciException("\u62c9\u53d6 models \u5f02\u5e38(" + e.getClass().getSimpleName() + "): " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
        }
        if (resp.statusCode() / 100 != 2) {
            throw new OciException("\u62c9\u53d6 models \u5931\u8d25: HTTP " + resp.statusCode() + " headers=" + OciGenerativeOpenAiService.truncate((String)String.valueOf(resp.headers().map()), (int)500) + " body=" + OciGenerativeOpenAiService.truncate((String)resp.body(), (int)500));
        }
        return this.ociModelsToOpenAiList(MAPPER.readTree(resp.body() != null ? resp.body() : "{}"), oneItemAsList);
    }

    private JsonNode ociModelsToOpenAiList(JsonNode ociBody) {
        return this.ociModelsToOpenAiList(ociBody, false);
    }

    private JsonNode ociModelsToOpenAiList(JsonNode ociBody, boolean single) {
        ArrayNode outItems = MAPPER.createArrayNode();
        if (single && ociBody != null && !ociBody.isObject()) {
            return OciGenerativeOpenAiService.buildOpenAiModelList((ArrayNode)outItems);
        }
        if (single && ociBody != null && ociBody.isObject() && !ociBody.has("items") && ociBody.has("id")) {
            outItems.add((JsonNode)OciGenerativeOpenAiService.ociItemToOpenAi((JsonNode)ociBody));
        } else if (ociBody != null && ociBody.isObject() && ociBody.has("items")) {
            for (JsonNode n : ociBody.withArray("items")) {
                outItems.add((JsonNode)OciGenerativeOpenAiService.ociItemToOpenAi((JsonNode)n));
            }
        }
        return OciGenerativeOpenAiService.buildOpenAiModelList((ArrayNode)outItems);
    }

    private static ObjectNode buildOpenAiModelList(ArrayNode data) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("object", "list");
        root.set("data", (JsonNode)data);
        return root;
    }

    private static ObjectNode ociItemToOpenAi(JsonNode oci) {
        JsonNode ociId;
        JsonNode idn;
        String dn;
        ObjectNode row = MAPPER.createObjectNode();
        String id = OciGenerativeOpenAiService.firstText((JsonNode)oci, (String[])new String[]{"name", "modelName", "model", "modelId"});
        JsonNode display = oci != null ? (oci.get("displayName") != null ? oci.get("displayName") : oci.get("modelName")) : null;
        String string = dn = display != null && display.isTextual() ? display.asText().trim() : null;
        if (dn != null && !dn.isBlank()) {
            boolean looksLikeModelName;
            String dnl = dn.toLowerCase(Locale.ROOT);
            boolean bl = looksLikeModelName = dnl.startsWith("xai.") || dnl.startsWith("cohere.") || dnl.startsWith("meta.") || dnl.startsWith("mistral.") || dnl.startsWith("openai.") || dn.matches("^[a-z0-9]+\\.[a-z0-9._\\-]+$");
            if (looksLikeModelName) {
                id = dn;
            }
        }
        if ((id == null || id.isBlank()) && dn != null && !dn.isBlank()) {
            id = dn;
        }
        if ((id == null || id.isBlank()) && oci != null && (idn = oci.get("id")) != null && !idn.isNull()) {
            id = idn.asText();
        }
        if (id == null || id.isBlank()) {
            id = "unknown";
        }
        row.put("id", id);
        row.put("object", "model");
        if (dn != null && !dn.isBlank()) {
            row.put("displayName", dn);
        }
        if (oci != null && (ociId = oci.get("id")) != null && ociId.isTextual() && !ociId.asText().isBlank()) {
            row.put("ociId", ociId.asText());
        }
        if (OciGenerativeOpenAiService.isLikelyMultiAgentModelName((String)id) || display != null && display.isTextual() && OciGenerativeOpenAiService.isLikelyMultiAgentModelName((String)display.asText())) {
            row.put("ociworkerNote", "\u8be5\u6a21\u578b\u4e3a Multi Agent\uff1a\u672c\u7f51\u5173\u4f1a\u628a /v1/chat/completions \u6539\u5199\u4e3a /v1/responses \u5e76\u5c3d\u91cf\u628a\u54cd\u5e94\u88c5\u6210 chat.completion\u3002 OCI \u901a\u5e38\u8981\u6c42 OpenAI-Project \u6216 opc-conversation-store-id\uff1b\u53ef\u5728\u300cOracle \u751f\u6210\u5f0f AI\u300d\u9875\u4e3a\u79df\u6237\u4fdd\u5b58\u9ed8\u8ba4\u503c\uff0c\u6216\u7531\u4e0a\u6e38\u8f6c\u53d1\u660e\u6587\u5934\u3002");
        }
        return row;
    }

    private static boolean isLikelyMultiAgentModelName(String s) {
        if (s == null) {
            return false;
        }
        String t = s.toLowerCase();
        return t.contains("multi-agent") || t.contains("multi agent") || t.contains("multiagent");
    }

    private static String firstText(JsonNode o, String ... fieldNames) {
        if (o == null) {
            return null;
        }
        for (String f : fieldNames) {
            JsonNode n = o.get(f);
            if (n == null || !n.isTextual() || n.asText().isBlank()) continue;
            return n.asText();
        }
        return null;
    }

    public static String extractPathAfterV1(HttpServletRequest request) {
        int p;
        String path;
        String uri = request.getRequestURI();
        String string = path = request.getContextPath() != null ? request.getContextPath() : "";
        if (!path.isEmpty() && uri.startsWith(path)) {
            uri = uri.substring(path.length());
        }
        if ((p = uri.indexOf("/v1")) < 0) {
            return "/";
        }
        String sub = uri.substring(p + "/v1".length());
        if (sub.isEmpty()) {
            return "/";
        }
        if (!sub.startsWith("/")) {
            return "/" + sub;
        }
        return sub;
    }

    public static String gatewayHint(int openaiPort) {
        return "http://<\u672c\u673a\u6216\u57df\u540d>:" + openaiPort + "/v1";
    }

    public static SimpleAuthenticationDetailsProvider buildProvider(OciUser tenant) {
        return OciGenerativeOpenAiService.buildProvider((OciUser)tenant, null);
    }

    public static SimpleAuthenticationDetailsProvider buildProvider(OciUser tenant, String regionId) {
        if (tenant == null) {
            throw new OciException("\u79df\u6237\u65e0\u6548");
        }
        String effectiveRegion = OciGenerativeOpenAiService.effectivePublicRegionId((OciUser)tenant, (Object)regionId);
        return SimpleAuthenticationDetailsProvider.builder().tenantId(tenant.getOciTenantId()).userId(tenant.getOciUserId()).fingerprint(tenant.getOciFingerprint()).region(OciRegionUtil.toRegion((String)effectiveRegion)).privateKeySupplier(() -> {
            try (FileInputStream fis = new FileInputStream(tenant.getOciKeyPath());){
                ByteArrayInputStream byteArrayInputStream;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                }
                return byteArrayInputStream;
            }
            catch (Exception e) {
                throw new OciException("\u65e0\u6cd5\u8bfb\u53d6 OCI \u79c1\u94a5: " + e.getMessage());
            }
        }).build();
    }

    private static RequestSigner newRequestSigner(OciUser tenant) {
        return OciGenerativeOpenAiService.newRequestSigner((OciUser)tenant, null);
    }

    private static RequestSigner newRequestSigner(OciUser tenant, String regionId) {
        return DefaultRequestSigner.createRequestSigner((BasicAuthenticationDetailsProvider)OciBasicForSigning.from((SimpleAuthenticationDetailsProvider)OciGenerativeOpenAiService.buildProvider((OciUser)tenant, (String)regionId)));
    }

    private static String effectivePublicRegionId(OciUser tenant, Object region) {
        String r;
        String string = r = region == null ? null : String.valueOf(region).trim();
        if (r == null || r.isEmpty() || "null".equalsIgnoreCase(r)) {
            r = tenant == null ? null : tenant.getOciRegion();
        }
        return OciRegionUtil.publicRegionId((String)r);
    }

    private static String encodePathSegmentOciModel(String s) {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
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
        if (body == null || body.length == 0) {
            return null;
        }
        if (contentType != null && !contentType.isBlank() && !contentType.toLowerCase().contains("json")) {
            return null;
        }
        try {
            JsonNode root = MAPPER.readTree(body);
            if (root != null && root.isObject()) {
                return OciGenerativeOpenAiService.textOrNull((ObjectNode)((ObjectNode)root), (String)"model");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private static boolean isAllowedModel(String model, List<String> allowedModels) {
        if (allowedModels == null || allowedModels.isEmpty()) {
            return true;
        }
        if (model == null || model.isBlank()) {
            return false;
        }
        HashSet<String> allowed = new HashSet<String>();
        for (String item : allowedModels) {
            if (item == null || item.isBlank()) continue;
            allowed.add(item.trim());
        }
        return allowed.contains(model.trim());
    }

    private static List<String> requestAllowedModels(HttpServletRequest request) {
        if (request == null) {
            return List.of();
        }
        Object v = request.getAttribute("ociworker.openai.allowedModelsJson");
        if (v == null) {
            return List.of();
        }
        return OracleAiPortBindingService.decodeAllowedModels((String)String.valueOf(v));
    }

    private static ObjectNode allowedModelsToOpenAiList(List<String> models) {
        ArrayNode data = MAPPER.createArrayNode();
        if (models != null) {
            for (String model : models) {
                if (model == null || model.isBlank()) continue;
                ObjectNode row = MAPPER.createObjectNode();
                row.put("id", model.trim());
                row.put("object", "model");
                data.add((JsonNode)row);
            }
        }
        return OciGenerativeOpenAiService.buildOpenAiModelList((ArrayNode)data);
    }

    private static void writeJson(HttpServletResponse response, JsonNode body) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        response.getOutputStream().write(MAPPER.writeValueAsBytes((Object)body));
    }

    private static void writeOpenAiError(HttpServletResponse response, int status, String type, String message, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=utf-8");
        ObjectNode root = MAPPER.createObjectNode();
        ObjectNode error = MAPPER.createObjectNode();
        error.put("type", type);
        error.put("message", message);
        error.put("code", code);
        root.set("error", (JsonNode)error);
        response.getOutputStream().write(MAPPER.writeValueAsBytes((Object)root));
    }

    private static byte[] normalizeResponsesInputForOci(byte[] input) {
        try {
            JsonNode content;
            String role;
            ObjectNode item;
            JsonNode root = MAPPER.readTree(input);
            if (root == null || !root.isObject()) {
                return input;
            }
            ObjectNode in = (ObjectNode)root;
            JsonNode inputNode = in.get("input");
            if (inputNode == null || inputNode.isNull() || inputNode.isMissingNode()) {
                return input;
            }
            if (inputNode.isTextual()) {
                ArrayNode arr = MAPPER.createArrayNode();
                ObjectNode item2 = MAPPER.createObjectNode();
                item2.put("role", "user");
                ArrayNode parts = MAPPER.createArrayNode();
                ObjectNode p = MAPPER.createObjectNode();
                p.put("type", "input_text");
                p.put("text", inputNode.asText());
                parts.add((JsonNode)p);
                item2.set("content", (JsonNode)parts);
                arr.add((JsonNode)item2);
                in.set("input", (JsonNode)arr);
                return MAPPER.writeValueAsBytes((Object)in);
            }
            if (inputNode.isObject()) {
                ObjectNode io = (ObjectNode)inputNode;
                JsonNode msgs = io.get("messages");
                if (msgs != null && msgs.isArray()) {
                    ObjectNode fauxChat = MAPPER.createObjectNode();
                    fauxChat.set("messages", msgs);
                    byte[] mapped = OciGenerativeOpenAiService.transformChatCompletionsToResponsesJson((byte[])MAPPER.writeValueAsBytes((Object)fauxChat), (int)OciGenerativeOpenAiService.defaultMaxTokens());
                    JsonNode mappedRoot = MAPPER.readTree(mapped);
                    if (mappedRoot != null && mappedRoot.isObject() && mappedRoot.get("input") != null) {
                        in.set("input", mappedRoot.get("input"));
                        return MAPPER.writeValueAsBytes((Object)in);
                    }
                }
                ArrayNode arr = MAPPER.createArrayNode();
                item = MAPPER.createObjectNode();
                role = OciGenerativeOpenAiService.textOrNull((ObjectNode)io, (String)"role");
                item.put("role", role == null || role.isBlank() ? "user" : role);
                content = io.get("content");
                if (content != null && content.isTextual()) {
                    item.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)content.asText()));
                } else if (content != null && content.isArray()) {
                    item.set("content", content);
                } else if (content != null && content.isObject()) {
                    item.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)content.toString()));
                } else {
                    item.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)String.valueOf(content)));
                }
                arr.add((JsonNode)item);
                in.set("input", (JsonNode)arr);
                inputNode = in.get("input");
            }
            if (inputNode.isArray()) {
                ArrayNode outArr = MAPPER.createArrayNode();
                for (JsonNode it : inputNode) {
                    if (it == null) continue;
                    if (it.isTextual()) {
                        item = MAPPER.createObjectNode();
                        item.put("role", "user");
                        item.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)it.asText()));
                        outArr.add((JsonNode)item);
                        continue;
                    }
                    if (!it.isObject()) {
                        item = MAPPER.createObjectNode();
                        item.put("role", "user");
                        item.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)String.valueOf(it)));
                        outArr.add((JsonNode)item);
                        continue;
                    }
                    ObjectNode io = (ObjectNode)it;
                    role = OciGenerativeOpenAiService.textOrNull((ObjectNode)io, (String)"role");
                    if (role == null || role.isBlank()) {
                        role = "user";
                    } else {
                        String rl = role.toLowerCase(Locale.ROOT);
                        if (!("user".equals(rl) || "assistant".equals(rl) || "system".equals(rl))) {
                            role = "user";
                        }
                    }
                    io.put("role", role);
                    content = io.get("content");
                    if (content != null && content.isTextual()) {
                        io.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)content.asText()));
                    } else if (content != null && content.isArray()) {
                        ArrayNode normalized = MAPPER.createArrayNode();
                        for (JsonNode part : content) {
                            if (part == null || part.isNull()) continue;
                            if (part.isTextual()) {
                                normalized.add((JsonNode)OciGenerativeOpenAiService.toInputTextPartNode((String)part.asText()));
                                continue;
                            }
                            if (part.isObject()) {
                                String tx;
                                ObjectNode po = (ObjectNode)part;
                                String t = OciGenerativeOpenAiService.textOrNull((ObjectNode)po, (String)"type");
                                if (t != null && ("text".equalsIgnoreCase(t) || "input_text".equalsIgnoreCase(t)) && (tx = OciGenerativeOpenAiService.textOrNull((ObjectNode)po, (String)"text")) != null) {
                                    normalized.add((JsonNode)OciGenerativeOpenAiService.toInputTextPartNode((String)tx));
                                    continue;
                                }
                                if (t != null && "input_image".equalsIgnoreCase(t)) {
                                    normalized.add((JsonNode)po);
                                    continue;
                                }
                            }
                            normalized.add((JsonNode)OciGenerativeOpenAiService.toInputTextPartNode((String)(part.isTextual() ? part.asText() : part.toString())));
                        }
                        if (normalized.size() > 0) {
                            io.set("content", (JsonNode)normalized);
                        } else {
                            io.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)""));
                        }
                    } else if (content != null && content.isObject()) {
                        io.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)content.toString()));
                    } else if (content == null || content.isNull()) {
                        io.set("content", (JsonNode)OciGenerativeOpenAiService.toInputTextParts((String)""));
                    }
                    outArr.add((JsonNode)io);
                }
                in.set("input", (JsonNode)outArr);
                return MAPPER.writeValueAsBytes((Object)in);
            }
            return input;
        }
        catch (Exception e) {
            return input;
        }
    }

    private static byte[] truncateResponsesInputForMultiAgent(byte[] body, int maxItems) {
        if (body == null || body.length == 0 || maxItems <= 0) {
            return body;
        }
        try {
            JsonNode root = MAPPER.readTree(body);
            if (root == null || !root.isObject()) {
                return body;
            }
            ObjectNode o = (ObjectNode)root;
            String model = OciGenerativeOpenAiService.textOrNull((ObjectNode)o, (String)"model");
            if (!OciGenerativeOpenAiService.isLikelyMultiAgentModelName((String)model)) {
                return body;
            }
            JsonNode input = o.get("input");
            if (input == null || !input.isArray()) {
                return body;
            }
            ArrayNode arr = (ArrayNode)input;
            int n = arr.size();
            if (n <= maxItems) {
                return body;
            }
            ArrayNode out = MAPPER.createArrayNode();
            for (int i = n - maxItems; i < n; ++i) {
                JsonNode it = arr.get(i);
                if (it == null) continue;
                out.add(it);
            }
            o.set("input", (JsonNode)out);
            return MAPPER.writeValueAsBytes((Object)o);
        }
        catch (Exception ignored) {
            return body;
        }
    }

    private static String describeResponsesInputShape(byte[] body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            if (root == null || !root.isObject()) {
                return "root=" + String.valueOf(root == null ? "null" : root.getNodeType());
            }
            JsonNode input = root.get("input");
            if (input == null) {
                return "input=<missing>";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("input=").append(input.getNodeType());
            if (input.isTextual()) {
                sb.append("(len=").append(input.asText().length()).append(")");
                return sb.toString();
            }
            if (input.isObject()) {
                sb.append("(keys=");
                Iterator it = input.fieldNames();
                for (int c = 0; it.hasNext() && c < 8; ++c) {
                    if (c > 0) {
                        sb.append(",");
                    }
                    sb.append((String)it.next());
                }
                if (it.hasNext()) {
                    sb.append(",\u2026");
                }
                sb.append(")");
                return sb.toString();
            }
            if (input.isArray()) {
                sb.append("(n=").append(input.size()).append(")");
                if (input.size() > 0) {
                    JsonNode first = input.get(0);
                    sb.append(" first=").append((Object)(first == null ? "null" : first.getNodeType()));
                    if (first != null && first.isObject()) {
                        JsonNode ctn = first.get("content");
                        sb.append(" content=").append(ctn == null ? "<missing>" : ctn.getNodeType().toString());
                        if (ctn != null && ctn.isArray() && ctn.size() > 0) {
                            String t;
                            JsonNode p0 = ctn.get(0);
                            String string = t = p0 != null && p0.isObject() ? OciGenerativeOpenAiService.textOrNull((ObjectNode)((ObjectNode)p0), (String)"type") : null;
                            if (t != null) {
                                sb.append(" part0.type=").append(t);
                            }
                        }
                    }
                }
                return sb.toString();
            }
            return sb.toString();
        }
        catch (Exception e) {
            return "parse_error(" + e.getClass().getSimpleName() + ")";
        }
    }

    private static ArrayNode toInputTextParts(String text) {
        ArrayNode parts = MAPPER.createArrayNode();
        parts.add((JsonNode)OciGenerativeOpenAiService.toInputTextPartNode((String)text));
        return parts;
    }

    private static ObjectNode toInputTextPartNode(String text) {
        ObjectNode p = MAPPER.createObjectNode();
        p.put("type", "input_text");
        p.put("text", text == null ? "" : text);
        return p;
    }

    private static boolean isStreamRequest(byte[] body, String contentType) {
        if (body == null || contentType == null || !contentType.toLowerCase().contains("json")) {
            return false;
        }
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
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    private static int defaultMaxTokens() {
        try {
            return OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)defaultMaxTokensSupplier.getAsInt());
        }
        catch (Exception ignored) {
            return 2048;
        }
    }

    private static int requestDefaultMaxTokens(HttpServletRequest request) {
        if (request != null) {
            Object v = request.getAttribute("ociworker.openai.defaultMaxTokens");
            if (v instanceof Number) {
                Number n = (Number)v;
                return OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)n.intValue());
            }
            if (v != null) {
                try {
                    return OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)Integer.parseInt(String.valueOf(v)));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return OciGenerativeOpenAiService.defaultMaxTokens();
    }

    private static byte[] transformChatCompletionsJson(byte[] input, int defaultMaxTokens) {
        try {
            JsonNode force;
            JsonNode root = MAPPER.readTree(input);
            if (root == null || !root.isObject()) {
                return input;
            }
            ObjectNode o = (ObjectNode)root;
            if (o.get("max_tokens") == null || o.get("max_tokens").isNull() || o.get("max_tokens").isMissingNode()) {
                o.put("max_tokens", OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)defaultMaxTokens));
            }
            if ((force = o.get("force_non_stream")) != null && (force.isBoolean() && force.asBoolean() || force.isTextual() && "true".equalsIgnoreCase(force.asText()))) {
                o.put("stream", false);
            }
            o.remove("force_non_stream");
            return MAPPER.writeValueAsBytes((Object)o);
        }
        catch (Exception e) {
            return input;
        }
    }

    private static byte[] transformChatCompletionsToResponsesJson(byte[] input, int defaultMaxTokens) {
        try {
            JsonNode topP;
            JsonNode messages;
            JsonNode root = MAPPER.readTree(input);
            if (root == null || !root.isObject()) {
                return input;
            }
            ObjectNode in = (ObjectNode)root;
            String model = OciGenerativeOpenAiService.textOrNull((ObjectNode)in, (String)"model");
            ObjectNode out = MAPPER.createObjectNode();
            if (model != null && !model.isBlank()) {
                out.put("model", model);
            }
            if ((messages = in.get("messages")) != null && messages.isArray()) {
                ArrayNode inputArr = MAPPER.createArrayNode();
                for (JsonNode m : messages) {
                    if (m == null || !m.isObject()) continue;
                    ObjectNode mo = (ObjectNode)m;
                    String role = OciGenerativeOpenAiService.textOrNull((ObjectNode)mo, (String)"role");
                    if (role == null || role.isBlank()) {
                        role = "user";
                    }
                    ObjectNode item = MAPPER.createObjectNode();
                    item.put("role", role);
                    JsonNode content = mo.get("content");
                    if (content == null || content.isNull()) continue;
                    if (content.isTextual()) {
                        parts = MAPPER.createArrayNode();
                        p = MAPPER.createObjectNode();
                        p.put("type", "input_text");
                        p.put("text", content.asText());
                        parts.add((JsonNode)p);
                        item.set("content", (JsonNode)parts);
                    } else if (content.isArray()) {
                        item.set("content", content);
                    } else if (content.isObject()) {
                        parts = MAPPER.createArrayNode();
                        p = MAPPER.createObjectNode();
                        p.put("type", "input_text");
                        p.put("text", content.toString());
                        parts.add((JsonNode)p);
                        item.set("content", (JsonNode)parts);
                    } else {
                        parts = MAPPER.createArrayNode();
                        p = MAPPER.createObjectNode();
                        p.put("type", "input_text");
                        p.put("text", String.valueOf(content.asText()));
                        parts.add((JsonNode)p);
                        item.set("content", (JsonNode)parts);
                    }
                    inputArr.add((JsonNode)item);
                }
                out.set("input", (JsonNode)inputArr);
            } else {
                JsonNode p = in.get("prompt");
                if (p != null && p.isTextual()) {
                    out.put("input", p.asText());
                }
            }
            JsonNode mt = in.get("max_tokens");
            if (mt != null && !mt.isNull() && !mt.isMissingNode()) {
                if (mt.isNumber()) {
                    out.put("max_output_tokens", mt.intValue());
                } else {
                    out.put("max_output_tokens", mt.asInt(OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)defaultMaxTokens)));
                }
            } else {
                out.put("max_output_tokens", OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)defaultMaxTokens));
            }
            JsonNode temp = in.get("temperature");
            if (temp != null && !temp.isNull() && !temp.isMissingNode()) {
                out.set("temperature", temp);
            }
            if ((topP = in.get("top_p")) != null && !topP.isNull() && !topP.isMissingNode()) {
                out.set("top_p", topP);
            }
            out.put("stream", false);
            return MAPPER.writeValueAsBytes((Object)out);
        }
        catch (Exception e) {
            return input;
        }
    }

    private void longCopyStream(HttpClient client, HttpRequest httpRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            HttpResponse<InputStream> resp = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            int code = resp.statusCode();
            for (Map.Entry<String, List<String>> e : resp.headers().map().entrySet()) {
                String k = e.getKey();
                if (k == null || "transfer-encoding".equalsIgnoreCase(k) || "connection".equalsIgnoreCase(k) || e.getValue() == null || e.getValue().isEmpty() || "content-length".equalsIgnoreCase(k) && code >= 200 && code < 300) continue;
                if ("content-type".equalsIgnoreCase(k) || "cache-control".equalsIgnoreCase(k)) {
                    response.setHeader(k, e.getValue().get(0));
                    continue;
                }
                for (String v : e.getValue()) {
                    response.addHeader(k, v);
                }
            }
            response.setStatus(code);
            if (code >= 400) {
                byte[] bytes = new byte[]{};
                try (InputStream in = resp.body();){
                    if (in != null) {
                        bytes = in.readAllBytes();
                        response.getOutputStream().write(bytes);
                    }
                }
                try {
                    boolean looksLikeInputDeserializeError;
                    String b = bytes.length > 0 ? new String(bytes, StandardCharsets.UTF_8) : "";
                    String bl = b.toLowerCase(Locale.ROOT);
                    boolean bl2 = looksLikeInputDeserializeError = bl.contains("failed to deserialize") || bl.contains("untagged enum") || bl.contains("modelinput") || bl.contains("modellnput");
                    if (request != null) {
                        String rid = OciGenerativeOpenAiService.firstRequestHeader((HttpServletRequest)request, (String[])new String[]{"x-request-id", "x-cursor-request-id", "x-openai-request-id", "x-amzn-trace-id", "traceparent"});
                        String origPath = String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1"));
                        String finalPath = String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1"));
                        String before = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.before"));
                        String after = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.after"));
                        log.warn("OCI proxy error(stream); rid={} code={} origPath={} finalPath={} before={} after={} body={}", new Object[]{rid, code, origPath, finalPath, before, after, OciGenerativeOpenAiService.truncate((String)b, (int)1200)});
                        if (looksLikeInputDeserializeError && OciGenerativeOpenAiService.isResponsesPath((String)OciGenerativeOpenAiService.extractPathAfterV1((HttpServletRequest)request))) {
                            log.warn("OCI /responses ModelInput error(stream); rid={} before={} after={} body={}", new Object[]{rid, before, after, OciGenerativeOpenAiService.truncate((String)b, (int)1200)});
                        }
                    }
                }
                catch (Exception b) {
                    // empty catch block
                }
                return;
            }
            if (response.getContentType() == null) {
                String ct = resp.headers().firstValue("content-type").orElse("text/event-stream; charset=utf-8");
                response.setContentType(ct);
            }
            try (InputStream in = resp.body();
                 ServletOutputStream out = response.getOutputStream();){
                int n;
                if (in == null) {
                    return;
                }
                response.setBufferSize(8192);
                byte[] buf = new byte[16384];
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                    out.flush();
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("\u6d41\u5f0f\u8bf7\u6c42\u4e2d\u65ad");
        }
        catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Broken pipe")) {
                return;
            }
            throw e;
        }
    }

    private void bufferAndCopy(HttpClient client, HttpRequest httpRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            String ct;
            String b;
            HttpResponse<String> resp = client.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = resp.statusCode();
            resp.headers().map().forEach((k, vals) -> {
                if (k == null || vals == null) {
                    return;
                }
                if ("transfer-encoding".equalsIgnoreCase((String)k) || "connection".equalsIgnoreCase((String)k) || "content-length".equalsIgnoreCase((String)k)) {
                    return;
                }
                if (vals != null) {
                    for (String v : vals) {
                        if (v == null) continue;
                        response.addHeader(k, v);
                    }
                }
            });
            if (response.getContentType() == null) {
                String ct2 = resp.headers().firstValue("content-type").orElse("application/json; charset=utf-8");
                response.setContentType(ct2);
            }
            response.setStatus(code);
            String string = b = resp.body() != null ? resp.body() : "";
            if (code >= 400 && request != null && b != null) {
                String bl = b.toLowerCase(Locale.ROOT);
                boolean looksLikeInputDeserializeError = bl.contains("failed to deserialize") || bl.contains("untagged enum") || bl.contains("modelinput") || bl.contains("modellnput");
                boolean maybeResponses = Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.useRawV1Base")) || OciGenerativeOpenAiService.isResponsesPath((String)String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1"))) || OciGenerativeOpenAiService.isResponsesPath((String)String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1"))) || OciGenerativeOpenAiService.isResponsesPath((String)OciGenerativeOpenAiService.extractPathAfterV1((HttpServletRequest)request));
                String before = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.before"));
                String after = String.valueOf(request.getAttribute("ociworker.debug.responsesInputShape.after"));
                String rid = OciGenerativeOpenAiService.firstRequestHeader((HttpServletRequest)request, (String[])new String[]{"x-request-id", "x-cursor-request-id", "x-openai-request-id", "x-amzn-trace-id", "traceparent"});
                String origPath = String.valueOf(request.getAttribute("ociworker.debug.origPathAfterV1"));
                String finalPath = String.valueOf(request.getAttribute("ociworker.debug.finalPathAfterV1"));
                log.warn("OCI proxy error; rid={} code={} origPath={} finalPath={} maybeResponses={} before={} after={} body={}", new Object[]{rid, code, origPath, finalPath, maybeResponses, before, after, OciGenerativeOpenAiService.truncate((String)b, (int)1200)});
                if (looksLikeInputDeserializeError && maybeResponses) {
                    log.warn("OCI /responses ModelInput error; rid={} code={} origPath={} finalPath={} before={} after={} body={}", new Object[]{rid, code, origPath, finalPath, before, after, OciGenerativeOpenAiService.truncate((String)b, (int)1200)});
                }
            }
            if (code >= 200 && code < 300 && request != null && Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.chatToResponses")) && b != null && !b.isBlank() && (ct = resp.headers().firstValue("content-type").orElse("application/json; charset=utf-8")).toLowerCase().contains("json")) {
                try {
                    String text;
                    String modelHint = (String)request.getAttribute("ociworker.rewrite.model");
                    if (Boolean.TRUE.equals(request.getAttribute("ociworker.rewrite.simulateSse")) && (text = OciGenerativeOpenAiService.extractResponsesAssistantText((ObjectNode)((ObjectNode)MAPPER.readTree(b)))) != null) {
                        response.setStatus(200);
                        response.setHeader("cache-control", "no-cache");
                        response.setContentType("text/event-stream; charset=utf-8");
                        OciGenerativeOpenAiService.writeChatCompletionSseFromText((HttpServletResponse)response, (String)text, (String)modelHint);
                        return;
                    }
                    b = OciGenerativeOpenAiService.convertResponsesJsonToChatCompletionJson((String)b, (String)modelHint);
                    response.setContentType("application/json; charset=utf-8");
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            response.getOutputStream().write(b.getBytes(StandardCharsets.UTF_8));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("\u8bf7\u6c42\u4e2d\u65ad");
        }
        catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Broken pipe")) {
                return;
            }
            throw e;
        }
    }

    private static String textOrNull(ObjectNode o, String field) {
        if (o == null) {
            return null;
        }
        JsonNode n = o.get(field);
        if (n == null || n.isNull() || n.isMissingNode()) {
            return null;
        }
        if (n.isTextual()) {
            return n.asText();
        }
        if (n.isNumber() || n.isBoolean()) {
            return n.toString();
        }
        return null;
    }

    private static String convertResponsesJsonToChatCompletionJson(String responsesJson, String modelHint) throws Exception {
        JsonNode m;
        JsonNode r = MAPPER.readTree(responsesJson);
        if (r == null || !r.isObject()) {
            return responsesJson;
        }
        ObjectNode ro = (ObjectNode)r;
        String text = OciGenerativeOpenAiService.extractResponsesAssistantText((ObjectNode)ro);
        if (text == null) {
            return responsesJson;
        }
        String model = modelHint;
        if ((model == null || model.isBlank()) && (m = ro.get("model")) != null && m.isTextual()) {
            model = m.asText();
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
        ch.set("message", (JsonNode)msg);
        ch.put("finish_reason", "stop");
        choices.add((JsonNode)ch);
        out.set("choices", (JsonNode)choices);
        return MAPPER.writeValueAsString((Object)out);
    }

    private static String extractResponsesAssistantText(ObjectNode r) {
        if (r == null) {
            return null;
        }
        JsonNode ot = r.get("output_text");
        if (ot != null && ot.isTextual() && !ot.asText().isBlank()) {
            return ot.asText();
        }
        JsonNode out = r.get("output");
        if (out == null || !out.isArray()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (JsonNode item : out) {
            JsonNode content;
            JsonNode role;
            if (item == null || !item.isObject()) continue;
            ObjectNode io = (ObjectNode)item;
            String type = OciGenerativeOpenAiService.textOrNull((ObjectNode)io, (String)"type");
            if (type == null || "message".equalsIgnoreCase(type) || !"output_message".equalsIgnoreCase(type)) {
                // empty if block
            }
            if ((role = io.get("role")) != null && role.isTextual() && !"assistant".equalsIgnoreCase(role.asText()) || (content = io.get("content")) == null) continue;
            if (content.isTextual()) {
                OciGenerativeOpenAiService.appendText((StringBuilder)sb, (String)content.asText());
                continue;
            }
            if (!content.isArray()) continue;
            for (JsonNode part : content) {
                JsonNode tx;
                ObjectNode po;
                String pt;
                if (part == null || !part.isObject() || (pt = OciGenerativeOpenAiService.textOrNull((ObjectNode)(po = (ObjectNode)part), (String)"type")) == null || !"output_text".equalsIgnoreCase(pt) && !"text".equalsIgnoreCase(pt) || (tx = po.get("text")) == null || !tx.isTextual()) continue;
                OciGenerativeOpenAiService.appendText((StringBuilder)sb, (String)tx.asText());
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private static void appendText(StringBuilder sb, String s) {
        if (s == null || s.isBlank()) {
            return;
        }
        if (sb.length() > 0) {
            sb.append("\n");
        }
        sb.append(s);
    }

    private static void writeChatCompletionSseFromText(HttpServletResponse response, String text, String modelHint) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        String id = "chatcmpl-ociworker";
        long created = System.currentTimeMillis() / 1000L;
        String model = modelHint == null ? "" : modelHint;
        String first = "{\"id\":\"" + id + "\",\"object\":\"chat.completion.chunk\",\"created\":" + created + ",\"model\":\"" + OciGenerativeOpenAiService.escapeJson((String)model) + "\",\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\"},\"finish_reason\":null}]}";
        out.write(("data: " + first + "\n\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
        int step = 200;
        for (int i = 0; i < text.length(); i += step) {
            String part = text.substring(i, Math.min(text.length(), i + step));
            String j = "{\"id\":\"" + id + "\",\"object\":\"chat.completion.chunk\",\"created\":" + created + ",\"model\":\"" + OciGenerativeOpenAiService.escapeJson((String)model) + "\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"" + OciGenerativeOpenAiService.escapeJson((String)part) + "\"},\"finish_reason\":null}]}";
            out.write(("data: " + j + "\n\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
        String last = "{\"id\":\"" + id + "\",\"object\":\"chat.completion.chunk\",\"created\":" + created + ",\"model\":\"" + OciGenerativeOpenAiService.escapeJson((String)model) + "\",\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"stop\"}]}";
        out.write(("data: " + last + "\n\n").getBytes(StandardCharsets.UTF_8));
        out.write("data: [DONE]\n\n".getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }

    private static Map<String, String> extractOciGenerativeForwardHeaders(HttpServletRequest request, OciUser tenant) {
        LinkedHashMap<String, String> out = new LinkedHashMap<String, String>();
        if (request != null) {
            String convStore;
            String project = OciGenerativeOpenAiService.firstRequestHeader((HttpServletRequest)request, (String[])new String[]{"OpenAI-Project", "openai-project", "X-OpenAI-Project"});
            if (project != null && !project.isBlank()) {
                out.put("OpenAI-Project", project.trim());
            }
            if ((convStore = OciGenerativeOpenAiService.firstRequestHeader((HttpServletRequest)request, (String[])new String[]{"opc-conversation-store-id", "OPC-Conversation-Store-Id"})) != null && !convStore.isBlank()) {
                out.put("opc-conversation-store-id", convStore.trim());
            }
        }
        if (tenant != null) {
            if (!out.containsKey("OpenAI-Project") && tenant.getGenerativeOpenaiProject() != null && !tenant.getGenerativeOpenaiProject().isBlank()) {
                out.put("OpenAI-Project", tenant.getGenerativeOpenaiProject().trim());
            }
            if (!out.containsKey("opc-conversation-store-id") && tenant.getGenerativeConversationStoreId() != null && !tenant.getGenerativeConversationStoreId().isBlank()) {
                out.put("opc-conversation-store-id", tenant.getGenerativeConversationStoreId().trim());
            }
        }
        return out.isEmpty() ? null : out;
    }

    private static String firstRequestHeader(HttpServletRequest request, String ... headerNames) {
        if (request == null || headerNames == null) {
            return null;
        }
        for (String name : headerNames) {
            String v;
            if (name == null || (v = request.getHeader(name)) == null || v.isBlank()) continue;
            return v;
        }
        return null;
    }

    private HttpRequest buildSignedRequest(RequestSigner signer, String method, URI uri, byte[] body, String contentType, String clientAccept, String opcCompartmentId, Map<String, String> extraSignedHeaders) {
        LinkedHashMap<String, List> headers = new LinkedHashMap<String, List>();
        headers.put("host", OciGenerativeOpenAiService.list((String)OciGenerativeOpenAiService.h((String)uri.getHost())));
        headers.put("accept", OciGenerativeOpenAiService.list((String)OciGenerativeOpenAiService.h((String)clientAccept)));
        if (opcCompartmentId != null && !opcCompartmentId.isBlank()) {
            headers.put("opc-compartment-id", OciGenerativeOpenAiService.list((String)opcCompartmentId));
        }
        if (extraSignedHeaders != null) {
            for (Map.Entry<String, String> e : extraSignedHeaders.entrySet()) {
                String val;
                if (e.getKey() == null || (val = e.getValue()) == null || val.isBlank()) continue;
                headers.put(e.getKey(), OciGenerativeOpenAiService.list((String)OciGenerativeOpenAiService.h((String)val.trim())));
            }
        }
        if (contentType != null && !contentType.isBlank()) {
            headers.put("content-type", OciGenerativeOpenAiService.list((String)contentType));
        } else if (body != null && body.length > 0) {
            headers.put("content-type", OciGenerativeOpenAiService.list((String)"application/json"));
        }
        OciDuplicatableByteArrayInputStream toSign = null;
        if (body != null && body.length > 0) {
            toSign = new OciDuplicatableByteArrayInputStream(body);
        }
        Map signedObject = signer.signRequest(uri, method, headers, toSign);
        Map signed = OciGenerativeOpenAiService.castSignedHeaders((Object)signedObject);
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(uri).version(HttpClient.Version.HTTP_1_1).timeout(Duration.ofHours(1L));
        this.applyToBuilder(b, headers);
        this.applyToBuilder(b, signed);
        if (body == null || body.length == 0) {
            if ("GET".equalsIgnoreCase(method)) {
                return b.GET().build();
            }
            if ("HEAD".equalsIgnoreCase(method)) {
                return b.method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
            }
            return b.method(method, HttpRequest.BodyPublishers.noBody()).build();
        }
        return b.method(method, HttpRequest.BodyPublishers.ofByteArray(body)).build();
    }

    private static String h(String s) {
        return s == null ? "" : s;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (signed == null) {
            return new LinkedHashMap<String, List<String>>();
        }
        if (signed instanceof Map) {
            Map raw = (Map)signed;
            LinkedHashMap<String, List<String>> out = new LinkedHashMap<String, List<String>>();
            for (Map.Entry e : raw.entrySet()) {
                String key = String.valueOf(e.getKey());
                if (e.getValue() == null) continue;
                Object v = e.getValue();
                if (v instanceof List) {
                    List list = (List)v;
                    ArrayList<String> ls = new ArrayList<String>();
                    for (Object o : list) {
                        if (o == null) continue;
                        ls.add(String.valueOf(o));
                    }
                    if (ls.isEmpty()) continue;
                    out.put(key, ls);
                    continue;
                }
                v = e.getValue();
                if (v instanceof String) {
                    String s = (String)v;
                    out.put(key, OciGenerativeOpenAiService.list((String)s));
                    continue;
                }
                out.put(key, OciGenerativeOpenAiService.list((String)String.valueOf(e.getValue())));
            }
            return out;
        }
        if (signed instanceof String) {
            return new LinkedHashMap<String, List<String>>();
        }
        return new LinkedHashMap<String, List<String>>();
    }

    private static List<String> list(String v) {
        ArrayList<String> l = new ArrayList<String>(1);
        l.add(v);
        return l;
    }

    private void applyToBuilder(HttpRequest.Builder b, Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String name;
            if (e.getValue() == null || (name = e.getKey()) == null || OciGenerativeOpenAiService.isDisallowedOnHttpRequestBuilder((String)name)) continue;
            for (String v : e.getValue()) {
                if (v == null) continue;
                b.header(name, v);
            }
        }
    }

    private static boolean isDisallowedOnHttpRequestBuilder(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        return n.equals("host") || n.equals("connection") || n.equals("content-length") || n.equals("expect") || n.equals("upgrade");
    }

    private HttpClient pickHttpClient() {
        return this.ociProxyConfigService == null ? HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(30L)).build() : this.ociProxyConfigService.newOutboundHttpClient();
    }

    private static String truncate(String s, int n) {
        if (s == null) {
            return "";
        }
        return s.length() > n ? s.substring(0, n) + "\u2026" : s;
    }
}

