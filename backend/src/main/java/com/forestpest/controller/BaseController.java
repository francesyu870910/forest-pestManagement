package com.forestpest.controller;

import com.forestpest.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础控制器类
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 返回成功响应
     */
    protected <T> ApiResponse<T> success() {
        return ApiResponse.success();
    }

    /**
     * 返回成功响应（带数据）
     */
    protected <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    /**
     * 返回成功响应（带消息和数据）
     */
    protected <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    /**
     * 返回错误响应
     */
    protected <T> ApiResponse<T> error(String message) {
        return ApiResponse.error(message);
    }

    /**
     * 返回参数错误响应
     */
    protected <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.badRequest(message);
    }
}