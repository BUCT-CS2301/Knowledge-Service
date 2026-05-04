<template>
  <div>
    <MainHeader></MainHeader>
    <div>
      <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
        <h3 class="login-title">登录</h3>
        <el-form-item label="账号" prop="username">
          <el-input type="text" placeholder="请输入账号" v-model="form.username"/>
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
    onSubmit () {
      if (!this.form.username || !this.form.password) {
        this.$message.error('请输入账号和密码')
        return
      }

      const users = JSON.parse(localStorage.getItem('users') || '[]')
      const user = users.find(u => u.username === this.form.username && u.password === this.form.password)

      if (user) {
        localStorage.setItem('username', user.username)
        localStorage.setItem('user_id', user.id.toString())
        localStorage.setItem('user_name', user.user_name)
        localStorage.setItem('islogin', '1')
        this.$message.success('登录成功！')
        setTimeout(() => {
          this.$router.push('/index')
        }, 1000)
      } else {
        this.$message.error('账号或密码错误')
      }
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
