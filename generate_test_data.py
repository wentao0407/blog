#!/usr/bin/env python3
"""生成博客测试数据"""
import random
import hashlib
from datetime import datetime, timedelta
import pymysql

# 数据库连接
conn = pymysql.connect(
    host='localhost', user='root', password='root123',
    database='blog', charset='utf8mb4', autocommit=True
)
cursor = conn.cursor()

# ========== 清空现有数据 ==========
print("清空现有数据...")
tables = ['t_article_tag', 't_comment', 't_article', 't_category', 't_tag']
for table in tables:
    cursor.execute(f"DELETE FROM {table}")
    cursor.execute(f"ALTER TABLE {table} AUTO_INCREMENT = 1")

# 保留管理员账号
cursor.execute("DELETE FROM t_user WHERE username != 'admin'")

# ========== 创建分类 ==========
print("创建分类...")
categories = [
    ('技术分享', 1), ('后端开发', 2), ('前端开发', 3), ('数据库', 4),
    ('运维部署', 5), ('生活随笔', 6), ('读书笔记', 7), ('项目实战', 8)
]
for name, sort in categories:
    cursor.execute("INSERT INTO t_category (name, sort) VALUES (%s, %s)", (name, sort))

# ========== 创建标签 ==========
print("创建标签...")
tags = [
    'Java', 'Spring Boot', 'Vue', 'MySQL', 'Redis', 'Docker', 'Linux',
    'JavaScript', 'TypeScript', 'Python', 'Git', 'Maven', 'Nginx',
    '微服务', '设计模式', '算法', '数据结构', '性能优化', '安全', '单元测试',
    'React', 'Node.js', 'MongoDB', 'Elasticsearch', 'Kafka', 'RabbitMQ',
    'REST API', 'GraphQL', 'CI/CD', '云原生'
]
for name in tags:
    cursor.execute("INSERT INTO t_tag (name) VALUES (%s)", (name,))

