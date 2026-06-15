package com.ocxworker.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.enums.ArchitectureEnum;
import com.ocxworker.exception.OciException;
import com.ocxworker.model.dto.InstanceDetailDTO;
import com.ocxworker.model.dto.OciProxySnapshot;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.util.BootVolumeVpusUtil;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.ShapeSeriesUtil;
import com.ocxworker.util.VcnIpv6Util;
import com.ocxworker.util.socks.OciSocksApacheConnectionManager;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.core.BlockstorageClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.ComputeWaiters;
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
import com.oracle.bmc.core.model.IngressSecurityRule.SourceType;
import com.oracle.bmc.core.model.RouteRule.DestinationType;
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
import com.oracle.bmc.core.requests.ListImagesRequest.SortBy;
import com.oracle.bmc.core.requests.ListImagesRequest.SortOrder;
import com.oracle.bmc.core.responses.CreateInternetGatewayResponse;
import com.oracle.bmc.core.responses.CreateSubnetResponse;
import com.oracle.bmc.core.responses.CreateVcnResponse;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.core.responses.GetRouteTableResponse;
import com.oracle.bmc.core.responses.LaunchInstanceResponse;
import com.oracle.bmc.http.ClientConfigurator;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.StandardClientProperties;
import com.oracle.bmc.http.client.jersey3.ApacheClientProperties;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.IdentityClient.Builder;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.Tenancy;
import com.oracle.bmc.identity.model.Compartment.LifecycleState;
import com.oracle.bmc.identity.requests.GetTenancyRequest;
import com.oracle.bmc.identity.requests.ListAvailabilityDomainsRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest.AccessLevel;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.workrequests.WorkRequestClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
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

