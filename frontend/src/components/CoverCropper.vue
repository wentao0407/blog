<template>
  <el-dialog v-model="visible" title="裁剪封面图" width="680px" :close-on-click-modal="false" @close="handleClose">
    <div class="cropper-container">
      <img ref="imgRef" :src="imgSrc" alt="裁剪图片" />
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确认裁剪</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import Cropper from 'cropperjs'

const props = defineProps({
  modelValue: Boolean,
  file: File
})
const emit = defineEmits(['update:modelValue', 'crop'])

const visible = ref(false)
const imgRef = ref(null)
const imgSrc = ref('')
let cropper = null

watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val && props.file) {
    imgSrc.value = URL.createObjectURL(props.file)
    await nextTick()
    initCropper()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val && cropper) {
    cropper.$destroy()
    cropper = null
  }
})

const initCropper = () => {
  if (cropper) cropper.$destroy()
  cropper = new Cropper(imgRef.value, {
    aspectRatio: 16 / 9,
    viewMode: 1,
    dragMode: 'move',
    autoCropArea: 0.9,
    responsive: true,
    background: false
  })
}

const handleClose = () => {
  if (cropper) {
    cropper.$destroy()
    cropper = null
  }
  if (imgSrc.value) {
    URL.revokeObjectURL(imgSrc.value)
    imgSrc.value = ''
  }
}

const handleConfirm = async () => {
  if (!cropper) return
  try {
    // cropperjs v2 使用 $toCanvas 方法
    const canvas = await cropper.$toCanvas({
      width: 800,
      height: 450
    })
    canvas.toBlob((blob) => {
      const croppedFile = new File([blob], props.file.name, { type: 'image/jpeg' })
      emit('crop', croppedFile)
      visible.value = false
    }, 'image/jpeg', 0.9)
  } catch (e) {
    console.error('裁剪失败:', e)
  }
}
</script>

<style scoped>
.cropper-container {
  width: 100%;
  height: 400px;
  background: #f5f5f5;
}
.cropper-container img {
  max-width: 100%;
  max-height: 100%;
}
</style>
