package com.chaos.schoollib.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 用户实体 (POJO)
 * 实现了 UserDetails 接口，以便与 Spring Security 集成
 */
@Data
public class User implements UserDetails {

    private Integer userID;
    private String username;
    private String password;
    private String role; // 'student', 'teacher', 'admin'
    private String contact;
    private LocalDateTime registrationDate;

    // ===================================
    //  UserDetails 接口的实现
    // ===================================

    /**
     * 返回用户的权限集合
     * Spring Security 需要权限前缀 "ROLE_"
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }

    // getPassword() 和 getUsername() 由 Lombok @Data 生成

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账户永不过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 账户永不锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证永不过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账户启用
    }
}