/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.VcnService
 *  com.ocxworker.service.VcnService$ChildrenFetcher
 *  com.ocxworker.service.VcnService$ClientAction
 *  com.ocxworker.service.VcnService$OpName
 *  com.oracle.bmc.core.VirtualNetworkClient
 *  com.oracle.bmc.core.model.ConnectLocalPeeringGatewaysDetails
 *  com.oracle.bmc.core.model.CreateDrgDetails
 *  com.oracle.bmc.core.model.CreateInternetGatewayDetails
 *  com.oracle.bmc.core.model.CreateLocalPeeringGatewayDetails
 *  com.oracle.bmc.core.model.CreateNatGatewayDetails
 *  com.oracle.bmc.core.model.CreateServiceGatewayDetails
 *  com.oracle.bmc.core.model.CreateSubnetDetails
 *  com.oracle.bmc.core.model.CreateSubnetDetails$Builder
 *  com.oracle.bmc.core.model.CreateVcnDetails
 *  com.oracle.bmc.core.model.Drg
 *  com.oracle.bmc.core.model.Drg$LifecycleState
 *  com.oracle.bmc.core.model.EgressSecurityRule
 *  com.oracle.bmc.core.model.EgressSecurityRule$DestinationType
 *  com.oracle.bmc.core.model.IngressSecurityRule
 *  com.oracle.bmc.core.model.IngressSecurityRule$SourceType
 *  com.oracle.bmc.core.model.InternetGateway
 *  com.oracle.bmc.core.model.InternetGateway$LifecycleState
 *  com.oracle.bmc.core.model.LocalPeeringGateway
 *  com.oracle.bmc.core.model.LocalPeeringGateway$LifecycleState
 *  com.oracle.bmc.core.model.NatGateway
 *  com.oracle.bmc.core.model.NatGateway$LifecycleState
 *  com.oracle.bmc.core.model.PortRange
 *  com.oracle.bmc.core.model.RouteRule
 *  com.oracle.bmc.core.model.RouteRule$Builder
 *  com.oracle.bmc.core.model.RouteRule$DestinationType
 *  com.oracle.bmc.core.model.RouteTable
 *  com.oracle.bmc.core.model.RouteTable$LifecycleState
 *  com.oracle.bmc.core.model.SecurityList
 *  com.oracle.bmc.core.model.SecurityList$LifecycleState
 *  com.oracle.bmc.core.model.Service
 *  com.oracle.bmc.core.model.ServiceGateway
 *  com.oracle.bmc.core.model.ServiceGateway$LifecycleState
 *  com.oracle.bmc.core.model.ServiceIdRequestDetails
 *  com.oracle.bmc.core.model.Subnet
 *  com.oracle.bmc.core.model.Subnet$LifecycleState
 *  com.oracle.bmc.core.model.TcpOptions
 *  com.oracle.bmc.core.model.UdpOptions
 *  com.oracle.bmc.core.model.UpdateInternetGatewayDetails
 *  com.oracle.bmc.core.model.UpdateInternetGatewayDetails$Builder
 *  com.oracle.bmc.core.model.UpdateLocalPeeringGatewayDetails
 *  com.oracle.bmc.core.model.UpdateLocalPeeringGatewayDetails$Builder
 *  com.oracle.bmc.core.model.UpdateNatGatewayDetails
 *  com.oracle.bmc.core.model.UpdateNatGatewayDetails$Builder
 *  com.oracle.bmc.core.model.UpdateRouteTableDetails
 *  com.oracle.bmc.core.model.UpdateRouteTableDetails$Builder
 *  com.oracle.bmc.core.model.UpdateSecurityListDetails
 *  com.oracle.bmc.core.model.UpdateServiceGatewayDetails
 *  com.oracle.bmc.core.model.UpdateServiceGatewayDetails$Builder
 *  com.oracle.bmc.core.model.UpdateSubnetDetails
 *  com.oracle.bmc.core.model.UpdateSubnetDetails$Builder
 *  com.oracle.bmc.core.model.UpdateVcnDetails
 *  com.oracle.bmc.core.model.UpdateVcnDetails$Builder
 *  com.oracle.bmc.core.model.Vcn
 *  com.oracle.bmc.core.model.Vcn$LifecycleState
 *  com.oracle.bmc.core.requests.ConnectLocalPeeringGatewaysRequest
 *  com.oracle.bmc.core.requests.CreateDrgRequest
 *  com.oracle.bmc.core.requests.CreateInternetGatewayRequest
 *  com.oracle.bmc.core.requests.CreateLocalPeeringGatewayRequest
 *  com.oracle.bmc.core.requests.CreateNatGatewayRequest
 *  com.oracle.bmc.core.requests.CreateServiceGatewayRequest
 *  com.oracle.bmc.core.requests.CreateSubnetRequest
 *  com.oracle.bmc.core.requests.CreateVcnRequest
 *  com.oracle.bmc.core.requests.DeleteDrgRequest
 *  com.oracle.bmc.core.requests.DeleteInternetGatewayRequest
 *  com.oracle.bmc.core.requests.DeleteLocalPeeringGatewayRequest
 *  com.oracle.bmc.core.requests.DeleteNatGatewayRequest
 *  com.oracle.bmc.core.requests.DeleteRouteTableRequest
 *  com.oracle.bmc.core.requests.DeleteSecurityListRequest
 *  com.oracle.bmc.core.requests.DeleteServiceGatewayRequest
 *  com.oracle.bmc.core.requests.DeleteSubnetRequest
 *  com.oracle.bmc.core.requests.DeleteVcnRequest
 *  com.oracle.bmc.core.requests.GetRouteTableRequest
 *  com.oracle.bmc.core.requests.GetSecurityListRequest
 *  com.oracle.bmc.core.requests.GetVcnRequest
 *  com.oracle.bmc.core.requests.ListDrgsRequest
 *  com.oracle.bmc.core.requests.ListInternetGatewaysRequest
 *  com.oracle.bmc.core.requests.ListLocalPeeringGatewaysRequest
 *  com.oracle.bmc.core.requests.ListNatGatewaysRequest
 *  com.oracle.bmc.core.requests.ListRouteTablesRequest
 *  com.oracle.bmc.core.requests.ListSecurityListsRequest
 *  com.oracle.bmc.core.requests.ListServiceGatewaysRequest
 *  com.oracle.bmc.core.requests.ListServicesRequest
 *  com.oracle.bmc.core.requests.ListSubnetsRequest
 *  com.oracle.bmc.core.requests.ListVcnsRequest
 *  com.oracle.bmc.core.requests.UpdateInternetGatewayRequest
 *  com.oracle.bmc.core.requests.UpdateLocalPeeringGatewayRequest
 *  com.oracle.bmc.core.requests.UpdateNatGatewayRequest
 *  com.oracle.bmc.core.requests.UpdateRouteTableRequest
 *  com.oracle.bmc.core.requests.UpdateSecurityListRequest
 *  com.oracle.bmc.core.requests.UpdateServiceGatewayRequest
 *  com.oracle.bmc.core.requests.UpdateSubnetRequest
 *  com.oracle.bmc.core.requests.UpdateVcnRequest
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.model.BmcException
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
import com.ocxworker.service.VcnService;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Exception performing whole class analysis ignored.
 */
