package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.entity.Shouzhi1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Mapper
public interface ShouzhiMapper extends BaseMapper<Shouzhi> {
    IPage pageCC(IPage<Shouzhi> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<Shouzhi1> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    Double calculateRecharge (Map<String, String> param);
}
