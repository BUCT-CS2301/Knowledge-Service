<template>
  <div>
    <MainHeader></MainHeader>
    {{res}}
  <div style="font-size: 14px;">
    <div style="background-color: #eeeeee;color: gray;">
      <div style="margin: 0 200px;">
        <div style="display: flex;margin-bottom: 20px;" v-for="i in 5" :key="i">
          <router-link to="antiqueDetail">
          <div class="product" v-for="(item,index) in products" :style="'flex: 1;border: 1px solid #dfdfdf;'+(index>0?'margin-left: 20px;':'')" :key="(item,index)">
            <div style="background-color: #FFFFFF;text-align: center;">
              <div style="margin: 0 20px;">
                <div>
                  <img :src="item.img_url">
                </div>
                <div style="font-size: 16px;font-weight: 500;height: 30px;overflow: hidden;text-overflow: ellipsis;">
                  {{item.object_name}}
                </div>
                <div style="font-size: 12px;height: 50px;overflow: hidden;text-overflow: ellipsis;">
                  {{item.id}}
                </div>
              </div>
            </div>
          </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
    <MainFooter></MainFooter>
  </div>

</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
export default {
  components: {
    MainHeader, MainFooter
  },
  data () {
    return {
      products: [
        {object_name: '',
          cat2: '',
          img_url: '',
          id: ''
        }],
      res: ''
    }
  },
  created () {
    this.res = this.$route.query.id
    // this.$route.query.key
  },
  methods: {
    handleSizeChange (val) {
      console.log(`每页 ${val} 条`)
    },
    handleCurrentChange (val) {
      console.log(`当前页: ${val}`)
    },
    handleClick (tab, event) {
      console.log(tab, event)
    }

  }
}
</script>

<style>
.product:hover{
  cursor: pointer;
  transform: scale(1.05);
  border-top: 1px solid red!important;
}
</style>
<!--<template>-->
<!--  <div style="font-size: 14px;">-->
<!--    <MainHeader></MainHeader>-->
<!--&lt;!&ndash;    <el-button plain v-on：click="sor_1">按时间排序</el-button>&ndash;&gt;-->
<!--&lt;!&ndash;    <el-button plain v-on:click="sor_2">按字母排序</el-button>&ndash;&gt;-->
<!--    <div style="background-color: #eeeeee;color: gray;">-->
<!--      <div style="margin: 0 200px;">-->
<!--        <div v-for="i in 5" style="display: flex;margin-bottom: 20px;" v-bind:key="i">-->
<!--          <div class="product" v-for="(item,index) in res_form" :style="'flex: 1;border: 1px solid #dfdfdf;'+(index>0?'margin-left: 20px;':'')" v-bind:key="item">-->
<!--            <div style="background-color: #FFFFFF;text-align: center;">-->
<!--              <div style="margin: 0 20px;">-->
<!--                <div>-->
<!--                  <img :src="item.img_url" v-on:click="jump_to_detial">-->
<!--                </div>-->
<!--                <div style="font-size: 16px;font-weight: 500;height: 30px;overflow: hidden;text-overflow: ellipsis;">-->
<!--                  {{item.cat2}}-->
<!--                </div>-->
<!--                <div style="font-size: 12px;height: 50px;overflow: hidden;text-overflow: ellipsis;">-->
<!--                  {{item.object_name}}-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->

<!--          </div>-->

<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--    <MainFooter></MainFooter>-->
<!--  </div>-->

<!--</template>-->

