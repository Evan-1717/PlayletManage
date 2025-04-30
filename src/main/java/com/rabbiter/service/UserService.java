package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rabbiter.entity.User;
import com.rabbiter.entity.User1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
public interface UserService extends IService<User> {
    IPage pageC(IPage<User> page);

    IPage pageCC(IPage<User> page, Wrapper wrapper);

    List<User1> selectrecord(Wrapper wrapper);

    List<String> distributors();

    Map<String, List<String>> inputInfo();
}
