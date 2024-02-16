package com.example.demo.config.security.mobile;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

public class MobileAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserDetailsChecker authenticationChecks = new MobileAuthenticationProvider.DefaultAuthenticationChecks();

    /**
     * 处理认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //(1) 如果入参的 Authentication 类型不是 MobileAuthenticationToken，抛出异常
        Assert.isInstanceOf(MobileAuthenticationToken.class, authentication, () -> {
            return this.messages.getMessage("MobileAuthenticationProvider.onlySupports", "Only MobileAuthenticationToken is supported");
        });

        // 获取手机号
        String mobile = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        //(2) 根据手机号从数据库中查询用户信息
        UserDetails user = this.userDetailsService.loadUserByUsername(mobile);
        if (user == null) {
            //(3) 未查询到用户信息，抛出异常
            throw new AuthenticationServiceException("该手机号未注册");
        }

        //(4) 检查账号是否锁定、账号是否可用、账号是否过期、密码是否过期
        this.authenticationChecks.check(user);

        //(5) 查询到了用户信息，则认证通过，构建标记认证成功用户信息类对象 AuthenticationToken
        MobileAuthenticationToken result = new MobileAuthenticationToken(user, user.getAuthorities());
        // 需要把认证前 Authentication 对象中的 details 信息加入认证后的 Authentication
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * ProviderManager 管理器通过此方法来判断是否采用此 AuthenticationProvider 类
     * 来处理由 AuthenticationFilter 过滤器传入的 Authentication 对象
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // isAssignableFrom 返回 true 当且仅当调用者为父类.class，参数为本身或者其子类.class
        // ProviderManager 会获取 MobileAuthenticationFilter 过滤器传入的 Authentication 类型
        // 所以当且仅当 authentication 的类型为 MobileAuthenticationToken 才返回 true
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 此处传入自定义的 MobileUserDetailsSevice 对象
     */
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    /**
     * 检查账号是否锁定、账号是否可用、账号是否过期、密码是否过期
     */
    private class DefaultAuthenticationChecks implements UserDetailsChecker {
        private DefaultAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            } else if (!user.isEnabled()) {
                throw new DisabledException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            } else if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            } else if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException(MobileAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }
}