import request from '@/utils/request'

/**
 * 获取防治任务列表
 * @param {string} planId 防治方案ID
 * @returns {Promise}
 */
export function getTreatmentTasks(planId) {
  return request({
    url: `/api/treatment-plan/${planId}/tasks`,
    method: 'get'
  })
}

/**
 * 创建防治任务
 * @param {string} planId 防治方案ID
 * @param {Object} taskData 任务数据
 * @returns {Promise}
 */
export function createTreatmentTask(planId, taskData) {
  return request({
    url: '/api/treatment-plan/task',
    method: 'post',
    data: {
      planId,
      task: taskData,
      userId: 'current-user-id' // 实际应用中应该从用户状态获取
    }
  })
}

/**
 * 更新防治任务
 * @param {string} taskId 任务ID
 * @param {Object} taskData 任务数据
 * @returns {Promise}
 */
export function updateTreatmentTask(taskId, taskData) {
  return request({
    url: `/api/treatment-plan/task/${taskId}`,
    method: 'put',
    data: taskData
  })
}

/**
 * 删除防治任务
 * @param {string} taskId 任务ID
 * @returns {Promise}
 */
export function deleteTreatmentTask(taskId) {
  return request({
    url: `/api/treatment-plan/task/${taskId}`,
    method: 'delete'
  })
}

/**
 * 分配防治任务
 * @param {string} taskId 任务ID
 * @param {string} assigneeId 分配对象ID
 * @returns {Promise}
 */
export function assignTreatmentTask(taskId, assigneeId) {
  return request({
    url: `/api/treatment-plan/task/${taskId}/assign`,
    method: 'post',
    data: {
      assigneeId,
      assignerId: 'current-user-id' // 实际应用中应该从用户状态获取
    }
  })
}

/**
 * 批量创建防治任务
 * @param {string} planId 防治方案ID
 * @param {Array} tasks 任务列表
 * @returns {Promise}
 */
export function batchCreateTreatmentTasks(planId, tasks) {
  return request({
    url: '/api/treatment-plan/task/batch',
    method: 'post',
    data: {
      planId,
      tasks,
      userId: 'current-user-id' // 实际应用中应该从用户状态获取
    }
  })
}

/**
 * 完成防治任务
 * @param {string} taskId 任务ID
 * @param {Object} completionData 完成数据
 * @returns {Promise}
 */
export function completeTreatmentTask(taskId, completionData) {
  return request({
    url: `/api/treatment-plan/task/${taskId}/complete`,
    method: 'post',
    data: {
      ...completionData,
      userId: 'current-user-id' // 实际应用中应该从用户状态获取
    }
  })
}
