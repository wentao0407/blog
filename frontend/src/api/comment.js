import request from '../utils/request'
export const getCommentList = (articleId) => request.post('/comment/list', { articleId })
export const addComment = (data) => request.post('/comment/add', data)
export const getAdminCommentList = (data) => request.post('/admin/comment/list', data)
export const deleteComment = (id) => request.post('/admin/comment/delete', { id })
