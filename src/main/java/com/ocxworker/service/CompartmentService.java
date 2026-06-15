/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.CompartmentService
 *  com.ocxworker.service.OciClientService
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.core.model.ChangeBootVolumeCompartmentDetails
 *  com.oracle.bmc.core.model.ChangeInstanceCompartmentDetails
 *  com.oracle.bmc.core.model.ChangeVolumeCompartmentDetails
 *  com.oracle.bmc.core.requests.ChangeBootVolumeCompartmentRequest
 *  com.oracle.bmc.core.requests.ChangeInstanceCompartmentRequest
 *  com.oracle.bmc.core.requests.ChangeVolumeCompartmentRequest
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.identity.model.Compartment$LifecycleState
 *  com.oracle.bmc.identity.model.CreateCompartmentDetails
 *  com.oracle.bmc.identity.model.MoveCompartmentDetails
 *  com.oracle.bmc.identity.model.Tenancy
 *  com.oracle.bmc.identity.model.UpdateCompartmentDetails
 *  com.oracle.bmc.identity.model.UpdateCompartmentDetails$Builder
 *  com.oracle.bmc.identity.requests.CreateCompartmentRequest
 *  com.oracle.bmc.identity.requests.DeleteCompartmentRequest
 *  com.oracle.bmc.identity.requests.GetCompartmentRequest
 *  com.oracle.bmc.identity.requests.GetTenancyRequest
 *  com.oracle.bmc.identity.requests.ListCompartmentsRequest
 *  com.oracle.bmc.identity.requests.ListCompartmentsRequest$AccessLevel
 *  com.oracle.bmc.identity.requests.ListCompartmentsRequest$Builder
 *  com.oracle.bmc.identity.requests.MoveCompartmentRequest
 *  com.oracle.bmc.identity.requests.UpdateCompartmentRequest
 *  com.oracle.bmc.identity.responses.CreateCompartmentResponse
 *  com.oracle.bmc.identity.responses.ListCompartmentsResponse
 *  com.oracle.bmc.identity.responses.MoveCompartmentResponse
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.resourcesearch.ResourceSearchClient
 *  com.oracle.bmc.resourcesearch.model.ResourceSummary
 *  com.oracle.bmc.resourcesearch.model.SearchDetails
 *  com.oracle.bmc.resourcesearch.model.StructuredSearchDetails
 *  com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest
 *  com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest$Builder
 *  com.oracle.bmc.resourcesearch.responses.SearchResourcesResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciClientService;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
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
import com.oracle.bmc.identity.requests.CreateCompartmentRequest;
import com.oracle.bmc.identity.requests.DeleteCompartmentRequest;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.identity.requests.MoveCompartmentRequest;
import com.oracle.bmc.identity.requests.UpdateCompartmentRequest;
import com.oracle.bmc.identity.responses.CreateCompartmentResponse;
import com.oracle.bmc.identity.responses.ListCompartmentsResponse;
import com.oracle.bmc.identity.responses.MoveCompartmentResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.resourcesearch.ResourceSearchClient;
import com.oracle.bmc.resourcesearch.model.ResourceSummary;
import com.oracle.bmc.resourcesearch.model.SearchDetails;
import com.oracle.bmc.resourcesearch.model.StructuredSearchDetails;
import com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest;
import com.oracle.bmc.resourcesearch.responses.SearchResourcesResponse;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class CompartmentService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CompartmentService.class);
    private static final Set<String> MOVEABLE_RESOURCE_TYPES = Set.of("Instance", "Volume", "BootVolume");
    @Resource
    private OciUserMapper userMapper;

    private OciClientService buildClient(String tenantId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return new OciClientService(SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build());
    }

    private static String tenancyId(OciUser user) {
        return user.getOciTenantId();
    }

    public Map<String, Object> listCompartments(String tenantId, String parentId, String keyword) {
        LinkedHashMap<String, Object> linkedHashMap;
        block14: {
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            if (user == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            String tenancy = CompartmentService.tenancyId((OciUser)user);
            boolean atRoot = StrUtil.isBlank((CharSequence)parentId) || tenancy.equals(parentId.trim());
            OciClientService client = this.buildClient(tenantId);
            try {
                IdentityClient identity = client.getIdentityClient();
                Tenancy tenancyInfo = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy();
                List subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                subtree = subtree.stream().filter(c -> c.getLifecycleState() != Compartment.LifecycleState.Deleted).collect(Collectors.toList());
                Map childCounts = CompartmentService.buildChildCounts(subtree, (String)tenancy);
                List<Object> items = new ArrayList<Map>();
                String listParentId = atRoot ? tenancy : parentId.trim();
                for (Compartment c2 : subtree) {
                    if (!listParentId.equals(c2.getCompartmentId())) continue;
                    items.add(CompartmentService.compartmentRow((Compartment)c2, (Map)childCounts, (boolean)false));
                }
                items.sort(Comparator.comparing(m -> String.valueOf(m.get("name")), String.CASE_INSENSITIVE_ORDER));
                if (atRoot) {
                    items.add(CompartmentService.compartmentRow((String)tenancy, (String)(tenancyInfo.getName() + " (root)"), (String)tenancyInfo.getDescription(), (String)Compartment.LifecycleState.Active.getValue(), (String)tenancy, (int)childCounts.getOrDefault(tenancy, 0), null, (boolean)true));
                }
                if (StrUtil.isNotBlank((CharSequence)keyword)) {
                    String kw = keyword.trim().toLowerCase();
                    items = items.stream().filter(m -> String.valueOf(m.get("name")).toLowerCase().contains(kw) || String.valueOf(m.get("id")).toLowerCase().contains(kw)).collect(Collectors.toList());
                }
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("tenancyId", tenancy);
                out.put("tenancyName", tenancyInfo.getName());
                out.put("parentId", atRoot ? tenancy : parentId.trim());
                out.put("flatSubtree", false);
                out.put("directChildrenOnly", true);
                out.put("items", items);
                out.put("count", items.size());
                out.put("breadcrumb", CompartmentService.buildBreadcrumb(subtree, (String)tenancy, (String)tenancyInfo.getName(), (String)(atRoot ? tenancy : parentId.trim())));
                linkedHashMap = out;
                if (client == null) break block14;
            }
            catch (Throwable throwable) {
                try {
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
                catch (OciException e) {
                    throw e;
                }
                catch (BmcException e) {
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u5217\u8868\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
                catch (Exception e) {
                    log.warn("listCompartments failed: {}", (Object)e.getMessage());
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, Object> listCompartmentsPicker(String tenantId) {
        LinkedHashMap<String, Object> linkedHashMap;
        block13: {
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            if (user == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            String tenancy = CompartmentService.tenancyId((OciUser)user);
            OciClientService client = this.buildClient(tenantId);
            try {
                IdentityClient identity = client.getIdentityClient();
                Tenancy tenancyInfo = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy();
                String rootName = tenancyInfo.getName();
                List subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                HashMap<String, Compartment> byId = new HashMap<String, Compartment>();
                for (Compartment c : subtree) {
                    if (c.getId() == null) continue;
                    byId.put(c.getId(), c);
                }
                ArrayList<Map> items = new ArrayList<Map>();
                LinkedHashMap<String, Object> root = new LinkedHashMap<String, Object>();
                root.put("id", tenancy);
                root.put("name", rootName + " (root)");
                root.put("pathLabel", rootName + " (root)");
                root.put("root", true);
                root.put("lifecycleState", "ACTIVE");
                items.add(root);
                for (Compartment c : subtree) {
                    String state = CompartmentService.stateName((Compartment.LifecycleState)c.getLifecycleState());
                    if (!"ACTIVE".equals(state)) continue;
                    LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("pathLabel", CompartmentService.buildPathLabel((String)c.getId(), (String)tenancy, (String)rootName, byId));
                    m.put("root", false);
                    m.put("lifecycleState", state);
                    items.add(m);
                }
                items.sort(Comparator.comparing(o -> String.valueOf(o.get("pathLabel")), String.CASE_INSENSITIVE_ORDER));
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("tenancyId", tenancy);
                out.put("items", items);
                linkedHashMap = out;
                if (client == null) break block13;
            }
            catch (Throwable throwable) {
                try {
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
                catch (OciException e) {
                    throw e;
                }
                catch (BmcException e) {
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u5217\u8868\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
                catch (Exception e) {
                    log.warn("listCompartmentsPicker failed: {}", (Object)e.getMessage());
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, Object> getCompartment(String tenantId, String compartmentId) {
        LinkedHashMap<String, Object> linkedHashMap;
        block15: {
            if (StrUtil.isBlank((CharSequence)compartmentId)) {
                throw new OciException("compartmentId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            if (user == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            String tenancy = CompartmentService.tenancyId((OciUser)user);
            String cid = compartmentId.trim();
            boolean isRoot = tenancy.equals(cid);
            OciClientService client = this.buildClient(tenantId);
            try {
                IdentityClient identity = client.getIdentityClient();
                LinkedHashMap<String, Object> detail = new LinkedHashMap<String, Object>();
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
                    Compartment c2 = identity.getCompartment(GetCompartmentRequest.builder().compartmentId(cid).build()).getCompartment();
                    detail.put("id", c2.getId());
                    detail.put("name", c2.getName());
                    detail.put("description", c2.getDescription());
                    detail.put("lifecycleState", CompartmentService.stateName((Compartment.LifecycleState)c2.getLifecycleState()));
                    detail.put("parentId", c2.getCompartmentId());
                    detail.put("root", false);
                    detail.put("timeCreated", c2.getTimeCreated());
                }
                List subtree = this.listCompartmentsPaginated(identity, tenancy, true);
                subtree = subtree.stream().filter(c -> c.getLifecycleState() != Compartment.LifecycleState.Deleted).collect(Collectors.toList());
                Map childCounts = CompartmentService.buildChildCounts(subtree, (String)tenancy);
                detail.put("childCount", childCounts.getOrDefault(cid, 0));
                ArrayList<Map> children = new ArrayList<Map>();
                for (Compartment c3 : subtree) {
                    if (!cid.equals(c3.getCompartmentId())) continue;
                    children.add(CompartmentService.compartmentRow((Compartment)c3, (Map)childCounts, (boolean)false));
                }
                detail.put("children", children);
                String rootName = identity.getTenancy(GetTenancyRequest.builder().tenancyId(tenancy).build()).getTenancy().getName();
                detail.put("breadcrumb", CompartmentService.buildBreadcrumb(subtree, (String)tenancy, (String)rootName, (String)cid));
                linkedHashMap = detail;
                if (client == null) break block15;
            }
            catch (Throwable throwable) {
                try {
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
                catch (OciException e) {
                    throw e;
                }
                catch (BmcException e) {
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u8be6\u60c5\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
                catch (Exception e) {
                    throw new OciException("\u83b7\u53d6\u533a\u95f4\u8be6\u60c5\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, Object> createCompartment(String tenantId, String parentId, String name, String description) {
        LinkedHashMap<String, Object> linkedHashMap;
        block10: {
            String parent;
            CompartmentService.validateCompartmentName((String)name);
            if (StrUtil.isBlank((CharSequence)parentId)) {
                throw new OciException("\u7236\u533a\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            String tenancy = CompartmentService.tenancyId((OciUser)user);
            if (!tenancy.equals(parent = parentId.trim()) && !parent.startsWith("ocid1.compartment.")) {
                throw new OciException("\u7236\u533a\u95f4 OCID \u65e0\u6548");
            }
            OciClientService client = this.buildClient(tenantId);
            try {
                CreateCompartmentResponse resp = client.getIdentityClient().createCompartment(CreateCompartmentRequest.builder().createCompartmentDetails(CreateCompartmentDetails.builder().compartmentId(parent).name(name.trim()).description(StrUtil.blankToDefault((CharSequence)description, (String)name.trim())).build()).build());
                Compartment c = resp.getCompartment();
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("id", c.getId());
                out.put("name", c.getName());
                out.put("lifecycleState", CompartmentService.stateName((Compartment.LifecycleState)c.getLifecycleState()));
                linkedHashMap = out;
                if (client == null) break block10;
            }
            catch (Throwable throwable) {
                try {
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
                catch (BmcException e) {
                    throw new OciException("\u521b\u5efa\u533a\u95f4\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, Object> updateCompartment(String tenantId, String compartmentId, String name, String description) {
        LinkedHashMap<String, Object> linkedHashMap;
        block13: {
            if (StrUtil.isBlank((CharSequence)compartmentId)) {
                throw new OciException("compartmentId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            if (CompartmentService.tenancyId((OciUser)user).equals(compartmentId.trim())) {
                throw new OciException("\u6839\u533a\u95f4\uff08tenancy\uff09\u4e0d\u80fd\u5728\u6b64\u91cd\u547d\u540d");
            }
            if (name != null && !name.isBlank()) {
                CompartmentService.validateCompartmentName((String)name);
            }
            UpdateCompartmentDetails.Builder b = UpdateCompartmentDetails.builder();
            if (name != null && !name.isBlank()) {
                b.name(name.trim());
            }
            if (description != null) {
                b.description(description);
            }
            OciClientService client = this.buildClient(tenantId);
            try {
                Compartment c = client.getIdentityClient().updateCompartment(UpdateCompartmentRequest.builder().compartmentId(compartmentId.trim()).updateCompartmentDetails(b.build()).build()).getCompartment();
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("id", c.getId());
                out.put("name", c.getName());
                out.put("description", c.getDescription());
                out.put("lifecycleState", CompartmentService.stateName((Compartment.LifecycleState)c.getLifecycleState()));
                linkedHashMap = out;
                if (client == null) break block13;
            }
            catch (Throwable throwable) {
                try {
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
                catch (BmcException e) {
                    throw new OciException("\u66f4\u65b0\u533a\u95f4\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public void deleteCompartment(String tenantId, String compartmentId) {
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (CompartmentService.tenancyId((OciUser)user).equals(compartmentId.trim())) {
            throw new OciException("\u4e0d\u80fd\u5220\u9664\u6839\u533a\u95f4\uff08tenancy\uff09");
        }
        try (OciClientService client = this.buildClient(tenantId);){
            client.getIdentityClient().deleteCompartment(DeleteCompartmentRequest.builder().compartmentId(compartmentId.trim()).build());
        }
        catch (BmcException e) {
            throw new OciException("\u5220\u9664\u533a\u95f4\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
        }
    }

    public Map<String, Object> moveCompartment(String tenantId, String compartmentId, String newParentId) {
        LinkedHashMap<String, Object> linkedHashMap;
        block10: {
            if (StrUtil.isBlank((CharSequence)compartmentId) || StrUtil.isBlank((CharSequence)newParentId)) {
                throw new OciException("compartmentId \u4e0e newParentId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
            if (CompartmentService.tenancyId((OciUser)user).equals(compartmentId.trim())) {
                throw new OciException("\u4e0d\u80fd\u79fb\u52a8\u6839\u533a\u95f4");
            }
            OciClientService client = this.buildClient(tenantId);
            try {
                MoveCompartmentResponse resp = client.getIdentityClient().moveCompartment(MoveCompartmentRequest.builder().compartmentId(compartmentId.trim()).moveCompartmentDetails(MoveCompartmentDetails.builder().targetCompartmentId(newParentId.trim()).build()).build());
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("compartmentId", compartmentId.trim());
                out.put("targetParentId", newParentId.trim());
                out.put("workRequestId", resp.getOpcWorkRequestId());
                linkedHashMap = out;
                if (client == null) break block10;
            }
            catch (Throwable throwable) {
                try {
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
                catch (BmcException e) {
                    throw new OciException("\u79fb\u52a8\u533a\u95f4\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    public Map<String, Object> listResources(String tenantId, String compartmentId, String pageToken, Integer limit) {
        if (StrUtil.isBlank((CharSequence)compartmentId)) {
            throw new OciException("compartmentId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        int lim = limit == null || limit < 1 ? 50 : Math.min(limit, 100);
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)tenantId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.buildClient(tenantId);){
            LinkedHashMap<String, Object> linkedHashMap;
            ResourceSearchClient searchClient = ResourceSearchClient.builder().build((AbstractAuthenticationDetailsProvider)client.getProvider());
            try {
                List summaries;
                String query = "query all resources where compartmentId = '" + compartmentId.trim().replace("'", "\\'") + "'";
                SearchResourcesRequest.Builder req = SearchResourcesRequest.builder().searchDetails((SearchDetails)StructuredSearchDetails.builder().query(query).build()).limit(Integer.valueOf(lim)).tenantId(CompartmentService.tenancyId((OciUser)user));
                if (StrUtil.isNotBlank((CharSequence)pageToken)) {
                    req.page(pageToken);
                }
                SearchResourcesResponse resp = searchClient.searchResources(req.build());
                ArrayList items = new ArrayList();
                List list = summaries = resp.getResourceSummaryCollection() != null ? resp.getResourceSummaryCollection().getItems() : null;
                if (summaries != null) {
                    for (ResourceSummary r : summaries) {
                        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
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
                LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
                out.put("items", items);
                out.put("count", items.size());
                out.put("opcNextPage", resp.getOpcNextPage());
                out.put("moveableTypes", MOVEABLE_RESOURCE_TYPES);
                linkedHashMap = out;
            }
            catch (Throwable throwable) {
                searchClient.close();
                throw throwable;
            }
            searchClient.close();
            return linkedHashMap;
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            throw new OciException("\u67e5\u8be2\u533a\u95f4\u8d44\u6e90\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
        }
        catch (Exception e) {
            throw new OciException("\u67e5\u8be2\u533a\u95f4\u8d44\u6e90\u5931\u8d25: " + e.getMessage());
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void moveResource(String tenantId, String resourceId, String resourceType, String targetCompartmentId) {
        if (StrUtil.isBlank((CharSequence)resourceId) || StrUtil.isBlank((CharSequence)resourceType) || StrUtil.isBlank((CharSequence)targetCompartmentId)) {
            throw new OciException("resourceId\u3001resourceType\u3001targetCompartmentId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String type = resourceType.trim();
        if (!MOVEABLE_RESOURCE_TYPES.contains(type)) {
            throw new OciException("\u6682\u4e0d\u652f\u6301\u8fc1\u79fb\u8d44\u6e90\u7c7b\u578b: " + type + "\uff0c\u8bf7\u5728 OCI \u63a7\u5236\u53f0\u64cd\u4f5c");
        }
        try (OciClientService client = this.buildClient(tenantId);){
            switch (type) {
                case "Instance": {
                    client.getComputeClient().changeInstanceCompartment(ChangeInstanceCompartmentRequest.builder().instanceId(resourceId.trim()).changeInstanceCompartmentDetails(ChangeInstanceCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()).build());
                    return;
                }
                case "Volume": {
                    client.getBlockstorageClient().changeVolumeCompartment(ChangeVolumeCompartmentRequest.builder().volumeId(resourceId.trim()).changeVolumeCompartmentDetails(ChangeVolumeCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()).build());
                    return;
                }
                case "BootVolume": {
                    client.getBlockstorageClient().changeBootVolumeCompartment(ChangeBootVolumeCompartmentRequest.builder().bootVolumeId(resourceId.trim()).changeBootVolumeCompartmentDetails(ChangeBootVolumeCompartmentDetails.builder().compartmentId(targetCompartmentId.trim()).build()).build());
                    return;
                }
                default: {
                    throw new OciException("\u4e0d\u652f\u6301\u7684\u8d44\u6e90\u7c7b\u578b: " + type);
                }
            }
        }
        catch (BmcException e) {
            throw new OciException("\u8fc1\u79fb\u8d44\u6e90\u5931\u8d25: " + CompartmentService.ociMessage((BmcException)e));
        }
    }

    private List<Compartment> listCompartmentsPaginated(IdentityClient identity, String tenancyId, boolean subtree) {
        ArrayList all = new ArrayList();
        for (Compartment.LifecycleState state : List.of(Compartment.LifecycleState.Active, Compartment.LifecycleState.Deleting)) {
            ListCompartmentsResponse resp;
            String page = null;
            do {
                ListCompartmentsRequest.Builder b = ListCompartmentsRequest.builder().compartmentId(tenancyId).accessLevel(ListCompartmentsRequest.AccessLevel.Accessible).compartmentIdInSubtree(Boolean.valueOf(subtree)).lifecycleState(state);
                if (page != null) {
                    b.page(page);
                }
                if ((resp = identity.listCompartments(b.build())).getItems() == null) continue;
                all.addAll(resp.getItems());
            } while ((page = resp.getOpcNextPage()) != null && !page.isBlank());
        }
        LinkedHashMap<String, Compartment> byId = new LinkedHashMap<String, Compartment>();
        for (Compartment c : all) {
            if (c.getId() == null) continue;
            byId.putIfAbsent(c.getId(), c);
        }
        return new ArrayList<Compartment>(byId.values());
    }

    private static Map<String, Integer> buildChildCounts(List<Compartment> subtree, String tenancyId) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        counts.put(tenancyId, 0);
        for (Compartment c : subtree) {
            String pid = c.getCompartmentId();
            if (pid == null) continue;
            counts.merge(pid, 1, Integer::sum);
        }
        return counts;
    }

    private static Map<String, Object> compartmentRow(Compartment c, Map<String, Integer> childCounts, boolean root) {
        return CompartmentService.compartmentRow((String)c.getId(), (String)c.getName(), (String)c.getDescription(), (String)CompartmentService.stateName((Compartment.LifecycleState)c.getLifecycleState()), (String)c.getCompartmentId(), (int)childCounts.getOrDefault(c.getId(), 0), (Date)c.getTimeCreated(), (boolean)root);
    }

    private static Map<String, Object> compartmentRow(String id, String name, String description, String state, String parentId, int childCount, Date timeCreated, boolean root) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
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
        Compartment c;
        if (tenancyId.equals(compartmentId)) {
            return rootName + " (root)";
        }
        ArrayDeque<String> names = new ArrayDeque<String>();
        String cur = compartmentId;
        int guard = 0;
        while (cur != null && !tenancyId.equals(cur) && guard++ < 32 && (c = byId.get(cur)) != null) {
            names.addFirst(c.getName());
            cur = c.getCompartmentId();
        }
        ArrayList<Object> parts = new ArrayList<Object>();
        parts.add(rootName + " (root)");
        parts.addAll(names);
        return String.join((CharSequence)" / ", parts);
    }

    private static List<Map<String, String>> buildBreadcrumb(List<Compartment> subtree, String tenancyId, String rootName, String currentId) {
        HashMap<String, Compartment> byId = new HashMap<String, Compartment>();
        for (Compartment c : subtree) {
            byId.put(c.getId(), c);
        }
        ArrayList<Map<String, String>> chain = new ArrayList<Map<String, String>>();
        chain.add(Map.of("id", tenancyId, "name", rootName + " (root)"));
        if (tenancyId.equals(currentId)) {
            return chain;
        }
        ArrayDeque<String> ids = new ArrayDeque<String>();
        String cur = currentId;
        int guard = 0;
        while (cur != null && !tenancyId.equals(cur) && guard++ < 20) {
            ids.addFirst(cur);
            Compartment c = (Compartment)byId.get(cur);
            if (c == null) break;
            cur = c.getCompartmentId();
        }
        Iterator iterator = ids.iterator();
        while (iterator.hasNext()) {
            String id;
            Compartment c = (Compartment)byId.get(id = (String)iterator.next());
            chain.add(Map.of("id", id, "name", c != null ? c.getName() : id));
        }
        return chain;
    }

    private static void validateCompartmentName(String name) {
        if (name == null || name.isBlank()) {
            throw new OciException("\u533a\u95f4\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String n = name.trim();
        if (n.length() > 100) {
            throw new OciException("\u533a\u95f4\u540d\u79f0\u4e0d\u80fd\u8d85\u8fc7 100 \u4e2a\u5b57\u7b26");
        }
    }

    private static String stateName(Compartment.LifecycleState s) {
        return s == null ? "\u2014" : s.getValue();
    }

    private static String ociMessage(BmcException e) {
        return e.getMessage() != null ? e.getMessage() : "HTTP " + e.getStatusCode();
    }
}

