<template>
  <div class="pest-identification">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>病虫害识别</span>
          <el-button type="primary" @click="showUploadDialog = true">
            <el-icon><Plus /></el-icon>
            新增识别
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="识别时间">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <el-form-item label="识别状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="识别成功" value="success" />
              <el-option label="识别失败" value="failed" />
              <el-option label="处理中" value="processing" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 识别记录列表 -->
      <el-table :data="identificationList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="pestName" label="病虫害名称" />

        <el-table-column prop="location" label="位置信息" width="180" show-overflow-tooltip />

        <el-table-column prop="confidence" label="置信度" width="100">
          <template #default="{ row }">
            <el-tag :type="getConfidenceType(row.confidence)">
              {{ (row.confidence * 100).toFixed(1) }}%
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="识别时间" width="180" />

        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row)"> 详情 </el-button>
              <el-button type="success" size="small" @click="createTreatmentPlan(row)">
                方案
              </el-button>
              <el-button type="danger" size="small" @click="deleteRecord(row)"> 删除 </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 上传识别对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="病虫害识别"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-width="100px">
        <!-- 识别方式选择 -->
        <el-form-item label="识别方式" prop="identifyType">
          <el-radio-group v-model="uploadForm.identifyType" @change="handleIdentifyTypeChange">
            <el-radio value="image">图片识别</el-radio>
            <el-radio value="symptom">症状识别</el-radio>
            <el-radio value="comprehensive">综合识别</el-radio>
            <el-radio value="batch">批量识别</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 图片上传区域 -->
        <el-form-item 
          v-if="uploadForm.identifyType === 'image' || uploadForm.identifyType === 'comprehensive'"
          label="上传图片" 
          prop="imageFile" 
          :required="uploadForm.identifyType === 'image'"
        >
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept="image/*"
            drag
          >
            <div v-if="!uploadForm.imagePreview" class="upload-area">
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">
                <p>将图片拖到此处，或<em>点击上传</em></p>
                <p class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 10MB</p>
              </div>
            </div>
            <div v-else class="image-preview">
              <el-image
                :src="uploadForm.imagePreview"
                fit="contain"
                style="width: 100%; height: 200px"
              />
              <div class="image-actions">
                <el-button size="small" @click="removeImage">重新选择</el-button>
              </div>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 批量图片上传区域 -->
        <el-form-item 
          v-if="uploadForm.identifyType === 'batch'"
          label="批量上传" 
          prop="batchFiles"
          required
        >
          <el-upload
            ref="batchUploadRef"
            :auto-upload="false"
            :show-file-list="true"
            :on-change="handleBatchFileChange"
            :on-remove="handleBatchFileRemove"
            accept="image/*"
            multiple
            drag
          >
            <div class="upload-area">
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">
                <p>将多张图片拖到此处，或<em>点击批量上传</em></p>
                <p class="upload-tip">支持 JPG、PNG 格式，单个文件不超过 10MB，最多20张</p>
              </div>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 症状描述区域 -->
        <el-form-item 
          v-if="uploadForm.identifyType === 'symptom' || uploadForm.identifyType === 'comprehensive'"
          label="症状描述"
          :prop="uploadForm.identifyType === 'symptom' ? 'symptoms' : ''"
          :required="uploadForm.identifyType === 'symptom'"
        >
          <el-input
            v-model="uploadForm.symptoms"
            type="textarea"
            :rows="4"
            :placeholder="uploadForm.identifyType === 'symptom' ? '请详细描述观察到的症状特征' : '请描述观察到的症状（可选）'"
          />
          <div v-if="uploadForm.identifyType === 'symptom'" class="symptom-guide">
            <el-alert
              title="症状描述指南"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p>请从以下方面描述症状：</p>
                <ul>
                  <li>受害部位：叶片、枝干、根部、果实等</li>
                  <li>症状特征：颜色变化、形状、大小、质地等</li>
                  <li>发生时间：什么时候开始出现症状</li>
                  <li>扩散情况：症状是否在扩散，扩散速度</li>
                  <li>环境条件：温度、湿度、光照等环境因素</li>
                </ul>
              </template>
            </el-alert>
          </div>
        </el-form-item>

        <!-- 环境信息区域 -->
        <el-form-item 
          v-if="uploadForm.identifyType === 'comprehensive'"
          label="环境信息"
        >
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="温度(°C)">
                <el-input-number v-model="uploadForm.temperature" :min="-50" :max="60" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="湿度(%)">
                <el-input-number v-model="uploadForm.humidity" :min="0" :max="100" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="光照强度">
                <el-select v-model="uploadForm.lightIntensity" placeholder="选择光照">
                  <el-option label="强光" value="strong" />
                  <el-option label="中等" value="medium" />
                  <el-option label="弱光" value="weak" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 植物信息区域 -->
        <el-form-item 
          v-if="uploadForm.identifyType === 'comprehensive'"
          label="植物信息"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="植物种类">
                <el-select v-model="uploadForm.plantType" placeholder="选择植物种类">
                  <el-option label="松树" value="pine" />
                  <el-option label="杨树" value="poplar" />
                  <el-option label="柳树" value="willow" />
                  <el-option label="桃树" value="peach" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="植物年龄">
                <el-select v-model="uploadForm.plantAge" placeholder="选择植物年龄">
                  <el-option label="幼苗期" value="seedling" />
                  <el-option label="幼树期" value="young" />
                  <el-option label="成年期" value="mature" />
                  <el-option label="老年期" value="old" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="位置信息">
          <el-input v-model="uploadForm.location" placeholder="请输入发现位置（可选）" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploadLoading">
          开始识别
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="识别详情" width="800px">
      <div v-if="currentRecord" class="detail-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-image">
              <el-image 
                :src="currentRecord.imageUrl" 
                fit="contain"
                :lazy="true"
                @error="handleImageError"
              >
                <template #placeholder>
                  <div class="image-slot">
                    <el-icon><Loading /></el-icon>
                    <span>图片加载中...</span>
                  </div>
                </template>
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                    <span>图片加载失败</span>
                    <el-button 
                      size="small" 
                      type="primary" 
                      @click="retryLoadImage(currentRecord)"
                    >
                      重试
                    </el-button>
                  </div>
                </template>
              </el-image>
            </div>
          </el-col>

          <el-col :span="12">
            <div class="detail-info">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="病虫害名称">
                  {{ currentRecord.pestName }}
                </el-descriptions-item>
                <el-descriptions-item label="置信度">
                  <el-tag :type="getConfidenceType(currentRecord.confidence)">
                    {{ (currentRecord.confidence * 100).toFixed(1) }}%
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="症状描述">
                  {{ currentRecord.symptoms || '无' }}
                </el-descriptions-item>
                <el-descriptions-item label="发现位置">
                  {{ currentRecord.location || '无' }}
                </el-descriptions-item>
                <el-descriptions-item label="识别时间">
                  {{ currentRecord.createTime }}
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="getStatusType(currentRecord.status)">
                    {{ getStatusText(currentRecord.status) }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>

              <div v-if="currentRecord.aiAnalysis" class="ai-analysis">
                <h4>AI分析结果</h4>
                <p>{{ currentRecord.aiAnalysis }}</p>
              </div>

              <div v-if="currentRecord.suggestions" class="suggestions">
                <h4>处理建议</h4>
                <ul>
                  <li v-for="suggestion in currentRecord.suggestions" :key="suggestion">
                    {{ suggestion }}
                  </li>
                </ul>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, UploadFilled, Picture, Loading } from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const uploadLoading = ref(false)
