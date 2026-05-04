<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
  </div>
</template>

<script>
import IndexView from './views/common/IndexView'
import axios from '_axios@0.27.2@axios'
export default {
  name: 'App',
  components: {IndexView},
  provide () { /* 父组件向子组件传递对象中的内容，子组件用inject: [reload]获取父组件中的方法或其他属性 */
    return {
      reload: this.reload
    }
  },
  data () {
    return {
      isRouterAlive: true
    }
  },
  methods: {
    reload () {
      this.isRouterAlive = false
      this.$nextTick(() => { /* 页面重新渲染完毕之后 */
        this.isRouterAlive = true
      })
    }
  }
}
</script>

<!--<template>-->
<!--  <div id="app">-->
<!--    <el-container>-->
<!--      <el-header>-->
<!--        <guide></guide>-->
<!--      </el-header>-->
<!--      <el-main>-->
<!--        <router-view></router-view>-->
<!--      </el-main>-->
<!--      <el-footer>-->
<!--        <kk></kk>-->
<!--      </el-footer>-->
<!--    </el-container>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import kk from './components/MainFooter/kk'-->
<!--import guide from './components/MainHeader/Guide'-->
<!--import Antique from './views/antique/antique'-->
<!--import personpage from './views/personpage/personpage'-->
<!--import comment from './views/comment/comment'-->
<!--export default {-->
<!--  name: 'App',-->
<!--  components: {Antique, guide, kk, personpage, comment}-->
<!--}-->
<!--</script>-->

<!--<style>-->
<!--#app {-->
<!--  font-family: 'Avenir', Helvetica, Arial, sans-serif;-->
<!--  -webkit-font-smoothing: antialiased;-->
<!--  -moz-osx-font-smoothing: grayscale;-->
<!--  text-align: center;-->
<!--  color: #2c3e50;-->
<!--  margin-top: 60px;-->
<!--}-->

<!--a {-->
<!--  text-decoration: none;-->
<!--}-->

<!--.router-link-active {-->
<!--  text-decoration: none;-->
<!--}-->
<!--</style>-->
