<template>
  <div>
<MainHeader></MainHeader>
  <div class="main-explore">
    <div class="explore-badge">

      <el-card class="box-card">
        分类浏览
      </el-card>

      <br><br>

      <el-row :gutter="12">
        <el-col v-for="(item, index) in classifyList" :key="index" class="classify-layout" :span="6">
          <el-card shadow="hover" class="badge" :body-style="{ padding: '0' }">
            <img :src="item.bgUrl" class="classify-img" :alt="item.title">
          </el-card>
        </el-col>
      </el-row>
      <div style="display: flex" >
        <div style="flex: 1"></div>
        <el-row :gutter="200" style="margin-bottom: 50%">
          <el-button type="primary" round @click.prevent="handle_dyn" >朝代</el-button>
          <el-button type="success" round style="margin-left: 120px" @click.prevent="handle_mate">材质</el-button>
          <el-button type="info" round style="margin-left: 120px" @click.prevent="handle_muse">博物馆</el-button>
          <el-button type="warning" round style="margin-left: 120px" @click.prevent="handle_usea">用途</el-button>
        </el-row>
        <div style="flex: 1"></div>
    </div>

    </div>

    <br>
    <MainFooter></MainFooter>
  </div>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import dynastyImg from '@/assets/index/1.png'
import materialImg from '@/assets/index/2.png'
import museumImg from '@/assets/index/3.png'
import usageImg from '@/assets/index/4.png'

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      classifyList: [
        { key: 'dynasty', title: '朝代', bgUrl: dynastyImg },
        { key: 'material', title: '材质', bgUrl: materialImg },
        { key: 'museum', title: '博物馆', bgUrl: museumImg },
        { key: 'usage', title: '用途', bgUrl: usageImg }
      ]
    }
  },
  methods: {
    handle_dyn () {
      this.$router.push('/classify_dynasty')
    },
    handle_mate () {
      this.$router.push('/classify_mart')
    },
    handle_muse () {
      this.$router.push('/classify_muse')
    },
    handle_res () {
      this.$router.push('/result')
    },
    handle_usea () {
      this.$router.push('/classify_usea')
    }
  }
}
</script>

<style lang="scss" scoped>
.classify-layout, .hotheme-layout {
  margin: 0 0 12px 0;
}

.explore-main-pic img {
  width: 100%;
  padding: 25px;
}

.explore-badge {
  margin-top: 40px;
  margin-left: auto;
  margin-right: auto;
  width: 60%;
  height: 400px;
}

.box-card {
  width: 100%;
  text-align: center;
}

.badge {
  overflow: hidden;
  height: 150px;
}

.classify-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}

a {
  text-decoration:none;
}
</style>
