package com.example.shirodemo.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 GlobalException
     */
    @ExceptionHandler(GlobalException.class)
    public JsonResult<Void> handleGlobalException(GlobalException e) {
        printMessage(e);

        return JsonResult.error(e.getResponseCode(), e);
    }

    /**
     * 处理 AuthenticationException（认证失败，如用户名不存在、账号被锁定）
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public JsonResult<Void> handleAuthenticationException(AuthenticationException e) {
        printMessage(e);

        return JsonResult.error(ResponseCode.FORBIDDEN, e);
    }

    /**
     * 处理 AuthorizationException（授权失败，即没有访问权限）
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public JsonResult<Void> handleAuthorizationException(AuthorizationException e) {
        printMessage(e);

        return JsonResult.error(ResponseCode.FORBIDDEN, "没有操作权限！");
    }

    /**
     * 处理 BindException
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonResult<Void> handleBindException(BindException e) {
        printMessage(e);

        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String defaultMessage = Objects.requireNonNull(fieldError).getDefaultMessage();

        return JsonResult.error(ResponseCode.BAD_REQUEST, defaultMessage);
    }

    /**
     * 处理 MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        printMessage(e);

        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String defaultMessage = Objects.requireNonNull(fieldError).getDefaultMessage();

        return JsonResult.error(ResponseCode.BAD_REQUEST, defaultMessage);
    }

    /**
     * 打印异常信息
     *
     * @param e Exception
     */
    private void printMessage(Exception e) {
        log.error("捕获异常", e);
    }
}
