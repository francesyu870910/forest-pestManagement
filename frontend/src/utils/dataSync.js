import { ElMessage } from 'element-plus'

/**
 * 数据同步工具类
 * 用于前后端数据状态同步和缓存管理
 */
export class DataSyncManager {
  constructor() {
    this.cache = new Map()
    this.syncQueue = []
    this.isOnline = navigator.onLine
    this.setupEventListeners()
  }

  // 设置事件监听器
  setupEventListeners() {
    // 监听网络状态变化
    window.addEventListener('online', () => {
      this.isOnline = true
      this.processSyncQueue()
      ElMessage.success('网络连接已恢复')
    })

    window.addEventListener('offline', () => {
      this.isOnline = false
      ElMessage.warning('网络连接已断开，数据将在恢复后同步')
    })

    // 监听页面可见性变化
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden && this.isOnline) {
        this.refreshCriticalData()
      }
    })
  }

  // 缓存数据
  setCache(key, data, ttl = 5 * 60 * 1000) {
    // 默认5分钟过期
    const expireTime = Date.now() + ttl
    this.cache.set(key, {
      data,
      expireTime,
      timestamp: Date.now()
    })
  }

  // 获取缓存数据
  getCache(key) {
    const cached = this.cache.get(key)
    if (!cached) return null

    if (Date.now() > cached.expireTime) {
      this.cache.delete(key)
      return null
    }

    return cached.data
  }

  // 清除缓存
  clearCache(key) {
    if (key) {
      this.cache.delete(key)
    } else {
      this.cache.clear()
    }
  }

  // 添加到同步队列
  addToSyncQueue(operation) {
    this.syncQueue.push({
      ...operation,
      timestamp: Date.now(),
      retryCount: 0
    })

    if (this.isOnline) {
      this.processSyncQueue()
    }
  }

  // 处理同步队列
  async processSyncQueue() {
    if (this.syncQueue.length === 0) return

    const operations = [...this.syncQueue]
    this.syncQueue = []

    for (const operation of operations) {
      try {
        await this.executeOperation(operation)
      } catch (error) {
        console.error('同步操作失败:', error)

        // 重试机制
        if (operation.retryCount < 3) {
          operation.retryCount++
          this.syncQueue.push(operation)
        } else {
          ElMessage.error(`数据同步失败: ${operation.type}`)
        }
      }
    }
  }

  // 执行同步操作
  async executeOperation(operation) {
    const { type, api, data, onSuccess, onError } = operation

    try {
      const result = await api(data)

      if (onSuccess) {
        onSuccess(result)
      }

      // 更新相关缓存
      this.updateRelatedCache(type, result)

      return result
    } catch (error) {
      if (onError) {
        onError(error)
      }
      throw error
    }
  }

  // 更新相关缓存
  updateRelatedCache(operationType, result) {
    switch (operationType) {
      case 'USER_UPDATE':
        this.clearCache('user-info')
        this.clearCache('user-list')
        break
      case 'PEST_IDENTIFY':
        this.clearCache('pest-history')
        break
      case 'TREATMENT_CREATE':
      case 'TREATMENT_UPDATE':
        this.clearCache('treatment-list')
        this.clearCache('treatment-progress')
        break
      case 'PESTICIDE_UPDATE':
        this.clearCache('pesticide-list')
        this.clearCache('pesticide-alerts')
        break
      case 'EVALUATION_CREATE':
        this.clearCache('evaluation-list')
        this.clearCache('evaluation-stats')
        break
    }
  }

  // 刷新关键数据
  async refreshCriticalData() {
    const criticalCaches = ['user-info', 'pesticide-alerts', 'warning-list']

    criticalCaches.forEach((key) => {
      this.clearCache(key)
    })
  }

  // 数据预加载
  async preloadData(dataLoaders) {
    const promises = dataLoaders.map(async ({ key, loader, ttl }) => {
      try {
        const data = await loader()
        this.setCache(key, data, ttl)
        return { key, success: true, data }
      } catch (error) {
        console.error(`预加载数据失败 ${key}:`, error)
        return { key, success: false, error }
      }
    })

    return Promise.allSettled(promises)
  }

  // 获取同步状态
  getSyncStatus() {
    return {
      isOnline: this.isOnline,
      queueLength: this.syncQueue.length,
      cacheSize: this.cache.size,
      lastSync: this.lastSyncTime
    }
  }

  // 强制同步
  async forceSync() {
    if (!this.isOnline) {
      ElMessage.warning('网络未连接，无法同步数据')
      return false
    }

    try {
      await this.processSyncQueue()
      this.lastSyncTime = Date.now()
      ElMessage.success('数据同步完成')
      return true
    } catch (error) {
      ElMessage.error('数据同步失败')
      return false
    }
  }
}

