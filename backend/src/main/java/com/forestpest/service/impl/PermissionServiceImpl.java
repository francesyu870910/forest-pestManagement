package com.forestpest.service.impl;

import com.forestpest.entity.User;
import com.forestpest.service.PermissionService;
import com.forestpest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private UserService userService;
    
    // 角色权限映射
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        // 管理员权限（拥有所有权限）
        Set<String> adminPermissions = new HashSet<>();
        adminPermissions.add(Permissions.USER_VIEW);
        adminPermissions.add(Permissions.USER_CREATE);
        adminPermissions.add(Permissions.USER_UPDATE);
        adminPermissions.add(Permissions.USER_DELETE);
        adminPermissions.add(Permissions.USER_MANAGE);
        
        adminPermissions.add(Permissions.PEST_VIEW);
        adminPermissions.add(Permissions.PEST_CREATE);
        adminPermissions.add(Permissions.PEST_UPDATE);
        adminPermissions.add(Permissions.PEST_DELETE);
        adminPermissions.add(Permissions.PEST_IDENTIFY);
        
        adminPermissions.add(Permissions.TREATMENT_VIEW);
        adminPermissions.add(Permissions.TREATMENT_CREATE);
        adminPermissions.add(Permissions.TREATMENT_UPDATE);
        adminPermissions.add(Permissions.TREATMENT_DELETE);
        adminPermissions.add(Permissions.TREATMENT_APPROVE);
        adminPermissions.add(Permissions.TREATMENT_EXECUTE);
        
        adminPermissions.add(Permissions.PESTICIDE_VIEW);
        adminPermissions.add(Permissions.PESTICIDE_CREATE);
        adminPermissions.add(Permissions.PESTICIDE_UPDATE);
        adminPermissions.add(Permissions.PESTICIDE_DELETE);
        adminPermissions.add(Permissions.PESTICIDE_MANAGE_STOCK);
        
        adminPermissions.add(Permissions.EVALUATION_VIEW);
        adminPermissions.add(Permissions.EVALUATION_CREATE);
        adminPermissions.add(Permissions.EVALUATION_UPDATE);
        adminPermissions.add(Permissions.EVALUATION_DELETE);
        
        adminPermissions.add(Permissions.PREDICTION_VIEW);
        adminPermissions.add(Permissions.PREDICTION_CREATE);
        adminPermissions.add(Permissions.PREDICTION_UPDATE);
        adminPermissions.add(Permissions.PREDICTION_DELETE);
        adminPermissions.add(Permissions.ALERT_MANAGE);
        
        adminPermissions.add(Permissions.FOREST_VIEW);
        adminPermissions.add(Permissions.FOREST_CREATE);
        adminPermissions.add(Permissions.FOREST_UPDATE);
        adminPermissions.add(Permissions.FOREST_DELETE);
        
        adminPermissions.add(Permissions.KNOWLEDGE_VIEW);
        adminPermissions.add(Permissions.KNOWLEDGE_CREATE);
        adminPermissions.add(Permissions.KNOWLEDGE_UPDATE);
        adminPermissions.add(Permissions.KNOWLEDGE_DELETE);
        adminPermissions.add(Permissions.KNOWLEDGE_APPROVE);
        
        adminPermissions.add(Permissions.SYSTEM_CONFIG);
        adminPermissions.add(Permissions.SYSTEM_MONITOR);
        adminPermissions.add(Permissions.SYSTEM_BACKUP);
        adminPermissions.add(Permissions.DATA_EXPORT);
        adminPermissions.add(Permissions.DATA_IMPORT);
        
        ROLE_PERMISSIONS.put(Roles.ADMIN, adminPermissions);
        
        // 普通用户权限
        Set<String> userPermissions = new HashSet<>();
        userPermissions.add(Permissions.PEST_VIEW);
        userPermissions.add(Permissions.PEST_IDENTIFY);
        
        userPermissions.add(Permissions.TREATMENT_VIEW);
        userPermissions.add(Permissions.TREATMENT_EXECUTE);
        
        userPermissions.add(Permissions.PESTICIDE_VIEW);
        
        userPermissions.add(Permissions.EVALUATION_VIEW);
        userPermissions.add(Permissions.EVALUATION_CREATE);
        
        userPermissions.add(Permissions.PREDICTION_VIEW);
        
        userPermissions.add(Permissions.FOREST_VIEW);
        
        userPermissions.add(Permissions.KNOWLEDGE_VIEW);
        
        ROLE_PERMISSIONS.put(Roles.USER, userPermissions);
        
        // 专家权限
        Set<String> expertPermissions = new HashSet<>(userPermissions);
        expertPermissions.add(Permissions.PEST_CREATE);
        expertPermissions.add(Permissions.PEST_UPDATE);
        
        expertPermissions.add(Permissions.TREATMENT_CREATE);
        expertPermissions.add(Permissions.TREATMENT_UPDATE);
        
        expertPermissions.add(Permissions.EVALUATION_UPDATE);
        
        expertPermissions.add(Permissions.PREDICTION_CREATE);
        expertPermissions.add(Permissions.PREDICTION_UPDATE);
        
        expertPermissions.add(Permissions.KNOWLEDGE_CREATE);
        expertPermissions.add(Permissions.KNOWLEDGE_UPDATE);
        
        ROLE_PERMISSIONS.put(Roles.EXPERT, expertPermissions);
        
        // 操作员权限
        Set<String> operatorPermissions = new HashSet<>(userPermissions);
        operatorPermissions.add(Permissions.TREATMENT_CREATE);
        operatorPermissions.add(Permissions.TREATMENT_UPDATE);
        
        operatorPermissions.add(Permissions.PESTICIDE_UPDATE);
        operatorPermissions.add(Permissions.PESTICIDE_MANAGE_STOCK);
        
        operatorPermissions.add(Permissions.FOREST_UPDATE);
        
        ROLE_PERMISSIONS.put(Roles.OPERATOR, operatorPermissions);
    }
    
    @Override
    public boolean hasPermission(String userId, String permission) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            return false;
        }
        
        // 获取用户权限
        Set<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permission);
    }
    
    @Override
    public boolean hasRole(String userId, String role) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        return role.equals(userOpt.get().getRole());
    }
    
    @Override
    public boolean hasAnyPermission(String userId, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        
        Set<String> userPermissions = getUserPermissions(userId);
        return Arrays.stream(permissions)
                .anyMatch(userPermissions::contains);
    }
    
    @Override
    public boolean hasAllPermissions(String userId, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        
        Set<String> userPermissions = getUserPermissions(userId);
        return Arrays.stream(permissions)
                .allMatch(userPermissions::contains);
    }
    
    @Override
    public Set<String> getUserPermissions(String userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            return Collections.emptySet();
        }
        
        User user = userOpt.get();
        return getRolePermissions(user.getRole());
    }
    
    @Override
    public Set<String> getRolePermissions(String role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }
    
    @Override
    public boolean isAdmin(String userId) {
        return hasRole(userId, Roles.ADMIN);
    }
    
    @Override
    public boolean isNormalUser(String userId) {
        return hasRole(userId, Roles.USER);
    }
    
    @Override
    public boolean canAccessResource(String userId, String resourceType, String resourceId) {
        // 基本权限检查
        String viewPermission = resourceType + ":view";
        if (!hasPermission(userId, viewPermission)) {
            return false;
        }
        
        // 管理员可以访问所有资源
        if (isAdmin(userId)) {
            return true;
        }
        
        // 这里可以添加更复杂的资源访问控制逻辑
        // 例如：用户只能访问自己创建的资源
        
        return true;
    }
    
    @Override
    public boolean canPerformAction(String userId, String action, String resourceType) {
        String permission = resourceType + ":" + action;
        return hasPermission(userId, permission);
    }
    
    @Override
    public List<String> getAccessibleResources(String userId, String resourceType) {
        // 这里应该根据用户权限返回可访问的资源列表
        // 简化实现，返回空列表
        return Collections.emptyList();
    }
    
    /**
     * 检查用户是否可以管理其他用户
     */
    public boolean canManageUser(String managerId, String targetUserId) {
        // 管理员可以管理所有用户
        if (isAdmin(managerId)) {
            return true;
        }
        
        // 用户不能管理自己以外的用户
        return managerId.equals(targetUserId);
    }
    
    /**
     * 检查用户是否可以审批防治方案
     */
    public boolean canApproveTreatmentPlan(String userId) {
        return hasPermission(userId, Permissions.TREATMENT_APPROVE);
    }
    
    /**
     * 检查用户是否可以管理库存
     */
    public boolean canManageStock(String userId) {
        return hasPermission(userId, Permissions.PESTICIDE_MANAGE_STOCK);
    }
    
    /**
     * 检查用户是否可以审批知识库内容
     */
    public boolean canApproveKnowledge(String userId) {
        return hasPermission(userId, Permissions.KNOWLEDGE_APPROVE);
    }
    
    /**
     * 检查用户是否可以导出数据
     */
    public boolean canExportData(String userId) {
        return hasPermission(userId, Permissions.DATA_EXPORT);
    }
    
    /**
     * 检查用户是否可以导入数据
     */
    public boolean canImportData(String userId) {
        return hasPermission(userId, Permissions.DATA_IMPORT);
    }
    
    /**
     * 检查用户是否可以配置系统
     */
    public boolean canConfigureSystem(String userId) {
        return hasPermission(userId, Permissions.SYSTEM_CONFIG);
    }
    
    /**
     * 获取用户可执行的操作列表
     */
    public List<String> getUserAvailableActions(String userId, String resourceType) {
        Set<String> userPermissions = getUserPermissions(userId);
        
        return userPermissions.stream()
                .filter(permission -> permission.startsWith(resourceType + ":"))
                .map(permission -> permission.substring(resourceType.length() + 1))
                .collect(Collectors.toList());
    }
    
    /**
     * 检查权限是否存在
     */
    public boolean isValidPermission(String permission) {
        return ROLE_PERMISSIONS.values().stream()
                .anyMatch(permissions -> permissions.contains(permission));
    }
    
    /**
     * 检查角色是否存在
     */
    public boolean isValidRole(String role) {
        return ROLE_PERMISSIONS.containsKey(role);
    }
}