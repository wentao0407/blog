<template>
  <div class="archive">
    <h2>归档</h2>
    <div v-for="item in archives" :key="item.month" class="archive-group">
      <h3>{{ item.month }} ({{ item.count }}篇)</h3>
      <ul class="article-list">
        <li v-for="a in item.articles" :key="a.id">
          <router-link :to="`/article/${a.id}`">{{ a.title }}</router-link>
          <span class="date">{{ a.createTime.substring(5, 10) }}</span>
        </li>
      </ul>
    </div>
    <div v-if="!archives.length" class="empty">暂无文章</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArchiveList } from '../../api/article'

const archives = ref([])
onMounted(async () => {
  archives.value = await getArchiveList()
})
</script>

<style scoped>
.archive { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.archive-group h3 { color: #5c4033; font-size: 16px; padding: 8px 0; border-bottom: 1px solid #f0e6d6; }
.article-list { list-style: none; padding: 0; margin: 0; }
.article-list li { display: flex; justify-content: space-between; align-items: center; padding: 8px 0 8px 12px; border-bottom: 1px solid #f5f0eb; }
.article-list a { color: #5c4033; text-decoration: none; font-size: 14px; }
.article-list a:hover { text-decoration: underline; }
.article-list .date { color: #b8a089; font-size: 13px; }
.empty { color: #b8a089; text-align: center; padding: 40px; }
</style>
