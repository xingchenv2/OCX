/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.AnnouncementService
 *  com.ociworker.service.OciClientService
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.announcementsservice.AnnouncementClient
 *  com.oracle.bmc.announcementsservice.model.AffectedResource
 *  com.oracle.bmc.announcementsservice.model.Announcement
 *  com.oracle.bmc.announcementsservice.model.AnnouncementSummary
 *  com.oracle.bmc.announcementsservice.model.AnnouncementUserStatusDetails
 *  com.oracle.bmc.announcementsservice.model.AnnouncementsCollection
 *  com.oracle.bmc.announcementsservice.model.BaseAnnouncement
 *  com.oracle.bmc.announcementsservice.model.Property
 *  com.oracle.bmc.announcementsservice.requests.GetAnnouncementRequest
 *  com.oracle.bmc.announcementsservice.requests.GetAnnouncementUserStatusRequest
 *  com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest
 *  com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest$Builder
 *  com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest$LifecycleState
 *  com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest$SortBy
 *  com.oracle.bmc.announcementsservice.requests.ListAnnouncementsRequest$SortOrder
 *  com.oracle.bmc.announcementsservice.responses.ListAnnouncementsResponse
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import com.ociworker.exception.OciException;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.dto.SysUserDTO;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.OciClientService;
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
import com.oracle.bmc.announcementsservice.responses.ListAnnouncementsResponse;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class AnnouncementService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return new OciClientService(SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build());
    }

    private AnnouncementClient createAnnouncementClient(OciClientService oci) {
        AnnouncementClient client = AnnouncementClient.builder().build((AbstractAuthenticationDetailsProvider)oci.getProvider());
        String regionId = oci.getUser() != null && oci.getUser().getOciCfg() != null ? oci.getUser().getOciCfg().getRegion() : null;
        try {
            client.setRegion(regionId != null ? Region.fromRegionId((String)regionId) : Region.US_ASHBURN_1);
        }
        catch (Exception e) {
            client.setRegion(Region.US_ASHBURN_1);
        }
        return client;
    }

    public Map<String, Object> listAnnouncements(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        String compartmentId = user.getOciTenantId();
        ArrayList items = new ArrayList();
        try (OciClientService oci = this.buildClient(tenantId);
             AnnouncementClient client = this.createAnnouncementClient(oci);){
            ListAnnouncementsResponse resp;
            String page = null;
            do {
                ListAnnouncementsRequest.Builder req = ListAnnouncementsRequest.builder().compartmentId(compartmentId).lifecycleState(ListAnnouncementsRequest.LifecycleState.Active).sortBy(ListAnnouncementsRequest.SortBy.TimeCreated).sortOrder(ListAnnouncementsRequest.SortOrder.Desc).limit(Integer.valueOf(100));
                if (page != null) {
                    req.page(page);
                }
                resp = client.listAnnouncements(req.build());
                AnnouncementService.appendListRows(items, (ListAnnouncementsResponse)resp, (boolean)true);
            } while ((page = resp.getOpcNextPage()) != null && !page.isBlank());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn("listAnnouncements failed for {}: {}", (Object)tenantId, (Object)e.getMessage());
            throw new OciException("\u83b7\u53d6\u4e91\u516c\u544a\u5931\u8d25: " + e.getMessage());
        }
        LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("compartmentId", compartmentId);
        out.put("items", items);
        out.put("count", items.size());
        out.put("retentionNote", "\u516c\u544a\u7531 Oracle \u4e91\u7aef\u4fdd\u7559\u7ea6 90 \u5929\uff0c\u9762\u677f\u4ec5\u5b9e\u65f6\u67e5\u8be2\uff0c\u4e0d\u505a\u672c\u5730\u5f52\u6863\u3002");
        return out;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public Map<String, Object> getAnnouncementDetail(String tenantId, String announcementId) {
        if (announcementId == null || announcementId.isBlank()) {
            throw new OciException("announcementId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService oci = this.buildClient(tenantId);){
            LinkedHashMap<String, Object> linkedHashMap;
            block21: {
                AnnouncementClient client = this.createAnnouncementClient(oci);
                try {
                    Announcement a = client.getAnnouncement(GetAnnouncementRequest.builder().announcementId(announcementId).build()).getAnnouncement();
                    if (a == null) {
                        throw new OciException("\u516c\u544a\u4e0d\u5b58\u5728");
                    }
                    Map detail = AnnouncementService.toDetailMap((Announcement)a);
                    detail.put("userStatus", AnnouncementService.fetchUserStatusLabel((AnnouncementClient)client, (String)announcementId));
                    ArrayList impacted = new ArrayList();
                    if (a.getAffectedResources() != null) {
                        for (AffectedResource r : a.getAffectedResources()) {
                            if (r == null) continue;
                            LinkedHashMap<String, Object> rm = new LinkedHashMap<String, Object>();
                            rm.put("resourceId", r.getResourceId());
                            rm.put("resourceName", r.getResourceName());
                            rm.put("region", r.getRegion());
                            rm.put("additionalProperties", AnnouncementService.toPropertyList((List)r.getAdditionalProperties()));
                            impacted.add(rm);
                        }
                    }
                    List history = new ArrayList();
                    String chainId = a.getChainId();
                    if (chainId != null && !chainId.isBlank()) {
                        history = this.listByChainId(client, user.getOciTenantId(), chainId, announcementId);
                    }
                    LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                    out.put("detail", detail);
                    out.put("impactedResources", impacted);
                    out.put("history", history);
                    linkedHashMap = out;
                    if (client == null) break block21;
                }
                catch (Throwable throwable) {
                    if (client != null) {
                        try {
                            client.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                client.close();
            }
            return linkedHashMap;
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn("getAnnouncementDetail {} failed: {}", (Object)announcementId, (Object)e.getMessage());
            throw new OciException("\u83b7\u53d6\u516c\u544a\u8be6\u60c5\u5931\u8d25: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> listByChainId(AnnouncementClient client, String compartmentId, String chainId, String excludeId) {
        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            ListAnnouncementsResponse resp;
            String page = null;
            do {
                AnnouncementsCollection coll;
                ListAnnouncementsRequest.Builder req = ListAnnouncementsRequest.builder().compartmentId(compartmentId).chainId(chainId).sortBy(ListAnnouncementsRequest.SortBy.TimeCreated).sortOrder(ListAnnouncementsRequest.SortOrder.Desc).limit(Integer.valueOf(100));
                if (page != null) {
                    req.page(page);
                }
                if ((coll = (resp = client.listAnnouncements(req.build())).getAnnouncementsCollection()) == null || coll.getItems() == null) continue;
                for (AnnouncementSummary a : coll.getItems()) {
                    if (a.getId() != null && a.getId().equals(excludeId)) continue;
                    items.add(AnnouncementService.toSummaryMap((BaseAnnouncement)a));
                }
            } while ((page = resp.getOpcNextPage()) != null && !page.isBlank());
        }
        catch (Exception e) {
            log.warn("listByChainId failed chainId={}: {}", (Object)chainId, (Object)e.getMessage());
        }
        return items;
    }

    private static void appendListRows(List<Map<String, Object>> target, ListAnnouncementsResponse resp, boolean withUserStatus) {
        AnnouncementsCollection coll = resp.getAnnouncementsCollection();
        if (coll == null || coll.getItems() == null) {
            return;
        }
        Map statusById = withUserStatus ? AnnouncementService.userStatusMap((List)coll.getUserStatuses()) : Map.of();
        for (AnnouncementSummary a : coll.getItems()) {
            Map row = AnnouncementService.toSummaryMap((BaseAnnouncement)a);
            if (withUserStatus) {
                String id = a.getId();
                row.put("userStatus", id != null && statusById.containsKey(id) ? statusById.get(id) : "Unread");
            }
            target.add(row);
        }
    }

    private static Map<String, String> userStatusMap(List<AnnouncementUserStatusDetails> statuses) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (statuses == null) {
            return map;
        }
        for (AnnouncementUserStatusDetails s : statuses) {
            if (s == null || s.getUserStatusAnnouncementId() == null) continue;
            map.put(s.getUserStatusAnnouncementId(), s.getTimeAcknowledged() != null ? "Read" : "Unread");
        }
        return map;
    }

    private static String fetchUserStatusLabel(AnnouncementClient client, String announcementId) {
        if (announcementId == null) {
            return "\u2014";
        }
        try {
            AnnouncementUserStatusDetails status = client.getAnnouncementUserStatus(GetAnnouncementUserStatusRequest.builder().announcementId(announcementId).build()).getAnnouncementUserStatusDetails();
            if (status != null && status.getTimeAcknowledged() != null) {
                return "Read";
            }
            return "Unread";
        }
        catch (Exception e) {
            log.debug("getAnnouncementUserStatus {}: {}", (Object)announcementId, (Object)e.getMessage());
            return "\u2014";
        }
    }

    private static Map<String, Object> toSummaryMap(BaseAnnouncement a) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("id", a.getId());
        m.put("summary", a.getSummary());
        m.put("referenceTicketNumber", a.getReferenceTicketNumber());
        m.put("announcementType", AnnouncementService.enumVal((Object)a.getAnnouncementType()));
        m.put("lifecycleState", AnnouncementService.enumVal((Object)a.getLifecycleState()));
        m.put("platformType", AnnouncementService.enumVal((Object)a.getPlatformType()));
        m.put("environmentName", a.getEnvironmentName());
        m.put("services", a.getServices());
        m.put("affectedRegions", a.getAffectedRegions());
        m.put("timeCreated", a.getTimeCreated());
        m.put("timeUpdated", a.getTimeUpdated());
        m.put("timeOneTitle", a.getTimeOneTitle());
        m.put("timeOneType", AnnouncementService.enumVal((Object)a.getTimeOneType()));
        m.put("timeOneValue", a.getTimeOneValue());
        m.put("timeTwoTitle", a.getTimeTwoTitle());
        m.put("timeTwoType", AnnouncementService.enumVal((Object)a.getTimeTwoType()));
        m.put("timeTwoValue", a.getTimeTwoValue());
        m.put("chainId", a.getChainId());
        m.put("isBanner", a.getIsBanner());
        return m;
    }

    private static Map<String, Object> toDetailMap(Announcement a) {
        Map m = AnnouncementService.toSummaryMap((BaseAnnouncement)a);
        m.put("description", a.getDescription());
        m.put("additionalInformation", a.getAdditionalInformation());
        return m;
    }

    private static String enumVal(Object e) {
        if (e == null) {
            return null;
        }
        try {
            return (String)e.getClass().getMethod("getValue", new Class[0]).invoke(e, new Object[0]);
        }
        catch (Exception ignored) {
            return String.valueOf(e);
        }
    }

    private static List<Map<String, String>> toPropertyList(List<Property> props) {
        if (props == null || props.isEmpty()) {
            return List.of();
        }
        ArrayList<Map<String, String>> out = new ArrayList<Map<String, String>>();
        for (Property p : props) {
            if (p == null) continue;
            LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
            row.put("name", p.getName());
            row.put("value", p.getValue());
            out.add(row);
        }
        return out;
    }
}

