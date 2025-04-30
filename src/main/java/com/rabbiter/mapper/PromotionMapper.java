package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.Promotion;
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
public interface PromotionMapper extends BaseMapper<Promotion> {
    IPage pageCC(IPage<Promotion> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<Promotion> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    int saveAutomationPromotion(Promotion promotion);
}
