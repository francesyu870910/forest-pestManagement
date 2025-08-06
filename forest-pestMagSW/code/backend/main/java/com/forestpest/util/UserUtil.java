package com.forestpest.util;

import com.forestpest.entity.User;
import com.forestpest.dto.UserDTO;
import com.forestpest.dto.UserStatisticsDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户工具类
 */
public class UserUtil {
    
    /**
     * 将User实体转换为UserDTO
     */
    public static UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    
    /**
     * 将UserDTO转换为User实体
     */
    public static User convertToEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }
    
    /**
     * 批量转换User实体列表为UserDTO列表
     */
    public static List<UserDTO> convertToDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(UserUtil::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 批量转换UserDTO列表为User实体列表
     */
    public static List<User> convertToEntityList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(UserUtil::convertToEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建用户统计信息DTO
     */
    public static UserStatisticsDTO createStatisticsDTO(
            long totalUsers,
            Map<String, Long> usersByRole,
            Map<String, Long> usersByStatus,
            Map<String, Long> usersByDepartment,
            Map<String, Long> registrationTrend) {
        
        UserStatisticsDTO dto = new UserStatisticsDTO();
        dto.setTotalUsers(totalUsers);
        dto.setUsersByRole(usersByRole);
        dto.setUsersByStatus(usersByStatus);
        dto.setUsersByDepartment(usersByDepartment);
        dto.setRegistrationTrend(registrationTrend);
        
        // 计算各类用户数量
        dto.setActiveUsers(usersByStatus.getOrDefault("ACTIVE", 0L));
        dto.setInactiveUsers(usersByStatus.getOrDefault("INACTIVE", 0L));
        dto.setAdminUsers(usersByRole.getOrDefault("ADMIN", 0L));
        dto.setNormalUsers(usersByRole.getOrDefault("USER", 0L));
        
        return dto;
    }
    
    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        // 用户名长度3-50个字符，只能包含字母、数字和下划线
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // 手机号可以为空
        }
        
        // 中国手机号格式验证
        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }
    
    /**
     * 生成用户显示名称
     */
    public static String generateDisplayName(User user) {
        if (user == null) {
            return "未知用户";
        }
        
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            return user.getRealName();
        }
        
        return user.getUsername();
    }
    
    /**
     * 检查用户是否为管理员
     */
    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    /**
     * 检查用户是否为活跃状态
     */
    public static boolean isActive(User user) {
        return user != null && "ACTIVE".equals(user.getStatus());
    }
    
    /**
     * 脱敏用户信息（隐藏敏感信息）
     */
    public static UserDTO maskSensitiveInfo(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = convertToDTO(user);
        
        // 脱敏邮箱
        if (dto.getEmail() != null) {
            dto.setEmail(maskEmail(dto.getEmail()));
        }
        
        // 脱敏手机号
        if (dto.getPhone() != null) {
            dto.setPhone(maskPhone(dto.getPhone()));
        }
        
        // 清除密码
        dto.setPassword(null);
        
        return dto;
    }
    
    /**
     * 脱敏邮箱地址
     */
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return "*".repeat(username.length()) + "@" + domain;
        }
        
        return username.charAt(0) + "*".repeat(username.length() - 2) + 
               username.charAt(username.length() - 1) + "@" + domain;
    }
    
    /**
     * 脱敏手机号
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 生成用户头像URL
     */
    public static String generateAvatarUrl(User user) {
        if (user == null) {
            return "/images/avatars/default.png";
        }
        
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            return user.getAvatar();
        }
        
        // 根据用户ID生成默认头像
        int avatarIndex = Math.abs(user.getId().hashCode()) % 10 + 1;
        return "/images/avatars/default_" + avatarIndex + ".png";
    }
}