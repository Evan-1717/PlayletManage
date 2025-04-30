package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.entity.User1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    IPage pageC(IPage<User> page);

    IPage pageCC(IPage<User> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<User1> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<String> distributors();

    List<String> getUserList();

    List<String> getRoleList();
}
