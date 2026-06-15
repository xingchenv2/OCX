/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.webssh.WebSshResponse
 */
package com.ocxworker.webssh;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Exception performing whole class analysis ignored.
 */
public final class WebSshResponse {
    private String duration;
    private Object data;
    private String msg = "success";

    private WebSshResponse() {
    }

    public static Map<String, Object> ok(Object data) {
        return WebSshResponse.body((String)"success", (Object)data, null);
    }

    public static Map<String, Object> ok() {
        return WebSshResponse.body((String)"success", null, null);
    }

    public static Map<String, Object> fail(String msg) {
        return WebSshResponse.body((String)msg, null, null);
    }

    public static Map<String, Object> body(String msg, Object data, String duration) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        if (duration != null) {
            m.put("Duration", duration);
        }
        if (data != null) {
            m.put("Data", data);
        }
        m.put("Msg", msg != null ? msg : "success");
        return m;
    }

    public String getDuration() {
        return this.duration;
    }

    public Object getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }
}

