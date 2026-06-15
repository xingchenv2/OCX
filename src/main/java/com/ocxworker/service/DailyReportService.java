/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.ocxworker.enums.SysCfgEnum
 *  com.ocxworker.enums.TaskStatusEnum
 *  com.ocxworker.mapper.OciCreateTaskMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciCreateTask
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.DailyReportService
 *  com.ocxworker.service.NotificationService
 *  com.ocxworker.service.OciClientService
 *  com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.enums.TaskStatusEnum;
import com.ocxworker.mapper.OciCreateTaskMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciCreateTask;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.NotificationService;
import com.ocxworker.service.OciClientService;
import com.oracle.bmc.identity.requests.ListRegionSubscriptionsRequest;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class DailyReportService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DailyReportService.class);
    private static final ZoneId DAILY_REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private final AtomicReference<LocalDate> lastDailyReportDate = new AtomicReference();
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private NotificationService notificationService;

    @Scheduled(cron="0 * * * * ?")
    public void tickDailyReport() {
        if (!this.notificationService.isNotifyTypeEnabled("daily_report")) {
            return;
        }
        int[] hm = DailyReportService.parseDailyTime((String)this.notificationService.getKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME));
        ZonedDateTime now = ZonedDateTime.now(DAILY_REPORT_ZONE);
        if (now.getHour() != hm[0] || now.getMinute() != hm[1]) {
            return;
        }
        LocalDate today = now.toLocalDate();
        if (this.lastDailyReportDate.get() != null && ((LocalDate)this.lastDailyReportDate.get()).equals(today)) {
            return;
        }
        try {
            this.sendDailyReport();
            this.lastDailyReportDate.set(today);
        }
        catch (Exception e) {
            log.error("Failed to send daily report: {}", (Object)e.getMessage());
        }
    }

    private static int[] parseDailyTime(String s) {
        if (s == null) {
            return new int[]{9, 0};
        }
        if (!(s = s.trim()).matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            return new int[]{9, 0};
        }
        String[] p = s.split(":");
        return new int[]{Integer.parseInt(p[0], 10), Integer.parseInt(p[1], 10)};
    }

    public void sendDailyReport() {
        try {
            List allUsers = this.userMapper.selectList(null);
            int total = allUsers.size();
            ArrayList<String> invalidNames = new ArrayList<String>();
            for (OciUser user : allUsers) {
                try {
                    SysUserDTO dto = SysUserDTO.builder().username(user.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(user.getOciTenantId()).userId(user.getOciUserId()).fingerprint(user.getOciFingerprint()).region(user.getOciRegion()).privateKeyPath(user.getOciKeyPath()).build()).build();
                    try (OciClientService client = new OciClientService(dto);){
                        client.getIdentityClient().listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(client.getCompartmentId()).build());
                    }
                }
                catch (Exception e) {
                    invalidNames.add(user.getUsername());
                }
            }
            long runningTasks = this.taskMapper.selectCount((Wrapper)new LambdaQueryWrapper().eq(OciCreateTask::getStatus, (Object)TaskStatusEnum.RUNNING.getStatus()));
            StringBuilder sb = new StringBuilder();
            sb.append("\u3010\u6bcf\u65e5\u64ad\u62a5\u3011\ud83d\udcca \u7cfb\u7edf\u65e5\u62a5\n");
            sb.append(String.format("\u79df\u6237\u603b\u6570: %d\n", total));
            sb.append(String.format("\u5931\u6548\u79df\u6237: %d\n", invalidNames.size()));
            sb.append(String.format("\u8fd0\u884c\u4e2d\u4efb\u52a1: %d\n", runningTasks));
            if (!invalidNames.isEmpty()) {
                sb.append("\u5931\u6548\u79df\u6237\u5217\u8868: ").append(String.join((CharSequence)", ", invalidNames));
            } else {
                sb.append("\u6240\u6709\u79df\u6237\u914d\u7f6e\u6b63\u5e38 \u2705");
            }
            this.notificationService.sendMessage("daily_report", sb.toString());
            log.info("Daily report sent");
        }
        catch (Exception e) {
            log.error("Failed to send daily report: {}", (Object)e.getMessage());
        }
    }
}

