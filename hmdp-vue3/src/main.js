import { createApp } from 'vue'
import pinia from '@/stores'

import App from '@/App.vue'
import router from '@/router'
// Import global styles
import '@/assets/css/main.css'
// Import the full Element Plus styles so Loading overlays and related styles work correctly
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(pinia)
app.use(router)

app.mount('#app')
