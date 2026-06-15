/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.ShapeEditTaskStatus
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.InstanceService
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.ShapeEditTaskManager
 *  com.ociworker.util.ShapeFlexLimitsUtil
 *  com.ociworker.util.ShapeFlexLimitsUtil$FlexLimits
 *  com.oracle.bmc.core.model.AddVcnIpv6CidrDetails
 *  com.oracle.bmc.core.model.AttachVolumeDetails
 *  com.oracle.bmc.core.model.BootVolume
 *  com.oracle.bmc.core.model.BootVolumeAttachment
 *  com.oracle.bmc.core.model.CreateIpv6Details
 *  com.oracle.bmc.core.model.CreatePrivateIpDetails
 *  com.oracle.bmc.core.model.CreatePublicIpDetails
 *  com.oracle.bmc.core.model.CreatePublicIpDetails$Builder
 *  com.oracle.bmc.core.model.CreatePublicIpDetails$Lifetime
 *  com.oracle.bmc.core.model.CreateVolumeDetails
 *  com.oracle.bmc.core.model.GetPublicIpByPrivateIpIdDetails
 *  com.oracle.bmc.core.model.Instance
 *  com.oracle.bmc.core.model.InternetGateway
 *  com.oracle.bmc.core.model.Ipv6
 *  com.oracle.bmc.core.model.PrivateIp
 *  com.oracle.bmc.core.model.PublicIp
 *  com.oracle.bmc.core.model.RouteRule
 *  com.oracle.bmc.core.model.RouteRule$DestinationType
 *  com.oracle.bmc.core.model.RouteTable
 *  com.oracle.bmc.core.model.Shape
 *  com.oracle.bmc.core.model.Subnet
 *  com.oracle.bmc.core.model.UpdateBootVolumeDetails
 *  com.oracle.bmc.core.model.UpdateBootVolumeDetails$Builder
 *  com.oracle.bmc.core.model.UpdateInstanceDetails
 *  com.oracle.bmc.core.model.UpdateInstanceDetails$Builder
 *  com.oracle.bmc.core.model.UpdateInstanceShapeConfigDetails
 *  com.oracle.bmc.core.model.UpdateInstanceShapeConfigDetails$Builder
 *  com.oracle.bmc.core.model.UpdatePublicIpDetails
 *  com.oracle.bmc.core.model.UpdateRouteTableDetails
 *  com.oracle.bmc.core.model.UpdateSubnetDetails
 *  com.oracle.bmc.core.model.UpdateVolumeDetails
 *  com.oracle.bmc.core.model.UpdateVolumeDetails$Builder
 *  com.oracle.bmc.core.model.Vcn
 *  com.oracle.bmc.core.model.Vnic
 *  com.oracle.bmc.core.model.VnicAttachment
 *  com.oracle.bmc.core.model.Volume
 *  com.oracle.bmc.core.model.Volume$LifecycleState
 *  com.oracle.bmc.core.model.VolumeAttachment
 *  com.oracle.bmc.core.model.VolumeAttachment$LifecycleState
 *  com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest
 *  com.oracle.bmc.core.requests.AttachVolumeRequest
 *  com.oracle.bmc.core.requests.CreateIpv6Request
 *  com.oracle.bmc.core.requests.CreatePrivateIpRequest
 *  com.oracle.bmc.core.requests.CreatePublicIpRequest
 *  com.oracle.bmc.core.requests.CreateVolumeRequest
 *  com.oracle.bmc.core.requests.DeleteIpv6Request
 *  com.oracle.bmc.core.requests.DeletePrivateIpRequest
 *  com.oracle.bmc.core.requests.DeletePublicIpRequest
 *  com.oracle.bmc.core.requests.DetachVolumeRequest
 *  com.oracle.bmc.core.requests.GetBootVolumeRequest
 *  com.oracle.bmc.core.requests.GetInstanceRequest
 *  com.oracle.bmc.core.requests.GetPrivateIpRequest
 *  com.oracle.bmc.core.requests.GetPublicIpByPrivateIpIdRequest
 *  com.oracle.bmc.core.requests.GetPublicIpRequest
 *  com.oracle.bmc.core.requests.GetRouteTableRequest
 *  com.oracle.bmc.core.requests.GetSubnetRequest
 *  com.oracle.bmc.core.requests.GetVcnRequest
 *  com.oracle.bmc.core.requests.GetVnicRequest
 *  com.oracle.bmc.core.requests.GetVolumeRequest
 *  com.oracle.bmc.core.requests.InstanceActionRequest
 *  com.oracle.bmc.core.requests.ListBootVolumeAttachmentsRequest
 *  com.oracle.bmc.core.requests.ListInternetGatewaysRequest
 *  com.oracle.bmc.core.requests.ListIpv6sRequest
 *  com.oracle.bmc.core.requests.ListPrivateIpsRequest
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest$Lifetime
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest$Scope
 *  com.oracle.bmc.core.requests.ListVnicAttachmentsRequest
 *  com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest
 *  com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest$Builder
 *  com.oracle.bmc.core.requests.ListVolumesRequest
 *  com.oracle.bmc.core.requests.TerminateInstanceRequest
 *  com.oracle.bmc.core.requests.UpdateBootVolumeRequest
 *  com.oracle.bmc.core.requests.UpdateInstanceRequest
 *  com.oracle.bmc.core.requests.UpdatePublicIpRequest
 *  com.oracle.bmc.core.requests.UpdateRouteTableRequest
 *  com.oracle.bmc.core.requests.UpdateSubnetRequest
 *  com.oracle.bmc.core.requests.UpdateVolumeRequest
 *  com.oracle.bmc.core.responses.ListVolumeAttachmentsResponse
 *  com.oracle.bmc.core.responses.ListVolumesResponse
 *  com.oracle.bmc.identity.model.AvailabilityDomain
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.model.BmcException
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import com.ociworker.exception.OciException;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.dto.ShapeEditTaskStatus;
import com.ociworker.model.dto.SysUserDTO;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.NotificationService;
import com.ociworker.service.OciClientService;
import com.ociworker.service.ShapeEditTaskManager;
import com.ociworker.util.ShapeFlexLimitsUtil;
import com.oracle.bmc.core.model.AddVcnIpv6CidrDetails;
import com.oracle.bmc.core.model.AttachVolumeDetails;
import com.oracle.bmc.core.model.BootVolume;
import com.oracle.bmc.core.model.BootVolumeAttachment;
import com.oracle.bmc.core.model.CreateIpv6Details;
import com.oracle.bmc.core.model.CreatePrivateIpDetails;
import com.oracle.bmc.core.model.CreatePublicIpDetails;
import com.oracle.bmc.core.model.CreateVolumeDetails;
import com.oracle.bmc.core.model.GetPublicIpByPrivateIpIdDetails;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.Ipv6;
import com.oracle.bmc.core.model.PrivateIp;
import com.oracle.bmc.core.model.PublicIp;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.Shape;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.UpdateBootVolumeDetails;
import com.oracle.bmc.core.model.UpdateInstanceDetails;
import com.oracle.bmc.core.model.UpdateInstanceShapeConfigDetails;
import com.oracle.bmc.core.model.UpdatePublicIpDetails;
import com.oracle.bmc.core.model.UpdateRouteTableDetails;
import com.oracle.bmc.core.model.UpdateSubnetDetails;
import com.oracle.bmc.core.model.UpdateVolumeDetails;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.model.VnicAttachment;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.model.VolumeAttachment;
import com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest;
import com.oracle.bmc.core.requests.AttachVolumeRequest;
import com.oracle.bmc.core.requests.CreateIpv6Request;
import com.oracle.bmc.core.requests.CreatePrivateIpRequest;
import com.oracle.bmc.core.requests.CreatePublicIpRequest;
import com.oracle.bmc.core.requests.CreateVolumeRequest;
import com.oracle.bmc.core.requests.DeleteIpv6Request;
import com.oracle.bmc.core.requests.DeletePrivateIpRequest;
import com.oracle.bmc.core.requests.DeletePublicIpRequest;
import com.oracle.bmc.core.requests.DetachVolumeRequest;
import com.oracle.bmc.core.requests.GetBootVolumeRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.GetPrivateIpRequest;
import com.oracle.bmc.core.requests.GetPublicIpByPrivateIpIdRequest;
import com.oracle.bmc.core.requests.GetPublicIpRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.core.requests.GetVnicRequest;
import com.oracle.bmc.core.requests.GetVolumeRequest;
import com.oracle.bmc.core.requests.InstanceActionRequest;
import com.oracle.bmc.core.requests.ListBootVolumeAttachmentsRequest;
import com.oracle.bmc.core.requests.ListInternetGatewaysRequest;
import com.oracle.bmc.core.requests.ListIpv6sRequest;
import com.oracle.bmc.core.requests.ListPrivateIpsRequest;
import com.oracle.bmc.core.requests.ListPublicIpsRequest;
import com.oracle.bmc.core.requests.ListVnicAttachmentsRequest;
import com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest;
import com.oracle.bmc.core.requests.ListVolumesRequest;
import com.oracle.bmc.core.requests.TerminateInstanceRequest;
import com.oracle.bmc.core.requests.UpdateBootVolumeRequest;
import com.oracle.bmc.core.requests.UpdateInstanceRequest;
import com.oracle.bmc.core.requests.UpdatePublicIpRequest;
import com.oracle.bmc.core.requests.UpdateRouteTableRequest;
import com.oracle.bmc.core.requests.UpdateSubnetRequest;
import com.oracle.bmc.core.requests.UpdateVolumeRequest;
import com.oracle.bmc.core.responses.ListVolumeAttachmentsResponse;
import com.oracle.bmc.core.responses.ListVolumesResponse;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class InstanceService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(InstanceService.class);
    private static final String SHAPE_A2_FLEX = "VM.Standard.A2.Flex";
    private static final String SHAPE_A1_FLEX = "VM.Standard.A1.Flex";
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private NotificationService notificationService;
    @Resource
    private ShapeEditTaskManager shapeEditTaskManager;

    private String tag(OciUser u) {
        return "[" + u.getUsername() + "] ";
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region == null || region.isBlank() ? null : region.trim();
        return new OciClientService(this.buildBasicDTO(ociUser), r);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Map<String, Object>> listInstances(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            List compartments = client.listAllCompartments();
            LinkedHashMap<String, String> compartmentNameMap = new LinkedHashMap<String, String>();
            for (Object c222 : compartments) {
                compartmentNameMap.put(c222.getId(), c222.getName());
            }
            ArrayList allInstances = new ArrayList();
            for (Compartment compartment : compartments) {
                allInstances.addAll(client.listAllInstancesInCompartment(compartment.getId()));
            }
            if (allInstances.isEmpty()) {
                Object c222;
                c222 = new ArrayList();
                return c222;
            }
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(Math.max(allInstances.size(), 1), 8));
            LinkedHashMap<String, Future<String>> ipFutures = new LinkedHashMap<String, Future<String>>();
            ArrayList result = new ArrayList();
            try {
                for (Instance inst : allInstances) {
                    ipFutures.put(inst.getId(), executor.submit(() -> {
                        try {
                            return client.getInstancePublicIp(inst);
                        }
                        catch (Exception e) {
                            return null;
                        }
                    }));
                }
                for (Instance inst : allInstances) {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("instanceId", inst.getId());
                    map.put("name", inst.getDisplayName());
                    map.put("region", inst.getRegion());
                    map.put("shape", inst.getShape());
                    map.put("state", inst.getLifecycleState().getValue());
                    map.put("timeCreated", inst.getTimeCreated() != null ? inst.getTimeCreated().toString() : null);
                    map.put("availabilityDomain", inst.getAvailabilityDomain());
                    map.put("compartmentId", inst.getCompartmentId());
                    map.put("compartmentName", compartmentNameMap.getOrDefault(inst.getCompartmentId(), "unknown"));
                    if (inst.getShapeConfig() != null) {
                        map.put("ocpus", inst.getShapeConfig().getOcpus());
                        map.put("memoryInGBs", inst.getShapeConfig().getMemoryInGBs());
                    }
                    try {
                        map.put("publicIp", ((Future)ipFutures.get(inst.getId())).get(15L, TimeUnit.SECONDS));
                    }
                    catch (Exception e) {
                        map.put("publicIp", null);
                    }
                    result.add(map);
                }
            }
            finally {
                executor.shutdownNow();
            }
            ArrayList arrayList = result;
            return arrayList;
        }
        catch (Exception e) {
            log.error("Failed to list instances: {}", (Object)e.getMessage());
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5b9e\u4f8b\u5217\u8868\u5931\u8d25: " + e.getMessage());
        }
    }

    public void updateInstanceState(String userId, String instanceId, String action, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            InstanceActionRequest request = InstanceActionRequest.builder().instanceId(instanceId).action(action).build();
            client.getComputeClient().instanceAction(request);
            log.info("Instance {} action: {}", (Object)instanceId, (Object)action);
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u64cd\u4f5c\u5931\u8d25: " + e.getMessage());
        }
    }

    public void terminateInstance(String userId, String instanceId, boolean preserveBootVolume, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getComputeClient().terminateInstance(TerminateInstanceRequest.builder().instanceId(instanceId).preserveBootVolume(Boolean.valueOf(preserveBootVolume)).build());
            log.info("Instance terminated: {}, preserveBootVolume={}", (Object)instanceId, (Object)preserveBootVolume);
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u7ec8\u6b62\u5b9e\u4f8b\u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listBootVolumesByInstance(String userId, String instanceId, String region) {
        ArrayList arrayList;
        block12: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance instance = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                List attachments = client.getComputeClient().listBootVolumeAttachments(ListBootVolumeAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).availabilityDomain(instance.getAvailabilityDomain()).build()).getItems();
                ArrayList result = new ArrayList();
                for (BootVolumeAttachment att : attachments) {
                    try {
                        BootVolume vol = client.getBlockstorageClient().getBootVolume(GetBootVolumeRequest.builder().bootVolumeId(att.getBootVolumeId()).build()).getBootVolume();
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put("id", vol.getId());
                        map.put("displayName", vol.getDisplayName());
                        map.put("sizeInGBs", vol.getSizeInGBs());
                        map.put("vpusPerGB", vol.getVpusPerGB());
                        map.put("lifecycleState", vol.getLifecycleState().getValue());
                        map.put("timeCreated", vol.getTimeCreated() != null ? vol.getTimeCreated().toString() : null);
                        result.add(map);
                    }
                    catch (Exception e) {
                        log.warn("Failed to get boot volume {}: {}", (Object)att.getBootVolumeId(), (Object)e.getMessage());
                    }
                }
                arrayList = result;
                if (client == null) break block12;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5f15\u5bfc\u5377\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public void updateBootVolume(String userId, String bootVolumeId, Long sizeInGBs, String displayName, Long vpusPerGB, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateBootVolumeDetails.Builder detailsBuilder = UpdateBootVolumeDetails.builder();
            if (sizeInGBs != null) {
                detailsBuilder.sizeInGBs(sizeInGBs);
            }
            if (displayName != null) {
                detailsBuilder.displayName(displayName);
            }
            if (vpusPerGB != null) {
                detailsBuilder.vpusPerGB(vpusPerGB);
            }
            client.getBlockstorageClient().updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails(detailsBuilder.build()).build());
            log.info("Boot volume updated: {}", (Object)bootVolumeId);
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u66f4\u65b0\u5f15\u5bfc\u5377\u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, Object> getInstanceNetworkDetail(String userId, String instanceId, String region) {
        LinkedHashMap linkedHashMap;
        block16: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                LinkedHashMap result = new LinkedHashMap();
                List attachments = client.getComputeClient().listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build()).getItems();
                ArrayList vnics = new ArrayList();
                for (VnicAttachment att : attachments) {
                    try {
                        Vnic vnic = client.getVirtualNetworkClient().getVnic(GetVnicRequest.builder().vnicId(att.getVnicId()).build()).getVnic();
                        LinkedHashMap<String, Object> vnicInfo = new LinkedHashMap<String, Object>();
                        vnicInfo.put("vnicId", vnic.getId());
                        vnicInfo.put("displayName", vnic.getDisplayName());
                        vnicInfo.put("privateIp", vnic.getPrivateIp());
                        vnicInfo.put("publicIp", vnic.getPublicIp());
                        vnicInfo.put("subnetId", att.getSubnetId());
                        List ipv6List = client.getVirtualNetworkClient().listIpv6s(ListIpv6sRequest.builder().vnicId(vnic.getId()).build()).getItems();
                        vnicInfo.put("ipv6Addresses", ipv6List.stream().map(Ipv6::getIpAddress).toList());
                        ArrayList ipv6Details = new ArrayList();
                        for (Ipv6 ip6 : ipv6List) {
                            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
                            m.put("ipv6Id", ip6.getId());
                            m.put("ipAddress", ip6.getIpAddress());
                            ipv6Details.add(m);
                        }
                        vnicInfo.put("ipv6List", ipv6Details);
                        List privateIps = client.getVirtualNetworkClient().listPrivateIps(ListPrivateIpsRequest.builder().vnicId(vnic.getId()).build()).getItems();
                        ArrayList ipDetails = new ArrayList();
                        for (PrivateIp pip : privateIps) {
                            LinkedHashMap<String, Object> ipInfo = new LinkedHashMap<String, Object>();
                            ipInfo.put("privateIpId", pip.getId());
                            ipInfo.put("privateIpAddress", pip.getIpAddress());
                            ipInfo.put("isPrimary", pip.getIsPrimary());
                            try {
                                PublicIp pubIp = client.getVirtualNetworkClient().getPublicIpByPrivateIpId(GetPublicIpByPrivateIpIdRequest.builder().getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(pip.getId()).build()).build()).getPublicIp();
                                ipInfo.put("publicIpAddress", pubIp.getIpAddress());
                                ipInfo.put("publicIpId", pubIp.getId());
                                ipInfo.put("publicIpLifetime", pubIp.getLifetime().getValue());
                            }
                            catch (Exception ignored) {
                                ipInfo.put("publicIpAddress", null);
                                ipInfo.put("publicIpId", null);
                                ipInfo.put("publicIpLifetime", null);
                            }
                            ipDetails.add(ipInfo);
                        }
                        vnicInfo.put("ipDetails", ipDetails);
                        vnics.add(vnicInfo);
                    }
                    catch (Exception e) {
                        log.warn("Failed to get VNIC detail: {}", (Object)e.getMessage());
                    }
                }
                result.put("vnics", vnics);
                linkedHashMap = result;
                if (client == null) break block16;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5b9e\u4f8b\u7f51\u7edc\u8be6\u60c5\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, String> addIpv6(String userId, String instanceId, String preferredVnicId, String region) {
        Map<String, String> map;
        block23: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Vcn vcn;
                Subnet subnet;
                String vnicId;
                block22: {
                    List attachments = client.getComputeClient().listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build()).getItems();
                    if (attachments.isEmpty()) {
                        throw new OciException("\u672a\u627e\u5230\u5b9e\u4f8b\u7684 VNIC");
                    }
                    VnicAttachment target = (VnicAttachment)attachments.get(0);
                    if (preferredVnicId != null && !preferredVnicId.isBlank()) {
                        for (VnicAttachment att : attachments) {
                            if (!preferredVnicId.equals(att.getVnicId())) continue;
                            target = att;
                            break;
                        }
                    }
                    vnicId = target.getVnicId();
                    String subnetId = target.getSubnetId();
                    subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(subnet.getVcnId()).build()).getVcn();
                    if (vcn.getIpv6CidrBlocks() == null || vcn.getIpv6CidrBlocks().isEmpty()) {
                        log.info("VCN {} has no IPv6 CIDR, adding Oracle-assigned IPv6...", (Object)vcn.getDisplayName());
                        try {
                            client.getVirtualNetworkClient().addIpv6VcnCidr(AddIpv6VcnCidrRequest.builder().vcnId(vcn.getId()).addVcnIpv6CidrDetails(AddVcnIpv6CidrDetails.builder().isOracleGuaAllocationEnabled(Boolean.valueOf(true)).build()).build());
                            Thread.sleep(8000L);
                            vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
                        }
                        catch (BmcException e) {
                            String em;
                            String string = em = e.getMessage() == null ? "" : e.getMessage();
                            if (!em.contains("already exists") && !em.contains("already has")) {
                                throw new OciException(this.tag(ociUser) + "VCN \u6dfb\u52a0 IPv6 CIDR \u5931\u8d25: " + this.extractOciErrorMessage(e));
                            }
                            vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
                        }
                    }
                    if (subnet.getIpv6CidrBlocks() == null || subnet.getIpv6CidrBlocks().isEmpty()) {
                        String vcnIpv6Cidr;
                        log.info("Subnet {} has no IPv6 CIDR, adding...", (Object)subnet.getDisplayName());
                        String string = vcnIpv6Cidr = vcn.getIpv6CidrBlocks() != null && !vcn.getIpv6CidrBlocks().isEmpty() ? (String)vcn.getIpv6CidrBlocks().get(0) : null;
                        if (vcnIpv6Cidr == null) {
                            throw new OciException("VCN \u6ca1\u6709 IPv6 CIDR\uff0c\u65e0\u6cd5\u4e3a\u5b50\u7f51\u6dfb\u52a0 IPv6\u3002\u8bf7\u5148\u5728OCI\u63a7\u5236\u53f0\u624b\u52a8\u4e3aVCN\u542f\u7528IPv6\u3002");
                        }
                        String subnetIpv6Cidr = vcnIpv6Cidr.replaceAll("/\\d+$", "/64");
                        try {
                            client.getVirtualNetworkClient().updateSubnet(UpdateSubnetRequest.builder().subnetId(subnetId).updateSubnetDetails(UpdateSubnetDetails.builder().ipv6CidrBlocks(List.of(subnetIpv6Cidr)).build()).build());
                            Thread.sleep(3000L);
                        }
                        catch (BmcException e) {
                            String em;
                            String string2 = em = e.getMessage() == null ? "" : e.getMessage();
                            if (em.contains("already exists") || em.contains("already has")) break block22;
                            throw new OciException(this.tag(ociUser) + "\u5b50\u7f51\u6dfb\u52a0 IPv6 CIDR \u5931\u8d25: " + this.extractOciErrorMessage(e));
                        }
                    }
                }
                this.ensureIpv6InternetRoute(client, vcn, subnet);
                Ipv6 ipv6 = client.getVirtualNetworkClient().createIpv6(CreateIpv6Request.builder().createIpv6Details(CreateIpv6Details.builder().vnicId(vnicId).build()).build()).getIpv6();
                map = Map.of("ipv6Address", ipv6.getIpAddress());
                if (client == null) break block23;
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
                    throw new OciException(this.tag(ociUser) + "\u6dfb\u52a0 IPv6 \u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u6dfb\u52a0 IPv6 \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public void removeIpv6(String userId, String ipv6Id, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        if (ipv6Id == null || ipv6Id.isBlank()) {
            throw new OciException("ipv6Id \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().deleteIpv6(DeleteIpv6Request.builder().ipv6Id(ipv6Id).build());
            log.info("IPv6 unassigned (deleted): {}", (Object)ipv6Id);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u53d6\u6d88\u5206\u914d IPv6 \u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u53d6\u6d88\u5206\u914d IPv6 \u5931\u8d25: " + e.getMessage());
        }
    }

    private void ensureIpv6InternetRoute(OciClientService client, Vcn vcn, Subnet subnet) {
        boolean hasIpv6DefaultRoute;
        String routeTableId;
        InternetGateway igw;
        List igws = client.getVirtualNetworkClient().listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(client.getCompartmentId()).vcnId(vcn.getId()).build()).getItems();
        if (igws == null || igws.isEmpty()) {
            log.info("VCN {} has no Internet Gateway, creating one...", (Object)vcn.getDisplayName());
            igw = client.createInternetGateway(vcn);
        } else {
            igw = igws.stream().filter(gw -> Boolean.TRUE.equals(gw.getIsEnabled())).findFirst().orElse((InternetGateway)igws.get(0));
        }
        String string = routeTableId = subnet.getRouteTableId() != null ? subnet.getRouteTableId() : vcn.getDefaultRouteTableId();
        if (routeTableId == null) {
            log.warn("No route table found for subnet {}, skip IPv6 default route setup", (Object)subnet.getId());
            return;
        }
        RouteTable routeTable = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(routeTableId).build()).getRouteTable();
        ArrayList<RouteRule> rules = new ArrayList<RouteRule>();
        if (routeTable.getRouteRules() != null) {
            rules.addAll(routeTable.getRouteRules());
        }
        if (!(hasIpv6DefaultRoute = rules.stream().anyMatch(rule -> "::/0".equals(rule.getDestination()) && RouteRule.DestinationType.CidrBlock.equals((Object)rule.getDestinationType())))) {
            rules.add(RouteRule.builder().destination("::/0").destinationType(RouteRule.DestinationType.CidrBlock).networkEntityId(igw.getId()).description("oci-worker auto add IPv6 default route").build());
            client.getVirtualNetworkClient().updateRouteTable(UpdateRouteTableRequest.builder().rtId(routeTableId).updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build()).build());
            log.info("Added IPv6 default route (::/0 -> IGW) to route table {}", (Object)routeTableId);
        }
    }

    public Map<String, String> createReservedIp(String userId, String displayName, String region) {
        Map<String, String> map;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                CreatePublicIpDetails.Builder builder = CreatePublicIpDetails.builder().compartmentId(client.getCompartmentId()).lifetime(CreatePublicIpDetails.Lifetime.Reserved);
                if (displayName != null && !displayName.isBlank()) {
                    builder.displayName(displayName);
                }
                PublicIp reservedIp = client.getVirtualNetworkClient().createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(builder.build()).build()).getPublicIp();
                map = Map.of("publicIpId", reservedIp.getId(), "ipAddress", reservedIp.getIpAddress());
                if (client == null) break block11;
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
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u9884\u7559IP\u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u9884\u7559IP\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public List<Map<String, Object>> listReservedIps(String userId, String region) {
        List<Map<String, Object>> list;
        block9: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                List publicIps = client.getVirtualNetworkClient().listPublicIps(ListPublicIpsRequest.builder().compartmentId(client.getCompartmentId()).scope(ListPublicIpsRequest.Scope.Region).lifetime(ListPublicIpsRequest.Lifetime.Reserved).build()).getItems();
                list = publicIps.stream().map(ip -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("id", ip.getId());
                    map.put("ipAddress", ip.getIpAddress());
                    map.put("displayName", ip.getDisplayName());
                    map.put("lifecycleState", ip.getLifecycleState().getValue());
                    map.put("lifetime", ip.getLifetime().getValue());
                    map.put("assignedEntityId", ip.getAssignedEntityId());
                    map.put("privateIpId", ip.getPrivateIpId());
                    map.put("isAssigned", ip.getAssignedEntityId() != null);
                    map.put("publicIpPoolId", ip.getPublicIpPoolId());
                    map.put("timeCreated", ip.getTimeCreated() != null ? ip.getTimeCreated().toString() : null);
                    return map;
                }).collect(Collectors.toList());
                if (client == null) break block9;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u9884\u7559 IP \u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return list;
    }

    public void deleteReservedIp(String userId, String publicIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(publicIpId).build());
            log.info("Reserved IP deleted: {}", (Object)publicIpId);
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u9884\u7559 IP \u5931\u8d25: " + e.getMessage());
        }
    }

    public void assignReservedIp(String userId, String publicIpId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            List attachments = client.getComputeClient().listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build()).getItems();
            if (attachments.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230\u5b9e\u4f8b\u7684 VNIC");
            }
            String vnicId = ((VnicAttachment)attachments.get(0)).getVnicId();
            String subnetId = ((VnicAttachment)attachments.get(0)).getSubnetId();
            PrivateIp secondaryPip = client.getVirtualNetworkClient().createPrivateIp(CreatePrivateIpRequest.builder().createPrivateIpDetails(CreatePrivateIpDetails.builder().vnicId(vnicId).displayName("privateip" + System.currentTimeMillis()).build()).build()).getPrivateIp();
            client.getVirtualNetworkClient().updatePublicIp(UpdatePublicIpRequest.builder().publicIpId(publicIpId).updatePublicIpDetails(UpdatePublicIpDetails.builder().privateIpId(secondaryPip.getId()).build()).build());
            log.info("Reserved IP {} assigned to secondary private IP {} on instance {}", new Object[]{publicIpId, secondaryPip.getIpAddress(), instanceId});
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u7ed1\u5b9a\u9884\u7559IP\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u7ed1\u5b9a\u9884\u7559IP\u5931\u8d25: " + e.getMessage());
        }
    }

    public void unassignReservedIp(String userId, String publicIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            PublicIp pubIp = client.getVirtualNetworkClient().getPublicIp(GetPublicIpRequest.builder().publicIpId(publicIpId).build()).getPublicIp();
            String privateIpId = pubIp.getPrivateIpId();
            client.getVirtualNetworkClient().updatePublicIp(UpdatePublicIpRequest.builder().publicIpId(publicIpId).updatePublicIpDetails(UpdatePublicIpDetails.builder().privateIpId("").build()).build());
            if (privateIpId != null) {
                try {
                    PrivateIp pip = client.getVirtualNetworkClient().getPrivateIp(GetPrivateIpRequest.builder().privateIpId(privateIpId).build()).getPrivateIp();
                    if (!Boolean.TRUE.equals(pip.getIsPrimary())) {
                        client.getVirtualNetworkClient().deletePrivateIp(DeletePrivateIpRequest.builder().privateIpId(privateIpId).build());
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            log.info("Reserved IP {} unassigned", (Object)publicIpId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u89e3\u7ed1\u9884\u7559IP\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u89e3\u7ed1\u9884\u7559IP\u5931\u8d25: " + e.getMessage());
        }
    }

    public Object updateInstance(String userId, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs, String region) {
        try {
            return this.updateInstanceOnce(userId, instanceId, displayName, shape, ocpus, memoryInGBs, region);
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            if (InstanceService.isShapeEditRequest((String)shape, (Float)ocpus, (Float)memoryInGBs) && ShapeEditTaskManager.isOutOfStock((Throwable)e)) {
                ShapeEditTaskStatus status = this.shapeEditTaskManager.startTask(userId, instanceId, region, shape, ocpus, memoryInGBs, () -> this.updateInstanceOnce(userId, instanceId, displayName, shape, ocpus, memoryInGBs, region));
                return status;
            }
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            String prefix = ociUser == null ? "" : this.tag(ociUser);
            throw new OciException(prefix + "\u4fee\u6539\u5b9e\u4f8b\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            String prefix = ociUser == null ? "" : this.tag(ociUser);
            throw new OciException(prefix + "\u4fee\u6539\u5b9e\u4f8b\u5931\u8d25: " + e.getMessage());
        }
    }

    private Map<String, Object> updateInstanceOnce(String userId, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Map map = this.updateInstanceOnce(client, ociUser, instanceId, displayName, shape, ocpus, memoryInGBs);
            return map;
        }
    }

    private Map<String, Object> updateInstanceOnce(OciClientService client, OciUser ociUser, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs) {
        Instance current = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
        String targetShape = shape != null && !shape.isBlank() ? shape.trim() : current.getShape();
        List compatible = client.getShapes(current.getAvailabilityDomain(), current.getImageId());
        Shape shapeMeta = InstanceService.findShapeMeta((List)compatible, (String)targetShape);
        if (shapeMeta == null) {
            throw new OciException(this.tag(ociUser) + "\u76ee\u6807 Shape \u4e0e\u5f53\u524d\u5b9e\u4f8b\u955c\u50cf\u4e0d\u517c\u5bb9: " + targetShape);
        }
        boolean flex = InstanceService.isFlexibleShape((String)targetShape);
        Float useOcpus = ocpus;
        Float useMemory = memoryInGBs;
        if (flex) {
            if (useOcpus == null && current.getShapeConfig() != null) {
                useOcpus = current.getShapeConfig().getOcpus();
            }
            if (useMemory == null && current.getShapeConfig() != null) {
                useMemory = current.getShapeConfig().getMemoryInGBs();
            }
            InstanceService.validateFlexResources((Shape)shapeMeta, (Float)useOcpus, (Float)useMemory);
        } else if (ocpus != null || memoryInGBs != null) {
            throw new OciException(this.tag(ociUser) + "\u975e Flex Shape \u4ec5\u53ef\u66f4\u6362\u5f62\u72b6\uff0c\u4e0d\u80fd\u5355\u72ec\u8c03\u6574 OCPU/\u5185\u5b58");
        }
        UpdateInstanceDetails.Builder detailsBuilder = UpdateInstanceDetails.builder();
        if (displayName != null && !displayName.isBlank()) {
            detailsBuilder.displayName(displayName);
        }
        if (shape != null && !shape.isBlank() && !shape.trim().equals(current.getShape())) {
            detailsBuilder.shape(shape.trim());
        }
        if (flex && (useOcpus != null || useMemory != null)) {
            UpdateInstanceShapeConfigDetails.Builder shapeBuilder = UpdateInstanceShapeConfigDetails.builder();
            if (useOcpus != null) {
                shapeBuilder.ocpus(useOcpus);
            }
            if (useMemory != null) {
                shapeBuilder.memoryInGBs(useMemory);
            }
            detailsBuilder.shapeConfig(shapeBuilder.build());
        }
        Instance updated = client.getComputeClient().updateInstance(UpdateInstanceRequest.builder().instanceId(instanceId).updateInstanceDetails(detailsBuilder.build()).build()).getInstance();
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("instanceId", updated.getId());
        result.put("name", updated.getDisplayName());
        result.put("shape", updated.getShape());
        if (updated.getShapeConfig() != null) {
            result.put("ocpus", updated.getShapeConfig().getOcpus());
            result.put("memoryInGBs", updated.getShapeConfig().getMemoryInGBs());
        }
        return result;
    }

    private static boolean isShapeEditRequest(String shape, Float ocpus, Float memoryInGBs) {
        return shape != null && !shape.isBlank() || ocpus != null || memoryInGBs != null;
    }

    public Map<String, Object> forceA2FlexToA1Flex(String userId, String instanceId, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        block16: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance current = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                String actualShape = current.getShape();
                if (!"VM.Standard.A2.Flex".equals(actualShape)) {
                    throw new OciException(this.tag(ociUser) + "\u5f53\u524d\u5b9e\u4f8b Shape \u4e0d\u662f VM.Standard.A2.Flex\uff0c\u65e0\u6cd5\u6267\u884c\u5f3a\u6539\u3002\u8bf7\u68c0\u67e5\u5f53\u524d Shape\uff0c\u5b9e\u9645\u4e3a\uff1a" + actualShape);
                }
                Float ocpus = null;
                Float memoryInGBs = null;
                if (current.getShapeConfig() != null) {
                    ocpus = current.getShapeConfig().getOcpus();
                    memoryInGBs = current.getShapeConfig().getMemoryInGBs();
                }
                if (ocpus == null || memoryInGBs == null) {
                    throw new OciException(this.tag(ociUser) + "\u65e0\u6cd5\u8bfb\u53d6\u5f53\u524d Flex \u7684 OCPU/\u5185\u5b58\u914d\u7f6e\uff0c\u8bf7\u68c0\u67e5\u540e\u91cd\u8bd5");
                }
                List compatible = client.getShapes(current.getAvailabilityDomain(), current.getImageId());
                Shape a1Meta = InstanceService.findShapeMeta((List)compatible, (String)"VM.Standard.A1.Flex");
                if (a1Meta != null) {
                    InstanceService.validateFlexResources((Shape)a1Meta, (Float)ocpus, (Float)memoryInGBs);
                }
                log.warn("{} force A2\u2192A1 instanceId={} ocpus={} memoryInGBs={}", new Object[]{this.tag(ociUser), instanceId, ocpus, memoryInGBs});
                Instance updated = client.getComputeClient().updateInstance(UpdateInstanceRequest.builder().instanceId(instanceId).updateInstanceDetails(UpdateInstanceDetails.builder().shape("VM.Standard.A1.Flex").shapeConfig(UpdateInstanceShapeConfigDetails.builder().ocpus(ocpus).memoryInGBs(memoryInGBs).build()).build()).build()).getInstance();
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("instanceId", updated.getId());
                result.put("name", updated.getDisplayName());
                result.put("shape", updated.getShape());
                if (updated.getShapeConfig() != null) {
                    result.put("ocpus", updated.getShapeConfig().getOcpus());
                    result.put("memoryInGBs", updated.getShapeConfig().getMemoryInGBs());
                }
                this.notifyForceA2ToA1Success(updated);
                linkedHashMap = result;
                if (client == null) break block16;
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
                    this.notifyForceA2ToA1Failure(ociUser, region);
                    throw e;
                }
                catch (BmcException e) {
                    this.notifyForceA2ToA1Failure(ociUser, region);
                    throw new OciException(this.tag(ociUser) + "A2 \u5f3a\u6539 A1 \u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    this.notifyForceA2ToA1Failure(ociUser, region);
                    throw new OciException(this.tag(ociUser) + "A2 \u5f3a\u6539 A1 \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    private void notifyForceA2ToA1Success(Instance updated) {
        String nowShape = updated.getShape() != null ? updated.getShape() : "VM.Standard.A1.Flex";
        String html = "\ud83c\udf89 <b>\u5b9e\u4f8b\u5f62\u72b6\u4fee\u6539\u6210\u529f\uff01</b>\n\n\u539fShape\uff1a<code>VM.Standard.A2.Flex</code>\n\u73b0Shape\uff1a<code>" + nowShape + "</code>\n\u516c\u7f51IP\u4ee5\u53ca\u5bc6\u7801\u65e0\u53d8\u5316\n\u5df2\u6210\u529f\u5b9e\u73b0A2\u27a1A1";
        this.notificationService.sendHtmlWithType("instance", html);
    }

    private void notifyForceA2ToA1Failure(OciUser ociUser, String region) {
        String username = ociUser.getUsername() != null ? ociUser.getUsername() : "-";
        String reg = region != null && !region.isBlank() ? region.trim() : "-";
        String html = "\ud83d\ude1f <b>\u5b9e\u4f8b\u5f62\u72b6\u4fee\u6539\u5931\u8d25\uff01</b>\n\n\u79df\u6237\uff1a" + username + "\n\u533a\u57df\uff1a" + reg + "\nA2\u27a1A1\u4fee\u6539\u5931\u8d25\uff0c\u53ef\u518d\u6b21\u5c1d\u8bd5";
        this.notificationService.sendHtmlWithType("instance", html);
    }

    public List<Map<String, Object>> listAvailableShapes(String userId, String region) {
        ArrayList<Map> arrayList;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                List ads = client.getAvailabilityDomains();
                LinkedHashSet<String> seen = new LinkedHashSet<String>();
                ArrayList<Map> result = new ArrayList<Map>();
                for (AvailabilityDomain ad : ads) {
                    for (Shape s : client.getShapes(ad.getName())) {
                        if (!seen.add(s.getShape())) continue;
                        result.add(InstanceService.shapeToMap((Shape)s));
                    }
                }
                arrayList = result;
                if (client == null) break block11;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u53ef\u7528 Shape \u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public List<Map<String, Object>> listShapesForInstance(String userId, String instanceId, String region) {
        ArrayList<Map> arrayList;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance inst = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                LinkedHashSet<String> seen = new LinkedHashSet<String>();
                ArrayList<Map> result = new ArrayList<Map>();
                for (Shape s : client.getShapes(inst.getAvailabilityDomain(), inst.getImageId())) {
                    if (!seen.add(s.getShape())) continue;
                    result.add(InstanceService.shapeToMap((Shape)s));
                }
                result.sort(Comparator.comparing(m -> String.valueOf(m.get("shape"))));
                arrayList = result;
                if (client == null) break block11;
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
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5b9e\u4f8b\u53ef\u7528 Shape \u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5b9e\u4f8b\u53ef\u7528 Shape \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    private static boolean isFlexibleShape(String shapeName) {
        return shapeName != null && shapeName.contains("Flex");
    }

    private static Shape findShapeMeta(List<Shape> shapes, String shapeName) {
        if (shapeName == null) {
            return null;
        }
        for (Shape s : shapes) {
            if (!shapeName.equals(s.getShape())) continue;
            return s;
        }
        return null;
    }

    private static Map<String, Object> shapeToMap(Shape shape) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        String name = shape.getShape();
        map.put("shape", name);
        map.put("processorDescription", shape.getProcessorDescription());
        boolean flex = InstanceService.isFlexibleShape((String)name);
        map.put("isFlexible", flex);
        Float ocpuMin = null;
        Float ocpuMax = null;
        if (shape.getOcpuOptions() != null) {
            ocpuMin = shape.getOcpuOptions().getMin();
            ocpuMax = shape.getOcpuOptions().getMax();
        }
        if (ocpuMin == null && shape.getOcpus() != null) {
            ocpuMin = shape.getOcpus();
            ocpuMax = shape.getOcpus();
        }
        map.put("ocpuMin", ocpuMin);
        map.put("ocpuMax", ocpuMax);
        map.put("ocpus", shape.getOcpus());
        Float memMin = null;
        Float memMax = null;
        if (shape.getMemoryOptions() != null) {
            memMin = shape.getMemoryOptions().getMinInGBs();
            memMax = shape.getMemoryOptions().getMaxInGBs();
        }
        if (memMin == null && shape.getMemoryInGBs() != null) {
            memMin = shape.getMemoryInGBs();
            memMax = shape.getMemoryInGBs();
        }
        map.put("memoryMinInGBs", memMin);
        map.put("memoryMaxInGBs", memMax);
        map.put("memoryInGBs", shape.getMemoryInGBs());
        InstanceService.applyFlexLimitsOverride(map, (String)name);
        return map;
    }

    private static void applyFlexLimitsOverride(Map<String, Object> map, String shapeName) {
        ShapeFlexLimitsUtil.FlexLimits lim = ShapeFlexLimitsUtil.forShape((String)shapeName);
        if (lim == null || !Boolean.TRUE.equals(map.get("isFlexible"))) {
            return;
        }
        map.put("ocpuMax", Float.valueOf(lim.maxOcpus()));
        map.put("memoryMaxInGBs", Float.valueOf(lim.maxMemoryGb()));
        if (map.get("ocpuMin") == null) {
            map.put("ocpuMin", Float.valueOf(1.0f));
        }
        if (map.get("memoryMinInGBs") == null) {
            map.put("memoryMinInGBs", Float.valueOf(lim.defaultMemoryGb()));
        }
    }

    private static void validateFlexResources(Shape shapeMeta, Float ocpus, Float memoryInGBs) {
        if (ocpus == null || memoryInGBs == null) {
            throw new OciException("Flex Shape \u987b\u540c\u65f6\u6307\u5b9a OCPU \u4e0e\u5185\u5b58 (GB)");
        }
        Float oMin = shapeMeta.getOcpuOptions() != null ? shapeMeta.getOcpuOptions().getMin() : shapeMeta.getOcpus();
        Float oMax = shapeMeta.getOcpuOptions() != null ? shapeMeta.getOcpuOptions().getMax() : shapeMeta.getOcpus();
        Float mMin = shapeMeta.getMemoryOptions() != null ? shapeMeta.getMemoryOptions().getMinInGBs() : shapeMeta.getMemoryInGBs();
        Float mMax = shapeMeta.getMemoryOptions() != null ? shapeMeta.getMemoryOptions().getMaxInGBs() : shapeMeta.getMemoryInGBs();
        ShapeFlexLimitsUtil.FlexLimits fixed = ShapeFlexLimitsUtil.forShape((String)shapeMeta.getShape());
        if (fixed != null) {
            oMax = Float.valueOf(fixed.maxOcpus());
            mMax = Float.valueOf(fixed.maxMemoryGb());
            if (oMin == null) {
                oMin = Float.valueOf(1.0f);
            }
            if (mMin == null) {
                mMin = Float.valueOf(fixed.defaultMemoryGb());
            }
        }
        if (oMin != null && ocpus.floatValue() < oMin.floatValue()) {
            throw new OciException(String.format("OCPU \u4e0d\u80fd\u5c0f\u4e8e %s\uff08\u8be5 Shape \u4e0b\u9650\uff09", InstanceService.trimFloat((Float)oMin)));
        }
        if (oMax != null && ocpus.floatValue() > oMax.floatValue()) {
            throw new OciException(String.format("OCPU \u4e0d\u80fd\u5927\u4e8e %s\uff08\u8be5 Shape \u4e0a\u9650\uff09", InstanceService.trimFloat((Float)oMax)));
        }
        if (mMin != null && memoryInGBs.floatValue() < mMin.floatValue()) {
            throw new OciException(String.format("\u5185\u5b58\u4e0d\u80fd\u5c0f\u4e8e %s GB\uff08\u8be5 Shape \u4e0b\u9650\uff09", InstanceService.trimFloat((Float)mMin)));
        }
        if (mMax != null && memoryInGBs.floatValue() > mMax.floatValue()) {
            throw new OciException(String.format("\u5185\u5b58\u4e0d\u80fd\u5927\u4e8e %s GB\uff08\u8be5 Shape \u4e0a\u9650\uff09", InstanceService.trimFloat((Float)mMax)));
        }
    }

    private static String trimFloat(Float v) {
        if (v == null) {
            return "";
        }
        if ((double)v.floatValue() == Math.floor(v.floatValue())) {
            return String.valueOf(v.intValue());
        }
        return String.valueOf(v);
    }

    public List<Map<String, Object>> listBlockVolumesByInstance(String userId, String instanceId, String region) {
        ArrayList<Map> arrayList;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            if (instanceId == null || instanceId.isBlank()) {
                throw new OciException("instanceId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance instance = this.getInstanceOrThrow(client, instanceId);
                String compartmentId = instance.getCompartmentId();
                String ad = instance.getAvailabilityDomain();
                List attachments = this.listActiveVolumeAttachments(client, compartmentId, ad, instanceId);
                ArrayList<Map> result = new ArrayList<Map>();
                for (VolumeAttachment att : attachments) {
                    String volumeId = att.getVolumeId();
                    if (volumeId == null) continue;
                    try {
                        Volume vol = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                        result.add(InstanceService.blockVolumeRow((VolumeAttachment)att, (Volume)vol));
                    }
                    catch (Exception e) {
                        log.warn("Failed to get block volume {}: {}", (Object)volumeId, (Object)e.getMessage());
                    }
                }
                arrayList = result;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5757\u5b58\u50a8\u5377\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public List<Map<String, Object>> listUnattachedBlockVolumesForInstance(String userId, String instanceId, String region) {
        ArrayList<Map<String, Object>> arrayList;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            if (instanceId == null || instanceId.isBlank()) {
                throw new OciException("instanceId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                ListVolumesResponse resp;
                Instance instance = this.getInstanceOrThrow(client, instanceId);
                String compartmentId = instance.getCompartmentId();
                String ad = instance.getAvailabilityDomain();
                HashSet<String> attachedVolumeIds = new HashSet<String>();
                for (VolumeAttachment att : this.listActiveVolumeAttachments(client, compartmentId, ad, null)) {
                    if (att.getVolumeId() == null) continue;
                    attachedVolumeIds.add(att.getVolumeId());
                }
                ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                HashSet<String> seen = new HashSet<String>();
                String page = null;
                do {
                    resp = client.getBlockstorageClient().listVolumes(ListVolumesRequest.builder().compartmentId(compartmentId).availabilityDomain(ad).lifecycleState(Volume.LifecycleState.Available).page(page).build());
                    for (Volume v : resp.getItems()) {
                        if (!seen.add(v.getId()) || attachedVolumeIds.contains(v.getId())) continue;
                        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                        m.put("id", v.getId());
                        m.put("displayName", v.getDisplayName());
                        m.put("sizeInGBs", v.getSizeInGBs());
                        m.put("vpusPerGB", v.getVpusPerGB());
                        m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
                        m.put("availabilityDomain", v.getAvailabilityDomain());
                        result.add(m);
                    }
                } while ((page = resp.getOpcNextPage()) != null);
                arrayList = result;
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u53ef\u6302\u8f7d\u5757\u5b58\u50a8\u5377\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public Map<String, Object> createBlockVolumeAndAttach(String userId, String instanceId, String displayName, Long sizeInGBs, Long vpusPerGB, String device, String region) {
        Map map;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            InstanceService.validateBlockVolumeSize((Long)sizeInGBs);
            long vpus = InstanceService.resolveVpusPerGb((Long)vpusPerGB);
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance instance = this.getInstanceOrThrow(client, instanceId);
                String compartmentId = instance.getCompartmentId();
                String ad = instance.getAvailabilityDomain();
                CreateVolumeDetails createDetails = CreateVolumeDetails.builder().compartmentId(compartmentId).availabilityDomain(ad).displayName(displayName != null && !displayName.isBlank() ? displayName.trim() : "block-volume").sizeInGBs(sizeInGBs).vpusPerGB(Long.valueOf(vpus)).build();
                Volume created = client.getBlockstorageClient().createVolume(CreateVolumeRequest.builder().createVolumeDetails(createDetails).build()).getVolume();
                Volume available = this.waitVolumeUntilAvailable(client, created.getId());
                VolumeAttachment attachment = this.attachVolumeToInstance(client, instanceId, available.getId(), device);
                Map out = InstanceService.blockVolumeRow((VolumeAttachment)attachment, (Volume)available);
                out.put("message", "\u5757\u5b58\u50a8\u5377\u5df2\u521b\u5efa\u5e76\u63d0\u4ea4\u6302\u8f7d");
                map = out;
                if (client == null) break block11;
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
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u5e76\u6302\u8f7d\u5757\u5b58\u50a8\u5377\u88ab\u4e2d\u65ad");
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u5e76\u6302\u8f7d\u5757\u5b58\u50a8\u5377\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public Map<String, Object> attachBlockVolume(String userId, String instanceId, String volumeId, String device, String region) {
        Map map;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            if (volumeId == null || volumeId.isBlank()) {
                throw new OciException("volumeId \u4e0d\u80fd\u4e3a\u7a7a");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Instance instance = this.getInstanceOrThrow(client, instanceId);
                Volume vol = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                if (!Objects.equals(vol.getAvailabilityDomain(), instance.getAvailabilityDomain())) {
                    throw new OciException("\u5757\u5b58\u50a8\u5377\u4e0e\u5b9e\u4f8b\u987b\u5728\u540c\u4e00\u53ef\u7528\u57df (Availability Domain)");
                }
                if (!Objects.equals(vol.getCompartmentId(), instance.getCompartmentId())) {
                    throw new OciException("\u5757\u5b58\u50a8\u5377\u4e0e\u5b9e\u4f8b\u987b\u5728\u540c\u4e00\u533a\u95f4 (Compartment)");
                }
                if (vol.getLifecycleState() != Volume.LifecycleState.Available) {
                    throw new OciException("\u5757\u5b58\u50a8\u5377\u987b\u4e3a AVAILABLE \u72b6\u6001\u65b9\u53ef\u6302\u8f7d\uff0c\u5f53\u524d: " + (vol.getLifecycleState() != null ? vol.getLifecycleState().getValue() : "unknown"));
                }
                VolumeAttachment attachment = this.attachVolumeToInstance(client, instanceId, volumeId, device);
                Volume refreshed = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                map = InstanceService.blockVolumeRow((VolumeAttachment)attachment, (Volume)refreshed);
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u6302\u8f7d\u5757\u5b58\u50a8\u5377\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public void detachBlockVolume(String userId, String volumeAttachmentId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        if (volumeAttachmentId == null || volumeAttachmentId.isBlank()) {
            throw new OciException("volumeAttachmentId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getComputeClient().detachVolume(DetachVolumeRequest.builder().volumeAttachmentId(volumeAttachmentId).build());
            log.info("Block volume detached: attachment {}", (Object)volumeAttachmentId);
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5378\u8f7d\u5757\u5b58\u50a8\u5377\u5931\u8d25: " + e.getMessage());
        }
    }

    public void updateBlockVolume(String userId, String volumeId, Long sizeInGBs, String displayName, Long vpusPerGB, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        if (volumeId == null || volumeId.isBlank()) {
            throw new OciException("volumeId \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateVolumeDetails.Builder detailsBuilder = UpdateVolumeDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                detailsBuilder.displayName(displayName.trim());
            }
            if (sizeInGBs != null) {
                InstanceService.validateBlockVolumeSize((Long)sizeInGBs);
                detailsBuilder.sizeInGBs(sizeInGBs);
            }
            if (vpusPerGB != null) {
                detailsBuilder.vpusPerGB(Long.valueOf(InstanceService.resolveVpusPerGb((Long)vpusPerGB)));
            }
            if (displayName == null && sizeInGBs == null && vpusPerGB == null) {
                throw new OciException("\u81f3\u5c11\u63d0\u4f9b displayName\u3001sizeInGBs \u6216 vpusPerGB \u4e4b\u4e00");
            }
            client.getBlockstorageClient().updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails(detailsBuilder.build()).build());
            log.info("Block volume updated: {}", (Object)volumeId);
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u66f4\u65b0\u5757\u5b58\u50a8\u5377\u5931\u8d25: " + e.getMessage());
        }
    }

    private Instance getInstanceOrThrow(OciClientService client, String instanceId) {
        return client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
    }

    private List<VolumeAttachment> listActiveVolumeAttachments(OciClientService client, String compartmentId, String availabilityDomain, String instanceId) {
        ListVolumeAttachmentsResponse resp;
        ArrayList<VolumeAttachment> all = new ArrayList<VolumeAttachment>();
        String page = null;
        do {
            ListVolumeAttachmentsRequest.Builder b = ListVolumeAttachmentsRequest.builder().compartmentId(compartmentId).availabilityDomain(availabilityDomain);
            if (instanceId != null && !instanceId.isBlank()) {
                b.instanceId(instanceId);
            }
            resp = client.getComputeClient().listVolumeAttachments(b.page(page).build());
            for (VolumeAttachment a : resp.getItems()) {
                if (a.getLifecycleState() == VolumeAttachment.LifecycleState.Detached) continue;
                all.add(a);
            }
        } while ((page = resp.getOpcNextPage()) != null);
        return all;
    }

    private VolumeAttachment attachVolumeToInstance(OciClientService client, String instanceId, String volumeId, String device) {
        try {
            Constructor ctor = AttachVolumeDetails.class.getDeclaredConstructor(String.class, String.class, String.class, Boolean.class, Boolean.class, String.class);
            ctor.setAccessible(true);
            String devicePath = device != null && !device.isBlank() ? device.trim() : null;
            AttachVolumeDetails details = (AttachVolumeDetails)ctor.newInstance(devicePath, null, instanceId, null, null, volumeId);
            return client.getComputeClient().attachVolume(AttachVolumeRequest.builder().attachVolumeDetails(details).build()).getVolumeAttachment();
        }
        catch (OciException e) {
            throw e;
        }
        catch (ReflectiveOperationException e) {
            throw new OciException("\u6784\u5efa AttachVolumeDetails \u5931\u8d25: " + e.getMessage());
        }
    }

    private Volume waitVolumeUntilAvailable(OciClientService client, String volumeId) throws InterruptedException {
        for (int i = 0; i < 120; ++i) {
            Volume v = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
            Volume.LifecycleState st = v.getLifecycleState();
            if (st == Volume.LifecycleState.Available) {
                return v;
            }
            if (st == Volume.LifecycleState.Faulty || st == Volume.LifecycleState.Terminated) {
                throw new OciException("\u5757\u5b58\u50a8\u5377\u72b6\u6001\u5f02\u5e38: " + (st != null ? st.getValue() : "unknown"));
            }
            Thread.sleep(1000L);
        }
        throw new OciException("\u7b49\u5f85\u5757\u5b58\u50a8\u5377\u8fdb\u5165 AVAILABLE \u72b6\u6001\u8d85\u65f6\uff08\u6700\u957f 120 \u79d2\uff09");
    }

    private static Map<String, Object> blockVolumeRow(VolumeAttachment att, Volume vol) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("attachmentId", att.getId());
        map.put("volumeId", vol.getId());
        map.put("displayName", vol.getDisplayName());
        map.put("sizeInGBs", vol.getSizeInGBs());
        map.put("vpusPerGB", vol.getVpusPerGB());
        map.put("device", att.getDevice());
        map.put("volumeLifecycleState", vol.getLifecycleState() != null ? vol.getLifecycleState().getValue() : null);
        map.put("attachmentLifecycleState", att.getLifecycleState() != null ? att.getLifecycleState().getValue() : null);
        map.put("timeCreated", vol.getTimeCreated() != null ? vol.getTimeCreated().toString() : null);
        map.put("availabilityDomain", vol.getAvailabilityDomain());
        map.put("isHydrated", vol.getIsHydrated());
        return map;
    }

    private static void validateBlockVolumeSize(Long sizeInGBs) {
        if (sizeInGBs == null || sizeInGBs < 50L) {
            throw new OciException("\u5757\u5b58\u50a8\u5377\u5bb9\u91cf\u987b\u81f3\u5c11 50 GB\uff08OCI CreateVolumeDetails.sizeInGBs\uff09");
        }
        if (sizeInGBs > 32768L) {
            throw new OciException("\u5757\u5b58\u50a8\u5377\u5bb9\u91cf\u4e0d\u80fd\u8d85\u8fc7 32768 GB");
        }
    }

    private static long resolveVpusPerGb(Long vpusPerGB) {
        if (vpusPerGB == null) {
            return 10L;
        }
        long v = vpusPerGB;
        if (v == 0L || v == 10L || v == 20L) {
            return v;
        }
        if (v >= 30L && v <= 120L && v % 10L == 0L) {
            return v;
        }
        throw new OciException("vpusPerGB \u987b\u4e3a 0\u300110\u300120 \u6216 30\uff5e120\uff08Ultra High \u6863\u6b65\u8fdb 10\uff09\uff0c\u89c1 OCI Block Volume \u6027\u80fd\u6863\u4f4d\u6587\u6863");
    }

    private String extractOciErrorMessage(BmcException e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            return "OCI \u8c03\u7528\u5931\u8d25\uff08\u65e0\u8be6\u7ec6\u4fe1\u606f\uff09";
        }
        if (msg.contains("LimitExceeded")) {
            return "\u5df2\u8d85\u51fa\u514d\u8d39\u8d26\u6237\u9650\u5236\uff0c\u65e0\u6cd5\u521b\u5efa\u66f4\u591a\u8d44\u6e90\u3002\u8bf7\u5728OCI\u63a7\u5236\u53f0\u7533\u8bf7\u63d0\u5347\u914d\u989d\u3002";
        }
        if (msg.contains("Conflict")) {
            return "\u8d44\u6e90\u51b2\u7a81\uff0c\u8be5\u79c1\u6709IP\u5df2\u6709\u516c\u7f51IP\u7ed1\u5b9a\u3002\u8bf7\u5148\u89e3\u7ed1\u73b0\u6709\u516c\u7f51IP\u3002";
        }
        if (msg.contains("NotAuthorizedOrNotFound")) {
            return "\u6743\u9650\u4e0d\u8db3\u6216\u8d44\u6e90\u4e0d\u5b58\u5728\u3002";
        }
        if (msg.contains("InvalidParameter")) {
            if (msg.contains("IPv6")) {
                return "\u5b50\u7f51\u6216VCN\u672a\u542f\u7528IPv6\uff0c\u6b63\u5728\u81ea\u52a8\u914d\u7f6e\u4e2d\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
            }
            return "\u53c2\u6570\u65e0\u6548: " + msg.substring(0, Math.min(msg.length(), 100));
        }
        if (msg.contains("TooManyRequests")) {
            return "\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
        }
        return msg.length() > 150 ? msg.substring(0, 150) + "..." : msg;
    }

    private SysUserDTO buildBasicDTO(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }
}

