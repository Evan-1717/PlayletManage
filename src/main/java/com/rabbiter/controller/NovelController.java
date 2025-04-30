package com.rabbiter.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.*;
import com.rabbiter.service.NovelService;
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
@RequestMapping("/api/novel")
public class NovelController {
    @Autowired
    private NovelService nvelService;

    /*
     * 新增仓库
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/save")
    public Result save(@RequestBody Novel novel){
        return nvelService.save(novel)?Result.success():Result.fail();
    }

    /*
     * 更新仓库
     * @author rabbiter
     * @date 2023/1/5 19:38
     */
    @PostMapping("/update")
    public Result update(@RequestBody Novel novel){
        return nvelService.updateById(novel)?Result.success():Result.fail();
    }

    /*
     * 删除仓库
     * @author rabbiter
     * @date 2023/1/5 19:40
     */
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return nvelService.removeById(id)?Result.success():Result.fail();
    }

    /*
     * 查询仓库列表
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @GetMapping("/list")
    public Result list(){
        List list = nvelService.list();
        return Result.success(list);
    }

    /*
     * 检查当日是否重复录入
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @PostMapping("/checkrecord")
    public Result checkrecord(@RequestBody HashMap<String, String> param){
        String creater = (String)param.get("creater");
        String date = (String)param.get("date");

        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(Novel::getCreater,creater);
        }
        if(!StringUtils.isEmpty(date)){
            queryWrapper.like(Novel::getDate,date);
        }

        List<Novel1> result = nvelService.selectrecord(queryWrapper);
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
        List<String> createrList = (List<String>)param.get("creater");
        List<String> roleList = (List<String>)param.get("role");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String hiredate1 = (String)param.get("hiredate1");
        String hiredate2 = (String)param.get("hiredate2");

        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            queryWrapper.in(Novel::getCreater,createrList);
        }

        if(roleList!= null && roleList.size()>0) {
            queryWrapper.in(Novel::getRole, roleList);
        }

        Page<Novel> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        if(!StringUtils.isEmpty(location)){
            queryWrapper.like(Novel::getLocation,location);
        }

        if(!StringUtils.isEmpty(content)){
            queryWrapper.like(Novel::getContent,content);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Novel::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Novel::getDate,date2);
        }

        if(!StringUtils.isEmpty(hiredate1)){
            queryWrapper.ge(Novel::getHiredate,hiredate1);
        }

        if(!StringUtils.isEmpty(hiredate2)){
            queryWrapper.le(Novel::getHiredate,hiredate2);
        }

        queryWrapper.orderByDesc(Novel::getDate ,Novel::getTime);
        IPage result = nvelService.pageCC(page,queryWrapper);
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

        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        if(createrList!= null && createrList.size()>0){
            queryWrapper.in(Novel::getCreater,createrList);
        }

        if(roleList!= null && roleList.size()>0) {
            queryWrapper.in(Novel::getRole, roleList);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(Novel::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(Novel::getDate,date2);
        }

        if(!StringUtils.isEmpty(location)){
            queryWrapper.like(Novel::getLocation,location);
        }

        if(!StringUtils.isEmpty(content)){
            queryWrapper.like(Novel::getContent,content);
        }

        if(!StringUtils.isEmpty(hiredate1)){
            queryWrapper.ge(Novel::getHiredate,hiredate1);
        }

        if(!StringUtils.isEmpty(hiredate2)){
            queryWrapper.le(Novel::getHiredate,hiredate2);
        }

        List<Novel1> result = nvelService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, Novel1.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
