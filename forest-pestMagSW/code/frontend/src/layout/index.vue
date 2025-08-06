<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo" @click="goHome">
        <el-icon v-if="!appStore.sidebar.opened || device === 'mobile'" class="logo-icon" size="32">
          <Sunny />
        </el-icon>
        <div v-if="appStore.sidebar.opened && device !== 'mobile'" class="logo-text">
          <div class="logo-top">
            <el-icon class="logo-icon-text" size="24">
              <Sunny />
            </el-icon>
          </div>
          <div class="text-content">
            <h3>
              <div class="title-line">森林病虫害防治数字化</div>
              <div class="title-line">管理系统</div>
            </h3>
          </div>
        </div>
      </div>

      <el-scrollbar class="sidebar-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="!appStore.sidebar.opened"
          :unique-opened="true"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <sidebar-item
            v-for="route in permissionRoutes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
          />
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header" :class="{ mobile: device === 'mobile' }">
        <div class="header-left">
          <el-button
            type="text"
            @click="toggleSidebar"
            class="sidebar-toggle"
            :icon="appStore.sidebar.opened ? Fold : Expand"
          />

          <!-- 面包屑导航 -->
          <breadcrumb v-if="device !== 'mobile'" class="breadcrumb" />
        </div>

        <div class="header-right">
          <!-- 全屏按钮 -->
          <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
            <el-button type="text" @click="toggleFullscreen" class="header-btn">
              <el-icon>
                <FullScreen v-if="!isFullscreen" />
                <Close v-else />
              </el-icon>
            </el-button>
          </el-tooltip>

          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span v-if="device !== 'mobile'" class="user-name">
                {{ userStore.userInfo.name || userStore.userInfo.username || '用户' }}
              </span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <div class="app-main">
          <router-view v-slot="{ Component, route }">
            <transition name="fade-transform" mode="out-in">
              <keep-alive :include="cachedViews">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>

    <!-- 移动端遮罩 -->
    <div
      v-if="device === 'mobile' && appStore.sidebar.opened"
      class="drawer-bg"
      @click="handleClickOutside"
    />
  </el-container>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import SidebarItem from './components/SidebarItem.vue'
import Breadcrumb from './components/Breadcrumb.vue'
import {
  Fold,
  Expand,
  User,
  ArrowDown,
  Setting,
  SwitchButton,
  FullScreen,
  Close,
  Sunny
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

// 响应式状态
const isFullscreen = ref(false)
const cachedViews = ref([])

// 计算属性
const sidebarWidth = computed(() => {
  return appStore.sidebar.opened ? '200px' : '64px'
})

const device = computed(() => appStore.device)

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

// 根据用户权限过滤路由
const permissionRoutes = computed(() => {
  const routes = router.getRoutes()

  // 过滤出需要在菜单中显示的路由
  const menuRoutes = routes.filter((route) => {
    // 必须有子路由且子路由有meta信息
    return (
      route.children &&
      route.children.length > 0 &&
      route.children[0].meta &&
      route.children[0].meta.title &&
      !route.children[0].meta.hideInMenu
    )
  })

  const filteredRoutes = filterRoutes(menuRoutes)

  // 手动排序以确保正确的显示顺序
  const menuOrder = [
    '病虫害识别',
    '防治方案',
    '效果评估',
    '森林资源',
    '预警中心',
    '知识库管理',
    '用户管理'
  ]

  const sortedRoutes = filteredRoutes.sort((a, b) => {
    const titleA = a.children[0].meta.title
    const titleB = b.children[0].meta.title
    const indexA = menuOrder.indexOf(titleA)
    const indexB = menuOrder.indexOf(titleB)

    // 如果在排序列表中找不到，放到最后
    if (indexA === -1) return 1
    if (indexB === -1) return -1

    return indexA - indexB
  })

  return sortedRoutes
})

// 过滤路由
const filterRoutes = (routes) => {
  const res = []

  routes.forEach((route) => {
    const tmp = { ...route }

    // 对于有子路由的情况，检查子路由的权限
    if (tmp.children && tmp.children.length > 0) {
      const child = tmp.children[0] // 我们的结构中每个路由只有一个子路由

      // 检查子路由权限
      if (hasPermission({ meta: child.meta })) {
        // 不在菜单中隐藏的路由才显示
        if (!child.meta?.hideInMenu && child.meta?.title) {
          res.push(tmp)
        }
      }
    }
  })

  return res
}

// 检查权限
const hasPermission = (route) => {
  if (route.meta && route.meta.roles) {
    const userRole = userStore.userInfo.role || 'guest'
    return route.meta.roles.includes(userRole)
  }
  return true
}

// 方法
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleMenuSelect = (index) => {
  // 手动导航到选中的路由
  if (index && index !== route.path) {
    router.push(index).catch((err) => {
      console.error('导航失败:', err)
    })
  }
}

const goHome = () => {
  router.push('/')
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile/index')
      break
    case 'logout':
      userStore.userLogout()
      break
  }
}

