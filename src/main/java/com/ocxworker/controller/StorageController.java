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
@RequestMapping({"/api/oci/storage"})
public class StorageController {
    @Resource
    private StorageService storageService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping({"/regions"})
    public ResponseData<?> regions(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.storageService.listSubscribedRegionIds(params.get("id")));
    }

    @PostMapping({"/compartments"})
    public ResponseData<?> compartments(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.storageService.listCompartments(params.get("id"), params.get("region")));
    }

    @PostMapping({"/block/aggregate"})
    public ResponseData<?> blockAggregate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.storageService.blockAggregate(params.get("id"), params.get("region"), params.get("compartmentId"), params.get("sections")));
    }

    @PostMapping({"/object/aggregate"})
    public ResponseData<?> objectAggregate(@RequestBody Map<String, String> params) {
        return ResponseData.ok(this.storageService.objectAggregate(params.get("id"), params.get("region"), params.get("compartmentId")));
    }

    @PostMapping({"/delete"})
    public ResponseData<?> delete(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("deleteStorage", params.get("verifyCode"));
        this.storageService
            .deleteResource(
                params.get("id"), params.get("region"), params.get("resourceType"), params.get("resourceId"), params.get("namespace"), params.get("bucketName")
            );
        return ResponseData.ok();
    }

    @PostMapping({"/object/bucketPolicy"})
    public ResponseData<?> putBucketPolicy(@RequestBody Map<String, String> params) {
        this.verifyCodeService.verifyCode("editBucketPolicy", params.get("verifyCode"));
        this.storageService.putBucketPolicy(params.get("id"), params.get("region"), params.get("namespace"), params.get("bucketName"), params.get("policy"));
        return ResponseData.ok();
    }

    @PostMapping({"/mutate"})
    public ResponseData<?> mutate(@RequestBody Map<String, Object> params) {
        return ResponseData.ok(this.storageService.mutate(params));
    }
}
