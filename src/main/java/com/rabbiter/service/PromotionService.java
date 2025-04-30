package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Promotion;

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
public interface PromotionService extends IService<Promotion> {
    IPage pageCC(IPage<Promotion> page, Wrapper wrapper);
    List<Promotion> selectrecord(Wrapper wrapper);

    Promotion promotion(Map<String, String> param);
}
