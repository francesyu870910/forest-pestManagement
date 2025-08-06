<template>
  <div class="map-test">
    <el-card>
      <template #header>
        <span>地图测试页面</span>
      </template>
      
      <div class="test-controls">
        <el-button @click="showTestMap = true">显示测试地图</el-button>
        <el-button @click="logTestData">打印测试数据</el-button>
      </div>
      
      <div v-if="showTestMap" class="test-map-container">
        <ForestMap
          :center="[116.397428, 39.90923]"
          :zoom="12"
          :forest-data="testForestData"
          height="500px"
          @marker-click="handleMarkerClick"
          @map-ready="handleMapReady"
          @error="handleMapError"
        />
      </div>
      
      <div class="test-info">
        <h4>测试数据:</h4>
        <pre>{{ JSON.stringify(testForestData, null, 2) }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ForestMap from '@/components/Map/ForestMap.vue'

const showTestMap = ref(false)

const testForestData = ref([
  {
    id: 1,
    name: '测试森林A',
    type: 'natural_forest',
    longitude: 116.397428,
    latitude: 39.90923,
    area: 100.5,
    healthStatus: 'healthy',
    manager: '测试管理员',
    lastInspection: '2024-01-15',
    region: '测试区域',
    treeSpecies: '松树',
    plantingYear: '2000'
  },
  {
    id: 2,
    name: '测试森林B',
    type: 'artificial_forest',
    longitude: 116.407428,
    latitude: 39.91923,
    area: 80.3,
    healthStatus: 'sub_healthy',
    manager: '测试管理员2',
    lastInspection: '2024-01-10',
    region: '测试区域2',
    treeSpecies: '杨树',
    plantingYear: '2010'
  }
])

const logTestData = () => {
  console.log('测试森林数据:', testForestData.value)
}

const handleMarkerClick = (feature, event) => {
  console.log('测试页面 - 标记点击:', feature, event)
}

const handleMapReady = (map) => {
  console.log('测试页面 - 地图就绪:', map)
}

const handleMapError = (error) => {
  console.error('测试页面 - 地图错误:', error)
}
</script>

<style lang="scss" scoped>
.map-test {
  padding: 20px;
  
  .test-controls {
    margin-bottom: 20px;
    
    .el-button {
      margin-right: 10px;
    }
  }
  
  .test-map-container {
    margin-bottom: 20px;
    border: 2px solid #409EFF;
    border-radius: 8px;
    overflow: hidden;
  }
  
  .test-info {
    h4 {
      color: #2c3e50;
      margin-bottom: 10px;
    }
    
    pre {
      background: #f8f9fa;
      padding: 15px;
      border-radius: 4px;
      font-size: 12px;
      overflow-x: auto;
    }
  }
}
</style>