/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.ConsoleService
 *  com.ocxworker.service.ConsoleService$ConsoleSession
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.OciProxyConfigService
 *  com.oracle.bmc.core.ComputeClient
 *  com.oracle.bmc.core.model.CreateInstanceConsoleConnectionDetails
 *  com.oracle.bmc.core.model.Instance
 *  com.oracle.bmc.core.model.InstanceConsoleConnection
 *  com.oracle.bmc.core.model.InstanceConsoleConnection$LifecycleState
 *  com.oracle.bmc.core.requests.CreateInstanceConsoleConnectionRequest
 *  com.oracle.bmc.core.requests.DeleteInstanceConsoleConnectionRequest
 *  com.oracle.bmc.core.requests.GetInstanceConsoleConnectionRequest
 *  com.oracle.bmc.core.requests.GetInstanceRequest
 *  com.oracle.bmc.core.requests.ListInstanceConsoleConnectionsRequest
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Lazy
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;
import lombok.Data;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.ConsoleService;
import com.ocxworker.service.OciClientService;
import com.ocxworker.service.OciProxyConfigService;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.CreateInstanceConsoleConnectionDetails;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InstanceConsoleConnection;
import com.oracle.bmc.core.requests.CreateInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.DeleteInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.GetInstanceConsoleConnectionRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.ListInstanceConsoleConnectionsRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
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

