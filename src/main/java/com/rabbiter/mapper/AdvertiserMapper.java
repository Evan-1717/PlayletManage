package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
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
public interface AdvertiserMapper extends BaseMapper<AdvertiserCost> {
    IPage pageCC(IPage<AdvertiserCost> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<AdvertiserCost> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<AdvertiserCost> getAdailyCostByUser(Map<String,String> map);
}
