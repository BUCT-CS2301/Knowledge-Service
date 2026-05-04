<template>
          <el-descriptions title="个人信息" direction="vertical" :column="4" border>
            <el-descriptions-item label="用户名">{{user.name}}</el-descriptions-item>
            <el-descriptions-item label="性别" :span="2">{{user.sex}}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ user.tel }}</el-descriptions-item>
            <!--    <el-descriptions-item label="备注">-->
            <!--      <el-tag size="small">学校</el-tag>-->
            <!--    </el-descriptions-item>-->
            <!--    <el-descriptions-item label="联系地址">江苏省苏州市吴中区吴中大道 1188 号</el-descriptions-item>-->
          </el-descriptions>
</template>

<script>
export default {
  data () {
    return {
      user: {
        name: '顾青山',
        sex: '男',
        tel: '17768888888'
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
