package com.hu.blog.exception;

import lombok.Getter;

/**
 * 业务异常，用于业务逻辑校验失败时抛出
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(400, message);
    }
}
