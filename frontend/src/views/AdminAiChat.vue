<template>
  <div class="admin-aichat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="ai-icon"><MagicStick /></el-icon>
            <span class="title">AI 数据分析助手</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="loadStatistics" :loading="loadingStats">
              <el-icon><Refresh /></el-icon> 刷新数据
            </el-button>
          </div>
        </div>
      </template>

      <div class="stats-overview" v-if="statsData">
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-value">{{ statsData.totalBooks }}</span>
              <span class="stat-label">图书总数</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-value">{{ statsData.totalBorrowed }}</span>
              <span class="stat-label">已借出</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-value">{{ statsData.totalAvailable }}</span>
              <span class="stat-label">在馆可借</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-value">{{ statsData.totalBorrowRecords }}</span>
              <span class="stat-label">借阅记录</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="quick-actions">
        <span class="action-label">快捷分析：</span>
        <el-button-group>
          <el-button @click="quickQuery('hot_books')" :disabled="loading">热门图书分析</el-button>
          <el-button @click="quickQuery('category')" :disabled="loading">分类分布分析</el-button>
          <el-button @click="quickQuery('restock')" :disabled="loading">补充建议</el-button>
        </el-button-group>
      </div>

      <div class="messages" ref="messagesRef">
        <div v-if="messages.length === 0" class="welcome-tips">
          <h3>欢迎使用 AI 数据分析助手</h3>
          <p>我可以帮您分析图书馆的运营数据：</p>
          <ul>
            <li>哪些书籍借阅频率最高，需要补充库存？</li>
            <li>哪些分类的书籍较受欢迎，建议引入更多？</li>
            <li>在馆图书的分布是否合理？</li>
          </ul>
          <p class="tip-hint">点击上方快捷分析按钮，或直接输入您的问题</p>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message" :class="msg.role">
          <div class="message-avatar">
            <el-icon v-if="msg.role === 'user'"><User /></el-icon>
            <el-icon v-else class="ai-avatar"><MagicStick /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="输入您的问题，例如：哪些书籍需要补充库存？"
          @keydown.enter.ctrl="handleSend"
        />
        <el-button type="primary" @click="handleSend" :loading="loading" class="send-btn">
          <el-icon><Promotion /></el-icon> 发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, User, Promotion, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(false)
const loadingStats = ref(false)
const inputMessage = ref('')
const messages = ref([])
const messagesRef = ref(null)
const statsData = ref(null)

const getAuthHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

// 加载统计数据
const loadStatistics = async () => {
  loadingStats.value = true
  try {
    const response = await axios.get('/api/statistics/dashboard', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      statsData.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loadingStats.value = false
  }
}

// 快捷查询
const quickQuery = async (type) => {
  const prompts = {
    hot_books: '请分析在馆数据，找出借阅次数最高的几本书，并建议哪些需要补充库存。',
    category: '请分析在馆数据的分类分布，指出哪些分类的书籍可能需要补充更多。',
    restock: '基于在馆数据，请给出具体的图书补充建议，包括具体书籍和分类。'
  }
  inputMessage.value = prompts[type]
  await handleSend()
}

// 发送消息
const handleSend = async () => {
  const content = inputMessage.value.trim()
  if (!content) {
    ElMessage.warning('请输入问题')
    return
  }

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: String(content),
    time: new Date().toLocaleTimeString()
  })

  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    // 构建上下文提示词
    let contextPrompt = ''
    if (statsData.value) {
      contextPrompt = `【图书馆在馆数据】
- 图书总数：${statsData.value.totalBooks}
- 已借出：${statsData.value.totalBorrowed}
- 在馆可借：${statsData.value.totalAvailable}
- 借阅记录总数：${statsData.value.totalBorrowRecords}

请基于以上数据进行分析，并用结构化的方式输出建议。
`
    }

    // 调用AI聊天接口
    const response = await axios.post('/ai/chat', {
      message: contextPrompt + content,
      sessionId: ''
    }, { headers: getAuthHeaders() })

    if (response.data.code === 200) {
      const msgContent = response.data.data?.reply || response.data.data?.message || JSON.stringify(response.data.data) || ''
      messages.value.push({
        role: 'assistant',
        content: String(msgContent),
        time: new Date().toLocaleTimeString()
      })
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// 格式化消息
const formatMessage = (content) => {
  if (!content) return ''
  if (typeof content === 'object') return JSON.stringify(content)
  return String(content).replace(/\n/g, '<br>')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.admin-aichat-container {
  padding: 20px;
  height: 100%;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-icon {
  font-size: 24px;
  color: #409EFF;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.stats-overview {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}

.action-label {
  font-size: 14px;
  color: #606266;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  min-height: 300px;
  max-height: 400px;
}

.welcome-tips {
  padding: 20px;
  background: #ecf5ff;
  border-radius: 8px;
  color: #409EFF;
}

.welcome-tips h3 {
  margin: 0 0 12px 0;
}

.welcome-tips ul {
  margin: 12px 0;
  padding-left: 20px;
}

.welcome-tips li {
  margin: 8px 0;
}

.tip-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 12px;
}

.message {
  display: flex;
  margin-bottom: 16px;
  gap: 12px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-avatar {
  color: #409EFF;
}

.message.user .message-avatar {
  background: #409EFF;
  color: white;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  background: #f0f2f5;
  line-height: 1.6;
}

.message.user .message-bubble {
  background: #409EFF;
  color: white;
}

.message-time {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

.message.user .message-time {
  text-align: right;
}

.input-area {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.send-btn {
  min-width: 80px;
}
</style>
