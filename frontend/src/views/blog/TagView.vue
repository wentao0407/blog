<template>
  <div>
    <h2 class="page-title">标签：{{ tagName }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div v-if="!articles.length" class="empty">该标签下暂无文章</div>
    <div class="load-more" v-if="hasMore" @click="loadMore">加载更多</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleList } from '../../api/article'
import { getTagList } from '../../api/tag'
import ArticleCard from '../../components/ArticleCard.vue'

const route = useRoute()
const articles = ref([])
const tagName = ref('')
const pageNum = ref(1)
const hasMore = ref(true)

const loadArticles = async () => {
  const data = await getArticleList({ pageNum: pageNum.value, pageSize: 10, tagId: route.params.id })
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
  const tags = await getTagList()
  const tag = tags.find(t => t.id == route.params.id)
  tagName.value = tag ? tag.name : ''
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
