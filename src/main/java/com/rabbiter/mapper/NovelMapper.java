package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.Novel;
import com.rabbiter.entity.Novel1;
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
 * @since 2023-01-05
 */
@Mapper
public interface NovelMapper extends BaseMapper<Novel> {
    IPage pageCC(IPage<Novel> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<Novel1> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<Map<String, String>> selectrecordai(Map<String, Object> map);
}
