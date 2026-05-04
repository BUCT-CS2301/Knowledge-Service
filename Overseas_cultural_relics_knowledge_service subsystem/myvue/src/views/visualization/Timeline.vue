<template>
  <div class="timeline-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>文物时间轴</h1>
        <p>按历史时期展示文物分布</p>
      </div>

      <div class="timeline-controls">
        <el-select v-model="selectedDynasty" placeholder="选择朝代">
          <el-option label="全部" value="all"></el-option>
          <el-option v-for="dynasty in dynasties" :key="dynasty.value" :label="dynasty.label" :value="dynasty.value"></el-option>
        </el-select>

        <el-button type="primary" @click="showFilterPanel = !showFilterPanel">筛选条件</el-button>
      </div>

      <div class="filter-panel" v-if="showFilterPanel">
        <div class="filter-row">
          <label>时间范围:</label>
          <el-date-picker v-model="timeRange" type="daterange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"></el-date-picker>
        </div>
        <div class="filter-row">
          <label>文物类型:</label>
          <el-checkbox-group v-model="selectedTypes">
            <el-checkbox label="青铜器"></el-checkbox>
            <el-checkbox label="陶瓷"></el-checkbox>
            <el-checkbox label="书画"></el-checkbox>
            <el-checkbox label="玉器"></el-checkbox>
            <el-checkbox label="金银器"></el-checkbox>
          </el-checkbox-group>
        </div>
      </div>

      <div class="timeline-container">
        <div class="timeline-line">
          <div class="timeline-progress"></div>

          <div
            v-for="(period, index) in timelineData"
            :key="index"
            class="timeline-node"
            :class="{ active: selectedPeriod === index }"
            @click="selectPeriod(index)"
          >
            <div class="node-dot"></div>
            <div class="node-label">{{ period.dynasty }}</div>
            <div class="node-year">{{ period.year }}</div>
          </div>
        </div>

        <div class="timeline-content">
          <div class="period-info">
            <h2>{{ currentPeriod.dynasty }}</h2>
            <p class="period-year">{{ currentPeriod.year }}</p>
            <p class="period-description">{{ currentPeriod.description }}</p>
          </div>

          <div class="relics-grid">
            <div
              v-for="(relic, index) in currentPeriod.relics"
              :key="index"
              class="relic-card"
            >
              <img :src="relic.image" :alt="relic.name" class="relic-image">
              <div class="relic-info">
                <h4>{{ relic.name }}</h4>
                <p class="relic-type">{{ relic.type }}</p>
                <p class="relic-museum">{{ relic.museum }}</p>
              </div>
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
  name: 'Timeline',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const selectedPeriod = ref(2)
    const selectedDynasty = ref('all')
    const showFilterPanel = ref(false)
    const timeRange = ref([])
    const selectedTypes = ref([])

    const dynasties = [
      { label: '商周', value: 'shangzhou' },
      { label: '秦汉', value: 'qinhan' },
      { label: '隋唐', value: 'suitang' },
      { label: '宋元', value: 'songyuan' },
      { label: '明清', value: 'mingqing' }
    ]

    const timelineData = ref([
      {
        dynasty: '商周',
        year: '约公元前1600-256年',
        description: '青铜文明鼎盛时期，青铜器工艺达到巅峰，出现大量精美的礼器和兵器。',
        relics: [
          { name: '青铜方鼎', type: '青铜器', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20bronze%20ding%20vessel%20shang%20dynasty&image_size=square' },
          { name: '青铜爵', type: '青铜器', museum: '大都会博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20bronze%20jue%20wine%20cup&image_size=square' }
        ]
      },
      {
        dynasty: '秦汉',
        year: '公元前221-220年',
        description: '统一王朝建立，陶瓷、漆器工艺发展，丝绸之路开始形成。',
        relics: [
          { name: '秦兵马俑', type: '陶俑', museum: '秦始皇兵马俑博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=terracotta%20warrior%20qin%20dynasty&image_size=square' },
          { name: '汉白玉雕', type: '玉器', museum: '波士顿美术馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=white%20jade%20carving%20han%20dynasty&image_size=square' }
        ]
      },
      {
        dynasty: '隋唐',
        year: '581-907年',
        description: '盛世繁荣，唐三彩、青花瓷兴起，中外文化交流频繁。',
        relics: [
          { name: '唐三彩骆驼', type: '陶瓷', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Tang%20dynasty%20tri-colored%20pottery%20camel&image_size=square' },
          { name: '唐代壁画', type: '绘画', museum: '敦煌研究院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Dunhuang%20mural%20painting%20Tang%20dynasty&image_size=square' }
        ]
      },
      {
        dynasty: '宋元',
        year: '960-1368年',
        description: '瓷器工艺达到顶峰，五大名窑闻名于世，文人书画兴盛。',
        relics: [
          { name: '汝窑青瓷', type: '陶瓷', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Ru%20kiln%20celadon%20porcelain%20song%20dynasty&image_size=square' },
          { name: '清明上河图', type: '书画', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Qingming%20Scroll%20painting%20song%20dynasty&image_size=square' }
        ]
      },
      {
        dynasty: '明清',
        year: '1368-1912年',
        description: '官窑瓷器精美绝伦，珐琅彩、粉彩等新工艺出现。',
        relics: [
          { name: '青花瓷瓶', type: '陶瓷', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=blue%20and%20white%20porcelain%20vase%20ming%20dynasty&image_size=square' },
          { name: '珐琅彩瓷', type: '陶瓷', museum: '大都会博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=enamel%20porcelain%20qing%20dynasty&image_size=square' }
        ]
      }
    ])

    const currentPeriod = computed(() => {
      return timelineData.value[selectedPeriod.value]
    })

    const selectPeriod = (index) => {
      selectedPeriod.value = index
    }

    return {
      selectedPeriod,
      selectedDynasty,
      showFilterPanel,
      timeRange,
      selectedTypes,
      dynasties,
      timelineData,
      currentPeriod,
      selectPeriod
    }
  }
}
</script>

<style lang="scss" scoped>
.timeline-page {
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

.timeline-controls {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;

  :deep(.el-select) {
    width: 150px;
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

.filter-panel {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

  .filter-row {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 15px;

    label {
      font-weight: 600;
      color: #333;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.timeline-container {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.timeline-line {
  position: relative;
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 3px solid #eee;
  margin-bottom: 30px;

  &::before {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: calc(20% * var(--progress, 2));
    height: 3px;
    background: #8B4513;
    transition: width 0.5s ease;
  }
}

.timeline-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-3px);
  }

  &.active {
    .node-dot {
      background: #8B4513;
      transform: scale(1.3);
    }

    .node-label {
      color: #8B4513;
      font-weight: 600;
    }
  }

  .node-dot {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #ddd;
    border: 3px solid white;
    box-shadow: 0 0 0 2px #ddd;
    margin-bottom: 8px;
    transition: all 0.3s;
  }

  .node-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
    transition: color 0.3s;
  }

  .node-year {
    font-size: 11px;
    color: #999;
  }
}

.timeline-content {
  .period-info {
    text-align: center;
    margin-bottom: 30px;

    h2 {
      font-size: 24px;
      color: #8B4513;
      margin-bottom: 8px;
    }

    .period-year {
      font-size: 14px;
      color: #999;
      margin-bottom: 12px;
    }

    .period-description {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      max-width: 600px;
      margin: 0 auto;
    }
  }
}

.relics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.relic-card {
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  }

  .relic-image {
    width: 100%;
    height: 180px;
    object-fit: cover;
  }

  .relic-info {
    padding: 15px;

    h4 {
      font-size: 16px;
      color: #333;
      margin-bottom: 8px;
    }

    .relic-type {
      font-size: 13px;
      color: #8B4513;
      margin-bottom: 5px;
    }

    .relic-museum {
      font-size: 12px;
      color: #999;
    }
  }
}
</style>
