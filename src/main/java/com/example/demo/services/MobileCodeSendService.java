package com.example.demo.services;

public interface MobileCodeSendService {
    /**
     * 模拟发送手机短信验证码
     */
    void send(String mobile, String code);
}
