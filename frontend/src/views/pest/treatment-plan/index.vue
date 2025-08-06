<template>
  <div class="treatment-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>防治方案管理</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            创建方案
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="方案名称">
            <el-input v-model="searchForm.planName" placeholder="请输入方案名称" clearable />
          </el-form-item>
          
          <el-form-item label="病虫害类型">
            <el-select v-model="searchForm.pestType" placeholder="请选择病虫害类型" clearable>
              <el-option label="松毛虫" value="pine_caterpillar" />
              <el-option label="杨树溃疡病" value="poplar_canker" />
              <el-option label="美国白蛾" value="american_white_moth" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="方案状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="草稿" value="draft" />
              <el-option label="执行中" value="executing" />
              <el-option label="已完成" value="completed" />
              <el-option label="已暂停" value="paused" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 方案列表 -->
      <el-table :data="planList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="planName" label="方案名称" min-width="150" />
        <el-table-column prop="pestType" label="病虫害类型" width="120">
          <template #default="{ row }">
            {{ getPestTypeText(row.pestType) }}
          </template>
        </el-table-column>
        <el-table-column prop="targetArea" label="目标区域" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="执行进度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :stroke-width="6" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row)">详情</el-button>
              <el-button type="success" size="small" @click="trackProgress(row)">跟踪</el-button>
              <el-button type="warning" size="small" @click="editPlan(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deletePlan(row)">删除</el-button>
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
    
    <!-- 创建/编辑方案对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="isEdit ? '编辑方案' : '创建方案'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案名称" prop="planName">
              <el-input v-model="planForm.planName" placeholder="请输入方案名称" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="病虫害类型" prop="pestType">
              <el-select v-model="planForm.pestType" placeholder="请选择病虫害类型">
                <el-option label="松毛虫" value="pine_caterpillar" />
                <el-option label="杨树溃疡病" value="poplar_canker" />
                <el-option label="美国白蛾" value="american_white_moth" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目标区域" prop="targetArea">
              <el-input v-model="planForm.targetArea" placeholder="请输入目标区域" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="预计面积" prop="estimatedArea">
              <el-input v-model="planForm.estimatedArea" placeholder="请输入面积（公顷）">
                <template #append>公顷</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="方案描述" prop="description">
          <el-input
            v-model="planForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入方案描述"
          />
        </el-form-item>
        
        <el-form-item label="防治措施">
          <div class="treatment-measures">
            <div v-for="(measure, index) in planForm.measures" :key="index" class="measure-item">
              <el-row :gutter="10">
                <el-col :span="6">
                  <el-select v-model="measure.type" placeholder="措施类型">
                    <el-option label="化学防治" value="chemical" />
                    <el-option label="生物防治" value="biological" />
                    <el-option label="物理防治" value="physical" />
                    <el-option label="营林措施" value="silvicultural" />
                  </el-select>
                </el-col>
                <el-col :span="14">
                  <el-input v-model="measure.description" placeholder="措施描述" />
                </el-col>
                <el-col :span="4">
                  <el-button type="danger" size="small" @click="removeMeasure(index)">
                    删除
                  </el-button>
                </el-col>
              </el-row>
            </div>
            <el-button type="primary" size="small" @click="addMeasure">
              添加措施
            </el-button>
          </div>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="planForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="预计结束时间" prop="endTime">
              <el-date-picker
                v-model="planForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="负责人" prop="responsible">
          <el-input v-model="planForm.responsible" placeholder="请输入负责人姓名" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSavePlan" :loading="saveLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 任务跟踪对话框 -->
    <el-dialog
      v-model="showTrackDialog"
      title="任务跟踪"
      width="900px"
    >
      <div v-if="currentPlan" class="track-content">
        <div class="plan-info">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="方案名称">{{ currentPlan.planName }}</el-descriptions-item>
            <el-descriptions-item label="执行进度">
              <el-progress :percentage="currentPlan.progress" />
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentPlan.status)">
                {{ getStatusText(currentPlan.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="task-timeline">
          <h4>执行时间线</h4>
          <el-timeline>
            <el-timeline-item
              v-for="task in currentPlan.tasks"
              :key="task.id"
              :timestamp="task.time"
              :type="getTaskType(task.status)"
            >
              <div class="task-item">
                <h5>{{ task.title }}</h5>
                <p>{{ task.description }}</p>
                <el-tag size="small" :type="getTaskType(task.status)">
                  {{ getTimelineStatusText(task.status) }}
                </el-tag>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>
    
    <!-- 方案详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="方案详情"
      width="1200px"
    >
      <div v-if="currentPlan" class="detail-content">
        <!-- 基本信息 -->
        <div class="basic-info">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="方案名称">{{ currentPlan.planName }}</el-descriptions-item>
            <el-descriptions-item label="病虫害类型">{{ getPestTypeText(currentPlan.pestType) }}</el-descriptions-item>
            <el-descriptions-item label="目标区域">{{ currentPlan.targetArea }}</el-descriptions-item>
            <el-descriptions-item label="预计面积">{{ currentPlan.estimatedArea }}公顷</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ currentPlan.responsible }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentPlan.status)">
                {{ getStatusText(currentPlan.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="执行进度">
              <el-progress :percentage="currentPlan.progress" />
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentPlan.createTime }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 防治任务管理 -->
        <div class="tasks-section">
          <div class="section-header">
            <h3>防治任务</h3>
            <div class="task-actions">
              <el-button type="primary" size="small" @click="showCreateTaskDialog = true">
                <el-icon><Plus /></el-icon>
                创建任务
              </el-button>
              <el-button type="success" size="small" @click="showBatchCreateDialog = true">
                批量创建
              </el-button>
            </div>
          </div>
          
          <!-- 任务搜索筛选 -->
          <div class="task-filters">
            <el-form :model="taskSearchForm" inline size="small">
              <el-form-item label="任务名称">
                <el-input v-model="taskSearchForm.taskName" placeholder="搜索任务" clearable style="width: 150px" />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="taskSearchForm.status" placeholder="选择状态" clearable style="width: 120px">
                  <el-option label="待执行" value="PENDING" />
                  <el-option label="执行中" value="IN_PROGRESS" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="已暂停" value="PAUSED" />
                </el-select>
              </el-form-item>
              <el-form-item label="优先级">
                <el-select v-model="taskSearchForm.priority" placeholder="选择优先级" clearable style="width: 100px">
                  <el-option label="高" value="HIGH" />
                  <el-option label="中" value="MEDIUM" />
                  <el-option label="低" value="LOW" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="filterTasks">搜索</el-button>
                <el-button @click="resetTaskFilters">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 任务列表 -->
          <el-table :data="filteredTasks" border size="small" v-loading="taskLoading">
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="assignedTo" label="分配人员" width="100">
              <template #default="{ row }">
                {{ getUserName(row.assignedTo) || '未分配' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getTaskStatusType(row.status)" size="small">
                  {{ getTaskStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">
                  {{ getPriorityText(row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="scheduledTime" label="计划时间" width="150">
              <template #default="{ row }">
                {{ formatDateTime(row.scheduledTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="actualStartTime" label="实际开始" width="150">
              <template #default="{ row }">
                {{ formatDateTime(row.actualStartTime) || '未开始' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editTask(row)">编辑</el-button>
                <el-button type="success" size="small" @click="assignTask(row)">分配</el-button>
                <el-button type="danger" size="small" @click="deleteTask(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
    
    <!-- 创建/编辑任务对话框 -->
    <el-dialog
      v-model="showCreateTaskDialog"
      :title="isEditTask ? '编辑任务' : '创建任务'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分配人员" prop="assignedTo">
              <el-select v-model="taskForm.assignedTo" placeholder="选择执行人员" style="width: 100%">
                <el-option
                  v-for="user in availableUsers"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="taskForm.priority" placeholder="选择优先级" style="width: 100%">
                <el-option label="高" value="HIGH" />
                <el-option label="中" value="MEDIUM" />
                <el-option label="低" value="LOW" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划时间" prop="scheduledTime">
              <el-date-picker
                v-model="taskForm.scheduledTime"
                type="datetime"
                placeholder="选择计划执行时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="目标区域" prop="targetArea">
              <el-input v-model="taskForm.targetArea" placeholder="请输入目标区域" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="执行备注" v-if="isEditTask">
          <el-input
            v-model="taskForm.executionNotes"
            type="textarea"
            :rows="3"
            placeholder="请输入执行备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateTaskDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTask" :loading="taskSaveLoading">
          {{ isEditTask ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 任务分配对话框 -->
    <el-dialog
      v-model="showAssignDialog"
      title="分配任务"
      width="400px"
    >
      <div v-if="currentTask">
        <p>任务名称：{{ currentTask.taskName }}</p>
        <p>当前分配：{{ getUserName(currentTask.assignedTo) || '未分配' }}</p>
        
        <el-form :model="assignForm" label-width="80px">
          <el-form-item label="分配给">
            <el-select v-model="assignForm.assignedTo" placeholder="选择执行人员" style="width: 100%">
              <el-option
                v-for="user in availableUsers"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAssignTask" :loading="assignLoading">
          确认分配
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getTreatmentTasks,
  createTreatmentTask,
  updateTreatmentTask,
  deleteTreatmentTask,
  assignTreatmentTask
} from '@/api/treatmentTask'

const route = useRoute()

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showCreateTaskDialog = ref(false)
const showBatchCreateDialog = ref(false)
const showAssignDialog = ref(false)
const showTrackDialog = ref(false)
const isEdit = ref(false)
const isEditTask = ref(false)
const currentPlan = ref(null)
const currentTask = ref(null)
const taskLoading = ref(false)
const taskSaveLoading = ref(false)
const assignLoading = ref(false)
const filteredTasks = ref([])
const allTasks = ref([])

const searchForm = reactive({
  planName: '',
  pestType: '',
  status: ''
})

const taskForm = reactive({
  taskName: '',
  description: '',
  assignedTo: '',
  priority: 'MEDIUM',
  scheduledTime: '',
  targetArea: '',
  executionNotes: ''
})

const taskSearchForm = reactive({
  taskName: '',
  status: '',
  priority: '',
  assignedTo: ''
})

const assignForm = reactive({
  assignedTo: ''
})

const planForm = reactive({
  planName: '',
  pestType: '',
  targetArea: '',
  estimatedArea: '',
  description: '',
  measures: [{ type: '', description: '' }],
  startTime: '',
  endTime: '',
  responsible: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const planList = ref([])

const availableUsers = ref([
  { id: 'user001', name: '王建国', role: '高级林业工程师' },
  { id: 'user002', name: '李明华', role: '防治专员' },
  { id: 'user003', name: '陈志强', role: '现场作业员' },
  { id: 'user004', name: '张德胜', role: '技术主管' },
  { id: 'user005', name: '刘晓东', role: '防治专员' },
  { id: 'user006', name: '赵国庆', role: '现场作业员' },
  { id: 'user007', name: '孙维民', role: '质量监督员' },
  { id: 'user008', name: '周建华', role: '设备操作员' }
])

// 表单引用
const taskFormRef = ref()
const planFormRef = ref()

// 表单验证规则
const taskRules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  assignedTo: [
    { required: true, message: '请选择分配人员', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  scheduledTime: [
    { required: true, message: '请选择计划执行时间', trigger: 'change' }
  ]
}

const planRules = {
  planName: [
    { required: true, message: '请输入方案名称', trigger: 'blur' }
  ],
  pestType: [
    { required: true, message: '请选择病虫害类型', trigger: 'change' }
  ],
  targetArea: [
    { required: true, message: '请输入目标区域', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  responsible: [
    { required: true, message: '请输入负责人', trigger: 'blur' }
  ]
}

// 方法
const getPestTypeText = (type) => {
  const typeMap = {
    pine_caterpillar: '松毛虫',
    poplar_canker: '杨树溃疡病',
    american_white_moth: '美国白蛾',
    other: '其他'
  }
  return typeMap[type] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    draft: 'info',
    executing: 'warning',
    completed: 'success',
    paused: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    draft: '草稿',
    executing: '执行中',
    completed: '已完成',
    paused: '已暂停'
  }
  return textMap[status] || '未知'
}

// 任务相关方法
const getTaskStatusType = (status) => {
  const typeMap = {
    PENDING: 'info',
    IN_PROGRESS: 'warning',
    COMPLETED: 'success',
    PAUSED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getTaskStatusText = (status) => {
  const textMap = {
    PENDING: '待执行',
    IN_PROGRESS: '执行中',
    COMPLETED: '已完成',
    PAUSED: '已暂停'
  }
  return textMap[status] || '未知'
}

const getPriorityType = (priority) => {
  const typeMap = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return typeMap[priority] || 'info'
}

const getPriorityText = (priority) => {
  const textMap = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return textMap[priority] || '未知'
}

const getUserName = (userId) => {
  const user = availableUsers.value.find(u => u.id === userId)
  return user ? user.name : null
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return null
  return new Date(dateTime).toLocaleString('zh-CN')
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadPlanList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadPlanList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadPlanList()
}

// 任务管理方法
const loadTasks = async (planId) => {
  if (!planId) return
  
  taskLoading.value = true
  try {
    const response = await getTreatmentTasks(planId)
    allTasks.value = response.data || []
    filteredTasks.value = allTasks.value
  } catch (error) {
    console.error('加载任务列表失败，使用模拟数据:', error)
    
    // 模拟任务数据
    const mockTasks = [
      {
        id: 'task001',
        planId: planId,
        taskName: '昌平区松毛虫生物防治作业',
        description: '在昌平区森林公园进行松毛虫生物防治，释放天敌昆虫',
        assignedTo: 'user001',
        status: 'IN_PROGRESS',
        priority: 'HIGH',
        scheduledTime: '2024-01-20 08:00:00',
        actualStartTime: '2024-01-20 08:15:00',
        targetArea: '昌平区森林公园A区',
        executionNotes: '天气良好，按计划执行'
      },
      {
        id: 'task002',
        planId: planId,
        taskName: '药剂喷洒作业',
        description: '对感染区域进行生物农药喷洒',
        assignedTo: 'user002',
        status: 'PENDING',
        priority: 'MEDIUM',
        scheduledTime: '2024-01-22 09:00:00',
        targetArea: '昌平区森林公园B区'
      },
      {
        id: 'task003',
        planId: planId,
        taskName: '效果监测',
        description: '监测防治效果，记录虫口密度变化',
        assignedTo: 'user007',
        status: 'PENDING',
        priority: 'MEDIUM',
        scheduledTime: '2024-01-25 10:00:00',
        targetArea: '昌平区森林公园全区'
      }
    ]
    
    allTasks.value = mockTasks
    filteredTasks.value = mockTasks
  } finally {
    taskLoading.value = false
  }
}

const filterTasks = () => {
  let filtered = allTasks.value
  
  if (taskSearchForm.taskName) {
    filtered = filtered.filter(task => 
      task.taskName.toLowerCase().includes(taskSearchForm.taskName.toLowerCase())
    )
  }
  
  if (taskSearchForm.status) {
    filtered = filtered.filter(task => task.status === taskSearchForm.status)
  }
  
  if (taskSearchForm.priority) {
    filtered = filtered.filter(task => task.priority === taskSearchForm.priority)
  }
  
  if (taskSearchForm.assignedTo) {
    filtered = filtered.filter(task => task.assignedTo === taskSearchForm.assignedTo)
  }
  
  filteredTasks.value = filtered
}

const resetTaskFilters = () => {
  Object.keys(taskSearchForm).forEach(key => {
    taskSearchForm[key] = ''
  })
  filteredTasks.value = allTasks.value
}

const handleSaveTask = async () => {
  if (!taskFormRef.value) return
  
  await taskFormRef.value.validate(async (valid) => {
    if (valid) {
      taskSaveLoading.value = true
      try {
        if (isEditTask.value) {
          await updateTreatmentTask(currentTask.value.id, taskForm)
          const index = allTasks.value.findIndex(t => t.id === currentTask.value.id)
          if (index !== -1) {
            allTasks.value[index] = { ...allTasks.value[index], ...taskForm }
          }
          ElMessage.success('任务更新成功')
        } else {
          const response = await createTreatmentTask(currentPlan.value.id, taskForm)
          allTasks.value.push(response.data)
          ElMessage.success('任务创建成功')
        }
        
        showCreateTaskDialog.value = false
        resetTaskForm()
        filterTasks()
      } catch (apiError) {
        console.error('API调用失败，使用模拟数据:', apiError)
        
        if (isEditTask.value) {
          const index = allTasks.value.findIndex(t => t.id === currentTask.value.id)
          if (index !== -1) {
            allTasks.value[index] = { ...allTasks.value[index], ...taskForm }
          }
          ElMessage.success('任务更新成功（模拟）')
        } else {
          const newTask = {
            id: 'task' + Date.now(),
            planId: currentPlan.value.id,
            ...taskForm,
            status: 'PENDING',
            createdTime: new Date().toISOString()
          }
          allTasks.value.push(newTask)
          ElMessage.success('任务创建成功（模拟）')
        }
        
        showCreateTaskDialog.value = false
        resetTaskForm()
        filterTasks()
      } finally {
        taskSaveLoading.value = false
      }
    }
  })
}

const editTask = (task) => {
  currentTask.value = task
  Object.assign(taskForm, task)
  isEditTask.value = true
  showCreateTaskDialog.value = true
}

const assignTask = (task) => {
  currentTask.value = task
  assignForm.assignedTo = task.assignedTo || ''
  showAssignDialog.value = true
}

const handleAssignTask = async () => {
  if (!assignForm.assignedTo) {
    ElMessage.warning('请选择分配人员')
    return
  }
  
  assignLoading.value = true
  try {
    await assignTreatmentTask(currentTask.value.id, assignForm.assignedTo)
    
    const index = allTasks.value.findIndex(t => t.id === currentTask.value.id)
    if (index !== -1) {
      allTasks.value[index].assignedTo = assignForm.assignedTo
    }
    
    ElMessage.success('任务分配成功')
    showAssignDialog.value = false
    filterTasks()
  } catch (error) {
    console.error('分配任务失败，使用模拟数据:', error)
    
    const index = allTasks.value.findIndex(t => t.id === currentTask.value.id)
    if (index !== -1) {
      allTasks.value[index].assignedTo = assignForm.assignedTo
    }
    
    ElMessage.success('任务分配成功（模拟）')
    showAssignDialog.value = false
    filterTasks()
  } finally {
    assignLoading.value = false
  }
}

const deleteTask = async (task) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务"${task.taskName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    try {
      await deleteTreatmentTask(task.id)
      
      const index = allTasks.value.findIndex(t => t.id === task.id)
      if (index !== -1) {
        allTasks.value.splice(index, 1)
      }
      
      ElMessage.success('删除成功')
      filterTasks()
    } catch (apiError) {
      console.error('API删除失败，使用模拟删除:', apiError)
      
      const index = allTasks.value.findIndex(t => t.id === task.id)
      if (index !== -1) {
        allTasks.value.splice(index, 1)
      }
      
      ElMessage.success('删除成功（模拟）')
      filterTasks()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const resetTaskForm = () => {
  Object.keys(taskForm).forEach(key => {
    if (key === 'priority') {
      taskForm[key] = 'MEDIUM'
    } else {
      taskForm[key] = ''
    }
  })
  isEditTask.value = false
  currentTask.value = null
  if (taskFormRef.value) {
    taskFormRef.value.clearValidate()
  }
}

// 方案管理方法
const addMeasure = () => {
  planForm.measures.push({ type: '', description: '' })
}

const removeMeasure = (index) => {
  if (planForm.measures.length > 1) {
    planForm.measures.splice(index, 1)
  }
}

const handleSavePlan = async () => {
  if (!planFormRef.value) return
  
  await planFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        if (isEdit.value) {
          // 更新方案逻辑
          ElMessage.success('方案更新成功')
        } else {
          // 创建方案逻辑
          const newPlan = {
            id: Date.now(),
            ...planForm,
            status: 'draft',
            progress: 0,
            createTime: new Date().toLocaleString('zh-CN')
          }
          planList.value.unshift(newPlan)
          ElMessage.success('方案创建成功')
        }
        
        showCreateDialog.value = false
        resetPlanForm()
        loadPlanList()
      } catch (error) {
        console.error('保存方案失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetPlanForm = () => {
  Object.keys(planForm).forEach(key => {
    if (key === 'measures') {
      planForm[key] = [{ type: '', description: '' }]
    } else {
      planForm[key] = ''
    }
  })
  isEdit.value = false
  if (planFormRef.value) {
    planFormRef.value.clearValidate()
  }
}

const getTaskType = (status) => {
  const typeMap = {
    // 英文状态
    'completed': 'success',
    'in_progress': 'warning',
    'pending': 'info',
    'failed': 'danger',
    'paused': 'danger',
    // 中文状态
    '已完成': 'success',
    '完成': 'success',
    '进行中': 'warning',
    '执行中': 'warning',
    '待执行': 'info',
    '待处理': 'info',
    '暂未开始': 'info',
    '未开始': 'info',
    '失败': 'danger',
    '暂停': 'danger',
    '已暂停': 'danger'
  }
  return typeMap[status] || 'info'
}

// 时间线状态文本转换
const getTimelineStatusText = (status) => {
  // 如果已经是中文，直接返回
  const chineseStatuses = ['已完成', '完成', '进行中', '执行中', '待执行', '待处理', '暂未开始', '未开始', '失败', '暂停', '已暂停']
  if (chineseStatuses.includes(status)) {
    return status
  }
  
  // 英文状态转中文
  const textMap = {
    'completed': '已完成',
    'in_progress': '进行中',
    'pending': '待执行',
    'failed': '失败',
    'paused': '暂停'
  }
  
  return textMap[status] || status
}

const viewDetail = (row) => {
  currentPlan.value = row
  showDetailDialog.value = true
  loadTasks(row.id)
}

const trackProgress = (row) => {
  currentPlan.value = {
    ...row,
    tasks: [
      {
        id: 1,
        title: '方案制定完成',
        description: '防治方案已制定并审核通过',
        time: '2024-01-15 09:00',
        status: '已完成'
      },
      {
        id: 2,
        title: '物资采购',
        description: '正在采购所需的防治药剂和设备',
        time: '2024-01-16 10:30',
        status: '进行中'
      },
      {
        id: 3,
        title: '现场施药',
        description: '计划在目标区域进行施药作业',
        time: '2024-01-18 08:00',
        status: '待执行'
      }
    ]
  }
  showTrackDialog.value = true
}

const editPlan = (row) => {
  Object.assign(planForm, row)
  isEdit.value = true
  showCreateDialog.value = true
}

const deletePlan = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个防治方案吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    loadPlanList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const loadPlanList = async () => {
  loading.value = true
  try {
    // 模拟方案数据
    const allMockData = [
      {
        id: 1,
        planName: '松毛虫综合防治方案',
        pestType: 'pine_caterpillar',
        targetArea: '北京市昌平区森林公园',
        status: 'executing',
        progress: 65,
        createTime: '2024-01-15 09:00:00',
        estimatedArea: '150',
        description: '针对松毛虫的综合防治方案',
        responsible: '王建国'
      },
      {
        id: 2,
        planName: '杨树溃疡病防治方案',
        pestType: 'poplar_canker',
        targetArea: '河北省承德市林场',
        status: 'completed',
        progress: 100,
        createTime: '2024-01-10 14:30:00',
        estimatedArea: '200',
        description: '杨树溃疡病的防治方案',
        responsible: '李明华'
      },
      {
        id: 3,
        planName: '美国白蛾防治方案',
        pestType: 'american_white_moth',
        targetArea: '天津市蓟州区林场',
        status: 'draft',
        progress: 0,
        createTime: '2024-01-12 16:45:00',
        estimatedArea: '300',
        description: '针对美国白蛾的综合防治方案',
        responsible: '陈志强'
      }
    ]
    
    // 根据搜索条件过滤数据
    let filteredData = allMockData
    
    if (searchForm.planName) {
      filteredData = filteredData.filter(item => 
        item.planName.toLowerCase().includes(searchForm.planName.toLowerCase())
      )
    }
    
    if (searchForm.pestType) {
      filteredData = filteredData.filter(item => item.pestType === searchForm.pestType)
    }
    
    if (searchForm.status) {
      filteredData = filteredData.filter(item => item.status === searchForm.status)
    }
    
    pagination.total = filteredData.length
    
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    const paginatedData = filteredData.slice(startIndex, endIndex)
    
    planList.value = paginatedData
  } catch (error) {
    console.error('加载方案列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadPlanList()
})
</script>

<style lang="scss" scoped>
.treatment-plan {
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
  
  .treatment-measures {
    .measure-item {
      margin-bottom: 10px;
      padding: 10px;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      background-color: #fafafa;
    }
  }
  
  .track-content {
    .plan-info {
      margin-bottom: 20px;
    }
    
    .task-timeline {
      h4 {
        margin-bottom: 15px;
        color: #2c3e50;
      }
      
      .task-item {
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
      }
    }
  }
  
  .detail-content {
    .basic-info,
    .tasks-section {
      margin-bottom: 30px;
      
      h3 {
        margin: 0 0 15px 0;
        color: #2c3e50;
        font-size: 16px;
        font-weight: 600;
        border-bottom: 2px solid #409eff;
        padding-bottom: 8px;
      }
      
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        
        h3 {
          margin: 0;
          color: #2c3e50;
          border-bottom: none;
          padding-bottom: 0;
        }
        
        .task-actions {
          display: flex;
          gap: 8px;
        }
      }
      
      .task-filters {
        margin-bottom: 15px;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 6px;
      }
    }
  }
}
</style>