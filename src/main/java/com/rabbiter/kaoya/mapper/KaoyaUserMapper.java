package com.rabbiter.kaoya.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.kaoya.entity.KaoyaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Mapper
public interface KaoyaUserMapper extends BaseMapper<KaoyaUser> {
    IPage pageC(IPage<KaoyaUser> page);

    IPage pageCC(IPage<KaoyaUser> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
