package com.chaos.schoollib.config;

import com.chaos.schoollib.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * 1. 禁用 Session，启用 STATELESS
 * 2. 注入 JwtAuthFilter
 * 3. 配置 AuthenticationManager
 * 4. 配置 URL 权限 (基于角色)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * 配置密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 曝露 AuthenticationManager (用于登录认证)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链 (核心)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF (因为我们使用 JWT，无状态)
                .csrf(csrf -> csrf.disable())

                // 2. 配置会话管理为 STATELESS (无状态)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 配置 URL 授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许访问 /ping 和 认证接口 (/api/auth/...)
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // 图书接口 (BookController)
                        // 获取图书列表 和 详情 -> 所有人可见
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                        // 创建/更新/删除图书 -> 仅限 ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

                        // (我们将在阶段四添加 /api/borrow 和 /api/return 的规则)

                        // 其他所有请求都需要身份验证
                        .anyRequest().authenticated()
                )

                // 4. 添加 JWT 过滤器
                // 在 UsernamePasswordAuthenticationFilter 之前运行我们的自定义过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}