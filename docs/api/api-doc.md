# 胡言记 — 接口文档

## 通用约定

### 请求方式

- 所有接口统一使用 `POST`（文件上传使用 `multipart/form-data`）
- Content-Type: `application/json`

### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200 表示成功 |
| message | String | 提示信息 |
| data | Object | 业务数据，失败时为 null |

### 错误码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未登录或 Token 过期 |
| 403 | 无权限（非管理员访问后台接口） |
| 500 | 服务器内部错误 |

### 认证方式

- 登录成功后返回 `token`，前端存储在 `localStorage`
- 需要认证的接口通过 `Authorization` 请求头传递 Token
- 管理后台接口（`/api/admin/**`）需要管理员角色

### 分页参数

分页接口统一使用以下字段：

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | int | 1 | 页码 |
| pageSize | int | 10 | 每页数量 |

分页响应：

```json
{
  "records": [],
  "total": 0
}
```

---

## 一、博客前台接口

### 1.1 用户模块

#### 1.1.1 用户登录

`POST /api/user/login`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**请求示例：**

```json
{
  "username": "admin",
  "password": "Fvjm0918!"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "a1b2c3d4...",
    "id": 1,
    "nickname": "管理员",
    "role": 1
  }
}
```

**错误情况：**
- 用户名或密码错误：`code: 500, message: "用户名或密码错误"`

---

#### 1.1.2 用户注册

`POST /api/user/register`

**请求参数：**

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| username | String | 是 | 3-20 位 | 用户名 |
| password | String | 是 | 6-20 位 | 密码 |
| nickname | String | 是 | — | 昵称 |

**请求示例：**

```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误情况：**
- 用户名已存在：`code: 500, message: "用户名已存在"`
- 参数校验失败：`code: 400, message: "用户名长度3-20"` 等

---

#### 1.1.3 获取当前用户信息

`POST /api/user/info`

**请求头：** `Authorization: <token>`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "http://example.com/avatar.jpg",
    "email": "admin@example.com",
    "role": 1
  }
}
```

---

### 1.2 文章模块

#### 1.2.1 文章列表

`POST /api/article/list`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| keyword | String | 否 | 搜索关键词（模糊匹配标题） |
| categoryId | Long | 否 | 分类 ID 筛选 |
| tagId | Long | 否 | 标签 ID 筛选 |

**请求示例：**

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "Spring",
  "categoryId": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "深入理解 Spring Boot 自动配置原理",
        "summary": "Spring Boot 的自动配置机制极大地简化了...",
        "coverImage": "http://example.com/cover.jpg",
        "categoryName": "技术",
        "tagNames": ["Spring Boot", "Java"],
        "isTop": 1,
        "viewCount": 42,
        "createTime": "2026-04-29T10:30:00"
      }
    ],
    "total": 25
  }
}
```

**排序规则：** 置顶文章优先，再按创建时间降序

---

#### 1.2.2 文章详情

`POST /api/article/detail`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 文章 ID |

**请求示例：**

```json
{
  "id": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "深入理解 Spring Boot 自动配置原理",
    "summary": "Spring Boot 的自动配置机制极大地简化了...",
    "content": "# 正文 Markdown 内容...",
    "coverImage": "http://example.com/cover.jpg",
    "categoryName": "技术",
    "tagNames": ["Spring Boot", "Java"],
    "tagIds": [1, 2],
    "isTop": 1,
    "viewCount": 43,
    "createTime": "2026-04-29T10:30:00"
  }
}
```

**说明：** 每次访问会通过 Redis 累加访问量（+1）

**错误情况：**
- 文章不存在或未发布：`code: 500, message: "文章不存在"`

---

#### 1.2.3 置顶文章

`POST /api/article/top`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "置顶文章标题",
      "summary": "...",
      "coverImage": "...",
      "categoryName": "技术",
      "tagNames": ["Java"],
      "isTop": 1,
      "viewCount": 100,
      "createTime": "2026-04-29T10:30:00"
    }
  ]
}
```

