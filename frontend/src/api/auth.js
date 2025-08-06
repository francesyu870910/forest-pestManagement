import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/auth/profile',
    method: 'get'
  })
}

// 刷新token
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 忘记密码 - 发送验证码
export function sendResetCode(email) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data: { email }
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}

// 验证重置码
export function verifyResetCode(data) {
  return request({
    url: '/auth/verify-reset-code',
    method: 'post',
    data
  })
}
