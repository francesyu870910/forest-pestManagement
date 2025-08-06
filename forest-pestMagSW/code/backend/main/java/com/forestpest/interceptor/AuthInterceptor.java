package com.forestpest.interceptor;

import com.forestpest.annotation.RequireAuth;
import com.forestpest.entity.User;
import com.forestpest.service.AuthService;
import com.forestpest.service.PermissionService;
import com.forestpest.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestpest.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 认证拦截
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法或类上是否有RequireAuth注解
        RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        RequireAuth classAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        
        RequireAuth requireAuth = methodAuth != null ? methodAuth : classAuth;
        
        // 如果没有RequireAuth注解，直接通过
        if (requireAuth == null) {
            return true;
        }
        
        // 如果不需要认证，直接通过
        if (!requireAuth.value()) {
            return true;
        }
        
        // 获取访问令牌
        String authHeader = request.getHeader("Authorization");
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        
        if (token == null) {
            return handleAuthFailure(response, "未提供访问令牌");
        }
        
        // 验证令牌
        if (!authService.validateAccessToken(token)) {
            return handleAuthFailure(response, "访问令牌无效或已过期");
        }
        
        // 获取用户信息
        Optional<User> userOpt = authService.getUserFromToken(token);
        if (!userOpt.isPresent()) {
            return handleAuthFailure(response, "用户不存在");
        }
        
        User user = userOpt.get();
        String userId = user.getId();
        
        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            return handleAuthFailure(response, "用户账户已被禁用");
        }
        
        // 检查角色权限
        String[] requiredRoles = requireAuth.roles();
        if (requiredRoles.length > 0) {
            boolean hasRole = false;
            for (String role : requiredRoles) {
                if (permissionService.hasRole(userId, role)) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                return handleAuthFailure(response, "权限不足：缺少必要角色");
            }
        }
        
        // 检查具体权限
        String[] requiredPermissions = requireAuth.permissions();
        if (requiredPermissions.length > 0) {
            boolean hasPermission = permissionService.hasAnyPermission(userId, requiredPermissions);
            if (!hasPermission) {
                return handleAuthFailure(response, "权限不足：缺少必要权限");
            }
        }
        
        // 设置当前用户到上下文
        if (authService instanceof com.forestpest.service.impl.AuthServiceImpl) {
            ((com.forestpest.service.impl.AuthServiceImpl) authService).setCurrentUser(user);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除当前用户上下文
        if (authService instanceof com.forestpest.service.impl.AuthServiceImpl) {
            ((com.forestpest.service.impl.AuthServiceImpl) authService).clearCurrentUser();
        }
    }
    
    /**
     * 处理认证失败
     */
    private boolean handleAuthFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<String> result = Result.error(message);
        String jsonResponse = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(jsonResponse);
        return false;
    }
}
