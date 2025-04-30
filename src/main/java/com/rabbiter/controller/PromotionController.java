package com.rabbiter.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Promotion;
import com.rabbiter.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器：小说
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    /*
     * 推广
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/promotion")
    public Result promotion(@RequestBody Map<String, String> param){
        Promotion promotion = promotionService.promotion(param);
        return null == promotion?Result.fail():Result.success(promotion);
    }



    /*
     * 删除仓库
     * @author rabbiter
     * @date 2023/1/5 19:40
     */
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return promotionService.removeById(id)?Result.success():Result.fail();
    }

    /*
     * 查询仓库列表
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @GetMapping("/list")
    public Result list(){
        List list = promotionService.list();
        return Result.success(list);
    }


    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String creater = (String)param.get("creater");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String promotion_id = (String)param.get("promotion_id");
        String promotion_name = (String)param.get("promotion_name");

        Page<Promotion> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Promotion> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(Promotion::getCreater,creater);
        }


        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Promotion::getCreater,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Promotion::getCreater,date2);
        }

        if(!StringUtils.isEmpty(promotion_id)){
            queryWrapper.like(Promotion::getPromotion_id,promotion_id);
        }

        if(!StringUtils.isEmpty(promotion_name)){
            queryWrapper.like(Promotion::getPromotion_name,promotion_name);
        }

        queryWrapper.orderByDesc(Promotion::getCreater);
        IPage result = promotionService.pageCC(page,queryWrapper);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 导出
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/export")
    public void export(@RequestBody HashMap<String, String> param, HttpServletResponse response){
        String creater = (String)param.get("creater");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String hiredate1 = (String)param.get("hiredate1");
        String hiredate2 = (String)param.get("hiredate2");

        LambdaQueryWrapper<Promotion> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(Promotion::getCreater,creater);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Promotion::getCreater,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Promotion::getCreater,date2);
        }


        List<Promotion> result = promotionService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, Promotion.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
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
