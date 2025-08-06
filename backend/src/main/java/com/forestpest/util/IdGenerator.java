package com.forestpest.util;

import java.util.UUID;

/**
 * ID生成器工具类
 */
public class IdGenerator {

    /**
     * 生成UUID
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的ID
     */
    public static String generateId(String prefix) {
        return prefix + generateId();
    }
}