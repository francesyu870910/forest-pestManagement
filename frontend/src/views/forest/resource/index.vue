<template>
  <div class="forest-resource">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>森林资源管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加资源
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="资源名称">
            <el-input v-model="searchForm.name" placeholder="请输入资源名称" clearable />
          </el-form-item>

          <el-form-item label="资源类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option label="天然林" value="natural_forest" />
              <el-option label="人工林" value="artificial_forest" />
              <el-option label="经济林" value="economic_forest" />
              <el-option label="防护林" value="protection_forest" />
            </el-select>
          </el-form-item>

          <el-form-item label="所在区域">
            <el-input v-model="searchForm.region" placeholder="请输入区域" clearable />
          </el-form-item>

          <el-form-item label="健康状态">
            <el-select v-model="searchForm.healthStatus" placeholder="请选择健康状态" clearable>
              <el-option label="健康" value="healthy" />
              <el-option label="亚健康" value="sub_healthy" />
              <el-option label="病害" value="diseased" />
              <el-option label="严重病害" value="severely_diseased" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 资源列表 -->
      <el-table :data="resourceList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="name" label="资源名称" min-width="150" />

        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>

        <el-table-column prop="region" label="所在区域" width="120" />

        <el-table-column prop="area" label="面积(公顷)" width="100" />

        <el-table-column prop="treeSpecies" label="主要树种" width="120" />

        <el-table-column prop="plantingYear" label="种植年份" width="100" />

        <el-table-column prop="healthStatus" label="健康状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthStatusType(row.healthStatus)">
              {{ getHealthStatusText(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="lastInspection" label="最后巡检" width="120" />

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row)"> 详情 </el-button>
              <el-button type="success" size="small" @click="viewOnMap(row)"> 地图 </el-button>
              <el-button type="warning" size="small" @click="editResource(row)"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="deleteResource(row)"> 删除 </el-button>
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

    <!-- 添加/编辑资源对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑资源' : '添加资源'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="resourceForm"
        :rules="resourceRules"
        ref="resourceFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资源名称" prop="name">
              <el-input v-model="resourceForm.name" placeholder="请输入资源名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="资源类型" prop="type">
              <el-select v-model="resourceForm.type" placeholder="请选择类型">
                <el-option label="天然林" value="natural_forest" />
                <el-option label="人工林" value="artificial_forest" />
                <el-option label="经济林" value="economic_forest" />
                <el-option label="防护林" value="protection_forest" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在区域" prop="region">
              <el-input v-model="resourceForm.region" placeholder="请输入所在区域" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="面积" prop="area">
              <el-input-number
                v-model="resourceForm.area"
                :min="0"
                :precision="2"
                style="width: 100%"
              >
                <template #append>公顷</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主要树种" prop="treeSpecies">
              <el-input v-model="resourceForm.treeSpecies" placeholder="请输入主要树种" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="种植年份" prop="plantingYear">
              <el-date-picker
                v-model="resourceForm.plantingYear"
                type="year"
                placeholder="选择种植年份"
                format="YYYY"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="健康状态" prop="healthStatus">
              <el-select v-model="resourceForm.healthStatus" placeholder="请选择健康状态">
                <el-option label="健康" value="healthy" />
                <el-option label="亚健康" value="sub_healthy" />
                <el-option label="病害" value="diseased" />
                <el-option label="严重病害" value="severely_diseased" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="管理负责人" prop="manager">
              <el-input v-model="resourceForm.manager" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地理坐标">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-input v-model="resourceForm.latitude" placeholder="纬度" type="number">
                <template #prepend>纬度</template>
              </el-input>
            </el-col>
            <el-col :span="12">
              <el-input v-model="resourceForm.longitude" placeholder="经度" type="number">
                <template #prepend>经度</template>
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="资源描述">
          <el-input
            v-model="resourceForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          />
        </el-form-item>

        <el-form-item label="备注信息">
          <el-input
            v-model="resourceForm.remarks"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveResource" :loading="saveLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="资源详情" width="800px">
      <div v-if="currentResource" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资源名称">{{ currentResource.name }}</el-descriptions-item>
          <el-descriptions-item label="资源类型">{{
            getTypeText(currentResource.type)
          }}</el-descriptions-item>
          <el-descriptions-item label="所在区域">{{ currentResource.region }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ currentResource.area }} 公顷</el-descriptions-item>
          <el-descriptions-item label="主要树种">{{
            currentResource.treeSpecies
          }}</el-descriptions-item>
          <el-descriptions-item label="种植年份">{{
            currentResource.plantingYear
          }}</el-descriptions-item>
          <el-descriptions-item label="健康状态">
            <el-tag :type="getHealthStatusType(currentResource.healthStatus)">
              {{ getHealthStatusText(currentResource.healthStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="管理负责人">{{
            currentResource.manager
          }}</el-descriptions-item>
          <el-descriptions-item label="最后巡检">{{
            currentResource.lastInspection
          }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            currentResource.createTime
          }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentResource.latitude && currentResource.longitude" class="coordinates-section">
          <h4>地理位置</h4>
          <p>纬度: {{ currentResource.latitude }}</p>
          <p>经度: {{ currentResource.longitude }}</p>
        </div>

        <div v-if="currentResource.description" class="description-section">
          <h4>资源描述</h4>
          <p>{{ currentResource.description }}</p>
        </div>

        <div v-if="currentResource.remarks" class="remarks-section">
          <h4>备注信息</h4>
          <p>{{ currentResource.remarks }}</p>
        </div>

        <div class="history-section">
          <h4>巡检历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="record in currentResource.inspectionHistory"
              :key="record.id"
              :timestamp="record.date"
              :type="getInspectionType(record.status)"
            >
              <div class="inspection-item">
                <h5>{{ record.title }}</h5>
                <p>{{ record.description }}</p>
                <el-tag size="small" :type="getInspectionType(record.status)">
                  {{ record.status }}
                </el-tag>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>

    <!-- 地图查看对话框 -->
    <el-dialog v-model="showMapDialog" title="森林资源地图" width="1200px">
      <div class="map-container">
        <ForestMap
          v-if="showMapDialog && currentResource"
          :center="[currentResource.longitude || 116.397428, currentResource.latitude || 39.90923]"
          :zoom="15"
          :forest-data="[currentResource]"
          :enable-measure="true"
          :enable-drawing="false"
          :enable-data-import="false"
          :show-controls="false"
          height="600px"
          @marker-click="handleMapMarkerClick"
          @map-ready="handleMapReady"
          @error="handleMapError"
        />
        
        <!-- 资源信息面板 -->
        <div class="resource-info-panel">
          <el-card v-if="currentResource" class="info-card">
            <template #header>
              <div class="info-header">
                <span>{{ currentResource.name }}</span>
                <el-tag :type="getHealthStatusType(currentResource.healthStatus)">
                  {{ getHealthStatusText(currentResource.healthStatus) }}
                </el-tag>
              </div>
            </template>
            
            <div class="info-content">
              <div class="info-item">
                <span class="label">资源类型：</span>
                <span class="value">{{ getTypeText(currentResource.type) }}</span>
              </div>
              <div class="info-item">
                <span class="label">所在区域：</span>
                <span class="value">{{ currentResource.region }}</span>
              </div>
              <div class="info-item">
                <span class="label">面积：</span>
                <span class="value">{{ currentResource.area }} 公顷</span>
              </div>
              <div class="info-item">
                <span class="label">树种：</span>
                <span class="value">{{ currentResource.species }}</span>
              </div>
              <div class="info-item">
                <span class="label">林龄：</span>
                <span class="value">{{ currentResource.age }} 年</span>
              </div>
              <div class="info-item">
                <span class="label">管理员：</span>
                <span class="value">{{ currentResource.manager }}</span>
              </div>
              <div class="info-item">
                <span class="label">最后检查：</span>
                <span class="value">{{ currentResource.lastInspection }}</span>
              </div>
            </div>
            
            <div class="info-actions">
              <el-button type="primary" size="small" @click="editResource(currentResource)">
                编辑资源
              </el-button>
              <el-button type="success" size="small" @click="viewDetail(currentResource)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import ForestMap from '@/components/Map/ForestMap.vue'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const showMapDialog = ref(false)
const isEdit = ref(false)
const currentResource = ref(null)

const searchForm = reactive({
  name: '',
  type: '',
  region: '',
  healthStatus: ''
})

const resourceForm = reactive({
  name: '',
  type: '',
  region: '',
  area: 0,
  treeSpecies: '',
  plantingYear: '',
  healthStatus: '',
  manager: '',
  latitude: 0,
  longitude: 0,
  description: '',
  remarks: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const resourceList = ref([])

// 表单引用
const resourceFormRef = ref()

// 表单验证规则
const resourceRules = {
  name: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  region: [{ required: true, message: '请输入所在区域', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
  treeSpecies: [{ required: true, message: '请输入主要树种', trigger: 'blur' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }],
  manager: [{ required: true, message: '请输入管理负责人', trigger: 'blur' }]
}

// 方法
const getTypeText = (type) => {
  const typeMap = {
    natural_forest: '天然林',
    artificial_forest: '人工林',
    economic_forest: '经济林',
    protection_forest: '防护林'
  }
  return typeMap[type] || '未知'
}

const getHealthStatusType = (status) => {
  const typeMap = {
    healthy: 'success',
    sub_healthy: 'warning',
    diseased: 'danger',
    severely_diseased: 'danger'
  }
  return typeMap[status] || 'info'
}

const getHealthStatusText = (status) => {
  const textMap = {
    healthy: '健康',
    sub_healthy: '亚健康',
    diseased: '病害',
    severely_diseased: '严重病害'
  }
  return textMap[status] || '未知'
}

const getInspectionType = (status) => {
  const typeMap = {
    正常: 'success',
    异常: 'warning',
    严重: 'danger'
  }
  return typeMap[status] || 'info'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadResourceList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSaveResource = async () => {
  if (!resourceFormRef.value) return

  await resourceFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存资源
        // if (isEdit.value) {
        //   await updateForestResource(resourceForm)
        // } else {
        //   await createForestResource(resourceForm)
        // }

        ElMessage.success(isEdit.value ? '资源更新成功' : '资源添加成功')
        showAddDialog.value = false
        resetResourceForm()
        loadResourceList()
      } catch (error) {
        console.error('保存资源失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetResourceForm = () => {
  Object.keys(resourceForm).forEach((key) => {
    if (typeof resourceForm[key] === 'number') {
      resourceForm[key] = 0
    } else {
      resourceForm[key] = ''
    }
  })
  // 确保坐标字段为数字类型
  resourceForm.latitude = 0
  resourceForm.longitude = 0
  
  isEdit.value = false
  if (resourceFormRef.value) {
    resourceFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentResource.value = {
    ...row,
    inspectionHistory: [
      {
        id: 1,
        title: '定期巡检',
        description: '森林资源状况良好，无明显病虫害',
        date: '2024-01-15 09:00',
        status: '正常'
      },
      {
        id: 2,
        title: '病虫害检查',
        description: '发现少量松毛虫，已采取防治措施',
        date: '2024-01-10 14:30',
        status: '异常'
      }
    ]
  }
  showDetailDialog.value = true
}

const viewOnMap = (row) => {
  console.log('查看地图 - 资源数据:', row)
  console.log('环境变量检查:', {
    VITE_TIANDITU_API_KEY: import.meta.env.VITE_TIANDITU_API_KEY,
    NODE_ENV: import.meta.env.NODE_ENV,
    MODE: import.meta.env.MODE
  })
  
  // 确保资源有坐标信息，如果没有则使用默认坐标
  if (!row.longitude || !row.latitude) {
    // 为演示目的，给不同类型的资源分配不同的默认坐标
    const defaultCoords = getDefaultCoordinates(row.type, row.id)
    row.longitude = defaultCoords.longitude
    row.latitude = defaultCoords.latitude
    console.log('使用默认坐标:', defaultCoords)
  }
  
  console.log('最终资源坐标:', {
    longitude: row.longitude,
    latitude: row.latitude,
    name: row.name
  })
  
  currentResource.value = row
  showMapDialog.value = true
  console.log('地图对话框已打开，当前资源:', currentResource.value)
}

// 地图相关方法
const handleMapMarkerClick = (feature, event) => {
  console.log('地图标记点击:', feature, event)
  // 可以在这里处理标记点击事件
}

const handleMapReady = (map) => {
  console.log('地图加载完成:', map)
  // 地图加载完成后的处理
}

const handleMapError = (error) => {
  console.error('地图加载错误:', error)
  ElMessage.error(`地图加载失败: ${error.message}`)
}

// 为不同类型的资源分配默认坐标（演示用）
const getDefaultCoordinates = (type, id) => {
  const baseCoords = {
    natural_forest: { longitude: 116.397428, latitude: 39.90923 },
    artificial_forest: { longitude: 116.407428, latitude: 39.91923 },
    economic_forest: { longitude: 116.387428, latitude: 39.89923 },
    protection_forest: { longitude: 116.417428, latitude: 39.92923 }
  }
  
  const base = baseCoords[type] || baseCoords.natural_forest
  
  // 根据ID添加一些随机偏移，模拟不同位置
  const offset = (parseInt(id) || 1) * 0.001
  
  return {
    longitude: base.longitude + offset,
    latitude: base.latitude + offset
  }
}

const editResource = (row) => {
  Object.assign(resourceForm, row)
  isEdit.value = true
  showAddDialog.value = true
}

const deleteResource = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个森林资源吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除资源
    // await deleteForestResource(row.id)

    ElMessage.success('删除成功')
    loadResourceList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadResourceList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadResourceList()
}

const loadResourceList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取资源列表
    // const response = await getForestResourceList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 完整的模拟数据
    const allMockData = [
      {
        id: 1,
        name: '昌平森林公园A区',
        type: 'natural_forest',
        region: '北京市昌平区',
        area: 150.5,
        treeSpecies: '松树、柏树',
        plantingYear: '1995',
        healthStatus: 'healthy',
        manager: '张三',
        lastInspection: '2024-01-15',
        createTime: '2023-01-01 10:00:00',
        latitude: 40.2181,
        longitude: 116.2432,
        description: '天然松柏混交林，生态环境良好',
        remarks: '重点保护区域'
      },
      {
        id: 2,
        name: '承德人工林基地',
        type: 'artificial_forest',
        region: '河北省承德市',
        area: 200.0,
        treeSpecies: '杨树',
        plantingYear: '2010',
        healthStatus: 'sub_healthy',
        manager: '李四',
        lastInspection: '2024-01-12',
        createTime: '2023-01-01 10:00:00',
        latitude: 40.9544,
        longitude: 117.9382,
        description: '人工种植杨树林，用于木材生产',
        remarks: '需要加强病虫害防治'
      },
      {
        id: 3,
        name: '天津蓟州经济林',
        type: 'economic_forest',
        region: '天津市蓟州区',
        area: 120.8,
        treeSpecies: '苹果、梨树',
        plantingYear: '2008',
        healthStatus: 'healthy',
        manager: '王五',
        lastInspection: '2024-01-10',
        createTime: '2023-01-01 10:00:00',
        latitude: 40.0415,
        longitude: 117.4073,
        description: '经济果树林，主要生产水果',
        remarks: '产量稳定，效益良好'
      },
      {
        id: 4,
        name: '济南防护林带',
        type: 'protection_forest',
        region: '山东省济南市',
        area: 180.3,
        treeSpecies: '槐树、柳树',
        plantingYear: '2005',
        healthStatus: 'diseased',
        manager: '赵六',
        lastInspection: '2024-01-08',
        createTime: '2023-01-01 10:00:00',
        latitude: 36.6512,
        longitude: 117.1201,
        description: '城市防护林带，防风固沙',
        remarks: '发现病虫害，需要及时处理'
      },
      {
        id: 5,
        name: '南京紫金山林区',
        type: 'natural_forest',
        region: '江苏省南京市',
        area: 300.2,
        treeSpecies: '梧桐、银杏',
        plantingYear: '1980',
        healthStatus: 'healthy',
        manager: '孙七',
        lastInspection: '2024-01-05',
        createTime: '2023-01-01 10:00:00',
        latitude: 32.0603,
        longitude: 118.8014,
        description: '城市森林公园，生态价值高',
        remarks: '重要的城市绿肺'
      },
      {
        id: 6,
        name: '杭州西湖竹林',
        type: 'economic_forest',
        region: '浙江省杭州市',
        area: 80.5,
        treeSpecies: '毛竹',
        plantingYear: '2012',
        healthStatus: 'sub_healthy',
        manager: '周八',
        lastInspection: '2024-01-03',
        createTime: '2023-01-01 10:00:00',
        latitude: 30.2741,
        longitude: 120.1551,
        description: '经济竹林，用于竹制品生产',
        remarks: '需要适当间伐'
      },
      {
        id: 7,
        name: '合肥人工松林',
        type: 'artificial_forest',
        region: '安徽省合肥市',
        area: 160.7,
        treeSpecies: '马尾松',
        plantingYear: '2015',
        healthStatus: 'healthy',
        manager: '吴九',
        lastInspection: '2024-01-01',
        createTime: '2023-01-01 10:00:00',
        latitude: 31.8206,
        longitude: 117.2272,
        description: '人工种植松林，生长良好',
        remarks: '定期抚育管理'
      },
      {
        id: 8,
        name: '武汉东湖防护林',
        type: 'protection_forest',
        region: '湖北省武汉市',
        area: 220.1,
        treeSpecies: '水杉、柳树',
        plantingYear: '2000',
        healthStatus: 'severely_diseased',
        manager: '郑十',
        lastInspection: '2023-12-28',
        createTime: '2023-01-01 10:00:00',
        latitude: 30.5728,
        longitude: 114.3048,
        description: '湖泊防护林，调节水质',
        remarks: '病害严重，急需治理'
      },
      {
        id: 9,
        name: '广州白云山林区',
        type: 'natural_forest',
        region: '广东省广州市',
        area: 280.9,
        treeSpecies: '榕树、木棉',
        plantingYear: '1990',
        healthStatus: 'healthy',
        manager: '冯十一',
        lastInspection: '2023-12-25',
        createTime: '2023-01-01 10:00:00',
        latitude: 23.1291,
        longitude: 113.2644,
        description: '南方天然林，物种丰富',
        remarks: '生态保护重点区域'
      },
      {
        id: 10,
        name: '成都青城山后山',
        type: 'economic_forest',
        region: '四川省成都市',
        area: 190.6,
        treeSpecies: '楠木林、桢楠',
        plantingYear: '2018',
        healthStatus: 'healthy',
        manager: '陈丽',
        lastInspection: '2023-12-20',
        createTime: '2023-01-01 10:00:00',
        latitude: 30.9003,
        longitude: 103.5670,
        description: '旅游观光竹林，景观价值高',
        remarks: '兼具经济和生态效益'
      }
    ]

    // 根据搜索条件过滤数据
    let filteredData = allMockData

    // 按资源名称过滤
    if (searchForm.name) {
      filteredData = filteredData.filter((item) =>
        item.name.toLowerCase().includes(searchForm.name.toLowerCase())
      )
    }

    // 按资源类型过滤
    if (searchForm.type) {
      filteredData = filteredData.filter((item) => item.type === searchForm.type)
    }

    // 按地区过滤
    if (searchForm.region) {
      filteredData = filteredData.filter((item) =>
        item.region.toLowerCase().includes(searchForm.region.toLowerCase())
      )
    }

    // 按健康状态过滤
    if (searchForm.healthStatus) {
      filteredData = filteredData.filter((item) => item.healthStatus === searchForm.healthStatus)
    }

    // 更新总数
    pagination.total = filteredData.length

    // 分页处理
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize
    const endIndex = startIndex + pagination.pageSize
    const paginatedData = filteredData.slice(startIndex, endIndex)

    resourceList.value = paginatedData
  } catch (error) {
    console.error('加载资源列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadResourceList()
})
</script>

<style lang="scss" scoped>
.forest-resource {
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
    .coordinates-section,
    .description-section,
    .remarks-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      p {
        color: #606266;
        line-height: 1.6;
        margin: 5px 0;
        padding: 10px;
        background-color: #f8f9fa;
        border-radius: 4px;
      }
    }

    .history-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 15px;
      }

      .inspection-item {
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

  .map-container {
    position: relative;
    display: flex;
    gap: 20px;
    
    .forest-map {
      flex: 1;
      min-height: 600px;
    }
    
    .resource-info-panel {
      width: 300px;
      flex-shrink: 0;
      
      .info-card {
        height: fit-content;
        
        .info-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          span {
            font-weight: 600;
            color: #2c3e50;
          }
        }
        
        .info-content {
          .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
            
            &:last-child {
              border-bottom: none;
            }
            
            .label {
              color: #606266;
              font-size: 14px;
              min-width: 80px;
            }
            
            .value {
              color: #2c3e50;
              font-size: 14px;
              font-weight: 500;
              text-align: right;
            }
          }
        }
        
        .info-actions {
          margin-top: 16px;
          display: flex;
          gap: 8px;
          
          .el-button {
            flex: 1;
          }
        }
      }
    }
  }
  
  // 响应式设计
  @media (max-width: 1024px) {
    .map-container {
      flex-direction: column;
      
      .resource-info-panel {
        width: 100%;
        order: -1;
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
  .forest-resource {
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
