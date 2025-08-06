package com.forestpest.service;

import com.forestpest.entity.LoginRequest;
import com.forestpest.entity.LoginResponse;
import com.forestpest.entity.PasswordResetRequest;
import com.forestpest.entity.User;
import com.forestpest.service.impl.AuthServiceImpl;
import com.forestpest.util.JwtUtil;
import com.forestpest.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserService userService;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private User testUser;
    private LoginRequest loginRequest;    
   
 @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setRealName("Test User");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }
    
    @Test
    void testLogin_Success() {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateAccessToken("1", "testuser", "USER")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("1", "testuser")).thenReturn("refreshToken");
        when(jwtUtil.getTokenRemainingTime("accessToken")).thenReturn(3600000L);
        doNothing().when(userService).updateLastLoginTime("1");
        
        // When
        LoginResponse response = authService.login(loginRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(testUser, response.getUser());
        verify(userService).updateLastLoginTime("1");
    }
    
    @Test
    void testLogin_UserNotFound() {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        assertEquals("用户名或密码错误", exception.getMessage());
    }    

    @Test
    void testLogin_UserInactive() {
        // Given
        testUser.setStatus("INACTIVE");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        assertEquals("用户账户已被禁用", exception.getMessage());
    }
    
    @Test
    void testLogin_WrongPassword() {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        assertEquals("用户名或密码错误", exception.getMessage());
    }
    
    @Test
    void testValidateAccessToken_Valid() {
        // Given
        when(jwtUtil.validateAccessToken("validToken")).thenReturn(true);
        
        // When
        boolean result = authService.validateAccessToken("validToken");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testValidateAccessToken_Invalid() {
        // Given
        when(jwtUtil.validateAccessToken("invalidToken")).thenReturn(false);
        
        // When
        boolean result = authService.validateAccessToken("invalidToken");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testGetUserFromToken_Success() {
        // Given
        when(jwtUtil.validateAccessToken("validToken")).thenReturn(true);
        when(jwtUtil.getUserIdFromToken("validToken")).thenReturn("1");
        when(userService.findById("1")).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = authService.getUserFromToken("validToken");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }    
 
   @Test
    void testGetUserFromToken_InvalidToken() {
        // Given
        when(jwtUtil.validateAccessToken("invalidToken")).thenReturn(false);
        
        // When
        Optional<User> result = authService.getUserFromToken("invalidToken");
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testInitiatePasswordReset_Success() {
        // Given
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When
        String resetToken = authService.initiatePasswordReset("test@example.com");
        
        // Then
        assertNotNull(resetToken);
        assertFalse(resetToken.isEmpty());
    }
    
    @Test
    void testInitiatePasswordReset_EmailNotFound() {
        // Given
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.initiatePasswordReset("nonexistent@example.com"));
        assertEquals("邮箱不存在", exception.getMessage());
    }
    
    @Test
    void testResetPassword_Success() {
        // Given
        String resetToken = authService.initiatePasswordReset("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userService.findById("1")).thenReturn(Optional.of(testUser));
        when(userService.updateUser(any(User.class))).thenReturn(testUser);
        
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken(resetToken);
        request.setNewPassword("newPassword123");
        
        // When
        boolean result = authService.resetPassword(request);
        
        // Then
        assertTrue(result);
        verify(userService).updateUser(any(User.class));
    }
    
    @Test
    void testLogout() {
        // Given
        String token = "validToken";
        
        // When
        authService.logout(token);
        
        // Then
        assertTrue(authService.isTokenBlacklisted(token));
    }
}
