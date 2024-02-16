package com.example.demo.config.security.mobile;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 手机短信验证码认证过滤器，仿照 UsernamePasswordAuthenticationFilter 过滤器编写
 */
public class MobileAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String mobileParamter = "mobile";  // 默认手机号参数名为 mobile
    private boolean postOnly = true;    // 默认请求方式只能为 POST

    protected MobileAuthenticationFilter() {
        // 默认登录表单提交路径为 /mobile/form，POST 方式请求
        super(new AntPathRequestMatcher("/mobile/form", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //(1) 默认情况下，如果请求方式不是 POST，会抛出异常
        if(postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }else {
            //(2) 获取请求携带的 mobile
            String mobile = request.getParameter(mobileParamter);
            if(mobile == null) {
                mobile = "";
            }
            mobile = mobile.trim();

            //(3) 使用前端传入的 mobile 构造 Authentication 对象，标记该对象未认证
            // MobileAuthenticationToken 是我们自定义的 Authentication 类，后续介绍
            MobileAuthenticationToken authRequest = new MobileAuthenticationToken(mobile);
            //(4) 将请求中的一些属性信息设置到 Authentication 对象中，如：remoteAddress，sessionId
            this.setDetails(request, authRequest);
            //(5) 调用 ProviderManager 类的 authenticate() 方法进行身份认证
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Nullable
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(this.mobileParamter);
    }

    protected void setDetails(HttpServletRequest request, MobileAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String mobileParamter) {
        Assert.hasText(mobileParamter, "Mobile par ameter must not be empty or null");
        this.mobileParamter = mobileParamter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public String getMobileParameter() {
        return mobileParamter;
    }
}
