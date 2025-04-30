package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rabbiter.controller.ShouzhiController;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.entity.UserPaidOrder;
import com.rabbiter.mapper.ShouzhiMapper;
import com.rabbiter.service.ShouzhiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class ShouzhiServiceImpl extends ServiceImpl<ShouzhiMapper, Shouzhi> implements ShouzhiService {
    private static final Logger LOGGER = Logger.getLogger(ShouzhiServiceImpl.class.getName());

    @Autowired
    private ShouzhiMapper shouzhiMapper;

    @Override
    public List<Shouzhi1> selectrecord(Wrapper wrapper) {
        return shouzhiMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<Shouzhi> page, Wrapper wrapper) {
        return shouzhiMapper.pageCC(page,wrapper);
    }

    @Override
    public double calculateRecharge(Map<String, String> param) {
        String date = param.get("date");
        param.put("tableName", date + "_userpaidorder");
        Double count = null;
        try {
            count = shouzhiMapper.calculateRecharge(param);
        } catch (Exception e) {
            LOGGER.info("tableName doesn't exist:" +  date + "_userpaidorder");
        }
        if (null == count) {
            return 0;
        } else {
            return count / 100;
        }

    }
}
