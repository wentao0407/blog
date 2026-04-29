import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { DataBoard, Document, Folder, PriceTag, ChatDotRound } from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)

// 按需注册图标组件
const icons = { DataBoard, Document, Folder, PriceTag, ChatDotRound }
for (const [key, component] of Object.entries(icons)) {
  app.component(key, component)
}
app.mount('#app')
