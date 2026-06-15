/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.OpenAiApiKeyFilter
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.entity.OciOpenaiKey
 *  com.ocxworker.model.entity.OciOpenaiPortBinding
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.DynamicOpenAiPortService
 *  com.ocxworker.service.OciOpenaiKeyService
 *  com.ocxworker.service.OracleAiPortBindingService
 *  jakarta.annotation.Resource
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.ocxworker.config;

import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciOpenaiKey;
import com.ocxworker.model.entity.OciOpenaiPortBinding;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.DynamicOpenAiPortService;
import com.ocxworker.service.OciOpenaiKeyService;
import com.ocxworker.service.OracleAiPortBindingService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
@Order(value=-2147483646)
public class OpenAiApiKeyFilter
extends OncePerRequestFilter {
    @Resource
    private OciOpenaiKeyService openaiKeyService;
    @Resource
    private OciUserMapper ociUserMapper;
    @Resource
    private OracleAiPortBindingService portBindingService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        OciUser u;
        String ctx;
        String path = request.getRequestURI();
        if (path == null) {
            path = "";
        }
        String string = ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (ctx.length() > 0 && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        if (path == null || !path.startsWith("/v1")) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.toLowerCase().startsWith("bearer ")) {
            OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)401, (String)"invalid_request_error", (String)"\u8bf7\u4f7f\u7528 Authorization: Bearer <api_key>", (String)"auth_missing");
            return;
        }
        String token = auth.substring(7).trim();
        if (token.isEmpty()) {
            OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)401, (String)"invalid_request_error", (String)"Bearer token \u4e3a\u7a7a", (String)"auth_empty");
            return;
        }
        OciOpenaiKey key = this.openaiKeyService.findByPlainKey(token);
        if (key == null) {
            OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)401, (String)"invalid_request_error", (String)"API Key \u65e0\u6548", (String)"invalid_api_key");
            return;
        }
        if (key.getDisabled() != null && key.getDisabled() == 1) {
            OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)403, (String)"permission_error", (String)"API Key \u5df2\u7981\u7528", (String)"key_disabled");
            return;
        }
        int localPort = request.getLocalPort();
        OciOpenaiPortBinding binding = null;
        String tenantId = key.getOciUserId();
        if (DynamicOpenAiPortService.isManagedPort((int)localPort)) {
            binding = this.portBindingService.getByPort(localPort);
            if (binding == null) {
                OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)404, (String)"invalid_request_error", (String)"\u4e2d\u8f6c\u7aef\u53e3\u672a\u7ed1\u5b9a", (String)"unknown_channel");
                return;
            }
            if (binding.getEnabled() == null || binding.getEnabled() != 1) {
                OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)403, (String)"permission_error", (String)"\u4e2d\u8f6c\u7aef\u53e3\u5df2\u7981\u7528", (String)"channel_disabled");
                return;
            }
            if (!key.getId().equals(binding.getOpenaiKeyId())) {
                OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)403, (String)"permission_error", (String)"API Key \u4e0d\u5c5e\u4e8e\u8be5\u4e2d\u8f6c\u7aef\u53e3", (String)"key_not_allowed_for_channel");
                return;
            }
            tenantId = binding.getOciUserId();
        }
        if ((u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)tenantId))) == null) {
            OpenAiApiKeyFilter.writeError((HttpServletResponse)response, (int)403, (String)"invalid_request_error", (String)"\u7ed1\u5b9a\u7684\u79df\u6237\u5df2\u5220\u9664", (String)"tenant_gone");
            return;
        }
        request.setAttribute("ocxworker.openai.ociUserId", (Object)u.getId());
        if (binding != null && binding.getOciRegion() != null && !binding.getOciRegion().isBlank()) {
            request.setAttribute("ocxworker.openai.ociRegion", (Object)binding.getOciRegion().trim());
        }
        request.setAttribute("ocxworker.openai.keyId", (Object)key.getId());
        if (binding != null) {
            request.setAttribute("ocxworker.openai.portBindingId", (Object)binding.getId());
            if (binding.getDefaultMaxTokens() != null && binding.getDefaultMaxTokens() > 0) {
                request.setAttribute("ocxworker.openai.defaultMaxTokens", (Object)binding.getDefaultMaxTokens());
            }
            if (binding.getAllowedModelsJson() != null && !binding.getAllowedModelsJson().isBlank()) {
                request.setAttribute("ocxworker.openai.allowedModelsJson", (Object)binding.getAllowedModelsJson());
            }
        }
        try {
            this.openaiKeyService.updateLastUsed(key.getId());
            if (binding != null) {
                this.portBindingService.touchLastUsed(binding.getId());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private static void writeError(HttpServletResponse r, int status, String type, String message, String code) throws IOException {
        r.setStatus(status);
        r.setContentType("application/json; charset=utf-8");
        String j = String.format("{\"error\":{\"type\":\"%s\",\"code\":\"%s\",\"message\":\"%s\"}}", OpenAiApiKeyFilter.escapeJson((String)type), OpenAiApiKeyFilter.escapeJson((String)code), OpenAiApiKeyFilter.escapeJson((String)message));
        r.getOutputStream().write(j.getBytes(StandardCharsets.UTF_8));
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

