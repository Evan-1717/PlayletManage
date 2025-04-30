package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;

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
public interface AdvertiserService extends IService<AdvertiserCost> {
    IPage pageCC(IPage<AdvertiserCost> page, Wrapper wrapper);

    List<AdvertiserCost> selectrecord(Wrapper wrapper);

    double calculateExpend (Map<String, String> param);
}
