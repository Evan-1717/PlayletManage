package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Novel1;
import com.rabbiter.entity.Novel;

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
public interface NovelService extends IService<Novel> {
    IPage pageCC(IPage<Novel> page, Wrapper wrapper);
    List<Novel1> selectrecord(Wrapper wrapper);
}
