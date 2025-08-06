import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register, getUserInfo, refreshToken, logout } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || sessionStorage.getItem('token') || 'dev-token')
  const refreshTokenValue = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref({
    id: '1',
    username: 'admin',
    name: '管理员',
    email: 'admin@example.com',
    role: 'admin',
    avatar: '',
    permissions: []
  })
  const rememberMe = ref(localStorage.getItem('rememberMe') === 'true')

  // 设置token
  const setToken = (newToken, newRefreshToken = '') => {
    token.value = newToken
    if (newRefreshToken) {
      refreshTokenValue.value = newRefreshToken
      localStorage.setItem('refreshToken', newRefreshToken)
    }

    if (rememberMe.value) {
      localStorage.setItem('token', newToken)
    } else {
      sessionStorage.setItem('token', newToken)
    }
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = { ...info }
  }

  // 设置记住密码状态
  const setRememberMe = (remember) => {
    rememberMe.value = remember
    localStorage.setItem('rememberMe', remember.toString())
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    refreshTokenValue.value = ''
    userInfo.value = {
      id: '',
      username: '',
      name: '',
      email: '',
      role: '',
      avatar: '',
      permissions: []
    }
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    sessionStorage.removeItem('token')
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!token.value
  }

  // 用户登录
  const userLogin = async (loginForm) => {
    try {
      const response = await login(loginForm)
      const { token: newToken, refreshToken: newRefreshToken, user } = response.data

      setRememberMe(loginForm.rememberMe)
      setToken(newToken, newRefreshToken)
      setUserInfo(user)

      ElMessage.success('登录成功')
      return response
    } catch (error) {
      const message = error.response?.data?.message || error.message || '登录失败'
      ElMessage.error(message)
      throw error
    }
  }

  // 用户注册
  const userRegister = async (registerForm) => {
    try {
      const response = await register(registerForm)
      ElMessage.success('注册成功，请登录')
      return response
    } catch (error) {
      const message = error.response?.data?.message || error.message || '注册失败'
      ElMessage.error(message)
      throw error
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await getUserInfo()
      setUserInfo(response.data)
      return response
    } catch (error) {
      const message = error.response?.data?.message || error.message || '获取用户信息失败'
      ElMessage.error(message)
      throw error
    }
  }

  // 刷新token
  const handleRefreshToken = async () => {
    try {
      const response = await refreshToken()
      const { token: newToken, refreshToken: newRefreshToken } = response.data
      setToken(newToken, newRefreshToken)
      return response
    } catch (error) {
      // 刷新失败，清除用户信息并跳转到登录页
      clearUserInfo()
      router.push('/login')
      throw error
    }
  }

  // 用户登出
  const userLogout = async () => {
    try {
      await logout()
      clearUserInfo()
      router.push('/login')
      ElMessage.success('退出成功')
    } catch (error) {
      // 即使登出接口失败，也要清除本地信息
      clearUserInfo()
      router.push('/login')
    }
  }

  // 检查权限
  const hasPermission = (permission) => {
    return userInfo.value.permissions.includes(permission)
  }

  // 检查角色
  const hasRole = (role) => {
    return userInfo.value.role === role
  }

  return {
    token,
    refreshTokenValue,
    userInfo,
    rememberMe,
    setToken,
    setUserInfo,
    setRememberMe,
    clearUserInfo,
    isLoggedIn,
    userLogin,
    userRegister,
    fetchUserInfo,
    handleRefreshToken,
    userLogout,
    hasPermission,
    hasRole
  }
})
