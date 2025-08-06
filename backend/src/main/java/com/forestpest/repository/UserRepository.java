package com.forestpest.repository;

import com.forestpest.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
public interface UserRepository extends BaseRepository<User, String> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据角色查找用户
     */
    List<User> findByRole(String role);
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(String status);
    
    /**
     * 根据部门查找用户
     */
    List<User> findByDepartment(String department);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据真实姓名模糊查询
     */
    List<User> findByRealNameContaining(String realName);
    
    /**
     * 根据多个条件查询用户
     */
    List<User> findByRoleAndStatus(String role, String status);
    
    /**
     * 获取活跃用户列表
     */
    List<User> findActiveUsers();
    
    /**
     * 根据创建时间范围查询用户
     */
    List<User> findByCreatedTimeBetween(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}