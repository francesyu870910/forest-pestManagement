<template>
  <div class="forest-map-container">
    <!-- 地图容器 -->
    <div :id="mapContainerId" class="map-canvas" :style="{ height: height }"></div>
    
    <!-- 地图控制面板 -->
    <div class="map-controls">
      <LayerControl 
        v-if="showControls"
        @layer-change="handleLayerChange" 
        @opacity-change="handleOpacityChange"
      />
      <MeasureTool 
        v-if="enableMeasure"
        @measure-start="handleMeasureStart"
        @measure-complete="handleMeasureComplete"
      />
      <DataImportExport 
        v-if="enableDataImport"
        @import="handleDataImport"
        @export="handleDataExport"
      />
    </div>
    
    <!-- 信息弹窗 -->
    <InfoPopup 
      v-if="selectedFeature"
      :feature="selectedFeature"
      :position="popupPosition"
      @close="closePopup"
      @edit="handleFeatureEdit"
    />
    
    <!-- 绘制工具 -->
    <DrawingTools 
      v-if="drawingMode"
      :mode="drawingMode"
      @draw-complete="handleDrawComplete"
      @draw-cancel="handleDrawCancel"
    />
    
    <!-- 加载状态 -->
    <div v-if="loading" class="map-loading">
      <div class="loading-spinner"></div>
      <p>地图加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-if="error" class="map-error">
      <el-alert
        :title="error.title"
        :description="error.message"
        type="error"
        show-icon
        :closable="false"
      >
        <template #default>
          <el-button @click="retryLoad">重试</el-button>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import LayerControl from './LayerControl.vue'
import MeasureTool from './MeasureTool.vue'
import DataImportExport from './DataImportExport.vue'
import InfoPopup from './InfoPopup.vue'
import DrawingTools from './DrawingTools.vue'
import { TiandituMapService } from '@/services/TiandituMapService'
import { LayerManager } from '@/services/LayerManager'
import { MapErrorHandler } from '@/services/MapErrorHandler'

// 类型定义
interface ForestResource {
  id: string | number
  name: string
  type: 'natural_forest' | 'artificial_forest' | 'economic_forest' | 'protection_forest'
  longitude: number
  latitude: number
  area: number
  age?: number
  density?: number
  healthStatus: 'healthy' | 'sub_healthy' | 'diseased' | 'severely_diseased'
  lastInspection: string
  manager: string
  description?: string
  images?: string[]
  region?: string
  treeSpecies?: string
  plantingYear?: string
  remarks?: string
}

interface PestData {
  id: string
  type: string
  name: string
  longitude: number
  latitude: number
  severity: 'low' | 'medium' | 'high' | 'critical'
  affectedArea: number
  discoveryDate: Date
  status: 'active' | 'treated' | 'resolved'
  description: string
  images?: string[]
}

interface TreatmentArea {
  id: string
  name: string
  coordinates: number[][]
  area: number
  status: 'planned' | 'active' | 'completed' | 'paused'
  treatmentType: string
  assignedTeam: string
  progress: number
}

interface MapError {
  title: string
  message: string
  code?: string
}

// Props定义
interface Props {
  // 地图配置
  center?: [number, number]
  zoom?: number
  minZoom?: number
  maxZoom?: number
  height?: string
  theme?: 'light' | 'dark'
  
  // 数据源
  forestData?: ForestResource[]
  pestData?: PestData[]
  treatmentAreas?: TreatmentArea[]
  
  // 功能开关
  enableMeasure?: boolean
  enableDrawing?: boolean
  enableDataImport?: boolean
  showControls?: boolean
  
  // API配置
  apiKey?: string
}

const props = withDefaults(defineProps<Props>(), {
  center: () => [116.397428, 39.90923],
  zoom: 10,
  minZoom: 3,
  maxZoom: 18,
  height: '600px',
  theme: 'light',
  forestData: () => [],
  pestData: () => [],
  treatmentAreas: () => [],
  enableMeasure: true,
  enableDrawing: true,
  enableDataImport: true,
  showControls: true
})

