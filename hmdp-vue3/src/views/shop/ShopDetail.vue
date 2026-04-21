<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, ArrowRight, Location, Timer } from '@element-plus/icons-vue'
import { ElLoading } from 'element-plus'
import { getShopById } from '@/api/shop'
import {
  getVoucherList,
  issueSeckillAccessToken,
  seckillVoucher,
  getSeckillOrderId,
  getVoucherOrderIdByVoucherId,
  cancelVoucherOrder,
  subscribeVoucher,
  unsubscribeVoucher,
  getSubscribeStatusBatch
} from '@/api/voucher'
import { useUserStore } from '@/stores'
import {
  formatDiscountLabel,
  formatReviewCount,
  formatSeckillWindow,
  formatVoucherStock,
  uiCopy
} from '@/constants/uiCopy'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const rate = ref(4.5)

// Reactive data
const shop = ref({})
const vouchers = ref([])
const seckillInProgress = ref(false)
// Purchased status map: voucherId -> boolean
const purchasedMap = ref({})
// Subscription status map: voucherId -> 0|1|2 (0 unsubscribed/cancelled, 1 subscribed, 2 auto-issued successfully)
const subscribeStatusMap = ref({})

const isPurchased = (voucherId) => {
  return !!purchasedMap.value[String(voucherId)]
}

// Query purchased status for all vouchers (logged-in user)
const refreshPurchaseStatus = async () => {
  try {
    // Skip checks when not logged in to avoid triggering a 401 redirect
    if (!userStore.token) return
    const list = vouchers.value || []
    await Promise.all(
      list.map(async (v) => {
        if (!v?.id) return
        try {
          const res = await getVoucherOrderIdByVoucherId(String(v.id))
          purchasedMap.value[String(v.id)] = !!res?.data
        } catch (e) {
          // Ignore individual item errors and preserve the current state
        }
      })
    )
  } catch (e) {
    // Ignore aggregate errors
  }
}

// Batch query subscription statuses (logged-in user)
const refreshSubscribeStatusBatch = async () => {
  try {
    if (!userStore.token) return
    const ids = (vouchers.value || []).map((v) => v.id).filter(Boolean)
    if (!ids.length) return
    const res = await getSubscribeStatusBatch(ids)
    const arr = Array.isArray(res?.data) ? res.data : []
    const map = {}
    for (const item of arr) {
      if (!item) continue
      const vid = String(item.voucherId)
      const st = Number(item.subscribeStatus)
      map[vid] = Number.isFinite(st) ? st : 0
      // If auto-issue succeeds, also mark the voucher as purchased locally as a safeguard
      if (st === 2) {
        purchasedMap.value[vid] = true
      }
    }
    subscribeStatusMap.value = { ...subscribeStatusMap.value, ...map }
  } catch (e) {
    // Fail silently
  }
}

const getSubscribeCode = (voucherId) => {
  return Number(subscribeStatusMap.value[String(voucherId)] ?? 0)
}
const isSubscribed = (voucherId) => getSubscribeCode(voucherId) === 1
const isSubscribeSuccess = (voucherId) => getSubscribeCode(voucherId) === 2

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const pollSeckillOrder = async (
  orderId,
  { delay = 800, timeoutMs = 11000 } = {}
) => {
  const end = Date.now() + timeoutMs
  while (Date.now() < end) {
    try {
      const { data } = await getSeckillOrderId(String(orderId))
      if (data) {
        ElMessage.success(uiCopy.shop.purchaseSuccess)
        return data
      }
    } catch (e) {
      // Brief exception, continue to try again
    }
    const remaining = end - Date.now()
    await sleep(Math.min(delay, Math.max(0, remaining)))
  }
  ElMessage.warning(uiCopy.shop.purchasePending)
  return null
}

// Get shop details
const queryShopById = async (shopId) => {
  try {
    const { data } = await getShopById(shopId)
    console.log('getShopByIddata', data)
    data.images = data.images.split(',')
    shop.value = data
  } catch (error) {
    console.error(error)
    ElMessage.error(uiCopy.shop.loadShopFailed)
  }
}

