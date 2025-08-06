<template>
  <div class="system-settings">
    <el-row :gutter="20">
      <!-- 基础设置 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>基础设置</span>
            </div>
          </template>

          <el-form :model="basicForm" :rules="basicRules" ref="basicFormRef" label-width="120px">
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="basicForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>

            <el-form-item label="系统版本" prop="systemVersion">
              <el-input v-model="basicForm.systemVersion" placeholder="请输入系统版本" />
            </el-form-item>

            <el-form-item label="系统描述" prop="systemDescription">
              <el-input
                v-model="basicForm.systemDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>

            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="basicForm.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>

            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="basicForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings" :loading="basicLoading">
                保存设置
              </el-button>
              <el-button @click="resetBasicForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 安全设置 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </div>
          </template>

          <el-form
            :model="securityForm"
            :rules="securityRules"
            ref="securityFormRef"
            label-width="120px"
          >
            <el-form-item label="密码最小长度" prop="minPasswordLength">
              <el-input-number
                v-model="securityForm.minPasswordLength"
                :min="6"
                :max="20"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="登录失败限制" prop="maxLoginAttempts">
              <el-input-number
                v-model="securityForm.maxLoginAttempts"
                :min="3"
                :max="10"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="会话超时时间" prop="sessionTimeout">
              <el-input-number
                v-model="securityForm.sessionTimeout"
                :min="30"
                :max="480"
                style="width: 100%"
              >
                <template #append>分钟</template>
              </el-input-number>
            </el-form-item>

            <el-form-item label="强制密码复杂度">
              <el-switch v-model="securityForm.enforcePasswordComplexity" />
            </el-form-item>

            <el-form-item label="启用双因子认证">
              <el-switch v-model="securityForm.enableTwoFactor" />
            </el-form-item>

            <el-form-item label="记录登录日志">
              <el-switch v-model="securityForm.enableLoginLog" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveSecuritySettings" :loading="securityLoading">
                保存设置
              </el-button>
              <el-button @click="resetSecurityForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 邮件设置 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Message /></el-icon>
              <span>邮件设置</span>
            </div>
          </template>

          <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef" label-width="120px">
            <el-form-item label="SMTP服务器" prop="smtpHost">
              <el-input v-model="emailForm.smtpHost" placeholder="请输入SMTP服务器地址" />
            </el-form-item>

            <el-form-item label="SMTP端口" prop="smtpPort">
              <el-input-number
                v-model="emailForm.smtpPort"
                :min="1"
                :max="65535"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="发送邮箱" prop="senderEmail">
              <el-input v-model="emailForm.senderEmail" placeholder="请输入发送邮箱" />
            </el-form-item>

            <el-form-item label="邮箱密码" prop="senderPassword">
              <el-input
                v-model="emailForm.senderPassword"
                type="password"
                placeholder="请输入邮箱密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="启用SSL">
              <el-switch v-model="emailForm.enableSSL" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveEmailSettings" :loading="emailLoading">
                保存设置
              </el-button>
              <el-button @click="testEmailConnection" :loading="testEmailLoading">
                测试连接
              </el-button>
              <el-button @click="resetEmailForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 数据备份 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><FolderOpened /></el-icon>
              <span>数据备份</span>
            </div>
          </template>

          <el-form :model="backupForm" label-width="120px">
            <el-form-item label="自动备份">
              <el-switch v-model="backupForm.autoBackup" />
            </el-form-item>

            <el-form-item label="备份频率" v-if="backupForm.autoBackup">
              <el-select v-model="backupForm.backupFrequency" style="width: 100%">
                <el-option label="每天" value="daily" />
                <el-option label="每周" value="weekly" />
                <el-option label="每月" value="monthly" />
              </el-select>
            </el-form-item>

            <el-form-item label="备份时间" v-if="backupForm.autoBackup">
              <el-time-picker
                v-model="backupForm.backupTime"
                format="HH:mm"
                value-format="HH:mm"
                placeholder="选择备份时间"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="保留备份数量">
              <el-input-number
                v-model="backupForm.keepBackupCount"
                :min="1"
                :max="30"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveBackupSettings" :loading="backupLoading">
                保存设置
              </el-button>
              <el-button type="success" @click="createBackup" :loading="createBackupLoading">
                立即备份
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 备份历史 -->
          <div class="backup-history">
            <h4>备份历史</h4>
            <el-table :data="backupHistory" size="small" max-height="200">
              <el-table-column prop="filename" label="文件名" show-overflow-tooltip />
              <el-table-column prop="size" label="大小" width="80" />
              <el-table-column prop="createTime" label="创建时间" width="120" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="text" size="small" @click="downloadBackup(row)">
                    下载
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    @click="deleteBackup(row)"
                    style="color: #f56c6c"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>系统信息</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">服务器时间</div>
                <div class="info-value">{{ systemInfo.serverTime }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">运行时间</div>
                <div class="info-value">{{ systemInfo.uptime }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">系统版本</div>
                <div class="info-value">{{ systemInfo.version }}</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">CPU使用率</div>
                <div class="info-value">
                  <el-progress
                    :percentage="systemInfo.cpuUsage"
                    :color="getProgressColor(systemInfo.cpuUsage)"
                  />
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">内存使用率</div>
                <div class="info-value">
                  <el-progress
                    :percentage="systemInfo.memoryUsage"
                    :color="getProgressColor(systemInfo.memoryUsage)"
                  />
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">磁盘使用率</div>
                <div class="info-value">
                  <el-progress
                    :percentage="systemInfo.diskUsage"
                    :color="getProgressColor(systemInfo.diskUsage)"
                  />
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Lock, Message, FolderOpened, Monitor } from '@element-plus/icons-vue'

// 响应式数据
const basicLoading = ref(false)
const securityLoading = ref(false)
const emailLoading = ref(false)
const testEmailLoading = ref(false)
const backupLoading = ref(false)
const createBackupLoading = ref(false)

// 表单数据
const basicForm = reactive({
  systemName: '森林病虫害防治管理系统',
  systemVersion: 'v1.0.0',
  systemDescription: '专业的森林病虫害防治管理系统，提供病虫害识别、防治方案管理、效果评估等功能',
  contactEmail: 'admin@forest-system.com',
  contactPhone: '400-123-4567'
})

const securityForm = reactive({
  minPasswordLength: 8,
  maxLoginAttempts: 5,
  sessionTimeout: 120,
  enforcePasswordComplexity: true,
  enableTwoFactor: false,
  enableLoginLog: true
})

const emailForm = reactive({
  smtpHost: 'smtp.qq.com',
  smtpPort: 587,
  senderEmail: '',
  senderPassword: '',
  enableSSL: true
})

const backupForm = reactive({
  autoBackup: true,
  backupFrequency: 'daily',
  backupTime: '02:00',
  keepBackupCount: 7
})

const systemInfo = reactive({
  serverTime: '',
  uptime: '15天 8小时 32分钟',
  version: 'v1.0.0',
  cpuUsage: 35,
  memoryUsage: 68,
  diskUsage: 42
})

const backupHistory = ref([
  {
    id: 1,
    filename: 'backup_20240120_020000.sql',
    size: '15.2MB',
    createTime: '2024-01-20 02:00'
  },
  {
    id: 2,
    filename: 'backup_20240119_020000.sql',
    size: '14.8MB',
    createTime: '2024-01-19 02:00'
  },
  {
    id: 3,
    filename: 'backup_20240118_020000.sql',
    size: '14.5MB',
    createTime: '2024-01-18 02:00'
  }
])

// 表单引用
const basicFormRef = ref()
const securityFormRef = ref()
const emailFormRef = ref()

// 验证规则
const basicRules = {
  systemName: [{ required: true, message: '请输入系统名称', trigger: 'blur' }],
  systemVersion: [{ required: true, message: '请输入系统版本', trigger: 'blur' }],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const securityRules = {
  minPasswordLength: [{ required: true, message: '请设置密码最小长度', trigger: 'blur' }],
  maxLoginAttempts: [{ required: true, message: '请设置登录失败限制', trigger: 'blur' }],
  sessionTimeout: [{ required: true, message: '请设置会话超时时间', trigger: 'blur' }]
}

const emailRules = {
  smtpHost: [{ required: true, message: '请输入SMTP服务器地址', trigger: 'blur' }],
  smtpPort: [{ required: true, message: '请输入SMTP端口', trigger: 'blur' }],
  senderEmail: [
    { required: true, message: '请输入发送邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  senderPassword: [{ required: true, message: '请输入邮箱密码', trigger: 'blur' }]
}

// 定时器
let systemInfoTimer = null

// 方法
const getProgressColor = (percentage) => {
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#e6a23c'
  return '#f56c6c'
}

const updateSystemTime = () => {
  systemInfo.serverTime = new Date().toLocaleString()
}

const saveBasicSettings = async () => {
  if (!basicFormRef.value) return

  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      basicLoading.value = true
      try {
        // 这里应该调用API保存基础设置
        // await saveBasicSettingsApi(basicForm)

        ElMessage.success('基础设置保存成功')
      } catch (error) {
        console.error('保存基础设置失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        basicLoading.value = false
      }
    }
  })
}

const resetBasicForm = () => {
  if (basicFormRef.value) {
    basicFormRef.value.resetFields()
  }
}

const saveSecuritySettings = async () => {
  if (!securityFormRef.value) return

  await securityFormRef.value.validate(async (valid) => {
    if (valid) {
      securityLoading.value = true
      try {
        // 这里应该调用API保存安全设置
        // await saveSecuritySettingsApi(securityForm)

        ElMessage.success('安全设置保存成功')
      } catch (error) {
        console.error('保存安全设置失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        securityLoading.value = false
      }
    }
  })
}

const resetSecurityForm = () => {
  if (securityFormRef.value) {
    securityFormRef.value.resetFields()
  }
}

const saveEmailSettings = async () => {
  if (!emailFormRef.value) return

  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      emailLoading.value = true
      try {
        // 这里应该调用API保存邮件设置
        // await saveEmailSettingsApi(emailForm)

        ElMessage.success('邮件设置保存成功')
      } catch (error) {
        console.error('保存邮件设置失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        emailLoading.value = false
      }
    }
  })
}

const testEmailConnection = async () => {
  testEmailLoading.value = true
  try {
    // 这里应该调用API测试邮件连接
    // await testEmailConnectionApi(emailForm)

    ElMessage.success('邮件连接测试成功')
  } catch (error) {
    console.error('邮件连接测试失败:', error)
    ElMessage.error('邮件连接测试失败，请检查配置')
  } finally {
    testEmailLoading.value = false
  }
}

const resetEmailForm = () => {
  if (emailFormRef.value) {
    emailFormRef.value.resetFields()
  }
}

const saveBackupSettings = async () => {
  backupLoading.value = true
  try {
    // 这里应该调用API保存备份设置
    // await saveBackupSettingsApi(backupForm)

    ElMessage.success('备份设置保存成功')
  } catch (error) {
    console.error('保存备份设置失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    backupLoading.value = false
  }
}

const createBackup = async () => {
  createBackupLoading.value = true
  try {
    // 这里应该调用API创建备份
    // await createBackupApi()

    ElMessage.success('备份创建成功')
    // 刷新备份历史
    loadBackupHistory()
  } catch (error) {
    console.error('创建备份失败:', error)
    ElMessage.error('创建备份失败，请重试')
  } finally {
    createBackupLoading.value = false
  }
}

const downloadBackup = (backup) => {
  // 这里应该实现备份文件下载
  ElMessage.info('开始下载备份文件')
}

const deleteBackup = async (backup) => {
  try {
    await ElMessageBox.confirm(`确定要删除备份文件 ${backup.filename} 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除备份
    // await deleteBackupApi(backup.id)

    ElMessage.success('备份文件删除成功')
    loadBackupHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除备份失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const loadBackupHistory = async () => {
  try {
    // 这里应该调用API获取备份历史
    // const response = await getBackupHistoryApi()
    // backupHistory.value = response.data
  } catch (error) {
    console.error('加载备份历史失败:', error)
  }
}

const loadSystemInfo = async () => {
  try {
    // 这里应该调用API获取系统信息
    // const response = await getSystemInfoApi()
    // Object.assign(systemInfo, response.data)
  } catch (error) {
    console.error('加载系统信息失败:', error)
  }
}

// 生命周期
onMounted(() => {
  updateSystemTime()
  systemInfoTimer = setInterval(updateSystemTime, 1000)
  loadSystemInfo()
})

onUnmounted(() => {
  if (systemInfoTimer) {
    clearInterval(systemInfoTimer)
  }
})
</script>

<style lang="scss" scoped>
.system-settings {
  .card-header {
    display: flex;
    align-items: center;
    font-weight: 600;
    font-size: 16px;

    .el-icon {
      margin-right: 8px;
      color: #409eff;
    }
  }

  .backup-history {
    margin-top: 20px;

    h4 {
      color: #2c3e50;
      font-size: 14px;
      margin-bottom: 10px;
    }
  }

  .info-item {
    text-align: center;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;

    .info-label {
      font-size: 14px;
      color: #606266;
      margin-bottom: 10px;
    }

    .info-value {
      font-size: 16px;
      font-weight: 600;
      color: #2c3e50;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .system-settings {
    .el-col {
      margin-bottom: 20px;
    }

    .info-item {
      padding: 15px;

      .info-label {
        font-size: 12px;
      }

      .info-value {
        font-size: 14px;
      }
    }
  }
}
</style>
