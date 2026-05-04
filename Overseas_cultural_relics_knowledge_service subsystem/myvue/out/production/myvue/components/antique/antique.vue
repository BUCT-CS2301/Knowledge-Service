<template>
    <el-card :body-style="{ padding: '0px' }">
      <div class="block">
        <span class="demonstration">图片展示</span>
        <el-carousel height="150px">
          <el-carousel-item v-for="item in 3" :key="item">
            <img :src="pic" class="image">
          </el-carousel-item>
        </el-carousel>
      </div>
      <div style="padding: 14px;">
        <span>{{name}}</span>
        <div class="bottom clearfix">
          <time >{{ info }}</time>
          <el-button type="text" class="button">收藏</el-button>
        </div>
      </div>
    </el-card>
</template>

<script>
export default {
  props: {
    name: {
      default: '',
      type: String
    },
    pic: {
      default: '',
      type: String
    },
    info: {
      default: '',
      type: String
    }
  }
}
</script>

<style scoped>
.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #d3dce6;
}
</style>

