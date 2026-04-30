<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <span>用户管理</span>
          <el-button
            type="primary"
            size="small"
            @click="handleCreateClick"
          >
            创建用户
          </el-button>
        </div>
      </template>
      
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建用户弹窗 - 使用原生 HTML -->
    <div v-if="showCreateDialog" style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 9999;">
      <div style="background: white; padding: 20px; border-radius: 8px; width: 400px;">
        <h3>创建新用户</h3>
        <div style="margin: 10px 0;">
          <label>用户名:</label>
          <input v-model="formData.username" placeholder="请输入用户名" style="width: 100%; padding: 8px;" />
        </div>
        <div style="margin: 10px 0;">
          <label>密码:</label>
          <input type="password" v-model="formData.password" placeholder="请输入密码" style="width: 100%; padding: 8px;" />
        </div>
        <div style="margin: 10px 0;">
          <label>角色:</label>
          <select v-model="formData.role" style="width: 100%; padding: 8px;">
            <option value="ADMIN">系统管理员</option>
            <option value="LIBRARIAN">图书管理员</option>
            <option value="READER">读者</option>
          </select>
        </div>
        <div style="margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px;">
          <button @click="showCreateDialog = false" style="padding: 8px 16px;">取消</button>
          <button @click="handleCreate" style="padding: 8px 16px; background: #409eff; color: white;">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUsers, createUser, deleteUser } from '../api'

const userList = ref([])
const showCreateDialog = ref(false)
const formData = ref({
  username: '',
  password: '',
  role: 'READER'
})

const handleCreateClick = () => {
  showCreateDialog.value = true
}

const loadUsers = async () => {
  try {
    const response = await getUsers()
    userList.value = response.data
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  }
}

const handleCreate = async () => {
  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('用户名和密码不能为空')
    return
  }
  try {
    await createUser(formData.value)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    formData.value = { username: '', password: '', role: 'READER' }
    loadUsers()
  } catch (error) {
    ElMessage.error('创建失败: ' + error.message)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteUser(id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('删除失败: ' + error.message)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
