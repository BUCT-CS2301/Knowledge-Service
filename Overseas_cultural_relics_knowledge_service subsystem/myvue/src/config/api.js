/** 开发与生产环境下的后端根地址（生产默认同域，便于 Nginx 反代）。 */
export function getApiRoot () {
  const fromEnv = import.meta.env.VITE_API_BASE
  if (fromEnv !== undefined && fromEnv !== '') {
    return fromEnv.replace(/\/$/, '')
  }
  return import.meta.env.DEV ? 'http://localhost:8085' : ''
}
