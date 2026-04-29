<template>
  <div class="archive">
    <h2>归档</h2>
    <div v-for="item in archives" :key="item.month" class="archive-group">
      <h3>{{ item.month }} ({{ item.count }}篇)</h3>
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
.empty { color: #b8a089; text-align: center; padding: 40px; }
</style>
