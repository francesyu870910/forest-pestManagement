<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <div class="profile-content">
        <div class="profile-avatar">
          <el-avatar :size="120" :src="userInfo.avatar">
            <el-icon size="60"><User /></el-icon>
          </el-avatar>
          <el-button type="primary" class="upload-btn" @click="handleAvatarUpload">
            更换头像
          </el-button>
        </div>

        <div class="profile-form">
          <el-form ref="profileFormRef" :model="userInfo" :rules="profileRules" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>

            <el-form-item label="姓名" prop="name">
              <el-input v-model="userInfo.name" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userInfo.email" />
            </el-form-item>

            <el-form-item label="角色">
              <el-input :value="getRoleText(userInfo.role)" disabled />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="updateLoading">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>

    <!-- 修改密码 -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        style="max-width: 400px"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

const userStore = useUserStore()

// 响应式数据
const updateLoading = ref(false)
const passwordLoading = ref(false)

const userInfo = reactive({
  username: '',
  name: '',
  email: '',
  role: '',
  avatar: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 表单验证规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 方法
const getRoleText = (role) => {
  const roleMap = {
    admin: '系统管理员',
    forest_manager: '森林管理员',
    expert: '技术专家',
    user: '普通用户'
  }
  return roleMap[role] || '未知角色'
}

const handleAvatarUpload = () => {
  ElMessage.info('头像上传功能待实现')
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updateLoading.value = true
      try {
        // 这里应该调用API更新用户信息
        // await updateUserProfile(userInfo)

        // 更新store中的用户信息
        userStore.setUserInfo(userInfo)

        ElMessage.success('个人信息更新成功')
      } catch (error) {
        console.error('更新个人信息失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        updateLoading.value = false
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        // 这里应该调用API修改密码
        // await changePassword(passwordForm)

        ElMessage.success('密码修改成功')
        resetPasswordForm()
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改失败，请重试')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  // 重置为原始用户信息
  Object.assign(userInfo, userStore.userInfo)
}

const resetPasswordForm = () => {
  Object.keys(passwordForm).forEach((key) => {
    passwordForm[key] = ''
  })
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 生命周期
onMounted(() => {
  // 初始化用户信息
  Object.assign(userInfo, userStore.userInfo)
})
</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;

  .card-header {
    font-weight: 600;
    font-size: 16px;
  }

  .profile-content {
    display: flex;
    gap: 40px;

    .profile-avatar {
      display: flex;
      flex-direction: column;
      align-items: center;

      .upload-btn {
        margin-top: 20px;
      }
    }

    .profile-form {
      flex: 1;
    }
  }

  .password-card {
    margin-top: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-container {
    .profile-content {
      flex-direction: column;
      align-items: center;
      gap: 20px;
    }
  }
}
</style>
