<template>
  <div class="menu-item">
    <el-icon v-if="iconComponent" class="menu-icon">
      <component :is="iconComponent" />
    </el-icon>
    <span class="menu-title">{{ title }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  icon: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  }
})

// 动态获取图标组件
const iconComponent = computed(() => {
  if (props.icon && ElementPlusIconsVue[props.icon]) {
    return ElementPlusIconsVue[props.icon]
  }
  return null
})
</script>

<style lang="scss" scoped>
.menu-item {
  display: flex;
  align-items: center;

  .menu-icon {
    margin-right: 8px;
    font-size: 16px;
  }

  .menu-title {
    font-size: 14px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
