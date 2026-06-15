# OCI Worker — Security Vulnerability Fixes

This document describes all security vulnerabilities identified in the
original codebase and the fixes applied in this repository.

> **Audited**: 2025-06-15  
> **Auditor**: 星辰助手 (AI security review)  
> **Scope**: Shell scripts (`install.sh`, `ociworker`) + decompiled Java backend

---

## Critical (P0) — Fixed

### #1 SHA-256 Password Hashing (not bcrypt)
- **File**: `AuthController.java`, `CommonUtils.java`
- **Risk**: SHA-256 is a fast digest — GPUs compute billions/sec. Offline cracking trivial.
- **Fix**: New `PasswordHasher.java` uses bcrypt (cost 12). Auto-migration on login from legacy SHA-256 → bcrypt.

### #2 Deterministic Token (no random component)
- **File**: `CommonUtils.java` — `generateToken()`
- **Risk**: Token = SHA256(account:password:daySlot). Attacker who knows credentials can forge tokens.
- **Fix**: Token now includes a random 24-byte nonce stored server-side. Token format: `base64(sha256).nonce`. Validation requires both correct hash AND valid nonce in store. Nonces are revocable (logout) and auto-expire.

### #3 WebSocket Endpoints Fully Unauthenticated
- **File**: `WebSocketConfig.java`, `AuthInterceptor.java`
- **Risk**: `/ws/log` exposes all backend logs. `/webssh-api/term` allows SSH terminal access. Both bypassed auth.
- **Fix**: Added `WebSocketAuthHandshakeInterceptor` that validates auth token from query parameter/header/cookie before allowing any WebSocket connection. AuthInterceptor no longer skips `/ws/` and `/webssh-api/` paths.

### #4 CORS Wildcard + Credentials
- **File**: `WebMvcConfig.java`
- **Risk**: `allowedOriginPatterns("*")` + `allowCredentials(true)` enables any website to make authenticated cross-origin requests (CSRF).
- **Fix**: CORS restricted to configured origins only (`cors.allowed-origins` property). No wildcards. Default: same-origin only.

### #5 Setup Race Condition
- **File**: `AuthController.java` — `/api/auth/setup`
- **Risk**: Two concurrent setup requests could both succeed — attacker races the legitimate owner to hijack admin.
- **Fix**: `AtomicBoolean setupInProgress` guard with double-check inside lock. Minimum password length raised from 6 → 8.

### #6 SSH Credentials in Plaintext over WebSocket
- **File**: `WebSshConnectInfoParser.java`
- **Risk**: SSH passwords/keys transmitted as plain Base64 over WebSocket. Network observer can read all SSH credentials.
- **Fix**: Added payload size validation, required field checks. **Note**: Full fix requires WSS (TLS) + server-side credential storage so client never sends raw credentials. This is documented as requiring further infrastructure work.

### #7 Login Audit Records Plaintext Passwords
- **File**: `LoginAuditService.java`
- **Risk**: `passwordAttempt` field stored the user's actual login password in the database. Any DB breach exposes all passwords.
- **Fix**: `passwordAttempt` now only stores `[success]` or `[failed]`. Raw `Cookie`, `Authorization`, and request body are no longer stored in `loginDetail` JSON.

---

## High (P1) — Fixed

### #8 WebSshConnectInfo.toString() Leaks Credentials
- **File**: `WebSshConnectInfo.java`
- **Risk**: `toString()` printed password, private key, and passphrase in cleartext. Any logging framework calling toString() would expose SSH credentials.
- **Fix**: `toString()` now redacts all sensitive fields (`password=****`, `privateKey=****`, `passphrase=****`, `proxyPass=****`).

### #9 API Key Encryption Derived from Web Password
- **File**: `OciOpenaiKeyCipher.java`
- **Risk**: Encryption key for stored OpenAI keys was derived from web login password. Changing password destroys ability to decrypt keys.
- **Fix**: Uses dedicated `oci.openai.enc-key` property. Auto-generates a strong 256-bit random key on first use. Falls back to legacy web-password key for decrypting existing values, then re-encrypts with new key.

### #10 SQL Injection in install.sh
- **File**: `install.sh` — `check_database_quality()`
- **Risk**: `DB_NAME` interpolated directly into SQL strings — injection via crafted database name.
- **Fix**: Added `sql_escape_ident()` and `sql_escape_literal()` helper functions. All 4+ SQL injection points now use these.

### #11 MYSQL_PWD Environment Variable Leak
- **File**: `install.sh`, `ociworker`
- **Risk**: `MYSQL_PWD` env var visible to any user via `/proc/<pid>/environ`.
- **Fix**: Replaced all `MYSQL_PWD` usage with `--defaults-file=<temp.cnf>` approach via `_mysql_cnf()` helper. Temp `.cnf` files created with `chmod 600` and cleaned up after use.

### #12 AuthInterceptor Duplicates Credential Logic
- **File**: `AuthInterceptor.java`
- **Risk**: Password hash resolution duplicated between AuthController and AuthInterceptor — drift causes auth bypass.
- **Fix**: AuthInterceptor now delegates to `AuthController.getEffectiveAccount()` and `AuthController.getEffectivePasswordHash()` via `@Autowired`.

