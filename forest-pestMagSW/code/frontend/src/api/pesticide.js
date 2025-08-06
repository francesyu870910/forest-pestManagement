import request from '@/utils/request'

// 获取药剂列表
export function getPesticideList(params) {
  return request({
    url: '/pesticide/list',
    method: 'get',
    params
  })
}

// 创建药剂
export function createPesticide(data) {
  return request({
    url: '/pesticide',
    method: 'post',
    data
  })
}

// 更新药剂
export function updatePesticide(id, data) {
  return request({
    url: `/pesticide/${id}`,
    method: 'put',
    data
  })
}

// 删除药剂
export function deletePesticide(id) {
  return request({
    url: `/pesticide/${id}`,
    method: 'delete'
  })
}

// 更新库存
export function updatePesticideStock(data) {
  return request({
    url: '/pesticide/stock/update',
    method: 'post',
    data
  })
}

// 获取使用记录
export function getPesticideUsageRecords(pesticideId, params) {
  return request({
    url: `/pesticide/${pesticideId}/usage`,
    method: 'get',
    params
  })
}

// 获取库存预警
export function getStockAlerts() {
  return request({
    url: '/pesticide/alerts',
    method: 'get'
  })
}

// 获取使用统计
export function getPesticideUsageStats(params) {
  return request({
    url: '/pesticide/usage/stats',
    method: 'get',
    params
  })
}
