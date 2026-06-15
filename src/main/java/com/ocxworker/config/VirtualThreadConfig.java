/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.config.VirtualThreadConfig
 *  jakarta.annotation.PreDestroy
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.ocxworker.config;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VirtualThreadConfig {
    public static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    @Bean
    public ExecutorService virtualExecutor() {
        return VIRTUAL_EXECUTOR;
    }

    @PreDestroy
    public void shutdown() {
        try {
            VIRTUAL_EXECUTOR.shutdown();
            if (!VIRTUAL_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS)) {
                VIRTUAL_EXECUTOR.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            VIRTUAL_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

