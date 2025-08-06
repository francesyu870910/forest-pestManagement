package com.forestpest.service.impl;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.LoginResponse;
import com.forestpest.entity.PasswordResetRequest;
import com.forestpest.entity.User;
import com.forestpest.service.AuthService;
import com.forestpest.service.UserService;
import com.forestpest.service.PermissionService;
import com.forestpest.util.JwtUtil;
import com.forestpest.util.IdGenerator;
import com.forestpest.exception.ForestPestSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 令牌黑名单存储
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();
    
    // 用户会话存储
    private final Map<String, List<UserSession>> userSessions = new ConcurrentHashMap<>();
    
    // 会话信息存储
    private final Map<String, String> sessionInfoStorage = new ConcurrentHashMap<>();
    
    // 密码重置令牌存储
    private final Map<String, PasswordResetToken> passwordResetTokens = new ConcurrentHashMap<>();
    
    // 当前用户上下文（线程本地存储）
    private final ThreadLocal<User> currentUserContext = new ThreadLocal<>();
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new ForestPestSystemException("登录请求不能为空");
        }
        
        // 验证用户名和密码
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
        if (!userOpt.isPresent()) {
            throw new ForestPestSystemException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ForestPestSystemException("用户账户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ForestPestSystemException("用户名或密码错误");
        }
        
        // 生成访问令牌和刷新令牌
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        
        // 创建用户会话
        createUserSession(user, accessToken);
        
        // 更新用户最后登录时间
        userService.updateLastLoginTime(user.getId());
        
        // 构建登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(accessToken);
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setExpiresIn(jwtUtil.getTokenRemainingTime(accessToken));
        response.setLoginTime(LocalDateTime.now());
        response.setAvatar(user.getAvatar());
        
        return response;
    }
    
    @Override
    public void logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        
        try {
            // 将令牌加入黑名单
            blacklistToken(token);
            
            // 获取用户信息并清理会话
            String userId = jwtUtil.getUserIdFromToken(token);
            if (userId != null) {
                removeUserSessionByToken(userId, token);
            }
        } catch (Exception e) {
            // 忽略令牌解析错误，继续执行登出逻辑
        }
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ForestPestSystemException("刷新令牌无效或已过期");
        }
        
        if (isTokenBlacklisted(refreshToken)) {
            throw new ForestPestSystemException("刷新令牌已失效");
        }
        
        try {
            String userId = jwtUtil.getUserIdFromToken(refreshToken);
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                throw new ForestPestSystemException("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 检查用户状态
            if (!"ACTIVE".equals(user.getStatus())) {
                throw new ForestPestSystemException("用户账户已被禁用");
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
            
            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(newAccessToken);
            response.setUsername(user.getUsername());
            response.setRealName(user.getRealName());
            response.setRole(user.getRole());
            response.setEmail(user.getEmail());
            response.setExpiresIn(jwtUtil.getTokenRemainingTime(newAccessToken));
            response.setLoginTime(LocalDateTime.now());
            response.setAvatar(user.getAvatar());
            
            return response;
        } catch (Exception e) {
            throw new ForestPestSystemException("刷新令牌失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateAccessToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        if (isTokenBlacklisted(token)) {
            return false;
        }
        
        return jwtUtil.validateAccessToken(token);
    }
    
    @Override
    public boolean validateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        if (isTokenBlacklisted(token)) {
            return false;
        }
        
        return jwtUtil.validateRefreshToken(token);
    }
    
    @Override
    public Optional<User> getUserFromToken(String token) {
        if (!validateAccessToken(token)) {
            return Optional.empty();
        }
        
        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            return userService.findById(userId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public String getUserIdFromToken(String token) {
        if (!validateAccessToken(token)) {
            return null;
        }
        
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        if (!validateAccessToken(token)) {
            return null;
        }
        
        try {
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String getRoleFromToken(String token) {
        if (!validateAccessToken(token)) {
            return null;
        }
        
        try {
            return jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String initiatePasswordReset(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ForestPestSystemException("邮箱地址不能为空");
        }
        
        // 查找用户
        Optional<User> userOpt = userService.findByEmail(email);
        if (!userOpt.isPresent()) {
            // 为了安全考虑，不暴露用户是否存在
            return "如果该邮箱地址存在，您将收到密码重置邮件";
        }
        
        User user = userOpt.get();
        
        // 生成重置令牌
        String resetToken = IdGenerator.generateId();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
            resetToken, user.getId(), user.getEmail(), LocalDateTime.now().plusHours(1)
        );
        
        passwordResetTokens.put(resetToken, passwordResetToken);
        
        // 这里应该发送邮件，目前只是模拟
        // emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        
        return "如果该邮箱地址存在，您将收到密码重置邮件";
    }
    
    @Override
    public boolean validatePasswordResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = passwordResetTokens.get(token);
        if (resetToken == null) {
            return false;
        }
        
        return resetToken.getExpiryTime().isAfter(LocalDateTime.now());
    }
    
    @Override
    public boolean resetPassword(PasswordResetRequest request) {
        if (request == null || request.getResetToken() == null || request.getNewPassword() == null) {
            throw new ForestPestSystemException("重置请求参数不完整");
        }
        
        if (!validatePasswordResetToken(request.getResetToken())) {
            throw new ForestPestSystemException("重置令牌无效或已过期");
        }
        
        PasswordResetToken resetToken = passwordResetTokens.get(request.getResetToken());
        
        // 更新用户密码
        Optional<User> userOpt = userService.findById(resetToken.getUserId());
        if (!userOpt.isPresent()) {
            throw new ForestPestSystemException("用户不存在");
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedTime(LocalDateTime.now());
        
        userService.updateUser(user);
        
        // 删除已使用的重置令牌
        passwordResetTokens.remove(request.getResetToken());
        
        // 终止用户的所有会话
        terminateAllUserSessions(user.getId());
        
        return true;
    }
    
    @Override
    public boolean hasPermission(String userId, String permission) {
        return permissionService.hasPermission(userId, permission);
    }
    
    @Override
    public boolean hasRole(String userId, String role) {
        return permissionService.hasRole(userId, role);
    }
    
    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUserContext.get());
    }
    
    @Override
    public String getCurrentUserId() {
        return getCurrentUser().map(User::getId).orElse(null);
    }
    
    @Override
    public boolean isCurrentUserAdmin() {
        return getCurrentUser()
            .map(user -> permissionService.isAdmin(user.getId()))
            .orElse(false);
    }
    
    @Override
    public void updateUserSession(String userId, String sessionInfo) {
        sessionInfoStorage.put(userId, sessionInfo);
    }
    
    @Override
    public String getUserSession(String userId) {
        return sessionInfoStorage.get(userId);
    }
    
    @Override
    public void clearUserSession(String userId) {
        sessionInfoStorage.remove(userId);
        userSessions.remove(userId);
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
    
    @Override
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            tokenBlacklist.add(token);
        }
    }
    
    @Override
    public void cleanupExpiredBlacklistedTokens() {
        // 清理过期的黑名单令牌
        tokenBlacklist.removeIf(token -> {
            try {
                return jwtUtil.isTokenExpired(token);
            } catch (Exception e) {
                // 如果令牌无法解析，也将其移除
                return true;
            }
        });
        
        // 清理过期的密码重置令牌
        passwordResetTokens.entrySet().removeIf(entry -> 
            entry.getValue().getExpiryTime().isBefore(LocalDateTime.now())
        );
    }
    
    @Override
    public List<UserSession> getUserActiveSessions(String userId) {
        return userSessions.getOrDefault(userId, new ArrayList<>()).stream()
            .filter(UserSession::isActive)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void terminateAllUserSessions(String userId) {
        List<UserSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.forEach(session -> {
                session.setActive(false);
                // 将相关令牌加入黑名单
                blacklistToken(session.getSessionId());
            });
        }
        clearUserSession(userId);
    }
    
    @Override
    public void terminateSession(String sessionId) {
        userSessions.values().forEach(sessions -> 
            sessions.stream()
                .filter(session -> sessionId.equals(session.getSessionId()))
                .forEach(session -> {
                    session.setActive(false);
                    blacklistToken(sessionId);
                })
        );
    }
    
    // 辅助方法
    
    /**
     * 创建用户会话
     */
    private void createUserSession(User user, String token) {
        UserSession session = new UserSession();
        session.setSessionId(token);
        session.setUserId(user.getId());
        session.setUsername(user.getUsername());
        session.setLoginTime(LocalDateTime.now());
        session.setLastAccessTime(LocalDateTime.now());
        session.setActive(true);
        
        userSessions.computeIfAbsent(user.getId(), k -> new ArrayList<>()).add(session);
        
        // 限制每个用户的活跃会话数量（最多5个）
        List<UserSession> sessions = userSessions.get(user.getId());
        if (sessions.size() > 5) {
            // 移除最旧的会话
            sessions.stream()
                .filter(UserSession::isActive)
                .min(Comparator.comparing(UserSession::getLoginTime))
                .ifPresent(oldestSession -> {
                    oldestSession.setActive(false);
                    blacklistToken(oldestSession.getSessionId());
                });
        }
    }
    
    /**
     * 根据令牌移除用户会话
     */
    private void removeUserSessionByToken(String userId, String token) {
        List<UserSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.stream()
                .filter(session -> token.equals(session.getSessionId()))
                .forEach(session -> session.setActive(false));
        }
    }
    
    /**
     * 设置当前用户上下文
     */
    public void setCurrentUser(User user) {
        currentUserContext.set(user);
    }
    
    /**
     * 清除当前用户上下文
     */
    public void clearCurrentUser() {
        currentUserContext.remove();
    }
    
    /**
     * 密码重置令牌内部类
     */
    private static class PasswordResetToken {
        private final String token;
        private final String userId;
        private final String email;
        private final LocalDateTime expiryTime;
        
        public PasswordResetToken(String token, String userId, String email, LocalDateTime expiryTime) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.expiryTime = expiryTime;
        }
        
        public String getToken() {
            return token;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}