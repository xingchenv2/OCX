package com.ociworker.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ociworker.mapper.OciKvMapper;
import com.ociworker.model.entity.OciKv;
import com.ociworker.model.params.LoginParams;
import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.LoginAuditService;
import com.ociworker.service.LoginSecurityService;
import com.ociworker.service.NotificationService;
import com.ociworker.service.VerifyCodeService;
import com.ociworker.util.CommonUtils;
import com.ociworker.util.HttpRequestUtil;
import com.ociworker.util.PasswordHasher;
import com.ociworker.util.SecretCompare;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController — Security-hardened version.
 *
 * Key fixes vs original decompiled code:
 *   1. Passwords now stored/verified with bcrypt (PasswordHasher) instead of SHA-256.
 *   2. Tokens use random nonces (CommonUtils.generateToken now includes nonce).
 *   3. Setup endpoint uses AtomicBoolean to prevent race-condition takeover.
 *   4. loginAuditService no longer receives the plaintext password (passes "" instead).
 *   5. verifyPassword/changePassword use timing-safe bcrypt comparison.
 *   6. Password comparison uses constant-time SecretCompare for legacy SHA-256 path.
 */
@RestController
@RequestMapping(value = {"/api/auth"})
public class AuthController {
    @Value(value = "${web.account}")
    private String defaultAccount;
    @Value(value = "${web.password}")
    private String defaultPassword;
    @Resource
    private OciKvMapper kvMapper;
    @Resource
    private NotificationService notificationService;
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private LoginSecurityService loginSecurityService;
    @Resource
    private LoginAuditService loginAuditService;

    private static final long TG_CODE_EXPIRE_MS = 30000L;
    private static final int TG_CODE_MAX_ATTEMPTS = 3;
    private static final int TG_SEND_BURST_MAX = 3;
    private static final long TG_SEND_BURST_COOLDOWN_MS = 60000L;

    private volatile String tgLoginCode;
    private volatile long tgLoginCodeExpireAt;
    private volatile long tgLoginCodeSentAt;
    private volatile int tgSendBurstCount;
    private final AtomicInteger tgLoginFailCount = new AtomicInteger(0);

    // FIX: Race-condition protection for setup endpoint
    private final AtomicBoolean setupInProgress = new AtomicBoolean(false);

    private static final String CODE_ACCOUNT = "web_account";
    private static final String CODE_PASSWORD = "web_password";
    private static final String TYPE = "sys_config";

    private String getKv(String code) {
        OciKv kv = (OciKv) this.kvMapper.selectOne(
            (Wrapper) ((LambdaQueryWrapper) new LambdaQueryWrapper()
                .eq(OciKv::getCode, (Object) code))
                .eq(OciKv::getType, (Object) TYPE));
        return kv != null ? kv.getValue() : null;
    }

    private void setKv(String code, String value) {
        OciKv existing = (OciKv) this.kvMapper.selectOne(
            (Wrapper) ((LambdaQueryWrapper) new LambdaQueryWrapper()
                .eq(OciKv::getCode, (Object) code))
                .eq(OciKv::getType, (Object) TYPE));
        if (existing != null) {
            existing.setValue(value);
            this.kvMapper.updateById((Object) existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode(code);
            kv.setValue(value);
            kv.setType(TYPE);
            this.kvMapper.insert((Object) kv);
        }
    }

    private boolean isSetupDone() {
        return this.getKv(CODE_ACCOUNT) != null || this.getKv(CODE_PASSWORD) != null;
    }

    public String getEffectiveAccount() {
        String stored = this.getKv(CODE_ACCOUNT);
        return stored != null ? stored : this.defaultAccount;
    }

    /**
     * Get the stored password hash. Uses bcrypt if already migrated,
     * otherwise returns legacy SHA-256 hash for backward compatibility.
     */
    public String getEffectivePasswordHash() {
        String stored = this.getKv(CODE_PASSWORD);
        if (stored != null) {
            // If already bcrypt, return as-is
            if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                return stored;
            }
            // Legacy SHA-256 — return as-is for verification, will upgrade on login
            return stored;
        }
        // Fallback to default password from config (also likely SHA-256)
        return DigestUtil.sha256Hex((String) this.defaultPassword);
    }

    @GetMapping(value = {"/needSetup"})
    public ResponseData<?> needSetup() {
        return ResponseData.ok((Object) (!this.isSetupDone() ? 1 : 0));
    }

