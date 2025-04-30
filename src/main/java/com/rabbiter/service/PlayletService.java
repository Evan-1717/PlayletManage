package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Playlet;
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
public interface PlayletService extends IService<Playlet> {
    IPage pageCC(IPage<Playlet> page, Wrapper wrapper);
    List<Playlet> selectrecord(Wrapper wrapper);

    double calculateRecharge (Map<String, String> param);

    Result save(Map<String, String> param);

    List<String> getdistributorIdBydistributor(Map<String, String> param);

    Map<String, Object> getdistributorIdInfo(Map<String, String> param);
}
