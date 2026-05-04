
<template>
  <div>
<MainHeader></MainHeader>
  <el-container>
    <el-main>
      <div>
        <div v-if="flag">
      <div label="种类">
        <span class="title">选择用途</span>
        <el-radio v-model="searchForm.v_3" label="Metalwork">金属</el-radio>
        <el-radio v-model="searchForm.v_3" label="Ceramic">陶瓷</el-radio>
        <el-radio v-model="searchForm.v_3" label="Jewelry and Ornament">珠宝</el-radio>
        <el-radio v-model="searchForm.v_3" label="Rubbing">拓印</el-radio>
        <el-radio v-model="searchForm.v_3" label="Calligraphy">书法</el-radio>
        <el-radio v-model="searchForm.v_3" label="Sculpture">雕塑</el-radio>
        <el-radio v-model="searchForm.v_3" label="Painting">绘画</el-radio>
        <el-radio v-model="searchForm.v_3" label="Tool and Equipment">工具</el-radio>
        <el-radio v-model="searchForm.v_3" label="Jade">玉</el-radio>
        <el-radio v-model="searchForm.v_3" label="Weapon and Armament">兵器</el-radio>
      </div>
          <el-button type="primary" @click="onSubmit_to_search">确 定</el-button>
        </div>
        <div v-else >
          <el-button type="primary" @click="ars">按字母降序</el-button>
          <el-button type="primary" @click="up">按字母升序</el-button>
          <div style="font-size: 14px;">
            <div style="background-color: #d3dce6;color: gray;">
              <div style="margin: 0 200px;">
                <div style="display: flex;margin-bottom: 20px;">
                  <el-row>
                    <el-col v-for="(item,index) in res_form" :key="(item,index)" :span="6">
                      <div class="grid-content bg-purple" :style="'flex: 1;border: 1px solid #dfdfdf;'+(index>-1?'margin-left: 20px;':'')" >
                        <router-link :to="{path: '/antiqueDetail', query: {id:item.id}}">
                          <el-card :body-style="{ padding: '0px',width:'300px',height:'400px' }" class="vert">
                            <img :src="item.img_url" class="image" alt="">
                            <div style="padding: 14px;">
                              <span>{{item.object_name}}</span>
                              <div class="bottom clearfix">
                                <time class="time">{{ item.cat2 }}</time>
                                <el-button type="text" class="button" >详情</el-button>
                              </div>
                            </div>
                          </el-card>
                        </router-link>
                      </div>
                    </el-col>
                  </el-row>
                </div>

              </div>
              </div>
            </div>
          </div>
      </div>
    </el-main>
  </el-container>
    <MainFooter></MainFooter>
  </div>
</template>

<script>
import axios from 'axios'
import MainFooter from '../../components/MainFooter/MainFooter'
import MainHeader from '../../components/MainHeader/MainHeader'
export default {
  name: 'search_bar',
  components: {
    MainFooter,
    MainHeader
  },
  data () {
    return {
      // 避免多个select选值混乱
      searchForm: {
        c: 'use',
        v_3: ''
      },
      res_form: [
        {
          object_name: '',
          cat2: '',
          img_url: '',
          // eslint-disable-next-line no-undef
          id: 0
        }
      ],
      flag: true
    }
  },
  methods: {
    onSubmit_to_search () {
      if (this.searchForm.v_3 === '') { alert('请选择用途') } else {
        axios.post('http://localhost:8085/search/classification', this.searchForm).then((response) => {
          console.log(response.data)
          if (response.data.state === 200) {
            // eslint-disable-next-line no-sequences,no-unused-expressions
            this.res_form = response.data.data,
            this.flag = false
          } else {
            alert(response.data)
          }
        }).catch(function (error) { console.log(error) })
        console.log('Received values of form: ', this.searchForm)
      }
    },
    up () {
      let arr = this.res_form.sort(function (x, y) {
        if (x.object_name < y.object_name) {
          return -1
        } else if (x.object_name > y.object_name) {
          return 1
        } else {
          return 0
        }
      })
    },
    ars () {
      let arr = this.res_form.sort(function (x, y) {
        return y.object_name.localeCompare(x.object_name)
      })
      this.res_form = arr
    }
  }
}

</script>

<style scoped>
.vert{
  padding: 0px;
  width: 300px;
  height: 400px;
  justify-content: space-around;
}
  .image{
    /*width: 250px;*/
    /*height: 30%;*/
    width: 150px;
    height: 150px
  }
.el-row {margin-bottom: 20px; }
.el-col {
  border-radius: 4px;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
.el-header, .el-footer {
  background-color: #B3C0D1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
  text-align: center;
  line-height: 160px;
}

body > .el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}
  body > .el-container {
    margin-bottom: 40px;
  }

  .el-container:nth-child(5) .el-aside,
  .el-container:nth-child(6) .el-aside {
    line-height: 260px;
  }

  .el-container:nth-child(7) .el-aside {
    line-height: 320px;
  }
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

</style>
