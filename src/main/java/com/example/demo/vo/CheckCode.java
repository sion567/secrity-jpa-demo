package com.example.demo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CheckCode implements Serializable {
    /**
     * 验证码字符
     */
    private String code;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * @param code 验证码字符
     * @param expireTime 过期时间，单位秒
     */
    public CheckCode(String code, int expireTime) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireTime);
    }

    public CheckCode(String code) {
        // 默认验证码 60 秒后过期
        this(code, 60);
    }

    // 是否过期
    public boolean isExpried() {
        return this.expireTime.isBefore(LocalDateTime.now());
    }

    public String getCode() {
        return this.code;
    }
}
