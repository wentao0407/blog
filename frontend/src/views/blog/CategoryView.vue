<template>
  <div>
    <h2 class="page-title">分类：{{ categoryName }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div v-if="!articles.length" class="empty">该分类下暂无文章</div>
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

onMounted(async () => {
  const categories = await getCategoryList()
  const cat = categories.find(c => c.id == route.params.id)
  categoryName.value = cat ? cat.name : ''
  const data = await getArticleList({ pageNum: 1, pageSize: 100, categoryId: route.params.id })
  articles.value = data.records
})
</script>

<style scoped>
.page-title { color: #5c4033; font-family: serif; margin-bottom: 20px; }
.empty { color: #b8a089; text-align: center; padding: 40px; }
</style>
