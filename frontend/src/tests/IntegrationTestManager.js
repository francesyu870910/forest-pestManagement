import { E2ETestFramework } from './e2e/E2ETestFramework'
import userAuthFlowTests from './e2e/userAuthFlow'
import pestIdentificationFlowTests from './e2e/pestIdentificationFlow'
import treatmentPlanFlowTests from './e2e/treatmentPlanFlow'
import dataConsistencyFlowTests from './e2e/dataConsistencyFlow'

/**
 * 集成测试管理器
 * 统一管理和执行所有集成测试
 */
export class IntegrationTestManager {
  constructor() {
    this.e2eFramework = new E2ETestFramework()
    this.testSuites = new Map()
    this.results = []
    this.isRunning = false

    this.registerTestSuites()
  }

  // 注册测试套件
  registerTestSuites() {
    this.testSuites.set('userAuth', {
      name: '用户认证流程测试',
      tests: userAuthFlowTests,
      priority: 1
    })

    this.testSuites.set('pestIdentification', {
      name: '病虫害识别流程测试',
      tests: pestIdentificationFlowTests,
      priority: 2
    })

    this.testSuites.set('treatmentPlan', {
      name: '防治方案管理流程测试',
      tests: treatmentPlanFlowTests,
      priority: 3
    })

    this.testSuites.set('dataConsistency', {
      name: '数据一致性测试',
      tests: dataConsistencyFlowTests,
      priority: 4
    })
  }

  // 运行所有测试套件
  async runAllTests() {
    if (this.isRunning) {
      throw new Error('测试正在运行中')
    }

    this.isRunning = true
    this.results = []

    console.log('🚀 开始执行集成测试...')
    const startTime = Date.now()

    try {
      // 按优先级排序执行测试套件
      const sortedSuites = Array.from(this.testSuites.entries()).sort(
        ([, a], [, b]) => a.priority - b.priority
      )

      for (const [key, suite] of sortedSuites) {
        console.log(`\n📋 执行测试套件: ${suite.name}`)

        try {
          const result = await this.e2eFramework.runTestSuite(suite.name, suite.tests)

          this.results.push({
            suiteKey: key,
            suiteName: suite.name,
            ...result,
            status: 'completed'
          })
        } catch (error) {
          console.error(`❌ 测试套件执行失败: ${suite.name}`, error)

          this.results.push({
            suiteKey: key,
            suiteName: suite.name,
            status: 'failed',
            error: error.message,
            total: 0,
            passed: 0,
            failed: 0,
            successRate: 0
          })
        }
      }

      const endTime = Date.now()
      const totalDuration = endTime - startTime

      this.printOverallResults(totalDuration)

      return this.getOverallResults()
    } finally {
      this.isRunning = false
    }
  }

  // 运行单个测试套件
  async runTestSuite(suiteKey) {
    const suite = this.testSuites.get(suiteKey)
    if (!suite) {
      throw new Error(`测试套件不存在: ${suiteKey}`)
    }

    console.log(`📋 执行单个测试套件: ${suite.name}`)

    const result = await this.e2eFramework.runTestSuite(suite.name, suite.tests)

    return {
      suiteKey,
      suiteName: suite.name,
      ...result
    }
  }

  // 运行特定的测试用例
  async runSpecificTest(suiteKey, testName) {
    const suite = this.testSuites.get(suiteKey)
    if (!suite) {
      throw new Error(`测试套件不存在: ${suiteKey}`)
    }

    const test = suite.tests.find((t) => t.name === testName)
    if (!test) {
      throw new Error(`测试用例不存在: ${testName}`)
    }

    console.log(`🎯 执行特定测试: ${testName}`)

    return this.e2eFramework.runTestSuite(`${suite.name} - ${testName}`, [test])
  }

  // 获取测试套件列表
  getTestSuites() {
    return Array.from(this.testSuites.entries()).map(([key, suite]) => ({
      key,
      name: suite.name,
      priority: suite.priority,
      testCount: suite.tests.length
    }))
  }

