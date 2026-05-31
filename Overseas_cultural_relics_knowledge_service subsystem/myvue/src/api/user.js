import request from './request'

/**
 * 用户登录
 * @param {{ username: string, password: string }} params
 */
export function login (params) {
  return request.post('/api/v1/auth/login', params)
}

/**
 * 用户注册（创建用户）
 * @param {{ username: string, password: string, nickname: string, phone: string }} params
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
      accessToken: body.data.accessToken,
      refreshToken: body.data.refreshToken,
      expiresIn: body.data.expiresIn
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
    return { userId: body.data?.objectId, message: body.message || '注册成功' }
  }
  const err = new Error(body.message || '注册失败')
  err.code = body.code
  throw err
}
