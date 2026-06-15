/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ociworker.exception.OciException
 *  com.ociworker.util.ObjectStorageBucketPolicyHttp
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.http.internal.ParamEncoder
 *  com.oracle.bmc.http.signing.DefaultRequestSigner
 *  com.oracle.bmc.http.signing.RequestSigner
 *  com.oracle.bmc.objectstorage.ObjectStorageClient
 */
package com.ociworker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ociworker.exception.OciException;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Exception performing whole class analysis ignored.
 */
public final class ObjectStorageBucketPolicyHttp {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ObjectStorageBucketPolicyHttp() {
    }

    public static void putBucketPolicy(HttpClient http, ObjectStorageClient objectStorageClient, BasicAuthenticationDetailsProvider authProvider, String namespace, String bucketName, String policy) {
        if (namespace == null || namespace.isBlank() || bucketName == null || bucketName.isBlank()) {
            throw new OciException("namespace / bucketName \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (policy == null) {
            throw new OciException("policy \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String endpoint = objectStorageClient.getEndpoint();
        if (endpoint == null || endpoint.isBlank()) {
            throw new OciException("Object Storage endpoint \u4e3a\u7a7a");
        }
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        String path = "/20160918/n/" + ParamEncoder.encodePathParam((String)namespace) + "/b/" + ParamEncoder.encodePathParam((String)bucketName) + "/policy";
        URI uri = URI.create(endpoint + path);
        RequestSigner signer = DefaultRequestSigner.createRequestSigner((BasicAuthenticationDetailsProvider)authProvider);
        String ifMatch = null;
        try {
            LinkedHashMap<String, List> getHeaders = new LinkedHashMap<String, List>();
            getHeaders.put("accept", ObjectStorageBucketPolicyHttp.list((String)"application/json"));
            getHeaders.put("host", ObjectStorageBucketPolicyHttp.list((String)uri.getHost()));
            Map signedGet = ObjectStorageBucketPolicyHttp.castSignedHeaders((Object)signer.signRequest(uri, "GET", getHeaders, null));
            HttpRequest.Builder getB = HttpRequest.newBuilder(uri).GET().timeout(Duration.ofSeconds(30L));
            ObjectStorageBucketPolicyHttp.applyHeaders((HttpRequest.Builder)getB, getHeaders);
            ObjectStorageBucketPolicyHttp.applyHeaders((HttpRequest.Builder)getB, (Map)signedGet);
            HttpRequest getReq = getB.build();
            HttpResponse<String> getResp = http.send(getReq, HttpResponse.BodyHandlers.ofString());
            int gs = getResp.statusCode();
            if (gs == 200) {
                ifMatch = getResp.headers().firstValue("etag").orElse(null);
            } else if (gs != 404) {
                throw new OciException("\u8bfb\u53d6\u6876\u7b56\u7565\u5931\u8d25: HTTP " + gs + " " + ObjectStorageBucketPolicyHttp.truncate((String)getResp.body()));
            }
            byte[] bodyBytes = MAPPER.writeValueAsBytes(Map.of("policy", policy));
            LinkedHashMap<String, List> putHeaders = new LinkedHashMap<String, List>();
            putHeaders.put("accept", ObjectStorageBucketPolicyHttp.list((String)"application/json"));
            putHeaders.put("content-type", ObjectStorageBucketPolicyHttp.list((String)"application/json"));
            putHeaders.put("host", ObjectStorageBucketPolicyHttp.list((String)uri.getHost()));
            if (ifMatch != null && !ifMatch.isBlank()) {
                putHeaders.put("if-match", ObjectStorageBucketPolicyHttp.list((String)ifMatch));
            } else {
                putHeaders.put("if-none-match", ObjectStorageBucketPolicyHttp.list((String)"*"));
            }
            Map signedPut = ObjectStorageBucketPolicyHttp.castSignedHeaders((Object)signer.signRequest(uri, "PUT", putHeaders, (Object)bodyBytes));
            HttpRequest.Builder putB = HttpRequest.newBuilder(uri).PUT(HttpRequest.BodyPublishers.ofByteArray(bodyBytes)).timeout(Duration.ofSeconds(60L));
            ObjectStorageBucketPolicyHttp.applyHeaders((HttpRequest.Builder)putB, putHeaders);
            ObjectStorageBucketPolicyHttp.applyHeaders((HttpRequest.Builder)putB, (Map)signedPut);
            HttpRequest putReq = putB.build();
            HttpResponse<String> putResp = http.send(putReq, HttpResponse.BodyHandlers.ofString());
            if (putResp.statusCode() / 100 != 2) {
                throw new OciException("\u4fdd\u5b58\u6876\u7b56\u7565\u5931\u8d25: HTTP " + putResp.statusCode() + " " + ObjectStorageBucketPolicyHttp.truncate((String)putResp.body()));
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OciException("\u4fdd\u5b58\u6876\u7b56\u7565\u5931\u8d25: " + e.getMessage());
        }
        catch (IOException e) {
            throw new OciException("\u4fdd\u5b58\u6876\u7b56\u7565\u5931\u8d25: " + e.getMessage());
        }
    }

    private static List<String> list(String v) {
        ArrayList<String> l = new ArrayList<String>(1);
        l.add(v);
        return l;
    }

    private static Map<String, List<String>> castSignedHeaders(Object signed) {
        if (!(signed instanceof Map)) {
            throw new OciException("\u7b7e\u540d\u7ed3\u679c\u683c\u5f0f\u5f02\u5e38");
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
            out.put(key, ObjectStorageBucketPolicyHttp.list((String)String.valueOf(v)));
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
        return s.length() > 500 ? s.substring(0, 500) + "\u2026" : s;
    }
}

