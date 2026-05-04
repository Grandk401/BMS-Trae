<template>
  <div class="dashboard-container">
    <el-container>
      <el-aside width="200px">
        <div class="logo">BMS</div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/statistics" v-if="showStatisticsMenu">
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/books" v-if="showBooksMenu && !showReaderBooksMenu">
            <span>在馆图书</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/reader-books" v-if="showReaderBooksMenu">
            <span>在馆图书</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/borrow-records" v-if="showBorrowRecordsMenu">
            <span>借阅管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/reader-borrow" v-if="showReaderBorrowMenu">
            <span>我的借阅</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/users" v-if="showUsersMenu">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/settings" v-if="showSettingsMenu">
            <span>系统设置</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/announcements" v-if="showSettingsMenu">
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/operation-logs" v-if="showSettingsMenu">
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header>
          <div class="header-content">
            <h3>图书管理系统 - {{ roleName }}</h3>
            <div class="user-info">
              <span>欢迎，{{ username }}</span>
              <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
            </div>
          </div>
        </el-header>

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
import { ElMessage } from 'element-plus'
import { getUserRole, hasPermission, RoleCode, PermissionCode } from '../utils/permission'

const router = useRouter()
const route = useRoute()
const username = ref('')
const role = ref(RoleCode.READER)

const activeMenu = computed(() => route.path)

const roleName = computed(() => {
  const roleNames = {
    [RoleCode.ADMIN]: '系统管理员',
    [RoleCode.LIBRARIAN]: '图书管理员',
    [RoleCode.READER]: '读者'
  }
  return roleNames[role.value] || '未知角色'
})

const showBooksMenu = computed(() => hasPermission(PermissionCode.BOOK_READ))
const showBorrowRecordsMenu = computed(() => hasPermission(PermissionCode.BORROW_READ))
const showUsersMenu = computed(() => hasPermission(PermissionCode.USER_READ))
const showSettingsMenu = computed(() => hasPermission(PermissionCode.SYSTEM_CONFIG))
const showReaderBooksMenu = computed(() => hasPermission(PermissionCode.BORROW_READ_OWN) && !hasPermission(PermissionCode.BORROW_READ))
const showReaderBorrowMenu = computed(() => hasPermission(PermissionCode.BORROW_READ_OWN))
const showStatisticsMenu = computed(() => hasPermission(PermissionCode.BORROW_READ))

onMounted(() => {
  username.value = localStorage.getItem('username') || 'User'
  role.value = getUserRole()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('userId')
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

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  background-color: #263445;
}

.el-menu-vertical {
  border-right: none;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h3 {
  margin: 0;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
