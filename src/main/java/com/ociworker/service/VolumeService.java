/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.dto.SysUserDTO
 *  com.ociworker.model.dto.SysUserDTO$OciCfg
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.OciClientService
 *  com.ociworker.service.VolumeService
 *  com.oracle.bmc.core.model.BootVolume
 *  com.oracle.bmc.core.model.BootVolume$LifecycleState
 *  com.oracle.bmc.core.model.BootVolumeBackup
 *  com.oracle.bmc.core.model.BootVolumeBackup$LifecycleState
 *  com.oracle.bmc.core.model.Volume
 *  com.oracle.bmc.core.model.Volume$LifecycleState
 *  com.oracle.bmc.core.model.VolumeBackup$LifecycleState
 *  com.oracle.bmc.core.requests.DeleteBootVolumeBackupRequest
 *  com.oracle.bmc.core.requests.DeleteBootVolumeRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeBackupRequest
 *  com.oracle.bmc.core.requests.DeleteVolumeRequest
 *  com.oracle.bmc.core.requests.ListBootVolumeBackupsRequest
 *  com.oracle.bmc.core.requests.ListBootVolumesRequest
 *  com.oracle.bmc.core.requests.ListVolumeBackupsRequest
 *  com.oracle.bmc.core.requests.ListVolumesRequest
 *  com.oracle.bmc.core.responses.ListBootVolumesResponse
 *  com.oracle.bmc.core.responses.ListVolumesResponse
 *  com.oracle.bmc.identity.model.Compartment
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
import com.oracle.bmc.core.model.BootVolume;
import com.oracle.bmc.core.model.BootVolumeBackup;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.model.VolumeBackup;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        ArrayList<Map> arrayList;
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        SysUserDTO dto = this.buildBasicDTO(ociUser);
        OciClientService client = new OciClientService(dto);
        try {
            List compartments = client.listAllCompartments();
            List<String> ads = client.getAvailabilityDomains().stream().map(ad -> ad.getName()).filter(n -> n != null && !n.isBlank()).distinct().toList();
            ArrayList<Map> result = new ArrayList<Map>();
            for (Compartment compartment : compartments) {
                String cid = compartment.getId();
                HashSet<String> seenBoot = new HashSet<String>();
                for (String string : ads) {
                    try {
                        ListBootVolumesResponse bootResp;
                        String page = null;
                        do {
                            bootResp = client.getBlockstorageClient().listBootVolumes(ListBootVolumesRequest.builder().compartmentId(cid).availabilityDomain(string).page(page).build());
                            for (BootVolume bv : bootResp.getItems()) {
                                if (bv.getLifecycleState() == BootVolume.LifecycleState.Terminated || !seenBoot.add(bv.getId())) continue;
                                result.add(this.volumeMap("BOOT", bv.getId(), bv.getDisplayName(), bv.getSizeInGBs(), bv.getLifecycleState().getValue(), bv.getTimeCreated() != null ? bv.getTimeCreated().toString() : null, null));
                            }
                        } while ((page = bootResp.getOpcNextPage()) != null);
                    }
                    catch (Exception e) {
                        log.debug("listBootVolumes in {} AD {} failed: {}", new Object[]{cid, string, e.getMessage()});
                    }
                }
                HashSet<String> seenBlock = new HashSet<String>();
                for (String ad3 : ads) {
                    try {
                        ListVolumesResponse volResp;
                        String page = null;
                        do {
                            volResp = client.getBlockstorageClient().listVolumes(ListVolumesRequest.builder().compartmentId(cid).availabilityDomain(ad3).page(page).build());
                            for (Volume v : volResp.getItems()) {
                                if (v.getLifecycleState() == Volume.LifecycleState.Terminated || !seenBlock.add(v.getId())) continue;
                                result.add(this.volumeMap("BLOCK", v.getId(), v.getDisplayName(), v.getSizeInGBs(), v.getLifecycleState().getValue(), v.getTimeCreated() != null ? v.getTimeCreated().toString() : null, null));
                            }
                        } while ((page = volResp.getOpcNextPage()) != null);
                    }
                    catch (Exception e) {
                        log.debug("listVolumes in {} AD {} failed: {}", new Object[]{cid, ad3, e.getMessage()});
                    }
                }
                try {
                    List list = client.getBlockstorageClient().listBootVolumeBackups(ListBootVolumeBackupsRequest.builder().compartmentId(cid).build()).getItems();
                    for (BootVolumeBackup b : list) {
                        if (b.getLifecycleState() == BootVolumeBackup.LifecycleState.Terminated) continue;
                        result.add(this.volumeMap("BOOT_BACKUP", b.getId(), b.getDisplayName(), b.getSizeInGBs(), b.getLifecycleState().getValue(), b.getTimeCreated() != null ? b.getTimeCreated().toString() : null, b.getBootVolumeId()));
                    }
                }
                catch (Exception exception) {
                    log.debug("listBootVolumeBackups in {} failed: {}", (Object)cid, (Object)exception.getMessage());
                }
                try {
                    List list = client.getBlockstorageClient().listVolumeBackups(ListVolumeBackupsRequest.builder().compartmentId(cid).build()).getItems();
                    for (BootVolumeBackup b : list) {
                        if (b.getLifecycleState() == VolumeBackup.LifecycleState.Terminated) continue;
                        result.add(this.volumeMap("BLOCK_BACKUP", b.getId(), b.getDisplayName(), b.getSizeInGBs(), b.getLifecycleState().getValue(), b.getTimeCreated() != null ? b.getTimeCreated().toString() : null, b.getVolumeId()));
                    }
                }
                catch (Exception exception) {
                    log.debug("listVolumeBackups in {} failed: {}", (Object)cid, (Object)exception.getMessage());
                }
            }
            arrayList = result;
        }
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (OciException e) {
                throw e;
            }
            catch (Exception e) {
                throw new OciException("\u67e5\u8be2\u5377\u5217\u8868\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return arrayList;
    }

    public void deleteVolume(String userId, String type, String volumeId) {
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        SysUserDTO dto = this.buildBasicDTO(ociUser);
        try (OciClientService client = new OciClientService(dto);){
            switch (type) {
                case "BOOT": {
                    client.getBlockstorageClient().deleteBootVolume(DeleteBootVolumeRequest.builder().bootVolumeId(volumeId).build());
                    break;
                }
                case "BLOCK": {
                    client.getBlockstorageClient().deleteVolume(DeleteVolumeRequest.builder().volumeId(volumeId).build());
                    break;
                }
                case "BOOT_BACKUP": {
                    client.getBlockstorageClient().deleteBootVolumeBackup(DeleteBootVolumeBackupRequest.builder().bootVolumeBackupId(volumeId).build());
                    break;
                }
                case "BLOCK_BACKUP": {
                    client.getBlockstorageClient().deleteVolumeBackup(DeleteVolumeBackupRequest.builder().volumeBackupId(volumeId).build());
                    break;
                }
                default: {
                    throw new OciException("\u672a\u77e5\u5377\u7c7b\u578b: " + type);
                }
            }
            log.info("Volume deleted: type={}, id={}", (Object)type, (Object)volumeId);
        }
        catch (OciException e) {
            throw e;
        }
        catch (BmcException e) {
            if (e.getStatusCode() == 409) {
                throw new OciException("\u8be5\u5377\u5f53\u524d\u72b6\u6001\u4e0d\u5141\u8bb8\u5220\u9664\uff08\u53ef\u80fd\u6b63\u5728\u4f7f\u7528\u6216\u590d\u5236\u4e2d\uff09\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5");
            }
            throw new OciException("\u5220\u9664\u5377\u5931\u8d25: " + (e.getMessage() != null ? e.getMessage() : "\u672a\u77e5\u9519\u8bef"));
        }
        catch (Exception e) {
            throw new OciException("\u5220\u9664\u5377\u5931\u8d25: " + e.getMessage());
        }
    }

    private Map<String, Object> volumeMap(String type, String id, String displayName, Long sizeInGBs, String state, String timeCreated, String sourceId) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
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
        return SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
    }
}

