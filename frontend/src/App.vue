<template>
  <router-view />
</template>

<script>
export default {
  name: 'App',
  mounted() {
    document.addEventListener('keyup', this.handleEscKey)
  },
  beforeUnmount() {
    document.removeEventListener('keyup', this.handleEscKey)
  },
  methods: {
    handleEscKey(event) {
      if (event.key === 'Escape') {
        this.$router.push('/dashboard')
      }
    }
  }
}
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
