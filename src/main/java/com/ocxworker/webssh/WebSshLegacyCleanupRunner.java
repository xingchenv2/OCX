/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.webssh.WebSshLegacyCleanupRunner
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.ApplicationArguments
 *  org.springframework.boot.ApplicationRunner
 *  org.springframework.stereotype.Component
 */
package com.ocxworker.webssh;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
public class WebSshLegacyCleanupRunner
implements ApplicationRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshLegacyCleanupRunner.class);
    private static final Path LEGACY_BIN = Path.of("/opt/oci-worker/oci-webssh", new String[0]);
    private static final String LEGACY_UNIT = "oci-webssh.service";

    public void run(ApplicationArguments args) {
        WebSshLegacyCleanupRunner.cleanupLegacyWebssh();
    }

    public static void cleanupLegacyWebssh() {
        try {
            Path unit;
            WebSshLegacyCleanupRunner.runQuiet((String[])new String[]{"systemctl", "stop", "oci-webssh.service"});
            WebSshLegacyCleanupRunner.runQuiet((String[])new String[]{"systemctl", "disable", "oci-webssh.service"});
            if (Files.exists(LEGACY_BIN, new LinkOption[0])) {
                Files.deleteIfExists(LEGACY_BIN);
                log.debug("Removed legacy binary {}", (Object)LEGACY_BIN);
            }
            if (Files.exists(unit = Path.of("/etc/systemd/system/oci-webssh.service", new String[0]), new LinkOption[0])) {
                Files.deleteIfExists(unit);
                WebSshLegacyCleanupRunner.runQuiet((String[])new String[]{"systemctl", "daemon-reload"});
            }
        }
        catch (Exception e) {
            log.debug("Legacy sidecar cleanup partial failure: {}", (Object)e.getMessage());
        }
    }

    private static void runQuiet(String ... cmd) {
        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            p.waitFor(8L, TimeUnit.SECONDS);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