<!--<script>-->
<!--import MainFooter from '../../components/MainFooter/MainFooter'-->
<!--import MainHeader from '../../components/MainHeader/MainHeader'-->
<!--export default {-->
<!--  components:{-->
<!--    MainFooter,MainHeader-->
<!--  },-->
<!--  data () {-->
<!--    return {-->
<!--      res_form: [{-->
<!--        id: '1',-->
<!--        img_url: 'https://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E5%9B%BE%E7%89%87&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=2196812579,178847292&os=2981951213,133282478&simid=3419287875,189417273&pn=1&rn=1&di=7077213605308923905&ln=1643&fr=&fmq=1651674880226_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%253A%252F%252Fimg.jj20.com%252Fup%252Fallimg%252F4k%252Fs%252F02%252F2109242332225H9-0-lp.jpg%26refer%3Dhttp%253A%252F%252Fimg.jj20.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Dauto%3Fsec%3D1654266879%26t%3Da887c9a2c804465ae80bbc9eb4be49a0&rpstart=0&rpnum=0&adpicid=0&nojc=undefined&dyTabStr=MCwzLDYsMSw0LDIsNSw3LDgsOQ%3D%3D',-->
<!--        cat2: '唐',-->
<!--        object_name: '树'-->
<!--      },-->
<!--      {-->
<!--        id: '1',-->
<!--        img_url: 'https://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E5%9B%BE%E7%89%87&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=2196812579,178847292&os=2981951213,133282478&simid=3419287875,189417273&pn=1&rn=1&di=7077213605308923905&ln=1643&fr=&fmq=1651674880226_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%253A%252F%252Fimg.jj20.com%252Fup%252Fallimg%252F4k%252Fs%252F02%252F2109242332225H9-0-lp.jpg%26refer%3Dhttp%253A%252F%252Fimg.jj20.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Dauto%3Fsec%3D1654266879%26t%3Da887c9a2c804465ae80bbc9eb4be49a0&rpstart=0&rpnum=0&adpicid=0&nojc=undefined&dyTabStr=MCwzLDYsMSw0LDIsNSw3LDgsOQ%3D%3D',-->
<!--        cat2: '唐',-->
<!--        object_name: '树'-->
<!--      },-->
<!--      {-->
<!--        id: '1',-->
<!--        img_url: 'https://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E5%9B%BE%E7%89%87&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=2196812579,178847292&os=2981951213,133282478&simid=3419287875,189417273&pn=1&rn=1&di=7077213605308923905&ln=1643&fr=&fmq=1651674880226_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%253A%252F%252Fimg.jj20.com%252Fup%252Fallimg%252F4k%252Fs%252F02%252F2109242332225H9-0-lp.jpg%26refer%3Dhttp%253A%252F%252Fimg.jj20.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Dauto%3Fsec%3D1654266879%26t%3Da887c9a2c804465ae80bbc9eb4be49a0&rpstart=0&rpnum=0&adpicid=0&nojc=undefined&dyTabStr=MCwzLDYsMSw0LDIsNSw3LDgsOQ%3D%3D',-->
<!--        cat2: '唐',-->
<!--        object_name: '树'-->
<!--      }]-->
<!--    }-->
<!--  },-->
<!--  mounted () {-->
<!--  },-->
<!--  created () {-->
<!--    this.res_form = this.$route.res_form-->
<!--  },-->
<!--  methods: {-->
<!--    handleSizeChange (val) {-->
<!--      console.log(`每页 ${val} 条`)-->
<!--    },-->
<!--    handleCurrentChange (val) {-->
<!--      console.log(`当前页: ${val}`)-->
<!--    },-->
<!--    handleClick (tab, event) {-->
<!--      console.log(tab, event)-->
<!--    },-->
<!--    // sor_2 () {-->
<!--    //   // eslint-disable-next-line vue/no-side-effects-in-computed-properties-->
<!--    //   return this.companyList.sort(function (x, y) {-->
<!--    //     return x['time'].localeCompare(y['time'])-->
<!--    //     // index是companyList你需要索引的字段名称-->
<!--    //   })-->
<!--    // },-->
<!--    // sor_1 () {-->
<!--    //   // eslint-disable-next-line vue/no-side-effects-in-computed-properties-->
<!--    //   return this.companyList.sort(function (x, y) {-->
<!--    //     return x['name'].localeCompare(y['name'])-->
<!--    //     // index是companyList你需要索引的字段名称-->
<!--    //   })-->
<!--    // },-->
<!--    jump_to_detial () {-->
<!--      this.router.pushd('/antiqueDetail')-->
<!--    }-->

<!--  }-->
<!--}-->
<!--</script>-->

<!--<style>-->
<!--.product:hover{-->
<!--  cursor: pointer;-->
<!--  transform: scale(1.05);-->
<!--  border-top: 1px solid red!important;-->
<!--}-->
<!--</style>-->
