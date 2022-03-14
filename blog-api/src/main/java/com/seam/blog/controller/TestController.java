package com.seam.blog.controller;

import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.utils.UserThreadLocal;
import com.seam.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }
}