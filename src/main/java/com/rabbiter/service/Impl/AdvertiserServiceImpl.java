package com.rabbiter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.mapper.AdvertiserMapper;
import com.rabbiter.service.AdvertiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
public class AdvertiserServiceImpl extends ServiceImpl<AdvertiserMapper, AdvertiserCost> implements AdvertiserService {
    private static final Logger LOGGER = Logger.getLogger(AdvertiserServiceImpl.class.getName());

    @Autowired
    private AdvertiserMapper advertiserMapper;

    @Override
    public List<AdvertiserCost> selectrecord(Wrapper wrapper) {
        return advertiserMapper.selectrecord(wrapper);
    }

    @Override
    public IPage pageCC(IPage<AdvertiserCost> page, Wrapper wrapper) {
        return advertiserMapper.pageCC(page,wrapper);
    }


    @Override
    public double calculateExpend(Map<String, String> param) {
        double res = 0;
        String date = param.get("date");
        List<AdvertiserCost> advertiserCostList = advertiserMapper.getAdailyCostByUser(param);
        for (AdvertiserCost advertiserCost : advertiserCostList) {
            List<String> dailyCosts = advertiserCost.getDaily_cost();
            for (String dailyCost : dailyCosts) {
                if (dailyCost.startsWith(date)) {
                    res += Double.parseDouble(dailyCost.split(":")[1]);
                }
            }
        }
        return res;
    }
}
