<template>
  <div class="measure-tool">
    <el-button-group>
      <el-button 
        :type="measureMode === 'distance' ? 'primary' : 'default'"
        @click="startMeasure('distance')"
        size="small"
      >
        <el-icon><Position /></el-icon>
        距离
      </el-button>
      <el-button 
        :type="measureMode === 'area' ? 'primary' : 'default'"
        @click="startMeasure('area')"
        size="small"
      >
        <el-icon><Menu /></el-icon>
        面积
      </el-button>
      <el-button @click="clearMeasure" size="small">
        <el-icon><Delete /></el-icon>
        清除
      </el-button>
    </el-button-group>
    
    <!-- 测量结果显示 -->
    <div v-if="measureResult" class="measure-result">
      <el-card>
        <p><strong>测量结果:</strong></p>
        <p v-if="measureMode === 'distance'">
          距离: {{ measureResult.distance }} 米
        </p>
        <p v-if="measureMode === 'area'">
          面积: {{ measureResult.area }} 平方米
        </p>
        <p v-if="measureResult.perimeter">
          周长: {{ measureResult.perimeter }} 米
        </p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Position, Menu, Delete } from '@element-plus/icons-vue'

// 响应式数据
const measureMode = ref(null)
const measureResult = ref(null)

// Emits
const emit = defineEmits(['measure-start', 'measure-complete'])

// 方法
const startMeasure = (mode) => {
  measureMode.value = mode
  measureResult.value = null
  emit('measure-start', mode)
}

const clearMeasure = () => {
  measureMode.value = null
  measureResult.value = null
}

const setResult = (result) => {
  measureResult.value = result
  emit('measure-complete', result)
}

// 暴露方法
defineExpose({
  setResult,
  clearMeasure
})
</script>

<style lang="scss" scoped>
.measure-tool {
  position: relative;
  
  .measure-result {
    position: absolute;
    top: 50px;
    right: 0;
    width: 200px;
    z-index: 1001;
    
    .el-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      
      p {
        margin: 4px 0;
        font-size: 13px;
        
        &:first-child {
          font-weight: 600;
          margin-bottom: 8px;
        }
      }
    }
  }
}
</style>