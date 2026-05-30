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
        { label: '陶瓷', value: '陶瓷' },
        { label: '绘画', value: '绘画' },
        { label: '雕塑', value: '雕塑' },
        { label: '打印', value: '打印' },
        { label: '亚洲', value: '亚洲' }
      ],
      materialOptions: [
        { label: '瓷', value: '瓷' },
        { label: '陶瓷', value: '陶瓷' },
        { label: '玉', value: '玉' },
        { label: '青铜', value: '青铜' },
        { label: '纸', value: '纸' }
      ],
      dynastyOptions: [
        { label: '唐', value: '唐' },
        { label: '宋', value: '宋' },
        { label: '元', value: '元' },
        { label: '明', value: '明' },
        { label: '清', value: '清' },
        { label: '汉', value: '汉' },
        { label: '隋', value: '隋' }
      ],
      museumOptions: [
        { label: '克利夫兰艺术博物馆', value: '克利夫兰' },
        { label: '尼尔森-阿特金斯艺术博物馆', value: '尼尔森' },
        { label: '宾夕法尼亚大学考古与人类学博物馆', value: '宾夕法尼亚' }
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
