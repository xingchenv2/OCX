/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.crypto.digest.DigestUtil
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciOpenaiKeyMapper
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.entity.OciOpenaiKey
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.OciOpenaiKeyService
 *  com.ocxworker.service.OciOpenaiKeyService$KeyCreateResult
 *  com.ocxworker.util.CommonUtils
 *  com.ocxworker.util.OciOpenaiKeyCipher
 *  jakarta.annotation.Resource
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.ocxworker.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciOpenaiKeyMapper;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.entity.OciOpenaiKey;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciOpenaiKeyService;
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.OciOpenaiKeyCipher;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OciOpenaiKeyService {
    @Resource
    private OciOpenaiKeyMapper openaiKeyMapper;
    @Resource
    private OciUserMapper ociUserMapper;
    @Value(value="${web.password}")
    private String webPassword;
    private static final String PREFIX = "sk-";
    private static final SecureRandom R = new SecureRandom();

    @Transactional(rollbackFor={Exception.class})
    public KeyCreateResult create(String ociUserId, String name) {
        if (ociUserId == null || ociUserId.isBlank()) {
            throw new OciException("\u8bf7\u9009\u62e9\u79df\u6237");
        }
        OciUser u = (OciUser)this.ociUserMapper.selectById((Serializable)((Object)ociUserId));
        if (u == null) {
            throw new OciException("\u79df\u6237\u4e0d\u5b58\u5728");
        }
        int randomBytes = 32;
        byte[] buf = new byte[randomBytes];
        R.nextBytes(buf);
        StringBuilder sb = new StringBuilder(PREFIX);
        for (byte t : buf) {
            sb.append(String.format("%02x", t));
        }
        String keyPlain = sb.toString();
        String hash = DigestUtil.sha256Hex((String)keyPlain);
        OciOpenaiKey row = new OciOpenaiKey();
        row.setId(CommonUtils.generateId());
        row.setOciUserId(ociUserId);
        row.setKeyHash(hash);
        String prefix = keyPlain.length() > 16 ? keyPlain.substring(0, 12) : keyPlain;
        row.setKeyPrefix(prefix);
        row.setKeyEncrypted(OciOpenaiKeyCipher.encrypt((String)keyPlain, (String)this.webPassword));
        row.setName(name);
        row.setDisabled(Integer.valueOf(0));
        row.setCreateTime(LocalDateTime.now());
        this.openaiKeyMapper.insert((Object)row);
        return new KeyCreateResult(row.getId(), keyPlain, prefix, OciOpenaiKeyCipher.maskForDisplay((String)keyPlain));
    }

    public List<OciOpenaiKey> listByTenant(String ociUserId) {
        if (ociUserId == null || ociUserId.isBlank()) {
            return List.of();
        }
        return this.openaiKeyMapper.selectList((Wrapper)((LambdaQueryWrapper)new LambdaQueryWrapper().eq(OciOpenaiKey::getOciUserId, (Object)ociUserId)).orderByDesc(OciOpenaiKey::getCreateTime));
    }

    public OciOpenaiKey getById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return (OciOpenaiKey)this.openaiKeyMapper.selectById((Serializable)((Object)id));
    }

    public String maskForList(OciOpenaiKey k) {
        if (k == null) {
            return "sk-****";
        }
        if (StrUtil.isNotBlank((CharSequence)k.getKeyEncrypted())) {
            try {
                String plain = OciOpenaiKeyCipher.decrypt((String)k.getKeyEncrypted(), (String)this.webPassword);
                if (StrUtil.isNotBlank((CharSequence)plain)) {
                    return OciOpenaiKeyCipher.maskForDisplay((String)plain);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (StrUtil.isNotBlank((CharSequence)k.getKeyPrefix())) {
            return k.getKeyPrefix() + "****";
        }
        return "sk-****";
    }

    public String revealPlainKey(String id) {
        if (id == null || id.isBlank()) {
            throw new OciException("id \u5fc5\u586b");
        }
        OciOpenaiKey k = (OciOpenaiKey)this.openaiKeyMapper.selectById((Serializable)((Object)id));
        if (k == null) {
            throw new OciException("\u5bc6\u94a5\u4e0d\u5b58\u5728");
        }
        if (StrUtil.isBlank((CharSequence)k.getKeyEncrypted())) {
            throw new OciException("\u8be5\u5bc6\u94a5\u4e3a\u65e7\u6570\u636e\uff0c\u672a\u4fdd\u5b58\u5b8c\u6574\u5bc6\u94a5\uff0c\u8bf7\u5220\u9664\u540e\u91cd\u65b0\u751f\u6210");
        }
        String plain = OciOpenaiKeyCipher.decrypt((String)k.getKeyEncrypted(), (String)this.webPassword);
        if (StrUtil.isBlank((CharSequence)plain)) {
            throw new OciException("\u5bc6\u94a5\u89e3\u5bc6\u5931\u8d25\uff08\u53ef\u80fd\u4fee\u6539\u8fc7\u9762\u677f\u767b\u5f55\u5bc6\u7801\uff09\uff0c\u8bf7\u91cd\u65b0\u751f\u6210\u5bc6\u94a5");
        }
        return plain;
    }

    @Transactional(rollbackFor={Exception.class})
    public void setDisabled(String id, boolean disabled) {
        OciOpenaiKey k = (OciOpenaiKey)this.openaiKeyMapper.selectById((Serializable)((Object)id));
        if (k == null) {
            return;
        }
        k.setDisabled(Integer.valueOf(disabled ? 1 : 0));
        this.openaiKeyMapper.updateById((Object)k);
    }

    @Transactional(rollbackFor={Exception.class})
    public void remove(String id) {
        this.openaiKeyMapper.deleteById((Serializable)((Object)id));
    }

    public OciOpenaiKey findByPlainKey(String plain) {
        if (plain == null || plain.isBlank() || !plain.startsWith(PREFIX)) {
            return null;
        }
        String hash = DigestUtil.sha256Hex((String)plain);
        return (OciOpenaiKey)this.openaiKeyMapper.selectOne((Wrapper)new LambdaQueryWrapper().eq(OciOpenaiKey::getKeyHash, (Object)hash));
    }

    public void updateLastUsed(String id) {
        if (id == null) {
            return;
        }
        this.openaiKeyMapper.update(null, (Wrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper().set(OciOpenaiKey::getLastUsed, (Object)LocalDateTime.now())).eq(OciOpenaiKey::getId, (Object)id));
    }
}

    record KeyCreateResult(String id, String plainKey, String keyPrefix, String keyMasked) {}


