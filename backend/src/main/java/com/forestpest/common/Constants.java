package com.forestpest.common;

/**
 * 系统常量类
 */
public class Constants {
    
    /**
     * HTTP状态码
     */
    public static class HttpStatus {
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    
    /**
     * 业务状态码
     */
    public static class BusinessCode {
        public static final int SUCCESS = 200;
        public static final int ERROR = 500;
        public static final int USER_NOT_FOUND = 1001;
        public static final int USER_ALREADY_EXISTS = 1002;
        public static final int INVALID_PASSWORD = 1003;
        public static final int TOKEN_EXPIRED = 1004;
        public static final int INVALID_TOKEN = 1005;
    }
    
    /**
     * 用户角色
     */
    public static class UserRole {
        public static final String ADMIN = "admin";
        public static final String FOREST_MANAGER = "forest_manager";
        public static final String EXPERT = "expert";
        public static final String USER = "user";
    }
    
    /**
     * 用户状态
     */
    public static class UserStatus {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String LOCKED = "locked";
    }
    
    /**
     * 病虫害类型
     */
    public static class PestType {
        public static final String PINE_CATERPILLAR = "pine_caterpillar";
        public static final String POPLAR_CANKER = "poplar_canker";
        public static final String AMERICAN_WHITE_MOTH = "american_white_moth";
        public static final String OTHER = "other";
    }
    
    /**
     * 防治方案状态
     */
    public static class TreatmentStatus {
        public static final String DRAFT = "draft";
        public static final String EXECUTING = "executing";
        public static final String COMPLETED = "completed";
        public static final String PAUSED = "paused";
    }
    
    /**
     * 任务状态
     */
    public static class TaskStatus {
        public static final String PENDING = "pending";
        public static final String IN_PROGRESS = "in_progress";
        public static final String COMPLETED = "completed";
        public static final String FAILED = "failed";
    }
    
    /**
     * 药剂类型
     */
    public static class PesticideType {
        public static final String INSECTICIDE = "insecticide";
        public static final String FUNGICIDE = "fungicide";
        public static final String HERBICIDE = "herbicide";
        public static final String BIOLOGICAL = "biological";
    }
    
    /**
     * 预警级别
     */
    public static class WarningLevel {
        public static final String LOW = "low";
        public static final String MEDIUM = "medium";
        public static final String HIGH = "high";
        public static final String CRITICAL = "critical";
    }
    
    /**
     * 文件类型
     */
    public static class FileType {
        public static final String IMAGE = "image";
        public static final String DOCUMENT = "document";
        public static final String VIDEO = "video";
    }
    
    /**
     * 默认分页参数
     */
    public static class Page {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_SIZE = 10;
        public static final int MAX_SIZE = 100;
    }
    
    /**
     * JWT相关常量
     */
    public static class JWT {
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_NAME = "Authorization";
        public static final String USER_ID_CLAIM = "userId";
        public static final String USERNAME_CLAIM = "username";
        public static final String ROLE_CLAIM = "role";
    }
    
    /**
     * 缓存相关常量
     */
    public static class Cache {
        public static final String USER_CACHE = "user_cache";
        public static final String PEST_CACHE = "pest_cache";
        public static final String TREATMENT_CACHE = "treatment_cache";
        public static final long DEFAULT_TTL = 300; // 5分钟
    }
    
    /**
     * 系统配置
     */
    public static class System {
        public static final String DEFAULT_AVATAR = "/images/default-avatar.png";
        public static final String UPLOAD_PATH = "/uploads/";
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    }
}