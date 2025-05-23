package com.rabbiter.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Tencent;
import com.rabbiter.entity.Tencent1;
import com.rabbiter.entity.TomatoPromotion;
import com.rabbiter.service.TencentService;
import com.rabbiter.service.TomatoService;
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
@RequestMapping("/api/tomato")
public class TomatoController {
    @Autowired
    private TomatoService tomatoService;

    /*
     * 新增仓库
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/save")
    public Result save(@RequestBody TomatoPromotion tomatoPromotion){
        return tomatoService.save(tomatoPromotion)?Result.success():Result.fail();
    }

    /*
     * 获取广告回传与充值模板码值
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/getCodeInfo")
    public Result getCodeInfo(){
        return Result.success(tomatoService.getCodeInfo());
    }

    /*
     * 获取短剧信息
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping("/getVideoInfo")
    public Result getVideoInfo(@RequestBody HashMap<String, Object> param){
        return Result.success(tomatoService.getVideoInfo(param));
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
        List<String> roleList = (List<String>)param.get("role");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String hiredate1 = (String)param.get("hiredate1");
        String hiredate2 = (String)param.get("hiredate2");

        LambdaQueryWrapper<TomatoPromotion> queryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            queryWrapper.in(TomatoPromotion::getCreater,createrList);
        }

        if(roleList!= null && roleList.size()>0){
            queryWrapper.in(TomatoPromotion::getRole,roleList);
        }

        Page<TomatoPromotion> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(TomatoPromotion::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(TomatoPromotion::getDate,date2);
        }

        queryWrapper.orderByDesc(TomatoPromotion::getDate ,TomatoPromotion::getTime);
        IPage result = tomatoService.pageCC(page,queryWrapper);
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
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String hiredate1 = (String)param.get("hiredate1");
        String hiredate2 = (String)param.get("hiredate2");

        LambdaQueryWrapper<TomatoPromotion> queryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            queryWrapper.in(TomatoPromotion::getCreater,createrList);
        }

        if(roleList!= null && roleList.size()>0) {
            queryWrapper.in(TomatoPromotion::getRole, roleList);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(TomatoPromotion::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(TomatoPromotion::getDate,date2);
        }


        List<TomatoPromotion> result = tomatoService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, TomatoPromotion.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
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
