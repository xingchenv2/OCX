/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.ociworker.exception.OciException
 *  com.ociworker.mapper.OciOpenaiKeyMapper
 *  com.ociworker.mapper.OciOpenaiPortBindingMapper
 *  com.ociworker.mapper.OciUserMapper
 *  com.ociworker.model.entity.OciOpenaiKey
 *  com.ociworker.model.entity.OciOpenaiPortBinding
 *  com.ociworker.model.entity.OciUser
 *  com.ociworker.service.DynamicOpenAiPortService
 *  com.ociworker.service.OracleAiGatewayConfigService
 *  com.ociworker.service.OracleAiPortBindingService
 *  com.ociworker.util.CommonUtils
 *  jakarta.annotation.Resource
 *  org.springframework.boot.web.context.WebServerInitializedEvent
 *  org.springframework.context.event.EventListener
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.ociworker.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ociworker.exception.OciException;
import com.ociworker.mapper.OciOpenaiKeyMapper;
import com.ociworker.mapper.OciOpenaiPortBindingMapper;
import com.ociworker.mapper.OciUserMapper;
import com.ociworker.model.entity.OciOpenaiKey;
import com.ociworker.model.entity.OciOpenaiPortBinding;
import com.ociworker.model.entity.OciUser;
import com.ociworker.service.DynamicOpenAiPortService;
import com.ociworker.service.OracleAiGatewayConfigService;
import com.ociworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Exception performing whole class analysis ignored.
 */
