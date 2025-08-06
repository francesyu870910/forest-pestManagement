package com.forestpest.controller;

import com.forestpest.entity.User;
import com.forestpest.dto.UserDTO;
import com.forestpest.dto.UserStatisticsDTO;
import com.forestpest.service.UserService;
import com.forestpest.service.PermissionService;
import com.forestpest.util.UserUtil;
import com.forestpest.common.Result;
import com.forestpest.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户管理控制
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = UserUtil.convertToEntity(userDTO);
            User registeredUser = userService.registerUser(user);
            UserDTO responseDTO = UserUtil.convertToDTO(registeredUser);
            return Result.success(responseDTO);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("用户注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable String id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                UserDTO userDTO = UserUtil.convertToDTO(userOpt.get());
                return Result.success(userDTO);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询所有用户
     */
    @GetMapping
    public Result<List<UserDTO>> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword) {
        try {
            List<User> users;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = userService.searchUsers(keyword);
            } else if (role != null) {
                users = userService.findUsersByRole(role);
            } else if (status != null) {
                users = userService.findUsersByStatus(status);
            } else if (department != null) {
                users = userService.findUsersByDepartment(department);
            } else {
                users = userService.findAllUsers();
            }
            
            List<UserDTO> userDTOs = UserUtil.convertToDTOList(users);
            return Result.success(userDTOs);
        } catch (Exception e) {
            return Result.error("查询用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<List<UserDTO>> getUsersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<User> users = userService.findUsersWithPagination(page, size);
            List<UserDTO> userDTOs = UserUtil.convertToDTOList(users);
            return Result.success(userDTOs);
        } catch (Exception e) {
            return Result.error("分页查询用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        try {
            userDTO.setId(id);
            User user = UserUtil.convertToEntity(userDTO);
            User updatedUser = userService.updateUser(user);
            UserDTO responseDTO = UserUtil.convertToDTO(updatedUser);
            return Result.success(responseDTO);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<UserDTO> updateUserStatus(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return Result.error("状态不能为空");
            }
            
            User updatedUser = userService.updateUserStatus(id, status);
            UserDTO responseDTO = UserUtil.convertToDTO(updatedUser);
            return Result.success(responseDTO);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 激活用户
     */
    @PutMapping("/{id}/activate")
    public Result<UserDTO> activateUser(@PathVariable String id) {
        try {
            User activatedUser = userService.activateUser(id);
            UserDTO responseDTO = UserUtil.convertToDTO(activatedUser);
            return Result.success(responseDTO);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("激活用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 停用用户
     */
    @PutMapping("/{id}/deactivate")
    public Result<UserDTO> deactivateUser(@PathVariable String id) {
        try {
            User deactivatedUser = userService.deactivateUser(id);
            UserDTO responseDTO = UserUtil.convertToDTO(deactivatedUser);
            return Result.success(responseDTO);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("停用用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetUserPassword(@PathVariable String id) {
        try {
            String tempPassword = userService.resetPassword(id);
            return Result.success(tempPassword);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户密码
     */
    @PutMapping("/{id}/password")
    public Result<String> updateUserPassword(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.error("旧密码和新密码不能为空");
            }
            
            boolean success = userService.updatePassword(id, oldPassword, newPassword);
            if (success) {
                return Result.success("密码更新成功");
            } else {
                return Result.error("密码更新失败");
            }
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable String id) {
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                return Result.success("用户删除成功");
            } else {
                return Result.error("用户删除失败");
            }
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查用户名失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查邮箱失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活跃用户列表
     */
    @GetMapping("/active")
    public Result<List<UserDTO>> getActiveUsers() {
        try {
            List<User> activeUsers = userService.getActiveUsers();
            List<UserDTO> userDTOs = UserUtil.convertToDTOList(activeUsers);
            return Result.success(userDTOs);
        } catch (Exception e) {
            return Result.error("获取活跃用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        try {
            List<User> users = userService.searchUsers(keyword);
            List<UserDTO> userDTOs = UserUtil.convertToDTOList(users);
            return Result.success(userDTOs);
        } catch (Exception e) {
            return Result.error("搜索用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public Result<UserStatisticsDTO> getUserStatistics() {
        try {
            long totalUsers = userService.countUsers();
            Map<String, Long> usersByRole = userService.countUsersByRole();
            Map<String, Long> usersByStatus = userService.countUsersByStatus();
            Map<String, Long> usersByDepartment = userService.countUsersByDepartment();
            Map<String, Long> registrationTrend = userService.getUserRegistrationTrend();
            
            UserStatisticsDTO statistics = UserUtil.createStatisticsDTO(
                totalUsers, usersByRole, usersByStatus, usersByDepartment, registrationTrend);
            
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取用户统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户操作日志
     */
    @GetMapping("/{id}/logs")
    public Result<List<UserService.UserOperationLog>> getUserOperationLogs(@PathVariable String id) {
        try {
            List<UserService.UserOperationLog> logs = userService.getUserOperationLogs(id);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("获取用户操作日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户权限信息
     */
    @GetMapping("/{id}/permissions")
    public Result<Map<String, Object>> getUserPermissions(@PathVariable String id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            User user = userOpt.get();
            Map<String, Object> permissionInfo = new java.util.HashMap<>();
            permissionInfo.put("role", user.getRole());
            permissionInfo.put("permissions", permissionService.getUserPermissions(id));
            permissionInfo.put("isAdmin", permissionService.isAdmin(id));
            
            return Result.success(permissionInfo);
        } catch (Exception e) {
            return Result.error("获取用户权限失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量操作用户
     */
    @PostMapping("/batch")
    public Result<String> batchOperateUsers(@RequestBody Map<String, Object> request) {
        try {
            String operation = (String) request.get("operation");
            @SuppressWarnings("unchecked")
            List<String> userIds = (List<String>) request.get("userIds");
            
            if (operation == null || userIds == null || userIds.isEmpty()) {
                return Result.error("操作类型和用户ID列表不能为空");
            }
            
            int successCount = 0;
            int failCount = 0;
            
            for (String userId : userIds) {
                try {
                    switch (operation) {
                        case "activate":
                            userService.activateUser(userId);
                            successCount++;
                            break;
                        case "deactivate":
                            userService.deactivateUser(userId);
                            successCount++;
                            break;
                        case "delete":
                            userService.deleteUser(userId);
                            successCount++;
                            break;
                        default:
                            failCount++;
                            break;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            return Result.success(String.format("批量操作完成，成功: %d, 失败: %d", successCount, failCount));
        } catch (Exception e) {
            return Result.error("批量操作失败: " + e.getMessage());
        }
    }
}
