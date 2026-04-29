import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    }
    ElMessage.error(message || '请求失败')
    if (code === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    return Promise.reject(new Error(message))
  },
  error => {
    ElMessage.error('网络错误')
    return Promise.reject(error)
  }
)

export default request
