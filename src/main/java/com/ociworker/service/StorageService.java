/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.OciProxyConfigService
 *  com.ociworker.service.StorageService
 *  com.ociworker.util.ObjectStorageBucketPolicyHttp
 *  com.oracle.bmc.auth.BasicAuthenticationDetailsProvider
 *  com.oracle.bmc.core.model.BlockVolumeReplica
 *  com.oracle.bmc.core.model.BlockVolumeReplica$LifecycleState
 *  com.oracle.bmc.core.model.BlockVolumeReplicaDetails
 *  com.oracle.bmc.core.model.BlockVolumeReplicaDetails$Builder
 *  com.oracle.bmc.core.model.BootVolume
 *  com.oracle.bmc.core.model.BootVolume$LifecycleState
 *  com.oracle.bmc.core.model.BootVolumeAttachment
 *  com.oracle.bmc.core.model.BootVolumeAttachment$LifecycleState
 *  com.oracle.bmc.core.model.BootVolumeBackup
 *  com.oracle.bmc.core.model.BootVolumeBackup$LifecycleState
 *  com.oracle.bmc.core.model.BootVolumeReplica
 *  com.oracle.bmc.core.model.BootVolumeReplica$LifecycleState
 *  com.oracle.bmc.core.model.BootVolumeReplicaDetails
 *  com.oracle.bmc.core.model.BootVolumeSourceDetails
 *  com.oracle.bmc.core.model.BootVolumeSourceFromBootVolumeReplicaDetails
 *  com.oracle.bmc.core.model.CreateBootVolumeDetails
 *  com.oracle.bmc.core.model.CreateVolumeBackupPolicyAssignmentDetails
 *  com.oracle.bmc.core.model.CreateVolumeBackupPolicyDetails
 *  com.oracle.bmc.core.model.CreateVolumeDetails
 *  com.oracle.bmc.core.model.CreateVolumeDetails$Builder
 *  com.oracle.bmc.core.model.CreateVolumeGroupDetails
 *  com.oracle.bmc.core.model.Instance
 *  com.oracle.bmc.core.model.Instance$LifecycleState
 *  com.oracle.bmc.core.model.UpdateBootVolumeDetails
 *  com.oracle.bmc.core.model.UpdateBootVolumeDetails$Builder
 *  com.oracle.bmc.core.model.UpdateVolumeBackupPolicyDetails
 *  com.oracle.bmc.core.model.UpdateVolumeBackupPolicyDetails$Builder
 *  com.oracle.bmc.core.model.UpdateVolumeDetails
 *  com.oracle.bmc.core.model.UpdateVolumeGroupDetails
 *  com.oracle.bmc.core.model.Volume
 *  com.oracle.bmc.core.model.Volume$LifecycleState
 *  com.oracle.bmc.core.model.VolumeAttachment
 *  com.oracle.bmc.core.model.VolumeAttachment$LifecycleState
 *  com.oracle.bmc.core.model.VolumeBackup
 *  com.oracle.bmc.core.model.VolumeBackup$LifecycleState
 *  com.oracle.bmc.core.model.VolumeBackupPolicy
 *  com.oracle.bmc.core.model.VolumeBackupPolicyAssignment
 *  com.oracle.bmc.core.model.VolumeBackupSchedule
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$BackupType
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$Builder
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$DayOfWeek
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$Month
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$OffsetType
 *  com.oracle.bmc.core.model.VolumeBackupSchedule$Period
 *  com.oracle.bmc.core.model.VolumeGroup
 *  com.oracle.bmc.core.model.VolumeGroup$LifecycleState
 *  com.oracle.bmc.core.model.VolumeGroupBackup
 *  com.oracle.bmc.core.model.VolumeGroupBackup$LifecycleState
 *  com.oracle.bmc.core.model.VolumeGroupReplica
 *  com.oracle.bmc.core.model.VolumeGroupReplica$LifecycleState
 *  com.oracle.bmc.core.model.VolumeGroupSourceDetails
 *  com.oracle.bmc.core.model.VolumeGroupSourceFromVolumesDetails
 *  com.oracle.bmc.core.model.VolumeSourceDetails
 *  com.oracle.bmc.core.model.VolumeSourceFromBlockVolumeReplicaDetails
 *  com.oracle.bmc.core.requests.CreateBootVolumeRequest
 *  com.oracle.bmc.core.requests.CreateVolumeBackupPolicyAssignmentRequest
 *  com.oracle.bmc.core.requests.CreateVolumeBackupPolicyRequest
 *  com.oracle.bmc.core.requests.CreateVolumeGroupRequest
 *  com.oracle.bmc.core.requests.CreateVolumeRequest
 *  com.oracle.bmc.core.requests.DeleteBootVolumeBackupRequest
 *  com.oracle.bmc.core.requests.DeleteBootVolumeRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeBackupPolicyAssignmentRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeBackupPolicyRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeBackupRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeGroupBackupRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeGroupRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeRequest
 *  com.oracle.bmc.core.requests.GetVolumeBackupPolicyAssetAssignmentRequest
 *  com.oracle.bmc.core.requests.ListBlockVolumeReplicasRequest
 *  com.oracle.bmc.core.requests.ListBootVolumeAttachmentsRequest
 *  com.oracle.bmc.core.requests.ListBootVolumeBackupsRequest
 *  com.oracle.bmc.core.requests.ListBootVolumeReplicasRequest
 *  com.oracle.bmc.core.requests.ListBootVolumesRequest
 *  com.oracle.bmc.core.requests.ListInstancesRequest
 *  com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest
 *  com.oracle.bmc.core.requests.ListVolumeBackupPoliciesRequest
 *  com.oracle.bmc.core.requests.ListVolumeBackupsRequest
 *  com.oracle.bmc.core.requests.ListVolumeGroupBackupsRequest
 *  com.oracle.bmc.core.requests.ListVolumeGroupReplicasRequest
 *  com.oracle.bmc.core.requests.ListVolumeGroupsRequest
 *  com.oracle.bmc.core.requests.ListVolumesRequest
 *  com.oracle.bmc.core.requests.UpdateBootVolumeRequest
 *  com.oracle.bmc.core.requests.UpdateVolumeBackupPolicyRequest
 *  com.oracle.bmc.core.requests.UpdateVolumeGroupRequest
 *  com.oracle.bmc.core.requests.UpdateVolumeRequest
 *  com.oracle.bmc.core.responses.GetVolumeBackupPolicyAssetAssignmentResponse
 *  com.oracle.bmc.core.responses.ListBlockVolumeReplicasResponse
 *  com.oracle.bmc.core.responses.ListBootVolumeAttachmentsResponse
 *  com.oracle.bmc.core.responses.ListBootVolumeBackupsResponse
 *  com.oracle.bmc.core.responses.ListBootVolumeReplicasResponse
 *  com.oracle.bmc.core.responses.ListBootVolumesResponse
 *  com.oracle.bmc.core.responses.ListInstancesResponse
 *  com.oracle.bmc.core.responses.ListVolumeAttachmentsResponse
 *  com.oracle.bmc.core.responses.ListVolumeBackupPoliciesResponse
 *  com.oracle.bmc.core.responses.ListVolumeBackupsResponse
 *  com.oracle.bmc.core.responses.ListVolumeGroupBackupsResponse
 *  com.oracle.bmc.core.responses.ListVolumeGroupReplicasResponse
 *  com.oracle.bmc.core.responses.ListVolumeGroupsResponse
 *  com.oracle.bmc.core.responses.ListVolumesResponse
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.identity.requests.GetCompartmentRequest
 *  com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest
 *  com.oracle.bmc.identity.responses.ListRegionSubscriptionsResponse
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.objectstorage.ObjectStorageClient
 *  com.oracle.bmc.objectstorage.model.Bucket
 *  com.oracle.bmc.objectstorage.model.BucketSummary
 *  com.oracle.bmc.objectstorage.model.CreateBucketDetails
 *  com.oracle.bmc.objectstorage.model.CreateBucketDetails$Builder
 *  com.oracle.bmc.objectstorage.model.CreateBucketDetails$PublicAccessType
 *  com.oracle.bmc.objectstorage.model.CreatePrivateEndpointDetails
 *  com.oracle.bmc.objectstorage.model.PrivateEndpoint
 *  com.oracle.bmc.objectstorage.model.PrivateEndpointSummary
 *  com.oracle.bmc.objectstorage.model.UpdateBucketDetails
 *  com.oracle.bmc.objectstorage.model.UpdateBucketDetails$Builder
 *  com.oracle.bmc.objectstorage.model.UpdateBucketDetails$PublicAccessType
 *  com.oracle.bmc.objectstorage.model.UpdateBucketDetails$Versioning
 *  com.oracle.bmc.objectstorage.requests.CreateBucketRequest
 *  com.oracle.bmc.objectstorage.requests.CreatePrivateEndpointRequest
 *  com.oracle.bmc.objectstorage.requests.DeleteBucketRequest
 *  com.oracle.bmc.objectstorage.requests.DeletePrivateEndpointRequest
 *  com.oracle.bmc.objectstorage.requests.GetNamespaceRequest
 *  com.oracle.bmc.objectstorage.requests.ListBucketsRequest
 *  com.oracle.bmc.objectstorage.requests.ListObjectsRequest
 *  com.oracle.bmc.objectstorage.requests.ListPrivateEndpointsRequest
 *  com.oracle.bmc.objectstorage.requests.UpdateBucketRequest
 *  com.oracle.bmc.objectstorage.responses.CreatePrivateEndpointResponse
 *  com.oracle.bmc.objectstorage.responses.ListBucketsResponse
 *  com.oracle.bmc.objectstorage.responses.ListObjectsResponse
 *  com.oracle.bmc.objectstorage.responses.ListPrivateEndpointsResponse
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
import com.ociworker.service.OciProxyConfigService;
import com.ociworker.util.ObjectStorageBucketPolicyHttp;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.core.model.BlockVolumeReplica;
import com.oracle.bmc.core.model.BlockVolumeReplicaDetails;
import com.oracle.bmc.core.model.BootVolume;
import com.oracle.bmc.core.model.BootVolumeAttachment;
import com.oracle.bmc.core.model.BootVolumeBackup;
import com.oracle.bmc.core.model.BootVolumeReplica;
import com.oracle.bmc.core.model.BootVolumeReplicaDetails;
import com.oracle.bmc.core.model.BootVolumeSourceDetails;
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
import com.oracle.bmc.core.model.VolumeGroupSourceDetails;
import com.oracle.bmc.core.model.VolumeGroupSourceFromVolumesDetails;
import com.oracle.bmc.core.model.VolumeSourceDetails;
import com.oracle.bmc.core.model.VolumeSourceFromBlockVolumeReplicaDetails;
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
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import com.oracle.bmc.identity.responses.ListRegionSubscriptionsResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.Bucket;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.model.CreatePrivateEndpointDetails;
import com.oracle.bmc.objectstorage.model.PrivateEndpoint;
import com.oracle.bmc.objectstorage.model.PrivateEndpointSummary;
import com.oracle.bmc.objectstorage.model.UpdateBucketDetails;
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
import java.io.Serializable;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class StorageService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciProxyConfigService ociProxyConfigService;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<String> listSubscribedRegionIds(String userId) {
        if (userId == null) throw new OciException("\u7f3a\u5c11\u79df\u6237 id");
        if (userId.isBlank()) {
            throw new OciException("\u7f3a\u5c11\u79df\u6237 id");
        }
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = new OciClientService(this.buildDto(ociUser));){
            ListRegionSubscriptionsResponse resp = client.getIdentityClient().listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(ociUser.getOciTenantId()).build());
            List items = resp.getItems();
            if (items == null || items.isEmpty()) {
                List list = StorageService.fallbackRegionList((OciUser)ociUser);
                return list;
            }
            List out = items.stream().map(r -> r.getRegionName()).filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().sorted().toList();
            List list = out.isEmpty() ? StorageService.fallbackRegionList((OciUser)ociUser) : out;
            return list;
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn("listRegionSubscriptions failed for user {}: {}", (Object)userId, (Object)e.getMessage());
            return StorageService.fallbackRegionList((OciUser)ociUser);
        }
    }

    private static List<String> fallbackRegionList(OciUser ociUser) {
        String r = ociUser.getOciRegion();
        if (r != null && !r.isBlank()) {
            return List.of(r.trim());
        }
        return List.of();
    }

    public List<Map<String, Object>> listCompartments(String userId, String region) {
        List<Map<String, Object>> list;
        OciUser ociUser = this.requireUser(userId);
        String tenantId = ociUser.getOciTenantId();
        OciClientService client = new OciClientService(this.buildDto(ociUser), region);
        try {
            list = client.listAllCompartments().stream().map(c -> {
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
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
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (OciException e) {
                throw e;
            }
            catch (Exception e) {
                throw new OciException("\u5217\u51fa\u533a\u95f4\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return list;
    }

    public Map<String, Object> blockAggregate(String userId, String region, String compartmentIdOpt, String sections) {
        LinkedHashMap<String, Object> linkedHashMap;
        OciUser ociUser = this.requireUser(userId);
        boolean loadAllSections = sections == null || sections.isBlank();
        HashSet<String> requested = new HashSet<String>();
        if (!loadAllSections) {
            for (String part : sections.split(",")) {
                String t = part.trim().toLowerCase(Locale.ROOT);
                if (t.isEmpty()) continue;
                requested.add(t);
            }
            if (requested.contains("volumebackuppolicyassignments")) {
                requested.add("bootvolumes");
                requested.add("blockvolumes");
                requested.add("volumegroups");
                requested.add("volumebackuppolicies");
            }
        }
        Predicate<String> want = key -> {
            if (loadAllSections) {
                return true;
            }
            return requested.contains(key.toLowerCase(Locale.ROOT));
        };
        OciClientService client = new OciClientService(this.buildDto(ociUser), region);
        try {
            List compartments = this.resolveCompartments(client, compartmentIdOpt);
            List availabilityDomains = this.listAvailabilityDomainNames(client);
            LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
            out.put("region", region);
            ArrayList bootVolumes = new ArrayList();
            ArrayList blockVolumes = new ArrayList();
            ArrayList bootBackups = new ArrayList();
            ArrayList blockBackups = new ArrayList();
            ArrayList bootReplicas = new ArrayList();
            ArrayList blockReplicas = new ArrayList();
            ArrayList volumeGroups = new ArrayList();
            ArrayList volumeGroupBackups = new ArrayList();
            ArrayList volumeGroupReplicas = new ArrayList();
            ArrayList backupPolicies = new ArrayList();
            ArrayList backupPolicyAssignments = new ArrayList();
            for (Compartment compartment : compartments) {
                Object id;
                int i;
                String cid = compartment.getId();
                String cname = compartment.getName();
                Map instanceNames = Map.of();
                if (want.test("bootVolumes") || want.test("blockVolumes")) {
                    instanceNames = this.loadInstanceNames(client, cid);
                }
                Map bootAttach = new HashMap();
                if (want.test("bootVolumes")) {
                    bootAttach = this.loadBootVolumeAttachments(client, cid, instanceNames, availabilityDomains);
                }
                Map volAttach = new HashMap();
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
                if (!want.test("volumeBackupPolicyAssignments")) continue;
                ArrayList<String> policyAssetIds = new ArrayList<String>();
                for (i = bootStart; i < bootVolumes.size(); ++i) {
                    id = ((Map)bootVolumes.get(i)).get("id");
                    if (id == null) continue;
                    policyAssetIds.add(String.valueOf(id));
                }
                for (i = blockStart; i < blockVolumes.size(); ++i) {
                    id = ((Map)blockVolumes.get(i)).get("id");
                    if (id == null) continue;
                    policyAssetIds.add(String.valueOf(id));
                }
                for (i = vgStart; i < volumeGroups.size(); ++i) {
                    id = ((Map)volumeGroups.get(i)).get("id");
                    if (id == null) continue;
                    policyAssetIds.add(String.valueOf(id));
                }
                this.collectVolumeBackupPolicyAssignments(client, region, cid, cname, policyAssetIds, backupPolicyAssignments);
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
            linkedHashMap = out;
        }
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (OciException e) {
                throw e;
            }
            catch (Exception e) {
                throw new OciException("\u52a0\u8f7d\u5757\u5b58\u50a8\u6570\u636e\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return linkedHashMap;
    }

    public Map<String, Object> objectAggregate(String userId, String region, String compartmentIdOpt) {
        LinkedHashMap<String, Object> linkedHashMap;
        OciUser ociUser = this.requireUser(userId);
        OciClientService client = new OciClientService(this.buildDto(ociUser), region);
        try {
            List compartments = this.resolveCompartments(client, compartmentIdOpt);
            String namespace = client.getObjectStorageClient().getNamespace(GetNamespaceRequest.builder().build()).getValue();
            ArrayList buckets = new ArrayList();
            ArrayList privateEndpoints = new ArrayList();
            for (Compartment compartment : compartments) {
                String cid = compartment.getId();
                String cname = compartment.getName();
                this.listBuckets(client, namespace, region, cid, cname, buckets);
                this.listPrivateEndpoints(client, namespace, region, cid, cname, privateEndpoints);
            }
            LinkedHashMap<String, Object> out = new LinkedHashMap<String, Object>();
            out.put("region", region);
            out.put("namespace", namespace);
            out.put("buckets", buckets);
            out.put("privateEndpoints", privateEndpoints);
            linkedHashMap = out;
        }
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (OciException e) {
                throw e;
            }
            catch (Exception e) {
                throw new OciException("\u52a0\u8f7d\u5bf9\u8c61\u5b58\u50a8\u6570\u636e\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return linkedHashMap;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void deleteResource(String userId, String region, String resourceType, String resourceId, String namespace, String bucketName) {
        if (resourceType == null || resourceType.isBlank()) {
            throw new OciException("resourceType \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (!"BUCKET".equals(resourceType) && (resourceId == null || resourceId.isBlank())) {
            throw new OciException("resourceId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = new OciClientService(this.buildDto(ociUser), region);){
            switch (resourceType) {
                case "BOOT_VOLUME": {
                    client.getBlockstorageClient().deleteBootVolume(DeleteBootVolumeRequest.builder().bootVolumeId(resourceId).build());
                    return;
                }
                case "BLOCK_VOLUME": {
                    client.getBlockstorageClient().deleteVolume(DeleteVolumeRequest.builder().volumeId(resourceId).build());
                    return;
                }
                case "BOOT_VOLUME_BACKUP": {
                    client.getBlockstorageClient().deleteBootVolumeBackup(DeleteBootVolumeBackupRequest.builder().bootVolumeBackupId(resourceId).build());
                    return;
                }
                case "BLOCK_VOLUME_BACKUP": {
                    client.getBlockstorageClient().deleteVolumeBackup(DeleteVolumeBackupRequest.builder().volumeBackupId(resourceId).build());
                    return;
                }
                case "BOOT_VOLUME_REPLICA": 
                case "BLOCK_VOLUME_REPLICA": 
                case "VOLUME_GROUP_REPLICA": {
                    throw new OciException("\u5f53\u524d OCI Java SDK \u5df2\u4e0d\u518d\u66b4\u9732\u526f\u672c\u5220\u9664\u63a5\u53e3\uff0c\u8bf7\u5728 OCI \u63a7\u5236\u53f0\u5220\u9664\u526f\u672c");
                }
                case "VOLUME_GROUP": {
                    client.getBlockstorageClient().deleteVolumeGroup(DeleteVolumeGroupRequest.builder().volumeGroupId(resourceId).build());
                    return;
                }
                case "VOLUME_GROUP_BACKUP": {
                    client.getBlockstorageClient().deleteVolumeGroupBackup(DeleteVolumeGroupBackupRequest.builder().volumeGroupBackupId(resourceId).build());
                    return;
                }
                case "BUCKET": {
                    if (namespace == null || namespace.isBlank() || bucketName == null || bucketName.isBlank()) {
                        throw new OciException("\u5220\u9664\u6876\u9700\u8981 namespace \u4e0e bucketName");
                    }
                    this.assertBucketEmpty(client, namespace, bucketName);
                    client.getObjectStorageClient().deleteBucket(DeleteBucketRequest.builder().namespaceName(namespace).bucketName(bucketName).build());
                    return;
                }
                case "VOLUME_BACKUP_POLICY": {
                    client.getBlockstorageClient().deleteVolumeBackupPolicy(DeleteVolumeBackupPolicyRequest.builder().policyId(resourceId).build());
                    return;
                }
                case "VOLUME_BACKUP_POLICY_ASSIGNMENT": {
                    client.getBlockstorageClient().deleteVolumeBackupPolicyAssignment(DeleteVolumeBackupPolicyAssignmentRequest.builder().policyAssignmentId(resourceId).build());
                    return;
                }
                case "PRIVATE_ENDPOINT": {
                    if (namespace == null || namespace.isBlank()) {
                        throw new OciException("\u5220\u9664\u4e13\u7528\u7aef\u70b9\u9700\u8981 namespace \u4e0e\u7aef\u70b9\u540d\u79f0\uff08resourceId \u4f20 peName\uff09");
                    }
                    client.getObjectStorageClient().deletePrivateEndpoint(DeletePrivateEndpointRequest.builder().namespaceName(namespace).peName(resourceId).build());
                    return;
                }
                default: {
                    throw new OciException("\u672a\u77e5\u8d44\u6e90\u7c7b\u578b: " + resourceType);
                }
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            throw new OciException("\u5220\u9664\u5931\u8d25: " + e.getMessage());
        }
        catch (Exception e) {
            throw new OciException("\u5220\u9664\u5931\u8d25: " + e.getMessage());
        }
    }

    public void putBucketPolicy(String userId, String region, String namespace, String bucketName, String policy) {
        if (namespace == null || namespace.isBlank() || bucketName == null || bucketName.isBlank()) {
            throw new OciException("namespace / bucketName \u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (policy == null) {
            throw new OciException("policy \u4e0d\u80fd\u4e3a\u7a7a");
        }
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = new OciClientService(this.buildDto(ociUser), region);){
            HttpClient http = this.ociProxyConfigService.newOutboundHttpClient();
            ObjectStorageBucketPolicyHttp.putBucketPolicy((HttpClient)http, (ObjectStorageClient)client.getObjectStorageClient(), (BasicAuthenticationDetailsProvider)client.getProvider(), (String)namespace, (String)bucketName, (String)policy);
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u4fdd\u5b58\u6876\u7b56\u7565\u5931\u8d25: " + e.getMessage());
        }
    }

    public Object mutate(Map<String, Object> params) {
        Object object;
        String action = StorageService.stringParam(params, (String)"action");
        String userId = StorageService.stringParam(params, (String)"id");
        String region = StorageService.stringParam(params, (String)"region");
        OciUser ociUser = this.requireUser(userId);
        OciClientService client = new OciClientService(this.buildDto(ociUser), region);
        try {
            object = switch (action) {
                case "updateBootVolume" -> {
                    String bootVolumeId = StorageService.stringParam(params, (String)"bootVolumeId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    Long size = StorageService.longParam(params, (String)"sizeInGBs");
                    Long vpusPerGb = StorageService.longParam(params, (String)"vpusPerGB");
                    UpdateBootVolumeDetails.Builder b = UpdateBootVolumeDetails.builder();
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
                        throw new OciException("\u81f3\u5c11\u63d0\u4f9b displayName\u3001sizeInGBs \u6216 vpusPerGB \u4e4b\u4e00");
                    }
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails(b.build()).build()).getBootVolume());
                }
                case "updateBlockVolume" -> {
                    String volumeId = StorageService.stringParam(params, (String)"volumeId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    Long size = StorageService.longParam(params, (String)"sizeInGBs");
                    Long vpusPerGb = StorageService.longParam(params, (String)"vpusPerGB");
                    UpdateBootVolumeDetails.Builder b = UpdateVolumeDetails.builder();
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
                        throw new OciException("\u81f3\u5c11\u63d0\u4f9b displayName\u3001sizeInGBs \u6216 vpusPerGB \u4e4b\u4e00");
                    }
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails(b.build()).build()).getVolume());
                }
                case "updateBootVolumeReplica", "updateBlockVolumeReplica" -> throw new OciException("\u5f53\u524d OCI Java SDK \u5df2\u4e0d\u518d\u66b4\u9732\u526f\u672c\u66f4\u65b0\u63a5\u53e3\uff0c\u8bf7\u5728 OCI \u63a7\u5236\u53f0\u4fee\u6539\u526f\u672c\u663e\u793a\u540d\u79f0");
                case "updateVolumeGroup" -> {
                    String id = StorageService.stringParam(params, (String)"volumeGroupId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateVolumeGroup(UpdateVolumeGroupRequest.builder().volumeGroupId(id).updateVolumeGroupDetails(UpdateVolumeGroupDetails.builder().displayName(displayName).build()).build()).getVolumeGroup());
                }
                case "enableBlockVolumeReplication" -> {
                    String volumeId = StorageService.stringParam(params, (String)"volumeId");
                    String replicaDisplayName = StorageService.stringParam(params, (String)"replicaDisplayName");
                    String destinationAvailabilityDomain = StorageService.stringParam(params, (String)"destinationAvailabilityDomain");
                    String xrrKmsKeyId = StorageService.stringParam(params, (String)"xrrKmsKeyId");
                    BlockVolumeReplicaDetails.Builder replica = BlockVolumeReplicaDetails.builder().displayName(replicaDisplayName).availabilityDomain(destinationAvailabilityDomain);
                    if (!xrrKmsKeyId.isBlank()) {
                        replica.xrrKmsKeyId(xrrKmsKeyId);
                    }
                    Object details = UpdateVolumeDetails.builder().blockVolumeReplicas(List.of(replica.build())).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails((UpdateVolumeDetails)details).build()).getVolume());
                }
                case "enableBootVolumeReplication" -> {
                    String bootVolumeId = StorageService.stringParam(params, (String)"bootVolumeId");
                    String replicaDisplayName = StorageService.stringParam(params, (String)"replicaDisplayName");
                    String destinationAvailabilityDomain = StorageService.stringParam(params, (String)"destinationAvailabilityDomain");
                    String xrrKmsKeyId = StorageService.stringParam(params, (String)"xrrKmsKeyId");
                    BlockVolumeReplicaDetails.Builder replica = BootVolumeReplicaDetails.builder().displayName(replicaDisplayName).availabilityDomain(destinationAvailabilityDomain);
                    if (!xrrKmsKeyId.isBlank()) {
                        replica.xrrKmsKeyId(xrrKmsKeyId);
                    }
                    Object details = UpdateBootVolumeDetails.builder().bootVolumeReplicas(List.of(replica.build())).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails((UpdateBootVolumeDetails)details).build()).getBootVolume());
                }
                case "activateBlockReplicaAsVolume" -> {
                    String replicaId = StorageService.stringParam(params, (String)"replicaId");
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String ad = StorageService.stringParam(params, (String)"availabilityDomain");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    Long sizeInGBs = StorageService.longParam(params, (String)"sizeInGBs");
                    VolumeSourceFromBlockVolumeReplicaDetails src = VolumeSourceFromBlockVolumeReplicaDetails.builder().id(replicaId).build();
                    CreateVolumeDetails.Builder var15_20 = CreateVolumeDetails.builder().availabilityDomain(ad).compartmentId(compartmentId).displayName(displayName).sourceDetails((VolumeSourceDetails)src);
                    if (sizeInGBs != null) {
                        var15_20.sizeInGBs(sizeInGBs);
                    }
                    yield StorageService.toMap((Object)client.getBlockstorageClient().createVolume(CreateVolumeRequest.builder().createVolumeDetails(var15_20.build()).build()).getVolume());
                }
                case "activateBootReplicaAsBootVolume" -> {
                    String replicaId = StorageService.stringParam(params, (String)"replicaId");
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String ad = StorageService.stringParam(params, (String)"availabilityDomain");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    Object src = BootVolumeSourceFromBootVolumeReplicaDetails.builder().id(replicaId).build();
                    Object details = CreateBootVolumeDetails.builder().availabilityDomain(ad).compartmentId(compartmentId).displayName(displayName).sourceDetails((BootVolumeSourceDetails)src).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().createBootVolume(CreateBootVolumeRequest.builder().createBootVolumeDetails((CreateBootVolumeDetails)details).build()).getBootVolume());
                }
                case "createBucket" -> {
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String name = StorageService.stringParam(params, (String)"name");
                    String accessType = StorageService.stringParam(params, (String)"publicAccessType");
                    CreateBucketDetails.Builder details = CreateBucketDetails.builder().compartmentId(compartmentId).name(name);
                    if (accessType != null && !accessType.isBlank()) {
                        details.publicAccessType(CreateBucketDetails.PublicAccessType.create((String)accessType));
                    }
                    yield StorageService.toMap((Object)client.getObjectStorageClient().createBucket(CreateBucketRequest.builder().namespaceName(StorageService.stringParam(params, (String)"namespace")).createBucketDetails(details.build()).build()).getBucket());
                }
                case "updateBucket" -> {
                    Object src;
                    String namespace = StorageService.stringParam(params, (String)"namespace");
                    String bucketName = StorageService.stringParam(params, (String)"bucketName");
                    UpdateBucketDetails.Builder ub = UpdateBucketDetails.builder();
                    if (params.containsKey("namespace")) {
                        // empty if block
                    }
                    if (params.get("versioning") != null) {
                        String v = String.valueOf(params.get("versioning"));
                        ub.versioning(UpdateBucketDetails.Versioning.create((String)v));
                    }
                    if ((src = params.get("freeformTags")) instanceof Map) {
                        Map m = (Map)src;
                        LinkedHashMap<String, String> tags = new LinkedHashMap<String, String>();
                        for (Map.Entry var15_21 : m.entrySet()) {
                            tags.put(String.valueOf(var15_21.getKey()), var15_21.getValue() == null ? "" : String.valueOf(var15_21.getValue()));
                        }
                        ub.freeformTags(tags);
                    }
                    if (params.containsKey("publicAccessType") && params.get("publicAccessType") != null) {
                        String pa = String.valueOf(params.get("publicAccessType"));
                        ub.publicAccessType(UpdateBucketDetails.PublicAccessType.create((String)pa));
                    }
                    yield StorageService.toMap((Object)client.getObjectStorageClient().updateBucket(UpdateBucketRequest.builder().namespaceName(namespace).bucketName(bucketName).updateBucketDetails(ub.build()).build()).getBucket());
                }
                case "createPrivateEndpoint" -> {
                    String namespace = StorageService.stringParam(params, (String)"namespace");
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String subnetId = StorageService.stringParam(params, (String)"subnetId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    CreatePrivateEndpointDetails det = CreatePrivateEndpointDetails.builder().compartmentId(compartmentId).subnetId(subnetId).name(displayName).build();
                    CreatePrivateEndpointResponse resp = client.getObjectStorageClient().createPrivateEndpoint(CreatePrivateEndpointRequest.builder().namespaceName(namespace).createPrivateEndpointDetails(det).build());
                    LinkedHashMap<String, String> var15_22 = new LinkedHashMap<String, String>();
                    var15_22.put("opcWorkRequestId", resp.getOpcWorkRequestId());
                    var15_22.put("namespace", namespace);
                    var15_22.put("name", displayName);
                    var15_22.put("compartmentId", compartmentId);
                    var15_22.put("subnetId", subnetId);
                    yield var15_22;
                }
                case "createVolumeBackupPolicyAssignment" -> {
                    String policyId = StorageService.stringParam(params, (String)"policyId");
                    String assetId = StorageService.stringParam(params, (String)"assetId");
                    CreateVolumeBackupPolicyAssignmentDetails det = CreateVolumeBackupPolicyAssignmentDetails.builder().policyId(policyId).assetId(assetId).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().createVolumeBackupPolicyAssignment(CreateVolumeBackupPolicyAssignmentRequest.builder().createVolumeBackupPolicyAssignmentDetails(det).build()).getVolumeBackupPolicyAssignment());
                }
                case "createVolumeBackupPolicy" -> {
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    List schedules = (List)params.get("schedules");
                    List built = StorageService.parseVolumeBackupSchedules((List)schedules);
                    if (built.isEmpty()) {
                        built.add(VolumeBackupSchedule.builder().backupType(VolumeBackupSchedule.BackupType.Full).period(VolumeBackupSchedule.Period.OneDay).offsetType(VolumeBackupSchedule.OffsetType.Structured).hourOfDay(Integer.valueOf(2)).retentionSeconds(Integer.valueOf(604800)).build());
                    }
                    CreatePrivateEndpointDetails det = CreateVolumeBackupPolicyDetails.builder().compartmentId(compartmentId).displayName(displayName).schedules(built).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().createVolumeBackupPolicy(CreateVolumeBackupPolicyRequest.builder().createVolumeBackupPolicyDetails((CreateVolumeBackupPolicyDetails)det).build()).getVolumeBackupPolicy());
                }
                case "updateVolumeBackupPolicy" -> {
                    String policyId = StorageService.stringParam(params, (String)"policyId");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    List schedules = (List)params.get("schedules");
                    List built = StorageService.parseVolumeBackupSchedules((List)schedules);
                    UpdateVolumeBackupPolicyDetails.Builder ub = UpdateVolumeBackupPolicyDetails.builder().displayName(displayName);
                    if (!built.isEmpty()) {
                        ub.schedules(built);
                    }
                    yield StorageService.toMap((Object)client.getBlockstorageClient().updateVolumeBackupPolicy(UpdateVolumeBackupPolicyRequest.builder().policyId(policyId).updateVolumeBackupPolicyDetails(ub.build()).build()).getVolumeBackupPolicy());
                }
                case "createVolumeGroup" -> {
                    String compartmentId = StorageService.stringParam(params, (String)"compartmentId");
                    String availabilityDomain = StorageService.stringParam(params, (String)"availabilityDomain");
                    String displayName = StorageService.stringParam(params, (String)"displayName");
                    List volumeIds = (List)params.get("volumeIds");
                    CreatePrivateEndpointDetails det = CreateVolumeGroupDetails.builder().compartmentId(compartmentId).availabilityDomain(availabilityDomain).displayName(displayName).sourceDetails((VolumeGroupSourceDetails)VolumeGroupSourceFromVolumesDetails.builder().volumeIds(volumeIds == null ? List.of() : volumeIds).build()).build();
                    yield StorageService.toMap((Object)client.getBlockstorageClient().createVolumeGroup(CreateVolumeGroupRequest.builder().createVolumeGroupDetails((CreateVolumeGroupDetails)det).build()).getVolumeGroup());
                }
                default -> throw new OciException("\u672a\u77e5\u64cd\u4f5c: " + action);
            };
        }
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (OciException e) {
                throw e;
            }
            catch (BmcException e) {
                throw new OciException("\u64cd\u4f5c\u5931\u8d25: " + e.getMessage());
            }
            catch (Exception e) {
                throw new OciException("\u64cd\u4f5c\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return object;
    }

    private void assertBucketEmpty(OciClientService client, String namespace, String bucketName) {
        ListObjectsResponse resp = client.getObjectStorageClient().listObjects(ListObjectsRequest.builder().namespaceName(namespace).bucketName(bucketName).limit(Integer.valueOf(1)).build());
        if (resp.getListObjects() != null && resp.getListObjects().getObjects() != null && !resp.getListObjects().getObjects().isEmpty()) {
            throw new OciException("\u6876\u975e\u7a7a\uff0c\u62d2\u7edd\u5220\u9664\u3002\u8bf7\u5148\u6e05\u7a7a\u5bf9\u8c61\u540e\u518d\u5220\u9664\u3002");
        }
    }

    private List<String> listAvailabilityDomainNames(OciClientService client) {
        try {
            List<String> names = client.getAvailabilityDomains().stream().map(ad -> ad.getName()).filter(n -> n != null && !n.isBlank()).distinct().toList();
            if (names.isEmpty()) {
                throw new OciException("\u5f53\u524d\u533a\u57df\u672a\u8fd4\u56de\u4efb\u4f55\u53ef\u7528\u57df\uff08Availability Domain\uff09\uff0c\u65e0\u6cd5\u5217\u4e3e\u5757\u5b58\u50a8\u8d44\u6e90");
            }
            return names;
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u83b7\u53d6\u53ef\u7528\u57df\u5217\u8868\u5931\u8d25: " + e.getMessage());
        }
    }

    private List<Compartment> resolveCompartments(OciClientService client, String compartmentIdOpt) {
        if (compartmentIdOpt == null || compartmentIdOpt.isBlank()) {
            return client.listAllCompartments();
        }
        try {
            Compartment c = client.getIdentityClient().getCompartment(GetCompartmentRequest.builder().compartmentId(compartmentIdOpt).build()).getCompartment();
            return List.of(c);
        }
        catch (Exception e) {
            throw new OciException("\u8bfb\u53d6\u533a\u95f4\u5931\u8d25: " + e.getMessage());
        }
    }

    private Map<String, String> loadInstanceNames(OciClientService client, String compartmentId) {
        HashMap<String, String> names = new HashMap<String, String>();
        for (Instance.LifecycleState state : List.of(Instance.LifecycleState.Running, Instance.LifecycleState.Stopped, Instance.LifecycleState.Starting, Instance.LifecycleState.Stopping)) {
            ListInstancesResponse resp;
            String page = null;
            do {
                resp = client.getComputeClient().listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).lifecycleState(state).page(page).build());
                for (Instance i : resp.getItems()) {
                    names.put(i.getId(), i.getDisplayName());
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        return names;
    }

    private Map<String, List<Map<String, Object>>> loadBootVolumeAttachments(OciClientService client, String compartmentId, Map<String, String> instanceNames, List<String> availabilityDomains) {
        HashMap<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
        for (String ad : availabilityDomains) {
            ListBootVolumeAttachmentsResponse resp;
            String page = null;
            do {
                resp = client.getComputeClient().listBootVolumeAttachments(ListBootVolumeAttachmentsRequest.builder().compartmentId(compartmentId).availabilityDomain(ad).page(page).build());
                for (BootVolumeAttachment a : resp.getItems()) {
                    if (a.getLifecycleState() == BootVolumeAttachment.LifecycleState.Detached) continue;
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("instanceId", a.getInstanceId());
                    row.put("instanceName", instanceNames.getOrDefault(a.getInstanceId(), ""));
                    row.put("lifecycleState", a.getLifecycleState() != null ? a.getLifecycleState().getValue() : null);
                    map.computeIfAbsent(a.getBootVolumeId(), k -> new ArrayList()).add(row);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        return map;
    }

    private Map<String, List<Map<String, Object>>> loadVolumeAttachments(OciClientService client, String compartmentId, Map<String, String> instanceNames, List<String> availabilityDomains) {
        HashMap<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
        for (String ad : availabilityDomains) {
            ListVolumeAttachmentsResponse resp;
            String page = null;
            do {
                resp = client.getComputeClient().listVolumeAttachments(ListVolumeAttachmentsRequest.builder().compartmentId(compartmentId).availabilityDomain(ad).page(page).build());
                for (VolumeAttachment a : resp.getItems()) {
                    String volId;
                    if (a.getLifecycleState() == VolumeAttachment.LifecycleState.Detached || (volId = a.getVolumeId()) == null) continue;
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("instanceId", a.getInstanceId());
                    row.put("instanceName", instanceNames.getOrDefault(a.getInstanceId(), ""));
                    row.put("lifecycleState", a.getLifecycleState() != null ? a.getLifecycleState().getValue() : null);
                    map.computeIfAbsent(volId, k -> new ArrayList()).add(row);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        return map;
    }

    private void listBootVolumes(OciClientService client, String region, String cid, String cname, Map<String, List<Map<String, Object>>> bootAttach, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListBootVolumesResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listBootVolumes(ListBootVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (BootVolume v : resp.getItems()) {
                        if (v.getLifecycleState() == BootVolume.LifecycleState.Terminated || !seenIds.add(v.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)v.getId(), (String)v.getDisplayName(), (String)(v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null), v.getTimeCreated() != null ? v.getTimeCreated().toString() : null);
                        m.put("sizeInGBs", v.getSizeInGBs());
                        m.put("vpusPerGB", v.getVpusPerGB());
                        m.put("availabilityDomain", v.getAvailabilityDomain());
                        m.put("imageId", v.getImageId());
                        m.put("attachments", bootAttach.getOrDefault(v.getId(), List.of()));
                        m.put("attachmentSummary", StorageService.summarizeAttachments(bootAttach.get(v.getId())));
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listBootVolumes {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBlockVolumes(OciClientService client, String region, String cid, String cname, Map<String, List<Map<String, Object>>> volAttach, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListVolumesResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listVolumes(ListVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (Volume v : resp.getItems()) {
                        if (v.getLifecycleState() == Volume.LifecycleState.Terminated || !seenIds.add(v.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)v.getId(), (String)v.getDisplayName(), (String)(v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null), v.getTimeCreated() != null ? v.getTimeCreated().toString() : null);
                        m.put("sizeInGBs", v.getSizeInGBs());
                        m.put("vpusPerGB", v.getVpusPerGB());
                        m.put("availabilityDomain", v.getAvailabilityDomain());
                        m.put("isHydrated", v.getIsHydrated());
                        m.put("attachments", volAttach.getOrDefault(v.getId(), List.of()));
                        m.put("attachmentSummary", StorageService.summarizeAttachments(volAttach.get(v.getId())));
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listVolumes {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBootBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListBootVolumeBackupsResponse resp;
            String page = null;
            do {
                resp = client.getBlockstorageClient().listBootVolumeBackups(ListBootVolumeBackupsRequest.builder().compartmentId(cid).page(page).build());
                for (BootVolumeBackup b : resp.getItems()) {
                    if (b.getLifecycleState() == BootVolumeBackup.LifecycleState.Terminated) continue;
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)b.getId(), (String)b.getDisplayName(), (String)(b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null), b.getTimeCreated() != null ? b.getTimeCreated().toString() : null);
                    m.put("sizeInGBs", b.getSizeInGBs());
                    m.put("uniqueSizeInGBs", b.getUniqueSizeInGBs());
                    m.put("sourceBootVolumeId", b.getBootVolumeId());
                    m.put("sourceType", b.getSourceType() != null ? b.getSourceType().getValue() : null);
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listBootVolumeBackups {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBlockBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListVolumeBackupsResponse resp;
            String page = null;
            do {
                resp = client.getBlockstorageClient().listVolumeBackups(ListVolumeBackupsRequest.builder().compartmentId(cid).page(page).build());
                for (VolumeBackup b : resp.getItems()) {
                    if (b.getLifecycleState() == VolumeBackup.LifecycleState.Terminated) continue;
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)b.getId(), (String)b.getDisplayName(), (String)(b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null), b.getTimeCreated() != null ? b.getTimeCreated().toString() : null);
                    m.put("sizeInGBs", b.getSizeInGBs());
                    m.put("uniqueSizeInGBs", b.getUniqueSizeInGBs());
                    m.put("sourceVolumeId", b.getVolumeId());
                    m.put("sourceType", b.getSourceType() != null ? b.getSourceType().getValue() : null);
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listVolumeBackups {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBootReplicas(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListBootVolumeReplicasResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listBootVolumeReplicas(ListBootVolumeReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (BootVolumeReplica r : resp.getItems()) {
                        if (r.getLifecycleState() == BootVolumeReplica.LifecycleState.Terminated || !seenIds.add(r.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)r.getId(), (String)r.getDisplayName(), (String)(r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null), r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
                        m.put("sizeInGBs", r.getSizeInGBs());
                        m.put("availabilityDomain", r.getAvailabilityDomain());
                        m.put("sourceBootVolumeId", r.getBootVolumeId());
                        m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listBootVolumeReplicas {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBlockReplicas(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListBlockVolumeReplicasResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listBlockVolumeReplicas(ListBlockVolumeReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (BlockVolumeReplica r : resp.getItems()) {
                        if (r.getLifecycleState() == BlockVolumeReplica.LifecycleState.Terminated || !seenIds.add(r.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)r.getId(), (String)r.getDisplayName(), (String)(r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null), r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
                        m.put("sizeInGBs", r.getSizeInGBs());
                        m.put("availabilityDomain", r.getAvailabilityDomain());
                        m.put("sourceVolumeId", r.getBlockVolumeId());
                        m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                        m.put("volumeGroupReplicaId", r.getVolumeGroupReplicaId());
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listBlockVolumeReplicas {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listVolumeGroups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListVolumeGroupsResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listVolumeGroups(ListVolumeGroupsRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (VolumeGroup g : resp.getItems()) {
                        if (g.getLifecycleState() == VolumeGroup.LifecycleState.Terminated || !seenIds.add(g.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)g.getId(), (String)g.getDisplayName(), (String)(g.getLifecycleState() != null ? g.getLifecycleState().getValue() : null), g.getTimeCreated() != null ? g.getTimeCreated().toString() : null);
                        m.put("availabilityDomain", g.getAvailabilityDomain());
                        m.put("volumeIds", g.getVolumeIds());
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listVolumeGroups {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listVolumeGroupBackups(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListVolumeGroupBackupsResponse resp;
            String page = null;
            do {
                resp = client.getBlockstorageClient().listVolumeGroupBackups(ListVolumeGroupBackupsRequest.builder().compartmentId(cid).page(page).build());
                for (VolumeGroupBackup b : resp.getItems()) {
                    if (b.getLifecycleState() == VolumeGroupBackup.LifecycleState.Terminated) continue;
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)b.getId(), (String)b.getDisplayName(), (String)(b.getLifecycleState() != null ? b.getLifecycleState().getValue() : null), b.getTimeCreated() != null ? b.getTimeCreated().toString() : null);
                    m.put("sizeInGBs", b.getSizeInGBs());
                    m.put("uniqueSizeInGBs", b.getUniqueSizeInGbs());
                    m.put("volumeGroupId", b.getVolumeGroupId());
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listVolumeGroupBackups {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listVolumeGroupReplicas(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out, List<String> availabilityDomains) {
        try {
            HashSet<String> seenIds = new HashSet<String>();
            for (String ad : availabilityDomains) {
                ListVolumeGroupReplicasResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listVolumeGroupReplicas(ListVolumeGroupReplicasRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());
                    for (VolumeGroupReplica r : resp.getItems()) {
                        if (r.getLifecycleState() == VolumeGroupReplica.LifecycleState.Terminated || !seenIds.add(r.getId())) continue;
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)r.getId(), (String)r.getDisplayName(), (String)(r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null), r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
                        m.put("availabilityDomain", r.getAvailabilityDomain());
                        m.put("sourceVolumeGroupId", r.getVolumeGroupId());
                        m.put("timeLastSynced", r.getTimeLastSynced() != null ? r.getTimeLastSynced().toString() : null);
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
        }
        catch (Exception e) {
            log.debug("listVolumeGroupReplicas {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listBackupPolicies(OciClientService client, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListVolumeBackupPoliciesResponse resp;
            String page = null;
            do {
                resp = client.getBlockstorageClient().listVolumeBackupPolicies(ListVolumeBackupPoliciesRequest.builder().compartmentId(cid).page(page).build());
                for (VolumeBackupPolicy p : resp.getItems()) {
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)p.getId(), (String)p.getDisplayName(), null, null);
                    m.put("schedules", p.getSchedules());
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listVolumeBackupPolicies {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void collectVolumeBackupPolicyAssignments(OciClientService client, String region, String cid, String cname, List<String> assetIds, List<Map<String, Object>> out) {
        for (String assetId : assetIds) {
            if (assetId == null || assetId.isBlank()) continue;
            try {
                GetVolumeBackupPolicyAssetAssignmentResponse resp;
                String page = null;
                do {
                    resp = client.getBlockstorageClient().getVolumeBackupPolicyAssetAssignment(GetVolumeBackupPolicyAssetAssignmentRequest.builder().assetId(assetId).limit(Integer.valueOf(100)).page(page).build());
                    for (VolumeBackupPolicyAssignment a : resp.getItems()) {
                        Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)a.getId(), null, null, a.getTimeCreated() != null ? a.getTimeCreated().toString() : null);
                        m.put("policyId", a.getPolicyId());
                        m.put("assetId", a.getAssetId());
                        out.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
            }
            catch (Exception e) {
                log.debug("getVolumeBackupPolicyAssetAssignment {}: {}", (Object)assetId, (Object)e.getMessage());
            }
        }
    }

    private static List<VolumeBackupSchedule> parseVolumeBackupSchedules(List<Map<String, Object>> schedules) {
        ArrayList<VolumeBackupSchedule> built = new ArrayList<VolumeBackupSchedule>();
        if (schedules == null) {
            return built;
        }
        for (Map<String, Object> s : schedules) {
            Integer ret;
            String month;
            Integer dom;
            String dow;
            Integer hod;
            Integer os;
            String ot;
            String period;
            VolumeBackupSchedule.Builder b = VolumeBackupSchedule.builder();
            String bt = StorageService.stringParam(s, (String)"backupType");
            if (!bt.isBlank()) {
                b.backupType(VolumeBackupSchedule.BackupType.create((String)bt));
            }
            if (!(period = StorageService.stringParam(s, (String)"period")).isBlank()) {
                b.period(VolumeBackupSchedule.Period.create((String)period));
            }
            if (!(ot = StorageService.stringParam(s, (String)"offsetType")).isBlank()) {
                b.offsetType(VolumeBackupSchedule.OffsetType.create((String)ot));
            }
            if ((os = StorageService.intParam(s, (String)"offsetSeconds")) != null) {
                b.offsetSeconds(os);
            }
            if ((hod = StorageService.intParam(s, (String)"hourOfDay")) != null) {
                b.hourOfDay(hod);
            }
            if (!(dow = StorageService.stringParam(s, (String)"dayOfWeek")).isBlank()) {
                b.dayOfWeek(VolumeBackupSchedule.DayOfWeek.create((String)dow));
            }
            if ((dom = StorageService.intParam(s, (String)"dayOfMonth")) != null) {
                b.dayOfMonth(dom);
            }
            if (!(month = StorageService.stringParam(s, (String)"month")).isBlank()) {
                b.month(VolumeBackupSchedule.Month.create((String)month));
            }
            if ((ret = StorageService.intParam(s, (String)"retentionSeconds")) != null) {
                b.retentionSeconds(ret);
            }
            built.add(b.build());
        }
        return built;
    }

    private void listBuckets(OciClientService client, String namespace, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListBucketsResponse resp;
            String page = null;
            do {
                resp = client.getObjectStorageClient().listBuckets(ListBucketsRequest.builder().namespaceName(namespace).compartmentId(cid).page(page).build());
                for (BucketSummary s : resp.getItems()) {
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, null, (String)s.getName(), null, null);
                    m.put("namespace", namespace);
                    m.put("name", s.getName());
                    m.put("publicAccessType", null);
                    m.put("storageTier", null);
                    m.put("createdBy", s.getCreatedBy());
                    m.put("timeCreated", s.getTimeCreated() != null ? s.getTimeCreated().toString() : null);
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listBuckets {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private void listPrivateEndpoints(OciClientService client, String namespace, String region, String cid, String cname, List<Map<String, Object>> out) {
        try {
            ListPrivateEndpointsResponse resp;
            String page = null;
            do {
                resp = client.getObjectStorageClient().listPrivateEndpoints(ListPrivateEndpointsRequest.builder().compartmentId(cid).namespaceName(namespace).page(page).build());
                for (PrivateEndpointSummary pe : resp.getItems()) {
                    String peName = pe.getName();
                    Map m = StorageService.baseRow((String)region, (String)cid, (String)cname, (String)peName, (String)peName, (String)(pe.getLifecycleState() != null ? pe.getLifecycleState().getValue() : null), pe.getTimeCreated() != null ? pe.getTimeCreated().toString() : null);
                    m.put("subnetId", null);
                    m.put("namespace", pe.getNamespace() != null ? pe.getNamespace() : namespace);
                    out.add(m);
                }
            } while ((page = resp.getOpcNextPage()) != null);
        }
        catch (Exception e) {
            log.debug("listPrivateEndpoints {}: {}", (Object)cid, (Object)e.getMessage());
        }
    }

    private static Map<String, Object> baseRow(String region, String compartmentId, String compartmentName, String id, String displayName, String lifecycleState, String timeCreated) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
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
        if (attachments == null || attachments.isEmpty()) {
            return "\u672a\u6302\u8f7d";
        }
        if (attachments.size() == 1) {
            Map<String, Object> a = attachments.get(0);
            String nm = Objects.toString(a.get("instanceName"), "");
            if (nm.isBlank()) {
                return "\u5df2\u6302\u8f7d 1";
            }
            return "\u5df2\u6302\u8f7d: " + nm;
        }
        return "\u5df2\u6302\u8f7d: " + attachments.size() + " \u5904";
    }

    private static Map<String, Object> toMap(Object model) {
        if (model == null) {
            return Map.of();
        }
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        if (model instanceof Volume) {
            Volume v = (Volume)model;
            m.put("id", v.getId());
            m.put("displayName", v.getDisplayName());
            m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
        } else if (model instanceof BootVolume) {
            BootVolume v = (BootVolume)model;
            m.put("id", v.getId());
            m.put("displayName", v.getDisplayName());
            m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
        } else if (model instanceof BootVolumeReplica) {
            BootVolumeReplica v = (BootVolumeReplica)model;
            m.put("id", v.getId());
            m.put("displayName", v.getDisplayName());
            m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
        } else if (model instanceof BlockVolumeReplica) {
            BlockVolumeReplica v = (BlockVolumeReplica)model;
            m.put("id", v.getId());
            m.put("displayName", v.getDisplayName());
            m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
        } else if (model instanceof VolumeGroup) {
            VolumeGroup v = (VolumeGroup)model;
            m.put("id", v.getId());
            m.put("displayName", v.getDisplayName());
            m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
        } else if (model instanceof Bucket) {
            Bucket b = (Bucket)model;
            m.put("name", b.getName());
            m.put("namespace", b.getNamespace());
        } else if (model instanceof PrivateEndpoint) {
            PrivateEndpoint pe = (PrivateEndpoint)model;
            m.put("id", pe.getId());
            m.put("displayName", pe.getName());
        } else if (model instanceof VolumeBackupPolicy) {
            VolumeBackupPolicy p = (VolumeBackupPolicy)model;
            m.put("id", p.getId());
            m.put("displayName", p.getDisplayName());
        } else if (model instanceof VolumeBackupPolicyAssignment) {
            VolumeBackupPolicyAssignment a = (VolumeBackupPolicyAssignment)model;
            m.put("id", a.getId());
            m.put("assetId", a.getAssetId());
            m.put("policyId", a.getPolicyId());
        } else {
            m.put("result", String.valueOf(model));
        }
        return m;
    }

    private static String stringParam(Map<?, ?> map, String key) {
        Object v = map.get(key);
        if (v == null) {
            return "";
        }
        return String.valueOf(v).trim();
    }

    private static Long longParam(Map<?, ?> map, String key) {
        Object v = map.get(key);
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            Number n = (Number)v;
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v).trim());
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Integer intParam(Map<?, ?> map, String key) {
        Long v = StorageService.longParam(map, (String)key);
        return v == null ? null : Integer.valueOf(v.intValue());
    }

    private OciUser requireUser(String userId) {
        OciUser u = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (u == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return u;
    }

    private SysUserDTO buildDto(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }
}

