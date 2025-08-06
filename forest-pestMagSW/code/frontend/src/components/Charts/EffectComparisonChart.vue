<template>
  <div class="effect-comparison-chart">
    <div class="chart-header">
      <h4>防治效果对比</h4>
      <div class="chart-controls">
        <el-select v-model="comparisonType" @change="updateChart" size="small">
          <el-option label="按时间对比" value="time" />
          <el-option label="按方案对比" value="plan" />
          <el-option label="按区域对比" value="region" />
        </el-select>
        <el-select v-model="chartType" @change="updateChart" size="small">
          <el-option label="柱状图" value="bar" />
          <el-option label="折线图" value="line" />
          <el-option label="雷达图" value="radar" />
        </el-select>
      </div>
    </div>

    <BaseChart :options="chartOptions" :height="height" ref="chartRef" />

    <div class="chart-metrics">
      <div class="metric-item">
        <div class="metric-label">平均防治效果</div>
        <div class="metric-value">{{ metricsData.averageEffect }}%</div>
        <div class="metric-trend" :class="metricsData.effectTrend">
          <el-icon>
            <component :is="metricsData.effectTrend === 'up' ? 'TrendCharts' : 'Bottom'" />
          </el-icon>
          {{ metricsData.effectChange }}%
        </div>
      </div>

      <div class="metric-item">
        <div class="metric-label">病虫害密度降低</div>
        <div class="metric-value">{{ metricsData.densityReduction }}%</div>
        <div class="metric-trend" :class="metricsData.densityTrend">
          <el-icon>
            <component :is="metricsData.densityTrend === 'up' ? 'TrendCharts' : 'Bottom'" />
          </el-icon>
          {{ metricsData.densityChange }}%
        </div>
      </div>

      <div class="metric-item">
        <div class="metric-label">植物恢复程度</div>
        <div class="metric-value">{{ metricsData.recoveryRate }}%</div>
        <div class="metric-trend" :class="metricsData.recoveryTrend">
          <el-icon>
            <component :is="metricsData.recoveryTrend === 'up' ? 'TrendCharts' : 'Bottom'" />
          </el-icon>
          {{ metricsData.recoveryChange }}%
        </div>
      </div>

      <div class="metric-item">
        <div class="metric-label">成本效益比</div>
        <div class="metric-value">{{ metricsData.costEfficiency }}</div>
        <div class="metric-trend" :class="metricsData.costTrend">
          <el-icon>
            <component :is="metricsData.costTrend === 'up' ? 'TrendCharts' : 'Bottom'" />
          </el-icon>
          {{ metricsData.costChange }}%
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { TrendCharts, Bottom } from '@element-plus/icons-vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  height: {
    type: String,
    default: '400px'
  }
})

const comparisonType = ref('time')
const chartType = ref('bar')
const chartRef = ref()

const chartData = reactive({
  categories: [],
  series: []
})

const metricsData = reactive({
  averageEffect: 0,
  effectTrend: 'up',
  effectChange: 0,
  densityReduction: 0,
  densityTrend: 'up',
  densityChange: 0,
  recoveryRate: 0,
  recoveryTrend: 'up',
  recoveryChange: 0,
  costEfficiency: 0,
  costTrend: 'up',
  costChange: 0
})

