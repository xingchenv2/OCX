package com.ocxworker.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CommonUtils — Security-hardened version.
 *
 * Key fixes vs original decompiled code:
 *   1. Token now includes a random nonce (stored server-side) → not derivable from credentials alone.
 *   2. validateToken uses constant-time comparison (already via MessageDigest.isEqual).
 *   3. Password hashing moved to BCrypt via PasswordHasher (this class no longer stores sha256 passwords).
 */
public class CommonUtils {
    public static final String CREATE_TASK_PREFIX = "create_task_";
    public static final String CHANGE_IP_TASK_PREFIX = "change_ip_task_";
    public static final String BEGIN_CREATE_MESSAGE_TEMPLATE =
        "【开机任务】\n\n🚀 开始抢机 🚀\n用户：%s\n时间：%s\nRegion：%s\nCPU类型：%s\nCPU：%s\n内存（GB）：%s\n磁盘大小（GB）：%s\n开机数量：%s\nroot密码：%s";

    private static final long TOKEN_EXPIRE_HOURS = 24L;

    // ---- Token nonce store (in-memory; survives for token lifetime) ----
    // Key: nonce string, Value: expiry timestamp (millis)
    private static final Map<String, Long> tokenNonceStore = new ConcurrentHashMap<>();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long NONCE_TTL_MS = 48 * 60 * 60 * 1000L; // 48h (allows yesterday's slot)

    public static String generateId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * Generate an authentication token that includes a random nonce.
     * Format: Base64(SHA256(account:passwordHash:daySlot:nonce))
     * The nonce is stored server-side so that the token cannot be forged
     * from credentials alone (fixes P0: deterministic token vulnerability).
     *
     * @param account      the login account name
     * @param passwordHash the STORED password hash (bcrypt or sha256)
     * @return Base64-encoded token string
     */
    public static String generateToken(String account, String passwordHash) {
        long expireSlot = System.currentTimeMillis() / 86400000L;
        String nonce = generateNonce();
        String raw = account + ":" + passwordHash + ":" + expireSlot + ":" + nonce;
        String token = Base64.getEncoder().encodeToString(DigestUtil.sha256(raw));
        // Store nonce with expiry
        tokenNonceStore.put(nonce, System.currentTimeMillis() + NONCE_TTL_MS);
        // Return compound token: token.nonce so we can validate both parts
        return token + "." + nonce;
    }

