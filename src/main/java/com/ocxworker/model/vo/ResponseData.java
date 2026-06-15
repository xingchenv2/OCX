package com.ocxworker.model.vo;

import lombok.Generated;

public class ResponseData<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData<>(0, "success", data);
    }

    public static <T> ResponseData<T> ok() {
        return new ResponseData<>(0, "success", null);
    }

    public static <T> ResponseData<T> error(String message) {
        return new ResponseData<>(-1, message, null);
    }

    public static <T> ResponseData<T> error(int code, String message) {
        return new ResponseData<>(code, message, null);
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
        return this.data;
    }

    @Generated
    public void setCode(final int code) {
        this.code = code;
    }

    @Generated
    public void setMessage(final String message) {
        this.message = message;
    }

    @Generated
    public void setData(final T data) {
        this.data = data;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResponseData<?> other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.getCode() != other.getCode()) {
            return false;
        } else {
            Object this$message = this.getMessage();
            Object other$message = other.getMessage();
            if (this$message == null ? other$message == null : this$message.equals(other$message)) {
                Object this$data = this.getData();
                Object other$data = other.getData();
                return this$data == null ? other$data == null : this$data.equals(other$data);
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof ResponseData;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCode();
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        return result * 59 + ($data == null ? 43 : $data.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "ResponseData(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }

    @Generated
    public ResponseData() {
    }

    @Generated
    public ResponseData(final int code, final String message, final T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
