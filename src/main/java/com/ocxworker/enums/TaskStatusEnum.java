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
    private TaskStatusEnum(final String status) {
        this.status = status;
    }
}