/*
 * Exception performing whole class analysis ignored.
 */
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
    private final Map<String, ConsoleSession> activeSessions = new ConcurrentHashMap();

    @PostConstruct
    public void init() {
        try {
            String privContent;
            boolean needRegenerate;
            Path keyDir = Path.of("./keys", new String[0]);
            Files.createDirectories(keyDir, new FileAttribute[0]);
            Path privPath = keyDir.resolve("console_rsa");
            Path pubPath = keyDir.resolve("console_rsa.pub");
            this.privateKeyPath = privPath.toAbsolutePath().toString();
            boolean bl = needRegenerate = !Files.exists(privPath, new LinkOption[0]) || !Files.exists(pubPath, new LinkOption[0]);
            if (!needRegenerate && !(privContent = Files.readString(privPath)).contains("-----BEGIN OPENSSH PRIVATE KEY-----")) {
                log.warn("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5bc6\u94a5\u975e ssh-keygen \u683c\u5f0f\uff0c\u5f3a\u5236\u91cd\u65b0\u751f\u6210...");
                needRegenerate = true;
            }
            if (needRegenerate) {
                this.generateSshKeyPair(privPath, pubPath);
                log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5df2\u751f\u6210 SSH \u5bc6\u94a5: {}", (Object)pubPath.toAbsolutePath());
            } else {
                this.publicKeyContent = Files.readString(pubPath).trim();
                log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5df2\u52a0\u8f7d SSH \u5bc6\u94a5: {}", (Object)pubPath.toAbsolutePath());
            }
        }
        catch (Exception e) {
            log.error("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011SSH \u5bc6\u94a5\u521d\u59cb\u5316\u5931\u8d25: {}", (Object)e.getMessage());
        }
        this.cleanupLegacyTempUsers();
    }

    private void generateSshKeyPair(Path privPath, Path pubPath) throws Exception {
        String output;
        Files.deleteIfExists(privPath);
        Files.deleteIfExists(pubPath);
        ProcessBuilder pb = new ProcessBuilder("ssh-keygen", "-t", "rsa", "-b", "2048", "-f", privPath.toAbsolutePath().toString(), "-N", "", "-C", "ocx-worker-console").redirectErrorStream(true);
        Process p = pb.start();
        try (InputStream in = p.getInputStream();){
            output = new String(in.readAllBytes());
        }
        p.waitFor();
        if (p.exitValue() != 0) {
            throw new RuntimeException("ssh-keygen failed: " + output);
        }
        this.publicKeyContent = Files.readString(pubPath).trim();
    }

    public Map<String, String> createConsoleConnection(String userId, String instanceId, String region) {
        LinkedHashMap<String, String> linkedHashMap;
        block17: {
            if (this.publicKeyContent == null || this.publicKeyContent.isEmpty()) {
                throw new OciException("SSH \u5bc6\u94a5\u672a\u521d\u59cb\u5316\uff0c\u65e0\u6cd5\u521b\u5efa\u63a7\u5236\u53f0\u8fde\u63a5");
            }
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService env = this.oci(ociUser, region);
            try {
                ComputeClient computeClient = env.getComputeClient();
                Instance instance = computeClient.getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                String compartmentId = instance.getCompartmentId();
                List existing = computeClient.listInstanceConsoleConnections(ListInstanceConsoleConnectionsRequest.builder().compartmentId(compartmentId).instanceId(instanceId).build()).getItems();
                for (InstanceConsoleConnection conn : existing) {
                    InstanceConsoleConnection.LifecycleState state = conn.getLifecycleState();
                    if (state != InstanceConsoleConnection.LifecycleState.Active && state != InstanceConsoleConnection.LifecycleState.Creating) continue;
                    computeClient.deleteInstanceConsoleConnection(DeleteInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(conn.getId()).build());
                    log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5220\u9664\u65e7\u8fde\u63a5: {} (\u72b6\u6001: {})", (Object)conn.getId(), (Object)state);
                }
                if (!existing.isEmpty()) {
                    boolean cleared = false;
                    for (int i = 0; i < 15; ++i) {
                        Thread.sleep(2000L);
                        List check = computeClient.listInstanceConsoleConnections(ListInstanceConsoleConnectionsRequest.builder().compartmentId(compartmentId).instanceId(instanceId).build()).getItems();
                        boolean allGone = check.stream().allMatch(c -> c.getLifecycleState() == InstanceConsoleConnection.LifecycleState.Deleted);
                        if (!allGone && !check.isEmpty()) continue;
                        cleared = true;
                        break;
                    }
                    if (!cleared) {
                        throw new OciException("\u65e7\u8fde\u63a5\u5c1a\u672a\u5b8c\u5168\u5220\u9664\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5");
                    }
                }
                InstanceConsoleConnection connection = computeClient.createInstanceConsoleConnection(CreateInstanceConsoleConnectionRequest.builder().createInstanceConsoleConnectionDetails(CreateInstanceConsoleConnectionDetails.builder().instanceId(instanceId).publicKey(this.publicKeyContent).build()).build()).getInstanceConsoleConnection();
                int maxWait = 15;
                InstanceConsoleConnection active = connection;
                while (maxWait-- > 0 && active.getLifecycleState() != InstanceConsoleConnection.LifecycleState.Active) {
                    Thread.sleep(2000L);
                    active = computeClient.getInstanceConsoleConnection(GetInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(connection.getId()).build()).getInstanceConsoleConnection();
                }
                if (active.getLifecycleState() != InstanceConsoleConnection.LifecycleState.Active) {
                    throw new OciException("\u63a7\u5236\u53f0\u8fde\u63a5\u521b\u5efa\u8d85\u65f6\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
                }
                String sshCommand = active.getConnectionString();
                log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011OCI connectionString: {}", (Object)sshCommand);
                ConsoleSession session = new ConsoleSession();
                session.consoleConnectionId = active.getId();
                session.instanceId = instanceId;
                session.tenantId = userId;
                session.sshCommand = sshCommand;
                session.createdAt = System.currentTimeMillis();
                this.activeSessions.put(active.getId(), session);
                LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
                result.put("connectionId", active.getId());
                result.put("sshCommand", sshCommand);
                result.put("state", active.getLifecycleState().getValue());
                log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u8fde\u63a5\u5df2\u521b\u5efa: {}", (Object)active.getId());
                linkedHashMap = result;
                if (env == null) break block17;
            }
            catch (Throwable throwable) {
                try {
                    if (env != null) {
                        try {
                            env.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new OciException("\u521b\u5efa\u63a7\u5236\u53f0\u8fde\u63a5\u5931\u8d25: " + e.getMessage());
                }
            }
            env.close();
        }
        return linkedHashMap;
    }

    public void deleteConsoleConnection(String userId, String connectionId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService env = this.oci(ociUser, region);){
            ComputeClient computeClient = env.getComputeClient();
            try {
                computeClient.deleteInstanceConsoleConnection(DeleteInstanceConsoleConnectionRequest.builder().instanceConsoleConnectionId(connectionId).build());
            }
            catch (Exception e) {
                log.warn("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5220\u9664OCI\u8fde\u63a5\u5931\u8d25: {}", (Object)e.getMessage());
            }
        }
        ConsoleSession session = (ConsoleSession)this.activeSessions.remove(connectionId);
        if (session != null) {
            this.deleteExecScript(session);
        }
        log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u8fde\u63a5\u5df2\u65ad\u5f00: {}", (Object)connectionId);
    }

    public String buildPreparedSshCommand(String connectionString) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new OciException("\u65e0\u6548\u7684 connectionString");
        }
        Object cmd = connectionString.trim();
        String key = this.privateKeyPath;
        if (!((String)cmd).contains("HostkeyAlgorithms")) {
            cmd = ((String)cmd).replaceFirst("^ssh\\s+", "ssh -o HostkeyAlgorithms=+ssh-rsa -o PubkeyAcceptedAlgorithms=+ssh-rsa ");
        }
        if (((String)cmd).contains("ProxyCommand='ssh ")) {
            cmd = ((String)cmd).replace("ProxyCommand='ssh ", "ProxyCommand='ssh -i " + key + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 ");
        } else if (((String)cmd).contains("ProxyCommand=\"ssh ")) {
            cmd = ((String)cmd).replace("ProxyCommand=\"ssh ", "ProxyCommand=\"ssh -i " + key + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 ");
        }
        if (((String)cmd).startsWith("ssh ")) {
            cmd = "ssh -i " + key + " -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ServerAliveInterval=15 -o ServerAliveCountMax=3 " + ((String)cmd).substring(4);
        }
        return cmd;
    }

    public Path getOrCreateExecScript(String connectionId) throws IOException {
        Path existing;
        ConsoleSession session = (ConsoleSession)this.activeSessions.get(connectionId);
        if (session == null) {
            throw new OciException("\u63a7\u5236\u53f0\u4f1a\u8bdd\u4e0d\u5b58\u5728\u6216\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u521b\u5efa\u8fde\u63a5");
        }
        if (session.execScriptPath != null && !Files.exists(existing = Path.of(session.execScriptPath, new String[0]), new LinkOption[0])) {
            session.execScriptPath = null;
        }
        String prepared = this.buildPreparedSshCommand(session.sshCommand);
        Path script = session.execScriptPath != null ? Path.of(session.execScriptPath, new String[0]) : Path.of("./keys", new String[0]).resolve("console_exec_" + ConsoleService.safeId((String)connectionId) + ".sh");
        String content = "#!/bin/bash\nexport TERM=vt100\nexec " + prepared + "\n";
        Files.writeString(script, (CharSequence)content, new OpenOption[0]);
        try {
            new ProcessBuilder("chmod", "+x", script.toAbsolutePath().toString()).redirectErrorStream(true).start().waitFor();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("chmod failed", e);
        }
        session.execScriptPath = script.toAbsolutePath().toString();
        log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u6267\u884c\u811a\u672c: {} -> {}", (Object)connectionId, (Object)prepared);
        return script;
    }

    private void deleteExecScript(ConsoleSession session) {
        if (session.execScriptPath == null) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(session.execScriptPath, new String[0]));
        }
        catch (Exception e) {
            log.warn("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u5220\u9664\u811a\u672c\u5931\u8d25: {}", (Object)e.getMessage());
        }
    }

    private static String safeId(String connectionId) {
        return connectionId.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void cleanupLegacyTempUsers() {
        try {
            String output;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "grep -o 'oci_console_[0-9]*' /etc/passwd 2>/dev/null").redirectErrorStream(true);
            Process p = pb.start();
            try (InputStream in = p.getInputStream();){
                output = new String(in.readAllBytes()).trim();
            }
            p.waitFor();
            if (!output.isEmpty()) {
                for (String user : output.split("\n")) {
                    if ((user = user.trim()).isEmpty()) continue;
                    log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u6e05\u7406\u65e7\u7248\u4e34\u65f6\u7528\u6237: {}", (Object)user);
                    this.cleanupLegacyTempUser(user);
                }
            }
        }
        catch (Exception e) {
            log.warn("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u6e05\u7406\u65e7\u7248\u4e34\u65f6\u7528\u6237\u5931\u8d25: {}", (Object)e.getMessage());
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
        }
        catch (Exception e) {
            log.warn("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u6e05\u7406\u65e7\u7248\u7528\u6237\u5931\u8d25: {} - {}", (Object)user, (Object)e.getMessage());
        }
    }

    @Scheduled(fixedRate=300000L)
    public void periodicCleanup() {
        long cutoff = System.currentTimeMillis() - 0x6DDD00L;
        ArrayList expired = new ArrayList();
        this.activeSessions.forEach((id, session) -> {
            if (session.createdAt < cutoff) {
                expired.add(id);
            }
        });
        for (String id2 : expired) {
            ConsoleSession session2 = (ConsoleSession)this.activeSessions.remove(id2);
            if (session2 == null) continue;
            this.deleteExecScript(session2);
            log.info("\u3010\u4e32\u884c\u63a7\u5236\u53f0\u3011\u6e05\u7406\u8fc7\u671f\u4f1a\u8bdd: {}", (Object)id2);
        }
    }

    private SysUserDTO buildDto(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region == null || region.isBlank() ? null : region.trim();
        return new OciClientService(this.buildDto(ociUser), r);
    }
}

    @Data
    class ConsoleSession {
        private String consoleConnectionId;
        private String instanceId;
        private String tenantId;
        private String sshCommand;
        private String execScriptPath;
        private long createdAt;
    }


