package com.seam.blog.vo.params;

import lombok.Data;

import java.lang.ref.PhantomReference;

@Data
public class LoginParams {

    private String account;

    private String password;

    private String NickName;


}