// Emits定义
const emit = defineEmits<{
  'map-ready': [map: any]
  'marker-click': [feature: any, event: any]
  'area-draw': [area: TreatmentArea]
  'measure-complete': [result: any]
  'data-import': [data: any]
  'feature-edit': [feature: any]
  'error': [error: MapError]
}>()

// 响应式数据
const loading = ref(true)
const error = ref<MapError | null>(null)
const selectedFeature = ref<any>(null)
const popupPosition = ref<[number, number] | null>(null)
const drawingMode = ref<string | null>(null)

// 生成唯一的地图容器ID
const mapContainerId = `tianditu-map-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`

// 服务实例
let mapService: TiandituMapService | null = null
let layerManager: LayerManager | null = null
let errorHandler: MapErrorHandler | null = null

// 地图初始化
const initializeMap = async () => {
  try {
    loading.value = true
    error.value = null
    
    // 检查是否有API密钥
    const apiKey = props.apiKey || import.meta.env.VITE_TIANDITU_API_KEY
    
    console.log('天地图API密钥检查:', {
      propsApiKey: props.apiKey,
      envApiKey: import.meta.env.VITE_TIANDITU_API_KEY,
      finalApiKey: apiKey
    })
    
    if (!apiKey) {
      // 如果没有API密钥，使用简化的地图显示
      console.warn('天地图API密钥未配置，使用简化地图模式')
      await initializeSimpleMap()
      return
    }
    
    console.log('开始初始化天地图，API密钥:', apiKey)
    console.log('森林资源数据:', props.forestData)
    
    // 创建地图服务实例
    mapService = new TiandituMapService(mapContainerId, {
      center: props.center,
      zoom: props.zoom,
      minZoom: props.minZoom,
      maxZoom: props.maxZoom,
      apiKey: apiKey
    })
    
    // 等待地图加载完成
    await mapService.initialize()
    
    // 创建图层管理器
    layerManager = new LayerManager(mapService)
    
    // 创建错误处理器
    errorHandler = new MapErrorHandler()
    
    // 设置事件监听
    setupEventListeners()
    
    // 加载数据图层
    await loadDataLayers()
    
    loading.value = false
    
    // 测试：手动添加一个标记点
    console.log('测试：手动添加标记点')
    try {
      const testMarker = mapService.addMarker([116.397428, 39.90923], {
        iconUrl: '/icons/forest/natural.svg',
        size: [32, 32],
        anchor: [16, 32]
      })
      console.log('测试标记添加结果:', testMarker)
    } catch (testErr) {
      console.error('测试标记添加失败:', testErr)
    }
    
    emit('map-ready', mapService.getMap())
    
  } catch (err) {
    console.error('地图初始化失败:', err)
    // 尝试降级到简化地图
    try {
      await initializeSimpleMap()
    } catch (fallbackErr) {
      error.value = {
        title: '地图加载失败',
        message: '无法连接到地图服务，请检查网络连接或稍后重试',
        code: 'MAP_INIT_ERROR'
      }
      loading.value = false
      emit('error', error.value)
    }
  }
}

