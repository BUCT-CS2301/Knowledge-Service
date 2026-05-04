<template>
  <div>
    <MainHeader></MainHeader>
    
    <div class="keyword-search-container">
      <div class="search-section">
        <div class="search-box-wrapper">
          <el-input v-model="in_form.keyword" placeholder="请输入内容">
            <template #append>
              <el-button type="primary" @click="res_res">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>

      <div class="filter-section">
        <div class="filter-title">选择朝代</div>
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
          <el-button type="primary" @click="confirmSearch">确定</el-button>
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
      in_form: {
        keyword: ''
      },
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
    res_res () {
      axios.post('http://localhost:8085/search/obscure', this.in_form).then((response) => {
        console.log(response.data)
        if (response.data.state === 200) {
          this.$router.push({ path: '/result', query: { keyword: this.in_form.keyword } })
        } else {
          alert(response.data)
        }
      }).catch(function (error) {
        console.log(error)
        this.$router.push({ path: '/result', query: { keyword: this.in_form.keyword } })
      })
    },
    confirmSearch () {
      const params = {}
      if (this.in_form.keyword) params.keyword = this.in_form.keyword
      if (this.selectedDynasty) params.dynasty = this.selectedDynasty
      this.$router.push({ path: '/result', query: params })
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
    max-width: 500px;
    margin: 0 auto;
  }

  :deep(.el-input) {
    height: 45px;
  }

  :deep(.el-button--primary) {
    background-color: #8B4513 !important;
    border-color: #8B4513 !important;
    height: 45px;
    color: #fff !important;

    &:hover {
      background-color: #6B3510 !important;
      border-color: #6B3510 !important;
      color: #fff !important;
    }
  }
}

.filter-section {
  max-width: 600px;
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

    input[type="radio"] {
      accent-color: #8B4513;
    }

    span {
      font-size: 14px;
      color: #666;
    }
  }

  .confirm-button-wrapper {
    margin-top: 30px;

    :deep(.el-button--primary) {
      background-color: #8B4513 !important;
      border-color: #8B4513 !important;

      &:hover {
        background-color: #6B3510 !important;
        border-color: #6B3510 !important;
      }
    }
  }
}
</style>
