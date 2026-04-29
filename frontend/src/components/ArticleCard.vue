<template>
  <div class="article-card" @click="$router.push(`/article/${article.id}`)">
    <div v-if="article.coverImage" class="card-cover">
      <img :src="article.coverImage" :alt="article.title" />
    </div>
    <div class="card-body">
      <div class="card-meta">
        <span>{{ formatDate(article.createTime) }}</span>
        <span v-if="article.categoryName"> · {{ article.categoryName }}</span>
        <span v-if="article.isTop" class="top-badge">置顶</span>
      </div>
      <h3 class="card-title">{{ article.title }}</h3>
      <p class="card-summary">{{ article.summary }}</p>
      <div class="card-tags" v-if="article.tagNames?.length">
        <span v-for="tag in article.tagNames" :key="tag" class="tag">{{ tag }}</span>
      </div>
      <div class="card-footer">
        <span>👁 {{ article.viewCount || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  article: {
    type: Object,
    required: true
  }
})
const formatDate = (d) => d ? d.substring(0, 10) : ''
</script>

<style scoped>
.article-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.2s;
  margin-bottom: 20px;
}
.article-card:hover {
  transform: translateY(-2px);
}
.card-cover img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.card-body {
  padding: 16px;
}
.card-meta {
  font-size: 13px;
  color: #b8a089;
  margin-bottom: 8px;
}
.top-badge {
  background: #e8a87c;
  color: #fff;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 8px;
}
.card-title {
  font-size: 18px;
  color: #3d2e1e;
  margin: 0 0 8px;
  font-family: serif;
}
.card-summary {
  font-size: 14px;
  color: #8b7355;
  line-height: 1.6;
  margin: 0 0 8px;
}
.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.tag {
  background: #f0e6d6;
  color: #8b7355;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.card-footer {
  font-size: 13px;
  color: #b8a089;
}
</style>
