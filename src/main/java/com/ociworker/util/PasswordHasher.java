package com.ociworker.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.regex.Pattern;

/**
 * PasswordHasher — Secure password hashing using bcrypt.
 *
 * Replaces DigestUtil.sha256Hex() for password storage.
 * SHA-256 is a fast digest unsuitable for passwords (GPU can compute billions/sec).
 * bcrypt is slow, adaptive, and salted — the standard for password hashing.
 *
 * Migration: On login, if the stored hash looks like SHA-256 (64 hex chars),
 * we verify it, then automatically upgrade to bcrypt.
 */
public final class PasswordHasher {

    private static final int BCRYPT_COST = 12; // ~250ms per hash, good balance
    private static final Pattern SHA256_PATTERN = Pattern.compile("^[a-f0-9]{64}$");

    private PasswordHasher() {}

    /**
     * Hash a plain-text password with bcrypt.
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verify a plain-text password against a stored hash.
     * Supports both bcrypt and legacy SHA-256 hashes.
     *
     * @return VerifyResult indicating success and whether the hash should be upgraded.
     */
    public static VerifyResult verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return VerifyResult.FAIL;

        // Check if it's a bcrypt hash ($2a$, $2b$, $2y$)
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            try {
                BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash);
                return result.verified ? VerifyResult.OK_BCRYPT : VerifyResult.FAIL;
            } catch (Exception e) {
                return VerifyResult.FAIL;
            }
        }

        // Legacy SHA-256: verify using timing-safe comparison
        if (SHA256_PATTERN.matcher(storedHash).matches()) {
            String inputHash = cn.hutool.crypto.digest.DigestUtil.sha256Hex(plainPassword);
            boolean match = SecretCompare.equalsUtf8(inputHash, storedHash);
            return match ? VerifyResult.OK_NEED_UPGRADE : VerifyResult.FAIL;
        }

        return VerifyResult.FAIL;
    }

    /**
     * Check if a stored hash is a legacy SHA-256 that needs upgrading to bcrypt.
     */
    public static boolean needsUpgrade(String storedHash) {
        if (storedHash == null) return false;
        return SHA256_PATTERN.matcher(storedHash).matches();
    }

    public enum VerifyResult {
        FAIL,           // Password does not match
        OK_BCRYPT,      // Matched bcrypt hash — current standard
        OK_NEED_UPGRADE // Matched legacy SHA-256 — should re-hash with bcrypt
    }
}
