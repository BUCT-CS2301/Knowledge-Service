import request from './request'

/**
 * @param {{ keyword: string }} params
 */
export function searchObscure (params) {
  return request.post('/search/obscure', params)
}

/**
 * @param {{ v_1?: string, v_2?: string, v_3?: string, v_4?: string }} params
 */
export function searchMulti (params) {
  return request.post('/search/multiFind', params)
}

/**
 * @param {{ c: string, v_1?: string, v_2?: string, v_3?: string, v_4?: string }} params
 */
export function searchClassify (params) {
  return request.post('/search/classification', params)
}

/**
 * @param {'wordUp'|'wordDown'|'timeUp'|'timeDown'} way
 */
export function searchSort (way) {
  return request.get('/search/sort', { params: { way } })
}

/**
 * @param {import('axios').AxiosResponse} response
 * @returns {{ list: object[], message: string }}
 */
export function parseSearchResponse (response) {
  const body = response.data
  if (body.state === 200 && Array.isArray(body.data)) {
    return { list: body.data, message: body.message || '' }
  }
  const err = new Error(body.message || '未查询到相关文物')
  err.code = body.state
  throw err
}
