package com.seam.blog.service;

import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.UserVo;

public interface SysUserService {

    UserVo findUserVoById(Long id);

    SysUser findUser(String account, String pwd);

    SysUser findUserByUser(Long id);

    Result getUserInfoByToken(String token);

    SysUser findByAccount(String account);

    void save(SysUser sysUser);
}
