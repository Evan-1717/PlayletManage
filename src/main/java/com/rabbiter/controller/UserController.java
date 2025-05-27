package com.rabbiter.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.common.QueryPageParam;
import com.rabbiter.common.Result;
import com.rabbiter.entity.*;
import com.rabbiter.service.MenuService;
import com.rabbiter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * <p>
 *  前端控制器：用户管理和管理员管理模块
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    /*
     * 查询全部用户
     * @author rabbiter
     * @date 2023/1/2 19:26
     */
    @GetMapping("/list")
    public List<User> list(){
        return userService.list();
    }

    /*
     * 根据账号查找用户
     * @author rabbiter
     * @date 2023/1/4 14:53
     */
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no){
        List list = userService.lambdaQuery()
                .eq(User::getNo,no)
                .list();
        return list.size()>0?Result.success(list):Result.fail();
    }

    @GetMapping("/inputInfo")
    public Result inputInfo(){
        Map<String, List<String>> info = userService.inputInfo();
        return info.size()>0?Result.success(info):Result.fail();
    }

    /*
     * 根据账号查找用户
     * @author rabbiter
     * @date 2023/1/4 14:53
     */
    @GetMapping("/distributors")
    public Result distributors(){
        List<String> list = userService.distributors();
        return list.size()>0?Result.success(list):Result.fail();
    }


    /*
     * 新增用户
     * @author rabbiter
     * @date 2023/1/2 19:11
     */
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        dealUserPermission(user);
        return userService.save(user)?Result.success():Result.fail();
    }

    /*
     * 更新用户
     * @author rabbiter
     * @date 2023/1/2 19:11
     */
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        dealUserPermission(user);
        return userService.updateById(user)?Result.success():Result.fail();
    }

    private void dealUserPermission(User user) {
        if ("d".equals(user.getBatch_permission())) {
            user.setDistributor_b("1830267573786713");
            user.setJlaccount(new ArrayList<>(Arrays.asList(new String[]{"jtduanju9075@163.com", "jtduanju9076@163.com", "jtduanju9077@163.com"})));
        } else if ("f".equals(user.getBatch_permission())) {
            user.setDistributor_f("1820577333781620");
            user.setJlaccount(new ArrayList<>(Arrays.asList(new String[]{"fqmfduanju06@163.com"})));
        } else if ("p".equals(user.getBatch_permission())) {
            user.setDistributor_p("1832249089412235");
            user.setJlaccount(new ArrayList<>(Arrays.asList(new String[]{"jtduanju1903A@163.com"})));
        }
    }

    /*
     * 用户登录：登录的时候一并将菜单信息也查询出来
     * @author rabbiter
     * @date 2023/1/3 14:08
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        //匹配账号和密码
        List list = userService.lambdaQuery()
                .eq(User::getNo,user.getNo())
                .eq(User::getPassword,user.getPassword())
                .list();

        if(list.size()>0){
            User user1 = (User)list.get(0);
            List<Menu> menuList = menuService.lambdaQuery()
                    .like(Menu::getMenuright,user1.getRoleId())
                    .list();
            HashMap res = new HashMap();
            res.put("user",user1);
            res.put("menu",menuList);
            return Result.success(res);
        }
        return Result.fail();
    }

    /*
     * 修改用户
     * @author rabbiter
     * @date 2023/1/4 15:02
     */
    @PostMapping("/mod")
    public boolean mod(@RequestBody User user){
        return userService.updateById(user);
    }

    /*
     * 新增或修改：存在用户则修改，否则新增用户
     * @author rabbiter
     * @date 2023/1/2 19:12
     */
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody User user){
        return userService.saveOrUpdate(user)?Result.success():Result.fail();
    }

    /*
     * 删除用户
     * @author rabbiter
     * @date 2023/1/2 19:15
     */
    @GetMapping("/del")
    public Result delete(Integer id){
        return userService.removeById(id)?Result.success():Result.fail();
    }

    /*
     * 模糊查询
     * @author rabbiter
     * @date 2023/1/2 19:36
     */
    @PostMapping("/listP")
    public Result query(@RequestBody User user){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(user.getName())){
            wrapper.like(User::getName,user.getName());
        }
        return Result.success(userService.list(wrapper));
    }

    /*
     * 分页查询
     * @author rabbiter
     * @date 2023/1/2 19:48
     */
//    @PostMapping("/listPage")
//    public Result page(@RequestBody QueryPageParam query){
//        HashMap param = query.getParam();
//        String name = (String)param.get("name");
//
//        Page<User> page = new Page();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.like(User::getName,name);
//
//        IPage result = userService.page(page,wrapper);
//        return Result.success(result.getRecords(),result.getTotal());
//    }

    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        System.out.println("name=>"+(String)param.get("name"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(User::getName,name);


        IPage result = userService.page(page,lambdaQueryWrapper);

        System.out.println("total=>"+result.getTotal());

        return result.getRecords();
    }

    /*
     * 查询功能：根据前端表单输入的信息或者下拉框选择查询用户，并以分页的形式返回前端
     * @author rabbiter
     * @date 2023/1/4 20:28
     */
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        String sex = (String)param.get("sex");
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String roleId = (String)param.get("roleId");

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }
        if(StringUtils.isNotBlank(sex)){
            lambdaQueryWrapper.eq(User::getSex,sex);
        }
        if(StringUtils.isNotBlank(roleId)){
            lambdaQueryWrapper.eq(User::getRoleId,roleId);
        }
        if(StringUtils.isNotBlank(location)){
            lambdaQueryWrapper.like(User::getLocation,location);
        }
        if(StringUtils.isNotBlank(content)){
            lambdaQueryWrapper.like(User::getContent,content);
        }

        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=>"+result.getTotal());

        return Result.success(result.getRecords(),result.getTotal());
    }

    /*
     * 导出
     * @author rabbiter
     * @date 2023/1/5 19:43
     */
    @PostMapping("/export")
    public void export(@RequestBody HashMap<String, String> param, HttpServletResponse response){
        String name = (String)param.get("name");
        String sex = (String)param.get("sex");
        String location = (String)param.get("location");
        String content = (String)param.get("content");
        String roleId = (String)param.get("roleId");

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }
        if(StringUtils.isNotBlank(sex)){
            lambdaQueryWrapper.eq(User::getSex,sex);
        }
        if(StringUtils.isNotBlank(roleId)){
            lambdaQueryWrapper.eq(User::getRoleId,roleId);
        }
        if(StringUtils.isNotBlank(location)){
            lambdaQueryWrapper.like(User::getLocation,location);
        }
        if(StringUtils.isNotBlank(content)){
            lambdaQueryWrapper.like(User::getContent,content);
        }

        List<User1> result = userService.selectrecord(lambdaQueryWrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-disposition", "attachment;fileName="+ System.currentTimeMillis() + ".xlsx");// 设定输出文件头，这里fileName=后面跟的就是文件的名称，可以随意更改
        OutputStream os = null;// 取得输出流
        try {
            os = response.getOutputStream();
            EasyExcel.write(os, User1.class).sheet("user").doWrite(result);//sheet()里面的内容是工作簿的名称
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