// Get the voucher list while preserving backend field types as strings
const queryVoucher = async (shopId) => {
  try {
    const { data } = await getVoucherList(shopId)
    vouchers.value = data || []
    // Query the purchased status of each voucher after loading
    await refreshPurchaseStatus()
    // Batch query subscription statuses
    await refreshSubscribeStatusBatch()
  } catch (error) {
    console.error(error)
    ElMessage.error(uiCopy.shop.loadVoucherFailed)
  }
}

// Format time
const formatTime = (v) => formatSeckillWindow(v.beginTime, v.endTime)

// Format the price and support both strings and numbers
const formatPrice = (price) => {
  const n = Number(price)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

// Check whether it has not started
const isNotBegin = (v) => {
  return new Date(v.beginTime).getTime() > new Date().getTime()
}

// Check whether it has ended
const isEnd = (v) => {
  return new Date(v.endTime).getTime() < new Date().getTime()
}

// Seckill purchase flow (get the token first, then place the order with the token)
const seckill = async (v) => {
  if (!userStore.token) {
    ElMessage.error(uiCopy.shop.loginRequired)
    setTimeout(() => {
      router.push('/login')
    }, 200)
    return
  }

  if (isNotBegin(v)) {
    ElMessage.error(uiCopy.shop.notStarted)
    return
  }

  if (isEnd(v)) {
    ElMessage.error(uiCopy.shop.ended)
    return
  }

  if (Number(v.stock) < 1) {
    ElMessage.error(uiCopy.shop.outOfStock)
    return
  }

  // Prevent duplicate purchases after a successful purchase
  if (isPurchased(v.id)) {
    ElMessage.error(uiCopy.shop.alreadyPurchased)
    return
  }

  let loading = null
  try {
    if (seckillInProgress.value) {
      // Confirmation is already in progress and the overlay is still shown, so return early to avoid duplicate clicks
      return
    }
    seckillInProgress.value = true
    loading = ElLoading.service({
      fullscreen: true,
      lock: true,
      text: uiCopy.shop.confirmingOrder,
      background: 'rgba(0,0,0,0.35)',
      customClass: 'seckill-overlay'
    })
    // 1) Acquire the access token first
    const tokenRes = await issueSeckillAccessToken(v.id)
    if (!tokenRes?.success || !tokenRes?.data) {
      ElMessage.error(tokenRes?.errorMsg || uiCopy.shop.tokenFailed)
      return
    }
    const accessToken = String(tokenRes.data)
    // 2) Submit the order with the token
    const res = await seckillVoucher(v.id, accessToken)
    // Poll for order confirmation only when the seckill API call succeeds
    if (res && res.success) {
      const order = await pollSeckillOrder(String(res.data), {
        delay: 800,
        timeoutMs: 11000
      })
      // After receiving the order successfully, re-query the purchase status and disable the button
      if (order) {
        try {
          const check = await getVoucherOrderIdByVoucherId(String(v.id))
          if (check?.data) {
            purchasedMap.value[String(v.id)] = true
          }
        } catch (e) {
          // Ignore errors so the current state is not affected
        }
      }
    } else {
      ElMessage.error(res?.errorMsg || uiCopy.shop.purchaseFailed)
      return
    }
  } catch (error) {
    console.error(error)
    ElMessage.error(uiCopy.shop.purchaseFailed)
  } finally {
    seckillInProgress.value = false
    if (loading) loading.close()
  }
}

// Cancel a claimed voucher
const cancelVoucher = async (v) => {
  if (!userStore.token) {
    ElMessage.error(uiCopy.shop.loginRequired)
    setTimeout(() => {
      router.push('/login')
    }, 200)
    return
  }
  if (!v?.id) return
  try {
    const res = await cancelVoucherOrder(String(v.id))
    if (res?.success && String(res.data) === 'true') {
      ElMessage.success(uiCopy.shop.cancelSuccess)
      // Update the local state and refresh the list to sync stock
      purchasedMap.value[String(v.id)] = false
      await queryVoucher(route.params.id)
    } else {
      ElMessage.error(res?.errorMsg || uiCopy.shop.cancelFailed)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error(uiCopy.shop.cancelFailed)
  }
}

// Subscribe to restock reminders
const subscribeToVoucher = async (v) => {
  if (!userStore.token) {
    ElMessage.error(uiCopy.shop.loginRequired)
    setTimeout(() => router.push('/login'), 200)
    return
  }
  if (!v?.id) return
  try {
    const res = await subscribeVoucher(String(v.id))
    if (res?.success) {
      ElMessage.success(uiCopy.shop.subscribeSuccess)
      subscribeStatusMap.value[String(v.id)] = 1
    } else {
      ElMessage.error(res?.errorMsg || uiCopy.shop.subscribeFailed)
    }
  } catch (e) {
    ElMessage.error(uiCopy.shop.subscribeFailed)
  }
}

// Cancel restock reminder subscription
const unsubscribeFromVoucher = async (v) => {
  if (!userStore.token) {
    ElMessage.error(uiCopy.shop.loginRequired)
    setTimeout(() => router.push('/login'), 200)
    return
  }
  if (!v?.id) return
  try {
    const res = await unsubscribeVoucher(String(v.id))
    if (res?.success) {
      ElMessage.success(uiCopy.shop.unsubscribeSuccess)
      subscribeStatusMap.value[String(v.id)] = 0
    } else {
      ElMessage.error(res?.errorMsg || uiCopy.shop.unsubscribeFailed)
    }
  } catch (e) {
    ElMessage.error(uiCopy.shop.unsubscribeFailed)
  }
}

// Go back to the previous page
const goBack = () => {
  router.back()
}

// Filtered coupon list
const filteredVouchers = computed(() => {
  return vouchers.value.filter((v) => !isEnd(v))
})

// Initialize
onMounted(() => {
  const shopId = route.params.id
  console.log('shopId', shopId)
  queryShopById(shopId)
  queryVoucher(shopId)
})
</script>

<template>
  <div class="shop-detail-container">
    <!-- Header -->
    <div class="header">
      <div class="header-back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <div class="header-title"></div>
      <div class="header-share">...</div>
    </div>

    <!-- Shop information -->
    <div class="shop-info-box">
      <div class="shop-title">{{ shop.name }}</div>
      <div class="shop-rate">
        <el-rate
          v-model="shop.score"
          disabled
          :max="5"
          :value="shop.score / 10"
          text-color="#F63"
          show-score
        />
        <span>{{ formatReviewCount(shop.comments) }}</span>
      </div>
      <div class="shop-rate-info">{{ uiCopy.shop.reviewBreakdown }}</div>
      <div class="shop-rank">
        <img src="/src/assets/imgs/bd.png" width="63" height="20" alt="" />
        <span>{{ uiCopy.shop.ranking }}</span>
        <div>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
      <div class="shop-images">
        <div v-for="(s, i) in shop.images" :key="i">
          <img :src="s" alt="" />
        </div>
      </div>
      <div class="shop-address">
        <div>
          <el-icon><Location /></el-icon>
        </div>
        <span>{{ shop.address }}</span>
        <div
          style="width: 10px; flex-grow: 2; text-align: center; color: #e1e2e3"
        >
          |
        </div>
        <div style="margin: 0 5px">
          <img
            src="https://p0.meituan.net/travelcube/bf684aa196c870810655e45b1e52ce843484.png@24w_16h_40q"
            alt=""
          />
        </div>
        <div>
          <img
            src="https://p0.meituan.net/travelcube/9277ace32123e0c9f59dedf4407892221566.png@24w_24h_40q"
            alt=""
          />
        </div>
      </div>
    </div>

    <div class="shop-divider"></div>

    <!-- Business hours -->
    <div class="shop-open-time">
      <span>
        <el-icon><Timer /></el-icon>
      </span>
      <div>{{ uiCopy.shop.businessHours }}</div>
      <div>{{ shop.openHours }}</div>
      <span class="line-right">
        {{ uiCopy.common.viewDetails }}
        <el-icon><ArrowRight /></el-icon>
      </span>
    </div>

    <div class="shop-divider"></div>

    <!-- Vouchers -->
    <div class="shop-voucher">
      <div>
        <span class="voucher-icon">{{ uiCopy.shop.voucherLabel }}</span>
        <span style="font-weight: bold">{{ uiCopy.shop.voucherTitle }}</span>
      </div>
      <div class="voucher-box" v-for="v in filteredVouchers" :key="v.id">
        <div class="voucher-circle">
          <div class="voucher-b"></div>
          <div class="voucher-b"></div>
          <div class="voucher-b"></div>
        </div>
        <div class="voucher-left">
          <div class="voucher-title">{{ v.title }}</div>
          <div class="voucher-subtitle">{{ v.subTitle }}</div>
          <div class="voucher-price">
            <div>￥ {{ formatPrice(v.payValue) }}</div>
            <span>{{ formatDiscountLabel(v.payValue, v.actualValue) }}</span>
          </div>
        </div>
        <div class="voucher-right">
          <div v-if="v.type" class="seckill-box">
            <div
              class="voucher-btn"
              :class="{
                'disable-btn': isNotBegin(v) || v.stock < 1 || isPurchased(v.id)
              }"
              @click="seckill(v)"
            >
              {{ uiCopy.shop.flashSale }}
            </div>
            <div class="seckill-stock">
              {{ formatVoucherStock(v.stock) }}
            </div>
            <div class="seckill-time">{{ formatTime(v) }}</div>
            <div v-if="isPurchased(v.id)" class="seckill-status">
              <span class="purchased-tag">{{ uiCopy.shop.claimed }}</span>
              <span class="cancel-link" @click="cancelVoucher(v)">{{
                uiCopy.shop.cancelClaim
              }}</span>
            </div>
            <!-- Show the restock subscription reminder when stock is 0 and the voucher has not been purchased -->
            <div
              v-else-if="Number(v.stock) < 1 && !isPurchased(v.id)"
              class="subscribe-box"
            >
              <div v-if="isSubscribed(v.id)" class="subscribe-status">
                <span class="subscribed-tag">{{
                  uiCopy.shop.subscribedReminder
                }}</span>
                <span
                  class="cancel-subscribe"
                  @click="unsubscribeFromVoucher(v)"
                  >{{ uiCopy.shop.unsubscribe }}</span
                >
              </div>
              <div v-else class="subscribe-status">
                <span class="subscribe-link" @click="subscribeToVoucher(v)">{{
                  uiCopy.shop.restockReminder
                }}</span>
              </div>
            </div>
          </div>
          <div class="voucher-btn" v-else>{{ uiCopy.shop.claimNow }}</div>
        </div>
      </div>
    </div>

    <div class="shop-divider"></div>

    <!-- Comments -->
    <div class="shop-comments">
      <div class="comments-head">
        <div>{{ uiCopy.shop.reviewsTitle }} <span>(119)</span></div>
        <div>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
      <div class="comment-tags">
        <div class="tag" v-for="tag in uiCopy.shop.tags" :key="tag">
          {{ tag }}
        </div>
      </div>
      <div class="comment-list">
        <div class="comment-box" v-for="i in 3" :key="i">
          <div class="comment-icon">
            <img
              src="https://p0.meituan.net/userheadpicbackend/57e44d6eba01aad0d8d711788f30a126549507.jpg%4048w_48h_1e_1c_1l%7Cwatermark%3D0"
              alt=""
            />
          </div>
          <div class="comment-info">
            <div class="comment-user">
              {{ uiCopy.samples.reviewer }} <span>Lv5</span>
            </div>
            <div style="display: flex">
              {{ uiCopy.samples.rating }}
              <el-rate disabled v-model="rate"></el-rate>
            </div>
            <div style="padding: 5px 0; font-size: 14px">
              {{ uiCopy.samples.reviewText }}
            </div>
            <div class="comment-images">
              <img
                src="https://qcloud.dpfile.com/pc/6T7MfXzx7USPIkSy7jzm40qZSmlHUF2jd-FZUL6WpjE9byagjLlrseWxnl1LcbuSGybIjx5eX6WNgCPvcASYAw.jpg"
                alt=""
              />
              <img
                src="https://qcloud.dpfile.com/pc/sZ5q-zgglv4VXEWU71xCFjnLM_jUHq-ylq0GKivtrz3JksWQ1f7oBWZsxm1DWgcaGybIjx5eX6WNgCPvcASYAw.jpg"
                alt=""
              />
              <img
                src="https://qcloud.dpfile.com/pc/xZy6W4NwuRFchlOi43DVLPFsx7KWWvPqifE1JTe_jreqdsBYA9CFkeSm2ZlF0OVmGybIjx5eX6WNgCPvcASYAw.jpg"
                alt=""
              />
              <img
                src="https://qcloud.dpfile.com/pc/xZy6W4NwuRFchlOi43DVLPFsx7KWWvPqifE1JTe_jreqdsBYA9CFkeSm2ZlF0OVmGybIjx5eX6WNgCPvcASYAw.jpg"
                alt=""
              />
            </div>
            <div>{{ uiCopy.samples.stats }}</div>
          </div>
        </div>
        <div
          style="
            display: flex;
            justify-content: space-between;
            padding: 15px 0;
            border-top: 1px solid #f1f1f1;
            margin-top: 10px;
          "
        >
          <div>{{ uiCopy.samples.viewAllReviews }}</div>
          <div>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <div class="shop-divider"></div>

    <div class="copyright">copyright ©2021 hmdp.com</div>
  </div>
</template>

<style scoped>
.shop-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-back-btn {
  padding: 5px;
  cursor: pointer;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}

.header-share {
  padding: 5px;
  cursor: pointer;
}

.shop-info-box {
  padding: 15px;
  background-color: #fff;
}

.shop-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.shop-rate {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.shop-rate-info {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.shop-rank {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.shop-images {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  overflow-x: auto;
}

.shop-images img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.shop-address {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.shop-divider {
  height: 10px;
  background-color: #f5f5f5;
}

.shop-open-time {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #fff;
}

.shop-open-time > div {
  margin-left: 10px;
}

.line-right {
  margin-left: auto;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
}

.shop-voucher {
  padding: 15px;
  background-color: #fff;
}

.voucher-icon {
  background-color: #f63;
  color: #fff;
  padding: 2px 5px;
  border-radius: 2px;
  margin-right: 5px;
}

.voucher-box {
  display: flex;
  margin-top: 10px;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 12px 12px 12px 16px;
}

.voucher-circle {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.voucher-b {
  width: 10px;
  height: 10px;
  background-color: #f5f5f5;
  border-radius: 50%;
}

.voucher-left {
  flex: 1;
  padding: 10px;
  border-right: 1px solid #f0f0f0;
}

.voucher-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.voucher-subtitle {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.voucher-price {
  color: #f63;
  font-size: 16px;
  display: flex;
  align-items: baseline;
  gap: 5px;
}

/* Enlarge the price digits and present the discount more lightly */
.voucher-price div {
  font-size: 22px;
  font-weight: 700;
}
.voucher-price span {
  font-size: 14px;
  color: #ff6d6d;
  padding: 0 6px;
  line-height: 18px;
  border-radius: 9px;
  background-color: #fff2f0;
}

.voucher-right {
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.voucher-btn {
  width: 100%;
  max-width: 240px;
  height: 40px;
  line-height: 40px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  text-align: center;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: #fff;
  background: linear-gradient(135deg, #ff7a45 0%, #ff4d4f 100%);
  box-shadow: 0 6px 14px rgba(255, 77, 79, 0.25);
}

.disable-btn {
  opacity: 0.7;
  cursor: not-allowed;
  background: #d9d9d9;
  box-shadow: none;
  color: #fff;
}

.seckill-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
}

.seckill-stock {
  font-size: 12px;
  color: #8a8a8a;
  margin-top: 4px;
}
.seckill-stock span {
  display: inline-block;
  padding: 0 6px;
  line-height: 18px;
  border-radius: 9px;
  background-color: #fff5f0;
  color: #ff4d4f;
  margin: 0 2px;
}

.seckill-time {
  font-size: 12px;
  color: #8a8a8a;
}

.seckill-status {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
}
.purchased-tag {
  display: inline-block;
  padding: 2px 8px;
  line-height: 20px;
  border-radius: 10px;
  background-color: #fff2f0;
  color: #ff4d4f;
  font-weight: 600;
}
.cancel-link {
  display: inline-block;
  padding: 4px 12px;
  line-height: 20px;
  border-radius: 12px;
  border: 1px solid #409eff;
  background-color: #409eff;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}
.cancel-link:hover {
  background-color: #337ecc;
  border-color: #337ecc;
}

/* Restock reminder subscription styles */
.subscribe-box {
  margin-top: 6px;
}
.subscribe-status {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
  font-size: 14px;
  color: #666;
}
.subscribed-tag {
  display: inline-block;
  padding: 2px 8px;
  line-height: 20px;
  border-radius: 10px;
  background-color: #e6f4ff;
  color: #1677ff;
  font-weight: 600;
}
.subscribe-link {
  display: inline-block;
  padding: 4px 12px;
  line-height: 20px;
  border-radius: 12px;
  border: 1px solid #ffa940;
  background-color: #ffa940;
  color: #fff;
  cursor: pointer;
}
.subscribe-link:hover {
  background-color: #fa8c16;
  border-color: #fa8c16;
}
.cancel-subscribe {
  display: inline-block;
  padding: 4px 12px;
  line-height: 20px;
  border-radius: 12px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  color: #606266;
  cursor: pointer;
}
.cancel-subscribe:hover {
  background-color: #f5f5f5;
}
.subscribe-tip {
  color: #8a8a8a;
  font-size: 12px;
}

/* Unify the size and styles of subscription buttons so both buttons share the same width and height */
.subscribe-link,
.cancel-subscribe,
.subscribed-tag {
  display: inline-block;
  width: 180px;
  height: 36px;
  line-height: 36px;
  border-radius: 18px;
  padding: 0 14px;
  text-align: center;
  font-weight: 600;
}

.shop-comments {
  padding: 15px;
  background-color: #fff;
}

.comments-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  padding: 5px 10px;
  background-color: #f5f5f5;
  border-radius: 15px;
  font-size: 14px;
}

.comment-box {
  display: flex;
  margin-bottom: 15px;
}

.comment-icon img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-info {
  flex: 1;
}

.comment-user span {
  color: #666;
  font-size: 12px;
  margin-left: 5px;
}

.comment-images {
  display: flex;
  gap: 5px;
  margin: 10px 0;
}

.comment-images img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.copyright {
  text-align: center;
  padding: 15px;
  color: #999;
  font-size: 12px;
}

/* Optimization: loading overlay and text styles during seckill confirmation (applies globally) */
:global(.seckill-overlay) {
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(2px);
  z-index: 2001;
}

:global(.seckill-overlay .el-loading-spinner) {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 16px 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:global(.seckill-overlay .el-loading-spinner .circular) {
  width: 42px;
  height: 42px;
  stroke: #ff4d4f; /* Harmonized with the theme color */
}

:global(.seckill-overlay .el-loading-text) {
  margin-top: 2px;
  font-size: 20px; /* Increase the text size */
  font-weight: 700;
  color: #333;
  letter-spacing: 0.3px;
}
</style>
