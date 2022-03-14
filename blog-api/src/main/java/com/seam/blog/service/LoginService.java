package com.seam.blog.service;

import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.LoginParams;

public interface LoginService {

    Result logout(String token);

    /**
     * 登录
     * @param loginParams
     * @return
     */
    Result login(LoginParams loginParams);

    Result register(LoginParams loginParams);

    SysUser checkToken(String token);
}
