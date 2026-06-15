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
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class OspGatewayHttp {
    private static final String API_VERSION = "20191001";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OspGatewayHttp() {
    }

    public static JsonNode listSubscriptionsJson(
        HttpClient http, SubscriptionServiceClient ospClient, BasicAuthenticationDetailsProvider authProvider, String ospHomeRegion, String compartmentId
    ) {
        String base = endpoint(ospClient);
        String q = query("ospHomeRegion", ospHomeRegion, "compartmentId", compartmentId);
        URI uri = URI.create(base + "/20191001/subscriptions" + q);
        return signedGetJson(http, authProvider, uri);
    }

    public static JsonNode getSubscriptionJson(
        HttpClient http,
        SubscriptionServiceClient ospClient,
        BasicAuthenticationDetailsProvider authProvider,
        String ospHomeRegion,
        String compartmentId,
        String subscriptionId
    ) {
        if (subscriptionId != null && !subscriptionId.isBlank()) {
            String base = endpoint(ospClient);
            String path = "/20191001/subscriptions/" + ParamEncoder.encodePathParam(subscriptionId.trim());
            String q = query("ospHomeRegion", ospHomeRegion, "compartmentId", compartmentId);
            URI uri = URI.create(base + path + q);
            return signedGetJson(http, authProvider, uri);
        } else {
            return null;
        }
    }

    public static JsonNode unwrapSubscriptionBody(JsonNode body) {
        if (body != null && !body.isNull()) {
            JsonNode sub = body.get("subscription");
            if (sub != null && !sub.isNull()) {
                return sub;
            } else {
                JsonNode items = body.get("items");
                if (items != null && items.isArray() && !items.isEmpty()) {
                    return items.get(0);
                } else {
                    return !body.has("id") && !body.has("planType") && !body.has("timeStart") ? body : body;
                }
            }
        } else {
            return null;
        }
    }

    private static String endpoint(SubscriptionServiceClient ospClient) {
        String endpoint = ospClient.getEndpoint();
        if (endpoint != null && !endpoint.isBlank()) {
            return endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        } else {
            throw new OciException("OSP Gateway endpoint 为空");
        }
    }

    private static String query(String... kv) {
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
            RequestSigner signer = DefaultRequestSigner.createRequestSigner(authProvider);
            Map<String, List<String>> headers = new LinkedHashMap<>();
            headers.put("accept", list("application/json"));
            headers.put("host", list(uri.getHost()));
            Map<String, List<String>> signed = castSignedHeaders(signer.signRequest(uri, "GET", headers, null));
            Builder b = HttpRequest.newBuilder(uri).GET().timeout(Duration.ofSeconds(45L));
            applyHeaders(b, headers);
            applyHeaders(b, signed);
            HttpResponse<String> resp = http.send(b.build(), BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == 404) {
                return null;
            } else if (code / 100 != 2) {
                throw new OciException("OSP 订阅接口失败: HTTP " + code + " " + truncate(resp.body()));
            } else {
                String body = resp.body();
                return body != null && !body.isBlank() ? MAPPER.readTree(body) : null;
            }
        } catch (OciException var10) {
            throw var10;
        } catch (InterruptedException var11) {
            Thread.currentThread().interrupt();
            throw new OciException("OSP 订阅请求中断: " + var11.getMessage());
        } catch (IOException var12) {
            throw new OciException("OSP 订阅请求失败: " + var12.getMessage());
        }
    }

    private static List<String> list(String v) {
        List<String> l = new ArrayList<>(1);
        l.add(v);
        return l;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (!(signed instanceof Map<?, ?> raw)) {
            throw new OciException("OSP 请求签名结果异常");
        } else {
            LinkedHashMap out = new LinkedHashMap();

            for (Entry<?, ?> e : raw.entrySet()) {
                String key = String.valueOf(e.getKey());
                Object v = e.getValue();
                if (v instanceof List) {
                    List<?> list = (List<?>)v;
                    List<String> vals = new ArrayList<>();

                    for (Object o : list) {
                        vals.add(String.valueOf(o));
                    }

                    out.put(key, vals);
                } else if (v != null) {
                    out.put(key, list(String.valueOf(v)));
                }
            }

            return out;
        }
    }

    private static void applyHeaders(Builder builder, Map<String, List<String>> headers) {
        for (Entry<String, List<String>> e : headers.entrySet()) {
            if (e.getValue() != null) {
                for (String v : e.getValue()) {
                    if (v != null) {
                        builder.header(e.getKey(), v);
                    }
                }
            }
        }
    }

    private static String truncate(String s) {
        if (s == null) {
            return "";
        } else {
            return s.length() > 400 ? s.substring(0, 400) + "…" : s;
        }
    }
}
