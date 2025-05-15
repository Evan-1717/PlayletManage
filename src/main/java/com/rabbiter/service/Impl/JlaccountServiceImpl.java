package com.rabbiter.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbiter.common.Constant;
import com.rabbiter.controller.ShouzhiController;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.User;
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

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public IPage listPagePromotion(IPage<JlPromotion> page) {
        return jlaccountMapper.listPagePromotion(page);
    }

    @Override
    public IPage listPageAutoPromotion(IPage<Map<String, String>> page) {
        return jlaccountMapper.listPageAutoPromotion(page);
    }

    @Override
    public IPage listPageProject(IPage<Map<String, String>> page) {
        return jlaccountMapper.listPageProject(page);
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


    public static JSONObject getAdvertiserFundDailyStat(String advertiser_id, String start_date, String end_date, String access_token) {
        // 请求地址
        String open_api_url_prefix = "https://ad.oceanengine.com/open_api/2/";
        String uri = "advertiser/fund/daily_stat/";

        // 请求参数
        Map data = new HashMap() {
            {
                put("advertiser_id", advertiser_id);
                put("start_date", start_date);
                put("end_date", end_date);
                put("page", 1);
                put("page_size", 1000);
            }
        };

        // 构造请求
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "GET";
            }
        };

        httpEntity.setHeader("Access-Token", access_token);

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;

        try {
            client = HttpClientBuilder.create().build();
            httpEntity.setURI(URI.create(open_api_url_prefix + uri));
            httpEntity.setEntity(new StringEntity(JSONObject.toJSONString(data), ContentType.APPLICATION_JSON));

            response = client.execute(httpEntity);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                return JSONObject.parseObject(result.toString());
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
        return null;
    }


    public List<String> getProject(Map<String, String> params){
        String accessToken = getJlaccount(params.get("jlaccount"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", accessToken);

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/project/list/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );
        List<Map<String, Object>> responseList = new ArrayList<>();
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            responseList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        List<String> resultList = new ArrayList<>();
        for (Map<String, Object> map : responseList) {
            resultList.add(map.get("project_id").toString());
        }
        return resultList;
    }

    public Map<String, String> createPromotion(Map<String, Object> params, String projectId,
                                               Map<String, Object> advertiserInfo){

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
                + UUID.randomUUID().toString().substring(0, 8);
        params.put("name", newName);

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", params.get("advertiser_id"));
                put("project_id", projectId);
                put("name", newName);
//                put("operation", "DISABLE");
                Map<String, Object> promotion_materials = new HashMap();
                List<Object> video_material_list = new ArrayList<>();
                for (String videoId: (List<String>)params.get("video_ids")) {
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
                // 字节小程序调起链接
                if ("1".equals(params.get("radio").toString())) {
                    mini_program_info.put("app_id", "ttb1d2c76f2ee36a0601");
                } else if ("7".equals(params.get("radio").toString())) {
                    mini_program_info.put("app_id", "tt7d2a0b97e21cb1a001");
                }

                String start_path = ((Map<String, String>)params.get("promotion_url")).get("page");
                mini_program_info.put("start_path", start_path.startsWith("//") ? start_path.substring(1) : start_path);
                String mini_program_info_params = ((Map<String, String>)params.get("promotion_url")).get("param");
                mini_program_info.put("params", mini_program_info_params);

                promotion_materials.put("mini_program_info", mini_program_info);

                promotion_materials.put("call_to_action_buttons", (List<String>)params.get("call_to_action_buttons")); // 行动号召文案，字数限制：[2-6]，数组上限为10

                List<String> external_url_material_list = new ArrayList<>();
                external_url_material_list.add(params.get("external_url_material_list").toString().trim());
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

                if ("1".equals(params.get("radio").toString())) {//单个授权抖音号id（抖音号推广身份）
                    native_setting.put("aweme_id", "81620176915");
                } else if ("7".equals(params.get("radio").toString())) {
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

        Map<String, String> result = new HashMap<>();
        result.put("message", response.getBody().get("message").toString());
        if ("0".equals(response.getBody().get("code").toString())) {
            Gson gson = new Gson();
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            params.put("promotion_id", content.get("promotion_id").toString());
            result.put("promotion_id", content.get("promotion_id").toString());
            params.put("title_json", gson.toJson(((List<String>)params.get("title"))));
            params.put("call_to_action_buttons_json", gson.toJson((List<String>)params.get("call_to_action_buttons")));
            params.put("external_url_material_list_json", gson.toJson(Arrays.asList(new String[]{(String)params.get("external_url_material_list")})));
            params.put("product_info_selling_points_json", gson.toJson((List<String>)params.get("product_info_selling_points")));
            jlaccountMapper.saveJlPromotion(params);
        }
        LOGGER.info("**** createPromotion Response: " + response.getBody());
        return result;
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

    @Override
    public Map<String, String> autoCreatePromotion(Map<String, Object> param) {
        Map<String, String> result = new HashMap<>();
        result.put("message", "success");
        LOGGER.info("autoCreatePromotion,param:" + param.toString());
        String accessToken = getJlaccount(param.get("jlaccount").toString());
        param.put("accessToken", accessToken);
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
        String microGame;
        String distributor;
        String ad_callback_key = "";
        if (media_source.equals("1")) {
            microGame = "抖小";
            distributor = "distributorId_b";
            ad_callback_key = "全回传";
        } else if("4".equals(media_source)) {
            microGame = "微小";
            distributor = "distributorId_w";
        } else {
            microGame = "免费";
            distributor = "distributorId_f";
            ad_callback_key = "番茄iaa-超低";
        }
        Map<String, String> distributorInfo = tomatoMapper.selectDistributorById((String)param.get(distributor)).get(0);
        distributorInfo.put("book_id", param.get("video_id").toString());
        Map<String, Object> videoInfo = tomatoService.getVideoInfo(distributorInfo);
        if (videoInfo.size() == 0) {
            result.put("message", "获取短剧信息失败！");
            return result;
        }

        List<Map<String, Object>> ad_callback_config_id_list = tomatoService.getAdCallback(distributorInfo);
        distributorInfo.put("ad_callback_config_id", getIdFromList(ad_callback_config_id_list, "config_name", ad_callback_key, "config_id"));
        List<Map<String, Object>> recharge_template_id_list = tomatoService.getRechargeTemplate(distributorInfo);
        String creater = param.get("creater").toString();
        for (int i = 0; i < advertiserIdList.size(); i++) {
            param.put("advertiser_id", advertiserIdList.get(i));
            getAvatarSubmit(param);
            Map<String, Object> advertiserInfo= getAdvertiserInfo(param);
            String company = getSourse(advertiserInfo.get("company").toString());
            String bid_strategy = ((List<String>)param.get("bid_strategy")).get(i);
            String bidStrategyVal = Constant.BID_STRATEGY_MAP.get(company).get(bid_strategy);
            param.put("bidStrategy", bidStrategyVal);
            param.put("bidStrategyKey", bid_strategy);
            param.put("byte_micro_app_instance_id", bidStrategyVal.split("&")[3]);
            dealAssetAndEvent(param);
            String name = creater + "-" + company + "-" +
                    dealBookName(videoInfo.get("book_name").toString()) + "-" +
                    microGame  + "-" + bid_strategy + "-" + Utils.getTime6_s();
            name = name.replace("，", "").replace(",", "");
            distributorInfo.put("media_source", media_source);
            nameList.add(name);
            distributorInfo.put("promotion_name", name);
            param.put("promotion_name", name);
            param.put("book_name", videoInfo.get("book_name").toString());
            distributorInfo.put("advertiser_id", advertiserIdList.get(i));
            updateAdvertiser(param, distributorInfo);
            distributorInfo.put("price", bidStrategyVal.split("&")[2]);
            distributorInfo.put("recharge_template_id", getIdFromList(recharge_template_id_list, "recharge_template_name", bid_strategy, "recharge_template_id"));
            distributorInfo.put("start_chapter", param.get("start_chapter").toString());
            distributorInfo.put("radio", media_source);
            Map<String, Object> promotionInfo = tomatoService.createPromotion(distributorInfo);
            if ("4000".equals(promotionInfo.get("code").toString())) {
                result.put("message", promotionInfo.get("message").toString());
                return result;
            }
            promotionInfo.put("creater", creater);
            promotionInfo.put("create_time", Utils.getTime9());
            tomatoService.savePromotion(promotionInfo);
            param.put("promotion_url", getParamFromUrl(promotionInfo.get("promotion_url").toString()));

            param.put("creater", creater);
            for(int x=0; x < Integer.parseInt(param.get("project_number").toString()); x++) {
                param.put("time", Utils.getTime9());
                String projectId = createProject(param, advertiserInfo, bidStrategyVal, bid_strategy);
                if(StringUtils.isEmpty(projectId)) {
                    continue;
                }
                param.put("project_id", projectId);
                jlaccountMapper.saveJlProject(param);
                for (int j = 0; j < Integer.parseInt(param.get("jlpromotion_number").toString()); j++) {
                    param.put("product_info_selling_points", Constant.SELLING_POINTS_LIST);
                    param.put("call_to_action_buttons", Constant.CALL_TO_ACTION_LIST);
                    param.put("time", Utils.getTime9());
                    createPromotion(param, projectId, advertiserInfo);
                }
            }
        }
        return result;
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
                + UUID.randomUUID().toString().substring(0, 8);
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
                } else if ("7".equals(params.get("radio").toString())) {
                    delivery_setting .put("budget", 300);
                    if ("ROI".equals(bid_strategy)) {
                        delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                        delivery_setting .put("roi_goal", Double.parseDouble(bidStrategyVal.split("&")[1]));
                    } else {
                        delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                    }
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
                if(params.get("radio").toString().equals("1")) {
                    put("micro_promotion_type", "BYTE_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                    optimize_goal.put("asset_ids", new Long[]{getAssets(params, advertiserId)});//getAssets_test
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_PAY");
                    optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_PURCHASE_ROI");
                    optimize_goal.put("external_actions", new int[]{14});
                } else if (params.get("radio").toString().equals("4")) {
                    put("micro_promotion_type", "WECHAT_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("wechat_micro_app_instance_id").toString()));
                    optimize_goal.put("external_action", "");
                    optimize_goal.put("deep_external_action", "");
                    optimize_goal.put("external_actions", new int[]{14});
                }  else {
                    put("micro_promotion_type", "BYTE_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                    optimize_goal.put("asset_ids", new Long[]{getAssets(params, advertiserId)});//getAssets_test
                    optimize_goal.put("external_action", "AD_CONVERT_TYPE_GAME_ADDICTION");

                    if ("ROI".equals(bid_strategy)) {
                        optimize_goal.put("external_action", "AD_CONVERT_TYPE_ACTIVE");
                        optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_LT_ROI");
                        optimize_goal.put("external_actions", new int[]{8});
                    }
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
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }

        if ("0".equals(response.getBody().get("code").toString())) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            return content.get("project_id").toString();
        }
        LOGGER.info("**** createPromotion Response: " + response.getBody());
        return "";
    }

    public void dealAssetAndEvent(Map<String, Object> param){
        try {
            String asset_id = getCreateAssets(param);
            if (!StringUtils.isEmpty(asset_id)) {
                param.put("asset_id", asset_id);
                getCreateEvents(param);
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
                return ((List<Map<String, Object>>)(JSONObject.parseObject(Utils.decodeUnicode(result.toString())).get("data"))).get(0);
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


    public List<Map<String, Object>> getVideoList(Map<String, Object> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", getJlaccount(params.get("jlaccount").toString()));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/file/video/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("page_size", 100)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().get("data") != null) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        return null;
    }

    @Override
    public  Map<String, Object> bindVideo(Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", getJlaccount(params.get("jlaccount").toString()));
        String url = "https://ad.oceanengine.com/open_api/2/file/material/bind/";
        List<String> video_list = new ArrayList<>();
        params.put("advertiser_id", params.get("videoAdvertiser_id"));
        List<Map<String, Object>> videoInfoList = getVideoList(params);
        for (Map<String, Object> videoInfo : videoInfoList) {
            video_list.add(videoInfo.get("id").toString());
        }

        List<Long> advertiser_ids = new ArrayList<>();
        for (String advertiser_id :(List<String>)params.get("advertiser_ids")) {
            advertiser_ids.add(Long.parseLong(advertiser_id));
        }

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("videoAdvertiser_id").toString()));
                put("target_advertiser_ids", advertiser_ids);
                put("video_ids", video_list);
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        List<Map<String, Object>> minVideoList = new ArrayList<>();
        for (String advertiser_id :(List<String>)params.get("advertiser_ids")) {
            params.put("advertiser_id", advertiser_id);
            List<Map<String, Object>> videoList = getVideoList(params);
            if (minVideoList.size() == 0) {
                minVideoList = videoList;
                continue;
            }
            minVideoList = dealMinVideoList(minVideoList, videoList);
        }
        result.put("data", minVideoList);
        result.put("videoSizeMax", videoInfoList.size());
        result.put("videoSizeMin", minVideoList.size());
        return result;
    }

    private List<Map<String, Object>> dealMinVideoList(List<Map<String, Object>> list1, List<Map<String, Object>> list2) {
        List<Map<String, Object>> minVideoList = new ArrayList<>();
        for (Map<String, Object> map1 : list1) {
            for (Map<String, Object> map2 : list2) {
                if (map1.get("id").toString().equals(map2.get("id").toString())) {
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
            if (list.size() == 0) {
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

        getUpdateMicroApp(params);
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
    private String getUpdateMicroApp(Map<String, Object> params){
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
            Request request = new Request.Builder()
                    .url(url.toString())
                    .method("GET", null)
                    .addHeader("Access-Token", params.get("accessToken").toString())
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            JSONObject json  = JSONObject.parseObject(response.body().string());
            return ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
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
        return  ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
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
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return res.getBody().toString();
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
            File file= new File("/etc/image/" + params.get("image").toString());
//            File file= new ClassPathResource("image/" + params.get("image").toString()).getFile();
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
            File file= new File("/etc/image/" +  params.get("image").toString());
//            File file= new ClassPathResource("image/" + params.get("image").toString()).getFile();
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
        }
        return "";
    }

    /**
     * 设置头像
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String getAvatarSubmit(Map<String, Object> params){
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
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return res.getBody().toString();
    }

    /**
     * 创建资产
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String getCreateAssets(Map<String, Object> params){
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
                if ("1".equals(params.get("radio").toString())) {
                    mini_program_asset.put("mini_program_id", "ttb1d2c76f2ee36a0601");
                } else if ("7".equals(params.get("radio").toString())) {
                    mini_program_asset.put("mini_program_id", "tt7d2a0b97e21cb1a001");
                }

                mini_program_asset.put("mini_program_name", "鸣宜剧场");
                mini_program_asset.put("instance_id", Long.parseLong(params.get("byte_micro_app_instance_id").toString()));
                mini_program_asset .put("mini_program_type", "BYTE_APP");
                put("mini_program_asset",mini_program_asset);

            }
        };
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
            return result.get("asset_id").toString();
        }
        return "";
    }

    /**
     * 创建事件
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    private String getCreateEvents(Map<String, Object> params){
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
                if ("1".equals(params.get("radio").toString())) {
                    put("event_id", 14);
                } else if ("7".equals(params.get("radio").toString())) {
                    if ("ROI".equals(params.get("bidStrategyKey").toString())) {
                        put("event_id", 8);
                    } else {
                        put("event_id", 25);
                    }
                }
                put("track_types", new String[]{"MINI_PROGRAME_API"});
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
                put("asset_id", Long.parseLong(params.get("asset_id").toString()));
                if ("1".equals(params.get("radio").toString())) {
                    put("event_id", 160);
                } else if ("7".equals(params.get("radio").toString())) {
                    put("event_id", 110);
                }
                put("track_types", new String[]{"MINI_PROGRAME_API"});
            }
        };
        requestEntity = new HttpEntity<>(body, headers);

        if ("1".equals(params.get("radio").toString())) {
            response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
            );
        } else if ("7".equals(params.get("radio").toString()) && "ROI".equals(params.get("bidStrategyKey").toString())) {
            response = restTemplate.exchange(
                    uri, HttpMethod.POST, requestEntity, Map.class
            );
        }
//
        return "";
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

    private String dealMicroAppLink(Map<String, Object> params) {
        Map<String, String> urlInfo = (Map<String, String>)params.get("promotion_url");
        StringBuilder link = new StringBuilder();
        link.append("sslocal://microapp?app_id=ttb1d2c76f2ee36a0601&bdp_log=%7B%22launch_from%22%3A%22ad%22%7D&scene=0&start_page=pages%2Ftheatre%2Findex%3Faid%3D40013835%26click_id%3D__CLICKID__%26code%3D");
        link.append(urlInfo.get("code"));
        link.append("%26item_source%3D1%26media_source%3D1%26mid1%3D__MID1__%26mid2%3D__MID2__%26mid3%3D__MID3__%26mid4%3D__MID4__%26mid5%3D__MID5__%26request_id%3D__REQUESTID__%26tt_album_id%3D");
        link.append(urlInfo.get("tt_album_id"));
        link.append("%26tt_episode_id%3D");
        link.append(urlInfo.get("tt_episode_id"));
        link.append("&uniq_id=S2025050920540356519208245877223096321e14adff8023&version=v2&version_type=current&bdpsum=1768364");
        return link.toString();
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
                if ("7".equals(params.get("radio").toString())) {
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
//                profession .put("ad_carrier", "字节小程序");
                profession .put("micro_app_link", params.get("microLink").toString());

                profession .put("has_paid_content", "1");
                profession .put("has_motivation_content", "1");
                profession .put("copyright_owner", "抖音视界有限公司");
                profession .put("playlet_gender", "3");
                profession .put("playlet_num", "50");
                profession .put("playlet_duration", "2");
                profession .put("start_pay_playlet", "10");
                profession .put("membership_types", "2");
                if ("7".equals(params.get("radio").toString())) {
                    profession .put("starting_unlock_episode", "8");
                }
                product_info.put("profession", profession);
                put("product_info", product_info);
            }
        };

        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> result = ((Map<String, Map<String, Object>>)response.getBody()).get("data");
            return result.get("product_id").toString();
        }
        return "";
    }

}