@org.springframework.stereotype.Service
public class VcnService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(VcnService.class);
    @Resource
    private OciUserMapper userMapper;

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region == null || region.isBlank() ? null : region.trim();
        return new OciClientService(this.buildBasicDTO(ociUser), r);
    }

    public List<Map<String, Object>> listVcns(String userId, String region) {
        ArrayList arrayList;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            String r = region == null || region.isBlank() ? null : region.trim();
            OciClientService client = this.oci(ociUser, region);
            try {
                List compartments = client.listAllCompartments();
                ArrayList result = new ArrayList();
                for (Compartment c : compartments) {
                    try {
                        List vcns = client.getVirtualNetworkClient().listVcns(ListVcnsRequest.builder().compartmentId(c.getId()).build()).getItems();
                        for (Vcn v : vcns) {
                            if (v.getLifecycleState() == Vcn.LifecycleState.Terminated) continue;
                            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
                    catch (Exception e) {
                        log.debug("listVcns in {} failed: {}", (Object)c.getId(), (Object)e.getMessage());
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
                    throw new OciException("\u67e5\u8be2 VCN \u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public Map<String, Object> createVcn(String userId, String compartmentId, String displayName, String cidrBlock, String dnsLabel, boolean createIgw, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        block13: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                String cid = compartmentId == null || compartmentId.isBlank() ? client.getProvider().getTenantId() : compartmentId;
                Vcn vcn = client.getVirtualNetworkClient().createVcn(CreateVcnRequest.builder().createVcnDetails(CreateVcnDetails.builder().compartmentId(cid).displayName(displayName).cidrBlock(cidrBlock).dnsLabel(dnsLabel).build()).build()).getVcn();
                String igwId = null;
                if (createIgw) {
                    try {
                        igwId = client.getVirtualNetworkClient().createInternetGateway(CreateInternetGatewayRequest.builder().createInternetGatewayDetails(CreateInternetGatewayDetails.builder().compartmentId(cid).vcnId(vcn.getId()).isEnabled(Boolean.valueOf(true)).displayName("default-igw").build()).build()).getInternetGateway().getId();
                    }
                    catch (Exception e) {
                        log.warn("createInternetGateway failed: {}", (Object)e.getMessage());
                    }
                }
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", vcn.getId());
                map.put("internetGatewayId", igwId);
                linkedHashMap = map;
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
                catch (Exception e) {
                    throw new OciException("\u521b\u5efa VCN \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public Map<String, Object> previewVcnDelete(String userId, String vcnId, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        block10: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                String cid = vcn.getCompartmentId();
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("subnets", this.listMapped(client.getVirtualNetworkClient().listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), Subnet::getId, Subnet::getDisplayName));
                map.put("internetGateways", this.listMapped(client.getVirtualNetworkClient().listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), InternetGateway::getId, InternetGateway::getDisplayName));
                map.put("natGateways", this.listMapped(client.getVirtualNetworkClient().listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), NatGateway::getId, NatGateway::getDisplayName));
                map.put("serviceGateways", this.listMapped(client.getVirtualNetworkClient().listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), ServiceGateway::getId, ServiceGateway::getDisplayName));
                map.put("localPeeringGateways", this.listMapped(client.getVirtualNetworkClient().listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), LocalPeeringGateway::getId, LocalPeeringGateway::getDisplayName));
                map.put("routeTables", this.listMapped(client.getVirtualNetworkClient().listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), RouteTable::getId, RouteTable::getDisplayName));
                map.put("securityLists", this.listMapped(client.getVirtualNetworkClient().listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems(), SecurityList::getId, SecurityList::getDisplayName));
                linkedHashMap = map;
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
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new OciException("\u67e5\u8be2 VCN \u5b50\u8d44\u6e90\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public void deleteVcn(String userId, String vcnId, boolean cascade, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            if (cascade) {
                this.cascadeDeleteVcnChildren(client, vcn);
            }
            client.getVirtualNetworkClient().deleteVcn(DeleteVcnRequest.builder().vcnId(vcnId).build());
            log.info("VCN deleted: {}", (Object)vcnId);
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            if (e.getStatusCode() == 409) {
                throw new OciException("\u5220\u9664 VCN \u5931\u8d25\uff1a\u4ecd\u6709\u5b50\u8d44\u6e90\u672a\u6e05\u7406\u3002" + this.summarizeRemainingVcnChildren(userId, vcnId, region));
            }
            throw new OciException("\u5220\u9664 VCN \u5931\u8d25: " + VcnService.briefBmcMessage((BmcException)e));
        }
        catch (Exception e) {
            throw new OciException("\u5220\u9664 VCN \u5931\u8d25: " + e.getMessage());
        }
    }

    private void cascadeDeleteVcnChildren(OciClientService client, Vcn vcn) {
        String cid = vcn.getCompartmentId();
        String vcnId = vcn.getId();
        VirtualNetworkClient net = client.getVirtualNetworkClient();
        Set gatewayIds = VcnService.collectVcnGatewayIds((VirtualNetworkClient)net, (String)cid, (String)vcnId);
        for (Subnet s : net.listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (s.getLifecycleState() == Subnet.LifecycleState.Terminated) continue;
            try {
                net.deleteSubnet(DeleteSubnetRequest.builder().subnetId(s.getId()).build());
                log.info("cascade delete subnet: {}", (Object)s.getDisplayName());
            }
            catch (Exception e) {
                throw new OciException("\u7ea7\u8054\u5220\u9664\u5b50\u7f51\u5931\u8d25\uff08" + s.getDisplayName() + "\uff09: " + e.getMessage() + "\u3002\u8bf7\u5148\u7ec8\u6b62\u4f7f\u7528\u8be5\u5b50\u7f51\u7684\u5b9e\u4f8b\u6216\u8d1f\u8f7d\u5747\u8861\u3002");
            }
        }
        VcnService.clearRouteRulesReferencingGateways((VirtualNetworkClient)net, (String)cid, (String)vcnId, (Set)gatewayIds);
        for (InternetGateway ig : net.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (ig.getLifecycleState() == InternetGateway.LifecycleState.Terminated) continue;
            VcnService.deleteOrThrow(() -> net.deleteInternetGateway(DeleteInternetGatewayRequest.builder().igId(ig.getId()).build()), (String)"Internet Gateway", (String)ig.getDisplayName());
        }
        for (NatGateway ng : net.listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (ng.getLifecycleState() == NatGateway.LifecycleState.Terminated) continue;
            VcnService.deleteOrThrow(() -> net.deleteNatGateway(DeleteNatGatewayRequest.builder().natGatewayId(ng.getId()).build()), (String)"NAT Gateway", (String)ng.getDisplayName());
        }
        for (ServiceGateway sg : net.listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (sg.getLifecycleState() == ServiceGateway.LifecycleState.Terminated) continue;
            VcnService.deleteOrThrow(() -> net.deleteServiceGateway(DeleteServiceGatewayRequest.builder().serviceGatewayId(sg.getId()).build()), (String)"Service Gateway", (String)sg.getDisplayName());
        }
        for (Object lpg : net.listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (lpg.getLifecycleState() == LocalPeeringGateway.LifecycleState.Terminated) continue;
            VcnService.deleteOrThrow(() -> VcnService.lambda$cascadeDeleteVcnChildren$3(net, (LocalPeeringGateway)lpg), (String)"Local Peering Gateway", (String)lpg.getDisplayName());
        }
        String defaultRtId = vcn.getDefaultRouteTableId();
        for (RouteTable rt : net.listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (rt.getId().equals(defaultRtId) || rt.getLifecycleState() == RouteTable.LifecycleState.Terminated) continue;
            try {
                net.deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rt.getId()).build());
            }
            catch (Exception e) {
                log.debug("deleteRouteTable {} skipped: {}", (Object)rt.getDisplayName(), (Object)e.getMessage());
            }
        }
        String defaultSlId = vcn.getDefaultSecurityListId();
        for (SecurityList sl : net.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
            if (sl.getId().equals(defaultSlId) || sl.getLifecycleState() == SecurityList.LifecycleState.Terminated) continue;
            try {
                net.deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(sl.getId()).build());
            }
            catch (Exception e) {
                log.debug("deleteSecurityList {} skipped: {}", (Object)sl.getDisplayName(), (Object)e.getMessage());
            }
        }
    }

    private static Set<String> collectVcnGatewayIds(VirtualNetworkClient net, String compartmentId, String vcnId) {
        HashSet<String> ids = new HashSet<String>();
        try {
            for (InternetGateway ig : net.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(ig.getId());
            }
            for (NatGateway ng : net.listNatGateways(ListNatGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(ng.getId());
            }
            for (ServiceGateway sg : net.listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(sg.getId());
            }
            for (LocalPeeringGateway lpg : net.listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                ids.add(lpg.getId());
            }
        }
        catch (Exception e) {
            log.warn("collectVcnGatewayIds failed: {}", (Object)e.getMessage());
        }
        return ids;
    }

    private static void clearRouteRulesReferencingGateways(VirtualNetworkClient net, String compartmentId, String vcnId, Set<String> gatewayIds) {
        if (gatewayIds.isEmpty()) {
            return;
        }
        try {
            for (RouteTable rt : net.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()) {
                List rules = rt.getRouteRules();
                if (rules == null || rules.isEmpty()) continue;
                ArrayList<RouteRule> kept = new ArrayList<RouteRule>();
                boolean removed = false;
                for (RouteRule r : rules) {
                    String target = r.getNetworkEntityId();
                    if (target != null && gatewayIds.contains(target)) {
                        removed = true;
                        continue;
                    }
                    kept.add(r);
                }
                if (!removed) continue;
                net.updateRouteTable(UpdateRouteTableRequest.builder().rtId(rt.getId()).updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(kept).build()).build());
                log.info("cleared gateway routes on route table: {}", (Object)rt.getDisplayName());
            }
        }
        catch (Exception e) {
            throw new OciException("\u7ea7\u8054\u5220\u9664\uff1a\u6e05\u7406\u8def\u7531\u8868\u89c4\u5219\u5931\u8d25: " + e.getMessage());
        }
    }

    private static void deleteOrThrow(Runnable delete, String resourceType, String displayName) {
        try {
            delete.run();
            log.info("cascade deleted {}: {}", (Object)resourceType, (Object)displayName);
        }
        catch (Exception e) {
            throw new OciException("\u7ea7\u8054\u5220\u9664 " + resourceType + "\uff08" + displayName + "\uff09\u5931\u8d25: " + e.getMessage());
        }
    }

    private String summarizeRemainingVcnChildren(String userId, String vcnId, String region) {
        try {
            Map left = this.previewVcnDelete(userId, vcnId, region);
            ArrayList parts = new ArrayList();
            VcnService.appendRemaining(parts, (String)"\u5b50\u7f51", left.get("subnets"));
            VcnService.appendRemaining(parts, (String)"Internet Gateway", left.get("internetGateways"));
            VcnService.appendRemaining(parts, (String)"NAT Gateway", left.get("natGateways"));
            VcnService.appendRemaining(parts, (String)"Service Gateway", left.get("serviceGateways"));
            VcnService.appendRemaining(parts, (String)"Local Peering Gateway", left.get("localPeeringGateways"));
            VcnService.appendRemaining(parts, (String)"\u8def\u7531\u8868", left.get("routeTables"));
            VcnService.appendRemaining(parts, (String)"\u5b89\u5168\u5217\u8868", left.get("securityLists"));
            if (parts.isEmpty()) {
                return "";
            }
            return " \u5269\u4f59: " + String.join((CharSequence)"\uff1b", parts);
        }
        catch (Exception e) {
            return "";
        }
    }

    private static void appendRemaining(List<String> parts, String label, Object raw) {
        List list;
        if (!(raw instanceof List) || (list = (List)raw).isEmpty()) {
            return;
        }
        ArrayList<String> names = new ArrayList<String>();
        for (Object o : list) {
            Map m;
            Object n;
            if (!(o instanceof Map) || (n = (m = (Map)o).get("displayName")) == null || String.valueOf(n).isBlank()) continue;
            names.add(String.valueOf(n));
        }
        if (!names.isEmpty()) {
            parts.add(label + "(" + String.join((CharSequence)", ", names) + ")");
        }
    }

    private static String briefBmcMessage(BmcException e) {
        String em = e.getMessage();
        if (em == null) {
            return "HTTP " + e.getStatusCode();
        }
        int nl = em.indexOf(10);
        return nl > 0 ? em.substring(0, nl) : (em.length() > 240 ? em.substring(0, 240) + "\u2026" : em);
    }

    public List<Map<String, Object>> listSubnets(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (Subnet s : client.getVirtualNetworkClient().listSubnets(ListSubnetsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (s.getLifecycleState() == Subnet.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
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
            return list;
        });
    }

    public void createSubnet(String userId, String vcnId, String displayName, String cidrBlock, String availabilityDomain, String routeTableId, Boolean prohibitPublicIp, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            CreateSubnetDetails.Builder b = CreateSubnetDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).cidrBlock(cidrBlock);
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
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa\u5b50\u7f51\u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteSubnet(String userId, String subnetId, String region) {
        this.deleteResource(userId, region, () -> "deleteSubnet", client -> client.getVirtualNetworkClient().deleteSubnet(DeleteSubnetRequest.builder().subnetId(subnetId).build()));
    }

    public void updateSubnet(String userId, String subnetId, String displayName, String routeTableId, List<String> securityListIds, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateSubnetDetails.Builder b = UpdateSubnetDetails.builder();
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
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0\u5b50\u7f51\u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listInternetGateways(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (InternetGateway ig : client.getVirtualNetworkClient().listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (ig.getLifecycleState() == InternetGateway.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", ig.getId());
                m.put("displayName", ig.getDisplayName());
                m.put("isEnabled", ig.getIsEnabled());
                m.put("lifecycleState", ig.getLifecycleState() != null ? ig.getLifecycleState().getValue() : null);
                m.put("timeCreated", ig.getTimeCreated() != null ? ig.getTimeCreated().toString() : null);
                list.add(m);
            }
            return list;
        });
    }

    public void createInternetGateway(String userId, String vcnId, String displayName, boolean enabled, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            client.getVirtualNetworkClient().createInternetGateway(CreateInternetGatewayRequest.builder().createInternetGatewayDetails(CreateInternetGatewayDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).isEnabled(Boolean.valueOf(enabled)).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa Internet Gateway \u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteInternetGateway(String userId, String igId, String region) {
        this.deleteResource(userId, region, () -> "deleteInternetGateway", client -> client.getVirtualNetworkClient().deleteInternetGateway(DeleteInternetGatewayRequest.builder().igId(igId).build()));
    }

    public void updateInternetGateway(String userId, String igId, String displayName, Boolean isEnabled, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateInternetGatewayDetails.Builder b = UpdateInternetGatewayDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            if (isEnabled != null) {
                b.isEnabled(isEnabled);
            }
            client.getVirtualNetworkClient().updateInternetGateway(UpdateInternetGatewayRequest.builder().igId(igId).updateInternetGatewayDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 IGW \u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listNatGateways(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (NatGateway ng : client.getVirtualNetworkClient().listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (ng.getLifecycleState() == NatGateway.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", ng.getId());
                m.put("displayName", ng.getDisplayName());
                m.put("natIp", ng.getNatIp());
                m.put("blockTraffic", ng.getBlockTraffic());
                m.put("lifecycleState", ng.getLifecycleState() != null ? ng.getLifecycleState().getValue() : null);
                m.put("timeCreated", ng.getTimeCreated() != null ? ng.getTimeCreated().toString() : null);
                list.add(m);
            }
            return list;
        });
    }

    public void createNatGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            client.getVirtualNetworkClient().createNatGateway(CreateNatGatewayRequest.builder().createNatGatewayDetails(CreateNatGatewayDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa NAT Gateway \u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteNatGateway(String userId, String natId, String region) {
        this.deleteResource(userId, region, () -> "deleteNatGateway", client -> client.getVirtualNetworkClient().deleteNatGateway(DeleteNatGatewayRequest.builder().natGatewayId(natId).build()));
    }

    public void updateNatGateway(String userId, String natId, String displayName, Boolean blockTraffic, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateNatGatewayDetails.Builder b = UpdateNatGatewayDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            if (blockTraffic != null) {
                b.blockTraffic(blockTraffic);
            }
            client.getVirtualNetworkClient().updateNatGateway(UpdateNatGatewayRequest.builder().natGatewayId(natId).updateNatGatewayDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 NAT \u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listServiceGateways(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (ServiceGateway sg : client.getVirtualNetworkClient().listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (sg.getLifecycleState() == ServiceGateway.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", sg.getId());
                m.put("displayName", sg.getDisplayName());
                m.put("blockTraffic", sg.getBlockTraffic());
                m.put("services", sg.getServices());
                m.put("lifecycleState", sg.getLifecycleState() != null ? sg.getLifecycleState().getValue() : null);
                m.put("timeCreated", sg.getTimeCreated() != null ? sg.getTimeCreated().toString() : null);
                list.add(m);
            }
            return list;
        });
    }

    public void createServiceGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            List services = client.getVirtualNetworkClient().listServices(ListServicesRequest.builder().build()).getItems();
            ArrayList<ServiceIdRequestDetails> serviceIds = new ArrayList<ServiceIdRequestDetails>();
            for (Service s : services) {
                if (s.getName() == null || !s.getName().toLowerCase().contains("all") || !s.getName().toLowerCase().contains("services")) continue;
                serviceIds.add(ServiceIdRequestDetails.builder().serviceId(s.getId()).build());
                break;
            }
            client.getVirtualNetworkClient().createServiceGateway(CreateServiceGatewayRequest.builder().createServiceGatewayDetails(CreateServiceGatewayDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).services(serviceIds).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa Service Gateway \u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteServiceGateway(String userId, String sgId, String region) {
        this.deleteResource(userId, region, () -> "deleteServiceGateway", client -> client.getVirtualNetworkClient().deleteServiceGateway(DeleteServiceGatewayRequest.builder().serviceGatewayId(sgId).build()));
    }

    public void updateServiceGateway(String userId, String sgId, String displayName, Boolean blockTraffic, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateServiceGatewayDetails.Builder b = UpdateServiceGatewayDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            if (blockTraffic != null) {
                b.blockTraffic(blockTraffic);
            }
            client.getVirtualNetworkClient().updateServiceGateway(UpdateServiceGatewayRequest.builder().serviceGatewayId(sgId).updateServiceGatewayDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 SG \u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listRouteTables(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (RouteTable rt : client.getVirtualNetworkClient().listRouteTables(ListRouteTablesRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (rt.getLifecycleState() == RouteTable.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", rt.getId());
                m.put("displayName", rt.getDisplayName());
                ArrayList rules = new ArrayList();
                if (rt.getRouteRules() != null) {
                    for (RouteRule r : rt.getRouteRules()) {
                        LinkedHashMap<String, String> rr = new LinkedHashMap<String, String>();
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
            return list;
        });
    }

    public void deleteRouteTable(String userId, String rtId, String region) {
        this.deleteResource(userId, region, () -> "deleteRouteTable", client -> client.getVirtualNetworkClient().deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rtId).build()));
    }

    public void updateRouteTable(String userId, String rtId, String displayName, List<Map<String, Object>> routeRules, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateRouteTableDetails.Builder b = UpdateRouteTableDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            if (routeRules != null) {
                ArrayList<RouteRule> rules = new ArrayList<RouteRule>();
                for (Map<String, Object> r : routeRules) {
                    RouteRule.Builder rb = RouteRule.builder().destination(this.asStr(r.get("destination"))).networkEntityId(this.asStr(r.get("networkEntityId"))).description(this.asStr(r.get("description")));
                    String dstType = this.asStr(r.get("destinationType"));
                    if (dstType != null && !dstType.isBlank()) {
                        try {
                            rb.destinationType(RouteRule.DestinationType.create((String)dstType));
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    rules.add(rb.build());
                }
                b.routeRules(rules);
            }
            client.getVirtualNetworkClient().updateRouteTable(UpdateRouteTableRequest.builder().rtId(rtId).updateRouteTableDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0\u8def\u7531\u8868\u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, Object> getRouteTable(String userId, String rtId, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        block12: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                RouteTable rt = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(rtId).build()).getRouteTable();
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", rt.getId());
                m.put("displayName", rt.getDisplayName());
                m.put("lifecycleState", rt.getLifecycleState() != null ? rt.getLifecycleState().getValue() : null);
                ArrayList rules = new ArrayList();
                if (rt.getRouteRules() != null) {
                    for (RouteRule r : rt.getRouteRules()) {
                        LinkedHashMap<String, String> rr = new LinkedHashMap<String, String>();
                        rr.put("destination", r.getDestination());
                        rr.put("destinationType", r.getDestinationType() != null ? r.getDestinationType().getValue() : null);
                        rr.put("networkEntityId", r.getNetworkEntityId());
                        rr.put("description", r.getDescription());
                        rules.add(rr);
                    }
                }
                m.put("routeRules", rules);
                linkedHashMap = m;
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
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new OciException("\u67e5\u8be2\u8def\u7531\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    public List<Map<String, Object>> listVcnGateways(String userId, String vcnId, String region) {
        Iterator iterator;
        block22: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                String cid = vcn.getCompartmentId();
                ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
                try {
                    for (InternetGateway ig : client.getVirtualNetworkClient().listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                        if (ig.getLifecycleState() == InternetGateway.LifecycleState.Terminated) continue;
                        result.add(Map.of("id", ig.getId(), "displayName", ig.getDisplayName(), "type", "internetGateway"));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    for (NatGateway ng : client.getVirtualNetworkClient().listNatGateways(ListNatGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                        if (ng.getLifecycleState() == NatGateway.LifecycleState.Terminated) continue;
                        result.add(Map.of("id", ng.getId(), "displayName", ng.getDisplayName(), "type", "natGateway"));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    for (ServiceGateway sg : client.getVirtualNetworkClient().listServiceGateways(ListServiceGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                        if (sg.getLifecycleState() == ServiceGateway.LifecycleState.Terminated) continue;
                        result.add(Map.of("id", sg.getId(), "displayName", sg.getDisplayName(), "type", "serviceGateway"));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    for (LocalPeeringGateway lpg : client.getVirtualNetworkClient().listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                        if (lpg.getLifecycleState() == LocalPeeringGateway.LifecycleState.Terminated) continue;
                        result.add(Map.of("id", lpg.getId(), "displayName", lpg.getDisplayName(), "type", "localPeeringGateway"));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                iterator = result;
                if (client == null) break block22;
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
                    throw new OciException("\u67e5\u8be2 VCN \u7f51\u5173\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return iterator;
    }

    public Map<String, Object> getSecurityList(String userId, String slId, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                SecurityList sl = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build()).getSecurityList();
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", sl.getId());
                m.put("displayName", sl.getDisplayName());
                m.put("lifecycleState", sl.getLifecycleState() != null ? sl.getLifecycleState().getValue() : null);
                ArrayList ingress = new ArrayList();
                int idx = 0;
                if (sl.getIngressSecurityRules() != null) {
                    for (IngressSecurityRule r : sl.getIngressSecurityRules()) {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
                ArrayList egress = new ArrayList();
                idx = 0;
                if (sl.getEgressSecurityRules() != null) {
                    for (EgressSecurityRule r : sl.getEgressSecurityRules()) {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
                linkedHashMap = m;
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
                    throw new OciException("\u67e5\u8be2\u5b89\u5168\u5217\u8868\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return linkedHashMap;
    }

    private String portRangeLabel(TcpOptions tcp, UdpOptions udp) {
        if (tcp != null && tcp.getDestinationPortRange() != null) {
            return tcp.getDestinationPortRange().getMin() + "-" + tcp.getDestinationPortRange().getMax();
        }
        if (udp != null && udp.getDestinationPortRange() != null) {
            return udp.getDestinationPortRange().getMin() + "-" + udp.getDestinationPortRange().getMax();
        }
        return "all";
    }

    public void addSecurityListRule(String userId, String slId, String direction, String protocol, String source, String portMin, String portMax, String description, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        if (description != null && description.isBlank()) {
            description = null;
        }
        boolean ingress = !"egress".equalsIgnoreCase(direction);
        try (OciClientService client = this.oci(ociUser, region);){
            SecurityList sl = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build()).getSecurityList();
            ArrayList<IngressSecurityRule> ingressRules = new ArrayList<IngressSecurityRule>(sl.getIngressSecurityRules());
            ArrayList<EgressSecurityRule> egressRules = new ArrayList<EgressSecurityRule>(sl.getEgressSecurityRules());
            TcpOptions tcpOpt = null;
            UdpOptions udpOpt = null;
            if (("6".equals(protocol) || "17".equals(protocol)) && portMin != null && !portMin.isBlank()) {
                int min = Integer.parseInt(portMin);
                int max = portMax == null || portMax.isBlank() ? min : Integer.parseInt(portMax);
                PortRange pr = PortRange.builder().min(Integer.valueOf(min)).max(Integer.valueOf(max)).build();
                if ("6".equals(protocol)) {
                    tcpOpt = TcpOptions.builder().destinationPortRange(pr).build();
                } else {
                    udpOpt = UdpOptions.builder().destinationPortRange(pr).build();
                }
            }
            String src = source == null || source.isBlank() ? "0.0.0.0/0" : source;
            boolean isIpv6 = src.contains(":");
            if (ingress) {
                b = IngressSecurityRule.builder().source(src).protocol(protocol == null || protocol.isBlank() ? "all" : protocol).description(description);
                if (isIpv6) {
                    b.sourceType(IngressSecurityRule.SourceType.CidrBlock);
                }
                if (tcpOpt != null) {
                    b.tcpOptions(tcpOpt);
                }
                if (udpOpt != null) {
                    b.udpOptions(udpOpt);
                }
                ingressRules.add(b.build());
            } else {
                b = EgressSecurityRule.builder().destination(src).protocol(protocol == null || protocol.isBlank() ? "all" : protocol).description(description);
                if (isIpv6) {
                    b.destinationType(EgressSecurityRule.DestinationType.CidrBlock);
                }
                if (tcpOpt != null) {
                    b.tcpOptions(tcpOpt);
                }
                if (udpOpt != null) {
                    b.udpOptions(udpOpt);
                }
                egressRules.add(b.build());
            }
            client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(slId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u6dfb\u52a0\u5b89\u5168\u89c4\u5219\u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteSecurityListRule(String userId, String slId, String direction, int ruleIndex, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        boolean ingress = !"egress".equalsIgnoreCase(direction);
        try (OciClientService client = this.oci(ociUser, region);){
            SecurityList sl = client.getVirtualNetworkClient().getSecurityList(GetSecurityListRequest.builder().securityListId(slId).build()).getSecurityList();
            ArrayList ingressRules = new ArrayList(sl.getIngressSecurityRules());
            ArrayList egressRules = new ArrayList(sl.getEgressSecurityRules());
            if (ingress) {
                if (ruleIndex < 0 || ruleIndex >= ingressRules.size()) {
                    throw new OciException("\u5165\u7ad9\u89c4\u5219\u7d22\u5f15\u8d8a\u754c");
                }
                ingressRules.remove(ruleIndex);
            } else {
                if (ruleIndex < 0 || ruleIndex >= egressRules.size()) {
                    throw new OciException("\u51fa\u7ad9\u89c4\u5219\u7d22\u5f15\u8d8a\u754c");
                }
                egressRules.remove(ruleIndex);
            }
            client.getVirtualNetworkClient().updateSecurityList(UpdateSecurityListRequest.builder().securityListId(slId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u5220\u9664\u5b89\u5168\u89c4\u5219\u5931\u8d25: " + e.getMessage());
        }
    }

    public void setupIgwDefaultRoutes(String userId, String vcnId, String igwId, boolean addIpv6, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            String defaultRtId = vcn.getDefaultRouteTableId();
            if (defaultRtId == null) {
                throw new OciException("\u672a\u627e\u5230 VCN \u7684\u9ed8\u8ba4\u8def\u7531\u8868");
            }
            RouteTable rt = client.getVirtualNetworkClient().getRouteTable(GetRouteTableRequest.builder().rtId(defaultRtId).build()).getRouteTable();
            ArrayList<RouteRule> rules = rt.getRouteRules() == null ? new ArrayList<RouteRule>() : new ArrayList(rt.getRouteRules());
            boolean hasIpv4 = rules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getDestination()) && igwId.equals(r.getNetworkEntityId()));
            boolean hasIpv6 = rules.stream().anyMatch(r -> "::/0".equals(r.getDestination()) && igwId.equals(r.getNetworkEntityId()));
            if (!hasIpv4) {
                rules.add(RouteRule.builder().destination("0.0.0.0/0").destinationType(RouteRule.DestinationType.CidrBlock).networkEntityId(igwId).description("Default IPv4 route via IGW").build());
            }
            if (addIpv6 && !hasIpv6) {
                rules.add(RouteRule.builder().destination("::/0").destinationType(RouteRule.DestinationType.CidrBlock).networkEntityId(igwId).description("Default IPv6 route via IGW").build());
            }
            client.getVirtualNetworkClient().updateRouteTable(UpdateRouteTableRequest.builder().rtId(defaultRtId).updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u914d\u7f6e IGW \u9ed8\u8ba4\u8def\u7531\u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listSecurityLists(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (SecurityList sl : client.getVirtualNetworkClient().listSecurityLists(ListSecurityListsRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (sl.getLifecycleState() == SecurityList.LifecycleState.Terminated) continue;
                LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
                m.put("id", sl.getId());
                m.put("displayName", sl.getDisplayName());
                m.put("ingressRulesCount", sl.getIngressSecurityRules() != null ? sl.getIngressSecurityRules().size() : 0);
                m.put("egressRulesCount", sl.getEgressSecurityRules() != null ? sl.getEgressSecurityRules().size() : 0);
                m.put("lifecycleState", sl.getLifecycleState() != null ? sl.getLifecycleState().getValue() : null);
                m.put("timeCreated", sl.getTimeCreated() != null ? sl.getTimeCreated().toString() : null);
                list.add(m);
            }
            return list;
        });
    }

    public void deleteSecurityList(String userId, String slId, String region) {
        this.deleteResource(userId, region, () -> "deleteSecurityList", client -> client.getVirtualNetworkClient().deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(slId).build()));
    }

    public List<Map<String, Object>> listDrgs(String userId, String region) {
        ArrayList arrayList;
        block14: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                ArrayList result = new ArrayList();
                for (Compartment c : client.listAllCompartments()) {
                    try {
                        for (Drg d : client.getVirtualNetworkClient().listDrgs(ListDrgsRequest.builder().compartmentId(c.getId()).build()).getItems()) {
                            if (d.getLifecycleState() == Drg.LifecycleState.Terminated) continue;
                            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
                            m.put("id", d.getId());
                            m.put("displayName", d.getDisplayName());
                            m.put("compartmentId", c.getId());
                            m.put("compartmentName", c.getName());
                            m.put("lifecycleState", d.getLifecycleState() != null ? d.getLifecycleState().getValue() : null);
                            m.put("timeCreated", d.getTimeCreated() != null ? d.getTimeCreated().toString() : null);
                            result.add(m);
                        }
                    }
                    catch (Exception exception) {
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
                    throw new OciException("\u67e5\u8be2 DRG \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return arrayList;
    }

    public void createDrg(String userId, String compartmentId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            String cid = compartmentId == null || compartmentId.isBlank() ? client.getProvider().getTenantId() : compartmentId;
            client.getVirtualNetworkClient().createDrg(CreateDrgRequest.builder().createDrgDetails(CreateDrgDetails.builder().compartmentId(cid).displayName(displayName).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa DRG \u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteDrg(String userId, String drgId, String region) {
        this.deleteResource(userId, region, () -> "deleteDrg", client -> client.getVirtualNetworkClient().deleteDrg(DeleteDrgRequest.builder().drgId(drgId).build()));
    }

    public List<Map<String, Object>> listLocalPeeringGateways(String userId, String vcnId, String region) {
        return this.listChildren(userId, vcnId, region, (client, cid) -> {
            ArrayList list = new ArrayList();
            for (LocalPeeringGateway lpg : client.getVirtualNetworkClient().listLocalPeeringGateways(ListLocalPeeringGatewaysRequest.builder().compartmentId(cid).vcnId(vcnId).build()).getItems()) {
                if (lpg.getLifecycleState() == LocalPeeringGateway.LifecycleState.Terminated) continue;
                LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
                m.put("id", lpg.getId());
                m.put("displayName", lpg.getDisplayName());
                m.put("peeringStatus", lpg.getPeeringStatus() != null ? lpg.getPeeringStatus().getValue() : null);
                m.put("peerAdvertisedCidr", lpg.getPeerAdvertisedCidr());
                m.put("lifecycleState", lpg.getLifecycleState() != null ? lpg.getLifecycleState().getValue() : null);
                m.put("timeCreated", lpg.getTimeCreated() != null ? lpg.getTimeCreated().toString() : null);
                list.add(m);
            }
            return list;
        });
    }

    public void createLocalPeeringGateway(String userId, String vcnId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
            client.getVirtualNetworkClient().createLocalPeeringGateway(CreateLocalPeeringGatewayRequest.builder().createLocalPeeringGatewayDetails(CreateLocalPeeringGatewayDetails.builder().compartmentId(vcn.getCompartmentId()).vcnId(vcnId).displayName(displayName).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u521b\u5efa LPG \u5931\u8d25: " + e.getMessage());
        }
    }

    public void connectLocalPeeringGateway(String userId, String lpgId, String peerId, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().connectLocalPeeringGateways(ConnectLocalPeeringGatewaysRequest.builder().localPeeringGatewayId(lpgId).connectLocalPeeringGatewaysDetails(ConnectLocalPeeringGatewaysDetails.builder().peerId(peerId).build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u8fde\u63a5 LPG \u5931\u8d25: " + e.getMessage());
        }
    }

    public void deleteLocalPeeringGateway(String userId, String lpgId, String region) {
        this.deleteResource(userId, region, () -> "deleteLocalPeeringGateway", client -> client.getVirtualNetworkClient().deleteLocalPeeringGateway(DeleteLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpgId).build()));
    }

    private List<Map<String, Object>> listChildren(String userId, String vcnId, String region, ChildrenFetcher fetcher) {
        List list;
        block10: {
            OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
            if (ociUser == null) {
                throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                Vcn vcn = client.getVirtualNetworkClient().getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
                list = fetcher.fetch(client, vcn.getCompartmentId());
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
                catch (OciException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new OciException("\u67e5\u8be2\u5b50\u8d44\u6e90\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return list;
    }

    private void deleteResource(String userId, String region, OpName op, ClientAction action) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            action.run(client);
            log.info("{} succeeded", (Object)op.get());
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            if (e.getStatusCode() == 409) {
                throw new OciException("\u8d44\u6e90\u4ecd\u88ab\u5f15\u7528\u6216\u6b63\u5728\u4f7f\u7528\uff0c\u65e0\u6cd5\u5220\u9664");
            }
            throw new OciException(op.get() + " \u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
        }
        catch (Exception e) {
            throw new OciException(op.get() + " \u5931\u8d25: " + e.getMessage());
        }
    }

    private <T> List<Map<String, String>> listMapped(List<T> items, Function<T, String> idFn, Function<T, String> nameFn) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (items == null) {
            return list;
        }
        for (T it : items) {
            LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
            m.put("id", idFn.apply(it));
            m.put("displayName", nameFn.apply(it));
            list.add(m);
        }
        return list;
    }

    private String asStr(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    public void updateVcn(String userId, String vcnId, String displayName, String dnsLabel, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateVcnDetails.Builder b = UpdateVcnDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            client.getVirtualNetworkClient().updateVcn(UpdateVcnRequest.builder().vcnId(vcnId).updateVcnDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 VCN \u5931\u8d25: " + e.getMessage());
        }
    }

    public void updateLocalPeeringGateway(String userId, String lpgId, String displayName, String region) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            UpdateLocalPeeringGatewayDetails.Builder b = UpdateLocalPeeringGatewayDetails.builder();
            if (displayName != null && !displayName.isBlank()) {
                b.displayName(displayName);
            }
            client.getVirtualNetworkClient().updateLocalPeeringGateway(UpdateLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpgId).updateLocalPeeringGatewayDetails(b.build()).build());
        }
        catch (OciException e) {
            throw e;
        }
        catch (Exception e) {
            throw new OciException("\u66f4\u65b0 LPG \u5931\u8d25: " + e.getMessage());
        }
    }

    private SysUserDTO buildBasicDTO(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }

    private static /* synthetic */ void lambda$cascadeDeleteVcnChildren$3(VirtualNetworkClient net, LocalPeeringGateway lpg) {
        net.deleteLocalPeeringGateway(DeleteLocalPeeringGatewayRequest.builder().localPeeringGatewayId(lpg.getId()).build());
    }
}

    interface ChildrenFetcher {
        List<Map<String, Object>> fetch(OciClientService client, String compartmentId);
    }

    interface ClientAction {
        void run(OciClientService client);
    }

    interface OpName {
        String get();
    }


