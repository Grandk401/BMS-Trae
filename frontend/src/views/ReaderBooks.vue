<template>
  <div class="reader-books-container">
    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 搜索区域 -->
      <section class="search-section">
        <el-card shadow="hover">
          <el-form :model="searchForm" inline class="search-form" @keyup.enter="handleSearch">
            <el-form-item label="书名">
              <el-input v-model="searchForm.title" placeholder="书名" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item label="作者">
              <el-input v-model="searchForm.author" placeholder="作者" clearable style="width: 120px" />
            </el-form-item>
            <el-form-item label="ISBN">
              <el-input v-model="searchForm.isbn" placeholder="ISBN" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select
                v-model="searchForm.category"
                placeholder="请选择分类"
                style="width: 140px"
                :popper-append-to-body="false"
                :loading="categoriesLoading"
                @visible-change="handleCategoryDropdownVisible"
                clearable
              >
                <el-option
                  v-for="category in categories"
                  :key="category"
                  :label="category"
                  :value="category"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="出版社">
              <el-input v-model="searchForm.publisher" placeholder="出版社" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon> 搜索
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon> 重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </section>

      <!-- 图书卡片展示区域 -->
      <section class="books-grid-section">
        <!-- 分页信息 -->
        <div class="pagination-info" v-if="total > 0">
          共找到 <span class="highlight">{{ total }}</span> 本图书，当前显示第 <span class="highlight">{{ (currentPage - 1) * pageSize + 1 }}</span> - <span class="highlight">{{ Math.min(currentPage * pageSize, total) }}</span> 本
        </div>

        <!-- 图书卡片网格 -->
        <div class="books-grid" v-loading="loading">
          <el-empty v-if="!loading && books.length === 0" description="暂无图书" />

          <div
            v-for="book in books"
            :key="book.id"
            class="book-card"
          >
            <!-- 封面图片 -->
            <div class="book-cover">
              <img
                v-if="book.coverImage"
                :src="book.coverImage"
                :alt="book.title"
                @error="handleImageError($event)"
              />
              <div v-else class="cover-placeholder">
                <el-icon class="placeholder-icon"><Notebook /></el-icon>
                <span>暂无封面</span>
              </div>
              <!-- 库存标签 -->
              <div class="stock-tag" :class="{ 'low-stock': book.stock <= 3 && book.stock > 0, 'no-stock': book.stock <= 0 }">
                {{ book.stock > 0 ? `剩余 ${book.stock} 本` : '暂无库存' }}
              </div>
            </div>

            <!-- 图书信息 -->
            <div class="book-info">
              <h3 class="book-title" :title="book.title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-publisher">{{ book.publisher }}</p>
              <div class="book-meta">
                <el-tag size="small" type="info">{{ book.category }}</el-tag>
                <span class="book-price">¥{{ book.price }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="book-actions">
              <el-button
                type="primary"
                :disabled="book.stock <= 0"
                @click="handleBorrow(book)"
                class="borrow-btn"
              >
                <el-icon><Tickets /></el-icon>
                {{ book.stock > 0 ? '立即借阅' : '暂无可借' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 36, 48]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Notebook, Tickets } from '@element-plus/icons-vue'
import { searchBooksPage, applyBorrow, getCategories } from '@/api'

const loading = ref(false)
const books = ref([])
const categories = ref([])
const categoriesLoading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

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

    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    if (searchForm.value.title) params.title = searchForm.value.title
    if (searchForm.value.author) params.author = searchForm.value.author
    if (searchForm.value.isbn) params.isbn = searchForm.value.isbn
    if (searchForm.value.category) params.category = searchForm.value.category
    if (searchForm.value.publisher) params.publisher = searchForm.value.publisher

    const res = hasSearchParams
      ? await searchBooksPage(params)
      : await searchBooksPage({ page: currentPage.value, size: pageSize.value })

    if (res.success) {
      books.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取图书列表失败')
    }
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
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
  currentPage.value = 1
  fetchBooks()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBooks()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
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
      ElMessage.success(res.message || '借阅申请成功！')
      fetchBooks()
    } else {
      ElMessage.error(res.message || '借阅申请失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '借阅申请失败')
  }
}

const handleImageError = (event) => {
  event.target.style.display = 'none'
  event.target.parentNode.querySelector('.cover-placeholder')?.classList.add('show')
}

const handleLogout = () => {
  localStorage.clear()
  window.location.href = '/login'
}

onMounted(() => {
  fetchBooks()
  fetchCategories()
})
</script>

<style scoped>
.reader-books-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
}

/* 顶部导航 */
.top-nav {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.3);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-item {
  padding: 8px 20px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
}

.nav-item.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.25);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
}

.user-info span {
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 图书网格区域 */
.books-grid-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.pagination-info {
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
}

.pagination-info .highlight {
  color: #667eea;
  font-weight: 600;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.book-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.book-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

/* 封面图片 */
.book-cover {
  position: relative;
  width: 100%;
  height: 160px;
  background: #f5f7fa;
  overflow: hidden;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-card:hover .book-cover img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  color: #909399;
}

.cover-placeholder.show {
  display: flex;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 8px;
  color: #c0c4cc;
}

.stock-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(103, 194, 58, 0.9);
  color: #fff;
}

.stock-tag.low-stock {
  background: rgba(230, 162, 60, 0.9);
}

.stock-tag.no-stock {
  background: rgba(245, 108, 108, 0.9);
}

/* 图书信息 */
.book-info {
  padding: 16px;
}

.book-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 13px;
  color: #606266;
  margin: 0 0 4px 0;
}

.book-publisher {
  font-size: 12px;
  color: #909399;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.book-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

/* 操作按钮 */
.book-actions {
  padding: 0 16px 16px;
}

.borrow-btn {
  width: 100%;
  border-radius: 20px;
  font-weight: 600;
}

/* 分页控件 */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-hover-color: #667eea;
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #667eea;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .nav-content {
    flex-wrap: wrap;
    height: auto;
    padding: 12px;
  }

  .nav-links {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 12px;
  }

  .main-content {
    padding: 16px;
  }

  .books-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }

  .book-cover {
    height: 180px;
  }
}
</style>
