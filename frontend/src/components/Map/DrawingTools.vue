<template>
  <div class="drawing-tools">
    <el-card class="tools-panel">
      <template #header>
        <div class="tools-header">
          <span>绘制工具</span>
          <el-button 
            type="text" 
            size="small" 
            @click="$emit('draw-cancel')"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>
      
      <div class="tools-content">
        <div class="mode-info">
          <p>当前模式: <strong>{{ getModeText(mode) }}</strong></p>
          <p class="instruction">{{ getInstruction(mode) }}</p>
        </div>
        
        <div class="tools-actions">
          <el-button 
            size="small" 
            type="success" 
            @click="completeDraw"
            :disabled="!canComplete"
          >
            完成绘制
          </el-button>
          <el-button 
            size="small" 
            @click="cancelDraw"
          >
            取消
          </el-button>
        </div>
        
        <!-- 绘制状态 -->
        <div v-if="drawingState.points.length > 0" class="drawing-state">
          <p>已绘制点数: {{ drawingState.points.length }}</p>
          <p v-if="mode === 'polygon' && drawingState.area">
            当前面积: {{ drawingState.area.toFixed(2) }} 平方米
          </p>
          <p v-if="mode === 'line' && drawingState.distance">
            当前距离: {{ drawingState.distance.toFixed(2) }} 米
          </p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  mode: {
    type: String,
    required: true,
    validator: (value) => ['polygon', 'line', 'point', 'circle'].includes(value)
  }
})

// Emits
const emit = defineEmits(['draw-complete', 'draw-cancel'])

// 响应式数据
const drawingState = ref({
  points: [],
  area: 0,
  distance: 0,
  isDrawing: false
})

// 计算属性
const canComplete = computed(() => {
  switch (props.mode) {
    case 'polygon':
      return drawingState.value.points.length >= 3
    case 'line':
      return drawingState.value.points.length >= 2
    case 'point':
      return drawingState.value.points.length >= 1
    case 'circle':
      return drawingState.value.points.length >= 2
    default:
      return false
  }
})

// 方法
const getModeText = (mode) => {
  const modeMap = {
    polygon: '多边形绘制',
    line: '线段绘制',
    point: '点标记',
    circle: '圆形绘制'
  }
  return modeMap[mode] || '未知模式'
}

const getInstruction = (mode) => {
  const instructionMap = {
    polygon: '点击地图绘制多边形，至少需要3个点',
    line: '点击地图绘制线段，至少需要2个点',
    point: '点击地图标记一个点',
    circle: '点击地图中心点，再点击边缘确定半径'
  }
  return instructionMap[mode] || ''
}

const completeDraw = () => {
  if (!canComplete.value) return
  
  const geometry = {
    type: props.mode,
    coordinates: drawingState.value.points,
    area: drawingState.value.area,
    distance: drawingState.value.distance
  }
  
  emit('draw-complete', geometry)
  resetDrawing()
}

const cancelDraw = () => {
  emit('draw-cancel')
  resetDrawing()
}

const resetDrawing = () => {
  drawingState.value = {
    points: [],
    area: 0,
    distance: 0,
    isDrawing: false
  }
}

const addPoint = (point) => {
  drawingState.value.points.push(point)
  updateMeasurements()
}

const updateMeasurements = () => {
  const points = drawingState.value.points
  
  if (props.mode === 'polygon' && points.length >= 3) {
    // 计算多边形面积（简化实现）
    drawingState.value.area = calculatePolygonArea(points)
  } else if (props.mode === 'line' && points.length >= 2) {
    // 计算线段总长度
    drawingState.value.distance = calculateLineDistance(points)
  }
}

const calculatePolygonArea = (points) => {
  // 简化的面积计算，实际应该使用更精确的地理计算
  if (points.length < 3) return 0
  
  let area = 0
  for (let i = 0; i < points.length; i++) {
    const j = (i + 1) % points.length
    area += points[i][0] * points[j][1]
    area -= points[j][0] * points[i][1]
  }
  return Math.abs(area) / 2 * 111000 * 111000 // 粗略转换为平方米
}

const calculateLineDistance = (points) => {
  // 简化的距离计算
  let distance = 0
  for (let i = 1; i < points.length; i++) {
    const dx = points[i][0] - points[i-1][0]
    const dy = points[i][1] - points[i-1][1]
    distance += Math.sqrt(dx * dx + dy * dy) * 111000 // 粗略转换为米
  }
  return distance
}

// 监听模式变化
watch(() => props.mode, () => {
  resetDrawing()
})

// 暴露方法
defineExpose({
  addPoint,
  resetDrawing,
  getDrawingState: () => drawingState.value
})
</script>

<style lang="scss" scoped>
.drawing-tools {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
  
  .tools-panel {
    width: 280px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    
    .tools-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        font-weight: 600;
        color: #2c3e50;
      }
    }
    
    .tools-content {
      .mode-info {
        margin-bottom: 16px;
        
        p {
          margin: 4px 0;
          font-size: 13px;
          
          &.instruction {
            color: #606266;
            font-style: italic;
          }
        }
      }
      
      .tools-actions {
        display: flex;
        gap: 8px;
        margin-bottom: 16px;
        
        .el-button {
          flex: 1;
        }
      }
      
      .drawing-state {
        padding: 12px;
        background: #f8f9fa;
        border-radius: 4px;
        
        p {
          margin: 4px 0;
          font-size: 12px;
          color: #606266;
        }
      }
    }
  }
}
</style>