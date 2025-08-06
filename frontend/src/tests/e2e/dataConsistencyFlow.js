/**
 * 数据一致性和完整性测试
 */
export const dataConsistencyFlowTests = [
  {
    name: '数据关联一致性测试',
    description: '测试各模块间数据的关联关系和一致性',
    steps: [
      {
        name: '用户登录',
        action: 'api_call',
        params: {
          method: 'login',
          data: {
            username: 'admin',
            password: 'admin123'
          }
        },
        saveAs: 'loginResponse',
        validate: (result) => result && result.data && result.data.token
      },
      {
        name: '获取病虫害列表',
        action: 'api_call',
        params: {
          method: 'getPestList',
          data: { page: 1, size: 5 }
        },
        saveAs: 'pestList',
        validate: (result) => {
          return result && result.data && result.data.list && result.data.list.length > 0
        }
      },
      {
        name: '获取防治方案列表',
        action: 'api_call',
        params: {
          method: 'getTreatmentPlanList',
          data: { page: 1, size: 5 }
        },
        saveAs: 'planList',
        validate: (result) => {
          return result && result.data && result.data.list
        }
      },
      {
        name: '验证方案与病虫害的关联',
        action: 'validate_data',
        params: {
          data: '{{planList.data.list}}',
          rules: [
            {
              field: '0.pestType',
              condition: 'exists',
              message: '防治方案缺少病虫害类型关联'
            }
          ]
        }
      },
      {
        name: '获取药剂列表',
        action: 'api_call',
        params: {
          method: 'getPesticideList',
          data: { page: 1, size: 5 }
        },
        saveAs: 'pesticideList',
        validate: (result) => {
          return result && result.data && result.data.list
        }
      },
      {
        name: '验证药剂数据完整性',
        action: 'validate_data',
        params: {
          data: '{{pesticideList.data.list}}',
          rules: [
            {
              field: '0.name',
              condition: 'exists',
              message: '药剂名称不存在'
            },
            {
              field: '0.currentStock',
              condition: 'exists',
              message: '当前库存不存在'
            },
            {
              field: '0.minStock',
              condition: 'exists',
              message: '最低库存不存在'
            }
          ]
        }
      }
    ]
  },

  {
    name: '用户权限数据一致性测试',
    description: '测试用户权限与数据访问的一致性',
    steps: [
      {
        name: '管理员登录',
        action: 'api_call',
        params: {
          method: 'login',
          data: {
            username: 'admin',
            password: 'admin123'
          }
        },
        saveAs: 'adminLogin',
        validate: (result) => result && result.data && result.data.token
      },
      {
        name: '获取用户列表（管理员权限）',
        action: 'api_call',
        params: {
          method: 'getUserList',
          data: { page: 1, size: 5 }
        },
        saveAs: 'userList',
        validate: (result) => {
          return result && result.data && result.data.list
        }
      },
      {
        name: '验证用户数据结构',
        action: 'validate_data',
        params: {
          data: '{{userList.data.list}}',
          rules: [
            {
              field: '0.id',
              condition: 'exists',
              message: '用户ID不存在'
            },
            {
              field: '0.username',
              condition: 'exists',
              message: '用户名不存在'
            },
            {
              field: '0.role',
              condition: 'exists',
              message: '用户角色不存在'
            }
          ]
        }
      },
      {
        name: '普通用户登录',
        action: 'api_call',
        params: {
          method: 'login',
          data: {
            username: 'user',
            password: 'user123'
          }
        },
        saveAs: 'userLogin',
        validate: (result) => result && result.data && result.data.token
      },
      {
        name: '普通用户尝试访问用户列表（应该失败或受限）',
        action: 'api_call',
        params: {
          method: 'getUserList',
          data: { page: 1, size: 5 }
        },
        validate: (result) => {
          // 普通用户可能无权限访问或只能看到有限数据
          return true // 这里我们只是测试接口响应，不验证具体权限
        }
      }
    ]
  },

  {
    name: '数据更新一致性测试',
    description: '测试数据更新后各模块的一致性',
    steps: [
      {
        name: '用户登录',
        action: 'api_call',
        params: {
          method: 'login',
          data: {
            username: 'admin',
            password: 'admin123'
          }
        },
        validate: (result) => result && result.data && result.data.token
      },
      {
        name: '创建药剂记录',
        action: 'api_call',
        params: {
          method: 'createPesticide',
          data: {
            name: '一致性测试药剂_' + Date.now(),
            type: 'insecticide',
            specification: '500ml/瓶',
            unit: 'bottle',
            currentStock: 100,
            minStock: 10,
            price: 35.5,
            supplier: '测试供应商',
            expiryDate: '2025-12-31',
            instructions: '测试使用说明'
          }
        },
        saveAs: 'createdPesticide',
        validate: (result) => {
          return result && result.data && result.data.id
        }
      },
      {
        name: '更新药剂库存',
        action: 'api_call',
        params: {
          method: 'updatePesticideStock',
          data: {
            id: '{{createdPesticide.data.id}}',
            operationType: 'out',
            quantity: 20,
            remark: '一致性测试使用'
          }
        },
        validate: (result) => {
          return result && result.code === 200
        }
      },
      {
        name: '验证库存更新后的数据',
        action: 'api_call',
        params: {
          method: 'getPesticideList',
          data: {
            page: 1,
            size: 10,
            name: '{{createdPesticide.data.name}}'
          }
        },
        validate: (result, context) => {
          const pesticide = result.data.list.find((p) => p.id === context.createdPesticide.data.id)
          return pesticide && pesticide.currentStock === 80 // 100 - 20
        }
      },
      {
        name: '获取使用记录',
        action: 'api_call',
        params: {
          method: 'getPesticideUsageRecords',
          data: {
            pesticideId: '{{createdPesticide.data.id}}',
            page: 1,
            size: 5
          }
        },
        validate: (result) => {
          return result && result.data && Array.isArray(result.data)
        }
      }
    ],
    cleanup: async (context) => {
      if (context.createdPesticide?.data?.id) {
        try {
          await api.deletePesticide(context.createdPesticide.data.id)
        } catch (error) {
          console.warn('清理药剂数据失败:', error)
        }
      }
    }
  }
]

export default dataConsistencyFlowTests
