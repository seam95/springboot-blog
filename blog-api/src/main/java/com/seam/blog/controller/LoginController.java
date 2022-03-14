package com.seam.blog.controller;


import com.seam.blog.service.LoginService;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.LoginParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public Result login(@RequestBody LoginParams loginParams){
        return loginService.login(loginParams);
    }

    @GetMapping("logout")
    public Result logout(@RequestHeader("Authorization") String token){
        return loginService.logout(token);
    }


}
