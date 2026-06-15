package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.core.model.ChangeBootVolumeCompartmentDetails;
import com.oracle.bmc.core.model.ChangeInstanceCompartmentDetails;
import com.oracle.bmc.core.model.ChangeVolumeCompartmentDetails;
import com.oracle.bmc.core.requests.ChangeBootVolumeCompartmentRequest;
import com.oracle.bmc.core.requests.ChangeInstanceCompartmentRequest;
import com.oracle.bmc.core.requests.ChangeVolumeCompartmentRequest;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.CreateCompartmentDetails;
import com.oracle.bmc.identity.model.MoveCompartmentDetails;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.model.UpdateCompartmentDetails;
import com.oracle.bmc.identity.model.Compartment.LifecycleState;
import com.oracle.bmc.identity.model.UpdateCompartmentDetails.Builder;
import com.oracle.bmc.identity.requests.CreateCompartmentRequest;
import com.oracle.bmc.identity.requests.DeleteCompartmentRequest;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.identity.requests.MoveCompartmentRequest;
import com.oracle.bmc.identity.requests.UpdateCompartmentRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest.AccessLevel;
import com.oracle.bmc.identity.responses.CreateCompartmentResponse;
import com.oracle.bmc.identity.responses.ListCompartmentsResponse;
import com.oracle.bmc.identity.responses.MoveCompartmentResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.resourcesearch.ResourceSearchClient;
import com.oracle.bmc.resourcesearch.model.ResourceSummary;
import com.oracle.bmc.resourcesearch.model.StructuredSearchDetails;
import com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest;
import com.oracle.bmc.resourcesearch.responses.SearchResourcesResponse;
import jakarta.annotation.Resource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CompartmentService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CompartmentService.class);
    private static final Set<String> MOVEABLE_RESOURCE_TYPES = Set.of("Instance", "Volume", "BootVolume");
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

    private static String tenancyId(OciUser user) {
        return user.getOciTenantId();
    }

    public Map<String, Object> listCompartments(String tenantId, String parentId, String keyword) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            String tenancy = tenancyId(user);
            boolean atRoot = StrUtil.isBlank(parentId) || tenancy.equals(parentId.trim());

            try {
                Object var24;
                try (OciClientService client = this.buildClient(tenantId)) {
                    IdentityClient identity = client.getIdentityClient();
                    Tenancy tenancyInfo = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy();
                    List<Compartment> subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                    subtree = subtree.stream().filter(cx -> cx.getLifecycleState() != LifecycleState.Deleted).collect(Collectors.toList());
                    Map<String, Integer> childCounts = buildChildCounts(subtree, tenancy);
                    List<Map<String, Object>> items = new ArrayList<>();
                    String listParentId = atRoot ? tenancy : parentId.trim();

                    for (Compartment c : subtree) {
                        if (listParentId.equals(c.getCompartmentId())) {
                            items.add(compartmentRow(c, childCounts, false));
                        }
                    }

                    items.sort(Comparator.comparing(m -> String.valueOf(m.get("name")), String.CASE_INSENSITIVE_ORDER));
                    if (atRoot) {
                        items.add(
                            compartmentRow(
                                tenancy,
                                tenancyInfo.getName() + " (root)",
                                tenancyInfo.getDescription(),
                                LifecycleState.Active.getValue(),
                                tenancy,
                                childCounts.getOrDefault(tenancy, 0),
                                null,
                                true
                            )
                        );
                    }

                    if (StrUtil.isNotBlank(keyword)) {
                        String kw = keyword.trim().toLowerCase();
                        items = items.stream()
                            .filter(m -> String.valueOf(m.get("name")).toLowerCase().contains(kw) || String.valueOf(m.get("id")).toLowerCase().contains(kw))
                            .collect(Collectors.toList());
                    }

                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("tenancyId", tenancy);
                    out.put("tenancyName", tenancyInfo.getName());
                    out.put("parentId", atRoot ? tenancy : parentId.trim());
                    out.put("flatSubtree", false);
                    out.put("directChildrenOnly", true);
                    out.put("items", items);
                    out.put("count", items.size());
                    out.put("breadcrumb", buildBreadcrumb(subtree, tenancy, tenancyInfo.getName(), atRoot ? tenancy : parentId.trim()));
                    var24 = out;
                }

                return (Map<String, Object>)var24;
            } catch (OciException var18) {
                throw var18;
            } catch (BmcException var19) {
                throw new OciException("获取区间列表失败: " + ociMessage(var19));
            } catch (Exception var20) {
                log.warn("listCompartments failed: {}", var20.getMessage());
                throw new OciException("获取区间列表失败: " + var20.getMessage());
            }
        }
    }

    public Map<String, Object> listCompartmentsPicker(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (user == null) {
            throw new OciException("租户配置不存在");
        } else {
            String tenancy = tenancyId(user);

            try {
                Object var24;
                try (OciClientService client = this.buildClient(tenantId)) {
                    IdentityClient identity = client.getIdentityClient();
                    Tenancy tenancyInfo = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy();
                    String rootName = tenancyInfo.getName();
                    List<Compartment> subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                    Map<String, Compartment> byId = new HashMap<>();

                    for (Compartment c : subtree) {
                        if (c.getId() != null) {
                            byId.put(c.getId(), c);
                        }
                    }

                    List<Map<String, Object>> items = new ArrayList<>();
                    Map<String, Object> root = new LinkedHashMap<>();
                    root.put("id", tenancy);
                    root.put("name", rootName + " (root)");
                    root.put("pathLabel", rootName + " (root)");
                    root.put("root", true);
                    root.put("lifecycleState", "ACTIVE");
                    items.add(root);

                    for (Compartment cx : subtree) {
                        String state = stateName(cx.getLifecycleState());
                        if ("ACTIVE".equals(state)) {
                            Map<String, Object> m = new LinkedHashMap<>();
                            m.put("id", cx.getId());
                            m.put("name", cx.getName());
                            m.put("pathLabel", buildPathLabel(cx.getId(), tenancy, rootName, byId));
                            m.put("root", false);
                            m.put("lifecycleState", state);
                            items.add(m);
                        }
                    }

                    items.sort(Comparator.comparing(o -> String.valueOf(o.get("pathLabel")), String.CASE_INSENSITIVE_ORDER));
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("tenancyId", tenancy);
                    out.put("items", items);
                    var24 = out;
                }

                return (Map<String, Object>)var24;
            } catch (OciException var18) {
                throw var18;
            } catch (BmcException var19) {
                throw new OciException("获取区间列表失败: " + ociMessage(var19));
            } catch (Exception var20) {
                log.warn("listCompartmentsPicker failed: {}", var20.getMessage());
                throw new OciException("获取区间列表失败: " + var20.getMessage());
            }
        }
    }

    public Map<String, Object> getCompartment(String tenantId, String compartmentId) {
        if (StrUtil.isBlank(compartmentId)) {
            throw new OciException("compartmentId 不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            if (user == null) {
                throw new OciException("租户配置不存在");
            } else {
                String tenancy = tenancyId(user);
                String cid = compartmentId.trim();
                boolean isRoot = tenancy.equals(cid);

                try {
                    Object var24;
                    try (OciClientService client = this.buildClient(tenantId)) {
                        IdentityClient identity = client.getIdentityClient();
                        Map<String, Object> detail = new LinkedHashMap<>();
                        if (isRoot) {
                            Tenancy t = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy();
                            detail.put("id", tenancy);
                            detail.put("name", t.getName() + " (root)");
                            detail.put("description", t.getDescription());
                            detail.put("lifecycleState", "ACTIVE");
                            detail.put("parentId", null);
                            detail.put("root", true);
                            detail.put("timeCreated", null);
                        } else {
                            Compartment c = identity.getCompartment(GetCompartmentRequest.builder().compartmentId(cid).build()).getCompartment();
                            detail.put("id", c.getId());
                            detail.put("name", c.getName());
                            detail.put("description", c.getDescription());
                            detail.put("lifecycleState", stateName(c.getLifecycleState()));
                            detail.put("parentId", c.getCompartmentId());
                            detail.put("root", false);
                            detail.put("timeCreated", c.getTimeCreated());
                        }

                        List<Compartment> subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                        subtree = subtree.stream().filter(cx -> cx.getLifecycleState() != LifecycleState.Deleted).collect(Collectors.toList());
                        Map<String, Integer> childCounts = buildChildCounts(subtree, tenancy);
                        detail.put("childCount", childCounts.getOrDefault(cid, 0));
                        List<Map<String, Object>> children = new ArrayList<>();

                        for (Compartment c : subtree) {
                            if (cid.equals(c.getCompartmentId())) {
                                children.add(compartmentRow(c, childCounts, false));
                            }
                        }

                        detail.put("children", children);
                        String rootName = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy().getName();
                        detail.put("breadcrumb", buildBreadcrumb(subtree, tenancy, rootName, cid));
                        var24 = detail;
                    }

                    return (Map<String, Object>)var24;
                } catch (OciException var17) {
                    throw var17;
                } catch (BmcException var18) {
                    throw new OciException("获取区间详情失败: " + ociMessage(var18));
                } catch (Exception var19) {
                    throw new OciException("获取区间详情失败: " + var19.getMessage());
                }
            }
        }
    }

    public Map<String, Object> createCompartment(String tenantId, String parentId, String name, String description) {
        validateCompartmentName(name);
        if (StrUtil.isBlank(parentId)) {
            throw new OciException("父区间不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            String tenancy = tenancyId(user);
            String parent = parentId.trim();
            if (!tenancy.equals(parent) && !parent.startsWith("ocid1.compartment.")) {
                throw new OciException("父区间 OCID 无效");
            } else {
                try {
                    Object var12;
                    try (OciClientService client = this.buildClient(tenantId)) {
                        CreateCompartmentResponse resp = client.getIdentityClient()
                            .createCompartment(
                                CreateCompartmentRequest.builder()
                                    .createCompartmentDetails(
                                        CreateCompartmentDetails.builder()
                                            .compartmentId(parent)
                                            .name(name.trim())
                                            .description(StrUtil.blankToDefault(description, name.trim()))
                                            .build()
                                    )
                                    .build()
                            );
                        Compartment c = resp.getCompartment();
                        Map<String, Object> out = new LinkedHashMap<>();
                        out.put("id", c.getId());
                        out.put("name", c.getName());
                        out.put("lifecycleState", stateName(c.getLifecycleState()));
                        var12 = out;
                    }

                    return (Map<String, Object>)var12;
                } catch (BmcException var15) {
                    throw new OciException("创建区间失败: " + ociMessage(var15));
                }
            }
        }
    }

    public Map<String, Object> updateCompartment(String tenantId, String compartmentId, String name, String description) {
        if (StrUtil.isBlank(compartmentId)) {
            throw new OciException("compartmentId 不能为空");
        } else {
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            if (tenancyId(user).equals(compartmentId.trim())) {
                throw new OciException("根区间（tenancy）不能在此重命名");
            } else {
                if (name != null && !name.isBlank()) {
                    validateCompartmentName(name);
                }

                Builder b = UpdateCompartmentDetails.builder();
                if (name != null && !name.isBlank()) {
                    b.name(name.trim());
                }

                if (description != null) {
                    b.description(description);
                }

                try {
                    Object var10;
                    try (OciClientService client = this.buildClient(tenantId)) {
                        Compartment c = client.getIdentityClient()
                            .updateCompartment(
                                UpdateCompartmentRequest.builder().compartmentId(compartmentId.trim()).updateCompartmentDetails(b.build()).build()
                            )
                            .getCompartment();
                        Map<String, Object> out = new LinkedHashMap<>();
                        out.put("id", c.getId());
                        out.put("name", c.getName());
                        out.put("description", c.getDescription());
                        out.put("lifecycleState", stateName(c.getLifecycleState()));
                        var10 = out;
                    }

                    return (Map<String, Object>)var10;
                } catch (BmcException var13) {
                    throw new OciException("更新区间失败: " + ociMessage(var13));
                }
            }
        }
    }

    public void deleteCompartment(String tenantId, String compartmentId) {
        OciUser user = (OciUser)this.userMapper.selectById(tenantId);
        if (tenancyId(user).equals(compartmentId.trim())) {
            throw new OciException("不能删除根区间（tenancy）");
        } else {
            try {
                try (OciClientService client = this.buildClient(tenantId)) {
                    client.getIdentityClient().deleteCompartment(DeleteCompartmentRequest.builder().compartmentId(compartmentId.trim()).build());
                }
            } catch (BmcException var9) {
                throw new OciException("删除区间失败: " + ociMessage(var9));
            }
        }
    }

    public Map<String, Object> moveCompartment(String tenantId, String compartmentId, String newParentId) {
        if (!StrUtil.isBlank(compartmentId) && !StrUtil.isBlank(newParentId)) {
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            if (tenancyId(user).equals(compartmentId.trim())) {
                throw new OciException("不能移动根区间");
            } else {
                try {
                    Object var8;
                    try (OciClientService client = this.buildClient(tenantId)) {
                        MoveCompartmentResponse resp = client.getIdentityClient()
                            .moveCompartment(
                                MoveCompartmentRequest.builder()
                                    .compartmentId(compartmentId.trim())
                                    .moveCompartmentDetails(MoveCompartmentDetails.builder().targetCompartmentId(newParentId.trim()).build())
                                    .build()
                            );
                        Map<String, Object> out = new LinkedHashMap<>();
                        out.put("compartmentId", compartmentId.trim());
                        out.put("targetParentId", newParentId.trim());
                        out.put("workRequestId", resp.getOpcWorkRequestId());
                        var8 = out;
                    }

                    return (Map<String, Object>)var8;
                } catch (BmcException var11) {
                    throw new OciException("移动区间失败: " + ociMessage(var11));
                }
            }
        } else {
            throw new OciException("compartmentId 与 newParentId 不能为空");
        }
    }

    public Map<String, Object> listResources(String tenantId, String compartmentId, String pageToken, Integer limit) {
        if (StrUtil.isBlank(compartmentId)) {
            throw new OciException("compartmentId 不能为空");
        } else {
            int lim = limit != null && limit >= 1 ? Math.min(limit, 100) : 50;
            OciUser user = (OciUser)this.userMapper.selectById(tenantId);
            if (user == null) {
                throw new OciException("租户配置不存在");
            } else {
                try {
                    Object var31;
                    try (OciClientService client = this.buildClient(tenantId)) {
                        ResourceSearchClient searchClient = ResourceSearchClient.builder().build(client.getProvider());

                        try {
                            String query = "query all resources where compartmentId = '" + compartmentId.trim().replace("'", "\\'") + "'";
                            com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest.Builder req = SearchResourcesRequest.builder()
                                .searchDetails(StructuredSearchDetails.builder().query(query).build())
                                .limit(lim)
                                .tenantId(tenancyId(user));
                            if (StrUtil.isNotBlank(pageToken)) {
                                req.page(pageToken);
                            }

                            SearchResourcesResponse resp = searchClient.searchResources(req.build());
                            List<Map<String, Object>> items = new ArrayList<>();
                            List<ResourceSummary> summaries = resp.getResourceSummaryCollection() != null
                                ? resp.getResourceSummaryCollection().getItems()
                                : null;
                            if (summaries != null) {
                                for (ResourceSummary r : summaries) {
                                    Map<String, Object> m = new LinkedHashMap<>();
                                    m.put("identifier", r.getIdentifier());
                                    m.put("displayName", r.getDisplayName());
                                    m.put("resourceType", r.getResourceType());
                                    m.put("lifecycleState", r.getLifecycleState());
                                    m.put("compartmentId", r.getCompartmentId());
                                    m.put("timeCreated", r.getTimeCreated());
                                    m.put("moveable", MOVEABLE_RESOURCE_TYPES.contains(r.getResourceType()));
                                    items.add(m);
                                }
                            }

                            Map<String, Object> out = new LinkedHashMap<>();
                            out.put("items", items);
                            out.put("count", items.size());
                            out.put("opcNextPage", resp.getOpcNextPage());
                            out.put("moveableTypes", MOVEABLE_RESOURCE_TYPES);
                            var31 = out;
                        } finally {
                            searchClient.close();
                        }
                    }

                    return (Map<String, Object>)var31;
                } catch (OciException var27) {
                    throw var27;
                } catch (BmcException var28) {
                    throw new OciException("查询区间资源失败: " + ociMessage(var28));
                } catch (Exception var29) {
                    throw new OciException("查询区间资源失败: " + var29.getMessage());
                }
            }
        }
    }

    public void moveResource(String tenantId, String resourceId, String resourceType, String targetCompartmentId) {
        if (!StrUtil.isBlank(resourceId) && !StrUtil.isBlank(resourceType) && !StrUtil.isBlank(targetCompartmentId)) {
            String type = resourceType.trim();
            if (!MOVEABLE_RESOURCE_TYPES.contains(type)) {
                throw new OciException("暂不支持迁移资源类型: " + type + "，请在 OCI 控制台操作");
            } else {
                try {
                    try (OciClientService client = this.buildClient(tenantId)) {
                        switch (type) {
                            case "Instance":
                                client.getComputeClient()
                                    .changeInstanceCompartment(
                                        ChangeInstanceCompartmentRequest.builder()
                                            .instanceId(resourceId.trim())
                                            .changeInstanceCompartmentDetails(
                                                ChangeInstanceCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()
                                            )
                                            .build()
                                    );
                                break;
                            case "Volume":
                                client.getBlockstorageClient()
                                    .changeVolumeCompartment(
                                        ChangeVolumeCompartmentRequest.builder()
                                            .volumeId(resourceId.trim())
                                            .changeVolumeCompartmentDetails(
                                                ChangeVolumeCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()
                                            )
                                            .build()
                                    );
                                break;
                            case "BootVolume":
                                client.getBlockstorageClient()
                                    .changeBootVolumeCompartment(
                                        ChangeBootVolumeCompartmentRequest.builder()
                                            .bootVolumeId(resourceId.trim())
                                            .changeBootVolumeCompartmentDetails(
                                                ChangeBootVolumeCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()
                                            )
                                            .build()
                                    );
                                break;
                            default:
                                throw new OciException("不支持的资源类型: " + type);
                        }
                    }
                } catch (BmcException var11) {
                    throw new OciException("迁移资源失败: " + ociMessage(var11));
                }
            }
        } else {
            throw new OciException("resourceId、resourceType、targetCompartmentId 不能为空");
        }
    }

    private List<Compartment> listCompartmentsPaginated(IdentityClient identity, String tenancyId, boolean subtree) {
        List<Compartment> all = new ArrayList<>();

        for (LifecycleState state : List.of(LifecycleState.Active, LifecycleState.Deleting)) {
            String page = null;

            do {
                com.oracle.bmc.identity.requests.ListCompartmentsRequest.Builder b = ListCompartmentsRequest.builder()
                    .compartmentId(tenancyId)
                    .accessLevel(AccessLevel.Accessible)
                    .compartmentIdInSubtree(subtree)
                    .lifecycleState(state);
                if (page != null) {
                    b.page(page);
                }

                ListCompartmentsResponse resp = identity.listCompartments(b.build());
                if (resp.getItems() != null) {
                    all.addAll(resp.getItems());
                }

                page = resp.getOpcNextPage();
            } while (page == null || page.isBlank());
        }

        Map<String, Compartment> byId = new LinkedHashMap<>();

        for (Compartment c : all) {
            if (c.getId() != null) {
                byId.putIfAbsent(c.getId(), c);
            }
        }

        return new ArrayList<>(byId.values());
    }

    private static Map<String, Integer> buildChildCounts(List<Compartment> subtree, String tenancyId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put(tenancyId, 0);

        for (Compartment c : subtree) {
            String pid = c.getCompartmentId();
            if (pid != null) {
                counts.merge(pid, 1, Integer::sum);
            }
        }

        return counts;
    }

    private static Map<String, Object> compartmentRow(Compartment c, Map<String, Integer> childCounts, boolean root) {
        return compartmentRow(
            c.getId(),
            c.getName(),
            c.getDescription(),
            stateName(c.getLifecycleState()),
            c.getCompartmentId(),
            childCounts.getOrDefault(c.getId(), 0),
            c.getTimeCreated(),
            root
        );
    }

    private static Map<String, Object> compartmentRow(
        String id, String name, String description, String state, String parentId, int childCount, Date timeCreated, boolean root
    ) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("description", description);
        m.put("lifecycleState", state);
        m.put("parentId", parentId);
        m.put("childCount", childCount);
        m.put("timeCreated", timeCreated);
        m.put("root", root);
        return m;
    }

    private static String buildPathLabel(String compartmentId, String tenancyId, String rootName, Map<String, Compartment> byId) {
        if (tenancyId.equals(compartmentId)) {
            return rootName + " (root)";
        } else {
            Deque<String> names = new ArrayDeque<>();
            String cur = compartmentId;
            int guard = 0;

            while (cur != null && !tenancyId.equals(cur) && guard++ < 32) {
                Compartment c = byId.get(cur);
                if (c == null) {
                    break;
                }

                names.addFirst(c.getName());
                cur = c.getCompartmentId();
            }

            List<String> parts = new ArrayList<>();
            parts.add(rootName + " (root)");
            parts.addAll(names);
            return String.join(" / ", parts);
        }
    }

    private static List<Map<String, String>> buildBreadcrumb(List<Compartment> subtree, String tenancyId, String rootName, String currentId) {
        Map<String, Compartment> byId = new HashMap<>();

        for (Compartment c : subtree) {
            byId.put(c.getId(), c);
        }

        List<Map<String, String>> chain = new ArrayList<>();
        chain.add(Map.of("id", tenancyId, "name", rootName + " (root)"));
        if (tenancyId.equals(currentId)) {
            return chain;
        } else {
            Deque<String> ids = new ArrayDeque<>();
            String cur = currentId;
            int guard = 0;

            while (cur != null && !tenancyId.equals(cur) && guard++ < 20) {
                ids.addFirst(cur);
                Compartment c = byId.get(cur);
                if (c == null) {
                    break;
                }

                cur = c.getCompartmentId();
            }

            for (String id : ids) {
                Compartment c = byId.get(id);
                chain.add(Map.of("id", id, "name", c != null ? c.getName() : id));
            }

            return chain;
        }
    }

    private static void validateCompartmentName(String name) {
        if (name != null && !name.isBlank()) {
            String n = name.trim();
            if (n.length() > 100) {
                throw new OciException("区间名称不能超过 100 个字符");
            }
        } else {
            throw new OciException("区间名称不能为空");
        }
    }

    private static String stateName(LifecycleState s) {
        return s == null ? "—" : s.getValue();
    }

    private static String ociMessage(BmcException e) {
        return e.getMessage() != null ? e.getMessage() : "HTTP " + e.getStatusCode();
    }
}
