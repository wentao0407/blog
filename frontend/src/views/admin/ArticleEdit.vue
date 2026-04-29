<template>
  <div>
    <h2>{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="文章标题" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" />
      </el-form-item>
      <el-form-item label="封面图">
        <el-upload
          class="cover-uploader"
          :show-file-list="false"
          :before-upload="beforeCoverUpload"
          :http-request="handleCoverUpload"
        >
          <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
          <div v-else class="cover-placeholder">
            <el-icon size="28"><Plus /></el-icon>
            <span>点击上传封面图</span>
          </div>
        </el-upload>
        <el-button v-if="form.coverImage" size="small" type="danger" text @click="form.coverImage = ''" style="margin-top: 8px;">移除</el-button>
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple placeholder="选择标签">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="内容">
        <MarkdownEditor v-model="form.content" style="width: 100%; border: 1px solid #dcdfe6; border-radius: 4px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit(1)">发布</el-button>
        <el-button @click="handleSubmit(0)">存为草稿</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail, saveArticle, uploadImage } from '../../api/article'
import { getCategoryList } from '../../api/category'
import { getTagList } from '../../api/tag'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import MarkdownEditor from '../../components/MarkdownEditor.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const form = ref({
  title: '', summary: '', content: '', coverImage: '',
  categoryId: null, tagIds: [], isTop: 0, status: 1
})
const categories = ref([])
const tags = ref([])

const handleSubmit = async (status) => {
  if (!form.value.title) return ElMessage.warning('请输入标题')
  if (!form.value.content) return ElMessage.warning('请输入内容')
  await saveArticle({ ...form.value, status })
  ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
  router.push('/admin/article')
}

const beforeCoverUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) ElMessage.error('仅支持 JPG/PNG/GIF/WEBP 格式')
  if (!isLt5M) ElMessage.error('图片大小不能超过 5MB')
  return isImage && isLt5M
}

const handleCoverUpload = async (options) => {
  try {
    const data = await uploadImage(options.file)
    form.value.coverImage = data.url
    ElMessage.success('封面图上传成功')
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

onMounted(async () => {
  categories.value = await getCategoryList()
  tags.value = await getTagList()
  if (route.params.id) {
    const article = await getArticleDetail(route.params.id)
    form.value = {
      id: article.id,
      title: article.title,
      summary: article.summary,
      content: article.content,
      coverImage: article.coverImage,
      categoryId: article.categoryId,
      tagIds: article.tagIds || [],
      isTop: article.isTop,
      status: article.status
    }
  }
})
</script>

<style scoped>
.cover-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  width: 320px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-uploader :deep(.el-upload:hover) {
  border-color: #5c4033;
}
.cover-preview {
  width: 320px;
  height: 180px;
  object-fit: cover;
  display: block;
}
.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #8c939d;
  font-size: 14px;
}
</style>
