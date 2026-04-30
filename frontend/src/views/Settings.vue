<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settings.systemName" />
        </el-form-item>
        <el-form-item label="系统版本">
          <el-input v-model="settings.version" disabled />
        </el-form-item>
        <el-form-item label="数据库状态">
          <el-tag type="success">已连接</el-tag>
        </el-form-item>
        <el-form-item label="后端服务地址">
          <el-input v-model="settings.apiUrl" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">保存设置</el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <div class="system-info">
        <h4>系统信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="前端框架">Vue 3</el-descriptions-item>
          <el-descriptions-item label="UI框架">Element Plus</el-descriptions-item>
          <el-descriptions-item label="后端框架">Spring Boot 2.7.x</el-descriptions-item>
          <el-descriptions-item label="持久层框架">MyBatis</el-descriptions-item>
          <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
          <el-descriptions-item label="认证方式">JWT</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const settings = reactive({
  systemName: '图书管理系统',
  version: '1.0.0',
  apiUrl: 'http://localhost:8080'
})
</script>

<style scoped>
.settings-container {
  height: 100%;
}

.system-info {
  margin-top: 30px;
}

.system-info h4 {
  margin-bottom: 15px;
}
</style>
