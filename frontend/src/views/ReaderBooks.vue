<template>
  <div class="reader-books-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>在馆图书</span>
        </div>
      </template>

      <el-table :data="books" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="isbn" label="ISBN" width="120" />
        <el-table-column prop="title" label="书名" width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="stock" label="库存" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.stock > 0 ? 'success' : 'danger'">
              {{ scope.row.stock > 0 ? scope.row.stock : '无库存' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              :disabled="scope.row.stock <= 0"
              @click="handleBorrow(scope.row)"
            >
              借阅
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBooks, applyBorrow } from '@/api'

const loading = ref(false)
const books = ref([])

const fetchBooks = async () => {
  loading.value = true
  try {
    const res = await getBooks()
    if (res.success) {
      books.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleBorrow = async (book) => {
  try {
    const res = await applyBorrow(book.id)

    if (res.success) {
      ElMessage.success(res.message)
      fetchBooks()
    } else {
      ElMessage.error(res.message || '借阅申请失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '借阅申请失败')
  }
}

onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.reader-books-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>