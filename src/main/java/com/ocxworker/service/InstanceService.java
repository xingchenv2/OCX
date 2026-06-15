package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.util.ShapeFlexLimitsUtil;
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
import com.oracle.bmc.core.model.CreatePublicIpDetails.Lifetime;
import com.oracle.bmc.core.model.RouteRule.DestinationType;
import com.oracle.bmc.core.model.UpdateBootVolumeDetails.Builder;
import com.oracle.bmc.core.model.Volume.LifecycleState;
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
import com.oracle.bmc.core.requests.ListPublicIpsRequest.Scope;
import com.oracle.bmc.core.responses.ListVolumeAttachmentsResponse;
import com.oracle.bmc.core.responses.ListVolumesResponse;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        String r = region != null && !region.isBlank() ? region.trim() : null;
        return new OciClientService(this.buildBasicDTO(ociUser), r);
    }

    public List<Map<String, Object>> listInstances(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var32;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<Compartment> compartments = client.listAllCompartments();
                    Map<String, String> compartmentNameMap = new LinkedHashMap<>();

                    for (Compartment c : compartments) {
                        compartmentNameMap.put(c.getId(), c.getName());
                    }

                    List<Instance> allInstances = new ArrayList<>();

                    for (Compartment compartment : compartments) {
                        allInstances.addAll(client.listAllInstancesInCompartment(compartment.getId()));
                    }

                    if (allInstances.isEmpty()) {
                        return new ArrayList<>();
                    }

                    ExecutorService executor = Executors.newFixedThreadPool(Math.min(Math.max(allInstances.size(), 1), 8));
                    Map<String, Future<String>> ipFutures = new LinkedHashMap<>();
                    List<Map<String, Object>> result = new ArrayList<>();

                    try {
                        for (Instance inst : allInstances) {
                            ipFutures.put(inst.getId(), executor.submit(() -> {
                                try {
                                    return client.getInstancePublicIp(inst);
                                } catch (Exception var3x) {
                                    return null;
                                }
                            }));
                        }

                        for (Instance inst : allInstances) {
                            Map<String, Object> map = new LinkedHashMap<>();
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
                                map.put("publicIp", ipFutures.get(inst.getId()).get(15L, TimeUnit.SECONDS));
                            } catch (Exception var22) {
                                map.put("publicIp", null);
                            }

                            result.add(map);
                        }
                    } finally {
                        executor.shutdownNow();
                    }

                    var32 = result;
                }

                return (List<Map<String, Object>>)var32;
            } catch (Exception var25) {
                log.error("Failed to list instances: {}", var25.getMessage());
                throw new OciException(this.tag(ociUser) + "获取实例列表失败: " + var25.getMessage());
            }
        }
    }

    public void updateInstanceState(String userId, String instanceId, String action, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    InstanceActionRequest request = InstanceActionRequest.builder().instanceId(instanceId).action(action).build();
                    client.getComputeClient().instanceAction(request);
                    log.info("Instance {} action: {}", instanceId, action);
                }
            } catch (Exception var11) {
                throw new OciException(this.tag(ociUser) + "操作失败: " + var11.getMessage());
            }
        }
    }

    public void terminateInstance(String userId, String instanceId, boolean preserveBootVolume, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getComputeClient()
                        .terminateInstance(TerminateInstanceRequest.builder().instanceId(instanceId).preserveBootVolume(preserveBootVolume).build());
                    log.info("Instance terminated: {}, preserveBootVolume={}", instanceId, preserveBootVolume);
                }
            } catch (Exception var11) {
                throw new OciException(this.tag(ociUser) + "终止实例失败: " + var11.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listBootVolumesByInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var17;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance instance = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                    List<BootVolumeAttachment> attachments = client.getComputeClient()
                        .listBootVolumeAttachments(
                            ListBootVolumeAttachmentsRequest.builder()
                                .compartmentId(client.getCompartmentId())
                                .instanceId(instanceId)
                                .availabilityDomain(instance.getAvailabilityDomain())
                                .build()
                        )
                        .getItems();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (BootVolumeAttachment att : attachments) {
                        try {
                            BootVolume vol = client.getBlockstorageClient()
                                .getBootVolume(GetBootVolumeRequest.builder().bootVolumeId(att.getBootVolumeId()).build())
                                .getBootVolume();
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("id", vol.getId());
                            map.put("displayName", vol.getDisplayName());
                            map.put("sizeInGBs", vol.getSizeInGBs());
                            map.put("vpusPerGB", vol.getVpusPerGB());
                            map.put("lifecycleState", vol.getLifecycleState().getValue());
                            map.put("timeCreated", vol.getTimeCreated() != null ? vol.getTimeCreated().toString() : null);
                            result.add(map);
                        } catch (Exception var14) {
                            log.warn("Failed to get boot volume {}: {}", att.getBootVolumeId(), var14.getMessage());
                        }
                    }

                    var17 = result;
                }

                return (List<Map<String, Object>>)var17;
            } catch (Exception var16) {
                throw new OciException(this.tag(ociUser) + "获取引导卷列表失败: " + var16.getMessage());
            }
        }
    }

    public void updateBootVolume(String userId, String bootVolumeId, Long sizeInGBs, String displayName, Long vpusPerGB, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Builder detailsBuilder = UpdateBootVolumeDetails.builder();
                    if (sizeInGBs != null) {
                        detailsBuilder.sizeInGBs(sizeInGBs);
                    }

                    if (displayName != null) {
                        detailsBuilder.displayName(displayName);
                    }

                    if (vpusPerGB != null) {
                        detailsBuilder.vpusPerGB(vpusPerGB);
                    }

                    client.getBlockstorageClient()
                        .updateBootVolume(UpdateBootVolumeRequest.builder().bootVolumeId(bootVolumeId).updateBootVolumeDetails(detailsBuilder.build()).build());
                    log.info("Boot volume updated: {}", bootVolumeId);
                }
            } catch (Exception var13) {
                throw new OciException(this.tag(ociUser) + "更新引导卷失败: " + var13.getMessage());
            }
        }
    }

    public Map<String, Object> getInstanceNetworkDetail(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var26;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Map<String, Object> result = new LinkedHashMap<>();
                    List<VnicAttachment> attachments = client.getComputeClient()
                        .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build())
                        .getItems();
                    List<Map<String, Object>> vnics = new ArrayList<>();

                    for (VnicAttachment att : attachments) {
                        try {
                            Vnic vnic = client.getVirtualNetworkClient().getVnic(GetVnicRequest.builder().vnicId(att.getVnicId()).build()).getVnic();
                            Map<String, Object> vnicInfo = new LinkedHashMap<>();
                            vnicInfo.put("vnicId", vnic.getId());
                            vnicInfo.put("displayName", vnic.getDisplayName());
                            vnicInfo.put("privateIp", vnic.getPrivateIp());
                            vnicInfo.put("publicIp", vnic.getPublicIp());
                            vnicInfo.put("subnetId", att.getSubnetId());
                            List<Ipv6> ipv6List = client.getVirtualNetworkClient()
                                .listIpv6s(ListIpv6sRequest.builder().vnicId(vnic.getId()).build())
                                .getItems();
                            vnicInfo.put("ipv6Addresses", ipv6List.stream().map(Ipv6::getIpAddress).toList());
                            List<Map<String, Object>> ipv6Details = new ArrayList<>();

                            for (Ipv6 ip6 : ipv6List) {
                                Map<String, Object> m = new LinkedHashMap<>();
                                m.put("ipv6Id", ip6.getId());
                                m.put("ipAddress", ip6.getIpAddress());
                                ipv6Details.add(m);
                            }

                            vnicInfo.put("ipv6List", ipv6Details);
                            List<PrivateIp> privateIps = client.getVirtualNetworkClient()
                                .listPrivateIps(ListPrivateIpsRequest.builder().vnicId(vnic.getId()).build())
                                .getItems();
                            List<Map<String, Object>> ipDetails = new ArrayList<>();

                            for (PrivateIp pip : privateIps) {
                                Map<String, Object> ipInfo = new LinkedHashMap<>();
                                ipInfo.put("privateIpId", pip.getId());
                                ipInfo.put("privateIpAddress", pip.getIpAddress());
                                ipInfo.put("isPrimary", pip.getIsPrimary());

                                try {
                                    PublicIp pubIp = client.getVirtualNetworkClient()
                                        .getPublicIpByPrivateIpId(
                                            GetPublicIpByPrivateIpIdRequest.builder()
                                                .getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(pip.getId()).build())
                                                .build()
                                        )
                                        .getPublicIp();
                                    ipInfo.put("publicIpAddress", pubIp.getIpAddress());
                                    ipInfo.put("publicIpId", pubIp.getId());
                                    ipInfo.put("publicIpLifetime", pubIp.getLifetime().getValue());
                                } catch (Exception var22) {
                                    ipInfo.put("publicIpAddress", null);
                                    ipInfo.put("publicIpId", null);
                                    ipInfo.put("publicIpLifetime", null);
                                }

                                ipDetails.add(ipInfo);
                            }

                            vnicInfo.put("ipDetails", ipDetails);
                            vnics.add(vnicInfo);
                        } catch (Exception var23) {
                            log.warn("Failed to get VNIC detail: {}", var23.getMessage());
                        }
                    }

                    result.put("vnics", vnics);
                    var26 = result;
                }

                return (Map<String, Object>)var26;
            } catch (Exception var25) {
                throw new OciException(this.tag(ociUser) + "获取实例网络详情失败: " + var25.getMessage());
            }
        }
    }

    public Map<String, String> addIpv6(String userId, String instanceId, String preferredVnicId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Map var28;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<VnicAttachment> attachments = client.getComputeClient()
                        .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build())
                        .getItems();
                    if (attachments.isEmpty()) {
                        throw new OciException("未找到实例的 VNIC");
                    }

                    VnicAttachment target = attachments.get(0);
                    if (preferredVnicId != null && !preferredVnicId.isBlank()) {
                        for (VnicAttachment att : attachments) {
                            if (preferredVnicId.equals(att.getVnicId())) {
                                target = att;
                                break;
                            }
                        }
                    }

                    String vnicId = target.getVnicId();
                    String subnetId = target.getSubnetId();
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(subnet.getVcnId()).build()).getVcn();
                    if (vcn.getIpv6CidrBlocks() == null || vcn.getIpv6CidrBlocks().isEmpty()) {
                        log.info("VCN {} has no IPv6 CIDR, adding Oracle-assigned IPv6...", vcn.getDisplayName());

                        try {
                            client.getVirtualNetworkClient()
                                .addIpv6VcnCidr(
                                    AddIpv6VcnCidrRequest.builder()
                                        .vcnId(vcn.getId())
                                        .addVcnIpv6CidrDetails(AddVcnIpv6CidrDetails.builder().isOracleGuaAllocationEnabled(true).build())
                                        .build()
                                );
                            Thread.sleep(8000L);
                            vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
                        } catch (BmcException var18) {
                            String em = var18.getMessage() == null ? "" : var18.getMessage();
                            if (!em.contains("already exists") && !em.contains("already has")) {
                                throw new OciException(this.tag(ociUser) + "VCN 添加 IPv6 CIDR 失败: " + this.extractOciErrorMessage(var18));
                            }

                            vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
                        }
                    }

                    if (subnet.getIpv6CidrBlocks() == null || subnet.getIpv6CidrBlocks().isEmpty()) {
                        log.info("Subnet {} has no IPv6 CIDR, adding...", subnet.getDisplayName());
                        String vcnIpv6Cidr = vcn.getIpv6CidrBlocks() != null && !vcn.getIpv6CidrBlocks().isEmpty()
                            ? (String)vcn.getIpv6CidrBlocks().get(0)
                            : null;
                        if (vcnIpv6Cidr == null) {
                            throw new OciException("VCN 没有 IPv6 CIDR，无法为子网添加 IPv6。请先在OCI控制台手动为VCN启用IPv6。");
                        }

                        String subnetIpv6Cidr = vcnIpv6Cidr.replaceAll("/\\d+$", "/64");

                        try {
                            client.getVirtualNetworkClient()
                                .updateSubnet(
                                    UpdateSubnetRequest.builder()
                                        .subnetId(subnetId)
                                        .updateSubnetDetails(UpdateSubnetDetails.builder().ipv6CidrBlocks(List.of(subnetIpv6Cidr)).build())
                                        .build()
                                );
                            Thread.sleep(3000L);
                        } catch (BmcException var19) {
                            String em = var19.getMessage() == null ? "" : var19.getMessage();
                            if (!em.contains("already exists") && !em.contains("already has")) {
                                throw new OciException(this.tag(ociUser) + "子网添加 IPv6 CIDR 失败: " + this.extractOciErrorMessage(var19));
                            }
                        }
                    }

                    this.ensureIpv6InternetRoute(client, vcn, subnet);
                    Ipv6 ipv6 = client.getVirtualNetworkClient()
                        .createIpv6(CreateIpv6Request.builder().createIpv6Details(CreateIpv6Details.builder().vnicId(vnicId).build()).build())
                        .getIpv6();
                    var28 = Map.of("ipv6Address", ipv6.getIpAddress());
                }

                return var28;
            } catch (OciException var21) {
                throw var21;
            } catch (BmcException var22) {
                throw new OciException(this.tag(ociUser) + "添加 IPv6 失败: " + this.extractOciErrorMessage(var22));
            } catch (Exception var23) {
                throw new OciException(this.tag(ociUser) + "添加 IPv6 失败: " + var23.getMessage());
            }
        }
    }

    public void removeIpv6(String userId, String ipv6Id, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (ipv6Id != null && !ipv6Id.isBlank()) {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient().deleteIpv6(DeleteIpv6Request.builder().ipv6Id(ipv6Id).build());
                    log.info("IPv6 unassigned (deleted): {}", ipv6Id);
                }
            } catch (BmcException var10) {
                throw new OciException(this.tag(ociUser) + "取消分配 IPv6 失败: " + this.extractOciErrorMessage(var10));
            } catch (Exception var11) {
                throw new OciException(this.tag(ociUser) + "取消分配 IPv6 失败: " + var11.getMessage());
            }
        } else {
            throw new OciException("ipv6Id 不能为空");
        }
    }

    private void ensureIpv6InternetRoute(OciClientService client, Vcn vcn, Subnet subnet) {
        List<InternetGateway> igws = client.getVirtualNetworkClient()
            .listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(client.getCompartmentId()).vcnId(vcn.getId()).build())
            .getItems();
        InternetGateway igw;
        if (igws != null && !igws.isEmpty()) {
            igw = igws.stream().filter(gw -> Boolean.TRUE.equals(gw.getIsEnabled())).findFirst().orElse(igws.get(0));
        } else {
            log.info("VCN {} has no Internet Gateway, creating one...", vcn.getDisplayName());
            igw = client.createInternetGateway(vcn);
        }

        String routeTableId = subnet.getRouteTableId() != null ? subnet.getRouteTableId() : vcn.getDefaultRouteTableId();
        if (routeTableId == null) {
            log.warn("No route table found for subnet {}, skip IPv6 default route setup", subnet.getId());
        } else {
            RouteTable routeTable = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(routeTableId).build()).getRouteTable();
            List<RouteRule> rules = new ArrayList<>();
            if (routeTable.getRouteRules() != null) {
                rules.addAll(routeTable.getRouteRules());
            }

            boolean hasIpv6DefaultRoute = rules.stream()
                .anyMatch(rule -> "::/0".equals(rule.getDestination()) && DestinationType.CidrBlock.equals(rule.getDestinationType()));
            if (!hasIpv6DefaultRoute) {
                rules.add(
                    RouteRule.builder()
                        .destination("::/0")
                        .destinationType(DestinationType.CidrBlock)
                        .networkEntityId(igw.getId())
                        .description("ocx-worker auto add IPv6 default route")
                        .build()
                );
                client.getVirtualNetworkClient()
                    .updateRouteTable(
                        UpdateRouteTableRequest.builder()
                            .rtId(routeTableId)
                            .updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build())
                            .build()
                    );
                log.info("Added IPv6 default route (::/0 -> IGW) to route table {}", routeTableId);
            }
        }
    }

    public Map<String, String> createReservedIp(String userId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Map var8;
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.CreatePublicIpDetails.Builder builder = CreatePublicIpDetails.builder()
                        .compartmentId(client.getCompartmentId())
                        .lifetime(Lifetime.Reserved);
                    if (displayName != null && !displayName.isBlank()) {
                        builder.displayName(displayName);
                    }

                    PublicIp reservedIp = client.getVirtualNetworkClient()
                        .createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(builder.build()).build())
                        .getPublicIp();
                    var8 = Map.of("publicIpId", reservedIp.getId(), "ipAddress", reservedIp.getIpAddress());
                }

                return var8;
            } catch (BmcException var11) {
                throw new OciException(this.tag(ociUser) + "创建预留IP失败: " + this.extractOciErrorMessage(var11));
            } catch (Exception var12) {
                throw new OciException(this.tag(ociUser) + "创建预留IP失败: " + var12.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listReservedIps(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                List var6;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<PublicIp> publicIps = client.getVirtualNetworkClient()
                        .listPublicIps(
                            ListPublicIpsRequest.builder()
                                .compartmentId(client.getCompartmentId())
                                .scope(Scope.Region)
                                .lifetime(com.oracle.bmc.core.requests.ListPublicIpsRequest.Lifetime.Reserved)
                                .build()
                        )
                        .getItems();
                    var6 = publicIps.stream().map(ip -> {
                        Map<String, Object> map = new LinkedHashMap<>();
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
                }

                return var6;
            } catch (Exception var9) {
                throw new OciException(this.tag(ociUser) + "获取预留 IP 列表失败: " + var9.getMessage());
            }
        }
    }

    public void deleteReservedIp(String userId, String publicIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(publicIpId).build());
                    log.info("Reserved IP deleted: {}", publicIpId);
                }
            } catch (Exception var10) {
                throw new OciException(this.tag(ociUser) + "删除预留 IP 失败: " + var10.getMessage());
            }
        }
    }

    public void assignReservedIp(String userId, String publicIpId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<VnicAttachment> attachments = client.getComputeClient()
                        .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build())
                        .getItems();
                    if (attachments.isEmpty()) {
                        throw new OciException("未找到实例的 VNIC");
                    }

                    String vnicId = attachments.get(0).getVnicId();
                    String subnetId = attachments.get(0).getSubnetId();
                    PrivateIp secondaryPip = client.getVirtualNetworkClient()
                        .createPrivateIp(
                            CreatePrivateIpRequest.builder()
                                .createPrivateIpDetails(
                                    CreatePrivateIpDetails.builder().vnicId(vnicId).displayName("privateip" + System.currentTimeMillis()).build()
                                )
                                .build()
                        )
                        .getPrivateIp();
                    client.getVirtualNetworkClient()
                        .updatePublicIp(
                            UpdatePublicIpRequest.builder()
                                .publicIpId(publicIpId)
                                .updatePublicIpDetails(UpdatePublicIpDetails.builder().privateIpId(secondaryPip.getId()).build())
                                .build()
                        );
                    log.info(
                        "Reserved IP {} assigned to secondary private IP {} on instance {}", new Object[]{publicIpId, secondaryPip.getIpAddress(), instanceId}
                    );
                }
            } catch (BmcException var13) {
                throw new OciException(this.tag(ociUser) + "绑定预留IP失败: " + this.extractOciErrorMessage(var13));
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                throw new OciException(this.tag(ociUser) + "绑定预留IP失败: " + var15.getMessage());
            }
        }
    }

    public void unassignReservedIp(String userId, String publicIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    PublicIp pubIp = client.getVirtualNetworkClient().getPublicIp(GetPublicIpRequest.builder().publicIpId(publicIpId).build()).getPublicIp();
                    String privateIpId = pubIp.getPrivateIpId();
                    client.getVirtualNetworkClient()
                        .updatePublicIp(
                            UpdatePublicIpRequest.builder()
                                .publicIpId(publicIpId)
                                .updatePublicIpDetails(UpdatePublicIpDetails.builder().privateIpId("").build())
                                .build()
                        );
                    if (privateIpId != null) {
                        try {
                            PrivateIp pip = client.getVirtualNetworkClient()
                                .getPrivateIp(GetPrivateIpRequest.builder().privateIpId(privateIpId).build())
                                .getPrivateIp();
                            if (!Boolean.TRUE.equals(pip.getIsPrimary())) {
                                client.getVirtualNetworkClient().deletePrivateIp(DeletePrivateIpRequest.builder().privateIpId(privateIpId).build());
                            }
                        } catch (Exception var10) {
                        }
                    }

                    log.info("Reserved IP {} unassigned", publicIpId);
                }
            } catch (BmcException var12) {
                throw new OciException(this.tag(ociUser) + "解绑预留IP失败: " + this.extractOciErrorMessage(var12));
            } catch (Exception var13) {
                throw new OciException(this.tag(ociUser) + "解绑预留IP失败: " + var13.getMessage());
            }
        }
    }

    public Object updateInstance(String userId, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs, String region) {
        try {
            return this.updateInstanceOnce(userId, instanceId, displayName, shape, ocpus, memoryInGBs, region);
        } catch (OciException var11) {
            throw var11;
        } catch (BmcException var12) {
            if (isShapeEditRequest(shape, ocpus, memoryInGBs) && ShapeEditTaskManager.isOutOfStock(var12)) {
                return this.shapeEditTaskManager
                    .startTask(
                        userId,
                        instanceId,
                        region,
                        shape,
                        ocpus,
                        memoryInGBs,
                        () -> this.updateInstanceOnce(userId, instanceId, displayName, shape, ocpus, memoryInGBs, region)
                    );
            } else {
                OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
                String prefix = ociUser == null ? "" : this.tag(ociUser);
                throw new OciException(prefix + "修改实例失败: " + this.extractOciErrorMessage(var12));
            }
        } catch (Exception var13) {
            OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
            String prefix = ociUser == null ? "" : this.tag(ociUser);
            throw new OciException(prefix + "修改实例失败: " + var13.getMessage());
        }
    }

    private Map<String, Object> updateInstanceOnce(
        String userId, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs, String region
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            Map var10;
            try (OciClientService client = this.oci(ociUser, region)) {
                var10 = this.updateInstanceOnce(client, ociUser, instanceId, displayName, shape, ocpus, memoryInGBs);
            }

            return var10;
        }
    }

    private Map<String, Object> updateInstanceOnce(
        OciClientService client, OciUser ociUser, String instanceId, String displayName, String shape, Float ocpus, Float memoryInGBs
    ) {
        Instance current = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
        String targetShape = shape != null && !shape.isBlank() ? shape.trim() : current.getShape();
        List<Shape> compatible = client.getShapes(current.getAvailabilityDomain(), current.getImageId());
        Shape shapeMeta = findShapeMeta(compatible, targetShape);
        if (shapeMeta == null) {
            throw new OciException(this.tag(ociUser) + "目标 Shape 与当前实例镜像不兼容: " + targetShape);
        } else {
            boolean flex = isFlexibleShape(targetShape);
            Float useOcpus = ocpus;
            Float useMemory = memoryInGBs;
            if (flex) {
                if (ocpus == null && current.getShapeConfig() != null) {
                    useOcpus = current.getShapeConfig().getOcpus();
                }

                if (memoryInGBs == null && current.getShapeConfig() != null) {
                    useMemory = current.getShapeConfig().getMemoryInGBs();
                }

                validateFlexResources(shapeMeta, useOcpus, useMemory);
            } else if (ocpus != null || memoryInGBs != null) {
                throw new OciException(this.tag(ociUser) + "非 Flex Shape 仅可更换形状，不能单独调整 OCPU/内存");
            }

            com.oracle.bmc.core.model.UpdateInstanceDetails.Builder detailsBuilder = UpdateInstanceDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                detailsBuilder.displayName(displayName);
            }

            if (shape != null && !shape.isBlank() && !shape.trim().equals(current.getShape())) {
                detailsBuilder.shape(shape.trim());
            }

            if (flex && (useOcpus != null || useMemory != null)) {
                com.oracle.bmc.core.model.UpdateInstanceShapeConfigDetails.Builder shapeBuilder = UpdateInstanceShapeConfigDetails.builder();
                if (useOcpus != null) {
                    shapeBuilder.ocpus(useOcpus);
                }

                if (useMemory != null) {
                    shapeBuilder.memoryInGBs(useMemory);
                }

                detailsBuilder.shapeConfig(shapeBuilder.build());
            }

            Instance updated = client.getComputeClient()
                .updateInstance(UpdateInstanceRequest.builder().instanceId(instanceId).updateInstanceDetails(detailsBuilder.build()).build())
                .getInstance();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("instanceId", updated.getId());
            result.put("name", updated.getDisplayName());
            result.put("shape", updated.getShape());
            if (updated.getShapeConfig() != null) {
                result.put("ocpus", updated.getShapeConfig().getOcpus());
                result.put("memoryInGBs", updated.getShapeConfig().getMemoryInGBs());
            }

            return result;
        }
    }

    private static boolean isShapeEditRequest(String shape, Float ocpus, Float memoryInGBs) {
        return shape != null && !shape.isBlank() || ocpus != null || memoryInGBs != null;
    }

    public Map<String, Object> forceA2FlexToA1Flex(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var14;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance current = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                    String actualShape = current.getShape();
                    if (!"VM.Standard.A2.Flex".equals(actualShape)) {
                        throw new OciException(this.tag(ociUser) + "当前实例 Shape 不是 VM.Standard.A2.Flex，无法执行强改。请检查当前 Shape，实际为：" + actualShape);
                    }

                    Float ocpus = null;
                    Float memoryInGBs = null;
                    if (current.getShapeConfig() != null) {
                        ocpus = current.getShapeConfig().getOcpus();
                        memoryInGBs = current.getShapeConfig().getMemoryInGBs();
                    }

                    if (ocpus == null || memoryInGBs == null) {
                        throw new OciException(this.tag(ociUser) + "无法读取当前 Flex 的 OCPU/内存配置，请检查后重试");
                    }

                    List<Shape> compatible = client.getShapes(current.getAvailabilityDomain(), current.getImageId());
                    Shape a1Meta = findShapeMeta(compatible, "VM.Standard.A1.Flex");
                    if (a1Meta != null) {
                        validateFlexResources(a1Meta, ocpus, memoryInGBs);
                    }

                    log.warn("{} force A2→A1 instanceId={} ocpus={} memoryInGBs={}", new Object[]{this.tag(ociUser), instanceId, ocpus, memoryInGBs});
                    Instance updated = client.getComputeClient()
                        .updateInstance(
                            UpdateInstanceRequest.builder()
                                .instanceId(instanceId)
                                .updateInstanceDetails(
                                    UpdateInstanceDetails.builder()
                                        .shape("VM.Standard.A1.Flex")
                                        .shapeConfig(UpdateInstanceShapeConfigDetails.builder().ocpus(ocpus).memoryInGBs(memoryInGBs).build())
                                        .build()
                                )
                                .build()
                        )
                        .getInstance();
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("instanceId", updated.getId());
                    result.put("name", updated.getDisplayName());
                    result.put("shape", updated.getShape());
                    if (updated.getShapeConfig() != null) {
                        result.put("ocpus", updated.getShapeConfig().getOcpus());
                        result.put("memoryInGBs", updated.getShapeConfig().getMemoryInGBs());
                    }

                    this.notifyForceA2ToA1Success(updated);
                    var14 = result;
                }

                return (Map<String, Object>)var14;
            } catch (OciException var17) {
                this.notifyForceA2ToA1Failure(ociUser, region);
                throw var17;
            } catch (BmcException var18) {
                this.notifyForceA2ToA1Failure(ociUser, region);
                throw new OciException(this.tag(ociUser) + "A2 强改 A1 失败: " + this.extractOciErrorMessage(var18));
            } catch (Exception var19) {
                this.notifyForceA2ToA1Failure(ociUser, region);
                throw new OciException(this.tag(ociUser) + "A2 强改 A1 失败: " + var19.getMessage());
            }
        }
    }

    private void notifyForceA2ToA1Success(Instance updated) {
        String nowShape = updated.getShape() != null ? updated.getShape() : "VM.Standard.A1.Flex";
        String html = "\ud83c\udf89 <b>实例形状修改成功！</b>\n\n原Shape：<code>VM.Standard.A2.Flex</code>\n现Shape：<code>" + nowShape + "</code>\n公网IP以及密码无变化\n已成功实现A2➡A1";
        this.notificationService.sendHtmlWithType("instance", html);
    }

    private void notifyForceA2ToA1Failure(OciUser ociUser, String region) {
        String username = ociUser.getUsername() != null ? ociUser.getUsername() : "-";
        String reg = region != null && !region.isBlank() ? region.trim() : "-";
        String html = "\ud83d\ude1f <b>实例形状修改失败！</b>\n\n租户：" + username + "\n区域：" + reg + "\nA2➡A1修改失败，可再次尝试";
        this.notificationService.sendHtmlWithType("instance", html);
    }

    public List<Map<String, Object>> listAvailableShapes(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var15;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<AvailabilityDomain> ads = client.getAvailabilityDomains();
                    Set<String> seen = new LinkedHashSet<>();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (AvailabilityDomain ad : ads) {
                        for (Shape s : client.getShapes(ad.getName())) {
                            if (seen.add(s.getShape())) {
                                result.add(shapeToMap(s));
                            }
                        }
                    }

                    var15 = result;
                }

                return (List<Map<String, Object>>)var15;
            } catch (Exception var14) {
                throw new OciException(this.tag(ociUser) + "获取可用 Shape 列表失败: " + var14.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listShapesForInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var15;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance inst = client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
                    Set<String> seen = new LinkedHashSet<>();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (Shape s : client.getShapes(inst.getAvailabilityDomain(), inst.getImageId())) {
                        if (seen.add(s.getShape())) {
                            result.add(shapeToMap(s));
                        }
                    }

                    result.sort(Comparator.comparing(m -> String.valueOf(m.get("shape"))));
                    var15 = result;
                }

                return (List<Map<String, Object>>)var15;
            } catch (BmcException var13) {
                throw new OciException(this.tag(ociUser) + "获取实例可用 Shape 失败: " + this.extractOciErrorMessage(var13));
            } catch (Exception var14) {
                throw new OciException(this.tag(ociUser) + "获取实例可用 Shape 失败: " + var14.getMessage());
            }
        }
    }

    private static boolean isFlexibleShape(String shapeName) {
        return shapeName != null && shapeName.contains("Flex");
    }

    private static Shape findShapeMeta(List<Shape> shapes, String shapeName) {
        if (shapeName == null) {
            return null;
        } else {
            for (Shape s : shapes) {
                if (shapeName.equals(s.getShape())) {
                    return s;
                }
            }

            return null;
        }
    }

    private static Map<String, Object> shapeToMap(Shape shape) {
        Map<String, Object> map = new LinkedHashMap<>();
        String name = shape.getShape();
        map.put("shape", name);
        map.put("processorDescription", shape.getProcessorDescription());
        boolean flex = isFlexibleShape(name);
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
        applyFlexLimitsOverride(map, name);
        return map;
    }

    private static void applyFlexLimitsOverride(Map<String, Object> map, String shapeName) {
        ShapeFlexLimitsUtil.FlexLimits lim = ShapeFlexLimitsUtil.forShape(shapeName);
        if (lim != null && Boolean.TRUE.equals(map.get("isFlexible"))) {
            map.put("ocpuMax", lim.maxOcpus());
            map.put("memoryMaxInGBs", lim.maxMemoryGb());
            if (map.get("ocpuMin") == null) {
                map.put("ocpuMin", 1.0F);
            }

            if (map.get("memoryMinInGBs") == null) {
                map.put("memoryMinInGBs", lim.defaultMemoryGb());
            }
        }
    }

    private static void validateFlexResources(Shape shapeMeta, Float ocpus, Float memoryInGBs) {
        if (ocpus != null && memoryInGBs != null) {
            Float oMin = shapeMeta.getOcpuOptions() != null ? shapeMeta.getOcpuOptions().getMin() : shapeMeta.getOcpus();
            Float oMax = shapeMeta.getOcpuOptions() != null ? shapeMeta.getOcpuOptions().getMax() : shapeMeta.getOcpus();
            Float mMin = shapeMeta.getMemoryOptions() != null ? shapeMeta.getMemoryOptions().getMinInGBs() : shapeMeta.getMemoryInGBs();
            Float mMax = shapeMeta.getMemoryOptions() != null ? shapeMeta.getMemoryOptions().getMaxInGBs() : shapeMeta.getMemoryInGBs();
            ShapeFlexLimitsUtil.FlexLimits fixed = ShapeFlexLimitsUtil.forShape(shapeMeta.getShape());
            if (fixed != null) {
                oMax = fixed.maxOcpus();
                mMax = fixed.maxMemoryGb();
                if (oMin == null) {
                    oMin = 1.0F;
                }

                if (mMin == null) {
                    mMin = fixed.defaultMemoryGb();
                }
            }

            if (oMin != null && ocpus < oMin) {
                throw new OciException(String.format("OCPU 不能小于 %s（该 Shape 下限）", trimFloat(oMin)));
            } else if (oMax != null && ocpus > oMax) {
                throw new OciException(String.format("OCPU 不能大于 %s（该 Shape 上限）", trimFloat(oMax)));
            } else if (mMin != null && memoryInGBs < mMin) {
                throw new OciException(String.format("内存不能小于 %s GB（该 Shape 下限）", trimFloat(mMin)));
            } else if (mMax != null && memoryInGBs > mMax) {
                throw new OciException(String.format("内存不能大于 %s GB（该 Shape 上限）", trimFloat(mMax)));
            }
        } else {
            throw new OciException("Flex Shape 须同时指定 OCPU 与内存 (GB)");
        }
    }

    private static String trimFloat(Float v) {
        if (v == null) {
            return "";
        } else {
            return (double)v.floatValue() == Math.floor((double)v.floatValue()) ? String.valueOf(v.intValue()) : String.valueOf(v);
        }
    }

    public List<Map<String, Object>> listBlockVolumesByInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (instanceId != null && !instanceId.isBlank()) {
            try {
                Object var20;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance instance = this.getInstanceOrThrow(client, instanceId);
                    String compartmentId = instance.getCompartmentId();
                    String ad = instance.getAvailabilityDomain();
                    List<VolumeAttachment> attachments = this.listActiveVolumeAttachments(client, compartmentId, ad, instanceId);
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (VolumeAttachment att : attachments) {
                        String volumeId = att.getVolumeId();
                        if (volumeId != null) {
                            try {
                                Volume vol = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                                result.add(blockVolumeRow(att, vol));
                            } catch (Exception var16) {
                                log.warn("Failed to get block volume {}: {}", volumeId, var16.getMessage());
                            }
                        }
                    }

                    var20 = result;
                }

                return (List<Map<String, Object>>)var20;
            } catch (OciException var18) {
                throw var18;
            } catch (Exception var19) {
                throw new OciException(this.tag(ociUser) + "获取块存储卷列表失败: " + var19.getMessage());
            }
        } else {
            throw new OciException("instanceId 不能为空");
        }
    }

    public List<Map<String, Object>> listUnattachedBlockVolumesForInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (instanceId != null && !instanceId.isBlank()) {
            try {
                Object var23;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance instance = this.getInstanceOrThrow(client, instanceId);
                    String compartmentId = instance.getCompartmentId();
                    String ad = instance.getAvailabilityDomain();
                    Set<String> attachedVolumeIds = new HashSet<>();

                    for (VolumeAttachment att : this.listActiveVolumeAttachments(client, compartmentId, ad, null)) {
                        if (att.getVolumeId() != null) {
                            attachedVolumeIds.add(att.getVolumeId());
                        }
                    }

                    List<Map<String, Object>> result = new ArrayList<>();
                    Set<String> seen = new HashSet<>();
                    String page = null;

                    do {
                        ListVolumesResponse resp = client.getBlockstorageClient()
                            .listVolumes(
                                ListVolumesRequest.builder()
                                    .compartmentId(compartmentId)
                                    .availabilityDomain(ad)
                                    .lifecycleState(LifecycleState.Available)
                                    .page(page)
                                    .build()
                            );

                        for (Volume v : resp.getItems()) {
                            if (seen.add(v.getId()) && !attachedVolumeIds.contains(v.getId())) {
                                Map<String, Object> m = new LinkedHashMap<>();
                                m.put("id", v.getId());
                                m.put("displayName", v.getDisplayName());
                                m.put("sizeInGBs", v.getSizeInGBs());
                                m.put("vpusPerGB", v.getVpusPerGB());
                                m.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
                                m.put("availabilityDomain", v.getAvailabilityDomain());
                                result.add(m);
                            }
                        }

                        page = resp.getOpcNextPage();
                    } while (page != null);

                    var23 = result;
                }

                return (List<Map<String, Object>>)var23;
            } catch (OciException var19) {
                throw var19;
            } catch (Exception var20) {
                throw new OciException(this.tag(ociUser) + "获取可挂载块存储卷失败: " + var20.getMessage());
            }
        } else {
            throw new OciException("instanceId 不能为空");
        }
    }

    public Map<String, Object> createBlockVolumeAndAttach(
        String userId, String instanceId, String displayName, Long sizeInGBs, Long vpusPerGB, String device, String region
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            validateBlockVolumeSize(sizeInGBs);
            long vpus = resolveVpusPerGb(vpusPerGB);

            try {
                Map var20;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance instance = this.getInstanceOrThrow(client, instanceId);
                    String compartmentId = instance.getCompartmentId();
                    String ad = instance.getAvailabilityDomain();
                    CreateVolumeDetails createDetails = CreateVolumeDetails.builder()
                        .compartmentId(compartmentId)
                        .availabilityDomain(ad)
                        .displayName(displayName != null && !displayName.isBlank() ? displayName.trim() : "block-volume")
                        .sizeInGBs(sizeInGBs)
                        .vpusPerGB(vpus)
                        .build();
                    Volume created = client.getBlockstorageClient()
                        .createVolume(CreateVolumeRequest.builder().createVolumeDetails(createDetails).build())
                        .getVolume();
                    Volume available = this.waitVolumeUntilAvailable(client, created.getId());
                    VolumeAttachment attachment = this.attachVolumeToInstance(client, instanceId, available.getId(), device);
                    Map<String, Object> out = blockVolumeRow(attachment, available);
                    out.put("message", "块存储卷已创建并提交挂载");
                    var20 = out;
                }

                return var20;
            } catch (OciException var23) {
                throw var23;
            } catch (InterruptedException var24) {
                Thread.currentThread().interrupt();
                throw new OciException(this.tag(ociUser) + "创建并挂载块存储卷被中断");
            } catch (Exception var25) {
                throw new OciException(this.tag(ociUser) + "创建并挂载块存储卷失败: " + var25.getMessage());
            }
        }
    }

    public Map<String, Object> attachBlockVolume(String userId, String instanceId, String volumeId, String device, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (volumeId != null && !volumeId.isBlank()) {
            try {
                Map var12;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Instance instance = this.getInstanceOrThrow(client, instanceId);
                    Volume vol = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                    if (!Objects.equals(vol.getAvailabilityDomain(), instance.getAvailabilityDomain())) {
                        throw new OciException("块存储卷与实例须在同一可用域 (Availability Domain)");
                    }

                    if (!Objects.equals(vol.getCompartmentId(), instance.getCompartmentId())) {
                        throw new OciException("块存储卷与实例须在同一区间 (Compartment)");
                    }

                    if (vol.getLifecycleState() != LifecycleState.Available) {
                        throw new OciException(
                            "块存储卷须为 AVAILABLE 状态方可挂载，当前: " + (vol.getLifecycleState() != null ? vol.getLifecycleState().getValue() : "unknown")
                        );
                    }

                    VolumeAttachment attachment = this.attachVolumeToInstance(client, instanceId, volumeId, device);
                    Volume refreshed = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
                    var12 = blockVolumeRow(attachment, refreshed);
                }

                return var12;
            } catch (OciException var15) {
                throw var15;
            } catch (Exception var16) {
                throw new OciException(this.tag(ociUser) + "挂载块存储卷失败: " + var16.getMessage());
            }
        } else {
            throw new OciException("volumeId 不能为空");
        }
    }

    public void detachBlockVolume(String userId, String volumeAttachmentId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (volumeAttachmentId != null && !volumeAttachmentId.isBlank()) {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getComputeClient().detachVolume(DetachVolumeRequest.builder().volumeAttachmentId(volumeAttachmentId).build());
                    log.info("Block volume detached: attachment {}", volumeAttachmentId);
                }
            } catch (Exception var10) {
                throw new OciException(this.tag(ociUser) + "卸载块存储卷失败: " + var10.getMessage());
            }
        } else {
            throw new OciException("volumeAttachmentId 不能为空");
        }
    }

    public void updateBlockVolume(String userId, String volumeId, Long sizeInGBs, String displayName, Long vpusPerGB, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else if (volumeId != null && !volumeId.isBlank()) {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateVolumeDetails.Builder detailsBuilder = UpdateVolumeDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        detailsBuilder.displayName(displayName.trim());
                    }

                    if (sizeInGBs != null) {
                        validateBlockVolumeSize(sizeInGBs);
                        detailsBuilder.sizeInGBs(sizeInGBs);
                    }

                    if (vpusPerGB != null) {
                        detailsBuilder.vpusPerGB(resolveVpusPerGb(vpusPerGB));
                    }

                    if (displayName == null && sizeInGBs == null && vpusPerGB == null) {
                        throw new OciException("至少提供 displayName、sizeInGBs 或 vpusPerGB 之一");
                    }

                    client.getBlockstorageClient()
                        .updateVolume(UpdateVolumeRequest.builder().volumeId(volumeId).updateVolumeDetails(detailsBuilder.build()).build());
                    log.info("Block volume updated: {}", volumeId);
                }
            } catch (OciException var13) {
                throw var13;
            } catch (Exception var14) {
                throw new OciException(this.tag(ociUser) + "更新块存储卷失败: " + var14.getMessage());
            }
        } else {
            throw new OciException("volumeId 不能为空");
        }
    }

    private Instance getInstanceOrThrow(OciClientService client, String instanceId) {
        return client.getComputeClient().getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
    }

    private List<VolumeAttachment> listActiveVolumeAttachments(OciClientService client, String compartmentId, String availabilityDomain, String instanceId) {
        List<VolumeAttachment> all = new ArrayList<>();
        String page = null;

        do {
            com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest.Builder b = ListVolumeAttachmentsRequest.builder()
                .compartmentId(compartmentId)
                .availabilityDomain(availabilityDomain);
            if (instanceId != null && !instanceId.isBlank()) {
                b.instanceId(instanceId);
            }

            ListVolumeAttachmentsResponse resp = client.getComputeClient().listVolumeAttachments(b.page(page).build());

            for (VolumeAttachment a : resp.getItems()) {
                if (a.getLifecycleState() != com.oracle.bmc.core.model.VolumeAttachment.LifecycleState.Detached) {
                    all.add(a);
                }
            }

            page = resp.getOpcNextPage();
        } while (page != null);

        return all;
    }

    private VolumeAttachment attachVolumeToInstance(OciClientService client, String instanceId, String volumeId, String device) {
        try {
            Constructor<AttachVolumeDetails> ctor = AttachVolumeDetails.class
                .getDeclaredConstructor(String.class, String.class, String.class, Boolean.class, Boolean.class, String.class);
            ctor.setAccessible(true);
            String devicePath = device != null && !device.isBlank() ? device.trim() : null;
            AttachVolumeDetails details = ctor.newInstance(devicePath, null, instanceId, null, null, volumeId);
            return client.getComputeClient().attachVolume(AttachVolumeRequest.builder().attachVolumeDetails(details).build()).getVolumeAttachment();
        } catch (OciException var8) {
            throw var8;
        } catch (ReflectiveOperationException var9) {
            throw new OciException("构建 AttachVolumeDetails 失败: " + var9.getMessage());
        }
    }

    private Volume waitVolumeUntilAvailable(OciClientService client, String volumeId) throws InterruptedException {
        for (int i = 0; i < 120; i++) {
            Volume v = client.getBlockstorageClient().getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
            LifecycleState st = v.getLifecycleState();
            if (st == LifecycleState.Available) {
                return v;
            }

            if (st == LifecycleState.Faulty || st == LifecycleState.Terminated) {
                throw new OciException("块存储卷状态异常: " + (st != null ? st.getValue() : "unknown"));
            }

            Thread.sleep(1000L);
        }

        throw new OciException("等待块存储卷进入 AVAILABLE 状态超时（最长 120 秒）");
    }

    private static Map<String, Object> blockVolumeRow(VolumeAttachment att, Volume vol) {
        Map<String, Object> map = new LinkedHashMap<>();
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
            throw new OciException("块存储卷容量须至少 50 GB（OCI CreateVolumeDetails.sizeInGBs）");
        } else if (sizeInGBs > 32768L) {
            throw new OciException("块存储卷容量不能超过 32768 GB");
        }
    }

    private static long resolveVpusPerGb(Long vpusPerGB) {
        if (vpusPerGB == null) {
            return 10L;
        } else {
            long v = vpusPerGB;
            if (v != 0L && v != 10L && v != 20L) {
                if (v >= 30L && v <= 120L && v % 10L == 0L) {
                    return v;
                } else {
                    throw new OciException("vpusPerGB 须为 0、10、20 或 30～120（Ultra High 档步进 10），见 OCI Block Volume 性能档位文档");
                }
            } else {
                return v;
            }
        }
    }

    private String extractOciErrorMessage(BmcException e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            return "OCI 调用失败（无详细信息）";
        } else if (msg.contains("LimitExceeded")) {
            return "已超出免费账户限制，无法创建更多资源。请在OCI控制台申请提升配额。";
        } else if (msg.contains("Conflict")) {
            return "资源冲突，该私有IP已有公网IP绑定。请先解绑现有公网IP。";
        } else if (msg.contains("NotAuthorizedOrNotFound")) {
            return "权限不足或资源不存在。";
        } else if (msg.contains("InvalidParameter")) {
            return msg.contains("IPv6") ? "子网或VCN未启用IPv6，正在自动配置中，请稍后重试。" : "参数无效: " + msg.substring(0, Math.min(msg.length(), 100));
        } else if (msg.contains("TooManyRequests")) {
            return "请求过于频繁，请稍后重试。";
        } else {
            return msg.length() > 150 ? msg.substring(0, 150) + "..." : msg;
        }
    }

    private SysUserDTO buildBasicDTO(OciUser ociUser) {
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
