package com.example.demo.config.security.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class MobileAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final Object principal;

    /**
     * 认证前，使用该构造器进行封装信息
     */
    public MobileAuthenticationToken(Object principal) {
        super((Collection) null);     // 用户权限为 null
        this.principal = principal;   // 前端传入的手机号
        this.setAuthenticated(false); // 标记为未认证
    }

    /**
     * 认证成功后，使用该构造器封装用户信息
     */
    public MobileAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);          // 用户权限集合
        this.principal = principal;  // 封装认证用户信息的 UserDetails 对象，不再是手机号
        super.setAuthenticated(true); // 标记认证成功
    }

    @Override
    public Object getCredentials() {
        // 由于使用手机短信验证码登录不需要密码，所以直接返回 null
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        // 手机短信验证码认证方式不必去除额外的敏感信息，所以直接调用父类方法
        super.eraseCredentials();
    }
}
