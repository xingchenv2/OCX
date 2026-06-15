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
        } else {
            OciKv kv = (OciKv)this.kvMapper
                .selectOne(
                    (Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai_default_max_tokens")).eq(OciKv::getType, "sys_config")
                );
            int value = parseDefaultMaxTokens(kv == null ? null : kv.getValue());
            this.cachedDefaultMaxTokens = value;
            this.cachedAtMs.set(now);
            return value;
        }
    }

    public int setDefaultMaxTokens(int value) {
        int normalized = normalizeDefaultMaxTokens(value);
        OciKv existing = (OciKv)this.kvMapper
            .selectOne(
                (Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai_default_max_tokens")).eq(OciKv::getType, "sys_config")
            );
        if (existing != null) {
            existing.setValue(String.valueOf(normalized));
            this.kvMapper.updateById(existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode("oracle_ai_default_max_tokens");
            kv.setType("sys_config");
            kv.setValue(String.valueOf(normalized));
            kv.setCreateTime(LocalDateTime.now());
            this.kvMapper.insert(kv);
        }

        this.cachedDefaultMaxTokens = normalized;
        this.cachedAtMs.set(System.currentTimeMillis());
        return normalized;
    }

    public static int normalizeDefaultMaxTokens(int value) {
        return value < 1 ? 1 : Math.min(value, 200000);
    }

    private static int parseDefaultMaxTokens(String raw) {
        if (raw != null && !raw.isBlank()) {
            try {
                return normalizeDefaultMaxTokens(Integer.parseInt(raw.trim()));
            } catch (Exception var2) {
                return 2048;
            }
        } else {
            return 2048;
        }
    }
}
