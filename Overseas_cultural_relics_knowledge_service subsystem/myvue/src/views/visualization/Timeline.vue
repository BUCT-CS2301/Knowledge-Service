<template>
  <div class="timeline-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>文物时间轴</h1>
        <p>按历史时期展示文物分布</p>
      </div>

      <div class="timeline-controls">
        <el-button @click="refreshData">刷新数据</el-button>
      </div>

      <div class="timeline-container" v-loading="isLoading">
        <div class="timeline-line">
          <div class="timeline-progress" :style="{ width: progressWidth }"></div>

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
              @click="showRelicDetail(relic)"
            >
              <img :src="relic.image" :alt="relic.name" class="relic-image" @error="handleImageError($event, relic)">
              <div class="relic-info">
                <h4>{{ relic.name }}</h4>
                <p class="relic-type">{{ relic.type }}</p>
                <p class="relic-museum">{{ relic.museum }}</p>
              </div>
            </div>
          </div>

          <div v-if="currentPeriod.relics.length === 0" class="empty-state">
            <el-empty description="该时期暂无文物数据"></el-empty>
          </div>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { getApiRoot } from '@/config/api'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'Timeline',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const selectedPeriod = ref(0)
    const isLoading = ref(false)
    const timelineData = ref([])

    const progressWidth = computed(() => {
      return `${((selectedPeriod.value + 1) / timelineData.value.length) * 100}%`
    })

    const currentPeriod = computed(() => {
      if (timelineData.value.length === 0) {
        return { dynasty: '', year: '', description: '', relics: [] }
      }
      return timelineData.value[selectedPeriod.value]
    })

    const selectPeriod = (index) => {
      selectedPeriod.value = index
    }

    const loadMockData = () => {
      const mockData = [
        {
          dynasty: '远古',
          year: '约公元前5000-2000年',
          description: '新石器时代，彩陶文化繁荣，玉器制作技艺开始发展。',
          relics: [
            { name: '彩陶盆', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1952.17/1952.17_web.jpg', backupImage: 'https://picsum.photos/seed/pottery/300/200' },
            { name: '玉琮', type: '玉器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1919.10/1919.10_web.jpg', backupImage: 'https://picsum.photos/seed/jade/300/200' }
          ]
        },
        {
          dynasty: '夏商',
          year: '约公元前2000-1046年',
          description: '青铜时代早期，甲骨文出现，青铜礼器开始盛行。',
          relics: [
            { name: '青铜鼎', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1962.62/1962.62_web.jpg', backupImage: 'https://picsum.photos/seed/bronze/300/200' },
            { name: '青铜酒器', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1975.40/1975.40_web.jpg', backupImage: 'https://picsum.photos/seed/bronze2/300/200' }
          ]
        },
        {
          dynasty: '西周',
          year: '公元前1046-771年',
          description: '礼乐制度确立，青铜器铭文发达，玉器工艺精湛。',
          relics: [
            { name: '西周青铜器', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1964.57/1964.57_web.jpg', backupImage: 'https://picsum.photos/seed/zhoubronze/300/200' },
            { name: '玉器', type: '玉器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1932.28/1932.28_web.jpg', backupImage: 'https://picsum.photos/seed/jade2/300/200' }
          ]
        },
        {
          dynasty: '春秋战国',
          year: '公元前770-221年',
          description: '百家争鸣，青铜器走向世俗化，漆器工艺兴起。',
          relics: [
            { name: '青铜剑', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1919.32/1919.32_web.jpg', backupImage: 'https://picsum.photos/seed/sword/300/200' },
            { name: '战国青铜器', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1964.58/1964.58_web.jpg', backupImage: 'https://picsum.photos/seed/warring/300/200' }
          ]
        },
        {
          dynasty: '秦汉',
          year: '公元前221-220年',
          description: '统一王朝建立，陶瓷、漆器工艺发展，丝绸之路开始形成。',
          relics: [
            { name: '秦汉陶器', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1932.59/1932.59_web.jpg', backupImage: 'https://picsum.photos/seed/terracotta/300/200' },
            { name: '汉代文物', type: '青铜器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1955.18/1955.18_web.jpg', backupImage: 'https://picsum.photos/seed/han/300/200' }
          ]
        },
        {
          dynasty: '三国两晋',
          year: '220-589年',
          description: '战乱频繁但文化繁荣，佛教艺术传入，绘画书法发展。',
          relics: [
            { name: '魏晋佛像', type: '雕塑', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1963.10/1963.10_web.jpg', backupImage: 'https://picsum.photos/seed/buddha/300/200' },
            { name: '两晋文物', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1931.26/1931.26_web.jpg', backupImage: 'https://picsum.photos/seed/jin/300/200' }
          ]
        },
        {
          dynasty: '隋唐',
          year: '581-907年',
          description: '盛世繁荣，唐三彩、青花瓷兴起，中外文化交流频繁。',
          relics: [
            { name: '唐三彩', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1968.3/1968.3_web.jpg', backupImage: 'https://picsum.photos/seed/tang/300/200' },
            { name: '唐代文物', type: '金银器', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1979.7/1979.7_web.jpg', backupImage: 'https://picsum.photos/seed/tang2/300/200' }
          ]
        },
        {
          dynasty: '五代十国',
          year: '907-960年',
          description: '政权更迭频繁，但艺术持续发展，绘画成就突出。',
          relics: [
            { name: '五代绘画', type: '书画', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1965.20/1965.20_web.jpg', backupImage: 'https://picsum.photos/seed/fiveDyn/300/200' },
            { name: '十国陶瓷', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1959.3/1959.3_web.jpg', backupImage: 'https://picsum.photos/seed/tenking/300/200' }
          ]
        },
        {
          dynasty: '宋元',
          year: '960-1368年',
          description: '瓷器工艺达到顶峰，五大名窑闻名于世，文人书画兴盛。',
          relics: [
            { name: '宋代瓷器', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1955.19/1955.19_web.jpg', backupImage: 'https://picsum.photos/seed/song/300/200' },
            { name: '元青花', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1968.2/1968.2_web.jpg', backupImage: 'https://picsum.photos/seed/yuan/300/200' }
          ]
        },
        {
          dynasty: '明清',
          year: '1368-1912年',
          description: '官窑瓷器精美绝伦，珐琅彩、粉彩等新工艺出现。',
          relics: [
            { name: '明代青花', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1977.7/1977.7_web.jpg', backupImage: 'https://picsum.photos/seed/ming/300/200' },
            { name: '清代珐琅', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1963.87/1963.87_web.jpg', backupImage: 'https://picsum.photos/seed/qing/300/200' }
          ]
        },
        {
          dynasty: '近现代',
          year: '1912-2000年',
          description: '近现代文物保护与收藏兴起，大量海外流失文物开始回流。',
          relics: [
            { name: '近现代文物', type: '各类', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1972.96/1972.96_web.jpg', backupImage: 'https://picsum.photos/seed/modern/300/200' },
            { name: '回流文物', type: '陶瓷', museum: 'Cleveland Museum of Art', image: 'https://openaccess-cdn.clevelandart.org/1952.510/1952.510_web.jpg', backupImage: 'https://picsum.photos/seed/return/300/200' }
          ]
        }
      ]
      timelineData.value = [...mockData]
    }

    const refreshData = async () => {
      isLoading.value = true
      try {
        const response = await axios.get(`${getApiRoot()}/api/v1/data/timeline`)
        if (response.data && response.data.code === 200 && response.data.data) {
          timelineData.value = [...response.data.data]
          if (timelineData.value.length > 0 && selectedPeriod.value >= timelineData.value.length) {
            selectedPeriod.value = 0
          }
        } else {
          loadMockData()
        }
      } catch (error) {
        console.error('Failed to fetch timeline data:', error)
        loadMockData()
      }
      isLoading.value = false
    }

    const showRelicDetail = (relic) => {
      console.log('Relic detail:', relic)
    }

    const handleImageError = (event, relic) => {
      if (relic.backupImage) {
        relic.image = relic.backupImage
      } else {
        event.target.style.display = 'none'
      }
    }

    onMounted(() => {
      refreshData()
    })

    return {
      selectedPeriod,
      timelineData,
      currentPeriod,
      progressWidth,
      selectPeriod,
      refreshData,
      showRelicDetail,
      handleImageError,
      isLoading
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
}

.timeline-progress {
  position: absolute;
  bottom: -3px;
  left: 0;
  height: 3px;
  background: #8B4513;
  transition: width 0.5s ease;
}

.timeline-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s;
  z-index: 1;

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
  cursor: pointer;

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

.empty-state {
  text-align: center;
  padding: 40px;
}
</style>