package com.rabbiter.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbiter.common.Constant;
import com.rabbiter.controller.ShouzhiController;
import com.rabbiter.entity.*;
import com.rabbiter.mapper.JlaccountMapper;
import com.rabbiter.mapper.TomatoMapper;
import com.rabbiter.mapper.UserMapper;
import com.rabbiter.service.JlaccountService;
import com.rabbiter.service.TomatoService;
import com.rabbiter.service.UserService;
import com.rabbiter.util.AccessToken;
import com.rabbiter.util.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Service
public class JlaccountServiceImpl extends ServiceImpl<JlaccountMapper, JlPromotion> implements JlaccountService {
    private static final Logger LOGGER = Logger.getLogger(JlaccountServiceImpl.class.getName());

    private static final String APP_ID = "1830365792890956";

    private static final String SECRET = "d89f8cae68457fff4b954ffb31c633ab9b794f20";

    @Autowired
    private JlaccountMapper jlaccountMapper;

    @Autowired
    private TomatoMapper tomatoMapper;

    @Autowired
    private TomatoService tomatoService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public IPage listPageAutoPromotion(IPage<JlPromotion> page, Wrapper wrapper) {
        return jlaccountMapper.listPageAutoPromotion(page, wrapper);
    }

    @Override
    public void saveJlaccount(Map<String, String> map){
        List<Map<String, String>> account = jlaccountMapper.selectJlaccount(map.get("account"));
        if (account.size() == 0) {
            JSONObject data = com.rabbiter.util.AccessToken.getAccessToken(APP_ID, SECRET, map.get("authcode"));
            String access_token = ((Map<String, String>)data.get("data")).get("access_token");
            String refresh_token = ((Map<String, String>)data.get("data")).get("refresh_token");
            map.put("accesstoken", access_token);
            map.put("refreshtoken", refresh_token);
            map.put("refreshtime", Instant.now().getEpochSecond() + "");
            jlaccountMapper.saveJlaccount(map);
        } else {
            JSONObject data = com.rabbiter.util.AccessToken.refreshToken(APP_ID, SECRET, account.get(0).get("refreshtoken"));
            String access_token = ((Map<String, String>)data.get("data")).get("access_token");
            String refresh_token = ((Map<String, String>)data.get("data")).get("refresh_token");
            map.put("accesstoken", access_token);
            map.put("refreshtoken", refresh_token);
            map.put("refreshtime", Instant.now().getEpochSecond() + "");
            jlaccountMapper.updateJlaccount(map);
        }
    }

    @Override
    public String getJlaccount(String account){
        Map<String, String> accountInfo = jlaccountMapper.selectJlaccount(account).get(0);
        Long time = Instant.now().getEpochSecond();
        if (time - 23 * 3600 > Long.parseLong(accountInfo.get("refreshtime"))) {
            JSONObject data = com.rabbiter.util.AccessToken.refreshToken(APP_ID, SECRET, accountInfo.get("refreshtoken"));
            String access_token = ((Map<String, String>)data.get("data")).get("access_token");
            String refresh_token = ((Map<String, String>)data.get("data")).get("refresh_token");
            accountInfo.put("accesstoken", access_token);
            accountInfo.put("refreshtoken", refresh_token);
            accountInfo.put("refreshtime", Instant.now().getEpochSecond() + "");
            jlaccountMapper.updateJlaccount(accountInfo);
        }
        return accountInfo.get("accesstoken");
    }

