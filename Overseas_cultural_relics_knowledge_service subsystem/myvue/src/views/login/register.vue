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
          <el-button type="primary" v-on:click="onSubmit()">确定</el-button>
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
import defaultAvatar from '../../assets/timg.jpeg'

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
          {required: true, message: '用户名不可为空', trigger: 'blur'},
          {min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '密码不可为空', trigger: 'blur'},
          {min: 6, message: '密码长度至少6位', trigger: 'blur'}
        ],
        confirmPassword: [
          {required: true, message: '请确认密码', trigger: 'blur'}
        ],
        tele: [
          {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    onSubmit () {
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

      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const exists = users.find(u => u.username === this.form.username)

      if (exists) {
        this.$message.error('用户名已存在')
        return
      }

      const newUser = {
        id: Date.now(),
        username: this.form.username,
        password: this.form.password,
        user_name: this.form.username,
        sex: this.form.sex,
        tele: this.form.tele,
        avatar: defaultAvatar,
        bio: '',
        user_login: 1
      }

      users.push(newUser)
      localStorage.setItem('users', JSON.stringify(users))

      this.$message.success('注册成功！')
      setTimeout(() => {
        this.$router.push('/login')
      }, 1000)
    },
    turn_to_login () {
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.login-box {
  border: 1px solid #DCDFE6;
  width: 350px;
  margin: 120px auto;
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
