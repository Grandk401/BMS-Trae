import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Books from '../views/Books.vue'
import BorrowRecords from '../views/BorrowRecords.vue'
import Users from '../views/Users.vue'
import Settings from '../views/Settings.vue'
import ReaderBooks from '../views/ReaderBooks.vue'
import ReaderRecords from '../views/ReaderRecords.vue'
import ReaderHome from '../views/ReaderHome.vue'
import HomeOrReaderHome from '../views/HomeOrReaderHome.vue'
import AnnouncementManager from '../views/AnnouncementManager.vue'
import OperationLogs from '../views/OperationLogs.vue'
import Statistics from '../views/Statistics.vue'
import { canAccessRoute } from '../utils/permission'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'DashboardHome',
        component: HomeOrReaderHome,
        meta: { permission: 'book:read' }
      },
      {
        path: 'books',
        name: 'Books',
        component: Books,
        meta: { permission: 'book:read' }
      },
      {
        path: 'borrow-records',
        name: 'BorrowRecords',
        component: BorrowRecords,
        meta: { permission: 'borrow:read' }
      },
      {
        path: 'users',
        name: 'Users',
        component: Users,
        meta: { permission: 'user:read' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: Settings,
        meta: { permission: 'system:config' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: AnnouncementManager,
        meta: { permission: 'system:config' }
      },
      {
        path: 'operation-logs',
        name: 'OperationLogs',
        component: OperationLogs,
        meta: { permission: 'system:config' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: Statistics,
        meta: { permission: 'borrow:read' }
      },
      {
        path: 'reader-books',
        name: 'ReaderBooks',
        component: ReaderBooks,
        meta: { requiresAuth: true, permission: 'borrow:read:own' }
      },
      {
        path: 'reader-borrow',
        name: 'ReaderBorrow',
        component: ReaderRecords,
        meta: { requiresAuth: true, permission: 'borrow:read:own' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (!token && to.meta.requiresAuth) {
    next('/login')
    return
  }

  if (token && to.path === '/login') {
    next('/dashboard')
    return
  }

  if (to.meta.requiresAuth && to.meta.permission) {
    if (!canAccessRoute(to.path, to.meta.permission)) {
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
