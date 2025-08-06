<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>

          <el-form-item label="姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
          </el-form-item>

          <el-form-item label="角色">
            <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
              <el-option label="管理员" value="admin" />
              <el-option label="专家" value="expert" />
              <el-option label="操作员" value="operator" />
              <el-option label="观察员" value="observer" />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" value="active" />
              <el-option label="禁用" value="disabled" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表 -->
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />

        <el-table-column prop="username" label="用户名" width="120" />

        <el-table-column prop="realName" label="姓名" width="100" />

        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />

        <el-table-column prop="phone" label="手机号" width="120" />

        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="department" label="部门" width="120" />

        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="lastLoginTime" label="最后登录" width="120" />

        <el-table-column prop="createTime" label="创建时间" width="120" />

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)"> 查看 </el-button>
            <el-button type="success" size="small" @click="editUser(row)"> 编辑 </el-button>
            <el-button type="warning" size="small" @click="resetPassword(row)">
              重置密码
            </el-button>
            <el-button
              :type="row.status === 'active' ? 'danger' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="!isEdit">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="userForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="userForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="userForm.role" placeholder="请选择角色">
                <el-option label="管理员" value="admin" />
                <el-option label="专家" value="expert" />
                <el-option label="操作员" value="operator" />
                <el-option label="观察员" value="observer" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="userForm.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="userForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveUser" :loading="saveLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="用户详情" width="700px">
      <div v-if="currentUser" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ currentUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleType(currentUser.role)">
              {{ getRoleText(currentUser.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="部门">{{ currentUser.department }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentUser.status)">
              {{ getStatusText(currentUser.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentUser.createTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{
            currentUser.lastLoginTime || '从未登录'
          }}</el-descriptions-item>
          <el-descriptions-item label="登录次数">{{
            currentUser.loginCount || 0
          }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentUser.remarks" class="remarks-section">
          <h4>备注信息</h4>
          <p>{{ currentUser.remarks }}</p>
        </div>

        <div class="permissions-section">
          <h4>权限信息</h4>
          <el-tag
            v-for="permission in getUserPermissions(currentUser.role)"
            :key="permission"
            class="permission-tag"
            size="small"
          >
            {{ permission }}
          </el-tag>
        </div>
      </div>
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
const showAddDialog = ref(false)
const showDetailDialog = ref(false)
const isEdit = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  username: '',
  realName: '',
  role: '',
  status: ''
})

const userForm = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: '',
  department: '',
  remarks: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const userList = ref([])

// 表单引用
const userFormRef = ref()

// 自定义验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== userForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const userRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }]
})

// 方法
const getRoleType = (role) => {
  const typeMap = {
    admin: 'danger',
    expert: 'warning',
    operator: 'success',
    observer: 'info'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    admin: '管理员',
    expert: '专家',
    operator: '操作员',
    observer: '观察员'
  }
  return textMap[role] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    disabled: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    active: '正常',
    disabled: '禁用'
  }
  return textMap[status] || '未知'
}

const getUserPermissions = (role) => {
  const permissionMap = {
    admin: ['系统管理', '用户管理', '数据管理', '病虫害管理', '森林资源管理', '预警管理'],
    expert: ['病虫害识别', '防治方案', '效果评估', '知识库管理'],
    operator: ['病虫害识别', '数据录入', '基础查询'],
    observer: ['数据查看', '报告查看']
  }
  return permissionMap[role] || []
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSaveUser = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 这里应该调用API保存用户
        // if (isEdit.value) {
        //   await updateUser(userForm)
        // } else {
        //   await createUser(userForm)
        // }

        ElMessage.success(isEdit.value ? '用户更新成功' : '用户添加成功')
        showAddDialog.value = false
        resetUserForm()
        loadUserList()
      } catch (error) {
        console.error('保存用户失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

const resetUserForm = () => {
  Object.keys(userForm).forEach((key) => {
    userForm[key] = ''
  })
  isEdit.value = false
  if (userFormRef.value) {
    userFormRef.value.clearValidate()
  }
}

const viewDetail = (row) => {
  currentUser.value = row
  showDetailDialog.value = true
}

const editUser = (row) => {
  Object.assign(userForm, row)
  // 编辑时不需要密码字段
  delete userForm.password
  delete userForm.confirmPassword
  isEdit.value = true
  showAddDialog.value = true
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 ${row.realName} 的密码吗？重置后密码将变为默认密码。`,
      '确认重置密码',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 这里应该调用API重置密码
    // await resetUserPassword(row.id)

    ElMessage.success('密码重置成功，默认密码为：123456')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败，请重试')
    }
  }
}

const toggleStatus = async (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${row.realName} 吗？`, `确认${action}`, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里应该调用API切换用户状态
    // await toggleUserStatus(row.id)

    ElMessage.success(`${action}成功`)
    loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败，请重试`)
    }
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadUserList()
}

const loadUserList = async () => {
  loading.value = true
  try {
    // 这里应该调用API获取用户列表
    // const response = await getUserList({
    //   page: pagination.currentPage,
    //   size: pagination.pageSize,
    //   ...searchForm
    // })

    // 完整的模拟数据
    const allMockData = [
      {
        id: 1,
        username: 'admin',
        realName: '管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        role: 'admin',
        department: '信息技术部',
        status: 'active',
        createTime: '2023-01-01 10:00:00',
        lastLoginTime: '2024-01-20 09:30:00',
        loginCount: 156,
        remarks: '系统超级管理员'
      },
      {
        id: 2,
        username: 'expert001',
        realName: '王云雅',
        email: 'expert001@example.com',
        phone: '13800138001',
        role: 'expert',
        department: '森林保护部',
        status: 'active',
        createTime: '2023-02-15 14:20:00',
        lastLoginTime: '2024-01-19 16:45:00',
        loginCount: 89,
        remarks: '病虫害防治专家'
      },
      {
        id: 3,
        username: 'operator001',
        realName: '李志刚',
        email: 'operator001@example.com',
        phone: '13800138002',
        role: 'operator',
        department: '现场作业部',
        status: 'active',
        createTime: '2023-03-10 11:15:00',
        lastLoginTime: '2024-01-18 08:20:00',
        loginCount: 234,
        remarks: '一线操作人员'
      },
      {
        id: 4,
        username: 'expert002',
        realName: '孙伟',
        email: 'expert002@example.com',
        phone: '13800138003',
        role: 'expert',
        department: '森林研究所',
        status: 'active',
        createTime: '2023-04-20 09:30:00',
        lastLoginTime: '2024-01-17 14:20:00',
        loginCount: 67,
        remarks: '森林生态研究专家'
      },
      {
        id: 5,
        username: 'manager001',
        realName: '林志文',
        email: 'manager001@example.com',
        phone: '13800138004',
        role: 'manager',
        department: '森林管理部',
        status: 'active',
        createTime: '2023-05-15 16:45:00',
        lastLoginTime: '2024-01-16 11:30:00',
        loginCount: 145,
        remarks: '森林资源管理主管'
      },
      {
        id: 6,
        username: 'operator002',
        realName: '孙加一',
        email: 'operator002@example.com',
        phone: '13800138005',
        role: 'operator',
        department: '技术支持部',
        status: 'active',
        createTime: '2023-06-10 13:20:00',
        lastLoginTime: '2024-01-15 10:15:00',
        loginCount: 178,
        remarks: '技术支持人员'
      },
      {
        id: 7,
        username: 'expert003',
        realName: '周天',
        email: 'expert003@example.com',
        phone: '13800138006',
        role: 'expert',
        department: '病虫害防治中心',
        status: 'inactive',
        createTime: '2023-07-05 10:10:00',
        lastLoginTime: '2024-01-10 15:40:00',
        loginCount: 45,
        remarks: '病虫害识别专家，暂时停用'
      },
      {
        id: 8,
        username: 'operator003',
        realName: '吴晓',
        email: 'operator003@example.com',
        phone: '13800138007',
        role: 'operator',
        department: '森林巡护队',
        status: 'active',
        createTime: '2023-08-12 14:30:00',
        lastLoginTime: '2024-01-14 07:50:00',
        loginCount: 267,
        remarks: '森林巡护人员'
      },
      {
        id: 9,
        username: 'manager002',
        realName: '王广文',
        email: 'manager002@example.com',
        phone: '13800138008',
        role: 'manager',
        department: '综合管理部',
        status: 'active',
        createTime: '2023-09-18 11:45:00',
        lastLoginTime: '2024-01-13 16:20:00',
        loginCount: 98,
        remarks: '综合事务管理'
      },
      {
        id: 10,
        username: 'expert004',
        realName: '冯签',
        email: 'expert004@example.com',
        phone: '13800138009',
        role: 'expert',
        department: '林业大学',
        status: 'active',
        createTime: '2023-10-25 15:15:00',
        lastLoginTime: '2024-01-12 13:10:00',
        loginCount: 56,
        remarks: '外聘林业专家'
      }
    ]

    // 根据搜索条件过滤数据
    let filteredData = allMockData

    // 按用户名过滤
    if (searchForm.username) {
      filteredData = filteredData.filter((item) =>
        item.username.toLowerCase().includes(searchForm.username.toLowerCase())
      )
    }

    // 按真实姓名过滤
    if (searchForm.realName) {
      filteredData = filteredData.filter((item) =>
        item.realName.toLowerCase().includes(searchForm.realName.toLowerCase())
      )
    }

    // 按角色过滤
    if (searchForm.role) {
      filteredData = filteredData.filter((item) => item.role === searchForm.role)
    }

    // 按部门过滤
    if (searchForm.department) {
      filteredData = filteredData.filter((item) =>
        item.department.toLowerCase().includes(searchForm.department.toLowerCase())
      )
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

    userList.value = paginatedData
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 编辑时动态更新验证规则
const updateValidationRules = () => {
  if (isEdit.value) {
    // 编辑时移除密码相关验证
    delete userRules.password
    delete userRules.confirmPassword
  } else {
    // 添加时恢复密码验证
    userRules.password = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
    userRules.confirmPassword = [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { validator: validateConfirmPassword, trigger: 'blur' }
    ]
  }
}

// 监听编辑状态变化
const watchEditState = () => {
  updateValidationRules()
}

// 生命周期
onMounted(() => {
  loadUserList()
})
</script>

<style lang="scss" scoped>
.user-management {
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
        padding: 10px;
        background-color: #f8f9fa;
        border-radius: 4px;
      }
    }

    .permissions-section {
      margin: 20px 0;

      h4 {
        color: #2c3e50;
        font-size: 16px;
        margin-bottom: 10px;
      }

      .permission-tag {
        margin-right: 8px;
        margin-bottom: 8px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-management {
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
