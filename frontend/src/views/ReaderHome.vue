<template>
  <div class="reader-home-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎来到图书馆</span>
        </div>
      </template>
      <div class="welcome-content">
        <div class="hero-section">
          <h1>📚 图书管理系统</h1>
          <p class="subtitle">Library Management System</p>
        </div>
        
        <div class="features">
          <div class="feature-card">
            <div class="feature-icon">📖</div>
            <h3>海量图书</h3>
            <p>馆内收藏各类图书数万册，涵盖文学、科技、历史、艺术等多个领域</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⏰</div>
            <h3>开放时间</h3>
            <p>周一至周五 8:00-21:00<br>周末及节假日 9:00-18:00</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔍</div>
            <h3>便捷借阅</h3>
            <p>支持在线查询和借阅，让阅读更简单</p>
          </div>
        </div>
        
        <div class="notice-section">
          <h3>📢 最新公告</h3>
          <ul class="notice-list" v-if="announcements.length > 0">
            <li v-for="announcement in announcements" :key="announcement.id">• {{ announcement.content }}</li>
          </ul>
          <p v-else class="no-notice">暂无公告</p>
        </div>
        
        <div class="quick-links">
          <el-button type="primary" @click="goToBooks">
            <span class="link-icon">📚</span> {{ isReader ? '浏览图书' : '图书管理' }}
          </el-button>
          <el-button @click="goToBorrow">
            <span class="link-icon">📋</span> {{ isReader ? '我的借阅' : '借阅管理' }}
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReaderAnnouncements, getAllAnnouncements } from '../api/announcement'
import { getUserRole, RoleCode } from '../utils/permission'

const router = useRouter()
const announcements = ref([])
const isReader = ref(false)

const fetchAnnouncements = async () => {
  try {
    const role = getUserRole()
    isReader.value = role === RoleCode.READER
    const res = role === RoleCode.READER 
      ? await getReaderAnnouncements() 
      : await getAllAnnouncements()
    if (res.success) {
      // 非读者角色只显示启用的公告
      announcements.value = role === RoleCode.READER 
        ? res.data 
        : res.data.filter(a => a.enabled)
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

const goToBooks = () => {
  router.push(isReader.value ? '/dashboard/reader-books' : '/dashboard/books')
}

const goToBorrow = () => {
  router.push(isReader.value ? '/dashboard/reader-borrow' : '/dashboard/borrow-records')
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.reader-home-container {
  padding: 20px;
  min-height: 100%;
}

.welcome-card {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  text-align: center;
}

.card-header span {
  font-size: 20px;
  font-weight: bold;
  color: #304156;
}

.welcome-content {
  text-align: center;
}

.hero-section {
  padding: 30px 0;
  border-bottom: 1px solid #e8e8e8;
}

.hero-section h1 {
  font-size: 36px;
  color: #304156;
  margin: 0 0 10px 0;
}

.subtitle {
  color: #8c8c8c;
  font-size: 16px;
}

.features {
  display: flex;
  justify-content: space-around;
  padding: 30px 0;
  gap: 20px;
}

.feature-card {
  flex: 1;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.feature-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.feature-card h3 {
  color: #304156;
  margin: 0 0 10px 0;
}

.feature-card p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.notice-section {
  text-align: left;
  padding: 20px;
  background: #fffbe6;
  border-radius: 8px;
  margin-bottom: 20px;
}

.notice-section h3 {
  color: #d48806;
  margin: 0 0 15px 0;
}

.notice-list {
  margin: 0;
  padding-left: 20px;
}

.notice-list li {
  color: #666;
  padding: 5px 0;
}

.no-notice {
  color: #999;
  text-align: center;
}

.quick-links {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.link-icon {
  margin-right: 5px;
}
</style>
