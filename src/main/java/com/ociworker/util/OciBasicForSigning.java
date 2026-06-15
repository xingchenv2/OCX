/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.util.OciBasicForSigning
 *  com.ociworker.util.OciBasicForSigning$BasicWrapper
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 */
package com.ociworker.util;

import com.ociworker.util.OciBasicForSigning;
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

