<template>
  <div class="warning-map-chart">
    <div class="chart-header">
      <h4>预警信息分布</h4>
      <div class="chart-controls">
        <el-select v-model="warningLevel" @change="updateMap" size="small">
          <el-option label="全部" value="all" />
          <el-option label="高风险" value="high" />
          <el-option label="中风险" value="medium" />
          <el-option label="低风险" value="low" />
        </el-select>
        <el-button @click="refreshData" size="small" type="primary">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <div class="map-container">
      <BaseChart :options="mapOptions" :height="height" ref="mapRef" />
    </div>

    <div class="warning-legend">
      <div class="legend-item">
        <div class="legend-color high-risk"></div>
        <span>高风险 ({{ warningStats.high }})</span>
      </div>
      <div class="legend-item">
        <div class="legend-color medium-risk"></div>
        <span>中风险 ({{ warningStats.medium }})</span>
      </div>
      <div class="legend-item">
        <div class="legend-color low-risk"></div>
        <span>低风险 ({{ warningStats.low }})</span>
      </div>
    </div>

    <div class="warning-list">
      <h5>最新预警</h5>
      <div class="warning-items">
        <div
          v-for="warning in recentWarnings"
          :key="warning.id"
          class="warning-item"
          :class="warning.level"
          @click="showWarningDetail(warning)"
        >
          <div class="warning-info">
            <div class="warning-title">{{ warning.title }}</div>
            <div class="warning-location">{{ warning.location }}</div>
            <div class="warning-time">{{ warning.time }}</div>
          </div>
          <div class="warning-level">
            <el-tag :type="getWarningType(warning.level)" size="small">
              {{ getWarningText(warning.level) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  height: {
    type: String,
    default: '400px'
  }
})

const warningLevel = ref('all')
const mapRef = ref()

const warningData = reactive({
  points: []
})

const warningStats = reactive({
  high: 0,
  medium: 0,
  low: 0
})

const recentWarnings = ref([])

// 地图配置
const mapOptions = computed(() => ({
  title: {
    show: false
  },
  tooltip: {
    trigger: 'item',
    formatter: function (params) {
      if (params.componentType === 'geo') {
        return `${params.name}<br/>预警数量: ${params.value || 0}`
      } else if (params.componentType === 'series') {
        const data = params.data
        return `
          <div style="text-align: left;">
            <strong>${data.name}</strong><br/>
            位置: ${data.location}<br/>
            风险等级: ${getWarningText(data.level)}<br/>
            时间: ${data.time}
          </div>
        `
      }
    }
  },
  geo: {
    map: 'china',
    roam: true,
    zoom: 1.2,
    center: [116.46, 39.92],
    itemStyle: {
      areaColor: '#f3f3f3',
      borderColor: '#999',
      borderWidth: 0.5
    },
    emphasis: {
      itemStyle: {
        areaColor: '#e6f7ff'
      }
    }
  },
  series: [
    {
      name: '预警点',
      type: 'scatter',
      coordinateSystem: 'geo',
      data: warningData.points,
      symbolSize: function (val) {
        return Math.max(val[2] * 3, 8) // 根据风险等级调整大小
      },
      itemStyle: {
        color: function (params) {
          const level = params.data.level
          if (level === 'high') return '#ff4757'
          if (level === 'medium') return '#ffa502'
          return '#2ed573'
        },
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.3)'
      },
      emphasis: {
        scale: true,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        }
      }
    }
  ]
}))

// 方法
const getWarningType = (level) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'success'
  }
  return typeMap[level] || 'info'
}

const getWarningText = (level) => {
  const textMap = {
    high: '高风险',
    medium: '中风险',
    low: '低风险'
  }
  return textMap[level] || '未知'
}

const updateMap = () => {
  loadWarningData()
}

const refreshData = () => {
  loadWarningData()
  ElMessage.success('数据已刷新')
}

const showWarningDetail = (warning) => {
  ElMessage.info(`查看预警详情: ${warning.title}`)
}

