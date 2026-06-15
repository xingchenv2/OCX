package com.ocxworker.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(new String[]{"*"})
            .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"})
            .allowedHeaders(new String[]{"*"})
            .allowCredentials(true)
            .maxAge(3600L);
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.authInterceptor).addPathPatterns(new String[]{"/api/**"}).excludePathPatterns(new String[]{"/api/auth/login"});
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/webssh/**"}).addResourceLocations(new String[]{"classpath:/static/webssh/"});
        registry.addResourceHandler(new String[]{"/**"}).addResourceLocations(new String[]{"classpath:/dist/"});
    }
}
