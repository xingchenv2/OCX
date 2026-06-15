package com.ocxworker.webssh;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WebSshResponse {
    private String duration;
    private Object data;
    private String msg = "success";

    private WebSshResponse() {
    }

    public static Map<String, Object> ok(Object data) {
        return body("success", data, null);
    }

    public static Map<String, Object> ok() {
        return body("success", null, null);
    }

    public static Map<String, Object> fail(String msg) {
        return body(msg, null, null);
    }

    public static Map<String, Object> body(String msg, Object data, String duration) {
        Map<String, Object> m = new LinkedHashMap<>();
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
