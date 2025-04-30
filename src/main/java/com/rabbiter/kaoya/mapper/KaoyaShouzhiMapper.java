package com.rabbiter.kaoya.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.kaoya.entity.KaoyaShouzhi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Mapper
public interface KaoyaShouzhiMapper extends BaseMapper<KaoyaShouzhi> {
    IPage pageCC(IPage<KaoyaShouzhi> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<KaoyaShouzhi> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);
}
