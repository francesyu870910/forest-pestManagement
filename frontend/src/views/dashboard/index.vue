<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>仪表盘</span>
        </div>
      </template>

      <div class="welcome-section">
        <h2>欢迎使用森林病虫害防治数字化管理系统</h2>
        <p>今天是 {{ currentDate }}，{{ getGreeting() }}</p>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-section">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon size="32" color="#f56c6c"><Search /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">156</div>
                <div class="stats-label">病虫害识别</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon size="32" color="#67c23a"><Document /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">89</div>
                <div class="stats-label">防治方案</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon size="32" color="#e6a23c"><Warning /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">12</div>
                <div class="stats-label">预警信息</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon">
                <el-icon size="32" color="#409eff"><Sunny /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">45</div>
                <div class="stats-label">森林区域</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 快速操作 -->
      <div class="quick-actions">
        <h3>快速操作</h3>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-button type="primary" @click="navigateTo('/pest/identification')" block>
              病虫害识别
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="success" @click="navigateTo('/pest/treatment-plan')" block>
              防治方案
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="warning" @click="navigateTo('/warning')" block> 预警中心 </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="info" @click="navigateTo('/forest/resource')" block>
              森林资源
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Document, Warning, Sunny } from '@element-plus/icons-vue'

const router = useRouter()

// 计算属性
const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 方法
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
}

const navigateTo = async (path) => {
  try {
    console.log('准备导航到:', path)

    // 检查路由是否存在
    const route = router.resolve(path)
    console.log('路由解析结果:', route)

    if (route.name === 'NotFound') {
      ElMessage.error(`路由 ${path} 不存在`)
      return
    }

    await router.push(path)
    console.log('导航成功')
  } catch (error) {
    console.error('导航错误:', error)
    ElMessage.error(`页面跳转失败: ${error.message}`)
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 18px;
  }

  .welcome-section {
    text-align: center;
    margin-bottom: 30px;

    h2 {
      color: #2c3e50;
      margin-bottom: 10px;
    }

    p {
      color: #7f8c8d;
      font-size: 16px;
    }
  }

  .stats-section {
    margin-bottom: 30px;

    .stats-card {
      .stats-content {
        display: flex;
        align-items: center;

        .stats-icon {
          margin-right: 15px;
        }

        .stats-info {
          .stats-number {
            font-size: 24px;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 5px;
          }

          .stats-label {
            font-size: 14px;
            color: #7f8c8d;
          }
        }
      }
    }
  }

  .quick-actions {
    h3 {
      color: #2c3e50;
      margin-bottom: 20px;
    }

    .el-button {
      height: 50px;
      font-size: 14px;
    }
  }
}
</style>
