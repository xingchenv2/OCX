package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.util.ObjectStorageBucketPolicyHttp;
import com.oracle.bmc.core.model.BlockVolumeReplica;
import com.oracle.bmc.core.model.BlockVolumeReplicaDetails;
import com.oracle.bmc.core.model.BootVolume;
import com.oracle.bmc.core.model.BootVolumeAttachment;
import com.oracle.bmc.core.model.BootVolumeBackup;
import com.oracle.bmc.core.model.BootVolumeReplica;
import com.oracle.bmc.core.model.BootVolumeReplicaDetails;
import com.oracle.bmc.core.model.BootVolumeSourceFromBootVolumeReplicaDetails;
import com.oracle.bmc.core.model.CreateBootVolumeDetails;
import com.oracle.bmc.core.model.CreateVolumeBackupPolicyAssignmentDetails;
import com.oracle.bmc.core.model.CreateVolumeBackupPolicyDetails;
import com.oracle.bmc.core.model.CreateVolumeDetails;
import com.oracle.bmc.core.model.CreateVolumeGroupDetails;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.UpdateBootVolumeDetails;
import com.oracle.bmc.core.model.UpdateVolumeBackupPolicyDetails;
import com.oracle.bmc.core.model.UpdateVolumeDetails;
import com.oracle.bmc.core.model.UpdateVolumeGroupDetails;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.model.VolumeAttachment;
import com.oracle.bmc.core.model.VolumeBackup;
import com.oracle.bmc.core.model.VolumeBackupPolicy;
import com.oracle.bmc.core.model.VolumeBackupPolicyAssignment;
import com.oracle.bmc.core.model.VolumeBackupSchedule;
import com.oracle.bmc.core.model.VolumeGroup;
import com.oracle.bmc.core.model.VolumeGroupBackup;
import com.oracle.bmc.core.model.VolumeGroupReplica;
import com.oracle.bmc.core.model.VolumeGroupSourceFromVolumesDetails;
import com.oracle.bmc.core.model.VolumeSourceFromBlockVolumeReplicaDetails;
import com.oracle.bmc.core.model.Instance.LifecycleState;
import com.oracle.bmc.core.model.UpdateBootVolumeDetails.Builder;
import com.oracle.bmc.core.model.VolumeBackupSchedule.BackupType;
import com.oracle.bmc.core.model.VolumeBackupSchedule.DayOfWeek;
import com.oracle.bmc.core.model.VolumeBackupSchedule.Month;
import com.oracle.bmc.core.model.VolumeBackupSchedule.OffsetType;
import com.oracle.bmc.core.model.VolumeBackupSchedule.Period;
import com.oracle.bmc.core.requests.CreateBootVolumeRequest;
import com.oracle.bmc.core.requests.CreateVolumeBackupPolicyAssignmentRequest;
import com.oracle.bmc.core.requests.CreateVolumeBackupPolicyRequest;
import com.oracle.bmc.core.requests.CreateVolumeGroupRequest;
import com.oracle.bmc.core.requests.CreateVolumeRequest;
import com.oracle.bmc.core.requests.DeleteBootVolumeBackupRequest;
import com.oracle.bmc.core.requests.DeleteBootVolumeRequest;
import com.oracle.bmc.core.requests.DeleteVolumeBackupPolicyAssignmentRequest;
import com.oracle.bmc.core.requests.DeleteVolumeBackupPolicyRequest;
import com.oracle.bmc.core.requests.DeleteVolumeBackupRequest;
import com.oracle.bmc.core.requests.DeleteVolumeGroupBackupRequest;
import com.oracle.bmc.core.requests.DeleteVolumeGroupRequest;
import com.oracle.bmc.core.requests.DeleteVolumeRequest;
import com.oracle.bmc.core.requests.GetVolumeBackupPolicyAssetAssignmentRequest;
import com.oracle.bmc.core.requests.ListBlockVolumeReplicasRequest;
import com.oracle.bmc.core.requests.ListBootVolumeAttachmentsRequest;
import com.oracle.bmc.core.requests.ListBootVolumeBackupsRequest;
import com.oracle.bmc.core.requests.ListBootVolumeReplicasRequest;
import com.oracle.bmc.core.requests.ListBootVolumesRequest;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest;
import com.oracle.bmc.core.requests.ListVolumeBackupPoliciesRequest;
import com.oracle.bmc.core.requests.ListVolumeBackupsRequest;
import com.oracle.bmc.core.requests.ListVolumeGroupBackupsRequest;
import com.oracle.bmc.core.requests.ListVolumeGroupReplicasRequest;
import com.oracle.bmc.core.requests.ListVolumeGroupsRequest;
import com.oracle.bmc.core.requests.ListVolumesRequest;
import com.oracle.bmc.core.requests.UpdateBootVolumeRequest;
import com.oracle.bmc.core.requests.UpdateVolumeBackupPolicyRequest;
import com.oracle.bmc.core.requests.UpdateVolumeGroupRequest;
import com.oracle.bmc.core.requests.UpdateVolumeRequest;
import com.oracle.bmc.core.responses.GetVolumeBackupPolicyAssetAssignmentResponse;
import com.oracle.bmc.core.responses.ListBlockVolumeReplicasResponse;
import com.oracle.bmc.core.responses.ListBootVolumeAttachmentsResponse;
import com.oracle.bmc.core.responses.ListBootVolumeBackupsResponse;
import com.oracle.bmc.core.responses.ListBootVolumeReplicasResponse;
import com.oracle.bmc.core.responses.ListBootVolumesResponse;
import com.oracle.bmc.core.responses.ListInstancesResponse;
import com.oracle.bmc.core.responses.ListVolumeAttachmentsResponse;
import com.oracle.bmc.core.responses.ListVolumeBackupPoliciesResponse;
import com.oracle.bmc.core.responses.ListVolumeBackupsResponse;
import com.oracle.bmc.core.responses.ListVolumeGroupBackupsResponse;
import com.oracle.bmc.core.responses.ListVolumeGroupReplicasResponse;
import com.oracle.bmc.core.responses.ListVolumeGroupsResponse;
import com.oracle.bmc.core.responses.ListVolumesResponse;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.RegionSubscription;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import com.oracle.bmc.identity.responses.ListRegionSubscriptionsResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.model.Bucket;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.model.CreatePrivateEndpointDetails;
import com.oracle.bmc.objectstorage.model.PrivateEndpoint;
import com.oracle.bmc.objectstorage.model.PrivateEndpointSummary;
import com.oracle.bmc.objectstorage.model.UpdateBucketDetails;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails.PublicAccessType;
import com.oracle.bmc.objectstorage.model.UpdateBucketDetails.Versioning;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.CreatePrivateEndpointRequest;
import com.oracle.bmc.objectstorage.requests.DeleteBucketRequest;
import com.oracle.bmc.objectstorage.requests.DeletePrivateEndpointRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.ListPrivateEndpointsRequest;
import com.oracle.bmc.objectstorage.requests.UpdateBucketRequest;
import com.oracle.bmc.objectstorage.responses.CreatePrivateEndpointResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.responses.ListPrivateEndpointsResponse;
import jakarta.annotation.Resource;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciProxyConfigService ociProxyConfigService;

    public List<String> listSubscribedRegionIds(String userId) {
        if (userId != null && !userId.isBlank()) {
            OciUser ociUser = this.requireUser(userId);

            try {
                List var7;
                try (OciClientService client = new OciClientService(this.buildDto(ociUser))) {
                    ListRegionSubscriptionsResponse resp = client.getIdentityClient()
                        .listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(ociUser.getOciTenantId()).build());
                    List<RegionSubscription> items = resp.getItems();
                    if (items == null || items.isEmpty()) {
                        return fallbackRegionList(ociUser);
                    }

                    List<String> out = items.stream()
                        .map(r -> r.getRegionName())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .sorted()
                        .toList();
                    var7 = out.isEmpty() ? fallbackRegionList(ociUser) : out;
                }

                return var7;
            } catch (OciException var10) {
                throw var10;
            } catch (Exception var11) {
                log.warn("listRegionSubscriptions failed for user {}: {}", userId, var11.getMessage());
                return fallbackRegionList(ociUser);
            }
        } else {
            throw new OciException("缺少租户 id");
        }
    }

    private static List<String> fallbackRegionList(OciUser ociUser) {
        String r = ociUser.getOciRegion();
        return r != null && !r.isBlank() ? List.of(r.trim()) : List.of();
    }

    public List<Map<String, Object>> listCompartments(String userId, String region) {
        OciUser ociUser = this.requireUser(userId);
        String tenantId = ociUser.getOciTenantId();

        try {
            List var6;
            try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                var6 = client.listAllCompartments().stream().map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("compartmentId", c.getCompartmentId());
                    boolean isRoot = tenantId != null && tenantId.equals(c.getId());
                    m.put("isRoot", isRoot);
                    if (c.getLifecycleState() != null) {
                        m.put("lifecycleState", c.getLifecycleState().getValue());
                    }

                    return m;
                }).toList();
            }

            return var6;
        } catch (OciException var10) {
            throw var10;
        } catch (Exception var11) {
            throw new OciException("列出区间失败: " + var11.getMessage());
        }
    }

    public Map<String, Object> blockAggregate(String userId, String region, String compartmentIdOpt, String sections) {
        OciUser ociUser = this.requireUser(userId);
        boolean loadAllSections = sections == null || sections.isBlank();
        Set<String> requested = new HashSet<>();
        if (!loadAllSections) {
            for (String part : sections.split(",")) {
                String t = part.trim().toLowerCase(Locale.ROOT);
                if (!t.isEmpty()) {
                    requested.add(t);
                }
            }

            if (requested.contains("volumebackuppolicyassignments")) {
                requested.add("bootvolumes");
                requested.add("blockvolumes");
                requested.add("volumegroups");
                requested.add("volumebackuppolicies");
            }
        }

        Predicate<String> want = key -> loadAllSections ? true : requested.contains(key.toLowerCase(Locale.ROOT));

        try {
            Object var46;
            try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                List<Compartment> compartments = this.resolveCompartments(client, compartmentIdOpt);
                List<String> availabilityDomains = this.listAvailabilityDomainNames(client);
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("region", region);
                List<Map<String, Object>> bootVolumes = new ArrayList<>();
                List<Map<String, Object>> blockVolumes = new ArrayList<>();
                List<Map<String, Object>> bootBackups = new ArrayList<>();
                List<Map<String, Object>> blockBackups = new ArrayList<>();
                List<Map<String, Object>> bootReplicas = new ArrayList<>();
                List<Map<String, Object>> blockReplicas = new ArrayList<>();
                List<Map<String, Object>> volumeGroups = new ArrayList<>();
                List<Map<String, Object>> volumeGroupBackups = new ArrayList<>();
                List<Map<String, Object>> volumeGroupReplicas = new ArrayList<>();
                List<Map<String, Object>> backupPolicies = new ArrayList<>();
                List<Map<String, Object>> backupPolicyAssignments = new ArrayList<>();

                for (Compartment compartment : compartments) {
                    String cid = compartment.getId();
                    String cname = compartment.getName();
                    Map<String, String> instanceNames = Map.of();
                    if (want.test("bootVolumes") || want.test("blockVolumes")) {
                        instanceNames = this.loadInstanceNames(client, cid);
                    }

                    Map<String, List<Map<String, Object>>> bootAttach = new HashMap<>();
                    if (want.test("bootVolumes")) {
                        bootAttach = this.loadBootVolumeAttachments(client, cid, instanceNames, availabilityDomains);
                    }

                    Map<String, List<Map<String, Object>>> volAttach = new HashMap<>();
                    if (want.test("blockVolumes")) {
                        volAttach = this.loadVolumeAttachments(client, cid, instanceNames, availabilityDomains);
                    }

                    int bootStart = bootVolumes.size();
                    int blockStart = blockVolumes.size();
                    int vgStart = volumeGroups.size();
                    if (want.test("bootVolumes")) {
                        this.listBootVolumes(client, region, cid, cname, bootAttach, bootVolumes, availabilityDomains);
                    }

                    if (want.test("blockVolumes")) {
                        this.listBlockVolumes(client, region, cid, cname, volAttach, blockVolumes, availabilityDomains);
                    }

                    if (want.test("bootVolumeBackups")) {
                        this.listBootBackups(client, region, cid, cname, bootBackups);
                    }

                    if (want.test("blockVolumeBackups")) {
                        this.listBlockBackups(client, region, cid, cname, blockBackups);
                    }

                    if (want.test("bootVolumeReplicas")) {
                        this.listBootReplicas(client, region, cid, cname, bootReplicas, availabilityDomains);
                    }

                    if (want.test("blockVolumeReplicas")) {
                        this.listBlockReplicas(client, region, cid, cname, blockReplicas, availabilityDomains);
                    }

                    if (want.test("volumeGroups")) {
                        this.listVolumeGroups(client, region, cid, cname, volumeGroups, availabilityDomains);
                    }

                    if (want.test("volumeGroupBackups")) {
                        this.listVolumeGroupBackups(client, region, cid, cname, volumeGroupBackups);
                    }

                    if (want.test("volumeGroupReplicas")) {
                        this.listVolumeGroupReplicas(client, region, cid, cname, volumeGroupReplicas, availabilityDomains);
                    }

                    if (want.test("volumeBackupPolicies")) {
                        this.listBackupPolicies(client, region, cid, cname, backupPolicies);
                    }

                    if (want.test("volumeBackupPolicyAssignments")) {
                        List<String> policyAssetIds = new ArrayList<>();

                        for (int i = bootStart; i < bootVolumes.size(); i++) {
                            Object id = bootVolumes.get(i).get("id");
                            if (id != null) {
                                policyAssetIds.add(String.valueOf(id));
                            }
                        }

                        for (int ix = blockStart; ix < blockVolumes.size(); ix++) {
                            Object id = blockVolumes.get(ix).get("id");
                            if (id != null) {
                                policyAssetIds.add(String.valueOf(id));
                            }
                        }

                        for (int ixx = vgStart; ixx < volumeGroups.size(); ixx++) {
                            Object id = volumeGroups.get(ixx).get("id");
                            if (id != null) {
                                policyAssetIds.add(String.valueOf(id));
                            }
                        }

                        this.collectVolumeBackupPolicyAssignments(client, region, cid, cname, policyAssetIds, backupPolicyAssignments);
                    }
                }

                if (loadAllSections) {
                    out.put("bootVolumes", bootVolumes);
                    out.put("blockVolumes", blockVolumes);
                    out.put("bootVolumeBackups", bootBackups);
                    out.put("blockVolumeBackups", blockBackups);
                    out.put("bootVolumeReplicas", bootReplicas);
                    out.put("blockVolumeReplicas", blockReplicas);
                    out.put("volumeGroups", volumeGroups);
                    out.put("volumeGroupBackups", volumeGroupBackups);
                    out.put("volumeGroupReplicas", volumeGroupReplicas);
                    out.put("volumeBackupPolicies", backupPolicies);
                    out.put("volumeBackupPolicyAssignments", backupPolicyAssignments);
                } else {
                    if (want.test("bootVolumes")) {
                        out.put("bootVolumes", bootVolumes);
                    }

                    if (want.test("blockVolumes")) {
                        out.put("blockVolumes", blockVolumes);
                    }

                    if (want.test("bootVolumeBackups")) {
                        out.put("bootVolumeBackups", bootBackups);
                    }

                    if (want.test("blockVolumeBackups")) {
                        out.put("blockVolumeBackups", blockBackups);
                    }

                    if (want.test("bootVolumeReplicas")) {
                        out.put("bootVolumeReplicas", bootReplicas);
                    }

                    if (want.test("blockVolumeReplicas")) {
                        out.put("blockVolumeReplicas", blockReplicas);
                    }

                    if (want.test("volumeGroups")) {
                        out.put("volumeGroups", volumeGroups);
                    }

                    if (want.test("volumeGroupBackups")) {
                        out.put("volumeGroupBackups", volumeGroupBackups);
                    }

                    if (want.test("volumeGroupReplicas")) {
                        out.put("volumeGroupReplicas", volumeGroupReplicas);
                    }

                    if (want.test("volumeBackupPolicies")) {
                        out.put("volumeBackupPolicies", backupPolicies);
                    }

                    if (want.test("volumeBackupPolicyAssignments")) {
                        out.put("volumeBackupPolicyAssignments", backupPolicyAssignments);
                    }
                }

                var46 = out;
            }

            return (Map<String, Object>)var46;
        } catch (OciException var39) {
            throw var39;
        } catch (Exception var40) {
            throw new OciException("加载块存储数据失败: " + var40.getMessage());
        }
    }

    public Map<String, Object> objectAggregate(String userId, String region, String compartmentIdOpt) {
        OciUser ociUser = this.requireUser(userId);

        try {
            Object var19;
            try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                List<Compartment> compartments = this.resolveCompartments(client, compartmentIdOpt);
                String namespace = client.getObjectStorageClient().getNamespace(GetNamespaceRequest.builder().build()).getValue();
                List<Map<String, Object>> buckets = new ArrayList<>();
                List<Map<String, Object>> privateEndpoints = new ArrayList<>();

                for (Compartment compartment : compartments) {
                    String cid = compartment.getId();
                    String cname = compartment.getName();
                    this.listBuckets(client, namespace, region, cid, cname, buckets);
                    this.listPrivateEndpoints(client, namespace, region, cid, cname, privateEndpoints);
                }

                Map<String, Object> out = new LinkedHashMap<>();
                out.put("region", region);
                out.put("namespace", namespace);
                out.put("buckets", buckets);
                out.put("privateEndpoints", privateEndpoints);
                var19 = out;
            }

            return (Map<String, Object>)var19;
        } catch (OciException var16) {
            throw var16;
        } catch (Exception var17) {
            throw new OciException("加载对象存储数据失败: " + var17.getMessage());
        }
    }

    public void deleteResource(String userId, String region, String resourceType, String resourceId, String namespace, String bucketName) {
        if (resourceType != null && !resourceType.isBlank()) {
            if ("BUCKET".equals(resourceType) || resourceId != null && !resourceId.isBlank()) {
                OciUser ociUser = this.requireUser(userId);

                try {
                    try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                        switch (resourceType) {
                            case "BOOT_VOLUME":
                                client.getBlockstorageClient().deleteBootVolume(DeleteBootVolumeRequest.builder().bootVolumeId(resourceId).build());
                                break;
                            case "BLOCK_VOLUME":
                                client.getBlockstorageClient().deleteVolume(DeleteVolumeRequest.builder().volumeId(resourceId).build());
                                break;
                            case "BOOT_VOLUME_BACKUP":
                                client.getBlockstorageClient()
                                    .deleteBootVolumeBackup(DeleteBootVolumeBackupRequest.builder().bootVolumeBackupId(resourceId).build());
                                break;
                            case "BLOCK_VOLUME_BACKUP":
                                client.getBlockstorageClient().deleteVolumeBackup(DeleteVolumeBackupRequest.builder().volumeBackupId(resourceId).build());
                                break;
                            case "BOOT_VOLUME_REPLICA":
                            case "BLOCK_VOLUME_REPLICA":
                            case "VOLUME_GROUP_REPLICA":
                                throw new OciException("当前 OCI Java SDK 已不再暴露副本删除接口，请在 OCI 控制台删除副本");
                            case "VOLUME_GROUP":
                                client.getBlockstorageClient().deleteVolumeGroup(DeleteVolumeGroupRequest.builder().volumeGroupId(resourceId).build());
                                break;
                            case "VOLUME_GROUP_BACKUP":
                                client.getBlockstorageClient()
                                    .deleteVolumeGroupBackup(DeleteVolumeGroupBackupRequest.builder().volumeGroupBackupId(resourceId).build());
                                break;
                            case "BUCKET":
                                if (namespace != null && !namespace.isBlank() && bucketName != null && !bucketName.isBlank()) {
                                    this.assertBucketEmpty(client, namespace, bucketName);
                                    client.getObjectStorageClient()
                                        .deleteBucket(DeleteBucketRequest.builder().namespaceName(namespace).bucketName(bucketName).build());
                                    break;
                                }

                                throw new OciException("删除桶需要 namespace 与 bucketName");
                            case "VOLUME_BACKUP_POLICY":
                                client.getBlockstorageClient().deleteVolumeBackupPolicy(DeleteVolumeBackupPolicyRequest.builder().policyId(resourceId).build());
                                break;
                            case "VOLUME_BACKUP_POLICY_ASSIGNMENT":
                                client.getBlockstorageClient()
                                    .deleteVolumeBackupPolicyAssignment(
                                        DeleteVolumeBackupPolicyAssignmentRequest.builder().policyAssignmentId(resourceId).build()
                                    );
                                break;
                            case "PRIVATE_ENDPOINT":
                                if (namespace != null && !namespace.isBlank()) {
                                    client.getObjectStorageClient()
                                        .deletePrivateEndpoint(DeletePrivateEndpointRequest.builder().namespaceName(namespace).peName(resourceId).build());
                                    break;
                                }

                                throw new OciException("删除专用端点需要 namespace 与端点名称（resourceId 传 peName）");
                            default:
                                throw new OciException("未知资源类型: " + resourceType);
                        }
                    }
                } catch (OciException var13) {
                    throw var13;
                } catch (BmcException var14) {
                    throw new OciException("删除失败: " + var14.getMessage());
                } catch (Exception var15) {
                    throw new OciException("删除失败: " + var15.getMessage());
                }
            } else {
                throw new OciException("resourceId 不能为空");
            }
        } else {
            throw new OciException("resourceType 不能为空");
        }
    }

    public void putBucketPolicy(String userId, String region, String namespace, String bucketName, String policy) {
        if (namespace == null || namespace.isBlank() || bucketName == null || bucketName.isBlank()) {
            throw new OciException("namespace / bucketName 不能为空");
        } else if (policy == null) {
            throw new OciException("policy 不能为空");
        } else {
            OciUser ociUser = this.requireUser(userId);

            try {
                try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                    HttpClient http = this.ociProxyConfigService.newOutboundHttpClient();
                    ObjectStorageBucketPolicyHttp.putBucketPolicy(http, client.getObjectStorageClient(), client.getProvider(), namespace, bucketName, policy);
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("保存桶策略失败: " + var13.getMessage());
            }
        }
    }

    public Object mutate(Map<String, Object> params) {
        String action = stringParam(params, "action");
        String userId = stringParam(params, "id");
        String region = stringParam(params, "region");
        OciUser ociUser = this.requireUser(userId);

        try {
            Object var7;
            try (OciClientService client = new OciClientService(this.buildDto(ociUser), region)) {
                var7 = switch (action) {
                    case "updateBootVolume" -> {
                        String bootVolumeId = stringParam(params, "bootVolumeId");
                        String displayName = stringParam(params, "displayName");
                        Long size = longParam(params, "sizeInGBs");
                        Long vpusPerGb = longParam(params, "vpusPerGB");
                        Builder b = UpdateBootVolumeDetails.builder();
                        if (!displayName.isBlank()) {
                            b.displayName(displayName);
                        }

                        if (size != null) {
                            b.sizeInGBs(size);
                        }

                        if (vpusPerGb != null) {
                            b.vpusPerGB(vpusPerGb);
                        }

                        if (displayName.isBlank() && size == null && vpusPerGb == null) {
                            throw new OciException("至少提供 displayName、sizeInGBs 或 vpusPerGB 之一");
                        }

                        yield toMap(
                            client.getBlockstorageClient()
                                .updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails(b.build()).build())
                                .getBootVolume()
                        );
                    }
                    case "updateBlockVolume" -> {
                        String volumeId = stringParam(params, "volumeId");
                        String displayName = stringParam(params, "displayName");
                        Long size = longParam(params, "sizeInGBs");
                        Long vpusPerGb = longParam(params, "vpusPerGB");
                        com.oracle.bmc.core.model.UpdateVolumeDetails.Builder b = UpdateVolumeDetails.builder();
                        if (!displayName.isBlank()) {
                            b.displayName(displayName);
                        }

                        if (size != null) {
                            b.sizeInGBs(size);
                        }

                        if (vpusPerGb != null) {
                            b.vpusPerGB(vpusPerGb);
                        }

                        if (displayName.isBlank() && size == null && vpusPerGb == null) {
                            throw new OciException("至少提供 displayName、sizeInGBs 或 vpusPerGB 之一");
                        }

                        yield toMap(
                            client.getBlockstorageClient()
                                .updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails(b.build()).build())
                                .getVolume()
                        );
                    }
                    case "updateBootVolumeReplica", "updateBlockVolumeReplica" -> throw new OciException("当前 OCI Java SDK 已不再暴露副本更新接口，请在 OCI 控制台修改副本显示名称");
                    case "updateVolumeGroup" -> {
                        String id = stringParam(params, "volumeGroupId");
                        String displayName = stringParam(params, "displayName");
                        yield toMap(
                            client.getBlockstorageClient()
                                .updateVolumeGroup(
                                    UpdateVolumeGroupRequest.builder()
                                        .volumeGroupId(id)
                                        .updateVolumeGroupDetails(UpdateVolumeGroupDetails.builder().displayName(displayName).build())
                                        .build()
                                )
                                .getVolumeGroup()
                        );
                    }
                    case "enableBlockVolumeReplication" -> {
                        String volumeId = stringParam(params, "volumeId");
                        String replicaDisplayName = stringParam(params, "replicaDisplayName");
                        String destinationAvailabilityDomain = stringParam(params, "destinationAvailabilityDomain");
                        String xrrKmsKeyId = stringParam(params, "xrrKmsKeyId");
                        com.oracle.bmc.core.model.BlockVolumeReplicaDetails.Builder replica = BlockVolumeReplicaDetails.builder()
                            .displayName(replicaDisplayName)
                            .availabilityDomain(destinationAvailabilityDomain);
                        if (!xrrKmsKeyId.isBlank()) {
                            replica.xrrKmsKeyId(xrrKmsKeyId);
                        }

                        UpdateVolumeDetails details = UpdateVolumeDetails.builder().blockVolumeReplicas(List.of(replica.build())).build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails(details).build())
                                .getVolume()
                        );
                    }
                    case "enableBootVolumeReplication" -> {
                        String bootVolumeId = stringParam(params, "bootVolumeId");
                        String replicaDisplayName = stringParam(params, "replicaDisplayName");
                        String destinationAvailabilityDomain = stringParam(params, "destinationAvailabilityDomain");
                        String xrrKmsKeyId = stringParam(params, "xrrKmsKeyId");
                        com.oracle.bmc.core.model.BootVolumeReplicaDetails.Builder replica = BootVolumeReplicaDetails.builder()
                            .displayName(replicaDisplayName)
                            .availabilityDomain(destinationAvailabilityDomain);
                        if (!xrrKmsKeyId.isBlank()) {
                            replica.xrrKmsKeyId(xrrKmsKeyId);
                        }

                        UpdateBootVolumeDetails details = UpdateBootVolumeDetails.builder().bootVolumeReplicas(List.of(replica.build())).build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails(details).build())
                                .getBootVolume()
                        );
                    }
                    case "activateBlockReplicaAsVolume" -> {
                        String replicaId = stringParam(params, "replicaId");
                        String compartmentId = stringParam(params, "compartmentId");
                        String ad = stringParam(params, "availabilityDomain");
                        String displayName = stringParam(params, "displayName");
                        Long sizeInGBs = longParam(params, "sizeInGBs");
                        VolumeSourceFromBlockVolumeReplicaDetails src = VolumeSourceFromBlockVolumeReplicaDetails.builder().id(replicaId).build();
                        com.oracle.bmc.core.model.CreateVolumeDetails.Builder detailsB = CreateVolumeDetails.builder()
                            .availabilityDomain(ad)
                            .compartmentId(compartmentId)
                            .displayName(displayName)
                            .sourceDetails(src);
                        if (sizeInGBs != null) {
                            detailsB.sizeInGBs(sizeInGBs);
                        }

                        yield toMap(
                            client.getBlockstorageClient()
                                .createVolume(CreateVolumeRequest.builder().createVolumeDetails(detailsB.build()).build())
                                .getVolume()
                        );
                    }
                    case "activateBootReplicaAsBootVolume" -> {
                        String replicaId = stringParam(params, "replicaId");
                        String compartmentId = stringParam(params, "compartmentId");
                        String ad = stringParam(params, "availabilityDomain");
                        String displayName = stringParam(params, "displayName");
                        BootVolumeSourceFromBootVolumeReplicaDetails src = BootVolumeSourceFromBootVolumeReplicaDetails.builder().id(replicaId).build();
                        CreateBootVolumeDetails details = CreateBootVolumeDetails.builder()
                            .availabilityDomain(ad)
                            .compartmentId(compartmentId)
                            .displayName(displayName)
                            .sourceDetails(src)
                            .build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .createBootVolume(CreateBootVolumeRequest.builder().createBootVolumeDetails(details).build())
                                .getBootVolume()
                        );
                    }
                    case "createBucket" -> {
                        String compartmentId = stringParam(params, "compartmentId");
                        String name = stringParam(params, "name");
                        String accessType = stringParam(params, "publicAccessType");
                        com.oracle.bmc.objectstorage.model.CreateBucketDetails.Builder details = CreateBucketDetails.builder()
                            .compartmentId(compartmentId)
                            .name(name);
                        if (accessType != null && !accessType.isBlank()) {
                            details.publicAccessType(PublicAccessType.create(accessType));
                        }

                        yield toMap(
                            client.getObjectStorageClient()
                                .createBucket(
                                    CreateBucketRequest.builder().namespaceName(stringParam(params, "namespace")).createBucketDetails(details.build()).build()
                                )
                                .getBucket()
                        );
                    }
                    case "updateBucket" -> {
                        String namespace = stringParam(params, "namespace");
                        String bucketName = stringParam(params, "bucketName");
                        com.oracle.bmc.objectstorage.model.UpdateBucketDetails.Builder ub = UpdateBucketDetails.builder();
                        if (params.containsKey("namespace")) {
                        }

                        if (params.get("versioning") != null) {
                            String v = String.valueOf(params.get("versioning"));
                            ub.versioning(Versioning.create(v));
                        }

                        if (params.get("freeformTags") instanceof Map<?, ?> m) {
                            Map<String, String> tags = new LinkedHashMap<>();

                            for (Entry<?, ?> e : m.entrySet()) {
                                tags.put(String.valueOf(e.getKey()), e.getValue() == null ? "" : String.valueOf(e.getValue()));
                            }

                            ub.freeformTags(tags);
                        }

                        if (params.containsKey("publicAccessType") && params.get("publicAccessType") != null) {
                            String pa = String.valueOf(params.get("publicAccessType"));
                            ub.publicAccessType(com.oracle.bmc.objectstorage.model.UpdateBucketDetails.PublicAccessType.create(pa));
                        }

                        yield toMap(
                            client.getObjectStorageClient()
                                .updateBucket(
                                    UpdateBucketRequest.builder().namespaceName(namespace).bucketName(bucketName).updateBucketDetails(ub.build()).build()
                                )
                                .getBucket()
                        );
                    }
                    case "createPrivateEndpoint" -> {
                        String namespace = stringParam(params, "namespace");
                        String compartmentId = stringParam(params, "compartmentId");
                        String subnetId = stringParam(params, "subnetId");
                        String displayName = stringParam(params, "displayName");
                        CreatePrivateEndpointDetails det = CreatePrivateEndpointDetails.builder()
                            .compartmentId(compartmentId)
                            .subnetId(subnetId)
                            .name(displayName)
                            .build();
                        CreatePrivateEndpointResponse resp = client.getObjectStorageClient()
                            .createPrivateEndpoint(CreatePrivateEndpointRequest.builder().namespaceName(namespace).createPrivateEndpointDetails(det).build());
                        Map<String, Object> peOut = new LinkedHashMap<>();
                        peOut.put("opcWorkRequestId", resp.getOpcWorkRequestId());
                        peOut.put("namespace", namespace);
                        peOut.put("name", displayName);
                        peOut.put("compartmentId", compartmentId);
                        peOut.put("subnetId", subnetId);
                        yield peOut;
                    }
                    case "createVolumeBackupPolicyAssignment" -> {
                        String policyId = stringParam(params, "policyId");
                        String assetId = stringParam(params, "assetId");
                        CreateVolumeBackupPolicyAssignmentDetails det = CreateVolumeBackupPolicyAssignmentDetails.builder()
                            .policyId(policyId)
                            .assetId(assetId)
                            .build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .createVolumeBackupPolicyAssignment(
                                    CreateVolumeBackupPolicyAssignmentRequest.builder().createVolumeBackupPolicyAssignmentDetails(det).build()
                                )
                                .getVolumeBackupPolicyAssignment()
                        );
                    }
                    case "createVolumeBackupPolicy" -> {
                        String compartmentId = stringParam(params, "compartmentId");
                        String displayName = stringParam(params, "displayName");
                        List<Map<String, Object>> schedules = (List<Map<String, Object>>)params.get("schedules");
                        List<VolumeBackupSchedule> built = parseVolumeBackupSchedules(schedules);
                        if (built.isEmpty()) {
                            built.add(
                                VolumeBackupSchedule.builder()
                                    .backupType(BackupType.Full)
                                    .period(Period.OneDay)
                                    .offsetType(OffsetType.Structured)
                                    .hourOfDay(2)
                                    .retentionSeconds(604800)
                                    .build()
                            );
                        }

                        CreateVolumeBackupPolicyDetails det = CreateVolumeBackupPolicyDetails.builder()
                            .compartmentId(compartmentId)
                            .displayName(displayName)
                            .schedules(built)
                            .build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .createVolumeBackupPolicy(CreateVolumeBackupPolicyRequest.builder().createVolumeBackupPolicyDetails(det).build())
                                .getVolumeBackupPolicy()
                        );
                    }
                    case "updateVolumeBackupPolicy" -> {
                        String policyId = stringParam(params, "policyId");
                        String displayName = stringParam(params, "displayName");
                        List<Map<String, Object>> schedules = (List<Map<String, Object>>)params.get("schedules");
                        List<VolumeBackupSchedule> built = parseVolumeBackupSchedules(schedules);
                        com.oracle.bmc.core.model.UpdateVolumeBackupPolicyDetails.Builder ub = UpdateVolumeBackupPolicyDetails.builder()
                            .displayName(displayName);
                        if (!built.isEmpty()) {
                            ub.schedules(built);
                        }

                        yield toMap(
                            client.getBlockstorageClient()
                                .updateVolumeBackupPolicy(
                                    UpdateVolumeBackupPolicyRequest.builder().policyId(policyId).updateVolumeBackupPolicyDetails(ub.build()).build()
                                )
                                .getVolumeBackupPolicy()
                        );
                    }
                    case "createVolumeGroup" -> {
                        String compartmentId = stringParam(params, "compartmentId");
                        String availabilityDomain = stringParam(params, "availabilityDomain");
                        String displayName = stringParam(params, "displayName");
                        List<String> volumeIds = (List<String>)params.get("volumeIds");
                        CreateVolumeGroupDetails det = CreateVolumeGroupDetails.builder()
                            .compartmentId(compartmentId)
                            .availabilityDomain(availabilityDomain)
                            .displayName(displayName)
                            .sourceDetails(VolumeGroupSourceFromVolumesDetails.builder().volumeIds(volumeIds == null ? List.of() : volumeIds).build())
                            .build();
                        yield toMap(
                            client.getBlockstorageClient()
                                .createVolumeGroup(CreateVolumeGroupRequest.builder().createVolumeGroupDetails(det).build())
                                .getVolumeGroup()
                        );
                    }
                    default -> throw new OciException("未知操作: " + action);
                };
            }

            return var7;
        } catch (OciException var18) {
            throw var18;
        } catch (BmcException var19) {
            throw new OciException("操作失败: " + var19.getMessage());
        } catch (Exception var20) {
            throw new OciException("操作失败: " + var20.getMessage());
        }
    }

    private void assertBucketEmpty(OciClientService client, String namespace, String bucketName) {
        ListObjectsResponse resp = client.getObjectStorageClient()
            .listObjects(ListObjectsRequest.builder().namespaceName(namespace).bucketName(bucketName).limit(1).build());
        if (resp.getListObjects() != null && resp.getListObjects().getObjects() != null && !resp.getListObjects().getObjects().isEmpty()) {
            throw new OciException("桶非空，拒绝删除。请先清空对象后再删除。");
        }
    }

    private List<String> listAvailabilityDomainNames(OciClientService client) {
        try {
            List<String> names = client.getAvailabilityDomains().stream().map(ad -> ad.getName()).filter(n -> n != null && !n.isBlank()).distinct().toList();
            if (names.isEmpty()) {
                throw new OciException("当前区域未返回任何可用域（Availability Domain），无法列举块存储资源");
            } else {
                return names;
            }
        } catch (OciException var3) {
            throw var3;
        } catch (Exception var4) {
            throw new OciException("获取可用域列表失败: " + var4.getMessage());
        }
    }

    private List<Compartment> resolveCompartments(OciClientService client, String compartmentIdOpt) {
        if (compartmentIdOpt != null && !compartmentIdOpt.isBlank()) {
            try {
                Compartment c = client.getIdentityClient()
                    .getCompartment(GetCompartmentRequest.builder().compartmentId(compartmentIdOpt).build())
                    .getCompartment();
                return List.of(c);
            } catch (Exception var4) {
                throw new OciException("读取区间失败: " + var4.getMessage());
            }
        } else {
            return client.listAllCompartments();
        }
    }

    private Map<String, String> loadInstanceNames(OciClientService client, String compartmentId) {
        Map<String, String> names = new HashMap<>();

        for (LifecycleState state : List.of(LifecycleState.Running, LifecycleState.Stopped, LifecycleState.Starting, LifecycleState.Stopping)) {
            String page = null;

            do {
                ListInstancesResponse resp = client.getComputeClient()
                    .listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).lifecycleState(state).page(page).build());

                for (Instance i : resp.getItems()) {
                    names.put(i.getId(), i.getDisplayName());
                }

                page = resp.getOpcNextPage();
            } while (page == null);
        }

        return names;
    }

    private Map<String, List<Map<String, Object>>> loadBootVolumeAttachments(
        OciClientService client, String compartmentId, Map<String, String> instanceNames, List<String> availabilityDomains
    ) {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();

        for (String ad : availabilityDomains) {
            String page = null;

            do {
                ListBootVolumeAttachmentsResponse resp = client.getComputeClient()
                    .listBootVolumeAttachments(
                        ListBootVolumeAttachmentsRequest.builder().compartmentId(compartmentId).availabilityDomain(ad).page(page).build()
                    );

                for (BootVolumeAttachment a : resp.getItems()) {
                    if (a.getLifecycleState() != com.oracle.bmc.core.model.BootVolumeAttachment.LifecycleState.Detached) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("instanceId", a.getInstanceId());
                        row.put("instanceName", instanceNames.getOrDefault(a.getInstanceId(), ""));
                        row.put("lifecycleState", a.getLifecycleState() != null ? a.getLifecycleState().getValue() : null);
                        map.computeIfAbsent(a.getBootVolumeId(), k -> new ArrayList<>()).add(row);
                    }
                }

                page = resp.getOpcNextPage();
            } while (page == null);
        }

        return map;
    }

    private Map<String, List<Map<String, Object>>> loadVolumeAttachments(
        OciClientService client, String compartmentId, Map<String, String> instanceNames, List<String> availabilityDomains
    ) {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();

        for (String ad : availabilityDomains) {
            String page = null;

            do {
                ListVolumeAttachmentsResponse resp = client.getComputeClient()
                    .listVolumeAttachments(ListVolumeAttachmentsRequest.builder().compartmentId(compartmentId).availabilityDomain(ad).page(page).build());

                for (VolumeAttachment a : resp.getItems()) {
                    if (a.getLifecycleState() != com.oracle.bmc.core.model.VolumeAttachment.LifecycleState.Detached) {
                        String volId = a.getVolumeId();
                        if (volId != null) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            row.put("instanceId", a.getInstanceId());
                            row.put("instanceName", instanceNames.getOrDefault(a.getInstanceId(), ""));
                            row.put("lifecycleState", a.getLifecycleState() != null ? a.getLifecycleState().getValue() : null);
                            map.computeIfAbsent(volId, k -> new ArrayList<>()).add(row);
                        }
                    }
                }

                page = resp.getOpcNextPage();
            } while (page == null);
        }

        return map;
    }

    private void listBootVolumes(
        OciClientService client,
        String region,
        String cid,
        String cname,
        Map<String, List<Map<String, Object>>> bootAttach,
        List<Map<String, Object>> out,
        List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListBootVolumesResponse resp = client.getBlockstorageClient()
                        .listBootVolumes(ListBootVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (BootVolume v : resp.getItems()) {
                        if (v.getLifecycleState() != com.oracle.bmc.core.model.BootVolume.LifecycleState.Terminated && seenIds.add(v.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                v.getId(),
                                v.getDisplayName(),
                                v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null,
                                v.getTimeCreated() != null ? v.getTimeCreated().toString() : null
                            );
                            m.put("sizeInGBs", v.getSizeInGBs());
                            m.put("vpusPerGB", v.getVpusPerGB());
                            m.put("availabilityDomain", v.getAvailabilityDomain());
                            m.put("imageId", v.getImageId());
                            m.put("attachments", bootAttach.getOrDefault(v.getId(), List.of()));
                            m.put("attachmentSummary", summarizeAttachments(bootAttach.get(v.getId())));
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var16) {
            log.debug("listBootVolumes {}: {}", cid, var16.getMessage());
        }
    }

    private void listBlockVolumes(
        OciClientService client,
        String region,
        String cid,
        String cname,
        Map<String, List<Map<String, Object>>> volAttach,
        List<Map<String, Object>> out,
        List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListVolumesResponse resp = client.getBlockstorageClient()
                        .listVolumes(ListVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (Volume v : resp.getItems()) {
                        if (v.getLifecycleState() != com.oracle.bmc.core.model.Volume.LifecycleState.Terminated && seenIds.add(v.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                v.getId(),
                                v.getDisplayName(),
                                v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null,
                                v.getTimeCreated() != null ? v.getTimeCreated().toString() : null
                            );
                            m.put("sizeInGBs", v.getSizeInGBs());
                            m.put("vpusPerGB", v.getVpusPerGB());
                            m.put("availabilityDomain", v.getAvailabilityDomain());
                            m.put("isHydrated", v.getIsHydrated());
                            m.put("attachments", volAttach.getOrDefault(v.getId(), List.of()));
                            m.put("attachmentSummary", summarizeAttachments(volAttach.get(v.getId())));
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var16) {
            log.debug("listVolumes {}: {}", cid, var16.getMessage());
        }
    }

    private void listBootBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListBootVolumeBackupsResponse resp = client.getBlockstorageClient()
                    .listBootVolumeBackups(ListBootVolumeBackupsRequest.builder().compartmentId(cid).page(page).build());

                for (BootVolumeBackup b : resp.getItems()) {
                    if (b.getLifecycleState() != com.oracle.bmc.core.model.BootVolumeBackup.LifecycleState.Terminated) {
                        Map<String, Object> m = baseRow(
                            region,
                            cid,
                            cname,
                            b.getId(),
                            b.getDisplayName(),
                            b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null,
                            b.getTimeCreated() != null ? b.getTimeCreated().toString() : null
                        );
                        m.put("sizeInGBs", b.getSizeInGBs());
                        m.put("uniqueSizeInGBs", b.getUniqueSizeInGBs());
                        m.put("sourceBootVolumeId", b.getBootVolumeId());
                        m.put("sourceType", b.getSourceType() != null ? b.getSourceType().getValue() : null);
                        out.add(m);
                    }
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var11) {
            log.debug("listBootVolumeBackups {}: {}", cid, var11.getMessage());
        }
    }

    private void listBlockBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListVolumeBackupsResponse resp = client.getBlockstorageClient()
                    .listVolumeBackups(ListVolumeBackupsRequest.builder().compartmentId(cid).page(page).build());

                for (VolumeBackup b : resp.getItems()) {
                    if (b.getLifecycleState() != com.oracle.bmc.core.model.VolumeBackup.LifecycleState.Terminated) {
                        Map<String, Object> m = baseRow(
                            region,
                            cid,
                            cname,
                            b.getId(),
                            b.getDisplayName(),
                            b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null,
                            b.getTimeCreated() != null ? b.getTimeCreated().toString() : null
                        );
                        m.put("sizeInGBs", b.getSizeInGBs());
                        m.put("uniqueSizeInGBs", b.getUniqueSizeInGBs());
                        m.put("sourceVolumeId", b.getVolumeId());
                        m.put("sourceType", b.getSourceType() != null ? b.getSourceType().getValue() : null);
                        out.add(m);
                    }
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var11) {
            log.debug("listVolumeBackups {}: {}", cid, var11.getMessage());
        }
    }

    private void listBootReplicas(
        OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListBootVolumeReplicasResponse resp = client.getBlockstorageClient()
                        .listBootVolumeReplicas(ListBootVolumeReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (BootVolumeReplica r : resp.getItems()) {
                        if (r.getLifecycleState() != com.oracle.bmc.core.model.BootVolumeReplica.LifecycleState.Terminated && seenIds.add(r.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                r.getId(),
                                r.getDisplayName(),
                                r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null,
                                r.getTimeCreated() != null ? r.getTimeCreated().toString() : null
                            );
                            m.put("sizeInGBs", r.getSizeInGBs());
                            m.put("availabilityDomain", r.getAvailabilityDomain());
                            m.put("sourceBootVolumeId", r.getBootVolumeId());
                            m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var15) {
            log.debug("listBootVolumeReplicas {}: {}", cid, var15.getMessage());
        }
    }

    private void listBlockReplicas(
        OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListBlockVolumeReplicasResponse resp = client.getBlockstorageClient()
                        .listBlockVolumeReplicas(ListBlockVolumeReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (BlockVolumeReplica r : resp.getItems()) {
                        if (r.getLifecycleState() != com.oracle.bmc.core.model.BlockVolumeReplica.LifecycleState.Terminated && seenIds.add(r.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                r.getId(),
                                r.getDisplayName(),
                                r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null,
                                r.getTimeCreated() != null ? r.getTimeCreated().toString() : null
                            );
                            m.put("sizeInGBs", r.getSizeInGBs());
                            m.put("availabilityDomain", r.getAvailabilityDomain());
                            m.put("sourceVolumeId", r.getBlockVolumeId());
                            m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                            m.put("volumeGroupReplicaId", r.getVolumeGroupReplicaId());
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var15) {
            log.debug("listBlockVolumeReplicas {}: {}", cid, var15.getMessage());
        }
    }

    private void listVolumeGroups(
        OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListVolumeGroupsResponse resp = client.getBlockstorageClient()
                        .listVolumeGroups(ListVolumeGroupsRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (VolumeGroup g : resp.getItems()) {
                        if (g.getLifecycleState() != com.oracle.bmc.core.model.VolumeGroup.LifecycleState.Terminated && seenIds.add(g.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                g.getId(),
                                g.getDisplayName(),
                                g.getLifecycleState() != null ? g.getLifecycleState().getValue() : null,
                                g.getTimeCreated() != null ? g.getTimeCreated().toString() : null
                            );
                            m.put("availabilityDomain", g.getAvailabilityDomain());
                            m.put("volumeIds", g.getVolumeIds());
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var15) {
            log.debug("listVolumeGroups {}: {}", cid, var15.getMessage());
        }
    }

    private void listVolumeGroupBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListVolumeGroupBackupsResponse resp = client.getBlockstorageClient()
                    .listVolumeGroupBackups(ListVolumeGroupBackupsRequest.builder().compartmentId(cid).page(page).build());

                for (VolumeGroupBackup b : resp.getItems()) {
                    if (b.getLifecycleState() != com.oracle.bmc.core.model.VolumeGroupBackup.LifecycleState.Terminated) {
                        Map<String, Object> m = baseRow(
                            region,
                            cid,
                            cname,
                            b.getId(),
                            b.getDisplayName(),
                            b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null,
                            b.getTimeCreated() != null ? b.getTimeCreated().toString() : null
                        );
                        m.put("sizeInGBs", b.getSizeInGBs());
                        m.put("uniqueSizeInGBs", b.getUniqueSizeInGbs());
                        m.put("volumeGroupId", b.getVolumeGroupId());
                        out.add(m);
                    }
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var11) {
            log.debug("listVolumeGroupBackups {}: {}", cid, var11.getMessage());
        }
    }

    private void listVolumeGroupReplicas(
        OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains
    ) {
        try {
            Set<String> seenIds = new HashSet<>();

            for (String ad : availabilityDomains) {
                String page = null;

                do {
                    ListVolumeGroupReplicasResponse resp = client.getBlockstorageClient()
                        .listVolumeGroupReplicas(ListVolumeGroupReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                    for (VolumeGroupReplica r : resp.getItems()) {
                        if (r.getLifecycleState() != com.oracle.bmc.core.model.VolumeGroupReplica.LifecycleState.Terminated && seenIds.add(r.getId())) {
                            Map<String, Object> m = baseRow(
                                region,
                                cid,
                                cname,
                                r.getId(),
                                r.getDisplayName(),
                                r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null,
                                r.getTimeCreated() != null ? r.getTimeCreated().toString() : null
                            );
                            m.put("availabilityDomain", r.getAvailabilityDomain());
                            m.put("sourceVolumeGroupId", r.getVolumeGroupId());
                            m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                            out.add(m);
                        }
                    }

                    page = resp.getOpcNextPage();
                } while (page == null);
            }
        } catch (Exception var15) {
            log.debug("listVolumeGroupReplicas {}: {}", cid, var15.getMessage());
        }
    }

    private void listBackupPolicies(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListVolumeBackupPoliciesResponse resp = client.getBlockstorageClient()
                    .listVolumeBackupPolicies(ListVolumeBackupPoliciesRequest.builder().compartmentId(cid).page(page).build());

                for (VolumeBackupPolicy p : resp.getItems()) {
                    Map<String, Object> m = baseRow(region, cid, cname, p.getId(), p.getDisplayName(), null, null);
                    m.put("schedules", p.getSchedules());
                    out.add(m);
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var11) {
            log.debug("listVolumeBackupPolicies {}: {}", cid, var11.getMessage());
        }
    }

    private void collectVolumeBackupPolicyAssignments(
        OciClientService client, String region, String cid, String cname, List<String> assetIds, List<Map<String, Object>> out
    ) {
        for (String assetId : assetIds) {
            if (assetId != null && !assetId.isBlank()) {
                try {
                    String page = null;

                    while (true) {
                        GetVolumeBackupPolicyAssetAssignmentResponse resp = client.getBlockstorageClient()
                            .getVolumeBackupPolicyAssetAssignment(
                                GetVolumeBackupPolicyAssetAssignmentRequest.builder().assetId(assetId).limit(100).page(page).build()
                            );

                        for (VolumeBackupPolicyAssignment a : resp.getItems()) {
                            Map<String, Object> m = baseRow(
                                region, cid, cname, a.getId(), null, null, a.getTimeCreated() != null ? a.getTimeCreated().toString() : null
                            );
                            m.put("policyId", a.getPolicyId());
                            m.put("assetId", a.getAssetId());
                            out.add(m);
                        }

                        page = resp.getOpcNextPage();
                        if (page == null) {
                            break;
                        }
                    }
                } catch (Exception var14) {
                    log.debug("getVolumeBackupPolicyAssetAssignment {}: {}", assetId, var14.getMessage());
                }
            }
        }
    }

    private static List<VolumeBackupSchedule> parseVolumeBackupSchedules(List<Map<String, Object>> schedules) {
        List<VolumeBackupSchedule> built = new ArrayList<>();
        if (schedules == null) {
            return built;
        } else {
            for (Map<String, Object> s : schedules) {
                com.oracle.bmc.core.model.VolumeBackupSchedule.Builder b = VolumeBackupSchedule.builder();
                String bt = stringParam(s, "backupType");
                if (!bt.isBlank()) {
                    b.backupType(BackupType.create(bt));
                }

                String period = stringParam(s, "period");
                if (!period.isBlank()) {
                    b.period(Period.create(period));
                }

                String ot = stringParam(s, "offsetType");
                if (!ot.isBlank()) {
                    b.offsetType(OffsetType.create(ot));
                }

                Integer os = intParam(s, "offsetSeconds");
                if (os != null) {
                    b.offsetSeconds(os);
                }

                Integer hod = intParam(s, "hourOfDay");
                if (hod != null) {
                    b.hourOfDay(hod);
                }

                String dow = stringParam(s, "dayOfWeek");
                if (!dow.isBlank()) {
                    b.dayOfWeek(DayOfWeek.create(dow));
                }

                Integer dom = intParam(s, "dayOfMonth");
                if (dom != null) {
                    b.dayOfMonth(dom);
                }

                String month = stringParam(s, "month");
                if (!month.isBlank()) {
                    b.month(Month.create(month));
                }

                Integer ret = intParam(s, "retentionSeconds");
                if (ret != null) {
                    b.retentionSeconds(ret);
                }

                built.add(b.build());
            }

            return built;
        }
    }

    private void listBuckets(OciClientService client, String namespace, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListBucketsResponse resp = client.getObjectStorageClient()
                    .listBuckets(ListBucketsRequest.builder().namespaceName(namespace).compartmentId(cid).page(page).build());

                for (BucketSummary s : resp.getItems()) {
                    Map<String, Object> m = baseRow(region, cid, cname, null, s.getName(), null, null);
                    m.put("namespace", namespace);
                    m.put("name", s.getName());
                    m.put("publicAccessType", null);
                    m.put("storageTier", null);
                    m.put("createdBy", s.getCreatedBy());
                    m.put("timeCreated", s.getTimeCreated() != null ? s.getTimeCreated().toString() : null);
                    out.add(m);
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var12) {
            log.debug("listBuckets {}: {}", cid, var12.getMessage());
        }
    }

    private void listPrivateEndpoints(OciClientService client, String namespace, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            String page = null;

            do {
                ListPrivateEndpointsResponse resp = client.getObjectStorageClient()
                    .listPrivateEndpoints(ListPrivateEndpointsRequest.builder().compartmentId(cid).namespaceName(namespace).page(page).build());

                for (PrivateEndpointSummary pe : resp.getItems()) {
                    String peName = pe.getName();
                    Map<String, Object> m = baseRow(
                        region,
                        cid,
                        cname,
                        peName,
                        peName,
                        pe.getLifecycleState() != null ? pe.getLifecycleState().getValue() : null,
                        pe.getTimeCreated() != null ? pe.getTimeCreated().toString() : null
                    );
                    m.put("subnetId", null);
                    m.put("namespace", pe.getNamespace() != null ? pe.getNamespace() : namespace);
                    out.add(m);
                }

                page = resp.getOpcNextPage();
            } while (page != null);
        } catch (Exception var13) {
            log.debug("listPrivateEndpoints {}: {}", cid, var13.getMessage());
        }
    }

    private static Map<String, Object> baseRow(
        String region, String compartmentId, String compartmentName, String id, String displayName, String lifecycleState, String timeCreated
    ) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("region", region);
        m.put("compartmentId", compartmentId);
        m.put("compartmentName", compartmentName);
        m.put("id", id);
        m.put("displayName", displayName);
        m.put("lifecycleState", lifecycleState);
        m.put("timeCreated", timeCreated);
        return m;
    }

    private static String summarizeAttachments(List<Map<String, Object>> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            if (attachments.size() == 1) {
                Map<String, Object> a = attachments.get(0);
                String nm = Objects.toString(a.get("instanceName"), "");
                return nm.isBlank() ? "已挂载 1" : "已挂载: " + nm;
            } else {
                return "已挂载: " + attachments.size() + " 处";
            }
        } else {
            return "未挂载";
        }
    }

    private static Map<String, Object> toMap(Object model) {
        if (model == null) {
            return Map.of();
        } else {
            Map<String, Object> m = new LinkedHashMap<>();
            if (model instanceof Volume v) {
                m.put("id", v.getId());
                m.put("displayName", v.getDisplayName());
                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
            } else if (model instanceof BootVolume v) {
                m.put("id", v.getId());
                m.put("displayName", v.getDisplayName());
                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
            } else if (model instanceof BootVolumeReplica v) {
                m.put("id", v.getId());
                m.put("displayName", v.getDisplayName());
                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
            } else if (model instanceof BlockVolumeReplica v) {
                m.put("id", v.getId());
                m.put("displayName", v.getDisplayName());
                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
            } else if (model instanceof VolumeGroup v) {
                m.put("id", v.getId());
                m.put("displayName", v.getDisplayName());
                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
            } else if (model instanceof Bucket b) {
                m.put("name", b.getName());
                m.put("namespace", b.getNamespace());
            } else if (model instanceof PrivateEndpoint pe) {
                m.put("id", pe.getId());
                m.put("displayName", pe.getName());
            } else if (model instanceof VolumeBackupPolicy p) {
                m.put("id", p.getId());
                m.put("displayName", p.getDisplayName());
            } else if (model instanceof VolumeBackupPolicyAssignment a) {
                m.put("id", a.getId());
                m.put("assetId", a.getAssetId());
                m.put("policyId", a.getPolicyId());
            } else {
                m.put("result", String.valueOf(model));
            }

            return m;
        }
    }

    private static String stringParam(Map<?, ?> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : String.valueOf(v).trim();
    }

    private static Long longParam(Map<?, ?> map, String key) {
        Object v = map.get(key);
        if (v == null) {
            return null;
        } else if (v instanceof Number n) {
            return n.longValue();
        } else {
            try {
                return Long.parseLong(String.valueOf(v).trim());
            } catch (Exception var4) {
                return null;
            }
        }
    }

    private static Integer intParam(Map<?, ?> map, String key) {
        Long v = longParam(map, key);
        return v == null ? null : v.intValue();
    }

    private OciUser requireUser(String userId) {
        OciUser u = (OciUser)this.userMapper.selectById(userId);
        if (u == null) {
            throw new OciException("租户配置不存在");
        } else {
            return u;
        }
    }

    private SysUserDTO buildDto(OciUser ociUser) {
        return SysUserDTO.builder()
            .username(ociUser.getUsername())
            .ociCfg(
                SysUserDTO.OciCfg.builder()
                    .tenantId(ociUser.getOciTenantId())
                    .userId(ociUser.getOciUserId())
                    .fingerprint(ociUser.getOciFingerprint())
                    .region(ociUser.getOciRegion())
                    .privateKeyPath(ociUser.getOciKeyPath())
                    .build()
            )
            .build();
    }
}
