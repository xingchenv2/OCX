package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShapeFlexLimitsUtil {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ShapeFlexLimitsUtil.class);
    public static final String ARM_TASK_SHAPE = "VM.Standard.A1.Flex";
    public static final String AMD_TASK_SHAPE = "VM.Standard.E2.1.Micro";
    private static final Map<String, ShapeFlexLimitsUtil.FlexLimits> SPECS = buildSpecs();

    private ShapeFlexLimitsUtil() {
    }

    private static Map<String, ShapeFlexLimitsUtil.FlexLimits> buildSpecs() {
        Map<String, ShapeFlexLimitsUtil.FlexLimits> m = new HashMap<>();
        put(m, "VM.Standard.E6.Flex", 1.0F, 11.0F, 126.0F, 1454.0F);
        put(m, "VM.Standard.E6.Ax.Flex", 1.0F, 7.0F, 94.0F, 712.0F);
        put(m, "VM.Standard.E5.Flex", 1.0F, 12.0F, 126.0F, 2098.0F);
        put(m, "VM.Standard.E4.Flex", 1.0F, 16.0F, 114.0F, 1760.0F);
        put(m, "VM.Standard3.Flex", 1.0F, 16.0F, 56.0F, 896.0F);
        put(m, "VM.Optimized3.Flex", 1.0F, 14.0F, 18.0F, 256.0F);
        put(m, "VM.Standard4.Ax.Flex", 1.0F, 9.0F, 39.0F, 360.0F);
        put(m, "VM.Standard.A1.Flex", 1.0F, 6.0F, 80.0F, 512.0F);
        put(m, "VM.Standard.A2.Flex", 1.0F, 6.0F, 78.0F, 946.0F);
        put(m, "VM.Standard.A4.Flex", 1.0F, 7.0F, 45.0F, 700.0F);
        put(m, "VM.Standard.E3.Flex", 1.0F, 16.0F, 114.0F, 1776.0F);
        return Map.copyOf(m);
    }

    private static void put(Map<String, ShapeFlexLimitsUtil.FlexLimits> m, String shape, float defO, float defM, float maxO, float maxM) {
        m.put(shape.toUpperCase(), new ShapeFlexLimitsUtil.FlexLimits(defO, defM, maxO, maxM));
    }

    public static ShapeFlexLimitsUtil.FlexLimits forShape(String shapeName) {
        return StrUtil.isBlank(shapeName) ? null : SPECS.get(shapeName.trim().toUpperCase());
    }

    public static ShapeFlexLimitsUtil.FlexLimits forTaskArchitecture(String architecture) {
        if (StrUtil.isBlank(architecture)) {
            return null;
        } else {
            String arch = architecture.trim();
            if ("ARM".equalsIgnoreCase(arch)) {
                return SPECS.get("VM.Standard.A1.Flex".toUpperCase());
            } else {
                return "AMD".equalsIgnoreCase(arch) ? new ShapeFlexLimitsUtil.FlexLimits(1.0F, 1.0F, 1.0F, 1.0F) : forShape(arch);
            }
        }
    }

    public static double[] normalizeOcpusAndMemory(String architecture, Double ocpus, Double memory) {
        ShapeFlexLimitsUtil.FlexLimits lim = forTaskArchitecture(architecture);
        double o = ocpus != null ? ocpus : (lim != null ? (double)lim.defaultOcpus() : 1.0);
        double m = memory != null ? memory : (lim != null ? (double)lim.defaultMemoryGb() : 6.0);
        if (lim == null) {
            return new double[]{o, m};
        } else {
            double co = Math.min(Math.max(o, 1.0), (double)lim.maxOcpus());
            double cm = Math.min(Math.max(m, 1.0), (double)lim.maxMemoryGb());
            return new double[]{co, cm};
        }
    }

    public static double[] normalizeAndLogIfAdjusted(String architecture, Double ocpus, Double memory, String context) {
        double beforeO = ocpus != null ? ocpus : -1.0;
        double beforeM = memory != null ? memory : -1.0;
        double[] out = normalizeOcpusAndMemory(architecture, ocpus, memory);
        if (ocpus != null && ocpus != out[0] || memory != null && memory != out[1]) {
            log.warn("{} 资源配置已按 Shape 上限调整: arch={} ocpus {} -> {}, memory {} -> {}", new Object[]{context, architecture, beforeO, out[0], beforeM, out[1]});
        }

        return out;
    }

    public static record FlexLimits(float defaultOcpus, float defaultMemoryGb, float maxOcpus, float maxMemoryGb) {
    }
}
