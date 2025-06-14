package com.rabbiter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.Promotion;
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
    IPage listPageAutoPromotion(IPage<JlPromotion> page, Wrapper wrapper);

    Map<String, String> autoCreatePromotion(Map<String, Object> params);

    int savePromotion(Map<String, Object> param);

    Map<String, Object> bindVideo(Map<String, Object> params);

    void saveJlaccount(Map<String, String> map);

    String getJlaccount(String account);

    Map<String, Object> getAdvertiserInfo(Map<String, Object> params);

    List<Map<String, Object>> getVideoList(Map<String, Object> params);
}
