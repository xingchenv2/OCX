package com.ocxworker.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ocxworker.enums.SysCfgEnum;
import com.ocxworker.exception.OciException;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerifyCodeService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(VerifyCodeService.class);
    private static final long CODE_EXPIRE_MS = 300000L;
    private static final Map<String, VerifyCodeService.CodeEntry> codeStore = new ConcurrentHashMap<>();
    @Resource
    private NotificationService notificationService;

    public boolean isTgConfigured() {
        String token = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String chatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        return StrUtil.isNotBlank(token) && StrUtil.isNotBlank(chatId);
    }

    public void sendCode(String action) {
        if (!this.isTgConfigured()) {
            throw new OciException("未绑定 Telegram，无法执行此操作。请先在系统设置中配置 TG Bot。");
        } else {
            String code = RandomUtil.randomNumbers(6);
            codeStore.put(action, new VerifyCodeService.CodeEntry(code, System.currentTimeMillis() + 300000L));

            String actionName = switch (action) {
                case "terminate" -> "终止实例";
                case "backup" -> "备份数据";
                case "createUser" -> "新增用户";
                case "updateUser" -> "修改用户信息";
                case "updateUserCapabilities" -> "编辑用户权限";
                case "removeFromAdmin" -> "移出管理员组";
                case "clearMfa" -> "清理 MFA";
                case "disableUser" -> "禁用用户";
                case "changePassword" -> "修改登录密码";
                case "deleteVolume" -> "删除卷";
                case "deleteStorage" -> "删除存储资源";
                case "editBucketPolicy" -> "修改存储桶策略";
                case "deleteVcn" -> "删除 VCN";
                case "deleteVcnSubnet" -> "删除子网";
                case "deleteVcnIgw" -> "删除 Internet 网关";
                case "deleteVcnNat" -> "删除 NAT 网关";
                case "deleteVcnSg" -> "删除服务网关";
                case "deleteVcnLpg" -> "删除本地对等连接网关";
                case "deleteVcnRt" -> "删除路由表";
                case "deleteVcnSl" -> "删除安全列表";
                case "deleteVcnDrg" -> "删除 DRG";
                case "authFactors" -> "修改域验证因素设置";
                case "banlist" -> "封禁列表管理";
                case "loginAudit" -> "登录统计查看";
                case "deleteCompartment" -> "删除区间";
                case "updateCompartment" -> "重命名区间";
                case "moveCompartmentResource" -> "迁移区间资源";
                case "notifyConfig" -> "修改 Telegram 通知配置";
                case "cfZonePause" -> "Cloudflare 暂停/恢复区域解析";
                case "cfZoneDelete" -> "Cloudflare 删除区域";
                case "cfTunnelDelete" -> "Cloudflare 删除 Tunnel";
                case "cfWorkerDelete" -> "Cloudflare 删除 Worker";
                case "cfEmailDestinationDelete" -> "Cloudflare 删除目标邮箱";
                case "cfEmailRoutingDisable" -> "Cloudflare 禁用 Email Routing";
                case "cfEmailDnsLock" -> "Cloudflare 锁定 Email DNS MX";
                case "cfEmailDnsUnlock" -> "Cloudflare 解锁 Email DNS MX";
                default -> action;
            };
            String msg = String.format("【OCI Worker 安全验证】\n操作：%s\n验证码：%s\n有效期：5分钟\n\n如非本人操作，请检查账户安全。", actionName, code);
            this.notificationService.sendMessage(msg);
            log.info("Verification code sent for action: {}", action);
        }
    }

    public void verifyCode(String action, String inputCode) {
        if (!this.isTgConfigured()) {
            throw new OciException("未绑定 Telegram，无法执行此操作");
        } else {
            VerifyCodeService.CodeEntry entry = codeStore.get(action);
            if (entry == null) {
                throw new OciException("请先获取验证码");
            } else if (System.currentTimeMillis() > entry.expireAt()) {
                codeStore.remove(action);
                throw new OciException("验证码已过期，请重新获取");
            } else if (!entry.code().equals(inputCode)) {
                throw new OciException("验证码错误");
            } else {
                codeStore.remove(action);
            }
        }
    }

    private static record CodeEntry(String code, long expireAt) {
    }
}
