<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import { uiCopy } from '@/constants/uiCopy'
import {
  uploadBlogImage,
  deleteBlogImage,
  queryShopsByName,
  createBlog
} from '@/api/blog'
// import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

// Data definitions
const fileInput = ref(null)
const fileList = ref([]) // Uploaded image list
const params = reactive({
  title: '',
  content: ''
})
const showDialog = ref(false)
const shops = ref([]) // Shop options
const shopName = ref('') // Selected shop name
const selectedShop = ref({}) // Selected shop object

// Lifecycle hooks
onMounted(() => {
  checkLogin()
  queryShops()
})

// Method definitions
const checkLogin = () => {
  // Get the token
  const token = userStore.getToken()
  if (!token) {
    router.push('/login')
    return
  }

  // TODO: Query user information
  // Here you can add API calls to obtain user information
}

const queryShops = () => {
  queryShopsByName(shopName.value)
    .then((res) => {
      shops.value = res.data
    })
    .catch((err) => {
      ElMessage.error(err.message || uiCopy.blog.loadShopListFailed)
    })
}

const selectShop = (shop) => {
  selectedShop.value = shop
  showDialog.value = false
}

// const submitBlog = () => {
//   if (!params.title) {
//     ElMessage.warning('Please enter a title')
//     return
//   }

//   if (!params.content) {
//     ElMessage.warning('Please enter content')
//     return
//   }

//   if (fileList.value.length === 0) {
//     ElMessage.warning('Please upload at least one picture')
//     return
//   }

//   if (!selectedShop.value.id) {
//     ElMessage.warning('Please select the associated merchant')
//     return
//   }

//   const data = {
//     ...params,
//     images: fileList.value.join(','),
//     shopId: selectedShop.value.id
//   }

//   createBlog(data)
//     .then(() => {
//       ElMessage.success('Published successfully')
//       router.push('/infoHtml')
//     })
//     .catch((err) => {
//       ElMessage.error(err.message || 'Publishing failed')
//     })
// }

const openFileDialog = () => {
  fileInput.value.click()
}

const fileSelected = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  uploadBlogImage(formData)
    .then((res) => {
      fileList.value.push('/imgs' + res.data)
    })
    .catch((err) => {
      ElMessage.error(err.message || uiCopy.blog.uploadFailed)
    })
}

const deletePic = (index) => {
  const imagePath = fileList.value[index]
  deleteBlogImage(imagePath)
    .then(() => {
      fileList.value.splice(index, 1)
    })
    .catch((err) => {
      ElMessage.error(err.message || uiCopy.blog.deleteFailed)
    })
}

const goBack = () => {
  router.back()
}

const publishBlog = async () => {
  if (!params.title) {
    ElMessage.warning(uiCopy.blog.enterTitle)
    return
  }
  if (!params.content) {
    ElMessage.warning(uiCopy.blog.enterContent)
    return
  }
  if (fileList.value.length === 0) {
    ElMessage.warning(uiCopy.blog.uploadRequired)
    return
  }

  try {
    const blogData = {
      title: params.title,
      content: params.content,
      images: fileList.value.join(','),
      shopId: selectedShop.value ? selectedShop.value.id : null
    }

    await createBlog(blogData)
    ElMessage.success(uiCopy.blog.publishSuccess)
    router.push('/infoHtml')
  } catch (error) {
    console.error('Failed to publish the post', error)
    ElMessage.error(uiCopy.blog.publishFailed)
  }
}
</script>

<template>
  <div class="blog-edit">
    <div class="header">
      <div class="left" @click="goBack">
        <i class="iconfont icon-arrow-left"></i>
      </div>
      <div class="title">{{ uiCopy.blog.publishTitle }}</div>
      <div class="right" @click="publishBlog">
        {{ uiCopy.blog.publishAction }}
      </div>
    </div>

    <div class="upload-box">
      <input
        type="file"
        @change="fileSelected"
        name="file"
        ref="fileInput"
        style="display: none"
      />
      <div class="upload-btn" @click="openFileDialog">
        <i class="iconfont icon-camera"></i>
        <span>{{ uiCopy.blog.uploadImage }}</span>
      </div>
      <div class="pic-list" v-if="fileList.length > 0">
        <div class="pic-box" v-for="(f, i) in fileList" :key="i">
          <img :src="`/src/assets${f}`" :alt="uiCopy.common.uploadedImageAlt" />
          <div class="delete" @click="deletePic(i)">
            <i class="iconfont icon-close"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="blog-title">
      <input
        type="text"
        v-model="params.title"
        :placeholder="uiCopy.blog.titlePlaceholder"
      />
    </div>

    <div class="blog-content">
      <textarea
        v-model="params.content"
        :placeholder="uiCopy.blog.contentPlaceholder"
      ></textarea>
    </div>

    <div class="divider"></div>

    <div class="blog-shop" @click="showDialog = true">
      <div class="shop-left">{{ uiCopy.blog.linkedShop }}</div>
      <div v-if="selectedShop.name">{{ selectedShop.name }}</div>
      <div v-else>
        {{ uiCopy.blog.selectPrompt }}&nbsp;<el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <div class="mask" v-show="showDialog" @click="showDialog = false"></div>

    <el-dialog
      v-model="showDialog"
      :title="uiCopy.blog.chooseShop"
      width="90%"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <div class="search-bar">
        <div class="city-select">
          {{ uiCopy.common.city }} <el-icon><ArrowRight /></el-icon>
        </div>
        <div class="search-input">
          <el-icon @click="queryShops"><Search /></el-icon>
          <el-input
            v-model="shopName"
            :placeholder="uiCopy.blog.searchShopName"
            @keyup.enter="queryShops"
          ></el-input>
        </div>
      </div>

      <div class="shop-list">
        <div
          v-for="s in shops"
          :key="s.id"
          class="shop-item"
          @click="selectShop(s)"
        >
          <div class="shop-name">{{ s.name }}</div>
          <div>{{ s.area }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
@import '@/assets/css/blog-edit.css';
</style>
