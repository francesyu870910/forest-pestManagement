import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 初始化用户认证状态
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()

// 如果有token，尝试获取用户信息
if (userStore.isLoggedIn() && !userStore.userInfo.id) {
  userStore.fetchUserInfo().catch(() => {
    // 获取用户信息失败，清除认证状态
    userStore.clearUserInfo()
  })
}

app.mount('#app')
