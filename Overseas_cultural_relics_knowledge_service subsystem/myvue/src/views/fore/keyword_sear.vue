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
        { label: '唐代', value: 'tang' },
        { label: '宋代', value: 'song' },
        { label: '元代', value: 'yuan' },
        { label: '明代', value: 'ming' },
        { label: '清代', value: 'qing' },
        { label: '北魏', value: 'beiwei' },
        { label: '周代', value: 'zhou' }
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
      if (!this.in_form.keyword.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      
      axios.post('http://localhost:8080/search/obscure', this.in_form).then((response) => {
        console.log(response.data)
        if (response.data.state === 200) {
          this.$router.push({ path: '/result', query: { keyword: this.in_form.keyword } })
        } else {
          alert(response.data)
        }
      }).catch((error) => {
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
    height: 45px;
  }
}

.filter-section {
  max-width: 600px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filter-title {
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.dynasty-options {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.dynasty-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 15px;
  background: #f5f5f5;
  border-radius: 20px;
  transition: all 0.3s;

  &:hover {
    background: #e8e8e8;
  }

  input[type="radio"] {
    margin-right: 8px;
    cursor: pointer;
  }

  span {
    font-size: 14px;
    color: #666;
  }
}

.confirm-button-wrapper {
  text-align: center;
  margin-top: 20px;
}
</style>