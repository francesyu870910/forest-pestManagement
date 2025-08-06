<template>
  <div v-if="!item.meta?.hideInMenu">
    <!-- 检查是否有子菜单 -->
    <template v-if="hasOneShowingChild(item.children, item)">
      <!-- 如果有多个子菜单，显示为子菜单 -->
      <el-sub-menu
        v-if="
          onlyOneChild.children &&
          onlyOneChild.children.length > 0 &&
          !onlyOneChild.noShowingChildren
        "
        :index="resolvePath(onlyOneChild.path)"
        :popper-append-to-body="true"
      >
        <template #title>
          <item
            :icon="onlyOneChild.meta?.icon || item.meta?.icon"
            :title="onlyOneChild.meta?.title || item.meta?.title"
          />
        </template>
        <sidebar-item
          v-for="child in onlyOneChild.children"
          :key="child.path"
          :is-nest="true"
          :item="child"
          :base-path="resolvePath(child.path)"
          class="nest-menu"
        />
      </el-sub-menu>

      <!-- 只有一个子菜单或没有子菜单的情况，显示为菜单项 -->
      <el-menu-item
        v-else
        :index="resolvePath(onlyOneChild.path)"
        :class="{ 'submenu-title-noDropdown': !isNest }"
      >
        <item
          :icon="onlyOneChild.meta?.icon || (item.meta && item.meta.icon)"
          :title="onlyOneChild.meta?.title"
        />
      </el-menu-item>
    </template>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import path from 'path-browserify'
import Item from './Item.vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  isNest: {
    type: Boolean,
    default: false
  },
  basePath: {
    type: String,
    default: ''
  }
})

const onlyOneChild = ref({})

// 判断是否只有一个显示的子菜单
const hasOneShowingChild = (children = [], parent) => {
  const showingChildren = children.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false
    } else {
      // 临时设置，用于判断是否只有一个子菜单
      onlyOneChild.value = item
      return true
    }
  })

  // 当只有一个子路由时，默认显示子路由
  if (showingChildren.length === 1) {
    return true
  }

  // 如果没有子路由显示，则显示父路由
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

// 解析路径
const resolvePath = (routePath) => {
  if (isExternalLink(routePath)) {
    return routePath
  }
  if (isExternalLink(props.basePath)) {
    return props.basePath
  }

  // 如果子路由path为空，直接返回basePath
  if (routePath === '' || routePath === undefined) {
    return props.basePath
  }

  return path.resolve(props.basePath, routePath)
}

// 判断是否为外部链接
const isExternalLink = (path) => {
  return /^(https?:|mailto:|tel:)/.test(path)
}
</script>

<style lang="scss" scoped>
.nest-menu .el-sub-menu > .el-sub-menu__title,
.el-sub-menu .el-menu-item {
  background-color: #1f2d3d !important;

  &:hover {
    background-color: #263445 !important;
  }
}

.submenu-title-noDropdown {
  &:hover {
    background-color: #263445 !important;
  }
}
</style>
