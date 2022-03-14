package com.seam.blog.handler;

import com.alibaba.fastjson.JSON;
import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.service.LoginService;
import com.seam.blog.service.SysUserService;
import com.seam.blog.utils.JWTUtils;
import com.seam.blog.utils.UserThreadLocal;
import com.seam.blog.vo.ErrorCode;
import com.seam.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 1.需要判断请求接口路径是否为HandlerMethod(Controller)
         * 2.判断token是否为空，如果为空，未登录
         * 3.如果token不为空，登录验证loginService checkToken
         * 4.如果认证成功，放行即可
         */
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        //控制台打印相关内容
        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(token == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(),"未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(),"未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //是登录状态，放行
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
            UserThreadLocal.remove();

    }



}
