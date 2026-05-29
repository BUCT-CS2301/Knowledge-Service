<template>
  <div>
    <MainHeader></MainHeader>
    <div>
      <el-form ref="registerForm" :model="form" :rules="rules" label-width="80px" class="login-box">
        <h3 class="login-title">注册</h3>
        <el-form-item label="用户名" prop="username">
          <el-input type="text" placeholder="请输入用户名" v-model="form.username"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" placeholder="请输入密码" v-model="form.password"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" placeholder="请确认密码" v-model="form.confirmPassword"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="tele">
          <el-input type="text" placeholder="请输入电话" v-model="form.tele"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="onSubmit()">注册</el-button>
          <el-button v-on:click="turn_to_login()">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainFooter from '../../components/MainFooter/MainFooter'
import MainHeader from '../../components/MainHeader/MainHeader'
import { register, parseRegisterResponse } from '@/api/user'

export default {
  name: 'Register',
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        sex: '男',
        tele: ''
      },
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, message: '密码长度至少6位', trigger: 'blur'}
        ],
        confirmPassword: [
          {required: true, message: '请确认密码', trigger: 'blur'}
        ],
        tele: [
          {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    async onSubmit () {
      if (!this.form.username) {
        this.$message.error('请输入用户名')
        return
      }
      if (this.form.username.length < 3) {
        this.$message.error('用户名长度至少3位')
        return
      }
      if (!this.form.password) {
        this.$message.error('请输入密码')
        return
      }
      if (this.form.password.length < 6) {
        this.$message.error('密码长度至少6位')
        return
      }
      if (this.form.password !== this.form.confirmPassword) {
        this.$message.error('两次输入的密码不一致')
        return
      }

      try {
        const response = await register({
          username: this.form.username,
          password: this.form.password,
          nickname: this.form.username,
          phone: this.form.tele || ''
        })
        const data = parseRegisterResponse(response)
        this.$message.success(
          `注册成功！您的登录账号（用户ID）为：${data.userId}，请使用该数字ID登录`
        )
        setTimeout(() => {
          this.$router.push('/login')
        }, 1000)
      } catch (error) {
        console.error('注册失败:', error)
        // 后端接口不可用，使用本地存储作为备用
        if (error.message && error.message.includes('Network Error')) {
          this.registerWithLocalStorage()
        } else if (error.code === 6000) {
          this.$message.error('用户名已存在')
        } else {
          this.$message.error(error.message || '注册失败')
        }
      }
    },
    registerWithLocalStorage () {
      const users = JSON.parse(localStorage.getItem('users') || '[]')
      // 检查用户名是否已存在
      const exists = users.some(u => u.username === this.form.username)
      if (exists) {
        this.$message.error('用户名已存在')
        return
      }
      // 生成简短的用户ID（4-6位数字）
      const userId = this.generateShortUserId(users)
      // 保存用户信息到本地存储
      const newUser = {
        id: userId,
        username: this.form.username,
        password: this.form.password,
        sex: this.form.sex === '女' ? '0' : '1',
        tele: this.form.tele || '',
        nickname: this.form.username,
        email: '',
        avatar: ''
      }
      users.push(newUser)
      localStorage.setItem('users', JSON.stringify(users))
      this.$message.success(
        `注册成功！您的登录账号（用户ID）为：${userId}，请使用该数字ID登录`
      )
      setTimeout(() => {
        this.$router.push('/login')
      }, 1000)
    },
    generateShortUserId (users) {
      // 生成4位用户ID，从1001开始递增
      let userId = 1001
      const existingIds = users.map(u => Number(u.id)).filter(id => !isNaN(id)).sort((a, b) => a - b)

      if (existingIds.length > 0) {
        userId = existingIds[existingIds.length - 1] + 1
      }

      return userId
    },
    turn_to_login () {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.login-box {
  width: 400px;
  margin: 50px auto;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}
</style>
