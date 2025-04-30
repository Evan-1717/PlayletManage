package com.rabbiter.controller;


import com.rabbiter.common.Result;
import com.rabbiter.entity.Menu;
import com.rabbiter.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器：菜单栏模块
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-03
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /*
     * 根据用户身份获取菜单列表
     * @author rabbiter
     * @date 2023/1/3 20:48
     */
    @GetMapping("/list")
    public Result list(@RequestParam String roleId){
        List list = menuService.lambdaQuery()
                .like(Menu::getMenuright,roleId)
                .list();
        return Result.success(list);
    }
}
