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
      <div class="logout-btn">
        <el-button type="danger" text @click="handleLogout">退出登录</el-button>
      </div>
    </el-aside>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
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
.el-aside { position: relative; }
.logout-btn {
  position: absolute;
  bottom: 20px;
  width: 100%;
  text-align: center;
}
</style>
