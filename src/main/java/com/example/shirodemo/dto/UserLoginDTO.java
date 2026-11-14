package com.example.shirodemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录信息 DTO
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @NotBlank(message = "用户名不允许为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不允许为空")
    private String password;
}
