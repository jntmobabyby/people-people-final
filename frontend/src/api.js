import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getSession } from './session'

const api = axios.create({
  baseURL: '/api',
  timeout: 8000
})

api.interceptors.request.use((config) => {
  const user = getSession()
  if (user?.userId) {
    config.headers['X-User-Id'] = user.userId
  }
  if (user?.token) {
    config.headers.Authorization = user.token
  }
  return config
})

api.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.success === false) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body?.data ?? body
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default api
