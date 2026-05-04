<template>
  <div class="operation-logs-container">
    <el-card>
      <template #header>
        <div class="header-content">
          <span>操作日志</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form" @keyup.enter="handleSearch">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="用户名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="模块">
          <el-select v-model="searchForm.module" placeholder="模块" clearable style="width: 150px">
            <el-option label="图书" value="BOOK" />
            <el-option label="用户" value="USER" />
            <el-option label="借阅记录" value="BORROW_RECORD" />
            <el-option label="认证" value="AUTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="操作类型" clearable style="width: 120px">
            <el-option label="新增" value="ADD" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="查询" value="QUERY" />
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 日志表格 -->
      <el-table :data="logList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="模块" width="120">
          <template #default="scope">
            <el-tag>{{ getModuleLabel(scope.row.module) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作类型" width="100">
          <template #default="scope">
            <el-tag :type="getOperationTypeTag(scope.row.operationType)">
              {{ getOperationTypeLabel(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="targetId" label="目标ID" width="80" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column label="操作时间" width="170">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOperationLogs } from '../api'

const logList = ref([])
const loading = ref(false)
const searchForm = ref({
  username: '',
  module: '',
  operationType: ''
})
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getModuleLabel = (module) => {
  const moduleMap = {
    'BOOK': '图书',
    'USER': '用户',
    'BORROW_RECORD': '借阅记录',
    'AUTH': '认证'
  }
  return moduleMap[module] || module
}

const getOperationTypeLabel = (type) => {
  const typeMap = {
    'ADD': '新增',
    'UPDATE': '更新',
    'DELETE': '删除',
    'QUERY': '查询',
    'LOGIN': '登录',
    'LOGOUT': '登出'
  }
  return typeMap[type] || type
}

const getOperationTypeTag = (type) => {
  const tagMap = {
    'ADD': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'QUERY': 'info',
    'LOGIN': 'primary',
    'LOGOUT': 'info'
  }
  return tagMap[type] || 'info'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const loadLogs = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    }
    if (searchForm.value.username) params.username = searchForm.value.username
    if (searchForm.value.module) params.module = searchForm.value.module
    if (searchForm.value.operationType) params.operationType = searchForm.value.operationType

    const response = await getOperationLogs(params)
    logList.value = response.data.list || []
    pagination.value.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取操作日志失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.pageNum = 1
  loadLogs()
}

const handleReset = () => {
  searchForm.value = {
    username: '',
    module: '',
    operationType: ''
  }
  pagination.value.pageNum = 1
  loadLogs()
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.pageNum = 1
  loadLogs()
}

const handlePageChange = (page) => {
  pagination.value.pageNum = page
  loadLogs()
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.operation-logs-container {
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

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>