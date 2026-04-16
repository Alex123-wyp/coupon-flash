import request from '@/utils/request'

// Upload an image
export function uploadBlogImage(data) {
  return request({
    url: '/upload/blog',
    method: 'post',
    data
  })
}

// Publish a blog post
export function publishBlog(data) {
  return request({
    url: '/blog',
    method: 'post',
    data
  })
}

// Query nearby shops
export function queryNearbyShops(params) {
  return request({
    url: '/shop/of/nearby',
    method: 'get',
    params
  })
}
// Query shops by name, or all shops when the name is empty
export const queryShopsByName = (name) =>
  request.get('/shop/of/name', null, { params: { name } })
// Create a blog post
export const createBlog = (data) => request.post('/blog', data)
// Delete a blog image
export const deleteBlogImage = (name) =>
  request.delete('/upload/blog/delete', null, { params: { name } })
