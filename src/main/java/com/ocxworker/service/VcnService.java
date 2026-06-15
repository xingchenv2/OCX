package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.core.model.ConnectLocalPeeringGatewaysDetails;
import com.oracle.bmc.core.model.CreateDrgDetails;
import com.oracle.bmc.core.model.CreateInternetGatewayDetails;
import com.oracle.bmc.core.model.CreateLocalPeeringGatewayDetails;
import com.oracle.bmc.core.model.CreateNatGatewayDetails;
import com.oracle.bmc.core.model.CreateServiceGatewayDetails;
import com.oracle.bmc.core.model.CreateSubnetDetails;
import com.oracle.bmc.core.model.CreateVcnDetails;
import com.oracle.bmc.core.model.Drg;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.LocalPeeringGateway;
import com.oracle.bmc.core.model.NatGateway;
import com.oracle.bmc.core.model.PortRange;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Service;
import com.oracle.bmc.core.model.ServiceGateway;
import com.oracle.bmc.core.model.ServiceIdRequestDetails;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.TcpOptions;
import com.oracle.bmc.core.model.UdpOptions;
import com.oracle.bmc.core.model.UpdateInternetGatewayDetails;
import com.oracle.bmc.core.model.UpdateLocalPeeringGatewayDetails;
import com.oracle.bmc.core.model.UpdateNatGatewayDetails;
import com.oracle.bmc.core.model.UpdateRouteTableDetails;
import com.oracle.bmc.core.model.UpdateSecurityListDetails;
import com.oracle.bmc.core.model.UpdateServiceGatewayDetails;
import com.oracle.bmc.core.model.UpdateSubnetDetails;
import com.oracle.bmc.core.model.UpdateVcnDetails;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.CreateSubnetDetails.Builder;
import com.oracle.bmc.core.model.IngressSecurityRule.SourceType;
import com.oracle.bmc.core.model.RouteRule.DestinationType;
import com.oracle.bmc.core.model.Vcn.LifecycleState;
import com.oracle.bmc.core.requests.ConnectLocalPeeringGatewaysRequest;
import com.oracle.bmc.core.requests.CreateDrgRequest;
import com.oracle.bmc.core.requests.CreateInternetGatewayRequest;
import com.oracle.bmc.core.requests.CreateLocalPeeringGatewayRequest;
import com.oracle.bmc.core.requests.CreateNatGatewayRequest;
import com.oracle.bmc.core.requests.CreateServiceGatewayRequest;
import com.oracle.bmc.core.requests.CreateSubnetRequest;
import com.oracle.bmc.core.requests.CreateVcnRequest;
import com.oracle.bmc.core.requests.DeleteDrgRequest;
import com.oracle.bmc.core.requests.DeleteInternetGatewayRequest;
import com.oracle.bmc.core.requests.DeleteLocalPeeringGatewayRequest;
import com.oracle.bmc.core.requests.DeleteNatGatewayRequest;
import com.oracle.bmc.core.requests.DeleteRouteTableRequest;
import com.oracle.bmc.core.requests.DeleteSecurityListRequest;
import com.oracle.bmc.core.requests.DeleteServiceGatewayRequest;
import com.oracle.bmc.core.requests.DeleteSubnetRequest;
import com.oracle.bmc.core.requests.DeleteVcnRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.core.requests.ListDrgsRequest;
import com.oracle.bmc.core.requests.ListInternetGatewaysRequest;
import com.oracle.bmc.core.requests.ListLocalPeeringGatewaysRequest;
import com.oracle.bmc.core.requests.ListNatGatewaysRequest;
import com.oracle.bmc.core.requests.ListRouteTablesRequest;
import com.oracle.bmc.core.requests.ListSecurityListsRequest;
import com.oracle.bmc.core.requests.ListServiceGatewaysRequest;
import com.oracle.bmc.core.requests.ListServicesRequest;
import com.oracle.bmc.core.requests.ListSubnetsRequest;
import com.oracle.bmc.core.requests.ListVcnsRequest;
import com.oracle.bmc.core.requests.UpdateInternetGatewayRequest;
import com.oracle.bmc.core.requests.UpdateLocalPeeringGatewayRequest;
import com.oracle.bmc.core.requests.UpdateNatGatewayRequest;
import com.oracle.bmc.core.requests.UpdateRouteTableRequest;
import com.oracle.bmc.core.requests.UpdateSecurityListRequest;
import com.oracle.bmc.core.requests.UpdateServiceGatewayRequest;
import com.oracle.bmc.core.requests.UpdateSubnetRequest;
import com.oracle.bmc.core.requests.UpdateVcnRequest;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
public class VcnService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(VcnService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region != null && !region.isBlank() ? region.trim() : null;
        return new OciClientService(this.buildBasicDTO(ociUser), r);
    }

    public List<Map<String, Object>> listVcns(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            String r = region != null && !region.isBlank() ? region.trim() : null;

            try {
                Object var19;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<Compartment> compartments = client.listAllCompartments();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (Compartment c : compartments) {
                        try {
                            for (Vcn v : client.getVirtualNetworkClient().listVcns(ListVcnsRequest.builder().compartmentId(c.getId()).build()).getItems()) {
                                if (v.getLifecycleState() != LifecycleState.Terminated) {
                                    Map<String, Object> map = new LinkedHashMap<>();
                                    map.put("id", v.getId());
                                    map.put("displayName", v.getDisplayName());
                                    map.put("cidrBlock", v.getCidrBlock());
                                    map.put("cidrBlocks", v.getCidrBlocks());
                                    map.put("ipv6CidrBlocks", v.getIpv6CidrBlocks());
                                    map.put("dnsLabel", v.getDnsLabel());
                                    map.put("vcnDomainName", v.getVcnDomainName());
                                    map.put("lifecycleState", v.getLifecycleState() != null ? v.getLifecycleState().getValue() : null);
                                    map.put("compartmentId", c.getId());
                                    map.put("compartmentName", c.getName());
                                    map.put("timeCreated", v.getTimeCreated() != null ? v.getTimeCreated().toString() : null);
                                    map.put("region", r != null ? r : ociUser.getOciRegion());
                                    result.add(map);
                                }
                            }
                        } catch (Exception var15) {
                            log.debug("listVcns in {} failed: {}", c.getId(), var15.getMessage());
                        }
                    }

                    var19 = result;
                }

                return (List<Map<String, Object>>)var19;
            } catch (OciException var17) {
                throw var17;
            } catch (Exception var18) {
                throw new OciException("查询 VCN 列表失败: " + var18.getMessage());
            }
        }
    }

    public Map<String, Object> createVcn(
        String userId, String compartmentId, String displayName, String cidrBlock, String dnsLabel, boolean createIgw, String region
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var14;
                try (OciClientService client = this.oci(ociUser, region)) {
                    String cid = compartmentId != null && !compartmentId.isBlank() ? compartmentId : client.getProvider().getTenantId();
                    Vcn vcn = client.getVirtualNetworkClient()
                        .createVcn(
                            CreateVcnRequest.builder()
                                .createVcnDetails(
                                    CreateVcnDetails.builder().compartmentId(cid).displayName(displayName).cidrBlock(cidrBlock).dnsLabel(dnsLabel).build()
                                )
                                .build()
                        )
                        .getVcn();
                    String igwId = null;
                    if (createIgw) {
                        try {
                            igwId = client.getVirtualNetworkClient()
                                .createInternetGateway(
                                    CreateInternetGatewayRequest.builder()
                                        .createInternetGatewayDetails(
                                            CreateInternetGatewayDetails.builder()
                                                .compartmentId(cid)
                                                .vcnId(vcn.getId())
                                                .isEnabled(true)
                                                .displayName("default-igw")
                                                .build()
                                        )
                                        .build()
                                )
                                .getInternetGateway()
                                .getId();
                        } catch (Exception var16) {
                            log.warn("createInternetGateway failed: {}", var16.getMessage());
                        }
                    }

                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", vcn.getId());
                    map.put("internetGatewayId", igwId);
                    var14 = map;
                }

                return (Map<String, Object>)var14;
            } catch (OciException var18) {
                throw var18;
            } catch (Exception var19) {
                throw new OciException("创建 VCN 失败: " + var19.getMessage());
            }
        }
    }

    public Map<String, Object> previewVcnDelete(String userId, String vcnId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var9;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    String cid = vcn.getCompartmentId();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(
                        "subnets",
                        this.listMapped(
                            client.getVirtualNetworkClient().listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(),
                            Subnet::getId,
                            Subnet::getDisplayName
                        )
                    );
                    map.put(
                        "internetGateways",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            InternetGateway::getId,
                            InternetGateway::getDisplayName
                        )
                    );
                    map.put(
                        "natGateways",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            NatGateway::getId,
                            NatGateway::getDisplayName
                        )
                    );
                    map.put(
                        "serviceGateways",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            ServiceGateway::getId,
                            ServiceGateway::getDisplayName
                        )
                    );
                    map.put(
                        "localPeeringGateways",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            LocalPeeringGateway::getId,
                            LocalPeeringGateway::getDisplayName
                        )
                    );
                    map.put(
                        "routeTables",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            RouteTable::getId,
                            RouteTable::getDisplayName
                        )
                    );
                    map.put(
                        "securityLists",
                        this.listMapped(
                            client.getVirtualNetworkClient()
                                .listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                                .getItems(),
                            SecurityList::getId,
                            SecurityList::getDisplayName
                        )
                    );
                    var9 = map;
                }

                return (Map<String, Object>)var9;
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("查询 VCN 子资源失败: " + var13.getMessage());
            }
        }
    }

    public void deleteVcn(String userId, String vcnId, boolean cascade, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    if (cascade) {
                        this.cascadeDeleteVcnChildren(client, vcn);
                    }

                    client.getVirtualNetworkClient().deleteVcn(DeleteVcnRequest.builder().vcnId(vcnId).build());
                    log.info("VCN deleted: {}", vcnId);
                }
            } catch (OciException var11) {
                throw var11;
            } catch (BmcException var12) {
                if (var12.getStatusCode() == 409) {
                    throw new OciException("删除 VCN 失败：仍有子资源未清理。" + this.summarizeRemainingVcnChildren(userId, vcnId, region));
                } else {
                    throw new OciException("删除 VCN 失败: " + briefBmcMessage(var12));
                }
            } catch (Exception var13) {
                throw new OciException("删除 VCN 失败: " + var13.getMessage());
            }
        }
    }

    private void cascadeDeleteVcnChildren(OciClientService client, Vcn vcn) {
        String cid = vcn.getCompartmentId();
        String vcnId = vcn.getId();
        VirtualNetworkClient net = client.getVirtualNetworkClient();
        Set<String> gatewayIds = collectVcnGatewayIds(net, cid, vcnId);

        for (Subnet s : net.listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (s.getLifecycleState() != com.oracle.bmc.core.model.Subnet.LifecycleState.Terminated) {
                try {
                    net.deleteSubnet(DeleteSubnetRequest.builder().subnetId(s.getId()).build());
                    log.info("cascade delete subnet: {}", s.getDisplayName());
                } catch (Exception var14) {
                    throw new OciException("级联删除子网失败（" + s.getDisplayName() + "）: " + var14.getMessage() + "。请先终止使用该子网的实例或负载均衡。");
                }
            }
        }

        clearRouteRulesReferencingGateways(net, cid, vcnId, gatewayIds);

        for (InternetGateway ig : net.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (ig.getLifecycleState() != com.oracle.bmc.core.model.InternetGateway.LifecycleState.Terminated) {
                deleteOrThrow(
                    () -> net.deleteInternetGateway(DeleteInternetGatewayRequest.builder().igId(ig.getId()).build()), "Internet Gateway", ig.getDisplayName()
                );
            }
        }

        for (NatGateway ng : net.listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (ng.getLifecycleState() != com.oracle.bmc.core.model.NatGateway.LifecycleState.Terminated) {
                deleteOrThrow(
                    () -> net.deleteNatGateway(DeleteNatGatewayRequest.builder().natGatewayId(ng.getId()).build()), "NAT Gateway", ng.getDisplayName()
                );
            }
        }

        for (ServiceGateway sg : net.listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (sg.getLifecycleState() != com.oracle.bmc.core.model.ServiceGateway.LifecycleState.Terminated) {
                deleteOrThrow(
                    () -> net.deleteServiceGateway(DeleteServiceGatewayRequest.builder().serviceGatewayId(sg.getId()).build()),
                    "Service Gateway",
                    sg.getDisplayName()
                );
            }
        }

        for (LocalPeeringGateway lpg : net.listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
            .getItems()) {
            if (lpg.getLifecycleState() != com.oracle.bmc.core.model.LocalPeeringGateway.LifecycleState.Terminated) {
                deleteOrThrow(
                    () -> net.deleteLocalPeeringGateway(DeleteLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpg.getId()).build()),
                    "Local Peering Gateway",
                    lpg.getDisplayName()
                );
            }
        }

        String defaultRtId = vcn.getDefaultRouteTableId();

        for (RouteTable rt : net.listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (!rt.getId().equals(defaultRtId) && rt.getLifecycleState() != com.oracle.bmc.core.model.RouteTable.LifecycleState.Terminated) {
                try {
                    net.deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rt.getId()).build());
                } catch (Exception var13) {
                    log.debug("deleteRouteTable {} skipped: {}", rt.getDisplayName(), var13.getMessage());
                }
            }
        }

        String defaultSlId = vcn.getDefaultSecurityListId();

        for (SecurityList sl : net.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (!sl.getId().equals(defaultSlId) && sl.getLifecycleState() != com.oracle.bmc.core.model.SecurityList.LifecycleState.Terminated) {
                try {
                    net.deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(sl.getId()).build());
                } catch (Exception var12) {
                    log.debug("deleteSecurityList {} skipped: {}", sl.getDisplayName(), var12.getMessage());
                }
            }
        }
    }

    private static Set<String> collectVcnGatewayIds(VirtualNetworkClient net, String compartmentId, String vcnId) {
        Set<String> ids = new HashSet<>();

        try {
            for (InternetGateway ig : net.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build())
                .getItems()) {
                ids.add(ig.getId());
            }

            for (NatGateway ng : net.listNatGateways(ListNatGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(ng.getId());
            }

            for (ServiceGateway sg : net.listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(sg.getId());
            }

            for (LocalPeeringGateway lpg : net.listLocalPeeringGateways(
                    ListLocalPeeringGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()
                )
                .getItems()) {
                ids.add(lpg.getId());
            }
        } catch (Exception var6) {
            log.warn("collectVcnGatewayIds failed: {}", var6.getMessage());
        }

        return ids;
    }

    private static void clearRouteRulesReferencingGateways(VirtualNetworkClient net, String compartmentId, String vcnId, Set<String> gatewayIds) {
        if (!gatewayIds.isEmpty()) {
            try {
                for (RouteTable rt : net.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                    List<RouteRule> rules = rt.getRouteRules();
                    if (rules != null && !rules.isEmpty()) {
                        List<RouteRule> kept = new ArrayList<>();
                        boolean removed = false;

                        for (RouteRule r : rules) {
                            String target = r.getNetworkEntityId();
                            if (target != null && gatewayIds.contains(target)) {
                                removed = true;
                            } else {
                                kept.add(r);
                            }
                        }

                        if (removed) {
                            net.updateRouteTable(
                                UpdateRouteTableRequest.builder()
                                    .rtId(rt.getId())
                                    .updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(kept).build())
                                    .build()
                            );
                            log.info("cleared gateway routes on route table: {}", rt.getDisplayName());
                        }
                    }
                }
            } catch (Exception var12) {
                throw new OciException("级联删除：清理路由表规则失败: " + var12.getMessage());
            }
        }
    }

    private static void deleteOrThrow(Runnable delete, String resourceType, String displayName) {
        try {
            delete.run();
            log.info("cascade deleted {}: {}", resourceType, displayName);
        } catch (Exception var4) {
            throw new OciException("级联删除 " + resourceType + "（" + displayName + "）失败: " + var4.getMessage());
        }
    }

    private String summarizeRemainingVcnChildren(String userId, String vcnId, String region) {
        try {
            Map<String, Object> left = this.previewVcnDelete(userId, vcnId, region);
            List<String> parts = new ArrayList<>();
            appendRemaining(parts, "子网", left.get("subnets"));
            appendRemaining(parts, "Internet Gateway", left.get("internetGateways"));
            appendRemaining(parts, "NAT Gateway", left.get("natGateways"));
            appendRemaining(parts, "Service Gateway", left.get("serviceGateways"));
            appendRemaining(parts, "Local Peering Gateway", left.get("localPeeringGateways"));
            appendRemaining(parts, "路由表", left.get("routeTables"));
            appendRemaining(parts, "安全列表", left.get("securityLists"));
            return parts.isEmpty() ? "" : " 剩余: " + String.join("；", parts);
        } catch (Exception var6) {
            return "";
        }
    }

    private static void appendRemaining(List<String> parts, String label, Object raw) {
        if (raw instanceof List<?> list && !list.isEmpty()) {
            List<String> names = new ArrayList<>();

            for (Object o : list) {
                if (o instanceof Map) {
                    Map<?, ?> m = (Map<?, ?>)o;
                    Object n = m.get("displayName");
                    if (n != null && !String.valueOf(n).isBlank()) {
                        names.add(String.valueOf(n));
                    }
                }
            }

            if (!names.isEmpty()) {
                parts.add(label + "(" + String.join(", ", names) + ")");
            }

            return;
        }
    }

    private static String briefBmcMessage(BmcException e) {
        String em = e.getMessage();
        if (em == null) {
            return "HTTP " + e.getStatusCode();
        } else {
            int nl = em.indexOf(10);
            return nl > 0 ? em.substring(0, nl) : (em.length() > 240 ? em.substring(0, 240) + "…" : em);
        }
    }

    public List<Map<String, Object>> listSubnets(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            List<Map<String, Object>> list = new ArrayList<>();

            for (Subnet s : client.getVirtualNetworkClient().listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (s.getLifecycleState() != com.oracle.bmc.core.model.Subnet.LifecycleState.Terminated) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", s.getId());
                    m.put("displayName", s.getDisplayName());
                    m.put("cidrBlock", s.getCidrBlock());
                    m.put("ipv6CidrBlock", s.getIpv6CidrBlock());
                    m.put("availabilityDomain", s.getAvailabilityDomain());
                    m.put("prohibitPublicIpOnVnic", s.getProhibitPublicIpOnVnic());
                    m.put("routeTableId", s.getRouteTableId());
                    m.put("dhcpOptionsId", s.getDhcpOptionsId());
                    m.put("securityListIds", s.getSecurityListIds());
                    m.put("lifecycleState", s.getLifecycleState() != null ? s.getLifecycleState().getValue() : null);
                    m.put("timeCreated", s.getTimeCreated() != null ? s.getTimeCreated().toString() : null);
                    list.add(m);
                }
            }

            return list;
        });
    }

    public void createSubnet(
        String userId,
        String vcnId,
        String displayName,
        String cidrBlock,
        String availabilityDomain,
        String routeTableId,
        Boolean prohibitPublicIp,
        String region
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    Builder b = CreateSubnetDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).cidrBlock(cidrBlock);
                    if (availabilityDomain != null && !availabilityDomain.isBlank()) {
                        b.availabilityDomain(availabilityDomain);
                    }

                    if (routeTableId != null && !routeTableId.isBlank()) {
                        b.routeTableId(routeTableId);
                    }

                    if (prohibitPublicIp != null) {
                        b.prohibitPublicIpOnVnic(prohibitPublicIp);
                    }

                    client.getVirtualNetworkClient().createSubnet(CreateSubnetRequest.builder().createSubnetDetails(b.build()).build());
                }
            } catch (OciException var15) {
                throw var15;
            } catch (Exception var16) {
                throw new OciException("创建子网失败: " + var16.getMessage());
            }
        }
    }

    public void deleteSubnet(String userId, String subnetId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteSubnet",
            client -> client.getVirtualNetworkClient().deleteSubnet(DeleteSubnetRequest.builder().subnetId(subnetId).build())
        );
    }

    public void updateSubnet(String userId, String subnetId, String displayName, String routeTableId, List<String> securityListIds, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateSubnetDetails.Builder b = UpdateSubnetDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    if (routeTableId != null && !routeTableId.isBlank()) {
                        b.routeTableId(routeTableId);
                    }

                    if (securityListIds != null && !securityListIds.isEmpty()) {
                        b.securityListIds(securityListIds);
                    }

                    client.getVirtualNetworkClient().updateSubnet(UpdateSubnetRequest.builder().subnetId(subnetId).updateSubnetDetails(b.build()).build());
                }
            } catch (OciException var13) {
                throw var13;
            } catch (Exception var14) {
                throw new OciException("更新子网失败: " + var14.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listInternetGateways(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (InternetGateway ig : client.getVirtualNetworkClient()
                    .listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (ig.getLifecycleState() != com.oracle.bmc.core.model.InternetGateway.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", ig.getId());
                        m.put("displayName", ig.getDisplayName());
                        m.put("isEnabled", ig.getIsEnabled());
                        m.put("lifecycleState", ig.getLifecycleState() != null ? ig.getLifecycleState().getValue() : null);
                        m.put("timeCreated", ig.getTimeCreated() != null ? ig.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void createInternetGateway(String userId, String vcnId, String displayName, boolean enabled, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    client.getVirtualNetworkClient()
                        .createInternetGateway(
                            CreateInternetGatewayRequest.builder()
                                .createInternetGatewayDetails(
                                    CreateInternetGatewayDetails.builder()
                                        .compartmentId(vcn.getCompartmentId())
                                        .vcnId(vcnId)
                                        .displayName(displayName)
                                        .isEnabled(enabled)
                                        .build()
                                )
                                .build()
                        );
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("创建 Internet Gateway 失败: " + var13.getMessage());
            }
        }
    }

    public void deleteInternetGateway(String userId, String igId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteInternetGateway",
            client -> client.getVirtualNetworkClient().deleteInternetGateway(DeleteInternetGatewayRequest.builder().igId(igId).build())
        );
    }

    public void updateInternetGateway(String userId, String igId, String displayName, Boolean isEnabled, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateInternetGatewayDetails.Builder b = UpdateInternetGatewayDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    if (isEnabled != null) {
                        b.isEnabled(isEnabled);
                    }

                    client.getVirtualNetworkClient()
                        .updateInternetGateway(UpdateInternetGatewayRequest.builder().igId(igId).updateInternetGatewayDetails(b.build()).build());
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("更新 IGW 失败: " + var13.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listNatGateways(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (NatGateway ng : client.getVirtualNetworkClient()
                    .listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (ng.getLifecycleState() != com.oracle.bmc.core.model.NatGateway.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", ng.getId());
                        m.put("displayName", ng.getDisplayName());
                        m.put("natIp", ng.getNatIp());
                        m.put("blockTraffic", ng.getBlockTraffic());
                        m.put("lifecycleState", ng.getLifecycleState() != null ? ng.getLifecycleState().getValue() : null);
                        m.put("timeCreated", ng.getTimeCreated() != null ? ng.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void createNatGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    client.getVirtualNetworkClient()
                        .createNatGateway(
                            CreateNatGatewayRequest.builder()
                                .createNatGatewayDetails(
                                    CreateNatGatewayDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).build()
                                )
                                .build()
                        );
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("创建 NAT Gateway 失败: " + var12.getMessage());
            }
        }
    }

    public void deleteNatGateway(String userId, String natId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteNatGateway",
            client -> client.getVirtualNetworkClient().deleteNatGateway(DeleteNatGatewayRequest.builder().natGatewayId(natId).build())
        );
    }

    public void updateNatGateway(String userId, String natId, String displayName, Boolean blockTraffic, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateNatGatewayDetails.Builder b = UpdateNatGatewayDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    if (blockTraffic != null) {
                        b.blockTraffic(blockTraffic);
                    }

                    client.getVirtualNetworkClient()
                        .updateNatGateway(UpdateNatGatewayRequest.builder().natGatewayId(natId).updateNatGatewayDetails(b.build()).build());
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("更新 NAT 失败: " + var13.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listServiceGateways(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (ServiceGateway sg : client.getVirtualNetworkClient()
                    .listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (sg.getLifecycleState() != com.oracle.bmc.core.model.ServiceGateway.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", sg.getId());
                        m.put("displayName", sg.getDisplayName());
                        m.put("blockTraffic", sg.getBlockTraffic());
                        m.put("services", sg.getServices());
                        m.put("lifecycleState", sg.getLifecycleState() != null ? sg.getLifecycleState().getValue() : null);
                        m.put("timeCreated", sg.getTimeCreated() != null ? sg.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void createServiceGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    List<Service> services = client.getVirtualNetworkClient().listServices(ListServicesRequest.builder().build()).getItems();
                    List<ServiceIdRequestDetails> serviceIds = new ArrayList<>();

                    for (Service s : services) {
                        if (s.getName() != null && s.getName().toLowerCase().contains("all") && s.getName().toLowerCase().contains("services")) {
                            serviceIds.add(ServiceIdRequestDetails.builder().serviceId(s.getId()).build());
                            break;
                        }
                    }

                    client.getVirtualNetworkClient()
                        .createServiceGateway(
                            CreateServiceGatewayRequest.builder()
                                .createServiceGatewayDetails(
                                    CreateServiceGatewayDetails.builder()
                                        .compartmentId(vcn.getCompartmentId())
                                        .vcnId(vcnId)
                                        .displayName(displayName)
                                        .services(serviceIds)
                                        .build()
                                )
                                .build()
                        );
                }
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                throw new OciException("创建 Service Gateway 失败: " + var15.getMessage());
            }
        }
    }

    public void deleteServiceGateway(String userId, String sgId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteServiceGateway",
            client -> client.getVirtualNetworkClient().deleteServiceGateway(DeleteServiceGatewayRequest.builder().serviceGatewayId(sgId).build())
        );
    }

    public void updateServiceGateway(String userId, String sgId, String displayName, Boolean blockTraffic, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateServiceGatewayDetails.Builder b = UpdateServiceGatewayDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    if (blockTraffic != null) {
                        b.blockTraffic(blockTraffic);
                    }

                    client.getVirtualNetworkClient()
                        .updateServiceGateway(UpdateServiceGatewayRequest.builder().serviceGatewayId(sgId).updateServiceGatewayDetails(b.build()).build());
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("更新 SG 失败: " + var13.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listRouteTables(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (RouteTable rt : client.getVirtualNetworkClient()
                    .listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (rt.getLifecycleState() != com.oracle.bmc.core.model.RouteTable.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", rt.getId());
                        m.put("displayName", rt.getDisplayName());
                        List<Map<String, Object>> rules = new ArrayList<>();
                        if (rt.getRouteRules() != null) {
                            for (RouteRule r : rt.getRouteRules()) {
                                Map<String, Object> rr = new LinkedHashMap<>();
                                rr.put("destination", r.getDestination());
                                rr.put("destinationType", r.getDestinationType() != null ? r.getDestinationType().getValue() : null);
                                rr.put("networkEntityId", r.getNetworkEntityId());
                                rr.put("description", r.getDescription());
                                rules.add(rr);
                            }
                        }

                        m.put("routeRules", rules);
                        m.put("lifecycleState", rt.getLifecycleState() != null ? rt.getLifecycleState().getValue() : null);
                        m.put("timeCreated", rt.getTimeCreated() != null ? rt.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void deleteRouteTable(String userId, String rtId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteRouteTable",
            client -> client.getVirtualNetworkClient().deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rtId).build())
        );
    }

    public void updateRouteTable(String userId, String rtId, String displayName, List<Map<String, Object>> routeRules, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateRouteTableDetails.Builder b = UpdateRouteTableDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    if (routeRules != null) {
                        List<RouteRule> rules = new ArrayList<>();

                        for (Map<String, Object> r : routeRules) {
                            com.oracle.bmc.core.model.RouteRule.Builder rb = RouteRule.builder()
                                .destination(this.asStr(r.get("destination")))
                                .networkEntityId(this.asStr(r.get("networkEntityId")))
                                .description(this.asStr(r.get("description")));
                            String dstType = this.asStr(r.get("destinationType"));
                            if (dstType != null && !dstType.isBlank()) {
                                try {
                                    rb.destinationType(DestinationType.create(dstType));
                                } catch (Exception var16) {
                                }
                            }

                            rules.add(rb.build());
                        }

                        b.routeRules(rules);
                    }

                    client.getVirtualNetworkClient().updateRouteTable(UpdateRouteTableRequest.builder().rtId(rtId).updateRouteTableDetails(b.build()).build());
                }
            } catch (OciException var18) {
                throw var18;
            } catch (Exception var19) {
                throw new OciException("更新路由表失败: " + var19.getMessage());
            }
        }
    }

    public Map<String, Object> getRouteTable(String userId, String rtId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var16;
                try (OciClientService client = this.oci(ociUser, region)) {
                    RouteTable rt = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(rtId).build()).getRouteTable();
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rt.getId());
                    m.put("displayName", rt.getDisplayName());
                    m.put("lifecycleState", rt.getLifecycleState() != null ? rt.getLifecycleState().getValue() : null);
                    List<Map<String, Object>> rules = new ArrayList<>();
                    if (rt.getRouteRules() != null) {
                        for (RouteRule r : rt.getRouteRules()) {
                            Map<String, Object> rr = new LinkedHashMap<>();
                            rr.put("destination", r.getDestination());
                            rr.put("destinationType", r.getDestinationType() != null ? r.getDestinationType().getValue() : null);
                            rr.put("networkEntityId", r.getNetworkEntityId());
                            rr.put("description", r.getDescription());
                            rules.add(rr);
                        }
                    }

                    m.put("routeRules", rules);
                    var16 = m;
                }

                return (Map<String, Object>)var16;
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                throw new OciException("查询路由表失败: " + var15.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listVcnGateways(String userId, String vcnId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var22;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    String cid = vcn.getCompartmentId();
                    List<Map<String, Object>> result = new ArrayList<>();

                    try {
                        for (InternetGateway ig : client.getVirtualNetworkClient()
                            .listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                            .getItems()) {
                            if (ig.getLifecycleState() != com.oracle.bmc.core.model.InternetGateway.LifecycleState.Terminated) {
                                result.add(Map.of("id", ig.getId(), "displayName", ig.getDisplayName(), "type", "internetGateway"));
                            }
                        }
                    } catch (Exception var12) {
                    }

                    try {
                        for (NatGateway ng : client.getVirtualNetworkClient()
                            .listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                            .getItems()) {
                            if (ng.getLifecycleState() != com.oracle.bmc.core.model.NatGateway.LifecycleState.Terminated) {
                                result.add(Map.of("id", ng.getId(), "displayName", ng.getDisplayName(), "type", "natGateway"));
                            }
                        }
                    } catch (Exception var13) {
                    }

                    try {
                        for (ServiceGateway sg : client.getVirtualNetworkClient()
                            .listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                            .getItems()) {
                            if (sg.getLifecycleState() != com.oracle.bmc.core.model.ServiceGateway.LifecycleState.Terminated) {
                                result.add(Map.of("id", sg.getId(), "displayName", sg.getDisplayName(), "type", "serviceGateway"));
                            }
                        }
                    } catch (Exception var14) {
                    }

                    try {
                        for (LocalPeeringGateway lpg : client.getVirtualNetworkClient()
                            .listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                            .getItems()) {
                            if (lpg.getLifecycleState() != com.oracle.bmc.core.model.LocalPeeringGateway.LifecycleState.Terminated) {
                                result.add(Map.of("id", lpg.getId(), "displayName", lpg.getDisplayName(), "type", "localPeeringGateway"));
                            }
                        }
                    } catch (Exception var15) {
                    }

                    var22 = result;
                }

                return (List<Map<String, Object>>)var22;
            } catch (OciException var17) {
                throw var17;
            } catch (Exception var18) {
                throw new OciException("查询 VCN 网关失败: " + var18.getMessage());
            }
        }
    }

    public Map<String, Object> getSecurityList(String userId, String slId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var21;
                try (OciClientService client = this.oci(ociUser, region)) {
                    SecurityList sl = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build())
                        .getSecurityList();
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", sl.getId());
                    m.put("displayName", sl.getDisplayName());
                    m.put("lifecycleState", sl.getLifecycleState() != null ? sl.getLifecycleState().getValue() : null);
                    List<Map<String, Object>> ingress = new ArrayList<>();
                    int idx = 0;
                    if (sl.getIngressSecurityRules() != null) {
                        for (IngressSecurityRule r : sl.getIngressSecurityRules()) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("index", idx++);
                            map.put("direction", "ingress");
                            map.put("protocol", r.getProtocol());
                            map.put("source", r.getSource());
                            map.put("sourceType", r.getSourceType() != null ? r.getSourceType().getValue() : null);
                            map.put("isStateless", r.getIsStateless());
                            map.put("description", r.getDescription());
                            map.put("portRange", this.portRangeLabel(r.getTcpOptions(), r.getUdpOptions()));
                            ingress.add(map);
                        }
                    }

                    List<Map<String, Object>> egress = new ArrayList<>();
                    idx = 0;
                    if (sl.getEgressSecurityRules() != null) {
                        for (EgressSecurityRule r : sl.getEgressSecurityRules()) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("index", idx++);
                            map.put("direction", "egress");
                            map.put("protocol", r.getProtocol());
                            map.put("destination", r.getDestination());
                            map.put("destinationType", r.getDestinationType() != null ? r.getDestinationType().getValue() : null);
                            map.put("isStateless", r.getIsStateless());
                            map.put("description", r.getDescription());
                            map.put("portRange", this.portRangeLabel(r.getTcpOptions(), r.getUdpOptions()));
                            egress.add(map);
                        }
                    }

                    m.put("ingressSecurityRules", ingress);
                    m.put("egressSecurityRules", egress);
                    var21 = m;
                }

                return (Map<String, Object>)var21;
            } catch (OciException var16) {
                throw var16;
            } catch (Exception var17) {
                throw new OciException("查询安全列表失败: " + var17.getMessage());
            }
        }
    }

    private String portRangeLabel(TcpOptions tcp, UdpOptions udp) {
        if (tcp != null && tcp.getDestinationPortRange() != null) {
            return tcp.getDestinationPortRange().getMin() + "-" + tcp.getDestinationPortRange().getMax();
        } else {
            return udp != null && udp.getDestinationPortRange() != null
                ? udp.getDestinationPortRange().getMin() + "-" + udp.getDestinationPortRange().getMax()
                : "all";
        }
    }

    public void addSecurityListRule(
        String userId, String slId, String direction, String protocol, String source, String portMin, String portMax, String description, String region
    ) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            if (description != null && description.isBlank()) {
                description = null;
            }

            boolean ingress = !"egress".equalsIgnoreCase(direction);

            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    SecurityList sl = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build())
                        .getSecurityList();
                    List<IngressSecurityRule> ingressRules = new ArrayList<>(sl.getIngressSecurityRules());
                    List<EgressSecurityRule> egressRules = new ArrayList<>(sl.getEgressSecurityRules());
                    TcpOptions tcpOpt = null;
                    UdpOptions udpOpt = null;
                    if (("6".equals(protocol) || "17".equals(protocol)) && portMin != null && !portMin.isBlank()) {
                        int min = Integer.parseInt(portMin);
                        int max = portMax != null && !portMax.isBlank() ? Integer.parseInt(portMax) : min;
                        PortRange pr = PortRange.builder().min(min).max(max).build();
                        if ("6".equals(protocol)) {
                            tcpOpt = TcpOptions.builder().destinationPortRange(pr).build();
                        } else {
                            udpOpt = UdpOptions.builder().destinationPortRange(pr).build();
                        }
                    }

                    String src = source != null && !source.isBlank() ? source : "0.0.0.0/0";
                    boolean isIpv6 = src.contains(":");
                    if (ingress) {
                        com.oracle.bmc.core.model.IngressSecurityRule.Builder b = IngressSecurityRule.builder()
                            .source(src)
                            .protocol(protocol != null && !protocol.isBlank() ? protocol : "all")
                            .description(description);
                        if (isIpv6) {
                            b.sourceType(SourceType.CidrBlock);
                        }

                        if (tcpOpt != null) {
                            b.tcpOptions(tcpOpt);
                        }

                        if (udpOpt != null) {
                            b.udpOptions(udpOpt);
                        }

                        ingressRules.add(b.build());
                    } else {
                        com.oracle.bmc.core.model.EgressSecurityRule.Builder bx = EgressSecurityRule.builder()
                            .destination(src)
                            .protocol(protocol != null && !protocol.isBlank() ? protocol : "all")
                            .description(description);
                        if (isIpv6) {
                            bx.destinationType(com.oracle.bmc.core.model.EgressSecurityRule.DestinationType.CidrBlock);
                        }

                        if (tcpOpt != null) {
                            bx.tcpOptions(tcpOpt);
                        }

                        if (udpOpt != null) {
                            bx.udpOptions(udpOpt);
                        }

                        egressRules.add(bx.build());
                    }

                    client.getVirtualNetworkClient()
                        .updateSecurityList(
                            UpdateSecurityListRequest.builder()
                                .securityListId(slId)
                                .updateSecurityListDetails(
                                    UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()
                                )
                                .build()
                        );
                }
            } catch (OciException var23) {
                throw var23;
            } catch (Exception var24) {
                throw new OciException("添加安全规则失败: " + var24.getMessage());
            }
        }
    }

    public void deleteSecurityListRule(String userId, String slId, String direction, int ruleIndex, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            boolean ingress = !"egress".equalsIgnoreCase(direction);

            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    SecurityList sl = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build())
                        .getSecurityList();
                    List<IngressSecurityRule> ingressRules = new ArrayList<>(sl.getIngressSecurityRules());
                    List<EgressSecurityRule> egressRules = new ArrayList<>(sl.getEgressSecurityRules());
                    if (ingress) {
                        if (ruleIndex < 0 || ruleIndex >= ingressRules.size()) {
                            throw new OciException("入站规则索引越界");
                        }

                        ingressRules.remove(ruleIndex);
                    } else {
                        if (ruleIndex < 0 || ruleIndex >= egressRules.size()) {
                            throw new OciException("出站规则索引越界");
                        }

                        egressRules.remove(ruleIndex);
                    }

                    client.getVirtualNetworkClient()
                        .updateSecurityList(
                            UpdateSecurityListRequest.builder()
                                .securityListId(slId)
                                .updateSecurityListDetails(
                                    UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()
                                )
                                .build()
                        );
                }
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                throw new OciException("删除安全规则失败: " + var15.getMessage());
            }
        }
    }

    public void setupIgwDefaultRoutes(String userId, String vcnId, String igwId, boolean addIpv6, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    String defaultRtId = vcn.getDefaultRouteTableId();
                    if (defaultRtId == null) {
                        throw new OciException("未找到 VCN 的默认路由表");
                    }

                    RouteTable rt = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(defaultRtId).build()).getRouteTable();
                    List<RouteRule> rules = rt.getRouteRules() == null ? new ArrayList<>() : new ArrayList<>(rt.getRouteRules());
                    boolean hasIpv4 = rules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getDestination()) && igwId.equals(r.getNetworkEntityId()));
                    boolean hasIpv6 = rules.stream().anyMatch(r -> "::/0".equals(r.getDestination()) && igwId.equals(r.getNetworkEntityId()));
                    if (!hasIpv4) {
                        rules.add(
                            RouteRule.builder()
                                .destination("0.0.0.0/0")
                                .destinationType(DestinationType.CidrBlock)
                                .networkEntityId(igwId)
                                .description("Default IPv4 route via IGW")
                                .build()
                        );
                    }

                    if (addIpv6 && !hasIpv6) {
                        rules.add(
                            RouteRule.builder()
                                .destination("::/0")
                                .destinationType(DestinationType.CidrBlock)
                                .networkEntityId(igwId)
                                .description("Default IPv6 route via IGW")
                                .build()
                        );
                    }

                    client.getVirtualNetworkClient()
                        .updateRouteTable(
                            UpdateRouteTableRequest.builder()
                                .rtId(defaultRtId)
                                .updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build())
                                .build()
                        );
                }
            } catch (OciException var16) {
                throw var16;
            } catch (Exception var17) {
                throw new OciException("配置 IGW 默认路由失败: " + var17.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listSecurityLists(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (SecurityList sl : client.getVirtualNetworkClient()
                    .listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (sl.getLifecycleState() != com.oracle.bmc.core.model.SecurityList.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", sl.getId());
                        m.put("displayName", sl.getDisplayName());
                        m.put("ingressRulesCount", sl.getIngressSecurityRules() != null ? sl.getIngressSecurityRules().size() : 0);
                        m.put("egressRulesCount", sl.getEgressSecurityRules() != null ? sl.getEgressSecurityRules().size() : 0);
                        m.put("lifecycleState", sl.getLifecycleState() != null ? sl.getLifecycleState().getValue() : null);
                        m.put("timeCreated", sl.getTimeCreated() != null ? sl.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void deleteSecurityList(String userId, String slId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteSecurityList",
            client -> client.getVirtualNetworkClient().deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(slId).build())
        );
    }

    public List<Map<String, Object>> listDrgs(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var16;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (Compartment c : client.listAllCompartments()) {
                        try {
                            for (Drg d : client.getVirtualNetworkClient().listDrgs(ListDrgsRequest.builder().compartmentId(c.getId()).build()).getItems()) {
                                if (d.getLifecycleState() != com.oracle.bmc.core.model.Drg.LifecycleState.Terminated) {
                                    Map<String, Object> m = new LinkedHashMap<>();
                                    m.put("id", d.getId());
                                    m.put("displayName", d.getDisplayName());
                                    m.put("compartmentId", c.getId());
                                    m.put("compartmentName", c.getName());
                                    m.put("lifecycleState", d.getLifecycleState() != null ? d.getLifecycleState().getValue() : null);
                                    m.put("timeCreated", d.getTimeCreated() != null ? d.getTimeCreated().toString() : null);
                                    result.add(m);
                                }
                            }
                        } catch (Exception var12) {
                        }
                    }

                    var16 = result;
                }

                return (List<Map<String, Object>>)var16;
            } catch (OciException var14) {
                throw var14;
            } catch (Exception var15) {
                throw new OciException("查询 DRG 失败: " + var15.getMessage());
            }
        }
    }

    public void createDrg(String userId, String compartmentId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    String cid = compartmentId != null && !compartmentId.isBlank() ? compartmentId : client.getProvider().getTenantId();
                    client.getVirtualNetworkClient()
                        .createDrg(
                            CreateDrgRequest.builder().createDrgDetails(CreateDrgDetails.builder().compartmentId(cid).displayName(displayName).build()).build()
                        );
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("创建 DRG 失败: " + var12.getMessage());
            }
        }
    }

    public void deleteDrg(String userId, String drgId, String region) {
        this.deleteResource(
            userId, region, () -> "deleteDrg", client -> client.getVirtualNetworkClient().deleteDrg(DeleteDrgRequest.builder().drgId(drgId).build())
        );
    }

    public List<Map<String, Object>> listLocalPeeringGateways(String userId, String vcnId, String region) {
        return this.listChildren(
            userId,
            vcnId,
            region,
            (client, cid) -> {
                List<Map<String, Object>> list = new ArrayList<>();

                for (LocalPeeringGateway lpg : client.getVirtualNetworkClient()
                    .listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build())
                    .getItems()) {
                    if (lpg.getLifecycleState() != com.oracle.bmc.core.model.LocalPeeringGateway.LifecycleState.Terminated) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", lpg.getId());
                        m.put("displayName", lpg.getDisplayName());
                        m.put("peeringStatus", lpg.getPeeringStatus() != null ? lpg.getPeeringStatus().getValue() : null);
                        m.put("peerAdvertisedCidr", lpg.getPeerAdvertisedCidr());
                        m.put("lifecycleState", lpg.getLifecycleState() != null ? lpg.getLifecycleState().getValue() : null);
                        m.put("timeCreated", lpg.getTimeCreated() != null ? lpg.getTimeCreated().toString() : null);
                        list.add(m);
                    }
                }

                return list;
            }
        );
    }

    public void createLocalPeeringGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    client.getVirtualNetworkClient()
                        .createLocalPeeringGateway(
                            CreateLocalPeeringGatewayRequest.builder()
                                .createLocalPeeringGatewayDetails(
                                    CreateLocalPeeringGatewayDetails.builder()
                                        .compartmentId(vcn.getCompartmentId())
                                        .vcnId(vcnId)
                                        .displayName(displayName)
                                        .build()
                                )
                                .build()
                        );
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("创建 LPG 失败: " + var12.getMessage());
            }
        }
    }

    public void connectLocalPeeringGateway(String userId, String lpgId, String peerId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient()
                        .connectLocalPeeringGateways(
                            ConnectLocalPeeringGatewaysRequest.builder()
                                .localPeeringGatewayId(lpgId)
                                .connectLocalPeeringGatewaysDetails(ConnectLocalPeeringGatewaysDetails.builder().peerId(peerId).build())
                                .build()
                        );
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("连接 LPG 失败: " + var12.getMessage());
            }
        }
    }

    public void deleteLocalPeeringGateway(String userId, String lpgId, String region) {
        this.deleteResource(
            userId,
            region,
            () -> "deleteLocalPeeringGateway",
            client -> client.getVirtualNetworkClient()
                    .deleteLocalPeeringGateway(DeleteLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpgId).build())
        );
    }

    private List<Map<String, Object>> listChildren(String userId, String vcnId, String region, VcnService.ChildrenFetcher fetcher) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                List var8;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                    var8 = fetcher.fetch(client, vcn.getCompartmentId());
                }

                return var8;
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("查询子资源失败: " + var12.getMessage());
            }
        }
    }

    private void deleteResource(String userId, String region, VcnService.OpName op, VcnService.ClientAction action) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    action.run(client);
                    log.info("{} succeeded", op.get());
                }
            } catch (OciException var11) {
                throw var11;
            } catch (BmcException var12) {
                if (var12.getStatusCode() == 409) {
                    throw new OciException("资源仍被引用或正在使用，无法删除");
                } else {
                    throw new OciException(op.get() + " 失败: " + (var12.getMessage() != null ? var12.getMessage() : "未知错误"));
                }
            } catch (Exception var13) {
                throw new OciException(op.get() + " 失败: " + var13.getMessage());
            }
        }
    }

    private <T> List<Map<String, String>> listMapped(List<T> items, Function<T, String> idFn, Function<T, String> nameFn) {
        List<Map<String, String>> list = new ArrayList<>();
        if (items == null) {
            return list;
        } else {
            for (T it : items) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("id", idFn.apply(it));
                m.put("displayName", nameFn.apply(it));
                list.add(m);
            }

            return list;
        }
    }

    private String asStr(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    public void updateVcn(String userId, String vcnId, String displayName, String dnsLabel, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateVcnDetails.Builder b = UpdateVcnDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    client.getVirtualNetworkClient().updateVcn(UpdateVcnRequest.builder().vcnId(vcnId).updateVcnDetails(b.build()).build());
                }
            } catch (OciException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new OciException("更新 VCN 失败: " + var13.getMessage());
            }
        }
    }

    public void updateLocalPeeringGateway(String userId, String lpgId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.UpdateLocalPeeringGatewayDetails.Builder b = UpdateLocalPeeringGatewayDetails.builder();
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName);
                    }

                    client.getVirtualNetworkClient()
                        .updateLocalPeeringGateway(
                            UpdateLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpgId).updateLocalPeeringGatewayDetails(b.build()).build()
                        );
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException("更新 LPG 失败: " + var12.getMessage());
            }
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

    @FunctionalInterface
    private interface ChildrenFetcher {
        List<Map<String, Object>> fetch(OciClientService client, String compartmentId);
    }

    @FunctionalInterface
    private interface ClientAction {
        void run(OciClientService client);
    }

    @FunctionalInterface
    private interface OpName {
        String get();
    }
}
