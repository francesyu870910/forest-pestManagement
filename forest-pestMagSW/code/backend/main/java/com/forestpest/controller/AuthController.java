package com.forestpest.controller;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.LoginResponse;
import com.forestpest.entity.PasswordResetRequest;
import com.forestpest.entity.User;
import com.forestpest.service.AuthService;
import com.forestpest.service.UserService;
import com.forestpest.util.JwtUtil;
import com.forestpest.common.Result;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            LoginResponse response = authService.login(loginRequest);
            
            // 记录登录IP和User-Agent（如果需要）
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            return Result.success(response);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token != null) {
                authService.logout(token);
            }
            
            return Result.success("登出成功");
        } catch (Exception e) {
            return Result.error("登出失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return Result.error("刷新令牌不能为空");
            }
            
            LoginResponse response = authService.refreshToken(refreshToken);
            return Result.success(response);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("刷新令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证访问令牌
     */
    @PostMapping("/validate")
    public Result<Map<String, Object>> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("令牌不能为空");
            }
            
            boolean isValid = authService.validateAccessToken(token);
            Map<String, Object> result = new HashMap<>();
            result.put("valid", isValid);
            
            if (isValid) {
                String userId = authService.getUserIdFromToken(token);
                String username = authService.getUsernameFromToken(token);
                String role = authService.getRoleFromToken(token);
                
                result.put("userId", userId);
                result.put("username", username);
                result.put("role", role);
                result.put("remainingTime", jwtUtil.getTokenRemainingTime(token));
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("验证令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("未提供访问令牌");
            }
            
            Optional<User> userOpt = authService.getUserFromToken(token);
            if (userOpt.isPresent()) {
                return Result.success(userOpt.get());
            } else {
                return Result.error("用户不存在或令牌无效");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 发起密码重置
     */
    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱不能为空");
            }
            
            String resetToken = authService.initiatePasswordReset(email);
            
            // 在实际项目中，这里应该发送邮件而不是直接返回令牌
            return Result.success("密码重置邮件已发送，重置令牌: " + resetToken);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("发起密码重置失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证密码重置令牌
     */
    @PostMapping("/validate-reset-token")
    public Result<Boolean> validateResetToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.trim().isEmpty()) {
                return Result.error("重置令牌不能为空");
            }
            
            boolean isValid = authService.validatePasswordResetToken(token);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error("验证重置令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            boolean success = authService.resetPassword(request);
            if (success) {
                return Result.success("密码重置成功");
            } else {
                return Result.error("密码重置失败");
            }
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("未提供访问令牌");
            }
            
            String userId = authService.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无效的访问令牌");
            }
            
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.error("旧密码和新密码不能为空");
            }
            
            boolean success = userService.updatePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户活跃会话
     */
    @GetMapping("/sessions")
    public Result<List<AuthService.UserSession>> getUserSessions(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("未提供访问令牌");
            }
            
            String userId = authService.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无效的访问令牌");
            }
            
            List<AuthService.UserSession> sessions = authService.getUserActiveSessions(userId);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error("获取用户会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 终止所有会话
     */
    @PostMapping("/terminate-all-sessions")
    public Result<String> terminateAllSessions(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("未提供访问令牌");
            }
            
            String userId = authService.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无效的访问令牌");
            }
            
            authService.terminateAllUserSessions(userId);
            return Result.success("所有会话已终止");
        } catch (Exception e) {
            return Result.error("终止会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 终止指定会话
     */
    @PostMapping("/terminate-session")
    public Result<String> terminateSession(@RequestBody Map<String, String> request) {
        try {
            String sessionId = request.get("sessionId");
            if (sessionId == null || sessionId.trim().isEmpty()) {
                return Result.error("会话ID不能为空");
            }
            
            authService.terminateSession(sessionId);
            return Result.success("会话已终止");
        } catch (Exception e) {
            return Result.error("终止会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查令牌是否即将过期
     */
    @GetMapping("/token-status")
    public Result<Map<String, Object>> getTokenStatus(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return Result.error("未提供访问令牌");
            }
            
            Map<String, Object> status = new HashMap<>();
            status.put("valid", authService.validateAccessToken(token));
            status.put("remainingTime", jwtUtil.getTokenRemainingTime(token));
            status.put("expiringSoon", jwtUtil.isTokenExpiringSoon(token));
            
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("获取令牌状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}