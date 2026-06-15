/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.util.OciRegionCatalog
 *  com.oracle.bmc.Region
 */
package com.ociworker.util;

import com.oracle.bmc.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/*
 * Exception performing whole class analysis ignored.
 */
public final class OciRegionCatalog {
    private static final Map<String, String> ZH_LABELS = new LinkedHashMap();

    private OciRegionCatalog() {
    }

    public static List<Map<String, String>> listUiRows() {
        TreeSet<String> ids = new TreeSet<String>();
        for (Region r : Region.values()) {
            String id = r.getRegionId();
            if (id == null || id.isBlank()) continue;
            ids.add(id);
        }
        return OciRegionCatalog.listUiRowsForIds(ids);
    }

    public static List<Map<String, String>> listUiRowsForIds(Collection<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return List.of();
        }
        TreeSet<String> sorted = new TreeSet<String>();
        for (String raw : regionIds) {
            String id;
            if (raw == null || (id = raw.trim()).isEmpty()) continue;
            sorted.add(id);
        }
        ArrayList<Map<String, String>> out = new ArrayList<Map<String, String>>(sorted.size());
        for (String id : sorted) {
            out.add(OciRegionCatalog.buildRow((String)id));
        }
        return out;
    }

    private static Map<String, String> buildRow(String id) {
        String zh = ZH_LABELS.getOrDefault(id, id);
        String label = ZH_LABELS.containsKey(id) ? zh + "\uff08" + id + "\uff09" : id;
        LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
        row.put("regionId", id);
        row.put("labelZh", zh);
        row.put("label", label);
        return row;
    }

    static {
        ZH_LABELS.put("us-ashburn-1", "\u7f8e\u56fd\u4e1c\u90e8\uff08\u963f\u4ec0\u672c\uff09");
        ZH_LABELS.put("us-phoenix-1", "\u7f8e\u56fd\u897f\u90e8\uff08\u51e4\u51f0\u57ce\uff09");
        ZH_LABELS.put("us-sanjose-1", "\u7f8e\u56fd\u897f\u90e8\uff08\u5723\u4f55\u585e\uff09");
        ZH_LABELS.put("us-chicago-1", "\u7f8e\u56fd\u4e2d\u897f\u90e8\uff08\u829d\u52a0\u54e5\uff09");
        ZH_LABELS.put("ca-toronto-1", "\u52a0\u62ff\u5927\u4e1c\u5357\u90e8\uff08\u591a\u4f26\u591a\uff09");
        ZH_LABELS.put("ca-montreal-1", "\u52a0\u62ff\u5927\u4e1c\u5357\u90e8\uff08\u8499\u7279\u5229\u5c14\uff09");
        ZH_LABELS.put("eu-frankfurt-1", "\u5fb7\u56fd\u4e2d\u90e8\uff08\u6cd5\u5170\u514b\u798f\uff09");
        ZH_LABELS.put("eu-zurich-1", "\u745e\u58eb\u5317\u90e8\uff08\u82cf\u9ece\u4e16\uff09");
        ZH_LABELS.put("eu-amsterdam-1", "\u8377\u5170\u897f\u5317\u90e8\uff08\u963f\u59c6\u65af\u7279\u4e39\uff09");
        ZH_LABELS.put("eu-marseille-1", "\u6cd5\u56fd\u5357\u90e8\uff08\u9a6c\u8d5b\uff09");
        ZH_LABELS.put("eu-stockholm-1", "\u745e\u5178\u5317\u90e8\uff08\u65af\u5fb7\u54e5\u5c14\u6469\uff09");
        ZH_LABELS.put("eu-milan-1", "\u610f\u5927\u5229\u897f\u5317\u90e8\uff08\u7c73\u5170\uff09");
        ZH_LABELS.put("eu-paris-1", "\u6cd5\u56fd\u4e2d\u90e8\uff08\u5df4\u9ece\uff09");
        ZH_LABELS.put("eu-madrid-1", "\u897f\u73ed\u7259\u4e2d\u90e8\uff08\u9a6c\u5fb7\u91cc\uff09");
        ZH_LABELS.put("eu-madrid-3", "\u897f\u73ed\u7259\u4e2d\u90e8\uff08\u9a6c\u5fb7\u91cc3\uff09");
        ZH_LABELS.put("uk-london-1", "\u82f1\u56fd\u5357\u90e8\uff08\u4f26\u6566\uff09");
        ZH_LABELS.put("uk-cardiff-1", "\u82f1\u56fd\u897f\u90e8\uff08\u52a0\u7684\u592b\uff09");
        ZH_LABELS.put("ap-tokyo-1", "\u65e5\u672c\u4e1c\u90e8\uff08\u4e1c\u4eac\uff09");
        ZH_LABELS.put("ap-osaka-1", "\u65e5\u672c\u4e2d\u90e8\uff08\u5927\u962a\uff09");
        ZH_LABELS.put("ap-seoul-1", "\u97e9\u56fd\u4e2d\u90e8\uff08\u9996\u5c14\uff09");
        ZH_LABELS.put("ap-chuncheon-1", "\u97e9\u56fd\u5317\u90e8\uff08\u6625\u5ddd\uff09");
        ZH_LABELS.put("ap-mumbai-1", "\u5370\u5ea6\u897f\u90e8\uff08\u5b5f\u4e70\uff09");
        ZH_LABELS.put("ap-hyderabad-1", "\u5370\u5ea6\u5357\u90e8\uff08\u6d77\u5f97\u62c9\u5df4\uff09");
        ZH_LABELS.put("ap-singapore-1", "\u65b0\u52a0\u5761\uff08\u65b0\u52a0\u5761\uff09");
        ZH_LABELS.put("ap-singapore-2", "\u65b0\u52a0\u5761\u897f\u90e8");
        ZH_LABELS.put("ap-batam-1", "\u5370\u5ea6\u5c3c\u897f\u4e9a\u5317\u90e8\uff08\u5df4\u6de1\uff09");
        ZH_LABELS.put("ap-kulai-2", "\u9a6c\u6765\u897f\u4e9a");
        ZH_LABELS.put("ap-sydney-1", "\u6fb3\u5927\u5229\u4e9a\u4e1c\u90e8\uff08\u6089\u5c3c\uff09");
        ZH_LABELS.put("ap-melbourne-1", "\u6fb3\u5927\u5229\u4e9a\u4e1c\u5357\u90e8\uff08\u58a8\u5c14\u672c\uff09");
        ZH_LABELS.put("sa-bogota-1", "\u54e5\u4f26\u6bd4\u4e9a\u4e2d\u90e8\uff08\u6ce2\u54e5\u5927\uff09");
        ZH_LABELS.put("sa-saopaulo-1", "\u5df4\u897f\u4e1c\u90e8\uff08\u5723\u4fdd\u7f57\uff09");
        ZH_LABELS.put("sa-vinhedo-1", "\u5df4\u897f\u4e1c\u5357\u90e8\uff08\u7ef4\u6d85\u675c\uff09");
        ZH_LABELS.put("sa-santiago-1", "\u667a\u5229\u4e2d\u90e8\uff08\u5723\u5730\u4e9a\u54e5\uff09");
        ZH_LABELS.put("sa-valparaiso-1", "\u667a\u5229\u897f\u90e8\uff08\u74e6\u5c14\u5e15\u83b1\u7d22\uff09");
        ZH_LABELS.put("me-jeddah-1", "\u6c99\u7279\u963f\u62c9\u4f2f\u897f\u90e8\uff08\u5409\u8fbe\uff09");
        ZH_LABELS.put("me-dubai-1", "\u963f\u8054\u914b\u4e1c\u90e8\uff08\u8fea\u62dc\uff09");
        ZH_LABELS.put("me-abudhabi-1", "\u963f\u8054\u914b\u4e2d\u90e8\uff08\u963f\u5e03\u624e\u6bd4\uff09");
        ZH_LABELS.put("me-riyadh-1", "\u6c99\u7279\u963f\u62c9\u4f2f\u4e2d\u90e8\uff08\u5229\u96c5\u5f97\uff09");
        ZH_LABELS.put("af-johannesburg-1", "\u5357\u975e\u4e2d\u90e8\uff08\u7ea6\u7ff0\u5185\u65af\u5821\uff09");
        ZH_LABELS.put("af-casablanca-1", "\u6469\u6d1b\u54e5\u897f\u90e8\uff08\u5361\u8428\u5e03\u5170\u5361\uff09");
        ZH_LABELS.put("il-jerusalem-1", "\u4ee5\u8272\u5217\u4e2d\u90e8\uff08\u8036\u8def\u6492\u51b7\uff09");
        ZH_LABELS.put("mx-queretaro-1", "\u58a8\u897f\u54e5\u4e2d\u90e8\uff08\u514b\u96f7\u5854\u7f57\uff09");
        ZH_LABELS.put("mx-monterrey-1", "\u58a8\u897f\u54e5\u4e1c\u5317\u90e8\uff08\u8499\u7279\u96f7\uff09");
        ZH_LABELS.put("us-saltlake-2", "\u7f8e\u56fd\u4e2d\u897f\u90e8\uff08\u76d0\u6e56\u57ce\uff09");
        ZH_LABELS.put("us-langley-1", "\u7f8e\u56fd\u653f\u5e9c\uff08\u5170\u5229\uff09");
        ZH_LABELS.put("us-luke-1", "\u7f8e\u56fd\u653f\u5e9c\uff08\u5362\u514b\uff09");
        ZH_LABELS.put("us-gov-ashburn-1", "\u7f8e\u56fd\u653f\u5e9c\uff08\u963f\u4ec0\u672c\uff09");
        ZH_LABELS.put("us-gov-chicago-1", "\u7f8e\u56fd\u653f\u5e9c\uff08\u829d\u52a0\u54e5\uff09");
        ZH_LABELS.put("us-gov-phoenix-1", "\u7f8e\u56fd\u653f\u5e9c\uff08\u51e4\u51f0\u57ce\uff09");
    }
}

