/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.util.SecretCompare
 */
package com.ocxworker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class SecretCompare {
    private SecretCompare() {
    }

    public static boolean equalsUtf8(String a, String b) {
        byte[] bb;
        byte[] ba;
        if (a == null) {
            a = "";
        }
        if (b == null) {
            b = "";
        }
        if ((ba = a.getBytes(StandardCharsets.UTF_8)).length != (bb = b.getBytes(StandardCharsets.UTF_8)).length) {
            return false;
        }
        return MessageDigest.isEqual(ba, bb);
    }
}

