package com.example.demo.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义验证码校验错误的异常类，继承 AuthenticationException
 */
public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }
}