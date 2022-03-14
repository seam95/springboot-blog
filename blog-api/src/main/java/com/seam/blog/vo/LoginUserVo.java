package com.seam.blog.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginUserVo {

    @NotBlank
    private Long id;

    @NotBlank
    private String account;

    @NotBlank
    private String NickName;

    private String avatar;

}
