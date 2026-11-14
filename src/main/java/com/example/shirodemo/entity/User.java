package com.example.shirodemo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（MD5 加密 1024 次的密文） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 性别（1-男；2-女） */
    private Integer gender;

    /** 锁定状态：0-正常；1-锁定 */
    private Integer lockState;

    /** 上一次登录时间（未登录默认为null） */
    private Date lastLoginTime;
}
