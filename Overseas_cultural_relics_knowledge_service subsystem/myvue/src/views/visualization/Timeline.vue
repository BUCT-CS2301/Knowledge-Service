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
              role="button"
              tabindex="0"
              @click="goRelicDetail(relic)"
              @keyup.enter="goRelicDetail(relic)"
            >
              <img :src="imageFor(relic)" :alt="relic.name" class="relic-image" @error="onImgError">
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
import { useRouter } from 'vue-router'
import axios from 'axios'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'
import { getApiRoot } from '@/config/api'
import { timelineMockData } from '@/data/timelineMock'
import { getArtifactImageUrl } from '@/utils/artifactPlaceholder'

export default {
  name: 'Timeline',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const router = useRouter()
    const LOCAL_FALLBACK_IMG = '/timg.jpeg'
    const selectedPeriod = ref(0)
    const isLoading = ref(false)
    const timelineData = ref([])
    const dataSource = ref('none')

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
      timelineData.value = timelineMockData.map(period => ({
        ...period,
        relics: period.relics.map(relic => ({ ...relic }))
      }))
    }

    const imageFor = (relic) => {
      const url = (relic?.image || relic?.img_url || '').toString().trim()
      if (url.startsWith('/timeline/')) {
        return url
      }
      if (url && !/neeko-copilot\.bytedance\.net|example\.com|^https?:\/\//i.test(url)) {
        return url
      }
      if (url && /^https?:\/\//i.test(url) && !/neeko-copilot\.bytedance\.net|example\.com/i.test(url)) {
        return url
      }
      return getArtifactImageUrl({ object_name: relic.name, cat1: relic.type, cat3: relic.type }) || LOCAL_FALLBACK_IMG
    }

    const onImgError = (e) => {
      e.target.onerror = null
      e.target.src = LOCAL_FALLBACK_IMG
    }

    const buildDetailRoute = (relic) => {
      if (relic.objectId) {
        return { path: '/antiqueDetail', query: { objectId: relic.objectId } }
      }
      if (relic.id) {
        return { path: '/relicDetail', query: { id: relic.id, name: relic.name } }
      }
      return { path: '/relicDetail', query: { from: 'timeline', name: relic.name } }
    }

    const saveRelicSnapshot = (relic) => {
      const period = currentPeriod.value
      sessionStorage.setItem('timelineRelicDetail', JSON.stringify({
        id: relic.id || 'timeline',
        objectId: relic.objectId || '',
        name: relic.name,
        museum: relic.museum,
        period: `${period.dynasty}（${period.year}）`,
        image: imageFor(relic),
        description: relic.description || `${relic.name}，${relic.type}类文物，现藏于${relic.museum}。`
      }))
    }

    const goRelicDetail = (relic) => {
      saveRelicSnapshot(relic)
      router.push(buildDetailRoute(relic))
    }

    const fetchTimelineData = async () => {
      loadMockData()
      dataSource.value = 'mock'
      isLoading.value = true
      try {
        const response = await axios.get(`${getApiRoot()}/api/v1/data/timeline`, { timeout: 3000 })
        const payload = response.data
        const list = payload?.data
        const ok = payload && (payload.state === 200 || payload.code === 200) && Array.isArray(list)
        const relicCount = ok ? list.reduce((sum, p) => sum + (p.relics?.length || 0), 0) : 0
        if (ok && relicCount > 0) {
          timelineData.value = list
          dataSource.value = 'api'
        }
      } catch (error) {
        /* 保持 mock */
      }
      if (selectedPeriod.value >= timelineData.value.length) {
        selectedPeriod.value = 0
      }
      isLoading.value = false
    }

    const refreshData = () => {
      fetchTimelineData()
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
      goRelicDetail,
      imageFor,
      onImgError,
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
  display: block;
  cursor: pointer;
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