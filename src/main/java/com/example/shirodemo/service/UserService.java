package com.example.shirodemo.service;

import com.example.shirodemo.dto.UserLoginDTO;

/**
 * 用户 Service 接口
 */
public interface UserService {

    /**
     * 登录认证
     *
     * @param loginDTO 登录信息
     */
    void login(UserLoginDTO loginDTO);
}