    /**
     * Validate a token against stored credentials.
     * The token must match SHA256(account:passwordHash:daySlot:nonce) AND
     * the nonce must exist in our nonce store (not revoked / not expired).
     */
    public static boolean validateToken(String token, String account, String passwordHash) {
        if (token == null || account == null || passwordHash == null) {
            return false;
        }
        // Split compound token
        int dotIdx = token.lastIndexOf('.');
        if (dotIdx < 0 || dotIdx >= token.length() - 1) {
            // Legacy token format — reject (force re-login after upgrade)
            return false;
        }
        String tokenPart = token.substring(0, dotIdx);
        String nonce = token.substring(dotIdx + 1);

        // Check nonce exists and isn't expired
        Long nonceExpiry = tokenNonceStore.get(nonce);
        if (nonceExpiry == null || System.currentTimeMillis() > nonceExpiry) {
            tokenNonceStore.remove(nonce);
            return false;
        }

        long currentSlot = System.currentTimeMillis() / 86400000L;
        byte[] tokenBytes = tokenPart.getBytes(StandardCharsets.UTF_8);
        // Check current day and previous day slots
        for (int i = 0; i <= 1; i++) {
            String raw = account + ":" + passwordHash + ":" + (currentSlot - i) + ":" + nonce;
            String expected = Base64.getEncoder().encodeToString(DigestUtil.sha256(raw));
            byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(tokenBytes, expectedBytes)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Revoke a specific token nonce (for logout).
     */
    public static void revokeToken(String token) {
        if (token == null) return;
        int dotIdx = token.lastIndexOf('.');
        if (dotIdx >= 0 && dotIdx < token.length() - 1) {
            String nonce = token.substring(dotIdx + 1);
            tokenNonceStore.remove(nonce);
        }
    }

    /**
     * Periodic cleanup of expired nonces (call from scheduled task or heartbeat).
     */
    public static void cleanupExpiredNonces() {
        long now = System.currentTimeMillis();
        tokenNonceStore.entrySet().removeIf(e -> now > e.getValue());
    }

    private static String generateNonce() {
        byte[] bytes = new byte[24];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ---- Password reset shell script generation ----

    public static String getPwdShell(String password) {
        return CommonUtils.getPwdShell(password, null);
    }

    public static String getPwdShell(String password, String customScript) {
        StringBuilder sb = new StringBuilder("#!/bin/bash\n");
        if (password != null && !password.isEmpty()) {
            // Use heredoc + chpasswd to avoid command injection via password string
            String chpasswdLine = "root:" + password + "\n";
            String chpasswdB64 = Base64.getEncoder().encodeToString(chpasswdLine.getBytes(StandardCharsets.UTF_8));
            sb.append("set -e\n");
            sb.append("printf '%s' '").append(chpasswdB64).append("' | base64 -d | chpasswd\n");
            sb.append("set +e\n");
            sb.append("OL_SSH_FIX() {\n");
            sb.append("  sed -i -E 's/^[#[:space:]]*PermitRootLogin[[:space:]].*/PermitRootLogin yes/; ");
            sb.append("s/^[#[:space:]]*PasswordAuthentication[[:space:]].*/PasswordAuthentication yes/' \"$1\" 2>/dev/null || true\n");
            sb.append("}\n");
            sb.append("mkdir -p /etc/ssh/sshd_config.d\n");
            sb.append("if [ -f /etc/ssh/sshd_config ]; then OL_SSH_FIX /etc/ssh/sshd_config; fi\n");
            sb.append("shopt -s nullglob; for f in /etc/ssh/sshd_config.d/*.conf; do ");
            sb.append("OL_SSH_FIX \"$f\"; done; shopt -u nullglob\n");
            sb.append("cat > /etc/ssh/sshd_config.d/99-ocxworker.conf <<'SSHEOF'\n");
            sb.append("PermitRootLogin yes\n");
            sb.append("PasswordAuthentication yes\n");
            sb.append("SSHEOF\n");
            sb.append("# zz- 覆盖 RHEL 系 5x/99 中仍残留项\n");
            sb.append("cat > /etc/ssh/sshd_config.d/zz-ocxworker-override.conf <<'SSHEOF2'\n");
            sb.append("PermitRootLogin yes\n");
            sb.append("PasswordAuthentication yes\n");
            sb.append("SSHEOF2\n");
            sb.append("chmod 644 /etc/ssh/sshd_config.d/99-ocxworker.conf /etc/ssh/sshd_config.d/zz-ocxworker-override.conf 2>/dev/null || true\n");
            sb.append("if getenforce 2>/dev/null | grep -q Enforcing; then restorecon -RFv /etc/ssh /etc/ssh/sshd_config.d 2>/dev/null || true; fi\n");
            sb.append("if sshd -t 2>>/var/log/ocxworker-bootstrap.log; then\n");
            sb.append("  systemctl restart sshd 2>/dev/null || systemctl restart ssh 2>/dev/null || ");
            sb.append("service sshd restart 2>/dev/null || service ssh restart\n");
            sb.append("else\n");
            sb.append("  echo 'ocxworker: sshd -t failed, not restarting ssh' >>/var/log/ocxworker-bootstrap.log\n");
            sb.append("fi\n");
        }
        if (customScript != null && !customScript.trim().isEmpty()) {
            sb.append("\n# --- Custom Script ---\n");
            sb.append(customScript.trim()).append("\n");
        }
        return sb.length() > "#!/bin/bash\n".length() ? sb.toString() : "";
    }
}
