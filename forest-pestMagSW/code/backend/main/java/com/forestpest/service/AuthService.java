package com.forestpest.service;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.LoginResponse;
import com.forestpest.entity.PasswordResetRequest;
import com.forestpest.entity.User;

import java.util.Optional;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 用户登出
     */
    void logout(String token);
    
    /**
     * 刷新访问令牌
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 验证访问令牌
     */
    boolean validateAccessToken(String token);
    
    /**
     * 验证刷新令牌
     */
    boolean validateRefreshToken(String token);
    
    /**
     * 从令牌中获取用户信息
     */
    Optional<User> getUserFromToken(String token);
    
    /**
     * 从令牌中获取用户ID
     */
    String getUserIdFromToken(String token);
    
    /**
     * 从令牌中获取用户名
     */
    String getUsernameFromToken(String token);
    
    /**
     * 从令牌中获取用户角色
     */
    String getRoleFromToken(String token);
    
    /**
     * 发起密码重置
     */
    String initiatePasswordReset(String email);
    
    /**
     * 验证密码重置令牌
     */
    boolean validatePasswordResetToken(String token);
    
    /**
     * 重置密码
     */
    boolean resetPassword(PasswordResetRequest request);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(String userId, String role);
    
    /**
     * 获取当前登录用户
     */
    Optional<User> getCurrentUser();
    
    /**
     * 获取当前登录用户ID
     */
    String getCurrentUserId();
    
    /**
     * 检查当前用户是否为管理员
     */
    boolean isCurrentUserAdmin();
    
    /**
     * 更新用户会话信息
     */
    void updateUserSession(String userId, String sessionInfo);
    
    /**
     * 获取用户会话信息
     */
    String getUserSession(String userId);
    
    /**
     * 清除用户会话
     */
    void clearUserSession(String userId);
    
    /**
     * 检查令牌是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);
    
    /**
     * 将令牌加入黑名单
     */
    void blacklistToken(String token);
    
    /**
     * 清理过期的黑名单令牌
     */
    void cleanupExpiredBlacklistedTokens();
    
    /**
     * 获取用户的所有活跃会话
     */
    java.util.List<UserSession> getUserActiveSessions(String userId);
    
    /**
     * 终止用户的所有会话
     */
    void terminateAllUserSessions(String userId);
    
    /**
     * 终止指定会话
     */
    void terminateSession(String sessionId);
    
    /**
     * 用户会话信息类
     */
    class UserSession {
        private String sessionId;
        private String userId;
        private String username;
        private String ipAddress;
        private String userAgent;
        private java.time.LocalDateTime loginTime;
        private java.time.LocalDateTime lastAccessTime;
        private boolean active;
        
        // Constructors
        public UserSession() {}
        
        public UserSession(String sessionId, String userId, String username) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.username = username;
            this.loginTime = java.time.LocalDateTime.now();
            this.lastAccessTime = java.time.LocalDateTime.now();
            this.active = true;
        }
        
        // Getters and Setters
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getIpAddress() {
            return ipAddress;
        }
        
        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
        
        public String getUserAgent() {
            return userAgent;
        }
        
        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }
        
        public java.time.LocalDateTime getLoginTime() {
            return loginTime;
        }
        
        public void setLoginTime(java.time.LocalDateTime loginTime) {
            this.loginTime = loginTime;
        }
        
        public java.time.LocalDateTime getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void setLastAccessTime(java.time.LocalDateTime lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public void setActive(boolean active) {
            this.active = active;
        }
    }
}