import request from '../utils/request'
export const getArticleList = (data) => request.post('/article/list', data)
export const getArticleDetail = (id) => request.post('/article/detail', { id })
export const getTopArticles = () => request.post('/article/top')
export const getArchiveList = () => request.post('/article/archive')
export const saveArticle = (data) => request.post('/admin/article/save', data)
export const deleteArticle = (id) => request.post('/admin/article/delete', { id })
export const toggleTopArticle = (id) => request.post('/admin/article/top', { id })
