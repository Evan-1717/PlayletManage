package com.rabbiter.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccessToken {

    public static String auth_code = "";

    public static String access_token = "";

    public static String refresh_token = "";

    public static Calendar calendar_old = null;

    public static String getAccessToken() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();

        // 添加一天到当前日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        if (calendar_old == null || calendar.after(calendar_old)) {
            JSONObject obj = refreshToken(null,null,null);
            if (obj.size() == 3) {
                JSONObject getObj = getAccessToken("1829618877107258","11923da08a872abbf0be43ce8a4c17deeab262cd",auth_code);
                JSONObject data = (JSONObject)getObj.get("data");
                access_token = (String)data.get("access_token");
                refresh_token = (String)data.get("refresh_token");
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar_old = calendar;
                return access_token;
            }
            JSONObject data = (JSONObject)obj.get("data");
            access_token = (String)data.get("access_token");
            refresh_token = (String)data.get("refresh_token");
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar_old = calendar;
            return access_token;
        }
        return access_token;

    }

    public static JSONObject refreshToken(String appid, String secret, String refresh_token) {
        // 请求地址
        String open_api_url_prefix = "https://ad.oceanengine.com/open_api/";
        String uri = "oauth2/refresh_token/";
        // 请求参数
        Map<String, Object> data = new HashMap() {
            {
                put("app_id", appid);
                put("secret", secret);
                put("grant_type", "refresh_token");
            }
        };
        data.put("refresh_token", refresh_token);

        // 构造请求
        HttpPost httpEntity = new HttpPost(open_api_url_prefix + uri);

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;

        try {
            client = HttpClientBuilder.create().build();
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

    public static JSONObject getAccessToken(String appid, String secret,String auth_code) {
        // 请求地址
        String open_api_url_prefix = "https://ad.oceanengine.com/open_api/";
        String uri = "oauth2/access_token/";

        // 请求参数
        Map<String, Object> data = new HashMap() {
            {
                put("app_id", appid);
                put("secret", secret);
                put("grant_type", "auth_code");
            }
        };
        data.put("auth_code", auth_code);

        // 构造请求
        HttpPost httpEntity = new HttpPost(open_api_url_prefix + uri);

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;

        try {
            client = HttpClientBuilder.create().build();
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


}
