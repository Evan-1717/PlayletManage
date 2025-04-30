package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
public interface TencentService extends IService<Tencent> {
    IPage pageCC(IPage<Tencent> page, Wrapper wrapper);
    List<Tencent1> selectrecord(Wrapper wrapper);
}
