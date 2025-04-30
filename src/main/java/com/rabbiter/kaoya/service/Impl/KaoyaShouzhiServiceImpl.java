package com.rabbiter.kaoya.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.kaoya.entity.KaoyaShouzhi;
import com.rabbiter.kaoya.mapper.KaoyaShouzhiMapper;
import com.rabbiter.kaoya.service.KaoyaShouzhiService;
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
public class KaoyaShouzhiServiceImpl extends ServiceImpl<KaoyaShouzhiMapper, KaoyaShouzhi> implements KaoyaShouzhiService {
    @Autowired
    private KaoyaShouzhiMapper shouzhiMapper;

    @Override
    public List<KaoyaShouzhi> selectrecord(Wrapper wrapper) {
        return shouzhiMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<KaoyaShouzhi> page, Wrapper wrapper) {
        return shouzhiMapper.pageCC(page,wrapper);
    }
}
