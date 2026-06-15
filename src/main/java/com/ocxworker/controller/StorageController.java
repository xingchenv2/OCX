/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.controller.StorageController
 *  com.ocxworker.model.vo.ResponseData
 *  com.ocxworker.service.StorageService
 *  com.ocxworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.StorageService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/oci/storage"})
public class StorageController {
    @Resource
    private StorageService storageService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/regions"})
    public ResponseData<?> regions(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.storageService.listSubscribedRegionIds(params.get("id")));
    }

    @PostMapping(value={"/compartments"})
    public ResponseData<?> compartments(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.storageService.listCompartments(params.get("id"), params.get("region")));
    }

    @PostMapping(value={"/block/aggregate"})
    public ResponseData<?> blockAggregate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.storageService.blockAggregate(params.get("id"), params.get("region"), params.get("compartmentId"), params.get("sections")));
    }

    @PostMapping(value={"/object/aggregate"})
    public ResponseData<?> objectAggregate(@RequestBody Map<String, String> params) {
        return ResponseData.ok((Object)this.storageService.objectAggregate(params.get("id"), params.get("region"), params.get("compartmentId")));
    }

    @PostMapping(value={"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("deleteStorage", params.get("verifyCode"));
        this.storageService.deleteResource(params.get("id"), params.get("region"), params.get("resourceType"), params.get("resourceId"), params.get("namespace"), params.get("bucketName"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/object/bucketPolicy"})
    public ResponseData<?> putBucketPolicy(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("editBucketPolicy", params.get("verifyCode"));
        this.storageService.putBucketPolicy(params.get("id"), params.get("region"), params.get("namespace"), params.get("bucketName"), params.get("policy"));
        return ResponseData.ok();
    }

    @PostMapping(value={"/mutate"})
    public ResponseData<?> mutate(@RequestBody Map<String, Object> params) {
        return ResponseData.ok((Object)this.storageService.mutate(params));
    }
}

