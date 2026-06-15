/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.VolumeController
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.VerifyCodeService
 *  com.ociworker.service.VolumeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ociworker.controller;

import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.VerifyCodeService;
import com.ociworker.service.VolumeService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/oci/volume"})
public class VolumeController {
    @Resource
    private VolumeService volumeService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/list"})
    public ResponseData<?> list(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.volumeService.listAllVolumes(params.get("id")));
    }

    @PostMapping(value={"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("deleteVolume", params.get("verifyCode"));
        this.volumeService.deleteVolume(params.get("id"), params.get("type"), params.get("volumeId"));
        return ResponseData.ok();
    }
}

