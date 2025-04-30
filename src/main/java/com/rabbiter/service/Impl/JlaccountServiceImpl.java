package com.rabbiter.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
                                               Map<String, Object> advertiserInfo, Map<String, String> vedioCover,
                                               int promotionNum){
        String accessToken = params.get("accessToken").toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", accessToken);

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String newName  = advertiserInfo.get("name").toString().split("-")[2]
                + UUID.randomUUID().toString().substring(0, 8);
        params.put("name", newName);

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", params.get("advertiser_id"));
                put("project_id", projectId);
                put("name", newName);
                put("operation", "DISABLE");
                Map<String, Object> promotion_materials = new HashMap();
                List<Object> video_material_list = new ArrayList<>();
                for (String videoId: (List<String>)params.get("promotion" + promotionNum)) {
                    Map<String, Object> video_material = new HashMap();
                    video_material.put("image_mode", "CREATIVE_IMAGE_MODE_VIDEO_VERTICAL");
                    video_material.put("video_id", videoId);
                    video_material.put("video_cover_id", vedioCover.get(videoId));
                    video_material_list.add(video_material);
                }

                promotion_materials.put("video_material_list", video_material_list);

                List<Object> title_material_list = new ArrayList<>();
                for (int i=0;i<3;i++) {
                    Map<String, Object> title_material = new HashMap();
                    title_material.put("title", Constant.TITLE_LIST.get(new Random().nextInt(50))); // 创意标题
                    title_material_list.add(title_material);
                }
                promotion_materials.put("title_material_list", title_material_list);

                Map<String, Object> mini_program_info = new HashMap();
                String promotion_url = params.get("promotion_url").toString();
                // 字节小程序调起链接
                mini_program_info.put("app_id", "ttb1d2c76f2ee36a0601");
                mini_program_info.put("start_path", promotion_url.split("\\?")[0].substring(1));
                String mini_program_info_params = promotion_url.split("\\?")[1];
                mini_program_info.put("params", mini_program_info_params);
                promotion_materials.put("mini_program_info", mini_program_info);

                promotion_materials.put("call_to_action_buttons", new String[]{(String)params.get("call_to_action_buttons")}); // 行动号召文案，字数限制：[2-6]，数组上限为10

                List<String> external_url_material_list = new ArrayList<>();
                if(params.get("radio").toString().equals("1")) {
                    external_url_material_list.add("https://www.chengzijianzhan.com/tetris/page/7493857882437238793/?projectid=__PROJECT_ID__&promotionid=__PROMOTION_ID__&creativetype=__CTYPE__&clickid=__CLICKID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                } else {
                    external_url_material_list = (List<String>)params.get("external_url_material_list");
                }
                promotion_materials.put("external_url_material_list", external_url_material_list); // 普通落地页链接素材

                Map<String, Object> product_info = new HashMap();

                List<String> titles = new ArrayList<>();
                titles.add("2025热门短剧");
                product_info.put("titles", titles);   // 产品名称，字数限制：[1-20]
                List<String> image_ids = new ArrayList<>();
                image_ids.add("tos-cn-i-sd07hgqsbj/c76af966b1f14944a44986be4ec79e96");
                product_info.put("image_ids", image_ids);  //产品主图，尺寸要求108*108，上限10个

                product_info.put("selling_points", new String[]{(String)params.get("product_info_selling_points").toString()}); //产品卖点，字数限制：[6-9]，数组上限为10
                promotion_materials.put("product_info", product_info);

                promotion_materials.put("intelligent_generation", "ON");

                put("promotion_materials", promotion_materials);

                put("source", getSourse(advertiserInfo.get("company").toString()));

                Map<String, String> native_setting = new HashMap();
                native_setting.put("aweme_id", "81620176915");  //单个授权抖音号id（抖音号推广身份）
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

        if ("OK".equals(response.getBody().get("message").toString())) {
            Gson gson = new Gson();
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            params.put("promotion_id", content.get("promotion_id").toString());
            result.put("promotion_id", content.get("promotion_id").toString());
            params.put("title_json", gson.toJson(((List<String>)params.get("title"))));
            params.put("call_to_action_buttons_json", gson.toJson(Arrays.asList(new String[]{(String)params.get("call_to_action_buttons")})));
            params.put("external_url_material_list_json", gson.toJson(((List<String>)params.get("external_url_material_list"))));
            params.put("product_info_selling_points_json", gson.toJson(Arrays.asList(new String[]{(String)params.get("product_info_selling_points")})));
            jlaccountMapper.saveJlPromotion(params);
        }
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
    public void autoCreatePromotion(Map<String, Object> param) {
        LOGGER.info("autoCreatePromotion,param:" + param.toString());
        String accessToken = getJlaccount(param.get("jlaccount").toString());
        param.put("accessToken", accessToken);
        List<String> advertiserIdList = (List<String>)param.get("advertiser_ids");
        param.put("advertiser_id",advertiserIdList.get(0));
        Map<String, String> vedioCover = dealVedioCover(param);
        List<String> nameList = new ArrayList<>();

        String media_source = param.get("radio").toString();
        String microGame;
        String distributor;
        if (media_source.equals("1")) {
            microGame = "抖小";
            distributor = "distributorId_b";
        } else {
            microGame = "微小";
            distributor = "distributorId_w";
        }
        Map<String, String> distributorInfo = tomatoMapper.selectDistributorById((String)param.get(distributor)).get(0);
        distributorInfo.put("book_id", param.get("video_id").toString());
        Map<String, Object> videoInfo = tomatoService.getVideoInfo(distributorInfo);

        List<Map<String, Object>> ad_callback_config_id_list = tomatoService.getAdCallback(distributorInfo);
        distributorInfo.put("ad_callback_config_id", getIdFromList(ad_callback_config_id_list, "config_name", "全回传", "config_id"));
        List<Map<String, Object>> recharge_template_id_list = tomatoService.getRechargeTemplate(distributorInfo);
        String creater = param.get("creater").toString();
        for (int i = 0; i< advertiserIdList.size(); i++) {
            param.put("advertiser_id", advertiserIdList.get(i));
            Map<String, Object> advertiserInfo= getAdvertiserInfo(param, advertiserIdList.get(i));

            String recharge_template_val = ((List<String>)param.get("bid_strategy")).get(i);
            String bidStrategyVal = Constant.BID_STRATEGY_MAP.get(recharge_template_val);
            param.put("bidStrategy", bidStrategyVal);
            String name = creater + "-" + getSourse(advertiserInfo.get("company").toString()) + "-" + videoInfo.get("book_name") + "-" +
                    microGame  + "-" + recharge_template_val + "-" + Utils.getTime6_s();
            name = name.replace("，", "").replace(",", "");
            distributorInfo.put("media_source", media_source);
            nameList.add(name);
            distributorInfo.put("promotion_name", name);
            distributorInfo.put("advertiser_id", advertiserIdList.get(i));
            updateAdvertiser(param, distributorInfo);
            distributorInfo.put("price", bidStrategyVal.split("&")[2]);
            distributorInfo.put("recharge_template_id", getIdFromList(recharge_template_id_list, "recharge_template_name", recharge_template_val, "recharge_template_id"));
            Map<String, Object> promotionInfo = tomatoService.createPromotion(distributorInfo);
            promotionInfo.put("creater", creater);
            promotionInfo.put("create_time", Utils.getTime9());
            tomatoService.savePromotion(promotionInfo);
            param.put("promotion_url", promotionInfo.get("promotion_url"));

            param.put("creater", creater);
            for(int x=0; x< Integer.parseInt(param.get("project_number").toString()); x++) {
                param.put("time", Utils.getTime9());
                String projectId = createProject(param, advertiserInfo, bidStrategyVal);
                param.put("project_id", projectId);
                jlaccountMapper.saveJlProject(param);
                for (int j = 0; j < Integer.parseInt(param.get("jlpromotion_number").toString()); j++) {
                    param.put("product_info_selling_points", Constant.SELLING_POINTS_LIST.get(j));
                    param.put("call_to_action_buttons", Constant.CALL_TO_ACTION_LIST.get(j));
                    param.put("time", Utils.getTime9());
                    createPromotion(param, projectId, advertiserInfo, vedioCover, j + 1);
                }
            }
        }
    }

    private String createProject(Map<String, Object> params, Map<String, Object> advertiserInfo, String bidStrategyVal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/project/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        String advertiserId = advertiserInfo.get("id").toString();
        String name = advertiserInfo.get("name").toString().split("-")[2]
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
                delivery_setting .put("budget", 9999999.99);
                delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                delivery_setting .put("cpa_bid", Double.parseDouble(bidStrategyVal.split("&")[0]));
                delivery_setting .put("roi_goal", Double.parseDouble(bidStrategyVal.split("&")[1]));
                put("delivery_setting",delivery_setting);

                Map<String, Object> related_product = new HashMap<>();
                related_product.put("product_setting", "SINGLE");
                String product_platform_id = getAdvertiserId(params, advertiserId);
                related_product.put("product_platform_id", Long.parseLong(product_platform_id));
                related_product.put("product_id", getProductId(params, advertiserId, product_platform_id));
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
                } else {
                    put("micro_promotion_type", "WECHAT_APP");
                    put("micro_app_instance_id", Long.parseLong(params.get("wechat_micro_app_instance_id").toString()));
                }

                optimize_goal.put("external_action", "AD_CONVERT_TYPE_PAY");
                optimize_goal.put("deep_external_action", "AD_CONVERT_TYPE_PURCHASE_ROI");
                optimize_goal.put("external_actions", new int[]{14});
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

        if ("OK".equals(response.getBody().get("message"))) {
            Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
            return content.get("project_id").toString();
        }
        return "";
    }

    private Map<String, Object> getAdvertiserInfo(Map<String, Object> params, String advertiser_id){
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/advertiser/info/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        Map data = new HashMap(){
            {
                put("advertiser_ids", new Long[] {Long.parseLong(advertiser_id)});
                put("fields", new String[] {"id", "name", "status", "company"});
            }
        };
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        httpEntity.setHeader("Access-Token", params.get("accessToken").toString());

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

    @Override
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
        if (response.getStatusCode() == HttpStatus.OK) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        return null;
    }

    private Map<String, String> dealVedioCover(Map<String, Object> params) {
        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> videoInfoList = getVideoList(params);
        for (int i=0; i<5 ; i++) {
            for (Map<String, Object> videoInfo: videoInfoList) {
                ResponseEntity<Map> response = getVideoCover(params, params.get("advertiser_id").toString(), videoInfo.get("id").toString());
                if (((Map<String, String>)response.getBody().get("data")).get("status").equals("SUCCESS")) {
                    result.put(videoInfo.get("id").toString(), ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list").get(0).get("id").toString());
                }
            }
            if (result.size() == videoInfoList.size()) {
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

    private String getAdvertiserId(Map<String, Object> params, String advertiser_id){
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
            return resultList.get(0).get("platform_id").toString();
        }
        return "";
    }

    private String getProductId(Map<String, Object> params, String advertiser_id, String product_platform_id){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("accessToken").toString());

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/detail/get/")
                .queryParam("advertiser_id", advertiser_id)
                .queryParam("product_platform_id", product_platform_id)
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
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
            return resultList.get(0).get("product_id").toString();
        }
        return "";
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
        List<Map<String, Object>> res = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("asset_list");
        if (res.size() > 0) {
            return (Long)res.get(0).get("asset_id");
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

}
