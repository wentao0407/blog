<template>
  <div class="login-page">
    <div class="login-box">
      <h2>{{ isRegister ? '注册' : '登录' }}</h2>
      <div class="form-item">
        <input v-model="form.username" placeholder="用户名" />
      </div>
      <div class="form-item">
        <input v-model="form.password" type="password" placeholder="密码" />
      </div>
      <div class="form-item" v-if="isRegister">
        <input v-model="form.nickname" placeholder="昵称" />
      </div>
      <button @click="handleSubmit" :disabled="loading">{{ loading ? '提交中...' : (isRegister ? '注册' : '登录') }}</button>
      <p class="toggle" @click="isRegister = !isRegister">
        {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../../api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const isRegister = ref(false)
const form = ref({ username: '', password: '', nickname: '' })
const loading = ref(false)

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请填写用户名和密码')
  }
  loading.value = true
  try {
    if (isRegister.value) {
      if (!form.value.nickname) { ElMessage.warning('请填写昵称'); return }
      await register(form.value)
      ElMessage.success('注册成功，请登录')
      form.value = { username: '', password: '', nickname: '' }
      isRegister.value = false
    } else {
      const data = await login(form.value)
      localStorage.setItem('token', data.token)
      localStorage.setItem('userId', data.id)
      localStorage.setItem('nickname', data.nickname)
      localStorage.setItem('role', data.role)
      ElMessage.success('登录成功')
      if (data.role === 1) {
        router.push('/admin')
      } else {
        router.push('/')
      }
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.login-box {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 360px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.login-box h2 { text-align: center; color: #5c4033; margin-bottom: 24px; font-family: serif; }
.form-item { margin-bottom: 16px; }
.form-item input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}
button {
  width: 100%;
  background: #5c4033;
  color: #fff;
  border: none;
  padding: 10px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.toggle { text-align: center; color: #8b7355; font-size: 14px; cursor: pointer; margin-top: 16px; }
</style>
