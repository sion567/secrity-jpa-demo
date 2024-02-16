package com.example.demo.services;

import com.example.demo.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 判断指定 mobile 是否存在
     */
    public boolean isExistByMobile(String mobile) {
        return userMapper.selectByMobile(mobile) != null;
    }
}