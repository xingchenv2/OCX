package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.core.model.AddPublicIpPoolCapacityDetails;
import com.oracle.bmc.core.model.AddVcnIpv6CidrDetails;
import com.oracle.bmc.core.model.ByoipAllocatedRangeCollection;
import com.oracle.bmc.core.model.ByoipRange;
import com.oracle.bmc.core.model.ByoipRangeCollection;
import com.oracle.bmc.core.model.ByoipRangeSummary;
import com.oracle.bmc.core.model.Byoipv6CidrDetails;
import com.oracle.bmc.core.model.ChangeByoipRangeCompartmentDetails;
import com.oracle.bmc.core.model.CreateByoipRangeDetails;
import com.oracle.bmc.core.model.CreatePublicIpDetails;
import com.oracle.bmc.core.model.CreatePublicIpPoolDetails;
import com.oracle.bmc.core.model.PublicIp;
import com.oracle.bmc.core.model.PublicIpPool;
import com.oracle.bmc.core.model.PublicIpPoolCollection;
import com.oracle.bmc.core.model.PublicIpPoolSummary;
import com.oracle.bmc.core.model.RemovePublicIpPoolCapacityDetails;
import com.oracle.bmc.core.model.UpdateByoipRangeDetails;
import com.oracle.bmc.core.model.UpdatePublicIpPoolDetails;
import com.oracle.bmc.core.model.CreateByoipRangeDetails.Builder;
import com.oracle.bmc.core.model.CreatePublicIpDetails.Lifetime;
import com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest;
import com.oracle.bmc.core.requests.AddPublicIpPoolCapacityRequest;
import com.oracle.bmc.core.requests.AdvertiseByoipRangeRequest;
import com.oracle.bmc.core.requests.ChangeByoipRangeCompartmentRequest;
import com.oracle.bmc.core.requests.CreateByoipRangeRequest;
import com.oracle.bmc.core.requests.CreatePublicIpPoolRequest;
import com.oracle.bmc.core.requests.CreatePublicIpRequest;
import com.oracle.bmc.core.requests.DeleteByoipRangeRequest;
import com.oracle.bmc.core.requests.DeletePublicIpPoolRequest;
import com.oracle.bmc.core.requests.GetByoipRangeRequest;
import com.oracle.bmc.core.requests.ListByoipAllocatedRangesRequest;
import com.oracle.bmc.core.requests.ListByoipRangesRequest;
import com.oracle.bmc.core.requests.ListPublicIpPoolsRequest;
import com.oracle.bmc.core.requests.ListPublicIpsRequest;
import com.oracle.bmc.core.requests.RemovePublicIpPoolCapacityRequest;
import com.oracle.bmc.core.requests.UpdateByoipRangeRequest;
import com.oracle.bmc.core.requests.UpdatePublicIpPoolRequest;
import com.oracle.bmc.core.requests.ValidateByoipRangeRequest;
import com.oracle.bmc.core.requests.WithdrawByoipRangeRequest;
import com.oracle.bmc.core.requests.ListPublicIpsRequest.Scope;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ByoipService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ByoipService.class);
    public static final int ORACLE_BGP_ASN_COMMERCIAL = 31898;
    @Resource
    private OciUserMapper userMapper;

    private String tag(OciUser u) {
        return "[" + u.getUsername() + "] ";
    }

    private OciClientService oci(OciUser ociUser, String region) {
        String r = region != null && !region.isBlank() ? region.trim() : null;
        return new OciClientService(this.buildBasicDTO(ociUser), r);
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

    private String extractOciErrorMessage(BmcException e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            return "OCI 调用失败（无详细信息）";
        } else if (msg.contains("LimitExceeded")) {
            return "已超出配额限制。请在 OCI 控制台申请提升 BYOIP / 公网 IP 限额。";
        } else if (msg.contains("NotAuthorizedOrNotFound")) {
            return "权限不足或资源不存在。请确认 IAM 策略含网络 BYOIP 相关权限。";
        } else {
            return msg.length() > 200 ? msg.substring(0, 200) + "…" : msg;
        }
    }

    private OciUser requireUser(String userId) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            return ociUser;
        }
    }

    public static String formatOciValidationToken(String cidrBlock, String ipv6CidrBlock, String validationToken) {
        if (validationToken != null && !validationToken.isBlank()) {
            String cidr = ipv6CidrBlock != null && !ipv6CidrBlock.isBlank() ? ipv6CidrBlock.trim() : cidrBlock;
            return cidr != null && !cidr.isBlank() ? "OCITOKEN::" + cidr.trim() + ":" + validationToken.trim() : validationToken.trim();
        } else {
            return "";
        }
    }

    private Map<String, Object> mapByoipRange(ByoipRange r) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", r.getId());
        map.put("compartmentId", r.getCompartmentId());
        map.put("displayName", r.getDisplayName());
        map.put("cidrBlock", r.getCidrBlock());
        map.put("ipv6CidrBlock", r.getIpv6CidrBlock());
        map.put("ipVersion", r.getIpv6CidrBlock() != null && !r.getIpv6CidrBlock().isBlank() ? "IPV6" : "IPV4");
        map.put("lifecycleState", r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null);
        map.put("lifecycleDetails", r.getLifecycleDetails() != null ? r.getLifecycleDetails().getValue() : null);
        map.put("validationToken", r.getValidationToken());
        map.put("ociValidationToken", formatOciValidationToken(r.getCidrBlock(), r.getIpv6CidrBlock(), r.getValidationToken()));
        map.put("timeCreated", r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
        map.put("timeValidated", r.getTimeValidated() != null ? r.getTimeValidated().toString() : null);
        map.put("timeAdvertised", r.getTimeAdvertised() != null ? r.getTimeAdvertised().toString() : null);
        map.put("timeWithdrawn", r.getTimeWithdrawn() != null ? r.getTimeWithdrawn().toString() : null);
        if (r.getByoipRangeVcnIpv6Allocations() != null) {
            map.put("vcnIpv6Allocations", r.getByoipRangeVcnIpv6Allocations().stream().map(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("vcnId", a.getVcnId());
                m.put("ipv6CidrBlock", a.getIpv6CidrBlock());
                return m;
            }).collect(Collectors.toList()));
        } else {
            map.put("vcnIpv6Allocations", List.of());
        }

        return map;
    }

    private Map<String, Object> mapByoipRangeSummary(ByoipRangeSummary r) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", r.getId());
        map.put("compartmentId", r.getCompartmentId());
        map.put("displayName", r.getDisplayName());
        map.put("cidrBlock", r.getCidrBlock());
        map.put("ipv6CidrBlock", r.getIpv6CidrBlock());
        map.put("ipVersion", r.getIpv6CidrBlock() != null && !r.getIpv6CidrBlock().isBlank() ? "IPV6" : "IPV4");
        map.put("lifecycleState", r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null);
        map.put("lifecycleDetails", r.getLifecycleDetails() != null ? r.getLifecycleDetails().getValue() : null);
        map.put("timeCreated", r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
        if (r.getByoipRangeVcnIpv6Allocations() != null) {
            map.put("vcnIpv6Allocations", r.getByoipRangeVcnIpv6Allocations().stream().map(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("vcnId", a.getVcnId());
                m.put("ipv6CidrBlock", a.getIpv6CidrBlock());
                return m;
            }).collect(Collectors.toList()));
        } else {
            map.put("vcnIpv6Allocations", List.of());
        }

        return map;
    }

    public List<Map<String, Object>> listByoipRanges(String userId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            List var6;
            try (OciClientService client = this.oci(ociUser, region)) {
                ByoipRangeCollection coll = client.getVirtualNetworkClient()
                    .listByoipRanges(ListByoipRangesRequest.builder().compartmentId(client.getCompartmentId()).build())
                    .getByoipRangeCollection();
                if (coll != null && coll.getItems() != null) {
                    return coll.getItems().stream().map(this::mapByoipRangeSummary).collect(Collectors.toList());
                }

                var6 = List.of();
            }

            return var6;
        } catch (BmcException var9) {
            throw new OciException(this.tag(ociUser) + "获取 BYOIP 网段失败: " + this.extractOciErrorMessage(var9));
        } catch (Exception var10) {
            throw new OciException(this.tag(ociUser) + "获取 BYOIP 网段失败: " + var10.getMessage());
        }
    }

    public Map<String, Object> getByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            Map var7;
            try (OciClientService client = this.oci(ociUser, region)) {
                ByoipRange r = client.getVirtualNetworkClient()
                    .getByoipRange(GetByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build())
                    .getByoipRange();
                var7 = this.mapByoipRange(r);
            }

            return var7;
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "获取 BYOIP 详情失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "获取 BYOIP 详情失败: " + var11.getMessage());
        }
    }

    public Map<String, Object> createByoipRange(String userId, String displayName, String cidrBlock, String ipv6CidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        boolean hasV4 = cidrBlock != null && !cidrBlock.isBlank();
        boolean hasV6 = ipv6CidrBlock != null && !ipv6CidrBlock.isBlank();
        if (!hasV4 && !hasV6) {
            throw new OciException("请填写 IPv4 CIDR（如 203.0.113.0/24）或 IPv6 前缀（/48 或更大）");
        } else if (hasV4 && hasV6) {
            throw new OciException("一次只能导入 IPv4 CIDR 或 IPv6 前缀其一");
        } else {
            try {
                Map var12;
                try (OciClientService client = this.oci(ociUser, region)) {
                    Builder b = CreateByoipRangeDetails.builder().compartmentId(client.getCompartmentId());
                    if (displayName != null && !displayName.isBlank()) {
                        b.displayName(displayName.trim());
                    }

                    if (hasV4) {
                        b.cidrBlock(cidrBlock.trim());
                    }

                    if (hasV6) {
                        b.ipv6CidrBlock(ipv6CidrBlock.trim());
                    }

                    ByoipRange created = client.getVirtualNetworkClient()
                        .createByoipRange(CreateByoipRangeRequest.builder().createByoipRangeDetails(b.build()).build())
                        .getByoipRange();
                    log.info("BYOIP range created: {}", created.getId());
                    var12 = this.mapByoipRange(created);
                }

                return var12;
            } catch (BmcException var15) {
                throw new OciException(this.tag(ociUser) + "创建 BYOIP 导入请求失败: " + this.extractOciErrorMessage(var15));
            } catch (Exception var16) {
                throw new OciException(this.tag(ociUser) + "创建 BYOIP 导入请求失败: " + var16.getMessage());
            }
        }
    }

    public Map<String, Object> updateByoipRange(String userId, String byoipRangeId, String displayName, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            Map var9;
            try (OciClientService client = this.oci(ociUser, region)) {
                com.oracle.bmc.core.model.UpdateByoipRangeDetails.Builder b = UpdateByoipRangeDetails.builder();
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }

                ByoipRange updated = client.getVirtualNetworkClient()
                    .updateByoipRange(UpdateByoipRangeRequest.builder().byoipRangeId(byoipRangeId).updateByoipRangeDetails(b.build()).build())
                    .getByoipRange();
                var9 = this.mapByoipRange(updated);
            }

            return var9;
        } catch (BmcException var12) {
            throw new OciException(this.tag(ociUser) + "更新 BYOIP 失败: " + this.extractOciErrorMessage(var12));
        } catch (Exception var13) {
            throw new OciException(this.tag(ociUser) + "更新 BYOIP 失败: " + var13.getMessage());
        }
    }

    public void deleteByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient().deleteByoipRange(DeleteByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
                log.info("BYOIP range deleted: {}", byoipRangeId);
            }
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "删除 BYOIP 网段失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "删除 BYOIP 网段失败: " + var11.getMessage());
        }
    }

    public void validateByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient().validateByoipRange(ValidateByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
                log.info("BYOIP validate requested: {}", byoipRangeId);
            }
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "提交 BYOIP 校验失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "提交 BYOIP 校验失败: " + var11.getMessage());
        }
    }

    public void advertiseByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient().advertiseByoipRange(AdvertiseByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
                log.info("BYOIP advertise requested: {}", byoipRangeId);
            }
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "宣告 BYOIP 失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "宣告 BYOIP 失败: " + var11.getMessage());
        }
    }

    public void withdrawByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient().withdrawByoipRange(WithdrawByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
                log.info("BYOIP withdraw requested: {}", byoipRangeId);
            }
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "撤回 BYOIP 宣告失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "撤回 BYOIP 宣告失败: " + var11.getMessage());
        }
    }

    public void changeByoipRangeCompartment(String userId, String byoipRangeId, String compartmentId, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (compartmentId != null && !compartmentId.isBlank()) {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient()
                        .changeByoipRangeCompartment(
                            ChangeByoipRangeCompartmentRequest.builder()
                                .byoipRangeId(byoipRangeId)
                                .changeByoipRangeCompartmentDetails(ChangeByoipRangeCompartmentDetails.builder().compartmentId(compartmentId.trim()).build())
                                .build()
                        );
                }
            } catch (BmcException var11) {
                throw new OciException(this.tag(ociUser) + "移动 BYOIP 区间失败: " + this.extractOciErrorMessage(var11));
            } catch (Exception var12) {
                throw new OciException(this.tag(ociUser) + "移动 BYOIP 区间失败: " + var12.getMessage());
            }
        } else {
            throw new OciException("目标区间不能为空");
        }
    }

    public List<Map<String, Object>> listByoipAllocatedRanges(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            List var7;
            try (OciClientService client = this.oci(ociUser, region)) {
                ByoipAllocatedRangeCollection allocColl = client.getVirtualNetworkClient()
                    .listByoipAllocatedRanges(ListByoipAllocatedRangesRequest.builder().byoipRangeId(byoipRangeId).build())
                    .getByoipAllocatedRangeCollection();
                if (allocColl != null && allocColl.getItems() != null) {
                    return allocColl.getItems().stream().map(a -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("cidrBlock", a.getCidrBlock());
                        m.put("publicIpPoolId", a.getPublicIpPoolId());
                        m.put("byoipRangeId", byoipRangeId);
                        return m;
                    }).collect(Collectors.toList());
                }

                var7 = List.of();
            }

            return var7;
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "获取已分配子网段失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "获取已分配子网段失败: " + var11.getMessage());
        }
    }

    public List<Map<String, Object>> listPublicIpPools(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            List var8;
            try (OciClientService client = this.oci(ociUser, region)) {
                com.oracle.bmc.core.requests.ListPublicIpPoolsRequest.Builder req = ListPublicIpPoolsRequest.builder().compartmentId(client.getCompartmentId());
                if (byoipRangeId != null && !byoipRangeId.isBlank()) {
                    req.byoipRangeId(byoipRangeId.trim());
                }

                PublicIpPoolCollection poolColl = client.getVirtualNetworkClient().listPublicIpPools(req.build()).getPublicIpPoolCollection();
                if (poolColl != null && poolColl.getItems() != null) {
                    return poolColl.getItems().stream().map(this::mapPublicIpPoolSummary).collect(Collectors.toList());
                }

                var8 = List.of();
            }

            return var8;
        } catch (BmcException var11) {
            throw new OciException(this.tag(ociUser) + "获取公网 IP 池失败: " + this.extractOciErrorMessage(var11));
        } catch (Exception var12) {
            throw new OciException(this.tag(ociUser) + "获取公网 IP 池失败: " + var12.getMessage());
        }
    }

    private Map<String, Object> mapPublicIpPool(PublicIpPool p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("displayName", p.getDisplayName());
        map.put("cidrBlocks", p.getCidrBlocks() != null ? p.getCidrBlocks() : List.of());
        map.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        map.put("timeCreated", p.getTimeCreated() != null ? p.getTimeCreated().toString() : null);
        return map;
    }

    private Map<String, Object> mapPublicIpPoolSummary(PublicIpPoolSummary p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("displayName", p.getDisplayName());
        map.put("cidrBlocks", List.of());
        map.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        map.put("timeCreated", p.getTimeCreated() != null ? p.getTimeCreated().toString() : null);
        return map;
    }

    public Map<String, Object> createPublicIpPool(String userId, String displayName, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            Map var8;
            try (OciClientService client = this.oci(ociUser, region)) {
                com.oracle.bmc.core.model.CreatePublicIpPoolDetails.Builder b = CreatePublicIpPoolDetails.builder().compartmentId(client.getCompartmentId());
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }

                PublicIpPool pool = client.getVirtualNetworkClient()
                    .createPublicIpPool(CreatePublicIpPoolRequest.builder().createPublicIpPoolDetails(b.build()).build())
                    .getPublicIpPool();
                var8 = this.mapPublicIpPool(pool);
            }

            return var8;
        } catch (BmcException var11) {
            throw new OciException(this.tag(ociUser) + "创建公网 IP 池失败: " + this.extractOciErrorMessage(var11));
        } catch (Exception var12) {
            throw new OciException(this.tag(ociUser) + "创建公网 IP 池失败: " + var12.getMessage());
        }
    }

    public Map<String, Object> updatePublicIpPool(String userId, String publicIpPoolId, String displayName, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            Map var9;
            try (OciClientService client = this.oci(ociUser, region)) {
                com.oracle.bmc.core.model.UpdatePublicIpPoolDetails.Builder b = UpdatePublicIpPoolDetails.builder();
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }

                PublicIpPool pool = client.getVirtualNetworkClient()
                    .updatePublicIpPool(UpdatePublicIpPoolRequest.builder().publicIpPoolId(publicIpPoolId).updatePublicIpPoolDetails(b.build()).build())
                    .getPublicIpPool();
                var9 = this.mapPublicIpPool(pool);
            }

            return var9;
        } catch (BmcException var12) {
            throw new OciException(this.tag(ociUser) + "更新公网 IP 池失败: " + this.extractOciErrorMessage(var12));
        } catch (Exception var13) {
            throw new OciException(this.tag(ociUser) + "更新公网 IP 池失败: " + var13.getMessage());
        }
    }

    public void deletePublicIpPool(String userId, String publicIpPoolId, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient().deletePublicIpPool(DeletePublicIpPoolRequest.builder().publicIpPoolId(publicIpPoolId).build());
            }
        } catch (BmcException var10) {
            throw new OciException(this.tag(ociUser) + "删除公网 IP 池失败: " + this.extractOciErrorMessage(var10));
        } catch (Exception var11) {
            throw new OciException(this.tag(ociUser) + "删除公网 IP 池失败: " + var11.getMessage());
        }
    }

    public void addPublicIpPoolCapacity(String userId, String publicIpPoolId, String byoipRangeId, String cidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (publicIpPoolId != null && !publicIpPoolId.isBlank() && byoipRangeId != null && !byoipRangeId.isBlank() && cidrBlock != null && !cidrBlock.isBlank()
            )
         {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient()
                        .addPublicIpPoolCapacity(
                            AddPublicIpPoolCapacityRequest.builder()
                                .publicIpPoolId(publicIpPoolId.trim())
                                .addPublicIpPoolCapacityDetails(
                                    AddPublicIpPoolCapacityDetails.builder().byoipRangeId(byoipRangeId.trim()).cidrBlock(cidrBlock.trim()).build()
                                )
                                .build()
                        );
                    log.info("Added {} from BYOIP {} to pool {}", new Object[]{cidrBlock, byoipRangeId, publicIpPoolId});
                }
            } catch (BmcException var12) {
                throw new OciException(this.tag(ociUser) + "向 IP 池添加 BYOIP 容量失败: " + this.extractOciErrorMessage(var12));
            } catch (Exception var13) {
                throw new OciException(this.tag(ociUser) + "向 IP 池添加 BYOIP 容量失败: " + var13.getMessage());
            }
        } else {
            throw new OciException("公网 IP 池、BYOIP 网段与子网 CIDR 均不能为空");
        }
    }

    public void removePublicIpPoolCapacity(String userId, String publicIpPoolId, String cidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);

        try {
            try (OciClientService client = this.oci(ociUser, region)) {
                client.getVirtualNetworkClient()
                    .removePublicIpPoolCapacity(
                        RemovePublicIpPoolCapacityRequest.builder()
                            .publicIpPoolId(publicIpPoolId)
                            .removePublicIpPoolCapacityDetails(RemovePublicIpPoolCapacityDetails.builder().cidrBlock(cidrBlock.trim()).build())
                            .build()
                    );
            }
        } catch (BmcException var11) {
            throw new OciException(this.tag(ociUser) + "从 IP 池移除 BYOIP 容量失败: " + this.extractOciErrorMessage(var11));
        } catch (Exception var12) {
            throw new OciException(this.tag(ociUser) + "从 IP 池移除 BYOIP 容量失败: " + var12.getMessage());
        }
    }

    public Map<String, String> createByoipReservedIp(String userId, String displayName, String publicIpPoolId, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (publicIpPoolId != null && !publicIpPoolId.isBlank()) {
            try {
                Map var9;
                try (OciClientService client = this.oci(ociUser, region)) {
                    com.oracle.bmc.core.model.CreatePublicIpDetails.Builder builder = CreatePublicIpDetails.builder()
                        .compartmentId(client.getCompartmentId())
                        .lifetime(Lifetime.Reserved)
                        .publicIpPoolId(publicIpPoolId.trim());
                    if (displayName != null && !displayName.isBlank()) {
                        builder.displayName(displayName.trim());
                    }

                    PublicIp ip = client.getVirtualNetworkClient()
                        .createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(builder.build()).build())
                        .getPublicIp();
                    var9 = Map.of("publicIpId", ip.getId(), "ipAddress", ip.getIpAddress() != null ? ip.getIpAddress() : "");
                }

                return var9;
            } catch (BmcException var12) {
                throw new OciException(this.tag(ociUser) + "从 BYOIP 池创建公网 IP 失败: " + this.extractOciErrorMessage(var12));
            } catch (Exception var13) {
                throw new OciException(this.tag(ociUser) + "从 BYOIP 池创建公网 IP 失败: " + var13.getMessage());
            }
        } else {
            throw new OciException("请选择公网 IP 池");
        }
    }

    public List<Map<String, Object>> listByoipPublicIps(String userId, String region) {
        OciUser ociUser = this.requireUser(userId);

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
                var6 = publicIps.stream().filter(ip -> ip.getPublicIpPoolId() != null && !ip.getPublicIpPoolId().isBlank()).map(ip -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", ip.getId());
                    map.put("ipAddress", ip.getIpAddress());
                    map.put("displayName", ip.getDisplayName());
                    map.put("publicIpPoolId", ip.getPublicIpPoolId());
                    map.put("lifecycleState", ip.getLifecycleState() != null ? ip.getLifecycleState().getValue() : null);
                    map.put("isAssigned", ip.getAssignedEntityId() != null);
                    map.put("assignedEntityId", ip.getAssignedEntityId());
                    map.put("timeCreated", ip.getTimeCreated() != null ? ip.getTimeCreated().toString() : null);
                    return map;
                }).collect(Collectors.toList());
            }

            return var6;
        } catch (Exception var9) {
            throw new OciException(this.tag(ociUser) + "获取 BYOIP 公网 IP 失败: " + var9.getMessage());
        }
    }

    public void assignByoipv6ToVcn(String userId, String vcnId, String byoipRangeId, String ipv6CidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (vcnId != null && !vcnId.isBlank() && byoipRangeId != null && !byoipRangeId.isBlank() && ipv6CidrBlock != null && !ipv6CidrBlock.isBlank()) {
            try {
                try (OciClientService client = this.oci(ociUser, region)) {
                    client.getVirtualNetworkClient()
                        .addIpv6VcnCidr(
                            AddIpv6VcnCidrRequest.builder()
                                .vcnId(vcnId.trim())
                                .addVcnIpv6CidrDetails(
                                    AddVcnIpv6CidrDetails.builder()
                                        .byoipv6CidrDetail(
                                            Byoipv6CidrDetails.builder().byoipv6RangeId(byoipRangeId.trim()).ipv6CidrBlock(ipv6CidrBlock.trim()).build()
                                        )
                                        .build()
                                )
                                .build()
                        );
                    log.info("BYOIPv6 {} assigned to VCN {}", ipv6CidrBlock, vcnId);
                }
            } catch (BmcException var12) {
                throw new OciException(this.tag(ociUser) + "分配 BYOIPv6 到 VCN 失败: " + this.extractOciErrorMessage(var12));
            } catch (Exception var13) {
                throw new OciException(this.tag(ociUser) + "分配 BYOIPv6 到 VCN 失败: " + var13.getMessage());
            }
        } else {
            throw new OciException("VCN、BYOIP 网段与 IPv6 子网前缀均不能为空");
        }
    }

    public Map<String, Object> getByoipHelp() {
        Map<String, Object> help = new LinkedHashMap<>();
        help.put("oracleBgpAsn", 31898);
        help.put("oracleBgpAsnNote", "塞尔维亚 Jovanovac 等区域 ASN 为 14544；美国政府云见 OCI 文档");
        help.put("ipv4CidrLimits", "/24 至 /8");
        help.put("ipv6PrefixLimits", "/48 或更大");
        help.put("maxRangesPerTenancy", 20);
        help.put("freeTierSupported", false);
        help.put("validationDays", "最长约 10 个工作日");
        help.put(
            "steps",
            List.of(
                "1. 在本面板「导入网段」创建 ByoipRange，复制 OCITOKEN 验证串",
                "2. 在 RIR（ARIN/RIPE/APNIC）添加验证 token，并创建 ROA 授权 Oracle ASN",
                "3. 点击「完成导入校验」(validateByoipRange)",
                "4. IPv4：创建/选择公网 IP 池，将 BYOIP 子网段加入池",
                "5. 状态 PROVISIONED 后点击「BGP 宣告」(advertise)",
                "6. 从 IP 池创建 Reserved 公网 IP，绑定实例/LB/NAT",
                "7. IPv6：校验通过后使用「分配到 VCN」，再在子网/实例上配置 IPv6"
            )
        );
        help.put("docUrl", "https://docs.oracle.com/en-us/iaas/Content/Network/Concepts/BYOIP.htm");
        return help;
    }
}
