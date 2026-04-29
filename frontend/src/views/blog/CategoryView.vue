<template>
  <div>
    <h2 class="page-title">分类：{{ categoryName }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div v-if="!articles.length" class="empty">该分类下暂无文章</div>
    <div class="load-more" v-if="hasMore" @click="loadMore">加载更多</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleList } from '../../api/article'
import { getCategoryList } from '../../api/category'
import ArticleCard from '../../components/ArticleCard.vue'

const route = useRoute()
const articles = ref([])
const categoryName = ref('')
const pageNum = ref(1)
const hasMore = ref(true)

const loadArticles = async () => {
  const data = await getArticleList({ pageNum: pageNum.value, pageSize: 10, categoryId: route.params.id })
  if (pageNum.value === 1) {
    articles.value = data.records
  } else {
    articles.value.push(...data.records)
  }
  hasMore.value = data.records.length === 10
}

const loadMore = () => {
  pageNum.value++
  loadArticles()
}

onMounted(async () => {
  const categories = await getCategoryList()
  const cat = categories.find(c => c.id == route.params.id)
  categoryName.value = cat ? cat.name : ''
  await loadArticles()
})
</script>

<style scoped>
.page-title { color: #5c4033; font-family: serif; margin-bottom: 20px; }
.empty { color: #b8a089; text-align: center; padding: 40px; }
.load-more {
  text-align: center;
  padding: 16px;
  color: #8b7355;
  cursor: pointer;
}
</style>
