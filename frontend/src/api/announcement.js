/**
 * 公告相关API
 */
import api from './index'

/**
 * 获取读者可见的公告列表
 */
export const getReaderAnnouncements = () => {
  return api.get('/reader/announcements')
}

/**
 * 获取所有公告（管理员/图书管理员）
 */
export const getAllAnnouncements = () => {
  return api.get('/announcements')
}

/**
 * 根据ID获取公告
 */
export const getAnnouncementById = (id) => {
  return api.get(`/announcements/${id}`)
}

/**
 * 添加公告
 */
export const addAnnouncement = (data) => {
  return api.post('/announcements', data)
}

/**
 * 更新公告
 */
export const updateAnnouncement = (id, data) => {
  return api.put(`/announcements/${id}`, data)
}

/**
 * 删除公告
 */
export const deleteAnnouncement = (id) => {
  return api.delete(`/announcements/${id}`)
}
