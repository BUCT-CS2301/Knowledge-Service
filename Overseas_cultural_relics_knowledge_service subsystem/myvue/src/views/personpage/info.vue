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

        <el-descriptions-item label="用户 ID">
          <span class="value">{{ userInfo.user_id || '—' }}</span>
        </el-descriptions-item>

        <el-descriptions-item label="用户名">
          <span class="value">{{ userInfo.user_name }}</span>
        </el-descriptions-item>

        <el-descriptions-item label="性别">
          <span class="value">{{ userInfo.sexText }}</span>
        </el-descriptions-item>

        <el-descriptions-item label="手机号">
          <span class="value">{{ userInfo.tele || '未设置' }}</span>
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
import { fetchCurrentUserProfile, getCurrentUserId } from '@/api/user'

export default {
  data () {
    return {
      userInfo: {
        user_id: '',
        user_name: '未登录',
        sexText: '—',
        tele: ''
      }
    }
  },
  methods: {
    sexLabel (value) {
      if (value === 1 || value === '1') return '男'
      if (value === 0 || value === '0') return '女'
      return '—'
    },
    async pageInit () {
      const userId = getCurrentUserId()
      if (!userId) {
        this.userInfo.user_name = '游客'
        return
      }

      try {
        const profile = await fetchCurrentUserProfile()
        this.userInfo = {
          user_id: profile.user_id,
          user_name: profile.user_name || '未设置',
          sexText: this.sexLabel(profile.user_sex),
          tele: profile.user_tel || ''
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.userInfo.user_name = localStorage.getItem('user_name') || userId
        this.userInfo.user_id = userId
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
