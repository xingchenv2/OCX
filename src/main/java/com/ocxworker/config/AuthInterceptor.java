package com.ocxworker.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.controller.AuthController;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * AuthInterceptor — Security-hardened version.
 *
 * Key fixes vs original:
 *   1. Delegates getEffectiveAccount/getEffectivePasswordHash to AuthController (single source of truth).
 *   2. WebSocket paths /ws/* and /webssh-api/* are NO LONGER exempted from auth.
 *      (The original code let anyone access /ws/log which exposes full backend logs.)
 *   3. Token validation uses the new nonce-based CommonUtils.validateToken.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthController authController;

    @Resource
    private LoginSecurityService loginSecurityService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Paths that do NOT require authentication.
     * FIX: Removed /ws/ and /webssh-api/ — these now require auth tokens.
     */
    private boolean isPublicPath(String uri) {
        if (uri == null) return false;
        return uri.startsWith("/api/auth/login")
            || uri.startsWith("/api/auth/needSetup")
            || uri.startsWith("/api/auth/setup")
            || uri.startsWith("/api/auth/tgLogin")
            || uri.startsWith("/api/auth/tgLoginAvailable")
            || uri.startsWith("/api/auth/device")
            || uri.equals("/")
            || uri.startsWith("/assets/")
            || uri.endsWith(".html")
            || uri.endsWith(".js")
            || uri.endsWith(".css")
            || uri.endsWith(".ico")
            || uri.startsWith("/ip-info");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Site pause check
        if (this.loginSecurityService.isSitePaused() && !this.loginSecurityService.isExemptFromSitePause(uri)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(503);
            response.getWriter().write(this.objectMapper.writeValueAsString(
                ResponseData.error(503, "站点已暂停访问。请通过 Telegram 中的「恢复全站访问」或修改数据库配置项 site_access_paused 后重试。")));
            return false;
        }

        // Deny-list check for login-hardened paths
        if (this.loginSecurityService.isLoginHardenedPath(uri)) {
            String ip = HttpRequestUtil.getClientIp(request);
            String did = this.loginSecurityService.readDeviceIdFromRequest(request);
            if (this.loginSecurityService.isDeniedForLogin(ip, did)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(403);
                response.getWriter().write(this.objectMapper.writeValueAsString(
                    ResponseData.error(403, "访问被拒绝")));
                return false;
            }
        }

        // Public paths skip auth
        if (isPublicPath(uri)) {
            return true;
        }

        // Extract token from header or query param
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank((CharSequence) token)) {
            token = request.getParameter("token");
        }
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        // FIX: Delegate to AuthController for consistent credential resolution
        String effectiveAccount = authController.getEffectiveAccount();
        String effectivePwdHash = authController.getEffectivePasswordHash();

        if (StrUtil.isBlank((CharSequence) token) || !CommonUtils.validateToken(token, effectiveAccount, effectivePwdHash)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(this.objectMapper.writeValueAsString(
                ResponseData.error(401, "Unauthorized")));
            return false;
        }

        // IP/device deny-list check for all authenticated paths
        String clientIp = HttpRequestUtil.getClientIp(request);
        String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
        if (this.loginSecurityService.isDeniedForLogin(clientIp, deviceId)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write(this.objectMapper.writeValueAsString(
                ResponseData.error(403, "访问被拒绝")));
            return false;
        }

        return true;
    }
}
