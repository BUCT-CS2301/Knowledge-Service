<template>
  <div>
    <MainHeader></MainHeader>
    <div>
      <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
        <h3 class="login-title">登录</h3>
        <el-form-item label="账号" prop="username">
          <el-input type="text" placeholder="请输入用户ID，如 1001" v-model="form.username"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" placeholder="请输入密码" v-model="form.password"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="onSubmit()">登录</el-button>
          <el-button type="primary" v-on:click="turn_to_register()">注册</el-button>
        </el-form-item>
      </el-form>

      <el-dialog
        title="温馨提示"
        :visible.sync="dialogVisible"
        width="30%"
        :before-close="handleClose">
        <span>请输入账号和密码</span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainFooter from '../../components/MainFooter/MainFooter'
import MainHeader from '../../components/MainHeader/MainHeader'
import { login, parseLoginResponse } from '@/api/user'

export default {
  name: 'Login',
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          {required: true, message: '账号不可为空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '密码不可为空', trigger: 'blur'}
        ]
      },
      dialogVisible: false
    }
  },
  methods: {
    async onSubmit () {
      if (!this.form.username || !this.form.password) {
        this.$message.error('请输入账号和密码')
        return
      }

      const userId = String(this.form.username || '').trim()
      if (!/^\d+$/.test(userId)) {
        this.$message.error('账号请填写数字用户ID（默认测试账号：1001）')
        return
      }

      try {
        const response = await login({
          username: userId,
          password: this.form.password
        })
        const data = parseLoginResponse(response)
        localStorage.setItem('accessToken', data.token || '')
        localStorage.setItem('username', String(data.userId))
        localStorage.setItem('user_id', String(data.userId))
        localStorage.setItem('user_name', data.userName || String(data.userId))
        localStorage.setItem('islogin', '1')
        this.syncUserToLocalStorage(data)
        this.$message.success('登录成功！')
        setTimeout(() => {
          this.$router.push('/index')
        }, 800)
      } catch (error) {
        console.error('登录失败:', error)
        // 后端接口不可用，使用本地存储作为备用
        if (error.message && error.message.includes('Network Error')) {
          this.loginWithLocalStorage(userId)
        } else if (error.code === 4000) {
          this.$message.error('用户不存在，请检查用户ID')
        } else if (error.code === 6000) {
          this.$message.error('密码错误')
        } else {
          this.$message.error(error.message || '账号或密码错误')
        }
      }
    },
    syncUserToLocalStorage (data) {
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const userId = String(data.userId)
      const existingIndex = users.findIndex(u => String(u.id) === userId)
      
      const userObj = {
        id: userId,
        username: data.userName || userId,
        password: '',
        sex: '1',
        tele: '',
        nickname: data.userName || userId,
        email: '',
        avatar: ''
      }
      
      if (existingIndex >= 0) {
        users[existingIndex] = { ...users[existingIndex], ...userObj }
      } else {
        users.push(userObj)
      }
      
      localStorage.setItem('users', JSON.stringify(users))
    },
    loginWithLocalStorage (userId) {
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => String(u.id) === userId)
      
      if (!user) {
        this.$message.error('用户不存在，请检查用户ID')
        return
      }
      
      if (user.password !== this.form.password) {
        this.$message.error('密码错误')
        return
      }
      
      localStorage.setItem('accessToken', 'local_token_' + userId)
      localStorage.setItem('username', userId)
      localStorage.setItem('user_id', userId)
      localStorage.setItem('user_name', user.username)
      localStorage.setItem('islogin', '1')
      this.$message.success('登录成功！')
      setTimeout(() => {
        this.$router.push('/index')
      }, 800)
    },
    turn_to_register () {
      this.$router.push('/register')
    },
    handleClose () {
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.login-box {
  border: 1px solid #DCDFE6;
  width: 350px;
  margin: 180px auto;
  padding: 35px 35px 15px 35px;
  border-radius: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px #909399;
  background: white;
}

.login-title {
  text-align: center;
  margin: 0 auto 40px auto;
  color: #303133;
}

:deep(.el-button--primary) {
  background-color: #8B4513 !important;
  border-color: #8B4513 !important;

  &:hover {
    background-color: #6B3510 !important;
    border-color: #6B3510 !important;
  }
}
</style>
