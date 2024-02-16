package com.example.demo.services;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MobileUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        //(1) 从数据库尝试读取该用户
        User user = userMapper.selectByMobile(mobile);
        // 用户不存在，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        //(2) 将数据库形式的 roles 解析为 UserDetails 的权限集合
        // AuthorityUtils.commaSeparatedStringToAuthorityList() 是 Spring Security 提供的方法，用于将逗号隔开的权限集字符串切割为可用权限对象列表
        user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));

        //(3) 返回 UserDetails 对象
        return user;
    }
}