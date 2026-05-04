<template>
  <div class="main-header">
    <el-menu class="el-menu" mode="horizontal">
      <el-menu-item class="menu-logo-holder">
        <img class="menu-logo" src="@/assets/menu-logo.png">
      </el-menu-item>

<!--      <router-link :to="{ name: 'Index' }">-->
        <div class="float-left">
          <el-menu-item class="disable-element-hover">首页</el-menu-item>
        </div>
<!--      </router-link>-->

<!--      <router-link :to="{ name: 'Explore' }">-->
        <div class="float-left">
          <el-menu-item class="disable-element-hover">浏览</el-menu-item>
        </div>
<!--      </router-link>-->

<!--      <router-link :to="{ name: 'Nearby' }">-->
        <div class="float-left">
          <el-menu-item class="disable-element-hover">附近</el-menu-item>
        </div>
<!--      </router-link>-->

      <router-link to="/login">
        <div class="float-right">
          <el-menu-item class="disable-element-hover">登录</el-menu-item>
        </div>
      </router-link>

<!--      <router-link v-if="isLogin" :to="{ name: 'Dashboard' }">-->
      <router-link to="/personpage">
        <div class="float-right">
          <el-menu-item class="disable-element-hover">后台管理</el-menu-item>
        </div>
      </router-link>
<!--      </router-link>-->

<!--      <router-link :to="{ name: 'Search' }">-->
        <div class="float-right">
          <el-menu-item class="disable-element-hover">专业搜索</el-menu-item>
        </div>
<!--      </router-link>&ndash;&gt;-->

      <div class="float-right">
        <el-menu-item class="disable-element-hover">
          <el-input size="small" type="text" v-model="resfrom.infro" placeholder="请输入内容">
            <el-button slot="append" class="search-button" icon="el-icon-search" v-on:click="res_res"/>
          </el-input>
        </el-menu-item>
      </div>
    </el-menu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import axios from 'axios'
export default {
  data () {
    return {
      resfrom: {
        infro: ''
      },
      isLogin: false
    }
  },
  computed: {
    ...mapGetters([
      'token'
    ])
  },
  mounted () {
    this.loadUser()
  },
  methods: {
    loadUser () {
      if (this.token) {
        this.isLogin = true
      } else {
        this.isLogin = false
      }
    },
    res_res () {
      axios.post('http://localhost:8085/search/obscure', this.resfrom)
        .then(function (res) {
          if (res.data.state === 200) {
            // this.$route('/exp/:key');
            alert('shuai')
          } else { alert('ERROR') }
        }).catch(function (error) {
          console.log(error)
        })
      console.log('Received values of form: ', this.resfrom)
    }
  }
}
</script>

<style lang="scss" scoped>
.float-left {
  float: left;
}

.float-right {
  float: right;
}

.disable-element-hover:hover {
  background: white !important;
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
