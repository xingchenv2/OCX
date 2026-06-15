package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShapeSeriesUtil {
    public static final String SERIES_AMD = "AMD";
    public static final String SERIES_INTEL = "Intel";
    public static final String SERIES_ARM = "ARM（Ampere）";
    public static final String SERIES_SPECIALTY = "专业和上一代";
    public static final String SERIES_BARE_METAL = "裸金属机";
    private static final Map<String, String> FIXED_VM_SHAPE_SERIES = buildFixedVmShapeSeries();

    private ShapeSeriesUtil() {
    }

    public static String resolveSeries(String shapeOrArchitecture) {
        if (StrUtil.isBlank(shapeOrArchitecture)) {
            return "-";
        } else {
            String raw = shapeOrArchitecture.trim();
            String key = raw.toUpperCase();
            if (key.startsWith("BM.")) {
                return "裸金属机";
            } else {
                String fixed = FIXED_VM_SHAPE_SERIES.get(key);
                if (fixed != null) {
                    return fixed;
                } else if ("ARM".equalsIgnoreCase(raw) || "Ampere".equalsIgnoreCase(raw)) {
                    return "ARM（Ampere）";
                } else if ("Intel".equalsIgnoreCase(raw) || "INTEL".equalsIgnoreCase(raw)) {
                    return "Intel";
                } else {
                    return "AMD".equalsIgnoreCase(raw) ? "专业和上一代" : raw;
                }
            }
        }
    }

    public static boolean isFullShapeName(String shapeOrArchitecture) {
        if (StrUtil.isBlank(shapeOrArchitecture)) {
            return false;
        } else {
            String u = shapeOrArchitecture.trim().toUpperCase();
            return u.startsWith("VM.") || u.startsWith("BM.");
        }
    }

    private static Map<String, String> buildFixedVmShapeSeries() {
        Map<String, String> m = new HashMap<>();
        register(m, "ARM（Ampere）", armShapes());
        register(m, "AMD", amdShapes());
        register(m, "Intel", intelShapes());
        register(m, "专业和上一代", specialtyShapes());
        return Map.copyOf(m);
    }

    private static void register(Map<String, String> map, String series, List<String> shapes) {
        for (String shape : shapes) {
            map.put(shape.toUpperCase(), series);
        }
    }

    private static List<String> armShapes() {
        return List.of("VM.Standard.A1.Flex", "VM.Standard.A2.Flex", "VM.Standard.A4.Flex");
    }

    private static List<String> amdShapes() {
        return List.of("VM.Standard.E4.Flex", "VM.Standard.E5.Flex", "VM.Standard.E6.Flex", "VM.Standard.E6.Ax.Flex");
    }

    private static List<String> intelShapes() {
        return List.of("VM.Standard3.Flex", "VM.Optimized3.Flex", "VM.Standard4.Ax.Flex");
    }

    private static List<String> specialtyShapes() {
        return List.of(
            "VM.Standard.E2.1.Micro",
            "VM.Standard.E3.Flex",
            "VM.DenseIO.E5.Flex",
            "VM.DenseIO.E4.Flex",
            "VM.DenseIO2.8",
            "VM.DenseIO2.16",
            "VM.DenseIO2.24",
            "VM.GPU.A10.1",
            "VM.GPU.A10.2",
            "VM.GPU2.1",
            "VM.GPU3.1",
            "VM.GPU3.2",
            "VM.GPU3.4",
            "VM.Standard.B1.1",
            "VM.Standard.B1.2",
            "VM.Standard.B1.4",
            "VM.Standard.B1.8",
            "VM.Standard.B1.16",
            "VM.Standard.E2.1",
            "VM.Standard.E2.2",
            "VM.Standard.E2.4",
            "VM.Standard.E2.8",
            "VM.Standard1.1",
            "VM.Standard1.2",
            "VM.Standard1.4",
            "VM.Standard1.8",
            "VM.Standard1.16",
            "VM.Standard2.1",
            "VM.Standard2.2",
            "VM.Standard2.4",
            "VM.Standard2.8",
            "VM.Standard2.16",
            "VM.Standard2.24"
        );
    }
}
