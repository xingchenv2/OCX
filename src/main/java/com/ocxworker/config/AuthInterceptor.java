package com.ocxworker.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Value("${web.account}")
    private String defaultAccount;
    @Value("${web.password}")
    private String defaultPassword;
    @Resource
    private OciKvMapper kvMapper;
    @Resource
    private LoginSecurityService loginSecurityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getKv(String code) {
        try {
            OciKv kv = (OciKv)this.kvMapper
                .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, code)).eq(OciKv::getType, "sys_config"));
            return kv != null ? kv.getValue() : null;
        } catch (Exception var3) {
            return null;
        }
    }

    private String getEffectiveAccount() {
        String stored = this.getKv("web_account");
        return stored != null ? stored : this.defaultAccount;
    }

    private boolean isHashedPassword(String pwd) {
        return pwd != null && pwd.length() == 64 && pwd.matches("[0-9a-f]+");
    }

    private String getEffectivePasswordHash() {
        String stored = this.getKv("web_password");
        if (stored != null) {
            return this.isHashedPassword(stored) ? stored : DigestUtil.sha256Hex(stored);
        } else {
            return DigestUtil.sha256Hex(this.defaultPassword);
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (this.loginSecurityService.isSitePaused() && !this.loginSecurityService.isExemptFromSitePause(uri)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(503);
            response.getWriter()
                .write(this.objectMapper.writeValueAsString(ResponseData.error(503, "站点已暂停访问。请通过 Telegram 中的「恢复全站访问」或修改数据库配置项 site_access_paused 后重试。")));
            return false;
        } else {
            if (this.loginSecurityService.isLoginHardenedPath(uri)) {
                String ip = HttpRequestUtil.getClientIp(request);
                String did = this.loginSecurityService.readDeviceIdFromRequest(request);
                if (this.loginSecurityService.isDeniedForLogin(ip, did)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write(this.objectMapper.writeValueAsString(ResponseData.error(403, "访问被拒绝")));
                    return false;
                }
            }

            if (!uri.startsWith("/api/auth/login")
                && !uri.startsWith("/api/auth/needSetup")
                && !uri.startsWith("/api/auth/setup")
                && !uri.startsWith("/api/auth/tgLogin")
                && !uri.startsWith("/api/auth/tgLoginAvailable")
                && !uri.startsWith("/api/auth/device")
                && !uri.startsWith("/ws/")
                && !uri.equals("/")
                && !uri.startsWith("/assets/")
                && !uri.endsWith(".html")
                && !uri.endsWith(".js")
                && !uri.endsWith(".css")
                && !uri.endsWith(".ico")
                && !uri.startsWith("/ip-info")) {
                String token = request.getHeader("Authorization");
                if (StrUtil.isBlank(token)) {
                    token = request.getParameter("token");
                }

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7).trim();
                }

                String effectiveAccount = this.getEffectiveAccount();
                String effectivePwdHash = this.getEffectivePasswordHash();
                if (!StrUtil.isBlank(token) && CommonUtils.validateToken(token, effectiveAccount, effectivePwdHash)) {
                    String clientIp = HttpRequestUtil.getClientIp(request);
                    String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
                    if (this.loginSecurityService.isDeniedForLogin(clientIp, deviceId)) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(403);
                        response.getWriter().write(this.objectMapper.writeValueAsString(ResponseData.error(403, "访问被拒绝")));
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write(this.objectMapper.writeValueAsString(ResponseData.error(401, "Unauthorized")));
                    return false;
                }
            } else {
                return true;
            }
        }
    }
}
