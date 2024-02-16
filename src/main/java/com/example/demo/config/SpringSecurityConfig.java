package com.example.demo.config;

import javax.sql.DataSource;

import com.example.demo.config.security.CustomAuthenticationFailureHandler;
import com.example.demo.config.security.CustomAuthenticationSuccessHandler;
import com.example.demo.config.security.CustomLogoutSuccessHandler;
import com.example.demo.config.security.ImageCodeValidateFilter;
import com.example.demo.config.security.mobile.MobileAuthenticationConfig;
import com.example.demo.config.security.session.CustomInvalidSessionStrategy;
import com.example.demo.config.security.session.CustomSessionInformationExpiredStrategy;
import com.example.demo.services.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@EnableWebSecurity       // 开启 MVC Security 安全配置
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;  // 自定义成功注销登录处理器
    @Autowired
    private ImageCodeValidateFilter imageCodeValidateFilter; // 自定义过滤器（图形验证码校验）
    @Autowired
    private MobileAuthenticationConfig mobileAuthenticationConfig; // 手机短信验证码认证方式的配置类
    @Autowired
    private CustomInvalidSessionStrategy invalidSessionStrategy;  // 自定义 Session 会话失效策略
    @Autowired
    private CustomSessionInformationExpiredStrategy sessionInformationExpiredStrategy;  // 自定义最老会话失效策略
    @Autowired
    private DataSource dataSource;  // 数据源

    /**
     * 配置 JdbcTokenRepositoryImpl，用于 Remember-Me 的持久化 Token
     */
    @Bean
    public JdbcTokenRepositoryImpl tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 配置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        // 第一次启动的时候可以使用以下语句自动建表（可以不用这句话，自己手动建表，源码中有语句的）
        // jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    /**
     * 密码编码器，密码不能明文存储
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 使用 BCryptPasswordEncoder 密码编码器，该编码器会将随机产生的 salt 混入最终生成的密文中
        return new BCryptPasswordEncoder();
    }

    /**
     * 定制用户认证管理器来实现用户认证
     *  1. 提供用户认证所需信息（用户名、密码、当前用户的资源权）
     *  2. 可采用内存存储方式，也可能采用数据库方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 采用内存存储方式，用户认证信息存储在内存中
        // auth.inMemoryAuthentication()
        //        .withUser("admin").password(passwordEncoder()
        //        .encode("123456")).roles("ROLE_ADMIN");

        // 不再使用内存方式存储用户认证信息，而是动态从数据库中获取
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 定制基于 HTTP 请求的用户访问控制
     *  1. 配置拦截的哪一些资源
     *  2. 配置资源所对应的角色权限
     *  3. 定义认证方式：HttpBasic、HttpForm
     *  4. 定制登录页面、登录请求地址、错误处理方式
     *  5. 自定义 Spring Security 过滤器等
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 启动 form 表单登录
        http.formLogin()
                // 设置登录页面的访问路径，默认为 /login，GET 请求；该路径不设限访问
                .loginPage("/login/page")
                // 设置登录表单提交路径，默认为 loginPage() 设置的路径，POST 请求
                .loginProcessingUrl("/login/form")
                // 设置登录表单中的用户名参数，默认为 username
                .usernameParameter("name")
                // 设置登录表单中的密码参数，默认为 password
                .passwordParameter("pwd")
                // 认证成功处理，如果存在原始访问路径，则重定向到该路径；如果没有，则重定向 /index
                //.defaultSuccessUrl("/index")
                // 认证失败处理，重定向到指定地址，默认为 loginPage() + ?error；该路径不设限访问
                //.failureUrl("/login/page?error");
                // 不再使用 defaultSuccessUrl() 和 failureUrl() 方法进行认证成功和失败处理，
                // 使用自定义的认证成功和失败处理器
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // 开启基于 HTTP 请求访问控制
        http.authorizeRequests()
                // 以下访问不需要任何权限，任何人都可以访问
                .antMatchers("/login/page", "/code/image", "/mobile/page", "/code/mobile").permitAll()
                // 以下访问需要 ROLE_ADMIN 权限
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 以下访问需要 ROLE_USER 权限
                .antMatchers("/user/**").hasAuthority("ROLE_USER")
                // 其它任何请求访问都需要先通过认证
                .anyRequest().authenticated();

        // 关闭 csrf 防护
        http.csrf().disable();

        // 将自定义过滤器（图形验证码校验）添加到 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(imageCodeValidateFilter, UsernamePasswordAuthenticationFilter.class);

        // 将手机短信验证码认证的配置与当前的配置绑定
        http.apply(mobileAuthenticationConfig);

        // 开启 Remember-Me 功能
        http.rememberMe()
                // 指定在登录时“记住我”的 HTTP 参数，默认为 remember-me
                .rememberMeParameter("remember-me")
                // 设置 Token 有效期为 200s，默认时长为 2 星期
                .tokenValiditySeconds(200)
                // 设置操作数据库表的 Repository
                .tokenRepository(tokenRepository())
                // 指定 UserDetailsService 对象
                .userDetailsService(userDetailsService);

        // 开启注销登录功能
        http.logout()
                // 用户注销登录时访问的 url，默认为 /logout
                .logoutUrl("/logout")
                // 用户成功注销登录后重定向的地址，默认为 loginPage() + ?logout
                //.logoutSuccessUrl("/login/page?logout")
                // 不再使用 logoutSuccessUrl() 方法，使用自定义的成功注销登录处理器
                .logoutSuccessHandler(logoutSuccessHandler)
                // 指定用户注销登录时删除的 Cookie
                .deleteCookies("JSESSIONID")
                // 用户注销登录时是否立即清除用户的 Session，默认为 true
                .invalidateHttpSession(true)
                // 用户注销登录时是否立即清除用户认证信息 Authentication，默认为 true
                .clearAuthentication(true);

        // 开启 Session 会话管理配置
        http.sessionManagement()
                // 设置 Session 会话失效时重定向路径，默认为 loginPage()
                // .invalidSessionUrl("/login/page")
                // 配置使用自定义的 Session 会话失效处理策略
                .invalidSessionStrategy(invalidSessionStrategy)
                // 设置单用户的 Session 最大并发会话数量，-1 表示不受限制
                .maximumSessions(1)
                // 设置为 true，表示某用户达到最大会话并发数后，新会话请求会被拒绝登录
                .maxSessionsPreventsLogin(true)
                // 设置所要使用的 sessionRegistry，默认为 SessionRegistryImpl 实现类
                .sessionRegistry(sessionRegistry())
                // 最老会话在下一次请求时失效，并重定向到 /login/page
                //.expiredUrl("/login/page");
                // 最老会话在下一次请求时失效，并按照自定义策略处理
                .expiredSessionStrategy(sessionInformationExpiredStrategy);
    }

    /**
     * 注册 SessionRegistry，该 Bean 用于管理 Session 会话并发控制
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 配置 Session 的监听器（注意：如果使用并发 Sessoion 控制，一般都需要配置该监听器）
     * 解决 Session 失效后, SessionRegistry 中 SessionInformation 没有同步失效的问题
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 定制一些全局性的安全配置，例如：不拦截静态资源的访问
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源的访问不需要拦截，直接放行
        web.ignoring().antMatchers("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}