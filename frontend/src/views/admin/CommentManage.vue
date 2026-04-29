<template>
  <div>
    <h2>评论管理</h2>
    <el-table :data="comments" border stripe>
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="content" label="内容" />
      <el-table-column label="时间" width="180">
        <template #default="{ row }">{{ row.createTime?.replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="10"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadComments"
      style="margin-top: 16px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminCommentList, deleteComment } from '../../api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const pageNum = ref(1)
const total = ref(0)

const loadComments = async () => {
  const data = await getAdminCommentList({ pageNum: pageNum.value, pageSize: 10 })
  comments.value = data.records
  total.value = data.total
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该评论？', '提示')
  await deleteComment(id)
  ElMessage.success('删除成功')
  loadComments()
}

onMounted(loadComments)
</script>

<style scoped>
</style>
