package com.forestpest.service;

import com.forestpest.entity.User;
import com.forestpest.repository.UserRepository;
import com.forestpest.service.impl.UserServiceImpl;
import com.forestpest.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setRealName("Test User");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        testUser.setDepartment("IT部门");
        testUser.setCreatedTime(LocalDateTime.now());
    }
    
    @Test
    void testRegisterUser_Success() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.registerUser(testUser);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(testUser.getPassword());
    }
    
    @Test
    void testRegisterUser_UsernameExists() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.registerUser(testUser));
        assertEquals("用户名已存在", exception.getMessage());
    }
    
    @Test
    void testRegisterUser_EmailExists() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.registerUser(testUser));
        assertEquals("邮箱已存在", exception.getMessage());
    }
    
    @Test
    void testRegisterUser_InvalidData() {
        // Given
        testUser.setUsername(""); // 无效用户名
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.registerUser(testUser));
        assertTrue(exception.getMessage().contains("用户名不能为空"));
    }
    
    @Test
    void testFindById_Success() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userService.findById("1");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }
    
    @Test
    void testFindById_NotFound() {
        // Given
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        
        // When
        Optional<User> result = userService.findById("999");
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testFindByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userService.findByUsername("testuser");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }
    
    @Test
    void testFindAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<User> result = userService.findAllUsers();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }
    
    @Test
    void testFindUsersByRole() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRole("USER")).thenReturn(users);
        
        // When
        List<User> result = userService.findUsersByRole("USER");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getRole());
    }
    
    @Test
    void testUpdateUser_Success() {
        // Given
        User existingUser = new User();
        existingUser.setId("1");
        existingUser.setUsername("oldusername");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");
        
        when(userRepository.findById("1")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.updateUser(testUser);
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_NotFound() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.updateUser(testUser));
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void testUpdateUserStatus_Success() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.updateUserStatus("1", "INACTIVE");
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testUpdatePassword_Success() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldpassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        boolean result = userService.updatePassword("1", "oldpassword", "newpassword");
        
        // Then
        assertTrue(result);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testUpdatePassword_WrongOldPassword() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.updatePassword("1", "wrongpassword", "newpassword"));
        assertEquals("原密码不正确", exception.getMessage());
    }
    
    @Test
    void testResetPassword_Success() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedTempPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        String tempPassword = userService.resetPassword("1");
        
        // Then
        assertNotNull(tempPassword);
        assertEquals(8, tempPassword.length());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById("1");
        
        // When
        boolean result = userService.deleteUser("1");
        
        // Then
        assertTrue(result);
        verify(userRepository).deleteById("1");
    }
    
    @Test
    void testDeleteUser_NotFound() {
        // Given
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        
        // When
        boolean result = userService.deleteUser("999");
        
        // Then
        assertFalse(result);
        verify(userRepository, never()).deleteById(anyString());
    }
    
    @Test
    void testExistsByUsername() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When
        boolean result = userService.existsByUsername("testuser");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testExistsByEmail() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When
        boolean result = userService.existsByEmail("test@example.com");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testActivateUser() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.activateUser("1");
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testDeactivateUser() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.deactivateUser("1");
        
        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testUpdateLastLoginTime() {
        // Given
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        userService.updateLastLoginTime("1");
        
        // Then
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testGetActiveUsers() {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findActiveUsers()).thenReturn(activeUsers);
        
        // When
        List<User> result = userService.getActiveUsers();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
    }
    
    @Test
    void testSearchUsers() {
        // Given
        List<User> allUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        // When
        List<User> result = userService.searchUsers("test");
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getUsername().contains("test") || 
                  result.get(0).getRealName().contains("test"));
    }
    
    @Test
    void testCountUsers() {
        // Given
        when(userRepository.count()).thenReturn(5L);
        
        // When
        long result = userService.countUsers();
        
        // Then
        assertEquals(5L, result);
    }
    
    @Test
    void testValidateUserData_ValidUser() {
        // When & Then
        assertDoesNotThrow(() -> userService.validateUserData(testUser));
    }
    
    @Test
    void testValidateUserData_InvalidUser() {
        // Given
        testUser.setUsername(""); // 无效用户名
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.validateUserData(testUser));
        assertTrue(exception.getMessage().contains("用户名不能为空"));
    }
    
    @Test
    void testValidateUserData_NullUser() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.validateUserData(null));
        assertEquals("用户数据不能为空", exception.getMessage());
    }
}