<template>
  <div>
    <h2>我的收藏</h2>
    <br>
<!--    <div v-for="i in 5" :key="i" style="display: flex;margin-bottom: 20px;">-->
<!--      <div v-for="(item,index) in cont" :key="item" class="product" :style="'flex: 1;border: 1px solid #dfdfdf;'+(index>0?'margin-left: 20px;':'')">-->
<!--        <div style="background-color: #FFFFFF;text-align: center;">-->
<!--          <div style="margin: 0 20px;">-->
<!--            <div>-->
<!--                <img :src="item.url"  alt="">-->
<!--            </div>-->
<!--            <div style="font-size: 16px;font-weight: 500;height:30px;overflow: hidden;text-overflow: ellipsis;">-->
<!--              {{ item.name }}-->
<!--            </div>-->
<!--            <div style="font-size: 12px;height: 50px;overflow: hidden;text-overflow: ellipsis;">-->
<!--              {{ item.info }}-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
    <smallAntique v-for="item in list" :key="item.id" :title="item.name" :pic="item.url" :info="item.info"></smallAntique>
    <div class="block">
      <el-pagination
        layout="prev, pager, next"
        :page-size="6"
        :total="100"
        @current-change="handleCurrentChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import smallAntique from '../../components/smallAntique/smallAntique'
export default {
  name: 'PicShow',
  components: {smallAntique},
  data () {
    return {
      list: []
    }
  },
  methods: {
    async pageInit () {
      /* 先用直接地址测试，之后换成api接口 */
      const {data: res} = await axios.get('', {
        params: {
          id: 1
        }
      })
      if (res.status === 200) {
        this.list = res.list
        console.log('111')
      }
    },
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
    },
    /* 分页后台要写好相关代码 */
    handleCurrentChange (currentPage) {
      alert(currentPage)
    },
    handleClick (tab, event) {
      console.log(tab, event)
    },
    jumpto () {
      // this.$router.push('/')
      this.$router.push('/')
    }
  },
  mounted () {
    this.pageInit()
  }
}
</script>

<style scoped>
.product:hover{
  cursor: pointer;
  transform: scale(1.05);
  border-top: 1px solid red!important;
}
</style>