// 简化地图初始化（降级方案）
const initializeSimpleMap = async () => {
  try {
    console.log('初始化简化地图模式，森林数据:', props.forestData)
    
    // 创建简化的地图显示
    const mapContainer = document.getElementById(mapContainerId)
    if (mapContainer) {
      // 生成所有森林资源的标记
      let markersHtml = ''
      
      if (props.forestData && props.forestData.length > 0) {
        props.forestData.forEach((resource, index) => {
          const lat = resource.latitude || props.center[1]
          const lng = resource.longitude || props.center[0]
          
          // 计算标记在地图上的相对位置
          const leftPercent = 30 + (index % 3) * 20 // 分散显示
          const topPercent = 30 + Math.floor(index / 3) * 15
          
          const iconEmoji = getResourceIcon(resource.type)
          const typeText = getResourceTypeText(resource.type)
          const healthColor = getHealthStatusColor(resource.healthStatus)
          
          markersHtml += `
            <div class="resource-marker" style="left: ${leftPercent}%; top: ${topPercent}%;" data-resource-id="${resource.id}">
              <div class="marker-icon" style="color: ${healthColor};">${iconEmoji}</div>
              <div class="marker-label">${resource.name}</div>
              <div class="marker-tooltip">
                <div class="tooltip-content">
                  <h5>${resource.name}</h5>
                  <p>类型: ${typeText}</p>
                  <p>面积: ${resource.area || 0} 公顷</p>
                  <p>健康: ${getHealthStatusText(resource.healthStatus)}</p>
                  <p>坐标: ${lat.toFixed(4)}, ${lng.toFixed(4)}</p>
                </div>
              </div>
            </div>
          `
        })
      } else {
        // 如果没有数据，显示默认标记
        markersHtml = `
          <div class="resource-marker" style="left: 50%; top: 50%;">
            <div class="marker-icon">🌲</div>
            <div class="marker-label">森林资源</div>
          </div>
        `
      }
      
      const primaryResource = props.forestData?.[0]
      const lat = primaryResource?.latitude || props.center[1]
      const lng = primaryResource?.longitude || props.center[0]
      
      mapContainer.innerHTML = `
        <div class="simple-map">
          <div class="map-view">
            <div class="map-background">
              <div class="coordinate-grid">
                <div class="grid-lines"></div>
                ${markersHtml}
              </div>
            </div>
            <div class="map-info-overlay">
              <div class="resource-summary">
                <h4>森林资源概览</h4>
                <div class="summary-stats">
                  <div class="stat-item">
                    <span class="stat-label">资源总数:</span>
                    <span class="stat-value">${props.forestData?.length || 0}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">总面积:</span>
                    <span class="stat-value">${calculateTotalArea()} 公顷</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">中心坐标:</span>
                    <span class="stat-value">${lat.toFixed(4)}, ${lng.toFixed(4)}</span>
                  </div>
                </div>
                ${primaryResource ? `
                <div class="primary-resource">
                  <h5>主要资源: ${primaryResource.name}</h5>
                  <div class="resource-details">
                    <div class="detail-item">
                      <span class="label">类型:</span>
                      <span class="value">${getResourceTypeText(primaryResource.type)}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">面积:</span>
                      <span class="value">${primaryResource.area || 0} 公顷</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">健康状态:</span>
                      <span class="value" style="color: ${getHealthStatusColor(primaryResource.healthStatus)};">
                        ${getHealthStatusText(primaryResource.healthStatus)}
                      </span>
                    </div>
                    <div class="detail-item">
                      <span class="label">管理员:</span>
                      <span class="value">${primaryResource.manager || '未指定'}</span>
                    </div>
                  </div>
                </div>
                ` : ''}
              </div>
            </div>
          </div>
        </div>
      `
      
      // 添加标记点击事件
      const markers = mapContainer.querySelectorAll('.resource-marker')
      markers.forEach(marker => {
        marker.addEventListener('click', (e) => {
          const resourceId = marker.getAttribute('data-resource-id')
          const resource = props.forestData?.find(r => r.id == resourceId)
          if (resource) {
            console.log('简化地图标记点击:', resource)
            emit('marker-click', resource, e)
          }
        })
        
        // 添加悬停效果
        marker.addEventListener('mouseenter', () => {
          const tooltip = marker.querySelector('.marker-tooltip')
          if (tooltip) {
            tooltip.style.display = 'block'
          }
        })
        
        marker.addEventListener('mouseleave', () => {
          const tooltip = marker.querySelector('.marker-tooltip')
          if (tooltip) {
            tooltip.style.display = 'none'
          }
        })
      })
    }
    
    loading.value = false
    emit('map-ready', null)
    console.log('简化地图初始化完成')
  } catch (err) {
    console.error('简化地图初始化失败:', err)
    throw err
  }
}

