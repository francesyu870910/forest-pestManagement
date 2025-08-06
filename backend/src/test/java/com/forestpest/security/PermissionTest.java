package com.forestpest.security;

import com.forestpest.entity.User;
import com.forestpest.service.PermissionService;
import com.forestpest.service.UserService;
import com.forestpest.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限控制测试
 */
@SpringBootTest
@ActiveProfiles("test")
class PermissionTest {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;
    
    private User adminUser;
    private User normalUser;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        adminUser = TestDataUtil.createAdminUser();
        normalUser = TestDataUtil.createNormalUser();
        
        try {
            userService.registerUser(adminUser);
            userService.registerUser(normalUser);
        } catch (Exception e) {
            // 用户可能已存在，忽略异常
        }
    }
    
    @Test
    void testAdminPermissions() {
        // 管理员应该拥有所有权限
        assertTrue(permissionService.isAdmin(adminUser.getId()));
        assertTrue(permissionService.hasPermission(adminUser.getId(), PermissionService.Permissions.USER_MANAGE));
        assertTrue(permissionService.hasPermission(adminUser.getId(), PermissionService.Permissions.SYSTEM_CONFIG));
        assertTrue(permissionService.hasRole(adminUser.getId(), PermissionService.Roles.ADMIN));
    }
    
    @Test
    void testNormalUserPermissions() {
        // 普通用户应该只有基本权限
        assertFalse(permissionService.isAdmin(normalUser.getId()));
        assertTrue(permissionService.hasPermission(normalUser.getId(), PermissionService.Permissions.PEST_VIEW));
        assertFalse(permissionService.hasPermission(normalUser.getId(), PermissionService.Permissions.USER_MANAGE));
        assertFalse(permissionService.hasPermission(normalUser.getId(), PermissionService.Permissions.SYSTEM_CONFIG));
        assertTrue(permissionService.hasRole(normalUser.getId(), PermissionService.Roles.USER));
    }
    
    @Test
    void testPermissionChecks() {
        // 测试多权限检查
        assertTrue(permissionService.hasAnyPermission(normalUser.getId(), 
            PermissionService.Permissions.PEST_VIEW, PermissionService.Permissions.USER_MANAGE));
        
        assertFalse(permissionService.hasAllPermissions(normalUser.getId(), 
            PermissionService.Permissions.PEST_VIEW, PermissionService.Permissions.USER_MANAGE));
        
        assertTrue(permissionService.hasAllPermissions(adminUser.getId(), 
            PermissionService.Permissions.PEST_VIEW, PermissionService.Permissions.USER_MANAGE));
    }
    
    @Test
    void testResourceAccess() {
        // 测试资源访问权限
        assertTrue(permissionService.canAccessResource(adminUser.getId(), "user", "1"));
        assertTrue(permissionService.canAccessResource(normalUser.getId(), "pest", "1"));
        
        assertTrue(permissionService.canPerformAction(adminUser.getId(), "delete", "user"));
        assertFalse(permissionService.canPerformAction(normalUser.getId(), "delete", "user"));
    }
}