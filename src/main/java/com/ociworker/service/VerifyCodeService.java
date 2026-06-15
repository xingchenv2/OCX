/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.RandomUtil
 *  cn.hutool.core.util.StrUtil
 *  com.ociworker.enums.SysCfgEnum
 *  com.ociworker.exception.OciException
 *  com.ociworker.service.NotificationService
 *  com.ociworker.service.VerifyCodeService
 *  com.ociworker.service.VerifyCodeService$CodeEntry
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ociworker.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ociworker.enums.SysCfgEnum;
import com.ociworker.exception.OciException;
import com.ociworker.service.NotificationService;
import com.ociworker.service.VerifyCodeService;
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
    private static final Map<String, CodeEntry> codeStore = new ConcurrentHashMap();
    @Resource
    private NotificationService notificationService;

    public boolean isTgConfigured() {
        String token = this.notificationService.getKvValue(SysCfgEnum.TG_BOT_TOKEN);
        String chatId = this.notificationService.getKvValue(SysCfgEnum.TG_CHAT_ID);
        return StrUtil.isNotBlank((CharSequence)token) && StrUtil.isNotBlank((CharSequence)chatId);
    }

    public void sendCode(String action) {
        if (!this.isTgConfigured()) {
            throw new OciException("\u672a\u7ed1\u5b9a Telegram\uff0c\u65e0\u6cd5\u6267\u884c\u6b64\u64cd\u4f5c\u3002\u8bf7\u5148\u5728\u7cfb\u7edf\u8bbe\u7f6e\u4e2d\u914d\u7f6e TG Bot\u3002");
        }
        String code = RandomUtil.randomNumbers((int)6);
        codeStore.put(action, new CodeEntry(code, System.currentTimeMillis() + 300000L));
        String actionName = switch (action) {
            case "terminate" -> "\u7ec8\u6b62\u5b9e\u4f8b";
            case "backup" -> "\u5907\u4efd\u6570\u636e";
            case "createUser" -> "\u65b0\u589e\u7528\u6237";
            case "updateUser" -> "\u4fee\u6539\u7528\u6237\u4fe1\u606f";
            case "updateUserCapabilities" -> "\u7f16\u8f91\u7528\u6237\u6743\u9650";
            case "removeFromAdmin" -> "\u79fb\u51fa\u7ba1\u7406\u5458\u7ec4";
            case "clearMfa" -> "\u6e05\u7406 MFA";
            case "disableUser" -> "\u7981\u7528\u7528\u6237";
            case "changePassword" -> "\u4fee\u6539\u767b\u5f55\u5bc6\u7801";
            case "deleteVolume" -> "\u5220\u9664\u5377";
            case "deleteStorage" -> "\u5220\u9664\u5b58\u50a8\u8d44\u6e90";
            case "editBucketPolicy" -> "\u4fee\u6539\u5b58\u50a8\u6876\u7b56\u7565";
            case "deleteVcn" -> "\u5220\u9664 VCN";
            case "deleteVcnSubnet" -> "\u5220\u9664\u5b50\u7f51";
            case "deleteVcnIgw" -> "\u5220\u9664 Internet \u7f51\u5173";
            case "deleteVcnNat" -> "\u5220\u9664 NAT \u7f51\u5173";
            case "deleteVcnSg" -> "\u5220\u9664\u670d\u52a1\u7f51\u5173";
            case "deleteVcnLpg" -> "\u5220\u9664\u672c\u5730\u5bf9\u7b49\u8fde\u63a5\u7f51\u5173";
            case "deleteVcnRt" -> "\u5220\u9664\u8def\u7531\u8868";
            case "deleteVcnSl" -> "\u5220\u9664\u5b89\u5168\u5217\u8868";
            case "deleteVcnDrg" -> "\u5220\u9664 DRG";
            case "authFactors" -> "\u4fee\u6539\u57df\u9a8c\u8bc1\u56e0\u7d20\u8bbe\u7f6e";
            case "banlist" -> "\u5c01\u7981\u5217\u8868\u7ba1\u7406";
            case "loginAudit" -> "\u767b\u5f55\u7edf\u8ba1\u67e5\u770b";
            case "deleteCompartment" -> "\u5220\u9664\u533a\u95f4";
            case "updateCompartment" -> "\u91cd\u547d\u540d\u533a\u95f4";
            case "moveCompartmentResource" -> "\u8fc1\u79fb\u533a\u95f4\u8d44\u6e90";
            case "notifyConfig" -> "\u4fee\u6539 Telegram \u901a\u77e5\u914d\u7f6e";
            case "cfZonePause" -> "Cloudflare \u6682\u505c/\u6062\u590d\u533a\u57df\u89e3\u6790";
            case "cfZoneDelete" -> "Cloudflare \u5220\u9664\u533a\u57df";
            case "cfTunnelDelete" -> "Cloudflare \u5220\u9664 Tunnel";
            case "cfWorkerDelete" -> "Cloudflare \u5220\u9664 Worker";
            case "cfEmailDestinationDelete" -> "Cloudflare \u5220\u9664\u76ee\u6807\u90ae\u7bb1";
            case "cfEmailRoutingDisable" -> "Cloudflare \u7981\u7528 Email Routing";
            case "cfEmailDnsLock" -> "Cloudflare \u9501\u5b9a Email DNS MX";
            case "cfEmailDnsUnlock" -> "Cloudflare \u89e3\u9501 Email DNS MX";
            default -> action;
        };
        String msg = String.format("\u3010OCX \u5b89\u5168\u9a8c\u8bc1\u3011\n\u64cd\u4f5c\uff1a%s\n\u9a8c\u8bc1\u7801\uff1a%s\n\u6709\u6548\u671f\uff1a5\u5206\u949f\n\n\u5982\u975e\u672c\u4eba\u64cd\u4f5c\uff0c\u8bf7\u68c0\u67e5\u8d26\u6237\u5b89\u5168\u3002", actionName, code);
        this.notificationService.sendMessage(msg);
        log.info("Verification code sent for action: {}", (Object)action);
    }

    public void verifyCode(String action, String inputCode) {
        if (!this.isTgConfigured()) {
            throw new OciException("\u672a\u7ed1\u5b9a Telegram\uff0c\u65e0\u6cd5\u6267\u884c\u6b64\u64cd\u4f5c");
        }
        CodeEntry entry = (CodeEntry)codeStore.get(action);
        if (entry == null) {
            throw new OciException("\u8bf7\u5148\u83b7\u53d6\u9a8c\u8bc1\u7801");
        }
        if (System.currentTimeMillis() > entry.expireAt()) {
            codeStore.remove(action);
            throw new OciException("\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u83b7\u53d6");
        }
        if (!entry.code().equals(inputCode)) {
            throw new OciException("\u9a8c\u8bc1\u7801\u9519\u8bef");
        }
        codeStore.remove(action);
    }
}

