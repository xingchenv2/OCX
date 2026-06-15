package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.CreateInstanceConsoleConnectionDetails;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InstanceConsoleConnection;
import com.oracle.bmc.core.model.InstanceConsoleConnection.LifecycleState;
import com.oracle.bmc.core.requests.CreateInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.DeleteInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.GetInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.ListInstanceConsoleConnectionsRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ConsoleService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ConsoleService.class);
    @Resource
    private OciUserMapper userMapper;
    @Lazy
    @Resource
    private OciProxyConfigService ociProxyConfigService;
    private static final String KEY_DIR = "./keys";
    private static final String PRIVATE_KEY_FILE = "console_rsa";
    private static final String PUBLIC_KEY_FILE = "console_rsa.pub";
    private static final String SSH_HOST_OPTS = "-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 ";
    private static final String RSA_OPTS = "-o HostkeyAlgorithms=+ssh-rsa -o PubkeyAcceptedAlgorithms=+ssh-rsa ";
    private String publicKeyContent;
    private String privateKeyPath;
    private final Map<String, ConsoleService.ConsoleSession> activeSessions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            Path keyDir = Path.of("./keys");
            Files.createDirectories(keyDir);
            Path privPath = keyDir.resolve("console_rsa");
            Path pubPath = keyDir.resolve("console_rsa.pub");
            this.privateKeyPath = privPath.toAbsolutePath().toString();
            boolean needRegenerate = !Files.exists(privPath) || !Files.exists(pubPath);
            if (!needRegenerate) {
                String privContent = Files.readString(privPath);
                if (!privContent.contains("-----BEGIN OPENSSH PRIVATE KEY-----")) {
                    log.warn("【串行控制台】密钥非 ssh-keygen 格式，强制重新生成...");
                    needRegenerate = true;
                }
            }

            if (needRegenerate) {
                this.generateSshKeyPair(privPath, pubPath);
                log.info("【串行控制台】已生成 SSH 密钥: {}", pubPath.toAbsolutePath());
            } else {
                this.publicKeyContent = Files.readString(pubPath).trim();
                log.info("【串行控制台】已加载 SSH 密钥: {}", pubPath.toAbsolutePath());
            }
        } catch (Exception var6) {
            log.error("【串行控制台】SSH 密钥初始化失败: {}", var6.getMessage());
        }

        this.cleanupLegacyTempUsers();
    }

    private void generateSshKeyPair(Path privPath, Path pubPath) throws Exception {
        Files.deleteIfExists(privPath);
        Files.deleteIfExists(pubPath);
        ProcessBuilder pb = new ProcessBuilder(
                "ssh-keygen", "-t", "rsa", "-b", "2048", "-f", privPath.toAbsolutePath().toString(), "-N", "", "-C", "ocx-worker-console"
            )
            .redirectErrorStream(true);
        Process p = pb.start();

        String output;
        try (InputStream in = p.getInputStream()) {
            output = new String(in.readAllBytes());
        }

        p.waitFor();
        if (p.exitValue() != 0) {
            throw new RuntimeException("ssh-keygen failed: " + output);
        } else {
            this.publicKeyContent = Files.readString(pubPath).trim();
        }
    }

    public Map<String, String> createConsoleConnection(String userId, String instanceId, String region) {
        if (this.publicKeyContent != null && !this.publicKeyContent.isEmpty()) {
            OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
            if (ociUser == null) {
                throw new OciException("租户配置不存在");
            } else {
                try {
                    Object var16;
                    try (OciClientService env = this.oci(ociUser, region)) {
                        ComputeClient computeClient = env.getComputeClient();
                        Instance instance = computeClient.getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                        String compartmentId = instance.getCompartmentId();
                        List<InstanceConsoleConnection> existing = computeClient.listInstanceConsoleConnections(
                                ListInstanceConsoleConnectionsRequest.builder().compartmentId(compartmentId).instanceId(instanceId).build()
                            )
                            .getItems();

                        for (InstanceConsoleConnection conn : existing) {
                            LifecycleState state = conn.getLifecycleState();
                            if (state == LifecycleState.Active || state == LifecycleState.Creating) {
                                computeClient.deleteInstanceConsoleConnection(
                                    DeleteInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(conn.getId()).build()
                                );
                                log.info("【串行控制台】删除旧连接: {} (状态: {})", conn.getId(), state);
                            }
                        }

                        if (!existing.isEmpty()) {
                            boolean cleared = false;

                            for (int i = 0; i < 15; i++) {
                                Thread.sleep(2000L);
                                List<InstanceConsoleConnection> check = computeClient.listInstanceConsoleConnections(
                                        ListInstanceConsoleConnectionsRequest.builder().compartmentId(compartmentId).instanceId(instanceId).build()
                                    )
                                    .getItems();
                                boolean allGone = check.stream().allMatch(c -> c.getLifecycleState() == LifecycleState.Deleted);
                                if (allGone || check.isEmpty()) {
                                    cleared = true;
                                    break;
                                }
                            }

                            if (!cleared) {
                                throw new OciException("旧连接尚未完全删除，请稍后再试");
                            }
                        }

                        InstanceConsoleConnection connection = computeClient.createInstanceConsoleConnection(
                                CreateInstanceConsoleConnectionRequest.builder()
                                    .createInstanceConsoleConnectionDetails(
                                        CreateInstanceConsoleConnectionDetails.builder().instanceId(instanceId).publicKey(this.publicKeyContent).build()
                                    )
                                    .build()
                            )
                            .getInstanceConsoleConnection();
                        int maxWait = 15;

                        InstanceConsoleConnection active;
                        for (active = connection;
                            maxWait-- > 0 && active.getLifecycleState() != LifecycleState.Active;
                            active = computeClient.getInstanceConsoleConnection(
                                    GetInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(connection.getId()).build()
                                )
                                .getInstanceConsoleConnection()
                        ) {
                            Thread.sleep(2000L);
                        }

                        if (active.getLifecycleState() != LifecycleState.Active) {
                            throw new OciException("控制台连接创建超时，请稍后重试");
                        }

                        String sshCommand = active.getConnectionString();
                        log.info("【串行控制台】OCI connectionString: {}", sshCommand);
                        ConsoleService.ConsoleSession session = new ConsoleService.ConsoleSession();
                        session.consoleConnectionId = active.getId();
                        session.instanceId = instanceId;
                        session.tenantId = userId;
                        session.sshCommand = sshCommand;
                        session.createdAt = System.currentTimeMillis();
                        this.activeSessions.put(active.getId(), session);
                        Map<String, String> result = new LinkedHashMap<>();
                        result.put("connectionId", active.getId());
                        result.put("sshCommand", sshCommand);
                        result.put("state", active.getLifecycleState().getValue());
                        log.info("【串行控制台】连接已创建: {}", active.getId());
                        var16 = result;
                    }

                    return (Map<String, String>)var16;
                } catch (OciException var19) {
                    throw var19;
                } catch (Exception var20) {
                    throw new OciException("创建控制台连接失败: " + var20.getMessage());
                }
            }
        } else {
            throw new OciException("SSH 密钥未初始化，无法创建控制台连接");
        }
    }

    public void deleteConsoleConnection(String userId, String connectionId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try (OciClientService env = this.oci(ociUser, region)) {
                ComputeClient computeClient = env.getComputeClient();

                try {
                    computeClient.deleteInstanceConsoleConnection(
                        DeleteInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(connectionId).build()
                    );
                } catch (Exception var9) {
                    log.warn("【串行控制台】删除OCI连接失败: {}", var9.getMessage());
                }
            }

            ConsoleService.ConsoleSession session = this.activeSessions.remove(connectionId);
            if (session != null) {
                this.deleteExecScript(session);
            }

            log.info("【串行控制台】连接已断开: {}", connectionId);
        }
    }

    public String buildPreparedSshCommand(String connectionString) {
        if (connectionString != null && !connectionString.isBlank()) {
            String cmd = connectionString.trim();
            String key = this.privateKeyPath;
            if (!cmd.contains("HostkeyAlgorithms")) {
                cmd = cmd.replaceFirst("^ssh\\s+", "ssh -o HostkeyAlgorithms=+ssh-rsa -o PubkeyAcceptedAlgorithms=+ssh-rsa ");
            }

            if (cmd.contains("ProxyCommand='ssh ")) {
                cmd = cmd.replace(
                    "ProxyCommand='ssh ",
                    "ProxyCommand='ssh -i "
                        + key
                        + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 "
                );
            } else if (cmd.contains("ProxyCommand=\"ssh ")) {
                cmd = cmd.replace(
                    "ProxyCommand=\"ssh ",
                    "ProxyCommand=\"ssh -i "
                        + key
                        + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 "
                );
            }

            if (cmd.startsWith("ssh ")) {
                cmd = "ssh -i "
                    + key
                    + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 "
                    + cmd.substring(4);
            }

            return cmd;
        } else {
            throw new OciException("无效的 connectionString");
        }
    }

    public Path getOrCreateExecScript(String connectionId) throws IOException {
        ConsoleService.ConsoleSession session = this.activeSessions.get(connectionId);
        if (session == null) {
            throw new OciException("控制台会话不存在或已过期，请重新创建连接");
        } else {
            if (session.execScriptPath != null) {
                Path existing = Path.of(session.execScriptPath);
                if (!Files.exists(existing)) {
                    session.execScriptPath = null;
                }
            }

            String prepared = this.buildPreparedSshCommand(session.sshCommand);
            Path script = session.execScriptPath != null
                ? Path.of(session.execScriptPath)
                : Path.of("./keys").resolve("console_exec_" + safeId(connectionId) + ".sh");
            String content = "#!/bin/bash\nexport TERM=vt100\nexec " + prepared + "\n";
            Files.writeString(script, content);

            try {
                new ProcessBuilder("chmod", "+x", script.toAbsolutePath().toString()).redirectErrorStream(true).start().waitFor();
            } catch (InterruptedException var7) {
                Thread.currentThread().interrupt();
                throw new IOException("chmod failed", var7);
            }

            session.execScriptPath = script.toAbsolutePath().toString();
            log.info("【串行控制台】执行脚本: {} -> {}", connectionId, prepared);
            return script;
        }
    }

    private void deleteExecScript(ConsoleService.ConsoleSession session) {
        if (session.execScriptPath != null) {
            try {
                Files.deleteIfExists(Path.of(session.execScriptPath));
            } catch (Exception var3) {
                log.warn("【串行控制台】删除脚本失败: {}", var3.getMessage());
            }
        }
    }

    private static String safeId(String connectionId) {
        return connectionId.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void cleanupLegacyTempUsers() {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "grep -o 'oci_console_[0-9]*' /etc/passwd 2>/dev/null").redirectErrorStream(true);
            Process p = pb.start();

            String output;
            try (InputStream in = p.getInputStream()) {
                output = new String(in.readAllBytes()).trim();
            }

            p.waitFor();
            if (!output.isEmpty()) {
                for (String user : output.split("\n")) {
                    user = user.trim();
                    if (!user.isEmpty()) {
                        log.info("【串行控制台】清理旧版临时用户: {}", user);
                        this.cleanupLegacyTempUser(user);
                    }
                }
            }
        } catch (Exception var10) {
            log.warn("【串行控制台】清理旧版临时用户失败: {}", var10.getMessage());
        }
    }

    private void cleanupLegacyTempUser(String user) {
        try {
            Process killAll = Runtime.getRuntime().exec(new String[]{"pkill", "-9", "-u", user});
            killAll.waitFor();
            Thread.sleep(500L);
            Runtime.getRuntime().exec(new String[]{"userdel", "-rf", user}).waitFor();
            Path scriptPath = Path.of("./keys", "console_" + user + ".sh");
            Files.deleteIfExists(scriptPath);
        } catch (Exception var4) {
            log.warn("【串行控制台】清理旧版用户失败: {} - {}", user, var4.getMessage());
        }
    }

    @Scheduled(
        fixedRate = 300000L
    )
    public void periodicCleanup() {
        long cutoff = System.currentTimeMillis() - 7200000L;
        List<String> expired = new ArrayList<>();
        this.activeSessions.forEach((idx, sessionx) -> {
            if (sessionx.createdAt < cutoff) {
                expired.add(idx);
            }
        });

        for (String id : expired) {
            ConsoleService.ConsoleSession session = this.activeSessions.remove(id);
            if (session != null) {
                this.deleteExecScript(session);
                log.info("【串行控制台】清理过期会话: {}", id);
            }
        }
    }

    private SysUserDTO buildDto(OciUser ociUser) {
        return SysUserDTO.builder()
            .username(ociUser.getUsername())
            .ociCfg(
                SysUserDTO.OciCfg.builder()
                    .tenantId(ociUser.getOciTenantId())
                    .userId(ociUser.getOciUserId())
                    .fingerprint(ociUser.getOciFingerprint())
                    .region(ociUser.getOciRegion())
                    .privateKeyPath(ociUser.getOciKeyPath())
                    .build()
            )
            .build();
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region != null && !region.isBlank() ? region.trim() : null;
        return new OciClientService(this.buildDto(ociUser), r);
    }

    public static class ConsoleSession {
        public String consoleConnectionId;
        public String instanceId;
        public String tenantId;
        public String sshCommand;
        public String execScriptPath;
        public long createdAt;
    }
}
