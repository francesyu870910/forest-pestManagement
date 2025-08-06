import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/',
    redirect: '/pest/identification'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      hideInMenu: true
    }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true, hideInMenu: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'Dashboard',
          requiresAuth: true,
          hideInMenu: true
        }
      }
    ]
  },
  {
    path: '/pest/identification',
    component: Layout,
    children: [
      {
        path: '',
        name: 'PestIdentification',
        component: () => import('@/views/pest/identification/index.vue'),
        meta: {
          title: '病虫害识别',
          icon: 'Search',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/pest/treatment-plan',
    component: Layout,
    children: [
      {
        path: '',
        name: 'TreatmentPlan',
        component: () => import('@/views/pest/treatment-plan/index.vue'),
        meta: {
          title: '防治方案',
          icon: 'Document',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/pest/evaluation',
    component: Layout,
    children: [
      {
        path: '',
        name: 'EffectEvaluation',
        component: () => import('@/views/pest/evaluation/index.vue'),
        meta: {
          title: '效果评估',
          icon: 'TrendCharts',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/forest/resource',
    component: Layout,
    children: [
      {
        path: '',
        name: 'ForestResource',
        component: () => import('@/views/forest/resource/index.vue'),
        meta: {
          title: '森林资源',
          icon: 'Sunny',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/warning',
    component: Layout,
    children: [
      {
        path: '',
        name: 'EarlyWarning',
        component: () => import('@/views/warning/index.vue'),
        meta: {
          title: '预警中心',
          icon: 'Warning',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/forest/knowledge',
    component: Layout,
    children: [
      {
        path: '',
        name: 'KnowledgeBase',
        component: () => import('@/views/forest/knowledge/index.vue'),
        meta: {
          title: '知识库管理',
          icon: 'Reading',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/system/user',
    component: Layout,
    children: [
      {
        path: '',
        name: 'UserManagement',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'User',
          requiresAuth: true,
          roles: ['admin']
        }
      }
    ]
  },

  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    meta: { hideInMenu: true },
    children: [
      {
        path: 'index',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true,
          hideInMenu: true
        }
      }
    ]
  },
  // 开发环境测试路由
  ...(import.meta.env.DEV
    ? [
        {
          path: '/test',
          component: Layout,
          meta: {
            title: '开发测试',
            icon: 'Tools',
            requiresAuth: true,
            hideInMenu: false
          },
          children: [
            {
              path: 'api',
              name: 'ApiTest',
              component: () => import('@/views/test/ApiTest.vue'),
              meta: {
                title: 'API测试',
                icon: 'Connection',
                requiresAuth: true
              }
            },
            {
              path: 'integration',
              name: 'IntegrationTest',
              component: () => import('@/views/test/IntegrationTest.vue'),
              meta: {
                title: '集成测试',
                icon: 'Cpu',
                requiresAuth: true
              }
            }
          ]
        }
      ]
    : []),

  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hideInMenu: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 白名单路由（不需要登录即可访问）
const whiteList = ['/login']

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 森林病虫害防治管理系统`
  }

  const userStore = useUserStore()
  const hasToken = userStore.isLoggedIn()

  if (hasToken) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      // 检查是否有用户信息
      if (!userStore.userInfo.id) {
        // 开发环境跳过API调用
        if (import.meta.env.DEV) {
          next()
        } else {
          try {
            // 获取用户信息
            await userStore.fetchUserInfo()
            next()
          } catch (error) {
            // 获取用户信息失败，清除token并跳转到登录页
            userStore.clearUserInfo()
            ElMessage.error('登录状态已过期，请重新登录')
            next(`/login?redirect=${to.path}`)
          }
        }
      } else {
        // 检查权限
        if (to.meta.requiresAuth !== false && to.meta.roles) {
          if (to.meta.roles.includes(userStore.userInfo.role)) {
            next()
          } else {
            ElMessage.error('您没有权限访问该页面')
            next('/dashboard')
          }
        } else {
          next()
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接进入
      next()
    } else {
      // 不在白名单中，重定向到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  ElMessage.error('页面加载失败')
})

export default router
