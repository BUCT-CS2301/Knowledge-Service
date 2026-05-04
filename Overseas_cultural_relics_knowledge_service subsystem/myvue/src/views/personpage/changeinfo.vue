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

        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input
            v-model="form.tel"
            placeholder="请输入联系电话"
            class="input-field">
          </el-input>
        </el-form-item>

        <el-form-item label="个人简介">
          <el-input
            type="textarea"
            v-model="form.bio"
            placeholder="请输入个人简介"
            :rows="3"
            class="input-field">
          </el-input>
        </el-form-item>

        <div class="password-section">
          <h3 class="section-title">
            <i class="el-icon-lock"></i>
            修改密码
          </h3>

          <el-form-item label="原密码">
            <el-input
              type="password"
              v-model="form.oldPassword"
              placeholder="请输入原密码"
              class="input-field">
            </el-input>
          </el-form-item>

          <el-form-item label="新密码">
            <el-input
              type="password"
              v-model="form.newPassword"
              placeholder="请输入新密码"
              class="input-field">
            </el-input>
          </el-form-item>
        </div>

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
export default {
  data () {
    return {
      form: {
        name: '',
        sex: '',
        tel: '',
        bio: '',
        oldPassword: '',
        newPassword: ''
      },
      userInfo: {
        password: ''
      },
      rules: {
        oldPassword: [
          {required: false, message: '请输入原密码', trigger: 'blur'}
        ],
        newPassword: [
          {required: false, message: '请输入新密码', trigger: 'blur'}
        ]
      }
    }
  },
  mounted () {
    this.pageInit()
  },
  methods: {
    pageInit () {
      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === username)

      if (user) {
        this.form.name = user.user_name || ''
        this.form.sex = user.sex || '男'
        this.form.tel = user.tele || ''
        this.form.bio = user.bio || ''
        this.userInfo.password = user.password || ''
      }
    },
    onSubmit () {
      const username = localStorage.getItem('username')
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const index = users.findIndex(u => u.username === username)

      if (index === -1) {
        this.$message.error('用户不存在')
        return
      }

      if (this.form.oldPassword && this.form.oldPassword !== this.userInfo.password) {
        this.$message.error('原密码不正确')
        return
      }

      if (this.form.newPassword && this.form.newPassword.length < 6) {
        this.$message.error('新密码长度至少6位')
        return
      }

      const updateData = {
        user_name: this.form.name,
        sex: this.form.sex,
        tele: this.form.tel,
        bio: this.form.bio
      }

      if (this.form.newPassword) {
        updateData.password = this.form.newPassword
      }

      users[index] = { ...users[index], ...updateData }
      localStorage.setItem('users', JSON.stringify(users))

      if (this.form.name) {
        localStorage.setItem('user_name', this.form.name)
      }

      this.$message.success('修改成功!')
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

.password-section {
  border-top: 1px dashed #ddd;
  padding-top: 24px;
  margin-top: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
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
