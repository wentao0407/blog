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
        <el-input v-model="form.coverImage" placeholder="封面图URL" />
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
import { getArticleDetail, saveArticle } from '../../api/article'
import { getCategoryList } from '../../api/category'
import { getTagList } from '../../api/tag'
import { ElMessage } from 'element-plus'
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
