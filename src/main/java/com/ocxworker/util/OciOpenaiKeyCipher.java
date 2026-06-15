package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import java.nio.charset.StandardCharsets;

public final class OciOpenaiKeyCipher {
    private static final String KEY_SALT = "ociworker-openai-key-v1:";

    private OciOpenaiKeyCipher() {
    }

    public static String encrypt(String plain, String webPassword) {
        return StrUtil.isBlank(plain) ? null : getAes(webPassword).encryptBase64(plain);
    }

    public static String decrypt(String encryptedBase64, String webPassword) {
        return StrUtil.isBlank(encryptedBase64) ? null : getAes(webPassword).decryptStr(encryptedBase64);
    }

    public static String maskForDisplay(String plain) {
        if (StrUtil.isBlank(plain)) {
            return "sk-****";
        } else {
            String k = plain.trim();
            if (k.regionMatches(true, 0, "sk-", 0, 3) && k.length() >= 11) {
                return k.substring(0, 7) + "****" + k.substring(k.length() - 4);
            } else {
                return k.length() >= 8 ? k.substring(0, 4) + "****" + k.substring(k.length() - 4) : "sk-****";
            }
        }
    }

    private static AES getAes(String webPassword) {
        String material = "ociworker-openai-key-v1:" + (webPassword == null ? "" : webPassword);
        byte[] key = SecureUtil.sha256().digest(material.getBytes(StandardCharsets.UTF_8));
        return SecureUtil.aes(key);
    }
}
