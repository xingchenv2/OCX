/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollectionUtil
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.enums.ArchitectureEnum
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.model.dto.InstanceDetailDTO
 *  com.ocxworker.model.dto.OciProxySnapshot
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.OciProxyConfigService
 *  com.ocxworker.util.BootVolumeVpusUtil
 *  com.ocxworker.util.CommonUtils
 *  com.ocxworker.util.ShapeSeriesUtil
 *  com.ocxworker.util.VcnIpv6Util
 *  com.ocxworker.util.socks.OciSocksApacheConnectionManager
 *  com.oracle.bmc.ClientConfiguration
 *  com.oracle.bmc.Region
 *  com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider
 *  com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
 *  com.oracle.bmc.core.BlockstorageClient
 *  com.oracle.bmc.core.BlockstorageClient$Builder
 *  com.oracle.bmc.core.ComputeClient
 *  com.oracle.bmc.core.ComputeClient$Builder
 *  com.oracle.bmc.core.ComputeWaiters
 *  com.oracle.bmc.core.VirtualNetwork
 *  com.oracle.bmc.core.VirtualNetworkClient
 *  com.oracle.bmc.core.VirtualNetworkClient$Builder
 *  com.oracle.bmc.core.model.AddVcnIpv6CidrDetails
 *  com.oracle.bmc.core.model.CreateInternetGatewayDetails
 *  com.oracle.bmc.core.model.CreateIpv6Details
 *  com.oracle.bmc.core.model.CreateSubnetDetails
 *  com.oracle.bmc.core.model.CreateVcnDetails
 *  com.oracle.bmc.core.model.CreateVnicDetails
 *  com.oracle.bmc.core.model.EgressSecurityRule
 *  com.oracle.bmc.core.model.EgressSecurityRule$DestinationType
 *  com.oracle.bmc.core.model.Image
 *  com.oracle.bmc.core.model.IngressSecurityRule
 *  com.oracle.bmc.core.model.IngressSecurityRule$SourceType
 *  com.oracle.bmc.core.model.Instance
 *  com.oracle.bmc.core.model.Instance$LifecycleState
 *  com.oracle.bmc.core.model.InstanceShapeConfig
 *  com.oracle.bmc.core.model.InstanceSourceDetails
 *  com.oracle.bmc.core.model.InstanceSourceViaImageDetails
 *  com.oracle.bmc.core.model.InstanceSourceViaImageDetails$Builder
 *  com.oracle.bmc.core.model.InternetGateway
 *  com.oracle.bmc.core.model.Ipv6
 *  com.oracle.bmc.core.model.LaunchInstanceDetails
 *  com.oracle.bmc.core.model.LaunchInstanceDetails$Builder
 *  com.oracle.bmc.core.model.LaunchInstanceShapeConfigDetails
 *  com.oracle.bmc.core.model.RouteRule
 *  com.oracle.bmc.core.model.RouteRule$DestinationType
 *  com.oracle.bmc.core.model.RouteTable
 *  com.oracle.bmc.core.model.SecurityList
 *  com.oracle.bmc.core.model.Shape
 *  com.oracle.bmc.core.model.Subnet
 *  com.oracle.bmc.core.model.Subnet$LifecycleState
 *  com.oracle.bmc.core.model.UpdateRouteTableDetails
 *  com.oracle.bmc.core.model.UpdateSecurityListDetails
 *  com.oracle.bmc.core.model.UpdateSubnetDetails
 *  com.oracle.bmc.core.model.Vcn
 *  com.oracle.bmc.core.model.Vcn$LifecycleState
 *  com.oracle.bmc.core.model.Vnic
 *  com.oracle.bmc.core.model.VnicAttachment
 *  com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest
 *  com.oracle.bmc.core.requests.CreateInternetGatewayRequest
 *  com.oracle.bmc.core.requests.CreateIpv6Request
 *  com.oracle.bmc.core.requests.CreateSubnetRequest
 *  com.oracle.bmc.core.requests.CreateVcnRequest
 *  com.oracle.bmc.core.requests.GetInstanceRequest
 *  com.oracle.bmc.core.requests.GetRouteTableRequest
 *  com.oracle.bmc.core.requests.GetSecurityListRequest
 *  com.oracle.bmc.core.requests.GetSubnetRequest
 *  com.oracle.bmc.core.requests.GetVcnRequest
 *  com.oracle.bmc.core.requests.GetVnicRequest
 *  com.oracle.bmc.core.requests.LaunchInstanceRequest
 *  com.oracle.bmc.core.requests.ListImagesRequest
 *  com.oracle.bmc.core.requests.ListImagesRequest$Builder
 *  com.oracle.bmc.core.requests.ListImagesRequest$SortBy
 *  com.oracle.bmc.core.requests.ListImagesRequest$SortOrder
 *  com.oracle.bmc.core.requests.ListInstancesRequest
 *  com.oracle.bmc.core.requests.ListInternetGatewaysRequest
 *  com.oracle.bmc.core.requests.ListShapesRequest
 *  com.oracle.bmc.core.requests.ListShapesRequest$Builder
 *  com.oracle.bmc.core.requests.ListSubnetsRequest
 *  com.oracle.bmc.core.requests.ListVcnsRequest
 *  com.oracle.bmc.core.requests.ListVnicAttachmentsRequest
 *  com.oracle.bmc.core.requests.UpdateRouteTableRequest
 *  com.oracle.bmc.core.requests.UpdateSecurityListRequest
 *  com.oracle.bmc.core.requests.UpdateSubnetRequest
 *  com.oracle.bmc.core.responses.CreateInternetGatewayResponse
 *  com.oracle.bmc.core.responses.CreateSubnetResponse
 *  com.oracle.bmc.core.responses.CreateVcnResponse
 *  com.oracle.bmc.core.responses.GetInstanceResponse
 *  com.oracle.bmc.core.responses.GetRouteTableResponse
 *  com.oracle.bmc.core.responses.LaunchInstanceResponse
 *  com.oracle.bmc.http.ClientConfigurator
 *  com.oracle.bmc.http.client.ClientProperty
 *  com.oracle.bmc.http.client.ProxyConfiguration
 *  com.oracle.bmc.http.client.StandardClientProperties
 *  com.oracle.bmc.http.client.jersey3.ApacheClientProperties
 *  com.oracle.bmc.identity.IdentityClient
 *  com.oracle.bmc.identity.IdentityClient$Builder
 *  com.oracle.bmc.identity.model.AvailabilityDomain
 *  com.oracle.bmc.identity.model.Compartment
 *  com.oracle.bmc.identity.model.Compartment$LifecycleState
 *  com.oracle.bmc.identity.model.Tenancy
 *  com.oracle.bmc.identity.requests.GetTenancyRequest
 *  com.oracle.bmc.identity.requests.ListAvailabilityDomainsRequest
 *  com.oracle.bmc.identity.requests.ListCompartmentsRequest
 *  com.oracle.bmc.identity.requests.ListCompartmentsRequest$AccessLevel
 *  com.oracle.bmc.model.BmcException
 *  com.oracle.bmc.monitoring.MonitoringClient
 *  com.oracle.bmc.monitoring.MonitoringClient$Builder
 *  com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient
 *  com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient$Builder
 *  com.oracle.bmc.objectstorage.ObjectStorageClient
 *  com.oracle.bmc.objectstorage.ObjectStorageClient$Builder
 *  com.oracle.bmc.workrequests.WorkRequest
 *  com.oracle.bmc.workrequests.WorkRequestClient
 *  com.oracle.bmc.workrequests.WorkRequestClient$Builder
 *  lombok.Generated
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ocxworker.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.enums.ArchitectureEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.model.dto.InstanceDetailDTO;
import com.ocxworker.model.dto.OciProxySnapshot;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.service.OciProxyConfigService;
import com.ocxworker.util.BootVolumeVpusUtil;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.ShapeSeriesUtil;
import com.ocxworker.util.VcnIpv6Util;
import com.ocxworker.util.socks.OciSocksApacheConnectionManager;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.core.BlockstorageClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.ComputeWaiters;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.core.model.AddVcnIpv6CidrDetails;
import com.oracle.bmc.core.model.CreateInternetGatewayDetails;
import com.oracle.bmc.core.model.CreateIpv6Details;
import com.oracle.bmc.core.model.CreateSubnetDetails;
import com.oracle.bmc.core.model.CreateVcnDetails;
import com.oracle.bmc.core.model.CreateVnicDetails;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.Image;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InstanceShapeConfig;
import com.oracle.bmc.core.model.InstanceSourceDetails;
import com.oracle.bmc.core.model.InstanceSourceViaImageDetails;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.Ipv6;
import com.oracle.bmc.core.model.LaunchInstanceDetails;
import com.oracle.bmc.core.model.LaunchInstanceShapeConfigDetails;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Shape;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.UpdateRouteTableDetails;
import com.oracle.bmc.core.model.UpdateSecurityListDetails;
import com.oracle.bmc.core.model.UpdateSubnetDetails;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.model.VnicAttachment;
import com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest;
import com.oracle.bmc.core.requests.CreateInternetGatewayRequest;
import com.oracle.bmc.core.requests.CreateIpv6Request;
import com.oracle.bmc.core.requests.CreateSubnetRequest;
import com.oracle.bmc.core.requests.CreateVcnRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.core.requests.GetVnicRequest;
import com.oracle.bmc.core.requests.LaunchInstanceRequest;
import com.oracle.bmc.core.requests.ListImagesRequest;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.requests.ListInternetGatewaysRequest;
import com.oracle.bmc.core.requests.ListShapesRequest;
import com.oracle.bmc.core.requests.ListSubnetsRequest;
import com.oracle.bmc.core.requests.ListVcnsRequest;
import com.oracle.bmc.core.requests.ListVnicAttachmentsRequest;
import com.oracle.bmc.core.requests.UpdateRouteTableRequest;
import com.oracle.bmc.core.requests.UpdateSecurityListRequest;
import com.oracle.bmc.core.requests.UpdateSubnetRequest;
import com.oracle.bmc.core.responses.CreateInternetGatewayResponse;
import com.oracle.bmc.core.responses.CreateSubnetResponse;
import com.oracle.bmc.core.responses.CreateVcnResponse;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.core.responses.GetRouteTableResponse;
import com.oracle.bmc.core.responses.LaunchInstanceResponse;
import com.oracle.bmc.http.ClientConfigurator;
import com.oracle.bmc.http.client.ClientProperty;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.StandardClientProperties;
import com.oracle.bmc.http.client.jersey3.ApacheClientProperties;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListAvailabilityDomainsRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.workrequests.WorkRequest;
import com.oracle.bmc.workrequests.WorkRequestClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Exception performing whole class analysis ignored.
 */
