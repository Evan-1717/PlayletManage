package com.rabbiter.kaoya.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.kaoya.entity.KaoyaShouzhi;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
public interface KaoyaShouzhiService extends IService<KaoyaShouzhi> {
    IPage pageCC(IPage<KaoyaShouzhi> page, Wrapper wrapper);
    List<KaoyaShouzhi> selectrecord(Wrapper wrapper);
}
