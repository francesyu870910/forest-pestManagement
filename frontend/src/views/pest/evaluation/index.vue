<template>
  <div class="effect-evaluation">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>效果评估</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            新增评估
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="方案名称">
            <el-input v-model="searchForm.planName" placeholder="请输入方案名称" clearable />
          </el-form-item>

          <el-form-item label="评估时间">
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

          <el-form-item label="评估结果">
            <el-select v-model="searchForm.result" placeholder="请选择评估结果" clearable>
              <el-option label="优秀" value="excellent" />
              <el-option label="良好" value="good" />
              <el-option label="一般" value="average" />
              <el-option label="较差" value="poor" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 评估列表 -->
      <el-table :data="evaluationList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="planName" label="方案名称" min-width="150" />

        <el-table-column prop="evaluationDate" label="评估时间" width="120" />

        <el-table-column prop="effectiveness" label="防治效果" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.effectiveness" :stroke-width="6" />
          </template>
        </el-table-column>

        <el-table-column prop="pestDensityReduction" label="病虫害密度降低率" width="140">
          <template #default="{ row }">
            <span>{{ row.pestDensityReduction }}%</span>
          </template>
        </el-table-column>

        <el-table-column prop="damageAreaReduction" label="受害面积减少率" width="140">
          <template #default="{ row }">
            <span>{{ row.damageAreaReduction }}%</span>
          </template>
        </el-table-column>

        <el-table-column prop="plantRecoveryDegree" label="植物恢复程度" width="120">
          <template #default="{ row }">
            <el-tag :type="getRecoveryType(row.plantRecoveryDegree)">
              {{ getRecoveryText(row.plantRecoveryDegree) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="result" label="评估结果" width="100">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.result)">
              {{ getResultText(row.result) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="evaluator" label="评估人" width="100" />

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row)"> 详情 </el-button>
              <el-button type="success" size="small" @click="generateReport(row)"> 报告 </el-button>
              <el-button type="warning" size="small" @click="editEvaluation(row)"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="deleteEvaluation(row)">
                删除
              </el-button>
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

    <!-- 创建/编辑评估对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="isEdit ? '编辑评估' : '新增评估'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="evaluationForm"
        :rules="evaluationRules"
        ref="evaluationFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联方案" prop="planId">
              <el-select
                v-model="evaluationForm.planId"
                placeholder="请选择防治方案"
                style="width: 100%"
              >
                <el-option
                  v-for="plan in treatmentPlans"
                  :key="plan.id"
                  :label="plan.name"
                  :value="plan.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="评估时间" prop="evaluationDate">
              <el-date-picker
                v-model="evaluationForm.evaluationDate"
                type="date"
                placeholder="选择评估时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="防治前图片" prop="beforeImages">
          <el-upload
            ref="beforeUploadRef"
            :file-list="evaluationForm.beforeImages"
            :auto-upload="false"
            :on-change="handleBeforeImageChange"
            :on-remove="handleBeforeImageRemove"
            accept="image/*"
            list-type="picture-card"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="防治后图片" prop="afterImages">
          <el-upload
            ref="afterUploadRef"
            :file-list="evaluationForm.afterImages"
            :auto-upload="false"
            :on-change="handleAfterImageChange"
            :on-remove="handleAfterImageRemove"
            accept="image/*"
            list-type="picture-card"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="防治效果" prop="effectiveness">
              <el-slider
                v-model="evaluationForm.effectiveness"
                :min="0"
                :max="100"
                show-input
                :format-tooltip="formatTooltip"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="评估结果" prop="result">
              <el-select v-model="evaluationForm.result" placeholder="请选择评估结果">
                <el-option label="优秀" value="excellent" />
                <el-option label="良好" value="good" />
                <el-option label="一般" value="average" />
                <el-option label="较差" value="poor" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="评估指标">
          <div class="evaluation-metrics">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="病虫害密度降低率">
                  <el-input-number
                    v-model="evaluationForm.metrics.densityReduction"
                    :min="0"
                    :max="100"
                    :precision="1"
                    style="width: 100%"
                  >
                    <template #append>%</template>
                  </el-input-number>
                </el-form-item>
              </el-col>

              <el-col :span="8">
                <el-form-item label="受害面积减少率">
                  <el-input-number
                    v-model="evaluationForm.metrics.areaReduction"
                    :min="0"
                    :max="100"
                    :precision="1"
                    style="width: 100%"
                  >
                    <template #append>%</template>
                  </el-input-number>
                </el-form-item>
              </el-col>

              <el-col :span="8">
                <el-form-item label="植物恢复程度">
                  <el-input-number
                    v-model="evaluationForm.metrics.recoveryRate"
                    :min="0"
                    :max="100"
                    :precision="1"
                    style="width: 100%"
                  >
                    <template #append>%</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-form-item>

        <el-form-item label="评估说明">
          <el-input
            v-model="evaluationForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入评估说明"
          />
        </el-form-item>

        <el-form-item label="改进建议">
          <el-input
            v-model="evaluationForm.suggestions"
            type="textarea"
            :rows="3"
            placeholder="请输入改进建议"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEvaluation" :loading="saveLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="评估详情" width="900px">
      <div v-if="currentEvaluation" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="方案名称">{{
            currentEvaluation.planName
          }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{
            currentEvaluation.evaluationDate
          }}</el-descriptions-item>
          <el-descriptions-item label="防治效果">
            <el-progress :percentage="currentEvaluation.effectiveness" />
          </el-descriptions-item>
          <el-descriptions-item label="评估结果">
            <el-tag :type="getResultType(currentEvaluation.result)">
              {{ getResultText(currentEvaluation.result) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评估人">{{
            currentEvaluation.evaluator
          }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{
            currentEvaluation.createTime
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="metrics-section">
          <h4>评估指标</h4>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-label">病虫害密度降低率</div>
                <div class="metric-value">
                  {{ currentEvaluation.metrics?.densityReduction || 0 }}%
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-label">受害面积减少率</div>
                <div class="metric-value">{{ currentEvaluation.metrics?.areaReduction || 0 }}%</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-label">植物恢复程度</div>
                <div class="metric-value">{{ currentEvaluation.metrics?.recoveryRate || 0 }}%</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="images-section">
          <h4>对比图片</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <h5>防治前</h5>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in currentEvaluation.beforeImages"
                  :key="index"
                  :src="img"
                  :preview-src-list="currentEvaluation.beforeImages"
                  fit="cover"
                  class="gallery-image"
                />
              </div>
            </el-col>
            <el-col :span="12">
              <h5>防治后</h5>
              <div class="image-gallery">
                <el-image
                  v-for="(img, index) in currentEvaluation.afterImages"
                  :key="index"
                  :src="img"
                  :preview-src-list="currentEvaluation.afterImages"
                  fit="cover"
                  class="gallery-image"
                />
              </div>
            </el-col>
          </el-row>
        </div>

        <div v-if="currentEvaluation.description" class="description-section">
          <h4>评估说明</h4>
          <p>{{ currentEvaluation.description }}</p>
        </div>

        <div v-if="currentEvaluation.suggestions" class="suggestions-section">
          <h4>改进建议</h4>
          <p>{{ currentEvaluation.suggestions }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 报告生成对话框 -->
    <el-dialog v-model="showReportDialog" title="效果评估报告" width="1000px">
      <div v-if="reportData" class="report-content" v-loading="reportLoading">
        <!-- 报告头部 -->
        <div class="report-header">
          <h2>{{ reportData.title }}</h2>
          <div class="report-meta">
            <p><strong>生成时间：</strong>{{ reportData.generateTime }}</p>
            <p><strong>评估方案：</strong>{{ reportData.planName }}</p>
            <p><strong>评估时间：</strong>{{ reportData.evaluationDate }}</p>
          </div>
        </div>

        <!-- 报告摘要 -->
        <div class="report-section">
          <h3>评估摘要</h3>
          <div class="summary-content">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">总体评级</div>
                  <div class="summary-value" :class="getResultClass(reportData.overallRating)">
                    {{ getResultText(reportData.overallRating) }}
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">防治效果</div>
                  <div class="summary-value">{{ reportData.effectivenessScore }}%</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">成本效益</div>
                  <div class="summary-value">{{ reportData.costEffectiveness }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">环境影响</div>
                  <div class="summary-value">{{ reportData.environmentalImpact }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 详细分析 -->
        <div class="report-section">
          <h3>详细分析</h3>
          <div class="analysis-content">
            <h4>防治效果分析</h4>
            <p>{{ reportData.effectivenessAnalysis }}</p>
            
            <h4>对比分析</h4>
            <div class="comparison-charts">
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="chart-container">
                    <h5>防治前后对比</h5>
                    <div class="before-after-comparison">
                      <div class="comparison-item">
                        <span>防治前虫口密度：</span>
                        <span class="value">{{ reportData.beforeDensity }} 头/株</span>
                      </div>
                      <div class="comparison-item">
                        <span>防治后虫口密度：</span>
                        <span class="value">{{ reportData.afterDensity }} 头/株</span>
                      </div>
                      <div class="comparison-item highlight">
                        <span>防治效果：</span>
                        <span class="value">{{ reportData.reductionRate }}%</span>
                      </div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="chart-container">
                    <h5>受害程度变化</h5>
                    <div class="damage-comparison">
                      <div class="damage-item">
                        <span>轻度受害：</span>
                        <span class="value">{{ reportData.lightDamage }}%</span>
                      </div>
                      <div class="damage-item">
                        <span>中度受害：</span>
                        <span class="value">{{ reportData.moderateDamage }}%</span>
                      </div>
                      <div class="damage-item">
                        <span>重度受害：</span>
                        <span class="value">{{ reportData.severeDamage }}%</span>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <!-- 结论与建议 -->
        <div class="report-section">
          <h3>结论与建议</h3>
          <div class="conclusion-content">
            <h4>评估结论</h4>
            <p>{{ reportData.conclusion }}</p>
            
            <h4>改进建议</h4>
            <ul>
              <li v-for="(suggestion, index) in reportData.suggestions" :key="index">
                {{ suggestion }}
              </li>
            </ul>
            
            <h4>后续监测建议</h4>
            <p>{{ reportData.monitoringAdvice }}</p>
          </div>
        </div>

        <!-- 附件信息 -->
        <div class="report-section">
          <h3>附件信息</h3>
          <div class="attachments">
            <div class="image-gallery">
              <h4>防治前后对比图</h4>
              <el-row :gutter="10">
                <el-col :span="12">
                  <div class="image-item">
                    <h5>防治前</h5>
                    <el-image
                      v-for="(img, index) in reportData.beforeImages"
                      :key="index"
                      :src="img"
                      fit="cover"
                      style="width: 100%; height: 150px; margin-bottom: 10px;"
                    />
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="image-item">
                    <h5>防治后</h5>
                    <el-image
                      v-for="(img, index) in reportData.afterImages"
                      :key="index"
                      :src="img"
                      fit="cover"
                      style="width: 100%; height: 150px; margin-bottom: 10px;"
                    />
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="report-actions">
          <el-button @click="showReportDialog = false">关闭</el-button>
          <el-button type="primary" @click="downloadReport" :loading="reportLoading">
            <el-icon><Download /></el-icon>
            下载报告
          </el-button>
          <el-button type="success" @click="printReport">
            <el-icon><Printer /></el-icon>
            打印报告
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Printer } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showReportDialog = ref(false)
const isEdit = ref(false)
const currentEvaluation = ref(null)
const reportData = ref(null)
const reportLoading = ref(false)

const searchForm = reactive({
  planName: '',
  dateRange: [],
  result: ''
})

const evaluationForm = reactive({
  planId: '',
  evaluationDate: '',
  beforeImages: [],
  afterImages: [],
  effectiveness: 0,
  result: '',
  metrics: {
    densityReduction: 0,
    areaReduction: 0,
    recoveryRate: 0
  },
  description: '',
  suggestions: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const evaluationList = ref([])
const treatmentPlans = ref([])

// 表单引用
const evaluationFormRef = ref()
const beforeUploadRef = ref()
const afterUploadRef = ref()

// 表单验证规则
const evaluationRules = {
  planId: [{ required: true, message: '请选择防治方案', trigger: 'change' }],
  evaluationDate: [{ required: true, message: '请选择评估时间', trigger: 'change' }],
  result: [{ required: true, message: '请选择评估结果', trigger: 'change' }]
}

// 方法
const getResultType = (result) => {
  const typeMap = {
    excellent: 'success',
    good: 'success',
    average: 'warning',
    poor: 'danger'
  }
  return typeMap[result] || 'info'
}

const getResultText = (result) => {
  const textMap = {
    excellent: '优秀',
    good: '良好',
    average: '一般',
    poor: '较差'
  }
  return textMap[result] || '未知'
}

const formatTooltip = (value) => {
  return `${value}%`
}

const getRecoveryType = (degree) => {
  const typeMap = {
    excellent: 'success',
    good: 'success',
    average: 'warning',
    poor: 'danger'
  }
  return typeMap[degree] || 'info'
}

const getRecoveryText = (degree) => {
  const textMap = {
    excellent: '优秀',
    good: '良好',
    average: '一般',
    poor: '较差'
  }
  return textMap[degree] || '未知'
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadEvaluationList()
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

const handleBeforeImageChange = (file, fileList) => {
  evaluationForm.beforeImages = fileList
}

const handleBeforeImageRemove = (file, fileList) => {
  evaluationForm.beforeImages = fileList
}

const handleAfterImageChange = (file, fileList) => {
  evaluationForm.afterImages = fileList
}

const handleAfterImageRemove = (file, fileList) => {
  evaluationForm.afterImages = fileList
}

const handleSaveEvaluation = async () => {
  if (!evaluationFormRef.value) return

  await evaluationFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存评估
        // if (isEdit.value) {
        //   await updateEvaluation(evaluationForm)
        // } else {
        //   await createEvaluation(evaluationForm)
        // }

        ElMessage.success(isEdit.value ? '评估更新成功' : '评估创建成功')
        showCreateDialog.value = false
        resetEvaluationForm()
        loadEvaluationList()
      } catch (error) {
        console.error('保存评估失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetEvaluationForm = () => {
  Object.keys(evaluationForm).forEach((key) => {
    if (key === 'beforeImages' || key === 'afterImages') {
      evaluationForm[key] = []
    } else if (key === 'metrics') {
      evaluationForm[key] = {
        densityReduction: 0,
        areaReduction: 0,
        recoveryRate: 0
      }
    } else if (typeof evaluationForm[key] === 'number') {
      evaluationForm[key] = 0
    } else {
      evaluationForm[key] = ''
    }
  })
  isEdit.value = false
  if (evaluationFormRef.value) {
    evaluationFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentEvaluation.value = row
  showDetailDialog.value = true
}

const generateReport = (row) => {
  currentEvaluation.value = row
  showReportDialog.value = true
  generateReportData()
}

// 生成报告数据
const generateReportData = async () => {
  reportLoading.value = true
  try {
    // 这里应该调用API生成报告数据
    // const response = await generateEvaluationReport(currentEvaluation.value.id)
    
    // 模拟报告数据生成
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    reportData.value = {
      title: `${currentEvaluation.value.planName} - 效果评估报告`,
      generateTime: new Date().toLocaleString('zh-CN'),
      planName: currentEvaluation.value.planName,
      evaluationDate: currentEvaluation.value.evaluationDate,
      overallRating: currentEvaluation.value.result,
      effectivenessScore: Math.floor(Math.random() * 20) + 80, // 80-100%
      costEffectiveness: '良好',
      environmentalImpact: '低',
      effectivenessAnalysis: `本次防治方案针对${currentEvaluation.value.planName}实施效果显著。通过对比防治前后的虫口密度、受害程度等关键指标，防治效果达到预期目标。防治措施科学合理，执行到位，取得了良好的防治效果。`,
      beforeDensity: Math.floor(Math.random() * 50) + 100, // 100-150
      afterDensity: Math.floor(Math.random() * 20) + 5, // 5-25
      reductionRate: Math.floor(Math.random() * 15) + 85, // 85-100%
      lightDamage: Math.floor(Math.random() * 20) + 70, // 70-90%
      moderateDamage: Math.floor(Math.random() * 15) + 5, // 5-20%
      severeDamage: Math.floor(Math.random() * 5) + 0, // 0-5%
      conclusion: `经过综合评估，本次防治方案执行效果${getResultText(currentEvaluation.value.result)}。防治目标基本达成，病虫害得到有效控制，生态环境影响较小，成本控制合理。`,
      suggestions: [
        '建议继续加强监测，及时发现新的病虫害发生情况',
        '优化防治时机，提高防治效果',
        '加强生物防治措施的应用，减少化学农药使用',
        '建立长效监测机制，预防病虫害复发'
      ],
      monitoringAdvice: '建议在防治后1个月、3个月、6个月分别进行跟踪监测，重点关注虫口密度变化、植物恢复情况和生态环境影响。',
      beforeImages: currentEvaluation.value.beforeImages || [],
      afterImages: currentEvaluation.value.afterImages || []
    }
  } catch (error) {
    console.error('生成报告失败:', error)
    ElMessage.error('生成报告失败，请重试')
  } finally {
    reportLoading.value = false
  }
}

// 下载报告
const downloadReport = async () => {
  reportLoading.value = true
  try {
    // 这里应该调用API生成PDF报告并下载
    // await downloadReportPDF(currentEvaluation.value.id)
    
    // 模拟下载过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 创建一个虚拟的下载链接
    const link = document.createElement('a')
    link.href = '#' // 实际应用中这里应该是PDF文件的URL
    link.download = `${reportData.value.title}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('报告下载成功')
  } catch (error) {
    console.error('下载报告失败:', error)
    ElMessage.error('下载报告失败，请重试')
  } finally {
    reportLoading.value = false
  }
}

// 打印报告
const printReport = () => {
  // 获取报告内容
  const reportContent = document.querySelector('.report-content')
  if (!reportContent) {
    ElMessage.error('无法获取报告内容')
    return
  }
  
  // 创建打印窗口
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html>
      <head>
        <title>${reportData.value.title}</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          .report-header h2 { color: #2c3e50; text-align: center; }
          .report-meta { text-align: center; margin-bottom: 30px; }
          .report-section { margin-bottom: 30px; }
          .report-section h3 { color: #409eff; border-bottom: 2px solid #409eff; padding-bottom: 5px; }
          .summary-content { display: flex; justify-content: space-around; margin: 20px 0; }
          .summary-item { text-align: center; }
          .summary-label { font-weight: bold; margin-bottom: 5px; }
          .summary-value { font-size: 18px; font-weight: bold; }
          .comparison-item, .damage-item { display: flex; justify-content: space-between; margin: 10px 0; }
          .highlight { background-color: #f0f9ff; padding: 5px; }
          ul { padding-left: 20px; }
          @media print { body { margin: 0; } }
        </style>
      </head>
      <body>
        ${reportContent.innerHTML}
      </body>
    </html>
  `)
  printWindow.document.close()
  printWindow.print()
}

const editEvaluation = (row) => {
  Object.assign(evaluationForm, row)
  isEdit.value = true
  showCreateDialog.value = true
}

const deleteEvaluation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评估记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API删除评估
    // await deleteEvaluation(row.id)

    ElMessage.success('删除成功')
    loadEvaluationList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadEvaluationList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadEvaluationList()
}

const loadEvaluationList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取评估列表
    // const response = await getEvaluationList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 模拟数据
    const mockData = [
      {
        id: 1,
        planName: '松毛虫综合防治方案',
        evaluationDate: '2024-01-20',
        beforeImages: [
          'https://picsum.photos/200/200?random=1',
          'https://picsum.photos/200/200?random=2',
          'https://picsum.photos/200/200?random=3'
        ],
        afterImages: [
          'https://picsum.photos/200/200?random=11',
          'https://picsum.photos/200/200?random=12',
          'https://picsum.photos/200/200?random=13'
        ],
        effectiveness: 85,
        pestDensityReduction: 80,
        damageAreaReduction: 75,
        plantRecoveryDegree: 'good',
        result: 'good',
        evaluator: '张利文',
        createTime: '2024-01-20 15:30:00',
        metrics: {
          densityReduction: 80,
          areaReduction: 75,
          recoveryRate: 90
        },
        description: '防治效果良好，病虫害密度明显降低',
        suggestions: '建议继续监测，必要时进行补充防治'
      },
      {
        id: 2,
        planName: '杨树溃疡病防治方案',
        evaluationDate: '2024-01-18',
        beforeImages: [
          'https://picsum.photos/200/200?random=4',
          'https://picsum.photos/200/200?random=5'
        ],
        afterImages: [
          'https://picsum.photos/200/200?random=14',
          'https://picsum.photos/200/200?random=15'
        ],
        effectiveness: 92,
        pestDensityReduction: 90,
        damageAreaReduction: 88,
        plantRecoveryDegree: 'excellent',
        result: 'excellent',
        evaluator: '李芝',
        createTime: '2024-01-18 14:20:00',
        metrics: {
          densityReduction: 90,
          areaReduction: 88,
          recoveryRate: 95
        },
        description: '防治效果优秀，溃疡病得到有效控制',
        suggestions: '继续观察，定期检查树木健康状况'
      },
      {
        id: 3,
        planName: '美国白蛾防治方案',
        evaluationDate: '2024-01-15',
        beforeImages: [
          'https://picsum.photos/200/200?random=6',
          'https://picsum.photos/200/200?random=7'
        ],
        afterImages: [
          'https://picsum.photos/200/200?random=16',
          'https://picsum.photos/200/200?random=17'
        ],
        effectiveness: 78,
        pestDensityReduction: 75,
        damageAreaReduction: 70,
        plantRecoveryDegree: 'good',
        result: 'good',
        evaluator: '王晓刚',
        createTime: '2024-01-15 16:45:00',
        metrics: {
          densityReduction: 75,
          areaReduction: 70,
          recoveryRate: 85
        },
        description: '防治效果良好，白蛾数量显著减少',
        suggestions: '加强后期监测，防止复发'
      },
      {
        id: 4,
        planName: '柳毒蛾防治方案',
        evaluationDate: '2024-01-12',
        beforeImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=60',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=60'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=60&brightness=1.3',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=60&brightness=1.3'
        ],
        effectiveness: 65,
        pestDensityReduction: 60,
        damageAreaReduction: 65,
        plantRecoveryDegree: 'average',
        result: 'average',
        evaluator: '赵知青',
        createTime: '2024-01-12 10:15:00',
        metrics: {
          densityReduction: 60,
          areaReduction: 65,
          recoveryRate: 70
        },
        description: '防治效果一般，需要加强防治措施',
        suggestions: '调整防治策略，增加防治频次'
      },
      {
        id: 5,
        planName: '桃蚜虫防治方案',
        evaluationDate: '2024-01-10',
        beforeImages: [
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=90',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=90',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=90'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=90&brightness=1.4',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=90&brightness=1.4',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=90&brightness=1.4'
        ],
        effectiveness: 88,
        result: 'good',
        evaluator: '孙武闻',
        createTime: '2024-01-10 13:30:00',
        metrics: {
          densityReduction: 85,
          areaReduction: 82,
          recoveryRate: 90
        },
        description: '蚜虫防治效果良好，植物恢复健康',
        suggestions: '继续观察，预防二次感染'
      },
      {
        id: 6,
        planName: '天牛防治方案',
        evaluationDate: '2024-01-08',
        beforeImages: [
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=120',
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=120'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=120&brightness=1.2',
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=120&brightness=1.2'
        ],
        effectiveness: 72,
        result: 'good',
        evaluator: '周林',
        createTime: '2024-01-08 11:20:00',
        metrics: {
          densityReduction: 70,
          areaReduction: 68,
          recoveryRate: 78
        },
        description: '天牛防治取得良好效果，树木损害减少',
        suggestions: '加强树干保护，定期检查虫孔'
      },
      {
        id: 7,
        planName: '红蜘蛛防治方案',
        evaluationDate: '2024-01-05',
        beforeImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=150',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=150'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=150&brightness=1.3',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=150&brightness=1.3'
        ],
        effectiveness: 95,
        result: 'excellent',
        evaluator: '吴东',
        createTime: '2024-01-05 09:45:00',
        metrics: {
          densityReduction: 95,
          areaReduction: 92,
          recoveryRate: 98
        },
        description: '红蜘蛛防治效果优秀，叶片恢复正常',
        suggestions: '保持当前防治策略，定期监测'
      },
      {
        id: 8,
        planName: '蚧壳虫防治方案',
        evaluationDate: '2024-01-03',
        beforeImages: [
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=180',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=180'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=180&brightness=1.2',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=180&brightness=1.2'
        ],
        effectiveness: 58,
        result: 'average',
        evaluator: '郑译',
        createTime: '2024-01-03 14:10:00',
        metrics: {
          densityReduction: 55,
          areaReduction: 60,
          recoveryRate: 60
        },
        description: '蚧壳虫防治效果一般，需要改进方案',
        suggestions: '更换防治药剂，增加防治次数'
      },
      {
        id: 9,
        planName: '叶螨防治方案',
        evaluationDate: '2024-01-01',
        beforeImages: [
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=210',
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=210',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=210'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=210&brightness=1.3',
          'https://images.unsplash.com/photo-1574263867128-a3d5c1b1deaa?w=200&h=200&fit=crop&hue=210&brightness=1.3',
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=210&brightness=1.3'
        ],
        effectiveness: 81,
        result: 'good',
        evaluator: '张易',
        createTime: '2024-01-01 16:30:00',
        metrics: {
          densityReduction: 78,
          areaReduction: 80,
          recoveryRate: 85
        },
        description: '叶螨防治效果良好，叶片损害明显减少',
        suggestions: '继续监测，注意环境湿度控制'
      },
      {
        id: 10,
        planName: '粉虱防治方案',
        evaluationDate: '2023-12-28',
        beforeImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=240',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=240'
        ],
        afterImages: [
          'https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=200&h=200&fit=crop&hue=240&brightness=1.4',
          'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=200&h=200&fit=crop&hue=240&brightness=1.4'
        ],
        effectiveness: 45,
        result: 'poor',
        evaluator: '李晓芳',
        createTime: '2023-12-28 12:15:00',
        metrics: {
          densityReduction: 40,
          areaReduction: 45,
          recoveryRate: 50
        },
        description: '粉虱防治效果较差，需要重新制定方案',
        suggestions: '更换防治策略，考虑生物防治方法'
      }
    ]

    evaluationList.value = mockData
    pagination.total = 20
  } catch (error) {
    console.error('加载评估列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadTreatmentPlans = async () => {
  try {
    // 这里应该调用API获取防治方案列表
    // const response = await getTreatmentPlanList()

    // 模拟数据
    treatmentPlans.value = [
      { id: 1, name: '松毛虫综合防治方案' },
      { id: 2, name: '杨树溃疡病防治方案' },
      { id: 3, name: '美国白蛾防治方案' },
      { id: 4, name: '柳毒蛾防治方案' },
      { id: 5, name: '桃蚜虫防治方案' },
      { id: 6, name: '天牛防治方案' },
      { id: 7, name: '红蜘蛛防治方案' },
      { id: 8, name: '蚧壳虫防治方案' },
      { id: 9, name: '叶螨防治方案' },
      { id: 10, name: '粉虱防治方案' }
    ]
  } catch (error) {
    console.error('加载防治方案失败:', error)
  }
}

// 生命周期
onMounted(() => {
  loadEvaluationList()
  loadTreatmentPlans()
})
</script>
<style lang="scss" scoped>
.effect-evaluation {
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

  .image-group {
    display: flex;
    align-items: center;
    gap: 5px;

    .table-image-small {
      width: 40px;
      height: 40px;
      border-radius: 4px;
    }

    .image-count {
      font-size: 12px;
      color: #909399;
      background-color: #f5f7fa;
      padding: 2px 6px;
      border-radius: 10px;
    }
  }

  .evaluation-metrics {
    .el-form-item {
      margin-bottom: 15px;
    }
  }

  .detail-content {
    .metrics-section {
      margin: 20px 0;

      h4 {
        margin-bottom: 15px;
        color: #2c3e50;
        font-size: 16px;
      }

      .metric-item {
        text-align: center;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 8px;

        .metric-label {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
        }

        .metric-value {
          font-size: 24px;
          font-weight: bold;
          color: #2c3e50;
        }
      }
    }

    .images-section {
      margin: 20px 0;

      h4,
      h5 {
        color: #2c3e50;
        margin-bottom: 15px;
      }

      h4 {
        font-size: 16px;
      }

      h5 {
        font-size: 14px;
        margin-bottom: 10px;
      }

      .image-gallery {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 10px;

        .gallery-image {
          width: 120px;
          height: 120px;
          border-radius: 8px;
          border: 1px solid #e4e7ed;
        }
      }
    }

    .description-section,
    .suggestions-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      p {
        color: #606266;
        line-height: 1.6;
        margin: 0;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 8px;
      }
    }
  }
}

// 上传组件样式覆盖
:deep(.el-upload--picture-card) {
  width: 80px;
  height: 80px;
  line-height: 80px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 80px;
  height: 80px;
}

.effect-evaluation {
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
  .effect-evaluation {
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
      .metrics-section {
        .el-row {
          flex-direction: column;

          .el-col {
            width: 100%;
            margin-bottom: 15px;
          }
        }
      }

      .images-section {
        .image-gallery {
          grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));

          .gallery-image {
            width: 80px;
            height: 80px;
          }
        }
      }
    }
  }
  
  // 报告样式
  .report-content {
    .report-header {
      text-align: center;
      margin-bottom: 30px;
      border-bottom: 2px solid #409eff;
      padding-bottom: 20px;
      
      h2 {
        color: #2c3e50;
        margin: 0 0 15px 0;
        font-size: 24px;
      }
      
      .report-meta {
        color: #606266;
        font-size: 14px;
        
        p {
          margin: 5px 0;
        }
      }
    }
    
    .report-section {
      margin-bottom: 30px;
      
      h3 {
        color: #409eff;
        font-size: 18px;
        margin: 0 0 15px 0;
        border-bottom: 2px solid #409eff;
        padding-bottom: 8px;
      }
      
      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin: 20px 0 10px 0;
      }
      
      h5 {
        color: #606266;
        font-size: 14px;
        margin: 15px 0 8px 0;
      }
    }
    
    .summary-content {
      .summary-item {
        text-align: center;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 8px;
        
        .summary-label {
          font-size: 14px;
          color: #606266;
          margin-bottom: 8px;
        }
        
        .summary-value {
          font-size: 20px;
          font-weight: bold;
          
          &.excellent { color: #67c23a; }
          &.good { color: #409eff; }
          &.average { color: #e6a23c; }
          &.poor { color: #f56c6c; }
        }
      }
    }
    
    .analysis-content {
      p {
        line-height: 1.6;
        color: #606266;
        margin: 10px 0;
      }
      
      .comparison-charts {
        margin: 20px 0;
        
        .chart-container {
          padding: 15px;
          background-color: #f8f9fa;
          border-radius: 8px;
          
          h5 {
            text-align: center;
            margin-bottom: 15px;
            color: #2c3e50;
          }
        }
        
        .before-after-comparison,
        .damage-comparison {
          .comparison-item,
          .damage-item {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #e4e7ed;
            
            &:last-child {
              border-bottom: none;
            }
            
            &.highlight {
              background-color: #e1f5fe;
              padding: 8px 12px;
              border-radius: 4px;
              font-weight: bold;
            }
            
            .value {
              color: #409eff;
              font-weight: bold;
            }
          }
        }
      }
    }
    
    .conclusion-content {
      p {
        line-height: 1.6;
        color: #606266;
        margin: 10px 0;
      }
      
      ul {
        padding-left: 20px;
        
        li {
          margin: 8px 0;
          color: #606266;
          line-height: 1.5;
        }
      }
    }
    
    .attachments {
      .image-gallery {
        .image-item {
          h5 {
            text-align: center;
            margin-bottom: 10px;
            color: #2c3e50;
          }
        }
      }
    }
  }
  
  .report-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
}
</style>