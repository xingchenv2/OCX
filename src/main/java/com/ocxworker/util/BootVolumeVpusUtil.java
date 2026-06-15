/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.util.BootVolumeVpusUtil
 */
package com.ocxworker.util;

/*
 * Exception performing whole class analysis ignored.
 */
public final class BootVolumeVpusUtil {
    public static final int DEFAULT = 10;
    public static final int MIN = 10;
    public static final int MAX = 120;
    public static final int STEP = 10;

    private BootVolumeVpusUtil() {
    }

    public static int normalize(Integer vpusPerGB) {
        if (vpusPerGB == null || vpusPerGB <= 0) {
            return 10;
        }
        int v = vpusPerGB;
        if (v < 10) {
            return 10;
        }
        if (v > 120) {
            return 120;
        }
        int rem = v % 10;
        if (rem == 0) {
            return v;
        }
        int down = v - rem;
        return down < 10 ? 10 : down;
    }

    public static String formatDiskWithVpus(int diskGb, int vpusPerGB) {
        return diskGb + "GB(" + BootVolumeVpusUtil.normalize((Integer)vpusPerGB) + "VPUs)";
    }
}

