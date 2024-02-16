package com.example.demo.config.security.mobile;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.demo.common.Constants;
import com.example.demo.config.security.CustomAuthenticationFailureHandler;
import com.example.demo.exceptions.ValidateCodeException;
import com.example.demo.vo.CheckCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 手机短信验证码校验
 */
@Component
public class MobileCodeValidateFilter extends OncePerRequestFilter {

    private String codeParamter = "mobileCode";  // 前端输入的手机短信验证码参数名

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler; // 自定义认证失败处理器

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 非 POST 方式的手机短信验证码提交请求不进行校验
        if("/mobile/form".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            try {
                // 检验手机验证码的合法性
                validate(request);
            } catch (ValidateCodeException e) {
                // 将异常交给自定义失败处理器进行处理
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }

        // 放行，进入下一个过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 检验用户输入的手机验证码的合法性
     */
    private void validate(HttpServletRequest request) {
        // 获取用户传入的手机验证码值
        String requestCode = request.getParameter(this.codeParamter);
        if(requestCode == null) {
            requestCode = "";
        }
        requestCode = requestCode.trim();


        // 获取 Session
        HttpSession session = request.getSession();
        // 获取 Session 中存储的手机短信验证码
        CheckCode savedCode = (CheckCode) session.getAttribute(Constants.MOBILE_SESSION_KEY);

        if (savedCode != null) {
            // 随手清除验证码，无论是失败，还是成功。客户端应在登录失败时刷新验证码
            session.removeAttribute(Constants.MOBILE_SESSION_KEY);
        }

        // 校验出错，抛出异常
        if (StringUtils.isBlank(requestCode)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (savedCode == null) {
            throw new ValidateCodeException("验证码不存在");
        }

        if (savedCode.isExpried()) {
            throw new ValidateCodeException("验证码过期");
        }

        if (!requestCode.equalsIgnoreCase(savedCode.getCode())) {
            throw new ValidateCodeException("验证码输入错误");
        }
    }
}