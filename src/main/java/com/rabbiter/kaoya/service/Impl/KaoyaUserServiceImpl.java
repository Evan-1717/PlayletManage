package com.rabbiter.kaoya.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.entity.User;
import com.rabbiter.kaoya.entity.KaoyaUser;
import com.rabbiter.kaoya.mapper.KaoyaUserMapper;
import com.rabbiter.kaoya.service.KaoyaUserService;
import com.rabbiter.mapper.UserMapper;
import com.rabbiter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Service
public class KaoyaUserServiceImpl extends ServiceImpl<KaoyaUserMapper, KaoyaUser> implements KaoyaUserService {
    @Autowired
    private KaoyaUserMapper userMapper;
    @Override
    public IPage pageC(IPage<KaoyaUser> page) {
        return userMapper.pageC(page);
    }

    @Override
    public IPage pageCC(IPage<KaoyaUser> page, Wrapper wrapper) {
        return userMapper.pageCC(page,wrapper);
    }
}
