package com.example.demo.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DummyMobileCodeSendService implements MobileCodeSendService{
    @Override
    public void send(String mobile, String code) {
        String sendContent = String.format("验证码为 %s，请勿泄露！", code);
        log.info("向手机号 " + mobile + " 发送短信：" + sendContent);
    }
}
