package com.ocxworker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class SecretCompare {
    private SecretCompare() {
    }

    public static boolean equalsUtf8(String a, String b) {
        if (a == null) {
            a = "";
        }

        if (b == null) {
            b = "";
        }

        byte[] ba = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        return ba.length != bb.length ? false : MessageDigest.isEqual(ba, bb);
    }
}
