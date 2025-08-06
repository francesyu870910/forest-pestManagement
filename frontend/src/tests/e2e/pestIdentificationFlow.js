/**
 * 病虫害识别流程端到端测试
 */
export const pestIdentificationFlowTests = [
  {
    name: '病虫害识别完整流程',
    description: '测试从图片上传到识别结果查看的完整流程',
    steps: [
      {
        name: '用户登录',
        action: 'api_call',
        params: {
          method: 'login',
          data: {
            username: 'admin',
            password: 'admin123',
            rememberMe: false
          }
        },
        saveAs: 'loginResponse',
        validate: (result) => {
          return result && result.data && result.data.token
        }
      },
      {
        name: '上传图片进行识别',
        action: 'api_call',
        params: {
          method: 'identifyPest',
          data: {
            // 模拟FormData
            image: 'mock_image_data',
            symptoms: '叶片出现黄褐色斑点，边缘枯萎',
            location: '北京市昌平区森林公园'
          }
        },
        saveAs: 'identificationResult',
        validate: (result) => {
          return result && result.data && result.data.pestName
        }
      },
      {
        name: '查看识别历史',
        action: 'api_call',
        params: {
          method: 'getIdentificationHistory',
          data: {
            page: 1,
            size: 10
          }
        },
        saveAs: 'historyList',
        validate: (result) => {
          return result && result.data && Array.isArray(result.data.list)
        }
      },
      {
        name: '获取病虫害详情',
        action: 'api_call',
        params: {
          method: 'getPestDetail',
          data: '{{identificationResult.data.pestId}}'
        },
        validate: (result) => {
          return result && result.data
        }
      }
    ]
  },

  {
    name: '识别结果管理流程',
    description: '测试识别结果的查看和删除功能',
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
        name: '获取识别历史列表',
        action: 'api_call',
        params: {
          method: 'getIdentificationHistory',
          data: { page: 1, size: 5 }
        },
        saveAs: 'historyList',
        validate: (result) => {
          return result && result.data && result.data.list
        }
      },
      {
        name: '验证历史记录数据结构',
        action: 'validate_data',
        params: {
          data: '{{historyList.data.list}}',
          rules: [
            {
              field: '0.id',
              condition: 'exists',
              message: '识别记录ID不存在'
            },
            {
              field: '0.pestName',
              condition: 'exists',
              message: '病虫害名称不存在'
            },
            {
              field: '0.confidence',
              condition: 'exists',
              message: '置信度不存在'
            }
          ]
        }
      }
    ]
  }
]

export default pestIdentificationFlowTests
