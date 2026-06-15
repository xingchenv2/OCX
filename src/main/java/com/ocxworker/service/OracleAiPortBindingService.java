package com.ocxworker.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciOpenaiKeyMapper;
import com.ocxworker.mapper.OciOpenaiPortBindingMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciOpenaiKey;
import com.ocxworker.model.entity.OciOpenaiPortBinding;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.util.CommonUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Order(-2147483638)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        this.syncEnabledConnectors();
    }

    public List<OciOpenaiPortBinding> list() {
        return this.bindingMapper
            .selectList(
                (Wrapper)(new LambdaQueryWrapper<OciOpenaiPortBinding>().orderByAsc(OciOpenaiPortBinding::getPort))
                    .orderByDesc(OciOpenaiPortBinding::getCreateTime)
            );
    }

    public OciOpenaiPortBinding getByPort(int port) {
        return !DynamicOpenAiPortService.isManagedPort(port)
            ? null
            : (OciOpenaiPortBinding)this.bindingMapper.selectOne((Wrapper)new LambdaQueryWrapper<OciOpenaiPortBinding>().eq(OciOpenaiPortBinding::getPort, port));
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public OciOpenaiPortBinding create(
        String name, int port, String ociUserId, String ociRegion, String openaiKeyId, Integer defaultMaxTokens, List<String> allowedModels, boolean enabled
    ) {
        DynamicOpenAiPortService.validateManagedPort(port);
        OciUser user = this.validateTenantAndKey(ociUserId, openaiKeyId);
        OciOpenaiPortBinding existing = this.getByPort(port);
        if (existing != null) {
            throw new OciException("端口已绑定: " + port);
        } else {
            OciOpenaiPortBinding row = new OciOpenaiPortBinding();
            row.setId(CommonUtils.generateId());
            row.setName(trimName(name));
            row.setPort(port);
            row.setOciUserId(ociUserId);
            row.setOciRegion(normalizeRegion(ociRegion, user));
            row.setOpenaiKeyId(openaiKeyId);
            row.setDefaultMaxTokens(normalizeDefaultMaxTokens(defaultMaxTokens));
            row.setAllowedModelsJson(encodeAllowedModels(allowedModels));
            row.setEnabled(enabled ? 1 : 0);
            row.setStatus("stopped");
            row.setCreateTime(LocalDateTime.now());
            row.setUpdateTime(LocalDateTime.now());
            this.bindingMapper.insert(row);
            if (enabled) {
                this.startAndMark(row);
            }

            return (OciOpenaiPortBinding)this.bindingMapper.selectById(row.getId());
        }
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public OciOpenaiPortBinding update(
        String id,
        String name,
        int port,
        String ociUserId,
        String ociRegion,
        String openaiKeyId,
        Integer defaultMaxTokens,
        List<String> allowedModels,
        boolean enabled
    ) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById(id);
        if (row == null) {
            throw new OciException("绑定不存在");
        } else {
            DynamicOpenAiPortService.validateManagedPort(port);
            OciUser user = this.validateTenantAndKey(ociUserId, openaiKeyId);
            OciOpenaiPortBinding samePort = this.getByPort(port);
            if (samePort != null && !samePort.getId().equals(id)) {
                throw new OciException("端口已绑定: " + port);
            } else {
                int oldPort = row.getPort() == null ? -1 : row.getPort();
                row.setName(trimName(name));
                row.setPort(port);
                row.setOciUserId(ociUserId);
                row.setOciRegion(normalizeRegion(ociRegion, user));
                row.setOpenaiKeyId(openaiKeyId);
                row.setDefaultMaxTokens(normalizeDefaultMaxTokens(defaultMaxTokens));
                row.setAllowedModelsJson(encodeAllowedModels(allowedModels));
                row.setEnabled(enabled ? 1 : 0);
                row.setUpdateTime(LocalDateTime.now());
                this.bindingMapper.updateById(row);
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

                return (OciOpenaiPortBinding)this.bindingMapper.selectById(id);
            }
        }
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public void setEnabled(String id, boolean enabled) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById(id);
        if (row != null) {
            row.setEnabled(enabled ? 1 : 0);
            row.setUpdateTime(LocalDateTime.now());
            this.bindingMapper.updateById(row);
            if (enabled) {
                this.startAndMark(row);
            } else {
                this.dynamicPortService.stopPort(row.getPort());
                this.markStatus(row.getId(), "disabled", null);
            }
        }
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public void remove(String id) {
        OciOpenaiPortBinding row = (OciOpenaiPortBinding)this.bindingMapper.selectById(id);
        if (row != null && row.getPort() != null) {
            this.dynamicPortService.stopPort(row.getPort());
        }

        this.bindingMapper.deleteById(id);
    }

    public void syncEnabledConnectors() {
        for (OciOpenaiPortBinding row : this.bindingMapper.selectList((Wrapper)new LambdaQueryWrapper<OciOpenaiPortBinding>().eq(OciOpenaiPortBinding::getEnabled, 1))) {
            try {
                this.startAndMark(row);
            } catch (Exception var5) {
            }
        }
    }

    public void touchLastUsed(String id) {
        if (id != null) {
            this.bindingMapper
                .update(
                    null,
                    (Wrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper().set(OciOpenaiPortBinding::getLastUsed, LocalDateTime.now()))
                        .eq(OciOpenaiPortBinding::getId, id)
                );
        }
    }

    public void markStatus(String id, String status, String message) {
        if (id != null) {
            this.bindingMapper
                .update(
                    null,
                    (Wrapper)((LambdaUpdateWrapper)((LambdaUpdateWrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper()
                                    .set(OciOpenaiPortBinding::getStatus, status))
                                .set(OciOpenaiPortBinding::getStatusMessage, message))
                            .set(OciOpenaiPortBinding::getUpdateTime, LocalDateTime.now()))
                        .eq(OciOpenaiPortBinding::getId, id)
                );
        }
    }

    private void startAndMark(OciOpenaiPortBinding row) {
        try {
            this.dynamicPortService.startPort(row.getPort());
            this.markStatus(row.getId(), "listening", null);
        } catch (Exception var3) {
            this.markStatus(row.getId(), "failed", var3.getMessage());
            throw new OciException(var3.getMessage());
        }
    }

    private OciUser validateTenantAndKey(String ociUserId, String openaiKeyId) {
        if (ociUserId == null || ociUserId.isBlank()) {
            throw new OciException("请选择租户");
        } else if (openaiKeyId != null && !openaiKeyId.isBlank()) {
            OciUser user = (OciUser)this.userMapper.selectById(ociUserId);
            if (user == null) {
                throw new OciException("租户不存在");
            } else {
                OciOpenaiKey key = (OciOpenaiKey)this.keyMapper.selectById(openaiKeyId);
                if (key == null) {
                    throw new OciException("API Key 不存在");
                } else if (key.getDisabled() != null && key.getDisabled() == 1) {
                    throw new OciException("API Key 已禁用");
                } else if (!ociUserId.equals(key.getOciUserId())) {
                    throw new OciException("API Key 不属于所选租户");
                } else {
                    return user;
                }
            }
        } else {
            throw new OciException("请选择 API Key");
        }
    }

    private static String normalizeRegion(String region, OciUser user) {
        String r = region == null ? null : region.trim();
        if (r == null || r.isEmpty()) {
            r = user == null ? null : user.getOciRegion();
        }

        if (r == null) {
            return null;
        } else {
            return r.length() > 64 ? r.substring(0, 64) : r;
        }
    }

    private static String trimName(String name) {
        if (name == null) {
            return null;
        } else {
            String s = name.trim();
            if (s.isEmpty()) {
                return null;
            } else {
                return s.length() > 128 ? s.substring(0, 128) : s;
            }
        }
    }

    private static Integer normalizeDefaultMaxTokens(Integer value) {
        return value == null ? null : OracleAiGatewayConfigService.normalizeDefaultMaxTokens(value);
    }

    public static List<String> decodeAllowedModels(String json) {
        List<String> out = new ArrayList<>();
        if (json != null && !json.isBlank()) {
            try {
                JsonNode root = MAPPER.readTree(json);
                if (root != null && root.isArray()) {
                    for (JsonNode n : root) {
                        if (n != null && n.isTextual()) {
                            String s = n.asText().trim();
                            if (!s.isBlank()) {
                                out.add(s);
                            }
                        }
                    }
                }
            } catch (Exception var6) {
            }

            return normalizeAllowedModels(out);
        } else {
            return out;
        }
    }

    public static List<String> normalizeAllowedModels(List<String> input) {
        if (input != null && !input.isEmpty()) {
            Set<String> set = new LinkedHashSet<>();

            for (String raw : input) {
                if (raw != null) {
                    String s = raw.trim();
                    if (!s.isBlank()) {
                        if (s.length() > 256) {
                            s = s.substring(0, 256);
                        }

                        set.add(s);
                        if (set.size() >= 200) {
                            break;
                        }
                    }
                }
            }

            return new ArrayList<>(set);
        } else {
            return List.of();
        }
    }

    private static String encodeAllowedModels(List<String> input) {
        List<String> normalized = normalizeAllowedModels(input);
        if (normalized.isEmpty()) {
            return null;
        } else {
            try {
                return MAPPER.writeValueAsString(normalized);
            } catch (Exception var3) {
                throw new OciException("allowedModels 保存失败");
            }
        }
    }
}
