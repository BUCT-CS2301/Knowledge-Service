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
              <img :src="relic.image" :alt="relic.name" class="relic-image">
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
            { name: '彩陶盆', type: '陶瓷', museum: '中国国家博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=neolithic%20painted%20pottery%20bowl%20chinese&image_size=square' },
            { name: '玉琮', type: '玉器', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=jade%20cong%20tube%20neolithic%20chinese&image_size=square' }
          ]
        },
        {
          dynasty: '夏商',
          year: '约公元前2000-1046年',
          description: '青铜时代早期，甲骨文出现，青铜礼器开始盛行。',
          relics: [
            { name: '青铜兽面纹鼎', type: '青铜器', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=bronze%20ding%20vessel%20with%20animal%20mask%20shang%20dynasty&image_size=square' },
            { name: '甲骨文', type: '文字', museum: '中国国家博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=oracle%20bone%20inscription%20shang%20dynasty&image_size=square' }
          ]
        },
        {
          dynasty: '西周',
          year: '公元前1046-771年',
          description: '礼乐制度确立，青铜器铭文发达，玉器工艺精湛。',
          relics: [
            { name: '毛公鼎', type: '青铜器', museum: '台北故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Maogong%20ding%20bronze%20vessel%20western%20zhou&image_size=square' },
            { name: '玉圭', type: '玉器', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=jade%20gui%20tablet%20zhou%20dynasty&image_size=square' }
          ]
        },
        {
          dynasty: '春秋战国',
          year: '公元前770-221年',
          description: '百家争鸣，青铜器走向世俗化，漆器工艺兴起。',
          relics: [
            { name: '越王勾践剑', type: '青铜器', museum: '湖北省博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=ancient%20chinese%20bronze%20sword%20spring%20autumn&image_size=square' },
            { name: '曾侯乙编钟', type: '青铜器', museum: '湖北省博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=bronze%20bell%20set%20zeng%20hou%20yi&image_size=square' }
          ]
        },
        {
          dynasty: '秦汉',
          year: '公元前221-220年',
          description: '统一王朝建立，陶瓷、漆器工艺发展，丝绸之路开始形成。',
          relics: [
            { name: '秦兵马俑', type: '陶俑', museum: '秦始皇兵马俑博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=terracotta%20warrior%20qin%20dynasty&image_size=square' },
            { name: '马王堆帛画', type: '绘画', museum: '湖南省博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=silk%20painting%20mawangdui%20han%20dynasty&image_size=square' }
          ]
        },
        {
          dynasty: '三国两晋',
          year: '220-589年',
          description: '战乱频繁但文化繁荣，佛教艺术传入，绘画书法发展。',
          relics: [
            { name: '顾恺之女史箴图', type: '书画', museum: '大英博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Admonitions%20Scroll%20gu%20kaizhi%20painting&image_size=square' },
            { name: '青瓷莲花尊', type: '陶瓷', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=celadon%20lotus%20vase%20southern%20dynasty&image_size=square' }
          ]
        },
        {
          dynasty: '隋唐',
          year: '581-907年',
          description: '盛世繁荣，唐三彩、青花瓷兴起，中外文化交流频繁。',
          relics: [
            { name: '唐三彩骆驼', type: '陶瓷', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Tang%20dynasty%20tri-colored%20pottery%20camel&image_size=square' },
            { name: '敦煌壁画', type: '绘画', museum: '敦煌研究院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Dunhuang%20mural%20painting%20Tang%20dynasty&image_size=square' }
          ]
        },
        {
          dynasty: '五代十国',
          year: '907-960年',
          description: '政权更迭频繁，但艺术持续发展，绘画成就突出。',
          relics: [
            { name: '韩熙载夜宴图', type: '书画', museum: '故宫博物院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Night%20Banquet%20painting%20gu%20hongzhong&image_size=square' },
            { name: '越窑青瓷', type: '陶瓷', museum: '上海博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=yue%20kiln%20celadon%20five%20dynasties&image_size=square' }
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
        },
        {
          dynasty: '近现代',
          year: '1912-2000年',
          description: '近现代文物保护与收藏兴起，大量海外流失文物开始回流。',
          relics: [
            { name: '敦煌遗书', type: '文献', museum: '敦煌研究院', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=dunhuang%20manuscript%20scroll%20document&image_size=square' },
            { name: '圆明园兽首', type: '青铜器', museum: '保利艺术博物馆', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=bronze%20animal%20head%20yuanmingyuan&image_size=square' }
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