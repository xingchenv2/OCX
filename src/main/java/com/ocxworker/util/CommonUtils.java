package com.ocxworker.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class CommonUtils {
    public static final String CREATE_TASK_PREFIX = "create_task_";
    public static final String CHANGE_IP_TASK_PREFIX = "change_ip_task_";
    public static final String BEGIN_CREATE_MESSAGE_TEMPLATE = "【开机任务】\n\n\ud83d\ude80 开始抢机 \ud83d\ude80\n用户：%s\n时间：%s\nRegion：%s\nCPU类型：%s\nCPU：%s\n内存（GB）：%s\n磁盘大小（GB）：%s\n开机数量：%s\nroot密码：%s";
    private static final long TOKEN_EXPIRE_HOURS = 24L;

    public static String generateId() {
        return IdUtil.fastSimpleUUID();
    }

    public static String generateToken(String account, String password) {
        long expireSlot = System.currentTimeMillis() / 86400000L;
        String raw = account + ":" + password + ":" + expireSlot;
        return Base64.getEncoder().encodeToString(DigestUtil.sha256(raw));
    }

    public static boolean validateToken(String token, String account, String password) {
        if (token != null && account != null && password != null) {
            long currentSlot = System.currentTimeMillis() / 86400000L;
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i <= 1; i++) {
                String raw = account + ":" + password + ":" + (currentSlot - (long)i);
                String expected = Base64.getEncoder().encodeToString(DigestUtil.sha256(raw));
                byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
                if (MessageDigest.isEqual(tokenBytes, expectedBytes)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String getPwdShell(String password) {
        return getPwdShell(password, null);
    }

    public static String getPwdShell(String password, String customScript) {
        StringBuilder sb = new StringBuilder("#!/bin/bash\n");
        if (password != null && !password.isEmpty()) {
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
            sb.append("cat > /etc/ssh/sshd_config.d/99-ociworker.conf <<'SSHEOF'\n");
            sb.append("PermitRootLogin yes\n");
            sb.append("PasswordAuthentication yes\n");
            sb.append("SSHEOF\n");
            sb.append("# zz- 覆盖 RHEL 系 5x/99 中仍残留项\n");
            sb.append("cat > /etc/ssh/sshd_config.d/zz-ociworker-override.conf <<'SSHEOF2'\n");
            sb.append("PermitRootLogin yes\n");
            sb.append("PasswordAuthentication yes\n");
            sb.append("SSHEOF2\n");
            sb.append("chmod 644 /etc/ssh/sshd_config.d/99-ociworker.conf /etc/ssh/sshd_config.d/zz-ociworker-override.conf 2>/dev/null || true\n");
            sb.append("if getenforce 2>/dev/null | grep -q Enforcing; then restorecon -RFv /etc/ssh /etc/ssh/sshd_config.d 2>/dev/null || true; fi\n");
            sb.append("if sshd -t 2>>/var/log/ociworker-bootstrap.log; then\n");
            sb.append("  systemctl restart sshd 2>/dev/null || systemctl restart ssh 2>/dev/null || ");
            sb.append("service sshd restart 2>/dev/null || service ssh restart\n");
            sb.append("else\n");
            sb.append("  echo 'ociworker: sshd -t failed, not restarting ssh' >>/var/log/ociworker-bootstrap.log\n");
            sb.append("fi\n");
        }

        if (customScript != null && !customScript.trim().isEmpty()) {
            sb.append("\n# --- Custom Script ---\n");
            sb.append(customScript.trim()).append("\n");
        }

        return sb.length() > "#!/bin/bash\n".length() ? sb.toString() : "";
    }
}
