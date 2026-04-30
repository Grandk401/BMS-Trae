<template>
  <div class="announcement-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="openAddDialog">
            <span class="icon">➕</span> 添加公告
          </el-button>
        </div>
      </template>

      <el-table :data="announcements" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="content" label="内容" min-width="300" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'warning'">
              {{ scope.row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            <el-button size="small" :type="scope.row.enabled ? 'warning' : 'success'" 
                       @click="toggleEnabled(scope.row)">
              {{ scope.row.enabled ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入公告内容" :rows="4" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input v-model.number="form.sortOrder" placeholder="数字越小越靠前，默认为0" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllAnnouncements, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '../api/announcement'
import { ElMessage } from 'element-plus'

const announcements = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加公告')
const isEdit = ref(false)

const form = ref({
  title: '',
  content: '',
  enabled: true,
  sortOrder: 0
})

const fetchAnnouncements = async () => {
  try {
    const res = await getAllAnnouncements()
    if (res.success) {
      announcements.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取公告列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加公告'
  form.value = {
    title: '',
    content: '',
    enabled: true,
    sortOrder: 0
  }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  form.value = {
    id: row.id,
    title: row.title,
    content: row.content,
    enabled: row.enabled,
    sortOrder: row.sortOrder || 0
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.error('请填写标题和内容')
    return
  }

  try {
    if (isEdit.value) {
      await updateAnnouncement(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addAnnouncement(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchAnnouncements()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

const handleDelete = async (row) => {
  if (!confirm(`确定删除公告「${row.title}」吗？`)) {
    return
  }

  try {
    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const toggleEnabled = async (row) => {
  try {
    await updateAnnouncement(row.id, {
      ...row,
      enabled: !row.enabled
    })
    ElMessage.success(row.enabled ? '已禁用' : '已启用')
    fetchAnnouncements()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  padding: 20px;
}

.page-card {
  height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 18px;
  font-weight: bold;
}

.icon {
  margin-right: 5px;
}
</style>
