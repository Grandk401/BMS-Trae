import axios from 'axios'
import { getApiPrefix } from '../utils/permission'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    const data = response.data
    // 适配新的 Result<T> 格式
    if (data && data.code !== undefined) {
      return {
        success: data.code === 200,
        message: data.message,
        data: data.data,
        code: data.code
      }
    }
    return data
  },
  error => {
    // 处理异常响应
    if (error.response) {
      const data = error.response.data
      if (data && data.code !== undefined) {
        return Promise.reject({
          success: false,
          message: data.message,
          code: data.code
        })
      }
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('role')
        localStorage.removeItem('userId')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// 获取带角色前缀的完整API路径
const getFullPath = (path) => {
  return getApiPrefix() + path
}

export const login = (data) => api.post('/login', data)
export const getUserInfo = () => api.get('/userinfo')

// 图书管理API（根据角色自动选择前缀）
export const getBooks = () => api.get(getFullPath('/books'))
export const getBookById = (id) => api.get(getFullPath(`/books/${id}`))
export const addBook = (data) => api.post(getFullPath('/books'), data)
export const updateBook = (id, data) => api.put(getFullPath(`/books/${id}`), data)
export const deleteBook = (id) => api.delete(getFullPath(`/books/${id}`))

// 借阅记录API（根据角色自动选择前缀）
export const getBorrowRecords = () => api.get(getFullPath('/borrow-records'))
export const getBorrowRecordById = (id) => api.get(getFullPath(`/borrow-records/${id}`))
export const addBorrowRecord = (data) => api.post(getFullPath('/borrow-records'), data)
export const updateBorrowRecord = (id, data) => api.put(getFullPath(`/borrow-records/${id}`), data)
export const deleteBorrowRecord = (id) => api.delete(getFullPath(`/borrow-records/${id}`))

// 读者申请借阅
export const applyBorrow = (bookId) => api.post(`/reader/borrow-records/apply/${bookId}`)

// 管理员借阅操作API
export const approveBorrow = (id) => api.put(`/admin/borrow-records/${id}/approve`)
export const rejectBorrow = (id) => api.put(`/admin/borrow-records/${id}/reject`)
export const confirmReturn = (id) => api.put(`/admin/borrow-records/${id}/return`)
export const markOverdue = (id) => api.put(`/admin/borrow-records/${id}/mark-overdue`)

// 搜索借阅记录
export const searchBorrowRecords = (params) => api.get('/admin/borrow-records/search', { params })

// 查询逾期借阅记录
export const getOverdueRecords = () => api.get('/admin/borrow-records/overdue')

export default api