// 创建全局实例
export const dataSyncManager = new DataSyncManager()

// 数据同步装饰器
export function withDataSync(options = {}) {
  return function (target, propertyKey, descriptor) {
    const originalMethod = descriptor.value

    descriptor.value = async function (...args) {
      const { cacheKey, ttl, syncType } = options

      // 尝试从缓存获取数据
      if (cacheKey) {
        const cached = dataSyncManager.getCache(cacheKey)
        if (cached) {
          return cached
        }
      }

      try {
        const result = await originalMethod.apply(this, args)

        // 缓存结果
        if (cacheKey && result) {
          dataSyncManager.setCache(cacheKey, result, ttl)
        }

        return result
      } catch (error) {
        // 如果是写操作且网络断开，添加到同步队列
        if (syncType === 'write' && !dataSyncManager.isOnline) {
          dataSyncManager.addToSyncQueue({
            type: syncType,
            api: originalMethod.bind(this),
            data: args[0],
            onSuccess: (result) => {
              ElMessage.success('数据已同步')
            },
            onError: (error) => {
              console.error('同步失败:', error)
            }
          })

          ElMessage.info('数据已保存到本地，将在网络恢复后同步')
          return { success: true, synced: false }
        }

        throw error
      }
    }

    return descriptor
  }
}

// 批量数据操作
export class BatchDataManager {
  constructor() {
    this.batchQueue = []
    this.batchSize = 10
    this.batchTimeout = 1000 // 1秒
    this.timer = null
  }

  // 添加批量操作
  addToBatch(operation) {
    this.batchQueue.push(operation)

    if (this.batchQueue.length >= this.batchSize) {
      this.processBatch()
    } else {
      this.scheduleBatchProcess()
    }
  }

  // 调度批量处理
  scheduleBatchProcess() {
    if (this.timer) {
      clearTimeout(this.timer)
    }

    this.timer = setTimeout(() => {
      this.processBatch()
    }, this.batchTimeout)
  }

  // 处理批量操作
  async processBatch() {
    if (this.batchQueue.length === 0) return

    const operations = [...this.batchQueue]
    this.batchQueue = []

    if (this.timer) {
      clearTimeout(this.timer)
      this.timer = null
    }

    try {
      // 按类型分组操作
      const groupedOps = this.groupOperations(operations)

      // 并行执行同类型操作
      const promises = Object.entries(groupedOps).map(([type, ops]) => {
        return this.executeBatchOperations(type, ops)
      })

      await Promise.allSettled(promises)
    } catch (error) {
      console.error('批量操作失败:', error)
    }
  }

  // 分组操作
  groupOperations(operations) {
    return operations.reduce((groups, op) => {
      const type = op.type || 'default'
      if (!groups[type]) {
        groups[type] = []
      }
      groups[type].push(op)
      return groups
    }, {})
  }

  // 执行批量操作
  async executeBatchOperations(type, operations) {
    // 根据操作类型选择执行策略
    switch (type) {
      case 'UPDATE':
        return this.executeBatchUpdates(operations)
      case 'DELETE':
        return this.executeBatchDeletes(operations)
      case 'CREATE':
        return this.executeBatchCreates(operations)
      default:
        return this.executeSequential(operations)
    }
  }

  // 批量更新
  async executeBatchUpdates(operations) {
    const promises = operations.map((op) => op.execute())
    return Promise.allSettled(promises)
  }

  // 批量删除
  async executeBatchDeletes(operations) {
    const promises = operations.map((op) => op.execute())
    return Promise.allSettled(promises)
  }

  // 批量创建
  async executeBatchCreates(operations) {
    const promises = operations.map((op) => op.execute())
    return Promise.allSettled(promises)
  }

  // 顺序执行
  async executeSequential(operations) {
    const results = []
    for (const op of operations) {
      try {
        const result = await op.execute()
        results.push({ status: 'fulfilled', value: result })
      } catch (error) {
        results.push({ status: 'rejected', reason: error })
      }
    }
    return results
  }
}

// 创建批量数据管理器实例
export const batchDataManager = new BatchDataManager()

export default {
  DataSyncManager,
  dataSyncManager,
  withDataSync,
  BatchDataManager,
  batchDataManager
}
