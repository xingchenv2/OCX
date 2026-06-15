/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.webssh.WebSshUploadRegistry
 *  org.springframework.stereotype.Component
 */
package com.ociworker.webssh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class WebSshUploadRegistry {
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap();

    public void track(String id) {
        if (id != null && !id.isBlank()) {
            this.counters.put(id, new AtomicInteger(0));
        }
    }

    public void add(String id, int bytes) {
        if (id == null) {
            return;
        }
        AtomicInteger c = (AtomicInteger)this.counters.get(id);
        if (c != null) {
            c.addAndGet(bytes);
        }
    }

    public int getAndRemove(String id) {
        AtomicInteger c = (AtomicInteger)this.counters.remove(id);
        return c != null ? c.get() : -1;
    }

    public Integer peek(String id) {
        AtomicInteger c = (AtomicInteger)this.counters.get(id);
        return c != null ? Integer.valueOf(c.get()) : null;
    }

    public void remove(String id) {
        if (id != null) {
            this.counters.remove(id);
        }
    }
}

