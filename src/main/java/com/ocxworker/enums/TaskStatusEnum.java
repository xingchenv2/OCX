/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.enums.TaskStatusEnum
 *  lombok.Generated
 */
package com.ocxworker.enums;

import lombok.Generated;

public enum TaskStatusEnum {
    RUNNING("RUNNING"),
    STOPPED("STOPPED"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private final String status;

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    private TaskStatusEnum(String status) {
        this.status = status;
    }
}