---

#### 1.2.4 文章归档

`POST /api/article/archive`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "month": "2026-04",
      "count": 5,
      "articles": [
        { "id": 1, "title": "文章标题", "createTime": "2026-04-29T10:30:00" },
        { "id": 2, "title": "另一篇文章", "createTime": "2026-04-25T08:00:00" }
      ]
    },
    {
      "month": "2026-03",
      "count": 3,
      "articles": []
    }
  ]
}
```

**说明：** 按月份降序排列，每月包含文章列表

---

### 1.3 分类模块

#### 1.3.1 分类列表

`POST /api/category/list`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "技术", "sort": 1 },
    { "id": 2, "name": "生活", "sort": 2 }
  ]
}
```

---

### 1.4 标签模块

#### 1.4.1 标签列表

`POST /api/tag/list`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "id": 1, "name": "Java" },
    { "id": 2, "name": "Spring Boot" }
  ]
}
```

---

### 1.5 评论模块

#### 1.5.1 文章评论列表

`POST /api/comment/list`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | Long | 是 | 文章 ID |

**请求示例：**

```json
{
  "articleId": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "nickname": "张三",
      "avatar": "http://example.com/avatar.jpg",
      "content": "写得真好！",
      "parentId": 0,
      "createTime": "2026-04-29T12:00:00",
      "children": [
        {
          "id": 2,
          "nickname": "李四",
          "avatar": null,
          "content": "同意！",
          "parentId": 1,
          "createTime": "2026-04-29T13:00:00",
          "children": []
        }
      ]
    }
  ]
}
```

**说明：** 返回树形结构，顶级评论的 `parentId` 为 0

---

#### 1.5.2 发表评论

`POST /api/comment/add`

**请求头：** `Authorization: <token>`（可选，支持匿名评论）

**请求参数：**

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| articleId | Long | 是 | — | 文章 ID |
| nickname | String | 是 | 最长 50 字 | 昵称 |
| content | String | 是 | 最长 1000 字 | 评论内容 |
| parentId | Long | 否 | — | 父评论 ID，不传或为 0 表示顶级评论 |

**请求示例：**

```json
{
  "articleId": 1,
  "nickname": "张三",
  "content": "写得真好！",
  "parentId": 0
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**说明：**
- 登录用户由后端从 Token 获取 userId，匿名评论 userId 为空
- parentId 不传或为 0 时作为顶级评论

---

### 1.6 站点统计

#### 1.6.1 获取站点统计数据

`POST /api/site/stats`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleCount": 25,
    "viewCount": 1234,
    "categoryCount": 5,
    "tagCount": 12
  }
}
```

---

## 二、管理后台接口

> 所有管理后台接口需要管理员角色，请求头需携带 `Authorization: <token>`

### 2.1 文章管理

#### 2.1.1 新增/编辑文章

`POST /api/admin/article/save`

**请求参数：**

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| id | Long | 否 | — | 文章 ID，不传为新增，传值为编辑 |
| title | String | 是 | — | 标题 |
| summary | String | 否 | — | 摘要 |
| content | String | 是 | — | 正文（Markdown） |
| coverImage | String | 否 | — | 封面图 URL |
| categoryId | Long | 否 | — | 分类 ID |
| tagIds | List\<Long\> | 否 | — | 标签 ID 列表 |
| isTop | Integer | 否 | — | 是否置顶：0-否 1-是 |
| status | Integer | 否 | — | 状态：0-草稿 1-已发布 |

**请求示例：**

```json
{
  "title": "新文章标题",
  "summary": "文章摘要",
  "content": "# Markdown 正文内容",
  "coverImage": "http://example.com/cover.jpg",
  "categoryId": 1,
  "tagIds": [1, 2],
  "isTop": 0,
  "status": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2.1.2 删除文章

`POST /api/admin/article/delete`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 文章 ID |

**请求示例：**

```json
{
  "id": 1
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**说明：** 逻辑删除

---

#### 2.1.3 切换置顶状态

`POST /api/admin/article/top`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 文章 ID |

**说明：** 置顶 ↔ 取消置顶切换

---

### 2.2 分类管理

#### 2.2.1 新增/编辑分类

`POST /api/admin/category/save`

**请求参数：**

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| id | Long | 否 | — | 分类 ID，不传为新增 |
| name | String | 是 | — | 分类名 |
| sort | Integer | 否 | — | 排序值 |

**请求示例：**

```json
{
  "name": "技术",
  "sort": 1
}
```

---

#### 2.2.2 删除分类

`POST /api/admin/category/delete`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 分类 ID |

---

### 2.3 标签管理

#### 2.3.1 新增/编辑标签

`POST /api/admin/tag/save`

**请求参数：**

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| id | Long | 否 | — | 标签 ID，不传为新增 |
| name | String | 是 | — | 标签名 |

**请求示例：**

```json
{
  "name": "Java"
}
```

---

#### 2.3.2 删除标签

`POST /api/admin/tag/delete`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 标签 ID |

---

### 2.4 评论管理

#### 2.4.1 评论列表（分页）

`POST /api/admin/comment/list`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | Long | 否 | 按文章 ID 筛选 |
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "nickname": "张三",
        "avatar": "http://example.com/avatar.jpg",
        "content": "评论内容",
        "parentId": 0,
        "createTime": "2026-04-29T12:00:00",
        "children": null
      }
    ],
    "total": 50
  }
}
```

---

#### 2.4.2 删除评论

`POST /api/admin/comment/delete`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 评论 ID |

**说明：** 级联删除子评论（事务保证）

---

### 2.5 图片上传

#### 2.5.1 上传图片

`POST /api/admin/upload/image`

**Content-Type:** `multipart/form-data`

**请求参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件 |

**支持格式：** jpg, jpeg, png, gif, webp

**大小限制：** 10MB

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "http://localhost:8080/uploads/abc123.jpg"
  }
}
```

**错误情况：**
- 文件为空：`code: 500, message: "文件不能为空"`
- 格式不支持：`code: 500, message: "仅支持图片格式"`

---

## 三、数据模型

### ArticleDTO（文章列表出参）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 文章 ID |
| title | String | 标题 |
| summary | String | 摘要 |
| coverImage | String | 封面图 URL |
| categoryName | String | 分类名 |
| tagNames | List\<String\> | 标签名列表 |
| isTop | Integer | 是否置顶 |
| viewCount | Integer | 访问量（含 Redis 增量） |
| createTime | String | 创建时间 |

### ArticleDetailDTO（文章详情出参，继承 ArticleDTO）

| 字段 | 类型 | 说明 |
|------|------|------|
| content | String | 正文（Markdown） |
| tagIds | List\<Long\> | 标签 ID 列表（用于编辑回显） |

### CommentDTO（评论出参）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 评论 ID |
| nickname | String | 昵称 |
| avatar | String | 头像 URL |
| content | String | 评论内容 |
| parentId | Long | 父评论 ID |
| createTime | String | 创建时间 |
| children | List\<CommentDTO\> | 子评论列表 |

### CategoryDTO（分类出参）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 分类 ID |
| name | String | 分类名 |
| sort | Integer | 排序值 |

### TagDTO（标签出参）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 标签 ID |
| name | String | 标签名 |

---

## 四、Redis 缓存说明

| Key 格式 | 类型 | TTL | 说明 |
|----------|------|-----|------|
| `article:view:{id}` | String | 无 | 文章访问量计数，定时同步到 MySQL |
| `user:token:{token}` | String | 7 天 | 登录 Token 存储 |
| `user:role:{token}` | String | 7 天 | 用户角色缓存 |
