<template>
  <div>
    <div class="header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openDialog()">新建分类</el-button>
    </div>
    <el-table :data="categories" border stripe>
      <el-table-column prop="name" label="分类名" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新建分类'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { getCategoryList, saveCategory, deleteCategory } from '../../api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const dialogVisible = ref(false)
const form = ref({ name: '', sort: 0 })

const loadCategories = async () => {
  categories.value = await getCategoryList()
}

const openDialog = (row) => {
  form.value = row ? { ...row } : { name: '', sort: 0 }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) return ElMessage.warning('请输入分类名')
  await saveCategory(form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadCategories()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该分类？', '提示')
  await deleteCategory(id)
  ElMessage.success('删除成功')
  loadCategories()
}

onMounted(loadCategories)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
