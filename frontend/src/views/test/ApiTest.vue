<template>
  <div class="api-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>API连接测试</span>
          <div class="header-actions">
            <el-button type="primary" @click="runAllTests" :loading="testing">
              <el-icon><Refresh /></el-icon>
              运行所有测试
            </el-button>
            <el-button @click="clearResults">
              <el-icon><Delete /></el-icon>
              清空结果
            </el-button>
          </div>
        </div>
      </template>

      <!-- 测试摘要 -->
      <div v-if="summary.total > 0" class="test-summary">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">总测试数</div>
              <div class="summary-value">{{ summary.total }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item success">
              <div class="summary-label">成功</div>
              <div class="summary-value">{{ summary.success }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item failed">
              <div class="summary-label">失败</div>
              <div class="summary-value">{{ summary.failed }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">成功率</div>
              <div class="summary-value">{{ summary.successRate }}%</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 单项测试按钮 -->
      <div class="test-actions">
        <el-button @click="testHealthCheck" :loading="testingItems.health"> 健康检查 </el-button>
        <el-button @click="testAuth" :loading="testingItems.auth"> 用户认证 </el-button>
        <el-button @click="testPestIdentification" :loading="testingItems.pest">
          病虫害识别
        </el-button>
        <el-button @click="testTreatmentPlan" :loading="testingItems.treatment">
          防治方案
        </el-button>
        <el-button @click="testPesticideManagement" :loading="testingItems.pesticide">
          药剂管理
        </el-button>
      </div>

      <!-- 测试结果 -->
      <div class="test-results">
        <h4>测试结果</h4>
        <el-timeline>
          <el-timeline-item
            v-for="result in testResults"
            :key="result.timestamp"
            :timestamp="formatTime(result.timestamp)"
            :type="result.success ? 'success' : 'danger'"
            :icon="result.success ? 'Check' : 'Close'"
          >
            <div class="result-item">
              <div class="result-header">
                <span class="result-name">{{ result.testName }}</span>
                <el-tag :type="result.success ? 'success' : 'danger'" size="small">
                  {{ result.success ? '成功' : '失败' }}
                </el-tag>
              </div>

              <div v-if="!result.success" class="result-error">
                <el-alert
                  :title="getErrorMessage(result.data)"
                  type="error"
                  :closable="false"
                  show-icon
                />
              </div>

              <div v-else class="result-success">
                <el-collapse>
                  <el-collapse-item title="查看响应数据" :name="result.timestamp">
                    <pre class="response-data">{{ formatResponse(result.data) }}</pre>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>

        <div v-if="testResults.length === 0" class="no-results">
          <el-empty description="暂无测试结果" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Delete, Check, Close } from '@element-plus/icons-vue'
import { ApiTester } from '@/utils/apiTest'

// 响应式数据
const testing = ref(false)
const testResults = ref([])
const apiTester = new ApiTester()

const testingItems = reactive({
  health: false,
  auth: false,
  pest: false,
  treatment: false,
  pesticide: false
})

// 计算属性
const summary = computed(() => {
  const total = testResults.value.length
  const success = testResults.value.filter((r) => r.success).length
  const failed = total - success
  const successRate = total > 0 ? ((success / total) * 100).toFixed(1) : 0

  return { total, success, failed, successRate }
})

// 方法
const runAllTests = async () => {
  testing.value = true
  testResults.value = []

  try {
    const results = await apiTester.runAllTests()
    testResults.value = results

    const summary = apiTester.getSummary()
    ElMessage.success(`测试完成: ${summary.success}/${summary.total} 通过`)
  } catch (error) {
    console.error('测试执行失败:', error)
    ElMessage.error('测试执行失败')
  } finally {
    testing.value = false
  }
}

const testHealthCheck = async () => {
  testingItems.health = true
  try {
    await apiTester.testHealthCheck()
    updateResults()
  } finally {
    testingItems.health = false
  }
}

const testAuth = async () => {
  testingItems.auth = true
  try {
    await apiTester.testAuth()
    updateResults()
  } finally {
    testingItems.auth = false
  }
}

const testPestIdentification = async () => {
  testingItems.pest = true
  try {
    await apiTester.testPestIdentification()
    updateResults()
  } finally {
    testingItems.pest = false
  }
}

const testTreatmentPlan = async () => {
  testingItems.treatment = true
  try {
    await apiTester.testTreatmentPlan()
    updateResults()
  } finally {
    testingItems.treatment = false
  }
}

const testPesticideManagement = async () => {
  testingItems.pesticide = true
  try {
    await apiTester.testPesticideManagement()
    updateResults()
  } finally {
    testingItems.pesticide = false
  }
}

const updateResults = () => {
  testResults.value = [...apiTester.results]
}

const clearResults = () => {
  testResults.value = []
  apiTester.results = []
  ElMessage.success('结果已清空')
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleString('zh-CN')
}

const getErrorMessage = (error) => {
  if (error?.response?.data?.message) {
    return error.response.data.message
  }
  if (error?.message) {
    return error.message
  }
  return '未知错误'
}

const formatResponse = (data) => {
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}
</script>

<style lang="scss" scoped>
.api-test {
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

  .test-summary {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;

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
        font-size: 24px;
        font-weight: bold;
        color: #2c3e50;
      }
    }
  }

  .test-actions {
    margin-bottom: 30px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;

    .el-button {
      margin-right: 10px;
      margin-bottom: 10px;
    }
  }

  .test-results {
    h4 {
      color: #2c3e50;
      margin-bottom: 20px;
    }

    .result-item {
      .result-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

        .result-name {
          font-weight: 500;
          color: #2c3e50;
        }
      }

      .result-error {
        margin-top: 10px;
      }

      .result-success {
        margin-top: 10px;

        .response-data {
          background-color: #f8f9fa;
          padding: 15px;
          border-radius: 4px;
          font-size: 12px;
          line-height: 1.5;
          max-height: 300px;
          overflow-y: auto;
        }
      }
    }

    .no-results {
      text-align: center;
      padding: 40px 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .api-test {
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

    .test-actions {
      .el-button {
        width: 100%;
        margin-right: 0;
      }
    }
  }
}
</style>
