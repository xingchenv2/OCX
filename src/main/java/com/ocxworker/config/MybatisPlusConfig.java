/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.DbType
 *  com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
 *  com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor
 *  com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
 *  com.ocxworker.config.MybatisPlusConfig
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.ocxworker.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor((InnerInterceptor)new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

