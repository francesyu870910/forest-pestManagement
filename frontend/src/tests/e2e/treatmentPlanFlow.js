/**
 * 防治方案管理流程端到端测试
 */
export const treatmentPlanFlowTests = [
  {
    name: '防治方案完整生命周期',
    description: '测试防治方案从创建到执行完成的完整流程',
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
        name: '创建防治方案',
        action: 'api_call',
        params: {
          method: 'createTreatmentPlan',
          data: {
            planName: 'E2E测试防治方案_' + Date.now(),
            pestType: 'pine_caterpillar',
            targetArea: '测试区域',
            estimatedArea: 100,
            description: 'E2E测试方案描述',
            measures: [
              {
                type: 'chemical',
                description: '化学防治措施'
              },
              {
                type: 'biological',
                description: '生物防治措施'
              }
            ],
            startTime: '2024-01-01 08:00:00',
            endTime: '2024-01-31 18:00:00',
            responsible: 'E2E测试负责人'
          }
        },
        saveAs: 'createdPlan',
        validate: (result) => {
          return result && result.data && result.data.id
        }
      },
      {
        name: '获取方案详情',
        action: 'api_call',
        params: {
          method: 'getTreatmentPlanDetail',
          data: '{{createdPlan.data.id}}'
        },
        saveAs: 'planDetail',
        validate: (result, context) => {
          return result && result.data && result.data.planName === context.createdPlan.data.planName
        }
      },
      {
        name: '获取方案任务列表',
        action: 'api_call',
        params: {
          method: 'getTreatmentTasks',
          data: '{{createdPlan.data.id}}'
        },
        saveAs: 'taskList',
        validate: (result) => {
          return result && result.data && Array.isArray(result.data)
        }
      },
      {
        name: '更新任务状态',
        action: 'api_call',
        params: {
          method: 'updateTaskStatus',
          data: {
            taskId: '{{taskList.data.0.id}}',
            status: 'in_progress'
          }
        },
        validate: (result) => {
          return result && result.code === 200
        }
      },
      {
        name: '获取防治进度统计',
        action: 'api_call',
        params: {
          method: 'getTreatmentProgress',
          data: {
            planId: '{{createdPlan.data.id}}'
          }
        },
        validate: (result) => {
          return result && result.data
        }
      }
    ],
    cleanup: async (context) => {
      // 清理创建的测试方案
      if (context.createdPlan?.data?.id) {
        try {
          await api.deleteTreatmentPlan(context.createdPlan.data.id)
        } catch (error) {
          console.warn('清理防治方案失败:', error)
        }
      }
    }
  },

  {
    name: '方案生成和修改流程',
    description: '测试自动生成方案和手动修改的流程',
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
        name: '生成防治方案',
        action: 'api_call',
        params: {
          method: 'generateTreatmentPlan',
          data: {
            pestType: 'pine_caterpillar',
            targetArea: '自动生成测试区域',
            severity: 'medium'
          }
        },
        saveAs: 'generatedPlan',
        validate: (result) => {
          return result && result.data && result.data.planName
        }
      },
      {
        name: '修改生成的方案',
        action: 'api_call',
        params: {
          method: 'updateTreatmentPlan',
          data: {
            id: '{{generatedPlan.data.id}}',
            planName: '修改后的方案名称',
            description: '修改后的方案描述'
          }
        },
        validate: (result) => {
          return result && result.code === 200
        }
      },
      {
        name: '验证修改结果',
        action: 'api_call',
        params: {
          method: 'getTreatmentPlanDetail',
          data: '{{generatedPlan.data.id}}'
        },
        validate: (result) => {
          return result && result.data && result.data.planName === '修改后的方案名称'
        }
      }
    ],
    cleanup: async (context) => {
      if (context.generatedPlan?.data?.id) {
        try {
          await api.deleteTreatmentPlan(context.generatedPlan.data.id)
        } catch (error) {
          console.warn('清理生成的方案失败:', error)
        }
      }
    }
  }
]

export default treatmentPlanFlowTests
