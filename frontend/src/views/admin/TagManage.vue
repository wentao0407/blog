<template>
  <div>
    <div class="header">
      <h2>标签管理</h2>
      <el-button type="primary" @click="openDialog()">新建标签</el-button>
    </div>
    <el-table :data="tags" border stripe>
      <el-table-column prop="name" label="标签名" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑标签' : '新建标签'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标签名">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTagList, saveTag, deleteTag } from '../../api/tag'
import { ElMessage, ElMessageBox } from 'element-plus'

const tags = ref([])
const dialogVisible = ref(false)
const form = ref({ name: '' })

const loadTags = async () => {
  tags.value = await getTagList()
}

const openDialog = (row) => {
  form.value = row ? { ...row } : { name: '' }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) return ElMessage.warning('请输入标签名')
  await saveTag(form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadTags()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该标签？', '提示')
  await deleteTag(id)
  ElMessage.success('删除成功')
  loadTags()
}

onMounted(loadTags)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
