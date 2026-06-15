-- 登录审计（保留 7 天由应用定时清理）；升级已有库时执行一次。
CREATE TABLE IF NOT EXISTS oci_login_audit (
    id VARCHAR(64) PRIMARY KEY,
    account VARCHAR(128) DEFAULT NULL,
    password_attempt VARCHAR(512) DEFAULT NULL,
    ip VARCHAR(255) DEFAULT NULL,
    success TINYINT(1) NOT NULL DEFAULT 0,
    device_id VARCHAR(128) DEFAULT NULL,
    os_name VARCHAR(128) DEFAULT NULL,
    browser_name VARCHAR(128) DEFAULT NULL,
    login_channel VARCHAR(32) DEFAULT 'password',
    user_agent TEXT,
    login_detail MEDIUMTEXT NULL COMMENT 'JSON: 访问入口、网络与链路、客户端与能力',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_oci_login_audit_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
