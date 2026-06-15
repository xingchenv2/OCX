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

@Service
public class DailyReportService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DailyReportService.class);
    private static final ZoneId DAILY_REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private final AtomicReference<LocalDate> lastDailyReportDate = new AtomicReference<>();
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private OciCreateTaskMapper taskMapper;
    @Resource
    private NotificationService notificationService;

    @Scheduled(
        cron = "0 * * * * ?"
    )
    public void tickDailyReport() {
        if (this.notificationService.isNotifyTypeEnabled("daily_report")) {
            int[] hm = parseDailyTime(this.notificationService.getKvValue(SysCfgEnum.TG_DAILY_REPORT_TIME));
            ZonedDateTime now = ZonedDateTime.now(DAILY_REPORT_ZONE);
            if (now.getHour() == hm[0] && now.getMinute() == hm[1]) {
                LocalDate today = now.toLocalDate();
                if (this.lastDailyReportDate.get() == null || !this.lastDailyReportDate.get().equals(today)) {
                    try {
                        this.sendDailyReport();
                        this.lastDailyReportDate.set(today);
                    } catch (Exception var5) {
                        log.error("Failed to send daily report: {}", var5.getMessage());
                    }
                }
            }
        }
    }

    private static int[] parseDailyTime(String s) {
        if (s == null) {
            return new int[]{9, 0};
        } else {
            s = s.trim();
            if (!s.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                return new int[]{9, 0};
            } else {
                String[] p = s.split(":");
                return new int[]{Integer.parseInt(p[0], 10), Integer.parseInt(p[1], 10)};
            }
        }
    }

    public void sendDailyReport() {
        try {
            List<OciUser> allUsers = this.userMapper.selectList(null);
            int total = allUsers.size();
            List<String> invalidNames = new ArrayList<>();

            for (OciUser user : allUsers) {
                try {
                    SysUserDTO dto = SysUserDTO.builder()
                        .username(user.getUsername())
                        .ociCfg(
                            SysUserDTO.OciCfg.builder()
                                .tenantId(user.getOciTenantId())
                                .userId(user.getOciUserId())
                                .fingerprint(user.getOciFingerprint())
                                .region(user.getOciRegion())
                                .privateKeyPath(user.getOciKeyPath())
                                .build()
                        )
                        .build();

                    try (OciClientService client = new OciClientService(dto)) {
                        client.getIdentityClient()
                            .listRegionSubscriptions(ListRegionSubscriptionsRequest.builder().tenancyId(client.getCompartmentId()).build());
                    }
                } catch (Exception var12) {
                    invalidNames.add(user.getUsername());
                }
            }

            long runningTasks = this.taskMapper.selectCount((Wrapper)new LambdaQueryWrapper<OciCreateTask>().eq(OciCreateTask::getStatus, TaskStatusEnum.RUNNING.getStatus()));
            StringBuilder sb = new StringBuilder();
            sb.append("【每日播报】\ud83d\udcca 系统日报\n");
            sb.append(String.format("租户总数: %d\n", total));
            sb.append(String.format("失效租户: %d\n", invalidNames.size()));
            sb.append(String.format("运行中任务: %d\n", runningTasks));
            if (!invalidNames.isEmpty()) {
                sb.append("失效租户列表: ").append(String.join(", ", invalidNames));
            } else {
                sb.append("所有租户配置正常 ✅");
            }

            this.notificationService.sendMessage("daily_report", sb.toString());
            log.info("Daily report sent");
        } catch (Exception var13) {
            log.error("Failed to send daily report: {}", var13.getMessage());
        }
    }
}