// 获取健康状态文本
const getHealthStatusText = (status) => {
  const textMap = {
    healthy: '健康',
    sub_healthy: '亚健康',
    diseased: '病害',
    severely_diseased: '严重病害'
  }
  return textMap[status] || '未知'
}

// 获取资源类型文本
const getResourceTypeText = (type) => {
  const typeMap = {
    natural_forest: '天然林',
    artificial_forest: '人工林', 
    economic_forest: '经济林',
    protection_forest: '防护林'
  }
  return typeMap[type] || '未知类型'
}

// 获取资源图标
const getResourceIcon = (type) => {
  const iconMap = {
    natural_forest: '🌲',
    artificial_forest: '🌳',
    economic_forest: '🍎',
    protection_forest: '🛡️'
  }
  return iconMap[type] || '🌲'
}

// 获取健康状态颜色
const getHealthStatusColor = (status) => {
  const colorMap = {
    healthy: '#67C23A',
    sub_healthy: '#E6A23C',
    diseased: '#F56C6C',
    severely_diseased: '#F56C6C'
  }
  return colorMap[status] || '#909399'
}

// 计算总面积
const calculateTotalArea = () => {
  if (!props.forestData || props.forestData.length === 0) return 0
  return props.forestData.reduce((total, resource) => total + (resource.area || 0), 0).toFixed(1)
}

// 设置事件监听
const setupEventListeners = () => {
  if (!mapService) return
  
  // 地图点击事件
  mapService.on('click', (event: any) => {
    // 检查是否点击了标记
    const feature = mapService?.getFeatureAtPixel(event.pixel)
    if (feature) {
      selectedFeature.value = feature
      popupPosition.value = [event.lngLat.lng, event.lngLat.lat]
      emit('marker-click', feature, event)
    } else {
      closePopup()
    }
  })
  
  // 地图移动事件
  mapService.on('moveend', () => {
    // 可以在这里实现视窗数据加载
    loadVisibleData()
  })
  
  // 缩放事件
  mapService.on('zoomend', () => {
    // 根据缩放级别调整数据显示
    updateDataByZoom()
  })
}

// 加载数据图层
const loadDataLayers = async () => {
  console.log('开始加载数据图层')
  console.log('LayerManager状态:', !!layerManager)
  console.log('森林数据:', props.forestData)
  console.log('森林数据长度:', props.forestData?.length)
  
  if (!layerManager) {
    console.error('LayerManager未初始化')
    return
  }
  
  try {
    // 加载森林资源图层
    if (props.forestData && props.forestData.length > 0) {
      console.log('开始添加森林资源图层，数据量:', props.forestData.length)
      await layerManager.addForestLayer(props.forestData)
      console.log('森林资源图层添加完成')
    } else {
      console.warn('没有森林资源数据需要加载')
    }
    
    // 加载病虫害图层
    if (props.pestData && props.pestData.length > 0) {
      console.log('开始添加病虫害图层，数据量:', props.pestData.length)
      await layerManager.addPestLayer(props.pestData)
    }
    
    // 加载防治区域图层
    if (props.treatmentAreas && props.treatmentAreas.length > 0) {
      console.log('开始添加防治区域图层，数据量:', props.treatmentAreas.length)
      await layerManager.addTreatmentLayer(props.treatmentAreas)
    }
    
    console.log('所有数据图层加载完成')
    
  } catch (err) {
    console.error('数据图层加载失败:', err)
    ElMessage.error('数据加载失败，请稍后重试')
  }
}

// 加载可视区域数据
const loadVisibleData = () => {
  if (!mapService) return
  
  const bounds = mapService.getBounds()
  const zoom = mapService.getZoom()
  
  // 这里可以实现按需加载逻辑
  // 根据当前视窗和缩放级别加载数据
}

