<template>
  <div style="background-color: white">
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="用户id">
        <el-input v-model="form.id" v-bind:placeholder="user_id"></el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="form.name" v-bind:placeholder="user_name"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio v-model="form.sex" value="0" label="0">男</el-radio>
        <el-radio v-model="form.sex" value="1" label="1">女</el-radio>
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="form.tel" v-bind:placeholder="userInfo.user_tel"></el-input>
      </el-form-item>
      <el-form-item label="旧密码">
        <el-input v-model="form.oldPassword" v-bind:placeholder="userInfo.user_password"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">修改</el-button>
        <el-button>取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import axios from 'axios'
  export default {
    data () {
      return {
        form: {
          id: '',
          name: '',
          sex: '',
          tel: '',
          oldPassword: '',
          newPassword: ''
        },
        user_id: 0,
        user_name: '',
        userInfo: [{
          user_id: '',
          user_password: '',
          user_name: '',
          user_sex: '',
          user_tel: '',
          user_comment: '',
          user_login: ''
        }],
        table: {
          username: '1',
          password: '18181818'
        }
      }
    },
    mounted () {
      this.pageInit()
    },
    methods: {
      async pageInit () {
        axios.post('http://localhost:8085/users/login', this.table
        ).then(function (response) {
          console.log(response.data.data)
          this.user_id = response.data.data.id
          this.user_name = response.data.data.user_name
          })
          .catch(function (error) {
            console.log(error)
          })
      },
      async onSubmit () {
        axios.post('http://localhost:8085/user_admin/update', this.form// 注意数据是直接保存到json对象
        ).then(function (response) {
          if (response.data.state === 200) {
            alert('hello world!')
          }
          console.log(response.data)
        }).catch(function (error) {
          console.log(error)
        })
      }
    }
  }

</script>

<style scoped>
  .el-header {
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
  }

  .el-aside {
    color: #333;
  }
  .alink{

    text-decoration: none;
  }
</style>
