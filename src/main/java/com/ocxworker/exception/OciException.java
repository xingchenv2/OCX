/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  lombok.Generated
 */
package com.ocxworker.exception;

import lombok.Generated;

public class OciException
extends RuntimeException {
    private final int code;

    public OciException(int code, String message) {
        super(message);
        this.code = code;
    }

    public OciException(String message) {
        this(-1, message);
    }

    @Generated
    public int getCode() {
        return this.code;
    }
}

