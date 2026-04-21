<script setup>
import { ref } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import router from '@/router'
import { uiCopy } from '@/constants/uiCopy'

const disabled = ref(false)
const codeBtnMsg = ref(uiCopy.auth.sendCode)
const formRef = ref()

import { userGetCode, userLogin } from '@/api/user'

const form = ref({
  phone: '13686869696',
  // phone: '13838411438',
  code: '',
  radio: ''
})

// Modify to asynchronous request
const sendCode = async () => {
  try {
    await formRef.value.validateField('phone')
    const res = await userGetCode(form.value.phone)
    console.log('Verification code sent:', res.data)
    form.value.code = res.data

    disabled.value = true
    codeBtnMsg.value = uiCopy.auth.resendIn(60)
    let time = 60
    const timer = setInterval(() => {
      time--
      codeBtnMsg.value = uiCopy.auth.resendIn(time)
      if (time === 0) {
        clearInterval(timer)
        codeBtnMsg.value = uiCopy.auth.sendCode
        disabled.value = false
      }
    }, 1000)
  } catch (error) {
    console.error('Failed to send verification code:', error)
    disabled.value = false
    codeBtnMsg.value = uiCopy.auth.sendCode
  }
}

const userStore = useUserStore()
const login = async () => {
  // Log in
  try {
    await formRef.value.validate()
    const res = await userLogin(form.value)
    console.log('Registration token:', res.data)
    userStore.setToken(res.data)
    ElMessage.success(uiCopy.auth.loginSuccess)
    router.push('/index')
  } catch (error) {
    console.error('Registration failed:', error)
  }
}
const goBack = () => {
  console.log('goBack')
  window.history.back()
}

const rules = {
  phone: [
    { required: true, message: uiCopy.auth.phoneRequired, trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: uiCopy.auth.phoneInvalid,
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: uiCopy.auth.codeRequired, trigger: 'blur' }
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
        {{ uiCopy.auth.codeLoginTitle }}&nbsp;&nbsp;&nbsp;
      </div>
    </div>
    <div class="content">
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="0"
        class="login-form"
      >
        <el-form-item prop="phone">
          <div style="display: flex; justify-content: space-between">
            <el-input
              style="width: 60%"
              :placeholder="uiCopy.auth.phonePlaceholder"
              v-model="form.phone"
            >
            </el-input>
            <el-button
              style="width: 38%"
              @click="sendCode"
              type="success"
              :disabled="disabled"
              >{{ codeBtnMsg }}</el-button
            >
          </div>
        </el-form-item>

        <el-form-item prop="code">
          <el-input
            :placeholder="uiCopy.auth.codePlaceholder"
            v-model="form.code"
          >
          </el-input>
        </el-form-item>

        <div style="text-align: center; color: #8c939d; margin: 5px 0">
          {{ uiCopy.auth.autoCreateAccount }}
        </div>

        <el-form-item>
          <el-button
            @click="login"
            style="width: 100%; background-color: #f63; color: #fff"
            >{{ uiCopy.auth.login }}</el-button
          >
        </el-form-item>

        <div style="text-align: right; color: #333333; margin: 5px 0">
          <router-link to="/login">
            {{ uiCopy.auth.passwordLogin }}
          </router-link>
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
