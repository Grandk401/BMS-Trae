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

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form" @keyup.enter="handleSearch">
        <el-form-item label="用户ID">
          <el-input v-model.number="searchForm.id" placeholder="用户ID" clearable style="width: 100px" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="用户名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="角色" clearable style="width: 120px">
            <el-option label="系统管理员" value="ADMIN" />
            <el-option label="图书管理员" value="LIBRARIAN" />
            <el-option label="读者" value="READER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.enabled" placeholder="状态" clearable style="width: 100px">
            <el-option label="正常" :value="true" />
            <el-option label="已禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色" width="180">
          <template #default="scope">
            <el-select
              v-if="scope.row.id !== currentUserId && (currentRole === 'ADMIN' || scope.row.role !== 'ADMIN')"
              v-model="scope.row.role"
              @change="handleRoleChange(scope.row)"
              size="small"
              style="width: 120px"
            >
              <el-option v-if="currentRole === 'ADMIN'" label="系统管理员" value="ADMIN" />
              <el-option label="图书管理员" value="LIBRARIAN" />
              <el-option label="读者" value="READER" />
            </el-select>
            <span v-else>{{ getRoleLabel(scope.row.role) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'" size="small">
              {{ scope.row.enabled ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.id !== currentUserId && (currentRole === 'ADMIN' || scope.row.role !== 'ADMIN')"
              size="small"
              :type="scope.row.enabled ? 'warning' : 'success'"
              @click="handleToggleEnabled(scope.row)"
            >
              {{ scope.row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.id)"
            >删除</el-button>
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
            <option value="ADMIN" v-if="currentRole === 'ADMIN'">系统管理员</option>
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
import { getUsers, searchUsers, createUser, updateUserRole, setUserEnabled, deleteUser } from '../api'

const userList = ref([])
const showCreateDialog = ref(false)
const currentUserId = ref(null)
const currentRole = ref('')
const formData = ref({
  username: '',
  password: '',
  role: 'READER'
})
const searchForm = ref({
  id: '',
  username: '',
  role: '',
  enabled: ''
})

const handleCreateClick = () => {
  showCreateDialog.value = true
}

const getRoleLabel = (role) => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'LIBRARIAN': '图书管理员',
    'READER': '读者'
  }
  return roleMap[role] || role
}

const handleRoleChange = async (row) => {
  try {
    await updateUserRole(row.id, row.role)
    ElMessage.success('角色修改成功')
  } catch (error) {
    ElMessage.error('角色修改失败: ' + error.message)
    loadUsers()
  }
}

const handleToggleEnabled = async (row) => {
  const action = row.enabled ? '禁用' : '启用'
  try {
    await setUserEnabled(row.id, !row.enabled)
    ElMessage.success(`${action}成功`)
    loadUsers()
  } catch (error) {
    ElMessage.error(`${action}失败: ` + error.message)
  }
}

const loadUsers = async () => {
  try {
    const hasSearchParams = searchForm.value.id || searchForm.value.username || 
                          searchForm.value.role || searchForm.value.enabled !== ''
    if (hasSearchParams) {
      const params = {}
      if (searchForm.value.id) params.id = searchForm.value.id
      if (searchForm.value.username) params.username = searchForm.value.username
      if (searchForm.value.role) params.role = searchForm.value.role
      if (searchForm.value.enabled !== '') params.enabled = searchForm.value.enabled
      const response = await searchUsers(params)
      userList.value = response.data
    } else {
      const response = await getUsers()
      userList.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  }
}

const handleSearch = () => {
  loadUsers()
}

const handleReset = () => {
  searchForm.value = {
    id: '',
    username: '',
    role: '',
    enabled: ''
  }
  loadUsers()
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
  currentUserId.value = parseInt(localStorage.getItem('userId') || '0')
  currentRole.value = localStorage.getItem('role') || ''
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

.search-form {
  margin-bottom: 20px;
}
</style>
