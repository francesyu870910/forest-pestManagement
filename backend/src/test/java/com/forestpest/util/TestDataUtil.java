package com.forestpest.util;

import com.forestpest.entity.User;
import com.forestpest.entity.LoginRequest;

import java.time.LocalDateTime;

/**
 * 测试数据工具类
 */
public class TestDataUtil {
    
    /**
     * 创建测试用户
     */
    public static User createTestUser(String id, String username, String email, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setEmail(email);
        user.setRealName("Test User " + id);
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setDepartment("测试部门");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        return user;
    }
    
    /**
     * 创建管理员用户
     */
    public static User createAdminUser() {
        return createTestUser("admin1", "admin", "admin@test.com", "ADMIN");
    }
    
    /**
     * 创建普通用户
     */
    public static User createNormalUser() {
        return createTestUser("user1", "testuser", "user@test.com", "USER");
    }
    
    /**
     * 创建登录请求
     */
    public static LoginRequest createLoginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
    
    /**
     * 创建无效用户（用于测试验证）
     */
    public static User createInvalidUser() {
        User user = new User();
        user.setUsername(""); // 无效用户名
        user.setEmail("invalid-email"); // 无效邮箱
        user.setPassword("123"); // 密码太短
        return user;
    }
}