<template>
  <div>
    <div class="header">
      <h2>文章管理</h2>
      <el-button type="primary" @click="$router.push('/admin/article/edit')">新建文章</el-button>
    </div>
    <el-table :data="articles" border stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="标签" width="200">
        <template #default="{ row }">
          <el-tag v-for="tag in row.tagNames" :key="tag" size="small" style="margin-right:4px">{{ tag }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="置顶" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.isTop === 1" @change="handleToggleTop(row.id)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/admin/article/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="10"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadArticles"
      style="margin-top: 16px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticleList, deleteArticle, toggleTopArticle } from '../../api/article'
import { ElMessage, ElMessageBox } from 'element-plus'

const articles = ref([])
const pageNum = ref(1)
const total = ref(0)

const loadArticles = async () => {
  const data = await getArticleList({ pageNum: pageNum.value, pageSize: 10 })
  articles.value = data.records
  total.value = data.total
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该文章？', '提示')
  await deleteArticle(id)
  ElMessage.success('删除成功')
  loadArticles()
}

const handleToggleTop = async (id) => {
  await toggleTopArticle(id)
  loadArticles()
}

onMounted(loadArticles)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
