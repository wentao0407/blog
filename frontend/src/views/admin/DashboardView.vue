<template>
  <div>
    <h2>仪表盘</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="stat-label">总文章数</div>
          <div class="stat-value">{{ stats.articleCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-label">总评论数</div>
          <div class="stat-value">{{ stats.commentCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-label">总访问量</div>
          <div class="stat-value">{{ stats.viewCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSiteStats } from '../../api/site'

const stats = ref({})
onMounted(async () => {
  stats.value = await getSiteStats()
})
</script>

<style scoped>
.stat-label { font-size: 14px; color: #999; }
.stat-value { font-size: 32px; font-weight: 700; color: #5c4033; margin-top: 8px; }
</style>
