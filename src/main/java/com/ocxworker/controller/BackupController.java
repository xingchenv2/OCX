package com.ocxworker.controller;

import com.ocxworker.model.vo.ResponseData;
import com.ocxworker.service.BackupService;
import com.ocxworker.service.VerifyCodeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/sys/backup"})
public class BackupController {
    @Resource
    private BackupService backupService;
    @Resource
    private VerifyCodeService verifyCodeService;

    @PostMapping({"/create"})
    public void createBackup(@RequestParam("password") String password, @RequestParam("verifyCode") String verifyCode, HttpServletResponse response) throws IOException {
        this.verifyCodeService.verifyCode("backup", verifyCode);
        byte[] data = this.backupService.createBackup(password);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=ocx-worker-backup.zip");
        response.getOutputStream().write(data);
    }

    @PostMapping({"/restore"})
    public ResponseData<?> restore(@RequestParam("file") MultipartFile file, @RequestParam("password") String password) throws IOException {
        this.backupService.restoreBackup(file.getBytes(), password);
        return ResponseData.ok();
    }
}
