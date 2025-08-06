<template>
  <div ref="chartRef" :style="{ width: width, height: height }" />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '400px'
  },
  options: {
    type: Object,
    required: true
  },
  theme: {
    type: String,
    default: 'default'
  }
})

const chartRef = ref()
let chartInstance = null

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value, props.theme)
  chartInstance.setOption(props.options)

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 更新图表配置
const updateChart = () => {
  if (chartInstance && props.options) {
    chartInstance.setOption(props.options, true)
  }
}

// 监听配置变化
watch(
  () => props.options,
  () => {
    updateChart()
  },
  { deep: true }
)

// 监听主题变化
watch(
  () => props.theme,
  () => {
    if (chartInstance) {
      chartInstance.dispose()
      initChart()
    }
  }
)

// 暴露图表实例
defineExpose({
  chartInstance,
  updateChart
})

onMounted(() => {
  nextTick(() => {
    initChart()
  })
})

onBeforeUnmount(() => {
  if (chartInstance) {
    window.removeEventListener('resize', handleResize)
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
/* 图表容器样式 */
</style>
