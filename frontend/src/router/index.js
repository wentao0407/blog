import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/blog/BlogLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/blog/HomeView.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('../views/blog/ArticleDetail.vue') },
      { path: 'category/:id', name: 'Category', component: () => import('../views/blog/CategoryView.vue') },
      { path: 'tag/:id', name: 'Tag', component: () => import('../views/blog/TagView.vue') },
      { path: 'archive', name: 'Archive', component: () => import('../views/blog/ArchiveView.vue') },
      { path: 'search', name: 'Search', component: () => import('../views/blog/SearchView.vue') },
      { path: 'about', name: 'About', component: () => import('../views/blog/AboutView.vue') },
      { path: 'login', name: 'Login', component: () => import('../views/blog/LoginView.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/DashboardView.vue') },
      { path: 'article', name: 'AdminArticle', component: () => import('../views/admin/ArticleManage.vue') },
      { path: 'article/edit/:id?', name: 'ArticleEdit', component: () => import('../views/admin/ArticleEdit.vue') },
      { path: 'category', name: 'AdminCategory', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'tag', name: 'AdminTag', component: () => import('../views/admin/TagManage.vue') },
      { path: 'comment', name: 'AdminComment', component: () => import('../views/admin/CommentManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(r => r.meta.requiresAuth)) {
    if (!localStorage.getItem('token')) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
