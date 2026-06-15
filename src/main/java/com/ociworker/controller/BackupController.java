/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.controller.BackupController
 *  com.ociworker.model.vo.ResponseData
 *  com.ociworker.service.BackupService
 *  com.ociworker.service.VerifyCodeService
 *  jakarta.annotation.Resource
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.multipart.MultipartFile
 */
package com.ociworker.controller;

import com.ociworker.model.vo.ResponseData;
import com.ociworker.service.BackupService;
import com.ociworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value={"/api/sys/backup"})
public class BackupController {
    @Resource
    private BackupService backupService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping(value={"/create"})
    public void createBackup(@RequestParam(value="password") String password, @RequestParam(value="verifyCode") String verifyCode, HttpServletResponse response) throws IOException {
        this.verifyCodeService.verifyCode("backup", verifyCode);
        byte[] data = this.backupService.createBackup(password);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=oci-worker-backup.zip");
        response.getOutputStream().write(data);
    }

    @PostMapping(value={"/restore"})
    public ResponseData<?> restore(@RequestParam(value="file") MultipartFile file, @RequestParam(value="password") String password) throws IOException {
        this.backupService.restoreBackup(file.getBytes(), password);
        return ResponseData.ok();
    }
}

