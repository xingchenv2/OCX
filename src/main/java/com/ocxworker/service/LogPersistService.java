package com.ocxworker.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogPersistService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(LogPersistService.class);
    private static final long MAX_SIZE = 20971520L;
    private static final long TRIM_TARGET = 15728640L;
    private static final String LOG_FILE = "logs/app-ws.log";
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path logPath;

    @PostConstruct
    public void init() {
        String userDir = System.getProperty("user.dir");
        this.logPath = Paths.get(userDir, "logs/app-ws.log");

        try {
            Files.createDirectories(this.logPath.getParent());
            if (!Files.exists(this.logPath)) {
                Files.createFile(this.logPath);
            }
        } catch (IOException var3) {
            log.error("Failed to init log file: {}", var3.getMessage());
        }
    }

    public void appendLog(String line) {
        this.lock.writeLock().lock();

        try {
            Files.writeString(this.logPath, line + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (Files.size(this.logPath) > 20971520L) {
                this.trimFile();
            }
        } catch (IOException var6) {
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public List<String> readAllLines() {
        this.lock.readLock().lock();

        List e;
        try {
            if (Files.exists(this.logPath)) {
                return Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            }

            e = Collections.emptyList();
        } catch (IOException var6) {
            log.error("Failed to read log: {}", var6.getMessage());
            return Collections.emptyList();
        } finally {
            this.lock.readLock().unlock();
        }

        return e;
    }

    public List<String> readLastLines(int maxLines) {
        this.lock.readLock().lock();

        List var10;
        try {
            if (!Files.exists(this.logPath)) {
                return Collections.emptyList();
            }

            List<String> all = Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            if (all.size() > maxLines) {
                return new ArrayList<>(all.subList(all.size() - maxLines, all.size()));
            }

            var10 = all;
        } catch (IOException var7) {
            return Collections.emptyList();
        } finally {
            this.lock.readLock().unlock();
        }

        return var10;
    }

    private void trimFile() {
        try {
            List<String> lines = Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            long currentSize = Files.size(this.logPath);
            long toRemove = currentSize - 15728640L;
            long removed = 0L;
            int startIdx = 0;

            for (int i = 0; i < lines.size() && removed < toRemove; i++) {
                removed += (long)(lines.get(i).getBytes(StandardCharsets.UTF_8).length + 1);
                startIdx = i + 1;
            }

            List<String> remaining = lines.subList(startIdx, lines.size());
            Files.writeString(this.logPath, String.join("\n", remaining) + "\n", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException var10) {
            log.error("Failed to trim log file: {}", var10.getMessage());
        }
    }
}
