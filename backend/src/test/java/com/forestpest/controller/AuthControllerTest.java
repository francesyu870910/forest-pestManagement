package com.forestpest.controller;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.LoginResponse;
import com.forestpest.entity.User;
import com.forestpest.service.AuthService;
import com.forestpest.service.UserService;
import com.forestpest.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController集成测试
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;   
 
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRealName("Test User");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        
        loginResponse = new LoginResponse();
        loginResponse.setAccessToken("accessToken");
        loginResponse.setRefreshToken("refreshToken");
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(3600000L);
        loginResponse.setUser(testUser);
    }
    
    @Test
    void testLogin_Success() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }
    
    @Test
    void testLogout() throws Exception {
        // Given
        when(jwtUtil.extractTokenFromHeader("Bearer validToken")).thenReturn("validToken");
        doNothing().when(authService).logout("validToken");
        
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("登出成功"));
    }
    
    @Test
    void testValidateToken_Valid() throws Exception {
        // Given
        when(jwtUtil.extractTokenFromHeader("Bearer validToken")).thenReturn("validToken");
        when(authService.validateAccessToken("validToken")).thenReturn(true);
        when(authService.getUserIdFromToken("validToken")).thenReturn("1");
        when(authService.getUsernameFromToken("validToken")).thenReturn("testuser");
        when(authService.getRoleFromToken("validToken")).thenReturn("USER");
        when(jwtUtil.getTokenRemainingTime("validToken")).thenReturn(3600000L);
        
        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.userId").value("1"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }    
 
   @Test
    void testGetCurrentUser() throws Exception {
        // Given
        when(jwtUtil.extractTokenFromHeader("Bearer validToken")).thenReturn("validToken");
        when(authService.getUserFromToken("validToken")).thenReturn(Optional.of(testUser));
        
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
    
    @Test
    void testForgotPassword() throws Exception {
        // Given
        when(authService.initiatePasswordReset("test@example.com")).thenReturn("resetToken123");
        
        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(containsString("resetToken123")));
    }
    
    @Test
    void testValidateResetToken() throws Exception {
        // Given
        when(authService.validatePasswordResetToken("validResetToken")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/auth/validate-reset-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"validResetToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    void testChangePassword() throws Exception {
        // Given
        when(jwtUtil.extractTokenFromHeader("Bearer validToken")).thenReturn("validToken");
        when(authService.getUserIdFromToken("validToken")).thenReturn("1");
        when(userService.updatePassword("1", "oldPassword", "newPassword")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"oldPassword\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("密码修改成功"));
    }
}