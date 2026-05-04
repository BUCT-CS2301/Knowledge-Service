<template>
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
</template>

<script>
  import axios from 'axios'
  // axios({
  //   method: 'GET',
  //   url: 'http://localhost:8085/login'
  // }).then(function (result) {
  //   console.log(result)
  // })
  export default {
    name: 'Login',
    data () {
      return {
        form: {
          username: '',
          password: ''
        },
        info: {
          flag: true
        },
        // 表单验证，需要在 el-form-item 元素中增加 prop 属性
        rules: {
          username: [
            {required: true, message: '账号不可为空', trigger: 'blur'}
          ],
          password: [
            {required: true, message: '密码不可为空', trigger: 'blur'}
          ]
        },

        // 对话框显示和隐藏
        dialogVisible: false
      }
    },
    methods: {
      onSubmit () {
        axios.post('http://localhost:8085/users/login', this.form// 注意数据是直接保存到json对象
        ).then(function (response) {
          console.log(response.data)
          if (response.data.state === 200) {
            alert('welcome to overseas museum!')
            // this.$route('/index')
          } else {
            alert(response.data.message)
          }
        }).catch(function (error) {
          console.log(error)
        })
        console.log('Received values of form: ', this.form)
      },
      turn_to_register () {
        this.$router.push('register')
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
  }

  .login-title {
    text-align: center;
    margin: 0 auto 40px auto;
    color: #303133;
  }
</style>
