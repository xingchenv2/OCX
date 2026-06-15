package com.ocxworker.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DatabaseGuardService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DatabaseGuardService.class);
    @Resource
    private DataSource dataSource;
    @Resource
    private NotificationService notificationService;
    private static final String BACKUP_DIR = "./db-backups";
    private static final int KEEP_DAYS = 7;
    private static final Map<String, String> TABLE_DDL = new LinkedHashMap<>();

    @PostConstruct
    public void startupCheck() {
        log.info("【数据库守护】启动自检...");
        List<String> missing = new ArrayList<>();
        List<String> repaired = new ArrayList<>();

        try (Connection conn = this.dataSource.getConnection()) {
            Set<String> existing = this.getExistingTables(conn);

            for (Entry<String, String> entry : TABLE_DDL.entrySet()) {
                String table = entry.getKey();
                if (!existing.contains(table)) {
                    missing.add(table);
                    log.warn("【数据库守护】表 {} 不存在，正在自动创建...", table);

                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(entry.getValue());
                    }

                    repaired.add(table);
                    log.info("【数据库守护】表 {} 已自动创建", table);
                }
            }

            this.migrateColumns(conn);
        } catch (Exception var15) {
            log.error("【数据库守护】启动自检失败: {}", var15.getMessage(), var15);
            this.sendAlert("启动自检失败", "数据库连接异常: " + var15.getMessage());
            return;
        }

        if (!missing.isEmpty()) {
            String msg = String.format("检测到 %d 张表缺失: %s\n已自动修复: %s", missing.size(), String.join(", ", missing), String.join(", ", repaired));
            log.warn("【数据库守护】{}", msg);
            this.sendAlert("表缺失已自动修复", msg);
        } else {
            log.info("【数据库守护】所有表正常 ✓");
        }
    }

    @Scheduled(
        cron = "0 0 */6 * * ?"
    )
    public void periodicCheck() {
        log.info("【数据库守护】定时巡检...");
        List<String> problems = new ArrayList<>();

        try (Connection conn = this.dataSource.getConnection()) {
            Set<String> existing = this.getExistingTables(conn);

            for (Entry<String, String> entry : TABLE_DDL.entrySet()) {
                String table = entry.getKey();
                if (!existing.contains(table)) {
                    log.warn("【数据库守护】巡检发现表 {} 缺失，自动修复", table);

                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(entry.getValue());
                    }

                    problems.add(table + "(已修复)");
                }
            }
        } catch (Exception var14) {
            log.error("【数据库守护】巡检异常: {}", var14.getMessage());
            this.sendAlert("巡检异常", "数据库连接失败: " + var14.getMessage());
            return;
        }

        if (!problems.isEmpty()) {
            this.sendAlert("巡检发现异常", "缺失表: " + String.join(", ", problems));
        }
    }

    @Scheduled(
        cron = "0 0 */6 * * ?"
    )
    public void autoBackup() {
        log.info("【数据库守护】开始自动备份...");

        try {
            Path backupDir = Path.of("./db-backups");
            Files.createDirectories(backupDir);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupFile = backupDir.resolve("auto_backup_" + timestamp + ".sql");
            String dump = this.exportAllTables();
            Files.writeString(backupFile, dump);
            long sizeKB = Files.size(backupFile) / 1024L;
            log.info("【数据库守护】自动备份完成: {} ({}KB)", backupFile.getFileName(), sizeKB);
            this.cleanOldBackups(backupDir);
        } catch (Exception var7) {
            log.error("【数据库守护】自动备份失败: {}", var7.getMessage(), var7);
            this.sendAlert("自动备份失败", var7.getMessage());
        }
    }

    private String exportAllTables() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- OCI Worker Auto Backup\n");
        sb.append("-- Generated: ").append(LocalDateTime.now()).append("\n\n");
        sb.append("SET NAMES utf8mb4;\n");
        sb.append("SET FOREIGN_KEY_CHECKS=0;\n\n");

        try (Connection conn = this.dataSource.getConnection()) {
            for (Entry<String, String> entry : TABLE_DDL.entrySet()) {
                String table = entry.getKey();
                sb.append("-- Table structure: ").append(table).append("\n");
                sb.append("DROP TABLE IF EXISTS `").append(table).append("`;\n");
                sb.append(entry.getValue()).append(";\n\n");

                try (
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                ) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();
                    sb.append("-- Data: ").append(table).append("\n");

                    while (rs.next()) {
                        sb.append("INSERT INTO `").append(table).append("` VALUES (");

                        for (int i = 1; i <= colCount; i++) {
                            if (i > 1) {
                                sb.append(", ");
                            }

                            Object val = rs.getObject(i);
                            if (val == null) {
                                sb.append("NULL");
                            } else {
                                sb.append("'").append(val.toString().replace("\\", "\\\\").replace("'", "\\'")).append("'");
                            }
                        }

                        sb.append(");\n");
                    }

                    sb.append("\n");
                } catch (SQLException var17) {
                    sb.append("-- WARN: table ").append(table).append(" export failed: ").append(var17.getMessage()).append("\n\n");
                }
            }
        }

        sb.append("SET FOREIGN_KEY_CHECKS=1;\n");
        return sb.toString();
    }

    private void cleanOldBackups(Path backupDir) throws IOException {
        LocalDate cutoff = LocalDate.now().minusDays(7L);

        try (Stream<Path> files = Files.list(backupDir)) {
            files.filter(p -> p.getFileName().toString().startsWith("auto_backup_"))
                .filter(
                    p -> {
                        try {
                            return Files.getLastModifiedTime(p)
                                .toInstant()
                                .isBefore(cutoff.atStartOfDay().toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                        } catch (IOException var3x) {
                            return false;
                        }
                    }
                )
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                        log.info("【数据库守护】清理过期备份: {}", p.getFileName());
                    } catch (IOException var2x) {
                    }
                });
        }
    }

    private void migrateColumns(Connection conn) {
        this.addColumnIfMissing(conn, "oci_user", "group_level1", "VARCHAR(64) DEFAULT NULL AFTER plan_type");
        this.addColumnIfMissing(conn, "oci_user", "group_level2", "VARCHAR(64) DEFAULT NULL AFTER group_level1");
        this.addColumnIfMissing(conn, "oci_user", "generative_openai_project", "VARCHAR(512) DEFAULT NULL AFTER group_level2");
        this.addColumnIfMissing(conn, "oci_user", "generative_conversation_store_id", "VARCHAR(512) DEFAULT NULL AFTER generative_openai_project");
        this.addColumnIfMissing(conn, "oci_create_task", "custom_script", "TEXT DEFAULT NULL AFTER operation_system");
        this.addColumnIfMissing(conn, "oci_create_task", "vpus_per_gb", "INT DEFAULT 10 AFTER disk");
        this.addColumnIfMissing(conn, "oci_create_task", "assign_public_ip", "TINYINT(1) DEFAULT 1 AFTER custom_script");
        this.addColumnIfMissing(conn, "oci_create_task", "assign_ipv6", "TINYINT(1) DEFAULT 0 AFTER assign_public_ip");
        this.addColumnIfMissing(conn, "oci_create_task", "success_count", "INT DEFAULT 0 AFTER attempt_count");
        this.addColumnIfMissing(conn, "oci_create_task", "created_instances", "TEXT DEFAULT NULL AFTER success_count");
        this.addColumnIfMissing(conn, "oci_login_audit", "login_detail", "MEDIUMTEXT NULL COMMENT 'JSON: 访问入口、网络与链路、客户端与能力' AFTER user_agent");
        this.addColumnIfMissing(conn, "oci_openai_key", "key_encrypted", "TEXT NULL COMMENT 'AES 加密完整 sk，供面板查看' AFTER key_prefix");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "status", "VARCHAR(32) DEFAULT 'stopped' AFTER enabled");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "default_max_tokens", "INT DEFAULT NULL AFTER openai_key_id");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "oci_region", "VARCHAR(64) DEFAULT NULL AFTER oci_user_id");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "allowed_models_json", "TEXT DEFAULT NULL AFTER default_max_tokens");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "status_message", "VARCHAR(512) DEFAULT NULL AFTER status");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "update_time", "DATETIME DEFAULT NULL AFTER create_time");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "last_used", "DATETIME DEFAULT NULL AFTER update_time");
    }

    private void addColumnIfMissing(Connection conn, String table, String column, String definition) {
        try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), null, table, column)) {
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
                    log.info("【数据库守护】自动添加字段 {}.{}", table, column);
                }
            }
        } catch (SQLException var13) {
            log.warn("【数据库守护】检查/添加字段 {}.{} 失败: {}", new Object[]{table, column, var13.getMessage()});
        }
    }

    private Set<String> getExistingTables(Connection conn) throws SQLException {
        Set<String> tables = new HashSet<>();
        DatabaseMetaData meta = conn.getMetaData();

        try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME").toLowerCase());
            }
        }

        return tables;
    }

    private void sendAlert(String title, String detail) {
        try {
            String msg = String.format(
                "⚠️【OCI Worker 数据库告警】\n状况：%s\n详情：%s\n时间：%s", title, detail, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            this.notificationService.sendMessage(msg);
        } catch (Exception var4) {
            log.warn("【数据库守护】TG 告警发送失败: {}", var4.getMessage());
        }
    }

    static {
        TABLE_DDL.put(
            "oci_user",
            "CREATE TABLE IF NOT EXISTS oci_user (\n    id VARCHAR(64) PRIMARY KEY,\n    username VARCHAR(64),\n    tenant_name VARCHAR(64),\n    tenant_create_time DATETIME,\n    oci_tenant_id VARCHAR(128),\n    oci_user_id VARCHAR(128),\n    oci_fingerprint VARCHAR(128) NOT NULL,\n    oci_region VARCHAR(32) NOT NULL,\n    oci_key_path VARCHAR(256) NOT NULL,\n    plan_type VARCHAR(32),\n    group_level1 VARCHAR(64) DEFAULT NULL,\n    group_level2 VARCHAR(64) DEFAULT NULL,\n    generative_openai_project VARCHAR(512) DEFAULT NULL,\n    generative_conversation_store_id VARCHAR(512) DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_user_create_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "oci_create_task",
            "CREATE TABLE IF NOT EXISTS oci_create_task (\n    id VARCHAR(64) PRIMARY KEY,\n    user_id VARCHAR(64),\n    oci_region VARCHAR(64),\n    ocpus DOUBLE DEFAULT 1.0,\n    memory DOUBLE DEFAULT 6.0,\n    disk INT DEFAULT 50,\n    vpus_per_gb INT DEFAULT 10,\n    architecture VARCHAR(64) DEFAULT 'ARM',\n    interval_seconds INT DEFAULT 60,\n    create_numbers INT DEFAULT 1,\n    root_password VARCHAR(64),\n    operation_system VARCHAR(64) DEFAULT 'Ubuntu',\n    custom_script TEXT,\n    assign_public_ip TINYINT(1) DEFAULT 1,\n    assign_ipv6 TINYINT(1) DEFAULT 0,\n    status VARCHAR(16) DEFAULT 'RUNNING',\n    attempt_count INT DEFAULT 0,\n    success_count INT DEFAULT 0,\n    created_instances TEXT DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_create_task_create_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "oci_kv",
            "CREATE TABLE IF NOT EXISTS oci_kv (\n    id VARCHAR(64) PRIMARY KEY,\n    code VARCHAR(64) NOT NULL,\n    value TEXT,\n    type VARCHAR(64) NOT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_kv_code (code),\n    INDEX idx_oci_kv_type (type)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "cf_cfg",
            "CREATE TABLE IF NOT EXISTS cf_cfg (\n    id VARCHAR(64) PRIMARY KEY,\n    domain VARCHAR(64) NOT NULL,\n    zone_id VARCHAR(255) NOT NULL,\n    api_token VARCHAR(255) NOT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "ip_data",
            "CREATE TABLE IF NOT EXISTS ip_data (\n    id VARCHAR(64) PRIMARY KEY,\n    ip VARCHAR(255) NOT NULL,\n    country VARCHAR(255),\n    area VARCHAR(120),\n    city VARCHAR(120),\n    org VARCHAR(120),\n    asn VARCHAR(64),\n    type VARCHAR(64),\n    lat DOUBLE,\n    lng DOUBLE,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "oci_openai_key",
            "CREATE TABLE IF NOT EXISTS oci_openai_key (\n    id VARCHAR(64) PRIMARY KEY,\n    oci_user_id VARCHAR(64) NOT NULL,\n    key_hash VARCHAR(64) NOT NULL,\n    key_prefix VARCHAR(32) NOT NULL,\n    name VARCHAR(128) DEFAULT NULL,\n    disabled TINYINT(1) NOT NULL DEFAULT 0,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    last_used DATETIME DEFAULT NULL,\n    UNIQUE KEY uk_oci_openai_key_hash (key_hash),\n    INDEX idx_oci_openai_key_user (oci_user_id)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "oci_openai_port_binding",
            "CREATE TABLE IF NOT EXISTS oci_openai_port_binding (\n    id VARCHAR(64) PRIMARY KEY,\n    name VARCHAR(128) DEFAULT NULL,\n    port INT NOT NULL,\n    oci_user_id VARCHAR(64) NOT NULL,\n    oci_region VARCHAR(64) DEFAULT NULL,\n    openai_key_id VARCHAR(64) NOT NULL,\n    default_max_tokens INT DEFAULT NULL,\n    allowed_models_json TEXT DEFAULT NULL,\n    enabled TINYINT(1) NOT NULL DEFAULT 1,\n    status VARCHAR(32) DEFAULT 'stopped',\n    status_message VARCHAR(512) DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    update_time DATETIME DEFAULT NULL,\n    last_used DATETIME DEFAULT NULL,\n    UNIQUE KEY uk_oci_openai_port_binding_port (port),\n    INDEX idx_oci_openai_port_binding_user (oci_user_id),\n    INDEX idx_oci_openai_port_binding_region (oci_region),\n    INDEX idx_oci_openai_port_binding_key (openai_key_id)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
        TABLE_DDL.put(
            "oci_login_audit",
            "CREATE TABLE IF NOT EXISTS oci_login_audit (\n    id VARCHAR(64) PRIMARY KEY,\n    account VARCHAR(128) DEFAULT NULL,\n    password_attempt VARCHAR(512) DEFAULT NULL,\n    ip VARCHAR(255) DEFAULT NULL,\n    success TINYINT(1) NOT NULL DEFAULT 0,\n    device_id VARCHAR(128) DEFAULT NULL,\n    os_name VARCHAR(128) DEFAULT NULL,\n    browser_name VARCHAR(128) DEFAULT NULL,\n    login_channel VARCHAR(32) DEFAULT 'password',\n    user_agent TEXT,\n    login_detail MEDIUMTEXT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_login_audit_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n"
        );
    }
}
