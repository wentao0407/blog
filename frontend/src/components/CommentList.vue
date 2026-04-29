<template>
  <div class="comment-section">
    <h3>评论 ({{ comments.length }})</h3>
    <div class="comment-form">
      <input v-if="!userId" v-model="form.nickname" placeholder="昵称" />
      <textarea v-model="form.content" placeholder="写下你的评论..." rows="3"></textarea>
      <button @click="submitComment">发表评论</button>
    </div>
    <div v-for="c in comments" :key="c.id" class="comment-item">
      <div class="comment-header">
        <span class="nickname">{{ c.nickname }}</span>
        <span class="time">{{ formatDate(c.createTime) }}</span>
      </div>
      <p class="comment-content">{{ c.content }}</p>
      <div v-if="c.children" class="comment-replies">
        <div v-for="reply in c.children" :key="reply.id" class="comment-item reply">
          <div class="comment-header">
            <span class="nickname">{{ reply.nickname }}</span>
            <span class="time">{{ formatDate(reply.createTime) }}</span>
          </div>
          <p class="comment-content">{{ reply.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCommentList, addComment } from '../api/comment'
import { ElMessage } from 'element-plus'

const props = defineProps({ articleId: Number })
const comments = ref([])
const form = ref({ nickname: '', content: '' })
const userId = localStorage.getItem('userId')

const formatDate = (d) => d ? d.substring(0, 16).replace('T', ' ') : ''

const loadComments = async () => {
  comments.value = await getCommentList(props.articleId)
}

const submitComment = async () => {
  if (!form.value.content.trim()) return ElMessage.warning('请输入评论内容')
  if (!userId && !form.value.nickname.trim()) return ElMessage.warning('请输入昵称')
  await addComment({
    articleId: props.articleId,
    nickname: userId ? (localStorage.getItem('nickname') || '用户') : (form.value.nickname || '匿名用户'),
    content: form.value.content
  })
  form.value.content = ''
  ElMessage.success('评论成功')
  await loadComments()
}

onMounted(loadComments)
</script>

<style scoped>
.comment-section { margin-top: 32px; border-top: 1px solid #eee; padding-top: 24px; }
.comment-form textarea { width: 100%; border: 1px solid #ddd; border-radius: 8px; padding: 12px; font-size: 14px; resize: vertical; margin: 8px 0; box-sizing: border-box; }
.comment-form input { border: 1px solid #ddd; border-radius: 8px; padding: 8px 12px; font-size: 14px; width: 200px; }
.comment-form button { background: #5c4033; color: #fff; border: none; padding: 8px 20px; border-radius: 8px; cursor: pointer; }
.comment-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.comment-item.reply { padding-left: 24px; border-bottom: none; }
.comment-header { font-size: 13px; margin-bottom: 4px; }
.nickname { font-weight: 600; color: #5c4033; }
.time { color: #b8a089; margin-left: 8px; }
.comment-content { font-size: 14px; color: #3d2e1e; margin: 0; }
</style>
