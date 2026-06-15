/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.NetworkService
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.util.VcnIpv6Util
 *  com.oracle.bmc.core.VirtualNetwork
 *  com.oracle.bmc.core.model.CreatePublicIpDetails
 *  com.oracle.bmc.core.model.CreatePublicIpDetails$Lifetime
 *  com.oracle.bmc.core.model.EgressSecurityRule
 *  com.oracle.bmc.core.model.EgressSecurityRule$Builder
 *  com.oracle.bmc.core.model.EgressSecurityRule$DestinationType
 *  com.oracle.bmc.core.model.GetPublicIpByPrivateIpIdDetails
 *  com.oracle.bmc.core.model.IngressSecurityRule
 *  com.oracle.bmc.core.model.IngressSecurityRule$Builder
 *  com.oracle.bmc.core.model.IngressSecurityRule$SourceType
 *  com.oracle.bmc.core.model.PortRange
 *  com.oracle.bmc.core.model.PrivateIp
 *  com.oracle.bmc.core.model.PublicIp
 *  com.oracle.bmc.core.model.SecurityList
 *  com.oracle.bmc.core.model.Subnet
 *  com.oracle.bmc.core.model.TcpOptions
 *  com.oracle.bmc.core.model.UdpOptions
 *  com.oracle.bmc.core.model.UpdateSecurityListDetails
 *  com.oracle.bmc.core.model.Vcn
 *  com.oracle.bmc.core.model.Vnic
 *  com.oracle.bmc.core.model.VnicAttachment
 *  com.oracle.bmc.core.requests.CreatePublicIpRequest
 *  com.oracle.bmc.core.requests.DeletePrivateIpRequest
 *  com.oracle.bmc.core.requests.DeletePublicIpRequest
 *  com.oracle.bmc.core.requests.GetPublicIpByPrivateIpIdRequest
 *  com.oracle.bmc.core.requests.GetSecurityListRequest
 *  com.oracle.bmc.core.requests.GetSubnetRequest
 *  com.oracle.bmc.core.requests.GetVnicRequest
 *  com.oracle.bmc.core.requests.ListPrivateIpsRequest
 *  com.oracle.bmc.core.requests.ListVnicAttachmentsRequest
 *  com.oracle.bmc.core.requests.UpdateSecurityListRequest
 *  com.oracle.bmc.identity.model.Compartment
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciClientService;
import com.ocxworker.util.VcnIpv6Util;
import com.oracle.bmc.core.VirtualNetwork;
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
import java.io.Serializable;
import java.util.ArrayList;
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
public class NetworkService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);
    @Resource
    private OciUserMapper userMapper;

    private String tag(OciUser u) {
        return "[" + u.getUsername() + "] ";
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region == null || region.isBlank() ? null : region.trim();
        return new OciClientService(this.buildDTO(ociUser), r);
    }

    private String firstSecurityListId(Subnet subnet) {
        if (subnet.getSecurityListIds() == null || subnet.getSecurityListIds().isEmpty()) {
            throw new OciException("\u5b50\u7f51\u672a\u5173\u8054\u5b89\u5168\u5217\u8868");
        }
        return (String)subnet.getSecurityListIds().get(0);
    }

    public List<Map<String, Object>> listVcns(String userId, String region) {
        ArrayList arrayList;
        block12: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            String r = region == null || region.isBlank() ? null : region.trim();
            OciClientService client = this.oci(ociUser, region);
            try {
                List compartments = client.listAllCompartments();
                LinkedHashMap<String, String> compartmentNameMap = new LinkedHashMap<String, String>();
                for (Compartment c : compartments) {
                    compartmentNameMap.put(c.getId(), c.getName());
                }
                ArrayList result = new ArrayList();
                for (Compartment compartment : compartments) {
                    List vcns = client.listVcnInCompartment(compartment.getId());
                    for (Vcn vcn : vcns) {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put("id", vcn.getId());
                        map.put("displayName", vcn.getDisplayName());
                        map.put("cidrBlocks", vcn.getCidrBlocks());
                        map.put("state", vcn.getLifecycleState().getValue());
                        map.put("compartmentId", vcn.getCompartmentId());
                        map.put("compartmentName", compartmentNameMap.getOrDefault(vcn.getCompartmentId(), "unknown"));
                        map.put("timeCreated", vcn.getTimeCreated() != null ? vcn.getTimeCreated().toString() : null);
                        map.put("region", r != null ? r : ociUser.getOciRegion());
                        List subnets = client.listSubnets(vcn.getId());
                        map.put("subnets", subnets.stream().map(s -> Map.of("id", s.getId(), "displayName", s.getDisplayName(), "cidrBlock", s.getCidrBlock(), "isPublic", s.getProhibitInternetIngress() == false)).toList());
                        result.add(map);
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
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6VCN\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public List<Map<String, Object>> listSecurityRulesByInstance(String userId, String instanceId, String region) {
        ArrayList arrayList;
        block19: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                LinkedHashMap<String, Object> map;
                String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                SecurityList secList = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(this.firstSecurityListId(subnet)).build()).getSecurityList();
                ArrayList result = new ArrayList();
                for (IngressSecurityRule rule : secList.getIngressSecurityRules()) {
                    map = new LinkedHashMap<String, Object>();
                    map.put("direction", "ingress");
                    map.put("protocol", rule.getProtocol());
                    map.put("source", rule.getSource());
                    map.put("description", rule.getDescription());
                    if (rule.getTcpOptions() != null && rule.getTcpOptions().getDestinationPortRange() != null) {
                        map.put("portRange", rule.getTcpOptions().getDestinationPortRange().getMin() + "-" + rule.getTcpOptions().getDestinationPortRange().getMax());
                    } else if (rule.getUdpOptions() != null && rule.getUdpOptions().getDestinationPortRange() != null) {
                        map.put("portRange", rule.getUdpOptions().getDestinationPortRange().getMin() + "-" + rule.getUdpOptions().getDestinationPortRange().getMax());
                    } else {
                        map.put("portRange", "all");
                    }
                    result.add(map);
                }
                for (IngressSecurityRule rule : secList.getEgressSecurityRules()) {
                    map = new LinkedHashMap();
                    map.put("direction", "egress");
                    map.put("protocol", rule.getProtocol());
                    map.put("source", rule.getDestination());
                    map.put("description", rule.getDescription());
                    if (rule.getTcpOptions() != null && rule.getTcpOptions().getDestinationPortRange() != null) {
                        map.put("portRange", rule.getTcpOptions().getDestinationPortRange().getMin() + "-" + rule.getTcpOptions().getDestinationPortRange().getMax());
                    } else if (rule.getUdpOptions() != null && rule.getUdpOptions().getDestinationPortRange() != null) {
                        map.put("portRange", rule.getUdpOptions().getDestinationPortRange().getMin() + "-" + rule.getUdpOptions().getDestinationPortRange().getMax());
                    } else {
                        map.put("portRange", "all");
                    }
                    result.add(map);
                }
                arrayList = result;
                if (client == null) break block19;
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
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5b89\u5168\u89c4\u5219\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public Map<String, Object> releaseAllPortsByInstance(String userId, String instanceId, String region) {
        Map<String, Object> map;
        block15: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                boolean ipv6Capable = VcnIpv6Util.isEnabled((VirtualNetwork)client.getVirtualNetworkClient(), (Subnet)subnet);
                String secListId = this.firstSecurityListId(subnet);
                SecurityList secList = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
                ArrayList<IngressSecurityRule> ingressRules = new ArrayList<IngressSecurityRule>(secList.getIngressSecurityRules());
                boolean hasIpv4Ingress = ingressRules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getSource()) && "all".equals(r.getProtocol()));
                boolean hasIpv6Ingress = ingressRules.stream().anyMatch(r -> "::/0".equals(r.getSource()) && "all".equals(r.getProtocol()));
                if (!hasIpv4Ingress) {
                    ingressRules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("all").description("Allow all IPv4 ingress").build());
                }
                if (ipv6Capable && !hasIpv6Ingress) {
                    ingressRules.add(IngressSecurityRule.builder().source("::/0").sourceType(IngressSecurityRule.SourceType.CidrBlock).protocol("all").description("Allow all IPv6 ingress").build());
                }
                ArrayList<EgressSecurityRule> egressRules = new ArrayList<EgressSecurityRule>(secList.getEgressSecurityRules());
                boolean hasIpv4Egress = egressRules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getDestination()) && "all".equals(r.getProtocol()));
                boolean hasIpv6Egress = egressRules.stream().anyMatch(r -> "::/0".equals(r.getDestination()) && "all".equals(r.getProtocol()));
                if (!hasIpv4Egress) {
                    egressRules.add(EgressSecurityRule.builder().destination("0.0.0.0/0").protocol("all").description("Allow all IPv4 egress").build());
                }
                if (ipv6Capable && !hasIpv6Egress) {
                    egressRules.add(EgressSecurityRule.builder().destination("::/0").destinationType(EgressSecurityRule.DestinationType.CidrBlock).protocol("all").description("Allow all IPv6 egress").build());
                }
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()).build());
                if (ipv6Capable) {
                    log.info("Released all ports (IPv4+IPv6) for subnet: {}", (Object)subnetId);
                } else {
                    log.info("Released all ports (IPv4 only, VCN has no IPv6 CIDR) for subnet: {}", (Object)subnetId);
                }
                map = Map.of("ipv6RulesApplied", ipv6Capable);
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
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u653e\u884c\u7aef\u53e3\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public Map<String, Object> releaseOciPresetByInstance(String userId, String instanceId, String region) {
        Map<String, Object> map;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                String subnetId = this.getSubnetIdFromInstance(client, instanceId);
                Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
                boolean ipv6Capable = VcnIpv6Util.isEnabled((VirtualNetwork)client.getVirtualNetworkClient(), (Subnet)subnet);
                String secListId = this.firstSecurityListId(subnet);
                List ingressRules = NetworkService.buildTcpPresetIngressRules((boolean)ipv6Capable);
                List egressRules = NetworkService.buildTcpPresetEgressRules((boolean)ipv6Capable);
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()).build());
                if (ipv6Capable) {
                    log.info("Applied TCP preset rules (IPv4+IPv6) for subnet: {}", (Object)subnetId);
                } else {
                    log.info("Applied TCP preset rules (IPv4 only, VCN has no IPv6 CIDR) for subnet: {}", (Object)subnetId);
                }
                map = Map.of("ipv6RulesApplied", ipv6Capable);
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
                    throw new OciException(this.tag(ociUser) + "\u5e94\u7528\u9884\u8bbe\u89c4\u5219\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    private static List<IngressSecurityRule> buildTcpPresetIngressRules(boolean ipv6Capable) {
        ArrayList<IngressSecurityRule> rules = new ArrayList<IngressSecurityRule>();
        rules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("6").description("TCP traffic for ports: All").build());
        rules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("1").description("ICMP traffic for: All").build());
        if (ipv6Capable) {
            rules.add(IngressSecurityRule.builder().source("::/0").sourceType(IngressSecurityRule.SourceType.CidrBlock).protocol("6").description("TCP traffic for ports: All").build());
            rules.add(IngressSecurityRule.builder().source("::/0").sourceType(IngressSecurityRule.SourceType.CidrBlock).protocol("1").description("ICMP traffic for: All").build());
            rules.add(IngressSecurityRule.builder().source("::/0").sourceType(IngressSecurityRule.SourceType.CidrBlock).protocol("58").description("IPv6-ICMP traffic for: All").build());
        }
        return rules;
    }

    private static List<EgressSecurityRule> buildTcpPresetEgressRules(boolean ipv6Capable) {
        ArrayList<EgressSecurityRule> rules = new ArrayList<EgressSecurityRule>();
        rules.add(EgressSecurityRule.builder().destination("0.0.0.0/0").protocol("all").description("Allow all egress").build());
        if (ipv6Capable) {
            rules.add(EgressSecurityRule.builder().destination("::/0").destinationType(EgressSecurityRule.DestinationType.CidrBlock).protocol("all").description("Allow all egress").build());
        }
        return rules;
    }

    public void addSecurityRule(String userId, String instanceId, String direction, String protocol, String source, String portMin, String portMax, String description, String region) {
        OciUser ociUser;
        if (description != null && description.isBlank()) {
            description = null;
        }
        if ((ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId))) == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            String proto;
            String subnetId = this.getSubnetIdFromInstance(client, instanceId);
            Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
            String secListId = this.firstSecurityListId(subnet);
            SecurityList secList = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
            switch (protocol == null ? "" : protocol.toUpperCase()) {
                case "TCP": {
                    String string = "6";
                    break;
                }
                case "UDP": {
                    String string = "17";
                    break;
                }
                case "ICMP": {
                    String string = "1";
                    break;
                }
                case "ICMPV6": 
                case "ICMP-IPV6": {
                    String string = "58";
                    break;
                }
                default: {
                    String string = proto = "all";
                }
            }
            if ("ingress".equalsIgnoreCase(direction)) {
                rules = new ArrayList<Object>(secList.getIngressSecurityRules());
                IngressSecurityRule.Builder ruleBuilder = IngressSecurityRule.builder().source(source != null ? source : "0.0.0.0/0").protocol(proto).description(description);
                if (("6".equals(proto) || "17".equals(proto)) && portMin != null && portMax != null) {
                    PortRange range = PortRange.builder().min(Integer.valueOf(Integer.parseInt(portMin))).max(Integer.valueOf(Integer.parseInt(portMax))).build();
                    if ("6".equals(proto)) {
                        ruleBuilder.tcpOptions(TcpOptions.builder().destinationPortRange(range).build());
                    } else {
                        ruleBuilder.udpOptions(UdpOptions.builder().destinationPortRange(range).build());
                    }
                }
                rules.add(ruleBuilder.build());
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(rules).egressSecurityRules(secList.getEgressSecurityRules()).build()).build());
            } else {
                rules = new ArrayList(secList.getEgressSecurityRules());
                EgressSecurityRule.Builder egressBuilder = EgressSecurityRule.builder().destination(source != null ? source : "0.0.0.0/0").protocol(proto).description(description);
                if (("6".equals(proto) || "17".equals(proto)) && portMin != null && portMax != null) {
                    PortRange range = PortRange.builder().min(Integer.valueOf(Integer.parseInt(portMin))).max(Integer.valueOf(Integer.parseInt(portMax))).build();
                    if ("6".equals(proto)) {
                        egressBuilder.tcpOptions(TcpOptions.builder().destinationPortRange(range).build());
                    } else {
                        egressBuilder.udpOptions(UdpOptions.builder().destinationPortRange(range).build());
                    }
                }
                rules.add(egressBuilder.build());
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(secList.getIngressSecurityRules()).egressSecurityRules(rules).build()).build());
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u6dfb\u52a0\u5b89\u5168\u89c4\u5219\u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteSecurityRule(String userId, String instanceId, String direction, int ruleIndex, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            String subnetId = this.getSubnetIdFromInstance(client, instanceId);
            Subnet subnet = client.getVirtualNetworkClient().getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
            String secListId = this.firstSecurityListId(subnet);
            SecurityList secList = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
            if ("ingress".equalsIgnoreCase(direction)) {
                ArrayList rules = new ArrayList(secList.getIngressSecurityRules());
                if (ruleIndex < 0 || ruleIndex >= rules.size()) {
                    throw new OciException("\u89c4\u5219\u7d22\u5f15\u65e0\u6548");
                }
                rules.remove(ruleIndex);
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(rules).egressSecurityRules(secList.getEgressSecurityRules()).build()).build());
            } else {
                ArrayList rules = new ArrayList(secList.getEgressSecurityRules());
                if (ruleIndex < 0 || ruleIndex >= rules.size()) {
                    throw new OciException("\u89c4\u5219\u7d22\u5f15\u65e0\u6548");
                }
                rules.remove(ruleIndex);
                client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(secList.getIngressSecurityRules()).egressSecurityRules(rules).build()).build());
            }
            log.info("Deleted {} security rule at index {} for instance {}", new Object[]{direction, ruleIndex, instanceId});
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u5b89\u5168\u89c4\u5219\u5931\u8d25: " + e.getMessage());
        }
    }

    public void changePublicIp(String userId, String instanceId, List<String> cidrFilters, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            List attachments = client.getComputeClient().listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build()).getItems();
            if (attachments.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230 VNIC");
            }
            VnicAttachment attachment = (VnicAttachment)attachments.get(0);
            Vnic vnic = client.getVirtualNetworkClient().getVnic(GetVnicRequest.builder().vnicId(attachment.getVnicId()).build()).getVnic();
            List privateIps = client.getVirtualNetworkClient().listPrivateIps(ListPrivateIpsRequest.builder().vnicId(vnic.getId()).build()).getItems();
            if (privateIps.isEmpty()) {
                throw new OciException("\u672a\u627e\u5230\u79c1\u6709IP");
            }
            PrivateIp primaryPrivateIp = privateIps.stream().filter(p -> Boolean.TRUE.equals(p.getIsPrimary())).findFirst().orElse((PrivateIp)privateIps.get(0));
            try {
                PublicIp oldPubIp = client.getVirtualNetworkClient().getPublicIpByPrivateIpId(GetPublicIpByPrivateIpIdRequest.builder().getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(primaryPrivateIp.getId()).build()).build()).getPublicIp();
                if (oldPubIp != null) {
                    client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(oldPubIp.getId()).build());
                }
            }
            catch (Exception oldPubIp) {
                // empty catch block
            }
            PublicIp newPubIp = client.getVirtualNetworkClient().createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(CreatePublicIpDetails.builder().compartmentId(client.getCompartmentId()).lifetime(CreatePublicIpDetails.Lifetime.Ephemeral).privateIpId(primaryPrivateIp.getId()).build()).build()).getPublicIp();
            log.info("Changed IP for instance {}: {}", (Object)instanceId, (Object)newPubIp.getIpAddress());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u66f4\u6362IP\u5931\u8d25: " + e.getMessage());
        }
    }

    public void deletePublicIpByPrivateIpId(String userId, String privateIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            PublicIp pubIp = client.getVirtualNetworkClient().getPublicIpByPrivateIpId(GetPublicIpByPrivateIpIdRequest.builder().getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(privateIpId).build()).build()).getPublicIp();
            if (pubIp != null) {
                client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(pubIp.getId()).build());
                log.info("Deleted public IP {} from private IP {}", (Object)pubIp.getIpAddress(), (Object)privateIpId);
            }
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u516c\u7f51IP\u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteSecondaryIp(String userId, String privateIpId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            try {
                PublicIp pubIp = client.getVirtualNetworkClient().getPublicIpByPrivateIpId(GetPublicIpByPrivateIpIdRequest.builder().getPublicIpByPrivateIpIdDetails(GetPublicIpByPrivateIpIdDetails.builder().privateIpId(privateIpId).build()).build()).getPublicIp();
                if (pubIp != null) {
                    client.getVirtualNetworkClient().deletePublicIp(DeletePublicIpRequest.builder().publicIpId(pubIp.getId()).build());
                    log.info("Deleted public IP {} before removing secondary private IP", (Object)pubIp.getIpAddress());
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            client.getVirtualNetworkClient().deletePrivateIp(DeletePrivateIpRequest.builder().privateIpId(privateIpId).build());
            log.info("Deleted secondary private IP {}", (Object)privateIpId);
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u8f85\u52a9IP\u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, String> assignEphemeralPublicIp(String userId, String instanceId, String privateIpId, String region) {
        Map<String, String> map;
        block11: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                PublicIp newPubIp = client.getVirtualNetworkClient().createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(CreatePublicIpDetails.builder().compartmentId(client.getCompartmentId()).lifetime(CreatePublicIpDetails.Lifetime.Ephemeral).privateIpId(privateIpId).build()).build()).getPublicIp();
                log.info("Assigned ephemeral IP {} to private IP {}", (Object)newPubIp.getIpAddress(), (Object)privateIpId);
                map = Map.of("publicIp", newPubIp.getIpAddress());
                if (client == null) break block11;
            }
            catch (Throwable newPubIp) {
                try {
                    if (client != null) {
                        try {
                            client.close();
                        }
                        catch (Throwable throwable) {
                            newPubIp.addSuppressed(throwable);
                        }
                    }
                    throw newPubIp;
                }
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && msg.contains("LimitExceeded")) {
                        throw new OciException(this.tag(ociUser) + "\u516c\u7f51 IP \u914d\u989d\u5df2\u6ee1\uff0c\u65e0\u6cd5\u5206\u914d\u66f4\u591a\u516c\u7f51 IP");
                    }
                    throw new OciException(this.tag(ociUser) + "\u5206\u914d\u516c\u7f51IP\u5931\u8d25: " + msg);
                }
            }
            client.close();
        }
        return map;
    }

    private String getSubnetIdFromInstance(OciClientService client, String instanceId) {
        List attachments = client.getComputeClient().listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(client.getCompartmentId()).instanceId(instanceId).build()).getItems();
        if (attachments.isEmpty()) {
            throw new OciException("\u672a\u627e\u5230\u5b9e\u4f8b\u7684 VNIC");
        }
        return ((VnicAttachment)attachments.get(0)).getSubnetId();
    }

    private SysUserDTO buildDTO(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }
}

