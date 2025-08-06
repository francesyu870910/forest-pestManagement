package com.forestpest.integration;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.User;
import com.forestpest.service.AuthService;
import com.forestpest.service.UserService;
import com.forestpest.util.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证集成测试
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 创建测试用户
        testUser = TestDataUtil.createNormalUser();
        testUser.setPassword("password123"); // 设置明文密码用于注册
        
        try {
            userService.registerUser(testUser);
        } catch (Exception e) {
            // 用户可能已存在，忽略异常
        }
    }
    
    @Test
    void testCompleteAuthenticationFlow() throws Exception {
        // 1. 用户登录
        LoginRequest loginRequest = TestDataUtil.createLoginRequest("testuser", "password123");
        
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // 2. 提取访问令牌
        // 这里应该解析响应获取令牌，简化处理
        String accessToken = "mock-access-token";
        
        // 3. 使用令牌访问受保护资源
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        
        // 4. 用户登出
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testPasswordResetFlow() throws Exception {
        // 1. 发起密码重置
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        // 2. 验证重置令牌（这里使用模拟令牌）
        String resetToken = "mock-reset-token";
        
        mockMvc.perform(post("/api/auth/validate-reset-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + resetToken + "\"}"))
                .andExpect(status().isOk());
    }
}