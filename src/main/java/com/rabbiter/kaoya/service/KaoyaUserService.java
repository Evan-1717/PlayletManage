package com.rabbiter.kaoya.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.kaoya.mapper.KaoyaUserMapper;
import com.rabbiter.kaoya.entity.KaoyaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
public interface KaoyaUserService extends IService<KaoyaUser> {
    IPage pageC(IPage<KaoyaUser> page);

    IPage pageCC(IPage<KaoyaUser> page, Wrapper wrapper);
}
