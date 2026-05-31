<template>
  <div class="info-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-user-solid"></i>
        个人信息
      </h1>
      <p class="page-desc">查看您的个人资料</p>
    </div>

    <div class="info-card">
      <el-descriptions
        :column="2"
        border
        :size="large"
        class="info-table">

        <el-descriptions-item label="用户名">
          <span class="value">{{userInfo.user_name}}</span>
        </el-descriptions-item>

        <el-descriptions-item label="手机号">
          <span class="value">{{ userInfo.tele || '未设置' }}</span>
        </el-descriptions-item>

        <el-descriptions-item label="电子邮箱">
          <span class="value">{{ userInfo.email || '未设置' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="action-area">
      <router-link to="/personpage/changeinfo" class="edit-btn">
        <el-button type="primary">
          <i class="el-icon-edit"></i>
          修改信息
        </el-button>
      </router-link>
    </div>
  </div>
</template>

<script>
import request from '@/api/request'
var storage = window.localStorage
export default {
  data () {
    return {
      userInfo: {
        user_name: '未登录',
        tele: '',
        email: ''
      }
    }
  },
  methods: {
    async pageInit () {
      const accessToken = storage.getItem('accessToken')
      const username = storage.getItem('username')
      const objectId = storage.getItem('objectId')

      if (!username) {
        this.userInfo.user_name = '游客'
        return
      }

      // 优先尝试从后端获取用户详细信息
      if (accessToken && objectId) {
        try {
          const response = await request.get(`/api/v1/users/${objectId}`, {
            headers: {
              Authorization: `Bearer ${accessToken}`
            }
          })

          if (response.data.code === 200) {
            const data = response.data.data
            this.userInfo = {
              user_name: data.nickname || data.username || '未设置',
              tele: data.phone || data.tele || '',
              email: data.email || ''
            }
            return
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }

      // 如果没有objectId，先获取基础信息
      if (accessToken) {
        try {
          const response = await request.get('/api/v1/auth/current-user', {
            headers: {
              Authorization: `Bearer ${accessToken}`
            }
          })

          if (response.data.code === 200) {
            const data = response.data.data
            localStorage.setItem('objectId', data.objectId)
            
            // 尝试用获取到的objectId获取详细信息
            try {
              const detailResponse = await request.get(`/api/v1/users/${data.objectId}`, {
                headers: {
                  Authorization: `Bearer ${accessToken}`
                }
              })
              if (detailResponse.data.code === 200) {
                const detailData = detailResponse.data.data
                this.userInfo = {
                  user_name: detailData.nickname || detailData.username || '未设置',
                  tele: detailData.phone || detailData.tele || '',
                  email: detailData.email || ''
                }
                return
              }
            } catch (detailError) {
              console.error('获取用户详细信息失败:', detailError)
            }
            
            // 如果获取详细信息失败，使用基础信息
            this.userInfo = {
              user_name: data.nickname || data.username || '未设置',
              tele: '',
              email: ''
            }
            return
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }

      // 后端不可用时，使用本地存储作为备用
      const users = JSON.parse(storage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.userInfo = {
          user_name: user.user_name || username,
          tele: user.tele,
          email: user.email
        }
      } else {
        this.userInfo.user_name = username || '游客'
      }
    }
  },
  created () {
    this.pageInit()
  }
}
</script>

<style scoped>
.info-page {
  min-height: 400px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.info-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
}

.info-table {
  background: white;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #666;
  background: #f8f9fa;
}

.value {
  color: #333;
  font-weight: 400;
}

.action-area {
  margin-top: 24px;
  text-align: right;
}

.edit-btn {
  text-decoration: none;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #8B4513 0%, #CD853F 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 24px;

  &:hover {
    background: linear-gradient(135deg, #6B3510 0%, #A06030 100%);
  }
}
</style>
