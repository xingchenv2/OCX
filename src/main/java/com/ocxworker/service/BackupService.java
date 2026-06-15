package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

@Service
public class BackupService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    @Value("${oci-cfg.key-dir-path}")
    private String keyDirPath;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    private final DataSource dataSource;
    private static final String[] TABLES = new String[]{
        "oci_user", "oci_create_task", "oci_kv", "cf_cfg", "ip_data", "oci_openai_key", "oci_openai_port_binding"
    };

    public BackupService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] createBackup(String password) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path tempDir = Files.createTempDirectory("ocx-worker-backup-");
            Path sqlDumpFile = tempDir.resolve("ocx-worker-dump.sql");
            String sqlDump = this.exportDatabase();
            Files.writeString(sqlDumpFile, sqlDump);
            String zipPath = tempDir.resolve("ocx-worker-backup-" + timestamp + ".zip").toString();
            ZipParameters params = new ZipParameters();
            params.setCompressionLevel(CompressionLevel.NORMAL);
            params.setEncryptFiles(true);
            params.setEncryptionMethod(EncryptionMethod.AES);
            ZipFile zipFile = new ZipFile(zipPath, password.toCharArray());

            try {
                zipFile.addFile(sqlDumpFile.toFile(), params);
                File keysDir = new File(this.keyDirPath);
                if (keysDir.exists() && keysDir.isDirectory()) {
                    zipFile.addFolder(keysDir, params);
                }
            } catch (Throwable var12) {
                try {
                    zipFile.close();
                } catch (Throwable var11) {
                    var12.addSuppressed(var11);
                }

                throw var12;
            }

            zipFile.close();
            byte[] var14 = Files.readAllBytes(Path.of(zipPath));
            Files.deleteIfExists(sqlDumpFile);
            Files.deleteIfExists(Path.of(zipPath));
            Files.deleteIfExists(tempDir);
            return var14;
        } catch (Exception var13) {
            throw new OciException("创建备份失败: " + var13.getMessage());
        }
    }

    public void restoreBackup(byte[] data, String password) {
        try {
            Path tempDir = Files.createTempDirectory("ocx-worker-restore-");
            Path tempFile = tempDir.resolve("restore.zip");
            Files.write(tempFile, data);
            ZipFile zipFile = new ZipFile(tempFile.toFile(), password.toCharArray());

            try {
                zipFile.extractAll(tempDir.toString());
            } catch (Throwable var9) {
                try {
                    zipFile.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            zipFile.close();
            Path sqlDumpFile = tempDir.resolve("ocx-worker-dump.sql");
            if (Files.exists(sqlDumpFile)) {
                String sql = Files.readString(sqlDumpFile);
                this.importDatabase(sql.replace("\r\n", "\n").replace("\r", "\n"));
            }

            Path keysSource = tempDir.resolve("keys");
            if (Files.exists(keysSource)) {
                File keysTarget = new File(this.keyDirPath);
                if (!keysTarget.exists()) {
                    keysTarget.mkdirs();
                }

                this.copyDirectory(keysSource, keysTarget.toPath());
            }

            this.deleteDirectory(tempDir);
            log.info("Backup restored successfully");
        } catch (Exception var10) {
            throw new OciException("恢复备份失败: " + var10.getMessage());
        }
    }

    private String exportDatabase() throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- OCI Worker Database Backup\n");
        sb.append("-- Generated: ").append(LocalDateTime.now()).append("\n\n");
        sb.append("SET FOREIGN_KEY_CHECKS=0;\n\n");

        try (Connection conn = this.dataSource.getConnection()) {
            for (String table : TABLES) {
                sb.append("-- Table: ").append(table).append("\n");
                sb.append("DELETE FROM `").append(table).append("`;\n");

                try (
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                ) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();

                    while (rs.next()) {
                        sb.append("INSERT INTO `").append(table).append("` VALUES (");

                        for (int i = 1; i <= colCount; i++) {
                            if (i > 1) {
                                sb.append(", ");
                            }

                            Object val = rs.getObject(i);
                            appendSqlLiteral(sb, val);
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
        } else if (val instanceof Boolean) {
            sb.append((Boolean)val ? "1" : "0");
        } else if (val instanceof Number) {
            sb.append(val);
        } else if (!(val instanceof byte[] b)) {
            if (val instanceof Date) {
                sb.append('\'').append(((Date)val).toString()).append('\'');
            } else if (val instanceof Time) {
                sb.append('\'').append(((Time)val).toString()).append('\'');
            } else if (val instanceof Timestamp) {
                sb.append('\'').append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)val)).append('\'');
            } else if (val instanceof LocalDateTime) {
                String s = val.toString().replace('T', ' ');
                if (s.length() > 19 && s.charAt(19) == '.') {
                    s = s.substring(0, 19);
                }

                sb.append('\'').append(s).append('\'');
            } else if (val instanceof LocalDate) {
                sb.append('\'').append(val).append('\'');
            } else if (val instanceof LocalTime) {
                sb.append('\'').append(val).append('\'');
            } else {
                String s = val.toString();
                if (!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
                    sb.append('\'').append(s.replace("\\", "\\\\").replace("'", "\\'")).append('\'');
                } else {
                    sb.append("true".equalsIgnoreCase(s) ? "1" : "0");
                }
            }
        } else {
            if (b.length == 0) {
                sb.append("''");
            } else {
                sb.append("0x");

                for (byte x : b) {
                    sb.append(String.format("%02x", x));
                }
            }
        }
    }

    private void importDatabase(String sql) throws SQLException {
        try (
            Connection conn = this.dataSource.getConnection();
            Statement stmt = conn.createStatement();
        ) {
            conn.setAutoCommit(false);

            for (String chunk : sql.split(";\n")) {
                String executable = stripSqlComments(chunk);
                if (!executable.isEmpty()) {
                    executable = fixLegacyBooleanStringLiterals(executable);
                    stmt.execute(executable);
                }
            }

            conn.commit();
        }
    }

    private static String fixLegacyBooleanStringLiterals(String sql) {
        return !sql.toUpperCase(Locale.ROOT).contains("INSERT")
            ? sql
            : sql.replace(", 'true',", ", 1,")
                .replace(", 'false',", ", 0,")
                .replace(", 'true')", ", 1)")
                .replace(", 'false')", ", 0)")
                .replace("('true',", "(1,")
                .replace("('false',", "(0,");
    }

    private static String stripSqlComments(String chunk) {
        StringBuilder out = new StringBuilder();

        for (String line : chunk.split("\n")) {
            String t = line.trim();
            if (!t.isEmpty() && !t.startsWith("--")) {
                if (out.length() > 0) {
                    out.append('\n');
                }

                out.append(line.trim());
            }
        }

        return out.toString().trim();
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> walk = Files.walk(source)) {
            walk.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize(src));
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException var4) {
                    throw new UncheckedIOException(var4);
                }
            });
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException var2x) {
                    }
                });
            }
        }
    }
}
