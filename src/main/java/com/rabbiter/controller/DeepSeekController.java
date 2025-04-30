package com.rabbiter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Novel;
import com.rabbiter.entity.Novel1;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.service.DeepSeekService;
import com.rabbiter.service.NovelService;
import com.rabbiter.service.ShouzhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deepseek")
public class DeepSeekController {

    private static String API_KEY = "sk-4efa15d49fe147aab7faa342aab5308a";

    private static String BASE_URL = "https://api.deepseek.com";

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/ask")
    public Result analyze(@RequestBody Map<String, Object> params){
        String answer = deepSeekService.analyze(params);
        return !StringUtils.isEmpty(answer) ?Result.success(answer):Result.fail();
    }

    @PostMapping("/clearAsk")
    public void clearAsk(@RequestBody Map<String, String> params){
        deepSeekService.clearAsk(params);
    }


}
