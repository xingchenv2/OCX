package com.ocxworker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ocxworker.model.entity.OciUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OciUserMapper extends BaseMapper<OciUser> {
}
