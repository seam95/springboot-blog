package com.seam.blog.controller;

import com.seam.blog.service.LoginService;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.LoginParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    LoginService loginService;


    @PostMapping
    public Result register(@Validated @RequestBody LoginParams loginParams){
        return loginService.register(loginParams);
    }
}
