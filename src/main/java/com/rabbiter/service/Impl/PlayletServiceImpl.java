package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.common.Constant;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Playlet;
import com.rabbiter.mapper.PlayletMapper;
import com.rabbiter.service.PlayletService;
import com.rabbiter.util.Utils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
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
public class PlayletServiceImpl extends ServiceImpl<PlayletMapper, Playlet> implements PlayletService {
    private static final Logger LOGGER = Logger.getLogger(PlayletServiceImpl.class.getName());

    @Autowired
    private PlayletMapper playletMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Playlet> selectrecord(Wrapper wrapper) {
        return playletMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<Playlet> page, Wrapper wrapper) {
        return playletMapper.pageCC(page,wrapper);
    }

    @Override
    public List<String> getdistributorIdBydistributor(Map<String, String> param){
        return playletMapper.getdistributorIdBydistributor(param);
    }

    @Override
    public Map<String, Object> getdistributorIdInfo(Map<String, String> param){
        Map<String, Object> result = playletMapper.getdistributorIdInfo(param).get(0);
        List<LinkedHashMap<String, Object>> rechargeTemplateInfo = getRechargeTemplateId(param, result);
        List<LinkedHashMap<String, Object>> packStrategyStatus = getpackStrategyStatus(param, result);
        result.put("distributorIdInfo", rechargeTemplateInfo);
        result.put("adCallbackConfigInfo", packStrategyStatus);
        return result;
    }

    @Override
    public
    List<String> exporttopPlaylet(MultipartFile file){
        playletMapper.clearTable();
        List<String> failList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream); // 自动识别.xls/.xlsx
            for (int i = 0; i< workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                Map<String, String> map = new HashMap<>();
                map.put("distributor_id", Constant.PLAYLET_COUNT.get(sheetName));
                map.put("secret_key", "gTHDJrzsiUOwepLIv6hq9AZ0nNf2aQCx");
                map.put("carrier", sheetName);
                for (Row row : sheet) {
                    String id = row.getCell(1).toString();
                    String timeInfo = row.getCell(3).toString();
                    map.put("playlet_id", id);
                    map.put("update_time", timeInfo);
                    if (id.length() != 19) {
                        continue;
                    }
                    String res = save(map);
                    if (!"success".equals(res)) {
                        failList.add(res);
                    }
                }
            }
            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return failList;
    }


    @Override
    public double calculateRecharge(Map<String, String> param) {
        String date = param.get("date");
        param.put("tableName", date + "_userpaidorder");
        Double count = null;
        try {
            count = playletMapper.calculateRecharge(param);
        } catch (Exception e) {
            LOGGER.info("tableName doesn't exist:" +  date + "_userpaidorder");
        }
        if (null == count) {
            return 0;
        } else {
            return count / 100;
        }
    }

    @Override
    public String save(Map<String, String> params){
        String url = "https://www.changdunovel.com/novelsale/openapi/content/book_meta/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&book_id={book_id}";
        String distributor_id = params.get("distributor_id");
        Long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id + params.get("secret_key") + ts);

        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("book_id", params.get("playlet_id"));
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        List<LinkedHashMap<String, Object>> resultList = (List<LinkedHashMap<String, Object>>)res.getBody().get("result");
        if (resultList.size()>0) {
            LinkedHashMap<String, Object> resultMap = resultList.get(0);
            resultMap.put("carrier", params.get("carrier"));
            resultMap.put("update_time", params.get("update_time"));
            Playlet playlet = new Playlet(resultMap);
            playletMapper.savePlaylet(playlet);
            return "success";
        }
        return params.get("playlet_id");
    }

    private List<LinkedHashMap<String, Object>> getRechargeTemplateId(Map<String, String> params, Map<String, Object> distributorIdInfo){
        String url = "https://www.changdunovel.com/novelsale/openapi/recharge_template/list/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&page_size={page_size}&page_index={page_index}";
        String distributor_id = params.get("distributorId");
        long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id+ (String)distributorIdInfo.get("secret_key") +ts);

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
        return (List<LinkedHashMap<String, Object>>)res.getBody().get("data");
    }

    private List<LinkedHashMap<String, Object>> getpackStrategyStatus(Map<String, String> params, Map<String, Object> distributorIdInfo){
        String url = "https://www.changdunovel.com/novelsale/openapi/ad_callback_config/config_list/v1/?sign={sign}&distributor_id={distributor_id}&ts={ts}&media_source={media_source}";
        String distributor_id = params.get("distributorId");
        long ts = Instant.now().getEpochSecond();
        String sign = Utils.calculateMD5(distributor_id+ (String)distributorIdInfo.get("secret_key") +ts);
        int media_source = "微信小程序".equals(distributorIdInfo.get("media_source")) ? 4 : 1;
        Map param = new HashMap() {
            {
                put("distributor_id", distributor_id);
                put("ts", ts);
                put("sign", sign);
                put("media_source", media_source);
            }
        };
        ResponseEntity<Map<String,Object>> res = restTemplate.getForEntity(url, Map.class, param);
        List<LinkedHashMap<String, Object>> result = (List<LinkedHashMap<String, Object>>)res.getBody().get("config_list");
        LinkedHashMap<String, Object> dft = result.get(0);
        if (dft.size() == 1 && "平台默认回传".equals(dft.get("config_name"))) {
            dft.put("config_id", "-1");
        }
        return result;
    }
}
