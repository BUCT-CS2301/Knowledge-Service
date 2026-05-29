<template>
  <div>
    <MainHeader />

    <div class="keyword-search-container">
      <div class="search-section">
        <div class="search-box-wrapper">
          <el-input
            v-model="in_form.keyword"
            placeholder="输入文物名称、作者、博物馆等关键字"
            clearable
            @keyup.enter="doSearch"
          >
            <template #append>
              <el-button type="primary" :loading="loading" @click="doSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>

      <div class="filter-section">
        <div class="filter-title">可选：按朝代缩小范围（结果页二次筛选）</div>
        <div class="dynasty-options">
          <label v-for="dynasty in dynasties" :key="dynasty.value" class="dynasty-label">
            <input type="radio" v-model="selectedDynasty" :value="dynasty.value">
            <span>{{ dynasty.label }}</span>
          </label>
        </div>
        <div class="dynasty-options">
          <label v-for="dynasty in dynasties2" :key="dynasty.value" class="dynasty-label">
            <input type="radio" v-model="selectedDynasty" :value="dynasty.value">
            <span>{{ dynasty.label }}</span>
          </label>
        </div>
        <div class="confirm-button-wrapper">
          <el-button type="primary" :loading="loading" @click="doSearch">确定</el-button>
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

const DYNASTY_MAP = {
  tang: '唐',
  song: '宋',
  yuan: '元',
  ming: '明',
  qing: '清',
  beiwei: '北魏',
  zhou: '周',
  dongzhou: '东周',
  nansong: '宋',
  donghan: '汉',
  xihan: '汉',
  zhongshang: '商'
}

export default {
  components: {
    MainHeader,
    MainFooter
  },
  data () {
    return {
      loading: false,
      in_form: { keyword: '' },
      selectedDynasty: '',
      dynasties: [
        { label: '唐', value: 'tang' },
        { label: '宋', value: 'song' },
        { label: '元', value: 'yuan' },
        { label: '明', value: 'ming' },
        { label: '清', value: 'qing' },
        { label: '北魏', value: 'beiwei' },
        { label: '周', value: 'zhou' }
      ],
      dynasties2: [
        { label: '东周', value: 'dongzhou' },
        { label: '南宋', value: 'nansong' },
        { label: '东汉', value: 'donghan' },
        { label: '西汉', value: 'xihan' },
        { label: '中商', value: 'zhongshang' }
      ]
    }
  },
  methods: {
    doSearch () {
      const keyword = (this.in_form.keyword || '').trim()
      if (!keyword) {
        ElMessage.warning('请输入搜索关键字')
        return
      }
      const query = {
        mode: 'obscure',
        keyword
      }
      if (this.selectedDynasty) {
        query.filterDynasty = DYNASTY_MAP[this.selectedDynasty] || this.selectedDynasty
      }
      this.$router.push({ path: '/result', query })
    }
  }
}
</script>

<style lang="scss" scoped>
.keyword-search-container {
  min-height: calc(100vh - 160px);
  padding: 40px 5%;
  background: #f5f5f5;
}

.search-section {
  text-align: center;
  margin-bottom: 50px;

  .search-box-wrapper {
    max-width: 560px;
    margin: 0 auto;
  }

  :deep(.el-input) {
    height: 45px;
  }

  :deep(.el-button--primary) {
    background-color: #8b4513 !important;
    border-color: #8b4513 !important;
    height: 45px;
    color: #fff !important;

    &:hover {
      background-color: #6b3510 !important;
      border-color: #6b3510 !important;
    }
  }
}

.filter-section {
  max-width: 720px;
  margin: 0 auto;
  text-align: center;

  .filter-title {
    font-size: 16px;
    color: #333;
    margin-bottom: 20px;
    font-weight: 500;
  }

  .dynasty-options {
    display: flex;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
    margin-bottom: 20px;
  }

  .dynasty-label {
    display: flex;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    padding: 8px 16px;
    background: white;
    border-radius: 20px;
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

  .confirm-button-wrapper {
    margin-top: 30px;

    :deep(.el-button--primary) {
      background-color: #8b4513 !important;
      border-color: #8b4513 !important;

      &:hover {
        background-color: #6b3510 !important;
        border-color: #6b3510 !important;
      }
    }
  }
}
</style>
