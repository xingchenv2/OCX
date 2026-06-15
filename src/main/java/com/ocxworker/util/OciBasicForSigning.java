/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.util.OciBasicForSigning
 *  com.ocxworker.util.OciBasicForSigning$BasicWrapper
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 */
package com.ocxworker.util;

import com.ocxworker.util.OciBasicForSigning;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import java.util.Objects;

public final class OciBasicForSigning {
    private OciBasicForSigning() {
    }

    public static BasicAuthenticationDetailsProvider from(SimpleAuthenticationDetailsProvider simple) {
        Objects.requireNonNull(simple, "simple");
        return new BasicWrapper(simple);
    }
}

