/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.service.DatabaseGuardService
 *  com.ocxworker.service.NotificationService
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.service.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
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
import java.util.Map;
import java.util.Set;
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
    private static final Map<String, String> TABLE_DDL = new LinkedHashMap();

    @PostConstruct
    public void startupCheck() {
        log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u542f\u52a8\u81ea\u68c0...");
        ArrayList<String> missing = new ArrayList<String>();
        ArrayList<String> repaired = new ArrayList<String>();
        try (Connection conn = this.dataSource.getConnection();){
            Set existing = this.getExistingTables(conn);
            for (Map.Entry entry : TABLE_DDL.entrySet()) {
                String table = (String)entry.getKey();
                if (existing.contains(table)) continue;
                missing.add(table);
                log.warn("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u8868 {} \u4e0d\u5b58\u5728\uff0c\u6b63\u5728\u81ea\u52a8\u521b\u5efa...", (Object)table);
                try (Statement stmt = conn.createStatement();){
                    stmt.execute((String)entry.getValue());
                }
                repaired.add(table);
                log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u8868 {} \u5df2\u81ea\u52a8\u521b\u5efa", (Object)table);
            }
            this.migrateColumns(conn);
        }
        catch (Exception e) {
            log.error("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u542f\u52a8\u81ea\u68c0\u5931\u8d25: {}", (Object)e.getMessage(), (Object)e);
            this.sendAlert("\u542f\u52a8\u81ea\u68c0\u5931\u8d25", "\u6570\u636e\u5e93\u8fde\u63a5\u5f02\u5e38: " + e.getMessage());
            return;
        }
        if (!missing.isEmpty()) {
            String msg = String.format("\u68c0\u6d4b\u5230 %d \u5f20\u8868\u7f3a\u5931: %s\n\u5df2\u81ea\u52a8\u4fee\u590d: %s", missing.size(), String.join((CharSequence)", ", missing), String.join((CharSequence)", ", repaired));
            log.warn("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011{}", (Object)msg);
            this.sendAlert("\u8868\u7f3a\u5931\u5df2\u81ea\u52a8\u4fee\u590d", msg);
        } else {
            log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u6240\u6709\u8868\u6b63\u5e38 \u2713");
        }
    }

    @Scheduled(cron="0 0 */6 * * ?")
    public void periodicCheck() {
        log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u5b9a\u65f6\u5de1\u68c0...");
        ArrayList<CallSite> problems = new ArrayList<CallSite>();
        try (Connection conn = this.dataSource.getConnection();){
            Set existing = this.getExistingTables(conn);
            for (Map.Entry entry : TABLE_DDL.entrySet()) {
                String table = (String)entry.getKey();
                if (existing.contains(table)) continue;
                log.warn("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u5de1\u68c0\u53d1\u73b0\u8868 {} \u7f3a\u5931\uff0c\u81ea\u52a8\u4fee\u590d", (Object)table);
                try (Statement stmt = conn.createStatement();){
                    stmt.execute((String)entry.getValue());
                }
                problems.add((CallSite)((Object)(table + "(\u5df2\u4fee\u590d)")));
            }
        }
        catch (Exception e) {
            log.error("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u5de1\u68c0\u5f02\u5e38: {}", (Object)e.getMessage());
            this.sendAlert("\u5de1\u68c0\u5f02\u5e38", "\u6570\u636e\u5e93\u8fde\u63a5\u5931\u8d25: " + e.getMessage());
            return;
        }
        if (!problems.isEmpty()) {
            this.sendAlert("\u5de1\u68c0\u53d1\u73b0\u5f02\u5e38", "\u7f3a\u5931\u8868: " + String.join((CharSequence)", ", problems));
        }
    }

    @Scheduled(cron="0 0 */6 * * ?")
    public void autoBackup() {
        log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u5f00\u59cb\u81ea\u52a8\u5907\u4efd...");
        try {
            Path backupDir = Path.of(BACKUP_DIR, new String[0]);
            Files.createDirectories(backupDir, new FileAttribute[0]);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupFile = backupDir.resolve("auto_backup_" + timestamp + ".sql");
            String dump = this.exportAllTables();
            Files.writeString(backupFile, (CharSequence)dump, new OpenOption[0]);
            long sizeKB = Files.size(backupFile) / 1024L;
            log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u81ea\u52a8\u5907\u4efd\u5b8c\u6210: {} ({}KB)", (Object)backupFile.getFileName(), (Object)sizeKB);
            this.cleanOldBackups(backupDir);
        }
        catch (Exception e) {
            log.error("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u81ea\u52a8\u5907\u4efd\u5931\u8d25: {}", (Object)e.getMessage(), (Object)e);
            this.sendAlert("\u81ea\u52a8\u5907\u4efd\u5931\u8d25", e.getMessage());
        }
    }

    private String exportAllTables() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- OCX Auto Backup\n");
        sb.append("-- Generated: ").append(LocalDateTime.now()).append("\n\n");
        sb.append("SET NAMES utf8mb4;\n");
        sb.append("SET FOREIGN_KEY_CHECKS=0;\n\n");
        try (Connection conn = this.dataSource.getConnection();){
            for (Map.Entry entry : TABLE_DDL.entrySet()) {
                String table = (String)entry.getKey();
                sb.append("-- Table structure: ").append(table).append("\n");
                sb.append("DROP TABLE IF EXISTS `").append(table).append("`;\n");
                sb.append((String)entry.getValue()).append(";\n\n");
                try {
                    Statement stmt = conn.createStatement();
                    try {
                        ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                        try {
                            ResultSetMetaData meta = rs.getMetaData();
                            int colCount = meta.getColumnCount();
                            sb.append("-- Data: ").append(table).append("\n");
                            while (rs.next()) {
                                sb.append("INSERT INTO `").append(table).append("` VALUES (");
                                for (int i = 1; i <= colCount; ++i) {
                                    Object val;
                                    if (i > 1) {
                                        sb.append(", ");
                                    }
                                    if ((val = rs.getObject(i)) == null) {
                                        sb.append("NULL");
                                        continue;
                                    }
                                    sb.append("'").append(val.toString().replace("\\", "\\\\").replace("'", "\\'")).append("'");
                                }
                                sb.append(");\n");
                            }
                            sb.append("\n");
                        }
                        finally {
                            if (rs == null) continue;
                            rs.close();
                        }
                    }
                    finally {
                        if (stmt == null) continue;
                        stmt.close();
                    }
                }
                catch (SQLException e) {
                    sb.append("-- WARN: table ").append(table).append(" export failed: ").append(e.getMessage()).append("\n\n");
                }
            }
        }
        sb.append("SET FOREIGN_KEY_CHECKS=1;\n");
        return sb.toString();
    }

    private void cleanOldBackups(Path backupDir) throws IOException {
        LocalDate cutoff = LocalDate.now().minusDays(7L);
        try (Stream<Path> files = Files.list(backupDir);){
            files.filter(p -> p.getFileName().toString().startsWith("auto_backup_")).filter(p -> {
                try {
                    return Files.getLastModifiedTime(p, new LinkOption[0]).toInstant().isBefore(cutoff.atStartOfDay().toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
                }
                catch (IOException e) {
                    return false;
                }
            }).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                    log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u6e05\u7406\u8fc7\u671f\u5907\u4efd: {}", (Object)p.getFileName());
                }
                catch (IOException iOException) {
                    // empty catch block
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
        this.addColumnIfMissing(conn, "oci_login_audit", "login_detail", "MEDIUMTEXT NULL COMMENT 'JSON: \u8bbf\u95ee\u5165\u53e3\u3001\u7f51\u7edc\u4e0e\u94fe\u8def\u3001\u5ba2\u6237\u7aef\u4e0e\u80fd\u529b' AFTER user_agent");
        this.addColumnIfMissing(conn, "oci_openai_key", "key_encrypted", "TEXT NULL COMMENT 'AES \u52a0\u5bc6\u5b8c\u6574 sk\uff0c\u4f9b\u9762\u677f\u67e5\u770b' AFTER key_prefix");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "status", "VARCHAR(32) DEFAULT 'stopped' AFTER enabled");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "default_max_tokens", "INT DEFAULT NULL AFTER openai_key_id");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "oci_region", "VARCHAR(64) DEFAULT NULL AFTER oci_user_id");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "allowed_models_json", "TEXT DEFAULT NULL AFTER default_max_tokens");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "status_message", "VARCHAR(512) DEFAULT NULL AFTER status");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "update_time", "DATETIME DEFAULT NULL AFTER create_time");
        this.addColumnIfMissing(conn, "oci_openai_port_binding", "last_used", "DATETIME DEFAULT NULL AFTER update_time");
    }

    private void addColumnIfMissing(Connection conn, String table, String column, String definition) {
        block14: {
            try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), null, table, column);){
                if (rs.next()) break block14;
                try (Statement stmt = conn.createStatement();){
                    stmt.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
                    log.info("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u81ea\u52a8\u6dfb\u52a0\u5b57\u6bb5 {}.{}", (Object)table, (Object)column);
                }
            }
            catch (SQLException e) {
                log.warn("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011\u68c0\u67e5/\u6dfb\u52a0\u5b57\u6bb5 {}.{} \u5931\u8d25: {}", new Object[]{table, column, e.getMessage()});
            }
        }
    }

    private Set<String> getExistingTables(Connection conn) throws SQLException {
        HashSet<String> tables = new HashSet<String>();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});){
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME").toLowerCase());
            }
        }
        return tables;
    }

    private void sendAlert(String title, String detail) {
        try {
            String msg = String.format("\u26a0\ufe0f\u3010OCX \u6570\u636e\u5e93\u544a\u8b66\u3011\n\u72b6\u51b5\uff1a%s\n\u8be6\u60c5\uff1a%s\n\u65f6\u95f4\uff1a%s", title, detail, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            this.notificationService.sendMessage(msg);
        }
        catch (Exception e) {
            log.warn("\u3010\u6570\u636e\u5e93\u5b88\u62a4\u3011TG \u544a\u8b66\u53d1\u9001\u5931\u8d25: {}", (Object)e.getMessage());
        }
    }

    static {
        TABLE_DDL.put("oci_user", "CREATE TABLE IF NOT EXISTS oci_user (\n    id VARCHAR(64) PRIMARY KEY,\n    username VARCHAR(64),\n    tenant_name VARCHAR(64),\n    tenant_create_time DATETIME,\n    oci_tenant_id VARCHAR(128),\n    oci_user_id VARCHAR(128),\n    oci_fingerprint VARCHAR(128) NOT NULL,\n    oci_region VARCHAR(32) NOT NULL,\n    oci_key_path VARCHAR(256) NOT NULL,\n    plan_type VARCHAR(32),\n    group_level1 VARCHAR(64) DEFAULT NULL,\n    group_level2 VARCHAR(64) DEFAULT NULL,\n    generative_openai_project VARCHAR(512) DEFAULT NULL,\n    generative_conversation_store_id VARCHAR(512) DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_user_create_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("oci_create_task", "CREATE TABLE IF NOT EXISTS oci_create_task (\n    id VARCHAR(64) PRIMARY KEY,\n    user_id VARCHAR(64),\n    oci_region VARCHAR(64),\n    ocpus DOUBLE DEFAULT 1.0,\n    memory DOUBLE DEFAULT 6.0,\n    disk INT DEFAULT 50,\n    vpus_per_gb INT DEFAULT 10,\n    architecture VARCHAR(64) DEFAULT 'ARM',\n    interval_seconds INT DEFAULT 60,\n    create_numbers INT DEFAULT 1,\n    root_password VARCHAR(64),\n    operation_system VARCHAR(64) DEFAULT 'Ubuntu',\n    custom_script TEXT,\n    assign_public_ip TINYINT(1) DEFAULT 1,\n    assign_ipv6 TINYINT(1) DEFAULT 0,\n    status VARCHAR(16) DEFAULT 'RUNNING',\n    attempt_count INT DEFAULT 0,\n    success_count INT DEFAULT 0,\n    created_instances TEXT DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_create_task_create_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("oci_kv", "CREATE TABLE IF NOT EXISTS oci_kv (\n    id VARCHAR(64) PRIMARY KEY,\n    code VARCHAR(64) NOT NULL,\n    value TEXT,\n    type VARCHAR(64) NOT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_kv_code (code),\n    INDEX idx_oci_kv_type (type)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("cf_cfg", "CREATE TABLE IF NOT EXISTS cf_cfg (\n    id VARCHAR(64) PRIMARY KEY,\n    domain VARCHAR(64) NOT NULL,\n    zone_id VARCHAR(255) NOT NULL,\n    api_token VARCHAR(255) NOT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("ip_data", "CREATE TABLE IF NOT EXISTS ip_data (\n    id VARCHAR(64) PRIMARY KEY,\n    ip VARCHAR(255) NOT NULL,\n    country VARCHAR(255),\n    area VARCHAR(120),\n    city VARCHAR(120),\n    org VARCHAR(120),\n    asn VARCHAR(64),\n    type VARCHAR(64),\n    lat DOUBLE,\n    lng DOUBLE,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("oci_openai_key", "CREATE TABLE IF NOT EXISTS oci_openai_key (\n    id VARCHAR(64) PRIMARY KEY,\n    oci_user_id VARCHAR(64) NOT NULL,\n    key_hash VARCHAR(64) NOT NULL,\n    key_prefix VARCHAR(32) NOT NULL,\n    name VARCHAR(128) DEFAULT NULL,\n    disabled TINYINT(1) NOT NULL DEFAULT 0,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    last_used DATETIME DEFAULT NULL,\n    UNIQUE KEY uk_oci_openai_key_hash (key_hash),\n    INDEX idx_oci_openai_key_user (oci_user_id)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("oci_openai_port_binding", "CREATE TABLE IF NOT EXISTS oci_openai_port_binding (\n    id VARCHAR(64) PRIMARY KEY,\n    name VARCHAR(128) DEFAULT NULL,\n    port INT NOT NULL,\n    oci_user_id VARCHAR(64) NOT NULL,\n    oci_region VARCHAR(64) DEFAULT NULL,\n    openai_key_id VARCHAR(64) NOT NULL,\n    default_max_tokens INT DEFAULT NULL,\n    allowed_models_json TEXT DEFAULT NULL,\n    enabled TINYINT(1) NOT NULL DEFAULT 1,\n    status VARCHAR(32) DEFAULT 'stopped',\n    status_message VARCHAR(512) DEFAULT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    update_time DATETIME DEFAULT NULL,\n    last_used DATETIME DEFAULT NULL,\n    UNIQUE KEY uk_oci_openai_port_binding_port (port),\n    INDEX idx_oci_openai_port_binding_user (oci_user_id),\n    INDEX idx_oci_openai_port_binding_region (oci_region),\n    INDEX idx_oci_openai_port_binding_key (openai_key_id)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
        TABLE_DDL.put("oci_login_audit", "CREATE TABLE IF NOT EXISTS oci_login_audit (\n    id VARCHAR(64) PRIMARY KEY,\n    account VARCHAR(128) DEFAULT NULL,\n    password_attempt VARCHAR(512) DEFAULT NULL,\n    ip VARCHAR(255) DEFAULT NULL,\n    success TINYINT(1) NOT NULL DEFAULT 0,\n    device_id VARCHAR(128) DEFAULT NULL,\n    os_name VARCHAR(128) DEFAULT NULL,\n    browser_name VARCHAR(128) DEFAULT NULL,\n    login_channel VARCHAR(32) DEFAULT 'password',\n    user_agent TEXT,\n    login_detail MEDIUMTEXT NULL,\n    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    INDEX idx_oci_login_audit_time (create_time DESC)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci\n");
    }
}

