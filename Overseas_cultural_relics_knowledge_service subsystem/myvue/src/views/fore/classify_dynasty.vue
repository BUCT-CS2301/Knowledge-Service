<template>
  <div>
    <MainHeader />
    <div class="classify-container">
      <div class="classify-card">
        <h2 class="classify-title">按朝代分类浏览</h2>
        <div class="options-group">
          <label
            v-for="item in dynasties"
            :key="item.value"
            class="option-label"
            :class="{ active: selected === item.value }"
          >
            <input type="radio" v-model="selected" :value="item.value" />
            <span>{{ item.label }}</span>
          </label>
        </div>
        <div class="action-row">
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="primary" :disabled="!selected" @click="doSearch">确定查询</el-button>
        </div>
      </div>
    </div>
    <MainFooter />
  </div>
</template>

<script>
import MainHeader from '../../components/MainHeader/MainHeader.vue'
import MainFooter from '../../components/MainFooter/MainFooter.vue'

const DYNASTIES = [
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
]

export default {
  components: { MainHeader, MainFooter },
  data () {
    return {
      selected: '',
      dynasties: DYNASTIES
    }
  },
  methods: {
    doSearch () {
      this.$router.push({
        path: '/result',
        query: { mode: 'classify', c: 'dynasty', v_1: this.selected }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.classify-container {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 60px 5%;
  background: #f5f5f5;
}

.classify-card {
  background: #fff;
  border-radius: 8px;
  padding: 40px 48px;
  max-width: 720px;
  width: 100%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.classify-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 32px;
}

.options-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 36px;
}

.option-label {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #8b4513;
    background: #fff8f0;
  }

  &.active {
    border-color: #8b4513;
    background: #fff8f0;
    color: #8b4513;
  }

  input[type='radio'] {
    accent-color: #8b4513;
  }

  span {
    font-size: 14px;
  }
}

.action-row {
  display: flex;
  gap: 12px;
  justify-content: flex-end;

  :deep(.el-button--primary) {
    background-color: #8b4513;
    border-color: #8b4513;

    &:hover {
      background-color: #6b3510;
      border-color: #6b3510;
    }
  }
}
</style>