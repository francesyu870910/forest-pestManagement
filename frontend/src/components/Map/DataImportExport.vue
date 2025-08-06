<template>
  <div class="data-import-export">
    <el-dropdown @command="handleCommand">
      <el-button size="small">
        <el-icon><Upload /></el-icon>
        数据操作
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="import">导入数据</el-dropdown-item>
          <el-dropdown-item command="export">导出数据</el-dropdown-item>
          <el-dropdown-item command="clear">清空数据</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    
    <!-- 隐藏的文件输入 -->
    <input
      ref="fileInput"
      type="file"
      accept=".json,.kml,.geojson"
      style="display: none"
      @change="handleFileSelect"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, ArrowDown } from '@element-plus/icons-vue'

// 响应式数据
const fileInput = ref()

// Emits
const emit = defineEmits(['import', 'export', 'clear'])

// 方法
const handleCommand = (command) => {
  switch (command) {
    case 'import':
      fileInput.value?.click()
      break
    case 'export':
      handleExport()
      break
    case 'clear':
      handleClear()
      break
  }
}

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = JSON.parse(e.target.result)
      emit('import', data)
      ElMessage.success('数据导入成功')
    } catch (error) {
      console.error('数据解析失败:', error)
      ElMessage.error('数据格式错误，请检查文件格式')
    }
  }
  reader.readAsText(file)
  
  // 清空文件输入
  event.target.value = ''
}

const handleExport = () => {
  emit('export')
  ElMessage.success('数据导出完成')
}

const handleClear = () => {
  emit('clear')
  ElMessage.success('数据已清空')
}
</script>

<style lang="scss" scoped>
.data-import-export {
  // 样式可以根据需要添加
}
</style>