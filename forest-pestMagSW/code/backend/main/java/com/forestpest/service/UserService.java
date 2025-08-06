package com.forestpest.service;

import com.forestpest.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户管理服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User registerUser(User user);
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(String id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 查找所有用户
     */
    List<User> findAllUsers();
    
    /**
     * 根据角色查找用户
     */
    List<User> findUsersByRole(String role);
    
    /**
     * 根据状态查找用户
     */
    List<User> findUsersByStatus(String status);
    
    /**
     * 根据部门查找用户
     */
    List<User> findUsersByDepartment(String department);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 更新用户状态
     */
    User updateUserStatus(String userId, String status);
    
    /**
     * 更新用户密码
     */
    boolean updatePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * 重置用户密码
     */
    String resetPassword(String userId);
    
    /**
     * 删除用户
     */
    boolean deleteUser(String userId);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 验证用户名和邮箱唯一性
     */
    void validateUserUniqueness(String username, String email, String excludeUserId);
    
    /**
     * 激活用户
     */
    User activateUser(String userId);
    
    /**
     * 停用用户
     */
    User deactivateUser(String userId);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(String userId);
    
    /**
     * 获取活跃用户列表
     */
    List<User> getActiveUsers();
    
    /**
     * 根据关键词搜索用户
     */
    List<User> searchUsers(String keyword);
    
    /**
     * 分页查询用户
     */
    List<User> findUsersWithPagination(int page, int size);
    
    /**
     * 统计用户数量
     */
    long countUsers();
    
    /**
     * 统计各角色用户数量
     */
    java.util.Map<String, Long> countUsersByRole();
    
    /**
     * 统计各状态用户数量
     */
    java.util.Map<String, Long> countUsersByStatus();
    
    /**
     * 统计各部门用户数量
     */
    java.util.Map<String, Long> countUsersByDepartment();
    
    /**
     * 获取用户注册趋势
     */
    java.util.Map<String, Long> getUserRegistrationTrend();
    
    /**
     * 记录用户操作日志
     */
    void logUserOperation(String userId, String operation, String details);
    
    /**
     * 获取用户操作日志
     */
    List<UserOperationLog> getUserOperationLogs(String userId);
    
    /**
     * 验证用户数据
     */
    void validateUserData(User user);
    
    /**
     * 用户操作日志内部类
     */
    class UserOperationLog {
        private String id;
        private String userId;
        private String operation;
        private String details;
        private LocalDateTime operationTime;
        private String ipAddress;
        private String userAgent;
        
        // Constructors
        public UserOperationLog() {}
        
        public UserOperationLog(String userId, String operation, String details) {
            this.userId = userId;
            this.operation = operation;
            this.details = details;
            this.operationTime = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public void setOperation(String operation) {
            this.operation = operation;
        }
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
        
        public LocalDateTime getOperationTime() {
            return operationTime;
        }
        
        public void setOperationTime(LocalDateTime operationTime) {
            this.operationTime = operationTime;
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
    }
}