public class OciClientService
implements Closeable {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(OciClientService.class);
    private final ComputeClient computeClient;
    private final IdentityClient identityClient;
    private final WorkRequestClient workRequestClient;
    private final VirtualNetworkClient virtualNetworkClient;
    private final BlockstorageClient blockstorageClient;
    private final ObjectStorageClient objectStorageClient;
    private final MonitoringClient monitoringClient;
    private final NetworkLoadBalancerClient networkLoadBalancerClient;
    private final SimpleAuthenticationDetailsProvider provider;
    private SysUserDTO user;
    private String compartmentId;
    private final HttpClientConnectionManager ociSocksPoolingManager;
    private static final String CIDR_BLOCK = "10.0.0.0/16";
    private static final String SUBNET_CIDR = "10.0.0.0/24";

    @Override
    public void close() {
        this.computeClient.close();
        this.identityClient.close();
        this.workRequestClient.close();
        this.virtualNetworkClient.close();
        this.blockstorageClient.close();
        this.objectStorageClient.close();
        this.monitoringClient.close();
        this.networkLoadBalancerClient.close();
        if (this.ociSocksPoolingManager != null) {
            try {
                this.ociSocksPoolingManager.shutdown();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public OciClientService(SysUserDTO user) {
        this(user, user.getOciCfg() != null ? user.getOciCfg().getRegion() : null);
    }

    public OciClientService(SysUserDTO user, String regionId) {
        ClientConfigurator ociApacheCfg;
        Optional ocx;
        OciProxySnapshot snap;
        this.user = user;
        SysUserDTO.OciCfg ociCfg = user.getOciCfg();
        Region region = OciClientService.resolveRegion((String)(StrUtil.isNotBlank((CharSequence)regionId) ? regionId : ociCfg.getRegion()));
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder().tenantId(ociCfg.getTenantId()).userId(ociCfg.getUserId()).fingerprint(ociCfg.getFingerprint()).privateKeySupplier(() -> {
            try (FileInputStream fis = new FileInputStream(ociCfg.getPrivateKeyPath());){
                ByteArrayInputStream byteArrayInputStream;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                }
                return byteArrayInputStream;
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to read private key");
            }
        }).region(region).build();
        ClientConfiguration clientConfig = ClientConfiguration.builder().connectionTimeoutMillis(Integer.valueOf(10000)).readTimeoutMillis(Integer.valueOf(30000)).build();
        OciProxyConfigService ps = OciProxyConfigService.instance();
        OciProxySnapshot ociProxySnapshot = snap = ps == null ? null : ps.snapshot();
        if (ps == null || !ps.ociUsesExplicitClientProxy()) {
            OciProxyConfigService.clearInProcessHttpSocksProxySystemProperties();
        }
        PoolingHttpClientConnectionManager socksPool = snap != null && snap.usesSocksForOci() ? OciSocksApacheConnectionManager.create((OciProxySnapshot)snap) : null;
        this.ociSocksPoolingManager = socksPool;
        Optional optional = ocx = ps == null ? Optional.empty() : ps.getOciProxyConfiguration();
        if (socksPool != null) {
            ociApacheCfg = b -> {
                b.property((ClientProperty)ApacheClientProperties.CONNECTION_MANAGER, (Object)socksPool);
                b.property((ClientProperty)ApacheClientProperties.CONNECTION_MANAGER_SHARED, (Object)Boolean.TRUE);
            };
        } else if (ocx.isPresent()) {
            ProxyConfiguration pc = (ProxyConfiguration)ocx.get();
            ociApacheCfg = c -> c.property(StandardClientProperties.PROXY, (Object)pc);
        } else {
            ociApacheCfg = OciProxyConfigService.ociSdkJerseyDirectConfigurator();
        }
        if (snap != null && snap.usesSocksForOci()) {
            log.debug("OCI \u5ba2\u6237\u7aef\u7ecf\u5e94\u7528\u5185 SOCKS5 \u4ee3\u7406\u51fa\u7ad9: {}:{}", (Object)snap.host(), (Object)snap.port());
        }
        IdentityClient.Builder idb = (IdentityClient.Builder)IdentityClient.builder().configuration(clientConfig);
        idb.additionalClientConfigurator(ociApacheCfg);
        this.identityClient = idb.build((AbstractAuthenticationDetailsProvider)provider);
        ComputeClient.Builder c1 = (ComputeClient.Builder)ComputeClient.builder().configuration(clientConfig);
        c1.additionalClientConfigurator(ociApacheCfg);
        this.computeClient = c1.build((AbstractAuthenticationDetailsProvider)provider);
        BlockstorageClient.Builder c2 = (BlockstorageClient.Builder)BlockstorageClient.builder().configuration(clientConfig);
        c2.additionalClientConfigurator(ociApacheCfg);
        this.blockstorageClient = c2.build((AbstractAuthenticationDetailsProvider)provider);
        ObjectStorageClient.Builder c3 = (ObjectStorageClient.Builder)ObjectStorageClient.builder().configuration(clientConfig);
        c3.additionalClientConfigurator(ociApacheCfg);
        this.objectStorageClient = c3.build((AbstractAuthenticationDetailsProvider)provider);
        WorkRequestClient.Builder c4 = (WorkRequestClient.Builder)WorkRequestClient.builder().configuration(clientConfig);
        c4.additionalClientConfigurator(ociApacheCfg);
        this.workRequestClient = c4.build((AbstractAuthenticationDetailsProvider)provider);
        VirtualNetworkClient.Builder c5 = (VirtualNetworkClient.Builder)VirtualNetworkClient.builder().configuration(clientConfig);
        c5.additionalClientConfigurator(ociApacheCfg);
        this.virtualNetworkClient = c5.build((AbstractAuthenticationDetailsProvider)provider);
        MonitoringClient.Builder c6 = (MonitoringClient.Builder)MonitoringClient.builder().configuration(clientConfig);
        c6.additionalClientConfigurator(ociApacheCfg);
        this.monitoringClient = c6.build((AbstractAuthenticationDetailsProvider)provider);
        NetworkLoadBalancerClient.Builder c7 = (NetworkLoadBalancerClient.Builder)NetworkLoadBalancerClient.builder().configuration(clientConfig);
        c7.additionalClientConfigurator(ociApacheCfg);
        this.networkLoadBalancerClient = c7.build((AbstractAuthenticationDetailsProvider)provider);
        this.provider = provider;
        this.compartmentId = StrUtil.isBlank((CharSequence)ociCfg.getCompartmentId()) ? this.findRootCompartment(this.identityClient, provider.getTenantId()) : ociCfg.getCompartmentId();
    }

    private static Region resolveRegion(String regionId) {
        if (StrUtil.isBlank((CharSequence)regionId)) {
            throw new OciException("Region \u4e0d\u80fd\u4e3a\u7a7a");
        }
        String trimmed = regionId.trim();
        try {
            return Region.fromRegionCodeOrId((String)trimmed);
        }
        catch (IllegalArgumentException ignored) {
            for (Region r : Region.values()) {
                if (!trimmed.equalsIgnoreCase(r.getRegionId())) continue;
                return r;
            }
            throw new OciException("\u672a\u77e5 Region: " + regionId + "\uff08\u8bf7\u68c0\u67e5\u62fc\u5199\u6216\u5347\u7ea7 OCI SDK\uff09");
        }
    }

    private String findRootCompartment(IdentityClient identityClient, String tenantId) {
        try {
            List compartments = identityClient.listCompartments(ListCompartmentsRequest.builder().compartmentId(tenantId).accessLevel(ListCompartmentsRequest.AccessLevel.Accessible).build()).getItems();
            if (CollectionUtil.isNotEmpty((Collection)compartments)) {
                return ((Compartment)compartments.get(0)).getCompartmentId() != null ? ((Compartment)compartments.get(0)).getCompartmentId() : tenantId;
            }
        }
        catch (Exception e) {
            log.warn("Failed to find root compartment, using tenantId: {}", (Object)e.getMessage());
        }
        return tenantId;
    }

    public List<Compartment> listAllCompartments() {
        String tenantId = this.provider.getTenantId();
        ArrayList<Compartment> all = new ArrayList<Compartment>();
        try {
            Tenancy tenancy = this.identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenantId).build()).getTenancy();
            Compartment root = Compartment.builder().id(tenantId).name("root").compartmentId(tenantId).lifecycleState(Compartment.LifecycleState.Active).build();
            all.add(root);
        }
        catch (Exception e) {
            Compartment root = Compartment.builder().id(tenantId).name("root").compartmentId(tenantId).lifecycleState(Compartment.LifecycleState.Active).build();
            all.add(root);
        }
        try {
            all.addAll(this.identityClient.listCompartments(ListCompartmentsRequest.builder().compartmentId(tenantId).accessLevel(ListCompartmentsRequest.AccessLevel.Accessible).compartmentIdInSubtree(Boolean.valueOf(true)).lifecycleState(Compartment.LifecycleState.Active).build()).getItems());
        }
        catch (Exception e) {
            log.warn("Failed to list compartments: {}", (Object)e.getMessage());
        }
        return all;
    }

    public List<Instance> listAllInstancesInCompartment(String cid) {
        ArrayList<Instance> all = new ArrayList<Instance>();
        for (Instance.LifecycleState state : List.of(Instance.LifecycleState.Running, Instance.LifecycleState.Stopped, Instance.LifecycleState.Starting, Instance.LifecycleState.Stopping)) {
            try {
                all.addAll(this.computeClient.listInstances(ListInstancesRequest.builder().compartmentId(cid).lifecycleState(state).build()).getItems());
            }
            catch (Exception exception) {}
        }
        return all;
    }

    public List<Vcn> listVcnInCompartment(String cid) {
        try {
            return this.virtualNetworkClient.listVcns(ListVcnsRequest.builder().compartmentId(cid).lifecycleState(Vcn.LifecycleState.Available).build()).getItems();
        }
        catch (Exception e) {
            return List.of();
        }
    }

    public List<AvailabilityDomain> getAvailabilityDomains() {
        return this.identityClient.listAvailabilityDomains(ListAvailabilityDomainsRequest.builder().compartmentId(this.compartmentId).build()).getItems();
    }

    public List<Shape> getShapes(String availabilityDomain) {
        return this.getShapes(availabilityDomain, null);
    }

    public List<Shape> getShapes(String availabilityDomain, String imageId) {
        ListShapesRequest.Builder b = ListShapesRequest.builder().compartmentId(this.compartmentId).availabilityDomain(availabilityDomain);
        if (imageId != null && !imageId.isBlank()) {
            b.imageId(imageId.trim());
        }
        return this.computeClient.listShapes(b.build()).getItems();
    }

    public List<Instance> listInstances() {
        return this.computeClient.listInstances(ListInstancesRequest.builder().compartmentId(this.compartmentId).lifecycleState(Instance.LifecycleState.Running).build()).getItems();
    }

    public List<Instance> listAllInstances() {
        ArrayList<Instance> all = new ArrayList<Instance>();
        for (Instance.LifecycleState state : List.of(Instance.LifecycleState.Running, Instance.LifecycleState.Stopped, Instance.LifecycleState.Starting, Instance.LifecycleState.Stopping)) {
            all.addAll(this.computeClient.listInstances(ListInstancesRequest.builder().compartmentId(this.compartmentId).lifecycleState(state).build()).getItems());
        }
        return all;
    }

    public List<Vcn> listVcn() {
        return this.virtualNetworkClient.listVcns(ListVcnsRequest.builder().compartmentId(this.compartmentId).lifecycleState(Vcn.LifecycleState.Available).build()).getItems();
    }

    public List<Subnet> listSubnets(String vcnId) {
        return this.virtualNetworkClient.listSubnets(ListSubnetsRequest.builder().compartmentId(this.compartmentId).vcnId(vcnId).lifecycleState(Subnet.LifecycleState.Available).build()).getItems();
    }

    public Vcn createVcn(String cidrBlock) {
        CreateVcnResponse response = this.virtualNetworkClient.createVcn(CreateVcnRequest.builder().createVcnDetails(CreateVcnDetails.builder().compartmentId(this.compartmentId).displayName("oci-worker-vcn").cidrBlocks(List.of(cidrBlock)).build()).build());
        log.info("Created VCN: {}", (Object)response.getVcn().getDisplayName());
        return response.getVcn();
    }

    public InternetGateway createInternetGateway(Vcn vcn) {
        CreateInternetGatewayResponse response = this.virtualNetworkClient.createInternetGateway(CreateInternetGatewayRequest.builder().createInternetGatewayDetails(CreateInternetGatewayDetails.builder().compartmentId(this.compartmentId).vcnId(vcn.getId()).displayName("oci-worker-igw").isEnabled(Boolean.valueOf(true)).build()).build());
        return response.getInternetGateway();
    }

    public void addInternetGatewayToDefaultRouteTable(Vcn vcn, InternetGateway igw) {
        GetRouteTableResponse rtResponse = this.virtualNetworkClient.getRouteTable(GetRouteTableRequest.builder().rtId(vcn.getDefaultRouteTableId()).build());
        ArrayList<RouteRule> rules = new ArrayList<RouteRule>(rtResponse.getRouteTable().getRouteRules());
        rules.add(RouteRule.builder().destination("0.0.0.0/0").destinationType(RouteRule.DestinationType.CidrBlock).networkEntityId(igw.getId()).build());
        this.virtualNetworkClient.updateRouteTable(UpdateRouteTableRequest.builder().rtId(vcn.getDefaultRouteTableId()).updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build()).build());
    }

    public Subnet createSubnet(String availabilityDomain, String cidrBlock, Vcn vcn) {
        try {
            CreateSubnetResponse response = this.virtualNetworkClient.createSubnet(CreateSubnetRequest.builder().createSubnetDetails(CreateSubnetDetails.builder().compartmentId(this.compartmentId).vcnId(vcn.getId()).displayName("oci-worker-subnet").cidrBlock(cidrBlock).availabilityDomain(availabilityDomain).build()).build());
            return response.getSubnet();
        }
        catch (Exception e) {
            log.error("Failed to create subnet: {}", (Object)e.getMessage());
            return null;
        }
    }

    public Image getImage(Shape shape) {
        List images;
        String apiOs;
        String os = this.user.getOperationSystem() != null ? this.user.getOperationSystem() : "Ubuntu";
        String apiVersion = null;
        if (os.startsWith("Ubuntu")) {
            apiOs = "Canonical Ubuntu";
            if (os.contains("20.04")) {
                apiVersion = "20.04";
            } else if (os.contains("22.04")) {
                apiVersion = "22.04";
            } else if (os.contains("24.04")) {
                apiVersion = "24.04";
            }
        } else {
            apiOs = os;
        }
        ListImagesRequest.Builder reqBuilder = ListImagesRequest.builder().compartmentId(this.compartmentId).shape(shape.getShape()).operatingSystem(apiOs).sortBy(ListImagesRequest.SortBy.Timecreated).sortOrder(ListImagesRequest.SortOrder.Desc);
        if (apiVersion != null) {
            reqBuilder.operatingSystemVersion(apiVersion);
        }
        if (CollectionUtil.isEmpty((Collection)(images = this.computeClient.listImages(reqBuilder.build()).getItems()))) {
            images = this.computeClient.listImages(ListImagesRequest.builder().compartmentId(this.compartmentId).shape(shape.getShape()).sortBy(ListImagesRequest.SortBy.Timecreated).sortOrder(ListImagesRequest.SortOrder.Desc).build()).getItems();
        }
        return CollectionUtil.isEmpty((Collection)images) ? null : (Image)images.get(0);
    }

    public synchronized InstanceDetailDTO createInstanceData() {
        boolean anyAdLeft;
        InstanceDetailDTO result = new InstanceDetailDTO();
        result.setTaskId(this.user.getTaskId());
        result.setUsername(this.user.getUsername());
        result.setRegion(this.user.getOciCfg().getRegion());
        result.setArchitecture(this.user.getArchitecture());
        result.setCreateNumbers(this.user.getCreateNumbers());
        List availabilityDomains = this.getAvailabilityDomains();
        String targetShape = OciClientService.resolveTargetShape((String)this.user.getArchitecture());
        result.setResolvedTargetShape(targetShape);
        Set excludedAds = this.user.getExcludedAvailabilityDomains() != null ? this.user.getExcludedAvailabilityDomains() : Set.of();
        boolean sawOutOfCapacity = false;
        block14: for (AvailabilityDomain ad2 : availabilityDomains) {
            List shapes;
            if (excludedAds.contains(ad2.getName())) continue;
            String tryNextAdSuffix = OciClientService.hasNextAvailabilityDomain((List)availabilityDomains, (AvailabilityDomain)ad2, (Set)excludedAds) ? "\uff0c\u5c1d\u8bd5\u4e0b\u4e00\u53ef\u7528\u57df" : "";
            try {
                shapes = this.getShapes(ad2.getName()).stream().filter(s -> s.getShape().equals(targetShape)).collect(Collectors.toList());
            }
            catch (Exception e) {
                OciClientService.markAdExcludedNoShape((InstanceDetailDTO)result, (String)ad2.getName());
                log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u5f53\u524d\u53ef\u7528\u57df\u65e0\u6b64 Shape [{}]\uff08ListShapes \u5931\u8d25\uff09", new Object[]{this.user.getUsername(), ad2.getName(), targetShape});
                continue;
            }
            if (shapes.isEmpty()) {
                OciClientService.markAdExcludedNoShape((InstanceDetailDTO)result, (String)ad2.getName());
                log.info("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u5f53\u524d\u53ef\u7528\u57df\u65e0\u6b64 Shape [{}]", new Object[]{this.user.getUsername(), ad2.getName(), targetShape});
                continue;
            }
            for (Shape shape : shapes) {
                String hint;
                Subnet subnet;
                Image image = this.getImage(shape);
                if (image == null) continue;
                try {
                    subnet = this.findOrCreateSubnet(ad2.getName());
                }
                catch (BmcException e) {
                    hint = OciClientService.describeBmcFailure((BmcException)e);
                    result.setFailureHint(hint);
                    if (OciClientService.isVcnCountLimitError((BmcException)e)) {
                        log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - {}", new Object[]{this.user.getUsername(), ad2.getName(), hint});
                        continue block14;
                    }
                    log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u51c6\u5907\u7f51\u7edc\u5931\u8d25{}\u3002{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix, hint});
                    continue block14;
                }
                catch (Exception e) {
                    hint = OciClientService.describeThrowableFailure((Throwable)e);
                    result.setFailureHint(hint);
                    log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u51c6\u5907\u7f51\u7edc\u5931\u8d25{}\u3002{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix, hint});
                    continue block14;
                }
                if (subnet == null) {
                    result.setNoPubVcn(true);
                    log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u65e0\u53ef\u7528\u516c\u6709\u5b50\u7f51{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix});
                    continue block14;
                }
                log.info("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}],\u533a\u57df:[{}], AD:[{}], \u5b50\u7f51:[{}] \u521b\u5efa\u5b9e\u4f8b...", new Object[]{this.user.getUsername(), this.user.getOciCfg().getRegion(), ad2.getName(), subnet.getDisplayName()});
                try {
                    String cloudInitScript = CommonUtils.getPwdShell((String)this.user.getRootPassword(), (String)this.user.getCustomScript());
                    LaunchInstanceDetails launchDetails = this.buildLaunchDetails(ad2, shape, image, subnet, cloudInitScript);
                    Instance instance = this.launchInstance(launchDetails);
                    String publicIp = this.getInstancePublicIp(instance);
                    try {
                        this.ensureIpv4AllIngressSecurityRules(subnet.getId());
                    }
                    catch (Exception e) {
                        log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - IPv4 \u5b89\u5168\u5217\u8868\u5165\u7ad9\u89c4\u5219\u5931\u8d25: {}", (Object)this.user.getUsername(), (Object)e.getMessage());
                    }
                    if (Boolean.TRUE.equals(this.user.getAssignIpv6())) {
                        boolean ipv6Ready;
                        String ipv6Address = null;
                        try {
                            ipv6Address = this.assignIpv6ToInstance(instance, subnet);
                            if (StrUtil.isNotBlank((CharSequence)ipv6Address)) {
                                result.setIpv6Address(ipv6Address);
                                log.info("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - IPv6 \u5df2\u5206\u914d: {}", (Object)this.user.getUsername(), (Object)ipv6Address);
                            } else {
                                log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - IPv6 \u5206\u914d\u672a\u5b8c\u6210\uff08VCN/\u5b50\u7f51/\u5730\u5740\u672a\u5c31\u7eea\uff09", (Object)this.user.getUsername());
                            }
                        }
                        catch (Exception e) {
                            log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - IPv6 \u5206\u914d\u5931\u8d25: {}", (Object)this.user.getUsername(), (Object)e.getMessage());
                        }
                        boolean bl = ipv6Ready = StrUtil.isNotBlank((CharSequence)ipv6Address) || VcnIpv6Util.isEnabled((VirtualNetwork)this.virtualNetworkClient, (String)subnet.getVcnId());
                        if (ipv6Ready) {
                            try {
                                this.ensureIpv6AllSecurityRules(subnet.getId());
                            }
                            catch (Exception e) {
                                log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - IPv6 \u5b89\u5168\u5217\u8868\u89c4\u5219\u5931\u8d25: {}", (Object)this.user.getUsername(), (Object)e.getMessage());
                            }
                        } else {
                            log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}] - VCN \u672a\u542f\u7528 IPv6\uff0c\u8df3\u8fc7 ::/0 \u5b89\u5168\u5217\u8868", (Object)this.user.getUsername());
                        }
                    }
                    result.setSuccess(true);
                    result.setInstanceId(instance.getId());
                    result.setInstanceName(instance.getDisplayName());
                    result.setShape(shape.getShape());
                    this.fillResultHardwareFromLaunch(result, instance, shape);
                    result.setDisk(this.user.getDisk());
                    result.setPublicIp(publicIp);
                    result.setImage(image.getId());
                    result.setRootPassword(this.user.getRootPassword());
                    result.setRegion(this.user.getOciCfg().getRegion());
                    return result;
                }
                catch (BmcException e) {
                    if (e.getStatusCode() == 401) {
                        result.setDie(true);
                        return result;
                    }
                    if (OciClientService.isBootVolumeQuotaError((BmcException)e)) {
                        hint = OciClientService.describeBmcFailure((BmcException)e);
                        result.setBootVolumeQuotaExceeded(true);
                        result.setFailureHint(hint);
                        log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - {}", new Object[]{this.user.getUsername(), ad2.getName(), hint});
                        return result;
                    }
                    hint = OciClientService.describeBmcFailure((BmcException)e);
                    if (OciClientService.isOutOfHostCapacityError((BmcException)e)) {
                        sawOutOfCapacity = true;
                        log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u5bb9\u91cf\u4e0d\u8db3{}\u3002{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix, hint});
                        continue block14;
                    }
                    log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u521b\u5efa\u5931\u8d25{}\u3002{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix, hint});
                    continue block14;
                }
                catch (Exception e) {
                    hint = OciClientService.describeThrowableFailure((Throwable)e);
                    result.setFailureHint(hint);
                    log.warn("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}], AD:[{}] - \u521b\u5efa\u5f02\u5e38{}\u3002{}", new Object[]{this.user.getUsername(), ad2.getName(), tryNextAdSuffix, hint});
                    continue block14;
                }
            }
        }
        if (sawOutOfCapacity) {
            result.setOutOfCapacity(true);
        }
        if (!(availabilityDomains.isEmpty() || (anyAdLeft = availabilityDomains.stream().anyMatch(ad -> !excludedAds.contains(ad.getName()) && (result.getAdsExcludedNoShape() == null || !result.getAdsExcludedNoShape().contains(ad.getName())))) || result.isSuccess())) {
            result.setAllAdsExcludedNoShape(true);
        }
        return result;
    }

    private static String resolveTargetShape(String arch) {
        if (arch != null && ("ARM".equalsIgnoreCase(arch) || "AMD".equalsIgnoreCase(arch))) {
            return ArchitectureEnum.getShape((String)arch);
        }
        if (ShapeSeriesUtil.isFullShapeName((String)arch)) {
            return arch.trim();
        }
        return ArchitectureEnum.getShape((String)(arch == null ? "ARM" : arch));
    }

    private static void markAdExcludedNoShape(InstanceDetailDTO result, String adName) {
        if (result.getAdsExcludedNoShape() == null) {
            result.setAdsExcludedNoShape(new ArrayList());
        }
        if (!result.getAdsExcludedNoShape().contains(adName)) {
            result.getAdsExcludedNoShape().add(adName);
        }
    }

    private static boolean hasNextAvailabilityDomain(List<AvailabilityDomain> ads, AvailabilityDomain current, Set<String> excludedAds) {
        boolean seenCurrent = false;
        for (AvailabilityDomain ad : ads) {
            if (excludedAds != null && excludedAds.contains(ad.getName())) continue;
            if (Objects.equals(ad.getName(), current.getName())) {
                seenCurrent = true;
                continue;
            }
            if (!seenCurrent) continue;
            return true;
        }
        return false;
    }

    private static boolean isOutOfHostCapacityError(BmcException e) {
        if (OciClientService.isBootVolumeQuotaError((BmcException)e)) {
            return false;
        }
        String em = e.getMessage() == null ? "" : e.getMessage();
        return e.getStatusCode() == 500 || em.contains("Out of host capacity") || e.getStatusCode() == 400 && em.contains("LimitExceeded") || e.getStatusCode() == 429;
    }

    private static boolean isBootVolumeQuotaError(BmcException e) {
        String em;
        String string = em = e.getMessage() == null ? "" : e.getMessage();
        if (em.contains("bootVolumeQuota")) {
            return true;
        }
        return em.contains("QuotaExceeded") && (em.toLowerCase().contains("bootvolume") || em.contains("boot volume"));
    }

    static String describeThrowableFailure(Throwable e) {
        if (e instanceof BmcException) {
            BmcException bmc = (BmcException)e;
            return OciClientService.describeBmcFailure((BmcException)bmc);
        }
        Throwable c = e.getCause();
        if (c instanceof BmcException) {
            BmcException bmc = (BmcException)c;
            return OciClientService.describeBmcFailure((BmcException)bmc);
        }
        String msg = e.getMessage();
        if (msg != null && msg.contains("ListShapes")) {
            return "\u5f53\u524d\u53ef\u7528\u57df\u65e0\u6b64 Shape";
        }
        if (msg == null || msg.isBlank()) {
            return "\u521b\u5efa\u5931\u8d25";
        }
        int cut = Math.min(msg.length(), 200);
        return msg.substring(0, cut);
    }

    static String describeBmcFailure(BmcException e) {
        String em;
        String string = em = e.getMessage() == null ? "" : e.getMessage();
        if (OciClientService.isBootVolumeQuotaError((BmcException)e)) {
            return "\u5f15\u5bfc\u5377\uff08\u542f\u52a8\u76d8\uff09\u5b58\u50a8\u914d\u989d\u5df2\u8fbe\u4e0a\u9650\uff0c\u786c\u76d8\u914d\u989d\u7528\u5c3d\uff0c\u521b\u5efa\u5931\u8d25";
        }
        if (OciClientService.isVcnCountLimitError((BmcException)e)) {
            return "VCN \u6570\u91cf\u5df2\u8fbe\u914d\u989d\u4e0a\u9650\uff0c\u65e0\u6cd5\u521b\u5efa\u865a\u62df\u4e91\u7f51\u7edc\uff0c\u8bf7\u5220\u9664\u65e0\u7528 VCN \u6216\u7533\u8bf7\u63d0\u989d";
        }
        if (em.contains("QuotaExceeded")) {
            return "OCI \u670d\u52a1\u914d\u989d\u5df2\u8fbe\u4e0a\u9650\uff0c\u521b\u5efa\u5931\u8d25";
        }
        if (em.contains("Out of host capacity")) {
            return "\u4e3b\u673a\u5bb9\u91cf\u4e0d\u8db3";
        }
        if (em.contains("LimitExceeded")) {
            return "\u5df2\u89e6\u53d1 OCI \u670d\u52a1\u9650\u5236\uff0c\u521b\u5efa\u5931\u8d25";
        }
        if (e.getStatusCode() == 429) {
            return "\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5";
        }
        int code = e.getStatusCode();
        int cut = Math.min(em.length(), 200);
        String brief = em.substring(0, cut);
        return code > 0 ? "OCI \u9519\u8bef (" + code + "): " + brief : brief;
    }

    private static boolean isVcnCountLimitError(BmcException e) {
        String em;
        String string = em = e.getMessage() == null ? "" : e.getMessage();
        if (em.contains("vcn-count")) {
            return true;
        }
        return em.contains("LimitExceeded") && (em.contains("CreateVcn") || em.contains("service limits were exceeded") && em.toLowerCase().contains("vcn"));
    }

    private void fillResultHardwareFromLaunch(InstanceDetailDTO result, Instance instance, Shape shape) {
        String shapeName = shape != null ? shape.getShape() : "";
        boolean flex = shapeName.contains("Flex");
        InstanceShapeConfig sc = instance.getShapeConfig();
        if (sc != null) {
            if (sc.getOcpus() != null) {
                result.setOcpus(Double.valueOf(sc.getOcpus().doubleValue()));
            }
            if (sc.getMemoryInGBs() != null) {
                result.setMemory(Double.valueOf(sc.getMemoryInGBs().doubleValue()));
            }
        }
        if (result.getOcpus() == null) {
            result.setOcpus(Double.valueOf(flex ? (this.user.getOcpus() != null ? this.user.getOcpus() : 1.0) : 1.0));
        }
        if (result.getMemory() == null) {
            if (flex) {
                result.setMemory(Double.valueOf(this.user.getMemory() != null ? this.user.getMemory() : 6.0));
            } else {
                result.setMemory(Double.valueOf(OciClientService.fixedShapeDefaultMemoryGb((String)shapeName)));
            }
        }
    }

    private static double fixedShapeDefaultMemoryGb(String shapeName) {
        if (StrUtil.isBlank((CharSequence)shapeName)) {
            return 1.0;
        }
        if (shapeName.contains("Micro")) {
            return 1.0;
        }
        return 1.0;
    }

    private Subnet findOrCreateSubnet(String availabilityDomain) {
        List vcnList = this.listVcn();
        if (CollectionUtil.isEmpty((Collection)vcnList)) {
            log.info("\u3010\u5f00\u673a\u4efb\u52a1\u3011\u7528\u6237:[{}],\u533a\u57df:[{}] - \u672a\u627e\u5230 VCN\uff0c\u6b63\u5728\u521b\u5efa...", (Object)this.user.getUsername(), (Object)this.user.getOciCfg().getRegion());
            Vcn vcn = this.createVcn("10.0.0.0/16");
            InternetGateway igw = this.createInternetGateway(vcn);
            this.addInternetGatewayToDefaultRouteTable(vcn, igw);
            return this.createSubnet(availabilityDomain, "10.0.0.0/24", vcn);
        }
        for (Vcn vcn : vcnList) {
            List subnets;
            List igws = this.virtualNetworkClient.listInternetGateways(ListInternetGatewaysRequest.builder().vcnId(vcn.getId()).compartmentId(this.compartmentId).build()).getItems();
            if (CollectionUtil.isEmpty((Collection)igws)) {
                InternetGateway igw = this.createInternetGateway(vcn);
                this.addInternetGatewayToDefaultRouteTable(vcn, igw);
            }
            if (CollectionUtil.isEmpty((Collection)(subnets = this.listSubnets(vcn.getId())))) {
                return this.createSubnet(availabilityDomain, "10.0.0.0/24", vcn);
            }
            for (Subnet subnet : subnets) {
                if (subnet.getProhibitInternetIngress().booleanValue()) continue;
                return subnet;
            }
        }
        return null;
    }

    private String resolveLaunchDisplayName() {
        int ord;
        int target = this.user.getCreateNumbers() != null && this.user.getCreateNumbers() > 0 ? this.user.getCreateNumbers() : 1;
        int n = ord = this.user.getInstanceDisplayOrdinal() != null && this.user.getInstanceDisplayOrdinal() > 0 ? this.user.getInstanceDisplayOrdinal() : 1;
        if (target == 1) {
            return "oci-worker-instance";
        }
        if (target <= 4) {
            int o = Math.min(Math.max(ord, 1), target);
            char letter = (char)(65 + o - 1);
            return "oci-worker-" + letter;
        }
        return "oci-instance-" + ord;
    }

    private static InstanceSourceViaImageDetails buildBootVolumeSource(String imageId, SysUserDTO user) {
        long sizeGb = user.getDisk() != null ? (long)user.getDisk().intValue() : 50L;
        InstanceSourceViaImageDetails.Builder b = InstanceSourceViaImageDetails.builder().imageId(imageId).bootVolumeSizeInGBs(Long.valueOf(sizeGb));
        int vpus = BootVolumeVpusUtil.normalize((Integer)user.getVpusPerGB());
        if (vpus > 0) {
            b.bootVolumeVpusPerGB(Long.valueOf(vpus));
        }
        return b.build();
    }

    private LaunchInstanceDetails buildLaunchDetails(AvailabilityDomain ad, Shape shape, Image image, Subnet subnet, String cloudInitScript) {
        LaunchInstanceDetails.Builder builder = LaunchInstanceDetails.builder().compartmentId(this.compartmentId).availabilityDomain(ad.getName()).displayName(this.resolveLaunchDisplayName()).shape(shape.getShape()).sourceDetails((InstanceSourceDetails)OciClientService.buildBootVolumeSource((String)image.getId(), (SysUserDTO)this.user)).createVnicDetails(CreateVnicDetails.builder().subnetId(subnet.getId()).assignPublicIp(Boolean.valueOf(this.user.getAssignPublicIp() != null ? this.user.getAssignPublicIp() : true)).build()).metadata(cloudInitScript != null && !cloudInitScript.isEmpty() ? Map.of("user_data", Base64.getEncoder().encodeToString(cloudInitScript.getBytes(StandardCharsets.UTF_8))) : null);
        if (shape.getShape().contains("Flex")) {
            builder.shapeConfig(LaunchInstanceShapeConfigDetails.builder().ocpus(Float.valueOf(this.user.getOcpus() != null ? this.user.getOcpus().floatValue() : 1.0f)).memoryInGBs(Float.valueOf(this.user.getMemory() != null ? this.user.getMemory().floatValue() : 6.0f)).build());
        }
        return builder.build();
    }

    private Instance launchInstance(LaunchInstanceDetails details) throws Exception {
        ComputeWaiters waiters = this.computeClient.newWaiters((WorkRequest)this.workRequestClient);
        LaunchInstanceResponse launchResponse = (LaunchInstanceResponse)waiters.forLaunchInstance(LaunchInstanceRequest.builder().launchInstanceDetails(details).build()).execute();
        return ((GetInstanceResponse)waiters.forInstance(GetInstanceRequest.builder().instanceId(launchResponse.getInstance().getId()).build(), new Instance.LifecycleState[]{Instance.LifecycleState.Running}).execute()).getInstance();
    }

    private String assignIpv6ToInstance(Instance instance, Subnet subnet) {
        Subnet freshSubnet;
        Vcn vcn;
        String vnicId;
        block9: {
            List attachments = this.computeClient.listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(this.compartmentId).instanceId(instance.getId()).build()).getItems();
            if (attachments.isEmpty()) {
                return null;
            }
            vnicId = ((VnicAttachment)attachments.get(0)).getVnicId();
            String subnetId = subnet.getId();
            vcn = this.virtualNetworkClient.getVcn(GetVcnRequest.builder().vcnId(subnet.getVcnId()).build()).getVcn();
            if (vcn.getIpv6CidrBlocks() == null || vcn.getIpv6CidrBlocks().isEmpty()) {
                block8: {
                    try {
                        this.virtualNetworkClient.addIpv6VcnCidr(AddIpv6VcnCidrRequest.builder().vcnId(vcn.getId()).addVcnIpv6CidrDetails(AddVcnIpv6CidrDetails.builder().isOracleGuaAllocationEnabled(Boolean.valueOf(true)).build()).build());
                        Thread.sleep(8000L);
                    }
                    catch (Exception e) {
                        String em;
                        String string = em = e.getMessage() == null ? "" : e.getMessage();
                        if (em.contains("already exists") || em.contains("already has")) break block8;
                        log.warn("VCN IPv6 CIDR \u6dfb\u52a0\u5931\u8d25: {}", (Object)em);
                        return null;
                    }
                }
                vcn = this.virtualNetworkClient.getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
            }
            if ((freshSubnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet()).getIpv6CidrBlocks() == null || freshSubnet.getIpv6CidrBlocks().isEmpty()) {
                String vcnIpv6Cidr;
                String string = vcnIpv6Cidr = vcn.getIpv6CidrBlocks() != null && !vcn.getIpv6CidrBlocks().isEmpty() ? (String)vcn.getIpv6CidrBlocks().get(0) : null;
                if (vcnIpv6Cidr == null) {
                    return null;
                }
                String subnetIpv6Cidr = vcnIpv6Cidr.replaceAll("/\\d+$", "/64");
                try {
                    this.virtualNetworkClient.updateSubnet(UpdateSubnetRequest.builder().subnetId(subnetId).updateSubnetDetails(UpdateSubnetDetails.builder().ipv6CidrBlocks(List.of(subnetIpv6Cidr)).build()).build());
                    Thread.sleep(3000L);
                }
                catch (Exception e) {
                    String em;
                    String string2 = em = e.getMessage() == null ? "" : e.getMessage();
                    if (em.contains("already exists") || em.contains("already has")) break block9;
                    log.warn("\u5b50\u7f51 IPv6 CIDR \u6dfb\u52a0\u5931\u8d25: {}", (Object)em);
                    return null;
                }
            }
        }
        this.ensureIpv6InternetRoute(vcn, freshSubnet);
        Ipv6 ipv6 = this.virtualNetworkClient.createIpv6(CreateIpv6Request.builder().createIpv6Details(CreateIpv6Details.builder().vnicId(vnicId).build()).build()).getIpv6();
        return ipv6 != null ? ipv6.getIpAddress() : null;
    }

    private void ensureIpv4AllIngressSecurityRules(String subnetId) {
        Subnet subnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
        if (subnet.getSecurityListIds() == null || subnet.getSecurityListIds().isEmpty()) {
            log.warn("\u5b50\u7f51 {} \u65e0\u5b89\u5168\u5217\u8868\uff0c\u8df3\u8fc7 IPv4 0.0.0.0/0 \u5165\u7ad9\u89c4\u5219", (Object)subnetId);
            return;
        }
        String secListId = (String)subnet.getSecurityListIds().get(0);
        SecurityList secList = this.virtualNetworkClient.getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
        ArrayList<IngressSecurityRule> ingressRules = new ArrayList<IngressSecurityRule>(secList.getIngressSecurityRules());
        if (OciClientService.hasIpv4AllIngress(ingressRules)) {
            return;
        }
        ingressRules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("all").description("oci-worker auto IPv4 ingress").build());
        this.virtualNetworkClient.updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(secList.getEgressSecurityRules()).build()).build());
        log.info("\u5b50\u7f51 {} \u5b89\u5168\u5217\u8868\u5df2\u8865 IPv4 0.0.0.0/0 \u5168\u534f\u8bae\u5165\u7ad9", (Object)subnetId);
    }

    private void ensureIpv6AllSecurityRules(String subnetId) {
        boolean changed;
        if (!VcnIpv6Util.isEnabledForSubnet((VirtualNetwork)this.virtualNetworkClient, (String)subnetId)) {
            log.warn("\u5b50\u7f51 {} \u6240\u5c5e VCN \u672a\u542f\u7528 IPv6\uff0c\u8df3\u8fc7 ::/0 \u5b89\u5168\u5217\u8868\u89c4\u5219", (Object)subnetId);
            return;
        }
        Subnet subnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
        if (subnet.getSecurityListIds() == null || subnet.getSecurityListIds().isEmpty()) {
            log.warn("\u5b50\u7f51 {} \u65e0\u5b89\u5168\u5217\u8868\uff0c\u8df3\u8fc7 IPv6 ::/0 \u89c4\u5219", (Object)subnetId);
            return;
        }
        String secListId = (String)subnet.getSecurityListIds().get(0);
        SecurityList secList = this.virtualNetworkClient.getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
        int ingressBefore = secList.getIngressSecurityRules() != null ? secList.getIngressSecurityRules().size() : 0;
        int egressBefore = secList.getEgressSecurityRules() != null ? secList.getEgressSecurityRules().size() : 0;
        List ingressRules = OciClientService.dedupeIpv6AllIngress(new ArrayList(secList.getIngressSecurityRules() != null ? secList.getIngressSecurityRules() : List.of()));
        List egressRules = OciClientService.dedupeIpv6AllEgress(new ArrayList(secList.getEgressSecurityRules() != null ? secList.getEgressSecurityRules() : List.of()));
        boolean bl = changed = ingressRules.size() != ingressBefore || egressRules.size() != egressBefore;
        if (!OciClientService.hasIpv6AllIngress((List)ingressRules)) {
            ingressRules.add(IngressSecurityRule.builder().source("::/0").sourceType(IngressSecurityRule.SourceType.CidrBlock).protocol("all").description("oci-worker auto IPv6 ingress").build());
            changed = true;
        }
        if (!OciClientService.hasIpv6AllEgress((List)egressRules)) {
            egressRules.add(EgressSecurityRule.builder().destination("::/0").destinationType(EgressSecurityRule.DestinationType.CidrBlock).protocol("all").description("oci-worker auto IPv6 egress").build());
            changed = true;
        }
        if (!changed) {
            return;
        }
        this.virtualNetworkClient.updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()).build());
        log.info("\u5b50\u7f51 {} \u5b89\u5168\u5217\u8868\u5df2\u8865/\u6574\u7406 IPv6 ::/0 \u5168\u534f\u8bae\u5165\u7ad9/\u51fa\u7ad9", (Object)subnetId);
    }

    private static boolean isProtocolAll(String protocol) {
        return protocol != null && "all".equalsIgnoreCase(protocol.trim());
    }

    private static boolean isIpv6WildcardCidr(String cidr) {
        return cidr != null && "::/0".equals(cidr.trim());
    }

    private static boolean hasIpv4AllIngress(List<IngressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getSource()) && OciClientService.isProtocolAll((String)r.getProtocol()));
    }

    private static boolean hasIpv6AllIngress(List<IngressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> OciClientService.isIpv6WildcardCidr((String)r.getSource()) && OciClientService.isProtocolAll((String)r.getProtocol()));
    }

    private static boolean hasIpv6AllEgress(List<EgressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> OciClientService.isIpv6WildcardCidr((String)r.getDestination()) && OciClientService.isProtocolAll((String)r.getProtocol()));
    }

    private static List<IngressSecurityRule> dedupeIpv6AllIngress(List<IngressSecurityRule> rules) {
        ArrayList<IngressSecurityRule> out = new ArrayList<IngressSecurityRule>();
        boolean seenIpv6All = false;
        for (IngressSecurityRule r : rules) {
            if (OciClientService.isIpv6WildcardCidr((String)r.getSource()) && OciClientService.isProtocolAll((String)r.getProtocol())) {
                if (seenIpv6All) continue;
                out.add(r);
                seenIpv6All = true;
                continue;
            }
            out.add(r);
        }
        return out;
    }

    private static List<EgressSecurityRule> dedupeIpv6AllEgress(List<EgressSecurityRule> rules) {
        ArrayList<EgressSecurityRule> out = new ArrayList<EgressSecurityRule>();
        boolean seenIpv6All = false;
        for (EgressSecurityRule r : rules) {
            if (OciClientService.isIpv6WildcardCidr((String)r.getDestination()) && OciClientService.isProtocolAll((String)r.getProtocol())) {
                if (seenIpv6All) continue;
                out.add(r);
                seenIpv6All = true;
                continue;
            }
            out.add(r);
        }
        return out;
    }

    private void ensureIpv6InternetRoute(Vcn vcn, Subnet subnet) {
        boolean hasIpv6DefaultRoute;
        String routeTableId;
        List igws = this.virtualNetworkClient.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(this.compartmentId).vcnId(vcn.getId()).build()).getItems();
        InternetGateway igw = CollectionUtil.isEmpty((Collection)igws) ? this.createInternetGateway(vcn) : igws.stream().filter(gw -> Boolean.TRUE.equals(gw.getIsEnabled())).findFirst().orElse((InternetGateway)igws.get(0));
        String string = routeTableId = subnet.getRouteTableId() != null ? subnet.getRouteTableId() : vcn.getDefaultRouteTableId();
        if (StrUtil.isBlank((CharSequence)routeTableId)) {
            return;
        }
        RouteTable routeTable = this.virtualNetworkClient.getRouteTable(GetRouteTableRequest.builder().rtId(routeTableId).build()).getRouteTable();
        ArrayList<RouteRule> rules = new ArrayList<RouteRule>();
        if (routeTable.getRouteRules() != null) {
            rules.addAll(routeTable.getRouteRules());
        }
        if (!(hasIpv6DefaultRoute = rules.stream().anyMatch(rule -> "::/0".equals(rule.getDestination()) && RouteRule.DestinationType.CidrBlock.equals((Object)rule.getDestinationType())))) {
            rules.add(RouteRule.builder().destination("::/0").destinationType(RouteRule.DestinationType.CidrBlock).networkEntityId(igw.getId()).description("oci-worker auto add IPv6 default route").build());
            this.virtualNetworkClient.updateRouteTable(UpdateRouteTableRequest.builder().rtId(routeTableId).updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build()).build());
        }
    }

    public String getInstancePublicIp(Instance instance) {
        try {
            List vnicAttachments = this.computeClient.listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(this.compartmentId).instanceId(instance.getId()).build()).getItems();
            for (VnicAttachment attachment : vnicAttachments) {
                Vnic vnic = this.virtualNetworkClient.getVnic(GetVnicRequest.builder().vnicId(attachment.getVnicId()).build()).getVnic();
                if (vnic.getPublicIp() == null) continue;
                return vnic.getPublicIp();
            }
        }
        catch (Exception e) {
            log.warn("Failed to get public IP: {}", (Object)e.getMessage());
        }
        return null;
    }

    @Generated
    public ComputeClient getComputeClient() {
        return this.computeClient;
    }

    @Generated
    public IdentityClient getIdentityClient() {
        return this.identityClient;
    }

    @Generated
    public WorkRequestClient getWorkRequestClient() {
        return this.workRequestClient;
    }

    @Generated
    public VirtualNetworkClient getVirtualNetworkClient() {
        return this.virtualNetworkClient;
    }

    @Generated
    public BlockstorageClient getBlockstorageClient() {
        return this.blockstorageClient;
    }

    @Generated
    public ObjectStorageClient getObjectStorageClient() {
        return this.objectStorageClient;
    }

    @Generated
    public MonitoringClient getMonitoringClient() {
        return this.monitoringClient;
    }

    @Generated
    public NetworkLoadBalancerClient getNetworkLoadBalancerClient() {
        return this.networkLoadBalancerClient;
    }

    @Generated
    public SimpleAuthenticationDetailsProvider getProvider() {
        return this.provider;
    }

    @Generated
    public SysUserDTO getUser() {
        return this.user;
    }

    @Generated
    public String getCompartmentId() {
        return this.compartmentId;
    }

    @Generated
    public HttpClientConnectionManager getOciSocksPoolingManager() {
        return this.ociSocksPoolingManager;
    }

    @Generated
    public void setUser(SysUserDTO user) {
        this.user = user;
    }

    @Generated
    public void setCompartmentId(String compartmentId) {
        this.compartmentId = compartmentId;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OciClientService)) {
            return false;
        }
        OciClientService other = (OciClientService)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        ComputeClient this$computeClient = this.getComputeClient();
        ComputeClient other$computeClient = other.getComputeClient();
        if (this$computeClient == null ? other$computeClient != null : !this$computeClient.equals(other$computeClient)) {
            return false;
        }
        IdentityClient this$identityClient = this.getIdentityClient();
        IdentityClient other$identityClient = other.getIdentityClient();
        if (this$identityClient == null ? other$identityClient != null : !this$identityClient.equals(other$identityClient)) {
            return false;
        }
        WorkRequestClient this$workRequestClient = this.getWorkRequestClient();
        WorkRequestClient other$workRequestClient = other.getWorkRequestClient();
        if (this$workRequestClient == null ? other$workRequestClient != null : !this$workRequestClient.equals(other$workRequestClient)) {
            return false;
        }
        VirtualNetworkClient this$virtualNetworkClient = this.getVirtualNetworkClient();
        VirtualNetworkClient other$virtualNetworkClient = other.getVirtualNetworkClient();
        if (this$virtualNetworkClient == null ? other$virtualNetworkClient != null : !this$virtualNetworkClient.equals(other$virtualNetworkClient)) {
            return false;
        }
        BlockstorageClient this$blockstorageClient = this.getBlockstorageClient();
        BlockstorageClient other$blockstorageClient = other.getBlockstorageClient();
        if (this$blockstorageClient == null ? other$blockstorageClient != null : !this$blockstorageClient.equals(other$blockstorageClient)) {
            return false;
        }
        ObjectStorageClient this$objectStorageClient = this.getObjectStorageClient();
        ObjectStorageClient other$objectStorageClient = other.getObjectStorageClient();
        if (this$objectStorageClient == null ? other$objectStorageClient != null : !this$objectStorageClient.equals(other$objectStorageClient)) {
            return false;
        }
        MonitoringClient this$monitoringClient = this.getMonitoringClient();
        MonitoringClient other$monitoringClient = other.getMonitoringClient();
        if (this$monitoringClient == null ? other$monitoringClient != null : !this$monitoringClient.equals(other$monitoringClient)) {
            return false;
        }
        NetworkLoadBalancerClient this$networkLoadBalancerClient = this.getNetworkLoadBalancerClient();
        NetworkLoadBalancerClient other$networkLoadBalancerClient = other.getNetworkLoadBalancerClient();
        if (this$networkLoadBalancerClient == null ? other$networkLoadBalancerClient != null : !this$networkLoadBalancerClient.equals(other$networkLoadBalancerClient)) {
            return false;
        }
        SimpleAuthenticationDetailsProvider this$provider = this.getProvider();
        SimpleAuthenticationDetailsProvider other$provider = other.getProvider();
        if (this$provider == null ? other$provider != null : !this$provider.equals(other$provider)) {
            return false;
        }
        SysUserDTO this$user = this.getUser();
        SysUserDTO other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) {
            return false;
        }
        String this$compartmentId = this.getCompartmentId();
        String other$compartmentId = other.getCompartmentId();
        if (this$compartmentId == null ? other$compartmentId != null : !this$compartmentId.equals(other$compartmentId)) {
            return false;
        }
        HttpClientConnectionManager this$ociSocksPoolingManager = this.getOciSocksPoolingManager();
        HttpClientConnectionManager other$ociSocksPoolingManager = other.getOciSocksPoolingManager();
        return !(this$ociSocksPoolingManager == null ? other$ociSocksPoolingManager != null : !this$ociSocksPoolingManager.equals(other$ociSocksPoolingManager));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof OciClientService;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ComputeClient $computeClient = this.getComputeClient();
        result = result * 59 + ($computeClient == null ? 43 : $computeClient.hashCode());
        IdentityClient $identityClient = this.getIdentityClient();
        result = result * 59 + ($identityClient == null ? 43 : $identityClient.hashCode());
        WorkRequestClient $workRequestClient = this.getWorkRequestClient();
        result = result * 59 + ($workRequestClient == null ? 43 : $workRequestClient.hashCode());
        VirtualNetworkClient $virtualNetworkClient = this.getVirtualNetworkClient();
        result = result * 59 + ($virtualNetworkClient == null ? 43 : $virtualNetworkClient.hashCode());
        BlockstorageClient $blockstorageClient = this.getBlockstorageClient();
        result = result * 59 + ($blockstorageClient == null ? 43 : $blockstorageClient.hashCode());
        ObjectStorageClient $objectStorageClient = this.getObjectStorageClient();
        result = result * 59 + ($objectStorageClient == null ? 43 : $objectStorageClient.hashCode());
        MonitoringClient $monitoringClient = this.getMonitoringClient();
        result = result * 59 + ($monitoringClient == null ? 43 : $monitoringClient.hashCode());
        NetworkLoadBalancerClient $networkLoadBalancerClient = this.getNetworkLoadBalancerClient();
        result = result * 59 + ($networkLoadBalancerClient == null ? 43 : $networkLoadBalancerClient.hashCode());
        SimpleAuthenticationDetailsProvider $provider = this.getProvider();
        result = result * 59 + ($provider == null ? 43 : $provider.hashCode());
        SysUserDTO $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        String $compartmentId = this.getCompartmentId();
        result = result * 59 + ($compartmentId == null ? 43 : $compartmentId.hashCode());
        HttpClientConnectionManager $ociSocksPoolingManager = this.getOciSocksPoolingManager();
        result = result * 59 + ($ociSocksPoolingManager == null ? 43 : $ociSocksPoolingManager.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "OciClientService(computeClient=" + String.valueOf(this.getComputeClient()) + ", identityClient=" + String.valueOf(this.getIdentityClient()) + ", workRequestClient=" + String.valueOf(this.getWorkRequestClient()) + ", virtualNetworkClient=" + String.valueOf(this.getVirtualNetworkClient()) + ", blockstorageClient=" + String.valueOf(this.getBlockstorageClient()) + ", objectStorageClient=" + String.valueOf(this.getObjectStorageClient()) + ", monitoringClient=" + String.valueOf(this.getMonitoringClient()) + ", networkLoadBalancerClient=" + String.valueOf(this.getNetworkLoadBalancerClient()) + ", provider=" + String.valueOf(this.getProvider()) + ", user=" + String.valueOf(this.getUser()) + ", compartmentId=" + this.getCompartmentId() + ", ociSocksPoolingManager=" + String.valueOf(this.getOciSocksPoolingManager()) + ")";
    }
}