const showUploadDialog = ref(false)
const showDetailDialog = ref(false)
const currentRecord = ref(null)

const searchForm = reactive({
  dateRange: [],
  status: ''
})

const uploadForm = reactive({
  identifyType: 'image', // 识别方式：image, symptom, comprehensive, batch
  imageFile: null,
  imagePreview: '',
  batchFiles: [], // 批量上传的文件列表
  symptoms: '',
  location: '',
  // 综合识别的环境信息
  temperature: null,
  humidity: null,
  lightIntensity: '',
  plantType: '',
  plantAge: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const identificationList = ref([])

// 表单引用
const uploadFormRef = ref()
const uploadRef = ref()
const batchUploadRef = ref()

// 表单验证规则
const uploadRules = {
  identifyType: [
    { required: true, message: '请选择识别方式', trigger: 'change' }
  ],
  imageFile: [
    { required: true, message: '请上传图片', trigger: 'change' }
  ],
  batchFiles: [
    { required: true, message: '请上传图片', trigger: 'change' }
  ],
  symptoms: [
    { required: true, message: '请描述症状', trigger: 'blur' }
  ]
}

// 方法
const getImageUrl = (filename) => {
  // 构建图片URL，支持多种路径尝试
  const backendUrl = 'http://localhost:8080'
  
  // 尝试不同的路径，因为可能被Spring Security拦截
  const possiblePaths = [
    `${backendUrl}/uploads/images/${filename}`,        // 直接路径（推荐）
    `${backendUrl}/api/uploads/images/${filename}`,    // API路径
    `${backendUrl}/public/images/${filename}`,         // 公共路径
    `${backendUrl}/static/images/${filename}`          // 静态资源路径
  ]
  
  // 默认使用第一个路径
  const imageUrl = possiblePaths[0]
  console.log('图片URL:', imageUrl)
  console.log('可选路径:', possiblePaths)
  
  return imageUrl
}



// 图片加载错误处理
const handleImageError = (event) => {
  console.error('图片加载失败:', {
    src: event.target.src,
    error: event,
    status: event.target.complete,
    naturalWidth: event.target.naturalWidth,
    naturalHeight: event.target.naturalHeight
  })
  
  ElMessage.error(`图片加载失败: ${event.target.src}`)
}

// 重试加载图片
const retryLoadImage = (record) => {
  console.log('重试加载图片:', record)
  
  const filename = record.imageUrl.split('/').pop()
  const backendUrl = 'http://localhost:8080'
  
  // 尝试不同的路径
  const possiblePaths = [
    `${backendUrl}/uploads/images/${filename}`,
    `${backendUrl}/api/uploads/images/${filename}`,
    `${backendUrl}/public/images/${filename}`,
    `${backendUrl}/static/images/${filename}`
  ]
  
  let currentIndex = 0
  
  const tryNextPath = () => {
    if (currentIndex >= possiblePaths.length) {
      ElMessage.error('所有图片路径都尝试失败，请检查后端配置和图片文件')
      return
    }
    
    const img = new Image()
    const currentPath = possiblePaths[currentIndex]
    
    img.onload = () => {
      console.log('图片加载成功:', currentPath)
      record.imageUrl = currentPath
      currentRecord.value = { ...record }
      ElMessage.success('图片重新加载成功')
    }
    
    img.onerror = () => {
      console.log('路径失败:', currentPath)
      currentIndex++
      tryNextPath()
    }
    
    console.log('尝试路径:', currentPath)
    img.src = currentPath + '?t=' + Date.now()
  }
  
  tryNextPath()
}

const getConfidenceType = (confidence) => {
  if (confidence >= 0.8) return 'success'
  if (confidence >= 0.6) return 'warning'
  return 'danger'
}

const getStatusType = (status) => {
  const typeMap = {
    success: 'success',
    failed: 'danger',
    processing: 'warning'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    success: '识别成功',
    failed: '识别失败',
    processing: '处理中'
  }
  return textMap[status] || '未知'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadIdentificationList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  handleSearch()
}

const handleFileChange = (file) => {
  const isImage = file.raw.type.startsWith('image/')
  const isLt10M = file.raw.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }

  uploadForm.imageFile = file.raw

  // 生成预览图
  const reader = new FileReader()
  reader.onload = (e) => {
    uploadForm.imagePreview = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const removeImage = () => {
  uploadForm.imageFile = null
  uploadForm.imagePreview = ''
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 识别方式变化处理
const handleIdentifyTypeChange = (type) => {
  // 清空相关数据
  uploadForm.imageFile = null
  uploadForm.imagePreview = ''
  uploadForm.batchFiles = []
  uploadForm.symptoms = ''
  uploadForm.temperature = null
  uploadForm.humidity = null
  uploadForm.lightIntensity = ''
  uploadForm.plantType = ''
  uploadForm.plantAge = ''
  
  // 清空文件上传组件
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
  if (batchUploadRef.value) {
    batchUploadRef.value.clearFiles()
  }
}

// 批量文件变化处理
const handleBatchFileChange = (file, fileList) => {
  if (fileList.length > 20) {
    ElMessage.warning('最多只能上传20张图片')
    return false
  }
  
  // 验证文件类型和大小
  const isImage = file.raw.type.startsWith('image/')
  const isLt10M = file.raw.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  
  uploadForm.batchFiles = fileList
  return true
}

// 批量文件移除处理
const handleBatchFileRemove = (file, fileList) => {
  uploadForm.batchFiles = fileList
}

const handleUpload = async () => {
  if (!uploadFormRef.value) return

  await uploadFormRef.value.validate(async (valid) => {
    if (valid) {
      uploadLoading.value = true
      try {
        let result = null
        
        switch (uploadForm.identifyType) {
          case 'image':
            result = await handleImageIdentify()
            break
          case 'symptom':
            result = await handleSymptomIdentify()
            break
          case 'comprehensive':
            result = await handleComprehensiveIdentify()
            break
          case 'batch':
            result = await handleBatchIdentify()
            break
        }

        if (result) {
          ElMessage.success(`${getIdentifyTypeText(uploadForm.identifyType)}请求已提交，正在处理中...`)
          showUploadDialog.value = false
          resetUploadForm()
          loadIdentificationList()
        }
      } catch (error) {
        console.error('识别失败:', error)
        ElMessage.error('识别失败，请重试')
      } finally {
        uploadLoading.value = false
      }
    }
  })
}

// 图片识别
const handleImageIdentify = async () => {
  const formData = new FormData()
  formData.append('image', uploadForm.imageFile)
  formData.append('location', uploadForm.location)
  formData.append('identifyType', 'image')
  
  // 这里应该调用API
  // return await identifyByImage(formData)
  
  // 模拟API调用
  return new Promise(resolve => {
    setTimeout(() => resolve({ success: true }), 1000)
  })
}

// 症状识别
const handleSymptomIdentify = async () => {
  const data = {
    symptoms: uploadForm.symptoms,
    location: uploadForm.location,
    identifyType: 'symptom'
  }
  
  // 这里应该调用API
  // return await identifyBySymptom(data)
  
  // 模拟API调用
  return new Promise(resolve => {
    setTimeout(() => resolve({ success: true }), 1000)
  })
}

// 综合识别
const handleComprehensiveIdentify = async () => {
  const formData = new FormData()
  if (uploadForm.imageFile) {
    formData.append('image', uploadForm.imageFile)
  }
  formData.append('symptoms', uploadForm.symptoms)
  formData.append('location', uploadForm.location)
  formData.append('temperature', uploadForm.temperature || '')
  formData.append('humidity', uploadForm.humidity || '')
  formData.append('lightIntensity', uploadForm.lightIntensity)
  formData.append('plantType', uploadForm.plantType)
  formData.append('plantAge', uploadForm.plantAge)
  formData.append('identifyType', 'comprehensive')
  
  // 这里应该调用API
  // return await identifyComprehensive(formData)
  
  // 模拟API调用
  return new Promise(resolve => {
    setTimeout(() => resolve({ success: true }), 2000)
  })
}

// 批量识别
const handleBatchIdentify = async () => {
  const formData = new FormData()
  uploadForm.batchFiles.forEach((file, index) => {
    formData.append(`images`, file.raw)
  })
  formData.append('location', uploadForm.location)
  formData.append('identifyType', 'batch')
  
  // 这里应该调用API
  // return await identifyBatch(formData)
  
  // 模拟API调用
  return new Promise(resolve => {
    setTimeout(() => resolve({ success: true }), 3000)
  })
}

// 获取识别方式文本
const getIdentifyTypeText = (type) => {
  const textMap = {
    image: '图片识别',
    symptom: '症状识别',
    comprehensive: '综合识别',
    batch: '批量识别'
  }
  return textMap[type] || '识别'
}

const resetUploadForm = () => {
  uploadForm.identifyType = 'image'
  uploadForm.imageFile = null
  uploadForm.imagePreview = ''
  uploadForm.batchFiles = []
  uploadForm.symptoms = ''
  uploadForm.location = ''
  uploadForm.temperature = null
  uploadForm.humidity = null
  uploadForm.lightIntensity = ''
  uploadForm.plantType = ''
  uploadForm.plantAge = ''
  
  // 清空文件上传组件
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
  if (batchUploadRef.value) {
    batchUploadRef.value.clearFiles()
  }
  if (uploadFormRef.value) {
    uploadFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentRecord.value = row
  showDetailDialog.value = true
}

const createTreatmentPlan = (row) => {
  router.push({
    path: '/pest/treatment-plan',
    query: { pestId: row.id }
  })
}

const deleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条识别记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除记录
    // await deleteIdentificationRecord(row.id)

    ElMessage.success('删除成功')
    loadIdentificationList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadIdentificationList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadIdentificationList()
}

const loadIdentificationList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取识别记录列表
    // const response = await getIdentificationList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 完整的模拟数据 - 使用backend/uploads/images/目录下的图片
    const allMockData = [
      {
        id: 1,
        imageUrl: getImageUrl('songmaochong.jpg'),
        pestName: '松毛虫',
        confidence: 0.95,
        symptoms: '叶片出现黄褐色斑点，边缘枯萎',
        location: '北京市昌平区森林公园',
        status: 'success',
        createTime: '2024-01-15 10:30:00',
        aiAnalysis: '根据图像分析，该病虫害为松毛虫，主要危害松树叶片，建议及时采取防治措施。',
        suggestions: ['使用生物农药防治', '加强林间管理', '定期监测虫害发展']
      },
      {
        id: 2,
        imageUrl: getImageUrl('yangshu_kuiyangbing.jpg'),
        pestName: '杨树溃疡病',
        confidence: 0.87,
        symptoms: '树干出现溃疡斑，流出褐色液体',
        location: '河北省承德市',
        status: 'success',
        createTime: '2024-01-14 15:20:00',
        aiAnalysis: '识别为杨树溃疡病，是一种常见的真菌性病害。',
        suggestions: ['清除病枝', '涂抹杀菌剂', '改善通风条件']
      },
      {
        id: 3,
        imageUrl: getImageUrl('meiguo_baie.jpg'),
        pestName: '美国白蛾',
        confidence: 0.92,
        symptoms: '叶片被啃食，出现网状孔洞',
        location: '天津市蓟州区',
        status: 'success',
        createTime: '2024-01-13 09:15:00',
        aiAnalysis: '识别为美国白蛾，是一种外来入侵害虫，危害性较大。',
        suggestions: ['物理防治', '生物防治', '化学防治相结合']
      },
      {
        id: 4,
        imageUrl: getImageUrl('taoyu.jpg'),
        pestName: '桃蚜',
        confidence: 0.78,
        symptoms: '叶片卷曲，有蚜虫聚集',
        location: '山东省济南市',
        status: 'success',
        createTime: '2024-01-12 14:45:00',
        aiAnalysis: '识别为桃蚜，常见的刺吸式害虫。',
        suggestions: ['喷洒杀虫剂', '引入天敌', '加强通风']
      },
      {
        id: 5,
        imageUrl: getImageUrl('hongzhizhu.jpg'),
        pestName: '红蜘蛛',
        confidence: 0.85,
        symptoms: '叶片出现黄色斑点，有细小蛛网',
        location: '江苏省南京市',
        status: 'success',
        createTime: '2024-01-11 16:20:00',
        aiAnalysis: '识别为红蜘蛛，高温干燥环境下易发生。',
        suggestions: ['增加湿度', '喷洒杀螨剂', '清除杂草']
      },
      {
        id: 6,
        imageUrl: getImageUrl('jiekechong.jpg'),
        pestName: '蚧壳虫',
        confidence: 0.89,
        symptoms: '枝干上有白色或褐色小点状虫体',
        location: '浙江省杭州市',
        status: 'success',
        createTime: '2024-01-10 11:30:00',
        aiAnalysis: '识别为蚧壳虫，需要及时防治避免扩散。',
        suggestions: ['刮除虫体', '喷洒杀虫剂', '加强修剪']
      },
      {
        id: 7,
        imageUrl: getImageUrl('yebanbing.jpg'),
        pestName: '叶斑病',
        confidence: 0.91,
        symptoms: '叶片上有圆形或不规则褐色斑点',
        location: '安徽省合肥市',
        status: 'success',
        createTime: '2024-01-09 13:15:00',
        aiAnalysis: '识别为叶斑病，真菌性病害，需要及时处理。',
        suggestions: ['清除病叶', '喷洒杀菌剂', '改善通风条件']
      },
      {
        id: 8,
        imageUrl: getImageUrl('tianniu.jpg'),
        pestName: '天牛',
        confidence: 0.88,
        symptoms: '树干有圆形蛀孔，有木屑排出',
        location: '湖北省武汉市',
        status: 'processing',
        createTime: '2024-01-08 10:45:00',
        aiAnalysis: '疑似天牛危害，正在进一步分析确认。',
        suggestions: ['钢丝钩杀', '药剂熏蒸', '树干涂白']
      },
      {
        id: 9,
        imageUrl: getImageUrl('fenshi.jpg'),
        pestName: '粉虱',
        confidence: 0.76,
        symptoms: '叶片背面有白色小虫，叶片发黄',
        location: '广东省广州市',
        status: 'success',
        createTime: '2024-01-07 15:30:00',
        aiAnalysis: '识别为粉虱，刺吸式害虫，会传播病毒。',
        suggestions: ['黄色粘虫板', '生物防治', '化学防治']
      },
      {
        id: 10,
        imageUrl: getImageUrl('tangjubing.jpg'),
        pestName: '炭疽病',
        confidence: 0.93,
        symptoms: '叶片和果实上有黑色凹陷斑点',
        location: '四川省成都市',
        status: 'failed',
        createTime: '2024-01-06 12:00:00',
        aiAnalysis: '图像质量不佳，识别失败，建议重新上传清晰图片。',
        suggestions: ['重新拍照', '确保光线充足', '图片清晰度要求']
      }
    ]

    // 根据搜索条件过滤数据
    let filteredData = allMockData

    // 按状态过滤
    if (searchForm.status) {
      filteredData = filteredData.filter((item) => item.status === searchForm.status)
    }

    // 按时间范围过滤
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      const [startDate, endDate] = searchForm.dateRange
      filteredData = filteredData.filter((item) => {
        const itemDate = item.createTime.split(' ')[0] // 获取日期部分
        return itemDate >= startDate && itemDate <= endDate
      })
    }

    // 更新总数
    pagination.total = filteredData.length

    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    const paginatedData = filteredData.slice(startIndex, endIndex)

    identificationList.value = paginatedData
  } catch (error) {
    console.error('加载识别记录失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadIdentificationList()
})
</script>
<style lang="scss" scoped>
.pest-identification {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;
  }

  .search-section {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
  }

  .table-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;

    .image-slot {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      background-color: #f5f7fa;
      color: #909399;
      font-size: 12px;

      .el-icon {
        font-size: 20px;
        margin-bottom: 4px;
      }
    }
  }

  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .upload-area {
    text-align: center;
    padding: 40px 20px;

    .upload-icon {
      font-size: 48px;
      color: #c0c4cc;
      margin-bottom: 16px;
    }

    .upload-text {
      color: #606266;

      p {
        margin: 8px 0;

        em {
          color: #409eff;
          font-style: normal;
        }
      }

      .upload-tip {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .image-preview {
    position: relative;

    .image-actions {
      position: absolute;
      bottom: 10px;
      right: 10px;
    }
  }

  .detail-content {
    .detail-image {
      .el-image {
        width: 100%;
        height: 300px;
        border-radius: 8px;
        border: 1px solid #e4e7ed;

        .image-slot {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 100%;
          background-color: #f5f7fa;
          color: #909399;
          font-size: 14px;

          .el-icon {
            font-size: 48px;
            margin-bottom: 8px;
          }
        }
      }
    }

    .detail-info {
      .ai-analysis,
      .suggestions {
        margin-top: 20px;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 8px;

        h4 {
          margin: 0 0 10px 0;
          color: #2c3e50;
          font-size: 14px;
        }

        p {
          margin: 0;
          color: #606266;
          line-height: 1.6;
        }

        ul {
          margin: 0;
          padding-left: 20px;

          li {
            color: #606266;
            line-height: 1.6;
            margin-bottom: 5px;
          }
        }
      }
    }
  }

  .operation-buttons {
    display: flex;
    gap: 4px;
    flex-wrap: nowrap;

    .el-button {
      padding: 5px 8px;
      font-size: 12px;
      min-width: auto;
      flex-shrink: 0;
    }
  }
}

// 上传组件样式覆盖
:deep(.el-upload) {
  width: 100%;

  .el-upload-dragger {
    width: 100%;
    height: auto;
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .pest-identification {
    .search-section {
      .el-form {
        .el-form-item {
          display: block;
          margin-bottom: 15px;

          .el-form-item__content {
            margin-left: 0 !important;
          }
        }
      }
    }

    .el-table {
      font-size: 12px;
    }

    .operation-buttons {
      .el-button {
        padding: 3px 6px;
        font-size: 11px;
      }
    }

    .detail-content {
      .el-row {
        flex-direction: column;

        .el-col {
          width: 100%;
          margin-bottom: 20px;
        }
      }
    }
  }
}
</style>
