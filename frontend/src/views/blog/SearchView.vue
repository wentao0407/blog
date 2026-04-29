<template>
  <div>
    <h2 class="page-title">搜索：{{ keyword }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div v-if="!articles.length" class="empty">未找到相关文章</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleList } from '../../api/article'
import ArticleCard from '../../components/ArticleCard.vue'

const route = useRoute()
const articles = ref([])
const keyword = ref('')

const doSearch = async () => {
  keyword.value = route.query.q || ''
  if (keyword.value) {
    const data = await getArticleList({ pageNum: 1, pageSize: 100, keyword: keyword.value })
    articles.value = data.records
  }
}

onMounted(doSearch)
watch(() => route.query.q, doSearch)
</script>

<style scoped>
.page-title { color: #5c4033; font-family: serif; margin-bottom: 20px; }
.empty { color: #b8a089; text-align: center; padding: 40px; }
</style>
