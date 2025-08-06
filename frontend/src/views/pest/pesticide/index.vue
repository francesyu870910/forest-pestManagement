<template>
  <div class="pesticide-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药剂管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加药剂
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="药剂名称">
            <el-input v-model="searchForm.name" placeholder="请输入药剂名称" clearable />
          </el-form-item>

          <el-form-item label="药剂类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option label="杀虫剂" value="insecticide" />
              <el-option label="杀菌剂" value="fungicide" />
              <el-option label="除草剂" value="herbicide" />
              <el-option label="生物农药" value="biological" />
            </el-select>
          </el-form-item>

          <el-form-item label="库存状态">
            <el-select v-model="searchForm.stockStatus" placeholder="请选择库存状态" clearable>
              <el-option label="充足" value="sufficient" />
              <el-option label="不足" value="insufficient" />
              <el-option label="缺货" value="out_of_stock" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 药剂列表 -->
      <el-table :data="pesticideList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="name" label="药剂名称" min-width="150" />

        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>

        <el-table-column prop="specification" label="规格" width="120" />

        <el-table-column prop="currentStock" label="当前库存" width="100">
          <template #default="{ row }">
            <span :class="getStockClass(row)">{{ row.currentStock }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="unit" label="单位" width="80" />

        <el-table-column prop="minStock" label="最低库存" width="100" />

        <el-table-column prop="price" label="单价(元)" width="100" />

        <el-table-column prop="supplier" label="供应商" width="120" />

        <el-table-column prop="expiryDate" label="有效期" width="120">
          <template #default="{ row }">
            <span :class="getExpiryClass(row.expiryDate)">
              {{ row.expiryDate }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewUsageRecord(row)">
              使用记录
            </el-button>
            <el-button type="success" size="small" @click="updateStock(row)"> 更新库存 </el-button>
            <el-button type="warning" size="small" @click="editPesticide(row)"> 编辑 </el-button>
            <el-button type="danger" size="small" @click="deletePesticide(row)"> 删除 </el-button>
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

    <!-- 添加/编辑药剂对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑药剂' : '添加药剂'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="pesticideForm"
        :rules="pesticideRules"
        ref="pesticideFormRef"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药剂名称" prop="name">
              <el-input v-model="pesticideForm.name" placeholder="请输入药剂名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="药剂类型" prop="type">
              <el-select v-model="pesticideForm.type" placeholder="请选择类型">
                <el-option label="杀虫剂" value="insecticide" />
                <el-option label="杀菌剂" value="fungicide" />
                <el-option label="除草剂" value="herbicide" />
                <el-option label="生物农药" value="biological" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="pesticideForm.specification" placeholder="如：500ml/瓶" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="pesticideForm.unit" placeholder="请选择单位">
                <el-option label="瓶" value="bottle" />
                <el-option label="袋" value="bag" />
                <el-option label="桶" value="barrel" />
                <el-option label="公斤" value="kg" />
                <el-option label="升" value="liter" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前库存" prop="currentStock">
              <el-input-number v-model="pesticideForm.currentStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="最低库存" prop="minStock">
              <el-input-number v-model="pesticideForm.minStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input-number
                v-model="pesticideForm.price"
                :min="0"
                :precision="2"
                style="width: 100%"
              >
                <template #append>元</template>
              </el-input-number>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="有效期" prop="expiryDate">
              <el-date-picker
                v-model="pesticideForm.expiryDate"
                type="date"
                placeholder="选择有效期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="pesticideForm.supplier" placeholder="请输入供应商名称" />
        </el-form-item>

        <el-form-item label="使用说明">
          <el-input
            v-model="pesticideForm.instructions"
            type="textarea"
            :rows="3"
            placeholder="请输入使用说明"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSavePesticide" :loading="saveLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 更新库存对话框 -->
    <el-dialog v-model="showStockDialog" title="更新库存" width="400px">
      <el-form :model="stockForm" :rules="stockRules" ref="stockFormRef" label-width="100px">
        <el-form-item label="药剂名称">
          <el-input :value="currentPesticide?.name" disabled />
        </el-form-item>

        <el-form-item label="当前库存">
          <el-input
            :value="currentPesticide?.currentStock + ' ' + currentPesticide?.unit"
            disabled
          />
        </el-form-item>

        <el-form-item label="操作类型" prop="operationType">
          <el-radio-group v-model="stockForm.operationType">
            <el-radio label="in">入库</el-radio>
            <el-radio label="out">出库</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="stockForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="stockForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showStockDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateStock" :loading="stockLoading">
          确认更新
        </el-button>
      </template>
    </el-dialog>

    <!-- 使用记录对话框 -->
    <el-dialog v-model="showUsageDialog" title="使用记录" width="800px">
      <el-table :data="usageRecords" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.operationType === 'in' ? 'success' : 'warning'">
              {{ row.operationType === 'in' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const stockLoading = ref(false)
const showAddDialog = ref(false)
const showStockDialog = ref(false)
const showUsageDialog = ref(false)
const isEdit = ref(false)
const currentPesticide = ref(null)

const searchForm = reactive({
  name: '',
  type: '',
  stockStatus: ''
})

const pesticideForm = reactive({
  name: '',
  type: '',
  specification: '',
  unit: '',
  currentStock: 0,
  minStock: 0,
  price: 0,
  supplier: '',
  expiryDate: '',
  instructions: ''
})

const stockForm = reactive({
  operationType: 'in',
  quantity: 1,
  remark: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const pesticideList = ref([])
const usageRecords = ref([])

// 表单引用
const pesticideFormRef = ref()
const stockFormRef = ref()

// 表单验证规则
const pesticideRules = {
  name: [{ required: true, message: '请输入药剂名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择药剂类型', trigger: 'change' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  supplier: [{ required: true, message: '请输入供应商', trigger: 'blur' }]
}

const stockRules = {
  operationType: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

// 方法
const getTypeText = (type) => {
  const typeMap = {
    insecticide: '杀虫剂',
    fungicide: '杀菌剂',
    herbicide: '除草剂',
    biological: '生物农药'
  }
  return typeMap[type] || '未知'
}

const getStockClass = (row) => {
  if (row.currentStock <= 0) return 'stock-out'
  if (row.currentStock <= row.minStock) return 'stock-low'
  return 'stock-normal'
}

const getExpiryClass = (expiryDate) => {
  const today = new Date()
  const expiry = new Date(expiryDate)
  const diffDays = Math.ceil((expiry - today) / (1000 * 60 * 60 * 24))

  if (diffDays < 0) return 'expired'
  if (diffDays <= 30) return 'expiring-soon'
  return 'normal'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadPesticideList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSavePesticide = async () => {
  if (!pesticideFormRef.value) return

  await pesticideFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存药剂信息
        // if (isEdit.value) {
        //   await updatePesticide(pesticideForm)
        // } else {
        //   await createPesticide(pesticideForm)
        // }

        ElMessage.success(isEdit.value ? '药剂更新成功' : '药剂添加成功')
        showAddDialog.value = false
        resetPesticideForm()
        loadPesticideList()
      } catch (error) {
        console.error('保存药剂失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetPesticideForm = () => {
  Object.keys(pesticideForm).forEach((key) => {
    if (typeof pesticideForm[key] === 'number') {
      pesticideForm[key] = 0
    } else {
      pesticideForm[key] = ''
    }
  })
  isEdit.value = false
  if (pesticideFormRef.value) {
    pesticideFormRef.value.clearValidate()
  }
}

const viewUsageRecord = async (row) => {
  currentPesticide.value = row

  // 这里应该调用API获取使用记录
  // const records = await getPesticideUsageRecords(row.id)

  // 模拟数据
  usageRecords.value = [
    {
      id: 1,
      operationType: 'in',
      quantity: 50,
      operator: '张三',
      operateTime: '2024-01-15 09:00:00',
      remark: '新采购入库'
    },
    {
      id: 2,
      operationType: 'out',
      quantity: 10,
      operator: '李四',
      operateTime: '2024-01-16 14:30:00',
      remark: '松毛虫防治使用'
    }
  ]

  showUsageDialog.value = true
}

const updateStock = (row) => {
  currentPesticide.value = row
  stockForm.operationType = 'in'
  stockForm.quantity = 1
  stockForm.remark = ''
  showStockDialog.value = true
}

const handleUpdateStock = async () => {
  if (!stockFormRef.value) return

  await stockFormRef.value.validate(async (valid) => {
    if (valid) {
      stockLoading.value = true
      try {
        // 这里应该调用API更新库存
        // await updatePesticideStock({
        //   id: currentPesticide.value.id,
        //   ...stockForm
        // })

        ElMessage.success('库存更新成功')
        showStockDialog.value = false
        loadPesticideList()
      } catch (error) {
        console.error('更新库存失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        stockLoading.value = false
      }
    }
  })
}

const editPesticide = (row) => {
  Object.assign(pesticideForm, row)
  isEdit.value = true
  showAddDialog.value = true
}

const deletePesticide = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个药剂吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除药剂
    // await deletePesticide(row.id)

    ElMessage.success('删除成功')
    loadPesticideList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadPesticideList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadPesticideList()
}

const loadPesticideList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取药剂列表
    // const response = await getPesticideList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 模拟数据
    const mockData = [
      {
        id: 1,
        name: '敌敌畏乳油',
        type: 'insecticide',
        specification: '500ml/瓶',
        currentStock: 25,
        unit: '瓶',
        minStock: 10,
        price: 35.5,
        supplier: '农药公司A',
        expiryDate: '2025-06-30',
        instructions: '稀释1000倍后喷雾使用'
      },
      {
        id: 2,
        name: '多菌灵可湿性粉剂',
        type: 'fungicide',
        specification: '1kg/袋',
        currentStock: 5,
        unit: '袋',
        minStock: 8,
        price: 28.0,
        supplier: '农药公司B',
        expiryDate: '2024-12-31',
        instructions: '稀释800倍后喷雾使用'
      }
    ]

    pesticideList.value = mockData
    pagination.total = 50
  } catch (error) {
    console.error('加载药剂列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadPesticideList()
})
</script>

<style lang="scss" scoped>
.pesticide-management {
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

  // 库存状态样式
  .stock-normal {
    color: #67c23a;
    font-weight: 600;
  }

  .stock-low {
    color: #e6a23c;
    font-weight: 600;
  }

  .stock-out {
    color: #f56c6c;
    font-weight: 600;
  }

  // 有效期状态样式
  .normal {
    color: #606266;
  }

  .expiring-soon {
    color: #e6a23c;
    font-weight: 600;
  }

  .expired {
    color: #f56c6c;
    font-weight: 600;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .pesticide-management {
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
