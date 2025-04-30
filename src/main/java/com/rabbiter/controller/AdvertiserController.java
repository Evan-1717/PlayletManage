package com.rabbiter.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.AdvertiserCost;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.service.AdvertiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 *  前端控制器：仓库管理模块
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/advertiser")
public class AdvertiserController {
    @Autowired
    private AdvertiserService advertiserService;

    private static final Logger LOGGER = Logger.getLogger(AdvertiserController.class.getName());


    /*
     * 计算充值
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping(value = "/calculateExpend")
    @ResponseBody
    public double calculateExpend(@RequestBody Map<String, String> param){
        return advertiserService.calculateExpend(param);
    }


    /*
     * 新增仓库
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/save")
    public Result save(@RequestBody AdvertiserCost advertiserCost){
        return advertiserService.save(advertiserCost)?Result.success():Result.fail();
    }

    /*
     * 删除仓库
     * @author rabbiter
     * @date 2023/1/5 19:40
     */
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return advertiserService.removeById(id)?Result.success():Result.fail();
    }


    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        List<String> createrList = (List<String>)param.get("creater");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");

        Page<AdvertiserCost> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<AdvertiserCost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            lambdaQueryWrapper.in(AdvertiserCost::getCreater,createrList);
        }

        if(!StringUtils.isEmpty(date1)){
            lambdaQueryWrapper.ge(AdvertiserCost::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            lambdaQueryWrapper.le(AdvertiserCost::getDate,date2);
        }
        lambdaQueryWrapper.orderByDesc(AdvertiserCost::getUpdate_date , AdvertiserCost::getFund_recharge, AdvertiserCost::getFund_cost);

        IPage result = advertiserService.pageCC(page,lambdaQueryWrapper);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 导出
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/export")
    public void export(@RequestBody Map<String, Object> param, HttpServletResponse response){
        List<String> createrList = (List<String>)param.get("creater");
        List<String> roleList = (List<String>)param.get("role");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");

        LambdaQueryWrapper<AdvertiserCost> queryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            queryWrapper.in(AdvertiserCost::getCreater,createrList);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(AdvertiserCost::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(AdvertiserCost::getDate,date2);
        }


        List<AdvertiserCost> result = advertiserService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, AdvertiserCost.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        response.reset();
    }

}
