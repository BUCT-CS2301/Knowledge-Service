<template>
  <div class="dashboard-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>统计分析看板</h1>
        <p>文物数据统计与可视化分析</p>
      </div>

      <div class="dashboard-controls">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        <el-select v-model="selectedMuseum" placeholder="选择博物馆">
          <el-option label="全部博物馆" value="all"></el-option>
          <el-option label="大英博物馆" value="british"></el-option>
          <el-option label="大都会博物馆" value="met"></el-option>
          <el-option label="卢浮宫" value="louvre"></el-option>
        </el-select>
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalRelics }}</div>
            <div class="stat-label">文物总数</div>
          </div>
          <div class="stat-trend up">↑ 12.5%</div>
        </div>

        <div class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ stats.museumCount }}</div>
            <div class="stat-label">博物馆数量</div>
          </div>
          <div class="stat-trend up">↑ 5.2%</div>
        </div>

        <div class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ stats.categoryCount }}</div>
            <div class="stat-label">文物类型</div>
          </div>
          <div class="stat-trend stable">—</div>
        </div>

        <div class="stat-card">
          <div class="stat-info">
            <div class="stat-value">{{ stats.countryCount }}</div>
            <div class="stat-label">分布国家</div>
          </div>
          <div class="stat-trend up">↑ 8.3%</div>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-card">
          <h3>文物类型分布</h3>
          <div class="pie-chart">
            <svg viewBox="0 0 200 200" class="pie-svg">
              <circle v-for="(slice, index) in typeDistribution" :key="index"
                cx="100" cy="100" r="80"
                :fill="slice.color"
                :stroke="slice.color"
                stroke-width="2"
                :stroke-dasharray="slice.dashArray"
                :stroke-dashoffset="slice.dashOffset"
                class="pie-slice"
                @click="selectType(index)"
              />
            </svg>
            <div class="pie-legend">
              <div v-for="(item, index) in typeDistribution" :key="index" class="legend-item">
                <span class="legend-color" :style="{ background: item.color }"></span>
                <span>{{ item.name }}: {{ item.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>朝代分布统计</h3>
          <div class="bar-chart">
            <div v-for="(item, index) in dynastyDistribution" :key="index" class="bar-item">
              <span class="bar-label">{{ item.dynasty }}</span>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: item.percentage + '%', background: item.color }"></div>
              </div>
              <span class="bar-value">{{ item.count }}</span>
            </div>
          </div>
        </div>

        <div class="chart-card full-width">
          <h3>博物馆馆藏量排行</h3>
          <div class="ranking-chart">
            <div v-for="(museum, index) in museumRanking" :key="index" class="ranking-item">
              <span class="rank-number" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <div class="rank-info">
                <span class="rank-name">{{ museum.name }}</span>
                <span class="rank-location">{{ museum.location }}</span>
              </div>
              <div class="rank-bar-container">
                <div class="rank-bar" :style="{ width: (museum.count / maxMuseumCount * 100) + '%' }"></div>
              </div>
              <span class="rank-count">{{ museum.count.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>年度新增文物趋势</h3>
          <div class="line-chart">
            <svg viewBox="0 0 300 200" class="line-svg">
              <line v-for="i in 5" :key="'h-' + i" x1="30" :y1="20 + i * 35" x2="270" :y2="20 + i * 35" stroke="#eee" stroke-width="1"/>
              <line v-for="i in 6" :key="'v-' + i" :x1="30 + i * 40" y1="180" :x2="30 + i * 40" y2="20" stroke="#eee" stroke-width="1"/>

              <path :d="linePath" fill="none" stroke="#8B4513" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>

              <circle v-for="(point, index) in trendPoints" :key="index"
                :cx="point.x" :cy="point.y" r="6" fill="#8B4513" stroke="white" stroke-width="2"
              />
            </svg>
            <div class="line-labels">
              <span v-for="year in trendYears" :key="year">{{ year }}</span>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>材质分布</h3>
          <div class="material-chart">
            <div v-for="(material, index) in materialDistribution" :key="index" class="material-item">
              <div class="material-bar" :style="{ height: material.percentage + '%', background: material.color }"></div>
              <span class="material-label">{{ material.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'Dashboard',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const dateRange = ref([])
    const selectedMuseum = ref('all')

    const stats = ref({
      totalRelics: 128650,
      museumCount: 156,
      categoryCount: 32,
      countryCount: 48
    })

    const typeDistribution = ref([
      { name: '青铜器', percentage: 28, color: '#8B4513' },
      { name: '陶瓷', percentage: 35, color: '#A0522D' },
      { name: '书画', percentage: 18, color: '#D2691E' },
      { name: '玉器', percentage: 12, color: '#CD853F' },
      { name: '其他', percentage: 7, color: '#DEB887' }
    ])

    const dynastyDistribution = ref([
      { dynasty: '商周', count: 15200, percentage: 20, color: '#8B4513' },
      { dynasty: '秦汉', count: 12800, percentage: 17, color: '#A0522D' },
      { dynasty: '隋唐', count: 18500, percentage: 24, color: '#D2691E' },
      { dynasty: '宋元', count: 16200, percentage: 21, color: '#CD853F' },
      { dynasty: '明清', count: 13300, percentage: 18, color: '#DEB887' }
    ])

    const museumRanking = ref([
      { name: '大英博物馆', location: '伦敦, 英国', count: 23000 },
      { name: '大都会博物馆', location: '纽约, 美国', count: 15000 },
      { name: '东京国立博物馆', location: '东京, 日本', count: 12000 },
      { name: '卢浮宫', location: '巴黎, 法国', count: 8000 },
      { name: '柏林亚洲艺术博物馆', location: '柏林, 德国', count: 6000 }
    ])

    const trendYears = ['2019', '2020', '2021', '2022', '2023', '2024']
    const trendData = [2800, 3200, 2900, 4100, 3800, 4500]

    const materialDistribution = ref([
      { name: '青铜', percentage: 25, color: '#8B4513' },
      { name: '瓷', percentage: 32, color: '#A0522D' },
      { name: '玉', percentage: 18, color: '#D2691E' },
      { name: '纸', percentage: 15, color: '#CD853F' },
      { name: '金', percentage: 10, color: '#DEB887' }
    ])

    const totalPercentage = computed(() => typeDistribution.value.reduce((sum, item) => sum + item.percentage, 0))

    const pieSlices = computed(() => {
      let offset = 0
      return typeDistribution.value.map(item => {
        const dashArray = `${(item.percentage / totalPercentage.value) * 2 * Math.PI * 80} ${2 * Math.PI * 80}`
        const dashOffset = -offset
        offset += (item.percentage / totalPercentage.value) * 2 * Math.PI * 80
        return { ...item, dashArray, dashOffset }
      })
    })

    const maxMuseumCount = computed(() => Math.max(...museumRanking.value.map(m => m.count)))

    const maxTrendValue = computed(() => Math.max(...trendData))

    const trendPoints = computed(() => {
      const width = 240
      const height = 160
      const padding = 30
      return trendData.map((value, index) => ({
        x: padding + (index / (trendData.length - 1)) * width,
        y: padding + height - (value / maxTrendValue.value) * height
      }))
    })

    const linePath = computed(() => {
      if (trendPoints.value.length === 0) return ''
      return trendPoints.value.map((point, index) =>
        `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`
      ).join(' ')
    })

    const selectType = (index) => {
      console.log('Selected type:', typeDistribution.value[index].name)
    }

    const refreshData = () => {
      console.log('Refreshing data...')
    }

    return {
      dateRange,
      selectedMuseum,
      stats,
      typeDistribution,
      dynastyDistribution,
      museumRanking,
      trendYears,
      materialDistribution,
      pieSlices,
      maxMuseumCount,
      trendPoints,
      linePath,
      selectType,
      refreshData
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.page-container {
  padding: 30px 5%;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 28px;
    color: #8B4513;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #666;
  }
}

.dashboard-controls {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
  align-items: center;

  :deep(.el-date-picker) {
    width: 280px;
  }

  :deep(.el-select) {
    width: 180px;
  }

  :deep(.el-button--primary) {
    background-color: #8B4513 !important;
    border-color: #8B4513 !important;

    &:hover {
      background-color: #6B3510 !important;
      border-color: #6B3510 !important;
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

  .stat-icon {
    font-size: 36px;
  }

  .stat-info {
    flex: 1;

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #333;
    }

    .stat-label {
      font-size: 13px;
      color: #999;
    }
  }

  .stat-trend {
    font-size: 12px;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 15px;

    &.up {
      color: #27ae60;
      background: #e8f5e9;
    }

    &.down {
      color: #e74c3c;
      background: #ffebee;
    }

    &.stable {
      color: #999;
      background: #f5f5f5;
    }
  }
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 25px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

  &.full-width {
    grid-column: 1 / -1;
  }

  h3 {
    font-size: 16px;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}

.pie-chart {
  display: flex;
  align-items: center;
  gap: 30px;

  .pie-svg {
    width: 150px;
    height: 150px;
  }

  .pie-slice {
    cursor: pointer;
    transition: opacity 0.3s;

    &:hover {
      opacity: 0.8;
    }
  }

  .pie-legend {
    .legend-item {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 10px;

      .legend-color {
        width: 14px;
        height: 14px;
        border-radius: 4px;
      }

      span:last-child {
        font-size: 13px;
        color: #666;
      }
    }
  }
}

.bar-chart {
  .bar-item {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .bar-label {
      width: 50px;
      font-size: 13px;
      color: #666;
    }

    .bar-track {
      flex: 1;
      height: 20px;
      background: #f0f0f0;
      border-radius: 10px;
      overflow: hidden;

      .bar-fill {
        height: 100%;
        border-radius: 10px;
        transition: width 0.5s ease;
      }
    }

    .bar-value {
      width: 60px;
      text-align: right;
      font-size: 13px;
      color: #8B4513;
      font-weight: 500;
    }
  }
}

.ranking-chart {
  .ranking-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .rank-number {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: #eee;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: 600;
      color: #666;

      &.top {
        background: #8B4513;
        color: white;
      }
    }

    .rank-info {
      width: 180px;

      .rank-name {
        display: block;
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }

      .rank-location {
        display: block;
        font-size: 12px;
        color: #999;
      }
    }

    .rank-bar-container {
      flex: 1;
      height: 8px;
      background: #f0f0f0;
      border-radius: 4px;
      overflow: hidden;

      .rank-bar {
        height: 100%;
        background: linear-gradient(90deg, #8B4513, #A0522D);
        border-radius: 4px;
      }
    }

    .rank-count {
      width: 100px;
      text-align: right;
      font-size: 14px;
      font-weight: 600;
      color: #8B4513;
    }
  }
}

.line-chart {
  .line-svg {
    width: 100%;
    height: 150px;
  }

  .line-labels {
    display: flex;
    justify-content: space-between;
    padding: 0 10px;
    margin-top: 5px;

    span {
      font-size: 12px;
      color: #999;
    }
  }
}

.material-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 150px;
  padding-top: 20px;

  .material-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;

    .material-bar {
      width: 30px;
      border-radius: 8px 8px 0 0;
      min-height: 10px;
      transition: height 0.5s ease;
    }

    .material-label {
      font-size: 12px;
      color: #666;
    }
  }
}
</style>
