// AI 聊天 API
import api from './index'

// AI 聊天
export const chat = (data) => api.post('/ai/chat', data)

// 获取剩余次数
export const getRemainingCount = () => api.get('/ai/remaining')

// 获取会话历史记录
export const getHistory = (sessionId) => api.get(`/ai/history/${sessionId}`)
