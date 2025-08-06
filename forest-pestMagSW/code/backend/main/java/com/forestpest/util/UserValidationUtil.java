package com.forestpest.util;

import com.forestpest.entity.User;
import com.forestpest.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户验证工具类
 */
public class UserValidationUtil {
    
    // 用户名正则表达式：3-50个字符，只能包含字母、数字和下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // 手机号正则表达式（中国手机号）
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 密码强度正则表达式：至少6位，包含字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$");
    
    /**
     * 验证用户注册数据
     */
    public static void validateUserForRegistration(User user) {
        List<String> errors = new ArrayList<>();
        
        // 验证基本字段
        validateBasicFields(user, errors);
        
        // 验证密码
        if (!StringUtils.hasText(user.getPassword())) {
            errors.add("密码不能为空");
        } else if (user.getPassword().length() < 6) {
            errors.add("密码长度不能少于6位");
        }
        
        if (!errors.isEmpty()) {
            throw new BusinessException("用户数据验证失败: " + String.join(", ", errors));
        }
    }
    
    /**
     * 验证用户更新数据
     */
    public static void validateUserForUpdate(User user) {
        List<String> errors = new ArrayList<>();
        
        // 验证基本字段
        validateBasicFields(user, errors);
        
        if (!errors.isEmpty()) {
            throw new BusinessException("用户数据验证失败: " + String.join(", ", errors));
        }
    }
    
    /**
     * 验证基本字段
     */
    private static void validateBasicFields(User user, List<String> errors) {
        if (user == null) {
            errors.add("用户数据不能为空");
            return;
        }
        
        // 验证用户名
        if (!StringUtils.hasText(user.getUsername())) {
            errors.add("用户名不能为空");
        } else if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            errors.add("用户名格式不正确，只能包含3-50个字母、数字和下划线");
        }
        
        // 验证邮箱
        if (!StringUtils.hasText(user.getEmail())) {
            errors.add("邮箱不能为空");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("邮箱格式不正确");
        }
        
        // 验证真实姓名
        if (!StringUtils.hasText(user.getRealName())) {
            errors.add("真实姓名不能为空");
        } else if (user.getRealName().length() > 50) {
            errors.add("真实姓名长度不能超过50个字符");
        }
        
        // 验证角色
        if (!StringUtils.hasText(user.getRole())) {
            errors.add("角色不能为空");
        } else if (!"ADMIN".equals(user.getRole()) && !"USER".equals(user.getRole())) {
            errors.add("角色只能是ADMIN或USER");
        }
        
        // 验证状态
        if (StringUtils.hasText(user.getStatus()) && 
            !"ACTIVE".equals(user.getStatus()) && !"INACTIVE".equals(user.getStatus())) {
            errors.add("状态只能是ACTIVE或INACTIVE");
        }
        
        // 验证手机号（可选）
        if (StringUtils.hasText(user.getPhone()) && 
            !PHONE_PATTERN.matcher(user.getPhone()).matches()) {
            errors.add("手机号格式不正确");
        }
    }
    
    /**
     * 验证密码强度
     */
    public static boolean isStrongPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 获取密码强度描述
     */
    public static String getPasswordStrengthDescription(String password) {
        if (!StringUtils.hasText(password)) {
            return "密码不能为空";
        }
        
        if (password.length() < 6) {
            return "密码长度至少6位";
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&].*");
        
        if (!hasLetter) {
            return "密码必须包含字母";
        }
        
        if (!hasDigit) {
            return "密码必须包含数字";
        }
        
        if (hasLetter && hasDigit && hasSpecial && password.length() >= 8) {
            return "强密码";
        } else if (hasLetter && hasDigit && password.length() >= 6) {
            return "中等强度密码";
        } else {
            return "弱密码";
        }
    }
    
    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        return StringUtils.hasText(username) && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        return !StringUtils.hasText(phone) || PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证角色
     */
    public static boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "USER".equals(role);
    }
    
    /**
     * 验证状态
     */
    public static boolean isValidStatus(String status) {
        return "ACTIVE".equals(status) || "INACTIVE".equals(status);
    }
    
    /**
     * 生成用户名建议
     */
    public static List<String> generateUsernameSuggestions(String baseName) {
        List<String> suggestions = new ArrayList<>();
        
        if (!StringUtils.hasText(baseName)) {
            return suggestions;
        }
        
        // 清理基础名称
        String cleanBase = baseName.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        
        if (cleanBase.length() < 3) {
            cleanBase = cleanBase + "user";
        }
        
        if (cleanBase.length() > 46) {
            cleanBase = cleanBase.substring(0, 46);
        }
        
        // 生成建议
        suggestions.add(cleanBase);
        suggestions.add(cleanBase + "123");
        suggestions.add(cleanBase + "_user");
        suggestions.add(cleanBase + System.currentTimeMillis() % 10000);
        
        return suggestions.stream()
                .filter(UserValidationUtil::isValidUsername)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查密码是否包含用户信息
     */
    public static boolean passwordContainsUserInfo(String password, User user) {
        if (!StringUtils.hasText(password) || user == null) {
            return false;
        }
        
        String lowerPassword = password.toLowerCase();
        
        // 检查是否包含用户名
        if (StringUtils.hasText(user.getUsername()) && 
            lowerPassword.contains(user.getUsername().toLowerCase())) {
            return true;
        }
        
        // 检查是否包含真实姓名
        if (StringUtils.hasText(user.getRealName()) && 
            lowerPassword.contains(user.getRealName().toLowerCase())) {
            return true;
        }
        
        // 检查是否包含邮箱前缀
        if (StringUtils.hasText(user.getEmail()) && user.getEmail().contains("@")) {
            String emailPrefix = user.getEmail().split("@")[0].toLowerCase();
            if (lowerPassword.contains(emailPrefix)) {
                return true;
            }
        }
        
        return false;
    }
}