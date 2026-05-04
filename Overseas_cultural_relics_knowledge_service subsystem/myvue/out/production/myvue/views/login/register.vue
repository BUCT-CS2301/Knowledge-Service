<template>
  <div>
    <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px" class="login-box">
      <h3 class="login-title">注册</h3>
      <el-form-item label="用户名" prop="username">
        <el-input type="text" placeholder="请输入用户名" v-model="form.username"/>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" placeholder="请输入密码" v-model="form.password"/>
      </el-form-item>
      <el-form-item label="性别" prop="username">
        <el-radio v-model="form.sex" label="1" value="1">女</el-radio>
        <el-radio v-model="form.sex" label="0" value="0">男</el-radio>
      </el-form-item>
      <el-form-item label="电话" prop="username">
        <el-input type="text" placeholder="请输入电话" v-model="form.tele"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" v-on:click="onSubmit()">确定</el-button>
      </el-form-item>
    </el-form>

    <el-dialog
      title="温馨提示"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose">
      <span>请输入账号和密码</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Register',
  data () {
    return {
      form: {
        username: '',
        password: '',
        sex: '',
        tele: ''
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
      // 为表单绑定验证功能
      axios.post('http://localhost:8085/users/register', this.form// 注意数据是直接保存到json对象
      ).then(function (response) {
        console.log(response.data)
        if (response.data.state === 200) {
          this.$route('/index')
        } else {
          alert(response.data.message)
        }
      }).catch(function (error) {
        console.log(error)
      })
      console.log('Received values of form: ', this.form)
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

<!--<template>-->
<!--  <div class="main-login">-->

<!--    <div class="login-container">-->

<!--      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">-->

<!--        <div class="title-container">-->
<!--          <h3 class="title">注册</h3>-->
<!--        </div>-->

<!--        <el-form-item prop="username">-->
<!--          <span class="svg-container">-->
<!--            <svg-icon icon-class="user" />-->
<!--          </span>-->
<!--          <el-input-->
<!--            ref="username"-->
<!--            v-model="loginForm.username"-->
<!--            placeholder="Username"-->
<!--            name="username"-->
<!--            type="text"-->
<!--            tabindex="1"-->
<!--            auto-complete="on"-->
<!--          />-->
<!--        </el-form-item>-->

<!--        <el-form-item prop="password">-->
<!--          <span class="svg-container">-->
<!--            <svg-icon icon-class="password" />-->
<!--          </span>-->
<!--          <el-input-->
<!--            :key="passwordType"-->
<!--            ref="password"-->
<!--            v-model="loginForm.password"-->
<!--            :type="passwordType"-->
<!--            placeholder="Password"-->
<!--            name="password"-->
<!--            tabindex="2"-->
<!--            auto-complete="on"-->
<!--            @keyup.enter.native="handleLogin"-->
<!--          />-->
<!--          <span class="show-pwd" @click="showPwd">-->
<!--            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />-->
<!--          </span>-->
<!--          <el-input-->
<!--            v-model="loginForm.sex"-->
<!--            placeholder="User_sex"-->
<!--            name="user_sex"-->
<!--            type="text"-->
<!--            tabindex="1"-->
<!--            auto-complete="on"-->
<!--          />-->
<!--          <el-input-->
<!--            v-model="loginForm.tele"-->
<!--            placeholder="User_sex"-->
<!--            name="user_sex"-->
<!--            type="text"-->
<!--            tabindex="1"-->
<!--            auto-complete="on"-->
<!--          />-->
<!--        </el-form-item>-->

<!--        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">登录</el-button>-->
<!--        &lt;!&ndash; <div class="tips">-->
<!--          <span style="margin-right:20px;">username: admin</span>-->
<!--          <span> password: any</span>-->
<!--        </div> &ndash;&gt;-->

<!--      </el-form>-->

<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { validUsername } from '@/utils/validate'-->
<!--import MainHeader from '@/components/MainHeader'-->
<!--import MainFooter from '@/components/MainFooter'-->
<!--import register from '@/views/common/register'-->
<!--import axios from 'axios'-->
<!--export default {-->
<!--  name: 'Login',-->
<!--  components: {-->
<!--    MainHeader,-->
<!--    MainFooter,-->
<!--    // eslint-disable-next-line vue/no-unused-components-->
<!--    register-->
<!--  },-->
<!--  data () {-->
<!--    const validateUsername = (rule, value, callback) => {-->
<!--      if (!validUsername(value)) {-->
<!--        callback(new Error('Please enter the correct user name'))-->
<!--      } else {-->
<!--        callback()-->
<!--      }-->
<!--    }-->
<!--    const validatePassword = (rule, value, callback) => { // 长度<6-->
<!--      if (value.length < 6) {-->
<!--        callback(new Error('The password can not be less than 6 digits'))-->
<!--      } else {-->
<!--        callback()-->
<!--      }-->
<!--    }-->
<!--    return {-->
<!--      loginForm: {-->
<!--        username: 'admin',-->
<!--        password: '12345677',-->
<!--        sex: '',-->
<!--        tele: ''-->
<!--      },-->
<!--      loginRules: {-->
<!--        username: [{ required: true, trigger: 'blur', validator: validateUsername }],-->
<!--        password: [{ required: true, trigger: 'blur', validator: validatePassword }]-->
<!--      },-->
<!--      loading: false,-->
<!--      passwordType: 'password',-->
<!--      redirect: undefined-->
<!--    }-->
<!--  },-->
<!--  watch: {-->
<!--    $route: {-->
<!--      handler: function (route) {-->
<!--        this.redirect = route.query && route.query.redirect-->
<!--      },-->
<!--      immediate: true-->
<!--    }-->
<!--  },-->
<!--  methods: {-->
<!--    showPwd () {-->
<!--      if (this.passwordType === 'password') {-->
<!--        this.passwordType = ''-->
<!--      } else {-->
<!--        this.passwordType = 'password'-->
<!--      }-->
<!--      this.$nextTick(() => {-->
<!--        this.$refs.password.focus()-->
<!--      })-->
<!--    },-->
<!--    handleLogin () {-->
<!--      this.$refs.loginForm.validate(valid => {-->
<!--        if (valid) {-->
<!--          let params = { uname: this.loginForm.password, password: this.loginForm.password }-->
<!--          var url = '/login'-->
<!--          axios.post(url, params// 注意数据是直接保存到json对象-->
<!--          ).then(function (response) {-->
<!--            console.log(response.data)-->
<!--            if (response.data === 2000) {-->
<!--              this.$route('/index')-->
<!--            } else {-->
<!--              alert(response.data)-->
<!--            }-->
<!--          }).catch(function (error) {-->
<!--            console.log(error)-->
<!--          })-->
<!--          console.log('Received values of form: ', this.loginForm)-->
<!--        } else {-->
<!--          // eslint-disable-next-line no-console-->
<!--          console.log('参数验证不合法！')-->
<!--          return false-->
<!--        }-->
<!--      })-->
<!--    },-->
<!--    jump_to_register () {-->
<!--      this.$router.push('/register')-->
<!--      this.$router.push('/nearby')-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style lang="scss">-->
<!--/* 修复input 背景不协调 和光标变色 */-->
<!--/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */-->

<!--$bg:#283443;-->
<!--$light_gray:rgb(107, 85, 85);-->
<!--$cursor: #fff;-->

<!--@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {-->
<!--  .login-container .el-input input {-->
<!--    color: $cursor;-->
<!--  }-->
<!--}-->

<!--/* reset element-ui css */-->
<!--.login-container {-->
<!--  .el-input {-->
<!--    display: inline-block;-->
<!--    height: 47px;-->
<!--    width: 85%;-->

<!--    input {-->
<!--      background: transparent;-->
<!--      border: 0px;-->
<!--      -webkit-appearance: none;-->
<!--      border-radius: 0px;-->
<!--      padding: 12px 5px 12px 15px;-->
<!--      color: $light_gray;-->
<!--      height: 47px;-->
<!--      caret-color: $cursor;-->

<!--      &:-webkit-autofill {-->
<!--        box-shadow: 0 0 0px 1000px $bg inset !important;-->
<!--        -webkit-text-fill-color: $cursor !important;-->
<!--      }-->
<!--    }-->
<!--  }-->

<!--  .el-form-item {-->
<!--    border: 1px solid rgba(255, 255, 255, 0.1);-->
<!--    background: rgba(0, 0, 0, 0.1);-->
<!--    border-radius: 5px;-->
<!--    color: #454545;-->
<!--  }-->
<!--}-->
<!--</style>-->

<!--<style lang="scss" scoped>-->
<!--$bg:#f4f8f2;-->
<!--$dark_gray:#485358;-->
<!--$light_gray:rgb(58, 48, 48);-->

<!--.main-login {-->
<!--  height: calc(100% - 241px);-->
<!--}-->

<!--.login-container {-->
<!--  min-height: 100%;-->
<!--  width: 100%;-->
<!--  background-color: $bg;-->
<!--  overflow: hidden;-->

<!--  .login-form {-->
<!--    position: relative;-->
<!--    width: 520px;-->
<!--    max-width: 100%;-->
<!--    padding: 160px 35px 0;-->
<!--    margin: 0 auto;-->
<!--    overflow: hidden;-->
<!--  }-->

<!--  .tips {-->
<!--    font-size: 14px;-->
<!--    color: rgb(88, 74, 74);-->
<!--    margin-bottom: 10px;-->

<!--    span {-->
<!--      &:first-of-type {-->
<!--        margin-right: 16px;-->
<!--      }-->
<!--    }-->
<!--  }-->

<!--  .svg-container {-->
<!--    padding: 6px 5px 6px 15px;-->
<!--    color: $dark_gray;-->
<!--    vertical-align: middle;-->
<!--    width: 30px;-->
<!--    display: inline-block;-->
<!--  }-->

<!--  .title-container {-->
<!--    position: relative;-->

<!--    .title {-->
<!--      font-size: 26px;-->
<!--      color: $light_gray;-->
<!--      margin: 0px auto 40px auto;-->
<!--      text-align: center;-->
<!--      font-weight: bold;-->
<!--    }-->
<!--  }-->

<!--  .show-pwd {-->
<!--    position: absolute;-->
<!--    right: 10px;-->
<!--    top: 7px;-->
<!--    font-size: 16px;-->
<!--    color: $dark_gray;-->
<!--    cursor: pointer;-->
<!--    user-select: none;-->
<!--  }-->
<!--}-->
<!--</style>-->