  // 获取特定套件的测试用例
  getTestCases(suiteKey) {
    const suite = this.testSuites.get(suiteKey)
    if (!suite) {
      return []
    }

    return suite.tests.map((test) => ({
      name: test.name,
      description: test.description,
      stepCount: test.steps.length
    }))
  }

  // 打印总体结果
  printOverallResults(duration) {
    const totalSuites = this.results.length
    const completedSuites = this.results.filter((r) => r.status === 'completed').length
    const failedSuites = totalSuites - completedSuites

    const totalTests = this.results.reduce((sum, r) => sum + (r.total || 0), 0)
    const totalPassed = this.results.reduce((sum, r) => sum + (r.passed || 0), 0)
    const totalFailed = totalTests - totalPassed

    const overallSuccessRate = totalTests > 0 ? ((totalPassed / totalTests) * 100).toFixed(1) : 0

    console.log('\n' + '='.repeat(60))
    console.log('🏁 集成测试总体结果')
    console.log('='.repeat(60))
    console.log(`📊 测试套件: ${completedSuites}/${totalSuites} 完成`)
    console.log(`📈 测试用例: ${totalPassed}/${totalTests} 通过`)
    console.log(`📉 失败用例: ${totalFailed}`)
    console.log(`🎯 总体成功率: ${overallSuccessRate}%`)
    console.log(`⏱️  总耗时: ${duration}ms`)

    if (failedSuites > 0) {
      console.log('\n❌ 失败的测试套件:')
      this.results
        .filter((r) => r.status === 'failed')
        .forEach((r) => {
          console.log(`  • ${r.suiteName}: ${r.error}`)
        })
    }

    if (totalFailed > 0) {
      console.log('\n❌ 失败的测试用例:')
      this.results.forEach((suite) => {
        if (suite.results) {
          suite.results
            .filter((test) => !test.passed)
            .forEach((test) => {
              console.log(`  • ${suite.suiteName} - ${test.name}: ${test.error}`)
            })
        }
      })
    }

    console.log('='.repeat(60))
  }

  // 获取总体结果
  getOverallResults() {
    const totalSuites = this.results.length
    const completedSuites = this.results.filter((r) => r.status === 'completed').length

    const totalTests = this.results.reduce((sum, r) => sum + (r.total || 0), 0)
    const totalPassed = this.results.reduce((sum, r) => sum + (r.passed || 0), 0)
    const totalFailed = totalTests - totalPassed

    const overallSuccessRate = totalTests > 0 ? ((totalPassed / totalTests) * 100).toFixed(1) : 0

    return {
      summary: {
        totalSuites,
        completedSuites,
        failedSuites: totalSuites - completedSuites,
        totalTests,
        totalPassed,
        totalFailed,
        overallSuccessRate
      },
      suiteResults: this.results,
      timestamp: new Date().toISOString()
    }
  }

  // 生成测试报告
  generateReport() {
    const results = this.getOverallResults()

    const report = {
      title: '森林病虫害防治系统 - 集成测试报告',
      generatedAt: new Date().toISOString(),
      summary: results.summary,
      details: results.suiteResults.map((suite) => ({
        suiteName: suite.suiteName,
        status: suite.status,
        successRate: suite.successRate,
        testResults:
          suite.results?.map((test) => ({
            name: test.name,
            passed: test.passed,
            duration: test.duration,
            error: test.error
          })) || []
      }))
    }

    return report
  }

  // 导出测试报告为JSON
  exportReportAsJson() {
    const report = this.generateReport()
    const blob = new Blob([JSON.stringify(report, null, 2)], {
      type: 'application/json'
    })

    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `integration-test-report-${Date.now()}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }

  // 清理测试数据
  cleanup() {
    this.results = []
    this.e2eFramework.cleanup()
  }

  // 获取运行状态
  getStatus() {
    return {
      isRunning: this.isRunning,
      completedSuites: this.results.length,
      totalSuites: this.testSuites.size
    }
  }
}

// 创建全局实例
export const integrationTestManager = new IntegrationTestManager()

export default IntegrationTestManager
