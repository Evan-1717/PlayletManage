package com.rabbiter.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.rabbiter.entity.Playlet;
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
public interface PlayletMapper extends BaseMapper<Playlet> {
    IPage pageCC(IPage<Playlet> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    List<Playlet> selectrecord(@Param(Constants.WRAPPER) Wrapper wrapper);

    Double calculateRecharge (Map<String, String> param);

    int savePlaylet(Playlet playlet);

    List<String> getdistributorIdBydistributor(Map<String, String> param);

    List<Map<String, Object>> getdistributorIdInfo(Map<String, String> param);
}
