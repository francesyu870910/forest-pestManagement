package com.forestpest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础服务类
 */
public abstract class BaseService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 验证参数是否为空
     */
    protected void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 验证字符串是否为空
     */
    protected void validateNotEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}