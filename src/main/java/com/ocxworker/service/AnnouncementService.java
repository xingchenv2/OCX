package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.Region;
import com.oracle.bmc.announcementsservice.AnnouncementClient;
import com.oracle.bmc.announcementsservice.model.AffectedResource;
import com.oracle.bmc.announcementsservice.model.Announcement;
import com.oracle.bmc.announcementsservice.model.AnnouncementSummary;
import com.oracle.bmc.announcementsservice.model.AnnouncementUserStatusDetails;
import com.oracle.bmc.announcementsservice.model.AnnouncementsCollection;
import com.oracle.bmc.announcementsservice.model.BaseAnnouncement;
import com.oracle.bmc.announcementsservice.model.Property;
import com.oracle.bmc.announcementsservice.requests.GetAnnouncementRequest;
import com.oracle.bmc.announcementsservice.requests.GetAnnouncementUserStatusRequest;
import com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest;
import com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest.Builder;
import com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest.LifecycleState;
import com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest.SortBy;
import com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest.SortOrder;
import com.oracle.bmc.announcementsservice.responses.ListAnnouncementsResponse;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            return new OciClientService(
                SysUserDTO.builder()
                    .username(user.getUsername())
                    .ociCfg(
                        SysUserDTO.OciCfg.builder()
                            .tenantId(user.getOciTenantId())
                            .userId(user.getOciUserId())
                            .fingerprint(user.getOciFingerprint())
                            .region(user.getOciRegion())
                            .privateKeyPath(user.getOciKeyPath())
                            .build()
                    )
                    .build()
            );
        }
    }

    private AnnouncementClient createAnnouncementClient(OciClientService oci) {
        AnnouncementClient client = AnnouncementClient.builder().build(oci.getProvider());
        String regionId = oci.getUser() != null && oci.getUser().getOciCfg() != null ? oci.getUser().getOciCfg().getRegion() : null;

        try {
            client.setRegion(regionId != null ? Region.fromRegionId(regionId) : Region.US_ASHBURN_1);
        } catch (Exception var5) {
            client.setRegion(Region.US_ASHBURN_1);
        }

        return client;
    }

    public Map<String, Object> listAnnouncements(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            String compartmentId = user.getOciTenantId();
            List<Map<String, Object>> items = new ArrayList<>();

            try (OciClientService oci = this.buildClient(tenantId)) {
                AnnouncementClient client = this.createAnnouncementClient(oci);

                try {
                    String page = null;

                    do {
                        Builder req = ListAnnouncementsRequest.builder()
                            .compartmentId(compartmentId)
                            .lifecycleState(LifecycleState.Active)
                            .sortBy(SortBy.TimeCreated)
                            .sortOrder(SortOrder.Desc)
                            .limit(100);
                        if (page != null) {
                            req.page(page);
                        }

                        ListAnnouncementsResponse resp = client.listAnnouncements(req.build());
                        appendListRows(items, resp, true);
                        page = resp.getOpcNextPage();
                    } while (page != null && !page.isBlank());
                } catch (Throwable var12) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                if (client != null) {
                    client.close();
                }
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                log.warn("listAnnouncements failed for {}: {}", tenantId, var15.getMessage());
                throw new OciException("获取云公告失败: " + var15.getMessage());
            }

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("compartmentId", compartmentId);
            out.put("items", items);
            out.put("count", items.size());
            out.put("retentionNote", "公告由 Oracle 云端保留约 90 天，面板仅实时查询，不做本地归档。");
            return out;
        }
    }

    public Map<String, Object> getAnnouncementDetail(String tenantId, String announcementId) {
        if (announcementId != null && !announcementId.isBlank()) {
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            if (user == null) {
                throw new OciException("租户配置不存在");
            } else {
                try {
                    Object var12;
                    try (OciClientService oci = this.buildClient(tenantId)) {
                        AnnouncementClient client = this.createAnnouncementClient(oci);

                        try {
                            Announcement a = client.getAnnouncement(GetAnnouncementRequest.builder().announcementId(announcementId).build()).getAnnouncement();
                            if (a == null) {
                                throw new OciException("公告不存在");
                            }

                            Map<String, Object> detail = toDetailMap(a);
                            detail.put("userStatus", fetchUserStatusLabel(client, announcementId));
                            List<Map<String, Object>> impacted = new ArrayList<>();
                            if (a.getAffectedResources() != null) {
                                for (AffectedResource r : a.getAffectedResources()) {
                                    if (r != null) {
                                        Map<String, Object> rm = new LinkedHashMap<>();
                                        rm.put("resourceId", r.getResourceId());
                                        rm.put("resourceName", r.getResourceName());
                                        rm.put("region", r.getRegion());
                                        rm.put("additionalProperties", toPropertyList(r.getAdditionalProperties()));
                                        impacted.add(rm);
                                    }
                                }
                            }

                            List<Map<String, Object>> history = new ArrayList<>();
                            String chainId = a.getChainId();
                            if (chainId != null && !chainId.isBlank()) {
                                history = this.listByChainId(client, user.getOciTenantId(), chainId, announcementId);
                            }

                            Map<String, Object> out = new LinkedHashMap<>();
                            out.put("detail", detail);
                            out.put("impactedResources", impacted);
                            out.put("history", history);
                            var12 = out;
                        } catch (Throwable var15) {
                            if (client != null) {
                                try {
                                    client.close();
                                } catch (Throwable var14) {
                                    var15.addSuppressed(var14);
                                }
                            }

                            throw var15;
                        }

                        if (client != null) {
                            client.close();
                        }
                    }

                    return (Map<String, Object>)var12;
                } catch (OciException var17) {
                    throw var17;
                } catch (Exception var18) {
                    log.warn("getAnnouncementDetail {} failed: {}", announcementId, var18.getMessage());
                    throw new OciException("获取公告详情失败: " + var18.getMessage());
                }
            }
        } else {
            throw new OciException("announcementId 不能为空");
        }
    }

    private List<Map<String, Object>> listByChainId(AnnouncementClient client, String compartmentId, String chainId, String excludeId) {
        List<Map<String, Object>> items = new ArrayList<>();

        try {
            String page = null;

            do {
                Builder req = ListAnnouncementsRequest.builder()
                    .compartmentId(compartmentId)
                    .chainId(chainId)
                    .sortBy(SortBy.TimeCreated)
                    .sortOrder(SortOrder.Desc)
                    .limit(100);
                if (page != null) {
                    req.page(page);
                }

                ListAnnouncementsResponse resp = client.listAnnouncements(req.build());
                AnnouncementsCollection coll = resp.getAnnouncementsCollection();
                if (coll != null && coll.getItems() != null) {
                    for (AnnouncementSummary a : coll.getItems()) {
                        if (a.getId() == null || !a.getId().equals(excludeId)) {
                            items.add(toSummaryMap(a));
                        }
                    }
                }

                page = resp.getOpcNextPage();
            } while (page != null && !page.isBlank());
        } catch (Exception var12) {
            log.warn("listByChainId failed chainId={}: {}", chainId, var12.getMessage());
        }

        return items;
    }

    private static void appendListRows(List<Map<String, Object>> target, ListAnnouncementsResponse resp, boolean withUserStatus) {
        AnnouncementsCollection coll = resp.getAnnouncementsCollection();
        if (coll != null && coll.getItems() != null) {
            Map<String, String> statusById = withUserStatus ? userStatusMap(coll.getUserStatuses()) : Map.of();

            for (AnnouncementSummary a : coll.getItems()) {
                Map<String, Object> row = toSummaryMap(a);
                if (withUserStatus) {
                    String id = a.getId();
                    row.put("userStatus", id != null && statusById.containsKey(id) ? statusById.get(id) : "Unread");
                }

                target.add(row);
            }
        }
    }

    private static Map<String, String> userStatusMap(List<AnnouncementUserStatusDetails> statuses) {
        Map<String, String> map = new HashMap<>();
        if (statuses == null) {
            return map;
        } else {
            for (AnnouncementUserStatusDetails s : statuses) {
                if (s != null && s.getUserStatusAnnouncementId() != null) {
                    map.put(s.getUserStatusAnnouncementId(), s.getTimeAcknowledged() != null ? "Read" : "Unread");
                }
            }

            return map;
        }
    }

    private static String fetchUserStatusLabel(AnnouncementClient client, String announcementId) {
        if (announcementId == null) {
            return "—";
        } else {
            try {
                AnnouncementUserStatusDetails status = client.getAnnouncementUserStatus(
                        GetAnnouncementUserStatusRequest.builder().announcementId(announcementId).build()
                    )
                    .getAnnouncementUserStatusDetails();
                return status != null && status.getTimeAcknowledged() != null ? "Read" : "Unread";
            } catch (Exception var3) {
                log.debug("getAnnouncementUserStatus {}: {}", announcementId, var3.getMessage());
                return "—";
            }
        }
    }

    private static Map<String, Object> toSummaryMap(BaseAnnouncement a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("summary", a.getSummary());
        m.put("referenceTicketNumber", a.getReferenceTicketNumber());
        m.put("announcementType", enumVal(a.getAnnouncementType()));
        m.put("lifecycleState", enumVal(a.getLifecycleState()));
        m.put("platformType", enumVal(a.getPlatformType()));
        m.put("environmentName", a.getEnvironmentName());
        m.put("services", a.getServices());
        m.put("affectedRegions", a.getAffectedRegions());
        m.put("timeCreated", a.getTimeCreated());
        m.put("timeUpdated", a.getTimeUpdated());
        m.put("timeOneTitle", a.getTimeOneTitle());
        m.put("timeOneType", enumVal(a.getTimeOneType()));
        m.put("timeOneValue", a.getTimeOneValue());
        m.put("timeTwoTitle", a.getTimeTwoTitle());
        m.put("timeTwoType", enumVal(a.getTimeTwoType()));
        m.put("timeTwoValue", a.getTimeTwoValue());
        m.put("chainId", a.getChainId());
        m.put("isBanner", a.getIsBanner());
        return m;
    }

    private static Map<String, Object> toDetailMap(Announcement a) {
        Map<String, Object> m = toSummaryMap(a);
        m.put("description", a.getDescription());
        m.put("additionalInformation", a.getAdditionalInformation());
        return m;
    }

    private static String enumVal(Object e) {
        if (e == null) {
            return null;
        } else {
            try {
                return (String)e.getClass().getMethod("getValue").invoke(e);
            } catch (Exception var2) {
                return String.valueOf(e);
            }
        }
    }

    private static List<Map<String, String>> toPropertyList(List<Property> props) {
        if (props != null && !props.isEmpty()) {
            List<Map<String, String>> out = new ArrayList<>();

            for (Property p : props) {
                if (p != null) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("name", p.getName());
                    row.put("value", p.getValue());
                    out.add(row);
                }
            }

            return out;
        } else {
            return List.of();
        }
    }
}
