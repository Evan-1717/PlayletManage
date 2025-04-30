package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;
import com.rabbiter.entity.TomatoPromotion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
public interface TomatoService extends IService<TomatoPromotion> {
    IPage pageCC(IPage<TomatoPromotion> page, Wrapper wrapper);
    List<TomatoPromotion> selectrecord(Wrapper wrapper);

    Map<String, List<Map<String, String>>> getCodeInfo();

    Map<String, Object> getVideoInfo( Map<String, String> params);

    Map<String, Object>  createPromotion(Map<String, String> params);

    List<Map<String, Object>> getRechargeTemplate(Map<String, String> params);

    List<Map<String, Object>> getAdCallback(Map<String, String> params);

    void savePromotion(Map<String, Object> map);
}
