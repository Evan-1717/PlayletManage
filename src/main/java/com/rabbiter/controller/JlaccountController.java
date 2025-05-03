package com.rabbiter.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.JlPromotion;
import com.rabbiter.entity.Jlaccount;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.service.Impl.JlaccountServiceImpl;
import com.rabbiter.service.JlaccountService;
import com.rabbiter.util.AccessToken;
import com.rabbiter.util.Utils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
        Map<String, String> res = new HashMap<>();
        res.put("message","OK");
//        try {
//            jlaccountService.autoCreatePromotion(params);
//        } catch (Exception e) {
//            res.put("message",e.getMessage());
//            LOGGER.info("autoCreatePromotion fail,message : " + e.toString());
//        }
        jlaccountService.autoCreatePromotion(params);
        return Result.success(res);
    }

    /**
     * 获取视频列表
     *
     * @param params:Args in JSON format
     * @return Response in JSON format
     */
    @PostMapping("/getVideoList")
    public Result getVideoList(@RequestBody Map<String, Object> params){
        List<Map<String, Object>> res = jlaccountService.getVideoList(params);
        return res.size() > 0 ? Result.success(res) : Result.fail();
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
    @PostMapping("/listPagePromotion")
    public Result listPagePromotion(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();

        Page<JlPromotion> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        IPage result = jlaccountService.listPagePromotion(page);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPageAutoPromotion")
    public Result listPageAutoPromotion(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        List<String> createrList = (List<String>)param.get("creater");
        List<String> roleList = (List<String>)param.get("role");

        Page<Map<String, String>> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        IPage result = jlaccountService.listPageAutoPromotion(page);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPageProject")
    public Result listPageProject(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        List<String> createrList = (List<String>)param.get("creater");

        Page<Map<String, String>> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        IPage result = jlaccountService.listPageProject(page);
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
                .queryParam("product_platform_id", params.get("product_platform_id"))
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
    @PostMapping("/advertiser_test")
    private String advertiser_test(@RequestBody Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Access-Token", params.get("access_token"));
        List<String> list = new ArrayList<>();

        // 构建带参数的 URI
        URI uri = UriComponentsBuilder.fromHttpUrl("https://ad.oceanengine.com/open_api/2/advertiser/info/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        Map data = new HashMap(){
            {
                put("advertiser_ids", new Long[] {Long.parseLong(params.get("advertiser_id"))});
                put("fields", new String[] {"id", "name", "status"});
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
}