const handleClickOutside = () => {
  appStore.closeSidebar(false)
}

// 全屏功能
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 响应式处理
const handleResize = () => {
  const rect = document.body.getBoundingClientRect()
  const isMobile = rect.width - 1 < 992

  appStore.setDevice(isMobile ? 'mobile' : 'desktop')

  if (isMobile) {
    appStore.closeSidebar(true)
  }
}

// 生命周期
onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  width: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.28s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  position: relative;
  z-index: 1001;
}

.logo {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border-bottom: 1px solid #434a50;
  cursor: pointer;
  transition: all 0.3s;
  padding: 10px 8px;

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  .logo-icon {
    color: #67c23a;
  }

  .logo-text {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;

    .logo-top {
      display: flex;
      justify-content: center;
      margin-bottom: 10px;

      .logo-icon-text {
        color: #67c23a;
      }
    }

    .text-content {
      text-align: center;
      width: 100%;

      h3 {
        margin: 0;
        font-size: 14px;
        font-weight: 600;
        line-height: 1.3;
        text-align: center;

        .title-line {
          margin-bottom: 2px;
          
          &:last-child {
            margin-bottom: 0;
          }
        }
      }

      p {
        margin: 2px 0 0;
        font-size: 12px;
        opacity: 0.8;
        line-height: 1;
      }
    }
  }
}

.sidebar-scrollbar {
  height: calc(100vh - 100px);

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }
}

.sidebar-menu {
  border: none;
  height: 100%;
  width: 100% !important;
}

.main-container {
  position: relative;
  min-height: 100%;
  transition: margin-left 0.28s;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  &.mobile {
    padding: 0 15px;
  }
}

.header-left {
  display: flex;
  align-items: center;

  .sidebar-toggle {
    font-size: 18px;
    color: #606266;
    margin-right: 15px;

    &:hover {
      color: #409eff;
    }
  }

  .breadcrumb {
    margin-left: 10px;
  }
}

.header-right {
  display: flex;
  align-items: center;

  .header-btn {
    font-size: 18px;
    color: #606266;
    margin-right: 15px;

    &:hover {
      color: #409eff;
    }
  }
}

.user-dropdown {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    color: #606266;
    transition: color 0.3s;

    &:hover {
      color: #409eff;
    }

    .user-avatar {
      margin-right: 8px;
    }

    .user-name {
      margin-right: 5px;
      font-size: 14px;
    }
  }
}

.main-content {
  background-color: #f0f2f5;
  padding: 0;
  min-height: calc(100vh - 60px);
}

.app-main {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

// 过渡动画
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

// 移动端适配
@media (max-width: 992px) {
  .sidebar {
    position: fixed !important;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1001;
    transition: transform 0.28s;

    &.hideSidebar {
      transform: translateX(-200px);
    }
  }

  .main-container {
    margin-left: 0 !important;
  }

  .header {
    padding: 0 15px;
  }
}
</style>
