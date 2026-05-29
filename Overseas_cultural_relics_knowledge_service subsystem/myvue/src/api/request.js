import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getApiRoot } from '@/config/api'

const request = axios.create({
  baseURL: getApiRoot(),
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
