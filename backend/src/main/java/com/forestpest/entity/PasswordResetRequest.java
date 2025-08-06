package com.forestpest.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 密码重置请求实体类
 */
public class PasswordResetRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "新密码长度必须在6-100个字符之间")
    private String newPassword;
    
    @NotBlank(message = "重置令牌不能为空")
    private String resetToken;
    
    private String confirmPassword;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(String email, String newPassword, String resetToken) {
        this.email = email;
        this.newPassword = newPassword;
        this.resetToken = resetToken;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "PasswordResetRequest{" +
                "email='" + email + '\'' +
                ", resetToken='" + resetToken + '\'' +
                '}';
    }
}