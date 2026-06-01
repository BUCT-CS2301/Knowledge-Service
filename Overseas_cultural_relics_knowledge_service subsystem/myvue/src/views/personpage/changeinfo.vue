<template>
  <div class="changeinfo-page">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-edit"></i>
        修改信息
      </h1>
      <p class="page-desc">更新您的个人资料（修改需验证原密码）</p>
    </div>

    <div class="form-card">
      <el-form
        ref="form"
        :model="form"
        label-width="120px"
        :rules="rules"
        class="info-form">

        <el-form-item label="用户名" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入用户名"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系电话" prop="tel">
          <el-input
            v-model="form.tel"
            placeholder="请输入联系电话"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入当前密码以保存修改"
            show-password
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
import {
  fetchCurrentUserProfile,
  getCurrentUserId,
  updateUserProfile,
  parseJsonResult
} from '@/api/user'

export default {
  data () {
    return {
      form: {
        name: '',
        sex: '1',
        tel: '',
        oldPassword: ''
      },
      userId: '',
      rules: {
        name: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }]
      }
    }
  },
  mounted () {
    this.pageInit()
  },
  methods: {
    async pageInit () {
      const userId = getCurrentUserId()
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      this.userId = userId
      try {
        const profile = await fetchCurrentUserProfile()
        this.userId = String(profile.user_id)
        this.form.name = profile.user_name || ''
        this.form.sex = String(profile.user_sex ?? 1)
        this.form.tel = profile.user_tel || ''
      } catch (error) {
        console.error('获取用户详细信息失败:', error)
        this.$message.error('加载用户信息失败')
      }
    },
    async onSubmit () {
      if (!this.userId) {
        this.$message.error('用户不存在')
        return
      }
      if (!this.form.oldPassword) {
        this.$message.error('请输入原密码')
        return
      }

      try {
        const response = await updateUserProfile({
          id: this.userId,
          oldPassword: this.form.oldPassword,
          name: this.form.name,
          sex: this.form.sex,
          tel: this.form.tel
        })
        parseJsonResult(response)
        localStorage.setItem('user_name', this.form.name)
        this.form.oldPassword = ''
        this.$message.success('修改成功!')
        this.pageInit()
      } catch (error) {
        console.error('修改用户信息失败:', error)
        if (error.code === 6000) {
          this.$message.error('原密码错误')
        } else if (error.code === 5000) {
          this.$message.error('用户名已存在，请更换')
        } else {
          this.$message.error(error.message || '修改失败')
        }
      }
    },
    resetForm () {
      this.form.oldPassword = ''
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
</style>
