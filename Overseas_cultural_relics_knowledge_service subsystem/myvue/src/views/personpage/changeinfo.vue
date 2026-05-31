<template>
  <div class="changeinfo-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-edit"></i>
        修改信息
      </h1>
      <p class="page-desc">更新您的个人资料</p>
    </div>

    <div class="form-card">
      <el-form
        ref="form"
        :model="form"
        label-width="120px"
        :rules="rules"
        class="info-form">

        <el-form-item label="用户名">
          <el-input
            v-model="form.name"
            placeholder="请输入用户名"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input
            v-model="form.tel"
            placeholder="请输入联系电话"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item label="电子邮箱">
          <el-input
            v-model="form.email"
            placeholder="请输入电子邮箱"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" @click="onSubmit">
            <i class="el-icon-check"></i>
            保存修改
          </el-button>
          <el-button @click="resetForm">
            <i class="el-icon-refresh"></i>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/api/request'
export default {
  data () {
    return {
      form: {
        name: '',
        tel: '',
        email: ''
      },
      userInfo: {
        id: ''
      },
      rules: {}
    }
  },
  mounted () {
    this.pageInit()
  },
  methods: {
    async pageInit () {
      const accessToken = localStorage.getItem('accessToken')
      const username = localStorage.getItem('username')
      const objectId = localStorage.getItem('objectId')
      const userId = localStorage.getItem('user_id')

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
            this.form.name = data.nickname || data.username || ''
            this.form.tel = data.phone || data.tele || ''
            this.form.email = data.email || ''
            this.userInfo.id = data.objectId || userId || ''
            return
          }
        } catch (error) {
          console.error('获取用户详细信息失败:', error)
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
            const objId = data.objectId
            localStorage.setItem('objectId', objId || '')
            this.userInfo.id = objId || userId || ''
            
            // 尝试用获取到的objectId获取详细信息
            if (objId) {
              try {
                const detailResponse = await request.get(`/api/v1/users/${objId}`, {
                  headers: {
                    Authorization: `Bearer ${accessToken}`
                  }
                })
                if (detailResponse.data.code === 200) {
                  const detailData = detailResponse.data.data
                  this.form.name = detailData.nickname || detailData.username || ''
                  this.form.tel = detailData.phone || detailData.tele || ''
                  this.form.email = detailData.email || ''
                  return
                }
              } catch (detailError) {
                console.error('获取用户详细信息失败:', detailError)
              }
            }
            
            // 如果获取详细信息失败，使用基础信息
            this.form.name = data.nickname || data.username || ''
            return
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }

      // 后端不可用时，使用本地存储作为备用
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.form.name = user.user_name || ''
        this.form.tel = user.tele || ''
        this.form.email = user.email || ''
        this.userInfo.password = user.password || ''
        this.userInfo.id = user.id || userId || ''
      }
    },
    async onSubmit () {
      const accessToken = localStorage.getItem('accessToken')
      const objectId = localStorage.getItem('objectId') || this.userInfo.id
      const userId = localStorage.getItem('user_id') || this.userInfo.id

      if (!objectId && !userId) {
        this.$message.error('用户不存在')
        return
      }

      const targetId = objectId || userId

      // 构建更新数据 - 按照接口文档字段名
      const updateData = {
        nickname: this.form.name,
        phone: this.form.tel,
        email: this.form.email
      }

      // 优先尝试调用后端API
      if (accessToken) {
        try {
          const response = await request.put(`/api/v1/users/${targetId}`, updateData, {
            headers: {
              Authorization: `Bearer ${accessToken}`
            }
          })

          if (response.data.code === 200) {
            if (this.form.name) {
              localStorage.setItem('user_name', this.form.name)
            }
            this.$message.success('修改成功!')
            this.pageInit()
            return
          } else {
            this.$message.error(response.data.message || '修改失败')
            return
          }
        } catch (error) {
          console.error('修改用户信息失败:', error)
          this.saveToLocalStorage()
          return
        }
      }

      // 后端不可用时，使用本地存储作为备用
      this.saveToLocalStorage()
    },
    saveToLocalStorage () {
      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const index = users.findIndex(u => u.username === username)

      if (index === -1) {
        this.$message.error('用户不存在')
        return
      }

      const localUpdateData = {
        user_name: this.form.name,
        tele: this.form.tel,
        email: this.form.email
      }

      users[index] = { ...users[index], ...localUpdateData }
      localStorage.setItem('users', JSON.stringify(users))

      if (this.form.name) {
        localStorage.setItem('user_name', this.form.name)
      }

      this.$message.warning('本地数据修改成功!')
    },
    resetForm () {
      this.pageInit()
    }
  }
}
</script>

<style scoped>
.changeinfo-page {
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

.form-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 32px;
}

.info-form {
  max-width: 600px;
}

.input-field {
  border-radius: 8px;
}

.form-actions {
  margin-top: 32px;
  text-align: right;
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

:deep(.el-button) {
  border-radius: 8px;
  padding: 10px 24px;
  margin-left: 12px;
}

:deep(.el-radio) {
  margin-right: 24px;
}
</style>
