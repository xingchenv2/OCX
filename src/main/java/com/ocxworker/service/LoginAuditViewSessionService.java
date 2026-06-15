package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.util.CommonUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoginAuditViewSessionService {
    private static final long TTL_MS = 1800000L;
    private final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();

    public String issue() {
        String id = CommonUtils.generateId();
        this.sessions.put(id, System.currentTimeMillis() + 1800000L);
        return id;
    }

    public boolean isValid(String id) {
        if (StrUtil.isBlank(id)) {
            return false;
        } else {
            Long exp = this.sessions.get(id);
            if (exp == null) {
                return false;
            } else if (System.currentTimeMillis() > exp) {
                this.sessions.remove(id);
                return false;
            } else {
                return true;
            }
        }
    }
}
