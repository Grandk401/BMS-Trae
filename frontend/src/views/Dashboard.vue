<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapsed ? '64px' : '220px'">
        <div class="logo-section">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span v-if="!isCollapsed" class="logo-text">{{ systemTitle }}</span>
        </div>

        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="20">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/statistics" v-if="showStatisticsMenu">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/books" v-if="showBooksMenu && !showReaderBooksMenu">
            <el-icon><Reading /></el-icon>
            <template #title>在馆图书</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/reader-books" v-if="showReaderBooksMenu">
            <el-icon><Reading /></el-icon>
            <template #title>在馆图书</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/borrow-records" v-if="showBorrowRecordsMenu">
            <el-icon><List /></el-icon>
            <template #title>借阅管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/reader-borrow" v-if="showAdminOwnBorrowMenu || showReaderBorrowMenu">
            <el-icon><Tickets /></el-icon>
            <template #title>我的借阅</template>
          </el-menu-item>
          <!-- 管理端AI数据分析 -->
          <el-menu-item index="/dashboard/ai-chat" v-if="showBorrowRecordsMenu">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>AI 数据分析</template>
          </el-menu-item>
          <!-- 读者端AI助手 -->
          <el-menu-item index="/dashboard/reader-ai-chat" v-if="showReaderBorrowMenu">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>AI 助手</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/users" v-if="showUsersMenu">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/announcements" v-if="showSettingsMenu">
            <el-icon><Bell /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/operation-logs" v-if="showSettingsMenu">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-menu>

        <!-- 用户信息底部 -->
        <div class="sidebar-footer">
          <div class="user-card" @click="handleCommand('logout')">
            <div class="user-avatar">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div v-if="!isCollapsed" class="user-detail">
              <span class="username">{{ username }}</span>
              <span class="role">{{ roleName }}</span>
            </div>
            <div v-if="!isCollapsed" class="logout-icon">
              <el-icon><SwitchButton /></el-icon>
            </div>
          </div>
        </div>
      </el-aside>

      <el-container>
        <!-- 顶部Header -->
        <el-header>
          <div class="header-left">
            <h2 class="page-title">{{ pageTitle }}</h2>
          </div>
          <div class="header-right">
            <span class="welcome-text">欢迎使用{{ systemTitle }}</span>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  DataAnalysis,
  Reading,
  List,
  Tickets,
  ChatDotRound,
  User,
  UserFilled,
  Bell,
  Document,
  Setting,
  SwitchButton,
  Fold,
  Expand
} from '@element-plus/icons-vue'
import { getUserRole, hasPermission, RoleCode, PermissionCode } from '../utils/permission'

const router = useRouter()
const route = useRoute()
const username = ref('')
const role = ref(RoleCode.READER)
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': '首页',
    '/dashboard/statistics': '数据统计',
    '/dashboard/books': '在馆图书',
    '/dashboard/reader-books': '在馆图书',
    '/dashboard/borrow-records': '借阅管理',
    '/dashboard/reader-borrow': '我的借阅',
    '/dashboard/ai-chat': 'AI 数据分析',
    '/dashboard/reader-ai-chat': 'AI 助手',
    '/dashboard/users': '用户管理',
    '/dashboard/announcements': '公告管理',
    '/dashboard/operation-logs': '操作日志'
  }
  return titles[activeMenu.value] || '首页'
})

const roleName = computed(() => {
  const roleNames = {
    [RoleCode.ADMIN]: '系统管理员',
    [RoleCode.LIBRARIAN]: '图书管理员',
    [RoleCode.READER]: '读者'
  }
  return roleNames[role.value] || '未知角色'
})

const systemTitle = computed(() => {
  return role.value === RoleCode.READER ? '图书在线系统' : '图书在线系统'
})

const showBooksMenu = computed(() => hasPermission(PermissionCode.BOOK_READ))
const showBorrowRecordsMenu = computed(() => hasPermission(PermissionCode.BORROW_READ))
const showUsersMenu = computed(() => hasPermission(PermissionCode.USER_READ))
const showSettingsMenu = computed(() => hasPermission(PermissionCode.SYSTEM_CONFIG))
const showReaderBooksMenu = computed(() => hasPermission(PermissionCode.BORROW_READ_OWN) && !hasPermission(PermissionCode.BORROW_READ))
const showReaderBorrowMenu = computed(() => hasPermission(PermissionCode.BORROW_READ_OWN))
const showStatisticsMenu = computed(() => hasPermission(PermissionCode.BORROW_READ))
const showAdminOwnBorrowMenu = computed(() => hasPermission(PermissionCode.BORROW_READ) && hasPermission(PermissionCode.BORROW_READ_OWN))

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

onMounted(() => {
  username.value = localStorage.getItem('username') || 'User'
  role.value = getUserRole()
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      handleLogout()
    }).catch(() => {})
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('userId')
  sessionStorage.removeItem('aiSessionId')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

/* 侧边栏 */
.el-aside {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 36px;
  height: 36px;
  min-width: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 22px;
  height: 22px;
  color: #fff;
}

.logo-text {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  white-space: nowrap;
}

/* 折叠按钮 */
.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.3s;
}

.collapse-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

/* 菜单 */
.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.sidebar-menu .el-menu-item {
  margin: 4px 8px;
  border-radius: 10px;
  height: 48px;
  color: rgba(255, 255, 255, 0.7);
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.sidebar-menu .el-menu-item .el-icon {
  font-size: 18px;
}

:deep(.el-menu--collapse .el-menu-item) {
  padding: 0 20px !important;
}

/* 底部用户信息 */
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.user-card:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 36px;
  height: 36px;
  min-width: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.user-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.username {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role {
  color: rgba(255, 255, 255, 0.5);
  font-size: 11px;
}

.logout-icon {
  color: rgba(255, 255, 255, 0.5);
  font-size: 16px;
}

.logout-icon:hover {
  color: #f56c6c;
}

/* 顶部Header */
.el-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
}

.header-right {
  display: flex;
  align-items: center;
}

.welcome-text {
  color: #909399;
  font-size: 14px;
}

/* 主内容区 */
.el-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
