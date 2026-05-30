<template>
  <div class="main-header">
    <el-menu class="el-menu" mode="horizontal">
      <el-menu-item class="menu-logo-holder" index="logo">
        <img class="menu-logo" src="@/assets/menu-logo.png">
      </el-menu-item>

      <router-link to="/index">
        <div class="float-left">
          <el-menu-item class="disable-element-hover" index="index">首页</el-menu-item>
        </div>
      </router-link>

      <router-link to="/classify">
        <div class="float-left">
          <el-menu-item class="disable-element-hover" index="classify">浏览</el-menu-item>
        </div>
      </router-link>
      <router-link to="/keyword">
        <div class="float-left">
          <el-menu-item class="disable-element-hover" index="keyword">关键字</el-menu-item>
        </div>
      </router-link>

      <router-link to="/visualization">
        <div class="float-left">
          <el-menu-item class="disable-element-hover" index="visualization">文物可视化</el-menu-item>
        </div>
      </router-link>

      <router-link to="/pro_search">
        <div class="float-right">
          <el-menu-item class="disable-element-hover" index="pro_search">专业搜索</el-menu-item>
        </div>
      </router-link>

      <router-link to="/personpage" v-if="isLogin">
        <div class="float-right">
          <el-menu-item class="disable-element-hover" index="personpage">后台管理</el-menu-item>
        </div>
      </router-link>
      <div v-if="isLogin">
        <div class="float-right">
          <el-menu-item class="disable-element-hover" index="logout" v-on:click="off_log" >登出</el-menu-item>
        </div>
      </div>

      <router-link to="/login" v-if="!isLogin">
        <div class="float-right">
          <el-menu-item class="disable-element-hover" index="login">登录</el-menu-item>
        </div>
      </router-link>



<!--      <div class="float-right">-->
<!--        <el-menu-item class="disable-element-hover">-->
<!--          <el-input size="small" type="text" v-model="resfrom.infro" placeholder="请输入内容">-->
<!--            <el-button slot="append" class="search-button" icon="el-icon-search" v-on:click="res_res"/>-->
<!--          </el-input>-->
<!--        </el-menu-item>-->
<!--      </div>-->
    </el-menu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data () {
    return {
      resfrom: {
        infro: ''
      },
      isLogin: false,
      res_form: [{
        object_name: '明egfielgsfesf',
        cat2: '明awdadvwajdwad',
        img_url: 'https://www.wantongdiannao.com/upload/goods/1786/17180525093_175_175.jpg',
        // eslint-disable-next-line no-undef
        id: '1'
      }]
    }
  },
  computed: {
    ...mapGetters([
      'token'
    ])
  },
  created () {
    this.loadUser()
  },
  methods: {
    loadUser () {
      if (localStorage.getItem('username')) {
        this.isLogin = true
      } else {
        this.isLogin = false
      }
    },
    jump_to_classify () {
      this.router.push('/classify')
    },
    off_log () {
      // eslint-disable-next-line no-unused-expressions,no-sequences
      localStorage.removeItem('username'),
      localStorage.removeItem('userpassword'),
      localStorage.setItem('islogin', 0)
      this.$router.go(0)
    }
  }
}
</script>

<style lang="scss" scoped>
.main-header {
  background: white;
}

.el-menu {
  background: white !important;
  border: none;
}

.el-menu-item {
  color: #8B4513 !important;
  border: none !important;

  &:hover {
    background: #f5f5f5 !important;
  }
}

.float-left {
  float: left;
}

.float-right {
  float: right;
}

.disable-element-hover:hover {
  background: #f5f5f5 !important;
}

.disable-element-hover {
  background: white !important;
}

.search-button {
  width: 30px;
  padding-right: 30px;
  padding-left: 8px;
}

.menu-logo-holder {
  width: 180px;
}

.menu-logo {
  max-width: 100%;
  max-height: 100%;
}
.main-footer {
  position: relative;
  bottom: 0px;
  overflow: hidden;
  height: 180px;
  width: 100%;
  background: #331414;
  color: #ffffff;
  text-align: center;
  font-family: "微软雅黑";
  line-height: 20px;
  font-size: 14px;
  letter-spacing: 2.25px;
}
.view-times {
  justify-content: center;
  align-items: center;
  display: flex;
  margin-top: 15px;
  height: 35px;
  text-align: center;
  line-height: 32px;
}

.num {
  width: 20px;
  height: 32px;
  background-color: rgba(255,255,255,0.1);
  z-index: 9999;
  color: #ffffff;
  font-size: 14px;
  line-height: 32px;
  margin: 0 0.2rem;
  padding-left: 3px;
  float: left;
}

.menu {
  letter-spacing: 0;
  margin-top: 1rem;
}

.menu a {
  margin: 0 0.5rem;
  color: #ffffff;
  text-decoration: none;
}

.copyright {
  letter-spacing: 0;
  font-family: "微软雅黑";
}

.logo {
  margin: 20px 0;
  /* length: 30%; */
  width: 10%;
}
</style>
