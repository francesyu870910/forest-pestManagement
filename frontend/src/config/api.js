// API配置文件
const config = {
  // 开发环境配置
  development: {
    baseURL: '/api',
    timeout: 10000,
    withCredentials: true
  },

  // 生产环境配置
  production: {
    baseURL: '/api',
    timeout: 15000,
    withCredentials: true
  },

  // 测试环境配置
  test: {
    baseURL: 'http://localhost:8080/api',
    timeout: 5000,
    withCredentials: false
  }
}

// 获取当前环境配置
export const getApiConfig = () => {
  const env = import.meta.env.MODE || 'development'
  return config[env] || config.development
}

// API端点配置
export const API_ENDPOINTS = {
  // 认证相关
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    PROFILE: '/auth/profile',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
    VERIFY_RESET_CODE: '/auth/verify-reset-code'
  },

  // 用户管理
  USER: {
    LIST: '/users',
    CREATE: '/users',
    UPDATE: (id) => `/users/${id}`,
    DELETE: (id) => `/users/${id}`,
    DETAIL: (id) => `/users/${id}`,
    STATUS: (id) => `/users/${id}/status`,
    RESET_PASSWORD: (id) => `/users/${id}/reset-password`,
    PROFILE: '/users/profile',
    CHANGE_PASSWORD: '/users/change-password',
    AVATAR: '/users/avatar'
  },

  // 病虫害识别
  PEST: {
    IDENTIFY: '/pest/identify',
    LIST: '/pest/list',
    DETAIL: (id) => `/pest/${id}`,
    HISTORY: '/pest/identification/history',
    DELETE_RECORD: (id) => `/pest/identification/${id}`
  },

  // 防治方案
  TREATMENT: {
    PLANS: '/treatment/plans',
    PLAN_DETAIL: (id) => `/treatment/plans/${id}`,
    GENERATE: '/treatment/generate',
    TASKS: (planId) => `/treatment/plans/${planId}/tasks`,
    TASK_STATUS: (taskId) => `/treatment/tasks/${taskId}/status`,
    PROGRESS: '/treatment/progress'
  },

  // 药剂管理
  PESTICIDE: {
    LIST: '/pesticide/list',
    CREATE: '/pesticide',
    UPDATE: (id) => `/pesticide/${id}`,
    DELETE: (id) => `/pesticide/${id}`,
    STOCK_UPDATE: '/pesticide/stock/update',
    USAGE: (id) => `/pesticide/${id}/usage`,
    ALERTS: '/pesticide/alerts',
    USAGE_STATS: '/pesticide/usage/stats'
  },

  // 效果评估
  EVALUATION: {
    LIST: '/evaluation/list',
    CREATE: '/evaluation',
    UPDATE: (id) => `/evaluation/${id}`,
    DELETE: (id) => `/evaluation/${id}`,
    DETAIL: (id) => `/evaluation/${id}`,
    REPORT: (id) => `/evaluation/${id}/report`,
    COMPARISON: '/evaluation/comparison',
    STATS: '/evaluation/stats'
  },

  // 预测预警
  PREDICTION: {
    LIST: '/prediction/list',
    WARNINGS: '/prediction/warnings',
    WARNING_STATUS: (id) => `/prediction/warnings/${id}/status`,
    PREDICT: '/prediction/predict',
    WARNING_MAP: '/prediction/warnings/map',
    WARNING_STATS: '/prediction/warnings/stats',
    ACCURACY: '/prediction/accuracy'
  },

  // 森林资源
  FOREST: {
    RESOURCES: '/forest/resources',
    RESOURCE_DETAIL: (id) => `/forest/resources/${id}`,
    KNOWLEDGE: '/forest/knowledge',
    KNOWLEDGE_DETAIL: (id) => `/forest/knowledge/${id}`,
    KNOWLEDGE_SEARCH: '/forest/knowledge/search'
  },

  // 健康检查
  HEALTH: '/health'
}

// 错误码映射
export const ERROR_CODES = {
  // 通用错误
  SUCCESS: 200,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500,

  // 业务错误码
  USER_NOT_FOUND: 1001,
  USER_ALREADY_EXISTS: 1002,
  INVALID_PASSWORD: 1003,
  TOKEN_EXPIRED: 1004,
  INVALID_TOKEN: 1005,

  PEST_NOT_FOUND: 2001,
  IDENTIFICATION_FAILED: 2002,

  PLAN_NOT_FOUND: 3001,
  PLAN_ALREADY_EXISTS: 3002,

  PESTICIDE_NOT_FOUND: 4001,
  INSUFFICIENT_STOCK: 4002,

  EVALUATION_NOT_FOUND: 5001,
  INVALID_EVALUATION_DATA: 5002
}

// 错误消息映射
export const ERROR_MESSAGES = {
  [ERROR_CODES.BAD_REQUEST]: '请求参数错误',
  [ERROR_CODES.UNAUTHORIZED]: '未授权访问',
  [ERROR_CODES.FORBIDDEN]: '禁止访问',
  [ERROR_CODES.NOT_FOUND]: '资源不存在',
  [ERROR_CODES.INTERNAL_ERROR]: '服务器内部错误',

  [ERROR_CODES.USER_NOT_FOUND]: '用户不存在',
  [ERROR_CODES.USER_ALREADY_EXISTS]: '用户已存在',
  [ERROR_CODES.INVALID_PASSWORD]: '密码错误',
  [ERROR_CODES.TOKEN_EXPIRED]: '登录已过期',
  [ERROR_CODES.INVALID_TOKEN]: '无效的访问令牌',

  [ERROR_CODES.PEST_NOT_FOUND]: '病虫害信息不存在',
  [ERROR_CODES.IDENTIFICATION_FAILED]: '识别失败',

  [ERROR_CODES.PLAN_NOT_FOUND]: '防治方案不存在',
  [ERROR_CODES.PLAN_ALREADY_EXISTS]: '防治方案已存在',

  [ERROR_CODES.PESTICIDE_NOT_FOUND]: '药剂信息不存在',
  [ERROR_CODES.INSUFFICIENT_STOCK]: '库存不足',

  [ERROR_CODES.EVALUATION_NOT_FOUND]: '评估记录不存在',
  [ERROR_CODES.INVALID_EVALUATION_DATA]: '评估数据无效'
}

export default {
  getApiConfig,
  API_ENDPOINTS,
  ERROR_CODES,
  ERROR_MESSAGES
}
