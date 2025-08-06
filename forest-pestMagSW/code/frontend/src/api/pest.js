import request from '@/utils/request'

// 病虫害识别
export function identifyPest(data) {
  return request({
    url: '/pest/identify',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取识别历史
export function getIdentificationHistory(params) {
  return request({
    url: '/pest/identification/history',
    method: 'get',
    params
  })
}

// 获取病虫害详情
export function getPestDetail(id) {
  return request({
    url: `/pest/${id}`,
    method: 'get'
  })
}

// 删除识别记录
export function deleteIdentificationRecord(id) {
  return request({
    url: `/pest/identification/${id}`,
    method: 'delete'
  })
}

// 获取病虫害列表
export function getPestList(params) {
  return request({
    url: '/pest/list',
    method: 'get',
    params
  })
}
