package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    @ResponseBody
    public String index() {   // 跳转到主页
        return "欢迎您登录！！！";
    }
}
