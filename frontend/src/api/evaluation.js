import request from '@/utils/request'

// 获取效果评估列表
export function getEvaluationList(params) {
  return request({
    url: '/evaluation/list',
    method: 'get',
    params
  })
}

// 创建效果评估
export function createEvaluation(data) {
  return request({
    url: '/evaluation',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新效果评估
export function updateEvaluation(id, data) {
  return request({
    url: `/evaluation/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除效果评估
export function deleteEvaluation(id) {
  return request({
    url: `/evaluation/${id}`,
    method: 'delete'
  })
}

// 获取评估详情
export function getEvaluationDetail(id) {
  return request({
    url: `/evaluation/${id}`,
    method: 'get'
  })
}

// 生成评估报告
export function generateEvaluationReport(id) {
  return request({
    url: `/evaluation/${id}/report`,
    method: 'post'
  })
}

// 获取效果对比数据
export function getEffectComparisonData(type) {
  return request({
    url: '/evaluation/comparison',
    method: 'get',
    params: { type }
  })
}

// 获取评估统计数据
export function getEvaluationStats(params) {
  return request({
    url: '/evaluation/stats',
    method: 'get',
    params
  })
}
