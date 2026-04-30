// 权限常量
export const PermissionCode = {
  // 用户管理
  USER_CREATE: 'user:create',
  USER_READ: 'user:read',
  USER_UPDATE: 'user:update',
  USER_DELETE: 'user:delete',
  USER_READ_OWN: 'user:read:own',
  USER_UPDATE_OWN: 'user:update:own',

  // 图书管理
  BOOK_CREATE: 'book:create',
  BOOK_READ: 'book:read',
  BOOK_UPDATE: 'book:update',
  BOOK_DELETE: 'book:delete',

  // 借阅管理
  BORROW_CREATE: 'borrow:create',
  BORROW_READ: 'borrow:read',
  BORROW_UPDATE: 'borrow:update',
  BORROW_DELETE: 'borrow:delete',
  BORROW_READ_OWN: 'borrow:read:own',
  BORROW_CREATE_OWN: 'borrow:create:own',

  // 角色管理
  ROLE_CREATE: 'role:create',
  ROLE_READ: 'role:read',
  ROLE_UPDATE: 'role:update',
  ROLE_DELETE: 'role:delete',

  // 权限管理
  PERMISSION_CREATE: 'permission:create',
  PERMISSION_READ: 'permission:read',
  PERMISSION_UPDATE: 'permission:update',
  PERMISSION_DELETE: 'permission:delete',

  // 系统配置
  SYSTEM_CONFIG: 'system:config'
}

// 角色常量
export const RoleCode = {
  ADMIN: 'ADMIN',
  LIBRARIAN: 'LIBRARIAN',
  READER: 'READER'
}

// 角色权限映射
export const RolePermissions = {
  [RoleCode.ADMIN]: [
    PermissionCode.USER_CREATE, PermissionCode.USER_READ, PermissionCode.USER_UPDATE, PermissionCode.USER_DELETE,
    PermissionCode.BOOK_CREATE, PermissionCode.BOOK_READ, PermissionCode.BOOK_UPDATE, PermissionCode.BOOK_DELETE,
    PermissionCode.BORROW_CREATE, PermissionCode.BORROW_READ, PermissionCode.BORROW_UPDATE, PermissionCode.BORROW_DELETE,
    PermissionCode.ROLE_CREATE, PermissionCode.ROLE_READ, PermissionCode.ROLE_UPDATE, PermissionCode.ROLE_DELETE,
    PermissionCode.PERMISSION_CREATE, PermissionCode.PERMISSION_READ, PermissionCode.PERMISSION_UPDATE, PermissionCode.PERMISSION_DELETE,
    PermissionCode.SYSTEM_CONFIG
  ],
  [RoleCode.LIBRARIAN]: [
    PermissionCode.USER_READ, PermissionCode.USER_READ_OWN, PermissionCode.USER_UPDATE_OWN,
    PermissionCode.BOOK_CREATE, PermissionCode.BOOK_READ, PermissionCode.BOOK_UPDATE, PermissionCode.BOOK_DELETE,
    PermissionCode.BORROW_CREATE, PermissionCode.BORROW_READ, PermissionCode.BORROW_UPDATE
  ],
  [RoleCode.READER]: [
    PermissionCode.USER_READ_OWN, PermissionCode.USER_UPDATE_OWN,
    PermissionCode.BOOK_READ,
    PermissionCode.BORROW_READ_OWN, PermissionCode.BORROW_CREATE_OWN
  ]
}

// 路由权限配置
export const RoutePermissions = {
  '/dashboard': [],
  '/dashboard/books': {
    view: PermissionCode.BOOK_READ,
    create: PermissionCode.BOOK_CREATE,
    update: PermissionCode.BOOK_UPDATE,
    delete: PermissionCode.BOOK_DELETE
  },
  '/dashboard/borrow-records': {
    view: PermissionCode.BORROW_READ,
    create: PermissionCode.BORROW_CREATE,
    update: PermissionCode.BORROW_UPDATE,
    delete: PermissionCode.BORROW_DELETE
  },
  '/dashboard/reader-borrow': {
    view: PermissionCode.BORROW_READ_OWN,
    create: PermissionCode.BORROW_CREATE_OWN
  },
  '/dashboard/users': {
    view: PermissionCode.USER_READ,
    create: PermissionCode.USER_CREATE,
    update: PermissionCode.USER_UPDATE,
    delete: PermissionCode.USER_DELETE
  },
  '/dashboard/settings': {
    view: PermissionCode.SYSTEM_CONFIG
  }
}

// API路径前缀映射（不包含/api，因为axios baseURL会添加）
const ApiPrefix = {
  [RoleCode.ADMIN]: '/admin',
  [RoleCode.LIBRARIAN]: '/librarian',
  [RoleCode.READER]: '/reader'
}

// 获取当前用户角色
export const getUserRole = () => {
  return localStorage.getItem('role') || RoleCode.READER
}

// 获取当前用户权限列表
export const getUserPermissions = () => {
  const role = getUserRole()
  return RolePermissions[role] || []
}

// 检查用户是否有指定权限
export const hasPermission = (permission) => {
  const permissions = getUserPermissions()
  return permissions.includes(permission)
}

// 检查用户是否有指定角色
export const hasRole = (role) => {
  const userRole = getUserRole()
  return userRole === role
}

// 检查路由是否可访问
export const canAccessRoute = (routePath, requiredPermission) => {
  // 如果有明确的权限要求，则检查该权限
  if (requiredPermission) {
    return hasPermission(requiredPermission)
  }
  
  const routePerm = RoutePermissions[routePath]
  if (!routePerm || routePerm.length === 0) {
    return true
  }
  if (typeof routePerm === 'object' && routePerm.view) {
    return hasPermission(routePerm.view)
  }
  return false
}

// 获取当前用户API路径前缀
export const getApiPrefix = () => {
  const role = getUserRole()
  return ApiPrefix[role] || ApiPrefix[RoleCode.READER]
}
