package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.User;
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
public interface JlaccountMapper extends BaseMapper<JlPromotion> {

    IPage listPagePromotion(IPage<JlPromotion> page);

    IPage listPageAutoPromotion(IPage<Map<String, String>> page);

    IPage listPageProject(IPage<Map<String, String>> page);

    void saveJlPromotion(Map<String, Object> map);

    void saveJlProject(Map<String, Object> map);

    void saveJlaccount(Map<String, String> map);

    void updateJlaccount(Map<String, String> map);

    List<Map<String, String>> selectJlaccount(@Param("account") String account);
}
