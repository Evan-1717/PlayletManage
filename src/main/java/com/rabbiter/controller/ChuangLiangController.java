package com.rabbiter.controller;

import com.rabbiter.common.Result;
import com.rabbiter.service.DeepSeekService;
import com.rabbiter.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

@RestController
@RequestMapping("/api/deepseek")
public class ChuangLiangController {

    private static String API_KEY = "sk-4efa15d49fe147aab7faa342aab5308a";

    private static String BASE_URL = "https://api.deepseek.com";

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private RestTemplate restTemplate;



//    public static void main(String[] args) {
//        ChuangLiangController vo = new ChuangLiangController();
//        try {
//            vo.test_request();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void test_request() throws Exception {
        String url = "https://openapi.mobgi.com/api/Account/report";

        String secret = "3b0a86732b58e6040805326835fdd042";
        String email = "1499519575@qq.com";
        long timestamp = System.currentTimeMillis()/1000;

        //请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("media_type", "toutiao_upgrade");
        params.put("kpis", new ArrayList<>(Arrays.asList(new String[]{"fund_cost", "stat_cost", "pay_amount_roi", "stat_pay_amount"})));
        params.put("page_size", 1000);
        params.put("page", 1);
        params.put("start_date", "2025-04-08");
        params.put("end_date", "2025-04-08");

        //获得到签名
        String signStr = buildSignature(timestamp, params , secret);

        //设置header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("email",email);
        headers.add("timestamp",String.valueOf(timestamp));
        headers.add("signature",signStr);

        //接口请求
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

        //获得响应
        String body = responseEntity.getBody();
        String decodedStr = Utils.decodeUnicode(body);
        System.out.println(decodedStr);

    }

    public String buildSignature(long timestamp, Map<String,Object> params, String secret) throws Exception {
        //加入时间戳
        params.put("timestamp", timestamp);
        //生成url参数字符串
        String signatureStr = http_build_query(params, true);
        //将密钥拼接在后面
        signatureStr += "&cl_secret=" + secret;

        //MD5
        return getMD5(signatureStr);
    }

    /**
     * @param params 参数
     * @return 返回php http_build_query()效果的URL-encode的字符串
     * @throws Exception
     */
    public String http_build_query(Map<String,Object> params) throws Exception {
        return http_build_query(params, true);
    }

    private String getMD5(String str) throws NoSuchAlgorithmException {
        DigestUtils.md5DigestAsHex(str.getBytes());
        String MD5 = DigestUtils.md5DigestAsHex(str.getBytes());
        return MD5.toUpperCase();
    }

    /**
     * Java实现PHP中的http_build_query()效果
     * https://www.cnblogs.com/timseng/p/13280722.html
     *
     * @return
     */
    private String http_build_query(Map<String, Object> array, boolean sort) throws Exception {
        String reString = "";
        //遍历数组形成akey=avalue&bkey=bvalue&ckey=cvalue形式的的字符串
        reString = rescBuild(array, "", true, sort);
        reString = StringUtils.removeEnd(reString, "&");

        //将得到的字符串进行处理得到目标格式的字符串：utf8处理中文出错
        reString = java.net.URLEncoder.encode(reString, "utf-8");
        reString = reString.replace("%3D", "=").replace("%26", "&");
        return reString;
    }

    private String rescBuild(Object object, String parentStr, boolean first, boolean sort) throws Exception {
        String r = "";
        if (object instanceof Map) {

            List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(((Map<String, Object>) object).entrySet());

            //按照map的key排序
            if(sort) {
                Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
                    //升序排序
                    public int compare(Map.Entry<String, Object> o1,
                                       Map.Entry<String, Object> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
            }

            for (Map.Entry<String, Object> mapping : list) {
                String key = mapping.getKey();
                Object value = mapping.getValue();

                if (first) {
                    r += rescBuild(value, key, false, sort);
                } else {
                    r += rescBuild(value, parentStr + "[" + key + "]", false, sort);
                }

            }

        } else if (object instanceof List) {
            for (int i = 0; i < ((List) object).size(); i++) {
                r += rescBuild(((List) object).get(i), parentStr + "[" + i + "]", false, sort);
            }
            //叶节点是String或者Number
        } else if (object instanceof String) {
            r += parentStr + "=" + object.toString() + "&";
        } else if (object instanceof Number) {
            r += parentStr + "=" + ((Number) object).toString() + "&";
        } else if (object instanceof Boolean) {
            r += parentStr + "=" + ((Boolean) object).toString() + "&";
        } else {
            throw new Exception("unsupported type:"+object.getClass().toString());
        }

        return r;
    }

}
