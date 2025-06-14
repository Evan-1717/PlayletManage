package com.rabbiter.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbiter.common.Constant;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.config.TaskExecutorConfig;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.Promotion;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.service.JlaccountService;
import com.rabbiter.util.AccessToken;
import com.rabbiter.util.Utils;
import okhttp3.Request;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import okhttp3.OkHttpClient;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p>
 *  前端控制器：巨量账号管理
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@RestController
@RequestMapping("/api/jlaccount")
public class JlaccountController {
    private static final Logger LOGGER = Logger.getLogger(JlaccountController.class.getName());

    @Autowired
    private JlaccountService jlaccountService;

    @Autowired
    private RestTemplate restTemplate;

    private String auth_code = "";

    private String state = "";


    /*
     * 回调授权
     * @author rabbiter
     * @date 2023/1/2 19:11
     */
    @GetMapping("/oauthCallBack")
    public Result oauthCallBack(@RequestParam String auth_code, @RequestParam String state){
        this.auth_code = auth_code;
        this.state = state;
        return Result.success();
    }

    /*
     * 回调授权
     * @author rabbiter
     * @date 2023/1/2 19:11
     */
    @GetMapping("/showAuthCode")
    @ResponseBody
    public Result showAuthCode(){
        Map<String, String> map = new HashMap();
        map.put("auth_code", this.auth_code);
        map.put("state", this.state);
        return Result.success(map);
    }

