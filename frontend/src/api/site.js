import request from '../utils/request'
export const getSiteStats = () => request.post('/site/stats')
