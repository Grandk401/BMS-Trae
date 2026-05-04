<template>
  <div class="borrow-records-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>借阅管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="图书名">
            <el-input v-model="searchForm.bookName" placeholder="请输入图书名" style="width: 150px" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" style="width: 150px" />
          </el-form-item>
          <el-form-item label="借阅日期">
            <el-date-picker v-model="searchForm.borrowDateStart" type="date" placeholder="开始日期" />
            <span style="margin: 0 5px">至</span>
            <el-date-picker v-model="searchForm.borrowDateEnd" type="date" placeholder="结束日期" />
          </el-form-item>
          <el-form-item label="应还日期">
            <el-date-picker v-model="searchForm.dueDateStart" type="date" placeholder="开始日期" />
            <span style="margin: 0 5px">至</span>
            <el-date-picker v-model="searchForm.dueDateEnd" type="date" placeholder="结束日期" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="records" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bookName" label="图书名" min-width="150" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.borrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应还日期" width="160">
          <template #default="scope">
            <span :class="{ 'overdue-text': scope.row.status === 'OVERDUE' || scope.row.status === 'OVERDUE_RETURNED' }">
              {{ formatDateTime(scope.row.dueDate) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="160">
          <template #default="scope">
            {{ scope.row.returnDate ? formatDateTime(scope.row.returnDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="scope">
            <el-button size="small" type="success" @click="handleApprove(scope.row)"
                       v-if="scope.row.status === 'PENDING'">同意</el-button>
            <el-button size="small" type="danger" @click="handleReject(scope.row)"
                       v-if="scope.row.status === 'PENDING'">拒绝</el-button>
            <el-button size="small" type="primary" @click="handleConfirmReturn(scope.row)"
                       v-if="scope.row.status === 'BORROWING' || scope.row.status === 'OVERDUE'">已归还</el-button>
            <el-button size="small" type="success" @click="handleApproveRenew(scope.row)"
                       v-if="scope.row.status === 'RENEW_PENDING'">同意续借</el-button>
            <el-button size="small" type="danger" @click="handleRejectRenew(scope.row)"
                       v-if="scope.row.status === 'RENEW_PENDING'">拒绝续借</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBorrowRecords, approveBorrow, rejectBorrow, confirmReturn, searchBorrowRecords, approveRenew, rejectRenew } from '@/api'

const loading = ref(false)
const records = ref([])
let isMounted = ref(false)

const searchForm = reactive({
  bookName: '',
  username: '',
  borrowDateStart: null,
  borrowDateEnd: null,
  dueDateStart: null,
  dueDateEnd: null
})

const fetchRecords = async (isSearch = false) => {
  if (!isMounted.value) return
  loading.value = true
  try {
    let res
    if (isSearch) {
      const params = {
        bookName: searchForm.bookName || undefined,
        username: searchForm.username || undefined,
        borrowDateStart: searchForm.borrowDateStart ? new Date(searchForm.borrowDateStart).toISOString() : undefined,
        borrowDateEnd: searchForm.borrowDateEnd ? new Date(searchForm.borrowDateEnd).toISOString() : undefined,
        dueDateStart: searchForm.dueDateStart ? new Date(searchForm.dueDateStart).toISOString() : undefined,
        dueDateEnd: searchForm.dueDateEnd ? new Date(searchForm.dueDateEnd).toISOString() : undefined
      }
      res = await searchBorrowRecords(params)
    } else {
      res = await getBorrowRecords()
    }
    if (res.success && isMounted.value) {
      records.value = res.data || []
    }
  } catch (error) {
    if (isMounted.value) {
      ElMessage.error('获取借阅记录失败')
    }
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const handleSearch = () => {
  fetchRecords(true)
}

const handleReset = () => {
  searchForm.bookName = ''
  searchForm.username = ''
  searchForm.borrowDateStart = null
  searchForm.borrowDateEnd = null
  searchForm.dueDateStart = null
  searchForm.dueDateEnd = null
  fetchRecords()
}

const handleApprove = async (row) => {
  try {
    const res = await approveBorrow(row.id)
    if (res.success) {
      ElMessage.success(res.message)
      fetchRecords()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    const res = await rejectBorrow(row.id)
    if (res.success) {
      ElMessage.success(res.message)
      fetchRecords()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleConfirmReturn = async (row) => {
  try {
    const res = await confirmReturn(row.id)
    if (res.success) {
      ElMessage.success(res.message)
      fetchRecords()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleApproveRenew = async (row) => {
  try {
    const res = await approveRenew(row.id)
    if (res.success) {
      ElMessage.success(res.message)
      fetchRecords()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRejectRenew = async (row) => {
  try {
    const res = await rejectRenew(row.id)
    if (res.success) {
      ElMessage.success(res.message)
      fetchRecords()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'BORROWING': '借阅中',
    'RETURNED': '已归还',
    'OVERDUE': '已逾期',
    'OVERDUE_RETURNED': '已逾期但已归还',
    'REJECTED': '已拒绝',
    'RENEW_PENDING': '待续借审核',
    'RENEWED': '已续借',
    'RENEW_REJECTED': '续借被拒绝'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'BORROWING': 'warning',
    'RETURNED': 'success',
    'OVERDUE': 'danger',
    'OVERDUE_RETURNED': 'danger',
    'REJECTED': 'default',
    'RENEW_PENDING': 'warning',
    'RENEWED': 'success',
    'RENEW_REJECTED': 'default'
  }
  return typeMap[status] || 'default'
}

onMounted(() => {
  isMounted.value = true
  fetchRecords()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style scoped>
.borrow-records-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>