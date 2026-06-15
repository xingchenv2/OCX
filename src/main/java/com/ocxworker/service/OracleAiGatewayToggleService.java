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
public class OracleAiGatewayToggleService {
    private static final String TYPE = "sys_config";
    private static final String CODE = "oracle_ai_openai_proxy_enabled";
    @Resource
    private OciKvMapper kvMapper;
    private volatile Boolean cachedEnabled = null;
    private final AtomicLong cachedAtMs = new AtomicLong(0L);
    private static final long CACHE_TTL_MS = 2000L;

    public boolean isEnabled() {
        long now = System.currentTimeMillis();
        Boolean c = this.cachedEnabled;
        if (c != null && now - this.cachedAtMs.get() < 2000L) {
            return c;
        } else {
            OciKv kv = (OciKv)this.kvMapper
                .selectOne(
                    (Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai_openai_proxy_enabled"))
                        .eq(OciKv::getType, "sys_config")
                );
            boolean enabled = kv == null || kv.getValue() == null || !"false".equalsIgnoreCase(kv.getValue().trim());
            this.cachedEnabled = enabled;
            this.cachedAtMs.set(now);
            return enabled;
        }
    }

    public void setEnabled(boolean enabled) {
        OciKv existing = (OciKv)this.kvMapper
            .selectOne(
                (Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, "oracle_ai_openai_proxy_enabled")).eq(OciKv::getType, "sys_config")
            );
        String val = enabled ? "true" : "false";
        if (existing != null) {
            existing.setValue(val);
            this.kvMapper.updateById(existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode("oracle_ai_openai_proxy_enabled");
            kv.setType("sys_config");
            kv.setValue(val);
            kv.setCreateTime(LocalDateTime.now());
            this.kvMapper.insert(kv);
        }

        this.cachedEnabled = enabled;
        this.cachedAtMs.set(System.currentTimeMillis());
    }
}
