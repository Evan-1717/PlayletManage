package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Playlet;
import com.rabbiter.entity.Promotion;
import com.rabbiter.mapper.PromotionMapper;
import com.rabbiter.service.PromotionService;
import com.rabbiter.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {
    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Promotion> selectrecord(Wrapper wrapper) {
        return promotionMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<Promotion> page, Wrapper wrapper) {
        return promotionMapper.pageCC(page,wrapper);
    }

    @Override
    public Promotion promotion(Map<String, String> params) {
        String url = "https://www.changdunovel.com/novelsale/openapi/promotion/create/v1?sign={sign}&distributor_id={distributor_id}&ts={ts}&media_source={media_source}&index={index}&book_id={book_id}&promotion_name={promotion_name}&pack_strategy_status={pack_strategy_status}&ad_callback_config_id={ad_callback_config_id}";
        String distributor_id = params.get("distributor_id");
        Long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id+params.get("secret_key")+ts);
        String media_source_name = "微信小程序".equals(params.get("media_source")) ? "微小" : "抖小";
        String promotion_name = params.get("creater") + "-番茄-" + media_source_name + params.get("promotion_name")
                + "-" + params.get("label") + "-" + params.get("remark");
        int media_source = "微信小程序".equals(params.get("media_source")) ? 4 : 1;
        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("book_id", params.get("playlet_id"));
                put("index", 1);
                put("media_source", media_source);
                put("promotion_name", promotion_name);
                put("pack_strategy_status", 1);
                put("recharge_template_id", params.get("recharge_template_id"));
                if(!"-1".equals(params.get("recharge_template_id"))) {
                    put("ad_callback_config_id", params.get("adCallbackConfigId"));
                }
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        Map<String,Object> resInfo = res.getBody();
        if ("200".equals(resInfo.get("code").toString())) {
            Promotion promotion = new Promotion(resInfo);
            promotion.setDistributor_id(distributor_id);
            promotion.setPlaylet_id(params.get("playlet_id"));
            promotion.setCreater(params.get("creater"));
            promotion.setCreate_time(Utils.getTime6());
            promotionMapper.saveAutomationPromotion(promotion);
            return promotion;
        }
        return null;
    }
}
