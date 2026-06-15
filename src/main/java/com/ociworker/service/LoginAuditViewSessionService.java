/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.service.LoginAuditViewSessionService
 *  com.ociworker.util.CommonUtils
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.util.StrUtil;
import com.ociworker.util.CommonUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginAuditViewSessionService {
    private static final long TTL_MS = 1800000L;
    private final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap();

    public String issue() {
        String id = CommonUtils.generateId();
        this.sessions.put(id, System.currentTimeMillis() + 1800000L);
        return id;
    }

    public boolean isValid(String id) {
        if (StrUtil.isBlank((CharSequence)id)) {
            return false;
        }
        Long exp = (Long)this.sessions.get(id);
        if (exp == null) {
            return false;
        }
        if (System.currentTimeMillis() > exp) {
            this.sessions.remove(id);
            return false;
        }
        return true;
    }
}

