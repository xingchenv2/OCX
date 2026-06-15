/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.util.ShapeSeriesUtil
 */
package com.ociworker.util;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Exception performing whole class analysis ignored.
 */
public final class ShapeSeriesUtil {
    public static final String SERIES_AMD = "AMD";
    public static final String SERIES_INTEL = "Intel";
    public static final String SERIES_ARM = "ARM\uff08Ampere\uff09";
    public static final String SERIES_SPECIALTY = "\u4e13\u4e1a\u548c\u4e0a\u4e00\u4ee3";
    public static final String SERIES_BARE_METAL = "\u88f8\u91d1\u5c5e\u673a";
    private static final Map<String, String> FIXED_VM_SHAPE_SERIES = ShapeSeriesUtil.buildFixedVmShapeSeries();

    private ShapeSeriesUtil() {
    }

    public static String resolveSeries(String shapeOrArchitecture) {
        if (StrUtil.isBlank((CharSequence)shapeOrArchitecture)) {
            return "-";
        }
        String raw = shapeOrArchitecture.trim();
        String key = raw.toUpperCase();
        if (key.startsWith("BM.")) {
            return "\u88f8\u91d1\u5c5e\u673a";
        }
        String fixed = (String)FIXED_VM_SHAPE_SERIES.get(key);
        if (fixed != null) {
            return fixed;
        }
        if ("ARM".equalsIgnoreCase(raw) || "Ampere".equalsIgnoreCase(raw)) {
            return "ARM\uff08Ampere\uff09";
        }
        if ("Intel".equalsIgnoreCase(raw) || "INTEL".equalsIgnoreCase(raw)) {
            return "Intel";
        }
        if ("AMD".equalsIgnoreCase(raw)) {
            return "\u4e13\u4e1a\u548c\u4e0a\u4e00\u4ee3";
        }
        return raw;
    }

    public static boolean isFullShapeName(String shapeOrArchitecture) {
        if (StrUtil.isBlank((CharSequence)shapeOrArchitecture)) {
            return false;
        }
        String u = shapeOrArchitecture.trim().toUpperCase();
        return u.startsWith("VM.") || u.startsWith("BM.");
    }

    private static Map<String, String> buildFixedVmShapeSeries() {
        HashMap m = new HashMap();
        ShapeSeriesUtil.register(m, (String)"ARM\uff08Ampere\uff09", (List)ShapeSeriesUtil.armShapes());
        ShapeSeriesUtil.register(m, (String)"AMD", (List)ShapeSeriesUtil.amdShapes());
        ShapeSeriesUtil.register(m, (String)"Intel", (List)ShapeSeriesUtil.intelShapes());
        ShapeSeriesUtil.register(m, (String)"\u4e13\u4e1a\u548c\u4e0a\u4e00\u4ee3", (List)ShapeSeriesUtil.specialtyShapes());
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
        return List.of("VM.Standard.E2.1.Micro", "VM.Standard.E3.Flex", "VM.DenseIO.E5.Flex", "VM.DenseIO.E4.Flex", "VM.DenseIO2.8", "VM.DenseIO2.16", "VM.DenseIO2.24", "VM.GPU.A10.1", "VM.GPU.A10.2", "VM.GPU2.1", "VM.GPU3.1", "VM.GPU3.2", "VM.GPU3.4", "VM.Standard.B1.1", "VM.Standard.B1.2", "VM.Standard.B1.4", "VM.Standard.B1.8", "VM.Standard.B1.16", "VM.Standard.E2.1", "VM.Standard.E2.2", "VM.Standard.E2.4", "VM.Standard.E2.8", "VM.Standard1.1", "VM.Standard1.2", "VM.Standard1.4", "VM.Standard1.8", "VM.Standard1.16", "VM.Standard2.1", "VM.Standard2.2", "VM.Standard2.4", "VM.Standard2.8", "VM.Standard2.16", "VM.Standard2.24");
    }
}