    /**
     * Setup endpoint — FIX: uses AtomicBoolean to prevent race-condition takeover.
     * Two concurrent setup requests can no longer both succeed.
     */
    @PostMapping(value = {"/setup"})
    public ResponseData<?> setup(@RequestBody Map<String, String> params) {
        // Double-check + atomic guard
        if (this.isSetupDone()) {
            return ResponseData.error((String) "系统已初始化，无法重复设置");
        }
        if (!this.setupInProgress.compareAndSet(false, true)) {
            return ResponseData.error((String) "初始化正在进行中，请勿重复提交");
        }
        try {
            // Re-check inside lock
            if (this.isSetupDone()) {
                return ResponseData.error((String) "系统已初始化，无法重复设置");
            }
            String account = params.get("account");
            String password = params.get("password");
            if (account == null || account.length() < 3) {
                return ResponseData.error((String) "用户名至少3个字符");
            }
            if (password == null || password.length() < 8) {
                return ResponseData.error((String) "密码至少8个字符");
            }
            // FIX: Store password as bcrypt, not SHA-256
            String bcryptHash = PasswordHasher.hash(password);
            this.setKv(CODE_ACCOUNT, account);
            this.setKv(CODE_PASSWORD, bcryptHash);
            String token = CommonUtils.generateToken(account, bcryptHash);
            return ResponseData.ok(Map.of("token", token, "account", account));
        } finally {
            this.setupInProgress.set(false);
        }
    }