public class OciClientService implements Closeable {
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
            } catch (Exception var2) {
            }
        }
    }

    public OciClientService(SysUserDTO user) {
        this(user, user.getOciCfg() != null ? user.getOciCfg().getRegion() : null);
    }

    public OciClientService(SysUserDTO user, String regionId) {
        this.user = user;
        SysUserDTO.OciCfg ociCfg = user.getOciCfg();
        Region region = resolveRegion(StrUtil.isNotBlank(regionId) ? regionId : ociCfg.getRegion());
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
            .tenantId(ociCfg.getTenantId())
            .userId(ociCfg.getUserId())
            .fingerprint(ociCfg.getFingerprint())
            .privateKeySupplier(() -> {
                try {
                    ByteArrayInputStream var5x;
                    try (
                        FileInputStream fis = new FileInputStream(ociCfg.getPrivateKeyPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ) {
                        byte[] buffer = new byte[1024];

                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }

                        var5x = new ByteArrayInputStream(baos.toByteArray());
                    }

                    return var5x;
                } catch (Exception var10x) {
                    throw new RuntimeException("Failed to read private key");
                }
            })
            .region(region)
            .build();
        ClientConfiguration clientConfig = ClientConfiguration.builder().connectionTimeoutMillis(10000).readTimeoutMillis(30000).build();
        OciProxyConfigService ps = OciProxyConfigService.instance();
        OciProxySnapshot snap = ps == null ? null : ps.snapshot();
        if (ps == null || !ps.ociUsesExplicitClientProxy()) {
            OciProxyConfigService.clearInProcessHttpSocksProxySystemProperties();
        }

        PoolingHttpClientConnectionManager socksPool;
        if (snap != null && snap.usesSocksForOci()) {
            socksPool = OciSocksApacheConnectionManager.create(snap);
        } else {
            socksPool = null;
        }

        this.ociSocksPoolingManager = socksPool;
        Optional<ProxyConfiguration> ocx = ps == null ? Optional.empty() : ps.getOciProxyConfiguration();
        ClientConfigurator ociApacheCfg;
        if (socksPool != null) {
            ociApacheCfg = b -> {
                b.property(ApacheClientProperties.CONNECTION_MANAGER, socksPool);
                b.property(ApacheClientProperties.CONNECTION_MANAGER_SHARED, Boolean.TRUE);
            };
        } else if (ocx.isPresent()) {
            ProxyConfiguration pc = ocx.get();
            ociApacheCfg = c -> c.property(StandardClientProperties.PROXY, pc);
        } else {
            ociApacheCfg = OciProxyConfigService.ociSdkJerseyDirectConfigurator();
        }

        if (snap != null && snap.usesSocksForOci()) {
            log.debug("OCI 客户端经应用内 SOCKS5 代理出站: {}:{}", snap.host(), snap.port());
        }

        Builder idb = (Builder)IdentityClient.builder().configuration(clientConfig);
        idb.additionalClientConfigurator(ociApacheCfg);
        this.identityClient = idb.build(provider);
        com.oracle.bmc.core.ComputeClient.Builder c1 = (com.oracle.bmc.core.ComputeClient.Builder)ComputeClient.builder().configuration(clientConfig);
        c1.additionalClientConfigurator(ociApacheCfg);
        this.computeClient = c1.build(provider);
        com.oracle.bmc.core.BlockstorageClient.Builder c2 = (com.oracle.bmc.core.BlockstorageClient.Builder)BlockstorageClient.builder()
            .configuration(clientConfig);
        c2.additionalClientConfigurator(ociApacheCfg);
        this.blockstorageClient = c2.build(provider);
        com.oracle.bmc.objectstorage.ObjectStorageClient.Builder c3 = (com.oracle.bmc.objectstorage.ObjectStorageClient.Builder)ObjectStorageClient.builder()
            .configuration(clientConfig);
        c3.additionalClientConfigurator(ociApacheCfg);
        this.objectStorageClient = c3.build(provider);
        com.oracle.bmc.workrequests.WorkRequestClient.Builder c4 = (com.oracle.bmc.workrequests.WorkRequestClient.Builder)WorkRequestClient.builder()
            .configuration(clientConfig);
        c4.additionalClientConfigurator(ociApacheCfg);
        this.workRequestClient = c4.build(provider);
        com.oracle.bmc.core.VirtualNetworkClient.Builder c5 = (com.oracle.bmc.core.VirtualNetworkClient.Builder)VirtualNetworkClient.builder()
            .configuration(clientConfig);
        c5.additionalClientConfigurator(ociApacheCfg);
        this.virtualNetworkClient = c5.build(provider);
        com.oracle.bmc.monitoring.MonitoringClient.Builder c6 = (com.oracle.bmc.monitoring.MonitoringClient.Builder)MonitoringClient.builder()
            .configuration(clientConfig);
        c6.additionalClientConfigurator(ociApacheCfg);
        this.monitoringClient = c6.build(provider);
        com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient.Builder c7 = (com.oracle.bmc.networkloadbalancer.NetworkLoadBalancerClient.Builder)NetworkLoadBalancerClient.builder()
            .configuration(clientConfig);
        c7.additionalClientConfigurator(ociApacheCfg);
        this.networkLoadBalancerClient = c7.build(provider);
        this.provider = provider;
        this.compartmentId = StrUtil.isBlank(ociCfg.getCompartmentId())
            ? this.findRootCompartment(this.identityClient, provider.getTenantId())
            : ociCfg.getCompartmentId();
    }

    private static Region resolveRegion(String regionId) {
        if (StrUtil.isBlank(regionId)) {
            throw new OciException("Region 不能为空");
        } else {
            String trimmed = regionId.trim();

            try {
                return Region.fromRegionCodeOrId(trimmed);
            } catch (IllegalArgumentException var7) {
                for (Region r : Region.values()) {
                    if (trimmed.equalsIgnoreCase(r.getRegionId())) {
                        return r;
                    }
                }

                throw new OciException("未知 Region: " + regionId + "（请检查拼写或升级 OCI SDK）");
            }
        }
    }

    private String findRootCompartment(IdentityClient identityClient, String tenantId) {
        try {
            List<Compartment> compartments = identityClient.listCompartments(
                    ListCompartmentsRequest.builder().compartmentId(tenantId).accessLevel(AccessLevel.Accessible).build()
                )
                .getItems();
            if (CollectionUtil.isNotEmpty(compartments)) {
                return compartments.get(0).getCompartmentId() != null ? compartments.get(0).getCompartmentId() : tenantId;
            }
        } catch (Exception var4) {
            log.warn("Failed to find root compartment, using tenantId: {}", var4.getMessage());
        }

        return tenantId;
    }

    public List<Compartment> listAllCompartments() {
        String tenantId = this.provider.getTenantId();
        List<Compartment> all = new ArrayList<>();

        try {
            Tenancy tenancy = this.identityClient.getTenancy(GetTenancyRequest.builder().tenancyId(tenantId).build()).getTenancy();
            Compartment root = Compartment.builder().id(tenantId).name("root").compartmentId(tenantId).lifecycleState(LifecycleState.Active).build();
            all.add(root);
        } catch (Exception var6) {
            Compartment rootx = Compartment.builder().id(tenantId).name("root").compartmentId(tenantId).lifecycleState(LifecycleState.Active).build();
            all.add(rootx);
        }

        try {
            all.addAll(
                this.identityClient
                    .listCompartments(
                        ListCompartmentsRequest.builder()
                            .compartmentId(tenantId)
                            .accessLevel(AccessLevel.Accessible)
                            .compartmentIdInSubtree(true)
                            .lifecycleState(LifecycleState.Active)
                            .build()
                    )
                    .getItems()
            );
        } catch (Exception var5) {
            log.warn("Failed to list compartments: {}", var5.getMessage());
        }

        return all;
    }

    public List<Instance> listAllInstancesInCompartment(String cid) {
        List<Instance> all = new ArrayList<>();

        for (com.oracle.bmc.core.model.Instance.LifecycleState state : List.of(
            com.oracle.bmc.core.model.Instance.LifecycleState.Running,
            com.oracle.bmc.core.model.Instance.LifecycleState.Stopped,
            com.oracle.bmc.core.model.Instance.LifecycleState.Starting,
            com.oracle.bmc.core.model.Instance.LifecycleState.Stopping
        )) {
            try {
                all.addAll(this.computeClient.listInstances(ListInstancesRequest.builder().compartmentId(cid).lifecycleState(state).build()).getItems());
            } catch (Exception var6) {
            }
        }

        return all;
    }

    public List<Vcn> listVcnInCompartment(String cid) {
        try {
            return this.virtualNetworkClient
                .listVcns(ListVcnsRequest.builder().compartmentId(cid).lifecycleState(com.oracle.bmc.core.model.Vcn.LifecycleState.Available).build())
                .getItems();
        } catch (Exception var3) {
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
        com.oracle.bmc.core.requests.ListShapesRequest.Builder b = ListShapesRequest.builder()
            .compartmentId(this.compartmentId)
            .availabilityDomain(availabilityDomain);
        if (imageId != null && !imageId.isBlank()) {
            b.imageId(imageId.trim());
        }

        return this.computeClient.listShapes(b.build()).getItems();
    }

    public List<Instance> listInstances() {
        return this.computeClient
            .listInstances(
                ListInstancesRequest.builder()
                    .compartmentId(this.compartmentId)
                    .lifecycleState(com.oracle.bmc.core.model.Instance.LifecycleState.Running)
                    .build()
            )
            .getItems();
    }

    public List<Instance> listAllInstances() {
        List<Instance> all = new ArrayList<>();

        for (com.oracle.bmc.core.model.Instance.LifecycleState state : List.of(
            com.oracle.bmc.core.model.Instance.LifecycleState.Running,
            com.oracle.bmc.core.model.Instance.LifecycleState.Stopped,
            com.oracle.bmc.core.model.Instance.LifecycleState.Starting,
            com.oracle.bmc.core.model.Instance.LifecycleState.Stopping
        )) {
            all.addAll(
                this.computeClient.listInstances(ListInstancesRequest.builder().compartmentId(this.compartmentId).lifecycleState(state).build()).getItems()
            );
        }

        return all;
    }

    public List<Vcn> listVcn() {
        return this.virtualNetworkClient
            .listVcns(
                ListVcnsRequest.builder().compartmentId(this.compartmentId).lifecycleState(com.oracle.bmc.core.model.Vcn.LifecycleState.Available).build()
            )
            .getItems();
    }

    public List<Subnet> listSubnets(String vcnId) {
        return this.virtualNetworkClient
            .listSubnets(
                ListSubnetsRequest.builder()
                    .compartmentId(this.compartmentId)
                    .vcnId(vcnId)
                    .lifecycleState(com.oracle.bmc.core.model.Subnet.LifecycleState.Available)
                    .build()
            )
            .getItems();
    }

    public Vcn createVcn(String cidrBlock) {
        CreateVcnResponse response = this.virtualNetworkClient
            .createVcn(
                CreateVcnRequest.builder()
                    .createVcnDetails(
                        CreateVcnDetails.builder().compartmentId(this.compartmentId).displayName("ocx-worker-vcn").cidrBlocks(List.of(cidrBlock)).build()
                    )
                    .build()
            );
        log.info("Created VCN: {}", response.getVcn().getDisplayName());
        return response.getVcn();
    }

    public InternetGateway createInternetGateway(Vcn vcn) {
        CreateInternetGatewayResponse response = this.virtualNetworkClient
            .createInternetGateway(
                CreateInternetGatewayRequest.builder()
                    .createInternetGatewayDetails(
                        CreateInternetGatewayDetails.builder()
                            .compartmentId(this.compartmentId)
                            .vcnId(vcn.getId())
                            .displayName("ocx-worker-igw")
                            .isEnabled(true)
                            .build()
                    )
                    .build()
            );
        return response.getInternetGateway();
    }

    public void addInternetGatewayToDefaultRouteTable(Vcn vcn, InternetGateway igw) {
        GetRouteTableResponse rtResponse = this.virtualNetworkClient.getRouteTable(GetRouteTableRequest.builder().rtId(vcn.getDefaultRouteTableId()).build());
        List<RouteRule> rules = new ArrayList<>(rtResponse.getRouteTable().getRouteRules());
        rules.add(RouteRule.builder().destination("0.0.0.0/0").destinationType(DestinationType.CidrBlock).networkEntityId(igw.getId()).build());
        this.virtualNetworkClient
            .updateRouteTable(
                UpdateRouteTableRequest.builder()
                    .rtId(vcn.getDefaultRouteTableId())
                    .updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build())
                    .build()
            );
    }

    public Subnet createSubnet(String availabilityDomain, String cidrBlock, Vcn vcn) {
        try {
            CreateSubnetResponse response = this.virtualNetworkClient
                .createSubnet(
                    CreateSubnetRequest.builder()
                        .createSubnetDetails(
                            CreateSubnetDetails.builder()
                                .compartmentId(this.compartmentId)
                                .vcnId(vcn.getId())
                                .displayName("ocx-worker-subnet")
                                .cidrBlock(cidrBlock)
                                .availabilityDomain(availabilityDomain)
                                .build()
                        )
                        .build()
                );
            return response.getSubnet();
        } catch (Exception var5) {
            log.error("Failed to create subnet: {}", var5.getMessage());
            return null;
        }
    }

    public Image getImage(Shape shape) {
        String os = this.user.getOperationSystem() != null ? this.user.getOperationSystem() : "Ubuntu";
        String apiVersion = null;
        String apiOs;
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

        com.oracle.bmc.core.requests.ListImagesRequest.Builder reqBuilder = ListImagesRequest.builder()
            .compartmentId(this.compartmentId)
            .shape(shape.getShape())
            .operatingSystem(apiOs)
            .sortBy(SortBy.Timecreated)
            .sortOrder(SortOrder.Desc);
        if (apiVersion != null) {
            reqBuilder.operatingSystemVersion(apiVersion);
        }

        List<Image> images = this.computeClient.listImages(reqBuilder.build()).getItems();
        if (CollectionUtil.isEmpty(images)) {
            images = this.computeClient
                .listImages(
                    ListImagesRequest.builder()
                        .compartmentId(this.compartmentId)
                        .shape(shape.getShape())
                        .sortBy(SortBy.Timecreated)
                        .sortOrder(SortOrder.Desc)
                        .build()
                )
                .getItems();
        }

        return CollectionUtil.isEmpty(images) ? null : images.get(0);
    }

    public synchronized InstanceDetailDTO createInstanceData() {
        InstanceDetailDTO result = new InstanceDetailDTO();
        result.setTaskId(this.user.getTaskId());
        result.setUsername(this.user.getUsername());
        result.setRegion(this.user.getOciCfg().getRegion());
        result.setArchitecture(this.user.getArchitecture());
        result.setCreateNumbers(this.user.getCreateNumbers());
        List<AvailabilityDomain> availabilityDomains = this.getAvailabilityDomains();
        String targetShape = resolveTargetShape(this.user.getArchitecture());
        result.setResolvedTargetShape(targetShape);
        Set<String> excludedAds = this.user.getExcludedAvailabilityDomains() != null ? this.user.getExcludedAvailabilityDomains() : Set.of();
        boolean sawOutOfCapacity = false;
        Iterator anyAdLeft = availabilityDomains.iterator();

        label154:
        while (true) {
            AvailabilityDomain ad;
            String tryNextAdSuffix;
            List<Shape> shapes;
            while (true) {
                if (!anyAdLeft.hasNext()) {
                    if (sawOutOfCapacity) {
                        result.setOutOfCapacity(true);
                    }

                    if (!availabilityDomains.isEmpty()) {
                        boolean anyAdLeftx = availabilityDomains.stream()
                            .anyMatch(
                                adx -> !excludedAds.contains(adx.getName())
                                        && (result.getAdsExcludedNoShape() == null || !result.getAdsExcludedNoShape().contains(adx.getName()))
                            );
                        if (!anyAdLeftx && !result.isSuccess()) {
                            result.setAllAdsExcludedNoShape(true);
                        }
                    }

                    return result;
                }

                ad = (AvailabilityDomain)anyAdLeft.next();
                if (!excludedAds.contains(ad.getName())) {
                    tryNextAdSuffix = hasNextAvailabilityDomain(availabilityDomains, ad, excludedAds) ? "，尝试下一可用域" : "";

                    try {
                        shapes = this.getShapes(ad.getName()).stream().filter(s -> s.getShape().equals(targetShape)).collect(Collectors.toList());
                        break;
                    } catch (Exception var24) {
                        markAdExcludedNoShape(result, ad.getName());
                        log.warn("【开机任务】用户:[{}], AD:[{}] - 当前可用域无此 Shape [{}]（ListShapes 失败）", new Object[]{this.user.getUsername(), ad.getName(), targetShape});
                    }
                }
            }

            Shape shape;
            Image image;
            Subnet subnet;
            if (!shapes.isEmpty()) {
                Iterator e = shapes.iterator();

                do {
                    if (!e.hasNext()) {
                        continue label154;
                    }

                    shape = (Shape)e.next();
                    image = this.getImage(shape);
                } while (image == null);

                try {
                    subnet = this.findOrCreateSubnet(ad.getName());
                } catch (BmcException var25) {
                    String hint = describeBmcFailure(var25);
                    result.setFailureHint(hint);
                    if (isVcnCountLimitError(var25)) {
                        log.warn("【开机任务】用户:[{}], AD:[{}] - {}", new Object[]{this.user.getUsername(), ad.getName(), hint});
                    } else {
                        log.warn("【开机任务】用户:[{}], AD:[{}] - 准备网络失败{}。{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix, hint});
                    }
                    continue;
                } catch (Exception var26) {
                    String hintx = describeThrowableFailure(var26);
                    result.setFailureHint(hintx);
                    log.warn("【开机任务】用户:[{}], AD:[{}] - 准备网络失败{}。{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix, hintx});
                    continue;
                }
            } else {
                markAdExcludedNoShape(result, ad.getName());
                log.info("【开机任务】用户:[{}], AD:[{}] - 当前可用域无此 Shape [{}]", new Object[]{this.user.getUsername(), ad.getName(), targetShape});
                continue;
            }

            if (subnet != null) {
                log.info(
                    "【开机任务】用户:[{}],区域:[{}], AD:[{}], 子网:[{}] 创建实例...",
                    new Object[]{this.user.getUsername(), this.user.getOciCfg().getRegion(), ad.getName(), subnet.getDisplayName()}
                );

                try {
                    String cloudInitScript = CommonUtils.getPwdShell(this.user.getRootPassword(), this.user.getCustomScript());
                    LaunchInstanceDetails launchDetails = this.buildLaunchDetails(ad, shape, image, subnet, cloudInitScript);
                    Instance instance = this.launchInstance(launchDetails);
                    String publicIp = this.getInstancePublicIp(instance);

                    try {
                        this.ensureIpv4AllIngressSecurityRules(subnet.getId());
                    } catch (Exception var21) {
                        log.warn("【开机任务】用户:[{}] - IPv4 安全列表入站规则失败: {}", this.user.getUsername(), var21.getMessage());
                    }

                    if (Boolean.TRUE.equals(this.user.getAssignIpv6())) {
                        String ipv6Address = null;

                        try {
                            ipv6Address = this.assignIpv6ToInstance(instance, subnet);
                            if (StrUtil.isNotBlank(ipv6Address)) {
                                result.setIpv6Address(ipv6Address);
                                log.info("【开机任务】用户:[{}] - IPv6 已分配: {}", this.user.getUsername(), ipv6Address);
                            } else {
                                log.warn("【开机任务】用户:[{}] - IPv6 分配未完成（VCN/子网/地址未就绪）", this.user.getUsername());
                            }
                        } catch (Exception var23) {
                            log.warn("【开机任务】用户:[{}] - IPv6 分配失败: {}", this.user.getUsername(), var23.getMessage());
                        }

                        boolean ipv6Ready = StrUtil.isNotBlank(ipv6Address) || VcnIpv6Util.isEnabled(this.virtualNetworkClient, subnet.getVcnId());
                        if (ipv6Ready) {
                            try {
                                this.ensureIpv6AllSecurityRules(subnet.getId());
                            } catch (Exception var22) {
                                log.warn("【开机任务】用户:[{}] - IPv6 安全列表规则失败: {}", this.user.getUsername(), var22.getMessage());
                            }
                        } else {
                            log.warn("【开机任务】用户:[{}] - VCN 未启用 IPv6，跳过 ::/0 安全列表", this.user.getUsername());
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
                } catch (BmcException var27) {
                    if (var27.getStatusCode() == 401) {
                        result.setDie(true);
                        return result;
                    }

                    if (isBootVolumeQuotaError(var27)) {
                        String hint = describeBmcFailure(var27);
                        result.setBootVolumeQuotaExceeded(true);
                        result.setFailureHint(hint);
                        log.warn("【开机任务】用户:[{}], AD:[{}] - {}", new Object[]{this.user.getUsername(), ad.getName(), hint});
                        return result;
                    }

                    String hint = describeBmcFailure(var27);
                    if (isOutOfHostCapacityError(var27)) {
                        sawOutOfCapacity = true;
                        log.warn("【开机任务】用户:[{}], AD:[{}] - 容量不足{}。{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix, hint});
                    } else {
                        log.warn("【开机任务】用户:[{}], AD:[{}] - 创建失败{}。{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix, hint});
                    }
                } catch (Exception var28) {
                    String hint = describeThrowableFailure(var28);
                    result.setFailureHint(hint);
                    log.warn("【开机任务】用户:[{}], AD:[{}] - 创建异常{}。{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix, hint});
                }
            } else {
                result.setNoPubVcn(true);
                log.warn("【开机任务】用户:[{}], AD:[{}] - 无可用公有子网{}", new Object[]{this.user.getUsername(), ad.getName(), tryNextAdSuffix});
            }
        }
    }

    private static String resolveTargetShape(String arch) {
        if (arch == null || !"ARM".equalsIgnoreCase(arch) && !"AMD".equalsIgnoreCase(arch)) {
            return ShapeSeriesUtil.isFullShapeName(arch) ? arch.trim() : ArchitectureEnum.getShape(arch == null ? "ARM" : arch);
        } else {
            return ArchitectureEnum.getShape(arch);
        }
    }

    private static void markAdExcludedNoShape(InstanceDetailDTO result, String adName) {
        if (result.getAdsExcludedNoShape() == null) {
            result.setAdsExcludedNoShape(new ArrayList<>());
        }

        if (!result.getAdsExcludedNoShape().contains(adName)) {
            result.getAdsExcludedNoShape().add(adName);
        }
    }

    private static boolean hasNextAvailabilityDomain(List<AvailabilityDomain> ads, AvailabilityDomain current, Set<String> excludedAds) {
        boolean seenCurrent = false;

        for (AvailabilityDomain ad : ads) {
            if (excludedAds == null || !excludedAds.contains(ad.getName())) {
                if (Objects.equals(ad.getName(), current.getName())) {
                    seenCurrent = true;
                } else if (seenCurrent) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isOutOfHostCapacityError(BmcException e) {
        if (isBootVolumeQuotaError(e)) {
            return false;
        } else {
            String em = e.getMessage() == null ? "" : e.getMessage();
            return e.getStatusCode() == 500
                || em.contains("Out of host capacity")
                || e.getStatusCode() == 400 && em.contains("LimitExceeded")
                || e.getStatusCode() == 429;
        }
    }

    private static boolean isBootVolumeQuotaError(BmcException e) {
        String em = e.getMessage() == null ? "" : e.getMessage();
        return em.contains("bootVolumeQuota") ? true : em.contains("QuotaExceeded") && (em.toLowerCase().contains("bootvolume") || em.contains("boot volume"));
    }

    static String describeThrowableFailure(Throwable e) {
        if (e instanceof BmcException bmc) {
            return describeBmcFailure(bmc);
        } else if (e.getCause() instanceof BmcException bmc) {
            return describeBmcFailure(bmc);
        } else {
            String msg = e.getMessage();
            if (msg != null && msg.contains("ListShapes")) {
                return "当前可用域无此 Shape";
            } else if (msg != null && !msg.isBlank()) {
                int cut = Math.min(msg.length(), 200);
                return msg.substring(0, cut);
            } else {
                return "创建失败";
            }
        }
    }

    static String describeBmcFailure(BmcException e) {
        String em = e.getMessage() == null ? "" : e.getMessage();
        if (isBootVolumeQuotaError(e)) {
            return "引导卷（启动盘）存储配额已达上限，硬盘配额用尽，创建失败";
        } else if (isVcnCountLimitError(e)) {
            return "VCN 数量已达配额上限，无法创建虚拟云网络，请删除无用 VCN 或申请提额";
        } else if (em.contains("QuotaExceeded")) {
            return "OCI 服务配额已达上限，创建失败";
        } else if (em.contains("Out of host capacity")) {
            return "主机容量不足";
        } else if (em.contains("LimitExceeded")) {
            return "已触发 OCI 服务限制，创建失败";
        } else if (e.getStatusCode() == 429) {
            return "请求过于频繁，请稍后重试";
        } else {
            int code = e.getStatusCode();
            int cut = Math.min(em.length(), 200);
            String brief = em.substring(0, cut);
            return code > 0 ? "OCI 错误 (" + code + "): " + brief : brief;
        }
    }

    private static boolean isVcnCountLimitError(BmcException e) {
        String em = e.getMessage() == null ? "" : e.getMessage();
        return em.contains("vcn-count")
            ? true
            : em.contains("LimitExceeded") && (em.contains("CreateVcn") || em.contains("service limits were exceeded") && em.toLowerCase().contains("vcn"));
    }

    private void fillResultHardwareFromLaunch(InstanceDetailDTO result, Instance instance, Shape shape) {
        String shapeName = shape != null ? shape.getShape() : "";
        boolean flex = shapeName.contains("Flex");
        InstanceShapeConfig sc = instance.getShapeConfig();
        if (sc != null) {
            if (sc.getOcpus() != null) {
                result.setOcpus(sc.getOcpus().doubleValue());
            }

            if (sc.getMemoryInGBs() != null) {
                result.setMemory(sc.getMemoryInGBs().doubleValue());
            }
        }

        if (result.getOcpus() == null) {
            result.setOcpus(flex ? (this.user.getOcpus() != null ? this.user.getOcpus() : 1.0) : 1.0);
        }

        if (result.getMemory() == null) {
            if (flex) {
                result.setMemory(this.user.getMemory() != null ? this.user.getMemory() : 6.0);
            } else {
                result.setMemory(fixedShapeDefaultMemoryGb(shapeName));
            }
        }
    }

    private static double fixedShapeDefaultMemoryGb(String shapeName) {
        if (StrUtil.isBlank(shapeName)) {
            return 1.0;
        } else {
            return shapeName.contains("Micro") ? 1.0 : 1.0;
        }
    }

    private Subnet findOrCreateSubnet(String availabilityDomain) {
        List<Vcn> vcnList = this.listVcn();
        if (CollectionUtil.isEmpty(vcnList)) {
            log.info("【开机任务】用户:[{}],区域:[{}] - 未找到 VCN，正在创建...", this.user.getUsername(), this.user.getOciCfg().getRegion());
            Vcn vcn = this.createVcn("10.0.0.0/16");
            InternetGateway igw = this.createInternetGateway(vcn);
            this.addInternetGatewayToDefaultRouteTable(vcn, igw);
            return this.createSubnet(availabilityDomain, "10.0.0.0/24", vcn);
        } else {
            for (Vcn vcn : vcnList) {
                List<InternetGateway> igws = this.virtualNetworkClient
                    .listInternetGateways(ListInternetGatewaysRequest.builder().vcnId(vcn.getId()).compartmentId(this.compartmentId).build())
                    .getItems();
                if (CollectionUtil.isEmpty(igws)) {
                    InternetGateway igw = this.createInternetGateway(vcn);
                    this.addInternetGatewayToDefaultRouteTable(vcn, igw);
                }

                List<Subnet> subnets = this.listSubnets(vcn.getId());
                if (CollectionUtil.isEmpty(subnets)) {
                    return this.createSubnet(availabilityDomain, "10.0.0.0/24", vcn);
                }

                for (Subnet subnet : subnets) {
                    if (!subnet.getProhibitInternetIngress()) {
                        return subnet;
                    }
                }
            }

            return null;
        }
    }

    private String resolveLaunchDisplayName() {
        int target = this.user.getCreateNumbers() != null && this.user.getCreateNumbers() > 0 ? this.user.getCreateNumbers() : 1;
        int ord = this.user.getInstanceDisplayOrdinal() != null && this.user.getInstanceDisplayOrdinal() > 0 ? this.user.getInstanceDisplayOrdinal() : 1;
        if (target == 1) {
            return "ocx-worker-instance";
        } else if (target <= 4) {
            int o = Math.min(Math.max(ord, 1), target);
            char letter = (char)(65 + o - 1);
            return "ocx-worker-" + letter;
        } else {
            return "oci-instance-" + ord;
        }
    }

    private static InstanceSourceViaImageDetails buildBootVolumeSource(String imageId, SysUserDTO user) {
        long sizeGb = user.getDisk() != null ? (long)user.getDisk().intValue() : 50L;
        com.oracle.bmc.core.model.InstanceSourceViaImageDetails.Builder b = InstanceSourceViaImageDetails.builder()
            .imageId(imageId)
            .bootVolumeSizeInGBs(sizeGb);
        int vpus = BootVolumeVpusUtil.normalize(user.getVpusPerGB());
        if (vpus > 0) {
            b.bootVolumeVpusPerGB((long)vpus);
        }

        return b.build();
    }

    private LaunchInstanceDetails buildLaunchDetails(AvailabilityDomain ad, Shape shape, Image image, Subnet subnet, String cloudInitScript) {
        com.oracle.bmc.core.model.LaunchInstanceDetails.Builder builder = LaunchInstanceDetails.builder()
            .compartmentId(this.compartmentId)
            .availabilityDomain(ad.getName())
            .displayName(this.resolveLaunchDisplayName())
            .shape(shape.getShape())
            .sourceDetails(buildBootVolumeSource(image.getId(), this.user))
            .createVnicDetails(
                CreateVnicDetails.builder()
                    .subnetId(subnet.getId())
                    .assignPublicIp(this.user.getAssignPublicIp() != null ? this.user.getAssignPublicIp() : true)
                    .build()
            )
            .metadata(
                cloudInitScript != null && !cloudInitScript.isEmpty()
                    ? Map.of("user_data", Base64.getEncoder().encodeToString(cloudInitScript.getBytes(StandardCharsets.UTF_8)))
                    : null
            );
        if (shape.getShape().contains("Flex")) {
            builder.shapeConfig(
                LaunchInstanceShapeConfigDetails.builder()
                    .ocpus(this.user.getOcpus() != null ? this.user.getOcpus().floatValue() : 1.0F)
                    .memoryInGBs(this.user.getMemory() != null ? this.user.getMemory().floatValue() : 6.0F)
                    .build()
            );
        }

        return builder.build();
    }

    private Instance launchInstance(LaunchInstanceDetails details) throws Exception {
        ComputeWaiters waiters = this.computeClient.newWaiters(this.workRequestClient);
        LaunchInstanceResponse launchResponse = (LaunchInstanceResponse)waiters.forLaunchInstance(
                LaunchInstanceRequest.builder().launchInstanceDetails(details).build()
            )
            .execute();
        return ((GetInstanceResponse)waiters.forInstance(
                    GetInstanceRequest.builder().instanceId(launchResponse.getInstance().getId()).build(),
                    new com.oracle.bmc.core.model.Instance.LifecycleState[]{com.oracle.bmc.core.model.Instance.LifecycleState.Running}
                )
                .execute())
            .getInstance();
    }

    private String assignIpv6ToInstance(Instance instance, Subnet subnet) {
        List<VnicAttachment> attachments = this.computeClient
            .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(this.compartmentId).instanceId(instance.getId()).build())
            .getItems();
        if (attachments.isEmpty()) {
            return null;
        } else {
            String vnicId = attachments.get(0).getVnicId();
            String subnetId = subnet.getId();
            Vcn vcn = this.virtualNetworkClient.getVcn(GetVcnRequest.builder().vcnId(subnet.getVcnId()).build()).getVcn();
            if (vcn.getIpv6CidrBlocks() == null || vcn.getIpv6CidrBlocks().isEmpty()) {
                try {
                    this.virtualNetworkClient
                        .addIpv6VcnCidr(
                            AddIpv6VcnCidrRequest.builder()
                                .vcnId(vcn.getId())
                                .addVcnIpv6CidrDetails(AddVcnIpv6CidrDetails.builder().isOracleGuaAllocationEnabled(true).build())
                                .build()
                        );
                    Thread.sleep(8000L);
                } catch (Exception var13) {
                    String em = var13.getMessage() == null ? "" : var13.getMessage();
                    if (!em.contains("already exists") && !em.contains("already has")) {
                        log.warn("VCN IPv6 CIDR 添加失败: {}", em);
                        return null;
                    }
                }

                vcn = this.virtualNetworkClient.getVcn(GetVcnRequest.builder().vcnId(vcn.getId()).build()).getVcn();
            }

            Subnet freshSubnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
            if (freshSubnet.getIpv6CidrBlocks() == null || freshSubnet.getIpv6CidrBlocks().isEmpty()) {
                String vcnIpv6Cidr = vcn.getIpv6CidrBlocks() != null && !vcn.getIpv6CidrBlocks().isEmpty() ? (String)vcn.getIpv6CidrBlocks().get(0) : null;
                if (vcnIpv6Cidr == null) {
                    return null;
                }

                String subnetIpv6Cidr = vcnIpv6Cidr.replaceAll("/\\d+$", "/64");

                try {
                    this.virtualNetworkClient
                        .updateSubnet(
                            UpdateSubnetRequest.builder()
                                .subnetId(subnetId)
                                .updateSubnetDetails(UpdateSubnetDetails.builder().ipv6CidrBlocks(List.of(subnetIpv6Cidr)).build())
                                .build()
                        );
                    Thread.sleep(3000L);
                } catch (Exception var12) {
                    String em = var12.getMessage() == null ? "" : var12.getMessage();
                    if (!em.contains("already exists") && !em.contains("already has")) {
                        log.warn("子网 IPv6 CIDR 添加失败: {}", em);
                        return null;
                    }
                }
            }

            this.ensureIpv6InternetRoute(vcn, freshSubnet);
            Ipv6 ipv6 = this.virtualNetworkClient
                .createIpv6(CreateIpv6Request.builder().createIpv6Details(CreateIpv6Details.builder().vnicId(vnicId).build()).build())
                .getIpv6();
            return ipv6 != null ? ipv6.getIpAddress() : null;
        }
    }

    private void ensureIpv4AllIngressSecurityRules(String subnetId) {
        Subnet subnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
        if (subnet.getSecurityListIds() != null && !subnet.getSecurityListIds().isEmpty()) {
            String secListId = (String)subnet.getSecurityListIds().get(0);
            SecurityList secList = this.virtualNetworkClient
                .getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build())
                .getSecurityList();
            List<IngressSecurityRule> ingressRules = new ArrayList<>(secList.getIngressSecurityRules());
            if (!hasIpv4AllIngress(ingressRules)) {
                ingressRules.add(IngressSecurityRule.builder().source("0.0.0.0/0").protocol("all").description("ocx-worker auto IPv4 ingress").build());
                this.virtualNetworkClient
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
                log.info("子网 {} 安全列表已补 IPv4 0.0.0.0/0 全协议入站", subnetId);
            }
        } else {
            log.warn("子网 {} 无安全列表，跳过 IPv4 0.0.0.0/0 入站规则", subnetId);
        }
    }

    private void ensureIpv6AllSecurityRules(String subnetId) {
        if (!VcnIpv6Util.isEnabledForSubnet(this.virtualNetworkClient, subnetId)) {
            log.warn("子网 {} 所属 VCN 未启用 IPv6，跳过 ::/0 安全列表规则", subnetId);
        } else {
            Subnet subnet = this.virtualNetworkClient.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
            if (subnet.getSecurityListIds() != null && !subnet.getSecurityListIds().isEmpty()) {
                String secListId = (String)subnet.getSecurityListIds().get(0);
                SecurityList secList = this.virtualNetworkClient
                    .getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build())
                    .getSecurityList();
                int ingressBefore = secList.getIngressSecurityRules() != null ? secList.getIngressSecurityRules().size() : 0;
                int egressBefore = secList.getEgressSecurityRules() != null ? secList.getEgressSecurityRules().size() : 0;
                List<IngressSecurityRule> ingressRules = dedupeIpv6AllIngress(
                    new ArrayList<>(secList.getIngressSecurityRules() != null ? secList.getIngressSecurityRules() : List.of())
                );
                List<EgressSecurityRule> egressRules = dedupeIpv6AllEgress(
                    new ArrayList<>(secList.getEgressSecurityRules() != null ? secList.getEgressSecurityRules() : List.of())
                );
                boolean changed = ingressRules.size() != ingressBefore || egressRules.size() != egressBefore;
                if (!hasIpv6AllIngress(ingressRules)) {
                    ingressRules.add(
                        IngressSecurityRule.builder()
                            .source("::/0")
                            .sourceType(SourceType.CidrBlock)
                            .protocol("all")
                            .description("ocx-worker auto IPv6 ingress")
                            .build()
                    );
                    changed = true;
                }

                if (!hasIpv6AllEgress(egressRules)) {
                    egressRules.add(
                        EgressSecurityRule.builder()
                            .destination("::/0")
                            .destinationType(com.oracle.bmc.core.model.EgressSecurityRule.DestinationType.CidrBlock)
                            .protocol("all")
                            .description("ocx-worker auto IPv6 egress")
                            .build()
                    );
                    changed = true;
                }

                if (changed) {
                    this.virtualNetworkClient
                        .updateSecurityList(
                            UpdateSecurityListRequest.builder()
                                .securityListId(secListId)
                                .updateSecurityListDetails(
                                    UpdateSecurityListDetails.builder().ingressSecurityRules(ingressRules).egressSecurityRules(egressRules).build()
                                )
                                .build()
                        );
                    log.info("子网 {} 安全列表已补/整理 IPv6 ::/0 全协议入站/出站", subnetId);
                }
            } else {
                log.warn("子网 {} 无安全列表，跳过 IPv6 ::/0 规则", subnetId);
            }
        }
    }

    private static boolean isProtocolAll(String protocol) {
        return protocol != null && "all".equalsIgnoreCase(protocol.trim());
    }

    private static boolean isIpv6WildcardCidr(String cidr) {
        return cidr != null && "::/0".equals(cidr.trim());
    }

    private static boolean hasIpv4AllIngress(List<IngressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> "0.0.0.0/0".equals(r.getSource()) && isProtocolAll(r.getProtocol()));
    }

    private static boolean hasIpv6AllIngress(List<IngressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> isIpv6WildcardCidr(r.getSource()) && isProtocolAll(r.getProtocol()));
    }

    private static boolean hasIpv6AllEgress(List<EgressSecurityRule> rules) {
        return rules.stream().anyMatch(r -> isIpv6WildcardCidr(r.getDestination()) && isProtocolAll(r.getProtocol()));
    }

    private static List<IngressSecurityRule> dedupeIpv6AllIngress(List<IngressSecurityRule> rules) {
        List<IngressSecurityRule> out = new ArrayList<>();
        boolean seenIpv6All = false;

        for (IngressSecurityRule r : rules) {
            if (!isIpv6WildcardCidr(r.getSource()) || !isProtocolAll(r.getProtocol())) {
                out.add(r);
            } else if (!seenIpv6All) {
                out.add(r);
                seenIpv6All = true;
            }
        }

        return out;
    }

    private static List<EgressSecurityRule> dedupeIpv6AllEgress(List<EgressSecurityRule> rules) {
        List<EgressSecurityRule> out = new ArrayList<>();
        boolean seenIpv6All = false;

        for (EgressSecurityRule r : rules) {
            if (!isIpv6WildcardCidr(r.getDestination()) || !isProtocolAll(r.getProtocol())) {
                out.add(r);
            } else if (!seenIpv6All) {
                out.add(r);
                seenIpv6All = true;
            }
        }

        return out;
    }

    private void ensureIpv6InternetRoute(Vcn vcn, Subnet subnet) {
        List<InternetGateway> igws = this.virtualNetworkClient
            .listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(this.compartmentId).vcnId(vcn.getId()).build())
            .getItems();
        InternetGateway igw;
        if (CollectionUtil.isEmpty(igws)) {
            igw = this.createInternetGateway(vcn);
        } else {
            igw = igws.stream().filter(gw -> Boolean.TRUE.equals(gw.getIsEnabled())).findFirst().orElse(igws.get(0));
        }

        String routeTableId = subnet.getRouteTableId() != null ? subnet.getRouteTableId() : vcn.getDefaultRouteTableId();
        if (!StrUtil.isBlank(routeTableId)) {
            RouteTable routeTable = this.virtualNetworkClient.getRouteTable(GetRouteTableRequest.builder().rtId(routeTableId).build()).getRouteTable();
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
                this.virtualNetworkClient
                    .updateRouteTable(
                        UpdateRouteTableRequest.builder()
                            .rtId(routeTableId)
                            .updateRouteTableDetails(UpdateRouteTableDetails.builder().routeRules(rules).build())
                            .build()
                    );
            }
        }
    }

    public String getInstancePublicIp(Instance instance) {
        try {
            for (VnicAttachment attachment : this.computeClient
                .listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(this.compartmentId).instanceId(instance.getId()).build())
                .getItems()) {
                Vnic vnic = this.virtualNetworkClient.getVnic(GetVnicRequest.builder().vnicId(attachment.getVnicId()).build()).getVnic();
                if (vnic.getPublicIp() != null) {
                    return vnic.getPublicIp();
                }
            }
        } catch (Exception var6) {
            log.warn("Failed to get public IP: {}", var6.getMessage());
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
    public void setUser(final SysUserDTO user) {
        this.user = user;
    }

    @Generated
    public void setCompartmentId(final String compartmentId) {
        this.compartmentId = compartmentId;
    }

    @Generated
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OciClientService other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$computeClient = this.getComputeClient();
            Object other$computeClient = other.getComputeClient();
            if (this$computeClient == null ? other$computeClient == null : this$computeClient.equals(other$computeClient)) {
                Object this$identityClient = this.getIdentityClient();
                Object other$identityClient = other.getIdentityClient();
                if (this$identityClient == null ? other$identityClient == null : this$identityClient.equals(other$identityClient)) {
                    Object this$workRequestClient = this.getWorkRequestClient();
                    Object other$workRequestClient = other.getWorkRequestClient();
                    if (this$workRequestClient == null ? other$workRequestClient == null : this$workRequestClient.equals(other$workRequestClient)) {
                        Object this$virtualNetworkClient = this.getVirtualNetworkClient();
                        Object other$virtualNetworkClient = other.getVirtualNetworkClient();
                        if (this$virtualNetworkClient == null
                            ? other$virtualNetworkClient == null
                            : this$virtualNetworkClient.equals(other$virtualNetworkClient)) {
                            Object this$blockstorageClient = this.getBlockstorageClient();
                            Object other$blockstorageClient = other.getBlockstorageClient();
                            if (this$blockstorageClient == null ? other$blockstorageClient == null : this$blockstorageClient.equals(other$blockstorageClient)) {
                                Object this$objectStorageClient = this.getObjectStorageClient();
                                Object other$objectStorageClient = other.getObjectStorageClient();
                                if (this$objectStorageClient == null
                                    ? other$objectStorageClient == null
                                    : this$objectStorageClient.equals(other$objectStorageClient)) {
                                    Object this$monitoringClient = this.getMonitoringClient();
                                    Object other$monitoringClient = other.getMonitoringClient();
                                    if (this$monitoringClient == null ? other$monitoringClient == null : this$monitoringClient.equals(other$monitoringClient)) {
                                        Object this$networkLoadBalancerClient = this.getNetworkLoadBalancerClient();
                                        Object other$networkLoadBalancerClient = other.getNetworkLoadBalancerClient();
                                        if (this$networkLoadBalancerClient == null
                                            ? other$networkLoadBalancerClient == null
                                            : this$networkLoadBalancerClient.equals(other$networkLoadBalancerClient)) {
                                            Object this$provider = this.getProvider();
                                            Object other$provider = other.getProvider();
                                            if (this$provider == null ? other$provider == null : this$provider.equals(other$provider)) {
                                                Object this$user = this.getUser();
                                                Object other$user = other.getUser();
                                                if (this$user == null ? other$user == null : this$user.equals(other$user)) {
                                                    Object this$compartmentId = this.getCompartmentId();
                                                    Object other$compartmentId = other.getCompartmentId();
                                                    if (this$compartmentId == null
                                                        ? other$compartmentId == null
                                                        : this$compartmentId.equals(other$compartmentId)) {
                                                        Object this$ociSocksPoolingManager = this.getOciSocksPoolingManager();
                                                        Object other$ociSocksPoolingManager = other.getOciSocksPoolingManager();
                                                        return this$ociSocksPoolingManager == null
                                                            ? other$ociSocksPoolingManager == null
                                                            : this$ociSocksPoolingManager.equals(other$ociSocksPoolingManager);
                                                    } else {
                                                        return false;
                                                    }
                                                } else {
                                                    return false;
                                                }
                                            } else {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof OciClientService;
    }

    @Generated
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $computeClient = this.getComputeClient();
        result = result * 59 + ($computeClient == null ? 43 : $computeClient.hashCode());
        Object $identityClient = this.getIdentityClient();
        result = result * 59 + ($identityClient == null ? 43 : $identityClient.hashCode());
        Object $workRequestClient = this.getWorkRequestClient();
        result = result * 59 + ($workRequestClient == null ? 43 : $workRequestClient.hashCode());
        Object $virtualNetworkClient = this.getVirtualNetworkClient();
        result = result * 59 + ($virtualNetworkClient == null ? 43 : $virtualNetworkClient.hashCode());
        Object $blockstorageClient = this.getBlockstorageClient();
        result = result * 59 + ($blockstorageClient == null ? 43 : $blockstorageClient.hashCode());
        Object $objectStorageClient = this.getObjectStorageClient();
        result = result * 59 + ($objectStorageClient == null ? 43 : $objectStorageClient.hashCode());
        Object $monitoringClient = this.getMonitoringClient();
        result = result * 59 + ($monitoringClient == null ? 43 : $monitoringClient.hashCode());
        Object $networkLoadBalancerClient = this.getNetworkLoadBalancerClient();
        result = result * 59 + ($networkLoadBalancerClient == null ? 43 : $networkLoadBalancerClient.hashCode());
        Object $provider = this.getProvider();
        result = result * 59 + ($provider == null ? 43 : $provider.hashCode());
        Object $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        Object $compartmentId = this.getCompartmentId();
        result = result * 59 + ($compartmentId == null ? 43 : $compartmentId.hashCode());
        Object $ociSocksPoolingManager = this.getOciSocksPoolingManager();
        return result * 59 + ($ociSocksPoolingManager == null ? 43 : $ociSocksPoolingManager.hashCode());
    }

    @Generated
    @Override
    public String toString() {
        return "OciClientService(computeClient="
            + this.getComputeClient()
            + ", identityClient="
            + this.getIdentityClient()
            + ", workRequestClient="
            + this.getWorkRequestClient()
            + ", virtualNetworkClient="
            + this.getVirtualNetworkClient()
            + ", blockstorageClient="
            + this.getBlockstorageClient()
            + ", objectStorageClient="
            + this.getObjectStorageClient()
            + ", monitoringClient="
            + this.getMonitoringClient()
            + ", networkLoadBalancerClient="
            + this.getNetworkLoadBalancerClient()
            + ", provider="
            + this.getProvider()
            + ", user="
            + this.getUser()
            + ", compartmentId="
            + this.getCompartmentId()
            + ", ociSocksPoolingManager="
            + this.getOciSocksPoolingManager()
            + ")";
    }
}
