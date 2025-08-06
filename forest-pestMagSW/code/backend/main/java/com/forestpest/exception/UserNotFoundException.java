package com.forestpest.exception;

/**
 * 用户未找到异常
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static UserNotFoundException byId(String userId) {
        return new UserNotFoundException("用户不存在，ID: " + userId);
    }
}