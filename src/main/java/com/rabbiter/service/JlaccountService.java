package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

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
public interface JlaccountService extends IService<JlPromotion> {
    IPage listPagePromotion(IPage<JlPromotion> page);

    IPage listPageAutoPromotion(IPage<Map<String, String>> page);

    IPage listPageProject(IPage<Map<String, String>> page);

    Map<String, String> autoCreatePromotion(Map<String, Object> params);

    Map<String, Object> bindVideo(Map<String, Object> params);

    void saveJlaccount(Map<String, String> map);

    String getJlaccount(String account);

    Map<String, Object> getAdvertiserInfo(Map<String, Object> params);
}
