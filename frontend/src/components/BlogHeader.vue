<template>
  <header class="blog-header">
    <div class="header-inner">
      <router-link to="/" class="logo">胡言记</router-link>
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/archive">归档</router-link>
        <router-link to="/about">关于</router-link>
        <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
        <a v-else @click="handleLogout" class="logout-link">退出</a>
      </nav>
      <div class="search">
        <input v-model="keyword" placeholder="搜索文章..." @keyup.enter="search" />
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const keyword = ref('')
const router = useRouter()
const isLoggedIn = ref(!!localStorage.getItem('token'))
const search = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { q: keyword.value } })
  }
}
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  localStorage.removeItem('role')
  isLoggedIn.value = false
  router.push('/')
}
</script>

<style scoped>
.blog-header {
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 24px;
}
.logo {
  font-size: 24px;
  font-weight: 700;
  color: #5c4033;
  text-decoration: none;
  font-family: serif;
}
.nav {
  display: flex;
  gap: 16px;
}
.nav a {
  color: #8b7355;
  text-decoration: none;
  font-size: 15px;
}
.nav a:hover, .nav a.router-link-active {
  color: #5c4033;
}
.search {
  margin-left: auto;
}
.search input {
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 14px;
  outline: none;
  background: #f9f6f2;
}
.logout-link {
  color: #8b7355;
  text-decoration: none;
  font-size: 15px;
  cursor: pointer;
}
.logout-link:hover {
  color: #5c4033;
}
</style>
