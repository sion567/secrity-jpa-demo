package com.example.demo.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.example.demo.vo.ResultData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/test1")
    @ResponseBody
    public Object test1() {
        // 从 SecurityContextHolder 获取认证用户信息对象 Authentication
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/test2")
    @ResponseBody
    public Object test2() {
        // 从 SecurityContextHolder 获取认证用户信息对象 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 从 Authentication 中获取 UserDetails
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return user;
    }

    @GetMapping("/test3")
    @ResponseBody
    public Object test3(HttpSession session) {
        // 获取 Session 获取 SecurityContext
        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        // 从 Authentication 中获取 UserDetails
        UserDetails user = (UserDetails) context.getAuthentication().getPrincipal();
        return user;
    }

    @GetMapping("/test4")
    @ResponseBody
    public Object getOnlineSession() {
        // 统计当前用户未过期的并发 Session 数量
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(user, false);
        return new ResultData<>(sessions.size());
    }

    @GetMapping("/test5")
    @ResponseBody
    public Object getOnlineUsers() {
        // 统计所有在线用户
        List<String> userList = sessionRegistry.getAllPrincipals().stream()
                .map(user -> ((UserDetails) user).getUsername())
                .collect(Collectors.toList());
        return new ResultData<>(userList);
    }
}