/**
 * JWT令牌管理工具
 */

const TOKEN_KEY = 'token'
const REFRESH_TOKEN_KEY = 'refreshToken'
const REMEMBER_ME_KEY = 'rememberMe'

/**
 * 获取token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
}

/**
 * 设置token
 * @param {string} token - JWT令牌
 * @param {boolean} remember - 是否记住登录状态
 */
export function setToken(token, remember = false) {
  if (remember) {
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(REMEMBER_ME_KEY, 'true')
  } else {
    sessionStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(REMEMBER_ME_KEY, 'false')
  }
}

/**
 * 移除token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REMEMBER_ME_KEY)
}

/**
 * 获取刷新token
 */
export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

/**
 * 设置刷新token
 * @param {string} refreshToken - 刷新令牌
 */
export function setRefreshToken(refreshToken) {
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

/**
 * 移除刷新token
 */
export function removeRefreshToken() {
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

/**
 * 获取记住登录状态
 */
export function getRememberMe() {
  return localStorage.getItem(REMEMBER_ME_KEY) === 'true'
}

/**
 * 解析JWT令牌
 * @param {string} token - JWT令牌
 * @returns {object|null} 解析后的payload
 */
export function parseJWT(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('解析JWT令牌失败:', error)
    return null
  }
}

/**
 * 检查token是否过期
 * @param {string} token - JWT令牌
 * @returns {boolean} 是否过期
 */
export function isTokenExpired(token) {
  if (!token) return true

  const payload = parseJWT(token)
  if (!payload || !payload.exp) return true

  // 提前5分钟判断为过期，用于自动刷新
  const currentTime = Math.floor(Date.now() / 1000)
  const expireTime = payload.exp - 300 // 提前5分钟

  return currentTime >= expireTime
}

/**
 * 获取token剩余有效时间（秒）
 * @param {string} token - JWT令牌
 * @returns {number} 剩余秒数，-1表示已过期或无效
 */
export function getTokenRemainingTime(token) {
  if (!token) return -1

  const payload = parseJWT(token)
  if (!payload || !payload.exp) return -1

  const currentTime = Math.floor(Date.now() / 1000)
  const remainingTime = payload.exp - currentTime

  return remainingTime > 0 ? remainingTime : -1
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeToken()
  removeRefreshToken()
}

/**
 * 自动刷新token定时器
 */
let refreshTimer = null

/**
 * 启动自动刷新token
 * @param {Function} refreshCallback - 刷新回调函数
 */
export function startAutoRefresh(refreshCallback) {
  stopAutoRefresh()

  const checkAndRefresh = () => {
    const token = getToken()
    if (token && isTokenExpired(token)) {
      refreshCallback()
    }
  }

  // 每分钟检查一次
  refreshTimer = setInterval(checkAndRefresh, 60000)

  // 立即检查一次
  checkAndRefresh()
}

/**
 * 停止自动刷新token
 */
export function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}
