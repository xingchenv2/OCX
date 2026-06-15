package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.oracle.bmc.Region;

public final class OciRegionUtil {
    private OciRegionUtil() {
    }

    public static String publicRegionId(String regionId) {
        if (StrUtil.isBlank(regionId)) {
            throw new OciException("Region 不能为空");
        } else {
            String trimmed = regionId.trim();

            try {
                return Region.fromRegionCodeOrId(trimmed).getRegionId();
            } catch (IllegalArgumentException var7) {
                for (Region r : Region.values()) {
                    if (trimmed.equalsIgnoreCase(r.getRegionId())) {
                        return r.getRegionId();
                    }
                }

                throw new OciException("未知 Region: " + regionId);
            }
        }
    }

    public static Region toRegion(String regionId) {
        return Region.fromRegionCodeOrId(publicRegionId(regionId));
    }
}
