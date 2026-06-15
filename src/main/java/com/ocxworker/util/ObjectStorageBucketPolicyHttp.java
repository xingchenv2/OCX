package com.ocxworker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.exception.OciException;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.http.internal.ParamEncoder;
import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ObjectStorageBucketPolicyHttp {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ObjectStorageBucketPolicyHttp() {
    }

    public static void putBucketPolicy(
        HttpClient http,
        ObjectStorageClient objectStorageClient,
        BasicAuthenticationDetailsProvider authProvider,
        String namespace,
        String bucketName,
        String policy
    ) {
        if (namespace == null || namespace.isBlank() || bucketName == null || bucketName.isBlank()) {
            throw new OciException("namespace / bucketName 不能为空");
        } else if (policy == null) {
            throw new OciException("policy 不能为空");
        } else {
            String endpoint = objectStorageClient.getEndpoint();
            if (endpoint != null && !endpoint.isBlank()) {
                if (endpoint.endsWith("/")) {
                    endpoint = endpoint.substring(0, endpoint.length() - 1);
                }

                String path = "/20160918/n/" + ParamEncoder.encodePathParam(namespace) + "/b/" + ParamEncoder.encodePathParam(bucketName) + "/policy";
                URI uri = URI.create(endpoint + path);
                RequestSigner signer = DefaultRequestSigner.createRequestSigner(authProvider);
                String ifMatch = null;

                try {
                    Map<String, List<String>> getHeaders = new LinkedHashMap<>();
                    getHeaders.put("accept", list("application/json"));
                    getHeaders.put("host", list(uri.getHost()));
                    Map<String, List<String>> signedGet = castSignedHeaders(signer.signRequest(uri, "GET", getHeaders, null));
                    Builder getB = HttpRequest.newBuilder(uri).GET().timeout(Duration.ofSeconds(30L));
                    applyHeaders(getB, getHeaders);
                    applyHeaders(getB, signedGet);
                    HttpRequest getReq = getB.build();
                    HttpResponse<String> getResp = http.send(getReq, BodyHandlers.ofString());
                    int gs = getResp.statusCode();
                    if (gs == 200) {
                        ifMatch = getResp.headers().firstValue("etag").orElse(null);
                    } else if (gs != 404) {
                        throw new OciException("读取桶策略失败: HTTP " + gs + " " + truncate(getResp.body()));
                    }

                    byte[] bodyBytes = MAPPER.writeValueAsBytes(Map.of("policy", policy));
                    Map<String, List<String>> putHeaders = new LinkedHashMap<>();
                    putHeaders.put("accept", list("application/json"));
                    putHeaders.put("content-type", list("application/json"));
                    putHeaders.put("host", list(uri.getHost()));
                    if (ifMatch != null && !ifMatch.isBlank()) {
                        putHeaders.put("if-match", list(ifMatch));
                    } else {
                        putHeaders.put("if-none-match", list("*"));
                    }

                    Map<String, List<String>> signedPut = castSignedHeaders(signer.signRequest(uri, "PUT", putHeaders, bodyBytes));
                    Builder putB = HttpRequest.newBuilder(uri).PUT(BodyPublishers.ofByteArray(bodyBytes)).timeout(Duration.ofSeconds(60L));
                    applyHeaders(putB, putHeaders);
                    applyHeaders(putB, signedPut);
                    HttpRequest putReq = putB.build();
                    HttpResponse<String> putResp = http.send(putReq, BodyHandlers.ofString());
                    if (putResp.statusCode() / 100 != 2) {
                        throw new OciException("保存桶策略失败: HTTP " + putResp.statusCode() + " " + truncate(putResp.body()));
                    }
                } catch (OciException var23) {
                    throw var23;
                } catch (InterruptedException var24) {
                    Thread.currentThread().interrupt();
                    throw new OciException("保存桶策略失败: " + var24.getMessage());
                } catch (IOException var25) {
                    throw new OciException("保存桶策略失败: " + var25.getMessage());
                }
            } else {
                throw new OciException("Object Storage endpoint 为空");
            }
        }
    }

    private static List<String> list(String v) {
        List<String> l = new ArrayList<>(1);
        l.add(v);
        return l;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (!(signed instanceof Map<?, ?> raw)) {
            throw new OciException("签名结果格式异常");
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
            return s.length() > 500 ? s.substring(0, 500) + "…" : s;
        }
    }
}
