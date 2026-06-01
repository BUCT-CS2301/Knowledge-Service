import request from './request'

/** 当前登录用户的数字 ID（localStorage.username 存的是 user_id） */
export function getCurrentUserId () {
  return localStorage.getItem('username') || ''
}

/** 登出时清除会话与本地缓存的收藏/评论 */
export function clearUserSession () {
  const uid = localStorage.getItem('username')
  localStorage.removeItem('username')
  localStorage.removeItem('userpassword')
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('objectId')
  localStorage.removeItem('user_name')
  localStorage.removeItem('islogin')
  localStorage.removeItem('collections')
  localStorage.removeItem('comments')
  if (uid) {
    const users = JSON.parse(localStorage.getItem('users') || '[]')
    localStorage.setItem('users', JSON.stringify(users.filter(u => String(u.username) !== String(uid))))
  }
}

/**
 * 用户登录（username 为数字用户 ID 字符串，如 "1001"）
 * @param {{ username: string, password: string }} params
 */
export function login (params) {
  return request.post('/users/login', params)
}

/**
 * 用户注册
 * @param {{ username: string, password: string, sex: string, tele: string }} params
 */
export function register (params) {
  return request.post('/users/register', params)
}

/**
 * 获取用户详情
 * @param {string|number} userId
 */
export function getUserDetail (userId) {
  return request.post('/users/get_detail', { id: String(userId) })
}

/** 加载当前登录用户资料 */
export async function fetchCurrentUserProfile () {
  const userId = getCurrentUserId()
  if (!userId) return null
  const res = await getUserDetail(userId)
  return parseUserDetailResponse(res)
}

/**
 * 修改个人资料
 * @param {{ id: string, oldPassword: string, newPassword?: string, name: string, sex: string, tel: string }} params
 */
export function updateUserProfile (params) {
  return request.post('/user_admin/update', {
    id: String(params.id),
    oldPassword: params.oldPassword,
    newPassword: params.newPassword || params.oldPassword,
    name: params.name,
    sex: String(params.sex),
    tel: params.tel
  })
}

/** 我的收藏 */
export function getUserCollections (userId) {
  return request.post('/user_admin/collect', { id: String(userId) })
}

/** 我的评论 */
export function getUserComments (userId) {
  return request.post('/user_admin/comment', { username: String(userId) })
}

/** 取消收藏 */
export function deleteUserCollection (uid, rid, objectId) {
  const payload = { uid: String(uid), rid: String(rid) }
  if (objectId) payload.objectId = objectId
  return request.post('/user_admin/deleteCollect', payload)
}

/** 删除评论 */
export function deleteUserComment (commentId) {
  return request.post('/user_admin/deleteComment', { cid: String(commentId) })
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseLoginResponse (response) {
  const body = response.data
  if (body.state === 200 && body.data) {
    return {
      token: body.token,
      userId: body.data.user_id,
      user: body.data
    }
  }
  const err = new Error(body.message || '登录失败')
  err.code = body.state
  throw err
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseRegisterResponse (response) {
  const body = response.data
  if (body.state === 200) {
    return { userId: body.data, message: body.message || '注册成功' }
  }
  const err = new Error(body.message || '注册失败')
  err.code = body.state
  throw err
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseUserDetailResponse (response) {
  const body = response.data
  if (body.state === 200 && body.data) {
    return body.data
  }
  const err = new Error(body.message || '获取用户信息失败')
  err.code = body.state
  throw err
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseJsonResult (response) {
  const body = response.data
  if (body.state === 200) {
    return body.data
  }
  const err = new Error(body.message || '请求失败')
  err.code = body.state
  throw err
}
