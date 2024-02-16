package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {  // 只能拥有 ROLE_USER 权限的用户访问

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello，User!!!";
    }
}