package com.ocxworker.model.params;

import lombok.Generated;

public class PageParams {
    private int current = 1;
    private int size = 10;
    private String keyword;
    private String status;

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
    public void setCurrent(final int current) {
        this.current = current;
    }

    @Generated
    public void setSize(final int size) {
        this.size = size;
    }

    @Generated
    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    @Generated
    public void setStatus(final String status) {
        this.status = status;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageParams other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.getCurrent() != other.getCurrent()) {
            return false;
        } else if (this.getSize() != other.getSize()) {
            return false;
        } else {
            Object this$keyword = this.getKeyword();
            Object other$keyword = other.getKeyword();
            if (this$keyword == null ? other$keyword == null : this$keyword.equals(other$keyword)) {
                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                return this$status == null ? other$status == null : this$status.equals(other$status);
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof PageParams;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCurrent();
        result = result * 59 + this.getSize();
        Object $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        Object $status = this.getStatus();
        return result * 59 + ($status == null ? 43 : $status.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "PageParams(current=" + this.getCurrent() + ", size=" + this.getSize() + ", keyword=" + this.getKeyword() + ", status=" + this.getStatus() + ")";
    }
}
