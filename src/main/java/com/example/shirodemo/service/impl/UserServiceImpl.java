package com.example.shirodemo.service.impl;

import com.example.shirodemo.common.GlobalException;
import com.example.shirodemo.common.ResponseCode;
import com.example.shirodemo.dto.UserLoginDTO;
import com.example.shirodemo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public void login(UserLoginDTO loginDTO) {
        // shiro 登录认证
        UsernamePasswordToken token = new UsernamePasswordToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            throw new GlobalException(ResponseCode.FORBIDDEN, "登录失败，用户名或密码错误！");
        }

        // 设置 session 失效时间：永不超时（仅演示用，生产环境请设置合理的超时时间）
        subject.getSession().setTimeout(-1001);
    }
}
