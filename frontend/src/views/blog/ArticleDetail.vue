<template>
  <article v-if="article" class="article-detail">
    <h1 class="title">{{ article.title }}</h1>
    <div class="meta">
      {{ formatDate(article.createTime) }}
      <span v-if="article.categoryName"> · {{ article.categoryName }}</span>
      · 👁 {{ article.viewCount }}
    </div>
    <div class="content" v-html="renderedContent"></div>
    <div class="tags" v-if="article.tagNames?.length">
      <span v-for="tag in article.tagNames" :key="tag" class="tag">{{ tag }}</span>
    </div>
    <CommentList :article-id="article.id" />
  </article>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '../../api/article'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
import CommentList from '../../components/CommentList.vue'

const route = useRoute()
const article = ref(null)
const md = new MarkdownIt()

const renderedContent = computed(() => article.value ? DOMPurify.sanitize(md.render(article.value.content)) : '')
const formatDate = (d) => d ? d.substring(0, 10) : ''

onMounted(async () => {
  article.value = await getArticleDetail(route.params.id)
})
</script>

<style scoped>
.article-detail {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.title { font-size: 28px; color: #3d2e1e; font-family: serif; margin-bottom: 12px; }
.meta { font-size: 14px; color: #b8a089; margin-bottom: 24px; }
.content { font-size: 16px; line-height: 1.8; color: #3d2e1e; }
.tags { margin-top: 24px; display: flex; gap: 6px; }
.tag { background: #f0e6d6; color: #8b7355; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
</style>
