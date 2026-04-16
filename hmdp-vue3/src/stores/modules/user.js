import { defineStore } from 'pinia'
import { ref } from 'vue'

// User store module
export const useUserStore = defineStore(
  'Hmdp-User',
  () => {
    const token = ref('') // 定义 token
    const setToken = (t) => {
      console.log('设置token:', t)
      token.value = t
      console.log('当前token:', token.value)
    } // 设置 token
    const getToken = () => {
      console.log('获取token:', token.value)
      return token.value
    }

    // Create a ref of personal information
    const userInfo = ref({})
    const getUserInfo = () => userInfo.value
    const setUserInfo = (obj) => (userInfo.value = obj)
    const resetUserInfo = () => {
      userInfo.value = {}
    }

    return {
      token,
      setToken,
      getToken,
      userInfo,
      getUserInfo,
      setUserInfo,
      resetUserInfo
    }
  },
  {
    persist: true // 持久化
  }
)
