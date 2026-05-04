<template>
  <router-view />
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    router.push('/dashboard')
  }
}

onMounted(() => {
  document.addEventListener('keyup', handleEscKey)
})

onUnmounted(() => {
  document.removeEventListener('keyup', handleEscKey)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>