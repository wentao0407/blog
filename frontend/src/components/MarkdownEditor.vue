<template>
  <div class="md-editor">
    <div class="toolbar">
      <button @click="insert('**', '**')">B</button>
      <button @click="insert('*', '*')">I</button>
      <button @click="insert('`', '`')">&lt;/&gt;</button>
      <button @click="insert('[', '](url)')">Link</button>
    </div>
    <textarea ref="textareaRef" :value="modelValue" @input="$emit('update:modelValue', $event.target.value)" placeholder="输入 Markdown 内容..."></textarea>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({ modelValue: String })
defineEmits(['update:modelValue'])

const textareaRef = ref(null)

const insert = (before, after = '') => {
  const el = textareaRef.value
  const start = el.selectionStart
  const end = el.selectionEnd
  const text = el.value
  const newText = text.substring(0, start) + before + text.substring(start, end) + after + text.substring(end)
  el.value = newText
  el.dispatchEvent(new Event('input'))
}
</script>

<style scoped>
.toolbar { padding: 8px; border-bottom: 1px solid #eee; display: flex; gap: 4px; }
.toolbar button { border: 1px solid #ddd; background: #fff; padding: 4px 8px; border-radius: 4px; cursor: pointer; }
textarea { width: 100%; height: 400px; border: none; padding: 12px; font-size: 14px; resize: none; outline: none; font-family: monospace; box-sizing: border-box; }
</style>
