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
      </div>

      <div class="map-container">
        <div class="world-map">
          <svg viewBox="0 0 800 400" class="map-svg">
            <defs>
              <filter id="glow">
                <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
                <feMerge>
                  <feMergeNode in="coloredBlur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
            </defs>

            <g class="continents">
              <path d="M100,150 Q150,100 200,120 L220,180 Q180,200 150,180 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
              <path d="M250,80 Q350,50 420,80 L450,180 Q400,220 300,200 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
              <path d="M500,60 Q600,40 700,70 L720,150 Q650,180 550,160 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
              <path d="M600,250 Q680,220 750,260 L780,320 Q700,350 620,330 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
              <path d="M150,280 Q250,250 320,280 L350,350 Q280,380 200,360 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
              <path d="M450,300 Q520,280 580,310 L600,360 Q540,380 480,350 Z" fill="#e8e8e8" stroke="#ccc" stroke-width="1"/>
            </g>

            <g>
              <g
                v-for="(location, index) in mapLocations"
                :key="index"
                :transform="`translate(${location.x}, ${location.y})`"
                class="map-marker"
                @click="selectLocation(index)"
                @mouseenter="hoverLocation(index)"
                @mouseleave="unhoverLocation"
              >
                <circle
                  :r="selectedLocation === index ? 12 : 8"
                  :fill="selectedLocation === index ? '#8B4513' : '#e74c3c'"
                  :stroke="selectedLocation === index ? '#5D3A1A' : '#c0392b'"
                  stroke-width="2"
                  filter="url(#glow)"
                  class="marker-circle"
                />
                <circle
                  r="4"
                  fill="white"
                  class="marker-inner"
                />
              </g>
            </g>
          </svg>

          <div v-if="hoveredLocation" class="location-tooltip" :style="tooltipStyle">
            <div class="tooltip-name">{{ mapLocations[hoveredLocation].name }}</div>
            <div class="tooltip-count">馆藏数量: {{ mapLocations[hoveredLocation].count }} 件</div>
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
import { ref, reactive } from 'vue'
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
    const tooltipStyle = reactive({ left: '0px', top: '0px' })

    const mapLocations = ref([
      { name: '大英博物馆', city: '伦敦', country: '英国', x: 320, y: 110, count: 23000 },
      { name: '大都会博物馆', city: '纽约', country: '美国', x: 680, y: 100, count: 15000 },
      { name: '卢浮宫', city: '巴黎', country: '法国', x: 300, y: 100, count: 8000 },
      { name: '东京国立博物馆', city: '东京', country: '日本', x: 720, y: 280, count: 12000 },
      { name: '维多利亚博物馆', city: '墨尔本', country: '澳大利亚', x: 750, y: 330, count: 5000 },
      { name: '柏林亚洲艺术博物馆', city: '柏林', country: '德国', x: 360, y: 90, count: 6000 },
      { name: '波士顿美术馆', city: '波士顿', country: '美国', x: 670, y: 95, count: 4500 },
      { name: '韩国国立中央博物馆', city: '首尔', country: '韩国', x: 690, y: 260, count: 3800 }
    ])

    const selectLocation = (index) => {
      selectedLocation.value = selectedLocation.value === index ? null : index
    }

    const hoverLocation = (index) => {
      hoveredLocation.value = index
      const loc = mapLocations.value[index]
      tooltipStyle.left = loc.x + 50 + 'px'
      tooltipStyle.top = loc.y - 40 + 'px'
    }

    const unhoverLocation = () => {
      hoveredLocation.value = null
    }

    return {
      selectedRegion,
      selectedLocation,
      hoveredLocation,
      showLegend,
      tooltipStyle,
      mapLocations,
      selectLocation,
      hoverLocation,
      unhoverLocation
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
  padding: 20px;

  .map-svg {
    width: 100%;
    height: auto;
  }
}

.map-marker {
  cursor: pointer;
}

.marker-circle {
  transition: all 0.3s;
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

  .tooltip-count {
    font-size: 13px;
    color: #666;
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
</style>
