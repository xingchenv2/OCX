package com.ocxworker.config;

import com.ocxworker.controller.AuthController;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig — Security-hardened version.
 *
 * Key fixes:
 *   1. CORS restricted to same-origin by default (removed allowedOriginPatterns("*") + allowCredentials = CSRF).
 *      Override via property cors.allowed-origins if remote access needed.
 *   2. /api/auth/login excluded from interceptor (still public for login flow).
 *   3. /api/auth/setup excluded only if setup is not yet done (handled in AuthController).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Value("${cors.allowed-origins:}")
    private String allowedOriginsConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // FIX: Removed allowedOriginPatterns("*") + allowCredentials(true) which enabled CSRF attacks.
        // By default, only same-origin requests are allowed.
        // To allow specific origins, set cors.allowed-origins in application.yml.
        var mapping = registry.addMapping("/api/**")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type")
            .allowCredentials(true)
            .maxAge(3600L);

        // Parse allowed origins from config
        if (allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()) {
            String[] origins = allowedOriginsConfig.split(",");
            for (String origin : origins) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty()) {
                    mapping.allowedOrigins(trimmed);
                }
            }
        } else {
            // Default: only allow same-origin (no explicit allowed origins = stricter)
            // Spring will default to same-origin when no allowed origins configured
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor((HandlerInterceptor) this.authInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/login",
                "/api/auth/needSetup",
                "/api/auth/setup",
                "/api/auth/tgLogin",
                "/api/auth/tgLoginAvailable",
                "/api/auth/device"
            );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webssh/**")
            .addResourceLocations("classpath:/static/webssh/");
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/dist/");
    }
}
