<template>
  <div style="background-color: white">
    <h2>欢迎访问主页!</h2>
  </div>
</template>

<script>
export default {
  name: 'personpage_hello',
  data () {
    return {
      user: {
        userpic: '@/assets/timg.jpeg',
        username: '游客'
      }
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