# ========== 创建测试用户 ==========
print("创建测试用户...")
users = [
    ('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@example.com'),
    ('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@example.com'),
    ('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@example.com'),
    ('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'zhaoliu@example.com'),
    ('chenqi', 'e10adc3949ba59abbe56e057f20f883e', '陈七', 'chenqi@example.com'),
    ('zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', 'zhouba@example.com'),
    ('liujing', 'e10adc3949ba59abbe56e057f20f883e', '刘静', 'liujing@example.com'),
    ('yangmei', 'e10adc3949ba59abbe56e057f20f883e', '杨梅', 'yangmei@example.com'),
    ('huangwei', 'e10adc3949ba59abbe56e057f20f883e', '黄伟', 'huangwei@example.com'),
    ('xiaoming', 'e10adc3949ba59abbe56e057f20f883e', '小明', 'xiaoming@example.com'),
]
for username, password, nickname, email in users:
    cursor.execute(
        "INSERT INTO t_user (username, password, nickname, email, role) VALUES (%s, %s, %s, %s, 0)",
        (username, password, nickname, email)
    )

# ========== 文章内容模板 ==========
article_templates = [
    # (title, summary, content_template, category_id, tag_ids)
    ("Spring Boot 3.2 新特性详解", "深入解析 Spring Boot 3.2 版本带来的重大改进和新功能",
     """## 前言

Spring Boot 3.2 带来了许多令人兴奋的新特性，本文将详细介绍这些改进。

## 虚拟线程支持

Java 21 引入的虚拟线程在 Spring Boot 3.2 中得到了更好的支持：

```java
@Bean
public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler -> {
        protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    };
}
```

## RestClient

新的 RestClient API 提供了更现代的 HTTP 客户端体验：

```java
RestClient client = RestClient.builder()
    .baseUrl("https://api.example.com")
    .build();

String result = client.get()
    .uri("/users/{id}", 1)
    .retrieve()
    .body(String.class);
```

## 总结

Spring Boot 3.2 是一个重要的版本更新，建议尽快升级体验。""", 2, [1, 2]),

    ("Vue 3 组合式 API 最佳实践", "掌握 Vue 3 组合式 API 的核心用法和设计模式",
     """## 为什么要用组合式 API

Vue 3 的组合式 API 提供了更好的代码组织方式。

## 响应式基础

```javascript
import { ref, computed, watch } from 'vue'

const count = ref(0)
const doubleCount = computed(() => count.value * 2)

watch(count, (newVal, oldVal) => {
  console.log(`count changed: ${oldVal} -> ${newVal}`)
})
```

## 自定义 Hook

```javascript
// useCounter.js
export function useCounter(initialValue = 0) {
  const count = ref(initialValue)
  const increment = () => count.value++
  const decrement = () => count.value--
  return { count, increment, decrement }
}
```

## 生命周期

```javascript
import { onMounted, onUnmounted } from 'vue'

export function useEventListener(target, event, callback) {
  onMounted(() => target.addEventListener(event, callback))
  onUnmounted(() => target.removeEventListener(event, callback))
}
```

## 总结

组合式 API 让代码更加模块化和可复用。""", 3, [3, 8]),

    ("MySQL 索引优化实战指南", "通过实际案例学习 MySQL 索引的设计与优化",
     """## 索引基础

索引是数据库性能优化的关键。

## B+ 树索引结构

```
[10 | 20 | 30]
  |    |    |
[1-9] [11-19] [21-29]
```

## 联合索引遵循最左前缀

```sql
-- 创建联合索引
CREATE INDEX idx_name_age ON users(name, age);

-- 能使用索引
SELECT * FROM users WHERE name = '张三';
SELECT * FROM users WHERE name = '张三' AND age = 25;

-- 不能使用索引
SELECT * FROM users WHERE age = 25;
```

## EXPLAIN 分析

```sql
EXPLAIN SELECT * FROM articles WHERE title LIKE 'Spring%';

-- 关注字段：type, key, rows, Extra
```

## 常见优化建议

1. 避免在索引列上使用函数
2. 避免隐式类型转换
3. 使用覆盖索引减少回表

## 总结

合理的索引设计能让查询性能提升数十倍。""", 4, [4, 18]),

    ("Docker 容器化部署实践", "从零开始学习 Docker 容器化部署 Spring Boot 应用",
     """## Dockerfile 编写

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass root123
```

## 常用命令

```bash
docker build -t myapp .
docker run -d -p 8080:8080 myapp
docker logs -f container_id
```

## 总结

Docker 让部署变得简单可靠。""", 5, [6, 14]),

    ("JavaScript 异步编程完全指南", "深入理解 Promise、async/await 和事件循环",
     """## 回调地狱

```javascript
getData(function(a) {
  getMoreData(a, function(b) {
    getMoreData(b, function(c) {
      console.log(c);
    });
  });
});
```

## Promise

```javascript
getData()
  .then(a => getMoreData(a))
  .then(b => getMoreData(b))
  .then(c => console.log(c))
  .catch(err => console.error(err));
```

## async/await

```javascript
async function fetchData() {
  try {
    const a = await getData();
    const b = await getMoreData(a);
    const c = await getMoreData(b);
    console.log(c);
  } catch (err) {
    console.error(err);
  }
}
```

## 事件循环

```
调用栈 -> 微任务队列 -> 宏任务队列
```

## 总结

async/await 是目前最优雅的异步解决方案。""", 3, [8, 9]),

    ("Redis 缓存策略与常见问题", "详解 Redis 缓存穿透、击穿、雪崩及解决方案",
     """## 缓存穿透

查询不存在的数据，每次都穿透到数据库。

```java
// 布隆过滤器或缓存空值
if (result == null) {
    redisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
}
```

## 缓存击穿

热点 key 过期，大量请求同时到达数据库。

```java
// 互斥锁
String lockKey = "lock:" + key;
if (redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS)) {
    // 从数据库加载并缓存
} else {
    // 等待重试
}
```

## 缓存雪崩

大量 key 同时过期。

```java
// 设置随机过期时间
int randomTTL = baseTTL + RandomUtils.nextInt(0, 300);
redisTemplate.opsForValue().set(key, value, randomTTL, TimeUnit.SECONDS);
```

## 总结

合理使用缓存能极大提升系统性能。""", 4, [5, 18]),

    ("Git 分支管理最佳实践", "学习如何在团队中有效管理 Git 分支",
     """## Git Flow

```
main ─────────────────────────────>
  └─ develop ─────────────────────>
       ├─ feature/xxx ──┐
       ├─ feature/yyy ──┤
       └─ release/1.0 ──┘
```

## 常用命令

```bash
# 创建特性分支
git checkout -b feature/user-auth develop

# 合并到 develop
git checkout develop
git merge --no-ff feature/user-auth

# 创建发布分支
git checkout -b release/1.0 develop

# 合并到 main 并打标签
git checkout main
git merge --no-ff release/1.0
git tag -a v1.0 -m "Release 1.0"
```

## Commit 规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

## 总结

好的分支策略能让团队协作更顺畅。""", 1, [11]),

    ("设计模式之单例模式", "深入理解单例模式的多种实现方式及其应用场景",
     """## 饿汉式

```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

## 懒汉式（双重检查锁）

```java
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## 枚举实现

```java
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}
```

## 应用场景

- 数据库连接池
- 配置管理器
- 日志记录器

## 总结

枚举单例是最推荐的实现方式。""", 1, [15, 1]),

    ("前端性能优化技巧总结", "提升 Web 应用性能的实用技巧和工具",
     """## 代码分割

```javascript
// 路由懒加载
const Home = () => import('./views/Home.vue');
const About = () => import('./views/About.vue');
```

## 图片优化

```html
<!-- 使用 WebP 格式 -->
<picture>
  <source srcset="image.webp" type="image/webp">
  <img src="image.jpg" alt="...">
</picture>

<!-- 懒加载 -->
<img loading="lazy" src="..." alt="...">
```

## 虚拟滚动

```javascript
// 大列表使用虚拟滚动
import { useVirtualList } from '@vueuse/core';

const { list, containerProps, wrapperProps } = useVirtualList(
  data,
  { itemHeight: 50 }
);
```

## 缓存策略

```nginx
# 静态资源缓存
location ~* \\.(js|css|png|jpg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## 总结

性能优化需要持续关注和改进。""", 3, [3, 8, 18]),

    ("Spring Security 入门教程", "快速上手 Spring Security 实现用户认证与授权",
     """## 基础配置

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
```

## JWT 认证

```java
@Component
public class JwtTokenProvider {
    private String secret = "your-secret-key";

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact();
    }
}
```

## 总结

Spring Security 提供了完整的安全解决方案。""", 2, [2, 19]),

    ("Linux 常用命令速查表", "开发必备的 Linux 命令大全",
     """## 文件操作

```bash
# 查找文件
find / -name "*.log" -mtime -7

# 查看文件大小
du -sh /var/log/*

# 搜索内容
grep -r "error" /var/log/
```

## 进程管理

```bash
# 查看进程
ps aux | grep java

# 杀死进程
kill -9 PID

# 后台运行
nohup java -jar app.jar &
```

## 网络工具

```bash
# 查看端口
netstat -tlnp | grep 8080

# 测试连通性
curl -v http://localhost:8080/health

# 下载文件
wget https://example.com/file.zip
```

## 系统监控

```bash
# CPU 和内存
top
htop

# 磁盘
df -h
```

## 总结

熟练掌握这些命令能大大提高工作效率。""", 5, [7]),

    ("MyBatis-Plus 使用技巧", "掌握 MyBatis-Plus 的高级用法提升开发效率",
     """## 条件构造器

```java
// Lambda 查询
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.like(User::getName, "张")
       .ge(User::getAge, 18)
       .orderByDesc(User::getCreateTime);
List<User> users = userMapper.selectList(wrapper);
```

## 分页查询

```java
Page<User> page = new Page<>(1, 10);
Page<User> result = userMapper.selectPage(page, wrapper);
// result.getRecords() - 数据列表
// result.getTotal() - 总数
```

## 逻辑删除

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 自动填充

```java
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
    }
}
```

## 总结

MyBatis-Plus 大大简化了数据库操作。""", 2, [1, 2, 4]),
]

# 更多文章数据
more_articles = [
    ("Webpack vs Vite 构建工具对比", "详细对比两款主流前端构建工具的优劣"),
    ("PostgreSQL 高级查询技巧", "学习 PostgreSQL 特有的高级查询语法"),
    ("微服务架构设计原则", "构建可扩展微服务系统的核心设计原则"),
    ("CSS Grid 布局完全指南", "掌握 CSS Grid 实现复杂页面布局"),
    ("Spring Boot 集成 Elasticsearch", "在 Spring Boot 项目中集成全文搜索"),
    ("Vue Router 路由守卫实战", "实现前端路由权限控制"),
    ("Docker Compose 编排多容器应用", "使用 Docker Compose 管理复杂应用"),
    ("Nginx 反向代理与负载均衡", "配置 Nginx 实现高可用架构"),
    ("Java 并发编程基础", "理解线程、锁和并发工具类"),
    ("前端状态管理方案对比", "Vuex vs Pinia vs Zustand"),
    ("MySQL 主从复制配置", "实现数据库读写分离"),
    ("RESTful API 设计规范", "设计高质量的 REST API"),
    ("Vue 3 Teleport 组件使用", "实现模态框等全局组件"),
    ("Spring Boot 自定义 Starter", "创建可复用的 Spring Boot 组件"),
    ("Redis 数据结构与应用场景", "深入理解 Redis 五种基础数据结构"),
    ("TypeScript 泛型编程", "掌握 TypeScript 泛型的高级用法"),
    ("Docker 网络模式详解", "理解 bridge、host、none 网络模式"),
    ("前端自动化测试实践", "使用 Vitest 编写单元测试"),
    ("MySQL 慢查询分析与优化", "定位并解决慢 SQL 问题"),
    ("Spring Boot 配置文件最佳实践", "多环境配置管理方案"),
    ("Vue 3 自定义指令开发", "创建实用的自定义指令"),
    ("Git Rebase vs Merge", "理解两种分支合并策略的区别"),
    ("Redis 持久化机制", "RDB 和 AOF 的原理与选择"),
    ("前端工程化实践", "代码规范、Git Hooks、CI/CD"),
    ("Spring Boot 异常处理最佳实践", "全局异常处理方案"),
    ("CSS 动画性能优化", "避免重排重绘提升动画流畅度"),
    ("MySQL 事务隔离级别", "理解四种隔离级别及其应用场景"),
    ("Kafka 消息队列入门", "理解消息队列的核心概念"),
    ("Vue 3 响应式原理深入", "理解 Proxy 和 Reflect"),
    ("Nginx 配置 HTTPS", "为网站配置 SSL 证书"),
    ("Java Stream API 实战", "使用 Stream 简化集合操作"),
    ("前端监控系统设计", "实现错误监控和性能监控"),
    ("Spring Boot 集成 Redis 缓存", "使用注解实现缓存管理"),
    ("React Hooks 使用指南", "掌握常用 Hooks 的用法"),
    ("MySQL 分库分表方案", "应对海量数据的存储方案"),
    ("Vue 3 组件通信方式汇总", "父子组件、跨组件通信方案"),
    ("Docker 镜像优化技巧", "减小镜像体积提升构建速度"),
    ("Spring Boot 定时任务", "使用 @Scheduled 实现定时任务"),
    ("前端性能指标与监控", "LCP、FID、CLS 指标详解"),
    ("Redis 分布式锁实现", "基于 Redis 的分布式锁方案"),
    ("TypeScript 装饰器使用", "理解装饰器的原理和应用"),
    ("MySQL 索引失效的常见场景", "避免索引失效的编码规范"),
    ("Vue 3 插件开发", "创建可复用的 Vue 插件"),
    ("Spring Boot 文件上传下载", "实现完整的文件管理功能"),
    ("CSS Flexbox 布局技巧", "掌握 Flexbox 实现灵活布局"),
    ("Redis 集群搭建", "配置 Redis 高可用集群"),
    ("前端路由实现原理", "理解 Hash 和 History 路由"),
    ("Spring Boot 接口文档生成", "使用 Swagger/OpenAPI 生成文档"),
    ("Git 常用技巧汇总", "提升 Git 使用效率的技巧"),
    ("MySQL 查询优化实战", "通过实际案例学习查询优化"),
    ("Vue 3 SSR 服务端渲染", "使用 Nuxt.js 实现 SSR"),
    ("Docker 容器安全实践", "容器安全最佳实践"),
    ("Spring Boot 日志管理", "配置 Logback 实现日志管理"),
    ("前端打包优化策略", "减小打包体积提升加载速度"),
    ("Redis 内存优化", "合理使用内存避免 OOM"),
    ("Java 反射机制详解", "理解反射的原理和应用"),
    ("Vue 3 国际化方案", "使用 vue-i18n 实现多语言"),
    ("MySQL 死锁分析与预防", "理解死锁产生的原因和解决方案"),
    ("Spring Boot 单元测试", "使用 JUnit 5 编写测试"),
    ("CSS 变量使用技巧", "使用 CSS 自定义属性"),
    ("Redis 发布订阅模式", "实现消息的发布和订阅"),
    ("前端安全防护", "XSS、CSRF 防护方案"),
    ("Spring Boot 优雅停机", "实现应用的平滑重启"),
    ("Vue 3 性能优化技巧", "提升 Vue 应用的性能"),
    ("MySQL 数据备份与恢复", "保障数据安全的备份策略"),
    ("Docker 数据卷管理", "持久化容器数据"),
    ("Spring Boot 接口限流", "使用注解实现接口限流"),
    ("TypeScript 类型体操", "高级类型编程技巧"),
    ("Redis 延迟队列实现", "基于 Redis 的延迟队列方案"),
    ("Vue 3 动态组件", "实现组件的动态加载"),
    ("MySQL 分页查询优化", "大数据量分页的优化方案"),
    ("Spring Boot 参数校验", "使用注解实现参数校验"),
    ("CSS 响应式设计", "实现多端适配的布局"),
    ("Redis 缓存一致性", "保证缓存与数据库的一致性"),
    ("前端构建流程优化", "提升构建速度和产出质量"),
    ("Spring Boot 多数据源配置", "配置和管理多个数据源"),
    ("Vue 3 Teleport 实战", "全局弹窗组件实现"),
    ("MySQL JSON 字段使用", "使用 JSON 类型存储复杂数据"),
    ("Docker 环境变量管理", "管理容器的配置信息"),
    ("Spring Boot 接口版本管理", "实现 API 的版本控制"),
    ("TypeScript 配置详解", "tsconfig.json 最佳配置"),
    ("Redis 分布式 Session", "使用 Redis 管理用户会话"),
    ("Vue 3 渲染函数", "使用 h() 函数创建组件"),
    ("MySQL 窗口函数", "使用 ROW_NUMBER、RANK 等函数"),
    ("Spring Boot 异步处理", "使用 @Async 实现异步任务"),
    ("CSS 预处理器对比", "Sass vs Less vs Stylus"),
    ("Redis Stream 使用", "实现消息队列功能"),
    ("前端模块化方案", "ES Module vs CommonJS"),
    ("Spring Boot 缓存注解", "@Cacheable、@CacheEvict 使用"),
    ("Vue 3 Provide/Inject", "跨层级组件通信"),
    ("MySQL 联合查询优化", "优化多表关联查询"),
    ("Docker 健康检查", "配置容器健康检查"),
    ("Spring Boot 自定义注解", "创建自定义注解实现功能"),
    ("TypeScript 接口与类型别名", "interface vs type"),
    ("Redis 地理位置功能", "实现附近的人功能"),
    ("Vue 3 异步组件", "实现组件的懒加载"),
    ("MySQL 数据导入导出", "使用 LOAD DATA 和 SELECT INTO"),
]

# 生成更多文章内容
contents = [
    "## 前言\n\n本文将详细介绍这个主题。\n\n## 基础概念\n\n首先了解基本概念...\n\n## 实践案例\n\n通过实际案例来学习...\n\n## 总结\n\n希望本文对你有所帮助。",
    "## 背景\n\n在实际开发中经常会遇到这个问题。\n\n## 解决方案\n\n```java\n// 代码示例\npublic void solve() {\n    // 实现细节\n}\n```\n\n## 注意事项\n\n1. 注意性能\n2. 考虑边界情况\n\n## 总结\n\n以上就是完整的解决方案。",
    "## 概述\n\n这是一个重要的技术点。\n\n## 详细说明\n\n### 第一步\n\n...\n\n### 第二步\n\n...\n\n## 代码示例\n\n```javascript\nconst example = () => {\n    console.log('Hello');\n};\n```\n\n## 总结\n\n掌握这个技能很重要。",
    "## 什么是 XXX\n\nXXX 是一种常用的技术方案。\n\n## 使用场景\n\n1. 场景一\n2. 场景二\n3. 场景三\n\n## 实现方式\n\n```python\ndef implement():\n    pass\n```\n\n## 最佳实践\n\n遵循以下原则...\n\n## 总结\n\n合理使用能提升开发效率。",
    "## 问题描述\n\n在开发过程中遇到了这个问题。\n\n## 原因分析\n\n经过分析发现...\n\n## 解决步骤\n\n1. 首先...\n2. 然后...\n3. 最后...\n\n## 验证结果\n\n问题已解决。\n\n## 经验总结\n\n遇到类似问题可以参考这个思路。",
]

# 评论内容
comment_contents = [
    "写得很好，学到了很多！",
    "感谢分享，正是我需要的。",
    "请问有完整的代码示例吗？",
    "这个问题困扰了我很久，终于解决了。",
    "收藏了，回头仔细看看。",
    "博主能详细讲讲这个部分吗？",
    "实测有效，感谢！",
    "这个方案在生产环境用过吗？稳定吗？",
    "有没有性能对比数据？",
    "写得通俗易懂，点赞！",
    "我按照这个方法试了，确实可以。",
    "有个疑问，如果遇到这种情况怎么办？",
    "博主更新频率很高啊，支持！",
    "这篇文章帮了我大忙，谢谢！",
    "能再写一篇关于 XXX 的文章吗？",
    "代码格式有点问题，建议检查一下。",
    "终于找到靠谱的教程了。",
    "这个思路很巧妙，学习了。",
    "有没有相关的视频教程推荐？",
    "博主是用什么编辑器写的？",
]

# 回复内容
reply_contents = [
    "谢谢支持！",
    "代码已更新，可以再看看。",
    "好问题，我会补充说明。",
    "是的，这个方案已在生产环境验证。",
    "欢迎持续关注！",
    "这个问题我会单独写一篇文章。",
    "感谢指正，已修改。",
    "可以参考官方文档。",
    "后续会更新更多内容。",
    "互相学习！",
]

# 生成用户昵称列表用于评论
comment_nicknames = [
    '小明', '大白', '码农小张', '前端小白', 'Java学徒',
    '路人甲', '技术爱好者', '程序猿', '攻城狮', '产品汪',
    '测试妹子', '运维大叔', '全栈工程师', '架构师老王', '数据库管理员',
    '小李飞刀', '代码搬运工', 'Bug猎人', '开源爱好者', '技术宅',
]

# ========== 生成文章 ==========
print("生成文章...")
base_time = datetime(2025, 1, 1, 10, 0, 0)
article_id = 0

for title, summary, content, cat_id, tag_ids in article_templates:
    article_id += 1
    create_time = base_time + timedelta(days=random.randint(0, 450), hours=random.randint(0, 23))
    view_count = random.randint(50, 5000)
    is_top = 1 if article_id <= 3 else 0

    cursor.execute(
        "INSERT INTO t_article (title, summary, content, category_id, is_top, status, view_count, create_time) "
        "VALUES (%s, %s, %s, %s, %s, 1, %s, %s)",
        (title, summary, content, cat_id, is_top, view_count, create_time)
    )

    for tag_id in tag_ids:
        cursor.execute("INSERT INTO t_article_tag (article_id, tag_id) VALUES (%s, %s)", (article_id, tag_id))

# 生成更多文章
for title, summary in more_articles:
    article_id += 1
    cat_id = random.randint(1, 8)
    content = random.choice(contents)
    create_time = base_time + timedelta(days=random.randint(0, 450), hours=random.randint(0, 23))
    view_count = random.randint(10, 3000)

    cursor.execute(
        "INSERT INTO t_article (title, summary, content, category_id, is_top, status, view_count, create_time) "
        "VALUES (%s, %s, %s, %s, 0, 1, %s, %s)",
        (title, summary, content, cat_id, view_count, create_time)
    )

    # 随机分配 1-3 个标签
    article_tags = random.sample(range(1, 31), random.randint(1, 3))
    for tag_id in article_tags:
        cursor.execute("INSERT INTO t_article_tag (article_id, tag_id) VALUES (%s, %s)", (article_id, tag_id))

# ========== 生成评论 ==========
print("生成评论...")
comment_id = 0
for art_id in range(1, article_id + 1):
    # 每篇文章 0-8 条评论
    num_comments = random.randint(0, 8)
    for _ in range(num_comments):
        comment_id += 1
        nickname = random.choice(comment_nicknames)
        content = random.choice(comment_contents)
        user_id = random.randint(2, 11) if random.random() > 0.3 else None
        create_time = base_time + timedelta(days=random.randint(0, 450), hours=random.randint(0, 23))

        cursor.execute(
            "INSERT INTO t_comment (article_id, user_id, nickname, content, parent_id, create_time) "
            "VALUES (%s, %s, %s, %s, 0, %s)",
            (art_id, user_id, nickname, content, create_time)
        )

        # 30% 概率有回复
        if random.random() < 0.3:
            reply_content = random.choice(reply_contents)
            reply_nickname = random.choice(['博主', '管理员', '胡言记'])
            reply_time = create_time + timedelta(hours=random.randint(1, 48))

            cursor.execute(
                "INSERT INTO t_comment (article_id, user_id, nickname, content, parent_id, create_time) "
                "VALUES (%s, 1, %s, %s, %s, %s)",
                (art_id, reply_nickname, reply_content, comment_id, reply_time)
            )

            # 20% 概率有多轮回复
            if random.random() < 0.2:
                reply2_content = random.choice(comment_contents)
                reply2_nickname = random.choice(comment_nicknames)
                reply2_time = reply_time + timedelta(hours=random.randint(1, 24))

                cursor.execute(
                    "INSERT INTO t_comment (article_id, user_id, nickname, content, parent_id, create_time) "
                    "VALUES (%s, %s, %s, %s, %s, %s)",
                    (art_id, user_id if user_id else random.randint(2, 11), reply2_nickname, reply2_content, comment_id, reply2_time)
                )

print(f"\n数据生成完成！")
print(f"- 文章: {article_id} 篇")
print(f"- 评论: {comment_id}+ 条")
print(f"- 分类: {len(categories)} 个")
print(f"- 标签: {len(tags)} 个")
print(f"- 用户: {len(users) + 1} 个")

conn.close()
