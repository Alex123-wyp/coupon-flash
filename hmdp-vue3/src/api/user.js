import request from '@/utils/request'

// User: get the verification code
// export const userGetCode = (phone) =>
//   request.post('/user/code', { params: { phone } })

export const userGetCode = (phone) =>
  request.post('/user/code', null, { params: { phone } })
// User: log in
export const userLogin = (data) => request.post('/user/login', data)

// Home: get the homepage matrix data
export const indexQueryTypes = () => request.get('/shop-type/list')

// Home: get blog data for swipe scrolling
export const indexQueryHotBlogsScroll = (current) =>
  request.get('/blog/hot', null, { params: { current } })

// Home: like a post and then get blog data when clicking to view more
export const indexAddLike = (id) => request.put('/blog/like/' + id)
// Home: get blog data
export const indexQueryBlogById = (id) => request.get('/blog/' + id)
// User: get current logged-in user information
export const getUser = (id) => request.get(id ? `/user/${id}` : '/user/me')
// User: get blogs for the current logged-in user
export const getUserBlog = () => request.get('/blog/of/me')
// User: get user details
export const getUserInfo = (id) => request.get(`/user/info/${id}`)
