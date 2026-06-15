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
import com.ocxworker.util.CommonUtils;
import com.ocxworker.util.OciOpenaiKeyCipher;
import jakarta.annotation.Resource;
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
    @Value("${web.password}")
    private String webPassword;
    private static final String PREFIX = "sk-";
    private static final SecureRandom R = new SecureRandom();

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public OciOpenaiKeyService.KeyCreateResult create(String ociUserId, String name) {
        if (ociUserId != null && !ociUserId.isBlank()) {
            OciUser u = (OciUser)this.ociUserMapper.selectById(ociUserId);
            if (u == null) {
                throw new OciException("租户不存在");
            } else {
                int randomBytes = 32;
                byte[] buf = new byte[randomBytes];
                R.nextBytes(buf);
                StringBuilder sb = new StringBuilder("sk-");

                for (byte t : buf) {
                    sb.append(String.format("%02x", t));
                }

                String keyPlain = sb.toString();
                String hash = DigestUtil.sha256Hex(keyPlain);
                OciOpenaiKey row = new OciOpenaiKey();
                row.setId(CommonUtils.generateId());
                row.setOciUserId(ociUserId);
                row.setKeyHash(hash);
                String prefix = keyPlain.length() > 16 ? keyPlain.substring(0, 12) : keyPlain;
                row.setKeyPrefix(prefix);
                row.setKeyEncrypted(OciOpenaiKeyCipher.encrypt(keyPlain, this.webPassword));
                row.setName(name);
                row.setDisabled(0);
                row.setCreateTime(LocalDateTime.now());
                this.openaiKeyMapper.insert(row);
                return new OciOpenaiKeyService.KeyCreateResult(row.getId(), keyPlain, prefix, OciOpenaiKeyCipher.maskForDisplay(keyPlain));
            }
        } else {
            throw new OciException("请选择租户");
        }
    }

    public List<OciOpenaiKey> listByTenant(String ociUserId) {
        return ociUserId != null && !ociUserId.isBlank()
            ? this.openaiKeyMapper
                .selectList(
                    (Wrapper)(new LambdaQueryWrapper<OciOpenaiKey>().eq(OciOpenaiKey::getOciUserId, ociUserId)).orderByDesc(OciOpenaiKey::getCreateTime)
                )
            : List.of();
    }

    public OciOpenaiKey getById(String id) {
        return id != null && !id.isBlank() ? (OciOpenaiKey)this.openaiKeyMapper.selectById(id) : null;
    }

    public String maskForList(OciOpenaiKey k) {
        if (k == null) {
            return "sk-****";
        } else {
            if (StrUtil.isNotBlank(k.getKeyEncrypted())) {
                try {
                    String plain = OciOpenaiKeyCipher.decrypt(k.getKeyEncrypted(), this.webPassword);
                    if (StrUtil.isNotBlank(plain)) {
                        return OciOpenaiKeyCipher.maskForDisplay(plain);
                    }
                } catch (Exception var3) {
                }
            }

            return StrUtil.isNotBlank(k.getKeyPrefix()) ? k.getKeyPrefix() + "****" : "sk-****";
        }
    }

    public String revealPlainKey(String id) {
        if (id != null && !id.isBlank()) {
            OciOpenaiKey k = (OciOpenaiKey)this.openaiKeyMapper.selectById(id);
            if (k == null) {
                throw new OciException("密钥不存在");
            } else if (StrUtil.isBlank(k.getKeyEncrypted())) {
                throw new OciException("该密钥为旧数据，未保存完整密钥，请删除后重新生成");
            } else {
                String plain = OciOpenaiKeyCipher.decrypt(k.getKeyEncrypted(), this.webPassword);
                if (StrUtil.isBlank(plain)) {
                    throw new OciException("密钥解密失败（可能修改过面板登录密码），请重新生成密钥");
                } else {
                    return plain;
                }
            }
        } else {
            throw new OciException("id 必填");
        }
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public void setDisabled(String id, boolean disabled) {
        OciOpenaiKey k = (OciOpenaiKey)this.openaiKeyMapper.selectById(id);
        if (k != null) {
            k.setDisabled(disabled ? 1 : 0);
            this.openaiKeyMapper.updateById(k);
        }
    }

    @Transactional(
        rollbackFor = {Exception.class}
    )
    public void remove(String id) {
        this.openaiKeyMapper.deleteById(id);
    }

    public OciOpenaiKey findByPlainKey(String plain) {
        if (plain != null && !plain.isBlank() && plain.startsWith("sk-")) {
            String hash = DigestUtil.sha256Hex(plain);
            return (OciOpenaiKey)this.openaiKeyMapper.selectOne((Wrapper)new LambdaQueryWrapper<OciOpenaiKey>().eq(OciOpenaiKey::getKeyHash, hash));
        } else {
            return null;
        }
    }

    public void updateLastUsed(String id) {
        if (id != null) {
            this.openaiKeyMapper
                .update(
                    null,
                    (Wrapper)((LambdaUpdateWrapper)new LambdaUpdateWrapper().set(OciOpenaiKey::getLastUsed, LocalDateTime.now())).eq(OciOpenaiKey::getId, id)
                );
        }
    }

    public static record KeyCreateResult(String id, String plainKey, String keyPrefix, String keyMasked) {
    }
}
