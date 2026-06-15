package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.util.VcnIpv6Util;
import com.oracle.bmc.core.model.CreatePublicIpDetails;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.GetPublicIpByPrivateIpIdDetails;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.PortRange;
import com.oracle.bmc.core.model.PrivateIp;
import com.oracle.bmc.core.model.PublicIp;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.TcpOptions;
import com.oracle.bmc.core.model.UdpOptions;
import com.oracle.bmc.core.model.UpdateSecurityListDetails;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.model.VnicAttachment;
import com.oracle.bmc.core.model.CreatePublicIpDetails.Lifetime;
import com.oracle.bmc.core.model.EgressSecurityRule.DestinationType;
import com.oracle.bmc.core.model.IngressSecurityRule.Builder;
import com.oracle.bmc.core.model.IngressSecurityRule.SourceType;
import com.oracle.bmc.core.requests.CreatePublicIpRequest;
import com.oracle.bmc.core.requests.DeletePrivateIpRequest;
import com.oracle.bmc.core.requests.DeletePublicIpRequest;
import com.oracle.bmc.core.requests.GetPublicIpByPrivateIpIdRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVnicRequest;
import com.oracle.bmc.core.requests.ListPrivateIpsRequest;
import com.oracle.bmc.core.requests.ListVnicAttachmentsRequest;
import com.oracle.bmc.core.requests.UpdateSecurityListRequest;
import com.oracle.bmc.identity.model.Compartment;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetworkService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    @Resource
    private OciUserMapper userMapper;

    private String tag(OciUser u) {
        return "[" + u.getUsername() + "] ";
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region != null && !region.isBlank() ? region.trim() : null;
        return new OciClientService(this.buildDTO(ociUser), r);
    }

    private String firstSecurityListId(Subnet subnet) {
        if (subnet.getSecurityListIds() != null && !subnet.getSecurityListIds().isEmpty()) {
            return (String)subnet.getSecurityListIds().get(0);
        } else {
            throw new OciException("子网未关联安全列表");
        }
    }

    public List<Map<String, Object>> listVcns(String userId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            String r = region != null && !region.isBlank() ? region.trim() : null;

            try {
                Object var21;
                try (OciClientService client = this.oci(ociUser, region)) {
                    List<Compartment> compartments = client.listAllCompartments();
                    Map<String, String> compartmentNameMap = new LinkedHashMap<>();

                    for (Compartment c : compartments) {
                        compartmentNameMap.put(c.getId(), c.getName());
                    }

                    List<Map<String, Object>> result = new ArrayList<>();

                    for (Compartment compartment : compartments) {
                        for (Vcn vcn : client.listVcnInCompartment(compartment.getId())) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("id", vcn.getId());
                            map.put("displayName", vcn.getDisplayName());
                            map.put("cidrBlocks", vcn.getCidrBlocks());
                            map.put("state", vcn.getLifecycleState().getValue());
                            map.put("compartmentId", vcn.getCompartmentId());
                            map.put("compartmentName", compartmentNameMap.getOrDefault(vcn.getCompartmentId(), "unknown"));
                            map.put("timeCreated", vcn.getTimeCreated() != null ? vcn.getTimeCreated().toString() : null);
                            map.put("region", r != null ? r : ociUser.getOciRegion());
                            List<Subnet> subnets = client.listSubnets(vcn.getId());
                            map.put(
                                "subnets",
                                subnets.stream()
                                    .map(
                                        s -> Map.of(
                                                "id",
                                                s.getId(),
                                                "displayName",
                                                s.getDisplayName(),
                                                "cidrBlock",
                                                s.getCidrBlock(),
                                                "isPublic",
                                                !s.getProhibitInternetIngress()
                                            )
                                    )
                                    .toList()
                            );
                            result.add(map);
                        }
                    }

                    var21 = result;
                }

                return (List<Map<String, Object>>)var21;
            } catch (Exception var18) {
                throw new OciException(this.tag(ociUser) + "获取VCN列表失败: " + var18.getMessage());
            }
        }
    }

    public List<Map<String, Object>> listSecurityRulesByInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Object var17;
                try (OciClientService client = this.oci(ociUser, region)) {
                    String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    SecurityList secList = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(this.firstSecurityListId(subnet)).build())
                        .getSecurityList();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (IngressSecurityRule rule : secList.getIngressSecurityRules()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("direction", "ingress");
                        map.put("protocol", rule.getProtocol());
                        map.put("source", rule.getSource());
                        map.put("description", rule.getDescription());
                        if (rule.getTcpOptions() != null && rule.getTcpOptions().getDestinationPortRange() != null) {
                            map.put(
                                "portRange",
                                rule.getTcpOptions().getDestinationPortRange().getMin() + "-" + rule.getTcpOptions().getDestinationPortRange().getMax()
                            );
                        } else if (rule.getUdpOptions() != null && rule.getUdpOptions().getDestinationPortRange() != null) {
                            map.put(
                                "portRange",
                                rule.getUdpOptions().getDestinationPortRange().getMin() + "-" + rule.getUdpOptions().getDestinationPortRange().getMax()
                            );
                        } else {
                            map.put("portRange", "all");
                        }

                        result.add(map);
                    }

                    for (EgressSecurityRule rule : secList.getEgressSecurityRules()) {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("direction", "egress");
                        map.put("protocol", rule.getProtocol());
                        map.put("source", rule.getDestination());
                        map.put("description", rule.getDescription());
                        if (rule.getTcpOptions() != null && rule.getTcpOptions().getDestinationPortRange() != null) {
                            map.put(
                                "portRange",
                                rule.getTcpOptions().getDestinationPortRange().getMin() + "-" + rule.getTcpOptions().getDestinationPortRange().getMax()
                            );
                        } else if (rule.getUdpOptions() != null && rule.getUdpOptions().getDestinationPortRange() != null) {
                            map.put(
                                "portRange",
                                rule.getUdpOptions().getDestinationPortRange().getMin() + "-" + rule.getUdpOptions().getDestinationPortRange().getMax()
                            );
                        } else {
                            map.put("portRange", "all");
                        }

                        result.add(map);
                    }

                    var17 = result;
                }

                return (List<Map<String, Object>>)var17;
            } catch (Exception var15) {
                throw new OciException(this.tag(ociUser) + "获取安全规则失败: " + var15.getMessage());
            }
        }
    }

    public Map<String, Object> releaseAllPortsByInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Map var17;
                try (OciClientService client = this.oci(ociUser, region)) {
                    String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    boolean ipv6Capable = VcnIpv6Util.isEnabled(client.getVirtualNetworkClient(), subnet);
                    String secListId = this.firstSecurityListId(subnet);
                    SecurityList secList = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build())
                        .getSecurityList();
                    List<IngressSecurityRule> ingressRules = new ArrayList<>(secList.getIngressSecurityRules());
                    boolean hasIpv4Ingress = ingressRules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getSource()) && "all".equals(r.getProtocol()));
                    boolean hasIpv6Ingress = ingressRules.stream().anyMatch(r -> "::/0".equals(r.getSource()) && "all".equals(r.getProtocol()));
                    if (!hasIpv4Ingress) {
                        ingressRules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("all").description("Allow all IPv4 ingress").build());
                    }

                    if (ipv6Capable && !hasIpv6Ingress) {
                        ingressRules.add(
                            IngressSecurityRule.builder()
                                .source("::/0")
                                .sourceType(SourceType.CidrBlock)
                                .protocol("all")
                                .description("Allow all IPv6 ingress")
                                .build()
                        );
                    }

                    List<EgressSecurityRule> egressRules = new ArrayList<>(secList.getEgressSecurityRules());
                    boolean hasIpv4Egress = egressRules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getDestination()) && "all".equals(r.getProtocol()));
                    boolean hasIpv6Egress = egressRules.stream().anyMatch(r -> "::/0".equals(r.getDestination()) && "all".equals(r.getProtocol()));
                    if (!hasIpv4Egress) {
                        egressRules.add(EgressSecurityRule.builder().destination("0.0.0.0/0").protocol("all").description("Allow all IPv4 egress").build());
                    }

                    if (ipv6Capable && !hasIpv6Egress) {
                        egressRules.add(
                            EgressSecurityRule.builder()
                                .destination("::/0")
                                .destinationType(DestinationType.CidrBlock)
                                .protocol("all")
                                .description("Allow all IPv6 egress")
                                .build()
                        );
                    }

                    client.getVirtualNetworkClient()
                        .updateSecurityList(
                            UpdateSecurityListRequest.builder()
                                .securityListId(secListId)
                                .updateSecurityListDetails(
                                    UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()
                                )
                                .build()
                        );
                    if (ipv6Capable) {
                        log.info("Released all ports (IPv4+IPv6) for subnet: {}", subnetId);
                    } else {
                        log.info("Released all ports (IPv4 only, VCN has no IPv6 CIDR) for subnet: {}", subnetId);
                    }

                    var17 = Map.of("ipv6RulesApplied", ipv6Capable);
                }

                return var17;
            } catch (Exception var20) {
                throw new OciException(this.tag(ociUser) + "放行端口失败: " + var20.getMessage());
            }
        }
    }

    public Map<String, Object> releaseOciPresetByInstance(String userId, String instanceId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Map var12;
                try (OciClientService client = this.oci(ociUser, region)) {
                    String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    boolean ipv6Capable = VcnIpv6Util.isEnabled(client.getVirtualNetworkClient(), subnet);
                    String secListId = this.firstSecurityListId(subnet);
                    List<IngressSecurityRule> ingressRules = buildTcpPresetIngressRules(ipv6Capable);
                    List<EgressSecurityRule> egressRules = buildTcpPresetEgressRules(ipv6Capable);
                    client.getVirtualNetworkClient()
                        .updateSecurityList(
                            UpdateSecurityListRequest.builder()
                                .securityListId(secListId)
                                .updateSecurityListDetails(
                                    UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()
                                )
                                .build()
                        );
                    if (ipv6Capable) {
                        log.info("Applied TCP preset rules (IPv4+IPv6) for subnet: {}", subnetId);
                    } else {
                        log.info("Applied TCP preset rules (IPv4 only, VCN has no IPv6 CIDR) for subnet: {}", subnetId);
                    }

                    var12 = Map.of("ipv6RulesApplied", ipv6Capable);
                }

                return var12;
            } catch (Exception var15) {
                throw new OciException(this.tag(ociUser) + "应用预设规则失败: " + var15.getMessage());
            }
        }
    }

    private static List<IngressSecurityRule> buildTcpPresetIngressRules(boolean ipv6Capable) {
        List<IngressSecurityRule> rules = new ArrayList<>();
        rules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("6").description("TCP traffic for ports: All").build());
        rules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("1").description("ICMP traffic for: All").build());
        if (ipv6Capable) {
            rules.add(
                IngressSecurityRule.builder().source("::/0").sourceType(SourceType.CidrBlock).protocol("6").description("TCP traffic for ports: All").build()
            );
            rules.add(IngressSecurityRule.builder().source("::/0").sourceType(SourceType.CidrBlock).protocol("1").description("ICMP traffic for: All").build());
            rules.add(
                IngressSecurityRule.builder().source("::/0").sourceType(SourceType.CidrBlock).protocol("58").description("IPv6-ICMP traffic for: All").build()
            );
        }

        return rules;
    }

    private static List<EgressSecurityRule> buildTcpPresetEgressRules(boolean ipv6Capable) {
        List<EgressSecurityRule> rules = new ArrayList<>();
        rules.add(EgressSecurityRule.builder().destination("0.0.0.0/0").protocol("all").description("Allow all egress").build());
        if (ipv6Capable) {
            rules.add(
                EgressSecurityRule.builder()
                    .destination("::/0")
                    .destinationType(DestinationType.CidrBlock)
                    .protocol("all")
                    .description("Allow all egress")
                    .build()
            );
        }

        return rules;
    }

    public void addSecurityRule(
        String userId, String instanceId, String direction, String protocol, String source, String portMin, String portMax, String description, String region
    ) {
        if (description != null && description.isBlank()) {
            description = null;
        }

        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    String secListId = this.firstSecurityListId(subnet);
                    SecurityList secList = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build())
                        .getSecurityList();
                    String rules = protocol == null ? "" : protocol.toUpperCase();

                    String proto = switch (rules) {
                        case "TCP" -> "6";
                        case "UDP" -> "17";
                        case "ICMP" -> "1";
                        case "ICMPV6", "ICMP-IPV6" -> "58";
                        default -> "all";
                    };
                    if ("ingress".equalsIgnoreCase(direction)) {
                        List<IngressSecurityRule> ingressRules = new ArrayList<>(secList.getIngressSecurityRules());
                        Builder ruleBuilder = IngressSecurityRule.builder()
                            .source(source != null ? source : "0.0.0.0/0")
                            .protocol(proto)
                            .description(description);
                        if (("6".equals(proto) || "17".equals(proto)) && portMin != null && portMax != null) {
                            PortRange range = PortRange.builder().min(Integer.parseInt(portMin)).max(Integer.parseInt(portMax)).build();
                            if ("6".equals(proto)) {
                                ruleBuilder.tcpOptions(TcpOptions.builder().destinationPortRange(range).build());
                            } else {
                                ruleBuilder.udpOptions(UdpOptions.builder().destinationPortRange(range).build());
                            }
                        }

                        ingressRules.add(ruleBuilder.build());
                        client.getVirtualNetworkClient()
                            .updateSecurityList(
                                UpdateSecurityListRequest.builder()
                                    .securityListId(secListId)
                                    .updateSecurityListDetails(
                                        UpdateSecurityListDetails.builder()
                                            .ingressSecurityRules(ingressRules)
                                            .egressSecurityRules(secList.getEgressSecurityRules())
                                            .build()
                                    )
                                    .build()
                            );
                    } else {
                        List<EgressSecurityRule> egressRules = new ArrayList<>(secList.getEgressSecurityRules());
                        com.oracle.bmc.core.model.EgressSecurityRule.Builder egressBuilder = EgressSecurityRule.builder()
                            .destination(source != null ? source : "0.0.0.0/0")
                            .protocol(proto)
                            .description(description);
                        if (("6".equals(proto) || "17".equals(proto)) && portMin != null && portMax != null) {
                            PortRange range = PortRange.builder().min(Integer.parseInt(portMin)).max(Integer.parseInt(portMax)).build();
                            if ("6".equals(proto)) {
                                egressBuilder.tcpOptions(TcpOptions.builder().destinationPortRange(range).build());
                            } else {
                                egressBuilder.udpOptions(UdpOptions.builder().destinationPortRange(range).build());
                            }
                        }

                        egressRules.add(egressBuilder.build());
                        client.getVirtualNetworkClient()
                            .updateSecurityList(
                                UpdateSecurityListRequest.builder()
                                    .securityListId(secListId)
                                    .updateSecurityListDetails(
                                        UpdateSecurityListDetails.builder()
                                            .ingressSecurityRules(secList.getIngressSecurityRules())
                                            .egressSecurityRules(egressRules)
                                            .build()
                                    )
                                    .build()
                            );
                    }
                }
            } catch (OciException var22) {
                throw var22;
            } catch (Exception var23) {
                throw new OciException(this.tag(ociUser) + "添加安全规则失败: " + var23.getMessage());
            }
        }
    }

    public void deleteSecurityRule(String userId, String instanceId, String direction, int ruleIndex, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                    Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                    String secListId = this.firstSecurityListId(subnet);
                    SecurityList secList = client.getVirtualNetworkClient()
                        .getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build())
                        .getSecurityList();
                    if ("ingress".equalsIgnoreCase(direction)) {
                        List<IngressSecurityRule> ingressRules2 = new ArrayList<>(secList.getIngressSecurityRules());
                        if (ruleIndex < 0 || ruleIndex >= ingressRules2.size()) {
                            throw new OciException("规则索引无效");
                        }

                        ingressRules2.remove(ruleIndex);
                        client.getVirtualNetworkClient()
                            .updateSecurityList(
                                UpdateSecurityListRequest.builder()
                                    .securityListId(secListId)
                                    .updateSecurityListDetails(
                                        UpdateSecurityListDetails.builder()
                                            .ingressSecurityRules(ingressRules2)
                                            .egressSecurityRules(secList.getEgressSecurityRules())
                                            .build()
                                    )
                                    .build()
                            );
                    } else {
                        List<EgressSecurityRule> egressRules2 = new ArrayList<>(secList.getEgressSecurityRules());
                        if (ruleIndex < 0 || ruleIndex >= egressRules2.size()) {
                            throw new OciException("规则索引无效");
                        }

                        egressRules2.remove(ruleIndex);
                        client.getVirtualNetworkClient()
                            .updateSecurityList(
                                UpdateSecurityListRequest.builder()
                                    .securityListId(secListId)
                                    .updateSecurityListDetails(
                                        UpdateSecurityListDetails.builder()
                                            .ingressSecurityRules(secList.getIngressSecurityRules())
                                            .egressSecurityRules(egressRules2)
                                            .build()
                                    )
                                    .build()
                            );
                    }

                    log.info("Deleted {} security rule at index {} for instance {}", new Object[]{direction, ruleIndex, instanceId});
                }
            } catch (OciException var15) {
                throw var15;
            } catch (Exception var16) {
                throw new OciException(this.tag(ociUser) + "删除安全规则失败: " + var16.getMessage());
            }
        }
    }

    public void changePublicIp(String userId, String instanceId, List<String> cidrFilters, String region) {
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
                        throw new OciException("未找到 VNIC");
                    }

                    VnicAttachment attachment = attachments.get(0);
                    Vnic vnic = client.getVirtualNetworkClient().getVnic(GetVnicRequest.builder().vnicId(attachment.getVnicId()).build()).getVnic();
                    List<PrivateIp> privateIps = client.getVirtualNetworkClient()
                        .listPrivateIps(ListPrivateIpsRequest.builder().vnicId(vnic.getId()).build())
                        .getItems();
                    if (privateIps.isEmpty()) {
                        throw new OciException("未找到私有IP");
                    }

                    PrivateIp primaryPrivateIp = privateIps.stream().filter(p -> Boolean.TRUE.equals(p.getIsPrimary())).findFirst().orElse(privateIps.get(0));

                    try {
                        PublicIp oldPubIp = client.getVirtualNetworkClient()
                            .getPublicIpByPrivateIpId(
                                GetPublicIpByPrivateIpIdRequest.builder()
                                    .getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(primaryPrivateIp.getId()).build())
                                    .build()
                            )
                            .getPublicIp();
                        if (oldPubIp != null) {
                            client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(oldPubIp.getId()).build());
                        }
                    } catch (Exception var14) {
                    }

                    PublicIp newPubIp = client.getVirtualNetworkClient()
                        .createPublicIp(
                            CreatePublicIpRequest.builder()
                                .createPublicIpDetails(
                                    CreatePublicIpDetails.builder()
                                        .compartmentId(client.getCompartmentId())
                                        .lifetime(Lifetime.Ephemeral)
                                        .privateIpId(primaryPrivateIp.getId())
                                        .build()
                                )
                                .build()
                        )
                        .getPublicIp();
                    log.info("Changed IP for instance {}: {}", instanceId, newPubIp.getIpAddress());
                }
            } catch (OciException var16) {
                throw var16;
            } catch (Exception var17) {
                throw new OciException(this.tag(ociUser) + "更换IP失败: " + var17.getMessage());
            }
        }
    }

    public void deletePublicIpByPrivateIpId(String userId, String privateIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    PublicIp pubIp = client.getVirtualNetworkClient()
                        .getPublicIpByPrivateIpId(
                            GetPublicIpByPrivateIpIdRequest.builder()
                                .getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(privateIpId).build())
                                .build()
                        )
                        .getPublicIp();
                    if (pubIp != null) {
                        client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(pubIp.getId()).build());
                        log.info("Deleted public IP {} from private IP {}", pubIp.getIpAddress(), privateIpId);
                    }
                }
            } catch (OciException var10) {
                throw var10;
            } catch (Exception var11) {
                throw new OciException(this.tag(ociUser) + "删除公网IP失败: " + var11.getMessage());
            }
        }
    }

    public void deleteSecondaryIp(String userId, String privateIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    try {
                        PublicIp pubIp = client.getVirtualNetworkClient()
                            .getPublicIpByPrivateIpId(
                                GetPublicIpByPrivateIpIdRequest.builder()
                                    .getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(privateIpId).build())
                                    .build()
                            )
                            .getPublicIp();
                        if (pubIp != null) {
                            client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(pubIp.getId()).build());
                            log.info("Deleted public IP {} before removing secondary private IP", pubIp.getIpAddress());
                        }
                    } catch (Exception var9) {
                    }

                    client.getVirtualNetworkClient().deletePrivateIp(DeletePrivateIpRequest.builder().privateIpId(privateIpId).build());
                    log.info("Deleted secondary private IP {}", privateIpId);
                }
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                throw new OciException(this.tag(ociUser) + "删除辅助IP失败: " + var12.getMessage());
            }
        }
    }

    public Map<String, String> assignEphemeralPublicIp(String userId, String instanceId, String privateIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            try {
                Map var8;
                try (OciClientService client = this.oci(ociUser, region)) {
                    PublicIp newPubIp = client.getVirtualNetworkClient()
                        .createPublicIp(
                            CreatePublicIpRequest.builder()
                                .createPublicIpDetails(
                                    CreatePublicIpDetails.builder()
                                        .compartmentId(client.getCompartmentId())
                                        .lifetime(Lifetime.Ephemeral)
                                        .privateIpId(privateIpId)
                                        .build()
                                )
                                .build()
                        )
                        .getPublicIp();
                    log.info("Assigned ephemeral IP {} to private IP {}", newPubIp.getIpAddress(), privateIpId);
                    var8 = Map.of("publicIp", newPubIp.getIpAddress());
                }

                return var8;
            } catch (OciException var11) {
                throw var11;
            } catch (Exception var12) {
                String msg = var12.getMessage();
                if (msg != null && msg.contains("LimitExceeded")) {
                    throw new OciException(this.tag(ociUser) + "公网 IP 配额已满，无法分配更多公网 IP");
                } else {
                    throw new OciException(this.tag(ociUser) + "分配公网IP失败: " + msg);
                }
            }
        }
    }

    private String getSubnetIdFromInstance(OciClientService client, String instanceId) {
        List<VnicAttachment> attachments = client.getComputeClient()
            .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build())
            .getItems();
        if (attachments.isEmpty()) {
            throw new OciException("未找到实例的 VNIC");
        } else {
            return attachments.get(0).getSubnetId();
        }
    }

    private SysUserDTO buildDTO(OciUser ociUser) {
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
