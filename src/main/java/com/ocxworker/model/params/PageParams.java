/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.model.params.PageParams
 *  lombok.Generated
 */
package com.ocxworker.model.params;

import lombok.Generated;

public class PageParams {
    private int current = 1;
    private int size = 10;
    private String keyword;
    private String status;

    @Generated
    public PageParams() {
    }

    @Generated
    public int getCurrent() {
        return this.current;
    }

    @Generated
    public int getSize() {
        return this.size;
    }

    @Generated
    public String getKeyword() {
        return this.keyword;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public void setCurrent(int current) {
        this.current = current;
    }

    @Generated
    public void setSize(int size) {
        this.size = size;
    }

    @Generated
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PageParams)) {
            return false;
        }
        PageParams other = (PageParams)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getCurrent() != other.getCurrent()) {
            return false;
        }
        if (this.getSize() != other.getSize()) {
            return false;
        }
        String this$keyword = this.getKeyword();
        String other$keyword = other.getKeyword();
        if (this$keyword == null ? other$keyword != null : !this$keyword.equals(other$keyword)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !this$status.equals(other$status));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof PageParams;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCurrent();
        result = result * 59 + this.getSize();
        String $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "PageParams(current=" + this.getCurrent() + ", size=" + this.getSize() + ", keyword=" + this.getKeyword() + ", status=" + this.getStatus() + ")";
    }
}

