// Import axios
import axios from 'axios'
import JSONbig from 'json-bigint'
import { useUserStore } from '@/stores'
import router from '@/router'
import { uiCopy } from '@/constants/uiCopy'
const baseURL = '/api'
const instance = axios.create({
  // TODO 1. Set the base address and timeout period
  baseURL,
  timeout: 10000,
  // Use json-bigint to store numbers beyond the safe integer range as strings and avoid precision loss
  transformResponse: [
    function (data) {
      try {
        // The data passed by axios is the raw string
        const parser = JSONbig({ storeAsString: true })
        return data ? parser.parse(data) : data
      } catch (e) {
        // Return the original value if it is not JSON or parsing fails
        return data
      }
    }
  ]
})
// Request interceptor
instance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    // TODO 2. Add token to request header
    if (userStore.token) {
      config.headers.Authorization = `${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)
// Response interceptor
instance.interceptors.response.use(
  (response) => {
    // Check the execution result
    if (response.status !== 200) {
      // TODO 3. Business processing failed and a pop-up window prompt was given.
      ElMessage.error(response.data.errorMsg || uiCopy.request.serviceError)
      return Promise.reject(response.data.errorMsg)
    }
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      // TODO 4. Not logged in or the token has expired. Jump to the login page.
      ElMessage.error(uiCopy.request.loginRequired)
      router.push('/login')
      return
    }
    // TODO 5. Handle common errors
    ElMessage.error(error.response?.data.message || uiCopy.request.serviceError)
    return Promise.reject(error)
  }
)
export default instance
export { baseURL }
