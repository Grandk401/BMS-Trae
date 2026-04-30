<template>
  <component :is="currentComponent" />
</template>

<script setup>
import { computed } from 'vue'
import ReaderHome from './ReaderHome.vue'

const currentComponent = computed(() => {
  // 所有角色都显示ReaderHome（图书馆介绍页面）
  return ReaderHome
})
</script>
