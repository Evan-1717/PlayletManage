package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;
import com.rabbiter.mapper.TencentMapper;
import com.rabbiter.service.TencentService;
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
public class TencentServiceImpl extends ServiceImpl<TencentMapper, Tencent> implements TencentService {
    @Autowired
    private TencentMapper tencentMapper;

    @Override
    public List<Tencent1> selectrecord(Wrapper wrapper) {
        return tencentMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<Tencent> page, Wrapper wrapper) {
        return tencentMapper.pageCC(page,wrapper);
    }
}
