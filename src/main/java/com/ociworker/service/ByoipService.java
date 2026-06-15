/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.ByoipService
 *  com.ociworker.service.OciClientService
 *  com.oracle.bmc.core.model.AddPublicIpPoolCapacityDetails
 *  com.oracle.bmc.core.model.AddVcnIpv6CidrDetails
 *  com.oracle.bmc.core.model.ByoipAllocatedRangeCollection
 *  com.oracle.bmc.core.model.ByoipRange
 *  com.oracle.bmc.core.model.ByoipRangeCollection
 *  com.oracle.bmc.core.model.ByoipRangeSummary
 *  com.oracle.bmc.core.model.Byoipv6CidrDetails
 *  com.oracle.bmc.core.model.ChangeByoipRangeCompartmentDetails
 *  com.oracle.bmc.core.model.CreateByoipRangeDetails
 *  com.oracle.bmc.core.model.CreateByoipRangeDetails$Builder
 *  com.oracle.bmc.core.model.CreatePublicIpDetails
 *  com.oracle.bmc.core.model.CreatePublicIpDetails$Builder
 *  com.oracle.bmc.core.model.CreatePublicIpDetails$Lifetime
 *  com.oracle.bmc.core.model.CreatePublicIpPoolDetails
 *  com.oracle.bmc.core.model.CreatePublicIpPoolDetails$Builder
 *  com.oracle.bmc.core.model.PublicIp
 *  com.oracle.bmc.core.model.PublicIpPool
 *  com.oracle.bmc.core.model.PublicIpPoolCollection
 *  com.oracle.bmc.core.model.PublicIpPoolSummary
 *  com.oracle.bmc.core.model.RemovePublicIpPoolCapacityDetails
 *  com.oracle.bmc.core.model.UpdateByoipRangeDetails
 *  com.oracle.bmc.core.model.UpdateByoipRangeDetails$Builder
 *  com.oracle.bmc.core.model.UpdatePublicIpPoolDetails
 *  com.oracle.bmc.core.model.UpdatePublicIpPoolDetails$Builder
 *  com.oracle.bmc.core.requests.AddIpv6VcnCidrRequest
 *  com.oracle.bmc.core.requests.AddPublicIpPoolCapacityRequest
 *  com.oracle.bmc.core.requests.AdvertiseByoipRangeRequest
 *  com.oracle.bmc.core.requests.ChangeByoipRangeCompartmentRequest
 *  com.oracle.bmc.core.requests.CreateByoipRangeRequest
 *  com.oracle.bmc.core.requests.CreatePublicIpPoolRequest
 *  com.oracle.bmc.core.requests.CreatePublicIpRequest
 *  com.oracle.bmc.core.requests.DeleteByoipRangeRequest
 *  com.oracle.bmc.core.requests.DeletePublicIpPoolRequest
 *  com.oracle.bmc.core.requests.GetByoipRangeRequest
 *  com.oracle.bmc.core.requests.ListByoipAllocatedRangesRequest
 *  com.oracle.bmc.core.requests.ListByoipRangesRequest
 *  com.oracle.bmc.core.requests.ListPublicIpPoolsRequest
 *  com.oracle.bmc.core.requests.ListPublicIpPoolsRequest$Builder
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest$Lifetime
 *  com.oracle.bmc.core.requests.ListPublicIpsRequest$Scope
 *  com.oracle.bmc.core.requests.RemovePublicIpPoolCapacityRequest
 *  com.oracle.bmc.core.requests.UpdateByoipRangeRequest
 *  com.oracle.bmc.core.requests.UpdatePublicIpPoolRequest
 *  com.oracle.bmc.core.requests.ValidateByoipRangeRequest
 *  com.oracle.bmc.core.requests.WithdrawByoipRangeRequest
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
import com.ociworker.model.dto.SysUserDTO;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.OciClientService;
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
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
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
        String r = region == null || region.isBlank() ? null : region.trim();
        return new OciClientService(this.buildBasicDTO(ociUser), r);
    }

    private SysUserDTO buildBasicDTO(OciUser ociUser) {
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }

    private String extractOciErrorMessage(BmcException e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            return "OCI \u8c03\u7528\u5931\u8d25\uff08\u65e0\u8be6\u7ec6\u4fe1\u606f\uff09";
        }
        if (msg.contains("LimitExceeded")) {
            return "\u5df2\u8d85\u51fa\u914d\u989d\u9650\u5236\u3002\u8bf7\u5728 OCI \u63a7\u5236\u53f0\u7533\u8bf7\u63d0\u5347 BYOIP / \u516c\u7f51 IP \u9650\u989d\u3002";
        }
        if (msg.contains("NotAuthorizedOrNotFound")) {
            return "\u6743\u9650\u4e0d\u8db3\u6216\u8d44\u6e90\u4e0d\u5b58\u5728\u3002\u8bf7\u786e\u8ba4 IAM \u7b56\u7565\u542b\u7f51\u7edc BYOIP \u76f8\u5173\u6743\u9650\u3002";
        }
        return msg.length() > 200 ? msg.substring(0, 200) + "\u2026" : msg;
    }

    private OciUser requireUser(String userId) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        return ociUser;
    }

    public static String formatOciValidationToken(String cidrBlock, String ipv6CidrBlock, String validationToken) {
        String cidr;
        if (validationToken == null || validationToken.isBlank()) {
            return "";
        }
        String string = cidr = ipv6CidrBlock != null && !ipv6CidrBlock.isBlank() ? ipv6CidrBlock.trim() : cidrBlock;
        if (cidr == null || cidr.isBlank()) {
            return validationToken.trim();
        }
        return "OCITOKEN::" + cidr.trim() + ":" + validationToken.trim();
    }

    private Map<String, Object> mapByoipRange(ByoipRange r) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", r.getId());
        map.put("compartmentId", r.getCompartmentId());
        map.put("displayName", r.getDisplayName());
        map.put("cidrBlock", r.getCidrBlock());
        map.put("ipv6CidrBlock", r.getIpv6CidrBlock());
        map.put("ipVersion", r.getIpv6CidrBlock() != null && !r.getIpv6CidrBlock().isBlank() ? "IPV6" : "IPV4");
        map.put("lifecycleState", r.getLifecycleState() != null ? r.getLifecycleState().getValue() : null);
        map.put("lifecycleDetails", r.getLifecycleDetails() != null ? r.getLifecycleDetails().getValue() : null);
        map.put("validationToken", r.getValidationToken());
        map.put("ociValidationToken", ByoipService.formatOciValidationToken((String)r.getCidrBlock(), (String)r.getIpv6CidrBlock(), (String)r.getValidationToken()));
        map.put("timeCreated", r.getTimeCreated() != null ? r.getTimeCreated().toString() : null);
        map.put("timeValidated", r.getTimeValidated() != null ? r.getTimeValidated().toString() : null);
        map.put("timeAdvertised", r.getTimeAdvertised() != null ? r.getTimeAdvertised().toString() : null);
        map.put("timeWithdrawn", r.getTimeWithdrawn() != null ? r.getTimeWithdrawn().toString() : null);
        if (r.getByoipRangeVcnIpv6Allocations() != null) {
            map.put("vcnIpv6Allocations", r.getByoipRangeVcnIpv6Allocations().stream().map(a -> {
                LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
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
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
                LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
                m.put("vcnId", a.getVcnId());
                m.put("ipv6CidrBlock", a.getIpv6CidrBlock());
                return m;
            }).collect(Collectors.toList()));
        } else {
            map.put("vcnIpv6Allocations", List.of());
        }
        return map;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Map<String, Object>> listByoipRanges(String userId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            ByoipRangeCollection coll = client.getVirtualNetworkClient().listByoipRanges(ListByoipRangesRequest.builder().compartmentId(client.getCompartmentId()).build()).getByoipRangeCollection();
            if (coll == null || coll.getItems() == null) {
                List<Map<String, Object>> list2 = List.of();
                return list2;
            }
            List<Map<String, Object>> list = coll.getItems().stream().map(arg_0 -> this.mapByoipRangeSummary(arg_0)).collect(Collectors.toList());
            return list;
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6 BYOIP \u7f51\u6bb5\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6 BYOIP \u7f51\u6bb5\u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, Object> getByoipRange(String userId, String byoipRangeId, String region) {
        Map map;
        block9: {
            OciUser ociUser = this.requireUser(userId);
            OciClientService client = this.oci(ociUser, region);
            try {
                ByoipRange r = client.getVirtualNetworkClient().getByoipRange(GetByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build()).getByoipRange();
                map = this.mapByoipRange(r);
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
                catch (BmcException e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6 BYOIP \u8be6\u60c5\u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6 BYOIP \u8be6\u60c5\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public Map<String, Object> createByoipRange(String userId, String displayName, String cidrBlock, String ipv6CidrBlock, String region) {
        Map map;
        block14: {
            boolean hasV6;
            OciUser ociUser = this.requireUser(userId);
            boolean hasV4 = cidrBlock != null && !cidrBlock.isBlank();
            boolean bl = hasV6 = ipv6CidrBlock != null && !ipv6CidrBlock.isBlank();
            if (!hasV4 && !hasV6) {
                throw new OciException("\u8bf7\u586b\u5199 IPv4 CIDR\uff08\u5982 203.0.113.0/24\uff09\u6216 IPv6 \u524d\u7f00\uff08/48 \u6216\u66f4\u5927\uff09");
            }
            if (hasV4 && hasV6) {
                throw new OciException("\u4e00\u6b21\u53ea\u80fd\u5bfc\u5165 IPv4 CIDR \u6216 IPv6 \u524d\u7f00\u5176\u4e00");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                CreateByoipRangeDetails.Builder b = CreateByoipRangeDetails.builder().compartmentId(client.getCompartmentId());
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }
                if (hasV4) {
                    b.cidrBlock(cidrBlock.trim());
                }
                if (hasV6) {
                    b.ipv6CidrBlock(ipv6CidrBlock.trim());
                }
                ByoipRange created = client.getVirtualNetworkClient().createByoipRange(CreateByoipRangeRequest.builder().createByoipRangeDetails(b.build()).build()).getByoipRange();
                log.info("BYOIP range created: {}", (Object)created.getId());
                map = this.mapByoipRange(created);
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
                catch (BmcException e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa BYOIP \u5bfc\u5165\u8bf7\u6c42\u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa BYOIP \u5bfc\u5165\u8bf7\u6c42\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public Map<String, Object> updateByoipRange(String userId, String byoipRangeId, String displayName, String region) {
        Map map;
        block10: {
            OciUser ociUser = this.requireUser(userId);
            OciClientService client = this.oci(ociUser, region);
            try {
                UpdateByoipRangeDetails.Builder b = UpdateByoipRangeDetails.builder();
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }
                ByoipRange updated = client.getVirtualNetworkClient().updateByoipRange(UpdateByoipRangeRequest.builder().byoipRangeId(byoipRangeId).updateByoipRangeDetails(b.build()).build()).getByoipRange();
                map = this.mapByoipRange(updated);
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
                catch (BmcException e) {
                    throw new OciException(this.tag(ociUser) + "\u66f4\u65b0 BYOIP \u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u66f4\u65b0 BYOIP \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public void deleteByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().deleteByoipRange(DeleteByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
            log.info("BYOIP range deleted: {}", (Object)byoipRangeId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664 BYOIP \u7f51\u6bb5\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664 BYOIP \u7f51\u6bb5\u5931\u8d25: " + e.getMessage());
        }
    }

    public void validateByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().validateByoipRange(ValidateByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
            log.info("BYOIP validate requested: {}", (Object)byoipRangeId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u63d0\u4ea4 BYOIP \u6821\u9a8c\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u63d0\u4ea4 BYOIP \u6821\u9a8c\u5931\u8d25: " + e.getMessage());
        }
    }

    public void advertiseByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().advertiseByoipRange(AdvertiseByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
            log.info("BYOIP advertise requested: {}", (Object)byoipRangeId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u5ba3\u544a BYOIP \u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5ba3\u544a BYOIP \u5931\u8d25: " + e.getMessage());
        }
    }

    public void withdrawByoipRange(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().withdrawByoipRange(WithdrawByoipRangeRequest.builder().byoipRangeId(byoipRangeId).build());
            log.info("BYOIP withdraw requested: {}", (Object)byoipRangeId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u64a4\u56de BYOIP \u5ba3\u544a\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u64a4\u56de BYOIP \u5ba3\u544a\u5931\u8d25: " + e.getMessage());
        }
    }

    public void changeByoipRangeCompartment(String userId, String byoipRangeId, String compartmentId, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (compartmentId == null || compartmentId.isBlank()) {
            throw new OciException("\u76ee\u6807\u533a\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().changeByoipRangeCompartment(ChangeByoipRangeCompartmentRequest.builder().byoipRangeId(byoipRangeId).changeByoipRangeCompartmentDetails(ChangeByoipRangeCompartmentDetails.builder().compartmentId(compartmentId.trim()).build()).build());
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u79fb\u52a8 BYOIP \u533a\u95f4\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u79fb\u52a8 BYOIP \u533a\u95f4\u5931\u8d25: " + e.getMessage());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Map<String, Object>> listByoipAllocatedRanges(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            ByoipAllocatedRangeCollection allocColl = client.getVirtualNetworkClient().listByoipAllocatedRanges(ListByoipAllocatedRangesRequest.builder().byoipRangeId(byoipRangeId).build()).getByoipAllocatedRangeCollection();
            if (allocColl == null || allocColl.getItems() == null) {
                List<Map<String, Object>> list2 = List.of();
                return list2;
            }
            List<Map<String, Object>> list = allocColl.getItems().stream().map(a -> {
                LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
                m.put("cidrBlock", a.getCidrBlock());
                m.put("publicIpPoolId", a.getPublicIpPoolId());
                m.put("byoipRangeId", byoipRangeId);
                return m;
            }).collect(Collectors.toList());
            return list;
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5df2\u5206\u914d\u5b50\u7f51\u6bb5\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u5df2\u5206\u914d\u5b50\u7f51\u6bb5\u5931\u8d25: " + e.getMessage());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Map<String, Object>> listPublicIpPools(String userId, String byoipRangeId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            PublicIpPoolCollection poolColl;
            ListPublicIpPoolsRequest.Builder req = ListPublicIpPoolsRequest.builder().compartmentId(client.getCompartmentId());
            if (byoipRangeId != null && !byoipRangeId.isBlank()) {
                req.byoipRangeId(byoipRangeId.trim());
            }
            if ((poolColl = client.getVirtualNetworkClient().listPublicIpPools(req.build()).getPublicIpPoolCollection()) == null || poolColl.getItems() == null) {
                List<Map<String, Object>> list2 = List.of();
                return list2;
            }
            List<Map<String, Object>> list = poolColl.getItems().stream().map(arg_0 -> this.mapPublicIpPoolSummary(arg_0)).collect(Collectors.toList());
            return list;
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u516c\u7f51 IP \u6c60\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u83b7\u53d6\u516c\u7f51 IP \u6c60\u5931\u8d25: " + e.getMessage());
        }
    }

    private Map<String, Object> mapPublicIpPool(PublicIpPool p) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", p.getId());
        map.put("displayName", p.getDisplayName());
        map.put("cidrBlocks", p.getCidrBlocks() != null ? p.getCidrBlocks() : List.of());
        map.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        map.put("timeCreated", p.getTimeCreated() != null ? p.getTimeCreated().toString() : null);
        return map;
    }

    private Map<String, Object> mapPublicIpPoolSummary(PublicIpPoolSummary p) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", p.getId());
        map.put("displayName", p.getDisplayName());
        map.put("cidrBlocks", List.of());
        map.put("lifecycleState", p.getLifecycleState() != null ? p.getLifecycleState().getValue() : null);
        map.put("timeCreated", p.getTimeCreated() != null ? p.getTimeCreated().toString() : null);
        return map;
    }

    public Map<String, Object> createPublicIpPool(String userId, String displayName, String region) {
        Map map;
        block10: {
            OciUser ociUser = this.requireUser(userId);
            OciClientService client = this.oci(ociUser, region);
            try {
                CreatePublicIpPoolDetails.Builder b = CreatePublicIpPoolDetails.builder().compartmentId(client.getCompartmentId());
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }
                PublicIpPool pool = client.getVirtualNetworkClient().createPublicIpPool(CreatePublicIpPoolRequest.builder().createPublicIpPoolDetails(b.build()).build()).getPublicIpPool();
                map = this.mapPublicIpPool(pool);
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
                catch (BmcException e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u516c\u7f51 IP \u6c60\u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u521b\u5efa\u516c\u7f51 IP \u6c60\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public Map<String, Object> updatePublicIpPool(String userId, String publicIpPoolId, String displayName, String region) {
        Map map;
        block10: {
            OciUser ociUser = this.requireUser(userId);
            OciClientService client = this.oci(ociUser, region);
            try {
                UpdatePublicIpPoolDetails.Builder b = UpdatePublicIpPoolDetails.builder();
                if (displayName != null && !displayName.isBlank()) {
                    b.displayName(displayName.trim());
                }
                PublicIpPool pool = client.getVirtualNetworkClient().updatePublicIpPool(UpdatePublicIpPoolRequest.builder().publicIpPoolId(publicIpPoolId).updatePublicIpPoolDetails(b.build()).build()).getPublicIpPool();
                map = this.mapPublicIpPool(pool);
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
                catch (BmcException e) {
                    throw new OciException(this.tag(ociUser) + "\u66f4\u65b0\u516c\u7f51 IP \u6c60\u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u66f4\u65b0\u516c\u7f51 IP \u6c60\u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public void deletePublicIpPool(String userId, String publicIpPoolId, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().deletePublicIpPool(DeletePublicIpPoolRequest.builder().publicIpPoolId(publicIpPoolId).build());
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u516c\u7f51 IP \u6c60\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5220\u9664\u516c\u7f51 IP \u6c60\u5931\u8d25: " + e.getMessage());
        }
    }

    public void addPublicIpPoolCapacity(String userId, String publicIpPoolId, String byoipRangeId, String cidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (publicIpPoolId == null || publicIpPoolId.isBlank() || byoipRangeId == null || byoipRangeId.isBlank() || cidrBlock == null || cidrBlock.isBlank()) {
            throw new OciException("\u516c\u7f51 IP \u6c60\u3001BYOIP \u7f51\u6bb5\u4e0e\u5b50\u7f51 CIDR \u5747\u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().addPublicIpPoolCapacity(AddPublicIpPoolCapacityRequest.builder().publicIpPoolId(publicIpPoolId.trim()).addPublicIpPoolCapacityDetails(AddPublicIpPoolCapacityDetails.builder().byoipRangeId(byoipRangeId.trim()).cidrBlock(cidrBlock.trim()).build()).build());
            log.info("Added {} from BYOIP {} to pool {}", new Object[]{cidrBlock, byoipRangeId, publicIpPoolId});
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u5411 IP \u6c60\u6dfb\u52a0 BYOIP \u5bb9\u91cf\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5411 IP \u6c60\u6dfb\u52a0 BYOIP \u5bb9\u91cf\u5931\u8d25: " + e.getMessage());
        }
    }

    public void removePublicIpPoolCapacity(String userId, String publicIpPoolId, String cidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().removePublicIpPoolCapacity(RemovePublicIpPoolCapacityRequest.builder().publicIpPoolId(publicIpPoolId).removePublicIpPoolCapacityDetails(RemovePublicIpPoolCapacityDetails.builder().cidrBlock(cidrBlock.trim()).build()).build());
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u4ece IP \u6c60\u79fb\u9664 BYOIP \u5bb9\u91cf\u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u4ece IP \u6c60\u79fb\u9664 BYOIP \u5bb9\u91cf\u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, String> createByoipReservedIp(String userId, String displayName, String publicIpPoolId, String region) {
        Map<String, String> map;
        block11: {
            OciUser ociUser = this.requireUser(userId);
            if (publicIpPoolId == null || publicIpPoolId.isBlank()) {
                throw new OciException("\u8bf7\u9009\u62e9\u516c\u7f51 IP \u6c60");
            }
            OciClientService client = this.oci(ociUser, region);
            try {
                CreatePublicIpDetails.Builder builder = CreatePublicIpDetails.builder().compartmentId(client.getCompartmentId()).lifetime(CreatePublicIpDetails.Lifetime.Reserved).publicIpPoolId(publicIpPoolId.trim());
                if (displayName != null && !displayName.isBlank()) {
                    builder.displayName(displayName.trim());
                }
                PublicIp ip = client.getVirtualNetworkClient().createPublicIp(CreatePublicIpRequest.builder().createPublicIpDetails(builder.build()).build()).getPublicIp();
                map = Map.of("publicIpId", ip.getId(), "ipAddress", ip.getIpAddress() != null ? ip.getIpAddress() : "");
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
                    throw new OciException(this.tag(ociUser) + "\u4ece BYOIP \u6c60\u521b\u5efa\u516c\u7f51 IP \u5931\u8d25: " + this.extractOciErrorMessage(e));
                }
                catch (Exception e) {
                    throw new OciException(this.tag(ociUser) + "\u4ece BYOIP \u6c60\u521b\u5efa\u516c\u7f51 IP \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return map;
    }

    public List<Map<String, Object>> listByoipPublicIps(String userId, String region) {
        List<Map<String, Object>> list;
        block8: {
            OciUser ociUser = this.requireUser(userId);
            OciClientService client = this.oci(ociUser, region);
            try {
                List publicIps = client.getVirtualNetworkClient().listPublicIps(ListPublicIpsRequest.builder().compartmentId(client.getCompartmentId()).scope(ListPublicIpsRequest.Scope.Region).lifetime(ListPublicIpsRequest.Lifetime.Reserved).build()).getItems();
                list = publicIps.stream().filter(ip -> ip.getPublicIpPoolId() != null && !ip.getPublicIpPoolId().isBlank()).map(ip -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
                if (client == null) break block8;
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
                    throw new OciException(this.tag(ociUser) + "\u83b7\u53d6 BYOIP \u516c\u7f51 IP \u5931\u8d25: " + e.getMessage());
                }
            }
            client.close();
        }
        return list;
    }

    public void assignByoipv6ToVcn(String userId, String vcnId, String byoipRangeId, String ipv6CidrBlock, String region) {
        OciUser ociUser = this.requireUser(userId);
        if (vcnId == null || vcnId.isBlank() || byoipRangeId == null || byoipRangeId.isBlank() || ipv6CidrBlock == null || ipv6CidrBlock.isBlank()) {
            throw new OciException("VCN\u3001BYOIP \u7f51\u6bb5\u4e0e IPv6 \u5b50\u7f51\u524d\u7f00\u5747\u4e0d\u80fd\u4e3a\u7a7a");
        }
        try (OciClientService client = this.oci(ociUser, region);){
            client.getVirtualNetworkClient().addIpv6VcnCidr(AddIpv6VcnCidrRequest.builder().vcnId(vcnId.trim()).addVcnIpv6CidrDetails(AddVcnIpv6CidrDetails.builder().byoipv6CidrDetail(Byoipv6CidrDetails.builder().byoipv6RangeId(byoipRangeId.trim()).ipv6CidrBlock(ipv6CidrBlock.trim()).build()).build()).build());
            log.info("BYOIPv6 {} assigned to VCN {}", (Object)ipv6CidrBlock, (Object)vcnId);
        }
        catch (BmcException e) {
            throw new OciException(this.tag(ociUser) + "\u5206\u914d BYOIPv6 \u5230 VCN \u5931\u8d25: " + this.extractOciErrorMessage(e));
        }
        catch (Exception e) {
            throw new OciException(this.tag(ociUser) + "\u5206\u914d BYOIPv6 \u5230 VCN \u5931\u8d25: " + e.getMessage());
        }
    }

    public Map<String, Object> getByoipHelp() {
        LinkedHashMap<String, Object> help = new LinkedHashMap<String, Object>();
        help.put("oracleBgpAsn", 31898);
        help.put("oracleBgpAsnNote", "\u585e\u5c14\u7ef4\u4e9a Jovanovac \u7b49\u533a\u57df ASN \u4e3a 14544\uff1b\u7f8e\u56fd\u653f\u5e9c\u4e91\u89c1 OCI \u6587\u6863");
        help.put("ipv4CidrLimits", "/24 \u81f3 /8");
        help.put("ipv6PrefixLimits", "/48 \u6216\u66f4\u5927");
        help.put("maxRangesPerTenancy", 20);
        help.put("freeTierSupported", false);
        help.put("validationDays", "\u6700\u957f\u7ea6 10 \u4e2a\u5de5\u4f5c\u65e5");
        help.put("steps", List.of("1. \u5728\u672c\u9762\u677f\u300c\u5bfc\u5165\u7f51\u6bb5\u300d\u521b\u5efa ByoipRange\uff0c\u590d\u5236 OCITOKEN \u9a8c\u8bc1\u4e32", "2. \u5728 RIR\uff08ARIN/RIPE/APNIC\uff09\u6dfb\u52a0\u9a8c\u8bc1 token\uff0c\u5e76\u521b\u5efa ROA \u6388\u6743 Oracle ASN", "3. \u70b9\u51fb\u300c\u5b8c\u6210\u5bfc\u5165\u6821\u9a8c\u300d(validateByoipRange)", "4. IPv4\uff1a\u521b\u5efa/\u9009\u62e9\u516c\u7f51 IP \u6c60\uff0c\u5c06 BYOIP \u5b50\u7f51\u6bb5\u52a0\u5165\u6c60", "5. \u72b6\u6001 PROVISIONED \u540e\u70b9\u51fb\u300cBGP \u5ba3\u544a\u300d(advertise)", "6. \u4ece IP \u6c60\u521b\u5efa Reserved \u516c\u7f51 IP\uff0c\u7ed1\u5b9a\u5b9e\u4f8b/LB/NAT", "7. IPv6\uff1a\u6821\u9a8c\u901a\u8fc7\u540e\u4f7f\u7528\u300c\u5206\u914d\u5230 VCN\u300d\uff0c\u518d\u5728\u5b50\u7f51/\u5b9e\u4f8b\u4e0a\u914d\u7f6e IPv6"));
        help.put("docUrl", "https://docs.oracle.com/en-us/iaas/Content/Network/Concepts/BYOIP.htm");
        return help;
    }
}

