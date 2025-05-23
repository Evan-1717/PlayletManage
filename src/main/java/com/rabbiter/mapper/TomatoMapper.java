package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
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
public interface TomatoMapper extends BaseMapper<TomatoPromotion> {
    IPage pageCC(IPage<TomatoPromotion> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<TomatoPromotion> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<Map<String, String>> selectCodeInfo(Map<String, String> map);

    List<Map<String, String>> selectDistributorById(@Param("distributorId")String distributorId);

    int savePromotion(@Param("promotion") JlPromotion promotion);

    void updatePromotion(Map<String, Object> map);
}
