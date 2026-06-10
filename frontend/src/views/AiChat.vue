<template>
  <div class="ai-chat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>AI 图书助手</span>
          <el-tag type="info">今日剩余 {{ remainingCount }} 次</el-tag>
        </div>
      </template>

      <div class="chat-container" ref="chatContainer">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <el-icon class="robot-icon" :size="48"><ChatDotRound /></el-icon>
          <h3>你好，我是你的 AI 图书助手</h3>
          <p>我可以为您推荐书籍、介绍图书内容、回答借阅相关问题</p>
          <div class="suggestions">
            <el-tag
              v-for="suggestion in suggestions"
              :key="suggestion"
              class="suggestion-tag"
              @click="sendSuggestion(suggestion)"
            >
              {{ suggestion }}
            </el-tag>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-else class="message-list">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
            :id="`message-${index}`"
          >
            <div class="message-avatar">
              <img
                v-if="msg.role === 'user'"
                :src="`https://api.dicebear.com/7.x/micah/svg?seed=${currentUserInitial}&backgroundColor=ffd5dc`"
                alt="用户头像"
              />
              <img
                v-else
                src="https://api.dicebear.com/7.x/bottts/svg?seed=AIAssistant&backgroundColor=c0aede"
                alt="AI助手头像"
              />
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>

              <!-- 书籍推荐卡片 -->
              <div v-if="msg.bookRecommendations && msg.bookRecommendations.length > 0" class="book-recommendations">
                <div class="recommendation-title">为你推荐：</div>
                <div class="book-cards">
                  <div
                    v-for="book in msg.bookRecommendations"
                    :key="book.id"
                    class="book-card"
                    @click="handleBookClick(book)"
                  >
                    <div class="book-cover">
                      <div class="cover-placeholder">
                        <el-icon :size="32"><Reading /></el-icon>
                        <span>图书</span>
                      </div>
                    </div>
                    <div class="book-info">
                      <div class="book-title">{{ book.title }}</div>
                      <div class="book-author">{{ book.author }}</div>
                      <div class="book-stock">
                        <el-tag :type="book.availableCopies > 0 ? 'success' : 'danger'" size="small">
                          {{ book.availableCopies > 0 ? `可借 ${book.availableCopies}` : '暂无库存' }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message assistant loading">
            <div class="message-avatar">
              <el-icon :size="24"><ChatDotRound /></el-icon>
            </div>
            <div class="message-content">
              <div class="loading-dots">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您想咨询的图书问题..."
          :disabled="remainingCount <= 0 || loading"
          @keyup.enter.ctrl="handleSend"
        />
        <el-button
          type="primary"
          :disabled="!inputMessage.trim() || remainingCount <= 0 || loading"
          @click="handleSend"
        >
          发送
        </el-button>
      </div>
    </el-card>

    <!-- 借阅对话框 -->
    <el-dialog v-model="borrowDialogVisible" title="借阅图书" width="500px">
      <div v-if="selectedBook" class="borrow-book-info">
        <h4>{{ selectedBook.title }}</h4>
        <p>作者：{{ selectedBook.author }}</p>
        <p>ISBN：{{ selectedBook.isbn }}</p>
        <p>出版社：{{ selectedBook.publisher }}</p>
        <p>可借数量：{{ selectedBook.availableCopies }}</p>
        <p v-if="selectedBook.description">简介：{{ selectedBook.description }}</p>
      </div>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBorrow" :loading="borrowLoading">
          {{ selectedBook && selectedBook.availableCopies > 0 ? '立即借阅' : '预约' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { chat, getRemainingCount, getHistory } from '@/api/ai'
import { applyBorrow } from '@/api'

const chatContainer = ref(null)
const inputMessage = ref('')
const messages = ref([])
const loading = ref(false)
const remainingCount = ref(10)
const currentUserInitial = ref('U')
const sessionId = ref(sessionStorage.getItem('aiSessionId') || null)  // 从 sessionStorage 读取 sessionId，关闭浏览器后自动清除

// 借阅相关
const borrowDialogVisible = ref(false)
const selectedBook = ref(null)
const borrowLoading = ref(false)

const suggestions = [
  '推荐一些编程书籍',
  '我想看科幻小说',
  '有什么关于历史的书吗',
  '给我推荐几本心理学书籍'
]

onMounted(async () => {
  // 获取当前用户首字母
  const username = localStorage.getItem('username') || 'User'
  currentUserInitial.value = username.slice(0, 1).toUpperCase()

  await fetchRemainingCount()

  // 如果有 sessionId，加载历史记录
  if (sessionId.value) {
    await loadHistory()
  }
})

// 加载历史记录
const loadHistory = async () => {
  try {
    const res = await getHistory(sessionId.value)
    if (res.success && res.data) {
      // 将历史消息添加到 messages
      messages.value = res.data
      // 滚动到底部
      await nextTick()
      await scrollToBottom()
    }
  } catch (error) {
    console.error('加载历史记录失败', error)
  }
}

const fetchRemainingCount = async () => {
  try {
    const res = await getRemainingCount()
    remainingCount.value = res.data.remaining
  } catch (error) {
    console.error('获取剩余次数失败', error)
  }
}

const sendSuggestion = (text) => {
  inputMessage.value = text
  handleSend()
}

const handleSend = async () => {
  if (!inputMessage.value.trim() || remainingCount <= 0 || loading.value) return

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage
  })

  await scrollToBottom()
  loading.value = true

  try {
    // 传递 sessionId 给后端
    const res = await chat({ message: userMessage, sessionId: sessionId.value })

    if (res.success) {
      // 保存新的 sessionId 到 sessionStorage
      if (res.data.sessionId) {
        sessionId.value = res.data.sessionId
        sessionStorage.setItem('aiSessionId', res.data.sessionId)
      }

      // 添加 AI 消息
      messages.value.push({
        role: 'assistant',
        content: res.data.reply,
        bookRecommendations: res.data.bookRecommendations
      })

      // 更新剩余次数
      remainingCount.value = Math.max(0, remainingCount.value - 1)
    } else {
      ElMessage.error(res.message || 'AI 服务调用失败')
      // 移除用户消息（因为调用失败）
      messages.value.pop()
    }
  } catch (error) {
    ElMessage.error(error.message || '请求失败，请重试')
    messages.value.pop()
  } finally {
    loading.value = false
    await nextTick()
    await scrollToBottom()
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messages.value.length > 0) {
    const lastMessage = document.getElementById(`message-${messages.value.length - 1}`)
    if (lastMessage) {
      lastMessage.scrollIntoView({ behavior: 'smooth', block: 'end' })
    } else if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  }
}

const formatMessage = (content) => {
  // 简单处理换行
  return content.replace(/\n/g, '<br>')
}

const handleBookClick = (book) => {
  selectedBook.value = book
  borrowDialogVisible.value = true
}

const handleBorrow = async () => {
  if (!selectedBook.value) return

  borrowLoading.value = true
  try {
    await applyBorrow(selectedBook.value.id)
    ElMessage.success(selectedBook.value.availableCopies > 0 ? '借阅成功' : '预约成功')
    borrowDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    borrowLoading.value = false
  }
}
</script>

<style scoped>
.ai-chat-container {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
  min-height: 400px;
}

.welcome-message {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.robot-icon {
  color: #409eff;
  margin-bottom: 20px;
}

.welcome-message h3 {
  margin: 10px 0;
  color: #333;
}

.suggestions {
  margin-top: 30px;
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.suggestion-tag {
  cursor: pointer;
  padding: 8px 16px;
  transition: all 0.3s;
}

.suggestion-tag:hover {
  transform: scale(1.05);
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  background: #f0f0f0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message.user .message-avatar {
  background: #409eff;
  color: white;
}

.message-content {
  flex: 1;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-text {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-text {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 书籍推荐 */
.book-recommendations {
  margin-top: 16px;
}

.recommendation-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.book-cards {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.book-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  width: 260px;
}

.book-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-cover {
  width: 60px;
  height: 80px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
  color: #fff;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.cover-placeholder span {
  font-size: 10px;
  opacity: 0.9;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-info {
  flex: 1;
  min-width: 0;
}

.book-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-stock {
  margin-top: auto;
}

/* 输入区域 */
.input-area {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.input-area .el-textarea {
  flex: 1;
}

/* 借阅对话框 */
.borrow-book-info {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.borrow-book-info h4 {
  margin: 0 0 12px 0;
}

.borrow-book-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}
</style>
