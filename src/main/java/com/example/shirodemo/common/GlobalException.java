package com.example.shirodemo.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义全局异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {

    /** 响应状态码 */
    private ResponseCode responseCode;

    public GlobalException(ResponseCode responseCode, String message) {
        super(message);

        setResponseCode(responseCode);
    }
}