const loadWarningData = async () => {
  try {
    // 这里应该调用API获取预警数据
    // const response = await getWarningMapData(warningLevel.value)

    // 模拟数据
    const mockData = generateMockWarningData()

    warningData.points = mockData.points
    recentWarnings.value = mockData.recentWarnings

    // 更新统计数据
    updateWarningStats(mockData.points)
  } catch (error) {
    console.error('获取预警数据失败:', error)
    ElMessage.error('获取预警数据失败')
  }
}

const generateMockWarningData = () => {
  const points = [
    {
      name: '昌平森林公园',
      value: [116.2432, 40.2181, 3],
      location: '北京市昌平区',
      level: 'high',
      time: '2024-01-15 09:30'
    },
    {
      name: '承德林场',
      value: [117.9382, 40.9544, 2],
      location: '河北省承德市',
      level: 'medium',
      time: '2024-01-14 14:20'
    },
    {
      name: '张家口森林',
      value: [114.8794, 40.8076, 1],
      location: '河北省张家口市',
      level: 'low',
      time: '2024-01-13 11:15'
    },
    {
      name: '密云水库周边',
      value: [116.8432, 40.3769, 3],
      location: '北京市密云区',
      level: 'high',
      time: '2024-01-12 16:45'
    },
    {
      name: '怀柔山区',
      value: [116.6317, 40.3242, 2],
      location: '北京市怀柔区',
      level: 'medium',
      time: '2024-01-11 08:30'
    }
  ]

  // 根据选择的风险等级过滤数据
  const filteredPoints =
    warningLevel.value === 'all'
      ? points
      : points.filter((point) => point.level === warningLevel.value)

  const recentWarnings = points.slice(0, 5).map((point, index) => ({
    id: index + 1,
    title: `${point.name}病虫害预警`,
    location: point.location,
    level: point.level,
    time: point.time
  }))

  return {
    points: filteredPoints,
    recentWarnings
  }
}

const updateWarningStats = (points) => {
  warningStats.high = points.filter((p) => p.level === 'high').length
  warningStats.medium = points.filter((p) => p.level === 'medium').length
  warningStats.low = points.filter((p) => p.level === 'low').length
}

onMounted(() => {
  loadWarningData()
})
</script>

<style lang="scss" scoped>
.warning-map-chart {
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h4 {
      margin: 0;
      color: #2c3e50;
      font-size: 16px;
    }

    .chart-controls {
      display: flex;
      gap: 10px;

      .el-select {
        width: 100px;
      }
    }
  }

  .map-container {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    overflow: hidden;
  }

  .warning-legend {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin: 15px 0;
    padding: 10px;
    background-color: #f8f9fa;
    border-radius: 4px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 5px;
      font-size: 12px;
      color: #606266;

      .legend-color {
        width: 12px;
        height: 12px;
        border-radius: 50%;

        &.high-risk {
          background-color: #ff4757;
        }

        &.medium-risk {
          background-color: #ffa502;
        }

        &.low-risk {
          background-color: #2ed573;
        }
      }
    }
  }

  .warning-list {
    margin-top: 20px;

    h5 {
      margin: 0 0 10px 0;
      color: #2c3e50;
      font-size: 14px;
    }

    .warning-items {
      max-height: 200px;
      overflow-y: auto;

      .warning-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        margin-bottom: 8px;
        background-color: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
        }

        &:last-child {
          margin-bottom: 0;
        }

        .warning-info {
          flex: 1;

          .warning-title {
            font-size: 13px;
            color: #2c3e50;
            font-weight: 500;
            margin-bottom: 4px;
          }

          .warning-location {
            font-size: 12px;
            color: #909399;
            margin-bottom: 2px;
          }

          .warning-time {
            font-size: 11px;
            color: #c0c4cc;
          }
        }

        .warning-level {
          margin-left: 10px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .warning-map-chart {
    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;
    }

    .warning-legend {
      flex-wrap: wrap;
      gap: 10px;
    }

    .warning-list {
      .warning-items {
        max-height: 150px;

        .warning-item {
          padding: 8px;

          .warning-info {
            .warning-title {
              font-size: 12px;
            }

            .warning-location,
            .warning-time {
              font-size: 11px;
            }
          }
        }
      }
    }
  }
}
</style>
