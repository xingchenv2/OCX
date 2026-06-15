/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.controller.VolumeController
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.VerifyCodeService
 *  com.ocxworker.service.VolumeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.VerifyCodeService;
import com.ocxworker.service.VolumeService;
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