// 图表配置
const chartOptions = computed(() => {
  const baseOptions = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: chartType.value === 'bar' ? 'shadow' : 'cross'
      },
      formatter: function (params) {
        let result = `${params[0].axisValue}<br/>`
        params.forEach((param) => {
          result += `${param.marker}${param.seriesName}: ${param.value}%<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['防治效果', '密度降低率', '恢复程度'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }

  if (chartType.value === 'radar') {
    return {
      ...baseOptions,
      radar: {
        indicator: [
          { name: '防治效果', max: 100 },
          { name: '密度降低率', max: 100 },
          { name: '恢复程度', max: 100 },
          { name: '成本控制', max: 100 },
          { name: '时效性', max: 100 },
          { name: '可持续性', max: 100 }
        ],
        radius: '60%'
      },
      series: [
        {
          name: '防治效果评估',
          type: 'radar',
          data: [
            {
              value: [85, 78, 92, 75, 88, 82],
              name: '综合评估',
              areaStyle: {
                color: 'rgba(64, 158, 255, 0.3)'
              }
            }
          ]
        }
      ]
    }
  }

  return {
    ...baseOptions,
    xAxis: {
      type: 'category',
      data: chartData.categories,
      axisLabel: {
        rotate: comparisonType.value === 'time' ? 45 : 0
      }
    },
    yAxis: {
      type: 'value',
      name: '百分比 (%)',
      nameTextStyle: {
        color: '#666'
      },
      max: 100
    },
    series: [
      {
        name: '防治效果',
        type: chartType.value,
        smooth: chartType.value === 'line',
        data: chartData.series[0] || [],
        itemStyle: {
          color: '#409EFF'
        },
        ...(chartType.value === 'line' && {
          lineStyle: {
            width: 3
          },
          symbol: 'circle',
          symbolSize: 6
        })
      },
      {
        name: '密度降低率',
        type: chartType.value,
        smooth: chartType.value === 'line',
        data: chartData.series[1] || [],
        itemStyle: {
          color: '#67C23A'
        },
        ...(chartType.value === 'line' && {
          lineStyle: {
            width: 3
          },
          symbol: 'circle',
          symbolSize: 6
        })
      },
      {
        name: '恢复程度',
        type: chartType.value,
        smooth: chartType.value === 'line',
        data: chartData.series[2] || [],
        itemStyle: {
          color: '#E6A23C'
        },
        ...(chartType.value === 'line' && {
          lineStyle: {
            width: 3
          },
          symbol: 'circle',
          symbolSize: 6
        })
      }
    ]
  }
})

// 更新图表数据
const updateChart = async () => {
  try {
    // 这里应该调用API获取数据
    // const response = await getEffectComparisonData(comparisonType.value)

    // 模拟数据
    const mockData = generateMockData(comparisonType.value)

    chartData.categories = mockData.categories
    chartData.series = mockData.series

    // 更新指标数据
    updateMetricsData(mockData)
  } catch (error) {
    console.error('获取效果对比数据失败:', error)
  }
}

// 生成模拟数据
const generateMockData = (type) => {
  const data = {
    categories: [],
    series: [[], [], []] // 防治效果, 密度降低率, 恢复程度
  }

  if (type === 'time') {
    data.categories = ['1月', '2月', '3月', '4月', '5月', '6月']
    data.series[0] = [75, 82, 88, 85, 92, 89] // 防治效果
    data.series[1] = [70, 78, 85, 82, 88, 86] // 密度降低率
    data.series[2] = [80, 85, 90, 87, 94, 91] // 恢复程度
  } else if (type === 'plan') {
    data.categories = ['松毛虫防治', '杨树溃疡病', '美国白蛾', '其他病害']
    data.series[0] = [85, 78, 92, 75] // 防治效果
    data.series[1] = [82, 75, 88, 72] // 密度降低率
    data.series[2] = [88, 80, 95, 78] // 恢复程度
  } else if (type === 'region') {
    data.categories = ['昌平区', '怀柔区', '密云区', '延庆区']
    data.series[0] = [88, 85, 90, 82] // 防治效果
    data.series[1] = [85, 82, 87, 79] // 密度降低率
    data.series[2] = [91, 88, 93, 85] // 恢复程度
  }

  return data
}

// 更新指标数据
const updateMetricsData = (data) => {
  // 计算平均值
  const avgEffect = Math.round(
    data.series[0].reduce((sum, val) => sum + val, 0) / data.series[0].length
  )
  const avgDensity = Math.round(
    data.series[1].reduce((sum, val) => sum + val, 0) / data.series[1].length
  )
  const avgRecovery = Math.round(
    data.series[2].reduce((sum, val) => sum + val, 0) / data.series[2].length
  )

  metricsData.averageEffect = avgEffect
  metricsData.densityReduction = avgDensity
  metricsData.recoveryRate = avgRecovery
  metricsData.costEfficiency = 1.25

  // 模拟趋势变化
  metricsData.effectTrend = 'up'
  metricsData.effectChange = 5.2
  metricsData.densityTrend = 'up'
  metricsData.densityChange = 3.8
  metricsData.recoveryTrend = 'up'
  metricsData.recoveryChange = 4.5
  metricsData.costTrend = 'up'
  metricsData.costChange = 2.1
}

onMounted(() => {
  updateChart()
})
</script>

<style lang="scss" scoped>
.effect-comparison-chart {
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
        width: 120px;
      }
    }
  }

  .chart-metrics {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
    margin-top: 20px;

    .metric-item {
      padding: 15px;
      background-color: #f8f9fa;
      border-radius: 8px;
      text-align: center;

      .metric-label {
        font-size: 12px;
        color: #909399;
        margin-bottom: 8px;
      }

      .metric-value {
        font-size: 24px;
        font-weight: bold;
        color: #2c3e50;
        margin-bottom: 5px;
      }

      .metric-trend {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        font-size: 12px;

        &.up {
          color: #67c23a;
        }

        &.down {
          color: #f56c6c;
        }

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .effect-comparison-chart {
    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;

      .chart-controls {
        width: 100%;
        justify-content: space-between;

        .el-select {
          width: 48%;
        }
      }
    }

    .chart-metrics {
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;

      .metric-item {
        padding: 10px;

        .metric-value {
          font-size: 20px;
        }
      }
    }
  }
}
</style>
