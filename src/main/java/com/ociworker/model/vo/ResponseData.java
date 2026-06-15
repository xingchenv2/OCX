/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.model.vo.ResponseData
 *  lombok.Generated
 */
package com.ociworker.model.vo;

import lombok.Generated;

public class ResponseData<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData(0, "success", data);
    }

    public static <T> ResponseData<T> ok() {
        return new ResponseData(0, "success", null);
    }

    public static <T> ResponseData<T> error(String message) {
        return new ResponseData(-1, message, null);
    }

    public static <T> ResponseData<T> error(int code, String message) {
        return new ResponseData(code, message, null);
    }

    @Generated
    public int getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public T getData() {
        return (T)this.data;
    }

    @Generated
    public void setCode(int code) {
        this.code = code;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public void setData(T data) {
        this.data = data;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ResponseData)) {
            return false;
        }
        ResponseData other = (ResponseData)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getCode() != other.getCode()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        Object this$data = this.getData();
        Object other$data = other.getData();
        return !(this$data == null ? other$data != null : !this$data.equals(other$data));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ResponseData;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCode();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ResponseData(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + String.valueOf(this.getData()) + ")";
    }

    @Generated
    public ResponseData() {
    }

    @Generated
    public ResponseData(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}

