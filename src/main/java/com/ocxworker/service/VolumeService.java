package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.core.model.BootVolume;
import com.oracle.bmc.core.model.BootVolumeBackup;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.model.VolumeBackup;
import com.oracle.bmc.core.model.BootVolume.LifecycleState;
import com.oracle.bmc.core.requests.DeleteBootVolumeBackupRequest;
import com.oracle.bmc.core.requests.DeleteBootVolumeRequest;
import com.oracle.bmc.core.requests.DeleteVolumeBackupRequest;
import com.oracle.bmc.core.requests.DeleteVolumeRequest;
import com.oracle.bmc.core.requests.ListBootVolumeBackupsRequest;
import com.oracle.bmc.core.requests.ListBootVolumesRequest;
import com.oracle.bmc.core.requests.ListVolumeBackupsRequest;
import com.oracle.bmc.core.requests.ListVolumesRequest;
import com.oracle.bmc.core.responses.ListBootVolumesResponse;
import com.oracle.bmc.core.responses.ListVolumesResponse;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.model.BmcException;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VolumeService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(VolumeService.class);
    @Resource
    private OciUserMapper userMapper;

    public List<Map<String, Object>> listAllVolumes(String userId) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            SysUserDTO dto = this.buildBasicDTO(ociUser);

            try {
                Object var27;
                try (OciClientService client = new OciClientService(dto)) {
                    List<Compartment> compartments = client.listAllCompartments();
                    List<String> ads = client.getAvailabilityDomains()
                        .stream()
                        .map(adx -> adx.getName())
                        .filter(n -> n != null && !n.isBlank())
                        .distinct()
                        .toList();
                    List<Map<String, Object>> result = new ArrayList<>();

                    for (Compartment compartment : compartments) {
                        String cid = compartment.getId();
                        Set<String> seenBoot = new HashSet<>();

                        for (String ad : ads) {
                            try {
                                String page = null;

                                while (true) {
                                    ListBootVolumesResponse bootResp = client.getBlockstorageClient()
                                        .listBootVolumes(ListBootVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                                    for (BootVolume bv : bootResp.getItems()) {
                                        if (bv.getLifecycleState() != LifecycleState.Terminated && seenBoot.add(bv.getId())) {
                                            result.add(
                                                this.volumeMap(
                                                    "BOOT",
                                                    bv.getId(),
                                                    bv.getDisplayName(),
                                                    bv.getSizeInGBs(),
                                                    bv.getLifecycleState().getValue(),
                                                    bv.getTimeCreated() != null ? bv.getTimeCreated().toString() : null,
                                                    null
                                                )
                                            );
                                        }
                                    }

                                    page = bootResp.getOpcNextPage();
                                    if (page == null) {
                                        break;
                                    }
                                }
                            } catch (Exception var20) {
                                log.debug("listBootVolumes in {} AD {} failed: {}", new Object[]{cid, ad, var20.getMessage()});
                            }
                        }

                        Set<String> seenBlock = new HashSet<>();

                        for (String ad : ads) {
                            try {
                                String page = null;

                                while (true) {
                                    ListVolumesResponse volResp = client.getBlockstorageClient()
                                        .listVolumes(ListVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad).page(page).build());

                                    for (Volume v : volResp.getItems()) {
                                        if (v.getLifecycleState() != com.oracle.bmc.core.model.Volume.LifecycleState.Terminated && seenBlock.add(v.getId())) {
                                            result.add(
                                                this.volumeMap(
                                                    "BLOCK",
                                                    v.getId(),
                                                    v.getDisplayName(),
                                                    v.getSizeInGBs(),
                                                    v.getLifecycleState().getValue(),
                                                    v.getTimeCreated() != null ? v.getTimeCreated().toString() : null,
                                                    null
                                                )
                                            );
                                        }
                                    }

                                    page = volResp.getOpcNextPage();
                                    if (page == null) {
                                        break;
                                    }
                                }
                            } catch (Exception var21) {
                                log.debug("listVolumes in {} AD {} failed: {}", new Object[]{cid, ad, var21.getMessage()});
                            }
                        }

                        try {
                            for (BootVolumeBackup b : client.getBlockstorageClient()
                                .listBootVolumeBackups(ListBootVolumeBackupsRequest.builder().compartmentId(cid).build())
                                .getItems()) {
                                if (b.getLifecycleState() != com.oracle.bmc.core.model.BootVolumeBackup.LifecycleState.Terminated) {
                                    result.add(
                                        this.volumeMap(
                                            "BOOT_BACKUP",
                                            b.getId(),
                                            b.getDisplayName(),
                                            b.getSizeInGBs(),
                                            b.getLifecycleState().getValue(),
                                            b.getTimeCreated() != null ? b.getTimeCreated().toString() : null,
                                            b.getBootVolumeId()
                                        )
                                    );
                                }
                            }
                        } catch (Exception var22) {
                            log.debug("listBootVolumeBackups in {} failed: {}", cid, var22.getMessage());
                        }

                        try {
                            for (VolumeBackup bx : client.getBlockstorageClient()
                                .listVolumeBackups(ListVolumeBackupsRequest.builder().compartmentId(cid).build())
                                .getItems()) {
                                if (bx.getLifecycleState() != com.oracle.bmc.core.model.VolumeBackup.LifecycleState.Terminated) {
                                    result.add(
                                        this.volumeMap(
                                            "BLOCK_BACKUP",
                                            bx.getId(),
                                            bx.getDisplayName(),
                                            bx.getSizeInGBs(),
                                            bx.getLifecycleState().getValue(),
                                            bx.getTimeCreated() != null ? bx.getTimeCreated().toString() : null,
                                            bx.getVolumeId()
                                        )
                                    );
                                }
                            }
                        } catch (Exception var23) {
                            log.debug("listVolumeBackups in {} failed: {}", cid, var23.getMessage());
                        }
                    }

                    var27 = result;
                }

                return (List<Map<String, Object>>)var27;
            } catch (OciException var25) {
                throw var25;
            } catch (Exception var26) {
                throw new OciException("查询卷列表失败: " + var26.getMessage());
            }
        }
    }

    public void deleteVolume(String userId, String type, String volumeId) {
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            SysUserDTO dto = this.buildBasicDTO(ociUser);

            try {
                try (OciClientService client = new OciClientService(dto)) {
                    switch (type) {
                        case "BOOT":
                            client.getBlockstorageClient().deleteBootVolume(DeleteBootVolumeRequest.builder().bootVolumeId(volumeId).build());
                            break;
                        case "BLOCK":
                            client.getBlockstorageClient().deleteVolume(DeleteVolumeRequest.builder().volumeId(volumeId).build());
                            break;
                        case "BOOT_BACKUP":
                            client.getBlockstorageClient().deleteBootVolumeBackup(DeleteBootVolumeBackupRequest.builder().bootVolumeBackupId(volumeId).build());
                            break;
                        case "BLOCK_BACKUP":
                            client.getBlockstorageClient().deleteVolumeBackup(DeleteVolumeBackupRequest.builder().volumeBackupId(volumeId).build());
                            break;
                        default:
                            throw new OciException("未知卷类型: " + type);
                    }

                    log.info("Volume deleted: type={}, id={}", type, volumeId);
                }
            } catch (OciException var11) {
                throw var11;
            } catch (BmcException var12) {
                if (var12.getStatusCode() == 409) {
                    throw new OciException("该卷当前状态不允许删除（可能正在使用或复制中），请稍后再试");
                } else {
                    throw new OciException("删除卷失败: " + (var12.getMessage() != null ? var12.getMessage() : "未知错误"));
                }
            } catch (Exception var13) {
                throw new OciException("删除卷失败: " + var13.getMessage());
            }
        }
    }

    private Map<String, Object> volumeMap(String type, String id, String displayName, Long sizeInGBs, String state, String timeCreated, String sourceId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", type);
        map.put("id", id);
        map.put("displayName", displayName);
        map.put("sizeInGBs", sizeInGBs);
        map.put("lifecycleState", state);
        map.put("timeCreated", timeCreated);
        if (sourceId != null) {
            map.put("sourceId", sourceId);
        }

        return map;
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
