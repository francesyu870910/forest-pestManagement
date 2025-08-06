package com.forestpest.dto;

import java.util.Map;

/**
 * 用户统计信息DTO
 */
public class UserStatisticsDTO {
    
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long adminUsers;
    private long normalUsers;
    
    private Map<String, Long> usersByRole;
    private Map<String, Long> usersByStatus;
    private Map<String, Long> usersByDepartment;
    private Map<String, Long> registrationTrend;
    
    // Constructors
    public UserStatisticsDTO() {}
    
    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public long getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public long getInactiveUsers() {
        return inactiveUsers;
    }
    
    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }
    
    public long getAdminUsers() {
        return adminUsers;
    }
    
    public void setAdminUsers(long adminUsers) {
        this.adminUsers = adminUsers;
    }
    
    public long getNormalUsers() {
        return normalUsers;
    }
    
    public void setNormalUsers(long normalUsers) {
        this.normalUsers = normalUsers;
    }
    
    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }
    
    public void setUsersByRole(Map<String, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }
    
    public Map<String, Long> getUsersByStatus() {
        return usersByStatus;
    }
    
    public void setUsersByStatus(Map<String, Long> usersByStatus) {
        this.usersByStatus = usersByStatus;
    }
    
    public Map<String, Long> getUsersByDepartment() {
        return usersByDepartment;
    }
    
    public void setUsersByDepartment(Map<String, Long> usersByDepartment) {
        this.usersByDepartment = usersByDepartment;
    }
    
    public Map<String, Long> getRegistrationTrend() {
        return registrationTrend;
    }
    
    public void setRegistrationTrend(Map<String, Long> registrationTrend) {
        this.registrationTrend = registrationTrend;
    }
}