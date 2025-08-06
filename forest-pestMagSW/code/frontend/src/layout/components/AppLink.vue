<template>
  <!-- 外部链接 -->
  <a v-if="isExternal(to)" :href="to" target="_blank" rel="noopener">
    <slot />
  </a>

  <!-- 内部路由链接 -->
  <router-link v-else :to="to" custom v-slot="{ navigate, href }">
    <a :href="href" @click="navigate">
      <slot />
    </a>
  </router-link>
</template>

<script setup>
const props = defineProps({
  to: {
    type: String,
    required: true
  }
})

const isExternal = (path) => {
  return /^(https?:|mailto:|tel:)/.test(path)
}
</script>
