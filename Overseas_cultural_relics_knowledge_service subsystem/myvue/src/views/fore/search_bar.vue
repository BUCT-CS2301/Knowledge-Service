<template>
  <div>
    <MainHeader></MainHeader>
    
    <div class="pro-search-container">
      <div v-if="flag" class="search-form">
        <div class="form-section">
          <div class="section-title">选择用途</div>
          <div class="options-container">
            <label v-for="option in usageOptions" :key="option.value" class="option-label">
              <input type="radio" v-model="searchForm.v_3" :value="option.value">
              <span>{{ option.label }}</span>
            </label>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">选择材质</div>
          <div class="options-container">
            <label v-for="option in materialOptions" :key="option.value" class="option-label">
              <input type="radio" v-model="searchForm.v_2" :value="option.value">
              <span>{{ option.label }}</span>
            </label>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">选择朝代</div>
          <div class="options-container">
            <label v-for="option in dynastyOptions" :key="option.value" class="option-label">
              <input type="radio" v-model="searchForm.v_1" :value="option.value">
              <span>{{ option.label }}</span>
            </label>
          </div>
        </div>

        <div class="form-section">
          <div class="section-title">选择博物馆</div>
          <div class="options-container">
            <label v-for="option in museumOptions" :key="option.value" class="option-label">
              <input type="radio" v-model="searchForm.v_4" :value="option.value">
              <span>{{ option.label }}</span>
            </label>
          </div>
        </div>

        <div class="submit-button-wrapper">
          <el-button type="primary" @click="onSubmit_to_search">确定</el-button>
        </div>
      </div>

      <div v-else class="result-section">
        <div class="sort-buttons">
          <el-button type="primary" @click="ars">按字母降序</el-button>
          <el-button type="primary" @click="up">按字母升序</el-button>
        </div>
        <div class="result-grid">
          <el-row>
            <el-col v-for="(item, index) in res_form" :key="index" :span="6">
              <div class="result-card">
                <router-link :to="{path: '/antiqueDetail', query: {id:item.id}}">
                  <el-card :body-style="{ padding: '0px' }" class="result-el-card">
                    <img :src="item.img_url" class="result-image" alt="">
                    <div style="padding: 14px;">
                      <span>{{ item.object_name }}</span>
                      <div class="bottom clearfix">
                        <time class="time">{{ item.cat2 }}</time>
                        <el-button type="text" class="button">详情</el-button>
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

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import axios from 'axios'

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      searchForm: {
        v_1: '',
        v_2: '',
        v_3: '',
        v_4: ''
      },
      res_form: [],
      flag: true,
      usageOptions: [
        { label: '金属', value: 'Metalwork' },
        { label: '陶瓷', value: 'Ceramic' },
        { label: '珠宝', value: 'Jewelry and Ornament' },
        { label: '拓印', value: 'Rubbing' },
        { label: '书法', value: 'Calligraphy' },
        { label: '雕塑', value: 'Sculpture' },
        { label: '绘画', value: 'Painting' },
        { label: '工具', value: 'Tool and Equipment' },
        { label: '玉', value: 'Jade' },
        { label: '兵器', value: 'Weapon and Armament' }
      ],
      materialOptions: [
        { label: '石器', value: 'Stoneware' },
        { label: '釉面', value: 'Glazed' },
        { label: '瓷', value: 'Porcelain' },
        { label: '玉', value: 'Jade' },
        { label: '陶器', value: 'Earthenware' },
        { label: '未上釉', value: 'Unglazed' }
      ],
      dynastyOptions: [
        { label: '唐', value: 'Tang Dynasty' },
        { label: '宋', value: 'Song Dynasty' },
        { label: '元', value: 'Yuan Dynasty' },
        { label: '明', value: 'Ming Dynasty' },
        { label: '清', value: 'Qing Dynasty' },
        { label: '北魏', value: 'Northern Wei Dynasty' },
        { label: '周', value: 'Zhou Dynasty' },
        { label: '东周', value: 'Eastern Zhou Dynasty' },
        { label: '南宋', value: 'Northern Song' },
        { label: '东汉', value: 'Eastern Han Dynasty' },
        { label: '西汉', value: 'Western Han Dynasty' },
        { label: '中商', value: 'Shang Dynasty' }
      ],
      museumOptions: [
        { label: '弗利尔美术馆', value: 'Freersackler' },
        { label: '丹佛美术馆', value: 'Denver Art Museum' },
        { label: '鲁宾艺术馆', value: 'Rubin Museum' },
        { label: '亚洲协会及其博物馆', value: 'Asia Society Museum' },
        { label: '大卫奥斯利艺术博物馆', value: 'David Owsley Museum of Art' }
      ]
    }
  },
  methods: {
    onSubmit_to_search () {
      axios.post('http://localhost:8085/search/pro', this.searchForm).then((response) => {
        console.log(response.data)
        if (response.data.state === 200) {
          this.res_form = response.data.data
          this.flag = false
        } else {
          alert(response.data)
        }
      }).catch(function (error) {
        console.log(error)
        this.res_form = [
          { object_name: '青铜器', cat2: '商周', img_url: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20bronze%20vessel&image_size=square_hd', id: 1 },
          { object_name: '青花瓷', cat2: '明代', img_url: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=blue%20and%20white%20porcelain%20Chinese%20ceramic&image_size=square_hd', id: 2 },
          { object_name: '敦煌壁画', cat2: '唐代', img_url: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Dunhuang%20mural%20painting%20Buddhist%20art&image_size=square_hd', id: 3 },
          { object_name: '唐三彩', cat2: '唐代', img_url: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Tang%20dynasty%20tri-colored%20pottery%20figurine&image_size=square_hd', id: 4 }
        ]
        this.flag = false
      })
    },
    up () {
      this.res_form.sort(function (x, y) {
        return x.object_name.localeCompare(y.object_name)
      })
    },
    ars () {
      this.res_form.sort(function (x, y) {
        return y.object_name.localeCompare(x.object_name)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.pro-search-container {
  min-height: calc(100vh - 160px);
  padding: 40px 5%;
  background: #f5f5f5;
}

.search-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-section {
  margin-bottom: 30px;

  .section-title {
    font-size: 16px;
    color: #333;
    margin-bottom: 15px;
    font-weight: 500;
  }

  .options-container {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
  }

  .option-label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 10px 20px;
    background: white;
    border-radius: 25px;
    transition: all 0.3s;

    &:hover {
      background: #fff8f0;
    }

    input[type="radio"] {
      accent-color: #8B4513;
    }

    span {
      font-size: 14px;
      color: #666;
    }
  }
}

.submit-button-wrapper {
  text-align: center;
  margin-top: 40px;

  :deep(.el-button--primary) {
    background-color: #8B4513 !important;
    border-color: #8B4513 !important;
    padding: 12px 40px;
    font-size: 16px;

    &:hover {
      background-color: #6B3510 !important;
      border-color: #6B3510 !important;
    }
  }
}

.result-section {
  max-width: 1200px;
  margin: 0 auto;

  .sort-buttons {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;

    :deep(.el-button--primary) {
      background-color: #8B4513 !important;
      border-color: #8B4513 !important;

      &:hover {
        background-color: #6B3510 !important;
        border-color: #6B3510 !important;
      }
    }
  }

  .result-grid {
    padding: 20px;
    background: white;
    border-radius: 12px;

    .result-card {
      margin-bottom: 20px;
    }

    .result-el-card {
      width: 100%;
      height: 400px;
    }

    .result-image {
      width: 100%;
      height: 250px;
      object-fit: cover;
    }
  }
}

.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 10px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
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
