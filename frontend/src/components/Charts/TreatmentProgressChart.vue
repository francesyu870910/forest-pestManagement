<template>
  <div class="treatment-progress-chart">
    <div class="chart-header">
      <h4>防治进度统计</h4>
      <div class="chart-controls">
        <el-select v-model="timeRange" @change="updateChart" size="small">
          <el-option label="最近7天" value="7days" />
          <el-option label="最近30天" value="30days" />
          <el-option label="最近3个月" value="3months" />
          <el-option label="最近1年" value="1year" />
        </el-select>
      </div>
    </div>

    <BaseChart :options="chartOptions" :height="height" ref="chartRef" />

    <div class="chart-summary">
      <div class="summary-item">
        <div class="summary-label">总方案数</div>
        <div class="summary-value">{{ summaryData.total }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">执行中</div>
        <div class="summary-value executing">{{ summaryData.executing }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">已完成</div>
        <div class="summary-value completed">{{ summaryData.completed }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">完成率</div>
        <div class="summary-value">{{ summaryData.completionRate }}%</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  height: {
    type: String,
    default: '350px'
  }
})

const timeRange = ref('30days')
const chartRef = ref()

const chartData = reactive({
  categories: [],
  series: []
})

const summaryData = reactive({
  total: 0,
  executing: 0,
  completed: 0,
  completionRate: 0
})

// 图表配置
const chartOptions = computed(() => ({
  title: {
    show: false
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      crossStyle: {
        color: '#999'
      }
    },
    formatter: function (params) {
      let result = `${params[0].axisValue}<br/>`
      params.forEach((param) => {
        result += `${param.marker}${param.seriesName}: ${param.value}<br/>`
      })
      return result
    }
  },
  legend: {
    data: ['新增方案', '执行中', '已完成'],
    top: 10
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: chartData.categories,
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    name: '数量',
    nameTextStyle: {
      color: '#666'
    }
  },
  series: [
    {
      name: '新增方案',
      type: 'line',
      stack: 'Total',
      smooth: true,
      lineStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: 'rgba(64, 158, 255, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(64, 158, 255, 0.1)'
            }
          ]
        }
      },
      data: chartData.series[0] || []
    },
    {
      name: '执行中',
      type: 'line',
      stack: 'Total',
      smooth: true,
      lineStyle: {
        color: '#E6A23C'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: 'rgba(230, 162, 60, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(230, 162, 60, 0.1)'
            }
          ]
        }
      },
      data: chartData.series[1] || []
    },
    {
      name: '已完成',
      type: 'line',
      stack: 'Total',
      smooth: true,
      lineStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: 'rgba(103, 194, 58, 0.3)'
            },
            {
              offset: 1,
              color: 'rgba(103, 194, 58, 0.1)'
            }
          ]
        }
      },
      data: chartData.series[2] || []
    }
  ]
}))

// 更新图表数据
const updateChart = async () => {
  try {
    // 这里应该调用API获取数据
    // const response = await getTreatmentProgressData(timeRange.value)

    // 模拟数据
    const mockData = generateMockData(timeRange.value)

    chartData.categories = mockData.categories
    chartData.series = mockData.series

    // 更新汇总数据
    updateSummaryData(mockData)
  } catch (error) {
    console.error('获取防治进度数据失败:', error)
  }
}

// 生成模拟数据
const generateMockData = (range) => {
  const data = {
    categories: [],
    series: [[], [], []] // 新增方案, 执行中, 已完成
  }

  let days = 7
  if (range === '30days') days = 30
  else if (range === '3months') days = 90
  else if (range === '1year') days = 365

  const now = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
    data.categories.push(date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }))

    // 生成随机数据
    data.series[0].push(Math.floor(Math.random() * 5) + 1) // 新增方案
    data.series[1].push(Math.floor(Math.random() * 8) + 2) // 执行中
    data.series[2].push(Math.floor(Math.random() * 6) + 1) // 已完成
  }

  return data
}

// 更新汇总数据
const updateSummaryData = (data) => {
  const newPlans = data.series[0].reduce((sum, val) => sum + val, 0)
  const executing = data.series[1].reduce((sum, val) => sum + val, 0)
  const completed = data.series[2].reduce((sum, val) => sum + val, 0)

  summaryData.total = newPlans + executing + completed
  summaryData.executing = executing
  summaryData.completed = completed
  summaryData.completionRate =
    summaryData.total > 0 ? Math.round((completed / summaryData.total) * 100) : 0
}

onMounted(() => {
  updateChart()
})
</script>

<style lang="scss" scoped>
.treatment-progress-chart {
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
      .el-select {
        width: 120px;
      }
    }
  }

  .chart-summary {
    display: flex;
    justify-content: space-around;
    margin-top: 15px;
    padding: 15px;
    background-color: #f8f9fa;
    border-radius: 8px;

    .summary-item {
      text-align: center;

      .summary-label {
        font-size: 12px;
        color: #909399;
        margin-bottom: 5px;
      }

      .summary-value {
        font-size: 20px;
        font-weight: bold;
        color: #2c3e50;

        &.executing {
          color: #e6a23c;
        }

        &.completed {
          color: #67c23a;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .treatment-progress-chart {
    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;
    }

    .chart-summary {
      flex-wrap: wrap;
      gap: 15px;

      .summary-item {
        flex: 1;
        min-width: 80px;
      }
    }
  }
}
</style>
