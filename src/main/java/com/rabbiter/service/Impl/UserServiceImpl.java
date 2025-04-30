package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rabbiter.entity.User;
import com.rabbiter.entity.User1;
import com.rabbiter.mapper.UserMapper;
import com.rabbiter.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage pageC(IPage<User> page) {
        return userMapper.pageC(page);
    }

    @Override
    public IPage pageCC(IPage<User> page, Wrapper wrapper) {
        return userMapper.pageCC(page,wrapper);
    }

    @Override
    public List<User1> selectrecord(Wrapper wrapper) {
        return userMapper.selectrecord(wrapper);
    }

    @Override
    public List<String> distributors() {
        return userMapper.distributors();
    }

    @Override
    public Map<String, List<String>> inputInfo() {
        List<String> userList = userMapper.getUserList();
        List<String> roleList = userMapper.getRoleList();
        Map<String, List<String>> result = new HashMap<>();
        result.put("user",userList);
        result.put("role",roleList);
        return result;
    }
}
