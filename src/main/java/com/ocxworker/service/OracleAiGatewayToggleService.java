/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.ocxworker.mapper.OciKvMapper
 *  com.ocxworker.model.entity.OciKv
 *  com.ocxworker.service.OracleAiGatewayToggleService
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
        }
        OciKv kv = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)CODE)).eq(OciKv::getType, (Object)TYPE));
        boolean enabled = kv == null || kv.getValue() == null || !"false".equalsIgnoreCase(kv.getValue().trim());
        this.cachedEnabled = enabled;
        this.cachedAtMs.set(now);
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        String val;
        OciKv existing = (OciKv)this.kvMapper.selectOne((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciKv::getCode, (Object)CODE)).eq(OciKv::getType, (Object)TYPE));
        String string = val = enabled ? "true" : "false";
        if (existing != null) {
            existing.setValue(val);
            this.kvMapper.updateById((Object)existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode(CODE);
            kv.setType(TYPE);
            kv.setValue(val);
            kv.setCreateTime(LocalDateTime.now());
            this.kvMapper.insert((Object)kv);
        }
        this.cachedEnabled = enabled;
        this.cachedAtMs.set(System.currentTimeMillis());
    }
}

