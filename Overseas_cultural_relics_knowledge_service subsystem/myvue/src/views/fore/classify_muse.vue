<template>
  <div>
    <MainHeader />
    <div class="classify-container">
      <div class="classify-card">
        <h2 class="classify-title">按博物馆分类浏览</h2>
        <div class="options-group">
          <label
            v-for="item in museums"
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

const MUSEUMS = [
  { label: '弗利尔美术馆', value: 'Freersackler' },
  { label: '丹佛美术馆', value: 'Denver Art Museum' },
  { label: '鲁宾艺术馆', value: 'Rubin Museum' },
  { label: '亚洲协会及其博物馆', value: 'Asia Society Museum' },
  { label: '大卫奥斯利艺术博物馆', value: 'David Owsley Museum of Art' }
]

export default {
  components: { MainHeader, MainFooter },
  data () {
    return {
      selected: '',
      museums: MUSEUMS
    }
  },
  methods: {
    doSearch () {
      this.$router.push({
        path: '/result',
        query: { mode: 'classify', c: 'museum', v_4: this.selected }
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