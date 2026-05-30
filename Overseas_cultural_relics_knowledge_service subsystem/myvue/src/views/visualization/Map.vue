<template>
  <div class="map-page">
    <MainHeader></MainHeader>

    <div class="page-container">
      <div class="page-header">
        <h1>文物地理分布图</h1>
        <p>展示海外博物馆藏中国文物的地理分布</p>
      </div>

      <div class="map-controls">
        <el-select v-model="selectedRegion" placeholder="选择区域">
          <el-option label="全球" value="global"></el-option>
          <el-option label="欧洲" value="europe"></el-option>
          <el-option label="北美洲" value="america"></el-option>
          <el-option label="亚洲" value="asia"></el-option>
          <el-option label="大洋洲" value="oceania"></el-option>
        </el-select>

        <el-button type="primary" @click="showLegend = !showLegend">图例</el-button>
        
        <el-button type="default" @click="refreshData" :loading="isLoading">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>

      <div class="map-container">
        <div class="world-map">
          <div class="map-image-wrapper">
            <img src="/world-map.png" alt="世界地图" class="map-image" @load="onMapLoad" />
            
            <svg class="markers-overlay" viewBox="0 0 800 400">
              <g
                v-for="(location, index) in filteredLocations"
                :key="index"
                :transform="`translate(${location.x}, ${location.y})`"
                class="marker-group"
                @click="selectLocation(index)"
                @mouseenter="hoverLocation(index, $event)"
                @mouseleave="unhoverLocation"
              >
                <circle
                  :r="selectedLocation === index ? 12 : 10"
                  :fill="selectedLocation === index ? '#8B4513' : '#e74c3c'"
                  class="marker-circle"
                />
                <circle r="4" fill="#fff" class="marker-inner" />
              </g>
            </svg>
          </div>

          <div v-if="hoveredLocation !== null && filteredLocations[hoveredLocation]" class="location-tooltip" :style="tooltipStyle">
            <div class="tooltip-name">{{ filteredLocations[hoveredLocation].name }}</div>
            <div class="tooltip-location">{{ filteredLocations[hoveredLocation].city }}, {{ filteredLocations[hoveredLocation].country }}</div>
            <div class="tooltip-count">馆藏文物: {{ filteredLocations[hoveredLocation].count.toLocaleString() }}件</div>
          </div>
        </div>

        <div class="location-panel">
          <h3>博物馆列表</h3>
          <div class="museum-list">
            <div
              v-for="(location, index) in mapLocations"
              :key="index"
              class="museum-item"
              :class="{ active: selectedLocation === index }"
              @click="selectLocation(index)"
            >
              <div class="museum-icon">🏛️</div>
              <div class="museum-info">
                <span class="museum-name">{{ location.name }}</span>
                <span class="museum-location">{{ location.city }}, {{ location.country }}</span>
              </div>
              <div class="museum-count">{{ location.count }}件</div>
            </div>
          </div>
        </div>
      </div>

      <div class="legend-panel" v-if="showLegend">
        <h4>图例说明</h4>
        <div class="legend-item">
          <span class="legend-dot" style="background: #e74c3c;"></span>
          <span>普通馆藏点</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot" style="background: #8B4513;"></span>
          <span>选中馆藏点</span>
        </div>
      </div>
    </div>

    <MainFooter></MainFooter>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import axios from 'axios'
import { getApiRoot } from '@/config/api'
import MainHeader from '../../components/MainHeader/MainHeader'
import MainFooter from '../../components/MainFooter/MainFooter'

