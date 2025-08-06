<template>
  <div 
    class="info-popup"
    :style="popupStyle"
  >
    <el-card>
      <template #header>
        <div class="popup-header">
          <span>{{ feature.name || '详细信息' }}</span>
          <el-button 
            type="text" 
            size="small" 
            @click="$emit('close')"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>
      
      <div class="popup-content">
        <!-- 森林资源信息 -->
        <div v-if="feature.type === 'forest'" class="forest-info">
          <div class="info-item">
            <span class="label">资源类型：</span>
            <span class="value">{{ getForestTypeText(feature.forestType) }}</span>
          </div>
          <div class="info-item">
            <span class="label">面积：</span>
            <span class="value">{{ feature.area }} 公顷</span>
          </div>
          <div class="info-item">
            <span class="label">健康状态：</span>
            <el-tag :type="getHealthStatusType(feature.healthStatus)">
              {{ getHealthStatusText(feature.healthStatus) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">管理员：</span>
            <span class="value">{{ feature.manager }}</span>
          </div>
        </div>
        
        <!-- 病虫害信息 -->
        <div v-else-if="feature.type === 'pest'" class="pest-info">
          <div class="info-item">
            <span class="label">病虫害类型：</span>
            <span class="value">{{ feature.pestType }}</span>
          </div>
          <div class="info-item">
            <span class="label">严重程度：</span>
            <el-tag :type="getSeverityType(feature.severity)">
              {{ getSeverityText(feature.severity) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">发现日期：</span>
            <span class="value">{{ formatDate(feature.discoveryDate) }}</span>
          </div>
          <div class="info-item">
            <span class="label">影响面积：</span>
            <span class="value">{{ feature.affectedArea }} 公顷</span>
          </div>
        </div>
        
        <!-- 防治区域信息 -->
        <div v-else-if="feature.type === 'treatment'" class="treatment-info">
          <div class="info-item">
            <span class="label">区域名称：</span>
            <span class="value">{{ feature.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">防治状态：</span>
            <el-tag :type="getTreatmentStatusType(feature.status)">
              {{ getTreatmentStatusText(feature.status) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">防治进度：</span>
            <el-progress :percentage="feature.progress" :stroke-width="6" />
          </div>
          <div class="info-item">
            <span class="label">负责团队：</span>
            <span class="value">{{ feature.assignedTeam }}</span>
          </div>
        </div>
        
        <!-- 坐标信息 -->
        <div class="coordinates-info">
          <div class="info-item">
            <span class="label">经度：</span>
            <span class="value">{{ feature.longitude?.toFixed(6) }}</span>
          </div>
          <div class="info-item">
            <span class="label">纬度：</span>
            <span class="value">{{ feature.latitude?.toFixed(6) }}</span>
          </div>
        </div>
      </div>
      
      <div class="popup-actions">
        <el-button size="small" @click="$emit('edit', feature)">
          编辑
        </el-button>
        <el-button size="small" type="primary" @click="viewDetails">
          详情
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Close } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  feature: {
    type: Object,
    required: true
  },
  position: {
    type: Array,
    required: true
  }
})

// Emits
const emit = defineEmits(['close', 'edit'])

// 计算弹窗位置
const popupStyle = computed(() => {
  return {
    left: `${props.position[0]}px`,
    top: `${props.position[1]}px`
  }
})

// 工具方法
const getForestTypeText = (type) => {
  const typeMap = {
    natural_forest: '天然林',
    artificial_forest: '人工林',
    economic_forest: '经济林',
    protection_forest: '防护林'
  }
  return typeMap[type] || '未知'
}

const getHealthStatusType = (status) => {
  const typeMap = {
    healthy: 'success',
    sub_healthy: 'warning',
    diseased: 'danger',
    severely_diseased: 'danger'
  }
  return typeMap[status] || 'info'
}

const getHealthStatusText = (status) => {
  const textMap = {
    healthy: '健康',
    sub_healthy: '亚健康',
    diseased: '病害',
    severely_diseased: '严重病害'
  }
  return textMap[status] || '未知'
}

const getSeverityType = (severity) => {
  const typeMap = {
    low: 'success',
    medium: 'warning',
    high: 'danger',
    critical: 'danger'
  }
  return typeMap[severity] || 'info'
}

const getSeverityText = (severity) => {
  const textMap = {
    low: '轻微',
    medium: '中等',
    high: '严重',
    critical: '极严重'
  }
  return textMap[severity] || '未知'
}

const getTreatmentStatusType = (status) => {
  const typeMap = {
    planned: 'info',
    active: 'warning',
    completed: 'success',
    paused: 'danger'
  }
  return typeMap[status] || 'info'
}

const getTreatmentStatusText = (status) => {
  const textMap = {
    planned: '计划中',
    active: '进行中',
    completed: '已完成',
    paused: '已暂停'
  }
  return textMap[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const viewDetails = () => {
  // 可以触发详情查看事件
  emit('edit', props.feature)
}
</script>

<style lang="scss" scoped>
.info-popup {
  position: absolute;
  z-index: 1001;
  max-width: 300px;
  min-width: 250px;
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    span {
      font-weight: 600;
      color: #2c3e50;
    }
  }
  
  .popup-content {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 4px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .label {
        color: #606266;
        font-size: 13px;
        min-width: 80px;
      }
      
      .value {
        color: #2c3e50;
        font-size: 13px;
        font-weight: 500;
        text-align: right;
      }
    }
    
    .coordinates-info {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid #f0f0f0;
    }
  }
  
  .popup-actions {
    margin-top: 12px;
    display: flex;
    gap: 8px;
    
    .el-button {
      flex: 1;
    }
  }
}
</style>