    public void createPromotion(Map<String, Object> params, String projectId,
                                               Map<String, Object> advertiserInfo, int index){
        String accessToken = params.get("accessToken").toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", accessToken);

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String newName  = dealBookName(params.get("book_name").toString())
                + UUID.randomUUID().toString();
        params.put("name", newName);

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", params.get("advertiser_id"));
                put("project_id", projectId);
                put("name", newName);
//                put("operation", "DISABLE");
                Map<String, Object> promotion_materials = new HashMap();
                List<Object> video_material_list = new ArrayList<>();
                List<String> video_ids;
                if ("true".equals(params.get("distributeVideo").toString())) {
                    video_ids = distributeVideo((List<String>)params.get("video_ids"), Integer.parseInt(params.get("jlpromotion_number").toString()), index);
                } else {
                    video_ids = (List<String>)params.get("video_ids");
                }
                for (String videoId : video_ids) {
                    Map<String, Object> video_material = new HashMap();
                    video_material.put("image_mode", "CREATIVE_IMAGE_MODE_VIDEO_VERTICAL");
                    video_material.put("video_id", videoId);
                    if ("y".equals(params.get("cover").toString())) {
                        video_material.put("video_cover_id", params.get("video_cover_id"));
                    } else {
                        Map<String, String> vedioCover = (Map<String, String>)params.get("vedioCover");
                        video_material.put("video_cover_id", vedioCover.get(videoId));
                    }
                    video_material_list.add(video_material);
                }
                promotion_materials.put("video_material_list", video_material_list);

                List<Object> title_material_list = new ArrayList<>();
                for (int i=0;i<10;i++) {
                    Map<String, Object> title_material = new HashMap();
                    title_material.put("title", Constant.TITLE_LIST.get(new Random().nextInt(50))); // 创意标题
                    title_material_list.add(title_material);
                }
                promotion_materials.put("title_material_list", title_material_list);

                Map<String, Object> mini_program_info = new HashMap();

                mini_program_info.put("app_id", params.get("bidStrategy").toString().split("&")[5]);

                String start_path = ((Map<String, String>)params.get("promotion_url")).get("page");
                mini_program_info.put("start_path", start_path.startsWith("//") ? start_path.substring(1) : start_path);
                String mini_program_info_params = ((Map<String, String>)params.get("promotion_url")).get("param");
                mini_program_info.put("params", mini_program_info_params);

                promotion_materials.put("mini_program_info", mini_program_info);

                promotion_materials.put("call_to_action_buttons", (List<String>)params.get("call_to_action_buttons")); // 行动号召文案，字数限制：[2-6]，数组上限为10

                List<String> external_url_material_list = new ArrayList<>();
                if ("4".equals(params.get("radio").toString())) {
                    external_url_material_list.add("https://www.chengzijianzhan.com/tetris/page/7509926613904375862?projectid=__PROJECT_ID__&promotionid=__PROMOTION_ID__&creativetype=__CTYPE__&clickid=__CLICKID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                } else {
                    external_url_material_list.add(params.get("external_url_material_list").toString().trim());
                }
                promotion_materials.put("external_url_material_list", external_url_material_list); // 普通落地页链接素材

                Map<String, Object> product_info = new HashMap();

                List<String> titles = new ArrayList<>();
                titles.add("2025热门短剧");
                product_info.put("titles", titles);   // 产品名称，字数限制：[1-20]
                List<String> image_ids = new ArrayList<>();
                image_ids.add("tos-cn-i-sd07hgqsbj/c76af966b1f14944a44986be4ec79e96");
                product_info.put("image_ids", image_ids);  //产品主图，尺寸要求108*108，上限10个

                product_info.put("selling_points", (List<String>)params.get("product_info_selling_points")); //产品卖点，字数限制：[6-9]，数组上限为10
                promotion_materials.put("product_info", product_info);

                promotion_materials.put("intelligent_generation", "ON");

                put("promotion_materials", promotion_materials);

                put("source", getSourse(advertiserInfo.get("company").toString()));

                Map<String, String> native_setting = new HashMap();

                if ("1".equals(params.get("radio").toString()) || "3".equals(params.get("radio").toString()) || "4".equals(params.get("radio").toString())) {//单个授权抖音号id（抖音号推广身份）
                    native_setting.put("aweme_id", "81620176915");
                } else if ("2".equals(params.get("radio").toString())) {
                    native_setting.put("aweme_id", "1037413046");
                }
                native_setting.put("anchor_related_type", "AUTO");
                put("native_setting", native_setting);

                put("is_comment_disable", "ON");

                put("creative_auto_generate_switch", "OFF");
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        if ("0".equals(response.getBody().get("code").toString())) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            params.put("promotion_id", content.get("promotion_id").toString());
            Utils.addListInfoToMap(params, "advertising_info", content.get("promotion_id").toString());
            return;
        }
        if ("Internal service timed out. Please retry in sometime.".equals(response.getBody().get("message").toString())
                || "服务内部错误，请稍后重试".equals(response.getBody().get("message").toString())
                || "当前绑定商品所属商品库不存在".equals(response.getBody().get("message").toString())
                || "服务器连接超时，请您稍后重新提交".equals(response.getBody().get("message").toString())
                || "图片信息有误".equals(response.getBody().get("message").toString())
                || "原生投放设置参数有误，请检查".equals(response.getBody().get("message").toString())
                || "建站信息获取为空".equals(response.getBody().get("message").toString())
                || response.getBody().get("message").toString().startsWith("biz error: remote or network error[remote]")
                || "小程序Url legoId请求失败".equals(response.getBody().get("message").toString())
                || "服务错误，请稍后重试".equals(response.getBody().get("message").toString())
                || "Too many requests by this developer. Please retry in some time.".equals(response.getBody().get("message").toString())
                || "Too many requests. Please retry in some time.".equals(response.getBody().get("message").toString())
                || "授权校验失败，请重试".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<5;i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
                LOGGER.info("Retry createPromotion第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
                    params.put("promotion_id", content.get("promotion_id").toString());
                    Utils.addListInfoToMap(params, "advertising_info", content.get("promotion_id").toString());
                    return;
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "createPromotion:" + response.getBody().get("message").toString());
    }

    public String getSourse(String company) {
        String info = company;
        if (company.contains("(")) {
            info = info.split("\\(")[0];
        }
        if (company.contains("（")) {
            info = info.split("（")[0];
        }
        return Constant.COMPANY_INFO.get(info);
    }

    @Async
    @Override
    public Map<String, String> autoCreatePromotion(Map<String, Object> param) {
        Map<String, String> result = new HashMap<>();
        result.put("message", "success");
        LOGGER.info("autoCreatePromotion,param:" + param.toString());
        String accessToken = getJlaccount(param.get("jlaccount").toString());
        param.put("accessToken", accessToken);
        String distributor = jlaccountMapper.selectJlaccount(param.get("jlaccount").toString()).get(0).get("distributorid");
        List<String> advertiserIdList = (List<String>)param.get("advertiser_ids");
        param.put("advertiser_id", advertiserIdList.get(0));
        param.put("image", "tx.png");
        param.put("image_id", getAvatarUpload(param));
        if ("y".equals(param.get("cover").toString())) {
            param.put("image", "fm.jpg");
            param.put("video_cover_id", imageAdUpload(param));
        } else {
            Map<String, String> vedioCover = dealVedioCover(param);
            param.put("vedioCover", vedioCover);
        }
        List<String> nameList = new ArrayList<>();

        String media_source = param.get("radio").toString();
        String microGame = "";
        String ad_callback_key = "";
        if (media_source.equals("1")) {
            microGame = "抖小";
            ad_callback_key = "全回传";
        } else if("2".equals(media_source)) {
            microGame = "免费";
        } else if("3".equals(media_source)) {
            microGame = "付费";
            ad_callback_key = "全回传";
        } else if("4".equals(media_source)) {
            microGame = "微小";
            ad_callback_key = "全回传";
        }
        Map<String, String> distributorInfo = tomatoMapper.selectDistributorById(distributor).get(0);
        List<Map<String, Object>> ad_callback_config_id_list = tomatoService.getAdCallback(distributorInfo);
        List<Map<String, Object>> recharge_template_id_list = tomatoService.getRechargeTemplate(distributorInfo);
        String creater = param.get("creater").toString();
        for (int i = 0; i < advertiserIdList.size(); i++) {
            param.put("advertiser_id", advertiserIdList.get(i));
            Utils.addListInfoToMap(param, "advertiser_id_info", advertiserIdList.get(i));
            avatarSubmit(param);
            Map<String, Object> advertiserInfo= getAdvertiserInfo(param);
            String company = getSourse(advertiserInfo.get("company").toString());
            String bid_strategy = ((List<String>)param.get("bid_strategy")).get(i);
            String bidStrategyVal = Constant.BID_STRATEGY_MAP.get(company).get(bid_strategy);
            param.put("bidStrategy", bidStrategyVal);
            param.put("bidStrategyKey", bid_strategy);
            param.put("byte_micro_app_instance_id", bidStrategyVal.split("&")[3]);
            String name = creater + "-" + company + "-" +
                    dealBookName(param.get("book_name").toString()) + "-" +
                    microGame  + "-" + bid_strategy + "-" + Utils.getTime6_s();
            name = name.replace("，", "").replace(",", "");
            distributorInfo.put("media_source", media_source);
            nameList.add(name);
            distributorInfo.put("promotion_name", name);
            param.put("promotion_name", name);
            param.put("book_name", param.get("book_name").toString());
            distributorInfo.put("advertiser_id", advertiserIdList.get(i));
            updateAdvertiser(param, distributorInfo);
            distributorInfo.put("price", bidStrategyVal.split("&")[2]);
            if (media_source.equals("2")) {
                ad_callback_key = "ROI";
            }
            distributorInfo.put("ad_callback_config_id", getIdFromList(ad_callback_config_id_list, "config_name", ad_callback_key, "config_id"));
            distributorInfo.put("recharge_template_id", getIdFromList(recharge_template_id_list, "recharge_template_name", bid_strategy, "recharge_template_id"));
            distributorInfo.put("start_chapter", param.get("start_chapter").toString());
            distributorInfo.put("radio", media_source);
            distributorInfo.put("book_id", param.get("video_id").toString());
            Map<String, Object> promotionInfo = tomatoService.createPromotion(distributorInfo);
            if ("4000".equals(promotionInfo.get("code").toString())) {
                result.put("message", promotionInfo.get("message").toString());
                Utils.addListInfoToMap(param, "error_info", "tomatoCreatePromotion:" + promotionInfo.get("message").toString());
                return result;
            }
            Utils.addListInfoToMap(param, "promotion_id_info", promotionInfo.get("promotion_id").toString());
            Utils.addListInfoToMap(param, "promotion_name_info", promotionInfo.get("promotion_name").toString());
            param.put("promotion_url", getParamFromUrl(promotionInfo.get("promotion_url").toString()));
            param.put("creater", creater);
            dealWechatSite(param);
            dealAssetAndEvent(param);
            for(int x=0; x < Integer.parseInt(param.get("project_number").toString()); x++) {
                param.put("time", Utils.getTime9());
                String projectId = createProject(param, advertiserInfo, bidStrategyVal, bid_strategy);
                if(StringUtils.isEmpty(projectId)) {
                    continue;
                }
                Utils.addListInfoToMap(param, "project_info", projectId);
                param.put("project_id", projectId);
                for (int j = 0; j < Integer.parseInt(param.get("jlpromotion_number").toString()); j++) {
                    param.put("product_info_selling_points", Constant.SELLING_POINTS_LIST);
                    param.put("call_to_action_buttons", Constant.CALL_TO_ACTION_LIST);
                    param.put("time", Utils.getTime9());

                    createPromotion(param, projectId, advertiserInfo, j);
                }
                param.put("status", "2");
                tomatoService.updatePromotion(param);
            }
        }
        param.put("status", "3");
        tomatoService.updatePromotion(param);
        return result;
    }

    @Override
    public int savePromotion(Map<String, Object> param){
        JlPromotion promotion = new JlPromotion(param);
        return tomatoService.savePromotion(promotion);
    }

    private String createProject(Map<String, Object> params, Map<String, Object> advertiserInfo, String bidStrategyVal, String bid_strategy) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/project/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String advertiserId = advertiserInfo.get("id").toString();
        String name = dealBookName(params.get("book_name").toString())
                + UUID.randomUUID().toString().substring(0, 12);
        params.put("name", name);
        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", advertiserId);
                put("operation", "DISABLE");
                put("delivery_mode", "PROCEDURAL");
                put("landing_type", "MICRO_GAME");
                put("marketing_goal", "VIDEO_AND_IMAGE");
                put("ad_type", "ALL");
                put("name", name);
                Map<String, Object> delivery_setting = new HashMap<>();
                delivery_setting.put("bid_type", "CUSTOM");
                delivery_setting.put("budget_mode", "BUDGET_MODE_DAY");
                delivery_setting.put("schedule_type", "SCHEDULE_FROM_NOW");
                if ("1".equals(params.get("radio").toString())) {//单个授权抖音号id（抖音号推广身份）
                    delivery_setting .put("budget", 9999999.99);
                    delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                    delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                    delivery_setting .put("roi_goal", Double.parseDouble(bidStrategyVal.split("&")[1]));
                } else if ("2".equals(params.get("radio").toString())) {
                    delivery_setting .put("budget", Double.parseDouble(bidStrategyVal.split("&")[2]));
                    delivery_setting .put("schedule_time", "111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001111111111111111111111111111111111111111111100001");
                    if (bid_strategy.contains("ROI")) {
                        delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                        delivery_setting .put("roi_goal", Double.parseDouble(bidStrategyVal.split("&")[1]));
                    } else {
                        delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                    }
                } else if ("3".equals(params.get("radio").toString())) {
                    delivery_setting .put("budget", 9999999.99);
                    delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                } else {
                    delivery_setting .put("budget", 9999999.99);
                    delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                    delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                    delivery_setting .put("roi_goal", Double.parseDouble(bidStrategyVal.split("&")[1]));
                }
                put("delivery_setting",delivery_setting);

                Map<String, Object> related_product = new HashMap<>();
                related_product.put("product_setting", "SINGLE");
                String product_platform_id = getAvailablesId(params, advertiserId);
                related_product.put("product_platform_id", Long.parseLong(product_platform_id));
                params.put("product_platform_id", product_platform_id);
                related_product.put("product_id", getProductId(params, advertiserId));
                put("related_product",related_product);

                Map<String, Object> delivery_range = new HashMap<>();
                delivery_range.put("inventory_catalog", "UNIVERSAL_SMART");
                delivery_range.put("inventory_type", new String[]{"INVENTORY_AWEME_FEED"});
                put("delivery_range",delivery_range);
                Map<String, Object> optimize_goal = new HashMap<>();
                if("1".equals(params.get("radio").toString())) {
                    put("micro_promotion_type", "BYTE_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                    optimize_goal.put("asset_ids", new Long[]{getAssets(params, advertiserId)});//getAssets_test
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_PAY");
                    optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_PURCHASE_ROI");
                    optimize_goal.put("external_actions", new int[]{14});
                } else if ("4".equals(params.get("radio").toString())) {
                    put("micro_promotion_type", "WECHAT_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("wechatInstance_id").toString()));
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_PAY");
                    optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_PURCHASE_ROI");
                    optimize_goal.put("external_actions", new int[]{14});
                }  else if ("2".equals(params.get("radio").toString())) {
                    put("micro_promotion_type", "BYTE_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                    optimize_goal.put("asset_ids", new Long[]{getAssets(params, advertiserId)});//getAssets_test
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_GAME_ADDICTION");

                    if (bid_strategy.contains("ROI")) {
                        optimize_goal.put("external_action", "AD_CONVERT_TYPE_ACTIVE");
                        optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_LT_ROI");
                        optimize_goal.put("external_actions", new int[]{8});
                    }
                }  else if ("3".equals(params.get("radio").toString())) {
                    put("micro_promotion_type", "BYTE_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                    optimize_goal.put("asset_ids", new Long[]{getAssets(params, advertiserId)});//getAssets_test
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_PAY");
                    optimize_goal.put("external_actions", new int[]{14});
                }
                put("optimize_goal",optimize_goal);

                Map<String, Object> audience  = new HashMap<>();
                audience.put("district", "REGION");
                List<Integer> city = Arrays.asList(12, 13, 14, 15, 21, 22, 23, 31, 32, 33, 34, 35, 36, 37, 41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62, 63, 64, 65);
                audience.put("city", city);
                audience.put("region_version", "2.3.2");
                audience.put("location_type", "CURRENT");
                put("audience",audience);
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class);

        System.out.println("Response: " + response.getBody());
        if ("0".equals(response.getBody().get("code").toString())) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            return content.get("project_id").toString();
        }
        if ("服务错误，请稍后重试".equals(response.getBody().get("message").toString())
                || "服务内部错误，请稍后重试".equals(response.getBody().get("message").toString())
                || "未能全量回滚".equals(response.getBody().get("message").toString())
                || "广告主风控检查服务错误RiskDefense".equals(response.getBody().get("message").toString())
                || "服务器连接超时，请您稍后重新提交".equals(response.getBody().get("message").toString())
                || "商品库不存在".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
                LOGGER.info("Retry createDpaProduct第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
                    return content.get("project_id").toString();
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "createProject:" + response.getBody().get("message").toString());
        return "";
    }

    public void dealAssetAndEvent(Map<String, Object> param){
        if ("4".equals(param.get("radio").toString())) {
            return;
        }
        try {
            String asset_id = createAssets(param);
            if (!StringUtils.isEmpty(asset_id)) {
                param.put("asset_id", asset_id);
                createEvents(param);
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    private String dealBookName(String bookname) {
        return bookname.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]", "");
    }

    @Override
    public Map<String, Object> getAdvertiserInfo(Map<String, Object> params){
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/advertiser/info/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        Map data = new HashMap(){
            {
                put("advertiser_ids", new Long[] {Long.parseLong(params.get("advertiser_id").toString())});
                put("fields", new String[] {"id", "name", "status", "company"});
            }
        };
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        httpEntity.setHeader("Access-Token", getJlaccount(params.get("jlaccount").toString()));

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();
            httpEntity.setURI(uri);
            httpEntity.setEntity(new StringEntity(JSONObject.toJSONString(data), ContentType.APPLICATION_JSON));

            response = client.execute(httpEntity);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                if ("0".equals(JSONObject.parseObject(Utils.decodeUnicode(result.toString())).get("code").toString())) {
                    return ((List<Map<String, Object>>)(JSONObject.parseObject(Utils.decodeUnicode(result.toString())).get("data"))).get(0);
                } else {
                    Map<String, Object> res = new HashMap<>();
                    res.put("message", "主体" + params.get("jlaccount").toString() + "与账户" + params.get("advertiser_id").toString() + "不匹配");
                    return res;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getVideoList(Map<String, Object> params){
        List<Map<String, Object>> linkList = new ArrayList<>();
        for (int i=1;i<3;i++) {
            List<Map<String, Object>> list = getVideoList(params, i);
            if (list == null || list.size() == 0) {
                break;
            }
            linkList.addAll(list);
        }
        return linkList;
    }

    public List<Map<String, Object>> getVideoList(Map<String, Object> params, int index){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", getJlaccount(params.get("jlaccount").toString()));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/file/video/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("page", index)
                .queryParam("page_size", 100)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
        if ("0".equals(response.getBody().get("code").toString())) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        Utils.addListInfoToMap(params, "error_info", "getVideoList:" + response.getBody().get("message").toString());
        return new ArrayList<>();
    }

    @Override
    public  Map<String, Object> bindVideo(Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> subjectList = (List<String>)params.get("subject");
        List<Map<String, Object>> videoInfoList = null;
        int videoSize = 0;
        for (int i=1; i <= subjectList.size(); i++) {
            headers.set("Access-Token", getJlaccount(subjectList.get(i-1)));
            String url = "https://ad.oceanengine.com/open_api/2/file/material/bind/";
            List<String> video_list = new ArrayList<>();
            params.put("advertiser_id", params.get("subjectvideo" + i));
            params.put("jlaccount", subjectList.get(i-1));
            videoInfoList = getVideoList(params);
            if (i==1) {
                videoSize = videoInfoList.size();
            }
            for (Map<String, Object> videoInfo : videoInfoList) {
                video_list.add(videoInfo.get("id").toString());
            }

            List<Long> advertiser_ids = new ArrayList<>();
            for (String advertiser_id :(List<String>)params.get("advertiser_id" + i + "s")) {
                advertiser_ids.add(Long.parseLong(advertiser_id));
            }
            final String advertiser_id = params.get("subjectvideo" + i).toString();
            Map param = new HashMap() {
                {
                    put("advertiser_id", Long.parseLong(advertiser_id));
                    put("target_advertiser_ids", advertiser_ids);
                    List<String> videoList = video_list;
                    if(videoList.size() > 50) {
                        videoList = videoList.subList(0, 50);
                    }
                    put("video_ids", videoList);
                }
            };
            HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
            ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            LOGGER.info(res.getBody().toString());
        }

        List<Map<String, Object>> minVideoList = new ArrayList<>();
        for (int i=1; i<=subjectList.size(); i++) {
            List<String> advertiser_ids = (List<String>) params.get("advertiser_id" + i + "s");
            for (int j = 0;j<advertiser_ids.size();j++) {
                params.put("advertiser_id", advertiser_ids.get(j));
                params.put("jlaccount", subjectList.get(i-1));
                List<Map<String, Object>> videoList = getVideoList(params);
                if (minVideoList.size() == 0 && i==1 & j ==0) {
                    minVideoList = videoList;
                    continue;
                }
                minVideoList = dealMinVideoList(minVideoList, videoList);
            }
        }
        result.put("data", minVideoList);
        result.put("videoSizeMax", videoSize);
        result.put("videoSizeMin", minVideoList.size());
        return result;
    }

    private List<Map<String, Object>> dealMinVideoList(List<Map<String, Object>> list1, List<Map<String, Object>> list2) {
        List<Map<String, Object>> minVideoList = new ArrayList<>();
        for (Map<String, Object> map1 : list1) {
            for (Map<String, Object> map2 : list2) {
                if (map1.get("material_id").toString().equals(map2.get("material_id").toString())) {
                    minVideoList.add(map1);
                    break;
                }
            }
        }
        return minVideoList;
    }

    private Map<String, String> dealVedioCover(Map<String, Object> params) {
        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> videoInfoList = (List<Map<String, Object>>)params.get("video_list");
        List<String> selectedVideoId = (List<String>)params.get("video_ids");
        for (int i=0; i<8 ; i++) {
            for (Map<String, Object> videoInfo: videoInfoList) {
                if (!selectedVideoId.contains(videoInfo.get("id").toString())) {
                    continue;
                }
                ResponseEntity<Map> response = getVideoCover(params, params.get("advertiser_id").toString(), videoInfo.get("id").toString());
                if (null != response.getBody().get("data") && "SUCCESS".equals(((Map<String, String>)response.getBody().get("data")).get("status"))) {
                    List<Map<String, Object>> list = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
                    if (list.size()>0) {
                        result.put(videoInfo.get("id").toString(), list.get(0).get("id").toString());
                    }
                } else {
                    Utils.addListInfoToMap(params, "error_info", "dealVedioCover:" + response.getBody().get("message").toString());
                }
            }
            if (result.size() == selectedVideoId.size()) {
                return result;
            }
            try {
                LOGGER.info("线程暂停1分钟");
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                LOGGER.info("线程被中断");
                Thread.currentThread().interrupt(); // 重新设置中断状态，以便调用者可以知道发生了中断
            }
        }
        return result;
    }

    private ResponseEntity<Map> getVideoCover(Map<String, Object> params, String advertiser_id, String video_id){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/tools/video_cover/suggest/")
                .queryParam("advertiser_id", advertiser_id)
                .queryParam("video_id", video_id)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        return response;
    }

    private String getAvailablesId(Map<String, Object> params, String advertiser_id){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/product/availables/")
                .queryParam("advertiser_id", advertiser_id)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );


        List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        for (Map<String, Object> svailables : resultList) {
            if ("通用".equals(svailables.get("name").toString())) {
                return svailables.get("platform_id").toString();
            }
        }
        return "7281789144008467";
    }

    private String getMicroLink(Map<String, Object> params){
        List<Map<String, Object>> linkList = new ArrayList<>();
        for (int i=1;i<50;i++) {
            List<Map<String, Object>> list = getAssetLinkFromHttp(params, i);
            if (list == null || list.size() == 0) {
                break;
            }
            linkList.addAll(list);
        }

        for (Map<String, Object> linkInfo : linkList) {
            if (dealBookName(params.get("book_name").toString()).equals(linkInfo.get("link_remark").toString())) {
                return linkInfo.get("link").toString();
            }
        }
        return "";
    }

    private String getProductId(Map<String, Object> params, String advertiser_id){
        List<Map<String, Object>> productList = new ArrayList<>();
        for (int i=1;i<50;i++) {
            List<Map<String, Object>> list = getProductInfoFromHttp(params, advertiser_id, i);
            productList.addAll(list);
            if (list.size() == 0) {
                break;
            }
        }

        for (Map<String, Object> productInfo : productList) {
           if (params.get("book_name").toString().equals(productInfo.get("name").toString())) {
               String microLink = getMicroLink(params);
               if(!StringUtils.isEmpty(microLink)) {
                   params.put("microLink", microLink);
               }
               return productInfo.get("product_id").toString();
           }
        }

        updateMicroApp(params);
        try {
            LOGGER.info("线程暂停5秒");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.info("线程被中断");
            Thread.currentThread().interrupt(); // 重新设置中断状态，以便调用者可以知道发生了中断
        }

        String microLink = getMicroLink(params);
        params.put("microLink", microLink);
        String product_id = createDpaProduct(params);
        try {
            LOGGER.info("线程暂停60秒");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            LOGGER.info("线程被中断");
            Thread.currentThread().interrupt(); // 重新设置中断状态，以便调用者可以知道发生了中断
        }
        return product_id;
    }

    /**
     * 修改小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String updateMicroApp(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/micro_app/update/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("bidStrategy").toString().split("&")[4]));
                put("instance_id", Long.parseLong(params.get("bidStrategy").toString().split("&")[3]));
                List<Map<String, Object>> app_page_list = new ArrayList<>();
                Map<String, Object> app_page = new HashMap<>();
                app_page.put("link", "");
                app_page.put("operate_type", "NEW");
                app_page.put("start_page", "pages/theatre/index");
                app_page.put("start_param", ((Map<String, String>)params.get("promotion_url")).get("param"));
                app_page.put("remark", dealBookName(params.get("book_name").toString()));
                app_page_list.add(app_page);
                put("app_page",app_page_list);

                put("tag_info", "1050107001");
            }
        };


        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );
        return response.getBody().toString();
    }

    /**
     * 修改小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private List<Map<String, Object>> getAssetLinkFromHttp(Map<String, Object> params, int i){

        Long advertiser_id = Long.parseLong(params.get("bidStrategy").toString().split("&")[4]);
        Long instance_id = Long.parseLong(params.get("bidStrategy").toString().split("&")[3]);
        if ("4".equals(params.get("radio").toString())) {
            instance_id = Long.parseLong(params.get("wechatInstance_id").toString());
        }
        int page = i;
        int page_size = 100;
        String myArgs = String.format("{\"advertiser_id\": \"%s\", \"filtering\": {\"instance_id\": %s}, \"page\": \"%s\", \"page_size\": \"%s\"}",advertiser_id, instance_id, page, page_size);
        try {
            ObjectMapper mapper = new ObjectMapper();
            URIBuilder ub = new URIBuilder("https://api.oceanengine.com/open_api/v3.0/tools/asset_link/list/");
            Map< String, Object > map = mapper.readValue(myArgs, Map.class);
            map.forEach((k, v) -> {
                try {
                    ub.addParameter(k, v instanceof String ? (String) v : mapper.writeValueAsString(v));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            URL url = ub.build().toURL();

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder().url(url.toString()).method("GET", null).addHeader("Access-Token", params.get("accessToken").toString()).build();
            okhttp3.Response response = client.newCall(request).execute();
            JSONObject json  = JSONObject.parseObject(response.body().string());
            if ("0".equals((json.get("code").toString()))) {
                return ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
            }
            if ("Too many requests by this developer. Please retry in some time.".equals(json.get("message").toString())) {
                for(int x = 0;x<5;x++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    response = client.newCall(request).execute();
                    json  = JSONObject.parseObject(response.body().string());
                    LOGGER.info("Retry getAssetLinkFromHttp第" +(x+1)+ "次, message:" + json.get("message").toString());
                    if ("0".equals((json.get("code").toString()))) {
                        return ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
                    }
                }
            }
            Utils.addListInfoToMap(params, "error_info", "getAssetLinkFromHttp:" + json.get("message").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> getProductInfoFromHttp(Map<String, Object> params, String advertiser_id,
                                                             int index){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());
        Map<String, String> filtering = new HashMap<>();
        filtering.put("product_name", params.get("book_name").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/detail/get/")
                .queryParam("advertiser_id", advertiser_id)
                .queryParam("product_platform_id", params.get("product_platform_id").toString())
                .queryParam("page", index)
                .queryParam("page_size", 100)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        if ("0".equals(response.getBody().get("code").toString())) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        if ("广告主没有该商品库的权限".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
                LOGGER.info("Retry getProductInfoFromHttp第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "getProductInfoFromHttp:" + response.getBody().get("message").toString());
        return  new ArrayList<>();
    }

    private Long getAssets(Map<String, Object> params, String advertiser_id){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/tools/event/all_assets/list/")
                .queryParam("advertiser_id", advertiser_id)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        if ("OK".equals(response.getBody().get("message")) && null != response.getBody().get("data")) {
            List<Map<String, Object>> res = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("asset_list");
            if (res.size() > 0) {
                return (Long)res.get(0).get("asset_id");
            }
        }
        if ("Too many requests. Please retry in some time.".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(
                        uri, HttpMethod.GET, requestEntity, Map.class
                );
                LOGGER.info("Retry getAssets第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("OK".equals(response.getBody().get("message")) && null != response.getBody().get("data")) {
                    List<Map<String, Object>> res = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("asset_list");
                    if (res.size() > 0) {
                        return (Long)res.get(0).get("asset_id");
                    }
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "getAssets:" + response.getBody().get("message").toString());
        return new Long(0);
    }

    private String updateAdvertiser(Map<String, Object> param1, Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", param1.get("accessToken").toString());
        String url = "https://ad.oceanengine.com/open_api/2/agent/advertiser/update/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("name", params.get("promotion_name"));
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        if ("OK".equals(response.getBody().get("message")) && null != response.getBody().get("data")) {
            response.getBody().toString();
            return response.getBody().toString();
        }
        if ("服务内部错误，请稍后重试".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
                LOGGER.info("Retry updateAdvertiser第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("OK".equals(response.getBody().get("message")) && null != response.getBody().get("data")) {
                    response.getBody().toString();
                    return response.getBody().toString();
                }
            }
        }
        Utils.addListInfoToMap(param1, "error_info", "updateAdvertiser:" + response.getBody().get("message").toString());
        return response.getBody().toString();
    }

   private String getIdFromList(List<Map<String, Object>> list, String key, String value, String resKey) {
        for (Map<String, Object> map : list) {
            if(value.equals(map.get(key))) {
                return map.get(resKey).toString();
            }
        }
        return "";
   }

    /**
     * 上传头像
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String getAvatarUpload(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Access-Token", params.get("accessToken").toString());
        String url = "https://api.oceanengine.com/open_api/2/advertiser/avatar/upload/";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
        try {
//            File file= new File("/etc/image/" + params.get("image").toString());
            File file= new ClassPathResource("image/" + params.get("image").toString()).getFile();
            body.add("image_file", new FileSystemResource(file)); // 添加文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送POST请求
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        LOGGER.info(response.getBody().toString());
        if ("OK".equals(response.getBody().get("message"))) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            return content.get("image_id").toString();
        } else {
            Utils.addListInfoToMap(params, "error_info", "getAvatarUpload:" + response.getBody().get("message").toString());
        }
        return "";
    }

    /**
     * 上传头像
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String imageAdUpload(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Access-Token", params.get("accessToken").toString());
        String url = "https://api.oceanengine.com/open_api/2/file/image/ad/";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
        body.add("upload_type", "UPLOAD_BY_FILE");
        try {
//            File file= new File("/etc/image/" +  params.get("image").toString());
            File file= new ClassPathResource("image/" + params.get("image").toString()).getFile();
            body.add("image_signature", Utils.getFileMD5(file));
            body.add("image_file", new FileSystemResource(file)); // 添加文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送POST请求
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        LOGGER.info(response.getBody().toString());
        if ("OK".equals(response.getBody().get("message"))) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            return content.get("id").toString();
        } else {
            Utils.addListInfoToMap(params, "error_info", "imageAdUpload:" + response.getBody().get("message").toString());
        }
        return "";
    }

    /**
     * 设置头像
     *
     * @param list:Args in JSON format
     * @return Response in JSON format
     */
    private List<String> distributeVideo(List<String> list, int num, int index){
        List<String> result = new ArrayList<>();
        if (list.size() == 0) {
            return result;
        }
        while(list.size() < num) {
            List<String> subList = list.size() < num-list.size() ? list:list.subList(0, num-list.size());
            list.addAll(subList);
        }

        for (int i = 0;i<list.size() ;i++) {
            if (i%num == 0 && i+index <list.size()) {
                result.add(list.get(i+index));
            }
        }
        return result;
    }

    /**
     * 设置头像
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String avatarSubmit(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());
        String url = "https://api.oceanengine.com/open_api/2/advertiser/avatar/submit/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("image_id", params.get("image_id").toString());
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        if ("0".equals(response.getBody().get("code").toString())) {
            return response.getBody().toString();
        }

        if ("Internal service timed out. Please retry in sometime.".equals(response.getBody().get("message").toString())
                ||"审核中的头像不允许修改".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
                LOGGER.info("Retry avatarSubmit第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    return response.getBody().toString();
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "avatarSubmit:" + response.getBody().get("message").toString());
        return "";
    }

    /**
     * 创建资产
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String createAssets(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/assets/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("asset_type", "MINI_PROGRAME");

                Map<String, Object> mini_program_asset = new HashMap<>();
                mini_program_asset.put("mini_program_id", params.get("bidStrategy").toString().split("&")[5]);
                mini_program_asset.put("mini_program_name", params.get("bidStrategy").toString().split("&")[6]);
                mini_program_asset.put("instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                mini_program_asset .put("mini_program_type", "BYTE_APP");
                put("mini_program_asset",mini_program_asset);

            }
        };
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
        if ("0".equals(response.getBody().get("code").toString())) {
            Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
            return result.get("asset_id").toString();
        }
        if ("服务器错误".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<3;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
                LOGGER.info("Retry createAssets第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
                    return result.get("asset_id").toString();
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "createAssets:" + response.getBody().get("message").toString());
        return "";
    }

    /**
     * 创建事件
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String createEvents(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/events/create/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("asset_id", Long.parseLong(params.get("asset_id").toString()));
                if ("1".equals(params.get("radio").toString()) || "3".equals(params.get("radio").toString())) {
                    put("event_id", 14);
                } else if ("2".equals(params.get("radio").toString())) {
                    if (params.get("bidStrategyKey").toString().contains("ROI")) {
                        put("event_id", 8);
                    } else {
                        put("event_id", 25);
                    }
                }
                put("track_types", new String[]{"MINI_PROGRAME_API"});
            }
        };
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
        response = retryCreateEvents(response, uri, requestEntity);
        if (!"0".equals(response.getBody().get("code").toString())) {
            Utils.addListInfoToMap(params, "error_info", "createEvents:" + response.getBody().get("message").toString());
        }
        body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("asset_id", Long.parseLong(params.get("asset_id").toString()));
                if ("1".equals(params.get("radio").toString())) {
                    put("event_id", 160);
                } else if ("2".equals(params.get("radio").toString())) {
                    put("event_id", 110);
                }
                put("track_types", new String[]{"MINI_PROGRAME_API"});
            }
        };
        requestEntity = new HttpEntity<>(body, headers);

        if ("1".equals(params.get("radio").toString())) {
            response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
            response = retryCreateEvents(response, uri, requestEntity);
            if (!"0".equals(response.getBody().get("code").toString())) {
                Utils.addListInfoToMap(params, "error_info", "createEvents:" + response.getBody().get("message").toString());
            }
        } else if ("2".equals(params.get("radio").toString()) && params.get("bidStrategyKey").toString().contains("ROI")) {
            response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
            response = retryCreateEvents(response, uri, requestEntity);
            if (!"0".equals(response.getBody().get("code").toString())) {
                Utils.addListInfoToMap(params, "error_info", "createEvents:" + response.getBody().get("message").toString());
            }
        }

        return "";
    }

    /**
     * 重试创建事件
     *
     * @return Response in JSON format
     */
    private ResponseEntity<Map> retryCreateEvents(ResponseEntity<Map> response, URI uri, HttpEntity<Map> requestEntity) {
        if ("Too many requests. Please retry in some time.".equals(response.getBody().get("message").toString())) {
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
                LOGGER.info("Retry CreateEvents第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    break;
                }
            }
        }
        return response;
    }

    /**
     * 获取事件
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private List<Map<String, Object>> getAssetConfigs( Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/event_configs/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("asset_id", 1831263424525322L)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("event_configs");
        }
        return new ArrayList<>();
    }

    private Map<String, String> getParamFromUrl(String url) {
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isEmpty(url)) {
            return result;
        }
        result.put("page", url.split("\\?")[0]);
        result.put("param", url.split("\\?")[1]);
        for (String info: result.get("param").split("&")) {
            String[] infos = info.split("=");
            result.put(infos[0], infos[1]);
        }
        return result;
    }

    /**
     * 创建DPA商品
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String createDpaProduct(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/product/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                String carrier = params.get("radio").toString();
                put("advertiser_id", Long.parseLong(params.get("bidStrategy").toString().split("&")[4]));
                put("platform_id", Long.parseLong(params.get("product_platform_id").toString()));

                Map<String, Object> product_info = new HashMap<>();
                product_info.put("name", params.get("book_name").toString());
                product_info.put("image_url", "ad-private-platform-dpa/3528714d7c7df78c5d5db3be4c1c08f8");
                product_info.put("first_category", "短剧");
                product_info.put("sub_category", "其他剧情");
                product_info.put("third_category", "其他");
                product_info.put("first_category_id", "2019");
                product_info.put("sub_category_id", "201912");
                product_info.put("third_category_id", "20191201");
                if ("2".equals(carrier)) {
                    product_info.put("first_category", "单剧目");
                    product_info.put("sub_category", "其他剧情");
                    product_info.put("third_category", "其他");
                    product_info.put("first_category_id", "2038");
                    product_info.put("sub_category_id", "203807");
                    product_info.put("third_category_id", "20380701");
                }

                Map<String, Object> price_info = new HashMap<>();
                price_info .put("price", 2);
                product_info.put("price_info", price_info);

                Map<String, Object> profession = new HashMap<>();
                profession .put("micro_app_link", params.get("microLink").toString());

                profession .put("has_paid_content", "1");
                profession .put("has_motivation_content", "1");
                profession .put("copyright_owner", "抖音视界有限公司");
                profession .put("playlet_gender", "3");
                profession .put("playlet_num", "50");
                profession .put("playlet_duration", "2");
                profession .put("start_pay_playlet", "10");
                profession .put("membership_types", "2");
                if ("2".equals(carrier)) {
                    profession .put("starting_unlock_episode", "8");
                } else if ("1".equals(carrier)) {
                    profession .put("ad_carrier", "[\"字节小程序\"]");
                }
                product_info.put("profession", profession);
                put("product_info", product_info);
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
        if ("0".equals(response.getBody().get("code").toString())) {
            Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
            return result.get("product_id").toString();
        }
        if ("Too many requests. Please retry in some time.".equals(response.getBody().get("message").toString())) {
            for(int i = 0;i<5;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Map.class);
                LOGGER.info("Retry createDpaProduct第" +(i+1)+ "次, message:" + response.getBody().get("message").toString());
                if ("0".equals(response.getBody().get("code").toString())) {
                    Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
                    return result.get("product_id").toString();
                }
            }
        }
        Utils.addListInfoToMap(params, "error_info", "createDpaProduct:" + response.getBody().get("message").toString());
        return "";
    }

    private void dealWechatSite(Map<String, Object> param) {
        if (!"4".equals(param.get("radio").toString())) {
            return;
        }
        String instance_id = createWechatApplet(param);
        param.put("wechatInstance_id", instance_id);
        for(int i=0;i<100;i++) {
            if(getWechatAppletStatus(param)) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        param.put("image", "bj.jpg");
        param.put("siteBj", imageAdUpload(param));
        String siteId = createSite(param);
        param.put("siteId", siteId);
        updateSiteStatus(param);
//        for(int i=0;i<100;i++) {
//            if(getSiteStatus(param)) {
//                break;
//            }
//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }

        String siteUrl = "https://www.chengzijianzhan.com/tetris/page/";
        siteUrl += siteId;
        siteUrl += "?projectid=__PROJECT_ID__&promotionid=__PROMOTION_ID__&creativetype=__CTYPE__&clickid=__CLICKID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__";
        param.put("wechatSiteUrl", siteUrl);
    }

    /**
     * 创建微信小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String createWechatApplet(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/wechat_applet/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("name", params.get("bidStrategy").toString().split("&")[6]);
                put("user_name", params.get("bidStrategy").toString().split("&")[5]);
                put("tag_info", "1030700000");
                put("remark_message", params.get("book_name").toString());
                put("path", params.get("promotion_url").toString());
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        if ("0".equals(response.getBody().get("code").toString())){
            return ((Map<String, Map<String, Map<String, Object>>>)response.getBody()).get("data").get("data").get("instance_id").toString();
        }
        Utils.addListInfoToMap(params, "error_info", "createWechatApplet:" + response.getBody().get("message").toString());
        return "";
    }

    /**
     * 创建落地页
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String createSite(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        String url = "https://ad.oceanengine.com/open_api/2/tools/site/create/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("name", params.get("book_name").toString());
                List<Object> bricks = new ArrayList<>();
                Map<String, Object> xrPicture = new HashMap<>();
                xrPicture.put("name", "XrPicture");
                xrPicture.put("ic_id", params.get("siteBj").toString());
                xrPicture.put("height", 500);
                bricks.add(xrPicture);

                Map<String, Object> xrSimpleText = new HashMap<>();
                xrSimpleText.put("name", "XrSimpleText");
                xrSimpleText.put("content", "抖音视界有限公司");
                xrSimpleText.put("height", 20);
                xrSimpleText.put("y", 127);
                xrSimpleText.put("text_align", "center");
                xrSimpleText.put("color", "white");
                xrSimpleText.put("float", "bottom");
                bricks.add(xrSimpleText);

                Map<String, Object> xrWechatApplet = new HashMap<>();
                xrWechatApplet.put("name", "XrWechatApplet");
                xrWechatApplet.put("instance_id", Long.parseLong(params.get("wechatInstance_id").toString()));
                Map<String, Object> setting = new HashMap<>();
                setting.put("style_type", 2);
                xrWechatApplet.put("setting", setting);
                xrWechatApplet.put("height", 100);
                bricks.add(xrWechatApplet);

                put("bricks", bricks);
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(response.getBody().toString());
        if ("0".equals(response.getBody().get("code").toString())){
            return ((Map<String, Map<String, Object>>)response.getBody()).get("data").get("site_id").toString();
        }
        Utils.addListInfoToMap(params, "error_info", "createPromotion:" + response.getBody().get("message").toString());
        return "";
    }

    /**
     * 修改落地页状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String updateSiteStatus(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        String url = "https://ad.oceanengine.com/open_api/2/tools/site/update_status/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("site_ids", new String[]{params.get("siteId").toString()});
                put("status", "published");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody().toString();
    }

    /**
     * 获取落地页状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private boolean getWechatAppletStatus(Map<String, Object> params){
        Long advertiser_id = Long.parseLong(params.get("advertiser_id").toString());
        String myArgs = String.format("{\"advertiser_id\": \"%s\", \"filtering\": {\"name\": \"%s\", \"audit_status\": \"%s\", \"search_type\": \"%s\"}, \"page\": \"%s\", \"page_size\": \"%s\"}",
                advertiser_id, params.get("book_name").toString(), "AUDIT_ACCEPTED", "CREATE_ONLY", 1, 100);
        JSONObject json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            URIBuilder ub = new URIBuilder("https://api.oceanengine.com/open_api/v3.0/tools/wechat_applet/list/");
            Map< String, Object > map = mapper.readValue(myArgs, Map.class);
            map.forEach((k, v) -> {
                try {
                    ub.addParameter(k, v instanceof String ? (String) v : mapper.writeValueAsString(v));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            URL url = ub.build().toURL();

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(url.toString())
                    .method("GET", null)
                    .addHeader("Access-Token", params.get("accessToken").toString())
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            json = JSONObject.parseObject(response.body().string());

            if ("0".equals((json.get("code").toString()))) {
                List<Map<String, Object>> list= ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
                for(Map<String, Object> info : list){
                    if (params.get("wechatInstance_id").toString().equals(info.get("instance_id").toString())) {
                        return true;
                    }
                }
            }
            if ("Too many requests by this developer. Please retry in some time.".equals(json.get("message").toString())) {
                for(int x = 0;x<5;x++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    response = client.newCall(request).execute();
                    json  = JSONObject.parseObject(response.body().string());
                    LOGGER.info("Retry getSiteStatus第" +(x+1)+ "次, message:" + json.get("message").toString());
                    if ("0".equals((json.get("code").toString()))) {
                        List<Map<String, Object>> list= ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
                        for(Map<String, Object> info : list){
                            if (params.get("wechatInstance_id").toString().equals(info.get("instance_id").toString())) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.addListInfoToMap(params, "error_info", "createPromotion:" + json.get("message").toString());
        return false;
    }

    /**
     * 获取落地页状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private boolean getSiteStatus(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/tools/site/read/")
                .queryParam("advertiser_id", params.get("advertiser_id").toString())
                .queryParam("site_id", params.get("siteId").toString())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            if ("enable".equals(((Map<String, String>)response.getBody().get("data")).get("status"))) {
                return true;
            }
        }
        return false;
    }

}
