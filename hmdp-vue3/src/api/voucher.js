import request from '@/utils/request'
export const getVoucherList = (shopId) => request.get('/voucher/list/' + shopId)

// Get the seckill access token
export const issueSeckillAccessToken = (id) =>
  request.get('/voucher-order/seckill/token/' + id)

// Place a seckill order with the token
export const seckillVoucher = (id, accessToken) =>
  request.post(`/voucher-order/seckill/${id}`,
    null,
    { params: { accessToken } }
  )
// Poll to check whether the seckill order has been created
export const getSeckillOrderId = (orderId) =>
  request.post('/voucher-order/get/seckill/voucher/order-id', {
    orderId: String(orderId)
  })

// Query whether the user has already purchased the voucher when entering the page or after a successful seckill
export const getVoucherOrderIdByVoucherId = (voucherId) =>
  request.post('/voucher-order/get/seckill/voucher/order-id/by/voucher-id', {
    voucherId: String(voucherId)
  })

// Cancel a claimed voucher
export const cancelVoucherOrder = (voucherId) =>
  request.post('/voucher-order/cancel', {
    voucherId: String(voucherId)
  })

// Subscribe to restock reminders (join the automatic coupon issuance queue)
export const subscribeVoucher = (voucherId) =>
  request.post('/voucher/subscribe', {
    voucherId: String(voucherId)
  })

// Cancel the subscription (remove it from the queue)
export const unsubscribeVoucher = (voucherId) =>
  request.post('/voucher/unsubscribe', {
    voucherId: String(voucherId)
  })

// Query the current user subscription status (single voucher): 0 unsubscribed or cancelled; 1 subscribed; 2 auto-issued successfully
export const getSubscribeStatus = (voucherId) =>
  request.post('/voucher/get/subscribe/status', {
    voucherId: String(voucherId)
  })

// Batch query subscription statuses (used during page initialization)
export const getSubscribeStatusBatch = (voucherIdList) =>
  request.post('/voucher/get/subscribe/status/batch', {
    voucherIdList: (voucherIdList || []).map((id) => String(id))
  })
