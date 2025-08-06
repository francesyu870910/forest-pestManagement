<template>
  <div class="layer-control">
    <el-card class="control-panel">
      <template #header>
        <span>图层控制</span>
      </template>
      
      <!-- 底图切换 -->
      <div class="base-layers">
        <h4>底图</h4>
        <el-radio-group v-model="activeBaseLayer" @change="changeBaseLayer">
          <el-radio value="satellite">卫星图</el-radio>
          <el-radio value="terrain">地形图</el-radio>
          <el-radio value="street">街道图</el-radio>
        </el-radio-group>
      </div>
      
      <!-- 数据图层 -->
      <div class="data-layers">
        <h4>数据图层</h4>
        <el-checkbox-group v-model="activeLayers" @change="toggleLayers">
          <el-checkbox value="forest">森林资源</el-checkbox>
          <el-checkbox value="pest">病虫害分布</el-checkbox>
          <el-checkbox value="treatment">防治区域</el-checkbox>
          <el-checkbox value="monitoring">监测点位</el-checkbox>
        </el-checkbox-group>
      </div>
      
      <!-- 图层透明度 -->
      <div class="layer-opacity">
        <h4>透明度</h4>
        <el-slider 
          v-model="layerOpacity" 
          :min="0" 
          :max="100"
          @change="updateOpacity"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

// 响应式数据
const activeBaseLayer = ref('satellite')
const activeLayers = ref(['forest'])
const layerOpacity = ref(100)

// Emits
const emit = defineEmits(['layer-change', 'opacity-change'])

// 方法
const changeBaseLayer = (layer) => {
  emit('layer-change', 'base', layer)
}

const toggleLayers = (layers) => {
  emit('layer-change', 'data', layers)
}

const updateOpacity = (opacity) => {
  emit('opacity-change', 'all', opacity / 100)
}

// 监听变化
watch(activeLayers, (newLayers) => {
  emit('layer-change', 'data', newLayers)
}, { deep: true })
</script>

<style lang="scss" scoped>
.layer-control {
  .control-panel {
    width: 250px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    
    h4 {
      margin: 0 0 10px 0;
      color: #2c3e50;
      font-size: 14px;
      font-weight: 600;
    }
    
    .base-layers,
    .data-layers,
    .layer-opacity {
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .el-radio-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    
    .el-checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }
}
</style>