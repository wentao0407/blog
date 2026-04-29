<template>
  <div>
    <h2 class="page-title">标签：{{ tagName }}</h2>
    <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    <div v-if="!articles.length" class="empty">该标签下暂无文章</div>
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

onMounted(async () => {
  const tags = await getTagList()
  const tag = tags.find(t => t.id == route.params.id)
  tagName.value = tag ? tag.name : ''
  const data = await getArticleList({ pageNum: 1, pageSize: 100, tagId: route.params.id })
  articles.value = data.records
})
</script>

<style scoped>
.page-title { color: #5c4033; font-family: serif; margin-bottom: 20px; }
.empty { color: #b8a089; text-align: center; padding: 40px; }
</style>
