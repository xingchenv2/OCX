package com.ocxworker.util;

public final class BootVolumeVpusUtil {
    public static final int DEFAULT = 10;
    public static final int MIN = 10;
    public static final int MAX = 120;
    public static final int STEP = 10;

    private BootVolumeVpusUtil() {
    }

    public static int normalize(Integer vpusPerGB) {
        if (vpusPerGB != null && vpusPerGB > 0) {
            int v = vpusPerGB;
            if (v < 10) {
                return 10;
            } else if (v > 120) {
                return 120;
            } else {
                int rem = v % 10;
                if (rem == 0) {
                    return v;
                } else {
                    int down = v - rem;
                    return down < 10 ? 10 : down;
                }
            }
        } else {
            return 10;
        }
    }

    public static String formatDiskWithVpus(int diskGb, int vpusPerGB) {
        return diskGb + "GB(" + normalize(vpusPerGB) + "VPUs)";
    }
}
