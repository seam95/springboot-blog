package com.seam.blog.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seam.blog.dao.mapper.SysMapper;
import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.service.SysUserService;
import com.seam.blog.utils.JWTUtils;
import com.seam.blog.vo.ErrorCode;
import com.seam.blog.vo.LoginUserVo;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysMapper sysMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public SysUser findUser(String account, String pwd) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,pwd);
        queryWrapper.select(SysUser::getAccount,SysUser::getAvatar,SysUser::getId,SysUser::getNickname);
        queryWrapper.last("limit 1");
        SysUser sysUser = sysMapper.selectOne(queryWrapper);
        return sysUser;
    }

    @Override
    public SysUser findUserByUser(Long id) {
        SysUser sysUser = sysMapper.selectById(id);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("seam");
        }
        return sysUser;
    }

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysMapper.selectById(id);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("seam");
        }
        UserVo userVo = new UserVo();
        userVo.setAvatar(sysUser.getAvatar());
        userVo.setId(sysUser.getId());
        userVo.setNickname(sysUser.getNickname());
        return userVo;
    }


    @Override
    public Result getUserInfoByToken(String token) {
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null ){
            return Result.fail(ErrorCode.ACCOUNT_TOKEN_ERROR.getCode(),ErrorCode.ACCOUNT_TOKEN_ERROR.getMsg());
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_"+token);
        if (StringUtils.isBlank(userJson)){
            Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        SysUser sysUser = JSON.parseObject(userJson,SysUser.class);
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickName(sysUser.getNickname());
        return Result.success(loginUserVo);
    }

    @Override
    public SysUser findByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");
        return sysMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
        //默认生成的id，是分布式id，采用了雪花算法
            this.sysMapper.insert(sysUser);
    }
}
