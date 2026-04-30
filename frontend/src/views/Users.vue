<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>
      <el-alert
        title="功能说明"
        type="info"
        :closable="false"
        description="当前版本为简化版本，用户管理功能暂未实现完整。管理员账号：admin，密码：123456"
        show-icon
      />
      <div class="placeholder-content">
        <el-icon size="64" color="#ccc"><User /></el-icon>
        <p>用户管理功能开发中...</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.users-container {
  height: 100%;
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.placeholder-content p {
  margin-top: 20px;
  font-size: 16px;
}
</style>
