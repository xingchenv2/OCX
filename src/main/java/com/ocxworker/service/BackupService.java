/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.service.BackupService
 *  lombok.Generated
 *  net.lingala.zip4j.ZipFile
 *  net.lingala.zip4j.model.ZipParameters
 *  net.lingala.zip4j.model.enums.CompressionLevel
 *  net.lingala.zip4j.model.enums.EncryptionMethod
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.Generated;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class BackupService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    @Value(value="${oci-cfg.key-dir-path}")
    private String keyDirPath;
    @Value(value="${spring.datasource.url}")
    private String dbUrl;
    @Value(value="${spring.datasource.username}")
    private String dbUser;
    @Value(value="${spring.datasource.password}")
    private String dbPassword;
    private final DataSource dataSource;
    private static final String[] TABLES = new String[]{"oci_user", "oci_create_task", "oci_kv", "cf_cfg", "ip_data", "oci_openai_key", "oci_openai_port_binding"};

    public BackupService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] createBackup(String password) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path tempDir = Files.createTempDirectory("oci-worker-backup-", new FileAttribute[0]);
            Path sqlDumpFile = tempDir.resolve("oci-worker-dump.sql");
            String sqlDump = this.exportDatabase();
            Files.writeString(sqlDumpFile, (CharSequence)sqlDump, new OpenOption[0]);
            String zipPath = tempDir.resolve("oci-worker-backup-" + timestamp + ".zip").toString();
            ZipParameters params = new ZipParameters();
            params.setCompressionLevel(CompressionLevel.NORMAL);
            params.setEncryptFiles(true);
            params.setEncryptionMethod(EncryptionMethod.AES);
            try (ZipFile zipFile = new ZipFile(zipPath, password.toCharArray());){
                zipFile.addFile(sqlDumpFile.toFile(), params);
                File keysDir = new File(this.keyDirPath);
                if (keysDir.exists() && keysDir.isDirectory()) {
                    zipFile.addFolder(keysDir, params);
                }
            }
            byte[] data = Files.readAllBytes(Path.of(zipPath, new String[0]));
            Files.deleteIfExists(sqlDumpFile);
            Files.deleteIfExists(Path.of(zipPath, new String[0]));
            Files.deleteIfExists(tempDir);
            return data;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa\u5907\u4efd\u5931\u8d25: " + e.getMessage());
        }
    }

    public void restoreBackup(byte[] data, String password) {
        try {
            Path keysSource;
            Path tempDir = Files.createTempDirectory("oci-worker-restore-", new FileAttribute[0]);
            Path tempFile = tempDir.resolve("restore.zip");
            Files.write(tempFile, data, new OpenOption[0]);
            try (ZipFile zipFile = new ZipFile(tempFile.toFile(), password.toCharArray());){
                zipFile.extractAll(tempDir.toString());
            }
            Path sqlDumpFile = tempDir.resolve("oci-worker-dump.sql");
            if (Files.exists(sqlDumpFile, new LinkOption[0])) {
                String sql = Files.readString(sqlDumpFile);
                this.importDatabase(sql.replace("\r\n", "\n").replace("\r", "\n"));
            }
            if (Files.exists(keysSource = tempDir.resolve("keys"), new LinkOption[0])) {
                File keysTarget = new File(this.keyDirPath);
                if (!keysTarget.exists()) {
                    keysTarget.mkdirs();
                }
                this.copyDirectory(keysSource, keysTarget.toPath());
            }
            this.deleteDirectory(tempDir);
            log.info("Backup restored successfully");
        }
        catch (Exception e) {
            throw new OciException("\u6062\u590d\u5907\u4efd\u5931\u8d25: " + e.getMessage());
        }
    }

    private String exportDatabase() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- OCX Database Backup\n");
        sb.append("-- Generated: ").append(LocalDateTime.now()).append("\n\n");
        sb.append("SET FOREIGN_KEY_CHECKS=0;\n\n");
        try (Connection conn = this.dataSource.getConnection();){
            for (String table : TABLES) {
                sb.append("-- Table: ").append(table).append("\n");
                sb.append("DELETE FROM `").append(table).append("`;\n");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);){
                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();
                    while (rs.next()) {
                        sb.append("INSERT INTO `").append(table).append("` VALUES (");
                        for (int i = 1; i <= colCount; ++i) {
                            if (i > 1) {
                                sb.append(", ");
                            }
                            Object val = rs.getObject(i);
                            BackupService.appendSqlLiteral((StringBuilder)sb, (Object)val);
                        }
                        sb.append(");\n");
                    }
                }
                sb.append("\n");
            }
        }
        sb.append("SET FOREIGN_KEY_CHECKS=1;\n");
        return sb.toString();
    }

    private static void appendSqlLiteral(StringBuilder sb, Object val) {
        if (val == null) {
            sb.append("NULL");
            return;
        }
        if (val instanceof Boolean) {
            sb.append((Boolean)val != false ? "1" : "0");
            return;
        }
        if (val instanceof Number) {
            sb.append(val);
            return;
        }
        if (val instanceof byte[]) {
            byte[] b = (byte[])val;
            if (b.length == 0) {
                sb.append("''");
            } else {
                sb.append("0x");
                for (byte x : b) {
                    sb.append(String.format("%02x", x));
                }
            }
            return;
        }
        if (val instanceof Date) {
            sb.append('\'').append(((Date)val).toString()).append('\'');
            return;
        }
        if (val instanceof Time) {
            sb.append('\'').append(((Time)val).toString()).append('\'');
            return;
        }
        if (val instanceof Timestamp) {
            sb.append('\'').append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)val)).append('\'');
            return;
        }
        if (val instanceof LocalDateTime) {
            String s = val.toString().replace('T', ' ');
            if (s.length() > 19 && s.charAt(19) == '.') {
                s = s.substring(0, 19);
            }
            sb.append('\'').append(s).append('\'');
            return;
        }
        if (val instanceof LocalDate) {
            sb.append('\'').append(val).append('\'');
            return;
        }
        if (val instanceof LocalTime) {
            sb.append('\'').append(val).append('\'');
            return;
        }
        String s = val.toString();
        if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
            sb.append("true".equalsIgnoreCase(s) ? "1" : "0");
            return;
        }
        sb.append('\'').append(s.replace("\\", "\\\\").replace("'", "\\'")).append('\'');
    }

    private void importDatabase(String sql) throws SQLException {
        try (Connection conn = this.dataSource.getConnection();
             Statement stmt = conn.createStatement();){
            conn.setAutoCommit(false);
            for (String chunk : sql.split(";\n")) {
                String executable = BackupService.stripSqlComments((String)chunk);
                if (executable.isEmpty()) continue;
                executable = BackupService.fixLegacyBooleanStringLiterals((String)executable);
                stmt.execute(executable);
            }
            conn.commit();
        }
    }

    private static String fixLegacyBooleanStringLiterals(String sql) {
        if (!sql.toUpperCase(Locale.ROOT).contains("INSERT")) {
            return sql;
        }
        return sql.replace(", 'true',", ", 1,").replace(", 'false',", ", 0,").replace(", 'true')", ", 1)").replace(", 'false')", ", 0)").replace("('true',", "(1,").replace("('false',", "(0,");
    }

    private static String stripSqlComments(String chunk) {
        StringBuilder out = new StringBuilder();
        for (String line : chunk.split("\n")) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("--")) continue;
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(line.trim());
        }
        return out.toString().trim();
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> walk = Files.walk(source, new FileVisitOption[0]);){
            walk.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize((Path)src));
                    if (Files.isDirectory(src, new LinkOption[0])) {
                        Files.createDirectories(dest, new FileAttribute[0]);
                    } else {
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir, new LinkOption[0])) {
            try (Stream<Path> walk = Files.walk(dir, new FileVisitOption[0]);){
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                });
            }
        }
    }
}

