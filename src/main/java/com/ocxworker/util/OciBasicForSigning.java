package com.ocxworker.util;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import java.io.InputStream;
import java.util.Objects;

public final class OciBasicForSigning {
    private OciBasicForSigning() {
    }

    public static BasicAuthenticationDetailsProvider from(SimpleAuthenticationDetailsProvider simple) {
        Objects.requireNonNull(simple, "simple");
        return new OciBasicForSigning.BasicWrapper(simple);
    }

    private static final class BasicWrapper implements BasicAuthenticationDetailsProvider {
        private final SimpleAuthenticationDetailsProvider inner;

        BasicWrapper(SimpleAuthenticationDetailsProvider inner) {
            this.inner = inner;
        }

        public String getKeyId() {
            return this.inner.getKeyId();
        }

        public InputStream getPrivateKey() {
            return this.inner.getPrivateKey();
        }

        @Deprecated
        public String getPassPhrase() {
            return this.inner.getPassPhrase();
        }

        public char[] getPassphraseCharacters() {
            return this.inner.getPassphraseCharacters();
        }
    }
}
