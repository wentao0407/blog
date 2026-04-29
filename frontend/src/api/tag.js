import request from '../utils/request'
export const getTagList = () => request.post('/tag/list')
export const saveTag = (data) => request.post('/admin/tag/save', data)
export const deleteTag = (id) => request.post('/admin/tag/delete', id)
