/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.util.ShapeFlexLimitsUtil
 *  com.ocxworker.util.ShapeFlexLimitsUtil$FlexLimits
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.util.ShapeFlexLimitsUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Exception performing whole class analysis ignored.
 */
public final class ShapeFlexLimitsUtil {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ShapeFlexLimitsUtil.class);
    public static final String ARM_TASK_SHAPE = "VM.Standard.A1.Flex";
    public static final String AMD_TASK_SHAPE = "VM.Standard.E2.1.Micro";
    private static final Map<String, FlexLimits> SPECS = ShapeFlexLimitsUtil.buildSpecs();

    private ShapeFlexLimitsUtil() {
    }

    private static Map<String, FlexLimits> buildSpecs() {
        HashMap m = new HashMap();
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.E6.Flex", (float)1.0f, (float)11.0f, (float)126.0f, (float)1454.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.E6.Ax.Flex", (float)1.0f, (float)7.0f, (float)94.0f, (float)712.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.E5.Flex", (float)1.0f, (float)12.0f, (float)126.0f, (float)2098.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.E4.Flex", (float)1.0f, (float)16.0f, (float)114.0f, (float)1760.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard3.Flex", (float)1.0f, (float)16.0f, (float)56.0f, (float)896.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Optimized3.Flex", (float)1.0f, (float)14.0f, (float)18.0f, (float)256.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard4.Ax.Flex", (float)1.0f, (float)9.0f, (float)39.0f, (float)360.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.A1.Flex", (float)1.0f, (float)6.0f, (float)80.0f, (float)512.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.A2.Flex", (float)1.0f, (float)6.0f, (float)78.0f, (float)946.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.A4.Flex", (float)1.0f, (float)7.0f, (float)45.0f, (float)700.0f);
        ShapeFlexLimitsUtil.put(m, (String)"VM.Standard.E3.Flex", (float)1.0f, (float)16.0f, (float)114.0f, (float)1776.0f);
        return Map.copyOf(m);
    }

    private static void put(Map<String, FlexLimits> m, String shape, float defO, float defM, float maxO, float maxM) {
        m.put(shape.toUpperCase(), new FlexLimits(defO, defM, maxO, maxM));
    }

    public static FlexLimits forShape(String shapeName) {
        if (StrUtil.isBlank((CharSequence)shapeName)) {
            return null;
        }
        return (FlexLimits)SPECS.get(shapeName.trim().toUpperCase());
    }

    public static FlexLimits forTaskArchitecture(String architecture) {
        if (StrUtil.isBlank((CharSequence)architecture)) {
            return null;
        }
        String arch = architecture.trim();
        if ("ARM".equalsIgnoreCase(arch)) {
            return (FlexLimits)SPECS.get("VM.Standard.A1.Flex".toUpperCase());
        }
        if ("AMD".equalsIgnoreCase(arch)) {
            return new FlexLimits(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return ShapeFlexLimitsUtil.forShape((String)arch);
    }

    public static double[] normalizeOcpusAndMemory(String architecture, Double ocpus, Double memory) {
        double m;
        double o;
        FlexLimits lim = ShapeFlexLimitsUtil.forTaskArchitecture((String)architecture);
        double d = ocpus != null ? ocpus : (o = lim != null ? (double)lim.defaultOcpus() : 1.0);
        double d2 = memory != null ? memory : (m = lim != null ? (double)lim.defaultMemoryGb() : 6.0);
        if (lim == null) {
            return new double[]{o, m};
        }
        double co = Math.min(Math.max(o, 1.0), (double)lim.maxOcpus());
        double cm = Math.min(Math.max(m, 1.0), (double)lim.maxMemoryGb());
        return new double[]{co, cm};
    }

    public static double[] normalizeAndLogIfAdjusted(String architecture, Double ocpus, Double memory, String context) {
        double beforeO = ocpus != null ? ocpus : -1.0;
        double beforeM = memory != null ? memory : -1.0;
        double[] out = ShapeFlexLimitsUtil.normalizeOcpusAndMemory((String)architecture, (Double)ocpus, (Double)memory);
        if (ocpus != null && ocpus != out[0] || memory != null && memory != out[1]) {
            log.warn("{} \u8d44\u6e90\u914d\u7f6e\u5df2\u6309 Shape \u4e0a\u9650\u8c03\u6574: arch={} ocpus {} -> {}, memory {} -> {}", new Object[]{context, architecture, beforeO, out[0], beforeM, out[1]});
        }
        return out;
    }
}

