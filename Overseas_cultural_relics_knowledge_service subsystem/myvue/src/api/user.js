import request from './request'

/**
 * 用户登录（账号为数字 user_id，如 1001）
 * @param {{ username: string, password: string }} params
 */
export function login (params) {
  return request.post('/api/v1/auth/login', params)
}

/**
 * 用户注册（写入本地 MySQL user 表）
 * @param {{ username: string, password: string, sex: string, tele: string }} params
 */
export function register (params) {
  return request.post('/api/v1/users', params)
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseLoginResponse (response) {
  const body = response.data
  if (body.code === 200 && body.data) {
    return {
      userId: body.data.objectId || body.data.userId,
      userName: body.data.username || body.data.nickname,
      token: body.data.accessToken
    }
  }
  const err = new Error(body.message || '登录失败')
  err.code = body.code
  throw err
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseRegisterResponse (response) {
  const body = response.data
  if (body.code === 200) {
    return { userId: body.data.objectId || body.data.userId, message: body.message }
  }
  const err = new Error(body.message || '注册失败')
  err.code = body.code
  throw err
}
