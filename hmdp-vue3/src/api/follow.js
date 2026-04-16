import request from '@/utils/request'

// Follow a user
export const follow = (id, isFollow) => {
  return request({
    url: `/follow/${id}/${isFollow}`,
    method: 'put'
  })
}

// Unfollow a user
export const unfollow = (id) => {
  return request({
    url: `/follow/${id}`,
    method: 'delete'
  })
}

// Get the user follow list
export const getFollows = (id) => {
  return request({
    url: `/follow/${id}`,
    method: 'get'
  })
}

// Get the user fan list
export const getFans = (id) => {
  return request({
    url: `/follow/fans/${id}`,
    method: 'get'
  })
}

// Get the mutual follows list
export const getCommonFollows = (id) => {
  return request({
    url: `/follow/common/${id}`,
    method: 'get'
  })
}

// Check whether the user is followed
export const isFollowed = (id) => {
  return request({
    url: `/follow/or/not/${id}`,
    method: 'get'
  })
}

// Get the user note list
export const getBlogsOfUser = (id, current) => {
  return request({
    url: '/blog/of/user',
    method: 'get',
    params: {
      id,
      current
    }
  })
}
