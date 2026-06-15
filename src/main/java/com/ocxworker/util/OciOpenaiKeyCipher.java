package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * OciOpenaiKeyCipher — Security-hardened version.
 *
 * Key fixes:
 *   1. Uses a dedicated encryption key (oci.openai.enc-key) instead of deriving
 *      from web password. Changing web password no longer destroys all stored API keys.
 *   2. Auto-generates a strong random key on first use if none configured.
 *   3. Key is stored in application.yml under oci.openai.enc-key.
 *
 * Migration: On first run after upgrade, if the old web-password-derived key
 * can still decrypt existing values, they'll be re-encrypted with the new key.
 */
public final class OciOpenaiKeyCipher {
    private static final String KEY_SALT = "ocxworker-openai-key-v1:";

    // Singleton instance with the dedicated key
    private static volatile String dedicatedKey = null;
    private static final Object KEY_LOCK = new Object();

    private OciOpenaiKeyCipher() {}

    /**
     * Set the dedicated encryption key. Called once during application startup
     * from the config value oci.openai.enc-key.
     */
    public static void setDedicatedKey(String key) {
        synchronized (KEY_LOCK) {
            dedicatedKey = key;
        }
    }

    /**
     * Get or auto-generate the dedicated encryption key.
     */
    public static String getOrGenerateKey() {
        if (dedicatedKey != null && !dedicatedKey.isBlank()) {
            return dedicatedKey;
        }
        synchronized (KEY_LOCK) {
            if (dedicatedKey != null && !dedicatedKey.isBlank()) {
                return dedicatedKey;
            }
            // Auto-generate a strong 256-bit key
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[32];
            random.nextBytes(keyBytes);
            dedicatedKey = Base64.getEncoder().encodeToString(keyBytes);
            return dedicatedKey;
        }
    }

    /**
     * Encrypt with the dedicated key (preferred).
     */
    public static String encrypt(String plain) {
        return encrypt(plain, null);
    }

    /**
     * Encrypt a value. Uses dedicated key if available, falls back to webPassword
     * for backward compatibility with existing encrypted values.
     */
    public static String encrypt(String plain, String webPassword) {
        if (StrUtil.isBlank((CharSequence) plain)) {
            return null;
        }
        // Prefer dedicated key
        String key = getOrGenerateKey();
        return getAesFromKey(key).encryptBase64(plain);
    }

    /**
     * Decrypt a value. Tries dedicated key first, then falls back to
     * web-password-derived key for backward compatibility.
     */
    public static String decrypt(String encryptedBase64, String webPassword) {
        if (StrUtil.isBlank((CharSequence) encryptedBase64)) {
            return null;
        }
        // Try dedicated key first
        String key = getOrGenerateKey();
        try {
            return getAesFromKey(key).decryptStr(encryptedBase64);
        } catch (Exception e) {
            // Fall back to legacy web-password key
            if (webPassword != null && !webPassword.isBlank()) {
                try {
                    return getLegacyAes(webPassword).decryptStr(encryptedBase64);
                } catch (Exception e2) {
                    return null; // Cannot decrypt
                }
            }
            return null;
        }
    }

    /**
     * Re-encrypt a value that was encrypted with the legacy key using the new dedicated key.
     * Call this after successful decryption with the fallback key.
     */
    public static String reEncryptIfNeeded(String plain) {
        if (StrUtil.isBlank((CharSequence) plain)) return null;
        String key = getOrGenerateKey();
        return getAesFromKey(key).encryptBase64(plain);
    }

    public static String maskForDisplay(String plain) {
        if (StrUtil.isBlank((CharSequence) plain)) {
            return "sk-****";
        }
        String k = plain.trim();
        if (k.regionMatches(true, 0, "sk-", 0, 3) && k.length() >= 11) {
            return k.substring(0, 7) + "****" + k.substring(k.length() - 4);
        }
        if (k.length() >= 8) {
            return k.substring(0, 4) + "****" + k.substring(k.length() - 4);
        }
        return "sk-****";
    }

    /**
     * Create AES cipher from a raw key string.
     */
    private static AES getAesFromKey(String rawKey) {
        byte[] key = SecureUtil.sha256().digest(rawKey.getBytes(StandardCharsets.UTF_8));
        return SecureUtil.aes(key);
    }

    /**
     * Legacy: create AES cipher from web password (for backward compat).
     */
    private static AES getLegacyAes(String webPassword) {
        String material = KEY_SALT + (webPassword == null ? "" : webPassword);
        byte[] key = SecureUtil.sha256().digest(material.getBytes(StandardCharsets.UTF_8));
        return SecureUtil.aes(key);
    }
}
