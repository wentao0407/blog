<template>
  <div class="blog-container">
    <BlogHeader />
    <main class="blog-main">
      <router-view />
    </main>
    <BlogFooter />
  </div>
</template>

<script setup>
import BlogHeader from '../../components/BlogHeader.vue'
import BlogFooter from '../../components/BlogFooter.vue'
</script>

<style scoped>
.blog-container {
  min-height: 100vh;
  background: #f5f0eb;
  display: flex;
  flex-direction: column;
}
.blog-main {
  flex: 1;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  padding: 24px 16px;
}
</style>
