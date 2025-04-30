package com.rabbiter.kaoya.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.Shouzhi;
import com.rabbiter.entity.Shouzhi1;
import com.rabbiter.kaoya.entity.KaoyaShouzhi;
import com.rabbiter.kaoya.service.KaoyaShouzhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器：仓库管理模块
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/kaoyashouzhi")
public class KaoyaShouzhiController {
    @Autowired
    private KaoyaShouzhiService shouzhiService;

    /*
     * 新增
     * @author rabbiter
     * @date 2023/1/5 19:36
     */
    @PostMapping("/save")
    public Result save(@RequestBody KaoyaShouzhi shouzhi){
        return shouzhiService.save(shouzhi)?Result.success():Result.fail();
    }

    /*
     * 更新仓库
     * @author rabbiter
     * @date 2023/1/5 19:38
     */
    @PostMapping("/update")
    public Result update(@RequestBody KaoyaShouzhi shouzhi){
        return shouzhiService.updateById(shouzhi)?Result.success():Result.fail();
    }

    /*
     * 删除仓库
     * @author rabbiter
     * @date 2023/1/5 19:40
     */
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return shouzhiService.removeById(id)?Result.success():Result.fail();
    }

    /*
     * 查询仓库列表
     * @author rabbiter
     * @date 2023/1/5 19:42
     */
    @GetMapping("/list")
    public Result list(){
        List list = shouzhiService.list();
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

        Page<KaoyaShouzhi> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<KaoyaShouzhi> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(KaoyaShouzhi::getCreater,creater);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(KaoyaShouzhi::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(KaoyaShouzhi::getDate,date2);
        }

        queryWrapper.orderByDesc(KaoyaShouzhi::getDate ,KaoyaShouzhi::getTime);
        IPage result = shouzhiService.pageCC(page,queryWrapper);
        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 模糊查询：根据输入查询仓库并以分页的形式展示
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/export")
    public void export(@RequestBody HashMap<String, String> param, HttpServletResponse response){
        String creater = (String)param.get("creater");
        String date1 = (String)param.get("date1");
        String date2 = (String)param.get("date2");

        LambdaQueryWrapper<KaoyaShouzhi> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(creater)){
            queryWrapper.like(KaoyaShouzhi::getCreater,creater);
        }

        if(!StringUtils.isEmpty(date1)){
            queryWrapper.ge(KaoyaShouzhi::getDate,date1);
        }

        if(!StringUtils.isEmpty(date2)){
            queryWrapper.le(KaoyaShouzhi::getDate,date2);
        }

        List<KaoyaShouzhi> result = shouzhiService.selectrecord(queryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, KaoyaShouzhi.class).sheet("record").doWrite(result);//sheet()里面的内容是工作簿的名称
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
