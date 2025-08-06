import * as api from '@/api'

/**
 * API连接测试工具
 */
export class ApiTester {
  constructor() {
    this.results = []
  }

  // 测试健康检查接口
  async testHealthCheck() {
    try {
      const response = await api.healthCheck()
      this.addResult('健康检查', true, response)
      return true
    } catch (error) {
      this.addResult('健康检查', false, error)
      return false
    }
  }

  // 测试用户认证接口
  async testAuth() {
    const testUser = {
      username: 'testuser',
      password: 'Test123456',
      name: '测试用户',
      email: 'test@example.com',
      role: 'user'
    }

    try {
      // 测试注册
      await api.register(testUser)
      this.addResult('用户注册', true, '注册成功')

      // 测试登录
      const loginResponse = await api.login({
        username: testUser.username,
        password: testUser.password,
        rememberMe: false
      })
      this.addResult('用户登录', true, loginResponse)

      // 测试获取用户信息
      const userInfoResponse = await api.getUserInfo()
      this.addResult('获取用户信息', true, userInfoResponse)

      return true
    } catch (error) {
      this.addResult('用户认证', false, error)
      return false
    }
  }

  // 测试病虫害识别接口
  async testPestIdentification() {
    try {
      // 创建模拟文件
      const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      const formData = new FormData()
      formData.append('image', mockFile)
      formData.append('symptoms', '叶片发黄')
      formData.append('location', '测试区域')

      const response = await api.identifyPest(formData)
      this.addResult('病虫害识别', true, response)

      // 测试获取识别历史
      const historyResponse = await api.getIdentificationHistory({ page: 1, size: 10 })
      this.addResult('识别历史查询', true, historyResponse)

      return true
    } catch (error) {
      this.addResult('病虫害识别', false, error)
      return false
    }
  }

  // 测试防治方案接口
  async testTreatmentPlan() {
    try {
      // 测试获取方案列表
      const listResponse = await api.getTreatmentPlanList({ page: 1, size: 10 })
      this.addResult('防治方案列表', true, listResponse)

      // 测试创建方案
      const planData = {
        planName: '测试防治方案',
        pestType: 'pine_caterpillar',
        targetArea: '测试区域',
        estimatedArea: 100,
        description: '测试方案描述',
        measures: [{ type: 'chemical', description: '化学防治' }],
        startTime: '2024-01-01 08:00:00',
        endTime: '2024-01-31 18:00:00',
        responsible: '测试负责人'
      }

      const createResponse = await api.createTreatmentPlan(planData)
      this.addResult('创建防治方案', true, createResponse)

      return true
    } catch (error) {
      this.addResult('防治方案', false, error)
      return false
    }
  }

  // 测试药剂管理接口
  async testPesticideManagement() {
    try {
      // 测试获取药剂列表
      const listResponse = await api.getPesticideList({ page: 1, size: 10 })
      this.addResult('药剂列表', true, listResponse)

      // 测试创建药剂
      const pesticideData = {
        name: '测试药剂',
        type: 'insecticide',
        specification: '500ml/瓶',
        unit: 'bottle',
        currentStock: 100,
        minStock: 10,
        price: 35.5,
        supplier: '测试供应商',
        expiryDate: '2025-12-31',
        instructions: '测试使用说明'
      }

      const createResponse = await api.createPesticide(pesticideData)
      this.addResult('创建药剂', true, createResponse)

      return true
    } catch (error) {
      this.addResult('药剂管理', false, error)
      return false
    }
  }

  // 运行所有测试
  async runAllTests() {
    console.log('开始API连接测试...')
    this.results = []

    await this.testHealthCheck()
    await this.testAuth()
    await this.testPestIdentification()
    await this.testTreatmentPlan()
    await this.testPesticideManagement()

    this.printResults()
    return this.results
  }

  // 添加测试结果
  addResult(testName, success, data) {
    this.results.push({
      testName,
      success,
      data,
      timestamp: new Date().toISOString()
    })
  }

  // 打印测试结果
  printResults() {
    console.log('\n=== API测试结果 ===')
    this.results.forEach((result) => {
      const status = result.success ? '✅ 成功' : '❌ 失败'
      console.log(`${status} ${result.testName}`)
      if (!result.success) {
        console.error('错误详情:', result.data)
      }
    })

    const successCount = this.results.filter((r) => r.success).length
    const totalCount = this.results.length
    console.log(`\n总计: ${successCount}/${totalCount} 个测试通过`)
  }

  // 获取测试摘要
  getSummary() {
    const successCount = this.results.filter((r) => r.success).length
    const totalCount = this.results.length
    return {
      total: totalCount,
      success: successCount,
      failed: totalCount - successCount,
      successRate: totalCount > 0 ? ((successCount / totalCount) * 100).toFixed(2) : 0
    }
  }
}

// 导出单例实例
export const apiTester = new ApiTester()

// 在开发环境下自动运行测试
if (import.meta.env.DEV) {
  // 延迟执行，确保应用已初始化
  setTimeout(() => {
    if (window.location.hash.includes('api-test')) {
      apiTester.runAllTests()
    }
  }, 2000)
}
