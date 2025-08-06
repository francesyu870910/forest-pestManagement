<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <h2>
          <div class="title-line">森林病虫害防治数字化</div>
          <div class="title-line">管理系统</div>
        </h2>
      </div>

      <div v-if="activeTab === 'login'" class="login-content">
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form-content"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="loginForm.rememberMe"> 记住密码 </el-checkbox>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loginLoading"
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loginLoading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="forgot-link">
              <el-link type="primary" @click="activeTab = 'forgot'"> 忘记密码？ </el-link>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <!-- 忘记密码表单 -->
      <div v-else-if="activeTab === 'forgot'" class="forgot-content">
        <el-form
          ref="forgotFormRef"
          :model="forgotForm"
          :rules="forgotRules"
          class="login-form-content"
        >
          <div v-if="forgotStep === 1" class="forgot-step">
            <div class="step-header">
              <h3>找回密码</h3>
              <p>请输入您的注册邮箱，我们将发送验证码到您的邮箱</p>
            </div>

            <el-form-item prop="email">
              <el-input
                v-model="forgotForm.email"
                placeholder="请输入注册邮箱"
                size="large"
                prefix-icon="Message"
                clearable
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="forgotLoading"
                class="login-button"
                @click="handleSendResetCode"
              >
                <span v-if="!forgotLoading">发送验证码</span>
                <span v-else>发送中...</span>
              </el-button>
            </el-form-item>
          </div>

          <div v-else-if="forgotStep === 2" class="forgot-step">
            <div class="step-header">
              <h3>重置密码</h3>
              <p>请输入收到的验证码和新密码</p>
            </div>

            <el-form-item prop="code">
              <el-input
                v-model="forgotForm.code"
                placeholder="请输入6位验证码"
                size="large"
                prefix-icon="Key"
                clearable
                maxlength="6"
              />
            </el-form-item>

            <el-form-item prop="newPassword">
              <el-input
                v-model="forgotForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                size="large"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item prop="confirmNewPassword">
              <el-input
                v-model="forgotForm.confirmNewPassword"
                type="password"
                placeholder="请确认新密码"
                size="large"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="forgotLoading"
                class="login-button"
                @click="handleResetPassword"
              >
                <span v-if="!forgotLoading">重置密码</span>
                <span v-else>重置中...</span>
              </el-button>
            </el-form-item>

            <el-form-item>
              <div class="forgot-link">
                <el-link type="info" @click="forgotStep = 1"> 重新发送验证码 </el-link>
              </div>
            </el-form-item>
          </div>

          <el-form-item>
            <div class="forgot-link">
              <el-link
                type="primary"
                @click="
                  activeTab = 'login';
                  forgotStep = 1;
                "
              >
                返回登录
              </el-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { sendResetCode, verifyResetCode, resetPassword } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 当前激活的标签页
const activeTab = ref('login')
const forgotStep = ref(1) // 忘记密码步骤：1-输入邮箱，2-验证码和新密码

// 加载状态
const loginLoading = ref(false)
const forgotLoading = ref(false)

// 表单引用
const loginFormRef = ref()
const forgotFormRef = ref()

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 忘记密码表单
const forgotForm = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmNewPassword: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
}

const forgotRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== forgotForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loginLoading.value = true
      try {
        await userStore.userLogin(loginForm)
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loginLoading.value = false
      }
    }
  })
}

// 发送重置验证码
const handleSendResetCode = async () => {
  if (!forgotFormRef.value) return

  await forgotFormRef.value.validateField('email', async (valid) => {
    if (valid) {
      forgotLoading.value = true
      try {
        await sendResetCode(forgotForm.email)
        ElMessage.success('验证码已发送到您的邮箱')
        forgotStep.value = 2
      } catch (error) {
        console.error('发送验证码失败:', error)
      } finally {
        forgotLoading.value = false
      }
    }
  })
}

// 重置密码
const handleResetPassword = async () => {
  if (!forgotFormRef.value) return

  await forgotFormRef.value.validate(async (valid) => {
    if (valid) {
      forgotLoading.value = true
      try {
        await resetPassword({
          email: forgotForm.email,
          code: forgotForm.code,
          newPassword: forgotForm.newPassword
        })
        ElMessage.success('密码重置成功，请使用新密码登录')
        activeTab.value = 'login'
        forgotStep.value = 1
        // 清空忘记密码表单
        Object.keys(forgotForm).forEach((key) => {
          forgotForm[key] = ''
        })
      } catch (error) {
        console.error('重置密码失败:', error)
      } finally {
        forgotLoading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-form {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 600;
    color: #2c3e50;
    line-height: 1.3;

    .title-line {
      display: block;
      margin-bottom: 4px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.login-form-content {
  animation: fadeInUp 0.6s ease-out;

  :deep(.el-form-item) {
    margin-bottom: 20px;

    .el-input__wrapper {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      border: 1px solid #e1e8ed;
      transition: all 0.3s ease;

      &:hover {
        border-color: #667eea;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
      }

      &.is-focus {
        border-color: #667eea;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
      }
    }

    .el-select .el-input__wrapper {
      border-radius: 12px;
    }
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0;

  .el-checkbox {
    :deep(.el-checkbox__label) {
      color: #7f8c8d;
      font-size: 14px;
      font-weight: 500;
    }

    :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
      background-color: #667eea;
      border-color: #667eea;
    }
  }

  .el-link {
    font-size: 14px;
    font-weight: 500;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }

  &:active {
    transform: translateY(0);
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
  }
}

.register-link {
  text-align: center;
  color: #7f8c8d;
  font-size: 14px;
  margin-top: 15px;

  .el-link {
    font-size: 14px;
    font-weight: 500;
    margin-left: 4px;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.forgot-step {
  .step-header {
    text-align: center;
    margin-bottom: 25px;

    .step-icon {
      font-size: 2.5rem;
      margin-bottom: 10px;
      display: block;
      animation: pulse 2s infinite;
    }

    h3 {
      margin: 0 0 8px 0;
      font-size: 1.2rem;
      font-weight: 600;
      color: #2c3e50;
    }

    p {
      margin: 0;
      font-size: 0.9rem;
      color: #7f8c8d;
      line-height: 1.4;
    }
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInScale {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }

  .login-form {
    width: 100%;
    max-width: 400px;
    padding: 30px 25px;
    border-radius: 16px;
  }

  .login-header {
    .logo-section {
      flex-direction: column;
      gap: 10px;

      .logo-icon {
        font-size: 2.5rem;
      }

      .title-section {
        text-align: center;

        h2 {
          font-size: 1.3rem;
        }

        .subtitle {
          font-size: 0.8rem;
        }
      }
    }
  }

  .background-decoration {
    .tree-icon,
    .leaf-icon {
      font-size: 1.5rem;
    }

    .leaf-1,
    .leaf-2,
    .leaf-3 {
      font-size: 1rem;
    }
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 15px;
  }

  .login-form {
    padding: 25px 20px;
    border-radius: 12px;
  }

  .login-header {
    margin-bottom: 25px;

    .logo-section {
      .logo-icon {
        font-size: 2rem;
      }

      .title-section {
        h2 {
          font-size: 1.1rem;
        }

        .subtitle {
          font-size: 0.75rem;
        }
      }
    }
  }

  .login-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 20px;

      .el-tabs__nav {
        .el-tabs__item {
          font-size: 14px;
          padding: 8px 12px;
        }
      }
    }
  }

  .login-button {
    height: 44px;
    font-size: 15px;
  }
}
</style>