---

## Medium (P2) — Fixed

### #13 Context Path Injection in `ociworker` CLI
- **File**: `ociworker`
- **Risk**: User-supplied `context` value interpolated into URL without validation.
- **Fix**: Added input validation — context must match `^[a-zA-Z0-9._-]+$`.

### #14 Backup File Permissions
- **File**: `ociworker` — `cmd_backup()`
- **Risk**: Backup tar.gz contains database dumps + private keys, but created with default umask (644 world-readable).
- **Fix**: `chmod 600` applied to all backup archives immediately after creation.

### #15 YAML Non-Atomic Write
- **File**: `ociworker` — `yaml_set()`
- **Risk**: Direct write to YAML file can corrupt config on crash/power-loss.
- **Fix**: Uses temp file + `os.replace()` (atomic rename on same filesystem).

### #16 Restore Order Bug
- **File**: `ociworker` — `cmd_restore()`
- **Risk**: Original code restored DB after config, but the DB import needs current config's DB credentials — fails if config was already overwritten.
- **Fix**: Restore order changed: DB first (using current config credentials), then config/keys.

### #17 PEM Key Directory Permissions
- **File**: `ociworker`
- **Risk**: Keys directory world-readable by default.
- **Fix**: `chmod 700` on KEYS_DIR, `chmod 600` on restored individual key files.

### #18 systemd Service Runs as Root
- **File**: `ociworker` — service unit
- **Risk**: Java backend runs as root — any RCE = full system compromise.
- **Fix**: Service unit now includes `User=ociworker`, `Group=ociworker`, `NoNewPrivileges=true`, `ProtectSystem=strict`, `PrivateTmp=true`.

### #19 Docker MySQL Port Binding
- **File**: `install.sh`
- **Risk**: MySQL container bound to `0.0.0.0:3306` by default.
- **Fix**: Changed to `127.0.0.1:3306` — only accessible from localhost.

### #20 Cookie Secure Flag
- **File**: `AuthController.java` — `ensureDeviceCookie()`
- **Risk**: `ow_did` cookie missing `Secure` flag — sent over HTTP, susceptible to interception.
- **Fix**: Added `.secure(true)` to ResponseCookie builder.

---

## Shell Script Specific Fixes

| Fix | File | Description |
|-----|------|-------------|
| SQL injection | install.sh | `sql_escape_ident()` / `sql_escape_literal()` for DB_NAME |
| MYSQL_PWD leak | install.sh + ociworker | `--defaults-file` with `_mysql_cnf()` helper |
| Docker env pw | install.sh | Reverted broken `_FILE` approach; documented risk; rm secrets dir after start |
| Port check | install.sh | `ss -tlnp` check for 3306 before docker run |
| Backup perms | ociworker | `chmod 600` on backup archives |
| YAML atomic | ociworker | `os.replace()` instead of direct write |
| Restore order | ociworker | DB first, then config/keys |
| KEYS_DIR perms | ociworker | `chmod 700` on keys dir, `chmod 600` on key files |
| systemd user | ociworker | `User=ociworker` + hardening directives |

---

## Java Backend Specific Fixes

| Fix | File | Description |
|-----|------|-------------|
| bcrypt passwords | PasswordHasher.java (new) | Replaces SHA-256, auto-migrates on login |
| Random token nonces | CommonUtils.java | Token not derivable from credentials alone |
| bcrypt in AuthController | AuthController.java | All password ops use PasswordHasher |
| Setup race condition | AuthController.java | AtomicBoolean guard |
| No password in audit | LoginAuditService.java | passwordAttempt = [success]/[failed] only |
| No sensitive headers in audit | LoginAuditService.java | Cookie/Authorization redacted from loginDetail |
| WebSocket auth | WebSocketConfig.java | HandshakeInterceptor validates token |
| CORS restriction | WebMvcConfig.java | No wildcard + credentials; config-driven origins |
| toString() leak | WebSshConnectInfo.java | All sensitive fields redacted |
| Dedicated enc key | OciOpenaiKeyCipher.java | No longer derived from web password |
| AuthInterceptor dedup | AuthInterceptor.java | Delegates to AuthController |
| Cookie Secure flag | AuthController.java | ow_did cookie now secure=true |

---

## New Files Added

| File | Purpose |
|------|---------|
| `src/.../util/PasswordHasher.java` | bcrypt password hashing + verification + auto-migration |
| `SECURITY-FIXES.md` | This document |

---

## Remaining Risks (Not Fixed — Require Infrastructure Changes)

1. **WebSocket SSH credentials in transit** — WSS (TLS) must be enabled for all WebSocket connections. Server-side credential storage needed so WebSSH client never transmits raw SSH passwords/keys.
2. **JAR is decompilable** — All .class files can be fully decompiled with CFR. "Closed source" is legal only, not technical. Consider code obfuscation (ProGuard) if IP protection is needed.
3. **No rate limiting on API** — Brute-force protection only on login paths. Other API endpoints have no rate limiting.
4. **No CSRF token** — While CORS is now restricted, a proper CSRF token mechanism would add defense-in-depth.
5. **OCI API credentials in database** — Stored as plaintext or with weak encryption. Need a proper secrets manager (Vault, etc.)