// 根据缩放级别更新数据显示
const updateDataByZoom = () => {
  if (!layerManager) return
  
  const zoom = mapService?.getZoom() || 10
  
  // 根据缩放级别调整标记聚合
  if (zoom < 12) {
    layerManager.enableClustering(true)
  } else {
    layerManager.enableClustering(false)
  }
}

// 事件处理方法
const handleLayerChange = (layerName: string, visible: boolean) => {
  layerManager?.toggleLayer(layerName, visible)
}

const handleOpacityChange = (layerName: string, opacity: number) => {
  layerManager?.setLayerOpacity(layerName, opacity)
}

const handleMeasureStart = (mode: string) => {
  mapService?.startMeasure(mode)
}

const handleMeasureComplete = (result: any) => {
  emit('measure-complete', result)
}

const handleDataImport = (data: any) => {
  emit('data-import', data)
  // 重新加载数据图层
  loadDataLayers()
}

const handleDataExport = () => {
  // 导出当前地图数据
  const data = layerManager?.exportData()
  return data
}

const handleDrawComplete = (geometry: any) => {
  drawingMode.value = null
  
  // 创建防治区域对象
  const area: TreatmentArea = {
    id: `area_${Date.now()}`,
    name: `防治区域_${Date.now()}`,
    coordinates: geometry.coordinates,
    area: calculateArea(geometry.coordinates),
    status: 'planned',
    treatmentType: '',
    assignedTeam: '',
    progress: 0
  }
  
  emit('area-draw', area)
}

const handleDrawCancel = () => {
  drawingMode.value = null
}

const handleFeatureEdit = (feature: any) => {
  emit('feature-edit', feature)
}

const closePopup = () => {
  selectedFeature.value = null
  popupPosition.value = null
}

const retryLoad = () => {
  error.value = null
  initializeMap()
}

// 工具方法
const calculateArea = (coordinates: number[][]): number => {
  // 使用Turf.js计算面积
  // 这里简化实现
  return 0
}

const startDrawing = (mode: string) => {
  drawingMode.value = mode
}

const stopDrawing = () => {
  drawingMode.value = null
}

// 监听数据变化
watch(() => props.forestData, (newData) => {
  if (layerManager && newData) {
    layerManager.updateForestLayer(newData)
  }
}, { deep: true })

watch(() => props.pestData, (newData) => {
  if (layerManager && newData) {
    layerManager.updatePestLayer(newData)
  }
}, { deep: true })

watch(() => props.treatmentAreas, (newData) => {
  if (layerManager && newData) {
    layerManager.updateTreatmentLayer(newData)
  }
}, { deep: true })

// 生命周期
onMounted(() => {
  nextTick(() => {
    initializeMap()
  })
})

onUnmounted(() => {
  // 清理地图资源
  if (mapService) {
    mapService.destroy()
  }
})

// 暴露方法给父组件
defineExpose({
  startDrawing,
  stopDrawing,
  getMap: () => mapService?.getMap(),
  getLayerManager: () => layerManager,
  retryLoad
})
</script>

