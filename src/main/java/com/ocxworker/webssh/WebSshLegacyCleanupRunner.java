package com.ocxworker.webssh;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class WebSshLegacyCleanupRunner implements ApplicationRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WebSshLegacyCleanupRunner.class);
    private static final Path LEGACY_BIN = Path.of("/opt/ocx-worker/oci-webssh");
    private static final String LEGACY_UNIT = "oci-webssh.service";

    public void run(ApplicationArguments args) {
        cleanupLegacyWebssh();
    }

    public static void cleanupLegacyWebssh() {
        try {
            runQuiet("systemctl", "stop", "oci-webssh.service");
            runQuiet("systemctl", "disable", "oci-webssh.service");
            if (Files.exists(LEGACY_BIN)) {
                Files.deleteIfExists(LEGACY_BIN);
                log.debug("Removed legacy binary {}", LEGACY_BIN);
            }

            Path unit = Path.of("/etc/systemd/system/oci-webssh.service");
            if (Files.exists(unit)) {
                Files.deleteIfExists(unit);
                runQuiet("systemctl", "daemon-reload");
            }
        } catch (Exception var1) {
            log.debug("Legacy sidecar cleanup partial failure: {}", var1.getMessage());
        }
    }

    private static void runQuiet(String... cmd) {
        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            p.waitFor(8L, TimeUnit.SECONDS);
        } catch (Exception var2) {
        }
    }
}
