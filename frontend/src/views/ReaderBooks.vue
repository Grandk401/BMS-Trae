<template>
  <div class="reader-books-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>在馆图书</span>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form" @keyup.enter="handleSearch">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="书名" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="作者" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="searchForm.isbn" placeholder="ISBN" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select 
            v-model="searchForm.category" 
            placeholder="请选择分类" 
            style="width: 150px"
            :popper-append-to-body="false"
            :loading="categoriesLoading"
            @visible-change="handleCategoryDropdownVisible"
            @change="handleSearch"
          >
            <el-option 
              key="all" 
              label="全部" 
              value="" 
            />
            <el-option 
              v-for="category in categories" 
              :key="category" 
              :label="category" 
              :value="category" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="searchForm.publisher" placeholder="出版社" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

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
import { getBooks, searchBooks, applyBorrow, getCategories } from '@/api'

const loading = ref(false)
const books = ref([])
const categories = ref([])
const categoriesLoading = ref(false)
const searchForm = ref({
  title: '',
  author: '',
  isbn: '',
  category: '',
  publisher: ''
})

const fetchBooks = async () => {
  loading.value = true
  try {
    const hasSearchParams = searchForm.value.title || searchForm.value.author ||
                          searchForm.value.isbn || searchForm.value.category ||
                          searchForm.value.publisher
    if (hasSearchParams) {
      const res = await searchBooks({
        title: searchForm.value.title || undefined,
        author: searchForm.value.author || undefined,
        isbn: searchForm.value.isbn || undefined,
        category: searchForm.value.category || undefined,
        publisher: searchForm.value.publisher || undefined
      })
      if (res.success) {
        books.value = res.data || []
      }
    } else {
      const res = await getBooks()
      if (res.success) {
        books.value = res.data || []
      }
    }
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchBooks()
}

const handleReset = () => {
  searchForm.value = {
    title: '',
    author: '',
    isbn: '',
    category: '',
    publisher: ''
  }
  fetchBooks()
}

const fetchCategories = async () => {
  categoriesLoading.value = true
  try {
    const res = await getCategories()
    if (res.success) {
      categories.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    categoriesLoading.value = false
  }
}

const handleCategoryDropdownVisible = async (visible) => {
  if (visible && categories.value.length === 0) {
    await fetchCategories()
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

.search-form {
  margin-bottom: 20px;
}
</style>