@Service
public class OracleAiPortBindingService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource
    private OciOpenaiPortBindingMapper bindingMapper;
    @Resource
    private OciOpenaiKeyMapper keyMapper;
    @Resource
    private OciUserMapper userMapper;
    @Resource
    private DynamicOpenAiPortService dynamicPortService;

    @EventListener
    @Order(value=-2147483638)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        this.syncEnabledConnectors();
    }

    public List<OciOpenaiPortBinding> list() {
        return this.bindingMapper.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().orderByAsc(OciOpenaiPortBinding::getPort)).orderByDesc(OciOpenaiPortBinding::getCreateTime));
    }

    public OciOpenaiPortBinding getByPort(int port) {
        if (!DynamicOpenAiPortService.isManagedPort((int)port)) {
            return null;
        }
        return (OciOpenaiPortBinding)this.bindingMapper.selectOne((Wrapper)new LambdaQueryWrapper().eq(OciOpenaiPortBinding::getPort, (Object)port));
    }

    @Transactional(rollbackFor={Exception.class})
    public OciOpenaiPortBinding create(String name, int port, String ociUserId, String ociRegion, String openaiKeyId, Integer defaultMaxTokens, List<String> allowedModels, boolean enabled) {
        DynamicOpenAiPortService.validateManagedPort((int)port);
        OciUser user = this.validateTenantAndKey(ociUserId, openaiKeyId);
        OciOpenaiPortBinding existing = this.getByPort(port);
        if (existing != null) {
            throw new OciException("\u7aef\u53e3\u5df2\u7ed1\u5b9a: " + port);
        }
        OciOpenaiPortBinding row = new OciOpenaiPortBinding();
        row.setId(CommonUtils.generateId());
        row.setName(OracleAiPortBindingService.trimName((String)name));
        row.setPort(Integer.valueOf(port));
        row.setOciUserId(ociUserId);
        row.setOciRegion(OracleAiPortBindingService.normalizeRegion((String)ociRegion, (OciUser)user));
        row.setOpenaiKeyId(openaiKeyId);
        row.setDefaultMaxTokens(OracleAiPortBindingService.normalizeDefaultMaxTokens((Integer)defaultMaxTokens));
        row.setAllowedModelsJson(OracleAiPortBindingService.encodeAllowedModels(allowedModels));
        row.setEnabled(Integer.valueOf(enabled ? 1 : 0));
        row.setStatus("stopped");
        row.setCreateTime(LocalDateTime.now());
        row.setUpdateTime(LocalDateTime.now());
        this.bindingMapper.insert((Object)row);
        if (enabled) {
            this.startAndMark(row);
        }
        return (OciOpenaiPortBinding)this.bindingMapper.selectById((Serializable)((Object)row.getId()));
    }

    @Transactional(rollbackFor={Exception.class})
    public OciOpenaiPortBinding update(String id, String name, int port, String ociUserId, String ociRegion, String openaiKeyId, Integer defaultMaxTokens, List<String> allowedModels, boolean enabled) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById((Serializable)((Object)id));
        if (row == null) {
            throw new OciException("\u7ed1\u5b9a\u4e0d\u5b58\u5728");
        }
        DynamicOpenAiPortService.validateManagedPort((int)port);
        OciUser user = this.validateTenantAndKey(ociUserId, openaiKeyId);
        OciOpenaiPortBinding samePort = this.getByPort(port);
        if (samePort != null && !samePort.getId().equals(id)) {
            throw new OciException("\u7aef\u53e3\u5df2\u7ed1\u5b9a: " + port);
        }
        int oldPort = row.getPort() == null ? -1 : row.getPort();
        row.setName(OracleAiPortBindingService.trimName((String)name));
        row.setPort(Integer.valueOf(port));
        row.setOciUserId(ociUserId);
        row.setOciRegion(OracleAiPortBindingService.normalizeRegion((String)ociRegion, (OciUser)user));
        row.setOpenaiKeyId(openaiKeyId);
        row.setDefaultMaxTokens(OracleAiPortBindingService.normalizeDefaultMaxTokens((Integer)defaultMaxTokens));
        row.setAllowedModelsJson(OracleAiPortBindingService.encodeAllowedModels(allowedModels));
        row.setEnabled(Integer.valueOf(enabled ? 1 : 0));
        row.setUpdateTime(LocalDateTime.now());
        this.bindingMapper.updateById((Object)row);
        if (enabled) {
            this.startAndMark(row);
            if (oldPort != port) {
                this.dynamicPortService.stopPort(oldPort);
            }
        } else {
            this.dynamicPortService.stopPort(port);
            if (oldPort != port) {
                this.dynamicPortService.stopPort(oldPort);
            }
            this.markStatus(id, "disabled", null);
        }
        return (OciOpenaiPortBinding)this.bindingMapper.selectById((Serializable)((Object)id));
    }

    @Transactional(rollbackFor={Exception.class})
    public void setEnabled(String id, boolean enabled) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById((Serializable)((Object)id));
        if (row == null) {
            return;
        }
        row.setEnabled(Integer.valueOf(enabled ? 1 : 0));
        row.setUpdateTime(LocalDateTime.now());
        this.bindingMapper.updateById((Object)row);
        if (enabled) {
            this.startAndMark(row);
        } else {
            this.dynamicPortService.stopPort(row.getPort().intValue());
            this.markStatus(row.getId(), "disabled", null);
        }
    }

    @Transactional(rollbackFor={Exception.class})
    public void remove(String id) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById((Serializable)((Object)id));
        if (row != null && row.getPort() != null) {
            this.dynamicPortService.stopPort(row.getPort().intValue());
        }
        this.bindingMapper.deleteById((Serializable)((Object)id));
    }

    public void syncEnabledConnectors() {
        List rows = this.bindingMapper.selectList((Wrapper)new LambdaQueryWrapper().eq(OciOpenaiPortBinding::getEnabled, (Object)1));
        for (OciOpenaiPortBinding row : rows) {
            try {
                this.startAndMark(row);
            }
            catch (Exception exception) {}
        }
    }

    public void touchLastUsed(String id) {
        if (id == null) {
            return;
        }
        this.bindingMapper.update(null, (Wrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper().set(OciOpenaiPortBinding::getLastUsed, (Object)LocalDateTime.now())).eq(OciOpenaiPortBinding::getId, (Object)id));
    }

    public void markStatus(String id, String status, String message) {
        if (id == null) {
            return;
        }
        this.bindingMapper.update(null, (Wrapper)((LambdaUpdateWrapper)((LambdaUpdateWrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper().set(OciOpenaiPortBinding::getStatus, (Object)status)).set(OciOpenaiPortBinding::getStatusMessage, (Object)message)).set(OciOpenaiPortBinding::getUpdateTime, (Object)LocalDateTime.now())).eq(OciOpenaiPortBinding::getId, (Object)id));
    }

    private void startAndMark(OciOpenaiPortBinding row) {
        try {
            this.dynamicPortService.startPort(row.getPort().intValue());
            this.markStatus(row.getId(), "listening", null);
        }
        catch (Exception e) {
            this.markStatus(row.getId(), "failed", e.getMessage());
            throw new OciException(e.getMessage());
        }
    }

    private OciUser validateTenantAndKey(String ociUserId, String openaiKeyId) {
        if (ociUserId == null || ociUserId.isBlank()) {
            throw new OciException("\u8bf7\u9009\u62e9\u79df\u6237");
        }
        if (openaiKeyId == null || openaiKeyId.isBlank()) {
            throw new OciException("\u8bf7\u9009\u62e9 API Key");
        }
        OciUser user = (OciUser)this.userMapper.selectById((Serializable)((Object)ociUserId));
        if (user == null) {
            throw new OciException("\u79df\u6237\u4e0d\u5b58\u5728");
        }
        OciOpenaiKey key = (OciOpenaiKey)this.keyMapper.selectById((Serializable)((Object)openaiKeyId));
        if (key == null) {
            throw new OciException("API Key \u4e0d\u5b58\u5728");
        }
        if (key.getDisabled() != null && key.getDisabled() == 1) {
            throw new OciException("API Key \u5df2\u7981\u7528");
        }
        if (!ociUserId.equals(key.getOciUserId())) {
            throw new OciException("API Key \u4e0d\u5c5e\u4e8e\u6240\u9009\u79df\u6237");
        }
        return user;
    }

    private static String normalizeRegion(String region, OciUser user) {
        String r;
        String string = r = region == null ? null : region.trim();
        if (r == null || r.isEmpty()) {
            String string2 = r = user == null ? null : user.getOciRegion();
        }
        if (r == null) {
            return null;
        }
        return r.length() > 64 ? r.substring(0, 64) : r;
    }

    private static String trimName(String name) {
        if (name == null) {
            return null;
        }
        String s = name.trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.length() > 128 ? s.substring(0, 128) : s;
    }

    private static Integer normalizeDefaultMaxTokens(Integer value) {
        if (value == null) {
            return null;
        }
        return OracleAiGatewayConfigService.normalizeDefaultMaxTokens((int)value);
    }

    public static List<String> decodeAllowedModels(String json) {
        ArrayList<String> out = new ArrayList<String>();
        if (json == null || json.isBlank()) {
            return out;
        }
        try {
            JsonNode root = MAPPER.readTree(json);
            if (root != null && root.isArray()) {
                for (JsonNode n : root) {
                    String s;
                    if (n == null || !n.isTextual() || (s = n.asText().trim()).isBlank()) continue;
                    out.add(s);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return OracleAiPortBindingService.normalizeAllowedModels(out);
    }

    public static List<String> normalizeAllowedModels(List<String> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (String raw : input) {
            String s;
            if (raw == null || (s = raw.trim()).isBlank()) continue;
            if (s.length() > 256) {
                s = s.substring(0, 256);
            }
            set.add(s);
            if (set.size() < 200) continue;
            break;
        }
        return new ArrayList<String>(set);
    }

    private static String encodeAllowedModels(List<String> input) {
        List normalized = OracleAiPortBindingService.normalizeAllowedModels(input);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString((Object)normalized);
        }
        catch (Exception e) {
            throw new OciException("allowedModels \u4fdd\u5b58\u5931\u8d25");
        }
    }
}

