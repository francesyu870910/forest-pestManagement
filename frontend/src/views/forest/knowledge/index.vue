<template>
  <div class="knowledge-base">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>知识库管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加知识
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="知识标题">
            <el-input v-model="searchForm.title" placeholder="请输入知识标题" clearable />
          </el-form-item>

          <el-form-item label="知识分类">
            <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <el-option label="病虫害防治" value="pest_control" />
              <el-option label="森林管理" value="forest_management" />
              <el-option label="树种识别" value="species_identification" />
              <el-option label="生态保护" value="ecological_protection" />
              <el-option label="法律法规" value="laws_regulations" />
            </el-select>
          </el-form-item>

          <el-form-item label="知识类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option label="文档" value="document" />
              <el-option label="图片" value="image" />
              <el-option label="视频" value="video" />
              <el-option label="音频" value="audio" />
            </el-select>
          </el-form-item>

          <el-form-item label="发布状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="已发布" value="published" />
              <el-option label="草稿" value="draft" />
              <el-option label="待审核" value="pending" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 知识列表 -->
      <el-table :data="knowledgeList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="title" label="知识标题" min-width="200" show-overflow-tooltip />

        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryText(row.category) }}
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="author" label="作者" width="100" />

        <el-table-column prop="viewCount" label="浏览量" width="80" />

        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="120" />

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)"> 查看 </el-button>
            <el-button type="success" size="small" @click="editKnowledge(row)"> 编辑 </el-button>
            <el-button
              v-if="row.status === 'draft'"
              type="warning"
              size="small"
              @click="publishKnowledge(row)"
            >
              发布
            </el-button>
            <el-button type="danger" size="small" @click="deleteKnowledge(row)"> 删除 </el-button>
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

    <!-- 添加/编辑知识对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑知识' : '添加知识'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="knowledgeForm"
        :rules="knowledgeRules"
        ref="knowledgeFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="知识标题" prop="title">
              <el-input v-model="knowledgeForm.title" placeholder="请输入知识标题" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="知识分类" prop="category">
              <el-select v-model="knowledgeForm.category" placeholder="请选择分类">
                <el-option label="病虫害防治" value="pest_control" />
                <el-option label="森林管理" value="forest_management" />
                <el-option label="树种识别" value="species_identification" />
                <el-option label="生态保护" value="ecological_protection" />
                <el-option label="法律法规" value="laws_regulations" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="知识类型" prop="type">
              <el-select v-model="knowledgeForm.type" placeholder="请选择类型">
                <el-option label="文档" value="document" />
                <el-option label="图片" value="image" />
                <el-option label="视频" value="video" />
                <el-option label="音频" value="audio" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="发布状态" prop="status">
              <el-select v-model="knowledgeForm.status" placeholder="请选择状态">
                <el-option label="草稿" value="draft" />
                <el-option label="待审核" value="pending" />
                <el-option label="已发布" value="published" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="关键词" prop="keywords">
          <el-input
            v-model="knowledgeForm.keywords"
            placeholder="请输入关键词，多个关键词用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="知识摘要" prop="summary">
          <el-input
            v-model="knowledgeForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入知识摘要"
          />
        </el-form-item>

        <el-form-item label="知识内容" prop="content">
          <el-input
            v-model="knowledgeForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入知识内容"
          />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            multiple
            :file-list="knowledgeForm.attachments"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持jpg/png/gif/pdf/doc/docx文件，且不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveKnowledge" :loading="saveLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="知识详情" width="900px">
      <div v-if="currentKnowledge" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="知识标题">{{ currentKnowledge.title }}</el-descriptions-item>
          <el-descriptions-item label="知识分类">{{
            getCategoryText(currentKnowledge.category)
          }}</el-descriptions-item>
          <el-descriptions-item label="知识类型">
            <el-tag :type="getTypeTagType(currentKnowledge.type)" size="small">
              {{ getTypeText(currentKnowledge.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布状态">
            <el-tag :type="getStatusType(currentKnowledge.status)" size="small">
              {{ getStatusText(currentKnowledge.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="作者">{{ currentKnowledge.author }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{
            currentKnowledge.viewCount
          }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            currentKnowledge.createTime
          }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{
            currentKnowledge.updateTime
          }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentKnowledge.keywords" class="keywords-section">
          <h4>关键词</h4>
          <el-tag
            v-for="keyword in currentKnowledge.keywords.split(',')"
            :key="keyword"
            class="keyword-tag"
            size="small"
          >
            {{ keyword.trim() }}
          </el-tag>
        </div>

        <div v-if="currentKnowledge.summary" class="summary-section">
          <h4>知识摘要</h4>
          <p>{{ currentKnowledge.summary }}</p>
        </div>

        <div v-if="currentKnowledge.content" class="content-section">
          <h4>知识内容</h4>
          <div class="content-text">{{ currentKnowledge.content }}</div>
        </div>

        <div
          v-if="currentKnowledge.attachments && currentKnowledge.attachments.length"
          class="attachments-section"
        >
          <h4>相关附件</h4>
          <div class="attachment-list">
            <div
              v-for="attachment in currentKnowledge.attachments"
              :key="attachment.id"
              class="attachment-item"
            >
              <el-icon><Document /></el-icon>
              <span>{{ attachment.name }}</span>
              <el-button type="text" size="small" @click="downloadAttachment(attachment)">
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, UploadFilled, Document } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const currentKnowledge = ref(null)
const uploadUrl = ref('/api/upload') // 上传接口地址

const searchForm = reactive({
  title: '',
  category: '',
  type: '',
  status: ''
})

const knowledgeForm = reactive({
  title: '',
  category: '',
  type: '',
  status: 'draft',
  keywords: '',
  summary: '',
  content: '',
  attachments: []
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const knowledgeList = ref([])

// 表单引用
const knowledgeFormRef = ref()

// 表单验证规则
const knowledgeRules = {
  title: [{ required: true, message: '请输入知识标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择知识分类', trigger: 'change' }],
  type: [{ required: true, message: '请选择知识类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择发布状态', trigger: 'change' }],
  summary: [{ required: true, message: '请输入知识摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入知识内容', trigger: 'blur' }]
}

// 方法
const getCategoryText = (category) => {
  const categoryMap = {
    pest_control: '病虫害防治',
    forest_management: '森林管理',
    species_identification: '树种识别',
    ecological_protection: '生态保护',
    laws_regulations: '法律法规'
  }
  return categoryMap[category] || '未知'
}

const getTypeText = (type) => {
  const typeMap = {
    document: '文档',
    image: '图片',
    video: '视频',
    audio: '音频'
  }
  return typeMap[type] || '未知'
}

const getTypeTagType = (type) => {
  const typeMap = {
    document: '',
    image: 'success',
    video: 'warning',
    audio: 'info'
  }
  return typeMap[type] || ''
}

const getStatusType = (status) => {
  const typeMap = {
    published: 'success',
    pending: 'warning',
    draft: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    published: '已发布',
    pending: '待审核',
    draft: '草稿'
  }
  return textMap[status] || '未知'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadKnowledgeList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSaveKnowledge = async () => {
  if (!knowledgeFormRef.value) return

  await knowledgeFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存知识
        // if (isEdit.value) {
        //   await updateKnowledge(knowledgeForm)
        // } else {
        //   await createKnowledge(knowledgeForm)
        // }

        ElMessage.success(isEdit.value ? '知识更新成功' : '知识添加成功')
        showAddDialog.value = false
        resetKnowledgeForm()
        loadKnowledgeList()
      } catch (error) {
        console.error('保存知识失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetKnowledgeForm = () => {
  Object.keys(knowledgeForm).forEach((key) => {
    if (key === 'attachments') {
      knowledgeForm[key] = []
    } else if (key === 'status') {
      knowledgeForm[key] = 'draft'
    } else {
      knowledgeForm[key] = ''
    }
  })
  isEdit.value = false
  if (knowledgeFormRef.value) {
    knowledgeFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentKnowledge.value = row
  showDetailDialog.value = true
}

const editKnowledge = (row) => {
  Object.assign(knowledgeForm, row)
  isEdit.value = true
  showAddDialog.value = true
}

const publishKnowledge = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发布这个知识吗？', '确认发布', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API发布知识
    // await publishKnowledgeApi(row.id)

    ElMessage.success('发布成功')
    loadKnowledgeList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
      ElMessage.error('发布失败，请重试')
    }
  }
}

const deleteKnowledge = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个知识吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除知识
    // await deleteKnowledgeApi(row.id)

    ElMessage.success('删除成功')
    loadKnowledgeList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleUploadSuccess = (response, file) => {
  knowledgeForm.attachments.push({
    id: Date.now(),
    name: file.name,
    url: response.url || '#'
  })
  ElMessage.success('文件上传成功')
}

const handleUploadRemove = (file) => {
  const index = knowledgeForm.attachments.findIndex((item) => item.name === file.name)
  if (index > -1) {
    knowledgeForm.attachments.splice(index, 1)
  }
}

const downloadAttachment = (attachment) => {
  // 这里应该实现文件下载逻辑
  window.open(attachment.url, '_blank')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadKnowledgeList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadKnowledgeList()
}

const loadKnowledgeList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取知识列表
    // const response = await getKnowledgeList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 完整的模拟数据
    const allMockData = [
      {
        id: 1,
        title: '松毛虫防治技术指南',
        category: 'pest_control',
        type: 'document',
        author: '张专家',
        viewCount: 156,
        status: 'published',
        createTime: '2024-01-15',
        updateTime: '2024-01-16',
        keywords: '松毛虫,防治,技术',
        summary: '详细介绍松毛虫的识别特征、生活习性及有效防治方法',
        content: '松毛虫是森林中常见的害虫之一，主要危害松科植物...',
        attachments: [
          { id: 1, name: '松毛虫识别图谱.pdf', url: '#' },
          { id: 2, name: '防治药剂使用说明.doc', url: '#' }
        ]
      },
      {
        id: 2,
        title: '森林火灾预防与应急处理',
        category: 'forest_management',
        type: 'video',
        author: '李专家',
        viewCount: 89,
        status: 'published',
        createTime: '2024-01-12',
        updateTime: '2024-01-13',
        keywords: '森林火灾,预防,应急',
        summary: '森林火灾的预防措施和应急处理流程培训视频',
        content: '森林火灾是森林资源的重大威胁，本视频详细介绍...',
        attachments: []
      },
      {
        id: 3,
        title: '常见树种识别手册',
        category: 'species_identification',
        type: 'image',
        author: '王专家',
        viewCount: 234,
        status: 'draft',
        createTime: '2024-01-10',
        updateTime: '2024-01-11',
        keywords: '树种,识别,手册',
        summary: '北方常见树种的识别特征图文介绍',
        content: '本手册收录了北方地区50种常见树种的识别要点...',
        attachments: [{ id: 3, name: '树种识别图册.zip', url: '#' }]
      },
      {
        id: 4,
        title: '杨树溃疡病诊断与治疗',
        category: 'disease_control',
        type: 'document',
        author: '赵专家',
        viewCount: 178,
        status: 'published',
        createTime: '2024-01-08',
        updateTime: '2024-01-09',
        keywords: '杨树,溃疡病,诊断,治疗',
        summary: '杨树溃疡病的症状识别、病因分析及治疗方案',
        content: '杨树溃疡病是杨树的主要病害之一...',
        attachments: []
      },
      {
        id: 5,
        title: '森林抚育技术规程',
        category: 'forest_management',
        type: 'document',
        author: '孙专家',
        viewCount: 145,
        status: 'published',
        createTime: '2024-01-05',
        updateTime: '2024-01-06',
        keywords: '森林抚育,技术规程,管理',
        summary: '森林抚育的技术标准和操作规程',
        content: '森林抚育是提高森林质量的重要措施...',
        attachments: [{ id: 4, name: '抚育技术标准.pdf', url: '#' }]
      },
      {
        id: 6,
        title: '美国白蛾识别与防控',
        category: 'pest_control',
        type: 'video',
        author: '周专家',
        viewCount: 267,
        status: 'published',
        createTime: '2024-01-03',
        updateTime: '2024-01-04',
        keywords: '美国白蛾,识别,防控',
        summary: '美国白蛾的形态特征、危害症状及防控措施',
        content: '美国白蛾是重要的外来入侵害虫...',
        attachments: []
      },
      {
        id: 7,
        title: '林木种苗培育技术',
        category: 'cultivation',
        type: 'document',
        author: '吴专家',
        viewCount: 198,
        status: 'published',
        createTime: '2024-01-01',
        updateTime: '2024-01-02',
        keywords: '林木,种苗,培育,技术',
        summary: '林木种苗的选择、培育和管理技术要点',
        content: '优质种苗是造林成功的基础...',
        attachments: [{ id: 5, name: '种苗培育手册.doc', url: '#' }]
      },
      {
        id: 8,
        title: '森林病虫害监测方法',
        category: 'monitoring',
        type: 'image',
        author: '郑专家',
        viewCount: 123,
        status: 'draft',
        createTime: '2023-12-28',
        updateTime: '2023-12-29',
        keywords: '病虫害,监测,方法',
        summary: '森林病虫害的监测技术和方法介绍',
        content: '及时准确的监测是病虫害防治的前提...',
        attachments: []
      },
      {
        id: 9,
        title: '生物防治技术应用',
        category: 'pest_control',
        type: 'video',
        author: '冯专家',
        viewCount: 189,
        status: 'published',
        createTime: '2023-12-25',
        updateTime: '2023-12-26',
        keywords: '生物防治,技术,应用',
        summary: '生物防治技术在森林病虫害防治中的应用',
        content: '生物防治是环境友好的防治方法...',
        attachments: []
      },
      {
        id: 10,
        title: '森林健康评价体系',
        category: 'forest_management',
        type: 'document',
        author: '陈专家',
        viewCount: 156,
        status: 'published',
        createTime: '2023-12-20',
        updateTime: '2023-12-21',
        keywords: '森林健康,评价,体系',
        summary: '森林健康状况的评价指标和方法体系',
        content: '森林健康评价是森林管理的重要工具...',
        attachments: [{ id: 6, name: '评价指标体系.xlsx', url: '#' }]
      }
    ]

    // 根据搜索条件过滤数据
    let filteredData = allMockData

    // 按标题过滤
    if (searchForm.title) {
      filteredData = filteredData.filter((item) =>
        item.title.toLowerCase().includes(searchForm.title.toLowerCase())
      )
    }

    // 按分类过滤
    if (searchForm.category) {
      filteredData = filteredData.filter((item) => item.category === searchForm.category)
    }

    // 按类型过滤
    if (searchForm.type) {
      filteredData = filteredData.filter((item) => item.type === searchForm.type)
    }

    // 按状态过滤
    if (searchForm.status) {
      filteredData = filteredData.filter((item) => item.status === searchForm.status)
    }

    // 更新总数
    pagination.total = filteredData.length

    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    const paginatedData = filteredData.slice(startIndex, endIndex)

    knowledgeList.value = paginatedData
  } catch (error) {
    console.error('加载知识列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadKnowledgeList()
})
</script>

<style lang="scss" scoped>
.knowledge-base {
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

  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .detail-content {
    .keywords-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      .keyword-tag {
        margin-right: 8px;
        margin-bottom: 8px;
      }
    }

    .summary-section,
    .content-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      p,
      .content-text {
        color: #606266;
        line-height: 1.8;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 4px;
        white-space: pre-wrap;
      }
    }

    .attachments-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      .attachment-list {
        .attachment-item {
          display: flex;
          align-items: center;
          padding: 10px;
          background-color: #f8f9fa;
          border-radius: 4px;
          margin-bottom: 8px;

          .el-icon {
            margin-right: 8px;
            color: #409eff;
          }

          span {
            flex: 1;
            color: #606266;
          }
        }
      }
    }
  }

  .upload-demo {
    width: 100%;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .knowledge-base {
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

      .el-button {
        padding: 5px 8px;
        font-size: 12px;
        margin-bottom: 5px;
      }
    }
  }
}
</style>
