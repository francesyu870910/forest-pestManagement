package com.forestpest.service.impl;

import com.forestpest.entity.User;
import com.forestpest.repository.UserRepository;
import com.forestpest.service.UserService;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 用户操作日志存储
    private final Map<String, List<UserOperationLog>> operationLogs = new ConcurrentHashMap<>();
    
    @Override
    public User registerUser(User user) {
        // 验证用户数据
        validateUserData(user);
        
        // 验证用户名和邮箱唯一性
        validateUserUniqueness(user.getUsername(), user.getEmail(), null);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (!StringUtils.hasText(user.getStatus())) {
            user.setStatus("ACTIVE");
        }
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 记录操作日志
        logUserOperation(savedUser.getId(), "USER_REGISTER", "用户注册成功");
        
        return savedUser;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public List<User> findUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }
    
    @Override
    public List<User> findUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }
    
    @Override
    public User updateUser(User user) {
        // 验证用户存在
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (!existingUserOpt.isPresent()) {
            throw new BusinessException("用户不存在");
        }
        
        User existingUser = existingUserOpt.get();
        
        // 验证用户名和邮箱唯一性（排除当前用户）
        if (!existingUser.getUsername().equals(user.getUsername()) || 
            !existingUser.getEmail().equals(user.getEmail())) {
            validateUserUniqueness(user.getUsername(), user.getEmail(), user.getId());
        }
        
        // 验证用户数据
        validateUserData(user);
        
        // 保留原密码（如果没有提供新密码）
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        
        // 记录操作日志
        logUserOperation(user.getId(), "USER_UPDATE", "用户信息更新");
        
        return updatedUser;
    }
    
    @Override
    public User updateUserStatus(String userId, String status) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("用户不存在");
        }
        
        User user = userOpt.get();
        String oldStatus = user.getStatus();
        user.setStatus(status);
        
        User updatedUser = userRepository.save(user);
        
        // 记录操作日志
        logUserOperation(userId, "STATUS_UPDATE", 
            String.format("状态从 %s 更新为 %s", oldStatus, status));
        
        return updatedUser;
    }
    
    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 记录操作日志
        logUserOperation(userId, "PASSWORD_UPDATE", "密码更新成功");
        
        return true;
    }
    
    @Override
    public String resetPassword(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 生成临时密码
        String tempPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        
        userRepository.save(user);
        
        // 记录操作日志
        logUserOperation(userId, "PASSWORD_RESET", "密码重置");
        
        return tempPassword;
    }
    
    @Override
    public boolean deleteUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        // 记录操作日志
        logUserOperation(userId, "USER_DELETE", "用户删除");
        
        userRepository.deleteById(userId);
        
        // 清理操作日志
        operationLogs.remove(userId);
        
        return true;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void validateUserUniqueness(String username, String email, String excludeUserId) {
        // 检查用户名唯一性
        Optional<User> existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername.isPresent() && 
            !existingUserByUsername.get().getId().equals(excludeUserId)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱唯一性
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent() && 
            !existingUserByEmail.get().getId().equals(excludeUserId)) {
            throw new BusinessException("邮箱已存在");
        }
    }
    
    @Override
    public User activateUser(String userId) {
        return updateUserStatus(userId, "ACTIVE");
    }
    
    @Override
    public User deactivateUser(String userId) {
        return updateUserStatus(userId, "INACTIVE");
    }
    
    @Override
    public void updateLastLoginTime(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
            
            // 记录操作日志
            logUserOperation(userId, "LOGIN", "用户登录");
        }
    }
    
    @Override
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    @Override
    public List<User> searchUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAllUsers();
        }
        
        List<User> allUsers = findAllUsers();
        String lowerKeyword = keyword.toLowerCase();
        
        return allUsers.stream()
                .filter(user -> 
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerKeyword)) ||
                    (user.getRealName() != null && user.getRealName().toLowerCase().contains(lowerKeyword)) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) ||
                    (user.getDepartment() != null && user.getDepartment().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> findUsersWithPagination(int page, int size) {
        List<User> allUsers = findAllUsers();
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());
        
        if (start >= allUsers.size()) {
            return new ArrayList<>();
        }
        
        return allUsers.subList(start, end);
    }
    
    @Override
    public long countUsers() {
        return userRepository.count();
    }
    
    @Override
    public Map<String, Long> countUsersByRole() {
        return findAllUsers().stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countUsersByStatus() {
        return findAllUsers().stream()
                .collect(Collectors.groupingBy(User::getStatus, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> countUsersByDepartment() {
        return findAllUsers().stream()
                .filter(user -> user.getDepartment() != null)
                .collect(Collectors.groupingBy(User::getDepartment, Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> getUserRegistrationTrend() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return findAllUsers().stream()
                .filter(user -> user.getCreatedTime() != null)
                .collect(Collectors.groupingBy(
                    user -> user.getCreatedTime().format(formatter),
                    Collectors.counting()
                ));
    }
    
    @Override
    public void logUserOperation(String userId, String operation, String details) {
        UserOperationLog log = new UserOperationLog(userId, operation, details);
        log.setId(UUID.randomUUID().toString());
        
        operationLogs.computeIfAbsent(userId, k -> new ArrayList<>()).add(log);
        
        // 限制日志数量，只保留最近100条
        List<UserOperationLog> userLogs = operationLogs.get(userId);
        if (userLogs.size() > 100) {
            userLogs.sort(Comparator.comparing(UserOperationLog::getOperationTime).reversed());
            operationLogs.put(userId, userLogs.subList(0, 100));
        }
    }
    
    @Override
    public List<UserOperationLog> getUserOperationLogs(String userId) {
        List<UserOperationLog> logs = operationLogs.get(userId);
        if (logs == null) {
            return new ArrayList<>();
        }
        
        // 按时间倒序返回
        return logs.stream()
                .sorted(Comparator.comparing(UserOperationLog::getOperationTime).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public void validateUserData(User user) {
        if (user == null) {
            throw new BusinessException("用户数据不能为空");
        }
        
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new BusinessException("用户名长度必须在3-50个字符之间");
        }
        
        if (!user.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new BusinessException("用户名只能包含字母、数字和下划线");
        }
        
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BusinessException("邮箱不能为空");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new BusinessException("邮箱格式不正确");
        }
        
        if (!StringUtils.hasText(user.getRealName())) {
            throw new BusinessException("真实姓名不能为空");
        }
        
        if (user.getRealName().length() > 50) {
            throw new BusinessException("真实姓名长度不能超过50个字符");
        }
        
        if (!StringUtils.hasText(user.getRole())) {
            throw new BusinessException("角色不能为空");
        }
        
        if (!"ADMIN".equals(user.getRole()) && !"USER".equals(user.getRole())) {
            throw new BusinessException("角色只能是ADMIN或USER");
        }
        
        if (StringUtils.hasText(user.getStatus()) && 
            !"ACTIVE".equals(user.getStatus()) && !"INACTIVE".equals(user.getStatus())) {
            throw new BusinessException("状态只能是ACTIVE或INACTIVE");
        }
    }
    
    /**
     * 生成临时密码
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}