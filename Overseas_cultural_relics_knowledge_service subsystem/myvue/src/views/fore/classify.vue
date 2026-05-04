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
            <el-card shadow="hover" class="badge" :style="{ backgroundImage: 'url(' + item.bgUrl + ')' }">
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

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      classifyList: [
        {
          key: 'dynasty',
          title: '朝代',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/OIP-C.8cF-Es_gVFYtmMwzaHoKIwHaFt?pid=ImgDet&rs=1'
        },
        {
          key: 'material',
          title: '材质',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/R-C.03057cce39380cf934605e8d4bfb65ef?rik=BP3kzTvHjqD6ZA&riu=http%3a%2f%2fimg01.baimao.com%2fM00%2f54%2f88%2fwKgAFFuHOgGAD6ofAAC8a81zVbQ551.jpg&ehk=%2bCiN%2brqmeEMrr2CGAsO9thQUEsDB3Y0izk7piXsNLTw%3d&risl=&pid=ImgRaw&r=0'
        },
        {
          key: 'museum',
          title: '博物馆',
          bgUrl: 'https://tse1-mm.cn.bing.net/th/id/R-C.eeefca935019f71260e6a9fcb1df0f63?rik=MoqnjhKYBCvfgg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1309%2f20%2fc87%2f25937376_25937376_1136074994421_mthumb.jpg&ehk=8vdy1RIbqowuPXHaMdMI283TboT8kZ1A3vQRlVMH14E%3d&risl=&pid=ImgRaw&r=0'
        },
        {
          key: 'usage',
          title: '用途',
          bgUrl: 'https://tse4-mm.cn.bing.net/th/id/OIP-C.5YOZG6psKqpAA89M7o3oaAHaGY?pid=ImgDet&rs=1'
        }
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
  display: -moz-box;
  display: -webkit-box;

  -moz-box-align: center;
  -webkit-box-align: center;

  -moz-box-pack: center;
  -webkit-box-pack: center;

  height: 150px;
}

a {
  text-decoration:none;
}
</style>
