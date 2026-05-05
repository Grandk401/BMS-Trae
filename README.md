# 图书管理系统 (Book Management System)

> 基于 Spring Boot + Vue.js 的前后端分离图书管理系统，支持多角色权限管理、图书借阅、数据统计等功能。

## 📋 项目简介

本系统是一个面向图书馆的综合管理平台，提供完整的图书管理、用户管理、借阅管理和数据统计功能。系统采用前后端分离架构，支持管理员、图书管理员和普通读者三种角色，分为管理端和读者端。项目大量使用Trae.cn进行开发，包括数据库设计、前端页面等，因此部分代码细节可能有一定出入或矛盾。

## 🛠️ 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 开发语言 |
| Spring Boot | 2.7.18 | 后端框架 |
| Spring Security | 5.7.x | 安全框架 |
| MyBatis | 2.2.2 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.11.5 | 身份认证 |
| Apache POI | 4.1.2 | Excel导出 |
| PageHelper | 1.4.7 | 分页插件 |
| Hutool | 5.x | 工具库 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.x | 前端框架 |
| Element Plus | 2.x | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Vite | 4.x | 构建工具 |
| ECharts | 5.x | 数据可视化 |
| Axios | 1.x | HTTP客户端 |

## 📁 项目结构

```
BMS/
├── backend/                    # 后端服务
│   ├── src/main/java/com/bms/
│   │   ├── controller/         # REST API控制器
│   │   │   ├── admin/          # 管理员接口
│   │   │   ├── librarian/      # 图书管理员接口
│   │   │   └── reader/         # 读者接口
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 数据库实体
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── security/           # 安全组件
│   │   ├── scheduler/          # 定时任务
│   │   ├── aspect/             # AOP切面
│   │   └── common/             # 公共模块
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis映射文件
│   │   └── application.yml     # 应用配置
│   └── sql/                    # 数据库初始化脚本
└── frontend/                   # 前端应用
    ├── src/
    │   ├── views/              # 页面组件
    │   ├── api/                # API封装
    │   ├── router/             # 路由配置
    │   └── utils/              # 工具函数
    └── package.json            # 依赖配置
```

## 🎯 功能模块

### 1. 用户管理
- 用户注册与登录
- 多角色权限控制（管理员/图书管理员/读者）
- 用户信息管理（禁用/启用账号）

### 2. 图书管理
- 图书信息管理（增删改查）
- 图书分类管理
- 图书搜索（按书名、作者、分类）
- 图书状态管理（在馆/借出）

### 3. 借阅管理
- 图书借阅申请
- 借阅记录查询
- 图书归还处理
- 逾期自动计算罚款（每日0.2元，10元封顶）
- 逾期账号自动禁用

### 4. 公告管理
- 公告发布与管理
- 公告列表展示

### 5. 数据统计
- 仪表盘统计（图书总数、借出数量、可借数量）
- 分类分布统计
- 借阅趋势分析
- 热门图书TOP10
- 月度借阅统计
- Excel报表导出

### 6. 操作日志
- 系统操作记录审计
- 日志查询与筛选

## 🔧 环境要求

- JDK 1.8+
- MySQL 8.0+
- Node.js 18+
- Maven 3.6+

## 🚀 快速开始

### 1. 数据库配置

创建数据库并执行初始化脚本：

```sql
CREATE DATABASE bms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bms;
SOURCE backend/sql/init-full.sql;
```

**脚本包含**：
- 5张数据表（用户表、图书表、借阅记录表、公告表、操作日志表）
- 15本测试图书（涵盖文学、科幻、计算机、历史等分类）
- 3条公告信息

> **注意**：用户账号由后端应用启动时自动初始化，无需手动创建。

### 2. 后端运行

```bash
cd backend

# 开发环境运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/bms-backend-1.0.0.jar
```

### 3. 前端运行

```bash
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build
```

### 4. 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| 后端 | http://localhost:8080 |

## 👤 默认账号

后端启动时自动创建以下账号：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 图书管理员 | librarian | 123456 |
| 读者 | reader | 123456 |

## 📝 API 接口示例

### 登录接口
```
POST /login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}
```

### 获取仪表盘统计
```
GET /api/statistics/dashboard
Authorization: Bearer <token>
```

### 借阅图书
```
POST /api/librarian/borrow-records
Authorization: Bearer <token>
Content-Type: application/json

{
    "bookId": 1,
    "userId": 3,
    "borrowDate": "2024-01-01",
    "returnDate": "2024-01-15"
}
```

## 🔒 权限说明

| 权限 | 管理员 | 图书管理员 | 读者 |
|------|--------|-----------|------|
| 用户管理 | ✅ | ⚠️ | ❌ |
| 图书管理 | ✅ | ✅ | ❌ |
| 借阅管理 | ✅ | ✅ | ⚠️ |
| 数据统计 | ✅ | ✅ | ❌ |
| 操作日志 | ✅ | ❌ | ❌ |
| 公告管理 | ✅ | ✅ | ❌ |

## 📊 定时任务

系统包含以下定时任务：

1. **逾期检查任务**：每天凌晨执行，检查逾期借阅记录并计算罚款
2. **账号禁用任务**：当罚款达到10元封顶时，自动禁用读者账号

## 📊 数据库设计

### 核心数据表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | id, username, password, role, enabled |
| `book` | 图书表 | id, isbn, title, author, publisher, category, stock |
| `borrow_record` | 借阅记录表 | id, book_id, user_id, borrow_date, due_date, return_date, status, fine |
| `announcement` | 公告表 | id, title, content, enabled, sort_order |
| `operation_log` | 操作日志表 | id, user_id, operation_type, module, description, ip_address |

### 借阅状态说明

| 状态码 | 说明 |
|--------|------|
| `PENDING` | 待审核 |
| `BORROWING` | 借阅中 |
| `RETURNED` | 已归还 |
| `OVERDUE` | 已逾期 |
| `OVERDUE_RETURNED` | 已逾期但已归还 |
| `REJECTED` | 已拒绝 |

## 📁 配置说明

### 后端配置

`backend/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bms?useSSL=false&serverTimezone=Asia/Shanghai
    username: ${db.username}
    password: ${db.password}

jwt:
  secret: bmsSecretKey2024BookManagementSystem
  expiration: 86400000

spring.config.import: optional:application-private.yml
```

### 前端代理配置

`frontend/vite.config.js`：

```javascript
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

## 🐛 常见问题

### 1. 数据库连接失败
- 检查数据库服务是否启动
- 确认数据库用户名和密码正确
- 检查数据库端口是否开放

### 2. 前端跨域问题
- 确保后端 `CorsConfig` 配置正确
- 检查前端代理配置是否生效

### 3. JWT Token 失效
- 检查 Token 是否过期
- 确认 Token 格式正确（Bearer + token）

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

**项目作者**：Grandk401
**创建时间**：2026年