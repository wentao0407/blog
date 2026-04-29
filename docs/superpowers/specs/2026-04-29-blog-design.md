# 胡言记 — 个人博客系统设计文档

## 概述

**项目名称：** 胡言记
**定位：** 技术 + 生活混合型个人博客
**风格：** 温暖文艺，卡片布局，暖色调，带封面图
**核心功能：** 文章发布、分类标签、评论（登录/匿名）、搜索、归档、文章置顶、访问量统计、关于我页面、管理后台

## 技术栈

- **后端：** JDK17 + SpringBoot 3.x + MyBatis-Plus + MySQL + Redis 7.2
- **前端：** Vue3 + Element Plus（后台）+ 自定义样式（前台）
- **传输安全：** HTTPS 全站加密（Nginx + SSL 证书）

## 系统架构

```
┌─────────────────────────────────────────────┐
│                  Vue3 前端                    │
│  ┌──────────────┐    ┌───────────────────┐  │
│  │  博客前台 /   │    │  管理后台 /admin/* │  │
│  │  (温暖文艺风) │    │  (Element Plus)   │  │
│  └──────┬───────┘    └────────┬──────────┘  │
│         └──────────┬──────────┘             │
│              request.js (Axios)              │
└─────────────────────┬───────────────────────┘
                      │ REST API
┌─────────────────────┴───────────────────────┐
│              SpringBoot 3 后端                │
│  ┌─────────┐ ┌─────────┐ ┌───────────────┐ │
│  │Controller│ │ Service │ │ MyBatis-Plus  │ │
│  └─────────┘ └─────────┘ └───────┬───────┘ │
│         ┌─────────┬───────────────┘         │
│    ┌────┴───┐ ┌───┴────┐                    │
│    │ MySQL  │ │ Redis  │                    │
│    └────────┘ └────────┘                    │
└─────────────────────────────────────────────┘
```

## 项目结构

```
blog/
├── backend/                    # SpringBoot 后端
│   ├── src/main/java/com/hu/blog/
│   │   ├── controller/         # 接口层
│   │   ├── service/            # 业务层
│   │   ├── mapper/             # 数据层
│   │   ├── entity/             # 数据库实体
│   │   ├── vo/                 # 前端入参
│   │   ├── dto/                # 传输对象
│   │   ├── config/             # 配置类
│   │   └── exception/          # 异常处理
│   └── pom.xml
├── frontend/                   # Vue3 前端
│   ├── src/
│   │   ├── views/
│   │   │   ├── blog/           # 博客前台页面
│   │   │   └── admin/          # 管理后台页面
│   │   ├── components/
│   │   ├── router/
│   │   ├── utils/
│   │   └── api/
│   └── package.json
└── docs/
```

## 数据库设计

### ER 关系

```
t_user ──1:N── t_article ──N:1── t_category
                    │
                    ├──N:N── t_tag (通过 t_article_tag)
                    │
                    └──1:N── t_comment ──N:1── t_user (可选)
```

### 表结构

**t_user — 用户表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint, PK | 主键 |
| username | varchar(50) | 用户名 |
| password | varchar(100) | 密码（BCrypt） |
| nickname | varchar(50) | 昵称 |
| avatar | varchar(255) | 头像URL |
| email | varchar(100) | 邮箱 |
| role | tinyint | 0-普通用户 1-管理员 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 逻辑删除 0-未删 1-已删 |

**t_article — 文章表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint, PK | 主键 |
| title | varchar(100) | 标题 |
| summary | varchar(500) | 摘要 |
| content | longtext | 正文（Markdown） |
| cover_image | varchar(255) | 封面图URL |
| category_id | bigint, FK | 分类ID |
| is_top | tinyint | 是否置顶 0-否 1-是 |
| status | tinyint | 0-草稿 1-已发布 |
| view_count | int | 访问量（Redis 定时同步） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 逻辑删除 |

**t_category — 分类表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint, PK | 主键 |
| name | varchar(50) | 分类名 |
| sort | int | 排序 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 逻辑删除 |

**t_tag — 标签表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint, PK | 主键 |
| name | varchar(50) | 标签名 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 逻辑删除 |

**t_article_tag — 文章标签关联表**

| 字段 | 类型 | 说明 |
|------|------|------|
| article_id | bigint, FK | 文章ID |
| tag_id | bigint, FK | 标签ID |

