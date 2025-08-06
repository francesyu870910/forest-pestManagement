import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebar = ref({
    opened: true,
    withoutAnimation: false
  })

  const device = ref('desktop')
  const size = ref('default')

  // 切换侧边栏
  const toggleSidebar = () => {
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = false
  }

  // 关闭侧边栏
  const closeSidebar = (withoutAnimation) => {
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }

  // 设置设备类型
  const setDevice = (deviceType) => {
    device.value = deviceType
  }

  // 设置组件大小
  const setSize = (sizeType) => {
    size.value = sizeType
  }

  return {
    sidebar,
    device,
    size,
    toggleSidebar,
    closeSidebar,
    setDevice,
    setSize
  }
})
