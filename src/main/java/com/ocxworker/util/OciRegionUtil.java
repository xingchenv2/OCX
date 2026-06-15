/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.util.OciRegionUtil
 *  com.oracle.bmc.Region
 */
package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.oracle.bmc.Region;

/*
 * Exception performing whole class analysis ignored.
 */
public final class OciRegionUtil {
    private OciRegionUtil() {
    }

    public static String publicRegionId(String regionId) {
        if (StrUtil.isBlank((CharSequence)regionId)) {
            throw new OciException("Region \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String trimmed = regionId.trim();
        try {
            return Region.fromRegionCodeOrId((String)trimmed).getRegionId();
        }
        catch (IllegalArgumentException ignored) {
            for (Region r : Region.values()) {
                if (!trimmed.equalsIgnoreCase(r.getRegionId())) continue;
                return r.getRegionId();
            }
            throw new OciException("\u672a\u77e5 Region: " + regionId);
        }
    }

    public static Region toRegion(String regionId) {
        return Region.fromRegionCodeOrId((String)OciRegionUtil.publicRegionId((String)regionId));
    }
}

