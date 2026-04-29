import request from '../utils/request'
export const getCategoryList = () => request.post('/category/list')
export const saveCategory = (data) => request.post('/admin/category/save', data)
export const deleteCategory = (id) => request.post('/admin/category/delete', id)
