import request from '@/utils/request'

// 获取防治方案列表
export function getTreatmentPlanList(params) {
  return request({
    url: '/treatment/plans',
    method: 'get',
    params
  })
}

// 创建防治方案
export function createTreatmentPlan(data) {
  return request({
    url: '/treatment/plans',
    method: 'post',
    data
  })
}

// 更新防治方案
export function updateTreatmentPlan(id, data) {
  return request({
    url: `/treatment/plans/${id}`,
    method: 'put',
    data
  })
}

// 删除防治方案
export function deleteTreatmentPlan(id) {
  return request({
    url: `/treatment/plans/${id}`,
    method: 'delete'
  })
}

// 获取方案详情
export function getTreatmentPlanDetail(id) {
  return request({
    url: `/treatment/plans/${id}`,
    method: 'get'
  })
}

// 生成防治方案
export function generateTreatmentPlan(data) {
  return request({
    url: '/treatment/generate',
    method: 'post',
    data
  })
}

// 获取任务列表
export function getTreatmentTasks(planId) {
  return request({
    url: `/treatment/plans/${planId}/tasks`,
    method: 'get'
  })
}

// 更新任务状态
export function updateTaskStatus(taskId, status) {
  return request({
    url: `/treatment/tasks/${taskId}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取防治进度统计
export function getTreatmentProgress(params) {
  return request({
    url: '/treatment/progress',
    method: 'get',
    params
  })
}