<style lang="scss" scoped>
.forest-map-container {
  position: relative;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  .map-canvas {
    width: 100%;
    background-color: #f5f5f5;
    
    .simple-map {
      width: 100%;
      height: 100%;
      position: relative;
      
      .map-view {
        width: 100%;
        height: 100%;
        position: relative;
        
        .map-background {
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, #e8f5e8 0%, #c8e6c9 50%, #a5d6a7 100%);
          position: relative;
          overflow: hidden;
          
          .coordinate-grid {
            width: 100%;
            height: 100%;
            position: relative;
            background-image: 
              linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px),
              linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px);
            background-size: 50px 50px;
            
            .resource-marker {
              position: absolute;
              transform: translate(-50%, -50%);
              z-index: 10;
              cursor: pointer;
              transition: all 0.3s ease;
              
              &:hover {
                transform: translate(-50%, -50%) scale(1.1);
                z-index: 20;
              }
              
              .marker-icon {
                font-size: 32px;
                text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                animation: bounce 2s infinite;
                margin-bottom: 5px;
                text-align: center;
              }
              
              .marker-label {
                background: rgba(255,255,255,0.95);
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 600;
                color: #2c3e50;
                box-shadow: 0 2px 8px rgba(0,0,0,0.2);
                white-space: nowrap;
                text-align: center;
                border: 1px solid rgba(0,0,0,0.1);
              }
              
              .marker-tooltip {
                position: absolute;
                top: -120px;
                left: 50%;
                transform: translateX(-50%);
                background: rgba(0,0,0,0.9);
                color: white;
                padding: 8px 12px;
                border-radius: 6px;
                font-size: 12px;
                white-space: nowrap;
                display: none;
                z-index: 1000;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                
                &::after {
                  content: '';
                  position: absolute;
                  top: 100%;
                  left: 50%;
                  transform: translateX(-50%);
                  border: 6px solid transparent;
                  border-top-color: rgba(0,0,0,0.9);
                }
                
                .tooltip-content {
                  h5 {
                    margin: 0 0 6px 0;
                    font-size: 13px;
                    font-weight: 600;
                    color: #fff;
                  }
                  
                  p {
                    margin: 2px 0;
                    font-size: 11px;
                    color: #ccc;
                  }
                }
              }
            }
          }
        }
        
        .map-info-overlay {
          position: absolute;
          top: 20px;
          right: 20px;
          background: rgba(255,255,255,0.95);
          backdrop-filter: blur(10px);
          border-radius: 8px;
          padding: 16px;
          box-shadow: 0 4px 12px rgba(0,0,0,0.15);
          max-width: 280px;
          max-height: calc(100% - 40px);
          overflow-y: auto;
          
          .resource-summary {
            h4 {
              margin: 0 0 12px 0;
              color: #2c3e50;
              font-size: 16px;
              font-weight: 600;
              text-align: center;
              border-bottom: 2px solid #409EFF;
              padding-bottom: 8px;
            }
            
            .summary-stats {
              margin-bottom: 16px;
              
              .stat-item {
                display: flex;
                justify-content: space-between;
                margin-bottom: 6px;
                padding: 4px 0;
                border-bottom: 1px solid #f0f0f0;
                
                .stat-label {
                  color: #606266;
                  font-size: 13px;
                  font-weight: 500;
                }
                
                .stat-value {
                  color: #409EFF;
                  font-size: 13px;
                  font-weight: 600;
                }
              }
            }
            
            .primary-resource {
              border-top: 1px solid #e0e0e0;
              padding-top: 12px;
              
              h5 {
                margin: 0 0 8px 0;
                color: #2c3e50;
                font-size: 14px;
                font-weight: 600;
              }
              
              .resource-details {
                .detail-item {
                  display: flex;
                  justify-content: space-between;
                  margin-bottom: 6px;
                  
                  .label {
                    color: #606266;
                    font-size: 12px;
                    min-width: 60px;
                  }
                  
                  .value {
                    color: #2c3e50;
                    font-size: 12px;
                    font-weight: 500;
                    text-align: right;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  @keyframes bounce {
    0%, 20%, 50%, 80%, 100% {
      transform: translateY(0);
    }
    40% {
      transform: translateY(-10px);
    }
    60% {
      transform: translateY(-5px);
    }
  }
  
  .map-controls {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 1000;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .map-loading {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    z-index: 2000;
    
    .loading-spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #409EFF;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
    
    p {
      margin-top: 16px;
      color: #606266;
      font-size: 14px;
    }
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .map-error {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 2000;
    width: 90%;
    max-width: 400px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .forest-map-container {
    .map-controls {
      top: 5px;
      right: 5px;
      gap: 5px;
    }
    
    .map-error {
      width: 95%;
    }
  }
}
</style>