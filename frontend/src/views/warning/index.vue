<template>
  <div class="early-warning">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警中心</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加预警
          </el-button>
        </div>
      </template>

      <!-- 预警统计 -->
      <div class="warning-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card danger">
              <div class="stat-content">
                <div class="stat-number">{{ stats.critical }}</div>
                <div class="stat-label">严重预警</div>
              </div>
              <el-icon class="stat-icon"><Warning /></el-icon>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card warning">
              <div class="stat-content">
                <div class="stat-number">{{ stats.high }}</div>
                <div class="stat-label">高级预警</div>
              </div>
              <el-icon class="stat-icon"><Notification /></el-icon>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card info">
              <div class="stat-content">
                <div class="stat-number">{{ stats.medium }}</div>
                <div class="stat-label">中级预警</div>
              </div>
              <el-icon class="stat-icon"><InfoFilled /></el-icon>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card success">
              <div class="stat-content">
                <div class="stat-number">{{ stats.low }}</div>
                <div class="stat-label">低级预警</div>
              </div>
              <el-icon class="stat-icon"><CircleCheck /></el-icon>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="预警类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option label="病虫害预警" value="pest" />
              <el-option label="火灾预警" value="fire" />
              <el-option label="气象预警" value="weather" />
              <el-option label="生态预警" value="ecology" />
            </el-select>
          </el-form-item>

          <el-form-item label="预警级别">
            <el-select v-model="searchForm.level" placeholder="请选择级别" clearable>
              <el-option label="严重" value="critical" />
              <el-option label="高级" value="high" />
              <el-option label="中级" value="medium" />
              <el-option label="低级" value="low" />
            </el-select>
          </el-form-item>

          <el-form-item label="预警状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="活跃" value="active" />
              <el-option label="已处理" value="handled" />
              <el-option label="已关闭" value="closed" />
            </el-select>
          </el-form-item>

          <el-form-item label="区域">
            <el-input v-model="searchForm.region" placeholder="请输入区域" clearable />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 当前筛选条件展示 -->
      <div class="filter-info" v-if="hasActiveFilters">
        <div class="filter-header">
          <span class="filter-title">当前筛选条件：</span>
          <el-button type="text" size="small" @click="clearAllFilters" class="clear-all-btn">
            <el-icon><Close /></el-icon>
            清空所有筛选
          </el-button>
        </div>
        <div class="filter-tags">
          <el-tag
            v-if="searchForm.type"
            closable
            @close="clearFilter('type')"
            type="primary"
            class="filter-tag"
          >
            类型：{{ getTypeText(searchForm.type) }}
          </el-tag>
          <el-tag
            v-if="searchForm.level"
            closable
            @close="clearFilter('level')"
            type="warning"
            class="filter-tag"
          >
            级别：{{ getLevelText(searchForm.level) }}
          </el-tag>
          <el-tag
            v-if="searchForm.status"
            closable
            @close="clearFilter('status')"
            type="success"
            class="filter-tag"
          >
            状态：{{ getStatusText(searchForm.status) }}
          </el-tag>
          <el-tag
            v-if="searchForm.region"
            closable
            @close="clearFilter('region')"
            type="info"
            class="filter-tag"
          >
            区域：{{ searchForm.region }}
          </el-tag>
        </div>
        <div class="filter-summary">
          <span class="summary-text">
            共找到 <strong>{{ pagination.total }}</strong> 条符合条件的预警信息
          </span>
        </div>
      </div>

      <!-- 预警列表 -->
      <el-table :data="warningList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="title" label="预警标题" min-width="200" show-overflow-tooltip />

        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>

        <el-table-column prop="level" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="region" label="区域" width="120" />

        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="120" />

        <el-table-column prop="updateTime" label="更新时间" width="120" />

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row)"> 详情 </el-button>
              <el-button
                v-if="row.status === 'active'"
                type="success"
                size="small"
                @click="handleWarning(row)"
              >
                处理
              </el-button>
              <el-button type="warning" size="small" @click="editWarning(row)"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="deleteWarning(row)"> 删除 </el-button>
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

    <!-- 添加/编辑预警对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑预警' : '添加预警'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="warningForm" :rules="warningRules" ref="warningFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预警标题" prop="title">
              <el-input v-model="warningForm.title" placeholder="请输入预警标题" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="预警类型" prop="type">
              <el-select v-model="warningForm.type" placeholder="请选择类型">
                <el-option label="病虫害预警" value="pest" />
                <el-option label="火灾预警" value="fire" />
                <el-option label="气象预警" value="weather" />
                <el-option label="生态预警" value="ecology" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预警级别" prop="level">
              <el-select v-model="warningForm.level" placeholder="请选择级别">
                <el-option label="严重" value="critical" />
                <el-option label="高级" value="high" />
                <el-option label="中级" value="medium" />
                <el-option label="低级" value="low" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="影响区域" prop="region">
              <el-input v-model="warningForm.region" placeholder="请输入影响区域" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="预警描述" prop="description">
          <el-input
            v-model="warningForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入预警描述"
          />
        </el-form-item>

        <el-form-item label="应对措施" prop="measures">
          <el-input
            v-model="warningForm.measures"
            type="textarea"
            :rows="3"
            placeholder="请输入应对措施"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="有效期开始" prop="startTime">
              <el-date-picker
                v-model="warningForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="有效期结束" prop="endTime">
              <el-date-picker
                v-model="warningForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveWarning" :loading="saveLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="预警详情" width="800px">
      <div v-if="currentWarning" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预警标题">{{ currentWarning.title }}</el-descriptions-item>
          <el-descriptions-item label="预警类型">{{
            getTypeText(currentWarning.type)
          }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <el-tag :type="getLevelType(currentWarning.level)">
              {{ getLevelText(currentWarning.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <el-tag :type="getStatusType(currentWarning.status)">
              {{ getStatusText(currentWarning.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="影响区域">{{ currentWarning.region }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{
            currentWarning.createTime
          }}</el-descriptions-item>
          <el-descriptions-item label="有效期开始">{{
            currentWarning.startTime
          }}</el-descriptions-item>
          <el-descriptions-item label="有效期结束">{{
            currentWarning.endTime
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="description-section">
          <h4>预警描述</h4>
          <p>{{ currentWarning.description }}</p>
        </div>

        <div class="measures-section">
          <h4>应对措施</h4>
          <p>{{ currentWarning.measures }}</p>
        </div>

        <div v-if="currentWarning.handleRecord" class="handle-section">
          <h4>处理记录</h4>
          <el-timeline>
            <el-timeline-item
              v-for="record in currentWarning.handleRecord"
              :key="record.id"
              :timestamp="record.time"
              :type="record.type"
            >
              <div class="handle-item">
                <h5>{{ record.title }}</h5>
                <p>{{ record.description }}</p>
                <span class="handler">处理人：{{ record.handler }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>

    <!-- 处理预警对话框 -->
    <el-dialog v-model="showHandleDialog" title="处理预警" width="600px">
      <el-form :model="handleForm" :rules="handleRules" ref="handleFormRef" label-width="120px">
        <el-form-item label="处理结果" prop="result">
          <el-select v-model="handleForm.result" placeholder="请选择处理结果">
            <el-option label="已处理" value="handled" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>

        <el-form-item label="处理说明" prop="description">
          <el-input
            v-model="handleForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入处理说明"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="handleLoading"> 提交 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Warning, Notification, InfoFilled, CircleCheck, Close } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const handleLoading = ref(false)
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const showHandleDialog = ref(false)
const isEdit = ref(false)
const currentWarning = ref(null)

const stats = reactive({
  critical: 3,
  high: 8,
  medium: 15,
  low: 24
})

const searchForm = reactive({
  type: '',
  level: '',
  status: '',
  region: ''
})

const warningForm = reactive({
  title: '',
  type: '',
  level: '',
  region: '',
  description: '',
  measures: '',
  startTime: '',
  endTime: ''
})

const handleForm = reactive({
  result: '',
  description: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const warningList = ref([])

// 表单引用
const warningFormRef = ref()
const handleFormRef = ref()

// 表单验证规则
const warningRules = {
  title: [{ required: true, message: '请输入预警标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择预警类型', trigger: 'change' }],
  level: [{ required: true, message: '请选择预警级别', trigger: 'change' }],
  region: [{ required: true, message: '请输入影响区域', trigger: 'blur' }],
  description: [{ required: true, message: '请输入预警描述', trigger: 'blur' }],
  measures: [{ required: true, message: '请输入应对措施', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择有效期开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择有效期结束时间', trigger: 'change' }]
}

const handleRules = {
  result: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  description: [{ required: true, message: '请输入处理说明', trigger: 'blur' }]
}

// 方法
const getTypeText = (type) => {
  const typeMap = {
    pest: '病虫害预警',
    fire: '火灾预警',
    weather: '气象预警',
    ecology: '生态预警'
  }
  return typeMap[type] || '未知'
}

const getLevelType = (level) => {
  const typeMap = {
    critical: 'danger',
    high: 'warning',
    medium: 'info',
    low: 'success'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    critical: '严重',
    high: '高级',
    medium: '中级',
    low: '低级'
  }
  return textMap[level] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    active: 'danger',
    handled: 'warning',
    closed: 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    active: '活跃',
    handled: '已处理',
    closed: '已关闭'
  }
  return textMap[status] || '未知'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadWarningList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 筛选信息相关方法
const hasActiveFilters = computed(() => {
  return Object.values(searchForm).some(value => value && value.trim() !== '')
})

const clearFilter = (filterKey) => {
  searchForm[filterKey] = ''
  handleSearch()
}

const clearAllFilters = () => {
  resetSearch()
}

const handleSaveWarning = async () => {
  if (!warningFormRef.value) return

  await warningFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存预警
        // if (isEdit.value) {
        //   await updateWarning(warningForm)
        // } else {
        //   await createWarning(warningForm)
        // }

        ElMessage.success(isEdit.value ? '预警更新成功' : '预警添加成功')
        showAddDialog.value = false
        resetWarningForm()
        loadWarningList()
      } catch (error) {
        console.error('保存预警失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetWarningForm = () => {
  Object.keys(warningForm).forEach((key) => {
    warningForm[key] = ''
  })
  isEdit.value = false
  if (warningFormRef.value) {
    warningFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentWarning.value = {
    ...row,
    handleRecord: [
      {
        id: 1,
        title: '预警发布',
        description: '系统自动发布预警信息',
        time: row.createTime,
        type: 'primary',
        handler: '系统'
      }
    ]
  }
  showDetailDialog.value = true
}

const editWarning = (row) => {
  Object.assign(warningForm, row)
  isEdit.value = true
  showAddDialog.value = true
}

const handleWarning = (row) => {
  currentWarning.value = row
  handleForm.result = ''
  handleForm.description = ''
  showHandleDialog.value = true
}

const submitHandle = async () => {
  if (!handleFormRef.value) return

  await handleFormRef.value.validate(async (valid) => {
    if (valid) {
      handleLoading.value = true
      try {
        // 这里应该调用API处理预警
        // await handleWarningApi(currentWarning.value.id, handleForm)

        ElMessage.success('预警处理成功')
        showHandleDialog.value = false
        loadWarningList()
      } catch (error) {
        console.error('处理预警失败:', error)
        ElMessage.error('处理失败，请重试')
      } finally {
        handleLoading.value = false
      }
    }
  })
}

const deleteWarning = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个预警吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除预警
    // await deleteWarningApi(row.id)

    ElMessage.success('删除成功')
    loadWarningList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadWarningList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadWarningList()
}

const loadWarningList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取预警列表
    // const response = await getWarningList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 完整的模拟数据
    const allMockData = [
      {
        id: 1,
        title: '松毛虫大面积爆发预警',
        type: 'pest',
        level: 'critical',
        region: '昌平区森林公园',
        status: 'active',
        description: '监测发现松毛虫虫口密度异常增高，预计将大面积爆发',
        measures: '立即启动应急防治方案，组织专业队伍进行药剂防治',
        startTime: '2024-01-20 08:00:00',
        endTime: '2024-01-25 18:00:00',
        createTime: '2024-01-20 08:00:00',
        updateTime: '2024-01-20 08:00:00'
      },
      {
        id: 2,
        title: '森林火险等级升高预警',
        type: 'fire',
        level: 'high',
        region: '承德市林区',
        status: 'handled',
        description: '近期天气干燥，风力较大，森林火险等级升高',
        measures: '加强巡护，严禁野外用火，做好防火准备',
        startTime: '2024-01-18 00:00:00',
        endTime: '2024-01-22 23:59:59',
        createTime: '2024-01-18 09:00:00',
        updateTime: '2024-01-19 14:30:00'
      },
      {
        id: 3,
        title: '美国白蛾入侵预警',
        type: 'pest',
        level: 'high',
        region: '天津蓟州区',
        status: 'active',
        description: '发现美国白蛾成虫，存在大面积扩散风险',
        measures: '设置诱捕器，加强监测，准备防治药剂',
        startTime: '2024-01-19 10:00:00',
        endTime: '2024-01-24 18:00:00',
        createTime: '2024-01-19 10:00:00',
        updateTime: '2024-01-19 10:00:00'
      },
      {
        id: 4,
        title: '干旱灾害预警',
        type: 'weather',
        level: 'medium',
        region: '山东济南市',
        status: 'active',
        description: '连续无降雨，土壤湿度下降，可能影响林木生长',
        measures: '启动抗旱预案，组织人工灌溉，监测林木状况',
        startTime: '2024-01-17 00:00:00',
        endTime: '2024-01-27 23:59:59',
        createTime: '2024-01-17 08:00:00',
        updateTime: '2024-01-17 08:00:00'
      },
      {
        id: 5,
        title: '杨树溃疡病扩散预警',
        type: 'disease',
        level: 'high',
        region: '河北承德市',
        status: 'handled',
        description: '杨树溃疡病发病率上升，有扩散趋势',
        measures: '清除病株，喷洒杀菌剂，加强林分管理',
        startTime: '2024-01-15 08:00:00',
        endTime: '2024-01-20 18:00:00',
        createTime: '2024-01-15 08:00:00',
        updateTime: '2024-01-18 16:00:00'
      },
      {
        id: 6,
        title: '强风天气预警',
        type: 'weather',
        level: 'medium',
        region: '江苏南京市',
        status: 'expired',
        description: '预计有6-8级大风，可能造成树木倒伏',
        measures: '加固支撑，清理枯枝，做好应急准备',
        startTime: '2024-01-12 12:00:00',
        endTime: '2024-01-13 18:00:00',
        createTime: '2024-01-12 08:00:00',
        updateTime: '2024-01-13 20:00:00'
      },
      {
        id: 7,
        title: '红蜘蛛爆发预警',
        type: 'pest',
        level: 'medium',
        region: '浙江杭州市',
        status: 'active',
        description: '高温干燥天气导致红蜘蛛大量繁殖',
        measures: '增加湿度，喷洒杀螨剂，引入天敌',
        startTime: '2024-01-16 08:00:00',
        endTime: '2024-01-21 18:00:00',
        createTime: '2024-01-16 08:00:00',
        updateTime: '2024-01-16 08:00:00'
      },
      {
        id: 8,
        title: '森林病虫害综合预警',
        type: 'pest',
        level: 'critical',
        region: '安徽合肥市',
        status: 'handled',
        description: '多种病虫害同时发生，威胁森林健康',
        measures: '启动应急响应，综合防治，加强监测',
        startTime: '2024-01-10 08:00:00',
        endTime: '2024-01-15 18:00:00',
        createTime: '2024-01-10 08:00:00',
        updateTime: '2024-01-14 16:00:00'
      },
      {
        id: 9,
        title: '低温冻害预警',
        type: 'weather',
        level: 'high',
        region: '湖北武汉市',
        status: 'expired',
        description: '气温骤降，可能对幼林造成冻害',
        measures: '覆盖保温，熏烟防霜，加强巡查',
        startTime: '2024-01-08 18:00:00',
        endTime: '2024-01-10 08:00:00',
        createTime: '2024-01-08 15:00:00',
        updateTime: '2024-01-10 10:00:00'
      },
      {
        id: 10,
        title: '蚧壳虫危害预警',
        type: 'pest',
        level: 'medium',
        region: '广东广州市',
        status: 'active',
        description: '蚧壳虫密度增加，影响树木正常生长',
        measures: '人工刮除，喷洒杀虫剂，加强修剪',
        startTime: '2024-01-14 08:00:00',
        endTime: '2024-01-19 18:00:00',
        createTime: '2024-01-14 08:00:00',
        updateTime: '2024-01-14 08:00:00'
      }
    ]

    // 根据搜索条件过滤数据
    let filteredData = allMockData

    // 按预警类型过滤
    if (searchForm.type) {
      filteredData = filteredData.filter((item) => item.type === searchForm.type)
    }

    // 按预警级别过滤
    if (searchForm.level) {
      filteredData = filteredData.filter((item) => item.level === searchForm.level)
    }

    // 按状态过滤
    if (searchForm.status) {
      filteredData = filteredData.filter((item) => item.status === searchForm.status)
    }

    // 按地区过滤
    if (searchForm.region) {
      filteredData = filteredData.filter((item) =>
        item.region.toLowerCase().includes(searchForm.region.toLowerCase())
      )
    }

    // 更新总数
    pagination.total = filteredData.length

    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    const paginatedData = filteredData.slice(startIndex, endIndex)

    warningList.value = paginatedData
  } catch (error) {
    console.error('加载预警列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadWarningList()
})
</script>

<style lang="scss" scoped>
.early-warning {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;
  }

  .warning-stats {
    margin-bottom: 20px;

    .stat-card {
      position: relative;
      overflow: hidden;

      &.danger {
        border-left: 4px solid #f56c6c;
        .stat-number {
          color: #f56c6c;
        }
        .stat-icon {
          color: #f56c6c;
        }
      }

      &.warning {
        border-left: 4px solid #e6a23c;
        .stat-number {
          color: #e6a23c;
        }
        .stat-icon {
          color: #e6a23c;
        }
      }

      &.info {
        border-left: 4px solid #409eff;
        .stat-number {
          color: #409eff;
        }
        .stat-icon {
          color: #409eff;
        }
      }

      &.success {
        border-left: 4px solid #67c23a;
        .stat-number {
          color: #67c23a;
        }
        .stat-icon {
          color: #67c23a;
        }
      }

      .stat-content {
        .stat-number {
          font-size: 32px;
          font-weight: bold;
          line-height: 1;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
        }
      }

      .stat-icon {
        position: absolute;
        right: 20px;
        top: 50%;
        transform: translateY(-50%);
        font-size: 40px;
        opacity: 0.3;
      }
    }
  }

  .search-section {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
  }

  .filter-info {
    margin-bottom: 20px;
    padding: 16px;
    background-color: #f0f9ff;
    border: 1px solid #e1f5fe;
    border-radius: 8px;
    
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      .filter-title {
        font-weight: 600;
        color: #2c3e50;
        font-size: 14px;
      }
      
      .clear-all-btn {
        color: #909399;
        font-size: 12px;
        padding: 0;
        
        &:hover {
          color: #f56c6c;
        }
      }
    }
    
    .filter-tags {
      margin-bottom: 12px;
      
      .filter-tag {
        margin-right: 8px;
        margin-bottom: 6px;
        font-size: 12px;
        
        &.el-tag--primary {
          background-color: #ecf5ff;
          border-color: #b3d8ff;
          color: #409eff;
        }
        
        &.el-tag--warning {
          background-color: #fdf6ec;
          border-color: #f5dab1;
          color: #e6a23c;
        }
        
        &.el-tag--success {
          background-color: #f0f9ff;
          border-color: #b3e19d;
          color: #67c23a;
        }
        
        &.el-tag--info {
          background-color: #f4f4f5;
          border-color: #d3d4d6;
          color: #909399;
        }
      }
    }
    
    .filter-summary {
      .summary-text {
        color: #606266;
        font-size: 13px;
        
        strong {
          color: #409eff;
          font-weight: 600;
        }
      }
    }
  }

  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .detail-content {
    .description-section,
    .measures-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      p {
        color: #606266;
        line-height: 1.8;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 4px;
      }
    }

    .handle-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 15px;
      }

      .handle-item {
        h5 {
          margin: 0 0 8px 0;
          color: #2c3e50;
          font-size: 14px;
        }

        p {
          margin: 0 0 8px 0;
          color: #606266;
          font-size: 13px;
          line-height: 1.5;
        }

        .handler {
          font-size: 12px;
          color: #909399;
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

// 响应式设计
@media (max-width: 768px) {
  .early-warning {
    .warning-stats {
      .el-col {
        margin-bottom: 15px;
      }
    }

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
  }
}
</style>
