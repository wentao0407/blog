<template>
  <div>
    <div v-if="topArticles.length" class="top-section">
      <ArticleCard v-for="a in topArticles" :key="a.id" :article="a" />
    </div>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div class="load-more" v-if="hasMore" @click="loadMore">加载更多</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticleList, getTopArticles } from '../../api/article'
import ArticleCard from '../../components/ArticleCard.vue'

const articles = ref([])
const topArticles = ref([])
const pageNum = ref(1)
const hasMore = ref(true)

const loadArticles = async () => {
  const data = await getArticleList({ pageNum: pageNum.value, pageSize: 10 })
  const topIds = new Set(topArticles.value.map(a => a.id))
  // 排除已在置顶区展示的文章
  const filtered = data.records.filter(a => !topIds.has(a.id))
  if (pageNum.value === 1) {
    articles.value = filtered
  } else {
    articles.value.push(...filtered)
  }
  hasMore.value = data.records.length === 10
}

const loadMore = () => {
  pageNum.value++
  loadArticles()
}

onMounted(async () => {
  topArticles.value = await getTopArticles()
  await loadArticles()
})
</script>

<style scoped>
.top-section { margin-bottom: 24px; }
.load-more {
  text-align: center;
  padding: 16px;
  color: #8b7355;
  cursor: pointer;
}
</style>