    @GetMapping(value = {"/device"})
    public ResponseEntity<Void> ensureDeviceCookie(HttpServletRequest request, HttpServletResponse response) {
        String existing = HttpRequestUtil.getCookie((HttpServletRequest) request, (String) "ow_did");
        if (StrUtil.isBlank((CharSequence) existing)) {
            String id = CommonUtils.generateId();
            ResponseCookie cookie = ResponseCookie.from((String) "ow_did", (String) id)
                .httpOnly(true).path("/").maxAge(Duration.ofDays(365L)).sameSite("Lax")
                .secure(true) // FIX: cookie only sent over HTTPS
                .build();
            response.addHeader("Set-Cookie", cookie.toString());
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Login endpoint — FIX: uses bcrypt for new accounts, auto-upgrades SHA-256→bcrypt.
     * FIX: no longer passes plaintext password to audit service.
     */
    @PostMapping(value = {"/login"})
    public ResponseData<?> login(@RequestBody @Valid LoginParams params, HttpServletRequest request) {
        String deviceId;
        if (!this.isSetupDone()) {
            return ResponseData.error((int) 403, (String) "请先完成初始化设置");
        }
        String ip = HttpRequestUtil.getClientIp((HttpServletRequest) request);
        if (this.loginSecurityService.isDeniedForLogin(ip,
                deviceId = this.loginSecurityService.readDeviceIdFromRequest(request))) {
            // FIX: Do NOT log the plaintext password
            this.loginAuditService.recordPasswordLogin(params.getAccount(), "", ip, deviceId, false, request);
            return ResponseData.error((int) 403, (String) "访问被拒绝");
        }

        String effectiveAccount = this.getEffectiveAccount();
        String effectivePwdHash = this.getEffectivePasswordHash();

        // FIX: Use PasswordHasher for constant-time verification
        // For legacy SHA-256: inputPwdSha256 is computed for comparison
        PasswordHasher.VerifyResult result;
        if (effectivePwdHash.startsWith("$2a$") || effectivePwdHash.startsWith("$2b$") || effectivePwdHash.startsWith("$2y$")) {
            // bcrypt verification
            result = PasswordHasher.verify(params.getPassword(), effectivePwdHash);
        } else {
            // Legacy SHA-256 → verify then upgrade
            String inputPwdHash = DigestUtil.sha256Hex(params.getPassword());
            boolean match = SecretCompare.equalsUtf8(inputPwdHash, effectivePwdHash);
            result = match ? PasswordHasher.VerifyResult.OK_NEED_UPGRADE : PasswordHasher.VerifyResult.FAIL;
        }

        // Verify account matches (constant-time comparison)
        boolean accountMatch = SecretCompare.equalsUtf8(params.getAccount(), effectiveAccount);

        if (!accountMatch || result == PasswordHasher.VerifyResult.FAIL) {
            // FIX: Do NOT log the plaintext password
            this.loginAuditService.recordPasswordLogin(params.getAccount(), "", ip, deviceId, false, request);
            this.loginSecurityService.onPasswordLoginFailed(params.getAccount(), ip, deviceId);
            return ResponseData.error((String) "账号或密码错误");
        }

        // Auto-upgrade: if matched on legacy SHA-256, re-hash with bcrypt
        if (result == PasswordHasher.VerifyResult.OK_NEED_UPGRADE) {
            String bcryptHash = PasswordHasher.hash(params.getPassword());
            this.setKv(CODE_PASSWORD, bcryptHash);
            effectivePwdHash = bcryptHash;
        }

        // FIX: Do NOT log the plaintext password
        this.loginAuditService.recordPasswordLogin(effectiveAccount, "", ip, deviceId, true, request);
        String token = CommonUtils.generateToken(effectiveAccount, effectivePwdHash);
        this.notificationService.sendMessage("login",
            String.format("【登录通知】✅ 登录成功\n账号: %s\nIP: %s\n时间: %s",
                params.getAccount(), ip, this.nowStr()));
        return ResponseData.ok(Map.of("token", token, "account", effectiveAccount, "expireHours", 24));
    }

    @PostMapping(value = {"/tgLoginSendCode"})
    public ResponseData<?> tgLoginSendCode(HttpServletRequest request) {
        long sinceLastSend;
        String deviceId;
        if (!this.verifyCodeService.isTgConfigured()) {
            return ResponseData.error((String) "未绑定 Telegram Bot，无法使用此登录方式");
        }
        if (!this.isSetupDone()) {
            return ResponseData.error((int) 403, (String) "请先完成初始化设置");
        }
        String ip = HttpRequestUtil.getClientIp((HttpServletRequest) request);
        if (this.loginSecurityService.isDeniedForLogin(ip,
                deviceId = this.loginSecurityService.readDeviceIdFromRequest(request))) {
            return ResponseData.error((int) 403, (String) "访问被拒绝");
        }
        long now = System.currentTimeMillis();
        if (this.tgLoginCodeSentAt > 0L) {
            sinceLastSend = now - this.tgLoginCodeSentAt;
            if (sinceLastSend < 30000L) {
                long wait = (30000L - sinceLastSend) / 1000L;
                return ResponseData.error((String) ("请求过于频繁，请 " + wait + " 秒后重试"));
            }
            if (sinceLastSend >= 60000L) {
                this.tgSendBurstCount = 0;
            }
        }
        if (this.tgSendBurstCount >= 3 && (sinceLastSend = now - this.tgLoginCodeSentAt) < 60000L) {
            long wait = Math.max(1L, (60000L - sinceLastSend + 999L) / 1000L);
            return ResponseData.error((String) ("已连续发码 3 次，请等待 " + wait + " 秒后再试"));
        }
        String numPart = RandomUtil.randomNumbers((int) 6);
        String mixPart = RandomUtil.randomString(
            (String) "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789", (int) 11);
        String code = numPart + ":" + mixPart;
        this.tgLoginCode = code;
        this.tgLoginCodeExpireAt = now + 30000L;
        this.tgLoginCodeSentAt = now;
        ++this.tgSendBurstCount;
        this.tgLoginFailCount.set(0);
        String html = String.format(
            "Your token: <code>%s</code>\n\n请在 <b>30</b> 秒内使用该验证码登录\n\n<i>IP: %s</i>",
            code, ip);
        this.notificationService.sendTelegramHtml(html, null);
        return ResponseData.ok(Map.of("message", "验证码已发送到 Telegram"));
    }

    @PostMapping(value = {"/tgLogin"})
    public ResponseData<?> tgLogin(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!this.verifyCodeService.isTgConfigured()) {
            return ResponseData.error((String) "未绑定 Telegram Bot");
        }
        if (!this.isSetupDone()) {
            return ResponseData.error((int) 403, (String) "请先完成初始化设置");
        }
        String ip = HttpRequestUtil.getClientIp((HttpServletRequest) request);
        String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
        String tgAcct = this.getEffectiveAccount();
        if (this.loginSecurityService.isDeniedForLogin(ip, deviceId)) {
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(封禁拦截)");
            return ResponseData.error((int) 403, (String) "访问被拒绝");
        }
        String inputCode = params.get("code");
        if (inputCode == null || inputCode.isBlank()) {
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(未填验证码)");
            return ResponseData.error((String) "请输入验证码");
        }
        if (this.tgLoginCode == null) {
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(未获取验证码)");
            return ResponseData.error((String) "请先获取验证码");
        }
        if (System.currentTimeMillis() > this.tgLoginCodeExpireAt) {
            this.tgLoginCode = null;
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(验证码过期)");
            return ResponseData.error((String) "验证码已过期，请重新获取");
        }
        if (this.tgLoginFailCount.get() >= 3) {
            this.tgLoginCode = null;
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(验证锁定)");
            this.notificationService.sendMessage(
                String.format("【登录通知】🚨 TG验证码登录被锁定\n连续错误 %d 次\nIP: %s\n时间: %s",
                    3, ip, this.nowStr()));
            return ResponseData.error((String) "验证码错误次数过多，已失效，请重新获取");
        }
        // FIX: Use constant-time comparison for verification code
        if (!SecretCompare.equalsUtf8(inputCode, this.tgLoginCode)) {
            this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, inputCode.trim());
            int fails = this.tgLoginFailCount.incrementAndGet();
            int remaining = 3 - fails;
            if (remaining <= 0) {
                this.tgLoginCode = null;
                this.notificationService.sendMessage(
                    String.format("【登录通知】🚨 TG验证码登录被锁定\n连续错误 %d 次\nIP: %s\n时间: %s",
                        3, ip, this.nowStr()));
                return ResponseData.error((String) "验证码错误次数过多，已失效");
            }
            return ResponseData.error((String) ("验证码错误，剩余 " + remaining + " 次尝试机会"));
        }
        this.tgLoginCode = null;
        this.tgLoginFailCount.set(0);
        String effectiveAccount = this.getEffectiveAccount();
        String effectivePwdHash = this.getEffectivePasswordHash();
        String token = CommonUtils.generateToken(effectiveAccount, effectivePwdHash);
        this.loginAuditService.recordTelegramLogin(effectiveAccount, ip, deviceId, true, request, "(TG验证码)");
        this.notificationService.sendMessage("login",
            String.format("【登录通知】✅ TG验证码登录成功\nIP: %s\n时间: %s", ip, this.nowStr()));
        return ResponseData.ok(Map.of("token", token, "account", effectiveAccount, "expireHours", 24));
    }

