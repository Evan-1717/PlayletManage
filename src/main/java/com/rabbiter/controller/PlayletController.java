package com.rabbiter.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Playlet;
import com.rabbiter.service.PlayletService;
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
 *  前端控制器：小说
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/playlet")
public class PlayletController {
    @Autowired
    private PlayletService playletService;

    private static final Logger LOGGER = Logger.getLogger(PlayletController.class.getName());

    /*
     * 计算充值
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping(value = "/calculate")
    @ResponseBody
    public double calculateRecharge(@RequestBody Map<String, String> param){
        return playletService.calculateRecharge(param);
    }

    /*
     * 新增仓库
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/save")
    public Result save(@RequestBody Map<String, String> param){
        return playletService.save(param);
    }

    /*
     * 更新仓库
     * @author rabbiter
     * @date 2023/1/5 19:38
     */
    @PostMapping("/update")
    public Result update(@RequestBody Playlet Playlet){
        return playletService.updateById(Playlet)?Result.success():Result.fail();
    }

    /*
     * 删除仓库
     * @author rabbiter
     * @date 2023/1/5 19:40
     */
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return playletService.removeById(id)?Result.success():Result.fail();
    }

    /*
     * 查询仓库列表
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @GetMapping("/list")
    public Result list(){
        List list = playletService.list();
        return Result.success(list);
    }

    /*
     * 检查当日是否重复录入
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping("/checkrecord")
    public Result checkrecord(@RequestBody HashMap<String, String> param){
        String playlet_id = (String)param.get("playlet_id");

        LambdaQueryWrapper<Playlet> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(playlet_id)){
            queryWrapper.like(Playlet::getPlaylet_id,playlet_id);
        }

        List<Playlet> result = playletService.selectrecord(queryWrapper);
        return Result.success(result);
    }


    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String playlet_id = (String)param.get("playlet_id");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String playlet_name = (String)param.get("playlet_name");

        Page<Playlet> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Playlet> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(playlet_id)){
            queryWrapper.like(Playlet::getPlaylet_id,playlet_id);
        }

        if(!StringUtils.isEmpty(playlet_name)){
            queryWrapper.like(Playlet::getPlaylet_name,playlet_name);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Playlet::getRelease_time,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Playlet::getRelease_time,date2);
        }



        queryWrapper.orderByDesc(Playlet::getRelease_time);
        IPage result = playletService.pageCC(page,queryWrapper);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 导出
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/export")
    public void export(@RequestBody HashMap<String, String> param, HttpServletResponse response){
        String playlet_id = (String)param.get("playlet_id");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String playlet_name = (String)param.get("playlet_name");

        LambdaQueryWrapper<Playlet> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(playlet_id)){
            queryWrapper.like(Playlet::getPlaylet_id,playlet_id);
        }

        if(!StringUtils.isEmpty(playlet_name)){
            queryWrapper.like(Playlet::getPlaylet_name,playlet_name);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Playlet::getRelease_time,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Playlet::getRelease_time,date2);
        }


        List<Playlet> result = playletService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, Playlet.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
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

    /*
     * 计算充值
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping(value = "/getdistributorIdBydistributor")
    @ResponseBody
    public Result getdistributorIdBydistributor(@RequestBody Map<String, String> param){
        List<String> list = playletService.getdistributorIdBydistributor(param);
        return list.size()>0?Result.success(list):Result.fail();
    }

    /*
     * 计算充值
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping(value = "/getdistributorIdInfo")
    @ResponseBody
    public Result getdistributorIdInfo(@RequestBody Map<String, String> param){
        Map<String, Object> info = playletService.getdistributorIdInfo(param);
        return info.size()>0?Result.success(info):Result.fail();
    }

}
