// 地图错误处理器
export class MapErrorHandler {
  constructor() {
    this.retryCount = 0
    this.maxRetries = 3
    this.retryDelay = 1000
  }

  // 处理天地图API加载失败
  handleAPILoadError(error) {
    console.error('天地图API加载失败:', error)
    
    return {
      title: '地图服务不可用',
      message: '无法连接到天地图服务，请检查网络连接或稍后重试',
      code: 'API_LOAD_ERROR',
      canRetry: true
    }
  }

  // 处理网络错误
  handleNetworkError(error) {
    console.error('网络错误:', error)
    
    if (error.code === 'TIMEOUT') {
      return {
        title: '网络连接超时',
        message: '请求超时，正在重试...',
        code: 'NETWORK_TIMEOUT',
        canRetry: true
      }
    } else if (error.code === 'NO_CONNECTION') {
      return {
        title: '网络连接不可用',
        message: '网络连接不可用，已切换到离线模式',
        code: 'NO_CONNECTION',
        canRetry: false
      }
    }
    
    return {
      title: '网络错误',
      message: '网络连接异常，请检查网络设置',
      code: 'NETWORK_ERROR',
      canRetry: true
    }
  }

  // 处理数据加载错误
  handleDataLoadError(error) {
    console.error('数据加载失败:', error)
    
    return {
      title: '数据加载失败',
      message: '无法加载地图数据，将显示缓存数据',
      code: 'DATA_LOAD_ERROR',
      canRetry: true
    }
  }

  // 处理地图初始化错误
  handleMapInitError(error) {
    console.error('地图初始化失败:', error)
    
    return {
      title: '地图初始化失败',
      message: '地图组件初始化失败，请刷新页面重试',
      code: 'MAP_INIT_ERROR',
      canRetry: true
    }
  }

  // 重试逻辑
  async retry(operation, maxRetries = this.maxRetries) {
    let lastError = null
    
    for (let i = 0; i <= maxRetries; i++) {
      try {
        return await operation()
      } catch (error) {
        lastError = error
        
        if (i < maxRetries) {
          console.log(`重试 ${i + 1}/${maxRetries}...`)
          await this.delay(this.retryDelay * (i + 1))
        }
      }
    }
    
    throw lastError
  }

  // 延迟函数
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  // 检查错误是否可以重试
  canRetry(error) {
    const retryableErrors = [
      'API_LOAD_ERROR',
      'NETWORK_TIMEOUT',
      'DATA_LOAD_ERROR',
      'MAP_INIT_ERROR'
    ]
    
    return retryableErrors.includes(error.code)
  }

  // 获取用户友好的错误消息
  getUserFriendlyMessage(error) {
    const messageMap = {
      'API_LOAD_ERROR': '地图服务暂时不可用，请稍后重试',
      'NETWORK_TIMEOUT': '网络连接超时，请检查网络设置',
      'NO_CONNECTION': '网络连接不可用，请检查网络连接',
      'DATA_LOAD_ERROR': '数据加载失败，请刷新页面重试',
      'MAP_INIT_ERROR': '地图加载失败，请刷新页面重试'
    }
    
    return messageMap[error.code] || '发生未知错误，请联系技术支持'
  }

  // 记录错误日志
  logError(error, context = {}) {
    const errorLog = {
      timestamp: new Date().toISOString(),
      error: {
        message: error.message,
        code: error.code,
        stack: error.stack
      },
      context,
      userAgent: navigator.userAgent,
      url: window.location.href
    }
    
    console.error('地图错误日志:', errorLog)
    
    // 这里可以发送错误日志到服务器
    // this.sendErrorLog(errorLog)
  }

  // 发送错误日志到服务器（可选）
  async sendErrorLog(errorLog) {
    try {
      // 发送到错误收集服务
      // await fetch('/api/error-log', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(errorLog)
      // })
    } catch (err) {
      console.error('发送错误日志失败:', err)
    }
  }
}