package com.forestpest.controller;

import com.forestpest.entity.User;
import com.forestpest.service.UserService;
import com.forestpest.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController集成测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private PermissionService permissionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;    
 
   @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRealName("Test User");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        testUser.setDepartment("IT部门");
    }
    
    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.findById("1")).thenReturn(Optional.of(testUser));
        
        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
    
    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.findById("999")).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);
        
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("1"));
    }
    
    @Test
    void testGetUsersByRole() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findUsersByRole("USER")).thenReturn(users);
        
        // When & Then
        mockMvc.perform(get("/api/users?role=USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].role").value("USER"));
    } 
   
    @Test
    void testUpdateUserStatus() throws Exception {
        // Given
        when(userService.updateUserStatus("1", "INACTIVE")).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(put("/api/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testActivateUser() throws Exception {
        // Given
        when(userService.activateUser("1")).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(put("/api/users/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testDeactivateUser() throws Exception {
        // Given
        when(userService.deactivateUser("1")).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(put("/api/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testResetUserPassword() throws Exception {
        // Given
        when(userService.resetPassword("1")).thenReturn("tempPassword123");
        
        // When & Then
        mockMvc.perform(post("/api/users/1/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("tempPassword123"));
    }
    
    @Test
    void testDeleteUser() throws Exception {
        // Given
        when(userService.deleteUser("1")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("用户删除成功"));
    }
    
    @Test
    void testCheckUsername() throws Exception {
        // Given
        when(userService.existsByUsername("testuser")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/users/check-username?username=testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }    

    @Test
    void testCheckEmail() throws Exception {
        // Given
        when(userService.existsByEmail("test@example.com")).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/api/users/check-email?email=test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }
    
    @Test
    void testSearchUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.searchUsers("test")).thenReturn(users);
        
        // When & Then
        mockMvc.perform(get("/api/users/search?keyword=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("testuser"));
    }
}