package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.entity.Novel;
import com.rabbiter.entity.Novel1;
import com.rabbiter.mapper.NovelMapper;
import com.rabbiter.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Service
public class NovelServiceImpl extends ServiceImpl<NovelMapper, Novel> implements NovelService {
    @Autowired
    private NovelMapper novelMapper;

    @Override
    public List<Novel1> selectrecord(Wrapper wrapper) {
        return novelMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<Novel> page, Wrapper wrapper) {
        return novelMapper.pageCC(page,wrapper);
    }
}
