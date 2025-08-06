package com.forestpest.exception;

/**
 * 系统基础异常类
 */
public class ForestPestSystemException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;

    public ForestPestSystemException() {
        super();
    }

    public ForestPestSystemException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public ForestPestSystemException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ForestPestSystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public ForestPestSystemException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}