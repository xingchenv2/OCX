package com.ocxworker.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocxworker.mapper.OciKvMapper;
import com.ocxworker.model.entity.OciKv;
import com.ocxworker.model.params.LoginParams;
import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.LoginAuditService;
import com.ocxworker.service.LoginSecurityService;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.VerifyCodeService;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.HttpRequestUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth"})
public class AuthController {
    @Value("${web.account}")
    private String defaultAccount;
    @Value("${web.password}")
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
    private static final String CODE_ACCOUNT = "web_account";
    private static final String CODE_PASSWORD = "web_password";
    private static final String TYPE = "sys_config";

    private String getKv(String code) {
        OciKv kv = (OciKv)this.kvMapper
            .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, code)).eq(OciKv::getType, "sys_config"));
        return kv != null ? kv.getValue() : null;
    }

    private void setKv(String code, String value) {
        OciKv existing = (OciKv)this.kvMapper
            .selectOne((Wrapper)(new LambdaQueryWrapper<OciKv>().eq(OciKv::getCode, code)).eq(OciKv::getType, "sys_config"));
        if (existing != null) {
            existing.setValue(value);
            this.kvMapper.updateById(existing);
        } else {
            OciKv kv = new OciKv();
            kv.setId(CommonUtils.generateId());
            kv.setCode(code);
            kv.setValue(value);
            kv.setType("sys_config");
            this.kvMapper.insert(kv);
        }
    }

    private boolean isSetupDone() {
        return this.getKv("web_account") != null || this.getKv("web_password") != null;
    }

    public String getEffectiveAccount() {
        String stored = this.getKv("web_account");
        return stored != null ? stored : this.defaultAccount;
    }

    private boolean isHashedPassword(String pwd) {
        return pwd != null && pwd.length() == 64 && pwd.matches("[0-9a-f]+");
    }

    public String getEffectivePasswordHash() {
        String stored = this.getKv("web_password");
        if (stored != null) {
            if (this.isHashedPassword(stored)) {
                return stored;
            } else {
                String hashed = DigestUtil.sha256Hex(stored);
                this.setKv("web_password", hashed);
                return hashed;
            }
        } else {
            return DigestUtil.sha256Hex(this.defaultPassword);
        }
    }

    @GetMapping({"/needSetup"})
    public ResponseData<?> needSetup() {
        return ResponseData.ok(!this.isSetupDone());
    }

    @PostMapping({"/setup"})
    public ResponseData<?> setup(@RequestBody Map<String, String> params) {
        if (this.isSetupDone()) {
            return ResponseData.error("系统已初始化，无法重复设置");
        } else {
            String account = params.get("account");
            String password = params.get("password");
            if (account == null || account.length() < 3) {
                return ResponseData.error("用户名至少3个字符");
            } else if (password != null && password.length() >= 6) {
                this.setKv("web_account", account);
                this.setKv("web_password", DigestUtil.sha256Hex(password));
                String token = CommonUtils.generateToken(account, DigestUtil.sha256Hex(password));
                return ResponseData.ok(Map.of("token", token, "account", account));
            } else {
                return ResponseData.error("密码至少6个字符");
            }
        }
    }

    @GetMapping({"/device"})
    public ResponseEntity<Void> ensureDeviceCookie(HttpServletRequest request, HttpServletResponse response) {
        String existing = HttpRequestUtil.getCookie(request, "ow_did");
        if (StrUtil.isBlank(existing)) {
            String id = CommonUtils.generateId();
            ResponseCookie cookie = ResponseCookie.from("ow_did", id).httpOnly(true).path("/").maxAge(Duration.ofDays(365L)).sameSite("Lax").build();
            response.addHeader("Set-Cookie", cookie.toString());
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping({"/login"})
    public ResponseData<?> login(@RequestBody @Valid LoginParams params, HttpServletRequest request) {
        if (!this.isSetupDone()) {
            return ResponseData.error(403, "请先完成初始化设置");
        } else {
            String ip = HttpRequestUtil.getClientIp(request);
            String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
            if (this.loginSecurityService.isDeniedForLogin(ip, deviceId)) {
                this.loginAuditService.recordPasswordLogin(params.getAccount(), params.getPassword(), ip, deviceId, false, request);
                return ResponseData.error(403, "访问被拒绝");
            } else {
                String effectiveAccount = this.getEffectiveAccount();
                String effectivePwdHash = this.getEffectivePasswordHash();
                String inputPwdHash = DigestUtil.sha256Hex(params.getPassword());
                if (effectiveAccount.equals(params.getAccount()) && effectivePwdHash.equals(inputPwdHash)) {
                    this.loginAuditService.recordPasswordLogin(effectiveAccount, params.getPassword(), ip, deviceId, true, request);
                    String token = CommonUtils.generateToken(effectiveAccount, effectivePwdHash);
                    this.notificationService
                        .sendMessage("login", String.format("【登录通知】✅ 登录成功\n账号: %s\nIP: %s\n时间: %s", params.getAccount(), ip, this.nowStr()));
                    return ResponseData.ok(Map.of("token", token, "account", effectiveAccount, "expireHours", 24));
                } else {
                    this.loginAuditService.recordPasswordLogin(params.getAccount(), params.getPassword(), ip, deviceId, false, request);
                    this.loginSecurityService.onPasswordLoginFailed(params.getAccount(), ip, deviceId);
                    return ResponseData.error("账号或密码错误");
                }
            }
        }
    }

    @PostMapping({"/tgLoginSendCode"})
    public ResponseData<?> tgLoginSendCode(HttpServletRequest request) {
        if (!this.verifyCodeService.isTgConfigured()) {
            return ResponseData.error("未绑定 Telegram Bot，无法使用此登录方式");
        } else if (!this.isSetupDone()) {
            return ResponseData.error(403, "请先完成初始化设置");
        } else {
            String ip = HttpRequestUtil.getClientIp(request);
            String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
            if (this.loginSecurityService.isDeniedForLogin(ip, deviceId)) {
                return ResponseData.error(403, "访问被拒绝");
            } else {
                long now = System.currentTimeMillis();
                if (this.tgLoginCodeSentAt > 0L) {
                    long sinceLastSend = now - this.tgLoginCodeSentAt;
                    if (sinceLastSend < 30000L) {
                        long wait = (30000L - sinceLastSend) / 1000L;
                        return ResponseData.error("请求过于频繁，请 " + wait + " 秒后重试");
                    }

                    if (sinceLastSend >= 60000L) {
                        this.tgSendBurstCount = 0;
                    }
                }

                if (this.tgSendBurstCount >= 3) {
                    long sinceLastSendx = now - this.tgLoginCodeSentAt;
                    if (sinceLastSendx < 60000L) {
                        long wait = Math.max(1L, (60000L - sinceLastSendx + 999L) / 1000L);
                        return ResponseData.error("已连续发码 3 次，请等待 " + wait + " 秒后再试");
                    }
                }

                String numPart = RandomUtil.randomNumbers(6);
                String mixPart = RandomUtil.randomString("ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789", 11);
                String code = numPart + ":" + mixPart;
                this.tgLoginCode = code;
                this.tgLoginCodeExpireAt = now + 30000L;
                this.tgLoginCodeSentAt = now;
                this.tgSendBurstCount++;
                this.tgLoginFailCount.set(0);
                String html = String.format("Your token: <code>%s</code>\n\n请在 <b>30</b> 秒内使用该验证码登录\n\n<i>IP: %s</i>", code, ip);
                this.notificationService.sendTelegramHtml(html, null);
                return ResponseData.ok(Map.of("message", "验证码已发送到 Telegram"));
            }
        }
    }

    @PostMapping({"/tgLogin"})
    public ResponseData<?> tgLogin(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (!this.verifyCodeService.isTgConfigured()) {
            return ResponseData.error("未绑定 Telegram Bot");
        } else if (!this.isSetupDone()) {
            return ResponseData.error(403, "请先完成初始化设置");
        } else {
            String ip = HttpRequestUtil.getClientIp(request);
            String deviceId = this.loginSecurityService.readDeviceIdFromRequest(request);
            String tgAcct = this.getEffectiveAccount();
            if (this.loginSecurityService.isDeniedForLogin(ip, deviceId)) {
                this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(封禁拦截)");
                return ResponseData.error(403, "访问被拒绝");
            } else {
                String inputCode = params.get("code");
                if (inputCode == null || inputCode.isBlank()) {
                    this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(未填验证码)");
                    return ResponseData.error("请输入验证码");
                } else if (this.tgLoginCode == null) {
                    this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(未获取验证码)");
                    return ResponseData.error("请先获取验证码");
                } else if (System.currentTimeMillis() > this.tgLoginCodeExpireAt) {
                    this.tgLoginCode = null;
                    this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(验证码过期)");
                    return ResponseData.error("验证码已过期，请重新获取");
                } else if (this.tgLoginFailCount.get() >= 3) {
                    this.tgLoginCode = null;
                    this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, "(验证锁定)");
                    this.notificationService.sendMessage(String.format("【登录通知】\ud83d\udea8 TG验证码登录被锁定\n连续错误 %d 次\nIP: %s\n时间: %s", 3, ip, this.nowStr()));
                    return ResponseData.error("验证码错误次数过多，已失效，请重新获取");
                } else if (!this.tgLoginCode.equals(inputCode)) {
                    this.loginAuditService.recordTelegramLogin(tgAcct, ip, deviceId, false, request, inputCode.trim());
                    int fails = this.tgLoginFailCount.incrementAndGet();
                    int remaining = 3 - fails;
                    if (remaining <= 0) {
                        this.tgLoginCode = null;
                        this.notificationService.sendMessage(String.format("【登录通知】\ud83d\udea8 TG验证码登录被锁定\n连续错误 %d 次\nIP: %s\n时间: %s", 3, ip, this.nowStr()));
                        return ResponseData.error("验证码错误次数过多，已失效");
                    } else {
                        return ResponseData.error("验证码错误，剩余 " + remaining + " 次尝试机会");
                    }
                } else {
                    this.tgLoginCode = null;
                    this.tgLoginFailCount.set(0);
                    String effectiveAccount = this.getEffectiveAccount();
                    String effectivePwdHash = this.getEffectivePasswordHash();
                    String token = CommonUtils.generateToken(effectiveAccount, effectivePwdHash);
                    this.loginAuditService.recordTelegramLogin(effectiveAccount, ip, deviceId, true, request, "(TG验证码)");
                    this.notificationService.sendMessage("login", String.format("【登录通知】✅ TG验证码登录成功\nIP: %s\n时间: %s", ip, this.nowStr()));
                    return ResponseData.ok(Map.of("token", token, "account", effectiveAccount, "expireHours", 24));
                }
            }
        }
    }

    @GetMapping({"/account"})
    public ResponseData<?> currentAccount() {
        return ResponseData.ok(Map.of("account", this.getEffectiveAccount()));
    }

    @GetMapping({"/tgLoginAvailable"})
    public ResponseData<?> tgLoginAvailable() {
        return ResponseData.ok(this.verifyCodeService.isTgConfigured());
    }

    @PostMapping({"/verifyPassword"})
    public ResponseData<?> verifyPassword(@RequestBody Map<String, String> params) {
        String pwd = params.get("password");
        if (pwd != null && !pwd.isBlank()) {
            return !this.getEffectivePasswordHash().equals(DigestUtil.sha256Hex(pwd)) ? ResponseData.error("密码错误") : ResponseData.ok();
        } else {
            return ResponseData.error("请输入密码");
        }
    }

    @PostMapping({"/changePassword"})
    public ResponseData<?> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        if (this.verifyCodeService.isTgConfigured()) {
            String code = params.get("verifyCode");
            if (code == null || code.isBlank()) {
                return ResponseData.error("请输入 TG 验证码");
            }

            this.verifyCodeService.verifyCode("changePassword", code);
        }

        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        if (oldPwd != null && newPwd != null && newPwd.length() >= 6) {
            String effectivePwdHash = this.getEffectivePasswordHash();
            if (!effectivePwdHash.equals(DigestUtil.sha256Hex(oldPwd))) {
                return ResponseData.error("原密码错误");
            } else {
                String newHash = DigestUtil.sha256Hex(newPwd);
                this.setKv("web_password", newHash);
                String account = this.getEffectiveAccount();
                String newToken = CommonUtils.generateToken(account, newHash);
                if (this.verifyCodeService.isTgConfigured()) {
                    String ip = HttpRequestUtil.getClientIp(request);
                    this.notificationService
                        .sendMessage(String.format("【登录通知】\ud83d\udd10 面板登录密码已成功修改\n账号: %s\nIP: %s\n时间: %s\n\n如非本人操作，请立即检查账户安全。", account, ip, this.nowStr()));
                }

                return ResponseData.ok(Map.of("token", newToken, "account", account, "message", "密码修改成功"));
            }
        } else {
            return ResponseData.error("新密码不能少于6位");
        }
    }

    private String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
