package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.rabbiter.entity.*;
import com.rabbiter.mapper.TencentMapper;
import com.rabbiter.mapper.TomatoMapper;
import com.rabbiter.service.TencentService;
import com.rabbiter.service.TomatoService;
import com.rabbiter.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Service
public class TomatoServiceImpl extends ServiceImpl<TomatoMapper, TomatoPromotion> implements TomatoService {
    private static final Logger LOGGER = Logger.getLogger(JlaccountServiceImpl.class.getName());
    @Autowired
    private TomatoMapper tomatoMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<TomatoPromotion> selectrecord(Wrapper wrapper) {
        return tomatoMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<TomatoPromotion> page, Wrapper wrapper) {
        return tomatoMapper.pageCC(page,wrapper);
    }

    @Override
    public Map<String, List<Map<String, String>>> getCodeInfo(){
        Map<String, List<Map<String, String>>> result = new HashMap();
        Map<String, String> param = new HashMap<>();
        param.put("business", "recharge_template");
        result.put("recharge_template", tomatoMapper.selectCodeInfo(param));
        param.put("business", "ad_callback");
        result.put("ad_callback", tomatoMapper.selectCodeInfo(param));
        return result;
    }

    @Override
    public Map<String, Object> getVideoInfo(Map<String, Object> param){
        return getVideoInfoHttp(param.get("video_id").toString());
    }

    private Map<String, Object> getVideoInfoHttp(String book_id){
        String url = "https://www.changdunovel.com/novelsale/openapi/content/book_meta/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&book_id={book_id}";
        String distributor_id = "1805370052162640";
        Long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id + "gTHDJrzsiUOwepLIv6hq9AZ0nNf2aQCx" + ts);

        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("book_id", book_id);
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        if (Integer.parseInt(res.getBody().get("code").toString()) == 200) {
            return ((List<Map<String, Object>>)res.getBody().get("result")).get(0);
        } else {
            LOGGER.info("getVideoInfo error,info:"+ res.getBody().toString() + ",param: " + param.toString());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> createPromotion(Map<String, String> params){
        String url = "https://www.changdunovel.com/novelsale/openapi/promotion/create/v1" +
                "?sign={sign}&distributor_id={distributor_id}&ts={ts}&media_source={media_source}" +
                "&index={index}&book_id={book_id}&promotion_name={promotion_name}" +
                "&ad_callback_config_id={ad_callback_config_id}" +
                "&pack_strategy_status={pack_strategy_status}&start_chapter={start_chapter}";
        if (!"2".equals(params.get("radio").toString())) {
            url += "&price={price}&recharge_template_id={recharge_template_id}";
        }
        String distributor_id = params.get("distributor_id");
        Long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id + params.get("secret_key") + ts);

        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("book_id", params.get("book_id"));
                put("index", 1);
                put("media_source", 1);
                put("promotion_name", params.get("promotion_name"));
                put("ad_callback_config_id", Long.parseLong(params.get("ad_callback_config_id")));
                if (!"2".equals(params.get("radio").toString())) {
                    put("recharge_template_id", Long.parseLong(params.get("recharge_template_id")));
                    put("price", params.get("price"));
                }
                put("pack_strategy_status", 1);
                put("start_chapter", Integer.parseInt(params.get("start_chapter")));
            }
        };
        ResponseEntity<Map> res = restTemplate.getForEntity(url, Map.class, param);
        Map<String, Object> resultMap = res.getBody();
        resultMap.putAll(params);
        return resultMap;
    }

    @Override
    public  List<Map<String, Object>> getRechargeTemplate(Map<String, String> params){
        String url = "https://www.changdunovel.com/novelsale/openapi/recharge_template/list/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&page_size={page_size}&page_index={page_index}";
        String distributor_id = params.get("distributor_id");
        long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id + params.get("secret_key") + ts);

        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("page_index", 0);
                put("page_size", 50);
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        List<Map<String, Object>> resultList = (List<Map<String, Object>>)res.getBody().get("data");
        return resultList;
    }

    @Override
    public  List<Map<String, Object>> getAdCallback(Map<String, String> params){
        String url = "https://www.changdunovel.com/novelsale/openapi/ad_callback_config/config_list/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&media_source={media_source}";
        String distributor_id = params.get("distributor_id");
        long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id + params.get("secret_key") + ts);

        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("media_source", 1);
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        List<Map<String, Object>> resultList = (List<Map<String, Object>>)res.getBody().get("config_list");
        return resultList;
    }

    @Override
    public int savePromotion(JlPromotion promotion){
        tomatoMapper.savePromotion(promotion);
        return promotion.getId();
    }

    @Override
    public void updatePromotion(Map<String, Object> map){
        Gson gson = new Gson();
        map.put("error_info_json", gson.toJson((List<String>)map.get("error_info")));
        map.put("project_info_json", gson.toJson((List<String>)map.get("project_info")));
        map.put("advertising_info_json", gson.toJson((List<String>)map.get("advertising_info")));
        map.put("promotion_id_info_json", gson.toJson((List<String>)map.get("promotion_id_info")));
        map.put("promotion_name_info_json", gson.toJson((List<String>)map.get("promotion_name_info")));
        map.put("advertiser_id_info_json", gson.toJson((List<String>)map.get("advertiser_id_info")));
        tomatoMapper.updatePromotion(map);
    }
}
