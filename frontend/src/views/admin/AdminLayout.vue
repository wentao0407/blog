<template>
  <el-container class="admin-layout">
    <el-aside width="200px">
      <div class="logo">胡言记 · 管理</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/article">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/tag">
          <el-icon><PriceTag /></el-icon>
          <span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comment">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="admin-user">{{ nickname || '管理员' }}</span>
          <el-button type="danger" text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const nickname = localStorage.getItem('nickname')

const titleMap = {
  '/admin/dashboard': '仪表盘',
  '/admin/article': '文章管理',
  '/admin/category': '分类管理',
  '/admin/tag': '标签管理',
  '/admin/comment': '评论管理'
}

const currentTitle = computed(() => {
  const path = route.path
  if (path.includes('/article/edit')) return '编辑文章'
  return titleMap[path] || ''
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.logo { padding: 20px; font-size: 18px; font-weight: 700; color: #5c4033; text-align: center; border-bottom: 1px solid #eee; }
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.admin-user {
  font-size: 14px;
  color: #666;
}
</style>
