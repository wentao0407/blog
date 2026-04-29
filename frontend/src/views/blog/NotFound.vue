<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面未找到</p>
    <router-link to="/" class="back-home">返回首页</router-link>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 80px 20px;
}
.not-found h1 {
  font-size: 72px;
  color: #b8a089;
  font-family: serif;
  margin-bottom: 16px;
}
.not-found p {
  font-size: 18px;
  color: #8b7355;
  margin-bottom: 24px;
}
.back-home {
  color: #5c4033;
  text-decoration: underline;
  font-size: 16px;
}
</style>
