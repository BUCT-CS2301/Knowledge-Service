<template>
  <div>
    <MainHeader />
    <div class="classify-container">
      <div class="classify-card">
        <h2 class="classify-title">按材质分类浏览</h2>
        <div class="options-group">
          <label
            v-for="item in materials"
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

const MATERIALS = [
  { label: '石器', value: 'Stoneware' },
  { label: '釉面', value: 'Glazed' },
  { label: '瓷', value: 'Porcelain' },
  { label: '玉', value: 'Jade' },
  { label: '陶器', value: 'Earthenware' },
  { label: '未上釉', value: 'Unglazed' }
]

export default {
  components: { MainHeader, MainFooter },
  data () {
    return {
      selected: '',
      materials: MATERIALS
    }
  },
  methods: {
    doSearch () {
      this.$router.push({
        path: '/result',
        query: { mode: 'classify', c: 'mart', v_2: this.selected }
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