    @GetMapping(value = {"/account"})
    public ResponseData<?> currentAccount() {
        return ResponseData.ok(Map.of("account", this.getEffectiveAccount()));
    }

    @GetMapping(value = {"/tgLoginAvailable"})
    public ResponseData<?> tgLoginAvailable() {
        return ResponseData.ok((Object) this.verifyCodeService.isTgConfigured());
    }

    /**
     * FIX: verifyPassword now uses bcrypt/constant-time comparison.
     */
    @PostMapping(value = {"/verifyPassword"})
    public ResponseData<?> verifyPassword(@RequestBody Map<String, String> params) {
        String pwd = params.get("password");
        if (pwd == null || pwd.isBlank()) {
            return ResponseData.error((String) "请输入密码");
        }
        String effectivePwdHash = this.getEffectivePasswordHash();
        PasswordHasher.VerifyResult result = PasswordHasher.verify(pwd, effectivePwdHash);
        if (result == PasswordHasher.VerifyResult.FAIL) {
            return ResponseData.error((String) "密码错误");
        }
        // Auto-upgrade if legacy
        if (result == PasswordHasher.VerifyResult.OK_NEED_UPGRADE) {
            String bcryptHash = PasswordHasher.hash(pwd);
            this.setKv(CODE_PASSWORD, bcryptHash);
        }
        return ResponseData.ok();
    }

    /**
     * FIX: changePassword now uses bcrypt, minimum 8 chars, constant-time comparison.
     */
    @PostMapping(value = {"/changePassword"})
    public ResponseData<?> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (this.verifyCodeService.isTgConfigured()) {
            String code = params.get("verifyCode");
            if (code == null || code.isBlank()) {
                return ResponseData.error((String) "请输入 TG 验证码");
            }
            this.verifyCodeService.verifyCode("changePassword", code);
        }
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        if (oldPwd == null || newPwd == null || newPwd.length() < 8) {
            return ResponseData.error((String) "新密码不能少于8位");
        }
        String effectivePwdHash = this.getEffectivePasswordHash();
        PasswordHasher.VerifyResult result = PasswordHasher.verify(oldPwd, effectivePwdHash);
        if (result == PasswordHasher.VerifyResult.FAIL) {
            return ResponseData.error((String) "原密码错误");
        }
        // FIX: Store new password as bcrypt
        String newHash = PasswordHasher.hash(newPwd);
        this.setKv(CODE_PASSWORD, newHash);
        String account = this.getEffectiveAccount();
        String newToken = CommonUtils.generateToken(account, newHash);
        if (this.verifyCodeService.isTgConfigured()) {
            String ip = HttpRequestUtil.getClientIp((HttpServletRequest) request);
            this.notificationService.sendMessage(
                String.format("【登录通知】🔓 面板登录密码已成功修改\n账号: %s\nIP: %s\n时间: %s\n\n如非本人操作，请立即检查账户安全。",
                    account, ip, this.nowStr()));
        }
        return ResponseData.ok(Map.of("token", newToken, "account", account, "message", "密码修改成功"));
    }

    private String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
