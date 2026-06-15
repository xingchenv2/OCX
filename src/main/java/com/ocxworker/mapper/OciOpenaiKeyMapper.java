package com.ocxworker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocxworker.model.entity.OciOpenaiKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OciOpenaiKeyMapper extends BaseMapper<OciOpenaiKey> {
}
