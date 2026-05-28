import request from './request'

/**
 * 用户登录（账号为数字 user_id，如 1001）
 * @param {{ username: string, password: string }} params
 */
export function login (params) {
  return request.post('/users/login', params)
}

/**
 * 用户注册（写入本地 MySQL user 表）
 * @param {{ username: string, password: string, sex: string, tele: string }} params
 */
export function register (params) {
  return request.post('/users/register', params)
}

/**
 * @param {import('axios').AxiosResponse} response
 */
export function parseLoginResponse (response) {
  const body = response.data
  if (body.state === 200 && body.data) {
    return {
      userId: body.data.user_id,
      userName: body.data.user_name,
      token: body.token
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
    return { userId: body.data, message: body.message }
  }
  const err = new Error(body.message || '注册失败')
  err.code = body.state
  throw err
}
