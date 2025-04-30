package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.entity.Novel;
import com.rabbiter.entity.Novel1;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;
import com.rabbiter.mapper.NovelMapper;
import com.rabbiter.mapper.TencentMapper;
import com.rabbiter.service.DeepSeekService;
import com.rabbiter.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Service
public class DeepSeekServiceImpl extends ServiceImpl<TencentMapper, Tencent> implements DeepSeekService {
    private static String API_KEY = "sk-4efa15d49fe147aab7faa342aab5308a";

    private static String API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private RestTemplate restTemplate;

    ConcurrentHashMap<String, Map<Long, List<Map<String, String>>>> dialogueInfoMap = new ConcurrentHashMap<>();

    @Override
    public void clearAsk(Map<String, String> params) {
        String userKey = params.get("asker") + params.get("tableName");
        dialogueInfoMap.remove(userKey);
    }

    @Override
    public String analyze(Map<String, Object> params) {
        // 1. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        List<Map<String, String>> messagesList = getMessagesList(params);
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");  // 可能需要指定模型
        body.put("messages", messagesList);

        // 3. 发送请求
        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
            Map<String, String> content = ((List<Map<String, Map<String, String>>>)((Map<String, Object>)response.getBody()).get("choices")).get(0).get("message");
            SaveDialogueInfo(params, messagesList, content);
            return content.get("content");
        } catch (Exception e) {
            System.err.println("请求失败: " + e.getMessage());
            return "";
        }
    }

    private List<Map<String, String>> getMessagesList(Map<String, Object> params) {
        String userKey = (String)params.get("asker") + (String)params.get("tableName");
        Long timeKey = Instant.now().getEpochSecond();
        Map<Long, List<Map<String, String>>> userDialogue = dialogueInfoMap.get(userKey);
        if (userDialogue != null && timeKey - userDialogue.keySet().iterator().next() < 1800) {
            Map<String, String> user = new HashMap() {
                {
                    put("role", "user");
                    put("content", (String)params.get("ask"));
                }
            };
            List<Map<String, String>> messagesList = userDialogue.values().iterator().next();
            messagesList.add(user);
            userDialogue.clear();
            userDialogue.put(timeKey, messagesList);
            return messagesList;
        } else {
            dialogueInfoMap.remove(userKey);
            return getNewMessagesList(params);
        }
    }

    private List<Map<String, String>> getNewMessagesList(Map<String, Object> params) {
        List<Map<String, String>> info =  getNovelInfo(params);
        Map<String, String> system = new HashMap() {
            {
                put("role", "system");
                put("content", info.toString());
            }
        };
        Map<String, String> user = new HashMap() {
            {
                put("role", "user");
                put("content", (String)params.get("ask"));
            }
        };
        List<Map<String, String>> messagesList = new ArrayList<>();
        messagesList.add(system);
        messagesList.add(user);
        return messagesList;
    }

    private void SaveDialogueInfo(Map<String, Object> params, List<Map<String, String>> messagesList, Map<String, String> content) {
        Map<String, String> assistant = new HashMap() {
            {
                put("role", "assistant");
                put("content", content.get("content"));
            }
        };
        messagesList.add(assistant);
        Map<Long, List<Map<String, String>>> userDialogue = new HashMap<>();
        Long timeKey = Instant.now().getEpochSecond();
        userDialogue.put(timeKey, messagesList);
        dialogueInfoMap.put((String)params.get("asker") + (String)params.get("tableName"), userDialogue);
    }

    private List<Map<String, String>> getNovelInfo(Map<String, Object> param) {
        return novelMapper.selectrecordai(param);
    }

}
