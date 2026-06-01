/** 开发与生产环境下的后端根地址（生产默认同域，便于 Nginx 反代）。 */
export function getApiRoot () {
  const fromEnv = import.meta.env.VITE_API_BASE
  if (fromEnv !== undefined && fromEnv !== '') {
    return fromEnv.replace(/\/$/, '')
  }
  // 生产构建未指定 VITE_API_BASE 时走同域 80 端口（Nginx → 8085），
  // 避免浏览器直连 8080（安全组常仅放行特定 IP 段，如 124.126.0.0/16）。
  if (import.meta.env.PROD) {
    return ''
  }
  return 'http://127.0.0.1:8085'
}
