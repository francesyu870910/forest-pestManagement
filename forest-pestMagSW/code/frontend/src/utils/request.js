import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getApiConfig, ERROR_MESSAGES } from '@/config/api'

// 获取API配置
const apiConfig = getApiConfig()

// 创建axios实例
const service = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: apiConfig.timeout,
  withCredentials: apiConfig.withCredentials,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 是否正在刷新token
let isRefreshing = false
// 重试队列
let requests = []

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    const res = response.data

    // 如果响应码不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未授权，跳转到登录页
      if (res.code === 401) {
        handleTokenExpired()
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  async (error) => {
    // 对响应错误做点什么
    console.error('响应错误:', error)

    const { response } = error
    let message = '网络错误'

    if (response) {
      const errorCode = response.data?.code || response.status
      message = ERROR_MESSAGES[errorCode] || response.data?.message

      switch (response.status) {
        case 400:
          message = message || '请求参数错误'
          break
        case 401:
          // Token过期，尝试刷新
          return handleTokenExpired(error)
        case 403:
          message = message || '拒绝访问'
          break
        case 404:
          message = message || '请求资源不存在'
          break
        case 500:
          message = message || '服务器内部错误'
          break
        default:
          message = message || `连接错误${response.status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.code === 'ECONNREFUSED') {
      message = '连接被拒绝，请检查服务器状态'
    } else if (error.code === 'ENOTFOUND') {
      message = '网络连接失败'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 处理token过期
async function handleTokenExpired(error) {
  const userStore = useUserStore()
  const refreshTokenValue = userStore.refreshTokenValue

  if (!refreshTokenValue) {
    // 没有refresh token，直接跳转登录
    userStore.clearUserInfo()
    window.location.href = '/login'
    return Promise.reject(error)
  }

  if (!isRefreshing) {
    isRefreshing = true

    try {
      // 尝试刷新token
      await userStore.handleRefreshToken()
      isRefreshing = false

      // 重新执行队列中的请求
      requests.forEach((cb) => cb())
      requests = []

      // 重新执行当前请求
      if (error && error.config) {
        const token = localStorage.getItem('token') || sessionStorage.getItem('token')
        error.config.headers.Authorization = `Bearer ${token}`
        return service.request(error.config)
      }
    } catch (refreshError) {
      isRefreshing = false
      requests = []

      // 刷新失败，清除用户信息并跳转登录
      userStore.clearUserInfo()
      window.location.href = '/login'
      return Promise.reject(refreshError)
    }
  } else {
    // 正在刷新token，将请求加入队列
    return new Promise((resolve) => {
      requests.push(() => {
        if (error && error.config) {
          const token = localStorage.getItem('token') || sessionStorage.getItem('token')
          error.config.headers.Authorization = `Bearer ${token}`
          resolve(service.request(error.config))
        }
      })
    })
  }
}

export default service
