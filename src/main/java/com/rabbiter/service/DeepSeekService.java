package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
public interface DeepSeekService extends IService<Tencent> {
    String analyze(Map<String, Object> params);

    void clearAsk(Map<String, String> params);
}
