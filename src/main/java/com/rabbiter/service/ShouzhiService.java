package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rabbiter.entity.UserPaidOrder;
import com.rabbiter.entity.Shouzhi;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface ShouzhiService extends IService<Shouzhi> {
    IPage pageCC(IPage<Shouzhi> page, Wrapper wrapper);
    List<Shouzhi1> selectrecord(Wrapper wrapper);

    double calculateRecharge (Map<String, String> param);
}