**t_comment — 评论表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint, PK | 主键 |
| article_id | bigint, FK | 文章ID |
| user_id | bigint, FK | 用户ID（可为空，匿名评论时为空） |
| nickname | varchar(50) | 昵称（匿名评论用） |
| content | varchar(1000) | 评论内容 |
| parent_id | bigint | 父评论ID（支持回复，顶级评论为0） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 逻辑删除 |

### Redis 用途

- `article:view:{id}` — 文章访问量计数，定时同步到 MySQL
- `article:hot` — 热门文章缓存（Sorted Set）
- `user:token:{token}` — 登录 Token 存储

## 接口设计

### 通用约定

- 所有接口统一使用 POST
- 统一响应格式：`{ "code": 200, "message": "success", "data": {} }`
- 分页入参：`pageNum`、`pageSize`
- Token 通过 `Authorization` 请求头传递
- 传输安全：HTTPS 全站加密

### 博客前台接口

| 接口 | 说明 |
|------|------|
| `POST /api/article/list` | 文章列表（分页、搜索、分类筛选） |
| `POST /api/article/detail` | 文章详情（含访问量+1） |
| `POST /api/article/top` | 获取置顶文章 |
| `POST /api/category/list` | 分类列表 |
| `POST /api/tag/list` | 标签列表 |
| `POST /api/comment/list` | 文章评论列表 |
| `POST /api/comment/add` | 发表评论（登录/匿名） |
| `POST /api/archive/list` | 按月归档 |
| `POST /api/user/login` | 用户登录 |
| `POST /api/user/register` | 用户注册 |
| `POST /api/user/info` | 当前用户信息 |
| `POST /api/site/stats` | 站点统计（总文章、总访问量等） |

### 管理后台接口

| 接口 | 说明 |
|------|------|
| `POST /api/admin/article/save` | 新增/编辑文章 |
| `POST /api/admin/article/delete` | 删除文章 |
| `POST /api/admin/article/top` | 置顶/取消置顶 |
| `POST /api/admin/category/save` | 新增/编辑分类 |
| `POST /api/admin/category/delete` | 删除分类 |
| `POST /api/admin/tag/save` | 新增/编辑标签 |
| `POST /api/admin/tag/delete` | 删除标签 |
| `POST /api/admin/comment/list` | 评论管理列表 |
| `POST /api/admin/comment/delete` | 删除评论 |
| `POST /api/admin/upload/image` | 图片上传 |

## 前端页面

### 路由方案

- `/` — 博客前台
- `/admin/*` — 管理后台

### 博客前台页面

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 置顶文章 + 文章列表（卡片布局） |
| `/article/:id` | 文章详情 | Markdown 渲染、评论区 |
| `/category/:id` | 分类文章 | 按分类筛选 |
| `/tag/:id` | 标签文章 | 按标签筛选 |
| `/archive` | 归档页 | 按月份分组展示 |
| `/search?q=xxx` | 搜索结果 | 关键词搜索 |
| `/about` | 关于我 | 个人介绍 |
| `/login` | 登录 | 用户登录/注册 |

### 管理后台页面

| 路由 | 页面 | 说明 |
|------|------|------|
| `/admin/dashboard` | 仪表盘 | 访问量趋势、文章统计 |
| `/admin/article` | 文章管理 | 列表、新增/编辑（Markdown 编辑器） |
| `/admin/category` | 分类管理 | CRUD |
| `/admin/tag` | 标签管理 | CRUD |
| `/admin/comment` | 评论管理 | 列表、删除 |

### 博客前台 UI 风格

温暖文艺风格：
- 暖色调（米白背景 #f5f0eb、棕色系文字 #5c4033）
- 卡片式文章列表，带封面图
- 圆角、轻阴影
- 衬线字体用于标题，无衬线用于正文
- 关于我页面带头像和简介

### 管理后台 UI 风格

使用 Element Plus 默认主题，标准后台管理布局：
- 左侧菜单 + 顶部导航
- 表格 + 表单 + 弹窗
- Markdown 编辑器用于文章编写

## 错误处理

- 全局异常处理器捕获所有异常，返回统一格式
- 业务异常使用自定义 `BusinessException`
- 参数校验使用 `@Valid` + VO 注解
- 前端统一拦截响应，401 跳转登录页

## 测试策略

- 每个接口自测：正常、异常、边界值
- 每个页面测试：所有按钮、输入、交互
- 列表测试：查询、重置、分页、操作栏
- 表单测试：提交、校验、清空、取消
- 界面在 1920x1080 下正常显示
