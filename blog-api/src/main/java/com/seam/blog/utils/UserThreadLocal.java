package com.seam.blog.utils;

import com.seam.blog.dao.pojo.SysUser;

public class UserThreadLocal {

    //构造方法私有化，不能在其他类中通过new关键字实例对象，单例设计模式
    private UserThreadLocal(){};

    private final static ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void put(SysUser sysUser){
        LOCAL.set(sysUser);
    }

    public static SysUser get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }


}