    @PostMapping("/upload")
    public Result handleFileUpload(@RequestParam("file") MultipartFile file, String creater) {
        if (file.isEmpty()) {
            return Result.fail();
        }
//        jlaccountService.autoCreatePromotion(file);
        return Result.success();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/autoCreatePromotion")
    private Result autoCreatePromotion(@RequestBody Map<String, Object> params){
        List<String> subjectList = (List<String>)params.get("subject");
        for (int i=1; i <= subjectList.size(); i++) {
            params.put("jlaccount", subjectList.get(i-1));
            params.put("advertiser_ids", params.get("advertiser_id" + i + "s"));
            Map<String, Object> copiedMap = SerializationUtils.clone(new HashMap<>(params));
            int id = jlaccountService.savePromotion(copiedMap);
            copiedMap.put("id", id);
            jlaccountService.autoCreatePromotion(copiedMap);
        }
        return Result.success();
    }

    /**
     * 获取视频列表
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/bindVideo")
    public Result bindVideo(@RequestBody Map<String, Object> params){
        Map<String, Object> res = jlaccountService.bindVideo(params);
        return res.size() > 0 ? Result.success(res) : Result.fail();
    }

    /**
     * 获取巨量账户信息
     *
     * @return Response in JSON format
     */
    @PostMapping("/getJlCountInfo")
    public Result getJlCountInfo(){
        return Result.success(Constant.JL_COUNT_INFO);
    }

    /**
     * 获取视频列表
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAdvertiserInfo")
    public Result getAdvertiserInfo(@RequestBody Map<String, Object> params){
        Map<String, Object> res = jlaccountService.getAdvertiserInfo(params);
        return res.size() > 0 ? Result.success(res) : Result.fail();
    }

    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPageAutoPromotion")
    public Result listPageAutoPromotion(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String creater = (String)param.get("creater");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");

        LambdaQueryWrapper<JlPromotion> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(JlPromotion::getCreater, creater);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(JlPromotion::getCreate_time,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(JlPromotion::getCreate_time,date2);
        }

        Page<JlPromotion> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        queryWrapper.orderByDesc(JlPromotion::getCreate_time);
        IPage result = jlaccountService.listPageAutoPromotion(page, queryWrapper);
        return Result.success(result.getRecords(),result.getTotal());
    }


    /*
     * 获取令牌
     * @author rabbiter
     * @date 2023/1/2 19:12
     */
    @PostMapping("/getAccessToken")
    @ResponseBody
    public Result getAccessToken(@RequestBody Jlaccount jlaccount){
        JSONObject data = AccessToken.getAccessToken(jlaccount.getAppid(),jlaccount.getSecret(),jlaccount.getAuthcode());
        String access_token = (String)data.get("access_token");
        String refresh_token = (String)data.get("refresh_token");
        return Result.success(data);
    }

    @PostMapping("/saveJlaccount")
    @ResponseBody
    public void saveJlaccount(@RequestBody Map<String, String> map){
        jlaccountService.saveJlaccount(map);
    }

    /*
     * 刷新令牌
     * @author rabbiter
     * @date 2023/1/2 19:12
     */
    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody Jlaccount jlaccount){
        JSONObject data = AccessToken.refreshToken(jlaccount.getAppid(),jlaccount.getSecret(),jlaccount.getRefreshtoken());
        String access_token = (String)data.get("access_token");
        String refresh_token = (String)data.get("refresh_token");
        return Result.success(data);
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getVideo")
    private String getVideo(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/file/video/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("page_size", 10)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getInfo")
    private String getInfo(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/user/info/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getImage")
    private String getImage(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/file/image/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCarousel")
    private String getCarousel(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/carousel/list/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCarouselAd")
    private String getCarouselAd(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        List<String> carousel_ids = new ArrayList<>();
        carousel_ids.add(params.get("carousel_ids"));
        carousel_ids.add(params.get("carousel_ids1"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/carousel/ad/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("carousel_ids", carousel_ids)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }


    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAwemeAuth")
    private String getAwemeAuth(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/tools/aweme_auth_list/")
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        List<Map<String, Map<String, String>>> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("data"));
        return content.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getPromotion")
    private String getPromotion(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/list/")
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        List<Map<String, Map<String, String>>> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("data"));
        return content.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getVideoAweme")
    private String getVideoAweme(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/file/video/aweme/get/")
                .queryParam("advertiser_id", Long.parseLong(params.get("advertiser_id")))
                .queryParam("aweme_id", "81620176915")
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
            System.out.println("Response: " + response.getBody());
        }
        List<Map<String, Map<String, String>>> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("data"));
        return content.toString();
    }

    /**
     * 上穿连山云视频
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/uploadTask")
    private String uploadTask(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        String url = "https://api.oceanengine.com/open_api/2/file/upload_task/create/";

        Map param = new HashMap() {
            {
                put("account_id", Long.parseLong(params.get("advertiser_id")));
                put("account_type", "ADVERTISER");
                put("filename", "连山云测试视频");
                put("video_url", "https://v26-web.douyinvod.com/e4eb7de7f1dca34057239723a95b29a5/68359a44/video/tos/cn/tos-cn-v-958309/oYQBI5apCaQxMh9IwpiGRCanIVQYrmQErvimv/?a=6383\\u0026ch=26\\u0026cr=3\\u0026dr=0\\u0026lr=all\\u0026cd=0%7C0%7C0%7C3\\u0026br=926\\u0026bt=926\\u0026cs=0\\u0026ds=6\\u0026ft=4TMWc6DnppftrdLv.s~.C_bAja-CInqV~JGc6BIDDZYNAYpHDD~1ThzyCOS~XusZ.\\u0026mime_type=video_mp4\\u0026qs=0\\u0026rc=NDMzPDk5aDo1NDo2NzlmOkBpM3Q0N3c5cml0MzUzNGU0M0BeMDIuYGA1Ni0xLjNeMl8uYSNhXi8xMmRrY2hhLS1kXjBzcw%3D%3D\\u0026btag=c0000e00030000\\u0026cquery=100o_100w_100B_102v_102u\\u0026dy_q=1748332055\\u0026feature_id=136e8d81b95d650c236697554379e663\\u0026l=20250527154734AB0F2780B6B924A7B347");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        List<Map<String, Map<String, String>>> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("data"));
        return content.toString();
    }

    /**
     * 创建落地页
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createSite")
    private String createSite(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        String url = "https://ad.oceanengine.com/open_api/2/tools/site/create/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("name", "测试落地页1");
                List<Object> bricks = new ArrayList<>();
                Map<String, Object> xrPicture = new HashMap<>();
                xrPicture.put("name", "XrPicture");
                xrPicture.put("ic_id", "tos-cn-i-sd07hgqsbj/5830d65249b1453d86da7a6cfb1727fe");
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
                xrWechatApplet.put("instance_id", 1833341004054602L);
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        List<Map<String, Map<String, String>>> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("data"));
        return content.toString();
    }

    /**
     * 修改落地页状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/updateSiteStatus")
    private String updateSiteStatus(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        String url = "https://ad.oceanengine.com/open_api/2/tools/site/update_status/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("site_ids", new String[]{"7509397553319575562"});
                put("status", "published");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.getBody().toString();
    }

    /**
     * 获取落地页状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getSiteStatus")
    private boolean getSiteStatus(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/tools/site/read/")
                .queryParam("advertiser_id", params.get("advertiser_id").toString())
                .queryParam("site_id", params.get("site_id").toString())
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
            if ("auditAccepted".equals(((Map<String, String>)response.getBody().get("data")).get("status"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取微信小程序状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getWechatAppletStatus")
    private String getWechatAppletStatus(@RequestBody Map<String, String> params){
        Long advertiser_id = Long.parseLong(params.get("advertiser_id"));
        Long instance_id = 7501273705260105779L;
        int page = 1;
        int page_size = 100;
        String myArgs = String.format("{\"advertiser_id\": \"%s\", \"filtering\": {\"name\": \"%s\", \"audit_status\": \"%s\", \"search_type\": \"%s\"}, \"page\": \"%s\", \"page_size\": \"%s\"}" ,advertiser_id, "陌生的爱人", "AUDIT_ACCEPTED", "CREATE_ONLY", 1, 100);
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
                    .addHeader("Access-Token", params.get("access_token"))
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            JSONObject json  = JSONObject.parseObject(response.body().string());
            return ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/video_cover")
    private List<Map<String, Object>> video_cover(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/tools/video_cover/suggest/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("video_id", params.get("video_id"))
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
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        }
        return new ArrayList<>();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getProject_test")
    private String getProject_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

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
        List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }


    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAssets_test")
    private String getAssets_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/tools/event/all_assets/list/")
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
        if (response.getStatusCode() == HttpStatus.OK) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("asset_list").get(0).get("asset_id").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAssetConfigs_test")
    private String getAssetConfigs_test(@RequestBody Map<String, String> params){
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
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("event_configs").get(0).get("asset_id").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getMicro_game_test")
    private String getMicro_game_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/micro_game/list/")
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
        if (response.getStatusCode() == HttpStatus.OK) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getWechat_game_test")
    private String getWechat_game_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/wechat_game/list/")
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
        if (response.getStatusCode() == HttpStatus.OK) {
            return ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getMicroApp_test")
    private String getMicroApp_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/micro_app/list/")
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
        List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAvailables_test")
    private String getAvailables_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/product/availables/")
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
            return resultList.get(0).get("platform_id").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getDpaDetail_test")
    private String getDpaDetail_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/detail/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("product_platform_id", "7281789144008467")
                .queryParam("page", 1)
                .queryParam("page_size", 100)
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


    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCountry_test")
    private String getCountry_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/tools/country/info/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("language", "ZH_CN_GOV")
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
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAdmin_test")
    private String getAdmin_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/tools/admin/info/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("language", "ZH_CN_GOV")
                .queryParam("codes","[\"CN\"]")
                .queryParam("sub_district", "ONE_LEVEL")
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
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAvailableEvents_test")
    private String getAvailableEvents_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/available_events/get/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .queryParam("asset_id", "1831940553564168")
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
        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getUpdateAdvertiser_test")
    private String getUpdateAdvertiser(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));
        String url = "https://ad.oceanengine.com/open_api/2/agent/advertiser/update/";

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("name", "赵恒-mk番茄张宕-抖小额灾荒年我商场通古代救全城百姓41111");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return res.getBody().toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createUploadTask")
    private String createUploadTask(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));
        String url = "https://api.oceanengine.com/open_api/2/file/upload_task/create/";

        Map param = new HashMap() {
            {
                put("account_id", Long.parseLong(params.get("advertiser_id")));
                put("account_type", "ADVERTISER");
                put("filename", "filename.mp4");
                put("video_url", "https://my-playlet-material.tos-cn-beijing.volces.com/8.mp4");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        if ("OK".equals(res.getBody().get("message"))) {
            return (((Map<String, Map<String, Object>>)res.getBody()).get("data")).get("task_id").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/uploadTaskList")
    private String uploadTaskList(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/2/file/video/upload_task/list/")
                .queryParam("account_id", Long.parseLong(params.get("advertiser_id")))
                .queryParam("account_type", "ADVERTISER")
                .queryParam("task_ids", "[250622085890]")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 封装请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );

        if ("OK".equals(response.getBody().get("message"))) {
            List<Map<String, Object>> resultList = ((Map<String, List<Map<String, Object>>>)response.getBody().get("data")).get("list");
            return resultList.get(0).get("stasus").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getBind_test")
    private String getBind(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));
        String url = "https://ad.oceanengine.com/open_api/2/file/material/bind/";
        List<Long> advertiser_ids = new ArrayList<>();
        advertiser_ids.add(1825659930906858L);

        List<String> video_ids = new ArrayList<>();
        video_ids.add("v0d033g10000d030aovog65u4h56a6j0");

        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("target_advertiser_ids", advertiser_ids);
                put("video_ids", video_ids);
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return res.getBody().toString();
    }


    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAvatarUpload_test")
    private String getAvatarUpload_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Access-Token", params.get("access_token").toString());
        String url = "https://api.oceanengine.com/open_api/2/file/image/ad/";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("advertiser_id", Long.parseLong(params.get("advertiser_id").toString()));
        try {
            File file= new ClassPathResource("image/bj.jpg" ).getFile();
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
            return content.get("image_id").toString();
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAvatarSubmit_test")
    private String getAvatarSubmit_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));
        String url = "https://api.oceanengine.com/open_api/2/advertiser/avatar/submit/";


        Map param = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("image_id", "web.business.image/4146451ef8225c0c272cacc0881ac88d");
            }
        };
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return res.getBody().toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/advertiser_test")
    private String advertiser_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/advertiser/info/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        Map data = new HashMap(){
            {
                put("advertiser_ids", new Long[] {Long.parseLong(params.get("advertiser_id"))});
                put("fields", new String[] {"id", "name", "status", "company"});
            }
        };

        // 封装请求实体
        // 构造请求
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        httpEntity.setHeader("Access-Token",params.get("access_token"));

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

                return JSONObject.parseObject(Utils.decodeUnicode(result.toString())).toString();
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
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createPromotion_test")
    private String createPromotion_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", params.get("advertiser_id"));
                put("project_id", "7493863989223473163");
                put("name", "测试广告名称13");
                put("operation", "DISABLE");
                Map<String, Object> promotion_materials = new HashMap();
                List<Object> video_material_list = new ArrayList<>();
                Map<String, Object> video_material = new HashMap();
                video_material.put("image_mode", "CREATIVE_IMAGE_MODE_VIDEO_VERTICAL");
                video_material.put("video_id", "v0d033g10000d00rq8fog65v0upu9v4g");
                video_material.put("video_cover_id", "tos-cn-p-0051/oYpfGeHPZIGogZh3TCfALJ6fQBswAA2AfECYBD");
                video_material_list.add(video_material);
                promotion_materials.put("video_material_list", video_material_list);

                List<Object> text_abstract_list = new ArrayList<>();
                Map<String, Object> text_abstract = new HashMap();
                text_abstract.put("abstract_text", "词词词包名词词词包名词词词包名词词词包名词词词包名词词词包名词词词包名"); // 文本摘要内容,单广告可添加1-10个，长度25-45个字
                text_abstract_list.add(text_abstract);
//                promotion_materials.put("text_abstract_list", text_abstract_list);

                List<Object> title_material_list = new ArrayList<>();
                Map<String, Object> title_material = new HashMap();
                title_material.put("title", "title-test"); // 创意标题
                title_material_list.add(title_material);
                promotion_materials.put("title_material_list", title_material_list);

                Map<String, Object> mini_program_info = new HashMap();
                // 字节小程序调起链接
                mini_program_info.put("url", "sslocal://microapp?app_id=ttb1d2c76f2ee36a0601&bdp_log=%7B%22launch_from%22%3A%22ad%22%2C%22location%22%3A%22%22%7D&scene=0&start_page=pages%2Ftheatre%2Findex%3Faid%3D40013835%26click_id%3D__CLICKID__%26code%3DPI0ITY0705N%26item_source%3D1%26media_source%3D1%26mid1%3D__MID1__%26mid2%3D__MID2__%26mid3%3D__MID3__%26mid4%3D__MID4__%26mid5%3D__MID5__%26request_id%3D__REQUESTID__%26tt_album_id%3D7492029487794618918%26tt_episode_id%3D7492029510478987811&uniq_id=W20250416183328040o1dustss&version=v2&version_type=current&bdpsum=72828ac");
                promotion_materials.put("mini_program_info", mini_program_info);

                List<String> call_to_action_buttons = new ArrayList<>();
                call_to_action_buttons.add("测试1");
                promotion_materials.put("call_to_action_buttons", call_to_action_buttons); // 行动号召文案，字数限制：[2-6]，数组上限为10

                List<String> external_url_material_list = new ArrayList<>();
                external_url_material_list.add("https://www.chengzijianzhan.com/tetris/page/7493857882437238793/?projectid=__PROJECT_ID__&promotionid=__PROMOTION_ID__&creativetype=__CTYPE__&clickid=__CLICKID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                promotion_materials.put("external_url_material_list", external_url_material_list); // 普通落地页链接素材

                Map<String, Object> product_info = new HashMap();
                List<String> titles = new ArrayList<>();
                titles.add("2025热门短剧");
                product_info.put("titles", titles);   // 产品名称，字数限制：[1-20]
                List<String> image_ids = new ArrayList<>();
                image_ids.add("tos-cn-i-sd07hgqsbj/c76af966b1f14944a44986be4ec79e96");
                product_info.put("image_ids", image_ids);  //产品主图，尺寸要求108*108，上限10个
                List<String> selling_points = new ArrayList<>();
                selling_points.add("测试测试测试");
                product_info.put("selling_points", selling_points); //产品卖点，字数限制：[6-9]，数组上限为10
                promotion_materials.put("product_info", product_info);

                promotion_materials.put("intelligent_generation", "ON");

                put("promotion_materials", promotion_materials);

                put("source", "测试");

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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Response: " + response.getBody());
        }
        Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
        if ("OK".equals(response.getBody().get("message"))) {

        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createProject_test")
    private String createProject_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/project/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", params.get("advertiser_id"));
                put("operation", "DISABLE");
                put("delivery_mode", "PROCEDURAL");
                put("landing_type", "MICRO_GAME");
                put("marketing_goal", "VIDEO_AND_IMAGE");

                put("ad_type", "ALL");
                put("name", "testProject5");
                Map<String, Object> delivery_setting = new HashMap<>();
                delivery_setting.put("bid_type", "CUSTOM");
                delivery_setting.put("budget_mode", "BUDGET_MODE_DAY");
                delivery_setting.put("schedule_type", "SCHEDULE_FROM_NOW");
//                delivery_setting.put("bid", 2.5f);
                delivery_setting .put("cpa_bid", 0.4f);
                delivery_setting .put("budget", 300f);
                delivery_setting .put("deep_bid_type", "ROI_COEFFICIENT");
                delivery_setting .put("roi_goal", 1.15);
                put("delivery_setting",delivery_setting);

                Map<String, Object> related_product = new HashMap<>();
                related_product.put("product_setting", "SINGLE");
                related_product.put("product_platform_id", 1699307224909595L);
                related_product.put("product_id", "1745226448773162393");
                put("related_product",related_product);


                Map<String, Object> delivery_range = new HashMap<>();
                delivery_range.put("inventory_catalog", "UNIVERSAL_SMART");
                delivery_range.put("inventory_type", new String[]{"INVENTORY_AWEME_FEED"});
                put("delivery_range",delivery_range);
                put("micro_promotion_type","BYTE_APP");
                put("micro_app_instance_id",7494100994079408178L);

                Map<String, Object> optimize_goal = new HashMap<>();
                optimize_goal.put("asset_ids", new Long[]{1830003285117347L});
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

                List<Map<String, Object>> keywords = new ArrayList<>();
                Map<String, Object> keywordsMap  = new HashMap<>();
                keywordsMap .put("word", "test-word");
                keywordsMap .put("bid_type", "WITH_PROMOTION");
                keywordsMap .put("match_type", "PRECISION");
                keywordsMap .put("bid", 0.3f);
                keywords.add(keywordsMap);
//                put("keywords",keywords);

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
        Map<String, Object> content = (((Map<String, Map<String, Object>>)response.getBody()).get("data"));
        if ("OK".equals(response.getBody().get("message"))) {

        }
        return response.toString();
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCreateAssets_test")
    private String getCreateAssets_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/assets/create/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("asset_type", "MINI_PROGRAME");

                Map<String, Object> mini_program_asset = new HashMap<>();
                mini_program_asset.put("mini_program_id", "ttb1d2c76f2ee36a0601");
                mini_program_asset.put("mini_program_name", "鸣宜剧场");
                mini_program_asset.put("instance_id", 7500578095032778790L);
                mini_program_asset .put("mini_program_type", "BYTE_APP");
                put("mini_program_asset",mini_program_asset);

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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("asset_id");
        }
        return "";
    }

    /**
     * Send GET request
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCreateEvents_test")
    private String getCreateEvents_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/event_manager/events/create/")
                .queryParam("advertiser_id", params.get("advertiser_id"))
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("asset_id", 1831263424525322L);
                put("event_id", 14);
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
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("asset_id", 1831263424525322L);
                put("event_id", 160);
                put("track_types", new String[]{"MINI_PROGRAME_API"});
            }
        };

        // 封装请求实体
        requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, Map.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("asset_id");
        }
        return "";
    }

    /**
     * 创建字节小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getCreateMicroApp_test")
    private String getCreateMicroApp_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/micro_app/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("app_id", "ttb1d2c76f2ee36a0601");
                put("remark", "员工名字");
                put("tag_info", "1050700000");


                Map<String, Object> app_page = new HashMap<>();
                app_page .put("link", "");
                app_page.put("start_page", "pages/theatre/index");
                app_page.put("start_param", "code=PI13YMJFMT6&aid=40013835&item_source=1&media_source=1&tt_album_id=7498688530302894632&tt_episode_id=7498688553501671963&click_id=__CLICKID__&request_id=__REQUESTID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                app_page .put("link_remark", "测试");
                put("app_page",app_page);

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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("instance_id");
        }
        return "";
    }

    /**
     * 共享小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/shareMicroApp_test")
    private String shareMicroApp_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/bp_asset_management/share/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("organization_id", 1829538761672148L);
                put("instance_id", 7500891842590965797L);
                put("asset_type", "BYTED_APPLETS");
                put("share_mode", "PART");

                List<Map<String, Object>> account_infos = new ArrayList<>();
                Map<String, Object> account_info = new HashMap<>();
                account_info .put("account_type", "AD");
                account_info.put("account_id", 1830643928627204L);
                account_infos.add(account_info);
                put("account_infos",account_infos);

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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("instance_id");
        }
        return "";
    }

    /**
     * 修改小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getUpdateMicroApp_test")
    private String getUpdateMicroApp_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/micro_app/update/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("instance_id", 7501273705260105779L);

                List<Map<String, Object>> app_page_list = new ArrayList<>();
                Map<String, Object> app_page = new HashMap<>();
                app_page.put("link", "");
                app_page.put("operate_type", "NEW");
                app_page.put("start_page", "pages/theatre/index");
                app_page.put("start_param", "code=PI1AIZBD1TM&aid=40013835&item_source=1&media_source=1&tt_album_id=7443669834242228790&tt_episode_id=7443669856429589030&click_id=__CLICKID__&request_id=__REQUESTID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                app_page.put("remark", "代码生成小程序链接测试");
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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("instance_id");
        }
        return "";
    }

    /**
     * 修改小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getAssetLink_test")
    private List<Map<String, Object>> getAssetLink_test(@RequestBody Map<String, String> params){

        Long advertiser_id = Long.parseLong(params.get("advertiser_id"));
        Long instance_id = 7501273705260105779L;
        int page = 1;
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
                    .addHeader("Access-Token", params.get("access_token"))
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            JSONObject json  = JSONObject.parseObject(response.body().string());
            return ((Map<String, List<Map<String, Object>>>)json.get("data")).get("list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 共享小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getShareMicroApp_test")
    private String getShareMicroApp_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/bp_asset_management/share/get/")
                .queryParam("organization_id", "1829538761672148")
                .queryParam("asset_type", "BYTED_APPLETS")
                .queryParam("instance_id", "7500891842590965797")
                .queryParam("share_type", "GROUP")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();


        // 封装请求实体
        HttpEntity<Map> requestEntity = new HttpEntity<>(null, headers);

        // 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, Map.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("instance_id");
        }
        return "";
    }

    /**
     * 创建微信小程序
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createWechatApplet_test")
    private String createWechatApplet_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/tools/wechat_applet/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("name", "丹壑剧场");
                put("user_name", "gh_5858e41e74ba");
                put("tag_info", "1030700000");
                put("remark_message", "备注");
                put("path", "/pages/theatre/index?code=PI1V7BXHY8Q&aid=30012338&item_source=1&media_source=1");
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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("instance_id");
        }
        return "";
    }

    /**
     * 创建DPA商品
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/createDpaProduct_test")
    private String createDpaProduct_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/dpa/product/create/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("platform_id", 7281789144008467L);

                Map<String, Object> product_info = new HashMap<>();
                product_info.put("name", "name");
                product_info.put("image_url", "ad-private-platform-dpa/3528714d7c7df78c5d5db3be4c1c08f8");
                product_info.put("first_category", "短剧");
                product_info.put("sub_category", "其他剧情");
                product_info.put("third_category", "其他");
                product_info.put("first_category_id", "2019");
                product_info.put("sub_category_id", "201912");
                product_info.put("third_category_id", "20191201");

                Map<String, Object> price_info = new HashMap<>();
                price_info .put("price", 2);
                product_info.put("price_info", price_info);

                Map<String, Object> profession = new HashMap<>();
//                profession .put("ad_carrier", "字节小程序");
                profession .put("micro_app_link", "sslocal://microapp?app_id=ttb1d2c76f2ee36a0601&bdp_log=%7B%22launch_from%22%3A%22ad%22%7D&scene=0&start_page=pages%2Ftheatre%2Findex%3Faid%3D40013835%26click_id%3D__CLICKID__%26code%3DPI18J3KP4Q1%26item_source%3D1%26media_source%3D1%26mid1%3D__MID1__%26mid2%3D__MID2__%26mid3%3D__MID3__%26mid4%3D__MID4__%26mid5%3D__MID5__%26request_id%3D__REQUESTID__%26tt_album_id%3D7501559271721533987%26tt_episode_id%3D7501559294426153507&uniq_id=S2025050900035080919205099614197678089b2896e94674&version=v2&version_type=current&bdpsum=9d90188");

                profession .put("has_paid_content", "1");
                profession .put("has_motivation_content", "1");
                profession .put("copyright_owner", "抖音视界有限公司");
                profession .put("playlet_gender", "3");
                profession .put("playlet_num", "50");
                profession .put("playlet_duration", "2");
                profession .put("start_pay_playlet", "10");
                profession .put("membership_types", "5");
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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("product_id");
        }
        return "";
    }


    /**
     * 修改广告状态
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/updatePromotionStatus_test")
    private String updatePromotionStatus_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/status/update/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                List<Object> data = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                map .put("promotion_id", Long.parseLong(params.get("promotion_id")));
                map .put("opt_status", "ENABLE");
                data.add(map);
                put("data", data);
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
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("product_id");
        }
        return "";
    }

    /**
     * 修改广告信息
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/updatePromotion_test")
    private String updatePromotion_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.oceanengine.com/open_api/v3.0/promotion/update/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Map<String, Object> body = new HashMap() {
            {
                put("advertiser_id", Long.parseLong(params.get("advertiser_id")));
                put("promotion_id", Long.parseLong(params.get("promotion_id")));
                put("name", "ceshi");
                Map<String, Object> promotion_materials = new HashMap();
                List<Object> video_material_list = new ArrayList<>();
                Map<String, Object> video_material = new HashMap();
                video_material.put("image_mode", "CREATIVE_IMAGE_MODE_VIDEO_VERTICAL");
                video_material.put("video_id", "v0d033g10000d00rq8fog65v0upu9v4g");
                video_material.put("video_cover_id", "tos-cn-p-0051/oYpfGeHPZIGogZh3TCfALJ6fQBswAA2AfECYBD");
                video_material_list.add(video_material);
                promotion_materials.put("video_material_list", video_material_list);

                List<Object> text_abstract_list = new ArrayList<>();
                Map<String, Object> text_abstract = new HashMap();
                text_abstract.put("abstract_text", "词词词包名词词词包名词词词包名词词词包名词词词包名词词词包名词词词包名"); // 文本摘要内容,单广告可添加1-10个，长度25-45个字
                text_abstract_list.add(text_abstract);
//                promotion_materials.put("text_abstract_list", text_abstract_list);

                List<Object> title_material_list = new ArrayList<>();
                Map<String, Object> title_material = new HashMap();
                title_material.put("title", "title-test"); // 创意标题
                title_material_list.add(title_material);
                promotion_materials.put("title_material_list", title_material_list);

                Map<String, Object> mini_program_info = new HashMap();
                // 字节小程序调起链接
                mini_program_info.put("url", "sslocal://microapp?app_id=ttb1d2c76f2ee36a0601&bdp_log=%7B%22launch_from%22%3A%22ad%22%2C%22location%22%3A%22%22%7D&scene=0&start_page=pages%2Ftheatre%2Findex%3Faid%3D40013835%26click_id%3D__CLICKID__%26code%3DPI0ITY0705N%26item_source%3D1%26media_source%3D1%26mid1%3D__MID1__%26mid2%3D__MID2__%26mid3%3D__MID3__%26mid4%3D__MID4__%26mid5%3D__MID5__%26request_id%3D__REQUESTID__%26tt_album_id%3D7492029487794618918%26tt_episode_id%3D7492029510478987811&uniq_id=W20250416183328040o1dustss&version=v2&version_type=current&bdpsum=72828ac");
                promotion_materials.put("mini_program_info", mini_program_info);

                List<String> call_to_action_buttons = new ArrayList<>();
                call_to_action_buttons.add("测试1");
                promotion_materials.put("call_to_action_buttons", call_to_action_buttons); // 行动号召文案，字数限制：[2-6]，数组上限为10

                List<String> external_url_material_list = new ArrayList<>();
                external_url_material_list.add("https://www.chengzijianzhan.com/tetris/page/7493857882437238793/?projectid=__PROJECT_ID__&promotionid=__PROMOTION_ID__&creativetype=__CTYPE__&clickid=__CLICKID__&mid1=__MID1__&mid2=__MID2__&mid3=__MID3__&mid4=__MID4__&mid5=__MID5__");
                promotion_materials.put("external_url_material_list", external_url_material_list); // 普通落地页链接素材

                Map<String, Object> product_info = new HashMap();
                List<String> titles = new ArrayList<>();
                titles.add("2025热门短剧");
                product_info.put("titles", titles);   // 产品名称，字数限制：[1-20]
                List<String> image_ids = new ArrayList<>();
                image_ids.add("tos-cn-i-sd07hgqsbj/c76af966b1f14944a44986be4ec79e96");
                product_info.put("image_ids", image_ids);  //产品主图，尺寸要求108*108，上限10个
                List<String> selling_points = new ArrayList<>();
                selling_points.add("测试测试测试");
                product_info.put("selling_points", selling_points); //产品卖点，字数限制：[6-9]，数组上限为10
                promotion_materials.put("product_info", product_info);

                promotion_materials.put("intelligent_generation", "ON");

                put("promotion_materials", promotion_materials);

                put("source", "测试");

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

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> result = ((Map<String, Map<String, String>>)response.getBody()).get("data");
            return result.get("product_id");
        }
        return "";
    }
}
