<template>
  <div class="task-test">
    <el-card>
      <template #header>
        <h3>防治任务管理功能测试</h3>
      </template>

      <div class="test-section">
        <h4>功能测试</h4>
        <el-space wrap>
          <el-button type="primary" @click="testCreateTask">测试创建任务</el-button>
          <el-button type="success" @click="testLoadTasks">测试加载任务</el-button>
          <el-button type="warning" @click="testUpdateTask">测试更新任务</el-button>
          <el-button type="danger" @click="testDeleteTask">测试删除任务</el-button>
        </el-space>
      </div>

      <div class="test-results" v-if="testResults.length > 0">
        <h4>测试结果</h4>
        <el-timeline>
          <el-timeline-item
            v-for="(result, index) in testResults"
            :key="index"
            :timestamp="result.time"
            :type="result.success ? 'success' : 'danger'"
          >
            <p>{{ result.message }}</p>
            <pre v-if="result.data">{{ JSON.stringify(result.data, null, 2) }}</pre>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getTreatmentTasks,
  createTreatmentTask,
  updateTreatmentTask,
  deleteTreatmentTask,
  assignTreatmentTask,
  batchCreateTreatmentTasks
} from '@/api/treatmentTask'

const testResults = ref([])

const addTestResult = (message, success = true, data = null) => {
  testResults.value.unshift({
    message,
    success,
    data,
    time: new Date().toLocaleString()
  })
}

const testCreateTask = async () => {
  try {
    const taskData = {
      taskName: '测试任务-' + Date.now(),
      description: '这是一个测试任务',
      assignedTo: 'user001',
      priority: 'HIGH',
      scheduledTime: new Date().toISOString(),
      targetArea: '测试区域'
    }

    const response = await createTreatmentTask('test-plan-id', taskData)
    addTestResult('创建任务成功', true, response.data)
    ElMessage.success('创建任务测试成功')
  } catch (error) {
    addTestResult('创建任务失败: ' + error.message, false, error)
    ElMessage.error('创建任务测试失败')
  }
}

const testLoadTasks = async () => {
  try {
    const response = await getTreatmentTasks('test-plan-id')
    addTestResult('加载任务成功', true, response.data)
    ElMessage.success('加载任务测试成功')
  } catch (error) {
    addTestResult('加载任务失败: ' + error.message, false, error)
    ElMessage.error('加载任务测试失败')
  }
}

const testUpdateTask = async () => {
  try {
    const taskData = {
      taskName: '更新后的任务名称',
      status: 'IN_PROGRESS',
      executionNotes: '任务已开始执行'
    }

    const response = await updateTreatmentTask('test-task-id', taskData)
    addTestResult('更新任务成功', true, response.data)
    ElMessage.success('更新任务测试成功')
  } catch (error) {
    addTestResult('更新任务失败: ' + error.message, false, error)
    ElMessage.error('更新任务测试失败')
  }
}

const testDeleteTask = async () => {
  try {
    await deleteTreatmentTask('test-task-id')
    addTestResult('删除任务成功', true)
    ElMessage.success('删除任务测试成功')
  } catch (error) {
    addTestResult('删除任务失败: ' + error.message, false, error)
    ElMessage.error('删除任务测试失败')
  }
}
</script>

<style lang="scss" scoped>
.task-test {
  padding: 20px;

  .test-section {
    margin-bottom: 30px;

    h4 {
      margin-bottom: 15px;
      color: #2c3e50;
    }
  }

  .test-results {
    h4 {
      margin-bottom: 15px;
      color: #2c3e50;
    }

    pre {
      background-color: #f5f5f5;
      padding: 10px;
      border-radius: 4px;
      font-size: 12px;
      max-height: 200px;
      overflow-y: auto;
    }
  }
}
</style>
