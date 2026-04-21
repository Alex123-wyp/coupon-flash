<script setup>
import { ref } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import router from '@/router'
import { userLogin } from '@/api/user'
import { uiCopy } from '@/constants/uiCopy'
const formRef = ref()
const userStore = useUserStore()
const form = ref({
  // phone: '13686869696',
  phone: '13838411438',
  password: '123456',
  radio: true
})

const goBack = () => {
  window.history.back()
}

const login = async () => {
  console.log('Password sign-in is not implemented yet')
  try {
    await formRef.value.validate()
    const res = await userLogin(form.value)
    console.log('Login successful:', res)
    console.log('Login token:', res.data)
    if (res.data) {
      userStore.setToken(res.data)
      console.log('Token stored:', userStore.getToken())
      ElMessage.success(uiCopy.auth.loginSuccess)
      router.push('/index')
    } else {
      ElMessage.error(uiCopy.auth.loginMissingToken)
    }
  } catch (error) {
    console.error('Login failed:', error)
    ElMessage.error(
      uiCopy.auth.loginFailedPrefix +
        (error.message || uiCopy.auth.unknownError)
    )
  }
}

// Add form validation rules
const rules = {
  phone: [
    { required: true, message: uiCopy.auth.phoneRequired, trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: uiCopy.auth.phoneInvalid,
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: uiCopy.auth.passwordRequired, trigger: 'blur' },
    { min: 6, message: uiCopy.auth.passwordMin, trigger: 'blur' }
  ],
  radio: [
    {
      required: true,
      message: uiCopy.auth.required,
      trigger: 'change'
    }
  ]
}
</script>

<template>
  <div class="login-container">
    <div class="header">
      <div class="header-back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="header-title">
        {{ uiCopy.auth.passwordLoginTitle }}&nbsp;&nbsp;&nbsp;
      </div>
    </div>
    <div class="content">
      <!-- Replace with el-form -->
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="0"
        class="login-form"
      >
        <el-form-item prop="phone">
          <el-input
            :placeholder="uiCopy.auth.phonePlaceholder"
            v-model="form.phone"
          >
          </el-input>
        </el-form-item>
        <div style="height: 5px"></div>
        <el-form-item prop="password">
          <el-input
            :placeholder="uiCopy.auth.passwordPlaceholder"
            v-model="form.password"
            show-password
          >
          </el-input>
        </el-form-item>
        <div style="text-align: center; color: #8c939d; margin: 5px 0">
          <a href="javascript:void(0)">{{ uiCopy.auth.forgotPassword }}</a>
        </div>
        <el-button
          @click="login"
          style="width: 100%; background-color: #f63; color: #fff"
          >{{ uiCopy.auth.login }}</el-button
        >
        <div style="text-align: right; color: #333333; margin: 5px 0">
          <router-link to="/register">{{ uiCopy.auth.codeLogin }}</router-link>
        </div>
        <div class="login-radio">
          <el-form-item prop="radio">
            <el-checkbox v-model="form.radio" :true-value="1" :false-value="''">
            </el-checkbox>
          </el-form-item>
          <div>
            {{ uiCopy.auth.agreementPrefix }}
            <a href="javascript:void(0)"> {{ uiCopy.auth.agreementName }}</a
            >,
            <a href="javascript:void(0)">{{ uiCopy.auth.privacyPolicy }}</a>
            {{ uiCopy.auth.agreementSuffix }}
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
@import '@/assets/css/login.css';
</style>
