import request from '@/utils/request'

// 获取预测预警列表
export function getPredictionList(params) {
  return request({
    url: '/prediction/list',
    method: 'get',
    params
  })
}

// 获取预警信息
export function getWarningList(params) {
  return request({
    url: '/prediction/warnings',
    method: 'get',
    params
  })
}

// 创建预警
export function createWarning(data) {
  return request({
    url: '/prediction/warnings',
    method: 'post',
    data
  })
}

// 更新预警状态
export function updateWarningStatus(id, status) {
  return request({
    url: `/prediction/warnings/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取预测结果
export function getPredictionResult(params) {
  return request({
    url: '/prediction/predict',
    method: 'post',
    data: params
  })
}

// 获取预警地图数据
export function getWarningMapData(level) {
  return request({
    url: '/prediction/warnings/map',
    method: 'get',
    params: { level }
  })
}

// 获取预警统计
export function getWarningStats() {
  return request({
    url: '/prediction/warnings/stats',
    method: 'get'
  })
}

// 获取预测准确性统计
export function getPredictionAccuracy(params) {
  return request({
    url: '/prediction/accuracy',
    method: 'get',
    params
  })
}
