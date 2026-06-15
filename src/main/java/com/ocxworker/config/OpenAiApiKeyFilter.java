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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(-2147483646)
public class OpenAiApiKeyFilter extends OncePerRequestFilter {
    @Resource
    private OciOpenaiKeyService openaiKeyService;
    @Resource
    private OciUserMapper ociUserMapper;
    @Resource
    private OracleAiPortBindingService portBindingService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path == null) {
            path = "";
        }

        String ctx = request.getContextPath() == null ? "" : request.getContextPath();
        if (ctx.length() > 0 && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        if (path == null || !path.startsWith("/v1")) {
            filterChain.doFilter(request, response);
        } else if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
        } else {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
                String token = auth.substring(7).trim();
                if (token.isEmpty()) {
                    writeError(response, 401, "invalid_request_error", "Bearer token 为空", "auth_empty");
                } else {
                    OciOpenaiKey key = this.openaiKeyService.findByPlainKey(token);
                    if (key == null) {
                        writeError(response, 401, "invalid_request_error", "API Key 无效", "invalid_api_key");
                    } else if (key.getDisabled() != null && key.getDisabled() == 1) {
                        writeError(response, 403, "permission_error", "API Key 已禁用", "key_disabled");
                    } else {
                        int localPort = request.getLocalPort();
                        OciOpenaiPortBinding binding = null;
                        String tenantId = key.getOciUserId();
                        if (DynamicOpenAiPortService.isManagedPort(localPort)) {
                            binding = this.portBindingService.getByPort(localPort);
                            if (binding == null) {
                                writeError(response, 404, "invalid_request_error", "中转端口未绑定", "unknown_channel");
                                return;
                            }

                            if (binding.getEnabled() == null || binding.getEnabled() != 1) {
                                writeError(response, 403, "permission_error", "中转端口已禁用", "channel_disabled");
                                return;
                            }

                            if (!key.getId().equals(binding.getOpenaiKeyId())) {
                                writeError(response, 403, "permission_error", "API Key 不属于该中转端口", "key_not_allowed_for_channel");
                                return;
                            }

                            tenantId = binding.getOciUserId();
                        }

                        OciUser u = (OciUser)this.ociUserMapper.selectById(tenantId);
                        if (u == null) {
                            writeError(response, 403, "invalid_request_error", "绑定的租户已删除", "tenant_gone");
                        } else {
                            request.setAttribute("ociworker.openai.ociUserId", u.getId());
                            if (binding != null && binding.getOciRegion() != null && !binding.getOciRegion().isBlank()) {
                                request.setAttribute("ociworker.openai.ociRegion", binding.getOciRegion().trim());
                            }

                            request.setAttribute("ociworker.openai.keyId", key.getId());
                            if (binding != null) {
                                request.setAttribute("ociworker.openai.portBindingId", binding.getId());
                                if (binding.getDefaultMaxTokens() != null && binding.getDefaultMaxTokens() > 0) {
                                    request.setAttribute("ociworker.openai.defaultMaxTokens", binding.getDefaultMaxTokens());
                                }

                                if (binding.getAllowedModelsJson() != null && !binding.getAllowedModelsJson().isBlank()) {
                                    request.setAttribute("ociworker.openai.allowedModelsJson", binding.getAllowedModelsJson());
                                }
                            }

                            try {
                                this.openaiKeyService.updateLastUsed(key.getId());
                                if (binding != null) {
                                    this.portBindingService.touchLastUsed(binding.getId());
                                }
                            } catch (Exception var14) {
                            }

                            filterChain.doFilter(request, response);
                        }
                    }
                }
            } else {
                writeError(response, 401, "invalid_request_error", "请使用 Authorization: Bearer <api_key>", "auth_missing");
            }
        }
    }

    private static void writeError(HttpServletResponse r, int status, String type, String message, String code) throws IOException {
        r.setStatus(status);
        r.setContentType("application/json; charset=utf-8");
        String j = String.format("{\"error\":{\"type\":\"%s\",\"code\":\"%s\",\"message\":\"%s\"}}", escapeJson(type), escapeJson(code), escapeJson(message));
        r.getOutputStream().write(j.getBytes(StandardCharsets.UTF_8));
    }

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
