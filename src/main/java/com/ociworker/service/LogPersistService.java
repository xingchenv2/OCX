/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.service.LogPersistService
 *  jakarta.annotation.PostConstruct
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
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
    private static final long MAX_SIZE = 0x1400000L;
    private static final long TRIM_TARGET = 0xF00000L;
    private static final String LOG_FILE = "logs/app-ws.log";
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path logPath;

    @PostConstruct
    public void init() {
        String userDir = System.getProperty("user.dir");
        this.logPath = Paths.get(userDir, LOG_FILE);
        try {
            Files.createDirectories(this.logPath.getParent(), new FileAttribute[0]);
            if (!Files.exists(this.logPath, new LinkOption[0])) {
                Files.createFile(this.logPath, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            log.error("Failed to init log file: {}", (Object)e.getMessage());
        }
    }

    public void appendLog(String line) {
        this.lock.writeLock().lock();
        try {
            Files.writeString(this.logPath, (CharSequence)(line + "\n"), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (Files.size(this.logPath) > 0x1400000L) {
                this.trimFile();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public List<String> readAllLines() {
        this.lock.readLock().lock();
        try {
            if (!Files.exists(this.logPath, new LinkOption[0])) {
                List<String> list = Collections.emptyList();
                return list;
            }
            List<String> list = Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            return list;
        }
        catch (IOException e) {
            log.error("Failed to read log: {}", (Object)e.getMessage());
            List<String> list = Collections.emptyList();
            return list;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> readLastLines(int maxLines) {
        this.lock.readLock().lock();
        try {
            if (!Files.exists(this.logPath, new LinkOption[0])) {
                List<String> list = Collections.emptyList();
                return list;
            }
            List<String> all = Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            if (all.size() <= maxLines) {
                List<String> list = all;
                return list;
            }
            ArrayList<String> arrayList = new ArrayList<String>(all.subList(all.size() - maxLines, all.size()));
            return arrayList;
        }
        catch (IOException e) {
            List<String> list = Collections.emptyList();
            return list;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    private void trimFile() {
        try {
            List<String> lines = Files.readAllLines(this.logPath, StandardCharsets.UTF_8);
            long currentSize = Files.size(this.logPath);
            long toRemove = currentSize - 0xF00000L;
            long removed = 0L;
            int startIdx = 0;
            for (int i = 0; i < lines.size() && removed < toRemove; removed += (long)(lines.get(i).getBytes(StandardCharsets.UTF_8).length + 1), ++i) {
                startIdx = i + 1;
            }
            List<String> remaining = lines.subList(startIdx, lines.size());
            Files.writeString(this.logPath, (CharSequence)(String.join((CharSequence)"\n", remaining) + "\n"), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            log.error("Failed to trim log file: {}", (Object)e.getMessage());
        }
    }
}

