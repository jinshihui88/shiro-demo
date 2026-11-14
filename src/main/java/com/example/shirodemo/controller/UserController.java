package com.example.shirodemo.controller;

import com.example.shirodemo.common.JsonResult;
import com.example.shirodemo.common.ResponseCode;
import com.example.shirodemo.dto.UserLoginDTO;
import com.example.shirodemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 登录认证
     *
     * @param loginDTO 登录信息
     * @return JsonResult<Void>
     */
    @PostMapping("/login")
    public JsonResult<Void> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        userService.login(loginDTO);

        return JsonResult.success("登录成功");
    }

    /**
     * 删除用户（需要 user:delete 权限）
     *
     * @return JsonResult<Void>
     */
    @PostMapping("/delete")
    public JsonResult<Void> delete() {
        return JsonResult.success("删除成功");
    }

    /**
     * 修改用户（需要 user:update 权限）
     *
     * @return JsonResult<Void>
     */
    @PostMapping("/update")
    public JsonResult<Void> update() {
        return JsonResult.success("修改成功");
    }

    /**
     * 无权限时的统一响应（Shiro 的 unauthorizedUrl 指向这里）
     *
     * @return JsonResult<Void>
     */
    @RequestMapping("/unauthorized")
    public JsonResult<Void> unauthorized() {
        return JsonResult.error(ResponseCode.FORBIDDEN, "没有操作权限！");
    }
}
