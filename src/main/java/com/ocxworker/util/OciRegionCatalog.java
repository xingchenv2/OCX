package com.ocxworker.util;

import com.oracle.bmc.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public final class OciRegionCatalog {
    private static final Map<String, String> ZH_LABELS = new LinkedHashMap<>();

    private OciRegionCatalog() {
    }

    public static List<Map<String, String>> listUiRows() {
        TreeSet<String> ids = new TreeSet<>();

        for (Region r : Region.values()) {
            String id = r.getRegionId();
            if (id != null && !id.isBlank()) {
                ids.add(id);
            }
        }

        return listUiRowsForIds(ids);
    }

    public static List<Map<String, String>> listUiRowsForIds(Collection<String> regionIds) {
        if (regionIds != null && !regionIds.isEmpty()) {
            TreeSet<String> sorted = new TreeSet<>();

            for (String raw : regionIds) {
                if (raw != null) {
                    String id = raw.trim();
                    if (!id.isEmpty()) {
                        sorted.add(id);
                    }
                }
            }

            List<Map<String, String>> out = new ArrayList<>(sorted.size());

            for (String id : sorted) {
                out.add(buildRow(id));
            }

            return out;
        } else {
            return List.of();
        }
    }

    private static Map<String, String> buildRow(String id) {
        String zh = ZH_LABELS.getOrDefault(id, id);
        String label = ZH_LABELS.containsKey(id) ? zh + "（" + id + "）" : id;
        Map<String, String> row = new LinkedHashMap<>();
        row.put("regionId", id);
        row.put("labelZh", zh);
        row.put("label", label);
        return row;
    }

    static {
        ZH_LABELS.put("us-ashburn-1", "美国东部（阿什本）");
        ZH_LABELS.put("us-phoenix-1", "美国西部（凤凰城）");
        ZH_LABELS.put("us-sanjose-1", "美国西部（圣何塞）");
        ZH_LABELS.put("us-chicago-1", "美国中西部（芝加哥）");
        ZH_LABELS.put("ca-toronto-1", "加拿大东南部（多伦多）");
        ZH_LABELS.put("ca-montreal-1", "加拿大东南部（蒙特利尔）");
        ZH_LABELS.put("eu-frankfurt-1", "德国中部（法兰克福）");
        ZH_LABELS.put("eu-zurich-1", "瑞士北部（苏黎世）");
        ZH_LABELS.put("eu-amsterdam-1", "荷兰西北部（阿姆斯特丹）");
        ZH_LABELS.put("eu-marseille-1", "法国南部（马赛）");
        ZH_LABELS.put("eu-stockholm-1", "瑞典北部（斯德哥尔摩）");
        ZH_LABELS.put("eu-milan-1", "意大利西北部（米兰）");
        ZH_LABELS.put("eu-paris-1", "法国中部（巴黎）");
        ZH_LABELS.put("eu-madrid-1", "西班牙中部（马德里）");
        ZH_LABELS.put("eu-madrid-3", "西班牙中部（马德里3）");
        ZH_LABELS.put("uk-london-1", "英国南部（伦敦）");
        ZH_LABELS.put("uk-cardiff-1", "英国西部（加的夫）");
        ZH_LABELS.put("ap-tokyo-1", "日本东部（东京）");
        ZH_LABELS.put("ap-osaka-1", "日本中部（大阪）");
        ZH_LABELS.put("ap-seoul-1", "韩国中部（首尔）");
        ZH_LABELS.put("ap-chuncheon-1", "韩国北部（春川）");
        ZH_LABELS.put("ap-mumbai-1", "印度西部（孟买）");
        ZH_LABELS.put("ap-hyderabad-1", "印度南部（海得拉巴）");
        ZH_LABELS.put("ap-singapore-1", "新加坡（新加坡）");
        ZH_LABELS.put("ap-singapore-2", "新加坡西部");
        ZH_LABELS.put("ap-batam-1", "印度尼西亚北部（巴淡）");
        ZH_LABELS.put("ap-kulai-2", "马来西亚");
        ZH_LABELS.put("ap-sydney-1", "澳大利亚东部（悉尼）");
        ZH_LABELS.put("ap-melbourne-1", "澳大利亚东南部（墨尔本）");
        ZH_LABELS.put("sa-bogota-1", "哥伦比亚中部（波哥大）");
        ZH_LABELS.put("sa-saopaulo-1", "巴西东部（圣保罗）");
        ZH_LABELS.put("sa-vinhedo-1", "巴西东南部（维涅杜）");
        ZH_LABELS.put("sa-santiago-1", "智利中部（圣地亚哥）");
        ZH_LABELS.put("sa-valparaiso-1", "智利西部（瓦尔帕莱索）");
        ZH_LABELS.put("me-jeddah-1", "沙特阿拉伯西部（吉达）");
        ZH_LABELS.put("me-dubai-1", "阿联酋东部（迪拜）");
        ZH_LABELS.put("me-abudhabi-1", "阿联酋中部（阿布扎比）");
        ZH_LABELS.put("me-riyadh-1", "沙特阿拉伯中部（利雅得）");
        ZH_LABELS.put("af-johannesburg-1", "南非中部（约翰内斯堡）");
        ZH_LABELS.put("af-casablanca-1", "摩洛哥西部（卡萨布兰卡）");
        ZH_LABELS.put("il-jerusalem-1", "以色列中部（耶路撒冷）");
        ZH_LABELS.put("mx-queretaro-1", "墨西哥中部（克雷塔罗）");
        ZH_LABELS.put("mx-monterrey-1", "墨西哥东北部（蒙特雷）");
        ZH_LABELS.put("us-saltlake-2", "美国中西部（盐湖城）");
        ZH_LABELS.put("us-langley-1", "美国政府（兰利）");
        ZH_LABELS.put("us-luke-1", "美国政府（卢克）");
        ZH_LABELS.put("us-gov-ashburn-1", "美国政府（阿什本）");
        ZH_LABELS.put("us-gov-chicago-1", "美国政府（芝加哥）");
        ZH_LABELS.put("us-gov-phoenix-1", "美国政府（凤凰城）");
    }
}
