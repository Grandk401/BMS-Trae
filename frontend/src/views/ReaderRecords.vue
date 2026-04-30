<template>
  <div class="reader-borrow-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的借阅</span>
        </div>
      </template>

      <el-table :data="records" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bookTitle" label="书名" min-width="180" />
        <el-table-column prop="bookIsbn" label="ISBN" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.borrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应还日期" width="160">
          <template #default="scope">
            <span :class="{ 'overdue-text': isOverdue(scope.row) }">
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
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBorrowRecords } from '@/api'

const loading = ref(false)
const records = ref([])

const getStatusType = (status) => {
  const types = {
    PENDING: 'info',
    BORROWING: 'warning',
    RETURNED: 'success',
    OVERDUE: 'danger',
    OVERDUE_RETURNED: 'danger',
    REJECTED: 'default'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    BORROWING: '借阅中',
    RETURNED: '已归还',
    OVERDUE: '已逾期',
    OVERDUE_RETURNED: '已逾期但已归还',
    REJECTED: '已拒绝'
  }
  return texts[status] || status
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const isOverdue = (row) => {
  if (!row.dueDate || row.status === 'RETURNED' || row.status === 'OVERDUE_RETURNED' || row.status === 'REJECTED') {
    return false
  }
  return new Date(row.dueDate) < new Date()
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getBorrowRecords()
    if (res.success) {
      records.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取借阅记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.reader-borrow-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>