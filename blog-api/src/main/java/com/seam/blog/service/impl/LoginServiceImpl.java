package com.seam.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.service.LoginService;
import com.seam.blog.service.SysUserService;
import com.seam.blog.utils.JWTUtils;
import com.seam.blog.vo.ErrorCode;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.ErrorManager;

/*
添加十五注解，当注册过程中redis挂了时，新添加的用户会执行回滚操作
 */
@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    private static final String salt = "seam!@#";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysUserService sysUserService;


    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    @Override
    public Result login(LoginParams loginParams) {
        /**
         * 1.检查参数是否合法
         * 2.根据用户名和密码去数据库中查询是否存在
         * 3.如果不存在则登陆失败
         * 4.如果存在，使用jwt生成token返回给前端
         * 5.token放入redis中， redis token：user信息，设置过期时间
         *（登录认证的时候，先认证token字符串是否合法，去redis中认证）
         */
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //登录成功，使用JWT生成token，返回token和redis中
        String pwd = DigestUtils.md5Hex(password+salt);
        SysUser sysUser = sysUserService.findUser(account,pwd);
        if(sysUser == null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        //登陆成功，使用jwt生成token，返回token和redis中
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public Result register(LoginParams loginParams) {
        /**
         * 1.判断loginParams是否为空
         * 2.判断账号是否已经存在
         * 3.注册成功后生成token自动登录
         */
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickname = loginParams.getNickName();
//        if (account == null || password == null || nickname == null){
//            retur n Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
//        }
        SysUser sysUser = sysUserService.findByAccount(account);
        if (sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIT.getCode(),ErrorCode.ACCOUNT_EXIT.getMsg());
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+salt));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("");
        sysUser.setAdmin(1); //1 为t rue
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);
        return Result.success(token);
    }


    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

}
