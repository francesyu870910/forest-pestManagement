package com.forestpest.exception;

/**
 * 用户已存在异常
 */
public class UserAlreadyExistsException extends BusinessException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super(String.format("用户%s已存在: %s", field, value));
    }
}