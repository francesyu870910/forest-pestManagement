package com.forestpest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 登录响应实体类
 */
public class LoginResponse {
    
    private String token;
    
    private String username;
    
    private String realName;
    
    private String role;
    
    private String email;
    
    private Long expiresIn;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
    
    private String avatar;

    public LoginResponse() {
        this.loginTime = LocalDateTime.now();
    }

    public LoginResponse(String token, String username, String realName, String role, String email, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.realName = realName;
        this.role = role;
        this.email = email;
        this.expiresIn = expiresIn;
        this.loginTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", expiresIn=" + expiresIn +
                ", loginTime=" + loginTime +
                '}';
    }
}