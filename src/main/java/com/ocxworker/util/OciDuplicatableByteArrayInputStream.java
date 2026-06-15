package com.ocxworker.util;

import com.oracle.bmc.http.client.io.DuplicatableInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class OciDuplicatableByteArrayInputStream extends InputStream implements DuplicatableInputStream {
    private final byte[] bytes;
    private ByteArrayInputStream delegate;

    public OciDuplicatableByteArrayInputStream(byte[] bytes) {
        this.bytes = bytes == null ? new byte[0] : bytes;
        this.delegate = new ByteArrayInputStream(this.bytes);
    }

    public InputStream duplicate() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public int read() {
        return this.delegate.read();
    }

    @Override
    public int read(byte[] b, int off, int len) {
        return this.delegate.read(b, off, len);
    }

    @Override
    public int available() {
        return this.delegate.available();
    }

    @Override
    public synchronized void reset() {
        this.delegate = new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}
