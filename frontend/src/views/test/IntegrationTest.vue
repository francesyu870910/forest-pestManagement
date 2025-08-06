<template>
  <div class="integration-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>集成测试和端到端测试</span>
          <div class="header-actions">
            <el-button
              type="primary"
              @click="runAllTests"
              :loading="testManager.isRunning"
              :disabled="testManager.isRunning"
            >
              <el-icon><VideoPlay /></el-icon>
              运行所有测试
            </el-button>
            <el-button @click="exportReport" :disabled="results.length === 0">
              <el-icon><Download /></el-icon>
              导出报告
            </el-button>
            <el-button @click="clearResults">
              <el-icon><Delete /></el-icon>
              清空结果
            </el-button>
          </div>
        </div>
      </template>

      <!-- 测试进度 -->
      <div v-if="testManager.isRunning" class="test-progress">
        <el-alert title="测试正在运行中..." type="info" :closable="false" show-icon>
          <template #default>
            <div class="progress-info">
              <p>当前进度: {{ status.completedSuites }}/{{ status.totalSuites }} 个测试套件</p>
              <el-progress :percentage="progressPercentage" :stroke-width="8" :show-text="false" />
            </div>
          </template>
        </el-alert>
      </div>

      <!-- 测试结果摘要 -->
      <div v-if="overallResults.summary" class="test-summary">
        <h4>测试结果摘要</h4>
        <el-row :gutter="20">
          <el-col :span="4">
            <div class="summary-item">
              <div class="summary-label">测试套件</div>
              <div class="summary-value">
                {{ overallResults.summary.completedSuites }}/{{
                  overallResults.summary.totalSuites
                }}
              </div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="summary-item">
              <div class="summary-label">测试用例</div>
              <div class="summary-value">
                {{ overallResults.summary.totalPassed }}/{{ overallResults.summary.totalTests }}
              </div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="summary-item success">
              <div class="summary-label">通过</div>
              <div class="summary-value">{{ overallResults.summary.totalPassed }}</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="summary-item failed">
              <div class="summary-label">失败</div>
              <div class="summary-value">{{ overallResults.summary.totalFailed }}</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="summary-item">
              <div class="summary-label">成功率</div>
              <div class="summary-value">{{ overallResults.summary.overallSuccessRate }}%</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="summary-item">
              <div class="summary-label">生成时间</div>
              <div class="summary-value">{{ formatTime(overallResults.timestamp) }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 测试套件列表 -->
      <div class="test-suites">
        <h4>测试套件</h4>
        <el-row :gutter="20">
          <el-col :span="12" v-for="suite in testSuites" :key="suite.key">
            <el-card class="suite-card" shadow="hover">
              <div class="suite-header">
                <div class="suite-info">
                  <h5>{{ suite.name }}</h5>
                  <p>{{ suite.testCount }} 个测试用例</p>
                </div>
                <div class="suite-actions">
                  <el-button
                    size="small"
                    @click="runSingleSuite(suite.key)"
                    :loading="runningSuites.has(suite.key)"
                  >
                    运行
                  </el-button>
                  <el-button size="small" type="info" @click="showSuiteDetails(suite.key)">
                    详情
                  </el-button>
                </div>
              </div>

              <!-- 套件结果 -->
              <div v-if="getSuiteResult(suite.key)" class="suite-result">
                <div class="result-header">
                  <el-tag
                    :type="getSuiteResult(suite.key).status === 'completed' ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ getSuiteResult(suite.key).status === 'completed' ? '完成' : '失败' }}
                  </el-tag>
                  <span class="success-rate">
                    成功率: {{ getSuiteResult(suite.key).successRate }}%
                  </span>
                </div>
                <el-progress
                  :percentage="parseFloat(getSuiteResult(suite.key).successRate)"
                  :stroke-width="4"
                  :show-text="false"
                  :color="getProgressColor(parseFloat(getSuiteResult(suite.key).successRate))"
                />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 详细测试结果 -->
      <div v-if="results.length > 0" class="detailed-results">
        <h4>详细测试结果</h4>
        <el-collapse v-model="activeCollapse">
          <el-collapse-item
            v-for="suite in results"
            :key="suite.suiteKey"
            :title="suite.suiteName"
            :name="suite.suiteKey"
          >
            <template #title>
              <div class="collapse-title">
                <span>{{ suite.suiteName }}</span>
                <div class="title-badges">
                  <el-tag :type="suite.status === 'completed' ? 'success' : 'danger'" size="small">
                    {{ suite.status === 'completed' ? '完成' : '失败' }}
                  </el-tag>
                  <el-tag type="info" size="small">
                    {{ suite.passed || 0 }}/{{ suite.total || 0 }}
                  </el-tag>
                </div>
              </div>
            </template>

            <div class="suite-details">
              <div v-if="suite.error" class="suite-error">
                <el-alert :title="suite.error" type="error" :closable="false" show-icon />
              </div>

              <div v-if="suite.results" class="test-results">
                <el-table :data="suite.results" stripe>
                  <el-table-column prop="name" label="测试名称" min-width="200" />
                  <el-table-column prop="passed" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="row.passed ? 'success' : 'danger'" size="small">
                        {{ row.passed ? '通过' : '失败' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="duration" label="耗时" width="100">
                    <template #default="{ row }"> {{ row.duration }}ms </template>
                  </el-table-column>
                  <el-table-column prop="error" label="错误信息" show-overflow-tooltip>
                    <template #default="{ row }">
                      <span v-if="row.error" class="error-text">{{ row.error }}</span>
                      <span v-else class="success-text">-</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 空状态 -->
      <div v-if="results.length === 0 && !testManager.isRunning" class="empty-state">
        <el-empty description="暂无测试结果">
          <el-button type="primary" @click="runAllTests">开始测试</el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 套件详情对话框 -->
    <el-dialog v-model="showDetailsDialog" :title="currentSuite?.name" width="600px">
      <div v-if="currentSuite" class="suite-details-dialog">
        <div class="suite-info">
          <p><strong>优先级:</strong> {{ currentSuite.priority }}</p>
          <p><strong>测试用例数:</strong> {{ currentSuite.testCount }}</p>
        </div>

        <h5>测试用例列表</h5>
        <div class="test-cases">
          <div v-for="testCase in currentSuiteTests" :key="testCase.name" class="test-case-item">
            <div class="test-case-header">
              <h6>{{ testCase.name }}</h6>
              <el-button
                size="small"
                type="primary"
                @click="runSpecificTest(currentSuite.key, testCase.name)"
              >
                单独运行
              </el-button>
            </div>
            <p class="test-case-desc">{{ testCase.description }}</p>
            <p class="test-case-steps">{{ testCase.stepCount }} 个步骤</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay, Download, Delete } from '@element-plus/icons-vue'
import { integrationTestManager } from '@/tests/IntegrationTestManager'

// 响应式数据
const testManager = integrationTestManager
const results = ref([])
const overallResults = ref({})
const testSuites = ref([])
const activeCollapse = ref([])
const runningSuites = ref(new Set())
const showDetailsDialog = ref(false)
const currentSuite = ref(null)
const currentSuiteTests = ref([])

// 计算属性
const status = computed(() => testManager.getStatus())

const progressPercentage = computed(() => {
  if (status.value.totalSuites === 0) return 0
  return Math.round((status.value.completedSuites / status.value.totalSuites) * 100)
})

// 方法
const runAllTests = async () => {
  try {
    ElMessage.info('开始执行集成测试...')

    const result = await testManager.runAllTests()

    results.value = result.suiteResults
    overallResults.value = result

    const successRate = parseFloat(result.summary.overallSuccessRate)
    if (successRate >= 90) {
      ElMessage.success(`测试完成！成功率: ${successRate}%`)
    } else if (successRate >= 70) {
      ElMessage.warning(`测试完成，但成功率较低: ${successRate}%`)
    } else {
      ElMessage.error(`测试完成，成功率过低: ${successRate}%`)
    }
  } catch (error) {
    console.error('集成测试执行失败:', error)
    ElMessage.error('集成测试执行失败')
  }
}

const runSingleSuite = async (suiteKey) => {
  runningSuites.value.add(suiteKey)

  try {
    ElMessage.info('开始执行测试套件...')

    const result = await testManager.runTestSuite(suiteKey)

    // 更新结果
    const existingIndex = results.value.findIndex((r) => r.suiteKey === suiteKey)
    if (existingIndex >= 0) {
      results.value[existingIndex] = result
    } else {
      results.value.push(result)
    }

    ElMessage.success(`测试套件执行完成，成功率: ${result.successRate}%`)
  } catch (error) {
    console.error('测试套件执行失败:', error)
    ElMessage.error('测试套件执行失败')
  } finally {
    runningSuites.value.delete(suiteKey)
  }
}

const runSpecificTest = async (suiteKey, testName) => {
  try {
    ElMessage.info(`开始执行测试: ${testName}`)

    const result = await testManager.runSpecificTest(suiteKey, testName)

    ElMessage.success(`测试执行完成，成功率: ${result.successRate}%`)
  } catch (error) {
    console.error('测试执行失败:', error)
    ElMessage.error('测试执行失败')
  }
}

const showSuiteDetails = (suiteKey) => {
  const suite = testSuites.value.find((s) => s.key === suiteKey)
  if (suite) {
    currentSuite.value = suite
    currentSuiteTests.value = testManager.getTestCases(suiteKey)
    showDetailsDialog.value = true
  }
}

const getSuiteResult = (suiteKey) => {
  return results.value.find((r) => r.suiteKey === suiteKey)
}

const getProgressColor = (percentage) => {
  if (percentage >= 90) return '#67c23a'
  if (percentage >= 70) return '#e6a23c'
  return '#f56c6c'
}

const exportReport = () => {
  testManager.exportReportAsJson()
  ElMessage.success('测试报告已导出')
}

const clearResults = () => {
  results.value = []
  overallResults.value = {}
  testManager.cleanup()
  ElMessage.success('结果已清空')
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return new Date(timestamp).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  testSuites.value = testManager.getTestSuites()
})
</script>

<style lang="scss" scoped>
.integration-test {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .test-progress {
    margin-bottom: 20px;

    .progress-info {
      p {
        margin: 0 0 10px 0;
      }
    }
  }

  .test-summary {
    margin-bottom: 30px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;

    h4 {
      margin: 0 0 15px 0;
      color: #2c3e50;
    }

    .summary-item {
      text-align: center;
      padding: 15px;
      background-color: white;
      border-radius: 6px;

      &.success {
        border-left: 4px solid #67c23a;
      }

      &.failed {
        border-left: 4px solid #f56c6c;
      }

      .summary-label {
        font-size: 12px;
        color: #909399;
        margin-bottom: 8px;
      }

      .summary-value {
        font-size: 18px;
        font-weight: bold;
        color: #2c3e50;
      }
    }
  }

  .test-suites {
    margin-bottom: 30px;

    h4 {
      margin: 0 0 15px 0;
      color: #2c3e50;
    }

    .suite-card {
      margin-bottom: 15px;

      .suite-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 15px;

        .suite-info {
          flex: 1;

          h5 {
            margin: 0 0 5px 0;
            color: #2c3e50;
            font-size: 16px;
          }

          p {
            margin: 0;
            color: #909399;
            font-size: 12px;
          }
        }

        .suite-actions {
          display: flex;
          gap: 8px;
        }
      }

      .suite-result {
        .result-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .success-rate {
            font-size: 12px;
            color: #606266;
          }
        }
      }
    }
  }

  .detailed-results {
    h4 {
      margin: 0 0 15px 0;
      color: #2c3e50;
    }

    .collapse-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;

      .title-badges {
        display: flex;
        gap: 8px;
      }
    }

    .suite-details {
      .suite-error {
        margin-bottom: 15px;
      }

      .test-results {
        .error-text {
          color: #f56c6c;
        }

        .success-text {
          color: #909399;
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 0;
  }

  .suite-details-dialog {
    .suite-info {
      margin-bottom: 20px;
      padding: 15px;
      background-color: #f8f9fa;
      border-radius: 4px;

      p {
        margin: 5px 0;
      }
    }

    h5 {
      margin: 20px 0 10px 0;
      color: #2c3e50;
    }

    .test-cases {
      .test-case-item {
        padding: 15px;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        margin-bottom: 10px;

        .test-case-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          h6 {
            margin: 0;
            color: #2c3e50;
            font-size: 14px;
          }
        }

        .test-case-desc {
          margin: 5px 0;
          color: #606266;
          font-size: 13px;
        }

        .test-case-steps {
          margin: 5px 0 0 0;
          color: #909399;
          font-size: 12px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .integration-test {
    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
    }

    .test-summary {
      .el-row {
        flex-direction: column;

        .el-col {
          width: 100%;
          margin-bottom: 10px;
        }
      }
    }

    .test-suites {
      .el-row {
        flex-direction: column;

        .el-col {
          width: 100%;
          margin-bottom: 15px;
        }
      }

      .suite-card .suite-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;

        .suite-actions {
          width: 100%;
          justify-content: flex-end;
        }
      }
    }
  }
}
</style>
