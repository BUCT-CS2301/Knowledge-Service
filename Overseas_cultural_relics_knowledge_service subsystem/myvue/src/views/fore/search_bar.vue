<template>
  <div>
    <MainHeader />

    <div class="pro-search-container">
      <div class="search-form">
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
          <el-button type="primary" :loading="loading" @click="onSubmit">确定</el-button>
          <el-button @click="resetForm">重置</el-button>
        </div>
      </div>
    </div>

    <MainFooter />
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader.vue'
import MainFooter from '../../components/MainFooter/MainFooter.vue'
import { ElMessage } from 'element-plus'

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      loading: false,
      searchForm: {
        v_1: '',
        v_2: '',
        v_3: '',
        v_4: ''
      },
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
    onSubmit () {
      const { v_1, v_2, v_3, v_4 } = this.searchForm
      if (!v_1 && !v_2 && !v_3 && !v_4) {
        ElMessage.warning('请至少选择一个查询条件')
        return
      }
      this.$router.push({
        path: '/result',
        query: {
          mode: 'multi',
          ...(v_1 && { v_1 }),
          ...(v_2 && { v_2 }),
          ...(v_3 && { v_3 }),
          ...(v_4 && { v_4 })
        }
      })
    },
    resetForm () {
      this.searchForm = { v_1: '', v_2: '', v_3: '', v_4: '' }
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

    input[type='radio'] {
      accent-color: #8b4513;
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
  display: flex;
  justify-content: center;
  gap: 16px;

  :deep(.el-button--primary) {
    background-color: #8b4513 !important;
    border-color: #8b4513 !important;
    padding: 12px 40px;
    font-size: 16px;

    &:hover {
      background-color: #6b3510 !important;
      border-color: #6b3510 !important;
    }
  }
}
</style>
