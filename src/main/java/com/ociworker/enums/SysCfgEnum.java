/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.enums.SysCfgEnum
 *  lombok.Generated
 */
package com.ociworker.enums;

import lombok.Generated;

public enum SysCfgEnum {
    TG_BOT_TOKEN("tg_bot_token", "sys"),
    TG_CHAT_ID("tg_chat_id", "sys"),
    TG_NOTIFY_TYPES("tg_notify_types", "sys"),
    TG_DAILY_REPORT_TIME("tg_daily_report_time", "sys"),
    TG_UPDATES_NEXT_OFFSET("tg_updates_next_offset", "sys"),
    TG_ROLLBACK_OLD_BOT_TOKEN("tg_rollback_old_bot_token", "sys"),
    TG_ROLLBACK_OLD_CHAT_ID("tg_rollback_old_chat_id", "sys"),
    TG_ROLLBACK_EXPIRE_AT("tg_rollback_expire_at", "sys"),
    TG_ROLLBACK_SESSION_ID("tg_rollback_session_id", "sys"),
    TG_ROLLBACK_UPDATES_OFFSET("tg_rollback_updates_offset", "sys"),
    OCI_PROXY_ENABLED("oci_proxy_enabled", "sys"),
    OCI_PROXY_TYPE("oci_proxy_type", "sys"),
    OCI_PROXY_HOST("oci_proxy_host", "sys"),
    OCI_PROXY_PORT("oci_proxy_port", "sys"),
    OCI_PROXY_USER("oci_proxy_user", "sys"),
    OCI_PROXY_PASS("oci_proxy_pass", "sys"),
    OCI_PROXY_FULL_URL("oci_proxy_full_url", "sys"),
    CF_API_TOKEN("cf_api_token", "sys"),
    CF_ACCOUNT_ID("cf_account_id", "sys"),
    MFA_SECRET("mfa_secret", "sys"),
    MFA_ENABLED("mfa_enabled", "sys"),
    LOGIN_IP_DENYLIST("login_ip_denylist", "sys"),
    LOGIN_DEVICE_DENYLIST("login_device_denylist", "sys"),
    SITE_ACCESS_PAUSED("site_access_paused", "sys"),
    ALIDNS_ACCESS_KEY_ID("alidns_access_key_id", "sys"),
    ALIDNS_ACCESS_KEY_SECRET("alidns_access_key_secret", "sys");

    private final String code;
    private final String type;

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    private SysCfgEnum(String code, String type) {
        this.code = code;
        this.type = type;
    }
}

