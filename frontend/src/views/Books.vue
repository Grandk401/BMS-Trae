<template>
  <div class="books-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button type="primary" @click="handleAdd" v-if="canCreate">添加图书</el-button>
        </div>
      </template>

      <el-table :data="books" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="isbn" label="ISBN" width="120" />
        <el-table-column prop="title" label="书名" width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="80" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="操作" fixed="right" width="180" v-if="canUpdate || canDelete">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)" v-if="canUpdate">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="bookForm" :rules="rules" ref="bookFormRef" label-width="100px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="bookForm.category" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBooks, addBook, updateBook, deleteBook } from '@/api'
import { hasPermission, PermissionCode } from '../utils/permission'

const loading = ref(false)
const books = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加图书')
const bookFormRef = ref(null)

const bookForm = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  category: '',
  price: 0,
  stock: 0,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }]
}

// 权限判断
const canCreate = computed(() => hasPermission(PermissionCode.BOOK_CREATE))
const canUpdate = computed(() => hasPermission(PermissionCode.BOOK_UPDATE))
const canDelete = computed(() => hasPermission(PermissionCode.BOOK_DELETE))

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

const handleAdd = () => {
  dialogTitle.value = '添加图书'
  Object.keys(bookForm).forEach(key => {
    if (key !== 'price' && key !== 'stock') {
      bookForm[key] = ''
    } else {
      bookForm[key] = 0
    }
  })
  bookForm.id = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑图书'
  Object.assign(bookForm, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!bookFormRef.value) return

  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log('提交数据:', bookForm)
        const res = bookForm.id ? await updateBook(bookForm.id, bookForm) : await addBook(bookForm)
        console.log('响应:', res)
        if (res.success) {
          ElMessage.success(res.message)
          dialogVisible.value = false
          fetchBooks()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + (error.message || JSON.stringify(error)))
      }
    }
  })
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这本书吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteBook(id)
    if (res.success) {
      ElMessage.success('删除成功')
      fetchBooks()
    }
  } catch {
  }
}

onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.books-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
