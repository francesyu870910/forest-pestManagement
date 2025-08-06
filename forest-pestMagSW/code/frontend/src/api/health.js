import request from '@/utils/request'

/**
 * 健康检查API
 */
export function getHealthStatus() {
  return request({
    url: '/health',
    method: 'get'
  })
}
