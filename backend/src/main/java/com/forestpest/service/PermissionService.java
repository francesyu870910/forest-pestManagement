package com.forestpest.service;

import com.forestpest.entity.User;

import java.util.List;
import java.util.Set;

/**
 * 权限管理服务接口
 */
public interface PermissionService {
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(String userId, String role);
    
    /**
     * 检查用户是否有任意一个权限
     */
    boolean hasAnyPermission(String userId, String... permissions);
    
    /**
     * 检查用户是否有所有权限
     */
    boolean hasAllPermissions(String userId, String... permissions);
    
    /**
     * 获取用户的所有权限
     */
    Set<String> getUserPermissions(String userId);
    
    /**
     * 获取角色的所有权限
     */
    Set<String> getRolePermissions(String role);
    
    /**
     * 检查是否为管理员
     */
    boolean isAdmin(String userId);
    
    /**
     * 检查是否为普通用户
     */
    boolean isNormalUser(String userId);
    
    /**
     * 检查用户是否可以访问指定资源
     */
    boolean canAccessResource(String userId, String resourceType, String resourceId);
    
    /**
     * 检查用户是否可以执行指定操作
     */
    boolean canPerformAction(String userId, String action, String resourceType);
    
    /**
     * 获取用户可访问的资源列表
     */
    List<String> getAccessibleResources(String userId, String resourceType);
    
    /**
     * 权限常量
     */
    interface Permissions {
        // 用户管理权限
        String USER_VIEW = "user:view";
        String USER_CREATE = "user:create";
        String USER_UPDATE = "user:update";
        String USER_DELETE = "user:delete";
        String USER_MANAGE = "user:manage";
        
        // 病虫害管理权限
        String PEST_VIEW = "pest:view";
        String PEST_CREATE = "pest:create";
        String PEST_UPDATE = "pest:update";
        String PEST_DELETE = "pest:delete";
        String PEST_IDENTIFY = "pest:identify";
        
        // 防治方案权限
        String TREATMENT_VIEW = "treatment:view";
        String TREATMENT_CREATE = "treatment:create";
        String TREATMENT_UPDATE = "treatment:update";
        String TREATMENT_DELETE = "treatment:delete";
        String TREATMENT_APPROVE = "treatment:approve";
        String TREATMENT_EXECUTE = "treatment:execute";
        
        // 药剂管理权限
        String PESTICIDE_VIEW = "pesticide:view";
        String PESTICIDE_CREATE = "pesticide:create";
        String PESTICIDE_UPDATE = "pesticide:update";
        String PESTICIDE_DELETE = "pesticide:delete";
        String PESTICIDE_MANAGE_STOCK = "pesticide:manage_stock";
        
        // 效果评估权限
        String EVALUATION_VIEW = "evaluation:view";
        String EVALUATION_CREATE = "evaluation:create";
        String EVALUATION_UPDATE = "evaluation:update";
        String EVALUATION_DELETE = "evaluation:delete";
        
        // 预测预警权限
        String PREDICTION_VIEW = "prediction:view";
        String PREDICTION_CREATE = "prediction:create";
        String PREDICTION_UPDATE = "prediction:update";
        String PREDICTION_DELETE = "prediction:delete";
        String ALERT_MANAGE = "alert:manage";
        
        // 森林资源权限
        String FOREST_VIEW = "forest:view";
        String FOREST_CREATE = "forest:create";
        String FOREST_UPDATE = "forest:update";
        String FOREST_DELETE = "forest:delete";
        
        // 知识库权限
        String KNOWLEDGE_VIEW = "knowledge:view";
        String KNOWLEDGE_CREATE = "knowledge:create";
        String KNOWLEDGE_UPDATE = "knowledge:update";
        String KNOWLEDGE_DELETE = "knowledge:delete";
        String KNOWLEDGE_APPROVE = "knowledge:approve";
        
        // 系统管理权限
        String SYSTEM_CONFIG = "system:config";
        String SYSTEM_MONITOR = "system:monitor";
        String SYSTEM_BACKUP = "system:backup";
        String DATA_EXPORT = "data:export";
        String DATA_IMPORT = "data:import";
    }
    
    /**
     * 角色常量
     */
    interface Roles {
        String ADMIN = "ADMIN";
        String USER = "USER";
        String EXPERT = "EXPERT";
        String OPERATOR = "OPERATOR";
    }
}