/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.util.OspGatewayHttp
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.http.internal.ParamEncoder
 *  com.oracle.bmc.http.signing.DefaultRequestSigner
 *  com.oracle.bmc.http.signing.RequestSigner
 *  com.oracle.bmc.ospgateway.SubscriptionServiceClient
 */
package com.ocxworker.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.exception.OciException;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.http.internal.ParamEncoder;
import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;
import com.oracle.bmc.ospgateway.SubscriptionServiceClient;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Exception performing whole class analysis ignored.
 */
public final class OspGatewayHttp {
    private static final String API_VERSION = "20191001";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OspGatewayHttp() {
    }

    public static JsonNode listSubscriptionsJson(HttpClient http, SubscriptionServiceClient ospClient, BasicAuthenticationDetailsProvider authProvider, String ospHomeRegion, String compartmentId) {
        String base = OspGatewayHttp.endpoint((SubscriptionServiceClient)ospClient);
        String q = OspGatewayHttp.query((String[])new String[]{"ospHomeRegion", ospHomeRegion, "compartmentId", compartmentId});
        URI uri = URI.create(base + "/20191001/subscriptions" + q);
        return OspGatewayHttp.signedGetJson((HttpClient)http, (BasicAuthenticationDetailsProvider)authProvider, (URI)uri);
    }

    public static JsonNode getSubscriptionJson(HttpClient http, SubscriptionServiceClient ospClient, BasicAuthenticationDetailsProvider authProvider, String ospHomeRegion, String compartmentId, String subscriptionId) {
        if (subscriptionId == null || subscriptionId.isBlank()) {
            return null;
        }
        String base = OspGatewayHttp.endpoint((SubscriptionServiceClient)ospClient);
        String path = "/20191001/subscriptions/" + ParamEncoder.encodePathParam((String)subscriptionId.trim());
        String q = OspGatewayHttp.query((String[])new String[]{"ospHomeRegion", ospHomeRegion, "compartmentId", compartmentId});
        URI uri = URI.create(base + path + q);
        return OspGatewayHttp.signedGetJson((HttpClient)http, (BasicAuthenticationDetailsProvider)authProvider, (URI)uri);
    }

    public static JsonNode unwrapSubscriptionBody(JsonNode body) {
        if (body == null || body.isNull()) {
            return null;
        }
        JsonNode sub = body.get("subscription");
        if (sub != null && !sub.isNull()) {
            return sub;
        }
        JsonNode items = body.get("items");
        if (items != null && items.isArray() && !items.isEmpty()) {
            return items.get(0);
        }
        if (body.has("id") || body.has("planType") || body.has("timeStart")) {
            return body;
        }
        return body;
    }

    private static String endpoint(SubscriptionServiceClient ospClient) {
        String endpoint = ospClient.getEndpoint();
        if (endpoint == null || endpoint.isBlank()) {
            throw new OciException("OSP Gateway endpoint \u4e3a\u7a7a");
        }
        return endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
    }

    private static String query(String ... kv) {
        StringBuilder sb = new StringBuilder("?");
        for (int i = 0; i < kv.length; i += 2) {
            if (i > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(kv[i], StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(kv[i + 1] == null ? "" : kv[i + 1], StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private static JsonNode signedGetJson(HttpClient http, BasicAuthenticationDetailsProvider authProvider, URI uri) {
        try {
            RequestSigner signer = DefaultRequestSigner.createRequestSigner((BasicAuthenticationDetailsProvider)authProvider);
            LinkedHashMap<String, List> headers = new LinkedHashMap<String, List>();
            headers.put("accept", OspGatewayHttp.list((String)"application/json"));
            headers.put("host", OspGatewayHttp.list((String)uri.getHost()));
            Map signed = OspGatewayHttp.castSignedHeaders((Object)signer.signRequest(uri, "GET", headers, null));
            HttpRequest.Builder b = HttpRequest.newBuilder(uri).GET().timeout(Duration.ofSeconds(45L));
            OspGatewayHttp.applyHeaders((HttpRequest.Builder)b, headers);
            OspGatewayHttp.applyHeaders((HttpRequest.Builder)b, (Map)signed);
            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == 404) {
                return null;
            }
            if (code / 100 != 2) {
                throw new OciException("OSP \u8ba2\u9605\u63a5\u53e3\u5931\u8d25: HTTP " + code + " " + OspGatewayHttp.truncate((String)resp.body()));
            }
            String body = resp.body();
            if (body == null || body.isBlank()) {
                return null;
            }
            return MAPPER.readTree(body);
        }
        catch (OciException e) {
            throw e;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("OSP \u8ba2\u9605\u8bf7\u6c42\u4e2d\u65ad: " + e.getMessage());
        }
        catch (IOException e) {
            throw new OciException("OSP \u8ba2\u9605\u8bf7\u6c42\u5931\u8d25: " + e.getMessage());
        }
    }

    private static List<String> list(String v) {
        ArrayList<String> l = new ArrayList<String>(1);
        l.add(v);
        return l;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (!(signed instanceof Map)) {
            throw new OciException("OSP \u8bf7\u6c42\u7b7e\u540d\u7ed3\u679c\u5f02\u5e38");
        }
        Map raw = (Map)signed;
        LinkedHashMap<String, List<String>> out = new LinkedHashMap<String, List<String>>();
        for (Map.Entry e : raw.entrySet()) {
            String key = String.valueOf(e.getKey());
            Object v = e.getValue();
            if (v instanceof List) {
                List list = (List)v;
                ArrayList<String> vals = new ArrayList<String>();
                for (Object o : list) {
                    vals.add(String.valueOf(o));
                }
                out.put(key, vals);
                continue;
            }
            if (v == null) continue;
            out.put(key, OspGatewayHttp.list((String)String.valueOf(v)));
        }
        return out;
    }

    private static void applyHeaders(HttpRequest.Builder builder, Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            if (e.getValue() == null) continue;
            for (String v : e.getValue()) {
                if (v == null) continue;
                builder.header(e.getKey(), v);
            }
        }
    }

    private static String truncate(String s) {
        if (s == null) {
            return "";
        }
        return s.length() > 400 ? s.substring(0, 400) + "\u2026" : s;
    }
}