export default {
  name: 'Map',
  components: {
    MainHeader,
    MainFooter
  },
  setup() {
    const selectedRegion = ref('global')
    const selectedLocation = ref(null)
    const hoveredLocation = ref(null)
    const showLegend = ref(false)
    const isLoading = ref(false)
    const tooltipStyle = reactive({ left: '0px', top: '0px', display: 'none' })
    const mapImageLoaded = ref(false)

    const mapLocations = ref([])

    const mapWidth = 800
    const mapHeight = 400

    const latLngToPixel = (lat, lng) => {
      const x = ((lng + 180) / 360) * mapWidth * 0.85 + 60
      const y = ((90 - lat) / 180) * mapHeight * 1.15 + 25
      return { x: Math.round(x), y: Math.round(y) }
    }

    const mockLocations = [
      { name: '大英博物馆', city: '伦敦', country: '英国', region: 'europe', x: 402, y: 135, count: 23000 },
      { name: '大都会博物馆', city: '纽约', country: '美国', region: 'america', x: 300, y: 160, count: 15000 },
      { name: '卢浮宫', city: '巴黎', country: '法国', region: 'europe', x: 410, y: 150, count: 8000 },
      { name: '东京国立博物馆', city: '东京', country: '日本', region: 'asia', x: 660, y: 185, count: 12000 },
      { name: '维多利亚博物馆', city: '墨尔本', country: '澳大利亚', region: 'oceania', x: 680, y: 315, count: 5000 },
      { name: '柏林亚洲艺术博物馆', city: '柏林', country: '德国', region: 'europe', x: 438, y: 145, count: 6000 },
      { name: '波士顿美术馆', city: '波士顿', country: '美国', region: 'america', x: 300, y: 155, count: 4500 },
      { name: '韩国国立中央博物馆', city: '首尔', country: '韩国', region: 'asia', x: 638, y: 180, count: 3800 }
    ]

    const loadMockData = () => {
      mapLocations.value = mockLocations
    }

    const fetchLocations = async () => {
      isLoading.value = true
      try {
        const response = await axios.get(`${getApiRoot()}/api/v1/data/geo-map`)
        if (response.data && response.data.data && response.data.data.length > 0) {
          mapLocations.value = response.data.data.map(loc => {
            if (loc.lat !== undefined && loc.lng !== undefined) {
              const { x, y } = latLngToPixel(loc.lat, loc.lng)
              return { ...loc, x: Math.round(x), y: Math.round(y) }
            }
            return loc
          })
        } else {
          loadMockData()
        }
      } catch (error) {
        console.error('Failed to fetch map data:', error)
        loadMockData()
      }
      isLoading.value = false
    }

    const refreshData = () => {
      fetchLocations()
    }

    const filteredLocations = computed(() => {
      if (!mapLocations.value.length) return []
      if (selectedRegion.value === 'global') {
        return mapLocations.value
      }
      return mapLocations.value.filter(loc => loc.region === selectedRegion.value)
    })

    const getLocationByIndex = (index) => {
      return filteredLocations.value[index]
    }

    onMounted(() => {
      fetchLocations()
    })

    const onMapLoad = () => {
      mapImageLoaded.value = true
    }

    const selectLocation = (index) => {
      selectedLocation.value = selectedLocation.value === index ? null : index
    }

    const hoverLocation = (index, event) => {
      hoveredLocation.value = index
      const location = filteredLocations.value[index]
      if (location) {
        tooltipStyle.left = (location.x + 15) + 'px'
        tooltipStyle.top = (location.y - 50) + 'px'
        tooltipStyle.display = 'block'
      }
    }

    const unhoverLocation = () => {
      hoveredLocation.value = null
      tooltipStyle.display = 'none'
    }

    return {
      selectedRegion,
      selectedLocation,
      hoveredLocation,
      showLegend,
      isLoading,
      tooltipStyle,
      mapLocations,
      filteredLocations,
      selectLocation,
      hoverLocation,
      unhoverLocation,
      refreshData,
      onMapLoad
    }
  }
}
</script>

<style lang="scss" scoped>
.map-page {
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

.map-controls {
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

.map-container {
  display: flex;
  gap: 30px;
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.world-map {
  flex: 1;
  position: relative;
  background: #fafafa;
  border-radius: 12px;
  padding: 0;
  min-height: 500px;
  height: 500px;
  overflow: hidden;
}

.map-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.map-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 12px;
}

.markers-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;

  .marker-group {
    pointer-events: auto;
    cursor: pointer;

    .marker-circle {
      transition: all 0.2s ease;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
    }

    .marker-inner {
      transition: all 0.2s ease;
    }

    &:hover .marker-circle {
      r: 14;
      filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
    }
  }
}

.location-tooltip {
  position: absolute;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  z-index: 100;
  pointer-events: none;
  min-width: 150px;

  .tooltip-name {
    font-weight: 600;
    color: #8B4513;
    margin-bottom: 5px;
  }

  .tooltip-location {
    font-size: 12px;
    color: #666;
    margin-bottom: 5px;
  }

  .tooltip-count {
    font-size: 13px;
    color: #e74c3c;
    font-weight: 500;
  }
}

.location-panel {
  width: 300px;
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;

  h3 {
    font-size: 16px;
    color: #333;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}

.museum-list {
  max-height: 450px;
  overflow-y: auto;

  .museum-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #fff;
    }

    &.active {
      background: #fff8f0;
      border-left: 3px solid #8B4513;
    }

    .museum-icon {
      font-size: 24px;
      margin-right: 12px;
    }

    .museum-info {
      flex: 1;

      .museum-name {
        display: block;
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }

      .museum-location {
        display: block;
        font-size: 12px;
        color: #999;
      }
    }

    .museum-count {
      font-size: 14px;
      font-weight: 600;
      color: #8B4513;
    }
  }
}

.legend-panel {
  margin-top: 25px;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

  h4 {
    font-size: 14px;
    color: #333;
    margin-bottom: 15px;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;

    .legend-dot {
      width: 14px;
      height: 14px;
      border-radius: 50%;
    }

    span:last-child {
      font-size: 13px;
      color: #666;
    }
  }
}

:deep(.leaflet-popup-content-wrapper) {
  border-radius: 8px;
  padding: 0;
}

:deep(.leaflet-popup-tip) {
  background-color: #fff;
}
</style>
