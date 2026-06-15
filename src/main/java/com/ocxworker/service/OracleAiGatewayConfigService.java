/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.ocxworker.mapper.OciKvMapper
 *  com.ocxworker.model.entity.OciKv
 *  com.ocxworker.service.OracleAiGatewayConfigService
 *  com.ocxworker.util.CommonUtils
 *  jakarta.annotation.Resource
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class OracleAiGatewayConfigService {
    private static final String TYPE = "sys_config";
    private static final String CODE_DEFAULT_MAX_TOKENS = "oracle_ai_default_max_tokens";
    private static final long CACHE_TTL_MS = 2000L;
    public static final int FALLBACK_DEFAULT_MAX_TOKENS = 2048;
    @Resource
    private OciKvMapper kvMapper;
    private volatile Integer cachedDefaultMaxTokens = null;
    private final AtomicLong cachedAtMs = new AtomicLong(0L);

    public int getDefaultMaxTokens() {
        long now = System.currentTimeMillis();
        Integer c = this.cachedDefaultMaxTokens;
        if (c != null && now - this.cachedAtMs.get() < 2000L) {
            return c;
        }
        OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)"oracle_ai_default_max_tokens")).eq(OciKv::getType, (Object)"sys_config"));
        int value = OracleAiGatewayConfigService.parseDefaultMaxTokens((String)(kv == null ? null : kv.getValue()));
        this.cachedDefaultMaxTokens = value;
        this.cachedAtMs.set(now);
        return value;
    }

    public int setDefaultMaxTokens(int value) {
        int normalized = OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)value);
        OciKv existing = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)"oracle_ai_default_max_tokens")).eq(OciKv::getType, (Object)"sys_config"));
        if (existing != null) {
            existing.setValue(String.valueOf(normalized));
            this.kvMapper.updateById((Object)existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode("oracle_ai_default_max_tokens");
            kv.setType("sys_config");
            kv.setValue(String.valueOf(normalized));
            kv.setCreateTime(LocalDateTime.now());
            this.kvMapper.insert((Object)kv);
        }
        this.cachedDefaultMaxTokens = normalized;
        this.cachedAtMs.set(System.currentTimeMillis());
        return normalized;
    }

    public static int normalizeDefaultMaxTokens(int value) {
        if (value < 1) {
            return 1;
        }
        return Math.min(value, 200000);
    }

    private static int parseDefaultMaxTokens(String raw) {
        if (raw == null || raw.isBlank()) {
            return 2048;
        }
        try {
            return OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)Integer.parseInt(raw.trim()));
        }
        catch (Exception ignored) {
            return 2048;
        }
    }
}

