import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8085',
  timeout: 15000
})

request.interceptors.response.use(
  (response) => response,
  (error) => {
    ElMessage.error(error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default request
