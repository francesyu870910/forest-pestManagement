import request from '@/utils/request'

// 获取森林资源列表
export function getForestResourceList(params) {
  return request({
    url: '/forest/resources',
    method: 'get',
    params
  })
}

// 创建森林资源
export function createForestResource(data) {
  return request({
    url: '/forest/resources',
    method: 'post',
    data
  })
}

// 更新森林资源
export function updateForestResource(id, data) {
  return request({
    url: `/forest/resources/${id}`,
    method: 'put',
    data
  })
}

// 删除森林资源
export function deleteForestResource(id) {
  return request({
    url: `/forest/resources/${id}`,
    method: 'delete'
  })
}

// 获取资源详情
export function getForestResourceDetail(id) {
  return request({
    url: `/forest/resources/${id}`,
    method: 'get'
  })
}

// 获取知识库列表
export function getKnowledgeList(params) {
  return request({
    url: '/forest/knowledge',
    method: 'get',
    params
  })
}

// 创建知识库条目
export function createKnowledge(data) {
  return request({
    url: '/forest/knowledge',
    method: 'post',
    data
  })
}

// 更新知识库条目
export function updateKnowledge(id, data) {
  return request({
    url: `/forest/knowledge/${id}`,
    method: 'put',
    data
  })
}

// 删除知识库条目
export function deleteKnowledge(id) {
  return request({
    url: `/forest/knowledge/${id}`,
    method: 'delete'
  })
}

// 搜索知识库
export function searchKnowledge(keyword) {
  return request({
    url: '/forest/knowledge/search',
    method: 'get',
    params: { keyword }
  })
}
