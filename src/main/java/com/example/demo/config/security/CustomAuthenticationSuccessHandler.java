package com.example.demo.config.security;

import com.example.demo.vo.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 继承 SavedRequestAwareAuthenticationSuccessHandler 类，该类是 defaultSuccessUrl() 方法使用的认证成功处理器
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String xRequestedWith = request.getHeader("x-requested-with");
        // 判断前端的请求是否为 ajax 请求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 认证成功，响应 JSON 数据
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(new ResultData<>(0, "认证成功！")));
        }else {
            // 以下配置等同于前文中的 defaultSuccessUrl("/index")

            // 认证成功后，如果存在原始访问路径，则重定向到该路径；如果没有，则重定向 /index
            // 设置默认的重定的路径
            super.setDefaultTargetUrl("/index");
            // 调用父类的 onAuthenticationSuccess() 方法
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}